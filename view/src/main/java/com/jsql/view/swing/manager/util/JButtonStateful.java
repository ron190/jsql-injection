package com.jsql.view.swing.manager.util;

import com.jsql.view.swing.util.I18nViewUtil;

import javax.swing.*;

public class JButtonStateful extends JButton {

    /**
     * State of current injection.
     */
    private StateButton state = StateButton.STARTABLE;
    
    public JButtonStateful(String keyI18nRunButton) {
        super(I18nViewUtil.valueByKey(keyI18nRunButton));
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
}
