package com.jsql.view.swing.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
