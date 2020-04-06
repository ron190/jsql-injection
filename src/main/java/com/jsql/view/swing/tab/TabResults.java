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
import javax.swing.TransferHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.util.I18nUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.scrollpane.LightScrollPaneShell;
import com.jsql.view.swing.shell.ShellSql;
import com.jsql.view.swing.shell.ShellWeb;
import com.jsql.view.swing.tab.dnd.DnDTabbedPane;
import com.jsql.view.swing.tab.dnd.TabTransferHandler;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;
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
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Create the panel containing injection results.
     */
    public TabResults() {
        
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        TransferHandler handler = new TabTransferHandler();
        
        this.setTransferHandler(handler);

        // Add hotkeys to rootpane ctrl-tab, ctrl-shift-tab, ctrl-w
        HotkeyUtil.addShortcut(this);
    }
    
    public void createFileTab(String name, String content, String path) {
        
        JTextArea fileText = new JPopupTextArea().getProxy();
        fileText.setText(content);
        fileText.setFont(new Font(UiUtil.FONT_NAME_UBUNTU_MONO, Font.PLAIN, 14));
        LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, fileText);
        
        fileText.setCaretPosition(0);
        this.addTab(name + StringUtils.SPACE, scroller);

        // Focus on the new tab
        this.setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(name, UiUtil.ICON_FILE_SERVER);

        this.setToolTipTextAt(this.indexOfComponent(scroller), path);

        // Apply the custom header to the tab
        this.setTabComponentAt(this.indexOfComponent(scroller), header);
        
        MediatorGui.tabManagers().createFileTab(path, name);
    }
    
    public void createShell(String url, String path) {
        
        try {
            UUID terminalID = UUID.randomUUID();
            ShellWeb terminal = new ShellWeb(terminalID, url);
            MediatorGui.frame().getConsoles().put(terminalID, terminal);
            
            LightScrollPane scroller = new LightScrollPaneShell(terminal);
    
            this.addTab("Web shell ", scroller);
    
            // Focus on the new tab
            this.setSelectedComponent(scroller);
    
            // Create a custom tab header with close button
            TabHeader header = new TabHeader("Web shell", UiUtil.ICON_SHELL_SERVER);
    
            this.setToolTipTextAt(
                this.indexOfComponent(scroller),
                "<html><b>URL</b><br>" + url + MediatorGui.model().getResourceAccess().filenameWebshell
                + "<br><b>Path</b><br>" + path + MediatorGui.model().getResourceAccess().filenameWebshell + "</html>"
            );
    
            // Apply the custom header to the tab
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
    
            terminal.requestFocusInWindow();
            
        } catch (MalformedURLException e) {
            LOGGER.warn("Incorrect shell Url", e);
        }
    }
    
    public void createSQLShellTab(String url, String user, String pass, String path) {
        
        try {
            UUID terminalID = UUID.randomUUID();
            
            ShellSql terminal = new ShellSql(terminalID, url, user, pass);
            
            MediatorGui.frame().getConsoles().put(terminalID, terminal);
    
            LightScrollPane scroller = new LightScrollPaneShell(terminal);
            
            this.addTab("SQL shell ", scroller);
    
            // Focus on the new tab
            this.setSelectedComponent(scroller);
    
            // Create a custom tab header with close button
            TabHeader header = new TabHeader("SQL shell", UiUtil.ICON_SHELL_SERVER);
    
            this.setToolTipTextAt(
                this.indexOfComponent(scroller),
                "<html><b>URL</b><br>" + url + MediatorGui.model().getResourceAccess().filenameSqlshell
                + "<br><b>Path</b><br>" + path + MediatorGui.model().getResourceAccess().filenameSqlshell + "</html>"
            );
    
            // Apply the custom header to the tab
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
    
            terminal.requestFocusInWindow();
            
        } catch (MalformedURLException e) {
            LOGGER.warn("Incorrect shell Url", e);
        }
    }
    
    public void createValuesTab(String[][] data, String[] columnNames, AbstractElementDatabase table) {
        
        // Create a new table to display the values
        PanelTable newTableJPanel = new PanelTable(data, columnNames);
        
        // Create a new tab: add header and table
        this.addTab(StringUtil.detectUtf8(table.toString()), newTableJPanel);
        newTableJPanel.setComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));
        
        // Focus on the new tab
        this.setSelectedComponent(newTableJPanel);
        
        // Create a custom tab header with close button
        TabHeader header = new TabHeader(UiStringUtil.detectUtf8Html(table.toString()));
        
        this.setToolTipTextAt(
            this.indexOfComponent(newTableJPanel),
            "<html>"
            + "<b>"+ table.getParent() +"."+ table +"</b><br>"
            + "<i>"+ String.join("<br>", Arrays.copyOfRange(columnNames, 2, columnNames.length)) +"</i>"
            + "</html>"
        );
        
        // Apply the custom header to the tab
        this.setTabComponentAt(
            this.indexOfComponent(newTableJPanel),
            header
        );
    }
}
