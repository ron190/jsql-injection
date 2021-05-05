package com.jsql.view.swing.sql.text;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

public class AttributeSetterForVendor {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private Object attributeVendor;
    private Method method;

    public AttributeSetterForVendor(Object attributeVendor, String nameSetter) {
        
        this.attributeVendor = attributeVendor;
        
        try {
            this.method = attributeVendor.getClass().getMethod(nameSetter, String.class);
            
        } catch (NullPointerException | NoSuchMethodException | SecurityException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
    
    public Object getAttribute() {
        return this.attributeVendor;
    }

    public Method getMethod() {
        return this.method;
    }
}