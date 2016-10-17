package com.jsql.view.swing.interaction;

import java.awt.ComponentOrientation;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.MediatorGui;

public class CreateTab {

    protected CreateTab() {
        if (MediatorGui.tabResults().getTabCount() == 0) {
            int i = MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.getDividerLocation();
            
            if (ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT) {
                MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setLeftComponent(
                    MediatorGui.tabResults()
                );
            } else {
                MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setRightComponent(
                    MediatorGui.tabResults()
                );
            }
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setDividerLocation(i);
        }
    }
    
}
