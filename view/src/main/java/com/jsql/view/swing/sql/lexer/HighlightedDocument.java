/*
 * This file is part of the programmer editor demo
 * Copyright (C) 2001-2005 Stephen Ostermiller
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

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;
import com.jsql.view.swing.sql.lexer.syntax.JavaScriptLexer;
import com.jsql.view.swing.sql.lexer.syntax.Lexer;
import com.jsql.view.swing.sql.lexer.syntax.SQLLexer;

/**
 * A <a href="http://ostermiller.org/syntax/editor.html">demonstration text
 * editor</a> that uses syntax highlighting.
 */
@SuppressWarnings("serial")
public class HighlightedDocument extends DefaultStyledDocument {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    public static final Object SQL_STYLE = SQLLexer.class;
    public static final Object JAVASCRIPT_STYLE = JavaScriptLexer.class;
    
    public static final Object GRAYED_OUT_STYLE = new Object();

    /**
     * A reader wrapped around the document so that the document can be fed into
     * the lexer.
     */
    private transient DocumentReader documentReader;
    
    /** If non-null, all is drawn with this style (no lexing). */
    private transient AttributeSet globalStyle = null;

    /**
     * The lexer that tells us what colors different words should be.
     */
    private transient Lexer syntaxLexer;

    /**
     * A thread that handles the actual coloring.
     */
    private transient Colorer colorer;

    /**
     * A lock for modifying the document, or for actions that depend on the
     * document not being modified.
     */
    private transient Object docLock = new Object();

    /**
     * Create a new Demo
     */
    public HighlightedDocument(Object l) {

        // Start the thread that does the coloring
        this.colorer = new Colorer(this);
        this.colorer.start();

        // create the new document.
        this.documentReader = new DocumentReader(this);
        
        if (l == SQL_STYLE) {
            
            this.syntaxLexer = new SQLLexer(this.documentReader);
            
        } else {
            
            this.syntaxLexer = new JavaScriptLexer(this.documentReader);
        }
    }

    /**
     * Color or recolor the entire document
     */
    public void colorAll() {
        
        this.color(0, this.getLength());
    }

    /**
     * Color a section of the document. The actual coloring will start somewhere
     * before the requested position and continue as long as needed.
     * 
     * @param position
     *            the starting point for the coloring.
     * @param adjustment
     *            amount of text inserted or removed at the starting point.
     */
    public void color(int position, int adjustment) {
        
        this.colorer.color(position, adjustment);
    }
    
    public void setGlobalStyle(AttributeSet value) {
        
        this.globalStyle = value;
        this.colorAll();
    }

    public void setHighlightStyle(Object valueSource) {
        
        Object value = valueSource;
        
        if (value == HighlightedDocument.GRAYED_OUT_STYLE) {
            
            this.setGlobalStyle(TokenStyles.getStyle("grayedOut"));
            return;
        }

        if (!(value instanceof Class)) {
            
            value = HighlightedDocument.SQL_STYLE;
        }
        
        Class<?> source = (Class<?>) value;
        Class<?>[] parms = { Reader.class };
        Object[] args = { this.documentReader };
        
        try {
            Constructor<?> cons = source.getConstructor(parms);
            this.syntaxLexer = (Lexer) cons.newInstance(args);
            this.globalStyle = null;
            this.colorAll();
            
        } catch (
            SecurityException
            | NoSuchMethodException
            | InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException e
        ) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
    
    //
    // Intercept inserts and removes to color them.
    //
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        
        synchronized (this.docLock) {
            
            super.insertString(offs, str, a);
            this.color(offs, str.length());
            this.documentReader.update(offs, str.length());
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        
        synchronized (this.docLock) {
            
            super.remove(offs, len);
            this.color(offs, -len);
            this.documentReader.update(offs, -len);
        }
    }

    // methods for Colorer to retrieve information
    DocumentReader getDocumentReader() { return this.documentReader; }
    Object getDocumentLock() { return this.docLock; }
    Lexer getSyntaxLexer() { return this.syntaxLexer; }
    AttributeSet getGlobalStyle() { return this.globalStyle; }

    /**
     * Deactivate the colorer to end the backend thread.
     */
    public void stopColorer() {
        this.colorer.stopThread();
    }
}
