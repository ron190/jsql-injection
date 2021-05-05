package com.jsql.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.AbstractMethodInjection;

public class JsonUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private InjectionModel injectionModel;
    
    public JsonUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public static boolean isJson(String param) {
        
        var isJson = false;
        
        try {
            // Test for JSON Object
            new JSONObject(param);
            isJson = true;
            
        } catch (JSONException exceptionJSONObject) {
            
            try {
                // Test for JSON Array
                new JSONArray(param);
                isJson = true;
                
            } catch (JSONException exceptionJSONArray) {
                // Not a JSON entity
            }
        }
        
        return isJson;
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
            
            JsonUtil.scanJsonObject(jsonEntity, parentName, parentXPath, attributesXPath);
            
        } else if (jsonEntity instanceof JSONArray) {
            
            JsonUtil.scanJsonArray(jsonEntity, parentName, parentXPath, attributesXPath);
        }
        
        return attributesXPath;
    }

    private static void scanJsonArray(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath, List<SimpleEntry<String, String>> attributesXPath) {
        
        var jsonArrayEntity = (JSONArray) jsonEntity;
        
        for (var i = 0; i < jsonArrayEntity.length(); i++) {
            
            Object jsonEntityInArray = jsonArrayEntity.get(i);
            String xpath = parentName +"["+ i +"]";
            
            // Not possible to make generic with scanJsonObject() because of JSONArray.put(int) != JSONObject.put(String)
            if (jsonEntityInArray instanceof JSONArray || jsonEntityInArray instanceof JSONObject) {
                
                attributesXPath.addAll(JsonUtil.createEntries(jsonEntityInArray, xpath, parentXPath));
                
            } else if (jsonEntityInArray instanceof String) {
                
                SimpleEntry<String, String> stringValue = new SimpleEntry<>(xpath, (String) jsonEntityInArray);
                attributesXPath.add(stringValue);
                
                if (parentXPath == null) {
                    
                    jsonArrayEntity.put(i, jsonEntityInArray.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", StringUtils.EMPTY));
                    
                } else if (stringValue.equals(parentXPath)) {
                    
                    jsonArrayEntity.put(i, jsonEntityInArray + InjectionModel.STAR);
                }
            }
        }
    }

    private static void scanJsonObject(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath, List<SimpleEntry<String, String>> attributesXPath) {
        
        var jsonObjectEntity = (JSONObject) jsonEntity;
        
        Iterator<?> keys = jsonObjectEntity.keys();
        
        while (keys.hasNext()) {
            
            String key = (String) keys.next();
            var value = jsonObjectEntity.get(key);
            String xpath = parentName +"."+ key;
            
            // Not possible to make generic with scanJsonObject() because of JSONArray.put(int) != JSONObject.put(String)
            if (value instanceof JSONArray || value instanceof JSONObject) {
                
                attributesXPath.addAll(JsonUtil.createEntries(value, xpath, parentXPath));
                
            } else if (value instanceof String) {
                
                SimpleEntry<String, String> stringValue = new SimpleEntry<>(xpath, (String) value);
                attributesXPath.add(stringValue);
                
                if (parentXPath == null) {
                    
                    jsonObjectEntity.put(key, value.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", StringUtils.EMPTY));
                    
                } else if (stringValue.equals(parentXPath)) {
                    
                    jsonObjectEntity.put(key, value + InjectionModel.STAR);
                }
            }
        }
    }
    
    public boolean testJsonParam(AbstractMethodInjection methodInjection, SimpleEntry<String, String> paramStar) {
        
        var hasFoundInjection = false;
        
        // Remove STAR at the end of parameter, STAR will be added inside json data instead
        paramStar.setValue(paramStar.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY));
        
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
            paramStar.setValue(jsonEntity.toString());
            
            try {
                LOGGER.log(
                    LogLevel.CONSOLE_INFORM,
                    "Checking JSON {} parameter {}={}",
                    methodInjection::name,
                    parentXPath::getKey,
                    () -> parentXPath.getValue().replace(InjectionModel.STAR, StringUtils.EMPTY)
                );
                
//                String paramBase64 = paramStar.getValue().replace("*", "");
//                if (
//                    Base64.isBase64(paramBase64)
//                    && StringUtil.isUtf8(StringUtil.base64Decode(paramBase64))
//                ) {
//
//                    LOGGER.info(
//                        String.format(
//                            "Param %s=%s appears to be Base64",
//                            paramStar.getKey(),
//                            paramStar.getValue()
//                        )
//                    );
//                }
                
                // Test current JSON value marked with * for injection
                // Keep original param
                hasFoundInjection = this.injectionModel.getMediatorStrategy().testStrategies(paramStar);
                
                // Injection successful
                break;
                
            } catch (JSqlException e) {
                
                // Injection failure
                LOGGER.log(
                    LogLevel.CONSOLE_ERROR,
                    String.format(
                        "No injection found for JSON %s parameter %s=%s",
                        methodInjection.name(),
                        parentXPath.getKey(),
                        parentXPath.getValue().replace(
                            InjectionModel.STAR,
                            StringUtils.EMPTY
                        )
                    )
                );
                
            } finally {
                
                // Erase * at the end of each params
                // TODO useless
                methodInjection
                .getParams()
                .stream()
                .forEach(e ->
                    e.setValue(
                        e.getValue().replaceAll(
                            Pattern.quote(InjectionModel.STAR) +"$",
                            StringUtils.EMPTY
                        )
                    )
                );
                
                // Erase * from JSON if failure
                if (!hasFoundInjection) {
                    
                    paramStar.setValue(
                        paramStar.getValue().replace(
                            InjectionModel.STAR,
                            StringUtils.EMPTY
                        )
                    );
                }
            }
        }
        
        return hasFoundInjection;
    }
}
