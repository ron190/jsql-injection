/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.component.popupmenu;

@SuppressWarnings("serial")
public class JPopupTextAreaEditable extends JPopupTextArea {
	
    public JPopupTextAreaEditable(){
        super();
        
        initialize();
    }

    public JPopupTextAreaEditable(int i, int j) {
        super(i,j);
        
        initialize();
    }
    
    public void initialize(){
        this.setComponentPopupMenu(new JPopupTextComponentMenu(this, true));
        this.setEditable(true);
    }
}
