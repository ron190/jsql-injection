/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.UiUtil;

/**
 * Abstract manager containing a drag and drop list of item.
 */
@SuppressWarnings("serial")
public abstract class AbstractManagerList extends JPanel implements Manager {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    protected transient List<ItemList> itemsList = new ArrayList<>();
    
    protected DnDList listFile;
    
    protected JPanel lastLine = new JPanel();
    
    /**
     * Contains the paths of webshell.
     */
    protected DnDList listPaths;

    /**
     * Starts the upload process.
     */
    protected JButtonStateful run;

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
    protected JLabel loader = new JLabel(UiUtil.ICON_LOADER_GIF);
    
    protected AbstractManagerList() {
        // Nothing
    }
    
    protected AbstractManagerList(String nameFile) {
        
        this.setLayout(new BorderLayout());

        try (
            var inputStream = UiUtil.class.getClassLoader().getResourceAsStream(nameFile);
            var inputStreamReader = new InputStreamReader(inputStream);
            var reader = new BufferedReader(inputStreamReader);
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                
                this.itemsList.add(new ItemList(line));
            }
        } catch (IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        this.listFile = new DnDList(this.itemsList);

        this.listFile.setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        this.add(new LightScrollPane(0, 0, 0, 0, this.listFile), BorderLayout.CENTER);

        this.lastLine.setOpaque(false);
        this.lastLine.setLayout(new BoxLayout(this.lastLine, BoxLayout.X_AXIS));

        this.lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
    }

    /**
     * Add a new string to the list if it's not a duplicate.
     * @param element The string to add to the list
     */
    public void addToList(String element) {
        
        var isFound = false;
        DefaultListModel<ItemList> listModel = (DefaultListModel<ItemList>) this.listPaths.getModel();
        
        for (var i = 0 ; i < listModel.size() ; i++) {
            
            if (listModel.get(i).toString().equals(element)) {
                
                isFound = true;
            }
        }
        
        if (!isFound) {
            
            var itemList = new ItemList(element);
            listModel.addElement(itemList);
        }
    }
    
    public void addTag(String url, String tag) {
        
        ListModel<ItemList> listModel = this.listPaths.getModel();
        
        for (var i = 0 ; i < listModel.getSize() ; i++) {
            
            if (url.contains(listModel.getElementAt(i).getOriginalString())) {
                
                listModel.getElementAt(i).setIsDatabaseConfirmed(true);
                listModel.getElementAt(i).setInternalString(listModel.getElementAt(i).getInternalString() +" ["+tag+"]");
                
                ((DefaultListModel<ItemList>) listModel).setElementAt(listModel.getElementAt(i), i);
            }
        }
    }
    
    public void highlight(String url, String strategy) {
        
        var itemLabel = String
            .format(
                " [%s]",
                strategy
            );
        
        ListModel<ItemList> listModel = this.listPaths.getModel();
        
        for (var i = 0 ; i < listModel.getSize() ; i++) {
            
            if (url.contains(listModel.getElementAt(i).getOriginalString())) {
                
                listModel.getElementAt(i).setIsVulnerable(true);
                listModel.getElementAt(i).setInternalString(
                    listModel
                    .getElementAt(i)
                    .getInternalString()
                    .replace(itemLabel, StringUtils.EMPTY)
                    + itemLabel
                );
                
                ((DefaultListModel<ItemList>) listModel).setElementAt(listModel.getElementAt(i), i);
            }
        }
    }
    
    public void endProcess() {
        
        this.run.setText(I18nViewUtil.valueByKey(this.defaultText));
        this.setButtonEnable(true);
        this.loader.setVisible(false);
        this.run.setState(StateButton.STARTABLE);
    }

    /**
     * Unselect every element of the list.
     */
    public void clearSelection() {
        
        this.listPaths.clearSelection();
    }

    /**
     * Enable or disable the button.
     * @param isEnable The new state of the button
     */
    public void setButtonEnable(boolean isEnable) {
        
        this.run.setEnabled(isEnable);
    }

    /**
     * Display another icon to the Privilege label.
     * @param icon The new icon
     */
    public void changePrivilegeIcon(Icon icon) {
        
        this.privilege.setIcon(icon);
    }
    
    
    // Getter and setter

    public DnDList getListPaths() {
        return this.listPaths;
    }
}
