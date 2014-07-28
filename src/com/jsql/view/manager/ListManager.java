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
package com.jsql.view.manager;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jsql.view.GUITools;
import com.jsql.view.list.dnd.DnDList;
import com.jsql.view.list.dnd.ListItem;

/**
 * Manager for uploading PHP webshell to the host
 */
@SuppressWarnings("serial")
public class ListManager extends JPanel{
    /**
     * Contains the paths of webshell.
     */
    protected DnDList listPaths;

    /**
     * Starts the upload process.
     */
    protected JButton run;

    /**
     * Display the FILE privilege of current user.
     */
    protected JLabel privilege;

    /**
     * Text of the button that start the upload process.
     * Used to get back the default text after a search (defaultText->"Stop"->defaultText).
     */
    protected String defaultText;  

	protected JLabel loader = new JLabel(GUITools.LOADER_GIF);

    /**
     * Add a new string to the list if it's not a duplicate.
     * @param element The string to add to the list
     */
    public void addToList(String element){
        boolean found = false;
        for (int i = 0 ; i < ((DefaultListModel<ListItem>)listPaths.getModel()).size() ; i++){
            if (((DefaultListModel<ListItem>)listPaths.getModel()).get(i).toString().equals(element)) {
                found = true;
            }
        }
        if(!found){
            ListItem v = new ListItem(element);
            ((DefaultListModel<ListItem>)listPaths.getModel()).addElement(v);
        }
    }

    /**
     * Hide the loader icon.
     */
    public void hideLoader(){
        loader.setVisible(false);
    }

    /**
     * Unselect every element of the list.
     */
    public void clearSelection(){
        listPaths.clearSelection();
    }

    /**
     * Enable or disable the button.
     * @param i The new state of the button
     */
    public void setButtonEnable(boolean a){
        run.setEnabled(a);
    }

    /**
     * Display another icon to the Privilege label.
     * @param i The new icon
     */
    public void changeIcon(Icon i){
        privilege.setIcon(i);
    }

    /**
     * Restore the default text to the button after a search.
     */
    public void restoreButtonText(){
        run.setText(defaultText);
    }  
    
    public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}
}
