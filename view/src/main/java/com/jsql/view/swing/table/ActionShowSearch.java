package com.jsql.view.swing.table;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ActionShowSearch extends AbstractAction{
    
    private final JPanel panelSearch;
    private final JTextField textFilter;
    
    public ActionShowSearch(JPanel panelSearch, JTextField textFilter) {
        
        this.panelSearch = panelSearch;
        this.textFilter = textFilter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        this.panelSearch.setVisible(true);
        this.textFilter.requestFocusInWindow();
    }
}
