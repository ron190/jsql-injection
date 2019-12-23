package com.jsql.view.swing.text;

import javax.swing.JToolTip;

/**
 * Create panel at the top with textfields and radio.
 */
@SuppressWarnings("serial")
public class JToolTipI18n extends JToolTip {
    
    String textTooltip;

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