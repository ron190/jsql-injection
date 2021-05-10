/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.dialog.translate.SwingWorkerGithubLocale;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * A dialog displaying current locale translation percentage.
 */
@SuppressWarnings("serial")
public class DialogTranslate extends JDialog {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Button receiving focus.
     */
    private JButton buttonSend = new JButton("Send");
    
    private Language language;
    
    private JLabel labelTranslation = new JLabel();
    
    // Contact info, use HTML text
    private JTextArea textToTranslate = new JPopupTextArea(new JTextAreaPlaceholder("Text to translate")).getProxy();
    
    private JProgressBar progressBarTranslation = new JProgressBar();

    private String textBeforeChange = StringUtils.EMPTY;

    /**
     * Create a dialog for general information on project jsql.
     */
    public DialogTranslate() {
        
        super(MediatorHelper.frame(), Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(UiUtil.getIcons());

        // Action for ESCAPE key
        ActionListener escapeListener = actionEvent -> DialogTranslate.this.dispose();

        this.getRootPane().registerKeyboardAction(
            escapeListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel lastLine = this.initializeLastLine();

        this.labelTranslation.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        var containerDialog = this.getContentPane();
        containerDialog.add(this.labelTranslation, BorderLayout.NORTH);
        containerDialog.add(lastLine, BorderLayout.SOUTH);

        this.initializeTextToTranslate();

        containerDialog.add(
            new LightScrollPane(1, 0, 1, 0, this.textToTranslate),
            BorderLayout.CENTER
        );
    }

    /**
     * Set back default setting for About frame.
     */
    public final void initializeDialog(final Language language) {
        
        this.progressBarTranslation.setValue(0);
        this.progressBarTranslation.setString("Loading...");
        
        DialogTranslate.this.language = language;
        
        this.labelTranslation.setText(
            String
            .join(
                "",
                "<html>",
                "<b>Contribute and translate parts of jSQL Injection into ",
                language.toString(),
                "</b><br>",
                "Help the community and translate some buttons, menus, tabs and tooltips into ",
                language.toString(),
                ", ",
                "then click on Send to forward your changes to the developer on Github.<br>",
                "<i>E.g. for Chinese, change <b>CONTEXT_MENU_COPY = Copy</b> to <b>CONTEXT_MENU_COPY = \u590d\u5236</b>, then click on Send. The list only displays what needs to be translated ",
                "and is updated as soon as the developer processes your translation.</i>",
                "</html>"
            )
        );
        this.labelTranslation.setIcon(language.getFlag());
        this.labelTranslation.setIconTextGap(8);
        
        DialogTranslate.this.setTitle("Translate to "+ language);
        this.textToTranslate.setText(null);
        this.textToTranslate.setEditable(false);
        this.buttonSend.setEnabled(false);
        
        // Ubuntu Regular is compatible with all required languages, this includes Chinese and Arabic,
        // but it's not a technical Mono Font.
        // Only Monospaced works both for copy/paste utf8 foreign characters in JTextArea and
        // it's a technical Mono Font.
        this.textToTranslate.setFont(new Font(
            UiUtil.FONT_NAME_MONOSPACED,
            Font.PLAIN,
            UIManager.getDefaults().getFont("TextField.font").getSize()
        ));
        
        LOGGER.log(LogLevel.CONSOLE_DEFAULT, "Loading text to translate into {}...", () -> language);
        
        new SwingWorkerGithubLocale(this).execute();
    }

    private JPanel initializeLastLine() {
        
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        this.buttonSend.setContentAreaFilled(false);
        this.buttonSend.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.buttonSend.setBackground(new Color(200, 221, 242));
        this.buttonSend.setToolTipText(
            String
            .join(
                "",
                "<html>",
                "<b>Send your translation to the developer</b><br>",
                "Your translation will be integrated in the next version of jSQL",
                "</html>"
            )
        );
        
        this.buttonSend.addMouseListener(new FlatButtonMouseAdapter(this.buttonSend));
        
        this.buttonSend.addActionListener(actionEvent -> {
            
            if (this.textToTranslate.getText().equals(this.textBeforeChange)) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Nothing changed, translate a piece of text then click on Send");
                return;
            }
            
            // Escape Markdown character # for h1 in .properties
            String clientDescription = this.textToTranslate
                .getText()
                .replace("\\\\", "\\\\\\\\")
                .replaceAll(
                    "(?m)^#",
                    "\\\\#"
                )
                .replace("<", "\\<");
              
            MediatorHelper.model().getMediatorUtils().getGitUtil().sendReport(
                clientDescription,
                ShowOnConsole.YES,
                DialogTranslate.this.language +" translation"
            );
            DialogTranslate.this.setVisible(false);
        });

        this.setLayout(new BorderLayout());
        
        this.progressBarTranslation.setUI(new BasicProgressBarUI());
        this.progressBarTranslation.setOpaque(false);
        this.progressBarTranslation.setStringPainted(true);
        this.progressBarTranslation.setValue(0);
        
        lastLine.add(this.progressBarTranslation);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonSend);
        
        return lastLine;
    }

    private void initializeTextToTranslate() {
        
        this.textToTranslate.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                
                super.mousePressed(e);
                DialogTranslate.this.textToTranslate.requestFocusInWindow();
            }
        });

        this.textToTranslate.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusGained(FocusEvent arg0) {
                
                DialogTranslate.this.textToTranslate.getCaret().setVisible(true);
                DialogTranslate.this.textToTranslate.getCaret().setSelectionVisible(true);
            }
        });

        this.textToTranslate.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.textToTranslate.setDragEnabled(true);

        this.textToTranslate.setComponentPopupMenu(new JPopupMenuText(this.textToTranslate));
    }
    
    
    // Getter / Setter

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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

    public JLabel getLabelTranslation() {
        return this.labelTranslation;
    }

    public JTextArea getTextToTranslate() {
        return this.textToTranslate;
    }

    public JProgressBar getProgressBarTranslation() {
        return this.progressBarTranslation;
    }
}
