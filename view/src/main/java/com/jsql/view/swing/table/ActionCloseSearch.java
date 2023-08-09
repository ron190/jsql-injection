package com.jsql.view.swing.table;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionCloseSearch extends AbstractAction {
    
    private final JTextField textFilter;
    private final JPanel panelSearch;
    private final PanelTable panelTable;
    
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