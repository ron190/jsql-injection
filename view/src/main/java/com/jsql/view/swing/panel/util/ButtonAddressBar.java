/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.panel.util;

import java.awt.Dimension;

import javax.swing.JButton;

import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.util.UiUtil;

/**
 * A button displayed in address.
 */
@SuppressWarnings("serial")
public class ButtonAddressBar extends JButton {
    
    /**
     * State of current injection.
     */
    private StateButton state = StateButton.STARTABLE;
    
    /**
     * Create a button in address bar.
     */
    public ButtonAddressBar() {
        
        this.setPreferredSize(new Dimension(18, 16));
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setIcon(UiUtil.ICON_ARROW_DEFAULT);
        this.setRolloverIcon(UiUtil.ICON_ARROW_ROLLOVER);
        this.setPressedIcon(UiUtil.ICON_ARROW_PRESSED);
    }

    /**
     * Return the current state of current process.
     * @return State of process
     */
    public StateButton getState() {
        
        return this.state;
    }

    /**
     * Replace button with Stop icon ; user can stop current process.
     */
    public void setInjectionReady() {
        
        this.state = StateButton.STARTABLE;
        this.setEnabled(true);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setIcon(UiUtil.ICON_ARROW_DEFAULT);
        this.setRolloverIcon(UiUtil.ICON_ARROW_ROLLOVER);
        this.setPressedIcon(UiUtil.ICON_ARROW_PRESSED);
    }

    /**
     * Replace button with Stop icon ; user can stop current process.
     */
    public void setInjectionRunning() {
        
        this.state = StateButton.STOPPABLE;
        this.setEnabled(true);
        
        // turn on before rollovers work
        this.setRolloverEnabled(true);
        this.setIcon(UiUtil.IMG_STOP_DEFAULT);
        this.setRolloverIcon(UiUtil.IMG_STOP_ROLLOVER);
        this.setPressedIcon(UiUtil.IMG_STOP_PPRESSED);
    }

    /**
     * Replace button with an animated GIF until injection process
     * is finished ; user waits the end of process.
     */
    public void setInjectionStopping() {
        
        this.state = StateButton.STOPPING;
        
        // turn on before rollovers work
        this.setRolloverEnabled(false);
        this.setIcon(UiUtil.ICON_LOADER_GIF);
        this.setEnabled(false);
    }
}
