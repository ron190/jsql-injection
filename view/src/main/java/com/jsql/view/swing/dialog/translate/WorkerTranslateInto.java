package com.jsql.view.swing.dialog.translate;

import com.jsql.util.ConnectionUtil;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.PropertiesUtil;
import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkerTranslateInto extends SwingWorker<Object, Object> {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final Properties propertiesLanguageToTranslate = new Properties();
    private final SortedProperties propertiesRoot = new SortedProperties();
    private final StringBuilder propertiesToTranslate = new StringBuilder();
    private final DialogTranslate dialogTranslate;
    
    private final ConnectionUtil connectionUtil = MediatorHelper.model().getMediatorUtils().getConnectionUtil();
    private final PropertiesUtil propertiesUtil = MediatorHelper.model().getMediatorUtils().getPropertiesUtil();
    
    private static final String LINE_FEED_ESCAPE = "{@|@}";
    private static final String LINE_FEED = "\\\\[\n\r]+";
    
    public WorkerTranslateInto(DialogTranslate dialogTranslate) {
        this.dialogTranslate = dialogTranslate;
    }

    @Override
    protected Object doInBackground() throws Exception {
        Thread.currentThread().setName("SwingWorkerDialogTranslate");
        this.dialogTranslate.getProgressBarTranslation().setVisible(this.dialogTranslate.getLanguageInto() != Language.OT);
        try {
            this.loadFromGithub();
        } catch (IOException eGithub) {
            this.logFileNotFound(eGithub);
        } finally {
            this.displayDiff();
        }
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Remaining text to translate loaded, send your version to contribute");
        return null;
    }

    private void displayDiff() {
        this.propertiesRoot.entrySet().stream()
            .filter(key ->
                this.dialogTranslate.getLanguageInto() == Language.OT
                || this.propertiesLanguageToTranslate.isEmpty()
                || !this.propertiesLanguageToTranslate.containsKey(key.getKey())
            )
            .forEach(key -> this.propertiesToTranslate.append(
                String.format("%n%n%s=%s", key.getKey(), key.getValue().toString().replace(WorkerTranslateInto.LINE_FEED_ESCAPE,"\\\n"))
            ));
        
        this.dialogTranslate.setTextBeforeChange(this.propertiesToTranslate.toString().trim());
        this.dialogTranslate.getButtonSend().setEnabled(true);
        this.dialogTranslate.getTextToTranslate().setText(this.dialogTranslate.getTextBeforeChange());
        this.dialogTranslate.getTextToTranslate().setCaretPosition(0);
        this.dialogTranslate.getTextToTranslate().setEditable(true);
        if (this.dialogTranslate.getLanguageInto() != Language.OT) {
            int percentTranslated = 100 * this.propertiesLanguageToTranslate.size() / this.propertiesRoot.size();
            this.dialogTranslate.getProgressBarTranslation().setValue(percentTranslated);

            var bundleInto = ResourceBundle.getBundle(I18nUtil.BASE_NAME, Locale.forLanguageTag(this.dialogTranslate.getLanguageInto().getLanguageTag()));
            var localeInto = Locale.forLanguageTag(this.dialogTranslate.getLanguageInto().getLanguageTag());
            this.dialogTranslate.getProgressBarTranslation().setString(
                String.format(
                    "%s%% %s %s",
                    percentTranslated,
                    bundleInto.getString("TRANSLATION_PROGRESS"),
                    localeInto.getDisplayLanguage(localeInto)
                )
            );
        }
    }

    private void loadFromGithub() throws IOException, URISyntaxException {
        this.loadRootFromGithub();
        if (this.dialogTranslate.getLanguageInto() != Language.OT) {
            this.loadLanguageFromGithub();
        }
    }

    private void logFileNotFound(IOException e) throws IOException {
        if (this.propertiesLanguageToTranslate.isEmpty()) {
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, () -> I18nUtil.valueByKey("LOG_I18N_TEXT_NOT_FOUND"), e);
        } else if (this.propertiesRoot.isEmpty()) {
            throw new IOException("Reference language not found");
        }
    }
    
    private void loadRootFromGithub() throws IOException, URISyntaxException {
        try {
            String pageSourceRoot = this.connectionUtil.getSourceLineFeed(
                this.propertiesUtil.getProperty("github.webservice.i18n.root")
            );
            String pageSourceRootFixed = Pattern.compile(WorkerTranslateInto.LINE_FEED).matcher(Matcher.quoteReplacement(pageSourceRoot)).replaceAll(WorkerTranslateInto.LINE_FEED_ESCAPE);
            this.propertiesRoot.load(new StringReader(pageSourceRootFixed));
        } catch (IOException e) {
            var uri = ClassLoader.getSystemResource("i18n/jsql.properties").toURI();
            var path = Paths.get(uri);
            byte[] root = Files.readAllBytes(path);
            var rootI18n = new String(root, StandardCharsets.UTF_8);
            String rootI18nFixed = Pattern.compile(WorkerTranslateInto.LINE_FEED).matcher(Matcher.quoteReplacement(rootI18n)).replaceAll(WorkerTranslateInto.LINE_FEED_ESCAPE);
            this.propertiesRoot.load(new StringReader(rootI18nFixed));
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Reference language loaded from local");
            LOGGER.log(LogLevelUtil.IGNORE, e);
        }
    }
    
    private void loadLanguageFromGithub() throws IOException, URISyntaxException {
        try {
            String pageSourceLanguage = this.connectionUtil.getSourceLineFeed(
                String.format(
                    "%sjsql_%s.properties",
                    this.propertiesUtil.getProperty("github.webservice.i18n.locale"),
                    this.dialogTranslate.getLanguageInto().getLanguageTag()
                )
            );
            this.propertiesLanguageToTranslate.load(new StringReader(pageSourceLanguage));
        } catch (IOException e) {
            var uri = ClassLoader.getSystemResource(
                String.format("i18n/jsql_%s.properties", this.dialogTranslate.getLanguageInto().getLanguageTag())
            ).toURI();
            
            var path = Paths.get(uri);
            byte[] root = Files.readAllBytes(path);
            var localeI18n = new String(root, StandardCharsets.UTF_8);
            String localeI18nFixed = Pattern.compile(WorkerTranslateInto.LINE_FEED).matcher(localeI18n).replaceAll(WorkerTranslateInto.LINE_FEED_ESCAPE);
            
            this.propertiesLanguageToTranslate.load(new StringReader(localeI18nFixed));
            LOGGER.log(
                LogLevelUtil.CONSOLE_INFORM,
                String.format("GitHub failure, %s translation loaded from local", this.dialogTranslate.getLanguageInto())
            );
            LOGGER.log(LogLevelUtil.IGNORE, e);
        }
    }
}