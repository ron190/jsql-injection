package com.jsql.view.swing.action;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ActionCloseTabResult extends AbstractAction {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ActionCloseTabResult.perform(MediatorHelper.tabResults().getSelectedIndex());
    }
    
    public static void perform(int closeTabNumber) {
        if (MediatorHelper.tabResults().getTabCount() > 0) {
            MediatorHelper.tabResults().removeTabAt(closeTabNumber);

            if (MediatorHelper.tabResults().getTabCount() == 0) {
                var splitPaneTopBottom = MediatorHelper.frame().getSplitNS();
                JSplitPane splitPaneLeftRight = splitPaneTopBottom.getSplitEW();
                int dividerLocation = splitPaneLeftRight.getDividerLocation();

                var label = new JLabel(UiUtil.APP_BIG.icon);
                label.setMinimumSize(new Dimension(100, 0));
                if (ComponentOrientation.LEFT_TO_RIGHT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))) {
                    splitPaneLeftRight.setRightComponent(label);
                } else {
                    splitPaneLeftRight.setLeftComponent(label);
                }

                splitPaneLeftRight.setDividerLocation(dividerLocation);
            }
        }
    }
}