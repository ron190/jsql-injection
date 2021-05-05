package com.jsql.view.swing.scrollpane;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class LightScrollPane extends JComponent {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    protected int scrollBarAlpha = 25;
    
    protected int scrollBarAlphaRollover = 100;
    
    private static final int THUMB_BORDER_SIZE = 0;
    
    public static final int THUMB_SIZE = 15;
    
    protected Color colorThumb = Color.DARK_GRAY;

    public final JScrollPane scrollPane;
    
    private final JScrollBar verticalScrollBar;
    
    private final JScrollBar horizontalScrollBar;

    /**
     * Create a scrollpane with top and left border for default component and a slide one.
     * A component slided to the right will normally hide the left border, JScrollPanePixelBorder fix this.
     * @param top Border top size
     * @param left Border left size
     * @param bottom Border bottom size
     * @param right Border right size
     * @param c Component to decorate
     */
    public LightScrollPane(int top, int left, int bottom, int right, JComponent c) {
        
        this(c);

        this.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, UiUtil.COLOR_COMPONENT_BORDER));
    }
    
    public LightScrollPane(JComponent component) {
        
        this.scrollPane = new JScrollPanePixelBorder(component);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        this.verticalScrollBar = this.scrollPane.getVerticalScrollBar();
        this.verticalScrollBar.setVisible(false);
        this.verticalScrollBar.setOpaque(false);
        this.verticalScrollBar.setUI(new MyScrollBarUI());

        this.horizontalScrollBar = this.scrollPane.getHorizontalScrollBar();
        this.horizontalScrollBar.setVisible(false);
        this.horizontalScrollBar.setOpaque(false);
        this.horizontalScrollBar.setUI(new MyScrollBarUI());

        var layeredPane = new JLayeredPane();
        layeredPane.setLayer(this.verticalScrollBar, JLayeredPane.PALETTE_LAYER);
        layeredPane.setLayer(this.horizontalScrollBar, JLayeredPane.PALETTE_LAYER);

        this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setLayout(new ScrollPaneLayout() {
            
            @Override
            public void layoutContainer(Container parent) {
                
                // Fix #13412: NullPointerException on setBounds()
                // Fix #48549: IllegalStateException on setBounds()
                // Implementation by sun.swing.SwingUtilities2.getFontMetrics()
                try {
                    this.viewport.setBounds(0, 0, LightScrollPane.this.getWidth(), LightScrollPane.this.getHeight() - 1);
                    
                } catch (NullPointerException e) {
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                }
                
                SwingUtilities.invokeLater(() -> LightScrollPane.this.displayScrollBarsIfNecessary(this.viewport));
            }
        });
        
        layeredPane.add(this.horizontalScrollBar);
        layeredPane.add(this.verticalScrollBar);
        layeredPane.add(this.scrollPane);

        this.setLayout(new BorderLayout() {
            
            @Override
            public void layoutContainer(Container target) {
                
                super.layoutContainer(target);
                int width = LightScrollPane.this.getWidth();
                int height = LightScrollPane.this.getHeight();
                
                LightScrollPane.this.scrollPane.setBounds(0, 0, width, height);

                int scrollBarSize = THUMB_SIZE;
                int cornerOffset =
                    LightScrollPane.this.verticalScrollBar.isVisible()
                    && LightScrollPane.this.horizontalScrollBar.isVisible()
                    ? scrollBarSize
                    : 0;
                
                if (LightScrollPane.this.verticalScrollBar.isVisible()) {
                    
                    LightScrollPane.this.verticalScrollBar.setBounds(
                        LightScrollPane.this.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT
                        ? 0
                        : width - scrollBarSize,
                        0,
                        scrollBarSize,
                        height - cornerOffset
                    );
                }
                
                if (LightScrollPane.this.horizontalScrollBar.isVisible()) {
                    
                    LightScrollPane.this.horizontalScrollBar.setBounds(
                        0,
                        height - scrollBarSize,
                        width - cornerOffset,
                        scrollBarSize
                    );
                }
            }
        });
        this.add(layeredPane, BorderLayout.CENTER);
        layeredPane.setBackground(Color.BLUE);
    }

    private void displayScrollBarsIfNecessary(JViewport viewPort) {
        
        this.displayVerticalScrollBarIfNecessary(viewPort);
        this.displayHorizontalScrollBarIfNecessary(viewPort);
    }

    private void displayVerticalScrollBarIfNecessary(JViewport viewPort) {
        
        Rectangle viewRect = viewPort.getViewRect();
        Dimension viewSize = viewPort.getViewSize();
        boolean isDisplayingVerticalScrollBar = viewSize.getHeight() > viewRect.getHeight();
        
        this.verticalScrollBar.setVisible(isDisplayingVerticalScrollBar);
    }

    private void displayHorizontalScrollBarIfNecessary(JViewport viewPort) {
        
        Rectangle viewRect = viewPort.getViewRect();
        Dimension viewSize = viewPort.getViewSize();
        boolean isDisplayingHorizontalScrollBar = viewSize.getWidth() > viewRect.getWidth();
        
        this.horizontalScrollBar.setVisible(isDisplayingHorizontalScrollBar);
    }

    private static class MyScrollBarButton extends JButton {
        
        private MyScrollBarButton() {
            this.setOpaque(false);
            this.setFocusable(false);
            this.setFocusPainted(false);
            this.setBorderPainted(false);
            this.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    private class MyScrollBarUI extends BasicScrollBarUI {
        
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
            return new Dimension(THUMB_SIZE, 24 + 2*2);
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            
            int alpha = this.isThumbRollover() ? LightScrollPane.this.scrollBarAlphaRollover : LightScrollPane.this.scrollBarAlpha;
            int orientation = this.scrollbar.getOrientation();
            int x = thumbBounds.x + THUMB_BORDER_SIZE;
            int y = thumbBounds.y + THUMB_BORDER_SIZE;

            int width =
                orientation == Adjustable.VERTICAL
                ? THUMB_SIZE
                : thumbBounds.width - THUMB_BORDER_SIZE * 2
            ;
            width = Math.max(width, THUMB_SIZE);

            int height =
                orientation == Adjustable.VERTICAL
                ? thumbBounds.height - THUMB_BORDER_SIZE * 2
                : THUMB_SIZE
            ;
            height = Math.max(height, THUMB_SIZE);

            var graphics2D = (Graphics2D) g.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fix Mac OS Color.DARK_GRAY and alpha incompatibility
            Color colorThumbAlpha;
            
            try {
                colorThumbAlpha = new Color(LightScrollPane.this.colorThumb.getRed(), LightScrollPane.this.colorThumb.getGreen(), LightScrollPane.this.colorThumb.getBlue(), alpha);
                
            } catch (NullPointerException e) {
                
                colorThumbAlpha = Color.GRAY;
                
                LOGGER.log(LogLevel.IGNORE, e);
            }
            
            graphics2D.setColor(colorThumbAlpha);
            
            // Unhandled NoClassDefFoundError #65554: Could not initialize class sun.dc.pr.Rasterizer
            graphics2D.fillRect(x, y, width, height);
            
            graphics2D.dispose();
        }
    }
}