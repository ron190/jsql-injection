package com.jsql.view.swing.panel.util;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.io.Serializable;

public class RadioMenuItemIconCustom implements Icon, UIResource, Serializable {
    
    public void paintOceanIcon(Component c, Graphics g, int x, int y) {

        // Fix #95383, NullPointerException on c.getModel()
        if (c == null) {
            return;
        }

        ButtonModel model = ((AbstractButton) c).getModel();
        boolean isSelected = model.isSelected();
        boolean isEnabled = model.isEnabled();
        boolean isPressed = model.isPressed();
        boolean isArmed = model.isArmed();

        g.translate(x, y);

        if (isEnabled) {
            
            if (isPressed || isArmed) {
                
                g.setColor(MetalLookAndFeel.getPrimaryControl());
                
            } else {
                
                g.setColor(MetalLookAndFeel.getControlHighlight());
            }
            
            g.drawLine(2, 9, 7, 9);
            g.drawLine(9, 2, 9, 7);
            g.drawLine(8, 8, 8, 8);

            if (isPressed || isArmed) {
                
                g.setColor(MetalLookAndFeel.getControlInfo());
                
            } else {
                
                g.setColor(MetalLookAndFeel.getControlDarkShadow());
            }
            
        } else {
            
            g.setColor( MetalLookAndFeel.getMenuDisabledForeground() );
        }
        g.drawLine(2, 0, 6, 0);
        g.drawLine(2, 8, 6, 8);
        g.drawLine(0, 2, 0, 6);
        g.drawLine(8, 2, 8, 6);
        g.drawLine(1, 1, 1, 1);
        g.drawLine(7, 1, 7, 1);
        g.drawLine(1, 7, 1, 7);
        g.drawLine(7, 7, 7, 7);

        if (isSelected) {
            this.drawSelected(c, g, model, isEnabled, isArmed);
        }

        g.translate(-x, -y);
    }

    private void drawSelected(Component c, Graphics g, ButtonModel model, boolean isEnabled, boolean isArmed) {
        
        if (isEnabled) {
            
            if (
                isArmed
                || (c instanceof JMenu && model.isSelected())
            ) {
                
                g.setColor(MetalLookAndFeel.getMenuSelectedForeground());
                
            } else {
                
                g.setColor(MetalLookAndFeel.getControlInfo());
            }
            
        } else {
            
            g.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        }
        
        g.drawLine(3, 2, 5, 2);
        g.drawLine(2, 3, 6, 3);
        g.drawLine(2, 4, 6, 4);
        g.drawLine(2, 5, 6, 5);
        g.drawLine(3, 6, 5, 6);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        
        this.paintOceanIcon(c, g, x, y);
    }

    @Override
    public int getIconWidth() {
        
        return new Dimension(10, 10).width;
    }

    @Override
    public int getIconHeight() {
        
        return new Dimension(10, 10).height;
    }
}
