package com.jsql.view.swing.ui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.jsql.util.I18nUtil;

/**
 * The ComponentBorder class allows you to place a real component in
 * the space reserved for painting the Border of a component.
 *
 * This class takes advantage of the knowledge that all Swing components are
 * also Containers. By default the layout manager is null, so we should be
 * able to place a child component anywhere in the parent component. In order
 * to prevent the child component from painting over top of the parent
 * component a Border is added to the parent component such that the insets of
 * the Border will reserve space for the child component to be painted without
 * affecting the parent component.
 */
public class ComponentBorder implements Border {
    
    public enum Edge {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT;
    }

    public static final float LEADING  = 0.0f;
    public static final float CENTER   = 0.5f;
    public static final float TRAILING = 1.0f;

    private JComponent parent;
    private JComponent component;
    private Edge edge;
    private float alignment;
    private int gap = 5;
    private boolean adjustInsets = true;
    private Insets borderInsets = new Insets(0, 0, 0, 0);
    
    private int addX;
    private int addY;

    /**
     * Convenience constructor that uses the default edge (Edge.RIGHT) and
     * alignment (CENTER).
     * @param component the component to be added in the Border area
     */
    public ComponentBorder(JComponent component) {
        this(component, Edge.RIGHT);
    }

    public ComponentBorder(JComponent component, int addX, int addY) {
        
        this(component, Edge.RIGHT);

        this.addX = addX;
        this.addY = addY;
    }

    /**
     * Convenience constructor that uses the default alignment (CENTER).
     * @param component the component to be added in the Border area
     * @param edge a valid Edge enum of TOP, LEFT, BOTTOM, RIGHT
     */
    public ComponentBorder(JComponent component, Edge edge) {
        this(component, edge, CENTER);
    }

    /**
     * Main constructor to create a ComponentBorder.
     * @param component the component to be added in the Border area
     * @param edge a valid Edge enum of TOP, LEFT, BOTTOM, RIGHT
     * @param alignment the alignment of the component along the
     * specified Edge. Must be in the range 0 - 1.0.
     */
    public ComponentBorder(JComponent component, Edge edge, float alignment) {
        
        this.component = component;
        component.setSize(component.getPreferredSize());
        component.setCursor(Cursor.getDefaultCursor());
        this.setEdge(edge);
        this.setAlignment(alignment);
    }

    public boolean isAdjustInsets() {
        return this.adjustInsets;
    }

    public void setAdjustInsets(boolean adjustInsets) {
        this.adjustInsets = adjustInsets;
    }

    /**
     * Get the component alignment along the Border Edge.
     * @return the alignment
     */
    public float getAlignment() {
        return this.alignment;
    }

    /**
     * Set the component alignment along the Border Edge.
     * @param alignment a value in the range 0 - 1.0. Standard values would be
     *                     CENTER (default), LEFT and RIGHT.
     */
    public void setAlignment(float alignment) {
        
        if (alignment > 1.0f) {
            
            this.alignment = 1.0f;
            
        } else if (alignment < 0.0f) {
            
            this.alignment = 0.0f;
            
        } else {
            
            this.alignment = alignment;
        }
    }

    /**
     * Get the Edge the component is positioned along.
     * @return the Edge
     */
    public Edge getEdge() {
        return this.edge;
    }

    /**
     * Set the Edge the component is positioned along.
     * @param edge the Edge the component is position on.
     */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    /**
     * Get the gap between the border component and the parent component.
     * @return the gap in pixels.
     */
    public int getGap() {
        return this.gap;
    }

    /**
     * Set the gap between the border component and the parent component.
     * @param gap the gap in pixels (default is 5)
     */
    public void setGap(int gap) {
        this.gap = gap;
    }

