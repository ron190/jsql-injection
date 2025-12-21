package com.jsql.view.swing.manager.util;

import com.jsql.view.swing.util.I18nViewUtil;

import javax.swing.*;
import java.awt.*;

public class ComboBoxTypeRenderer extends JLabel implements ListCellRenderer<Object> {
    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        if (value == ComboBoxMethodRenderer.SEPARATOR) {
            return ComboBoxMethodRenderer.SEPARATOR;
        }
        if (value instanceof ModelItemType) {
            var exploitMethods = (ModelItemType) value;
            this.setToolTipText(I18nViewUtil.valueByKey(exploitMethods.getKeyTooltip()));
            this.setText(I18nViewUtil.valueByKey(exploitMethods.getKeyLabel()));
        }
        this.setForeground(UIManager.getColor("ComboBox.foreground"));
        this.setBackground(UIManager.getColor("ComboBox.background"));
        if (isSelected) {
            this.setForeground(UIManager.getColor("ComboBox.selectionForeground"));
            this.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
        }
        return this;
    }
}