package com.jsql.view.swing.menubar;

import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;

public class JMenuItemWithMargin extends JMenuItem {

    public JMenuItemWithMargin() {
        
        this.setMargin();
    }

    public JMenuItemWithMargin(String valueByKey, char c) {
        
        super(valueByKey, c);
        
        this.setMargin();
    }
    
    public JMenuItemWithMargin(String valueByKey) {
        
        super(valueByKey);
        
        this.setMargin();
    }

    public JMenuItemWithMargin(Action action) {

        super(action);
        
        this.setMargin();
    }

    private void setMargin() {
        
        // Menu item on Mac has enough margin
        if (!SystemUtils.IS_OS_MAC) {
            
            this.setIcon(UiUtil.ICON_EMPTY);
        }
    }
}
