package com.jsql.model.injection.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.injection.vendor.model.yaml.Configuration;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;

/**
 * Injection strategy using error attack.
 */
public class StrategyInjectionError extends AbstractStrategy {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private String[] tabCapacityMethod;
    
    private int indexErrorStrategy = 0;

    public StrategyInjectionError(InjectionModel injectionModel) {
        
        super(injectionModel);
    }

    @Override
    public void checkApplicability() {
        
        // Reset applicability of new Vendor
        this.isApplicable = false;
        
        var strategyYaml = this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy();
        
        if (strategyYaml.getError().getMethod().isEmpty()) {
            
            LOGGER.log(LogLevel.CONSOLE_DEFAULT, "No Error strategy for {}", this.injectionModel.getMediatorVendor().getVendor());
            return;
        }
        
        var configurationYaml = strategyYaml.getConfiguration();
        
        LOGGER.log(LogLevel.CONSOLE_DEFAULT, "{} Error...", () -> I18nUtil.valueByKey("LOG_CHECKING_STRATEGY"));
        
        this.tabCapacityMethod = new String[strategyYaml.getError().getMethod().size()];
        var indexErrorMethod = 0;
        var errorCapacity = 0;
        
        for (Method errorMethod: strategyYaml.getError().getMethod()) {
            
            boolean methodIsApplicable = this.isApplicable(configurationYaml, errorMethod);
            
            if (methodIsApplicable) {
                
                Matcher regexSearch = this.getPerformance(configurationYaml, errorMethod);
                
                if (regexSearch.find()) {
                    
                    errorCapacity = this.getCapacity(indexErrorMethod, errorCapacity, errorMethod, regexSearch);
                    
                } else {
                    
                    LOGGER.log(
                        LogLevel.CONSOLE_ERROR,
                        "{} {} but injectable size is incorrect",
                        () -> I18nUtil.valueByKey("LOG_VULNERABLE"),
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

    private boolean isApplicable(Configuration configurationYaml, Method errorMethod) {
        
        var methodIsApplicable = false;
      
        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            StringUtils.SPACE
            + VendorYaml.replaceTags(
                errorMethod
                .getQuery()
                .replace("${window}", configurationYaml.getSlidingWindow())
                .replace("${injection}", configurationYaml.getFailsafe().replace("${indice}","0"))
                .replace("${window.char}", "1")
                .replace("${capacity}", Integer.toString(errorMethod.getCapacity()))
            ),
            "error#confirm"
        );
   
        if (performanceSourcePage.matches(
            VendorYaml.replaceTags(
                // TODO Set static value in DataAccess
                "(?s).*133707331.*"
            )
        )) {
            methodIsApplicable = true;
            this.isApplicable = true;
        }
        
        return methodIsApplicable;
    }

    private Matcher getPerformance(Configuration configurationYaml, Method errorMethod) {
        
        String performanceErrorSourcePage = this.injectionModel.injectWithoutIndex(
            StringUtils.SPACE
            + VendorYaml.replaceTags(
                errorMethod.getQuery()
                .replace("${window}", configurationYaml.getSlidingWindow())
                .replace("${injection}", configurationYaml.getCalibrator())
                .replace("${window.char}", "1")
                .replace("${capacity}", Integer.toString(errorMethod.getCapacity()))
            ),
            "error#size"
        );
        
        return Pattern.compile("(?s)"+ DataAccess.LEAD +"(#+)").matcher(performanceErrorSourcePage);
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
            LogLevel.CONSOLE_SUCCESS,
            "{} [Error {}] using [{}] characters",
            () -> I18nUtil.valueByKey("LOG_VULNERABLE"),
            errorMethod::getName,
            () -> Integer.toString(logErrorCapacityImproved)
        );
        
        return errorCapacityImproved;
    }

    @Override
    public void allow(int... indexError) {
        
        this.markVulnerability(Interaction.MARK_ERROR_VULNERABLE, indexError[0]);
    }

    @Override
    public void unallow(int... indexError) {
        
        this.markVulnerability(Interaction.MARK_ERROR_INVULNERABLE, indexError[0]);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) throws StoppedByUserSlidingException {
        
        return this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlError(sqlQuery, startPosition),
            metadataInjectionProcess
        );
    }

    @Override
    public void activateStrategy() {
        
        LOGGER.log(
            LogLevel.CONSOLE_INFORM,
            "{} [{} {}]",
            () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
            this::getName,
            () -> this.injectionModel
                .getMediatorVendor()
                .getVendor()
                .instance()
                .getModelYaml()
                .getStrategy()
                .getError()
                .getMethod()
                .get(this.indexErrorStrategy)
                .getName()
        );
        this.injectionModel.getMediatorStrategy().setStrategy(this.injectionModel.getMediatorStrategy().getError());
        
        var request = new Request();
        request.setMessage(Interaction.MARK_ERROR_STRATEGY);
        this.injectionModel.sendToViews(request);
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
