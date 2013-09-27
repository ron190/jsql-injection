/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import com.jsql.view.GUI;
import com.jsql.view.component.TabHeader;
import com.jsql.view.terminal.SQLTerminal;
import com.jsql.view.terminal.Terminal;

/**
 * Create a new tab for the terminal
 */
public class CreateSQLShellTab implements Interaction{
    // The main View
    private GUI gui;

    // Full path of the shell file on remote host
    private String path;

    // Url of the shell webpage on remote host
    private String url;
    private String user;
    private String pass;

    /**
     * @param mainGUI
     * @param interactionParams The local path and url for the shell
     */
    public CreateSQLShellTab(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;

        path = (String) interactionParams[0];
        url = (String) interactionParams[1];
        user = (String) interactionParams[2];
        pass = (String) interactionParams[3];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        UUID terminalID = UUID.randomUUID();
        SQLTerminal terminal = new SQLTerminal(gui.model, terminalID, url, user, pass);
        gui.consoles.put(terminalID, terminal);

        JScrollPane scroller = new JScrollPane(terminal);
        gui.right.addTab("SQL shell ", scroller);

        // Focus on the new tab
        gui.right.setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(gui.right, new ImageIcon(getClass().getResource("/com/jsql/view/images/shell.png")));

        gui.right.setToolTipTextAt(gui.right.indexOfComponent(scroller),
                "<html><b>URL</b><br>"+url+gui.model.rao.SQLSHELL_FILENAME+"<br><b>Path</b><br>"+path+gui.model.rao.SQLSHELL_FILENAME+"</html>");

        // Apply the custom header to the tab
        gui.right.setTabComponentAt(gui.right.indexOfComponent(scroller), header);

        terminal.requestFocusInWindow();
    }
}
