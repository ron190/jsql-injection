package com.jsql.view.swing.dialog.translate;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.view.swing.dialog.DialogTranslate;

public class SwingWorkerGithubLocale extends SwingWorker<Object, Object> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private Properties propertiesLanguageToTranslate = new Properties();
    private OrderedProperties propertiesRoot = new OrderedProperties();
    private StringBuilder propertiesToTranslate = new StringBuilder();
    private DialogTranslate dialogTranslate;
    
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
        .forEach(key -> this.propertiesToTranslate.append("\n\n"+ key.getKey() +"="+ key.getValue().replace("{@|@}","\\\n")));
        
        this.dialogTranslate.setTextBeforeChange(this.propertiesToTranslate.toString().trim());
        
        this.dialogTranslate.buttonSend.setEnabled(true);
        this.dialogTranslate.getTextToTranslate()[0].setText(this.dialogTranslate.getTextBeforeChange());
        this.dialogTranslate.getTextToTranslate()[0].setCaretPosition(0);
        this.dialogTranslate.getTextToTranslate()[0].setEditable(true);
        
        if (this.dialogTranslate.getLanguage() != Language.OT) {
            
            int percentTranslated = 100 * this.propertiesLanguageToTranslate.size() / this.propertiesRoot.size();
            this.dialogTranslate.getProgressBarTranslation().setValue(percentTranslated);
            this.dialogTranslate.getProgressBarTranslation().setString(percentTranslated +"% translated into "+ this.dialogTranslate.getLanguage());
        }
    }

    private void loadFromGithub() throws IOException {
        
        this.loadRootFromGithub();
        
        if (this.dialogTranslate.getLanguage() != Language.OT) {
            this.loadLanguageFromGithub();
        } else {
            LOGGER.info("Text to translate loaded from source");
        }
    }

    private void logFileNotFound(IOException eGithub) throws IOException {
        
        if (this.propertiesLanguageToTranslate.size() == 0) {
            
            if (this.dialogTranslate.getLanguage() == Language.OT) {
                LOGGER.info("Language file not found, text to translate loaded from local", eGithub);
            } else {
                LOGGER.info("Language file not found, text to translate into "+ this.dialogTranslate.getLanguage() +" loaded from local", eGithub);
            }
        } else if (this.propertiesRoot.size() == 0) {
            throw new IOException("Reference language not found");
        }
    }
    
    private void loadRootFromGithub() throws IOException {
        
        try {
            String pageSourceRoot = MediatorModel.model().getMediatorUtils().getConnectionUtil().getSourceLineFeed(
                MediatorModel.model().getMediatorUtils().getPropertiesUtil().getProperties().getProperty("github.webservice.i18n.url")
            );
            
            this.propertiesRoot.load(new StringReader(Pattern.compile("\\\\\n").matcher(Matcher.quoteReplacement(pageSourceRoot)).replaceAll("{@|@}")));
            
            LOGGER.info("Reference language loaded from Github");
            
        } catch (IOException e) {
            
            this.propertiesRoot.load(new StringReader(Pattern.compile("\\\\\n").matcher(Matcher.quoteReplacement(new String(Files.readAllBytes(Paths.get("/com/jsql/i18n/jsql.properties"))))).replaceAll("{@|@}")));
            LOGGER.info("Reference language loaded from local");
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        }
    }
    
    private void loadLanguageFromGithub() throws IOException {
        
        try {
            String pageSourceLanguage = MediatorModel.model().getMediatorUtils().getConnectionUtil().getSourceLineFeed(
                "https://raw.githubusercontent.com/ron190/jsql-injection/master/src/main/resources/i18n/jsql_"+ this.dialogTranslate.getLanguage().getLabelLocale() +".properties"
            );
            
            this.propertiesLanguageToTranslate.load(new StringReader(pageSourceLanguage));
            
            LOGGER.info("Text for "+ this.dialogTranslate.getLanguage() +" translation loaded from Github");
            
        } catch (IOException e) {
            
            this.propertiesLanguageToTranslate.load(new StringReader(new String(Files.readAllBytes(Paths.get("/com/jsql/i18n/jsql_"+ this.dialogTranslate.getLanguage().getLabelLocale() +".properties")))));
            LOGGER.info("Text for "+ this.dialogTranslate.getLanguage() +" translation loaded from local");
            
            // Ignore
            IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
            LOGGER.trace(exceptionIgnored, exceptionIgnored);
        }
    }
}