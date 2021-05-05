package com.jsql.view.swing.dialog.translate;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.ConnectionUtil;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.util.PropertiesUtil;
import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.util.MediatorHelper;

public class SwingWorkerGithubLocale extends SwingWorker<Object, Object> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private Properties propertiesLanguageToTranslate = new Properties();
    private OrderedProperties propertiesRoot = new OrderedProperties();
    private StringBuilder propertiesToTranslate = new StringBuilder();
    private DialogTranslate dialogTranslate;
    
    private ConnectionUtil connectionUtil = MediatorHelper.model().getMediatorUtils().getConnectionUtil();
    private PropertiesUtil propertiesUtil = MediatorHelper.model().getMediatorUtils().getPropertiesUtil();
    
    private static final String LINE_FEED_ESCAPE = "{@|@}";
    private static final String LINE_FEED = "\\\\[\n\r]+";
    
    public SwingWorkerGithubLocale(DialogTranslate dialogTranslate) {
        
        this.dialogTranslate = dialogTranslate;
    }

    @Override
    protected Object doInBackground() throws Exception {
        
        Thread.currentThread().setName("SwingWorkerDialogTranslate");
        
        this.dialogTranslate.getProgressBarTranslation().setVisible(this.dialogTranslate.getLanguage() != Language.OT);
        
        try {
            this.loadFromGithub();
            
        } catch (IOException eGithub) {
            
            this.logFileNotFound(eGithub);
            
        } finally {
            
            this.displayDiff();
        }
        
        return null;
    }

    private void displayDiff() {
        
        this.propertiesRoot
        .entrySet()
        .stream()
        .filter(key ->
        
            this.dialogTranslate.getLanguage() == Language.OT
            || this.propertiesLanguageToTranslate.size() == 0
            || !this.propertiesLanguageToTranslate.containsKey(key.getKey())
        )
        .forEach(key ->
        
            this.propertiesToTranslate.append(
                String.format(
                    "%n%n%s=%s",
                    key.getKey(),
                    key.getValue().replace(LINE_FEED_ESCAPE,"\\\n")
                )
            )
        );
        
        this.dialogTranslate.setTextBeforeChange(this.propertiesToTranslate.toString().trim());
        
        this.dialogTranslate.getButtonSend().setEnabled(true);
        this.dialogTranslate.getTextToTranslate().setText(this.dialogTranslate.getTextBeforeChange());
        this.dialogTranslate.getTextToTranslate().setCaretPosition(0);
        this.dialogTranslate.getTextToTranslate().setEditable(true);
        
        if (this.dialogTranslate.getLanguage() != Language.OT) {
            
            int percentTranslated = 100 * this.propertiesLanguageToTranslate.size() / this.propertiesRoot.size();
            this.dialogTranslate.getProgressBarTranslation().setValue(percentTranslated);
            this.dialogTranslate.getProgressBarTranslation().setString(
                String.format(
                     "%s%% translated into %s",
                     percentTranslated,
                     this.dialogTranslate.getLanguage()
                )
            );
        }
    }

    private void loadFromGithub() throws IOException, URISyntaxException {
        
        this.loadRootFromGithub();
        
        if (this.dialogTranslate.getLanguage() != Language.OT) {
            
            this.loadLanguageFromGithub();
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, () -> I18nUtil.valueByKey("LOG_I18N_DEFAULT_LOADED"));
        }
    }

    private void logFileNotFound(IOException eGithub) throws IOException {
        
        if (this.propertiesLanguageToTranslate.size() == 0) {
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, "Language file not found, text to translate loaded from local", eGithub);
            
        } else if (this.propertiesRoot.size() == 0) {
            
            throw new IOException("Reference language not found");
        }
    }
    
    private void loadRootFromGithub() throws IOException, URISyntaxException {
        
        try {
            String pageSourceRoot = this.connectionUtil.getSourceLineFeed(
                this.propertiesUtil.getProperties().getProperty("github.webservice.i18n.root")
            );
            
            String pageSourceRootFixed = Pattern.compile(LINE_FEED).matcher(Matcher.quoteReplacement(pageSourceRoot)).replaceAll(LINE_FEED_ESCAPE);
            
            this.propertiesRoot.load(new StringReader(pageSourceRootFixed));
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, () -> I18nUtil.valueByKey("LOG_I18N_ROOT_LOADED"));
            
        } catch (IOException e) {
            
            var uri = ClassLoader.getSystemResource("i18n/jsql.properties").toURI();
            var path = Paths.get(uri);
            byte[] root = Files.readAllBytes(path);
            var rootI18n = new String(root);
            String rootI18nFixed = Pattern.compile(LINE_FEED).matcher(Matcher.quoteReplacement(rootI18n)).replaceAll(LINE_FEED_ESCAPE);
            
            this.propertiesRoot.load(new StringReader(rootI18nFixed));
            LOGGER.log(LogLevel.CONSOLE_INFORM, "Reference language loaded from local");
            
            LOGGER.log(LogLevel.IGNORE, e);
        }
    }
    
    private void loadLanguageFromGithub() throws IOException, URISyntaxException {
        
        try {
            String pageSourceLanguage = this.connectionUtil.getSourceLineFeed(
                String.format(
                    "%sjsql_%s.properties",
                    this.propertiesUtil.getProperties().getProperty("github.webservice.i18n.locale"),
                    this.dialogTranslate.getLanguage().getLabelLocale()
                )
            );
            
            this.propertiesLanguageToTranslate.load(new StringReader(pageSourceLanguage));
            
            LOGGER.log(
                LogLevel.CONSOLE_INFORM,
                "{} {}",
                () -> I18nUtil.valueByKey("LOG_I18N_TEXT_LOADED"),
                () -> this.dialogTranslate.getLanguage()
            );
            
        } catch (IOException e) {
            
            var uri = ClassLoader
                .getSystemResource(
                    String
                    .format(
                        "i18n/jsql_%s.properties",
                        this.dialogTranslate.getLanguage().getLabelLocale()
                    )
                )
                .toURI();
            
            var path = Paths.get(uri);
            byte[] root = Files.readAllBytes(path);
            var localeI18n = new String(root);
            String localeI18nFixed = Pattern.compile(LINE_FEED).matcher(localeI18n).replaceAll(LINE_FEED_ESCAPE);
            
            this.propertiesLanguageToTranslate.load(new StringReader(localeI18nFixed));
            LOGGER.log(
                LogLevel.CONSOLE_INFORM,
                String.format(
                    "%s translation loaded from local",
                    this.dialogTranslate.getLanguage()
                )
            );
            
            LOGGER.log(LogLevel.IGNORE, e);
        }
    }
}