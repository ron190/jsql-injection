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

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTextArea;

import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.text.JPopupTextArea;

/**
 * Create a new tab for the file.
 */
public class CreateFileTab implements InteractionCommand {
    /**
     * Name of the file.
     */
    private String name;

    /**
     * Content of the file.
     */
    private String content;

    /**
     * Full path of the file.
     */
    private String path;

    /**
     * @param interactionParams Name, content and path of the file
     */
    public CreateFileTab(Object[] interactionParams) {
        name = (String) interactionParams[0];
        content = (String) interactionParams[1];
        path = (String) interactionParams[2];
    }

    @Override
    public void execute() {
        JTextArea fileText = new JPopupTextArea().getProxy();
        fileText.setText(content);
        fileText.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
        LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, fileText);
        
        fileText.setCaretPosition(0);
        MediatorGUI.tabResults().addTab(name + " ", scroller);

        // Focus on the new tab
        MediatorGUI.tabResults().setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(new ImageIcon(CreateFileTab.class.getResource("/com/jsql/view/swing/resources/images/file.png")));

        MediatorGUI.tabResults().setToolTipTextAt(MediatorGUI.tabResults().indexOfComponent(scroller), path);

        // Apply the custom header to the tab
        MediatorGUI.tabResults().setTabComponentAt(MediatorGUI.tabResults().indexOfComponent(scroller), header);

        // Add the path String to the list of files only if there is no same StringObject value already
        MediatorGUI.tabManagers().shellManager.addToList(path.replace(name, ""));
        MediatorGUI.tabManagers().uploadManager.addToList(path.replace(name, ""));
        MediatorGUI.tabManagers().sqlShellManager.addToList(path.replace(name, ""));
    }
}
