/*
 * This file is part of the programmer editor demo
 * Copyright (C) 2005 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Syntax+Highlighting
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * See COPYING.TXT for details.
 */
package com.jsql.view.swing.sql.lexer;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.jsql.view.swing.HelperUi;

class TokenStyles {
	private TokenStyles() { } // disable constructor
	
	/**
	 * A hash table containing the text styles. Simple attribute sets are hashed
	 * by name (String)
	 */
	private static HashMap styles = new HashMap();

	/**
	 * Create the styles and place them in the hash table.
	 */
	static {
		Color maroon = new Color(0xB03060);
		Color darkBlue = new Color(0x000080);
		Color darkGreen = Color.GREEN.darker();
		Color darkPurple = new Color(0xA020F0).darker();

		addStyle("body", Color.WHITE, Color.BLACK, false, false);
		addStyle("tag", Color.WHITE, Color.BLUE, true, false);
		addStyle("endtag", Color.WHITE, Color.BLUE, false, false);
		addStyle("reference", Color.WHITE, Color.BLACK, false, false);
		addStyle("name", Color.WHITE, maroon, true, false);
		addStyle("value", Color.WHITE, maroon, false, true);
		addStyle("text", Color.WHITE, Color.BLACK, true, false);
		addStyle("reservedWord", Color.WHITE, Color.BLUE, false, false);
		addStyle("identifier", Color.WHITE, Color.BLACK, false, false);
		addStyle("literal", Color.WHITE, maroon, false, false);
		addStyle("separator", Color.WHITE, darkBlue, false, false);
		addStyle("operator", Color.WHITE, Color.BLACK, true, false);
		addStyle("comment", Color.WHITE, darkGreen, false, false);
		addStyle("preprocessor", Color.WHITE, darkPurple, false, false);
		addStyle("whitespace", Color.WHITE, Color.BLACK, false, false);
		addStyle("error", Color.WHITE, Color.RED, false, false);
		addStyle("unknown", Color.WHITE, Color.ORANGE, false, false);
		addStyle("grayedOut", Color.WHITE, Color.GRAY, false, false);
	}
	
	private static void addStyle(String name, Color bg, Color fg, boolean bold,
			boolean italic) {
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, HelperUi.FONT_UBUNTU.getFontName());
		StyleConstants.setFontSize(style, HelperUi.FONT_UBUNTU.getSize());
		StyleConstants.setBackground(style, bg);
		StyleConstants.setForeground(style, fg);
		StyleConstants.setBold(style, bold);
		StyleConstants.setItalic(style, italic);
		styles.put(name, style);
	}

	/**
	 * Retrieve the style for the given type of token.
	 * 
	 * @param styleName
	 *            the label for the type of text ("tag" for example) or null if
	 *            the styleName is not known.
	 * @return the style
	 */
	public static AttributeSet getStyle(String styleName) {
		return (AttributeSet) styles.get(styleName);
	}
}
