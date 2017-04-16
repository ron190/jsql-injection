package com.jsql.view.swing.interaction;

import java.awt.ComponentOrientation;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.MediatorGui;

public class CreateTab {

    protected CreateTab() {
        CreateTab.initializeSplitOrientation();
    }
    
    public static void initializeSplitOrientation() {
        if (MediatorGui.tabResults().getTabCount() == 0) {
            int i = MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getDividerLocation();
            
            if (ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT) {
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setLeftComponent(
                    MediatorGui.tabResults()
                );
            } else {
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setRightComponent(
                    MediatorGui.tabResults()
                );
            }
            MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setDividerLocation(i);
        }
    }
}
