package com.jsql.view.swing.manager.util;

import com.jsql.view.swing.util.I18nViewUtil;

import javax.swing.*;
import java.awt.*;

public class ComboBoxTypeRenderer extends JLabel implements ListCellRenderer<ModelItemType> {
    public Component getListCellRendererComponent(
        JList<? extends ModelItemType> list, ModelItemType value, int index, boolean isSelected, boolean cellHasFocus
    ) {
        this.setToolTipText(I18nViewUtil.valueByKey(value.getKeyTooltip()));
        this.setText(I18nViewUtil.valueByKey(value.getKeyLabel()));
        this.setForeground(UIManager.getColor("ComboBox.foreground"));
        this.setBackground(UIManager.getColor("ComboBox.background"));
        if (isSelected) {
            this.setForeground(UIManager.getColor("ComboBox.selectionForeground"));
            this.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
        }
        return this;
    }
}