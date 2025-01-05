package com.jsql.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final Properties properties = new Properties();
    
    public PropertiesUtil() {
        var filename = "config.properties";
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Properties file {} not found", filename);
                return;
            }
            this.getProperties().load(input);  // load a properties file from class path, inside static method
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    public String getVersionJsql() {
        return this.properties.getProperty("jsql.version");
    }

    public Properties getProperties() {
        return this.properties;
    }
}
