package com.jsql.mvc.view.component.popup;

public class JPopupTextAreaEditable extends JPopupTextArea {
    public JPopupTextAreaEditable(){
        super();
        
        this.setComponentPopupMenu(new JPopupTextComponentMenu(this, true));
        this.setEditable(true);
    }
}
