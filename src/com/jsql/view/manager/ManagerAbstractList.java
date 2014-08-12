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
package com.jsql.view.manager;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jsql.view.ToolsGUI;
import com.jsql.view.list.dnd.DnDList;
import com.jsql.view.list.dnd.ListItem;

/**
 * Abstract manager containing a drag and drop list of item.
 */
@SuppressWarnings("serial")
abstract class ManagerAbstractList extends JPanel {
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

    /**
     * A animated GIF displayed during processing.
     */
    protected JLabel loader = new JLabel(ToolsGUI.LOADER_GIF);

    /**
     * Add a new string to the list if it's not a duplicate.
     * @param element The string to add to the list
     */
    public void addToList(String element) {
        boolean found = false;
        for (int i = 0; i < ((DefaultListModel<ListItem>) listPaths.getModel()).size(); i++) {
            if (((DefaultListModel<ListItem>) listPaths.getModel()).get(i).toString().equals(element)) {
                found = true;
            }
        }
        if (!found) {
            ListItem v = new ListItem(element);
            ((DefaultListModel<ListItem>) listPaths.getModel()).addElement(v);
        }
    }

    /**
     * Hide the loader icon.
     */
    public void hideLoader() {
        loader.setVisible(false);
    }

    /**
     * Unselect every element of the list.
     */
    public void clearSelection() {
        listPaths.clearSelection();
    }

    /**
     * Enable or disable the button.
     * @param isEnable The new state of the button
     */
    public void setButtonEnable(boolean isEnable) {
        run.setEnabled(isEnable);
    }

    /**
     * Display another icon to the Privilege label.
     * @param icon The new icon
     */
    public void changePrivilegeIcon(Icon icon) {
        privilege.setIcon(icon);
    }

    /**
     * Restore the default text to the button after a search.
     */
    public void restoreButtonText() {
        run.setText(defaultText);
    }
    
    /**
     * Set text of the button.
     * @param defaultText The text of the button
     */
    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }
}
