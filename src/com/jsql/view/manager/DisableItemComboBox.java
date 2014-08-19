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
package com.jsql.view.manager;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class DisableItemComboBox<E> extends JComboBox<E> {
    private final Set<Integer> disableIndexSet = new HashSet<Integer>();
    
    private boolean isDisableIndex;
    
    private final Action up = new AbstractAction() {
        @Override public void actionPerformed(ActionEvent e) {
            int si = getSelectedIndex();
            for (int i = si - 1; i >= 0; i--) {
                if (!disableIndexSet.contains(i)) {
                    setSelectedIndex(i);
                    break;
                }
            }
        }
    };
    
    private final Action down = new AbstractAction() {
        @Override public void actionPerformed(ActionEvent e) {
            int si = getSelectedIndex();
            for (int i = si + 1; i < getModel().getSize(); i++) {
                if (!disableIndexSet.contains(i)) {
                    setSelectedIndex(i);
                    break;
                }
            }
        }
    };
    
    public DisableItemComboBox() {
        super();
    }
    
    public DisableItemComboBox(ComboBoxModel<E> aModel) {
        super(aModel);
    }
    
    public DisableItemComboBox(E[] items) {
        super(items);
    }
    
    @Override public void updateUI() {
        super.updateUI();
        setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c;
                if (disableIndexSet.contains(index)) {
                    c = super.getListCellRendererComponent(list, value, index, false, false);
                    c.setEnabled(false);
                    c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                    c.setFont(c.getFont().deriveFont(Font.ITALIC));
                } else {
                    if ("----".equals(value.toString())) {
                        c = new JSeparator();
                    } else {
                        c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        c.setEnabled(true);
                    }
                }
                return c;
            }
        });
        ActionMap am = getActionMap();
        am.put("selectPrevious3", up);
        am.put("selectNext3", down);
        InputMap im = getInputMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),      "selectPrevious3");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0),   "selectPrevious3");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),    "selectNext3");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), "selectNext3");
    }
    
    public void setDisableIndex(Set<Integer> set) {
        disableIndexSet.clear();
        for (Integer i: set) {
            disableIndexSet.add(i);
        }
    }
    
    @Override public void setPopupVisible(boolean v) {
        if (!v && isDisableIndex) {
            isDisableIndex = false;
        } else {
            super.setPopupVisible(v);
        }
    }
    
    @Override public void setSelectedIndex(int index) {
        if ("----".equals(this.getItemAt(index).toString()) || disableIndexSet.contains(index)) {
            isDisableIndex = true;
        } else {
            super.setSelectedIndex(index);
        }
    }
}