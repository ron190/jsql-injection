package com.jsql.model.injection.strategy;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.suspendable.SuspendableGetCharInsertion;
import com.jsql.util.StringUtil;

public class MediatorStrategy {
    
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
    
    public String getMeta() {
        
        String strategyName = this.strategy == null ? StringUtils.EMPTY : this.strategy.toString().toLowerCase();
        
        var strategyMode = "default";
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isDiosStrategy()) {
            
            strategyMode = "dios";
            
        } else if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isZipStrategy()) {
            
            strategyMode = "zip";
        }
        
        return String.format("%s#%s", strategyName, strategyMode);
    }
    
    /**
     * Build correct data for GET, POST, HEADER.
     * Each can be either raw data (no injection), SQL query without index requirement,
     * or SQL query with index requirement.
     * @param urlBase Beginning of the request data
     * @param isUsingIndex False if request doesn't use indexes
     * @param sqlTrail SQL statement
     * @return Final data
     */
    public String buildURL(String urlBase, boolean isUsingIndex, String sqlTrail) {
        
        String result = urlBase;
        
        if (urlBase.contains(InjectionModel.STAR)) {
            
            if (!isUsingIndex) {
                
                result = urlBase.replace(InjectionModel.STAR, this.extracted(sqlTrail));
                
            } else {
                
                result =
                    urlBase
                    .replace(
                        InjectionModel.STAR,
                        this.extracted(
                            this.injectionModel.getIndexesInUrl().replaceAll(
                                "1337" + this.normal.getVisibleIndex() + "7331",
                                /**
                                 * Oracle column often contains $, which is reserved for regex.
                                 * => need to be escape with quoteReplacement()
                                 */
                                Matcher.quoteReplacement(sqlTrail)
                            )
                        )
                    );
            }
        }
        
        return result;
    }

    private String extracted(String sqlTrail) {
        
        return
            StringUtil.clean(sqlTrail)
            .replace("\"", "%22")
            .replace("'", "%27")
            .replace("(", "%28")
            .replace(")", "%29")
            .replace("{", "%7B")
            .replace("[", "%5B")
            .replace("|", "%7C")
            .replace("`", "%60")
            .replace("]", "%5D")
            .replace("}", "%7D")
            .replace(">", "%3E")
            .replace("<", "%3C")
            .replace("?", "%3F")
            .replace("_", "%5F")
            .replace("\\", "%5C")
            .replace(",", "%2C")
            .replace(StringUtils.SPACE, "%20")
            .replace("+", "%20")
            + this.injectionModel.getMediatorVendor().getVendor().instance().endingComment().replace("+", "%20")
            ;
    }
    
    /**
     * Find the insertion character, test each strategy, inject metadata and list databases.
     * @param parameterToInject to be tested, null when injection point
     * @return true when successful injection
     * @throws JSqlException when no params' integrity, process stopped by user, or injection failure
     */
    public boolean testStrategies(SimpleEntry<String, String> parameterToInject) throws JSqlException {
        
        // Define insertionCharacter, i.e, -1 in "[..].php?id=-1 union select[..]",
        
        String parameterOriginalValue = null;
        
        // Fingerprint database
        this.injectionModel.getMediatorVendor().setVendor(this.injectionModel.getMediatorVendor().fingerprintVendor());
        
        // If not an injection point then find insertion character.
        // Force to 1 if no insertion char works and empty value from user,
        // Force to user's value if no insertion char works,
        // Force to insertion char otherwise.
        // parameterToInject null on true STAR injection
        // TODO Use also on Json injection where parameter == null
        if (parameterToInject != null) {
            
            parameterOriginalValue = parameterToInject.getValue();
                     
            // Test for params integrity
            String characterInsertionByUser = this.injectionModel.getMediatorUtils().getParameterUtil().initializeStar(parameterToInject);
            
            String characterInsertion = new SuspendableGetCharInsertion(this.injectionModel).run(characterInsertionByUser);
            
            if (characterInsertion.contains(InjectionModel.STAR)) {
                
                // When injecting all parameters or JSON
                parameterToInject.setValue(characterInsertion);
                
            } else {
                
                // When injecting last parameter
                parameterToInject.setValue(characterInsertion +"+"+ InjectionModel.STAR);
            }
        }

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
            
            // Restore initial parameter value on injection failure
            // Only when not true STAR injection
            if (parameterOriginalValue != null) {
                
                parameterToInject.setValue(parameterOriginalValue.replace(InjectionModel.STAR, StringUtils.EMPTY));
            }
            
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
