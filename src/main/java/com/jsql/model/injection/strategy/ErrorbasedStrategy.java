package com.jsql.model.injection.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.accessible.DataAccess;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.vendor.Model.Strategy.Error.Method;
import com.jsql.model.injection.vendor.VendorXml;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Injection strategy using error attack.
 */
public class ErrorbasedStrategy extends AbstractStrategy {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public String[] tabCapacityMethod;
    
    public int indexMethodByUser = 0;

    @Override
    public void checkApplicability() {
        // Reset applicability of new Vendor
        this.isApplicable = false;
        this.tabCapacityMethod = null;
        
        if (
            MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getError() == null
        ) {
            LOGGER.info("No Error based strategy known for "+ MediatorModel.model().vendor +".");
            return;
        } else {
            LOGGER.trace("Error based test...");
        }
        
        this.tabCapacityMethod = new String[MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getError().getMethod().size()];
        int indexErrorMethod = 0;
        int errorCapacity = 0;
        for (Method errorMethod: MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getError().getMethod()) {
            boolean methodIsApplicable = false;
            LOGGER.trace("Testing "+ errorMethod.getName() +"...");
        
            String performanceSourcePage = MediatorModel.model().injectWithoutIndex(
                MediatorModel.model().getCharInsertion() + 
                " "+ VendorXml.replaceTags(
                    errorMethod.getQuery()
                    .replace("${WINDOW}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getSlidingWindow())
                    .replace("${INJECTION}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getFailsafe().replace("${INDICE}","0"))
                    .replace("${INDEX}", "1")
                    .replace("${CAPACITY}", Integer.toString(errorMethod.getCapacity()))
                )
            );
    
            if (performanceSourcePage.matches(
                VendorXml.replaceTags(
                    "(?s).*"+ 
                    MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getFailsafe()
                    .replace("${INDICE}","0")
                    .replace("0%2b1", "1") +".*"
                )
            )) {
                methodIsApplicable = true;
                this.isApplicable = true;
            }
            
            if (methodIsApplicable) {
                LOGGER.debug("Vulnerable to "+ errorMethod.getName());
                this.allow(indexErrorMethod);
                
                String performanceErrorBasedSourcePage = MediatorModel.model().injectWithoutIndex(
                    MediatorModel.model().getCharInsertion() 
                    + " " 
                    + VendorXml.replaceTags(
                        errorMethod.getQuery()
                        .replace("${WINDOW}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getSlidingWindow())
                        .replace("${INJECTION}", MediatorModel.model().vendor.instance().getXmlModel().getStrategy().getConfiguration().getCalibrator())
                        .replace("${INDEX}", "1")
                        .replace("${CAPACITY}", Integer.toString(errorMethod.getCapacity()))
                    )
                );
                
                Matcher regexSearch = Pattern.compile("(?s)"+ DataAccess.LEAD +"(#+)").matcher(performanceErrorBasedSourcePage);
                while (regexSearch.find()) {
                    if (errorCapacity < regexSearch.group(1).length()) {
                        indexMethodByUser = indexErrorMethod;
                    }
                    errorCapacity = regexSearch.group(1).length();
                    this.tabCapacityMethod[indexErrorMethod] = Integer.toString(errorCapacity);
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
    public String inject(String sqlQuery, String startPosition, /*TODO parametre inutile? */AbstractSuspendable<String> stoppable) throws StoppedByUserSlidingException {
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
        return tabCapacityMethod[indexMethodByUser] +"" ;
    }
    
    @Override
    public String getName() {
        return "Error based";
    }
    
    @Override
    public Integer getIndexMethodByUser() {
        return indexMethodByUser;
    }
    
    public void setIndexMethodByUser(int i) {
        indexMethodByUser = i;
    }
    
}
