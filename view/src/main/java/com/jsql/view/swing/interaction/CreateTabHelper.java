package com.jsql.view.swing.interaction;

import java.awt.ComponentOrientation;

import javax.swing.JSplitPane;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Set result tab panel orientation according to locale when first
 * tab is inserted.
 */
public class CreateTabHelper {

    protected CreateTabHelper() {
        
        CreateTabHelper.initializeSplitOrientation();
    }
    
    /**
     * Switch left component with right component when locale orientation requires this.
     */
    public static void initializeSplitOrientation() {
        
        if (MediatorHelper.tabResults().getTabCount() == 0) {
            
            JSplitPane splitPaneLeftRight = MediatorHelper.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight();
            int dividerLocation = splitPaneLeftRight.getDividerLocation();
            
            if (ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))) {
                
                splitPaneLeftRight.setLeftComponent(MediatorHelper.tabResults());
                
            } else {
                
                splitPaneLeftRight.setRightComponent(MediatorHelper.tabResults());
            }
            
            splitPaneLeftRight.setDividerLocation(dividerLocation);
        }
    }
}
