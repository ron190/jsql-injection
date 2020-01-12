package com.jsql.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private final Properties properties = new Properties();
    
    public PropertiesUtil() {
        
        InputStream input = null;

        try {
            String filename = "config.properties";
            input = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                LOGGER.warn("Properties file "+ filename +" not found");
                return;
            }

            // load a properties file from class path, inside static method
            this.getProperties().load(input);
        } catch (IOException e) {
            
            LOGGER.error(e, e);
        } finally {
            
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        }
        
    }

    public Properties getProperties() {
        return this.properties;
    }

}
