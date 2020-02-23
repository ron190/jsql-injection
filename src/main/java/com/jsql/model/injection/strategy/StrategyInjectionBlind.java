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

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBoolean.BooleanMode;
import com.jsql.model.injection.strategy.blind.InjectionBlind;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Injection strategy using blind attack.
 */
public class StrategyInjectionBlind extends AbstractStrategy {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Blind injection object.
     */
    private InjectionBlind injectionBlind;
    
    public StrategyInjectionBlind(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public void checkApplicability() throws StoppedByUserSlidingException {
        
        if ("".equals(this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBooleanInitialization())) {
            LOGGER.info("No Blind strategy known for "+ this.injectionModel.getMediatorVendor().getVendor());
        } else {
            
            LOGGER.trace(I18n.valueByKey("LOG_CHECKING_STRATEGY") +" Blind with operator AND...");
            
            this.injectionBlind = new InjectionBlind(this.injectionModel, BooleanMode.AND);
            this.isApplicable = this.injectionBlind.isInjectable();
            
            if (!this.isApplicable) {
                LOGGER.trace(I18n.valueByKey("LOG_CHECKING_STRATEGY") +" Blind with operator OR...");
                
                this.injectionBlind = new InjectionBlind(this.injectionModel, BooleanMode.OR);
                this.isApplicable = this.injectionBlind.isInjectable();
                
                if (this.isApplicable) {
                    LOGGER.debug(I18n.valueByKey("LOG_VULNERABLE") +" Blind injection with operator OR");
                }
            } else {
                LOGGER.debug(I18n.valueByKey("LOG_VULNERABLE") +" Blind injection with operator AND");
            }
            
            if (this.isApplicable) {
                this.allow();
                
                Request requestMessageBinary = new Request();
                requestMessageBinary.setMessage(Interaction.MESSAGE_BINARY);
                requestMessageBinary.setParameters(this.injectionBlind.getInfoMessage());
                this.injectionModel.sendToViews(requestMessageBinary);
            } else {
                this.unallow();
            }
        }
    }

    @Override
    public void allow(int... i) {
        this.markVulnerability(Interaction.MARK_BLIND_VULNERABLE);
    }

    @Override
    public void unallow(int... i) {
        this.markVulnerability(Interaction.MARK_BLIND_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserSlidingException {
        
        return this.injectionBlind.inject(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlind(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        
        LOGGER.info(I18n.valueByKey("LOG_USING_STRATEGY") +" ["+ this.getName() +"]");
        this.injectionModel.getMediatorStrategy().setStrategy(this.injectionModel.getMediatorStrategy().getBlind());
        
        Request requestMarkBlindStrategy = new Request();
        requestMarkBlindStrategy.setMessage(Interaction.MARK_BLIND_STRATEGY);
        this.injectionModel.sendToViews(requestMarkBlindStrategy);
    }
    
    @Override
    public String getPerformanceLength() {
        return "65565";
    }
    
    @Override
    public String getName() {
        return "Blind";
    }
    
}
