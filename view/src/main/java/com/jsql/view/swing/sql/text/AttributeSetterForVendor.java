package com.jsql.view.swing.sql.text;

import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class AttributeSetterForVendor {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final Object attributeVendor;
    private Method method;

    public AttributeSetterForVendor(Object attributeVendor, String nameSetter) {
        
        this.attributeVendor = attributeVendor;
        
        try {
            this.method = attributeVendor.getClass().getMethod(nameSetter, String.class);
            
        } catch (NullPointerException | NoSuchMethodException | SecurityException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
    
    public Object getAttribute() {
        return this.attributeVendor;
    }

    public Method getMethod() {
        return this.method;
    }
}