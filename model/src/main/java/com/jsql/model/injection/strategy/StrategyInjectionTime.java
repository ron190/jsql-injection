/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.model.injection.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBoolean.BooleanMode;
import com.jsql.model.injection.strategy.blind.InjectionTime;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;

/**
 * Injection strategy using time attack.
 */
public class StrategyInjectionTime extends AbstractStrategy {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Injection method using time attack.
     */
    private InjectionTime injectionTime;
    
    public StrategyInjectionTime(InjectionModel injectionModel) {
        
        super(injectionModel);
    }

    @Override
    public void checkApplicability() throws StoppedByUserSlidingException {
        
        if (StringUtils.isEmpty(this.injectionModel.getMediatorVendor().getVendor().instance().sqlBooleanTime())) {
            
            LOGGER.info("No Time strategy known for "+ this.injectionModel.getMediatorVendor().getVendor());
            
        } else {
            
            LOGGER.trace(I18nUtil.valueByKey("LOG_CHECKING_STRATEGY") +" Time with AND...");
            
            this.injectionTime = new InjectionTime(this.injectionModel, BooleanMode.AND);
            this.isApplicable = this.injectionTime.isInjectable();
            
            if (!this.isApplicable) {
                
                LOGGER.trace(I18nUtil.valueByKey("LOG_CHECKING_STRATEGY") +" Time with OR...");
                
                this.injectionTime = new InjectionTime(this.injectionModel, BooleanMode.OR);
                this.isApplicable = this.injectionTime.isInjectable();
                
                if (this.isApplicable) {
                    
                    LOGGER.debug(I18nUtil.valueByKey("LOG_VULNERABLE") +" Time injection with OR");
                }
                
            } else {
                
                LOGGER.debug(I18nUtil.valueByKey("LOG_VULNERABLE") +" Time injection with AND");
            }
            
            if (this.isApplicable) {
                
                this.allow();
                
                Request requestMessageBinary = new Request();
                requestMessageBinary.setMessage(Interaction.MESSAGE_BINARY);
                requestMessageBinary.setParameters(this.injectionTime.getInfoMessage());
                this.injectionModel.sendToViews(requestMessageBinary);
                
            } else {
                
                this.unallow();
            }
        }
    }
    
    @Override
    public void allow(int... i) {
        
        this.markVulnerability(Interaction.MARK_TIME_VULNERABLE);
    }

    @Override
    public void unallow(int... i) {
        
        this.markVulnerability(Interaction.MARK_TIME_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) throws StoppedByUserSlidingException {
        
        return this.injectionTime.inject(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTime(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        
        if (this.injectionTime.getBooleanMode() == BooleanMode.OR) {
            
            LOGGER.info("Using OR statement, database optimizer's short-circuit can mess with Boolean strategies");

        } else {
            
            LOGGER.info("Using AND statement");
        }
        
        LOGGER.info(I18nUtil.valueByKey("LOG_USING_STRATEGY") +" ["+ this.getName() +"]");
        this.injectionModel.getMediatorStrategy().setStrategy(this.injectionModel.getMediatorStrategy().getTime());
        
        Request requestMarkTimeStrategy = new Request();
        requestMarkTimeStrategy.setMessage(Interaction.MARK_TIME_STRATEGY);
        this.injectionModel.sendToViews(requestMarkTimeStrategy);
    }
    
    @Override
    public String getPerformanceLength() {
        return "65565";
    }
    
    @Override
    public String getName() {
        return "Time";
    }
}
