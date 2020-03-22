package com.jsql.view.swing.panel.consoles;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class TooltipCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
    ) {
        
        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setToolTipText(
            "<html><div style=\"font-size:10px;font-family:'Ubuntu Mono'\">"
            + c.getText().replaceAll("(.{100})(?!$)", "$1<br>")
            + "</div></html>"
        );
        
        return c;
    }
}