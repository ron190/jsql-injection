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

import java.awt.Font;

import javax.swing.JTextArea;

import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.text.JPopupTextArea;

/**
 * Create a new tab for the file.
 */
public class CreateFileTab extends CreateTab implements InteractionCommand {
    
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
        this.name = (String) interactionParams[0];
        this.content = (String) interactionParams[1];
        this.path = (String) interactionParams[2];
    }

    @Override
    public void execute() {
        if (MediatorGui.tabResults() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.tabResults() in "+ this.getClass());
        }
        
        JTextArea fileText = new JPopupTextArea().getProxy();
        fileText.setText(this.content);
        fileText.setFont(new Font(HelperUi.FONT_NAME_UBUNTU_MONO, Font.PLAIN, 14));
        LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, fileText);
        
        fileText.setCaretPosition(0);
        MediatorGui.tabResults().addTab(this.name +" ", scroller);

        // Focus on the new tab
        MediatorGui.tabResults().setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(this.name, HelperUi.ICON_FILE_SERVER);

        MediatorGui.tabResults().setToolTipTextAt(MediatorGui.tabResults().indexOfComponent(scroller), this.path);

        // Apply the custom header to the tab
        MediatorGui.tabResults().setTabComponentAt(MediatorGui.tabResults().indexOfComponent(scroller), header);

        // Add the path String to the list of files only if there is no same StringObject value already
        MediatorGui.managerWebshell().addToList(this.path.replace(this.name, ""));
        MediatorGui.managerUpload().addToList(this.path.replace(this.name, ""));
        MediatorGui.managerSqlshell().addToList(this.path.replace(this.name, ""));
    }
    
}
