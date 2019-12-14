/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.exception.IgnoreMessageException;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.PropertiesUtil;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * A dialog displaying information on jSQL.
 */
@SuppressWarnings("serial")
public class DialogTranslate extends JDialog {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Button receiving focus.
     */
    public final JButton buttonSend = new JButton("Send");
    
    private Language language;
    
    private final JLabel labelTranslation = new JLabel();
    
    private final JTextArea[] textToTranslate = new JTextArea[1];
    
    private final JProgressBar progressBarTranslation = new JProgressBar();

    private String textBeforeChange = "";

    /**
     * Create a dialog for general information on project jsql.
     */
    public DialogTranslate() {
        super(MediatorGui.frame(), Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(HelperUi.getIcons());

        // Action for ESCAPE key
        ActionListener escapeListener = actionEvent -> DialogTranslate.this.dispose();

        this.getRootPane().registerKeyboardAction(
            escapeListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        
        this.buttonSend.setContentAreaFilled(false);
        this.buttonSend.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.buttonSend.setBackground(new Color(200, 221, 242));
        this.buttonSend.setToolTipText(
            "<html>"
            + "<b>Send your translation to the developer</b><br>"
            + "Your translation will be integrated in the next version of jSQL"
            + "</html>"
        );
        
        this.buttonSend.addMouseListener(new FlatButtonMouseAdapter(this.buttonSend));
        
        this.buttonSend.addActionListener(actionEvent -> {
            if (this.textToTranslate[0].getText().equals(this.textBeforeChange)) {
                LOGGER.warn("Nothing changed, translate a piece of text then click on Send");
                return;
            }
            
            String clientDescription =
                // Escape Markdown character # for h1 in .properties
                this.textToTranslate[0].getText()
                    .replaceAll("\\\\", "\\\\\\\\")
                    .replaceAll("(?m)^#", "\\\\#")
                    .replaceAll("<", "\\\\<")
            ;
              
            MediatorModel.model().gitUtil.sendReport(clientDescription, ShowOnConsole.YES, DialogTranslate.this.language +" translation");
            DialogTranslate.this.setVisible(false);
        });

        this.setLayout(new BorderLayout());
        Container containerDialog = this.getContentPane();
        
        this.progressBarTranslation.setUI(new BasicProgressBarUI());
        this.progressBarTranslation.setOpaque(false);
        this.progressBarTranslation.setStringPainted(true);
        this.progressBarTranslation.setValue(0);
        
        lastLine.add(this.progressBarTranslation);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonSend);

        this.labelTranslation.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        containerDialog.add(
            this.labelTranslation,
            BorderLayout.NORTH
        );
        containerDialog.add(lastLine, BorderLayout.SOUTH);

        // Contact info, use HTML text
        this.textToTranslate[0] = new JPopupTextArea(new JTextAreaPlaceholder("Text to translate")).getProxy();

        this.textToTranslate[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                DialogTranslate.this.textToTranslate[0].requestFocusInWindow();
            }
        });

