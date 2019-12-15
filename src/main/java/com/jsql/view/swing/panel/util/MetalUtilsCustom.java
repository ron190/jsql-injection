package com.jsql.view.swing.panel.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

//import sun.swing.CachedPainter;
//import sun.swing.ImageIconUIResource;

public class MetalUtilsCustom {
    
    /**
     * Utility class
     */
    private MetalUtilsCustom() {
        // nothing
    }

    static void drawFlush3DBorder(Graphics g, Rectangle r) {
        drawFlush3DBorder(g, r.x, r.y, r.width, r.height);
    }

    /**
      * This draws the "Flush 3D Border" which is used throughout the Metal L&F
      */
    static void drawFlush3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);
        g.setColor( MetalLookAndFeel.getControlDarkShadow() );
        g.drawRect( 0, 0, w-2, h-2 );
        g.setColor( MetalLookAndFeel.getControlHighlight() );
        g.drawRect( 1, 1, w-2, h-2 );
        g.setColor( MetalLookAndFeel.getControl() );
        g.drawLine( 0, h-1, 1, h-2 );
        g.drawLine( w-1, 0, w-2, 1 );
        g.translate( -x, -y);
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like pressed buttons.
      */
    static void drawPressed3DBorder(Graphics g, Rectangle r) {
        drawPressed3DBorder( g, r.x, r.y, r.width, r.height );
    }

    static void drawDisabledBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);
        g.setColor( MetalLookAndFeel.getControlShadow() );
        g.drawRect( 0, 0, w-1, h-1 );
        g.translate(-x, -y);
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like pressed buttons.
      */
    static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);

        drawFlush3DBorder(g, 0, 0, w, h);

        g.setColor( MetalLookAndFeel.getControlShadow() );
        g.drawLine( 1, 1, 1, h-2 );
        g.drawLine( 1, 1, w-2, 1 );
        g.translate( -x, -y);
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like active toggle buttons.
      * This is used rarely.
      */
    static void drawDark3DBorder(Graphics g, Rectangle r) {
        drawDark3DBorder(g, r.x, r.y, r.width, r.height);
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like active toggle buttons.
      * This is used rarely.
      */
    static void drawDark3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);

        drawFlush3DBorder(g, 0, 0, w, h);

        g.setColor( MetalLookAndFeel.getControl() );
        g.drawLine( 1, 1, 1, h-2 );
        g.drawLine( 1, 1, w-2, 1 );
        g.setColor( MetalLookAndFeel.getControlShadow() );
        g.drawLine( 1, h-2, 1, h-2 );
        g.drawLine( w-2, 1, w-2, 1 );
        g.translate( -x, -y);
    }

    static void drawButtonBorder(Graphics g, int x, int y, int w, int h, boolean active) {
        if (active) {
            drawActiveButtonBorder(g, x, y, w, h);
        } else {
            drawFlush3DBorder(g, x, y, w, h);
        }
    }

    static void drawActiveButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawFlush3DBorder(g, x, y, w, h);
        g.setColor( MetalLookAndFeel.getPrimaryControl() );
        g.drawLine( x+1, y+1, x+1, h-3 );
        g.drawLine( x+1, y+1, w-3, x+1 );
        g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
        g.drawLine( x+2, h-2, w-2, h-2 );
        g.drawLine( w-2, y+2, w-2, h-2 );
    }

    static void drawDefaultButtonBorder(Graphics g, int x, int y, int w, int h, boolean active) {
        drawButtonBorder(g, x+1, y+1, w-1, h-1, active);
        g.translate(x, y);
        g.setColor( MetalLookAndFeel.getControlDarkShadow() );
        g.drawRect( 0, 0, w-3, h-3 );
        g.drawLine( w-2, 0, w-2, 0);
        g.drawLine( 0, h-2, 0, h-2);
        g.translate(-x, -y);
    }

    static void drawDefaultButtonPressedBorder(Graphics g, int x, int y, int w, int h) {
        drawPressed3DBorder(g, x + 1, y + 1, w - 1, h - 1);
        g.translate(x, y);
        g.setColor(MetalLookAndFeel.getControlDarkShadow());
        g.drawRect(0, 0, w - 3, h - 3);
        g.drawLine(w - 2, 0, w - 2, 0);
        g.drawLine(0, h - 2, 0, h - 2);
        g.setColor(MetalLookAndFeel.getControl());
        g.drawLine(w - 1, 0, w - 1, 0);
        g.drawLine(0, h - 1, 0, h - 1);
        g.translate(-x, -y);
    }

    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    static boolean isLeftToRight( Component c ) {
        return c.getComponentOrientation().isLeftToRight();
    }

    static int getInt(Object key, int defaultValue) {
        Object value = UIManager.get(key);

        if (value instanceof Integer) {
            return ((Integer)value);
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            } catch (NumberFormatException nfe) {
                // ignore
            }
        }
        return defaultValue;
    }

