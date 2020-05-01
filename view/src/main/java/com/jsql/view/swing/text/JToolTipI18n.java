package com.jsql.view.swing.text;

import javax.swing.JToolTip;

/**
 * Tooltip which text can be changed using setText() when switching i18n language.
 * Overriden method getTipText() provide text to Swing after i18n switch.
 */
@SuppressWarnings("serial")
public class JToolTipI18n extends JToolTip {
    
    private String textTooltip;

    public JToolTipI18n(String textTooltip) {
        this.textTooltip = textTooltip;
    }

    public void setText(String textTooltip) {
        this.textTooltip = textTooltip;
    }

    @Override
    public String getTipText() {
        return this.textTooltip;
    }
}