package com.jsql.view.swing.manager.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SeparatorListener implements ActionListener {
    private final JComboBox<Object> comboBox;
    private Object currentItem;

    public SeparatorListener(JComboBox<Object> comboBox) {
        this.comboBox = comboBox;
        comboBox.setSelectedIndex(0);
        this.currentItem = comboBox.getSelectedItem();
    }

    public void actionPerformed(ActionEvent e) {
        if (this.comboBox.getSelectedItem() == ComboBoxMethodRenderer.SEPARATOR) {
            this.comboBox.setSelectedItem(this.currentItem);
        } else {
            this.currentItem = this.comboBox.getSelectedItem();
        }
    }
}