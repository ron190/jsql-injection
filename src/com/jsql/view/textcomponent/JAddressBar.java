/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.textcomponent;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.jsql.model.InjectionModel;

/**
 * A JTextField displaying an icon and buttons.
 */
@SuppressWarnings("serial")
public class JAddressBar extends JPopupTextField implements JComponentDecorator<JTextField> {

    /**
     * Constructor with default text.
     * @param string The text to display
     */
    public JAddressBar(String string) {
        super(new JTextFieldWithIcon(string));
        
        this.proxy.setPreferredSize(new Dimension(0, 27));
        this.proxy.setFont(this.proxy.getFont().deriveFont(Font.PLAIN,this.proxy.getFont().getSize()+2));
    }
    
    private static class JTextFieldWithIcon extends JTextField{
        public JTextFieldWithIcon(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            URL url = JAddressBar.class.getResource("/com/jsql/view/images/globe.png");
            BufferedImage image = null;
            try {
                image = ImageIO.read(url);
            } catch (IOException e) {
                InjectionModel.logger.error(e, e);
            }
            
            Border border = UIManager.getBorder("TextField.border");
            
            int x = border.getBorderInsets(this).left;
            int y = (getHeight() - image.getHeight())/2;
            
            g.drawImage(image, x+4, y+1, this);
        }
    }
}