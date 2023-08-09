package com.jsql.view.swing.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class RowHeaderRenderer extends DefaultTableCellRenderer {
    
    public RowHeaderRenderer() {
        
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setBackground(new Color(230, 230, 230));
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
    ) {
        
        // Report #218: ignore if value is null
        if (value != null) {
            
            this.setText(value.toString());
        }
        
        return this;
    }
}