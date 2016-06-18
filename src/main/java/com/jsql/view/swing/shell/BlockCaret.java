/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.shell;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/**
 * A caret in a block shape.
 */
@SuppressWarnings("serial")
public class BlockCaret extends DefaultCaret {
    /**
     * Create a caret shaped for terminal.
     */
    public BlockCaret() {
        // half a second
        this.setBlinkRate(500);
    }

    @Override
    protected synchronized void damage(Rectangle r) {
        if (r == null) {
            return;
        }

        // give values to x,y,width,height (inherited from java.awt.Rectangle)
        x = r.x;
        y = r.y;
        height = r.height;
        // A value for width was probably set by paint(), which we leave alone.
        // But the first call to damage() precedes the first call to paint(), so
        // in this case we must be prepared to set a valid width, or else
        // paint()
        // will receive a bogus clip area and caret will not get drawn properly.
        if (width <= 0) {
            width = getComponent().getWidth();
        }

        //Calls getComponent().repaint(x, y, width, height) to erase
        repaint();
        // previous location of caret. Sometimes one call isn't enough.
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        JTextComponent comp = getComponent();

        if (comp == null) {
            return;
        }

        int dot = getDot();
        Rectangle r = null;
        char dotChar;
        try {
            r = comp.modelToView(dot);
            if (r == null) {
                return;
            }
            dotChar = comp.getText(dot, 1).charAt(0);
        } catch (BadLocationException e) {
            return;
        }

        if (Character.isWhitespace(dotChar)) {
            dotChar = '_';
        }

        if ((x != r.x) || (y != r.y)) {
            // paint() has been called directly, without a previous call to
            // damage(), so do some cleanup. (This happens, for example, when
            // the text component is resized.)
            damage(r);
            return;
        }

        g.setColor(new Color(0, 255, 0));

        // do this to draw in XOR mode
        g.setXORMode(comp.getBackground());

        width = g.getFontMetrics().charWidth(dotChar);
        if (isVisible()) {
            g.fillRect(r.x, r.y, width, r.height);
        }
    }
}
