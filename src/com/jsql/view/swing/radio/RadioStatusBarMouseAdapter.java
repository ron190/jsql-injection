package com.jsql.view.swing.radio;

import java.awt.event.MouseEvent;

import com.jsql.model.injection.MediatorModel;

/**
 * Mouse adapter for radio link effect (hover and click).
 */
public class RadioStatusBarMouseAdapter extends RadioAddressBarMouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (MediatorModel.model().isInjectionBuilt) {
            super.mouseClicked(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (MediatorModel.model().isInjectionBuilt) {
            super.mouseEntered(e);
        }
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        if (MediatorModel.model().isInjectionBuilt) {
            super.mouseExited(e);
        }
    }
}