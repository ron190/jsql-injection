package com.jsql.view.panel;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.jsql.view.GUITools;

@SuppressWarnings("serial")
public abstract class RadioLink extends JLabel{

	public RadioLink(String string, boolean isSelected) {
		this(string);
		GUITools.setUnderlined(this);
	}
	
	public RadioLink(String string) {
		super(string);
		
		addMouseListener(new MouseAdapter() {
			Font original;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(RadioLink.this.isActivable() && SwingUtilities.isLeftMouseButton(e)){
					for(JLabel r: RadioLink.this.getGroup())
						if(((JLabel) e.getComponent()) != r)
							r.setFont(GUITools.myFont);
						else
							RadioLink.this.action();
							
					GUITools.setUnderlined((JLabel) e.getComponent());
					
					original = e.getComponent().getFont();
					RadioLink.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				original = e.getComponent().getFont();
				
				if(RadioLink.this.isActivable()){
    				Font font = RadioLink.this.getFont();
    		        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>(font.getAttributes());
    		        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    		        RadioLink.this.setFont(font.deriveFont(attributes));
    				RadioLink.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				RadioLink.this.setFont(original);
				RadioLink.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}
	
	protected boolean isActivable(){
		return !RadioLink.this.getFont().getAttributes().containsValue(TextAttribute.WEIGHT_BOLD);
	}
	
	abstract void action();
	abstract ArrayList<JLabel> getGroup();
}