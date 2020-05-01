package com.jsql.view.swing.panel.consoles;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class CenterRenderer extends DefaultTableCellRenderer {
    
    public CenterRenderer() {
        
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
}