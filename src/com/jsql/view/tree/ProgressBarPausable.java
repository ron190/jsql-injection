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
package com.jsql.view.tree;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JProgressBar;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;

/**
 * A progress bar with a Pause icon over it.
 */
@SuppressWarnings("serial")
public class ProgressBarPausable extends JProgressBar{
    /**
     * True if icon should be displayed, false otherwise.
     */
    private boolean showIcon = false;

    public ProgressBarPausable(){
        super();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(showIcon){
            try {
                BufferedImage im2 = ImageIO.read(NodePanel.class.getResource(GUITools.PATH_PAUSE));
                g.drawImage(im2, (this.getWidth()-im2.getWidth())/2, (this.getHeight()-im2.getHeight())/2, null);
            } catch (IOException e) {
                GUIMediator.model().sendDebugMessage(e);
            }
        }
    }

    /**
     * Activate pause state, hence display pause icon.
     */
    public void pause(){
        showIcon = true;
    }
}
