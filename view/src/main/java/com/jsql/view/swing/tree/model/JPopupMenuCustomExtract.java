package com.jsql.view.swing.tree.model;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class JPopupMenuCustomExtract extends JPopupMenu {
    
    private ButtonGroup buttonGroupLoadRows;
    private JCheckBox radioCustomFromRow;
    private JCheckBox radioCustomToRow;
    private JCheckBox radioCustomFromChar;
    private JCheckBox radioCustomToChar;
    private JMenuItem menuItemDump;
    
    public ButtonGroup getButtonGroupLoadRows() {
        return this.buttonGroupLoadRows;
    }
    
    public void setButtonGroupLoadRows(ButtonGroup buttonGroupLoadRows) {
        this.buttonGroupLoadRows = buttonGroupLoadRows;
    }
    
    public JCheckBox getRadioCustomFromRow() {
        return this.radioCustomFromRow;
    }
    
    public void setRadioCustomFromRow(JCheckBox radioCustomFromRow) {
        this.radioCustomFromRow = radioCustomFromRow;
    }
    
    public JCheckBox getRadioCustomToRow() {
        return this.radioCustomToRow;
    }
    
    public void setRadioCustomToRow(JCheckBox radioCustomToRow) {
        this.radioCustomToRow = radioCustomToRow;
    }
    
    public JCheckBox getRadioCustomFromChar() {
        return this.radioCustomFromChar;
    }
    
    public void setRadioCustomFromChar(JCheckBox radioCustomFromChar) {
        this.radioCustomFromChar = radioCustomFromChar;
    }
    
    public JCheckBox getRadioCustomToChar() {
        return this.radioCustomToChar;
    }
    
    public void setRadioCustomToChar(JCheckBox radioCustomToChar) {
        this.radioCustomToChar = radioCustomToChar;
    }
    
    public JMenuItem getMenuItemDump() {
        return this.menuItemDump;
    }
    
    public void setMenuItemDump(JMenuItem menuItemDump) {
        this.menuItemDump = menuItemDump;
    }
}