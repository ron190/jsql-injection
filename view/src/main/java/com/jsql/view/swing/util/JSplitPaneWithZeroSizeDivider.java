package com.jsql.view.swing.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

public class JSplitPaneWithZeroSizeDivider extends JSplitPane {

    private final int dividerDragOffset = 4;

    public JSplitPaneWithZeroSizeDivider(int i) {
        super(i);
        this.setDividerSize(1);
        this.setContinuousLayout(true);
    }

    @Override
    public void doLayout() {
        super.doLayout();

        // increase divider width or height
        BasicSplitPaneDivider divider = ((BasicSplitPaneUI) this.getUI()).getDivider();
        Rectangle bounds = divider.getBounds();
        int dividerDragSize = 9;
        if (this.orientation == JSplitPane.HORIZONTAL_SPLIT) {
            bounds.x -= this.dividerDragOffset;
            bounds.width = dividerDragSize;
        } else {
            bounds.y -= this.dividerDragOffset;
            bounds.height = dividerDragSize;
        }
        divider.setBounds(bounds);
    }

    @Override
    public void updateUI() {
        this.setUI(new SplitPaneWithZeroSizeDividerUI());
        this.revalidate();
    }

    private class SplitPaneWithZeroSizeDividerUI extends BasicSplitPaneUI {

        @Override
        public BasicSplitPaneDivider createDefaultDivider() {
            return new ZeroSizeDivider(this);
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();

            // make sure that divider is first component (and can overlap left/right components)
            if (this.splitPane.getComponent(0) != this.divider) {
                this.splitPane.setLeftComponent(this.splitPane.getLeftComponent());
                this.splitPane.setRightComponent(this.splitPane.getRightComponent());
            }
        }
    }

    private class ZeroSizeDivider extends BasicSplitPaneDivider {

        public ZeroSizeDivider(BasicSplitPaneUI ui) {
            super(ui);
            super.setBorder(null);
            this.setBackground(UIManager.getColor("controlShadow"));
        }

        @Override
        public void setBorder(Border border) {
            // ignore
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(this.getBackground());
            if (this.orientation == JSplitPane.HORIZONTAL_SPLIT) {
                g.drawLine(JSplitPaneWithZeroSizeDivider.this.dividerDragOffset, 0, JSplitPaneWithZeroSizeDivider.this.dividerDragOffset, this.getHeight() - 1);
            } else {
                g.drawLine(0, JSplitPaneWithZeroSizeDivider.this.dividerDragOffset, this.getWidth() - 1, JSplitPaneWithZeroSizeDivider.this.dividerDragOffset);
            }
        }

        @Override
        protected void dragDividerTo(int location) {
            super.dragDividerTo(location + JSplitPaneWithZeroSizeDivider.this.dividerDragOffset);
        }

        @Override
        protected void finishDraggingTo(int location) {
            super.finishDraggingTo(location + JSplitPaneWithZeroSizeDivider.this.dividerDragOffset);
        }
    }
}