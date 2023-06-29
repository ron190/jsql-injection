package com.jsql.view.swing.panel.consoles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.JTable;
import javax.swing.UIManager;

import com.jsql.model.injection.strategy.blind.AbstractCallableBoolean;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
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
            
            if (callableBoolean.isTrue()) {
                
                if (isSelected) {
                    component.setBackground(new Color(128, 255, 128));
                } else {
                    component.setBackground(new Color(192, 255, 192));
                }
                
            } else {
                
                if (isSelected) {
                    component.setBackground(new Color(255, 128, 128));
                } else {
                    component.setBackground(new Color(255, 192, 192));
                }
            }
        }
        
        return component;
    }
}