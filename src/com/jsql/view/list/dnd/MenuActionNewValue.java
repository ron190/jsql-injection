/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.list.dnd;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.textcomponent.JPopupTextArea;

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
        super();
        this.myList = myList;
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        JPanel panel = new JPanel(new BorderLayout());
        final JTextArea textarea = new JPopupTextArea(new JTextArea(6, 50)).getProxy();
        panel.add(new JLabel("Add new value(s) to the list:"), BorderLayout.NORTH);
        panel.add(new JScrollPanePixelBorder(1, 1, 1, 1, textarea));
        
        textarea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                textarea.requestFocusInWindow();
            }
        });

        int result = JOptionPane.showOptionDialog(myList.getTopLevelAncestor(),
                panel,
                "Add Value(s)",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Ok", "Cancel"}, // this is the array
                "Cancel");

        if (!"".equals(textarea.getText()) && result == JOptionPane.YES_OPTION) {
            int lastIndex = 0;
            if (myList.getSelectedIndex() > 0) {
                lastIndex = myList.getSelectedIndex();
            }

            int firstIndex = lastIndex;
            for (String newItem: textarea.getText().split("\\n")) {
                if (!newItem.equals("")) {
                    ((DefaultListModel<ListItem>) myList.getModel()).add(lastIndex++, new ListItem(newItem.replace("\\", "/")));
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
