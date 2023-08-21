package com.jsql.model.suspendable;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.InjectionCharInsertion;
import com.jsql.model.injection.vendor.MediatorVendor;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runnable class, define insertionCharacter to be used during injection,
 * i.e -1 in "[..].php?id=-1 union select[..]", sometimes it's -1, 0', 0, etc.
 * Find working insertion char when error message occurs in source.
 * Force to 1 if no insertion char works and empty value from user,
 * Force to user's value if no insertion char works,
 * Force to insertion char otherwise.
 */
public class SuspendableGetCharInsertion extends AbstractSuspendable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private static final String LABEL_PREFIX = "prefix";

    public SuspendableGetCharInsertion(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Object... args) throws JSqlException {
        
        String characterInsertionByUser = (String) args[0];
        
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetInsertionCharacter");
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        var charFromBooleanMatch = new String[1];
        List<String> charactersInsertion = this.initializeCallables(taskCompletionService, characterInsertionByUser, charFromBooleanMatch);
        
        var mediatorVendor = this.injectionModel.getMediatorVendor();

        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting database and character insertion with Order by match...");

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
                
                List<Vendor> vendorsOrderByMatch = this.getVendorsOrderByMatch(mediatorVendor, pageSource);
                
                if (!vendorsOrderByMatch.isEmpty()) {

                    this.setVendor(mediatorVendor, vendorsOrderByMatch);
                    
                    LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Using [{}]", mediatorVendor.getVendor());
                    var requestSetVendor = new Request();
                    requestSetVendor.setMessage(Interaction.SET_VENDOR);
                    Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
                    msgHeader.put(Header.URL, this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser());
                    msgHeader.put(Header.VENDOR, mediatorVendor.getVendor());
                    requestSetVendor.setParameters(msgHeader);
                    this.injectionModel.sendToViews(requestSetVendor);
                    
                    // Char insertion
                    charFromOrderBy = currentCallable.getCharacterInsertion();
                    
                    LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Character insertion [{}] matching with Order by and compatible with Error strategy", charFromOrderBy);
                    
                    break;
                }
                    
            } catch (InterruptedException e) {
                
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
                
            } catch (ExecutionException e) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }
        
        // End the job
        try {
            taskExecutor.shutdown();
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            Thread.currentThread().interrupt();
        }
        
        if (charFromOrderBy == null && charFromBooleanMatch[0] != null) {
            
            charFromOrderBy = charFromBooleanMatch[0];
        }
        
        return this.getCharacterInsertion(characterInsertionByUser, charFromOrderBy);
    }

    private void setVendor(MediatorVendor mediatorVendor, List<Vendor> vendorsOrderByMatch) {
        
        // Vendor
        if (
            vendorsOrderByMatch.size() == 1
            && vendorsOrderByMatch.get(0) != mediatorVendor.getVendor()
        ) {
            
            mediatorVendor.setVendor(vendorsOrderByMatch.get(0));
            
        } else if (vendorsOrderByMatch.size() > 1) {
            
            if (vendorsOrderByMatch.contains(mediatorVendor.getPostgreSQL())) {
                
                mediatorVendor.setVendor(mediatorVendor.getPostgreSQL());
                
            } else if (vendorsOrderByMatch.contains(mediatorVendor.getMySQL())) {
                
                mediatorVendor.setVendor(mediatorVendor.getMySQL());
                
            } else {
                
                mediatorVendor.setVendor(vendorsOrderByMatch.get(0));
            }
        }
    }

    private List<Vendor> getVendorsOrderByMatch(MediatorVendor mediatorVendor, String pageSource) {
        
        return
            mediatorVendor
            .getVendors()
            .stream()
            .filter(vendor -> vendor != mediatorVendor.getAuto())
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
                        .split("[\\r\\n]+")
                    )
                    .filter(errorMessage ->
                        Pattern
                        .compile(".*" + errorMessage + ".*", Pattern.DOTALL)
                        .matcher(pageSource)
                        .matches()
                    )
                    .findAny();
                
                if (optionalOrderByErrorMatch.isPresent()) {
                    
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_SUCCESS,
                        String.format("Order by fingerprint matching vendor [%s]", vendor)
                    );
                }
                
                return optionalOrderByErrorMatch.isPresent();
            })
            .collect(Collectors.toList());
    }

    private List<String> initializeCallables(CompletionService<CallablePageSource> taskCompletionService, String characterInsertionByUser, String[] charFromBooleanMatch) throws JSqlException {
        
        List<String> prefixValues = Arrays
            .asList(
                RandomStringUtils.random(10, "012"),
//                "-1",
                "1"
//                characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY),
//                StringUtils.EMPTY
            );
        
        List<String> prefixQuotes = Arrays.asList(
            LABEL_PREFIX,
            LABEL_PREFIX +"'",
            LABEL_PREFIX +"\"",
            LABEL_PREFIX +"%bf'"
//                "prefix`",
//                "'prefix'"
//                "`prefix`",
//                "\"prefix\"",
        );
        
        List<String> prefixParentheses = Arrays.asList(StringUtils.EMPTY, ")", "))");
        
        List<String> charactersInsertion = new ArrayList<>();
        
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting character insertion with Boolean match...");
        for (String prefixValue: prefixValues) {
            
            for (String prefixQuote: prefixQuotes) {
                
                for (String prefixParenthesis: prefixParentheses) {
                    
                    this.checkInsertionChar(charFromBooleanMatch, charactersInsertion, prefixValue, prefixQuote, prefixParenthesis);
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
                    "prefix#orderby"
                )
            );
        }
        
        return charactersInsertion;
    }

    private void checkInsertionChar(
        String[] charFromBooleanMatch,
        List<String> charactersInsertion,
        String prefixValue,
        String prefixQuote,
        String prefixParenthesis
    ) throws StoppedByUserSlidingException {

        String characterInsertion = prefixQuote.replace(LABEL_PREFIX, prefixValue) + prefixParenthesis;
        charactersInsertion.add(characterInsertion);
        
        // Skipping Boolean match when already found
        if (charFromBooleanMatch[0] == null) {
            
            var injectionCharInsertion = new InjectionCharInsertion(
                this.injectionModel,
                characterInsertion,
                prefixQuote + prefixParenthesis
            );
            if (injectionCharInsertion.isInjectable()) {
   
                if (this.isSuspended()) {
                    throw new StoppedByUserSlidingException();
                }
                
                charFromBooleanMatch[0] = characterInsertion;
                LOGGER.log(
                    LogLevelUtil.CONSOLE_SUCCESS,
                    "Found character insertion [{}] using Boolean match",
                    () -> charFromBooleanMatch[0]
                );
            }
        }
    }
    
    private String getCharacterInsertion(String characterInsertionByUser, String characterInsertionDetected) {
        
        String characterInsertionDetectedFixed = characterInsertionDetected;
        
        if (characterInsertionDetectedFixed == null) {
            
            if (StringUtils.isEmpty(characterInsertionByUser) || InjectionModel.STAR.equals(characterInsertionByUser)) {
                
                characterInsertionDetectedFixed = "1";
                
            } else {
                
                characterInsertionDetectedFixed = characterInsertionByUser;
            }
            
            String logCharacterInsertion = characterInsertionDetectedFixed;
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "No character insertion found, forcing to [{}]",
                () -> logCharacterInsertion.replace(InjectionModel.STAR, StringUtils.EMPTY)
            );
            
        } else if (!characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY).equals(characterInsertionDetectedFixed)) {
            
            String characterInsertionByUserFormat = characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY);
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "Using [{}] and [{}]",
                () -> this.injectionModel.getMediatorVendor().getVendor(),
                () -> characterInsertionDetected
            );
            LOGGER.log(
                LogLevelUtil.CONSOLE_DEFAULT,
                "Add manually the character * like [{}*] to force the value [{}]",
                () -> characterInsertionByUserFormat,
                () -> characterInsertionByUserFormat
            );
            
        } else {
            
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{} [{}]",
                () -> I18nUtil.valueByKey("LOG_USING_INSERTION_CHARACTER"),
                () -> characterInsertionDetected.replace(InjectionModel.STAR, StringUtils.EMPTY)
            );
        }
        
        // Encoded space required for integer insertion
        // Fail on neo4j when plain space ' '
        return characterInsertionDetectedFixed.replace(InjectionModel.STAR, "+" + InjectionModel.STAR);
    }
}