//    //
//    // Ocean specific stuff.
//    //
//    /**
//     * Draws a radial type gradient. The gradient will be drawn vertically if
//     * <code>vertical</code> is true, otherwise horizontally.
//     * The UIManager key consists of five values:
//     * r1 r2 c1 c2 c3. The gradient is broken down into four chunks drawn
//     * in order from the origin.
//     * <ol>
//     * <li>Gradient r1 % of the size from c1 to c2
//     * <li>Rectangle r2 % of the size in c2.
//     * <li>Gradient r1 % of the size from c2 to c1
//     * <li>The remaining size will be filled with a gradient from c1 to c3.
//     * </ol>
//     *
//     * @param c Component rendering to
//     * @param g Graphics to draw to.
//     * @param key UIManager key used to look up gradient values.
//     * @param x X coordinate to draw from
//     * @param y Y coordinate to draw from
//     * @param w Width to draw to
//     * @param h Height to draw to
//     * @param vertical Direction of the gradient
//     * @return true if <code>key</code> exists, otherwise false.
//     */
//    @SuppressWarnings("unchecked")
//    public static boolean drawGradient(Component c, Graphics g, String key,
//                                int x, int y, int w, int h, boolean vertical) {
//        List<Object> gradient = (List<Object>)UIManager.get(key);
//        if (gradient == null || !(g instanceof Graphics2D)) {
//            return false;
//        }
//
//        if (w <= 0 || h <= 0) {
//            return true;
//        }
//
//        GradientPainter.INSTANCE.paint(
//                c, (Graphics2D)g, gradient, x, y, w, h, vertical);
//        return true;
//    }
//

