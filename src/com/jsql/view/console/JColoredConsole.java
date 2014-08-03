package com.jsql.view.console;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;

@SuppressWarnings("serial")
public class JColoredConsole extends JTextPane {
	String tabName;
	
	public JColoredConsole(String tabName){
		this.tabName = tabName;
//		this.setAutoscrolls(true);	// does not work
	}
	
	public void append(String message, SimpleAttributeSet attribut){
		try {
			this.getDocument().insertString(this.getDocument().getLength(), (this.getDocument().getLength()==0?"":"\n")+message, attribut);
			
			int tabIndex = GUIMediator.bottom().indexOfTab(tabName);
			Component tabHeader = GUIMediator.bottom().getTabComponentAt(tabIndex);
			if(GUIMediator.bottom().getSelectedIndex() != tabIndex)
				tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
		} catch (BadLocationException e) {
			InjectionModel.logger.fatal(message);
		}
	}
}
