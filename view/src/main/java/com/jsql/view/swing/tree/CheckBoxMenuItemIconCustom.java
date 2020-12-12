package com.jsql.view.swing.tree;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

@SuppressWarnings("serial")
public class CheckBoxMenuItemIconCustom implements Icon, UIResource, Serializable {

    @Override
    public void paintIcon( Component c, Graphics g, int x, int y ) {
        
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        boolean isSelected = model.isSelected();
        boolean isEnabled = model.isEnabled();
        boolean isPressed = model.isPressed();
        boolean isArmed = model.isArmed();

        g.translate(x, y);

        if (isEnabled) {
            
            if (isPressed || isArmed) {
                
                g.setColor( MetalLookAndFeel.getControlInfo() );
                g.drawLine( 0, 0, 8, 0 );
                g.drawLine( 0, 0, 0, 8 );
                g.drawLine( 8, 2, 8, 8 );
                g.drawLine( 2, 8, 8, 8 );

                g.setColor( MetalLookAndFeel.getPrimaryControl() );
                g.drawLine( 1, 1, 7, 1 );
                g.drawLine( 1, 1, 1, 7 );
                g.drawLine( 9, 1, 9, 9 );
                g.drawLine( 1, 9, 9, 9 );
                
            } else {
                
                g.setColor( MetalLookAndFeel.getControlDarkShadow() );
                g.drawLine( 0, 0, 8, 0 );
                g.drawLine( 0, 0, 0, 8 );
                g.drawLine( 8, 2, 8, 8 );
                g.drawLine( 2, 8, 8, 8 );

                g.setColor( MetalLookAndFeel.getControlHighlight() );
                g.drawLine( 1, 1, 7, 1 );
                g.drawLine( 1, 1, 1, 7 );
                g.drawLine( 9, 1, 9, 9 );
                g.drawLine( 1, 9, 9, 9 );
            }
        } else {
            
            g.setColor( MetalLookAndFeel.getMenuDisabledForeground() );
            g.drawRect( 0, 0, 8, 8 );
        }

        if (isSelected) {
            this.drawSelected(c, g, b, model, isEnabled);
        }

        g.translate( -x, -y );
    }

    private void drawSelected(Component c, Graphics g, AbstractButton b, ButtonModel model, boolean isEnabled) {
        
        if (isEnabled) {
            
            if ( model.isArmed() || ( c instanceof JMenu && model.isSelected() ) ) {
                
                g.setColor( MetalLookAndFeel.getMenuSelectedForeground() );
                
            } else {
                
                g.setColor( b.getForeground() );
            }
        } else {
            
            g.setColor( MetalLookAndFeel.getMenuDisabledForeground() );
        }

        g.drawLine( 2, 2, 2, 6 );
        g.drawLine( 3, 2, 3, 6 );
        g.drawLine( 4, 4, 8, 0 );
        g.drawLine( 4, 5, 9, 0 );
    }

    @Override
    public int getIconWidth() {
        return 10;
    }

    @Override
    public int getIconHeight() {
        return 10;
    }
}