//    private static class GradientPainter extends CachedPainter {
//        /**
//         * Instance used for painting.  This is the only instance that is
//         * ever created.
//         */
//        public static final GradientPainter INSTANCE = new GradientPainter(8);
//
//        // Size of images to create. For vertical gradients this is the width,
//        // otherwise it's the height.
//        private static final int IMAGE_SIZE = 64;
//
//        /**
//         * This is the actual width we're painting in, or last painted to.
//         */
//        private int w;
//        /**
//         * This is the actual height we're painting in, or last painted to
//         */
//        private int h;
//
//
//        GradientPainter(int count) {
//            super(count);
//        }
//
//        public void paint(Component c, Graphics2D g,
//                          List<Object> gradient, int x, int y, int w,
//                          int h, boolean isVertical) {
//            int imageWidth;
//            int imageHeight;
//            if (isVertical) {
//                imageWidth = IMAGE_SIZE;
//                imageHeight = h;
//            }
//            else {
//                imageWidth = w;
//                imageHeight = IMAGE_SIZE;
//            }
//            synchronized(c.getTreeLock()) {
//                this.w = w;
//                this.h = h;
//                this.paint(c, g, x, y, imageWidth, imageHeight,
//                      gradient, isVertical);
//            }
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void paintToImage(Component c, Image image, Graphics g,
//                                    int w, int h, Object[] args) {
//            Graphics2D g2 = (Graphics2D)g;
//            List<Object> gradient = (List<Object>)args[0];
//            boolean isVertical = ((Boolean)args[1]).booleanValue();
//            // Render to the VolatileImage
//            if (isVertical) {
//                this.drawVerticalGradient(g2,
//                                     ((Number)gradient.get(0)).floatValue(),
//                                     ((Number)gradient.get(1)).floatValue(),
//                                     (Color)gradient.get(2),
//                                     (Color)gradient.get(3),
//                                     (Color)gradient.get(4), w, h);
//            }
//            else {
//                this.drawHorizontalGradient(g2,
//                                      ((Number)gradient.get(0)).floatValue(),
//                                      ((Number)gradient.get(1)).floatValue(),
//                                      (Color)gradient.get(2),
//                                      (Color)gradient.get(3),
//                                      (Color)gradient.get(4), w, h);
//            }
//        }
//
//        @Override
//        protected void paintImage(Component c, Graphics g,
//                                  int x, int y, int imageW, int imageH,
//                                  Image image, Object[] args) {
//            boolean isVertical = ((Boolean)args[1]).booleanValue();
//            // Render to the screen
//            g.translate(x, y);
//            if (isVertical) {
//                for (int counter = 0 ; counter < this.w ; counter += IMAGE_SIZE) {
//                    int tileSize = Math.min(IMAGE_SIZE, this.w - counter);
//                    g.drawImage(image, counter, 0, counter + tileSize, this.h,
//                                0, 0, tileSize, this.h, null);
//                }
//            }
//            else {
//                for (int counter = 0 ; counter < this.h ; counter += IMAGE_SIZE) {
//                    int tileSize = Math.min(IMAGE_SIZE, this.h - counter);
//                    g.drawImage(image, 0, counter, this.w, counter + tileSize,
//                                0, 0, this.w, tileSize, null);
//                }
//            }
//            g.translate(-x, -y);
//        }
//
//        private void drawVerticalGradient(Graphics2D g, float ratio1,
//                                          float ratio2, Color c1,Color c2,
//                                          Color c3, int w, int h) {
//            int mid = (int)(ratio1 * h);
//            int mid2 = (int)(ratio2 * h);
//            if (mid > 0) {
//                g.setPaint(this.getGradient((float)0, (float)0, c1, (float)0,
//                                       (float)mid, c2));
//                g.fillRect(0, 0, w, mid);
//            }
//            if (mid2 > 0) {
//                g.setColor(c2);
//                g.fillRect(0, mid, w, mid2);
//            }
//            if (mid > 0) {
//                g.setPaint(this.getGradient((float)0, (float)mid + mid2, c2,
//                                       (float)0, (float)mid * 2 + mid2, c1));
//                g.fillRect(0, mid + mid2, w, mid);
//            }
//            if (h - mid * 2 - mid2 > 0) {
//                g.setPaint(this.getGradient((float)0, (float)mid * 2 + mid2, c1,
//                                       (float)0, (float)h, c3));
//                g.fillRect(0, mid * 2 + mid2, w, h - mid * 2 - mid2);
//            }
//        }
//
//        private void drawHorizontalGradient(Graphics2D g, float ratio1,
//                                            float ratio2, Color c1,Color c2,
//                                            Color c3, int w, int h) {
//            int mid = (int)(ratio1 * w);
//            int mid2 = (int)(ratio2 * w);
//            if (mid > 0) {
//                g.setPaint(this.getGradient((float)0, (float)0, c1,
//                                       (float)mid, (float)0, c2));
//                g.fillRect(0, 0, mid, h);
//            }
//            if (mid2 > 0) {
//                g.setColor(c2);
//                g.fillRect(mid, 0, mid2, h);
//            }
//            if (mid > 0) {
//                g.setPaint(this.getGradient((float)mid + mid2, (float)0, c2,
//                                       (float)mid * 2 + mid2, (float)0, c1));
//                g.fillRect(mid + mid2, 0, mid, h);
//            }
//            if (w - mid * 2 - mid2 > 0) {
//                g.setPaint(this.getGradient((float)mid * 2 + mid2, (float)0, c1,
//                                       w, (float)0, c3));
//                g.fillRect(mid * 2 + mid2, 0, w - mid * 2 - mid2, h);
//            }
//        }
//
//        private GradientPaint getGradient(float x1, float y1,
//                                                 Color c1, float x2, float y2,
//                                                 Color c2) {
//            return new GradientPaint(x1, y1, c1, x2, y2, c2, true);
//        }
//    }
//
//
//    /**
//     * Returns true if the specified widget is in a toolbar.
//     */
//    static boolean isToolBarButton(JComponent c) {
//        return c.getParent() instanceof JToolBar;
//    }
//
//    static Icon getOceanToolBarIcon(Image i) {
//        ImageProducer prod = new FilteredImageSource(i.getSource(),
//                             new OceanToolBarImageFilter());
//        return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(prod));
//    }
//
//    static Icon getOceanDisabledButtonIcon(Image image) {
//        Object[] range = (Object[])UIManager.get("Button.disabledGrayRange");
//        int min = 180;
//        int max = 215;
//        if (range != null) {
//            min = ((Integer)range[0]).intValue();
//            max = ((Integer)range[1]).intValue();
//        }
//        ImageProducer prod = new FilteredImageSource(image.getSource(),
//                      new OceanDisabledButtonImageFilter(min , max));
//        return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(prod));
//    }

    
}

