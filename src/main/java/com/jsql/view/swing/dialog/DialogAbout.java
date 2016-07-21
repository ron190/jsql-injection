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
import java.awt.Desktop;
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
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.scrollpane.LightScrollPane;

/**
 * A dialog displaying information on jSQL.
 */
@SuppressWarnings("serial")
public class DialogAbout extends JDialog {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(DialogAbout.class);

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
        ActionListener escapeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogAbout.this.dispose();
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

        this.buttonClose = new JButton("Close");
        this.buttonClose.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, HelperUi.BLU_COLOR),
            BorderFactory.createEmptyBorder(2, 20, 2, 20))
        );
        this.buttonClose.addActionListener(escapeListener);

        this.setLayout(new BorderLayout());
        Container dialogPane = this.getContentPane();
        
        final JButton buttonWebpage = new JButton("Webpage");
        buttonWebpage.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, HelperUi.BLU_COLOR),
            BorderFactory.createEmptyBorder(2, 20, 2, 20))
        );
        buttonWebpage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/ron190/jsql-injection"));
                } catch (IOException | URISyntaxException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        
        buttonWebpage.setContentAreaFilled(false);
        buttonWebpage.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonWebpage.setBackground(new Color(200, 221, 242));
        
        buttonWebpage.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                buttonWebpage.setContentAreaFilled(true);
                buttonWebpage.setBorder(HelperUi.BLU_ROUND_BORDER);
                
            }

            @Override public void mouseExited(MouseEvent e) {
                buttonWebpage.setContentAreaFilled(false);
                buttonWebpage.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            }
        });
        
        buttonClose.setContentAreaFilled(false);
        buttonClose.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonClose.setBackground(new Color(200, 221, 242));
        
        buttonClose.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                buttonClose.setContentAreaFilled(true);
                buttonClose.setBorder(HelperUi.BLU_ROUND_BORDER);
                
            }

            @Override public void mouseExited(MouseEvent e) {
                buttonClose.setContentAreaFilled(false);
                buttonClose.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            }
        });
        
        lastLine.add(buttonWebpage);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonClose);

        JLabel iconJSQL = new JLabel(new ImageIcon(DialogAbout.class.getResource("/com/jsql/view/swing/resources/images/icons/app-32x32.png")));
        iconJSQL.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));
        dialogPane.add(iconJSQL, BorderLayout.WEST);
        dialogPane.add(lastLine, BorderLayout.SOUTH);

        // Contact info, use HTML text
        final JEditorPane[] text = new JEditorPane[1];
        try {
            text[0] = new JEditorPane();
            text[0].setContentType("text/html");

            InputStream in = DialogAbout.class.getResourceAsStream("about.htm");
            String line, result = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            reader.close();

            text[0].setText(result.replace("%JSQLVERSION%", InjectionModel.VERSION_JSQL));
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

        text[0].addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent linkEvent) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(linkEvent.getEventType())) {
                    try {
                        Desktop.getDesktop().browse(linkEvent.getURL().toURI());
                    } catch (IOException e) {
                        LOGGER.warn("Browsing to Url failed", e);
                    } catch (URISyntaxException e) {
                        LOGGER.error("Incorrect Url", e);
                    }
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
