package com.jsql.view.swing.action;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.jsql.i18n.I18nUtil;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.tab.TabHeader;

@SuppressWarnings("serial")
public class ActionCloseTabResult extends AbstractAction {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        ActionCloseTabResult.perform(MediatorGui.tabResults().getSelectedIndex());
    }
    
    public static void perform(int closeTabNumber) {
        
        if (MediatorGui.tabResults().getTabCount() > 0) {
            
            Component tab = MediatorGui.tabResults().getTabComponentAt(closeTabNumber);
            
            // Stop syntax color highlighter
            if (tab instanceof TabHeader && ((TabHeader) tab).getCleanableTab() != null) {
                ((TabHeader) tab).getCleanableTab().clean();
            }

            MediatorGui.tabResults().removeTabAt(closeTabNumber);
            
            ActionCloseTabResult.displayPlaceholder();
        }
    }
    
    private static void displayPlaceholder() {
        
        if (MediatorGui.tabResults().getTabCount() == 0) {
            
            int i = MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getDividerLocation();
            
            if (ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()) == ComponentOrientation.LEFT_TO_RIGHT) {
                
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setRightComponent(
                    MediatorGui.frame().getSplitHorizontalTopBottom().getLabelPlaceholderResult()
                );
            } else {
                
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setLeftComponent(
                    MediatorGui.frame().getSplitHorizontalTopBottom().getLabelPlaceholderResult()
                );
            }
            
            MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setDividerLocation(i);
        }
    }
}