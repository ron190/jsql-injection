package com.jsql.view.swing.manager.util;

import com.jsql.model.accessible.ExploitMode;
import com.jsql.view.swing.util.I18nViewUtil;

import javax.swing.*;
import java.awt.*;

public class ComboBoxMethodRenderer extends JLabel implements ListCellRenderer<Object> {
    public static final JSeparator SEPARATOR = new JSeparator();

    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        if (ComboBoxMethodRenderer.SEPARATOR.equals(value)) {
            return ComboBoxMethodRenderer.SEPARATOR;
        }
        if (value instanceof ExploitMode exploitMethods) {
            this.setToolTipText(I18nViewUtil.valueByKey(exploitMethods.getKeyTooltip()));
            this.setText(I18nViewUtil.valueByKey(exploitMethods.getKeyLabel()));
        }
        this.setForeground(UIManager.getColor("ComboBox.foreground"));
        this.setBackground(UIManager.getColor("ComboBox.background"));
        if (isSelected) {
            this.setForeground(UIManager.getColor("ComboBox.selectionForeground"));
            this.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
        }
        this.setFont(list.getFont());
        return this;
    }
}