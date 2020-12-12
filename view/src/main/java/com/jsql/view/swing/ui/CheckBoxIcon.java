package com.jsql.view.swing.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

@SuppressWarnings("serial")
public class CheckBoxIcon implements Icon, UIResource, Serializable {

    private static final int CONTROL_SIZE = 12;

    private void paintOceanIcon(Component c, Graphics g, int x, int y) {
        
        ButtonModel model = ((JCheckBoxMenuItem) c).getModel();

        g.translate(x, y);
        
        int w = this.getIconWidth();
        int h = this.getIconHeight();
        
        if (model.isEnabled()) {
            
            if (model.isPressed() && model.isArmed()) {
                
                g.setColor(MetalLookAndFeel.getControlShadow());
                g.fillRect(0, 0, w, h);
                g.setColor(MetalLookAndFeel.getControlDarkShadow());
                g.fillRect(0, 0, w, 2);
                g.fillRect(0, 2, 2, h - 2);
                g.fillRect(w - 1, 1, 1, h - 1);
                g.fillRect(1, h - 1, w - 2, 1);
                
            } else if (model.isRollover()) {
                
                g.setColor(MetalLookAndFeel.getControlDarkShadow());
                g.drawRect(0, 0, w - 1, h - 1);
                g.setColor(MetalLookAndFeel.getPrimaryControl());
                g.drawRect(1, 1, w - 3, h - 3);
                g.drawRect(2, 2, w - 5, h - 5);
                
            } else {
                
                g.setColor(MetalLookAndFeel.getControlDarkShadow());
                g.drawRect(0, 0, w - 1, h - 1);
            }
            
            g.setColor( MetalLookAndFeel.getControlInfo() );
            
        } else {
            
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            g.drawRect(0, 0, w - 1, h - 1);
        }
        
        g.translate(-x, -y);
        
        if (model.isSelected()) {
            
            this.drawCheck(g, x, y);
        }
    }
    
    protected void drawCheck(Graphics g, int x, int y) {
        
        g.fillRect(x + 3, y + 5, 2, CheckBoxIcon.CONTROL_SIZE - 8);
        g.drawLine(x + CheckBoxIcon.CONTROL_SIZE - 4, y + 3, x + 5, y + CheckBoxIcon.CONTROL_SIZE - 6);
        g.drawLine(x + CheckBoxIcon.CONTROL_SIZE - 4, y + 4, x + 5, y + CheckBoxIcon.CONTROL_SIZE - 5);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        this.paintOceanIcon(c, g, x, y);
    }

    @Override
    public int getIconWidth() {
        return CheckBoxIcon.CONTROL_SIZE;
    }

    @Override
    public int getIconHeight() {
        return CheckBoxIcon.CONTROL_SIZE;
    }
}