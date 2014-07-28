package com.jsql.view.component;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class CustomBasicComboBoxUI extends BasicComboBoxUI{
	@Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}
	
	@Override
	protected JButton createArrowButton() {
		JButton button = new BasicArrowButton(BasicArrowButton.SOUTH);

		button.setOpaque(false);
		button.setBorderPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder());

		//	      button.setPreferredSize(new Dimension(5, 5));
		//	      button.setMaximumSize(new Dimension(5, 5));
		//	      button.setSize(new Dimension(5, 5));
		return button;
	}
	
	
}

