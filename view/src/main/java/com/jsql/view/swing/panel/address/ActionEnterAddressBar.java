package com.jsql.view.swing.panel.address;

import java.awt.event.ActionEvent;

import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.panel.PanelAddressBar;

public class ActionEnterAddressBar extends ActionStart {
    
    public ActionEnterAddressBar(PanelAddressBar panelAddressBar) {
        super(panelAddressBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // No injection running
        if (this.panelAddressBar.getAddressMenuBar().getButtonInUrl().getState() == StateButton.STARTABLE) {
            
            this.startInjection();
        }
    }
}