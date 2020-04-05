package com.jsql.model.injection.method;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.util.JsonUtil;

@SuppressWarnings("serial")
public abstract class MethodInjection implements Serializable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    protected InjectionModel injectionModel;
    
    public MethodInjection(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    
    public abstract boolean isCheckingAllParam();
    public abstract String getParamsAsString();
    public abstract List<SimpleEntry<String, String>> getParams();
    public abstract String name();
    
    /**
     * Verify if injection works for specific Method using 3 modes: standard (last param), injection point
     * and full params injection. Special injections like JSON and SOAP are checked.
     * @param methodInjection currently tested (Query, Request or Header)
     * @param paramsAsString to verify if contains injection point
     * @param params from Query, Request or Header as a list of key/value to be tested for insertion character ;
     * Mode standard: last param, mode injection point: no test, mode full: every params.
     * @return true if injection didn't failed
     * @throws JSqlException when no params' integrity, process stopped by user, or injection failure
     */
    public boolean testParameters() throws JSqlException {
        
        boolean hasFoundInjection = false;
        
        // Injects URL, Request or Header params only if user tests every params
        // or method is selected by user.
        if (
            !this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllParam()
            && this.injectionModel.getMediatorUtils().getConnectionUtil().getMethodInjection() != this
        ) {
            return hasFoundInjection;
        }
        
        // Force injection method of model to current running method
        this.injectionModel.getMediatorUtils().getConnectionUtil().setMethodInjection(this);
        
        // Injection by injection point
        if (this.getParamsAsString().contains(InjectionModel.STAR)) {
            
            LOGGER.info("Checking single "+ this.name() +" parameter with injection point at [*]");
            
            // Will keep param value as is,
            // Does not test for insertion character (param is null)
            hasFoundInjection = this.injectionModel.getMediatorStrategy().testStrategies(null);
            
        } else if (!this.isCheckingAllParam()) {
            // Default injection: last param tested only
            
            // Injection point defined on last parameter
            // TODO ADD STAR
            this.getParams().stream().reduce((a, b) -> b).ifPresent(e -> e.setValue(e.getValue() + InjectionModel.STAR));

            // Will check param value by user.
            // Notice options 'Inject each URL params' and 'inject JSON' must be checked both
            // for JSON injection of last param
            SimpleEntry<String, String> parameterToInject = this.getParams().stream().reduce((a, b) -> b).orElseThrow(NullPointerException::new);

            hasFoundInjection = this.injectionModel.getMediatorStrategy().testStrategies(parameterToInject);
            
        } else {
            // Injection of every params: isCheckingAllParam() == true.
            // Params are tested one by one in two loops:
            // - inner loop erases * from previous param
            // - outer loop adds * to current param
            
            // This param will be marked by * if injection is found,
            // inner loop will erase mark * otherwise
            injectionSuccessful:
            for (SimpleEntry<String, String> paramBase: this.getParams()) {

                // This param is the current tested one.
                // For JSON value attributes are traversed one by one to test every values.
                // For standard value mark * is simply added to the end of its value.
                for (SimpleEntry<String, String> paramStar: this.getParams()) {
                    
                    if (paramStar == paramBase) {
                        
                        try {
                            // Will test if current value is a JSON entity
                            Object jsonEntity = JsonUtil.getJson(paramStar.getValue());
                            
                            // Define a tree of JSON attributes with path as the key: root.a => value of a
                            List<SimpleEntry<String, String>> attributesJson = JsonUtil.createEntries(jsonEntity, "root", null);
                            
                            // When option 'Inject JSON' is selected and there's a JSON entity to inject
                            // then loop through each paths to add * at the end of value and test each strategies.
                            // Marks * are erased between each tests.
                            if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllJSONParam() && !attributesJson.isEmpty()) {
                                
                                hasFoundInjection = this.injectionModel.getMediatorUtils().getJsonUtil().testJsonParameter(this, paramStar);
                                
                            } else {
                                
                                // Standard non JSON injection
                                hasFoundInjection = this.testStandardParameter(paramStar);
                            }
                            
                            if (hasFoundInjection) {
                                break injectionSuccessful;
                            }
                        } catch (JSONException e) {
                            LOGGER.error("Error parsing JSON parameters", e);
                        }
                    }
                }
            }
        }
        
        return hasFoundInjection;
    }
    
    public boolean testStandardParameter(SimpleEntry<String, String> paramStar) {
        
        boolean hasFoundInjection = false;
        
        // Add * to end of value
        // TODO ADD STAR
        paramStar.setValue(paramStar.getValue() + InjectionModel.STAR);
        
        try {
            LOGGER.info("Checking "+ this.name() +" parameter "+ paramStar.getKey() +"="+ paramStar.getValue().replace(InjectionModel.STAR, ""));
            
            // Test current standard value marked with * for injection
            // Keep original param
            hasFoundInjection = this.injectionModel.getMediatorStrategy().testStrategies(paramStar);
            
        } catch (JSqlException e) {
            
            // Injection failure
            LOGGER.warn(
                "No "+ this.name() +" injection found for parameter "
                + paramStar.getKey() +"="+ paramStar.getValue().replace(InjectionModel.STAR, "")
                + " (" + e +")", e
            );
            
        } finally {
            
            // Erase * from JSON if failure
            if (!hasFoundInjection) {
                
                // Erase * at the end of each params
                this.getParams().stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", "")));
                
                // TODO It erases STAR from value => * can't be used in parameter
                paramStar.setValue(paramStar.getValue().replace("*", ""));
            }
        }
        
        return hasFoundInjection;
    }
}