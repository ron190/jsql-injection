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

import com.jsql.view.GUIMediator;
import com.jsql.view.component.RoundScroller;
import com.jsql.view.component.TabHeader;
import com.jsql.view.component.popupmenu.JPopupTextArea;

/**
 * Create a new tab for the file
 */
public class CreateFileTab implements InteractionCommand{
    // Name of the file
    private String name;

    // Content of the file
    private String content;

    // Full path of the file
    private String path;

    /**
     * @param interactionParams Name, content and path of the file
     */
    public CreateFileTab(Object[] interactionParams){
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
        GUIMediator.right().addTab(name+" ", scroller);

        // Focus on the new tab
        GUIMediator.right().setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(new ImageIcon(getClass().getResource("/com/jsql/view/images/file.png")));

        GUIMediator.right().setToolTipTextAt(GUIMediator.right().indexOfComponent(scroller), path);

        // Apply the custom header to the tab
        GUIMediator.right().setTabComponentAt(GUIMediator.right().indexOfComponent(scroller), header);

        // Add the path String to the list of files only if there is no same StringObject value already
        GUIMediator.gui().getOutputPanel().shellManager.addToList(path.replace(name, ""));
        GUIMediator.gui().getOutputPanel().uploadManager.addToList(path.replace(name, ""));
        GUIMediator.gui().getOutputPanel().sqlShellManager.addToList(path.replace(name, ""));
    }
}
