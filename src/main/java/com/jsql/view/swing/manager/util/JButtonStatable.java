package com.jsql.view.swing.manager.util;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class JButtonStatable extends JButton {

    /**
     * State of current injection.
     */
    private StateButton state = StateButton.STARTABLE;

    public JButtonStatable(String defaultText) {
        super(defaultText);
    }

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
