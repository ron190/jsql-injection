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

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.jsql.i18n.I18n;
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
        I18n.addComponentForKey("SELECT_ALL", labelAddValue);
        panel.add(new LightScrollPane(1, 1, 1, 1, textarea));
        
        panel.setPreferredSize(new Dimension(300, 200));
        panel.setMinimumSize(new Dimension(300, 200));
        
        textarea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                textarea.requestFocusInWindow();
            }
        });

        int result = JOptionPane.showOptionDialog(
            myList.getTopLevelAncestor(),
            panel,
            I18n.valueByKey("LIST_ADD_VALUE"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{I18n.valueByKey("OK"), I18n.valueByKey("CANCEL")},
            I18n.valueByKey("CANCEL")
        );

        if (!"".equals(textarea.getText()) && result == JOptionPane.YES_OPTION) {
            int lastIndex = 0;
            if (myList.getSelectedIndex() > 0) {
                lastIndex = myList.getSelectedIndex();
            }

            int firstIndex = lastIndex;
            for (String newItem: textarea.getText().split("\\n")) {
                if (!"".equals(newItem)) {
                    ((DefaultListModel<ListItem>) myList.getModel()).add(
                        lastIndex++, 
                        new ListItem(newItem.replace("\\", "/")
                    ));
                }
            }

            myList.setSelectionInterval(firstIndex, lastIndex - 1);
            myList.scrollRectToVisible(
                myList.getCellBounds(
                    myList.getMinSelectionIndex(),
                    myList.getMaxSelectionIndex()
                )
            );

            textarea.setText(null);
        }
    }
}
