/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.list;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.util.I18nViewUtil;

/**
 * Action to add a new item to a JList.
 */
public class MenuActionNewValue implements ActionListener {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * List to add new items.
     */
    private DnDList myList;
    
    /**
     * Create action to add new item list.
     * @param myList List to add new items.
     */
    public MenuActionNewValue(DnDList myList) {
        this.myList = myList;
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        
        var panel = new JPanel(new BorderLayout());
        final JTextArea textarea = new JPopupTextArea(new JTextArea()).getProxy();
        var labelAddValue = new JLabel(I18nUtil.valueByKey("LIST_ADD_VALUE_LABEL") + ":");
        panel.add(labelAddValue, BorderLayout.NORTH);
        I18nViewUtil.addComponentForKey("LIST_ADD_VALUE_LABEL", labelAddValue);
        panel.add(new LightScrollPane(1, 1, 1, 1, textarea));
        
        panel.setPreferredSize(new Dimension(600, 400));
        panel.setMinimumSize(new Dimension(600, 400));
        
        textarea.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                
                super.mousePressed(e);
                textarea.requestFocusInWindow();
            }
        });

        int result = -1;
               
        // Unhandled NullPointerException #92858 on showOptionDialog()
        // Unhandled IllegalArgumentException #92859 on showOptionDialog()
        // Fix #70832: ClassCastException on showOptionDialog()
        try {
            result = JOptionPane.showOptionDialog(
                this.myList.getTopLevelAncestor(),
                panel,
                I18nUtil.valueByKey("LIST_ADD_VALUE_TITLE"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] {
                    I18nUtil.valueByKey("LIST_ADD_VALUE_OK"),
                    I18nUtil.valueByKey("LIST_ADD_VALUE_CANCEL")
                },
                I18nUtil.valueByKey("LIST_ADD_VALUE_CANCEL")
            );
            
        } catch (NullPointerException | IllegalArgumentException | ClassCastException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        if (StringUtils.isEmpty(textarea.getText()) || result != JOptionPane.YES_OPTION) {
            return;
        }
            
        var lastIndex = 0;
        if (this.myList.getSelectedIndex() > 0) {
            
            lastIndex = this.myList.getSelectedIndex();
        }

        int firstIndex = lastIndex;
        
        if ("scan".equals(this.myList.getName())) {
            
            lastIndex = this.addToScanList(textarea, lastIndex);
            
        } else {
            
            lastIndex = this.addToList(textarea, lastIndex);
        }

        this.myList.setSelectionInterval(firstIndex, lastIndex - 1);
        this.myList.scrollRectToVisible(
            this.myList.getCellBounds(
                this.myList.getMinSelectionIndex(),
                this.myList.getMaxSelectionIndex()
            )
        );

        textarea.setText(null);
    }

    private int addToList(final JTextArea textarea, int index) {
        
        int lastIndex = index;
        
        for (String newItem: textarea.getText().split("\\n")) {
            
            if (StringUtils.isNotEmpty(newItem)) {
                
                ((DefaultListModel<ItemList>) this.myList.getModel()).add(
                    lastIndex++,
                    new ItemList(newItem.replace("\\", "/"))
                );
            }
        }
        
        return lastIndex;
    }

    private int addToScanList(final JTextArea textarea, int index) {
        
        int lastIndex = index;
        
        List<ItemListScan> listParsedItems = ListTransfertHandlerScan.parse(textarea.getText().replace("\\", "/"));
        
        for (ItemListScan item: listParsedItems) {
            
            ((DefaultListModel<ItemList>) this.myList.getModel()).add(lastIndex++, item);
        }
        
        return lastIndex;
    }
}
