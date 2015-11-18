package com.jsql.view.swing.scrollpane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * A scrollpane like component, where the scroll bars are floating over the
 * scrollable view to indicate the current scroll positions.
 * The scroll indicators appear smoothly during scroll events and disappear
 * smoothly afterwards.
 * <p>
 * The scrollbars can be dragged just as normal.</p>
 * <p>
 * The usage is similar to a classic scrollpane.</p>
 *
 * @author Jolly Littlebottom
 */
@SuppressWarnings("serial")
public class JScrollIndicator extends JLayeredPane {

    public static final int SCROLL_BAR_ALPHA_ROLLOVER = 100;
    public static final int SCROLL_BAR_ALPHA = 25;

    private static final Color THUMB_COLOR = Color.DARK_GRAY;
    private static final int THUMB_THICKNESS = 11;
    private static final int THUMB_MIN_SIZE = 48;
    private static final int THUMB_MARGIN = 0;


    public final JScrollPane scrollPane;
    private final ControlPanel controlPanel;

    /**
     * Creates a <code>JScrollIndicator</code> that displays the contents of the
     * specified component, where both horizontal and vertical scrollbars appear
     * whenever the component's contents are larger than the view and scrolling
     * in underway or the mouse is over the scrollbar position.
     *
     * @see #setViewportView
     * @param view the component to display in the scrollpane's viewport
     */
    public JScrollIndicator(final JComponent view) {
        this(view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Creates a <code>JScrollIndicator</code> that displays the view component
     * in a viewport whose view position can be controlled with a pair of
     * scrollbars.
     * The scrollbar policies specify when the scrollbars are displayed,
     * For example, if <code>vsbPolicy</code> is
     * <code>JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED</code>
     * then the vertical scrollbar only appears if the view doesn't fit
     * vertically. The available policy settings are listed at
     * {@link #JScrollPane.setVerticalScrollBarPolicy} and
     * {@link #JScrollPane.setHorizontalScrollBarPolicy}.
     *
     * @param view the component to display in the scrollpanes viewport
     * @param vsbPolicy an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal scrollbar policy
     */
    public JScrollIndicator(final JComponent view, int vsbPolicy, int hsbPolicy) {

        scrollPane = new JScrollPane(view, vsbPolicy, hsbPolicy);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        controlPanel = new ControlPanel(scrollPane);
        add(controlPanel, JLayeredPane.PALETTE_LAYER);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                // listen to changes of JLayeredPane size
                scrollPane.setSize(getSize());
                scrollPane.getViewport().revalidate();
                controlPanel.setSize(getSize());
                controlPanel.revalidate();
            }
        });
    }

    /**
     * Returns the scroll pane used by this scroll indicator.
     * Use carefully (e.g. to set unit increments) because not all changes have an
     * effect. You have to write listeners in this cases (e.g. for changing the
     * scrollbar policy)
     * 
     * @return
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    private class ControlPanel extends JPanel {

        private final JMyScrollBar vScrollBar;
        private final JMyScrollBar hScrollBar;

        private ControlPanel(JScrollPane scrollPane) {

            setLayout(new BorderLayout());
            setOpaque(false);

            vScrollBar = new JMyScrollBar(JScrollBar.VERTICAL);
            scrollPane.setVerticalScrollBar(vScrollBar);
            scrollPane.remove(vScrollBar);
            if (scrollPane.getVerticalScrollBarPolicy() != JScrollPane.VERTICAL_SCROLLBAR_NEVER) {
                add(vScrollBar, BorderLayout.EAST);
            }

            hScrollBar = new JMyScrollBar(JScrollBar.HORIZONTAL);
            scrollPane.setHorizontalScrollBar(hScrollBar);
            scrollPane.remove(hScrollBar);
            if (scrollPane.getHorizontalScrollBarPolicy() != JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
                add(hScrollBar, BorderLayout.SOUTH);
            }
        }
    }

    private class JMyScrollBar extends JScrollBar {

        protected final MyScrollBarUI scrollUI;

        public JMyScrollBar(int direction) {
            super(direction);

            scrollUI = new MyScrollBarUI(this);
            super.setUI(scrollUI);
            this.setUnitIncrement(64);
            int size = THUMB_THICKNESS + THUMB_MARGIN;
            setPreferredSize(new Dimension(size, size));
            scrollUI.setVisible(true, true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    scrollUI.setVisible(true, true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    scrollUI.setVisible(false, false);
                }
            });

            addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    scrollUI.setVisible(true, false);
                }
            });
        }

        @Override
        public void setUI(ScrollBarUI ui) {
        }

        @Override
        public void updateUI() {
        }

        @Override
        public void paint(Graphics g) {
            scrollUI.paintThumb(g, this); // just the thumb
        }

        @Override
        public void repaint(Rectangle r) {
            JScrollIndicator scrollIndicator = JScrollIndicator.this;
            Rectangle rect = SwingUtilities.convertRectangle(this, r, scrollIndicator);
            rect.grow(1, 1);
            // ensure for a translucent thumb, that the view is first painted
            scrollIndicator.repaint(rect);
        }
    }

    public class MyScrollBarUI extends BasicScrollBarUI {
        private JMyScrollBar scrollBar;
        private float alpha = 0.0f;

        private MyScrollBarUI(JMyScrollBar scrollBar) {
            this.scrollBar = scrollBar;
        }

        @Override
        protected void installComponents() {
            incrButton = new JButton();
            decrButton = new JButton();
            if (scrollBar.getOrientation() == JScrollBar.HORIZONTAL) {
                int size = THUMB_THICKNESS + THUMB_MARGIN; // let lower right corner empty
                incrButton.setPreferredSize(new Dimension(size, size));
            } else {
                incrButton.setPreferredSize(new Dimension(THUMB_MARGIN, THUMB_MARGIN));
            }
            decrButton.setPreferredSize(new Dimension(THUMB_MARGIN, THUMB_MARGIN));
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();

            // ensure the minimum size of the thumb
            int w = minimumThumbSize.width;
            int h = minimumThumbSize.height;
            if (scrollBar.getOrientation() == JScrollBar.VERTICAL) {
                h = Math.max(h, Math.min(maximumThumbSize.height, THUMB_MIN_SIZE));
            } else {
                w = Math.max(w, Math.min(maximumThumbSize.width, THUMB_MIN_SIZE));
            }
            minimumThumbSize = new Dimension(w, h);
        }

        private void paintThumb(Graphics g, JComponent c) {
            int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;

            g.setColor(new Color(getAlphaColor(THUMB_COLOR).getRed(),
                    getAlphaColor(THUMB_COLOR).getGreen(), getAlphaColor(THUMB_COLOR).getBlue(), alpha));
            
            Rectangle thumbBounds = getThumbBounds();

            int x = thumbBounds.x;
            int y = thumbBounds.y;
            int w = thumbBounds.width;
            int h = thumbBounds.height;

            if (scrollBar.getOrientation() == JScrollBar.VERTICAL) {
                w -= THUMB_MARGIN;
            } else {
                h -= THUMB_MARGIN;
            }

            g.fillRect(x, y, w, h);
        }

        private Color getAlphaColor(Color color) {
            if (alpha == 1.0f) {
                return color;
            }
            int rgb = color.getRGB() & 0xFFFFFF; // color without alpha values
            rgb |= ((int)(alpha*255)) << 24; // add alpha value
            return new Color(rgb, true);
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
            scrollBar.repaint(getThumbBounds());
        }

        public void setVisible(boolean visible, boolean mouseOver) {
            scrollBar.repaint(getThumbBounds());
        }
    }
}