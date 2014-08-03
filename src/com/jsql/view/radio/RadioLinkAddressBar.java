package com.jsql.view.radio;

import java.util.ArrayList;

import javax.swing.JLabel;

import com.jsql.view.GUIMediator;

@SuppressWarnings("serial")
public class RadioLinkAddressBar extends RadioLink{
	
	private static ArrayList<JLabel> group = new ArrayList<JLabel>();
	
	public RadioLinkAddressBar(String string) {
		super(string);
		init();
	}

	public RadioLinkAddressBar(String string, boolean isSelected) {
		super(string, isSelected);
		init();
	}
	
	private void init(){
	    RadioLinkAddressBar.group.add(this);
	}
	
	@Override
	void action() {
		GUIMediator.top().setSendMethod(RadioLinkAddressBar.this.getText());
	}

	@Override
	ArrayList<JLabel> getGroup() {
		return RadioLinkAddressBar.group;
	}
}