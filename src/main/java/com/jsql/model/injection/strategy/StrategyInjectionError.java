package com.jsql.model.injection.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.injection.vendor.model.yaml.Configuration;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.injection.vendor.model.yaml.Strategy;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;

/**
 * Injection strategy using error attack.
 */
public class StrategyInjectionError extends AbstractStrategy {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private String[] tabCapacityMethod;
    
    private int indexMethod = 0;

    public StrategyInjectionError(InjectionModel injectionModel) {
        
        super(injectionModel);
    }

    @Override
    public void checkApplicability() {
        
        // Reset applicability of new Vendor
        this.isApplicable = false;
        this.tabCapacityMethod = null;
        
        Strategy strategyYaml = this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy();
        Configuration configurationYaml = strategyYaml.getConfiguration();
        
        if (strategyYaml.getError() == null) {
            
            LOGGER.info("No Error strategy known for "+ this.injectionModel.getMediatorVendor().getVendor());
            return;
        }

        LOGGER.trace(I18nUtil.valueByKey("LOG_CHECKING_STRATEGY") +" Error...");
        
        this.tabCapacityMethod = new String[strategyYaml.getError().getMethod().size()];
        int indexErrorMethod = 0;
        int errorCapacity = 0;
        
        for (Method errorMethod: strategyYaml.getError().getMethod()) {
            
            boolean methodIsApplicable = this.isApplicable(configurationYaml, errorMethod);
            
            if (methodIsApplicable) {
                
                Matcher regexSearch = this.getPerformance(configurationYaml, errorMethod);
                
                if (regexSearch.find()) {
                    
                    errorCapacity = this.getCapacity(indexErrorMethod, errorCapacity, errorMethod, regexSearch);
                    
                } else {
                    
                    LOGGER.warn(I18nUtil.valueByKey("LOG_VULNERABLE") + StringUtils.SPACE + errorMethod.getName() +" but injectable size is incorrect");
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

    private int getCapacity(int indexErrorMethod, int errorCapacityDefault, Method errorMethod, Matcher regexSearch) {
        
        int errorCapacityImproved = errorCapacityDefault;
        
        regexSearch.reset();
        
        while (regexSearch.find()) {
            
            if (errorCapacityImproved < regexSearch.group(1).length()) {
                
                this.indexMethod = indexErrorMethod;
            }
            
            errorCapacityImproved = regexSearch.group(1).length();
            this.tabCapacityMethod[indexErrorMethod] = Integer.toString(errorCapacityImproved);
        }
        
        LOGGER.debug(I18nUtil.valueByKey("LOG_VULNERABLE") + StringUtils.SPACE + errorMethod.getName() +" using "+ Integer.toString(errorCapacityImproved) +" characters");
        
        return errorCapacityImproved;
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
            )
        );
        
        return Pattern.compile("(?s)"+ DataAccess.LEAD +"(#+)").matcher(performanceErrorSourcePage);
    }

    private boolean isApplicable(Configuration configurationYaml, Method errorMethod) {
        
        LOGGER.trace(I18nUtil.valueByKey("LOG_CHECKING") + StringUtils.SPACE + errorMethod.getName() +"...");
        boolean methodIsApplicable = false;
      
        String performanceSourcePage = this.injectionModel.injectWithoutIndex(
            StringUtils.SPACE
            + VendorYaml.replaceTags(
                errorMethod
                .getQuery()
                .replace("${window}", configurationYaml.getSlidingWindow())
                .replace("${injection}", configurationYaml.getFailsafe().replace("${indice}","0"))
                .replace("${window.char}", "1")
                .replace("${capacity}", Integer.toString(errorMethod.getCapacity()))
            )
        );
   
        if (performanceSourcePage.matches(
            VendorYaml.replaceTags(
                "(?s).*"
                + configurationYaml.getFailsafe()
                .replace("${indice}","0")
                // TODO postgres
                .replace("0%2b1", "1")
                .replace("(133707331)::text", "133707331")
//                    .replace("(cast(133707331 as text))", "133707331")
                + ".*"
            )
        )) {
            methodIsApplicable = true;
            this.isApplicable = true;
        }
        
        return methodIsApplicable;
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
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserSlidingException {
        
        return this.injectionModel.injectWithoutIndex(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlError(sqlQuery, startPosition)
        );
    }

    @Override
    public void activateStrategy() {
        
        LOGGER.info(
            I18nUtil.valueByKey("LOG_USING_STRATEGY")
            +" ["
            + this.getName()
            + StringUtils.SPACE
            + this.injectionModel.getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getError().getMethod().get(this.indexMethod).getName()
            +"]"
        );
        this.injectionModel.getMediatorStrategy().setStrategy(this.injectionModel.getMediatorStrategy().getError());
        
        Request request = new Request();
        request.setMessage(Interaction.MARK_ERROR_STRATEGY);
        this.injectionModel.sendToViews(request);
    }
    
    @Override
    public String getPerformanceLength() {
        return this.tabCapacityMethod[this.indexMethod];
    }
    
    @Override
    public String getName() {
        return "Error";
    }
    
    public Integer getIndexMethodError() {
        return this.indexMethod;
    }
    
    public void setIndexMethod(int i) {
        this.indexMethod = i;
    }
}