        this.textToTranslate[0].addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                DialogTranslate.this.textToTranslate[0].getCaret().setVisible(true);
                DialogTranslate.this.textToTranslate[0].getCaret().setSelectionVisible(true);
            }
        });

        this.textToTranslate[0].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.textToTranslate[0].setDragEnabled(true);

        this.textToTranslate[0].setComponentPopupMenu(new JPopupMenuText(this.textToTranslate[0]));

        containerDialog.add(
            new LightScrollPane(1, 0, 1, 0, this.textToTranslate[0]),
            BorderLayout.CENTER
        );
    }

    /**
     * Set back default setting for About frame.
     */
    public final void reinit(final Language language) {
        this.progressBarTranslation.setValue(0);
        this.progressBarTranslation.setString("Loading...");
        
        DialogTranslate.this.language = language;
        
        this.labelTranslation.setText(
            "<html>"
            + "<b>Contribute and translate pieces of jSQL into "+ language +"</b><br>"
            + "Help the community and translate some buttons, menus, tabs and tooltips into "+ language +", "
            + "then click on Send to forward your changes to the developer on Github.<br>"
            + "<i>E.g. for Chinese, change '<b>CONTEXT_MENU_COPY = Copy</b>' to '<b>CONTEXT_MENU_COPY = \u590d\u5236</b>', then click on Send. The list only displays what needs to be translated "
            + "and is updated as soon as the developer processes your translation.</i>"
            + "</html>"
        );
        this.labelTranslation.setIcon(language.getFlag());
        this.labelTranslation.setIconTextGap(8);
        
        DialogTranslate.this.setTitle("Translate to "+ language);
        this.textToTranslate[0].setText(null);
        this.textToTranslate[0].setEditable(false);
        this.buttonSend.setEnabled(false);
        
        // Ubuntu Regular is compatible with all required languages, this includes Chinese and Arabic,
        // but it's not a technical Mono Font.
        // Only Monospaced both works for copy/paste utf8 foreign characters in JTextArea and
        // is a technical Mono Font.
        this.textToTranslate[0].setFont(new Font(
            HelperUi.FONT_NAME_MONOSPACED,
            Font.PLAIN,
            UIManager.getDefaults().getFont("TextField.font").getSize()
        ));
        
        LOGGER.trace("Loading text to translate into "+ language +"...");
        new SwingWorkerGithubLocale().execute();
    }
    
    public void requestButtonFocus() {
        this.buttonSend.requestFocusInWindow();
    }
    
    private class SwingWorkerGithubLocale extends SwingWorker<Object, Object> {
        
        private Properties languageProperties = new Properties();
        private OrderedProperties sourceProperties = new OrderedProperties();
        private StringBuilder propertiesToTranslate = new StringBuilder();
        
        private void getI18nRoot() throws IOException {
            try {
                String pageSourceRoot = MediatorModel.model().connectionUtil.getSourceLineFeed(
                    MediatorModel.model().propertiesUtil.getProperties().getProperty("github.webservice.i18n.url")
                );
                this.sourceProperties.load(new StringReader(Pattern.compile("\\\\\n").matcher(Matcher.quoteReplacement(pageSourceRoot)).replaceAll("{@|@}")));
                LOGGER.info("Reference language loaded from Github");
            } catch (IOException e) {
                this.sourceProperties.load(new StringReader(Pattern.compile("\\\\\n").matcher(Matcher.quoteReplacement(new String(Files.readAllBytes(Paths.get("/com/jsql/i18n/jsql.properties"))))).replaceAll("{@|@}")));
                LOGGER.info("Reference language loaded from local");
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
        }
        
        private void getI18nLanguage() throws IOException {
            try {
                String pageSourceLanguage = MediatorModel.model().connectionUtil.getSourceLineFeed(
                    "https://raw.githubusercontent.com/ron190/jsql-injection/master/web/services/i18n/jsql_"+ DialogTranslate.this.language.getNameLocale() +".properties"
                );
                this.languageProperties.load(new StringReader(pageSourceLanguage));
                LOGGER.info("Text for "+ DialogTranslate.this.language +" translation loaded from Github");
            } catch (IOException e) {
                this.languageProperties.load(new StringReader(new String(Files.readAllBytes(Paths.get("/com/jsql/i18n/jsql_"+ DialogTranslate.this.language.getNameLocale() +".properties")))));
                LOGGER.info("Text for "+ DialogTranslate.this.language +" translation loaded from local");
                
                // Ignore
                IgnoreMessageException exceptionIgnored = new IgnoreMessageException(e);
                LOGGER.trace(exceptionIgnored, exceptionIgnored);
            }
        }
        
        @Override
        protected Object doInBackground() throws Exception {
            Thread.currentThread().setName("SwingWorkerDialogTranslate");
            
            if (DialogTranslate.this.language == Language.OT) {
                DialogTranslate.this.progressBarTranslation.setVisible(false);
            } else {
                DialogTranslate.this.progressBarTranslation.setVisible(true);
            }
            
            try {
                this.getI18nRoot();
                if (DialogTranslate.this.language != Language.OT) {
                    this.getI18nLanguage();
                } else {
                    LOGGER.info("Text to translate loaded from source");
                }
            } catch (IOException eGithub) {
                if (this.languageProperties.size() == 0) {
                    if (DialogTranslate.this.language == Language.OT) {
                        LOGGER.info("Language file not found, text to translate loaded from local", eGithub);
                    } else {
                        LOGGER.info("Language file not found, text to translate into "+ DialogTranslate.this.language +" loaded from local", eGithub);
                    }
                } else if (this.sourceProperties.size() == 0) {
                    throw new IOException("Reference language not found");
                }
            } finally {
                for (Entry<String, String> key: this.sourceProperties.entrySet()) {
                    if (DialogTranslate.this.language == Language.OT || this.languageProperties.size() == 0) {
                        this.propertiesToTranslate.append("\n\n"+ key.getKey() +"="+ key.getValue().replace("{@|@}","\\\n"));
                    } else {
                        if (!this.languageProperties.containsKey(key.getKey())) {
                            this.propertiesToTranslate.append("\n\n"+ key.getKey() +"="+ key.getValue().replace("{@|@}","\\\n"));
                        }
                    }
                }
                
                DialogTranslate.this.textBeforeChange = this.propertiesToTranslate.toString().trim();
                
                DialogTranslate.this.buttonSend.setEnabled(true);
                DialogTranslate.this.textToTranslate[0].setText(DialogTranslate.this.textBeforeChange);
                DialogTranslate.this.textToTranslate[0].setCaretPosition(0);
                DialogTranslate.this.textToTranslate[0].setEditable(true);
                
                if (DialogTranslate.this.language != Language.OT) {
                    int percentTranslated = 100 * this.languageProperties.size() / this.sourceProperties.size();
                    DialogTranslate.this.progressBarTranslation.setValue(percentTranslated);
                    DialogTranslate.this.progressBarTranslation.setString(percentTranslated +"% translated into "+ DialogTranslate.this.language);
                }
            }
            
            return null;
        }
    }
    
}
