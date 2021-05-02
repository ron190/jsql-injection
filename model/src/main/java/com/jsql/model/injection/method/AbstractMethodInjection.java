package com.jsql.model.injection.method;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.util.JsonUtil;
import com.jsql.util.LogLevel;

@SuppressWarnings("serial")
public abstract class AbstractMethodInjection implements Serializable {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    protected InjectionModel injectionModel;
    
    protected AbstractMethodInjection(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }
    
    public abstract boolean isCheckingAllParam();
    public abstract String getParamsAsString();
    public abstract List<SimpleEntry<String, String>> getParams();
    public abstract String name();
    
    /**
     * Verify if injection works for specific Method using 3 modes: standard (last param), injection point
     * and full params injection. Special injections like JSON and SOAP are checked.
     * @return true if injection didn't failed
     * @throws JSqlException when no params' integrity, process stopped by user, or injection failure
     */
    public boolean testParameters() throws JSqlException {
        
        var hasFoundInjection = false;
        
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
        
        // Injection by injection point in params or in path
        if (
            this.getParamsAsString().contains(InjectionModel.STAR)
            || this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlBase().contains(InjectionModel.STAR)
        ) {
            
            hasFoundInjection = this.checkParamWithStar();
            
        } else if (!this.isCheckingAllParam()) {
            
            hasFoundInjection = this.checkLastParam();
            
        } else {
            
            hasFoundInjection = this.checkAllParams();
        }
        
        return hasFoundInjection;
    }

    private boolean checkParamWithStar() throws JSqlException {
        
        LOGGER.log(LogLevel.CONSOLE_INFORM, "Checking single {} parameter with injection point at [*]", this::name);
        
        // Will keep param value as is,
        // Does not test for insertion character (param is null)
        return this.injectionModel.getMediatorStrategy().testStrategies(null);
    }

    /**
     *  Default injection: last param tested only
     */
    private boolean checkLastParam() throws JSqlException {
        
        // Will check param value by user.
        // Notice options 'Inject each URL params' and 'inject JSON' must be checked both
        // for JSON injection of last param
        SimpleEntry<String, String> parameterToInject = this.getParams().stream().reduce((a, b) -> b).orElseThrow(NullPointerException::new);

        return this.injectionModel.getMediatorStrategy().testStrategies(parameterToInject);
    }

    /**
     * Injection of every params: isCheckingAllParam() == true.
     * Params are tested one by one in two loops:
     * - inner loop erases * from previous param
     * - outer loop adds * to current param
     * @throws StoppedByUserSlidingException
     */
    private boolean checkAllParams() throws StoppedByUserSlidingException {
        
        var hasFoundInjection = false;
        
        // This param will be marked by * if injection is found,
        // inner loop will erase mark * otherwise
        for (SimpleEntry<String, String> paramBase: this.getParams()) {

            // This param is the current tested one.
            // For JSON value attributes are traversed one by one to test every values.
            // For standard value mark * is simply added to the end of its value.
            for (SimpleEntry<String, String> paramStar: this.getParams()) {

                if (paramStar == paramBase) {
                    
                    try {
                        hasFoundInjection = this.testSingleFromAllParams(paramStar);
                        
                        if (hasFoundInjection) {
                            
                            return hasFoundInjection;
                        }
                        
                    } catch (JSONException e) {
                        
                        LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                    }
                }
            }
        }
    
        return hasFoundInjection;
    }

    private boolean testSingleFromAllParams(SimpleEntry<String, String> paramStar) throws StoppedByUserSlidingException {
        
        boolean hasFoundInjection;
        
        // Will test if current value is a JSON entity
        Object jsonEntity = JsonUtil.getJson(paramStar.getValue());
        
        // Define a tree of JSON attributes with path as the key: root.a => value of a
        List<SimpleEntry<String, String>> attributesJson = JsonUtil.createEntries(jsonEntity, "root", null);
        
//        String paramBase64 = paramStar.getValue().replace("*", "");
//        if (Base64.isBase64(paramBase64) && StringUtil.isUtf8(StringUtil.base64Decode(paramBase64))) {
//
//            LOGGER.info(
//                String.format(
//                    "Param %s=%s appears to be Base64",
//                    paramStar.getKey(),
//                    paramStar.getValue()
//                )
//            );
//        }
        
        // When option 'Inject JSON' is selected and there's a JSON entity to inject
        // then loop through each paths to add * at the end of value and test each strategies.
        // Marks * are erased between each tests.
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam() && !attributesJson.isEmpty()) {
            
            hasFoundInjection = this.injectionModel.getMediatorUtils().getJsonUtil().testJsonParam(this, paramStar);
            
        } else {
            
            // Standard non JSON injection
            hasFoundInjection = this.testJsonlessParam(paramStar);
        }
        
        return hasFoundInjection;
    }
    
    public boolean testJsonlessParam(SimpleEntry<String, String> paramStar) throws StoppedByUserSlidingException {

        var hasFoundInjection = false;
        
        // Add * to end of value
        paramStar.setValue(paramStar.getValue() + InjectionModel.STAR);
        
        try {
            LOGGER.log(
                LogLevel.CONSOLE_INFORM,
                "Checking {} parameter {}={}",
                this::name,
                paramStar::getKey,
                () -> paramStar.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY)
            );
            
            // Test current standard value marked with * for injection
            // Keep original param
            hasFoundInjection = this.injectionModel.getMediatorStrategy().testStrategies(paramStar);
            
        } catch (StoppedByUserSlidingException e) {
            
            // Break all params processing in upper methods
            throw e;
            
        } catch (JSqlException e) {
            
            // Injection failure
            LOGGER.log(
                LogLevel.CONSOLE_ERROR,
                "No {} injection found for parameter {}={} ({})",
                this.name(),
                paramStar.getKey(),
                paramStar.getValue().replaceAll("\\+.?$|\\" + InjectionModel.STAR, StringUtils.EMPTY),
                e.getMessage()
            );
            
        } finally {
            
            // Erase * from JSON if failure
            if (!hasFoundInjection) {
                
                // Erase * at the end of each params
                this.getParams().stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", StringUtils.EMPTY)));
                
                // TODO It erases STAR from value => * can't be used in parameter
                paramStar.setValue(paramStar.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY));
            }
        }
        
        return hasFoundInjection;
    }
}