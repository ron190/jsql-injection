/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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

import com.jsql.i18n.I18n;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextArea;

/**
 * Action to add a new item to a JList.
 */
public class MenuActionNewValue implements ActionListener {
    
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
        JPanel panel = new JPanel(new BorderLayout());
        final JTextArea textarea = new JPopupTextArea(new JTextArea()).getProxy();
        JLabel labelAddValue = new JLabel(I18n.valueByKey("LIST_ADD_VALUE_LABEL") + ":");
        panel.add(labelAddValue, BorderLayout.NORTH);
        I18nView.addComponentForKey("LIST_ADD_VALUE_LABEL", labelAddValue);
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

        int result = JOptionPane.showOptionDialog(
            this.myList.getTopLevelAncestor(),
            panel,
            I18n.valueByKey("LIST_ADD_VALUE_TITLE"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{I18n.valueByKey("LIST_ADD_VALUE_OK"), I18n.valueByKey("LIST_ADD_VALUE_CANCEL")},
            I18n.valueByKey("LIST_ADD_VALUE_CANCEL")
        );

        if (!"".equals(textarea.getText()) && result == JOptionPane.YES_OPTION) {
            int lastIndex = 0;
            if (this.myList.getSelectedIndex() > 0) {
                lastIndex = this.myList.getSelectedIndex();
            }

            int firstIndex = lastIndex;
            
            if ("scan".equals(this.myList.getName())) {
                List<ItemListScan> listParsedItems = ListTransfertHandlerScan.parse(textarea.getText().replace("\\", "/"));
                for (ItemListScan item: listParsedItems) {
                    ((DefaultListModel<ItemList>) this.myList.getModel()).add(
                        lastIndex++,
                        item
                    );
                }
                
            } else {
                for (String newItem: textarea.getText().split("\\n")) {
                    if (!"".equals(newItem)) {
                        ((DefaultListModel<ItemList>) this.myList.getModel()).add(
                            lastIndex++,
                            new ItemList(newItem.replace("\\", "/"))
                        );
                    }
                }
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
    }
    
}
