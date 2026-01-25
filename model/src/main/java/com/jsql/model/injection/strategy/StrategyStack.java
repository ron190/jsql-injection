package com.jsql.model.injection.strategy;

import com.jsql.model.InjectionModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Request3;
import com.jsql.model.injection.engine.model.EngineYaml;
import com.jsql.model.injection.engine.model.yaml.Configuration;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrategyStack extends AbstractStrategy {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private String performanceLength = "0";

    public StrategyStack(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public void checkApplicability() {
        if (this.injectionModel.getMediatorUtils().preferencesUtil().isStrategyStackDisabled()) {
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, AbstractStrategy.FORMAT_SKIP_STRATEGY_DISABLED, this.getName());
            return;
        }

        // Reset applicability of new engine
        this.isApplicable = false;
        var strategyYaml = this.injectionModel.getMediatorEngine().getEngine().instance().getModelYaml().getStrategy();
        var configurationYaml = strategyYaml.getConfiguration();

        this.logChecking();

        boolean methodIsApplicable = this.isApplicable(configurationYaml, strategyYaml.getStack());
        if (methodIsApplicable) {
            Matcher regexSearch = this.getPerformance(configurationYaml, strategyYaml.getStack());
            if (!regexSearch.find()) {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_ERROR,
                    "{} {} but injectable size is incorrect",
                    () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_VULNERABLE),
                    () -> "Stack"
                );
                methodIsApplicable = false;
            } else {
                this.performanceLength = String.valueOf(regexSearch.group(1).length());
            }
        }

        if (methodIsApplicable) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                "{} Stack injection showing [{}] characters",
                () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_VULNERABLE),
                () -> this.performanceLength
            );
            this.allow();
        } else {
            this.unallow();
        }
    }

    private boolean isApplicable(Configuration configurationYaml, String stack) {
        var methodIsApplicable = false;
        var indexZeroToFind = "0";
        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            EngineYaml.replaceTags(
                stack
                .replace(EngineYaml.WINDOW, configurationYaml.getSlidingWindow())
                .replace(EngineYaml.INJECTION, configurationYaml.getFailsafe().replace(EngineYaml.INDICE,indexZeroToFind))
                .replace(EngineYaml.WINDOW_CHAR, "1")
                .replace(EngineYaml.CAPACITY, EngineYaml.DEFAULT_CAPACITY)
            ),
            "stack#confirm"
        );
        String regexIndexZero = String.format(EngineYaml.FORMAT_INDEX, indexZeroToFind);
        if (performanceSourcePage.matches("(?s).*"+ regexIndexZero +".*")) {
            methodIsApplicable = true;
            this.isApplicable = true;
        }
        return methodIsApplicable;
    }

    private Matcher getPerformance(Configuration configurationYaml, String stack) {
        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            EngineYaml.replaceTags(
                stack
                .replace(EngineYaml.WINDOW, configurationYaml.getSlidingWindow())
                .replace(EngineYaml.INJECTION, configurationYaml.getCalibrator())
                .replace(EngineYaml.WINDOW_CHAR, "1")
                .replace(EngineYaml.CAPACITY, EngineYaml.DEFAULT_CAPACITY)
            ),
            "stack#size"
        );
        return Pattern.compile("(?s)"+ DataAccess.LEAD +"("+ EngineYaml.CALIBRATOR_SQL +"+)").matcher(performanceSourcePage);
    }

    @Override
    public void allow(int... i) {
        this.injectionModel.appendAnalysisReport(
            StringUtil.formatReport(LogLevelUtil.COLOR_BLU, "### Strategy: " + this.getName())
            + this.injectionModel.getReportWithoutIndex(
                this.injectionModel.getMediatorEngine().getEngine().instance().sqlStack(StringUtil.formatReport(LogLevelUtil.COLOR_GREEN, "&lt;query&gt;"), "0", true),
                "metadataInjectionProcess"
            )
        );
        this.injectionModel.sendToViews(new Request3.MarkVulnerable(this));
    }

    @Override
    public void unallow(int... i) {
        this.injectionModel.sendToViews(new Request3.MarkInvulnerable(this));
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) {
        return this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorEngine().getEngine().instance().sqlStack(sqlQuery, startPosition, false),
            metadataInjectionProcess
        );
    }

    @Override
    public void activateWhenApplicable() {
        if (this.injectionModel.getMediatorStrategy().getStrategy() == null && this.isApplicable()) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{} [{}]",
                () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
                this::getName
            );
            this.injectionModel.getMediatorStrategy().setStrategy(this);
            this.injectionModel.sendToViews(new Request3.ActivateStrategy(this));
        }
    }

    @Override
    public String getPerformanceLength() {
        return this.performanceLength;
    }

    @Override
    public String getName() {
        return "Stack";
    }
}
