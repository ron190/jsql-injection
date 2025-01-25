package com.jsql.model.injection.strategy;

import com.jsql.model.InjectionModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.injection.vendor.model.yaml.Configuration;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrategyInjectionStack extends AbstractStrategy {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String performanceLength = "0";

    public StrategyInjectionStack(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public void checkApplicability() {
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isStrategyStackDisabled()) {
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, AbstractStrategy.FORMAT_SKIP_STRATEGY_DISABLED, this.getName());
            return;
        }

        // Reset applicability of new Vendor
        this.isApplicable = false;
        var strategyYaml = this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy();
        var configurationYaml = strategyYaml.getConfiguration();

        LOGGER.log(
            LogLevelUtil.CONSOLE_DEFAULT,
            AbstractStrategy.FORMAT_CHECKING_STRATEGY,
            () -> I18nUtil.valueByKey("LOG_CHECKING_STRATEGY"),
            this::getName
        );

        boolean methodIsApplicable = this.isApplicable(configurationYaml, strategyYaml.getStack());
        if (methodIsApplicable) {
            Matcher regexSearch = this.getPerformance(configurationYaml, strategyYaml.getStack());
            if (!regexSearch.find()) {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_ERROR,
                    "{} {} but injectable size is incorrect",
                    () -> I18nUtil.valueByKey("LOG_VULNERABLE"),
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
                () -> I18nUtil.valueByKey("LOG_VULNERABLE"),
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
            VendorYaml.replaceTags(
                stack
                .replace(VendorYaml.WINDOW, configurationYaml.getSlidingWindow())
                .replace(VendorYaml.INJECTION, configurationYaml.getFailsafe().replace(VendorYaml.INDICE,indexZeroToFind))
                .replace(VendorYaml.WINDOW_CHAR, "1")
                .replace(VendorYaml.CAPACITY, VendorYaml.DEFAULT_CAPACITY)
            ),
            "stack#confirm"
        );
        String regexIndexZero = String.format(VendorYaml.FORMAT_INDEX, indexZeroToFind);
        if (performanceSourcePage.matches("(?s).*"+ regexIndexZero +".*")) {
            methodIsApplicable = true;
            this.isApplicable = true;
        }
        return methodIsApplicable;
    }

    private Matcher getPerformance(Configuration configurationYaml, String stack) {
        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            VendorYaml.replaceTags(
                stack
                .replace(VendorYaml.WINDOW, configurationYaml.getSlidingWindow())
                .replace(VendorYaml.INJECTION, configurationYaml.getCalibrator())
                .replace(VendorYaml.WINDOW_CHAR, "1")
                .replace(VendorYaml.CAPACITY, VendorYaml.DEFAULT_CAPACITY)
            ),
            "stack#size"
        );
        return Pattern.compile("(?s)"+ DataAccess.LEAD +"("+ VendorYaml.CALIBRATOR_SQL +"+)").matcher(performanceSourcePage);
    }

    @Override
    public void allow(int... i) {
        this.injectionModel.appendAnalysisReport(
            StringUtil.formatReport(LogLevelUtil.COLOR_BLU, "### Strategy: " + this.getName())
            + this.injectionModel.getReportWithoutIndex(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlStack(StringUtil.formatReport(LogLevelUtil.COLOR_GREEN, "&lt;query&gt;"), "0", true),
                "metadataInjectionProcess"
            )
        );
        this.markVulnerability(Interaction.MARK_STACK_VULNERABLE);
    }

    @Override
    public void unallow(int... i) {
        this.markVulnerability(Interaction.MARK_STACK_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) {
        return this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlStack(sqlQuery, startPosition, false),
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
            this.injectionModel.getMediatorStrategy().setStrategy(this.injectionModel.getMediatorStrategy().getStack());

            var request = new Request();
            request.setMessage(Interaction.MARK_STACK_STRATEGY);
            this.injectionModel.sendToViews(request);
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
