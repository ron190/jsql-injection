/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.component.popupmenu;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;


@SuppressWarnings("serial")
public class JPopupTextField extends JTextField {
	
    private boolean bigTextField = false;
    private boolean drawPic = false;
    BufferedImage image;
    int x0 = 0;

    public JPopupTextField(){
        initialize();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(drawPic){
            int y = (getHeight() - image.getHeight())/2;
            g.drawImage(image, x0, y+1, this);
        }
    }
    
    public JPopupTextField(boolean big) {
        bigTextField = big;
        initialize();
    }

    public JPopupTextField(String string, boolean bigTextField, boolean drawPic) {
        super(string);
        this.bigTextField = bigTextField;
        this.drawPic = drawPic;
        initialize();
    }

    public JPopupTextField(String string, boolean big) {
        super(string);
        bigTextField = big;
        initialize();
    }

    public JPopupTextField(String string) {
        super(string);
        initialize();
    }

    public void initialize(){
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder( GUITools.DEFAULT_BACKGROUND, 2),
                GUITools.BLU_ROUND_BORDER));
        
        if(drawPic){
            URL url = this.getClass().getResource("/com/jsql/view/images/globe.png");
            try {
                image = ImageIO.read(url);
            } catch (IOException e) {
                GUIMediator.model().sendDebugMessage(e);
            }
            Border border = UIManager.getBorder("TextField.border");
            x0 = border.getBorderInsets(this).left + 4;
            this.setMargin(new Insets(0, x0 + image.getWidth() + 2, 0, 0));
        }
        
        if(bigTextField){
            this.setPreferredSize(new Dimension(0, 26));
            Font plainFont = new Font(this.getFont().getName(),Font.PLAIN,this.getFont().getSize()+2);
            this.setFont(plainFont);
        }
        
        JTextEditable.setEditable(this);
    }
}
