package com.jsql.view.swing.action;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JSplitPane;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.util.MediatorHelper;

@SuppressWarnings("serial")
public class ActionCloseTabResult extends AbstractAction {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        ActionCloseTabResult.perform(MediatorHelper.tabResults().getSelectedIndex());
    }
    
    public static void perform(int closeTabNumber) {
        
        if (MediatorHelper.tabResults().getTabCount() > 0) {
            
            var tab = MediatorHelper.tabResults().getTabComponentAt(closeTabNumber);
            
            // Stop syntax color highlighter
            if (
                tab instanceof TabHeader
                && ((TabHeader) tab).getCleanableTab() != null
            ) {
                
                ((TabHeader) tab).getCleanableTab().clean();
            }

            MediatorHelper.tabResults().removeTabAt(closeTabNumber);
            
            ActionCloseTabResult.displayPlaceholder();
        }
    }
    
    private static void displayPlaceholder() {
        
        if (MediatorHelper.tabResults().getTabCount() == 0) {
            
            var splitPaneTopBottom = MediatorHelper.frame().getSplitHorizontalTopBottom();
            JSplitPane splitPaneLeftRight = splitPaneTopBottom.getSplitVerticalLeftRight();
            
            int i = splitPaneLeftRight.getDividerLocation();
            
            if (ComponentOrientation.LEFT_TO_RIGHT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))) {
                
                splitPaneLeftRight.setRightComponent(
                    splitPaneTopBottom.getLabelPlaceholderResult()
                );
                
            } else {
                
                splitPaneLeftRight.setLeftComponent(
                    splitPaneTopBottom.getLabelPlaceholderResult()
                );
            }
            
            splitPaneLeftRight.setDividerLocation(i);
        }
    }
}