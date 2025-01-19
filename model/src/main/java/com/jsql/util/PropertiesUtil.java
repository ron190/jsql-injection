package com.jsql.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void displayStatus(Locale newLocale) {
        AtomicInteger countJvm = new AtomicInteger();
        var statusJvm = StringUtils.EMPTY;
        AtomicInteger countGui = new AtomicInteger();
        var statusGui = StringUtils.EMPTY;
        var propertiesRoot = PropertiesUtil.getProperties("i18n/jsql.properties");

        if (!Arrays.asList(StringUtils.EMPTY, "en").contains(newLocale.getLanguage())) {
            var bundleUser = PropertiesUtil.getProperties("i18n/jsql_" + newLocale.getLanguage() + ".properties");
            propertiesRoot.entrySet().stream()
            .filter(key -> bundleUser.isEmpty() || !bundleUser.containsKey(key.getKey()))
            .forEach(key -> countGui.getAndIncrement());
            statusGui = String.format("gui %s %s%% %s",
                newLocale.getDisplayLanguage(newLocale),
                Float.valueOf((float) (countGui.get() * 100) / propertiesRoot.entrySet().size()).intValue(),
                countGui.get() <= 0 ? StringUtils.EMPTY : "("+ countGui.get() +" items)"
            );
        }

        if (!Locale.getDefault().getLanguage().equals("en")) {
            var propertiesJvm = PropertiesUtil.getProperties("i18n/jsql_"+ Locale.getDefault().getLanguage() +".properties");
            propertiesRoot.entrySet().stream()
            .filter(key -> propertiesJvm.isEmpty() || !propertiesJvm.containsKey(key.getKey()))
            .forEach(key -> countJvm.getAndIncrement());
            statusJvm = String.format("jvm %s %s%% %s",
                Locale.getDefault().getDisplayLanguage(newLocale),
                Float.valueOf((float) (countJvm.get() * 100) / propertiesRoot.entrySet().size()).intValue(),
                countJvm.get() <= 0 ? StringUtils.EMPTY : " ("+ countJvm.get() +" items)"
            );
        }

        if (countJvm.get() > 0 || countGui.get() > 0) {
            LOGGER.info(
                "i18n status: {}{}",
                countJvm.get() > 0 ? statusJvm : StringUtils.EMPTY,
                countGui.get() > 0 ? statusGui : StringUtils.EMPTY
            );
        }
    }

    private static Properties getProperties(String name) {
        var properties = new Properties();
        try {
            var uri = ClassLoader.getSystemResource(name).toURI();
            var path = Paths.get(uri);
            byte[] root = Files.readAllBytes(path);
            var rootI18n = new String(root, StandardCharsets.UTF_8);
            String rootI18nFixed = Pattern.compile("\\\\[\n\r]+")
                .matcher(Matcher.quoteReplacement(rootI18n))
                .replaceAll("a");
            properties.load(new StringReader(rootI18nFixed));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    public String getVersionJsql() {
        return this.properties.getProperty("jsql.version");
    }

    public Properties getProperties() {
        return this.properties;
    }
}
