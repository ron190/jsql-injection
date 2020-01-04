package com.jsql.view.swing.sql.text;

import org.apache.log4j.Logger;

public class AttributeSetterForVendor {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    Object attributeVendor;
    java.lang.reflect.Method method;

    public AttributeSetterForVendor(Object attributeVendor, String nameSetter) {
        this.attributeVendor = attributeVendor;
        try {
            this.method = attributeVendor.getClass().getMethod(nameSetter, String.class);
        } catch (NullPointerException | NoSuchMethodException | SecurityException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }
    
    public Object getAttribute() {
        return this.attributeVendor;
    }

    public java.lang.reflect.Method getSetter() {
        return this.method;
    }
    
}