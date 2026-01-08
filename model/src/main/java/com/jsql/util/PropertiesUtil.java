package com.jsql.util;

import com.jsql.model.exception.JSqlRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final Properties properties = new Properties();

    public PropertiesUtil() {
        var filename = "config.properties";
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Properties file {} not found", filename);
                return;
            }
            this.properties.load(input);  // load a properties file from class path, inside static method
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    public void displayI18nStatus(Locale newLocale) {
        if (!Arrays.asList(StringUtils.EMPTY, "en").contains(newLocale.getLanguage())) {
            AtomicInteger countGui = new AtomicInteger();
            var bundleRoot = PropertiesUtil.getProperties("i18n/jsql.properties");
            var bundleUser = PropertiesUtil.getProperties("i18n/jsql_" + newLocale.getLanguage() + ".properties");
            bundleRoot.entrySet().stream().filter(
                key -> bundleUser.isEmpty() || !bundleUser.containsKey(key.getKey())
            ).forEach(
                key -> countGui.getAndIncrement()
            );
            if (countGui.get() > 0) {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_SUCCESS,
                    "Switched to {} with {}% translated, contribute and translate any of {} items in menu Community",
                    () -> newLocale.getDisplayLanguage(newLocale),
                    () -> BigDecimal.valueOf(
                        100.0 - countGui.get() * 100.0 / bundleRoot.size()
                    ).setScale(1, RoundingMode.HALF_UP).doubleValue(),
                    countGui::get
                );
            }
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
            throw new JSqlRuntimeException(e);
        }
        return properties;
    }

    public String getVersionJsql() {
        return this.properties.getProperty("jsql.version");
    }

    public String getProperty(String property) {
        return this.properties.getProperty(property);
    }
}
