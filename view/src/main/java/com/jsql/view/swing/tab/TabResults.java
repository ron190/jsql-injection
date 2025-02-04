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

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.terminal.*;
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

    public static final String TAB_EXPLOIT_FAILURE_INCORRECT_URL = "Tab exploit failure: incorrect URL";

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

    public void addFileTab(String label, String content, String path) {
        JTextArea fileText = new JPopupTextArea().getProxy();
        fileText.setText(content);
        fileText.setFont(new Font(UiUtil.FONT_NAME_MONO_NON_ASIAN, Font.PLAIN, 14));
        fileText.setCaretPosition(0);
        this.addTextTab(label, path, fileText, UiUtil.DOWNLOAD.getIcon());
        MediatorHelper.tabManagersCards().addToLists(path, label);
    }

    public void addReportTab(String content) {
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
            public void focusGained(FocusEvent focusEvent) {
                editorPane.getCaret().setVisible(true);
                editorPane.getCaret().setSelectionVisible(true);
                editorPane.getCaret().setBlinkRate(0);
            }
        });
        UiUtil.init(editorPane);  // silent delete

        this.addTextTab("Vulnerability report", "Analysis report with all payloads detected", editorPane, UiUtil.APP_ICON.getIcon());
    }

    public void addTextTab(String label, String toolTipText, JComponent componentText, FlatSVGIcon icon) {
        var scroller = new JScrollPane(componentText);
        this.addTab(label + StringUtils.SPACE, scroller);
        this.setSelectedComponent(scroller);  // Focus on the new tab
        this.setToolTipTextAt(this.indexOfComponent(scroller), toolTipText);
        var header = new TabHeader(label, icon);
        this.setTabComponentAt(this.indexOfComponent(scroller), header);

        this.updateUI();  // required: light, open/close prefs, dark => light artifacts
    }

    public void addTabExploitWeb(String url) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ExploitWeb(terminalID, url);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("Web shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab

            var header = new TabHeader("Web shell", UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }

    public void addTabExploitUdfMysql() {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ExploitUdfMysql(terminalID);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("UDF shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab

            var header = new TabHeader("UDF shell", UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }

    public void addTabExploitRceOracle() {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ExploitRceOracle(terminalID);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("RCE shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab

            var header = new TabHeader("RCE shell", UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }

    public void addTabExploitRcePostgres() {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ExploitRcePostgres(terminalID);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("RCE shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab

            var header = new TabHeader("RCE shell", UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }

    public void addTabExploitSql(String url, String user, String pass) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ExploitSql(terminalID, url, user, pass);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("SQL shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab

            var header = new TabHeader("SQL shell", UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }
    
    public void addTabValues(String[][] data, String[] columnNames, AbstractElementDatabase table) {
        var panelTable = new PanelTable(data, columnNames);
        
        this.addTab(StringUtil.detectUtf8(table.toString()), panelTable);
        panelTable.setComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()));
        
        this.setSelectedComponent(panelTable);  // Focus on the new tab

        var header = new TabHeader(UiStringUtil.detectUtf8Html(table.toString()), UiUtil.TABLE_BOLD.getIcon());
        this.setTabComponentAt(this.indexOfComponent(panelTable), header);

        this.updateUI();  // required: light, open/close prefs, dark => light artifacts
    }
}
