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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrategyInjectionStacked extends AbstractStrategy {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String performanceLength = "0";

    public StrategyInjectionStacked(InjectionModel injectionModel) {
        
        super(injectionModel);
    }

    @Override
    public void checkApplicability() {

        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isStrategyStackedDisabled()) {

            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Skipping strategy Stacked disabled");
            return;
        }

        // Reset applicability of new Vendor
        this.isApplicable = false;

        var strategyYaml = this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy();

        var configurationYaml = strategyYaml.getConfiguration();

        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "{} Stacked...", () -> I18nUtil.valueByKey("LOG_CHECKING_STRATEGY"));

        boolean methodIsApplicable = this.isApplicable(configurationYaml, strategyYaml.getStacked());

        if (methodIsApplicable) {

            Matcher regexSearch = this.getPerformance(configurationYaml, strategyYaml.getStacked());

            if (!regexSearch.find()) {

                LOGGER.log(
                    LogLevelUtil.CONSOLE_ERROR,
                    "{} {} but injectable size is incorrect",
                    () -> I18nUtil.valueByKey("LOG_VULNERABLE"),
                    () -> "Stacked"
                );

                methodIsApplicable = false;
            } else {
                this.performanceLength = "" + regexSearch.group(1).length();
            }
        }

        if (methodIsApplicable) {

            LOGGER.log(
                LogLevelUtil.CONSOLE_SUCCESS,
                "{} Stacked injection using [{}] characters",
                () -> I18nUtil.valueByKey("LOG_VULNERABLE"),
                () -> this.performanceLength
            );

            this.allow();

        } else {

            this.unallow();
        }
    }

    private boolean isApplicable(Configuration configurationYaml, String stacked) {
        
        var methodIsApplicable = false;
      
        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            StringUtils.SPACE
            + VendorYaml.replaceTags(
                stacked
                .replace(VendorYaml.WINDOW, configurationYaml.getSlidingWindow())
                .replace(VendorYaml.INJECTION, configurationYaml.getFailsafe().replace("${indice}","0"))
                .replace(VendorYaml.WINDOW_CHAR, "1")
                .replace(VendorYaml.CAPACITY, "65565")
            ),
            "stacked#confirm"
        );
   
        // TODO Set static value in DataAccess
        if (performanceSourcePage.matches("(?s).*133707331.*")) {
            methodIsApplicable = true;
            this.isApplicable = true;
        }
        
        return methodIsApplicable;
    }

    private Matcher getPerformance(Configuration configurationYaml, String stacked) {
        
        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            StringUtils.SPACE
            + VendorYaml.replaceTags(
                stacked
                .replace(VendorYaml.WINDOW, configurationYaml.getSlidingWindow())
                .replace(VendorYaml.INJECTION, configurationYaml.getCalibrator())
                .replace(VendorYaml.WINDOW_CHAR, "1")
                .replace(VendorYaml.CAPACITY, "65565")
            ),
            "stacked#size"
        );
        
        return Pattern.compile("(?s)"+ DataAccess.LEAD +"(#+)").matcher(performanceSourcePage);
    }

    @Override
    public void allow(int... i) {
        
        this.markVulnerability(Interaction.MARK_STACKED_VULNERABLE);
    }

    @Override
    public void unallow(int... i) {
        
        this.markVulnerability(Interaction.MARK_STACKED_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) {
        
        return this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlStacked(sqlQuery, startPosition),
            metadataInjectionProcess
        );
    }

    @Override
    public void activateStrategy() {

        LOGGER.log(
            LogLevelUtil.CONSOLE_INFORM,
            "{} [{}]",
            () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
            this::getName
        );
        this.injectionModel.getMediatorStrategy().setStrategy(this.injectionModel.getMediatorStrategy().getStacked());

        var request = new Request();
        request.setMessage(Interaction.MARK_STACKED_STRATEGY);
        this.injectionModel.sendToViews(request);
    }
    
    public String getPerformanceLength() {
        return "65565";
    }

    @Override
    public String getName() {
        return "Stacked";
    }
}
