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
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.dialog.translate.WorkerTranslateInto;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A dialog displaying current locale translation percentage.
 */
public class DialogTranslate extends JDialog {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Button receiving focus.
     */
    private final JButton buttonSend = new JButton("Send");
    
    private Language language;
    
    private final JLabel labelTranslation = new JLabel();
    
    // Contact info, use HTML text
    private final JTextArea textToTranslate = new JPopupTextArea(new JTextAreaPlaceholder("Text to translate")).getProxy();
    
    private final JProgressBar progressBarTranslation = new JProgressBar();

    private String textBeforeChange = StringUtils.EMPTY;

    /**
     * Create a dialog for general information on project jsql.
     */
    public DialogTranslate() {
        super(MediatorHelper.frame(), Dialog.ModalityType.MODELESS);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImages(UiUtil.getIcons());  // Define a small and large app icon

        ActionListener escapeListener = actionEvent -> DialogTranslate.this.dispose();  // Action for ESCAPE key
        this.getRootPane().registerKeyboardAction(
            escapeListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel lastLine = this.initializeLastLine();

        this.labelTranslation.setBorder(UiUtil.BORDER_5PX);
        var contentPane = this.getContentPane();
        contentPane.add(this.labelTranslation, BorderLayout.NORTH);
        contentPane.add(lastLine, BorderLayout.SOUTH);

        this.initializeTextToTranslate();

        contentPane.add(new JScrollPane(this.textToTranslate), BorderLayout.CENTER);
    }

    /**
     * Set back default setting for About frame.
     */
    public final void initializeDialog(final Language language) {
        
        this.progressBarTranslation.setValue(0);
        this.progressBarTranslation.setString("Loading...");
        
        this.language = language;
        
        this.labelTranslation.setText(
            String.join(
                "",
                "<html>",
                "<b>Contribute and translate parts of jSQL Injection into ",
                language.toString(),
                "</b><br>",
                "Help the community and translate some buttons, menus, tabs and tooltips into ",
                language.toString(),
                ", ",
                "then click on Send to forward your changes to the developer on GitHub.<br>",
                "<i>E.g. for French, change <b>CONTEXT_MENU_COPY = Copy</b> to <b>CONTEXT_MENU_COPY = Copier</b>, then click on Send. The list only displays what needs to be translated ",
                "and is updated as soon as the developer processes your request.</i>",
                "</html>"
            )
        );
        this.labelTranslation.setIcon(language.getFlag());
        this.labelTranslation.setIconTextGap(8);
        
        this.setTitle("Translate to "+ language);
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
        
        new WorkerTranslateInto(this).execute();
    }

    private JPanel initializeLastLine() {
        
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(UiUtil.BORDER_5PX);
        
        this.buttonSend.setToolTipText(
            String.join(
                "",
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
                this.language +" translation"
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
        this.textToTranslate.setBorder(UiUtil.BORDER_5PX);
        this.textToTranslate.setDragEnabled(true);
        this.textToTranslate.getCaret().setBlinkRate(500);
        this.textToTranslate.setComponentPopupMenu(new JPopupMenuText(this.textToTranslate));
    }
    
    
    // Getter / Setter

    public Language getLanguage() {
        return this.language;
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
