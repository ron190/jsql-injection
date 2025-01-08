/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.panel.address;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A button displayed in address.
 */
public class ButtonStart extends JButton {

    /**
     * State of current injection.
     */
    private StateButton state = StateButton.STARTABLE;
    private static final String BUTTON_START_TOOLTIP = "BUTTON_START_TOOLTIP";

    private final AtomicReference<JToolTipI18n> tooltipRadio = new AtomicReference<>(
        new JToolTipI18n(I18nUtil.valueByKey(BUTTON_START_TOOLTIP))
    );

    @Override
    public JToolTip createToolTip() {
        tooltipRadio.set(new JToolTipI18n(I18nUtil.valueByKey(BUTTON_START_TOOLTIP)));
        return tooltipRadio.get();
    }

    /**
     * Create a button in address bar.
     */
    public ButtonStart() {
        this.setName("buttonInUrl");
        this.setToolTipText(I18nUtil.valueByKey(BUTTON_START_TOOLTIP));
        I18nViewUtil.addComponentForKey(BUTTON_START_TOOLTIP, tooltipRadio.get());

        this.setPreferredSize(new Dimension(18, 16));
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setRolloverEnabled(true);
        this.setIcons();
    }

    private void setIcons() {
        // required to get correct color at startup instead of blu
        this.setIcon(UiUtil.ARROW.icon);
        this.setRolloverIcon(UiUtil.ARROW_HOVER.icon);
        this.setPressedIcon(UiUtil.ARROW_PRESSED.icon);
    }

    /**
     * Replace button with Stop icon ; user can stop current process.
     */
    public void setInjectionReady() {
        this.state = StateButton.STARTABLE;
        this.setRolloverEnabled(true);
        this.setEnabled(true);
        this.setIcons();
    }

    /**
     * Replace button with Stop icon ; user can stop current process.
     */
    public void setInjectionRunning() {
        this.state = StateButton.STOPPABLE;
        this.setRolloverEnabled(false);
        this.setEnabled(true);
        this.setIcon(UiUtil.CROSS_RED.icon);
        this.setToolTipText(I18nUtil.valueByKey("BUTTON_STOP_TOOLTIP"));
    }

    /**
     * Replace button with icon loader until injection process
     * is finished ; user waits the end of process.
     */
    public void setInjectionStopping() {
        this.state = StateButton.STOPPING;
        this.setRolloverEnabled(false);
        this.setEnabled(false);
        this.setIcon(UiUtil.HOURGLASS.icon);
        this.setToolTipText(I18nUtil.valueByKey("BUTTON_STOPPING_TOOLTIP"));
    }

    /**
     * Return the current state of current process.
     * @return State of process
     */
    public StateButton getState() {
        return this.state;
    }
}
