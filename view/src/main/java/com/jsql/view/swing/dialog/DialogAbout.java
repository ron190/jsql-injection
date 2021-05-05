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
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * A dialog displaying information on jSQL.
 */
@SuppressWarnings("serial")
public class DialogAbout extends JDialog {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

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
        
        super(MediatorHelper.frame(), "About jSQL Injection", Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(UiUtil.getIcons());

        // Action for ESCAPE key
        ActionListener escapeListener = actionEvent -> DialogAbout.this.dispose();

        this.getRootPane().registerKeyboardAction(
            escapeListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        this.setLayout(new BorderLayout());
        Container dialogPane = this.getContentPane();

        JPanel lastLine = this.initializeLastLine(escapeListener);

        var iconJsql = new JLabel(new ImageIcon(UiUtil.URL_ICON_96));
        dialogPane.add(iconJsql, BorderLayout.WEST);
        dialogPane.add(lastLine, BorderLayout.SOUTH);

        // Contact info, use HTML text
        final JEditorPane text = this.initializeEditorPane();

        this.scrollPane = new LightScrollPane(1, 1, 1, 0, text);
        dialogPane.add(this.scrollPane, BorderLayout.CENTER);

        this.initializeDialog();
    }

    private JPanel initializeLastLine(ActionListener escapeListener) {
        
        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        final JButton buttonWebpage = this.initializeButtonWebpage();
        
        this.initializeButtonClose(escapeListener);
        
        lastLine.add(buttonWebpage);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonClose);
        
        return lastLine;
    }

    private void initializeButtonClose(ActionListener escapeListener) {
        
        this.buttonClose = new JButton("Close");
        this.buttonClose.setBorder(BorderFactory.createCompoundBorder(
            UiUtil.BORDER_FOCUS_GAINED,
            BorderFactory.createEmptyBorder(2, 20, 2, 20))
        );
        this.buttonClose.addActionListener(escapeListener);
        
        this.buttonClose.setContentAreaFilled(false);
        this.buttonClose.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.buttonClose.setBackground(new Color(200, 221, 242));
        
        this.buttonClose.addMouseListener(new FlatButtonMouseAdapter(this.buttonClose));
    }

    private JButton initializeButtonWebpage() {
        
        final var buttonWebpage = new JButton("Webpage");
        
        buttonWebpage.setBorder(BorderFactory.createCompoundBorder(
            UiUtil.BORDER_FOCUS_GAINED,
            BorderFactory.createEmptyBorder(2, 20, 2, 20))
        );
        
        buttonWebpage.addActionListener(ev -> {
            
            try {
                Desktop.getDesktop().browse(new URI((String) MediatorHelper.model().getMediatorUtils().getPropertiesUtil().getProperties().get("github.url")));
                
            } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Browsing to Url failed", e);
            }
        });
        
        buttonWebpage.setContentAreaFilled(false);
        buttonWebpage.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonWebpage.setBackground(new Color(200, 221, 242));
        
        buttonWebpage.addMouseListener(new FlatButtonMouseAdapter(buttonWebpage));
        
        return buttonWebpage;
    }

    private JEditorPane initializeEditorPane() {
        
        var editorPane = new JEditorPane();
        
        // Fix #82540: NoClassDefFoundError on setText()
        try {
            editorPane.setContentType("text/html");

            var result = new StringBuilder();
            
            try (
                InputStream in = DialogAbout.class.getClassLoader().getResourceAsStream("swing/about.htm");
                var reader = new BufferedReader(new InputStreamReader(in))
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    
                    result.append(line);
                }
            }

            editorPane.setText(result.toString().replace("%JSQLVERSION%", MediatorHelper.model().getVersionJsql()));
            
        } catch (NoClassDefFoundError | IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        editorPane.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                
                super.mousePressed(e);
                editorPane.requestFocusInWindow();
            }
        });

        editorPane.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusGained(FocusEvent arg0) {
                
                editorPane.getCaret().setVisible(true);
                editorPane.getCaret().setSelectionVisible(true);
            }
        });

        editorPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        editorPane.setDragEnabled(true);
        editorPane.setEditable(false);

        editorPane.setComponentPopupMenu(new JPopupMenuText(editorPane));

        editorPane.addHyperlinkListener(linkEvent -> {
            
            if (HyperlinkEvent.EventType.ACTIVATED.equals(linkEvent.getEventType())) {
                
                try {
                    Desktop.getDesktop().browse(linkEvent.getURL().toURI());
                    
                } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
                    
                    LOGGER.log(LogLevel.CONSOLE_ERROR, "Browsing to Url failed", e);
                }
            }
        });
        
        return editorPane;
    }

    /**
     * Set back default setting for About frame.
     */
    public final void initializeDialog() {
        
        this.scrollPane.scrollPane.getViewport().setViewPosition(new Point(0, 0));
        this.setSize(533, 400);
        this.setLocationRelativeTo(MediatorHelper.frame());
        this.buttonClose.requestFocusInWindow();
        this.getRootPane().setDefaultButton(this.buttonClose);
    }

    public void requestButtonFocus() {
        
        this.buttonClose.requestFocusInWindow();
    }
}
