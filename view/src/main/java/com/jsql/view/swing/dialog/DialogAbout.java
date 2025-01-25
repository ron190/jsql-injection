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

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A dialog displaying information about jSQL.
 */
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
     * Create a dialog about project general information.
     */
    public DialogAbout() {
        super(MediatorHelper.frame(), I18nUtil.valueByKey("ABOUT_WINDOW_TITLE") +" "+ StringUtil.APP_NAME, Dialog.ModalityType.MODELESS);
        I18nViewUtil.addComponentForKey("ABOUT_WINDOW_TITLE", this);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setIconImages(UiUtil.getIcons());  // Define a small and large app icon

        ActionListener escapeListener = actionEvent -> this.dispose();  // Action for ESCAPE key
        this.getRootPane().registerKeyboardAction(
            escapeListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        this.setLayout(new BorderLayout());

        Container dialogPane = this.getContentPane();
        JPanel lastLine = this.initializeLastLine(escapeListener);

        var labelIcon = new JLabel(UiUtil.APP_MIDDLE.icon);
        labelIcon.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        dialogPane.add(labelIcon, BorderLayout.WEST);
        dialogPane.add(lastLine, BorderLayout.SOUTH);

        final JEditorPane text = this.initializeEditorPane();  // Contact info, use HTML text
        dialogPane.add(new JScrollPane(text), BorderLayout.CENTER);

        this.initializeDialog();
    }

    private JPanel initializeLastLine(ActionListener escapeListener) {
        final var buttonWebpage = new JButton(I18nUtil.valueByKey("ABOUT_WEBPAGE"));
        I18nViewUtil.addComponentForKey("ABOUT_WEBPAGE", buttonWebpage);
        buttonWebpage.addActionListener(ev -> {
            try {
                Desktop.getDesktop().browse(new URI(MediatorHelper.model().getMediatorUtils().getPropertiesUtil().getProperties().getProperty("github.url")));
            } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Browsing to Url failed", e);
            }
        });

        this.buttonClose = new JButton(I18nUtil.valueByKey("ABOUT_CLOSE"));
        I18nViewUtil.addComponentForKey("ABOUT_CLOSE", this.buttonClose);
        this.buttonClose.addActionListener(escapeListener);

        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(UiUtil.BORDER_5PX);
        lastLine.add(buttonWebpage);
        lastLine.add(Box.createGlue());
        lastLine.add(this.buttonClose);
        return lastLine;
    }

    private JEditorPane initializeEditorPane() {
        var editorPane = new JEditorPane();
        
        // Fix #82540: NoClassDefFoundError on setText()
        try (
            InputStream inputStream = DialogAbout.class.getClassLoader().getResourceAsStream("swing/about.htm");
            var inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
            var reader = new BufferedReader(inputStreamReader)
        ) {
            editorPane.setContentType("text/html");
            var result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            editorPane.setText(result.toString().replace(
                "%JSQLVERSION%",
                MediatorHelper.model().getPropertiesUtil().getVersionJsql()
            ));
        } catch (NoClassDefFoundError | IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
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
            public void focusGained(FocusEvent focusEvent) {
                editorPane.getCaret().setVisible(true);
                editorPane.getCaret().setSelectionVisible(true);
            }
        });

        editorPane.setDragEnabled(true);
        editorPane.setEditable(false);
        editorPane.getCaret().setBlinkRate(0);
        editorPane.setCaretPosition(0);
        editorPane.setComponentPopupMenu(new JPopupMenuText(editorPane));
        editorPane.addHyperlinkListener(linkEvent -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(linkEvent.getEventType())) {
                try {
                    Desktop.getDesktop().browse(linkEvent.getURL().toURI());
                } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Browsing to Url failed", e);
                }
            }
        });
        
        return editorPane;
    }

    /**
     * Set back default setting for About frame.
     */
    public final void initializeDialog() {
        this.setSize(533, 400);
        this.setLocationRelativeTo(MediatorHelper.frame());
        this.buttonClose.requestFocusInWindow();
        this.getRootPane().setDefaultButton(this.buttonClose);
    }

    public void requestButtonFocus() {
        this.buttonClose.requestFocusInWindow();
    }
}
