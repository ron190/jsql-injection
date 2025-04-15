package com.jsql.view.swing.panel.consoles;

import com.jsql.model.injection.strategy.blind.callable.AbstractCallableBit;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CenterRendererWithIcon extends CenterRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Object text = ((List<?>) value).get(0);
        Object objectCallableBoolean = ((List<?>) value).get(1);
        
        if (objectCallableBoolean != null) {
            AbstractCallableBit<?> callableBoolean = (AbstractCallableBit<?>) objectCallableBoolean;
            String charText = callableBoolean.getCharText();
            if (charText != null && charText.charAt(0) >= 32) {
                text += ":" + callableBoolean.getCharText();
            }
        }

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        label.setIcon(null);  // required to remove icon as same renderer applies to subsequent labels

        if (column == 3 && objectCallableBoolean != null) {
            AbstractCallableBit<?> callableBoolean = (AbstractCallableBit<?>) objectCallableBoolean;
            if (!callableBoolean.isMultibit()) {
                label.setIcon(callableBoolean.isTrue() ? UiUtil.TICK_GREEN.getIcon() : UiUtil.CROSS_RED.getIcon());
            }
        }
        return label;
    }
}