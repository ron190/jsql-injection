package com.jsql.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.MethodInjection;

public class JsonUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private InjectionModel injectionModel;
    
    public JsonUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }

    public static Object getJson(String param) {
        // Will test if current value is a JSON entity
        Object jsonEntity = null;
        
        try {
            // Test for JSON Object
            jsonEntity = new JSONObject(param);
        } catch (JSONException exceptionJSONObject) {
            try {
                // Test for JSON Array
                jsonEntity = new JSONArray(param);
            } catch (JSONException exceptionJSONArray) {
                // Not a JSON entity
                jsonEntity = new Object();
            }
        }
        
        return jsonEntity;
    }

    public static List<SimpleEntry<String, String>> createEntries(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath) {
        List<SimpleEntry<String, String>> attributesXPath = new ArrayList<>();
        
        if (jsonEntity instanceof JSONObject) {
            
            JSONObject jsonObjectEntity = (JSONObject) jsonEntity;
            
            Iterator<?> keys = jsonObjectEntity.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object value = jsonObjectEntity.get(key);
                String xpath = parentName +"."+ key;
                
                if (value instanceof JSONArray || value instanceof JSONObject) {
                    attributesXPath.addAll(JsonUtil.createEntries(value, xpath, parentXPath));
                } else if (value instanceof String) {
                    SimpleEntry<String, String> stringValue = new SimpleEntry<>(xpath, (String) value);
                    attributesXPath.add(stringValue);
                    
                    if (parentXPath == null) {
                        jsonObjectEntity.put(key, value.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", ""));
                    } else if (stringValue.equals(parentXPath)) {
                        jsonObjectEntity.put(key, value + InjectionModel.STAR);
                    }
                }
            }
            
        } else if (jsonEntity instanceof JSONArray) {
            
            JSONArray jsonArrayEntity = (JSONArray) jsonEntity;
            for (int i = 0; i < jsonArrayEntity.length(); i++) {
                Object jsonEntityInArray = jsonArrayEntity.get(i);
                if(!(jsonEntityInArray instanceof JSONObject) && !(jsonEntityInArray instanceof JSONArray)){
                    continue;
                }

                String xpath = parentName +"["+ i +"]";
                attributesXPath.addAll(JsonUtil.createEntries(jsonEntityInArray, xpath, parentXPath));
            }
            
        }
        
        return attributesXPath;
    }
    
    public boolean testStandardParameter(MethodInjection methodInjection, SimpleEntry<String, String> paramStar) {
        boolean hasFoundInjection = false;
        
        // Add * to end of value
        paramStar.setValue(paramStar.getValue() + InjectionModel.STAR);
        
        try {
            LOGGER.info("Checking "+ methodInjection.name() +" parameter "+ paramStar.getKey() +"="+ paramStar.getValue().replace(InjectionModel.STAR, ""));
            
            // Test current standard value marked with * for injection
            // Keep original param
            hasFoundInjection = this.injectionModel.testStrategies(InjectionModel.IS_PARAM_BY_USER, !InjectionModel.IS_JSON, paramStar);
            
        } catch (JSqlException e) {
            // Injection failure
            LOGGER.warn(
                "No "+ methodInjection.name() +" injection found for parameter "
                + paramStar.getKey() +"="+ paramStar.getValue().replace(InjectionModel.STAR, "")
                + " (" + e.getMessage() +")", e
            );
            
        } finally {
            // Erase * from JSON if failure
            if (!hasFoundInjection) {
                // Erase * at the end of each params
                methodInjection.getParams().stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", "")));
                
                // TODO It erases STAR from value => * can't be used in parameter
                paramStar.setValue(paramStar.getValue().replace("*", ""));
            }
        }
        
        return hasFoundInjection;
    
    }
    
    public boolean testJsonParameter(MethodInjection methodInjection, SimpleEntry<String, String> paramStar) {
        boolean hasFoundInjection = false;
        
        // Will test if current value is a JSON entity
        Object jsonEntity = JsonUtil.getJson(paramStar.getValue());
        
        // Define a tree of JSON attributes with path as the key: root.a => value of a
        List<SimpleEntry<String, String>> attributesJson = JsonUtil.createEntries(jsonEntity, "root", null);
        
        // Loop through each JSON values
        for (SimpleEntry<String, String> parentXPath: attributesJson) {
            
            // Erase previously defined *
            JsonUtil.createEntries(jsonEntity, "root", null);
            
            // Add * to current parameter's value
            JsonUtil.createEntries(jsonEntity, "root", parentXPath);
            
            // Replace param value by marked one.
            // paramStar and paramBase are the same object
            paramStar.setValue(jsonEntity.toString());
            
            try {
                LOGGER.info("Checking JSON "+ methodInjection.name() +" parameter "+ parentXPath.getKey() +"="+ parentXPath.getValue().replace(InjectionModel.STAR, ""));
                
                // Test current JSON value marked with * for injection
                // Keep original param
                hasFoundInjection = this.injectionModel.testStrategies(InjectionModel.IS_PARAM_BY_USER, InjectionModel.IS_JSON, paramStar);
                
                // Injection successful
                break;
                
            } catch (JSqlException e) {
                // Injection failure
                LOGGER.warn("No "+ methodInjection.name() +" injection found for JSON "+ methodInjection.name() +" parameter "+ parentXPath.getKey() +"="+ parentXPath.getValue().replace(InjectionModel.STAR, ""), e);
                
            } finally {
                // Erase * at the end of each params
                // TODO useless
                methodInjection.getParams().stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", "")));
                
                // Erase * from JSON if failure
                if (!hasFoundInjection) {
                    paramStar.setValue(paramStar.getValue().replace("*", ""));
                }
            }
        }
        
        return hasFoundInjection;
    }
    
}
