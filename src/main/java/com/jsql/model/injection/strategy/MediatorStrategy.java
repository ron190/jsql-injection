package com.jsql.model.injection.strategy;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.suspendable.SuspendableGetCharInsertion;
import com.jsql.model.suspendable.SuspendableGetVendor;
import com.jsql.util.I18nUtil;
import com.jsql.util.JsonUtil;

public class MediatorStrategy {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private AbstractStrategy time;
    private AbstractStrategy blind;
    private StrategyInjectionError error;
    private AbstractStrategy normal;
    
    private List<AbstractStrategy> strategies;
    
    /**
     * Current injection strategy.
     */
    private AbstractStrategy strategy;

    private InjectionModel injectionModel;
    
    public MediatorStrategy(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
        
        this.time = new StrategyInjectionTime(this.injectionModel);
        this.blind = new StrategyInjectionBlind(this.injectionModel);
        this.error = new StrategyInjectionError(this.injectionModel);
        this.normal = new StrategyInjectionNormal(this.injectionModel);
        
        this.strategies = Arrays.asList(this.time, this.blind, this.error, this.normal);
    }
    
    /**
     * Build correct data for GET, POST, HEADER.
     * Each can be either raw data (no injection), SQL query without index requirement,
     * or SQL query with index requirement.
     * @param dataType Current method to build
     * @param urlBase Beginning of the request data
     * @param isUsingIndex False if request doesn't use indexes
     * @param sqlTrail SQL statement
     * @return Final data
     */
    public String buildURL(String urlBase, boolean isUsingIndex, String sqlTrail) {
        
        if (urlBase.contains(InjectionModel.STAR)) {
            
            if (!isUsingIndex) {
                
                return urlBase.replace(InjectionModel.STAR, sqlTrail);
                
            } else {
                
                return
                    urlBase.replace(
                        InjectionModel.STAR,
                        this.injectionModel.getIndexesInUrl().replaceAll(
                            "1337" + this.normal.getVisibleIndex() + "7331",
                            /**
                             * Oracle column often contains $, which is reserved for regex.
                             * => need to be escape with quoteReplacement()
                             */
                            Matcher.quoteReplacement(sqlTrail)
                        )
                    )
                ;
            }
        }
        
        return urlBase;
    }
    
    /**
     * Find the insertion character, test each strategy, inject metadata and list databases.
     * @param isNotInjectionPoint true if mode standard/JSON/full, false if injection point
     * @param isJson true if param contains JSON
     * @param parameterToInject to be tested, null when injection point
     * @return true when successful injection
     * @throws JSqlException when no params' integrity, process stopped by user, or injection failure
     */
    public boolean testStrategies(SimpleEntry<String, String> parameterToInject) throws JSqlException {
        
        // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
        LOGGER.trace(I18nUtil.valueByKey("LOG_GET_INSERTION_CHARACTER"));
        
        // If not an injection point then find insertion character.
        // Force to 1 if no insertion char works and empty value from user,
        // Force to user's value if no insertion char works,
        // Force to insertion char otherwise.
        // TODO Use also on Json injection where parameter == null
        if (parameterToInject != null) {
            
            // Test for params integrity
            String characterInsertionByUser = this.injectionModel.getMediatorUtils().getParameterUtil().initializeStar(parameterToInject);
            
            String characterInsertion = new SuspendableGetCharInsertion(this.injectionModel).run(characterInsertionByUser);
            
            // TODO double star on normal last param, not on json or error
            if (!JsonUtil.isJson(parameterToInject.getValue())) {
                characterInsertion = characterInsertion + InjectionModel.STAR;
            }
            
            parameterToInject.setValue(characterInsertion);
            
            LOGGER.info(
                I18nUtil.valueByKey("LOG_USING_INSERTION_CHARACTER")
                + " ["
                + characterInsertion.replace(InjectionModel.STAR, StringUtils.EMPTY).replaceAll("\\+$", StringUtils.EMPTY)
                + "]"
            );
        }
        
        // Fingerprint database
        this.injectionModel.getMediatorVendor().setVendor(new SuspendableGetVendor(this.injectionModel).run());

        // Test each injection strategies: time < blind < error < normal
        // Choose the most efficient strategy: normal > error > blind > time
        this.time.checkApplicability();
        this.blind.checkApplicability();
        this.error.checkApplicability();
        this.normal.checkApplicability();

        // Choose the most efficient strategy: normal > error > blind > time
        if (this.normal.isApplicable()) {
            
            this.normal.activateStrategy();
            
        } else if (this.error.isApplicable()) {
            
            this.error.activateStrategy();
            
        } else if (this.blind.isApplicable()) {
            
            this.blind.activateStrategy();
            
        } else if (this.time.isApplicable()) {
            
            this.time.activateStrategy();
            
        } else {
            
            throw new InjectionFailureException("No injection found");
        }
        
        return true;
    }
    
    // Getter and setter

    public AbstractStrategy getNormal() {
        return this.normal;
    }

    public StrategyInjectionError getError() {
        return this.error;
    }

    public AbstractStrategy getBlind() {
        return this.blind;
    }

    public AbstractStrategy getTime() {
        return this.time;
    }

    public List<AbstractStrategy> getStrategies() {
        return this.strategies;
    }

    public AbstractStrategy getStrategy() {
        return this.strategy;
    }

    public void setStrategy(AbstractStrategy strategy) {
        this.strategy = strategy;
    }
}
