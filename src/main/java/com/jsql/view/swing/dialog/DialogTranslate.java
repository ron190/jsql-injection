/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;

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
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.util.ConnectionUtil;
import com.jsql.util.GitUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextArea;

/**
 * A dialog displaying information on jSQL.
 */
@SuppressWarnings("serial")
public class DialogTranslate extends JDialog {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(DialogTranslate.class);

    /**
     * Button receiving focus.
     */
    public JButton buttonSend = null;
    
    /**
     * Dialog scroller.
     */
    public LightScrollPane scrollPane;

    private Language language;
    
    private JLabel labelTranslation;
    
    final JTextArea[] textToTranslate;
    
    /**
     * Create a dialog for general information on project jsql.
     */
    public DialogTranslate() {
        super(MediatorGui.frame(), Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(HelperUi.getIcons());

        // Action for ESCAPE key
        ActionListener escapeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogTranslate.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(
            escapeListener, 
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.buttonSend = new JButton("Send");
        
        this.buttonSend.setContentAreaFilled(false);
        this.buttonSend.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.buttonSend.setBackground(new Color(200, 221, 242));
        
        this.buttonSend.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                DialogTranslate.this.buttonSend.setContentAreaFilled(true);
                DialogTranslate.this.buttonSend.setBorder(HelperUi.BLU_ROUND_BORDER);
                
            }

            @Override public void mouseExited(MouseEvent e) {
                DialogTranslate.this.buttonSend.setContentAreaFilled(false);
                DialogTranslate.this.buttonSend.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            }
        });
        
        this.buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clientDescription = 
                    // Escape Markdown character # for h1 in .properties
                    textToTranslate[0].getText()
                        .replaceAll("\\\\", "\\\\\\\\")
                        .replaceAll("(?m)^#", "\\\\#")
                        .replaceAll("<", "\\\\<")
                ;
                  
                GitUtil.sendReport(clientDescription, ShowOnConsole.YES, DialogTranslate.this.language + " translation");
            }
        });

        this.setLayout(new BorderLayout());
        Container containerDialog = this.getContentPane();
        
        progressBarTranslation.setUI(new BasicProgressBarUI());
        progressBarTranslation.setOpaque(false);
        progressBarTranslation.setStringPainted(true);
        progressBarTranslation.setValue(0);
        progressBarTranslation.setVisible(false);
        
        lastLine.add(progressBarTranslation);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonSend);

        labelTranslation = new JLabel();
        labelTranslation.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        containerDialog.add(
            labelTranslation,
            BorderLayout.NORTH
        );
        containerDialog.add(lastLine, BorderLayout.SOUTH);

        // Contact info, use HTML text
        textToTranslate = new JTextArea[1];
        textToTranslate[0] = new JPopupTextArea(new JTextArea()).getProxy();

        textToTranslate[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                textToTranslate[0].requestFocusInWindow();
            }
        });

        textToTranslate[0].addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                textToTranslate[0].getCaret().setVisible(true);
                textToTranslate[0].getCaret().setSelectionVisible(true);
            }
        });

        textToTranslate[0].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textToTranslate[0].setDragEnabled(true);

        textToTranslate[0].setComponentPopupMenu(new JPopupMenuText(textToTranslate[0]));

        this.scrollPane = new LightScrollPane(1, 0, 1, 0, textToTranslate[0]);
        containerDialog.add(this.scrollPane, BorderLayout.CENTER);
    }

    JProgressBar progressBarTranslation = new JProgressBar();

    /**
     * Set back default setting for About frame.
     */
    public final void reinit(final Language language) {
        DialogTranslate.this.language = language;
        
        labelTranslation.setText(
            "<html>"
            + "<b>Contribute and translate pieces of jSQL into "+ DialogTranslate.this.language +"</b><br>"
            + "Help the community and translate some buttons, menus, tabs and tooltips into "+ DialogTranslate.this.language +", "
            + "then click on Send to forward the changes to the developer on Github, it's that simple.<br>"
            + "<i>E.g. for Chinese, change '<b>COPY = Copy</b>' to '<b>COPY = \u590d\u5236</b>', then click on Send. The list only displays what needs to be translated "
            + "and it's updated as soon as the developer processes your translation.</i><br>"
            + "<b style='color:blue'>Thank you for your contribution !</b>"
            + "</html>"
        );
        labelTranslation.setIcon(language.getFlag());
        
        DialogTranslate.this.setTitle("Translate to "+ language);
        textToTranslate[0].setText(null);
        
        // Monospaced is compatible with all required languages, this includes Chinese and Arabic
        textToTranslate[0].setFont(new Font("Monospaced", Font.PLAIN, ((Font) UIManager.get("TextField.font")).getSize()));
        
        new SwingWorker<Object, Object>(){
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    String pageSource = ConnectionUtil.getSource(
                        "https://raw.githubusercontent.com/ron190/jsql-injection/master/web/services/i18n/"
                        + "jsql_"+ DialogTranslate.this.language.name().toLowerCase() +".properties"
                    );
                    
                    LOGGER.info("Text to translate into "+ DialogTranslate.this.language +" loaded from Github");
                    
                    Properties prop = new Properties();       
                    prop.load(new StringReader(pageSource));
                    int percentTranslated = 100 * (I18n.keys().size() - prop.size()) / I18n.keys().size();
                    progressBarTranslation.setValue(percentTranslated);
                    progressBarTranslation.setString(percentTranslated +"% translated to "+ language);
                
                    if (language == Language.OT || percentTranslated == 0) {
                        progressBarTranslation.setVisible(false);
                    } else {
                        progressBarTranslation.setVisible(true);
                    }
                    
                    textToTranslate[0].setText(pageSource);
                    textToTranslate[0].setCaretPosition(0);
                } catch (IOException errGithub) {
                    LOGGER.info("Text to translate loaded from local");
                    
                    InputStream in = DialogAbout.class.getResourceAsStream("/com/jsql/i18n/jsql.properties");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                        String line, result = "";
                        while ((line = reader.readLine()) != null) {
                            result += line+"\n";
                        }
                        textToTranslate[0].setText(result);
                        textToTranslate[0].setCaretPosition(0);
                    } catch (IOException errFile) {
                        LOGGER.warn("File error: /com/jsql/i18n/jsql.properties");
                    }
                }
                
                return null;
            }
        }.execute();
    }

    public void requestButtonFocus() {
        this.buttonSend.requestFocusInWindow();
    }
}
