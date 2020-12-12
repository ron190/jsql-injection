package com.jsql.view.swing.manager.util;

import javax.swing.JButton;

import com.jsql.view.swing.util.I18nViewUtil;

@SuppressWarnings("serial")
public class JButtonStateful extends JButton {

    /**
     * State of current injection.
     */
    private StateButton state = StateButton.STARTABLE;
    
    private String defaultText;

    public JButtonStateful(String defaultText) {
        
        super(I18nViewUtil.valueByKey(defaultText));
        
        this.defaultText = defaultText;
    }
    
    
    // Getter and setter

    /**
     * Return the current state of current process.
     * @return State of process
     */
    public StateButton getState() {
        return this.state;
    }
    
    public void setState(StateButton state) {
        this.state = state;
    }

    public String getDefaultText() {
        return this.defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }
}
