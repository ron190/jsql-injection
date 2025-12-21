package com.jsql.view.swing.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

public class JSplitPaneWithZeroSizeDivider extends JSplitPane {

    private static final int DIVIDER_DRAG_OFFSET = 4;

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
            bounds.x -= JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET;
            bounds.width = dividerDragSize;
        } else {
            bounds.y -= JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET;
            bounds.height = dividerDragSize;
        }
        divider.setBounds(bounds);
    }

    @Override
    public void updateUI() {
        this.setUI(new SplitPaneWithZeroSizeDividerUI());
        this.revalidate();
    }

    private static class SplitPaneWithZeroSizeDividerUI extends BasicSplitPaneUI {

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

    private static class ZeroSizeDivider extends BasicSplitPaneDivider {

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
                g.drawLine(JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET, 0, JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET, this.getHeight() - 1);
            } else {
                g.drawLine(0, JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET, this.getWidth() - 1, JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET);
            }
        }

        @Override
        protected void dragDividerTo(int location) {
            super.dragDividerTo(location + JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET);
        }

        @Override
        protected void finishDraggingTo(int location) {
            super.finishDraggingTo(location + JSplitPaneWithZeroSizeDivider.DIVIDER_DRAG_OFFSET);
        }
    }
}