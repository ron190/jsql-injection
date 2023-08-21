package com.jsql.view.swing.panel.consoles;

import com.jsql.model.injection.strategy.blind.AbstractCallableBoolean;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CenterRendererWithColor extends CenterRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        Object text = ((List<?>) value).get(0);
        Object objectCallableBoolean = ((List<?>) value).get(1);
        
        if (objectCallableBoolean != null) {
            
            AbstractCallableBoolean<?> callableBoolean = (AbstractCallableBoolean<?>) objectCallableBoolean;
            String charText = callableBoolean.getCharText();
            
            if (charText != null && charText.charAt(0) >= 32) {
                text += ":" + callableBoolean.getCharText();
            }
        }
        
        Component component = super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        if (isSelected) {
            component.setBackground(UiUtil.COLOR_FOCUS_GAINED);
        } else {
            component.setBackground(Color.WHITE);
        }
        
        if (column == 3 && objectCallableBoolean != null) {
            
            AbstractCallableBoolean<?> callableBoolean = (AbstractCallableBoolean<?>) objectCallableBoolean;

            if (!callableBoolean.isMultibit()) {
                if (callableBoolean.isTrue()) {

                    if (isSelected) {
                        component.setBackground(new Color(215, 255, 215));
                    } else {
                        component.setBackground(new Color(235, 255, 235));
                    }

                } else {

                    if (isSelected) {
                        component.setBackground(new Color(255, 215, 215));
                    } else {
                        component.setBackground(new Color(255, 235, 235));
                    }
                }
            }
        }
        
        return component;
    }
}