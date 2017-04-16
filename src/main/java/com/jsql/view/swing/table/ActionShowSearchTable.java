package com.jsql.view.swing.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ActionShowSearchTable extends AbstractAction{
    
    private JPanel panelSearch;
    private JTextField textFilter;
    
    public ActionShowSearchTable(JPanel panelSearch, JTextField textFilter) {
        this.panelSearch = panelSearch;
        this.textFilter = textFilter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.panelSearch.setVisible(true);
        this.textFilter.requestFocusInWindow();
    }
    
}