    //
    // Implement the Border interface
    //
    @Override
    public Insets getBorderInsets(Component c) {
        return this.borderInsets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * In this case a real component is to be painted. Setting the location
     * of the component will cause it to be painted at that location.
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        
        float x2 =
            ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
            ? (0 + this.component.getWidth()) * this.component.getAlignmentX() + x
            : (width - this.component.getWidth()) * this.component.getAlignmentX() + x;
        
        float y2 = (height - this.component.getHeight()) * this.component.getAlignmentY() + y;
        
        this.component.setLocation((int) x2 + this.addX, (int) y2 + this.addY);
    }

    /**
     * Install this Border on the specified component by replacing the
     * existing Border with a CompoundBorder containing the original Border
     * and our ComponentBorder
     *
     * This method should only be invoked once all the properties of this
     * class have been set. Installing the Border more than once will cause
     * unpredictable results.
     */
    public void install(JComponent parent) {
        
        this.parent = parent;

        this.determineInsetsAndAlignment();

        // Add this Border to the parent
        var current = parent.getBorder();

        if (current == null) {
            
            parent.setBorder(this);
            
        } else {
            
            var compound = new CompoundBorder(current, this);
            parent.setBorder(compound);
        }

        // Add component to the parent
        parent.add(this.component);
    }

    /**
     * The insets need to be determined so they are included in the preferred
     * size of the component the Border is attached to.
     *
     * The alignment of the component is determined here so it doesn't need
     * to be recalculated every time the Border is painted.
     */
    private void determineInsetsAndAlignment() {
        
        this.borderInsets = new Insets(0, 0, 0, 0);

        // The insets will only be updated for the edge the component will be
        // displayed on.
        //
        // The X, Y alignment of the component is controlled by both the edge
        // and alignment parameters
        if (this.edge == Edge.TOP) {
            
            this.borderInsets.top = this.component.getPreferredSize().height + this.gap;
            this.component.setAlignmentX(this.alignment);
            this.component.setAlignmentY(0.0f);
            
        } else if (this.edge == Edge.BOTTOM) {
            
            this.borderInsets.bottom = this.component.getPreferredSize().height + this.gap;
            this.component.setAlignmentX(this.alignment);
            this.component.setAlignmentY(1.0f);
            
        } else if (this.edge == Edge.LEFT) {
            
            this.borderInsets.left = this.component.getPreferredSize().width + this.gap;
            this.component.setAlignmentX(0.0f);
            this.component.setAlignmentY(this.alignment);
            
        } else if (this.edge == Edge.RIGHT) {
            
            this.borderInsets.right = this.component.getPreferredSize().width + this.gap;
            this.component.setAlignmentX(1.0f);
            this.component.setAlignmentY(this.alignment);
        }

        if (this.adjustInsets) {
            
            this.adjustBorderInsets();
        }
    }

    /**
     * The complimentary edges of the Border may need to be adjusted to allow
     * the component to fit completely in the bounds of the parent component.
     */
    private void adjustBorderInsets() {
        
        var parentInsets = this.parent.getInsets();

        // May need to adjust the height of the parent component to fit
        // the component in the Border
        if (this.edge == Edge.RIGHT || this.edge == Edge.LEFT) {
            
            int parentHeight = this.parent.getPreferredSize().height - parentInsets.top - parentInsets.bottom;
            int diff = this.component.getHeight() - parentHeight;

            if (diff > 0) {
                int topDiff = (int) (diff * this.alignment);
                int bottomDiff = diff - topDiff;
                this.borderInsets.top += topDiff;
                this.borderInsets.bottom += bottomDiff;
            }
        }

        // May need to adjust the width of the parent component to fit
        // the component in the Border
        if (this.edge == Edge.TOP || this.edge == Edge.BOTTOM) {
            
            int parentWidth = this.parent.getPreferredSize().width - parentInsets.left - parentInsets.right;
            int diff = this.component.getWidth() - parentWidth;

            if (diff > 0) {
                int leftDiff = (int) (diff * this.alignment);
                int rightDiff = diff - leftDiff;
                this.borderInsets.left += leftDiff;
                this.borderInsets.right += rightDiff;
            }
        }
    }
}
