package com.jsql.view.swing.tree.custom;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.io.Serializable;

public class CheckBoxMenuItemIconCustom implements Icon, UIResource, Serializable {

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        AbstractButton abstractButton = (AbstractButton) component;
        ButtonModel model = abstractButton.getModel();
        boolean isSelected = model.isSelected();
        boolean isEnabled = model.isEnabled();
        boolean isPressed = model.isPressed();
        boolean isArmed = model.isArmed();

        graphics.translate(x, y);

        if (isEnabled) {
            if (isPressed || isArmed) {
                graphics.setColor(MetalLookAndFeel.getControlInfo());
                graphics.drawLine(0, 0, 8, 0);
                graphics.drawLine(0, 0, 0, 8);
                graphics.drawLine(8, 2, 8, 8);
                graphics.drawLine(2, 8, 8, 8);

                graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            } else {
                graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                graphics.drawLine(0, 0, 8, 0);
                graphics.drawLine(0, 0, 0, 8);
                graphics.drawLine(8, 2, 8, 8);
                graphics.drawLine(2, 8, 8, 8);

                graphics.setColor(MetalLookAndFeel.getControlHighlight());
            }

            graphics.drawLine(1, 1, 7, 1);
            graphics.drawLine(1, 1, 1, 7);
            graphics.drawLine(9, 1, 9, 9);
            graphics.drawLine(1, 9, 9, 9);
        } else {
            graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
            graphics.drawRect(0, 0, 8, 8);
        }

        if (isSelected) {
            this.drawSelected(component, graphics, abstractButton, model, isEnabled);
        }
        graphics.translate(-x, -y);
    }

    private void drawSelected(Component component, Graphics graphics, AbstractButton abstractButton, ButtonModel model, boolean isEnabled) {
        if (isEnabled) {
            if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
            } else {
                graphics.setColor(abstractButton.getForeground());
            }
        } else {
            graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        }

        graphics.drawLine(2, 2, 2, 6);
        graphics.drawLine(3, 2, 3, 6);
        graphics.drawLine(4, 4, 8, 0);
        graphics.drawLine(4, 5, 9, 0);
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