package com.jsql.view.swing.panel.preferences;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SpinnerMouseWheelListener implements MouseWheelListener {
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        
        JSpinner source = (JSpinner) e.getComponent();
        SpinnerNumberModel model = (SpinnerNumberModel) source.getModel();
        Integer oldValue = (Integer) source.getValue();
        var intValue = oldValue.intValue() - e.getWheelRotation() * model.getStepSize().intValue();
        int max = (Integer) model.getMaximum(); //1000
        int min = (Integer) model.getMinimum(); //0
        if (min <= intValue && intValue <= max) {
            
            source.setValue(intValue);
        }
    }
}