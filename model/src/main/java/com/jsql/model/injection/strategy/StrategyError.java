package com.jsql.model.injection.strategy;

import com.jsql.model.InjectionModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Request3;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrategyError extends AbstractStrategy {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private String[] tabCapacityMethod;
    
    private int indexErrorStrategy = 0;

    public StrategyError(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public void checkApplicability() {
        // Reset applicability of new Vendor
        this.isApplicable = false;
        
        var strategyYaml = this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy();

        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isStrategyErrorDisabled()) {
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, AbstractStrategy.FORMAT_SKIP_STRATEGY_DISABLED, this.getName());
            return;
        } else if (strategyYaml.getError().getMethod().isEmpty()) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                AbstractStrategy.FORMAT_STRATEGY_NOT_IMPLEMENTED,
                this.getName(),
                this.injectionModel.getMediatorVendor().getVendor()
            );
            return;
        }

        this.logChecking();

        this.tabCapacityMethod = new String[strategyYaml.getError().getMethod().size()];
        var indexErrorMethod = 0;
        var errorCapacity = 0;
        
        for (Method errorMethod: strategyYaml.getError().getMethod()) {
            boolean methodIsApplicable = this.isApplicable(errorMethod);
            if (methodIsApplicable) {
                Matcher regexSearch = this.getPerformance(errorMethod);
                if (regexSearch.find()) {
                    errorCapacity = this.getCapacity(indexErrorMethod, errorCapacity, errorMethod, regexSearch);
                } else {
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_ERROR,
                        "{} {} but injectable size is incorrect",
                        () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_VULNERABLE),
                        errorMethod::getName
                    );
                    methodIsApplicable = false;
                }
            }
            
            if (methodIsApplicable) {
                this.allow(indexErrorMethod);
            } else {
                this.unallow(indexErrorMethod);
            }
            indexErrorMethod++;
        }
    }

    private boolean isApplicable(Method errorMethod) {
        var methodIsApplicable = false;

        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlErrorIndice(errorMethod),
            "error#confirm"
        );

        var indexZeroToFind = "0";
        String regexIndexZero = String.format(VendorYaml.FORMAT_INDEX, indexZeroToFind);
        if (performanceSourcePage.matches("(?s).*"+ regexIndexZero +".*")) {
            methodIsApplicable = true;
            this.isApplicable = true;
        }
        return methodIsApplicable;
    }

    private Matcher getPerformance(Method errorMethod) {
        String performanceErrorSourcePage = this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlErrorCalibrator(errorMethod),
            "error#size"
        );
        return Pattern.compile("(?s)"+ DataAccess.LEAD +"("+ VendorYaml.CALIBRATOR_SQL +"+)").matcher(performanceErrorSourcePage);
    }

    private int getCapacity(int indexErrorMethod, int errorCapacityDefault, Method errorMethod, Matcher regexSearch) {
        int errorCapacityImproved = errorCapacityDefault;
        
        regexSearch.reset();
        while (regexSearch.find()) {
            if (errorCapacityImproved < regexSearch.group(1).length()) {
                this.indexErrorStrategy = indexErrorMethod;
            }
            errorCapacityImproved = regexSearch.group(1).length();
            this.tabCapacityMethod[indexErrorMethod] = Integer.toString(errorCapacityImproved);
        }
        
        int logErrorCapacityImproved = errorCapacityImproved;
        LOGGER.log(
            LogLevelUtil.CONSOLE_SUCCESS,
            "{} [Error {}] showing [{}] characters",
            () -> I18nUtil.valueByKey(AbstractStrategy.KEY_LOG_VULNERABLE),
            errorMethod::getName,
            () -> Integer.toString(logErrorCapacityImproved)
        );
        
        return errorCapacityImproved;
    }

    @Override
    public void allow(int... indexError) {
        this.injectionModel.appendAnalysisReport(
            StringUtil.formatReport(
                LogLevelUtil.COLOR_BLU,
                "### Strategy: "+ this.getName() +":"+ this.injectionModel.getMediatorVendor().getVendor().instance()
                .getModelYaml()
                .getStrategy()
                .getError()
                .getMethod()
                .get(indexError[0])
                .getName()
            )
            + this.injectionModel.getReportWithoutIndex(
                this.injectionModel.getMediatorVendor().getVendor().instance().sqlError(
                    StringUtil.formatReport(LogLevelUtil.COLOR_GREEN, "&lt;query&gt;"),
                    "0",
                    indexError[0],
                    true
                ),
                "metadataInjectionProcess"
            )
        );
        this.injectionModel.sendToViews(new Request3.MarkErrorVulnerable(indexError[0], this));
    }

    @Override
    public void unallow(int... indexError) {
        this.injectionModel.sendToViews(new Request3.MarkErrorInvulnerable(indexError[0], this));
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) {
        return this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlError(sqlQuery, startPosition, this.indexErrorStrategy, false),
            metadataInjectionProcess
        );
    }

    @Override
    public void activateWhenApplicable() {
        if (this.injectionModel.getMediatorStrategy().getStrategy() == null && this.isApplicable()) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                "{} [{} {}]",
                () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
                this::getName,
                () -> this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy()
                .getError().getMethod().get(this.indexErrorStrategy).getName()
            );
            this.injectionModel.getMediatorStrategy().setStrategy(this);
            this.injectionModel.sendToViews(new Request3.MarkErrorStrategy(this));
        }
    }
    
    @Override
    public String getPerformanceLength() {
        return this.tabCapacityMethod[this.indexErrorStrategy];
    }
    
    @Override
    public String getName() {
        return "Error";
    }
    
    public Integer getIndexErrorStrategy() {
        return this.indexErrorStrategy;
    }
    
    public void setIndexErrorStrategy(int indexErrorStrategy) {
        this.indexErrorStrategy = indexErrorStrategy;
    }
}
