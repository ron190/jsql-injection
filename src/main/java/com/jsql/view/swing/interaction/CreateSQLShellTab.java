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
package com.jsql.view.swing.interaction;

import java.awt.Color;
import java.net.MalformedURLException;
import java.util.UUID;

import javax.swing.BorderFactory;

import org.apache.log4j.Logger;

import com.jsql.model.accessible.RessourceAccess;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.shell.ShellSql;
import com.jsql.view.swing.tab.TabHeader;

/**
 * Create a new tab for the terminal.
 */
public class CreateSQLShellTab extends CreateTab implements InteractionCommand {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Full path of the shell file on remote host.
     */
    private String path;

    // Url of the shell webpage on remote host
    private String url;
    
    private String user;
    
    private String pass;

    /**
     * @param interactionParams The local path and url for the shell
     */
    public CreateSQLShellTab(Object[] interactionParams) {
        this.path = (String) interactionParams[0];
        this.url = (String) interactionParams[1];
        this.user = (String) interactionParams[2];
        this.pass = (String) interactionParams[3];
    }

    @Override
    public void execute() {
        try {
            UUID terminalID = UUID.randomUUID();
            ShellSql terminal;
            terminal = new ShellSql(terminalID, this.url, this.user, this.pass);
            MediatorGui.frame().getConsoles().put(terminalID, terminal);
    
            LightScrollPane scroller = new LightScrollPane(terminal);
            scroller.colorThumb = HelperUi.COLOR_SELECTION_BACKGROUND;
            scroller.scrollBarAlphaRollover = 175;
            scroller.scrollBarAlpha = 100;
            
            scroller.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
            
            MediatorGui.tabResults().addTab("SQL shell ", scroller);
    
            // Focus on the new tab
            MediatorGui.tabResults().setSelectedComponent(scroller);
    
            // Create a custom tab header with close button
            TabHeader header = new TabHeader("SQL shell ", HelperUi.ICON_SHELL_SERVER);
    
            MediatorGui.tabResults().setToolTipTextAt(
                MediatorGui.tabResults().indexOfComponent(scroller),
                "<html><b>URL</b><br>" + this.url + RessourceAccess.FILENAME_SQLSHELL
                + "<br><b>Path</b><br>" + this.path + RessourceAccess.FILENAME_SQLSHELL + "</html>"
            );
    
            // Apply the custom header to the tab
            MediatorGui.tabResults().setTabComponentAt(
                MediatorGui.tabResults().indexOfComponent(scroller), 
                header
            );
    
            terminal.requestFocusInWindow();
        } catch (MalformedURLException e) {
            LOGGER.warn("Incorrect shell Url", e);
        }
    }
    
}
