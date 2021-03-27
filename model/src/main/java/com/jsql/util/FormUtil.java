package com.jsql.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jsql.model.InjectionModel;

public class FormUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final String INPUT_ATTR_VALUE = "value";
    private static final String FORM_ATTR_VALUE = "method";
    
    private InjectionModel injectionModel;
    
    public FormUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public void parseForms(HttpURLConnection connection, StringBuilder pageSource) throws IOException {
        
        Elements elementsForm = Jsoup.parse(pageSource.toString()).select("form");
        
        if (elementsForm.isEmpty()) {
            return;
        }
        
        StringBuilder result = new StringBuilder();
        
        Map<Element, List<Element>> mapForms = new HashMap<>();
        
        for (Element form: elementsForm) {
            
            mapForms.put(form, new ArrayList<>());
            
            result.append(
                String.format(
                    "%n<form action=\"%s\" method=\"%s\" />",
                    form.attr("action"),
                    form.attr(FORM_ATTR_VALUE)
                )
            );
            
            for (Element input: form.select("input")) {
                
                result.append(
                    String.format(
                        "%n    <input name=\"%s\" value=\"%s\" />",
                        input.attr("name"),
                        input.attr(INPUT_ATTR_VALUE)
                    )
                );
                
                mapForms.get(form).add(input);
            }
            
            Collections.reverse(mapForms.get(form));
        }
            
        if (!this.injectionModel.getMediatorUtils().getPreferencesUtil().isParsingForm()) {
            
            this.logForms(connection, elementsForm, result);
            
        } else {
            
            this.addForms(elementsForm, result, mapForms);
        }
    }

    private void addForms(Elements elementsForm, StringBuilder result, Map<Element, List<Element>> mapForms) {
        
        LOGGER.debug(
            "Found {} <form> in HTML body, adding input(s) to requests: {}",
            () -> elementsForm.size(),
            () -> result
        );
        
        for(Entry<Element, List<Element>> form: mapForms.entrySet()) {
            
            for (Element input: form.getValue()) {
                
                if ("get".equalsIgnoreCase(form.getKey().attr(FORM_ATTR_VALUE))) {
                    
                    this.injectionModel.getMediatorUtils().getParameterUtil().getListQueryString().add(
                        0,
                        new SimpleEntry<>(
                            input.attr("name"),
                            input.attr(INPUT_ATTR_VALUE)
                        )
                    );
                    
                } else if ("post".equalsIgnoreCase(form.getKey().attr(FORM_ATTR_VALUE))) {
                    
                    this.injectionModel.getMediatorUtils().getParameterUtil().getListRequest().add(
                        0,
                        new SimpleEntry<>(
                            input.attr("name"),
                            input.attr(INPUT_ATTR_VALUE)
                        )
                    );
                }
            }
        }
    }

    private void logForms(HttpURLConnection connection, Elements elementsForm, StringBuilder result) throws IOException {
        
        if (connection.getResponseCode() != 200) {
            
            LOGGER.trace(
                "Found {} ignored <form> in HTML body: {}",
                () -> elementsForm.size(),
                () -> result
            );
            LOGGER.info("WAF can detect missing form parameters, you may enable 'Add <input> parameters' in Preferences and retry");
            
        } else {
            
            LOGGER.trace(
                "Found {} <form> in HTML body while status 200 Success:{}",
                () -> elementsForm.size(),
                () -> result
            );
        }
    }

}
