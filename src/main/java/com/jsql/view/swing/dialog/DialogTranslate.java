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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Point;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.jsql.util.ConnectionUtil;
import com.jsql.view.swing.HelperGui;
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

    private Lang language;
    
    private JLabel labelTranslation;
    
    /**
     * Create a dialog for general information on project jsql.
     */
    public DialogTranslate() {
        super(MediatorGui.frame(), Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(HelperGui.getIcons());

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

        this.buttonSend = new JButton("Send my translation to developers");
        this.buttonSend.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, HelperGui.BLU_COLOR),
            BorderFactory.createEmptyBorder(2, 20, 2, 20))
        );
        this.buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        this.setLayout(new BorderLayout());
        Container containerDialog = this.getContentPane();
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
        text = new JTextArea[1];
        try {
            text[0] = new JPopupTextArea(new JTextArea()).getProxy();

            InputStream in = DialogAbout.class.getResourceAsStream("/com/jsql/i18n/jsql.properties");
            String line, result = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                result += line+"\n";
            }
            reader.close();
            text[0].setText(result);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        text[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                text[0].requestFocusInWindow();
            }
        });

        text[0].addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                text[0].getCaret().setVisible(true);
                text[0].getCaret().setSelectionVisible(true);
            }
        });

        text[0].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        text[0].setDragEnabled(true);

        text[0].setComponentPopupMenu(new JPopupMenuText(text[0]));

        this.scrollPane = new LightScrollPane(1, 0, 1, 0, text[0]);
        containerDialog.add(this.scrollPane, BorderLayout.CENTER);
    }

    final JTextArea[] text;
    
    /**
     * Set back default setting for About frame.
     */
    public final void reinit(Lang language) {
//        String pageSource = ConnectionUtil.getSource("https://raw.githubusercontent.com/ron190/jsql-injection/master/.version");
        
        InputStream in = DialogAbout.class.getResourceAsStream("/com/jsql/i18n/jsql.properties");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line, result = "";
            while ((line = reader.readLine()) != null) {
                result += line+"\n";
            }
            text[0].setText(result);
            text[0].setCaretPosition(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.language = language;
        
        labelTranslation.setText(
            "<html>"
            + "This is the English text for buttons and menus of jSQL. "
            + "Help the community and translate one word to "+ this.language +" and send your translation to developers.<br>"
            + "<i>This list is refreshed by developers and it contains only the text remaining to translate.</i>"
            + "</html>"
        );
        labelTranslation.setIcon(language.getFlag());
        
        this.setTitle("Translate to "+ language);
    }

    public void requestButtonFocus() {
        this.buttonSend.requestFocusInWindow();
    }
}
