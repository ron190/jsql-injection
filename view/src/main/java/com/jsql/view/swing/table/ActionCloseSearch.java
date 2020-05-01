package com.jsql.view.swing.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ActionCloseSearch extends AbstractAction {
    
    private JTextField textFilter;
    private JPanel panelSearch;
    private PanelTable panelTable;
    
    public ActionCloseSearch(JTextField textFilter, JPanel panelSearch, PanelTable panelTable) {
        
        this.textFilter = textFilter;
        this.panelSearch = panelSearch;
        this.panelTable = panelTable;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        this.textFilter.setText(null);
        this.panelSearch.setVisible(false);
        this.panelTable.getTableValues().requestFocusInWindow();
    }
}