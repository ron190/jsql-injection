/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.UUID;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.scrollpane.LightScrollPaneShell;
import com.jsql.view.swing.shell.ShellSql;
import com.jsql.view.swing.shell.ShellWeb;
import com.jsql.view.swing.tab.dnd.DnDTabbedPane;
import com.jsql.view.swing.tab.dnd.TabTransferHandler;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiStringUtil;
import com.jsql.view.swing.util.UiUtil;

/**
 * TabbedPane containing result injection panels.
 */
@SuppressWarnings("serial")
public class TabResults extends DnDTabbedPane {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Create the panel containing injection results.
     */
    public TabResults() {
        
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.setTransferHandler(new TabTransferHandler());

        // Add hotkeys to rootpane ctrl-tab, ctrl-shift-tab, ctrl-w
        HotkeyUtil.addShortcut(this);
    }
    
    public void createFileTab(String name, String content, String path) {
        
        JTextArea fileText = new JPopupTextArea().getProxy();
        fileText.setText(content);
        fileText.setFont(new Font(UiUtil.FONT_NAME_MONO_NON_ASIAN, Font.PLAIN, 14));
        var scroller = new LightScrollPane(1, 0, 0, 0, fileText);
        
        fileText.setCaretPosition(0);
        this.addTab(name + StringUtils.SPACE, scroller);

        // Focus on the new tab
        this.setSelectedComponent(scroller);

        // Create a custom tab header with close button
        var header = new TabHeader(name, UiUtil.ICON_FILE_SERVER);

        this.setToolTipTextAt(this.indexOfComponent(scroller), path);

        // Apply the custom header to the tab
        this.setTabComponentAt(this.indexOfComponent(scroller), header);
        
        MediatorHelper.tabManagers().createFileTab(path, name);
    }
    
    public void createShell(String url, String path) {
        
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ShellWeb(terminalID, url);
            MediatorHelper.frame().getConsoles().put(terminalID, terminal);
            
            LightScrollPane scroller = new LightScrollPaneShell(terminal);
    
            this.addTab("Web shell ", scroller);
    
            // Focus on the new tab
            this.setSelectedComponent(scroller);
    
            // Create a custom tab header with close button
            var header = new TabHeader("Web shell", UiUtil.ICON_SHELL_SERVER);
    
            this.setToolTipTextAt(
                this.indexOfComponent(scroller),
                String.format(
                    "<html><b>URL</b><br>%s%s<br><b>Path</b><br>%s%s</html>",
                    url,
                    MediatorHelper.model().getResourceAccess().filenameWebshell,
                    path,
                    MediatorHelper.model().getResourceAccess().filenameWebshell
                )
            );
    
            // Apply the custom header to the tab
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
    
            terminal.requestFocusInWindow();
            
        } catch (MalformedURLException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Incorrect shell Url", e);
        }
    }
    
    public void createSQLShellTab(String url, String user, String pass, String path) {
        
        try {
            var terminalID = UUID.randomUUID();
            
            var terminal = new ShellSql(terminalID, url, user, pass);
            
            MediatorHelper.frame().getConsoles().put(terminalID, terminal);
    
            LightScrollPane scroller = new LightScrollPaneShell(terminal);
            
            this.addTab("SQL shell ", scroller);
    
            // Focus on the new tab
            this.setSelectedComponent(scroller);
    
            // Create a custom tab header with close button
            var header = new TabHeader("SQL shell", UiUtil.ICON_SHELL_SERVER);
    
            this.setToolTipTextAt(
                this.indexOfComponent(scroller),
                String.format(
                    "<html><b>URL</b><br>%s%s<br><b>Path</b><br>%s%s</html>",
                    url,
                    MediatorHelper.model().getResourceAccess().filenameSqlshell,
                    path,
                    MediatorHelper.model().getResourceAccess().filenameSqlshell
                )
            );
    
            // Apply the custom header to the tab
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
    
            terminal.requestFocusInWindow();
            
        } catch (MalformedURLException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Incorrect shell Url", e);
        }
    }
    
    public void createValuesTab(String[][] data, String[] columnNames, AbstractElementDatabase table) {
        
        // Create a new table to display the values
        var newTableJPanel = new PanelTable(data, columnNames);
        
        // Create a new tab: add header and table
        this.addTab(StringUtil.detectUtf8(table.toString()), newTableJPanel);
        newTableJPanel.setComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));
        
        // Focus on the new tab
        this.setSelectedComponent(newTableJPanel);
        
        // Create a custom tab header with close button
        var header = new TabHeader(UiStringUtil.detectUtf8Html(table.toString()));
        
        this.setToolTipTextAt(
            this.indexOfComponent(newTableJPanel),
            String.format(
                "<html><b>%s.%s</b><br><i>%s</i></html>",
                table.getParent(),
                table,
                String.join("<br>", Arrays.copyOfRange(columnNames, 2, columnNames.length))
            )
        );
        
        // Apply the custom header to the tab
        this.setTabComponentAt(
            this.indexOfComponent(newTableJPanel),
            header
        );
    }
}
