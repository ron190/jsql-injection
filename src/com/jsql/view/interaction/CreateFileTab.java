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

import javax.swing.ImageIcon;

import com.jsql.view.GUI;
import com.jsql.view.RoundScroller;
import com.jsql.view.component.TabHeader;
import com.jsql.view.component.popup.JPopupTextArea;

/**
 * Create a new tab for the file
 */
public class CreateFileTab implements Interaction{
    // The main View
    private GUI gui;

    // Name of the file
    private String name;

    // Content of the file
    private String content;

    // Full path of the file
    private String path;

    /**
     * @param mainGUI
     * @param interactionParams Name, content and path of the file
     */
    public CreateFileTab(GUI mainGUI, Object[] interactionParams){
        gui = mainGUI;

        name = (String) interactionParams[0];
        content = (String) interactionParams[1];
        path = (String) interactionParams[2];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        JPopupTextArea fileText = new JPopupTextArea();
        fileText.setText(content);
        RoundScroller scroller = new RoundScroller(fileText);

        fileText.setCaretPosition(0);
        gui.right.addTab(name+" ", scroller);

        // Focus on the new tab
        gui.right.setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(gui.right, new ImageIcon(getClass().getResource("/com/jsql/view/images/file.png")));

        gui.right.setToolTipTextAt(gui.right.indexOfComponent(scroller), path);

        // Apply the custom header to the tab
        gui.right.setTabComponentAt(gui.right.indexOfComponent(scroller), header);

        // Add the path String to the list of files only if there is no same StringObject value already
        gui.getOutputPanel().shellManager.addToList(path.replace(name, ""));
        gui.getOutputPanel().uploadManager.addToList(path.replace(name, ""));
        gui.getOutputPanel().sqlShellManager.addToList(path.replace(name, ""));
    }
}
