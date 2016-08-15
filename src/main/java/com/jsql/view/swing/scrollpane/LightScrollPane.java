package com.jsql.view.swing.scrollpane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.jsql.view.swing.HelperUi;

@SuppressWarnings("serial")
public class LightScrollPane extends JComponent {
    
    public int scrollBarAlpha = 25;
    public int scrollBarAlphaRollover = 100;
    private static final int THUMB_BORDER_SIZE = 0;
    private static final int THUMB_SIZE = 11;
    public Color colorThumb = Color.DARK_GRAY;

    public final JScrollPane scrollPane;
    private final JScrollBar verticalScrollBar;
    private final JScrollBar horizontalScrollBar;

    /**
     * Create a scrollpane with top and left border for default component and a slide one.
     * A component slided to the right will normaly hide the left border, JScrollPanePixelBorder fix this.
     * @param top Border top size
     * @param left Border left size
     * @param bottom Border bottom size
     * @param right Border right size
     * @param c Component to decorate
     */
    public LightScrollPane(int top, int left, int bottom, int right, JComponent c) {
        this(c);

        this.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, HelperUi.COLOR_COMPONENT_BORDER));
    }
    
    public LightScrollPane(JComponent component) {
        scrollPane = new JScrollPanePixelBorder(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setVisible(false);
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new MyScrollBarUI());

        horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setVisible(false);
        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setUI(new MyScrollBarUI());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayer(verticalScrollBar, JLayeredPane.PALETTE_LAYER);
        layeredPane.setLayer(horizontalScrollBar, JLayeredPane.PALETTE_LAYER);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setLayout(new ScrollPaneLayout() {
            @Override
            public void layoutContainer(Container parent) {
                viewport.setBounds(0, 0, getWidth() - 1, getHeight() - 1);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        displayScrollBarsIfNecessary(viewport);
                    }
                });
            }
        });
        
        layeredPane.add(horizontalScrollBar);
        layeredPane.add(verticalScrollBar);
        layeredPane.add(scrollPane);

        setLayout(new BorderLayout() {
            @Override
            public void layoutContainer(Container target) {
                super.layoutContainer(target);
                int width = getWidth();
                int height = getHeight();
                scrollPane.setBounds(0, 0, width, height);

                int scrollBarSize = 12;
                int cornerOffset = verticalScrollBar.isVisible() && horizontalScrollBar.isVisible() ? scrollBarSize : 0;
                if (verticalScrollBar.isVisible()) {
                    verticalScrollBar.setBounds(
                        width - scrollBarSize, 
                        0, 
                        scrollBarSize, 
                        height - cornerOffset
                    );
                }
                if (horizontalScrollBar.isVisible()) {
                    horizontalScrollBar.setBounds(
                        0, 
                        height - scrollBarSize, 
                        width - cornerOffset, 
                        scrollBarSize
                    );
                }
            }
        });
        add(layeredPane, BorderLayout.CENTER);
        layeredPane.setBackground(Color.BLUE);
    }

    private void displayScrollBarsIfNecessary(JViewport viewPort) {
        displayVerticalScrollBarIfNecessary(viewPort);
        displayHorizontalScrollBarIfNecessary(viewPort);
    }

    private void displayVerticalScrollBarIfNecessary(JViewport viewPort) {
        Rectangle viewRect = viewPort.getViewRect();
        Dimension viewSize = viewPort.getViewSize();
        boolean isDisplayingVerticalScrollBar =
                viewSize.getHeight() > viewRect.getHeight();
        verticalScrollBar.setVisible(isDisplayingVerticalScrollBar);
    }

    private void displayHorizontalScrollBarIfNecessary(JViewport viewPort) {
        Rectangle viewRect = viewPort.getViewRect();
        Dimension viewSize = viewPort.getViewSize();
        boolean isDisplayingHorizontalScrollBar =
                viewSize.getWidth() > viewRect.getWidth();
        horizontalScrollBar.setVisible(isDisplayingHorizontalScrollBar);
    }

    private static class MyScrollBarButton extends JButton {
        private MyScrollBarButton() {
            setOpaque(false);
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
        }
    }

    private  class MyScrollBarUI extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return new MyScrollBarButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return new MyScrollBarButton();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Nothing
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(12, 24 + 2*2);
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            int alpha = isThumbRollover() ? scrollBarAlphaRollover : scrollBarAlpha;
            int orientation = scrollbar.getOrientation();
            int x = thumbBounds.x + THUMB_BORDER_SIZE;
            int y = thumbBounds.y + THUMB_BORDER_SIZE;

            int width = 
                orientation == JScrollBar.VERTICAL
                ? THUMB_SIZE
                : thumbBounds.width - (THUMB_BORDER_SIZE * 2)
            ;
            width = Math.max(width, THUMB_SIZE);

            int height = 
                orientation == JScrollBar.VERTICAL
                ? thumbBounds.height - (THUMB_BORDER_SIZE * 2) 
                : THUMB_SIZE
            ;
            height = Math.max(height, THUMB_SIZE);

            Graphics2D graphics2D = (Graphics2D) g.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fix Mac OS Color.DARK_GRAY and alpha incompatibility
            Color colorThumbAlpha;
            try {
                colorThumbAlpha = new Color(colorThumb.getRed(), colorThumb.getGreen(), colorThumb.getBlue(), alpha);                
            } catch (NullPointerException e) {
                colorThumbAlpha = Color.GRAY;                
            }
            graphics2D.setColor(colorThumbAlpha);
            
            graphics2D.fillRect(x, y, width, height);
            graphics2D.dispose();
        }
    }
}