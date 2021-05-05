package com.jsql.view.swing.scrollpane;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

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
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private static final int SCROLL_BAR_ALPHA_ROLLOVER = 100;
    private static final int SCROLL_BAR_ALPHA = 25;

    private static final Color THUMB_COLOR = Color.DARK_GRAY;
    private static final int THUMB_THICKNESS = 15;
    private static final int THUMB_MIN_SIZE = 48;
    private static final int THUMB_MARGIN = 0;

    private final JScrollPane scrollPane;
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
        
        this(view, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    
    public JScrollIndicator(final JComponent view, int scrollPaneConstants) {
        
        this(view, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, scrollPaneConstants);
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
        
        this.scrollPane = new JScrollPane(view, vsbPolicy, hsbPolicy);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(this.scrollPane, JLayeredPane.DEFAULT_LAYER);

        this.controlPanel = new ControlPanel(this.scrollPane);
        this.add(this.controlPanel, JLayeredPane.PALETTE_LAYER);

        this.addComponentListener(
            new ComponentAdapter() {
                
                @Override
                public void componentResized(ComponentEvent e) {
                    
                    // listen to changes of JLayeredPane size
                    JScrollIndicator.this.scrollPane.setSize(JScrollIndicator.this.getSize());
                    JScrollIndicator.this.scrollPane.getViewport().revalidate();
                    JScrollIndicator.this.controlPanel.setSize(JScrollIndicator.this.getSize());
                    JScrollIndicator.this.controlPanel.revalidate();
                }
            }
        );
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
        return this.scrollPane;
    }

    private class ControlPanel extends JPanel {

        private final JMyScrollBar vScrollBar;
        private final JMyScrollBar hScrollBar;

        private ControlPanel(JScrollPane scrollPane) {
            
            this.setLayout(new BorderLayout());
            this.setOpaque(false);

            this.vScrollBar = new JMyScrollBar(Adjustable.VERTICAL);
            scrollPane.setVerticalScrollBar(this.vScrollBar);
            scrollPane.remove(this.vScrollBar);
            
            if (scrollPane.getVerticalScrollBarPolicy() != ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER) {
                
                this.add(this.vScrollBar, BorderLayout.EAST);
            }

            this.hScrollBar = new JMyScrollBar(Adjustable.HORIZONTAL);
            scrollPane.setHorizontalScrollBar(this.hScrollBar);
            scrollPane.remove(this.hScrollBar);
            
            if (scrollPane.getHorizontalScrollBarPolicy() != ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {
                
                this.add(this.hScrollBar, BorderLayout.SOUTH);
            }
        }
    }

    private class JMyScrollBar extends JScrollBar {

        protected final transient MyScrollBarUI scrollUI;

        public JMyScrollBar(int direction) {
            
            super(direction);

            this.scrollUI = new MyScrollBarUI(this);
            super.setUI(this.scrollUI);
            this.setUnitIncrement(64);
            int size = THUMB_THICKNESS + THUMB_MARGIN;
            this.setPreferredSize(new Dimension(size, size));
            this.scrollUI.setVisible();
            
            this.addMouseListener(new MouseAdapter() {
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    
                    JMyScrollBar.this.scrollUI.setVisible();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    
                    JMyScrollBar.this.scrollUI.setVisible();
                }
            });

            this.addAdjustmentListener(adjustmentEvent -> this.scrollUI.setVisible());
        }

        @Override
        public void setUI(ScrollBarUI ui) {
            // Nothing
        }

        @Override
        public void updateUI() {
            // Nothing
        }

        @Override
        public void paint(Graphics g) {
            this.scrollUI.paintThumb(g); // just the thumb
        }

        @Override
        public void repaint(Rectangle r) {
            
            JScrollIndicator scrollIndicator = JScrollIndicator.this;
            
            // Fix #15956: NullPointerException on convertRectangle()
            try {
                var rect = SwingUtilities.convertRectangle(this, r, scrollIndicator);
                rect.grow(1, 1);
                // ensure for a translucent thumb, that the view is first painted
                scrollIndicator.repaint(rect);
                
            } catch (NullPointerException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }
    }

    public class MyScrollBarUI extends BasicScrollBarUI {
        
        private JMyScrollBar myScrollBar;
        private int alpha = 0;

        private MyScrollBarUI(JMyScrollBar scrollBar) {
            
            this.myScrollBar = scrollBar;
        }

        @Override
        protected void installComponents() {
            
            this.incrButton = new JButton();
            this.decrButton = new JButton();
            
            if (this.myScrollBar.getOrientation() == Adjustable.HORIZONTAL) {
                
                int size = THUMB_THICKNESS + THUMB_MARGIN; // let lower right corner empty
                this.incrButton.setPreferredSize(new Dimension(size, size));
                
            } else {
                
                this.incrButton.setPreferredSize(new Dimension(THUMB_MARGIN, THUMB_MARGIN));
            }
            
            this.decrButton.setPreferredSize(new Dimension(THUMB_MARGIN, THUMB_MARGIN));
        }

        @Override
        protected void installDefaults() {
            
            super.installDefaults();

            // ensure the minimum size of the thumb
            int w = this.minimumThumbSize.width;
            int h = this.minimumThumbSize.height;
            
            if (this.myScrollBar.getOrientation() == Adjustable.VERTICAL) {
                
                h = Math.max(h, Math.min(this.maximumThumbSize.height, THUMB_MIN_SIZE));
                
            } else {
                
                w = Math.max(w, Math.min(this.maximumThumbSize.width, THUMB_MIN_SIZE));
            }
            
            this.minimumThumbSize = new Dimension(w, h);
        }

        private void paintThumb(Graphics g) {
            
            int alphaThumb =
                this.isThumbRollover()
                ? SCROLL_BAR_ALPHA_ROLLOVER
                : SCROLL_BAR_ALPHA;

            g.setColor(
                new Color(
                    this.getAlphaColor(THUMB_COLOR).getRed(),
                    this.getAlphaColor(THUMB_COLOR).getGreen(),
                    this.getAlphaColor(THUMB_COLOR).getBlue(),
                    alphaThumb
                )
            );
            
            Rectangle thumbBounds = this.getThumbBounds();

            int x = thumbBounds.x;
            int y = thumbBounds.y;
            int w = thumbBounds.width;
            int h = thumbBounds.height;

            if (this.myScrollBar.getOrientation() == Adjustable.VERTICAL) {
                
                w -= THUMB_MARGIN;
                
            } else {
                
                h -= THUMB_MARGIN;
            }

            g.fillRect(x, y, w, h);
        }

        private Color getAlphaColor(Color color) {
            
            if (this.alpha == 100) {
                
                return color;
            }
            
            int rgb = color.getRGB() & 0xFFFFFF; // color without alpha values
            rgb |= (this.alpha / 100 * 255) << 24; // add alpha value
            
            return new Color(rgb, true);
        }

        public void setAlpha(int alpha) {
            
            this.alpha = alpha;
            this.myScrollBar.repaint(this.getThumbBounds());
        }

        public void setVisible() {
            
            this.myScrollBar.repaint(this.getThumbBounds());
        }
    }
}