package com.jsql.view.swing.interaction;

import com.jsql.view.swing.MediatorGui;

public class CreateTab {

    protected CreateTab() {
        if (MediatorGui.tabResults().getTabCount() == 0) {
            int i = MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.getDividerLocation();
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setRightComponent(
                MediatorGui.tabResults()
            );
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setDividerLocation(i);
        }
    }
    
}
