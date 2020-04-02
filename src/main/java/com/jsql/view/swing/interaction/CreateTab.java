package com.jsql.view.swing.interaction;

import java.awt.ComponentOrientation;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.MediatorGui;

/**
 * Set result tab panel orientation according to locale when first
 * tab is inserted.
 */
public class CreateTab {

    protected CreateTab() {
        
        CreateTab.initializeSplitOrientation();
    }
    
    /**
     * Switch left component with right component when locale orientation requires this.
     */
    public static void initializeSplitOrientation() {
        
        if (MediatorGui.tabResults().getTabCount() == 0) {
            
            int dividerLocation = MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getDividerLocation();
            
            if (ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT) {
                
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setLeftComponent(MediatorGui.tabResults());
            } else {
                
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setRightComponent(MediatorGui.tabResults());
            }
            
            MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setDividerLocation(dividerLocation);
        }
    }
}
