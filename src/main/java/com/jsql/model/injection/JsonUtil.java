package com.jsql.model.injection;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jsql.model.InjectionModel;

public class JsonUtil {

    public JsonUtil() {
        // TODO Auto-generated constructor stub
    }

    public static List<SimpleEntry<String, String>> loopThroughJson(Object jsonEntity, String parentName, SimpleEntry<String, String> parentXPath) {
        List<SimpleEntry<String, String>> attributesXPath = new ArrayList<>();
        
        if (jsonEntity instanceof JSONObject) {
            
            JSONObject jsonObjectEntity = (JSONObject) jsonEntity;
            Iterator<?> keys = jsonObjectEntity.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object value = jsonObjectEntity.get(key);
                String xpath = parentName +"."+ key;
                
                if (value instanceof JSONArray) {
                    attributesXPath.addAll(JsonUtil.loopThroughJson(value, xpath, parentXPath));
                } else if (value instanceof String) {
                    SimpleEntry<String, String> c = new SimpleEntry<>(xpath, (String) value);
                    attributesXPath.add(c);
                    
                    if (parentXPath == null) {
                        jsonObjectEntity.put(key, value.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", ""));
                    } else if (c.equals(parentXPath)) {
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
                
                JSONObject jsonObjectEntity = jsonArrayEntity.getJSONObject(i);
                
                Iterator<?> keys = jsonObjectEntity.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object value = jsonObjectEntity.opt(key);
                    
                    if (value instanceof JSONArray) {
                        attributesXPath.addAll(JsonUtil.loopThroughJson(value, parentName +"."+ key, parentXPath));
                    } else if (value instanceof String) {
                        SimpleEntry<String, String> s = new SimpleEntry<>(parentName +"."+ key, (String) value);
                        attributesXPath.add(s);
                        
                        if (parentXPath == null) {
                            jsonObjectEntity.put(key, value.toString().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", ""));
                        } else if (s.equals(parentXPath)) {
                            jsonObjectEntity.put(key, value + InjectionModel.STAR);
                        }
                    }
                }
            }
            
        }
        
        return attributesXPath;
    }
    
}
