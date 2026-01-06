/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.dialog;

import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.dialog.translate.WorkerTranslateInto;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A dialog displaying current locale translation percentage.
 */
public class DialogTranslate extends JDialog {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Button receiving focus.
     */
    private final JButton buttonSend = new JButton();  // "Send" text displayed in target locale at runtime

    private Language languageInto;
    private final JLabel labelTranslation = new JLabel();
    private final JTextArea textToTranslate = new JPopupTextArea(new JTextAreaPlaceholder(I18nViewUtil.valueByKey("TRANSLATION_PLACEHOLDER"))).getProxy();
    private final JProgressBar progressBarTranslation = new JProgressBar();
    private String textBeforeChange = StringUtils.EMPTY;
    private final JPanel lastLine;

    /**
     * Displays dialog into target locale.
     * Switching language does not affect this dialog
     */
    public DialogTranslate() {
        super(MediatorHelper.frame(), Dialog.ModalityType.MODELESS);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImages(UiUtil.getIcons());  // Define a small and large app icon

        ActionListener escapeListener = actionEvent -> this.dispose();  // Action for ESCAPE key
        this.getRootPane().registerKeyboardAction(
            escapeListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        this.lastLine = this.initLastLine();

        this.labelTranslation.setBorder(UiUtil.BORDER_5PX);
        var contentPane = this.getContentPane();
        contentPane.add(this.labelTranslation, BorderLayout.NORTH);
        contentPane.add(this.lastLine, BorderLayout.SOUTH);

        this.initTextToTranslate();

        contentPane.add(new JScrollPane(this.textToTranslate), BorderLayout.CENTER);
    }

    /**
     * Set back default setting for About frame.
     */
    public final void initDialog(final Language language) {
        this.progressBarTranslation.setValue(0);
        this.progressBarTranslation.setString("Loading...");
        this.languageInto = language;

        var bundleInto = ResourceBundle.getBundle(I18nUtil.BASE_NAME, Locale.forLanguageTag(language.getLanguageTag()));
        var localeInto = Locale.forLanguageTag(language.getLanguageTag());
        this.labelTranslation.setText(  // set language into
            String.format(
                bundleInto.getString("TRANSLATION_TEXT"),
                localeInto.getDisplayLanguage(localeInto),
                localeInto.getDisplayLanguage(localeInto)
            )
        );
        ComponentOrientation orientation = ComponentOrientation.getOrientation(Locale.forLanguageTag(language.getLanguageTag()));
        this.labelTranslation.setComponentOrientation(orientation);
        this.progressBarTranslation.setComponentOrientation(orientation);
        this.lastLine.setComponentOrientation(orientation);
        this.buttonSend.setText(  // display text at runtime
            ResourceBundle.getBundle(
                I18nUtil.BASE_NAME,
                Locale.forLanguageTag(language.getLanguageTag())
            ).getString("TRANSLATION_SEND")
        );

        this.textToTranslate.setText(null);
        this.textToTranslate.setEditable(false);
        this.buttonSend.setEnabled(false);  // will be enabled when done with GitHub
        
        // Ubuntu Regular is compatible with all required languages, this includes Chinese and Arabic,
        // but it's not a technical Mono Font.
        // Only Monospaced works both for copy/paste utf8 foreign characters in JTextArea, and
        // it's a technical Mono Font.
        this.textToTranslate.setFont(new Font(
            UiUtil.FONT_NAME_MONOSPACED,
            Font.PLAIN,
            UIManager.getDefaults().getFont("TextField.font").getSize()
        ));
        
        new WorkerTranslateInto(this).execute();

        this.setIconImage(language.getFlag().getImage());
        this.setTitle(bundleInto.getString("TRANSLATION_TITLE") +" "+ localeInto.getDisplayLanguage(localeInto));
        if (!this.isVisible()) {  // Center the dialog
            this.setSize(640, 460);
            this.setLocationRelativeTo(MediatorHelper.frame());
            this.getRootPane().setDefaultButton(this.getButtonSend());
        }
        this.setVisible(true);
    }

    private JPanel initLastLine() {
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(UiUtil.BORDER_5PX);
        
        this.buttonSend.setToolTipText(
            String.join(
                StringUtils.EMPTY,
                "<html>",
                "<b>Send your translation to the developer</b><br>",
                "Your translation will be integrated in the next version of jSQL",
                "</html>"
            )
        );
        
        this.buttonSend.addActionListener(actionEvent -> {
            if (this.textToTranslate.getText().equals(this.textBeforeChange)) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Nothing changed, translate a piece of text then click on Send");
                return;
            }
            
            // Escape Markdown character # for h1 in .properties
            String clientDescription = this.textToTranslate.getText()
                .replace("\\\\", "\\\\\\\\")
                .replaceAll("(?m)^#","\\\\#")
                .replace("<", "\\<");
              
            MediatorHelper.model().getMediatorUtils().getGitUtil().sendReport(
                clientDescription,
                ShowOnConsole.YES,
                this.languageInto +" translation"
            );
            this.setVisible(false);
        });

        this.setLayout(new BorderLayout());
        
        this.progressBarTranslation.setStringPainted(true);
        this.progressBarTranslation.setValue(0);
        
        lastLine.add(this.progressBarTranslation);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonSend);
        return lastLine;
    }

    private void initTextToTranslate() {
        this.textToTranslate.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                DialogTranslate.this.textToTranslate.requestFocusInWindow();
            }
        });
        this.textToTranslate.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                DialogTranslate.this.textToTranslate.getCaret().setVisible(true);
                DialogTranslate.this.textToTranslate.getCaret().setSelectionVisible(true);
            }
        });
        this.textToTranslate.setBorder(UiUtil.BORDER_5PX);
        this.textToTranslate.setDragEnabled(true);
        this.textToTranslate.getCaret().setBlinkRate(500);
        this.textToTranslate.setComponentPopupMenu(new JPopupMenuText(this.textToTranslate));
    }
    
    
    // Getter / Setter

    public Language getLanguageInto() {
        return this.languageInto;
    }

    public String getTextBeforeChange() {
        return this.textBeforeChange;
    }

    public void setTextBeforeChange(String textBeforeChange) {
        this.textBeforeChange = textBeforeChange;
    }

    public JButton getButtonSend() {
        return this.buttonSend;
    }

    public JTextArea getTextToTranslate() {
        return this.textToTranslate;
    }

    public JProgressBar getProgressBarTranslation() {
        return this.progressBarTranslation;
    }
}
