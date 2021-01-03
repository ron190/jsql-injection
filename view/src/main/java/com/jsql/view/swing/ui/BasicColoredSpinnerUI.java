package com.jsql.view.swing.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

import com.jsql.view.swing.util.UiUtil;

public class BasicColoredSpinnerUI extends BasicSpinnerUI {

    @Override
    protected Component createPreviousButton() {
        
        JButton button = (JButton) super.createPreviousButton();
        button.setBorder(BorderFactory.createLineBorder(UiUtil.COLOR_COMPONENT_BORDER, 1, false));
        return button;
    }
    
    @Override
    protected Component createNextButton() {
        
        JButton button = (JButton) super.createNextButton();
        button.setBorder(BorderFactory.createLineBorder(UiUtil.COLOR_COMPONENT_BORDER, 1, false));
        return button;
    }
}
