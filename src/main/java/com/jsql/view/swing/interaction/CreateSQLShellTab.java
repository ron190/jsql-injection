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
package com.jsql.view.swing.interaction;

import java.awt.Color;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import com.jsql.model.accessible.RessourceAccess;
import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.shell.ShellSql;
import com.jsql.view.swing.tab.TabHeader;

/**
 * Create a new tab for the terminal.
 */
public class CreateSQLShellTab implements InteractionCommand {
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
        path = (String) interactionParams[0];
        url = (String) interactionParams[1];
        user = (String) interactionParams[2];
        pass = (String) interactionParams[3];
    }

    @Override
    public void execute() {
        UUID terminalID = UUID.randomUUID();
        ShellSql terminal = new ShellSql(terminalID, url, user, pass);
        MediatorGui.frame().getConsoles().put(terminalID, terminal);

        LightScrollPane scroller = new LightScrollPane(terminal);
        scroller.THUMB_COLOR = HelperGui.SELECTION_BACKGROUND;
        scroller.SCROLL_BAR_ALPHA_ROLLOVER = 175;
        scroller.SCROLL_BAR_ALPHA = 100;
        
        scroller.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
        
        MediatorGui.tabResults().addTab("SQL shell ", scroller);

        // Focus on the new tab
        MediatorGui.tabResults().setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(new ImageIcon(CreateSQLShellTab.class.getResource("/com/jsql/view/swing/resources/images/shell.png")));

        MediatorGui.tabResults().setToolTipTextAt(
            MediatorGui.tabResults().indexOfComponent(scroller),
            "<html><b>URL</b><br>" + url + RessourceAccess.SQLSHELL_FILENAME
            + "<br><b>Path</b><br>" + path + RessourceAccess.SQLSHELL_FILENAME + "</html>"
        );

        // Apply the custom header to the tab
        MediatorGui.tabResults().setTabComponentAt(
            MediatorGui.tabResults().indexOfComponent(scroller), 
            header
        );

        terminal.requestFocusInWindow();
    }
}
