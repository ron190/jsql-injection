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
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Point;
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
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * A dialog displaying information on jSQL.
 */
@SuppressWarnings("serial")
public class DialogAbout extends JDialog {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Button receiving focus.
     */
    private JButton buttonClose = null;
    
    /**
     * Dialog scroller.
     */
    private LightScrollPane scrollPane;

    /**
     * Create a dialog for general information on project jsql.
     */
    public DialogAbout() {
        super(MediatorGui.frame(), "About jSQL Injection", Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(HelperUi.getIcons());

        // Action for ESCAPE key
        ActionListener escapeListener = actionEvent -> DialogAbout.this.dispose();

        this.getRootPane().registerKeyboardAction(
            escapeListener, 
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.buttonClose = new JButton("Close");
        this.buttonClose.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, HelperUi.COLOR_BLU),
            BorderFactory.createEmptyBorder(2, 20, 2, 20))
        );
        this.buttonClose.addActionListener(escapeListener);

        this.setLayout(new BorderLayout());
        Container dialogPane = this.getContentPane();
        
        final JButton buttonWebpage = new JButton("Webpage");
        buttonWebpage.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, HelperUi.COLOR_BLU),
            BorderFactory.createEmptyBorder(2, 20, 2, 20))
        );
        buttonWebpage.addActionListener(ev -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/ron190/jsql-injection"));
            } catch (IOException e) {
                LOGGER.warn("Browsing to Url failed", e);
            } catch (URISyntaxException e) {
                LOGGER.warn("Incorrect Url", e);
            } catch (UnsupportedOperationException e) {
                LOGGER.warn("BROWSE action not supported on current platform", e);
            }
        });
        
        buttonWebpage.setContentAreaFilled(false);
        buttonWebpage.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonWebpage.setBackground(new Color(200, 221, 242));
        
        buttonWebpage.addMouseListener(new FlatButtonMouseAdapter(buttonWebpage));
        
        buttonClose.setContentAreaFilled(false);
        buttonClose.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonClose.setBackground(new Color(200, 221, 242));
        
        buttonClose.addMouseListener(new FlatButtonMouseAdapter(buttonClose));
        
        lastLine.add(buttonWebpage);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonClose);

        JLabel iconJSQL = new JLabel(new ImageIcon(HelperUi.URL_ICON_96));
        dialogPane.add(iconJSQL, BorderLayout.WEST);
        dialogPane.add(lastLine, BorderLayout.SOUTH);

        // Contact info, use HTML text
        final JEditorPane[] text = new JEditorPane[1];
        try {
            text[0] = new JEditorPane();
            text[0].setContentType("text/html");

            StringBuilder result = new StringBuilder();
            
            try (
                InputStream in = DialogAbout.class.getResourceAsStream("about.htm");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

            text[0].setText(result.toString().replace("%JSQLVERSION%", InjectionModel.VERSION_JSQL));
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
        text[0].setEditable(false);

        text[0].setComponentPopupMenu(new JPopupMenuText(text[0]));

        text[0].addHyperlinkListener(linkEvent -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(linkEvent.getEventType())) {
                try {
                    Desktop.getDesktop().browse(linkEvent.getURL().toURI());
                } catch (IOException e) {
                    LOGGER.warn("Browsing to Url failed", e);
                } catch (URISyntaxException e) {
                    LOGGER.warn("Incorrect Url", e);
                } catch (UnsupportedOperationException e) {
                    LOGGER.warn("BROWSE action not supported on current platform", e);
                }
            }
        });

        this.scrollPane = new LightScrollPane(1, 1, 1, 0, text[0]);
        dialogPane.add(this.scrollPane, BorderLayout.CENTER);

        this.reinit();
    }

    /**
     * Set back default setting for About frame.
     */
    public final void reinit() {
        this.scrollPane.scrollPane.getViewport().setViewPosition(new Point(0, 0));
        this.setSize(460, 300);
        this.setLocationRelativeTo(MediatorGui.frame());
        this.buttonClose.requestFocusInWindow();
        this.getRootPane().setDefaultButton(this.buttonClose);
    }

    public void requestButtonFocus() {
        this.buttonClose.requestFocusInWindow();
    }
    
}
