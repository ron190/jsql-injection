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
import com.jsql.model.injection.strategy.blind.InjectionBlind;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;

/**
 * Injection strategy using blind attack.
 */
public class StrategyInjectionBlind extends AbstractStrategy {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Blind injection object.
     */
    private InjectionBlind injectionBlind;
    
    public StrategyInjectionBlind(InjectionModel injectionModel) {
        
        super(injectionModel);
    }

    @Override
    public void checkApplicability() throws StoppedByUserSlidingException {
        
        if (StringUtils.isEmpty(this.injectionModel.getMediatorVendor().getVendor().instance().sqlBooleanBlind())) {
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, "No Blind strategy known for {}", this.injectionModel.getMediatorVendor().getVendor());
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_DEFAULT, "{} Blind with AND...", () -> I18nUtil.valueByKey("LOG_CHECKING_STRATEGY"));
            
            this.injectionBlind = new InjectionBlind(this.injectionModel, BooleanMode.AND);
            this.isApplicable = this.injectionBlind.isInjectable();
            
            if (!this.isApplicable) {
                
                LOGGER.log(LogLevel.CONSOLE_DEFAULT, "{} Blind with OR...", () -> I18nUtil.valueByKey("LOG_CHECKING_STRATEGY"));
                
                this.injectionBlind = new InjectionBlind(this.injectionModel, BooleanMode.OR);
                this.isApplicable = this.injectionBlind.isInjectable();
                
                if (this.isApplicable) {
                    
                    LOGGER.log(LogLevel.CONSOLE_SUCCESS, "{} Blind injection with OR", () -> I18nUtil.valueByKey("LOG_VULNERABLE"));
                }
                
            } else {
                
                LOGGER.log(LogLevel.CONSOLE_SUCCESS, "{} Blind injection with AND", () -> I18nUtil.valueByKey("LOG_VULNERABLE"));
            }
            
            if (this.isApplicable) {
                
                this.allow();
                
                var requestMessageBinary = new Request();
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
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable, String metadataInjectionProcess) throws StoppedByUserSlidingException {
        
        return this.injectionBlind.inject(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlind(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        
        if (this.injectionBlind.getBooleanMode() == BooleanMode.OR) {
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, "Using OR statement, database optimizer\'s short-circuit can mess with Boolean strategies");

        } else {
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, "Using AND statement");
        }
        
        LOGGER.log(
            LogLevel.CONSOLE_INFORM,
            "{} [{}]",
            () -> I18nUtil.valueByKey("LOG_USING_STRATEGY"),
            this::getName
        );
        this.injectionModel.getMediatorStrategy().setStrategy(this.injectionModel.getMediatorStrategy().getBlind());
        
        var requestMarkBlindStrategy = new Request();
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
