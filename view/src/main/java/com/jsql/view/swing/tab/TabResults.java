/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.shell.ShellSql;
import com.jsql.view.swing.shell.ShellWeb;
import com.jsql.view.swing.tab.dnd.DnDTabbedPane;
import com.jsql.view.swing.tab.dnd.TabTransferHandler;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiStringUtil;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.IntConsumer;

/**
 * TabbedPane containing result injection panels.
 */
public class TabResults extends DnDTabbedPane {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Create the panel containing injection results.
     */
    public TabResults() {
        this.setName("tabResults");
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.setTransferHandler(new TabTransferHandler());
        this.putClientProperty("JTabbedPane.tabClosable", true);
        this.putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) ActionCloseTabResult::perform);
        UIManager.put("TabbedPane.closeHoverForeground", LogLevelUtil.COLOR_RED);
        HotkeyUtil.addShortcut(this);  // Add hotkeys to root-pane ctrl-tab, ctrl-shift-tab, ctrl-w
        this.addMouseWheelListener(new TabbedPaneMouseWheelListener());
        MediatorHelper.register(this);
    }

    public void createFileTab(String label, String content, String path) {
        JTextArea fileText = new JPopupTextArea().getProxy();
        fileText.setText(content);
        fileText.setFont(new Font(UiUtil.FONT_NAME_MONO_NON_ASIAN, Font.PLAIN, 14));
        fileText.setCaretPosition(0);
        this.createTextTab(label, path, fileText, UiUtil.DOWNLOAD.icon);
        MediatorHelper.tabManagersCards().addToLists(path, label);
    }

    public void createReportTab(String content) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText("<html><span style=\"white-space: nowrap; font-family:'"+ UiUtil.FONT_NAME_MONO_NON_ASIAN +"'\">" + content + "</span></html>");
        editorPane.setFont(UIManager.getFont("TextArea.font"));  // required to increase text size
        editorPane.setDragEnabled(true);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);
        editorPane.getCaret().setBlinkRate(0);
        editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        editorPane.setComponentPopupMenu(new JPopupMenuText(editorPane));
        editorPane.addHyperlinkListener(linkEvent -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(linkEvent.getEventType())) {
                try {
                    Desktop.getDesktop().browse(linkEvent.getURL().toURI());
                } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Failing to browse Url", e);
                }
            }
        });
        editorPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                editorPane.getCaret().setVisible(true);
                editorPane.getCaret().setSelectionVisible(true);
                editorPane.getCaret().setBlinkRate(0);
            }
        });
        UiUtil.initialize(editorPane);  // silent delete

        this.createTextTab("Vulnerability report", "Analysis report with all payloads detected", editorPane, UiUtil.REPORT.icon);
    }

    public void createTextTab(String label, String toolTipText, JComponent componentText, FlatSVGIcon icon) {
        var scroller = new JScrollPane(componentText);
        this.addTab(label + StringUtils.SPACE, scroller);
        this.setSelectedComponent(scroller);  // Focus on the new tab
        this.setToolTipTextAt(this.indexOfComponent(scroller), toolTipText);
        var header = new TabHeader(label, icon);
        this.setTabComponentAt(this.indexOfComponent(scroller), header);

        FlatLaf.updateUI();  // required: light, open/close prefs, dark => light artifacts
        MediatorHelper.frame().revalidate();
        MediatorHelper.frame().repaint();
    }

    public void createShell(String url, String path) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ShellWeb(terminalID, url);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("Web shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab
            this.setToolTipTextAt(
                this.indexOfComponent(scroller),
                String.format(
                    "<html><b>URL</b><br>%s<br><b>Path</b><br>%s%s</html>",
                    url,
                    path,
                    MediatorHelper.model().getResourceAccess().filenameWebshell
                )
            );

            var header = new TabHeader("Web shell", UiUtil.TERMINAL.icon);
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            FlatLaf.updateUI();  // required: light, open/close prefs, dark => light artifacts
            MediatorHelper.frame().revalidate();
            MediatorHelper.frame().repaint();
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect shell Url", e);
        }
    }
    
    public void createSQLShellTab(String url, String user, String pass, String path) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ShellSql(terminalID, url, user, pass);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("SQL shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab
            this.setToolTipTextAt(
                this.indexOfComponent(scroller),
                String.format(
                    "<html><b>URL</b><br>%s<br><b>Path</b><br>%s%s</html>",
                    url,
                    path,
                    MediatorHelper.model().getResourceAccess().filenameSqlshell
                )
            );

            var header = new TabHeader("SQL shell", UiUtil.TERMINAL.icon);
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            FlatLaf.updateUI();  // required: light, open/close prefs, dark => light artifacts
            MediatorHelper.frame().revalidate();
            MediatorHelper.frame().repaint();
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect shell Url", e);
        }
    }
    
    public void createValuesTab(String[][] data, String[] columnNames, AbstractElementDatabase table) {
        var panelTable = new PanelTable(data, columnNames);
        
        this.addTab(StringUtil.detectUtf8(table.toString()), panelTable);
        panelTable.setComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));
        
        this.setSelectedComponent(panelTable);  // Focus on the new tab
        this.setToolTipTextAt(
            this.indexOfComponent(panelTable),
            String.format(
                "<html><b>%s.%s</b><br><i>%s</i></html>",
                table.getParent(),
                table,
                String.join("<br>", Arrays.copyOfRange(columnNames, 2, columnNames.length))
            )
        );

        var header = new TabHeader(UiStringUtil.detectUtf8Html(table.toString()), UiUtil.TABLE_BOLD.icon);
        this.setTabComponentAt(this.indexOfComponent(panelTable), header);

        FlatLaf.updateUI();  // required: light, open/close prefs, dark => light artifacts
        MediatorHelper.frame().revalidate();
        MediatorHelper.frame().repaint();
    }
}
