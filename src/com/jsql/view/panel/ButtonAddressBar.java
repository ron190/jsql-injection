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
package com.jsql.view.panel;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.jsql.view.GUITools;

@SuppressWarnings("serial")
public class ButtonAddressBar extends JButton {
    private static Icon arrowDefault = new ImageIcon(ButtonAddressBar.class.getResource("/com/jsql/view/images/arrowDefault.png"));
    private static Icon arrowRollover = new ImageIcon(ButtonAddressBar.class.getResource("/com/jsql/view/images/arrowRollover.png"));
    private static Icon arrowPressed = new ImageIcon(ButtonAddressBar.class.getResource("/com/jsql/view/images/arrowPressed.png"));

    public ButtonAddressBar() {
        this.setPreferredSize(new Dimension(18, 16));
        this.setBorder(null);
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        // turn on before rollovers work
        this.setRolloverEnabled(true); this.setIcon(arrowDefault);
        this.setRolloverIcon(arrowRollover);
        this.setPressedIcon(arrowPressed);
    }

    public String state = "Connect";

    public void setInjectionReady() {
        state = "Connect";
        this.setEnabled(true);
        // turn on before rollovers work
        this.setRolloverEnabled(true); this.setIcon(arrowDefault);
        this.setRolloverIcon(arrowRollover);
        this.setPressedIcon(arrowPressed);
    }

    public void setInjectionRunning() {
        state = "Stop";
        this.setEnabled(true);
        // turn on before rollovers work
        this.setRolloverEnabled(true); this.setIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/stopDefault.png")));
        this.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/stopRollover.png")));
        this.setPressedIcon(new ImageIcon(this.getClass().getResource("/com/jsql/view/images/stopPressed.png")));
    }

    public void setInjectionStopping() {
        state = "Stopping...";
        // turn on before rollovers work
        this.setRolloverEnabled(false); this.setIcon(GUITools.LOADER_GIF);
        this.setEnabled(false);
    }
}
