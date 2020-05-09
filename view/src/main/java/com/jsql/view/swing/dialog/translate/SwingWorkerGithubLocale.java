package com.jsql.view.swing.dialog.translate;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.I18nUtil;
import com.jsql.util.PropertiesUtil;
import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.util.MediatorHelper;

public class SwingWorkerGithubLocale extends SwingWorker<Object, Object> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private Properties propertiesLanguageToTranslate = new Properties();
    private OrderedProperties propertiesRoot = new OrderedProperties();
    private StringBuilder propertiesToTranslate = new StringBuilder();
    private DialogTranslate dialogTranslate;
    
    private ConnectionUtil connectionUtil = MediatorHelper.model().getMediatorUtils().getConnectionUtil();
    private PropertiesUtil propertiesUtil = MediatorHelper.model().getMediatorUtils().getPropertiesUtil();
    
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
        
            this.propertiesToTranslate.append("\n\n"+ key.getKey() +"="+ key.getValue().replace("{@|@}","\\\n"))
        );
        
        this.dialogTranslate.setTextBeforeChange(this.propertiesToTranslate.toString().trim());
        
        this.dialogTranslate.getButtonSend().setEnabled(true);
        this.dialogTranslate.getTextToTranslate().setText(this.dialogTranslate.getTextBeforeChange());
        this.dialogTranslate.getTextToTranslate().setCaretPosition(0);
        this.dialogTranslate.getTextToTranslate().setEditable(true);
        
        if (this.dialogTranslate.getLanguage() != Language.OT) {
            
            int percentTranslated = 100 * this.propertiesLanguageToTranslate.size() / this.propertiesRoot.size();
            this.dialogTranslate.getProgressBarTranslation().setValue(percentTranslated);
            this.dialogTranslate.getProgressBarTranslation().setString(percentTranslated +"% translated into "+ this.dialogTranslate.getLanguage());
        }
    }

    private void loadFromGithub() throws IOException, URISyntaxException {
        
        this.loadRootFromGithub();
        
        if (this.dialogTranslate.getLanguage() != Language.OT) {
            
            this.loadLanguageFromGithub();
            
        } else {
            
            LOGGER.info(I18nUtil.valueByKey("LOG_I18N_DEFAULT_LOADED"));
        }
    }

    private void logFileNotFound(IOException eGithub) throws IOException {
        
        if (this.propertiesLanguageToTranslate.size() == 0) {
            
            LOGGER.info("Language file not found, text to translate loaded from local", eGithub);
            
        } else if (this.propertiesRoot.size() == 0) {
            
            throw new IOException("Reference language not found");
        }
    }
    
    private void loadRootFromGithub() throws IOException, URISyntaxException {
        
        try {
            String pageSourceRoot = connectionUtil.getSourceLineFeed(
                propertiesUtil.getProperties().getProperty("github.webservice.i18n.root")
            );
            
            String pageSourceRootFixed = Pattern.compile("\\\\[\n\r]+").matcher(Matcher.quoteReplacement(pageSourceRoot)).replaceAll("{@|@}");
            
            this.propertiesRoot.load(new StringReader(pageSourceRootFixed));
            
            LOGGER.info(I18nUtil.valueByKey("LOG_I18N_ROOT_LOADED"));
            
        } catch (IOException e) {
            
            URI uri = ClassLoader.getSystemResource("i18n/jsql.properties").toURI();
            Path path = Paths.get(uri);
            byte[] root = Files.readAllBytes(path);
            String rootI18n = new String(root);
            String rootI18nFixed = Pattern.compile("\\\\[\n\r]+").matcher(Matcher.quoteReplacement(rootI18n)).replaceAll("{@|@}");
            
            this.propertiesRoot.load(new StringReader(rootI18nFixed));
            LOGGER.info("Reference language loaded from local");
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        }
    }
    
    private void loadLanguageFromGithub() throws IOException, URISyntaxException {
        
        try {
            String pageSourceLanguage = connectionUtil.getSourceLineFeed(
                propertiesUtil.getProperties().getProperty("github.webservice.i18n.locale")
                + "jsql_"+ this.dialogTranslate.getLanguage().getLabelLocale() +".properties"
            );
            
            this.propertiesLanguageToTranslate.load(new StringReader(pageSourceLanguage));
            
            LOGGER.info(I18nUtil.valueByKey("LOG_I18N_TEXT_LOADED") +" "+ this.dialogTranslate.getLanguage());
            
        } catch (IOException e) {
            
            URI uri = ClassLoader.getSystemResource("i18n/jsql_"+ this.dialogTranslate.getLanguage().getLabelLocale() +".properties").toURI();
            Path path = Paths.get(uri);
            byte[] root = Files.readAllBytes(path);
            String localeI18n = new String(root);
            String localeI18nFixed = Pattern.compile("\\\\[\n\r]+").matcher(localeI18n).replaceAll("{@|@}");
            
            this.propertiesLanguageToTranslate.load(new StringReader(localeI18nFixed));
            LOGGER.info(this.dialogTranslate.getLanguage() +" translation loaded from local");
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        }
    }
}