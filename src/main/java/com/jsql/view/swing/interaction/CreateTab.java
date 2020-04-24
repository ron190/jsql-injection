package com.jsql.view.swing.interaction;

import java.awt.ComponentOrientation;

import javax.swing.JSplitPane;

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
            
            JSplitPane splitPaneLeftRight = MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight();
            int dividerLocation = splitPaneLeftRight.getDividerLocation();
            
            if (ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT) {
                
                splitPaneLeftRight.setLeftComponent(MediatorGui.tabResults());
                
            } else {
                
                splitPaneLeftRight.setRightComponent(MediatorGui.tabResults());
            }
            
            splitPaneLeftRight.setDividerLocation(dividerLocation);
        }
    }
}
