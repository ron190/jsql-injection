/*
 * This file is part of a syntax highlighting package
 * Copyright (C) 1999, 2000  Stephen Ostermiller
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
package com.jsql.view.swing.sql.lexer.syntax;

/**
 * A generic token class.
 */
public abstract class Token {

    /**
     * The state of the tokenizer is undefined.
     */
    public static final int UNDEFINED_STATE = -1;

    /**
     * The initial state of the tokenizer.
     * Anytime the tokenizer returns to this state,
     * the tokenizer could be restarted from that point
     * with side effects.
     */
    public static final int INITIAL_STATE = 0;

    /**
     * A unique ID for this type of token.
     * Typically, ID numbers for each type will
     * be static variables of the Token class.
     * 
     * @return an ID for this type of token.
     */
    public abstract int getID();
    
    /**
     * A description of this token.  The description should
     * be appropriate for syntax highlighting.  For example
     * "comment" might be returned for a comment.  This should
     * make it easy to do html syntax highlighting.  Just use
     * style sheets to define classes with the same name as
     * the description and write the token in the html file
     * with that css class name.
     *
     * @return a description of this token.
     */
    public abstract String getDescription();
    
    /**
     * The actual meat of the token.
     *
     * @return a string representing the text of the token.
     */
    public abstract String getContents();
    
    /**
     * Determine if this token is a comment.  Sometimes comments should be
     * ignored (compiling code) other times they should be used
     * (syntax highlighting).  This provides a method to check
     * in case you feel you should ignore comments.
     *
     * @return true if this token represents a comment.
     */
    public abstract boolean isComment();
    
    /**
     * Determine if this token is whitespace.  Sometimes whitespace should be
     * ignored (compiling code) other times they should be used
     * (code beautification).  This provides a method to check
     * in case you feel you should ignore whitespace.
     *
     * @return true if this token represents whitespace.
     */
    public abstract boolean isWhiteSpace();
    
    /**
     * Determine if this token is an error.  Lets face it, not all code
     * conforms to spec. The lexer might know about an error
     * if a string literal is not closed, for example.
     *
     * @return true if this token is an error.
     */
      public abstract boolean isError();
    
    /**
     * get the line number of the input on which this token started
     * 
     * @return the line number of the input on which this token started
     */
    public abstract int getLineNumber();

    /**
     * get the offset into the input in characters at which this token started
     *
     * @return the offset into the input in characters at which this token started
     */
    public abstract int getCharBegin();

    /**
     * get the offset into the input in characters at which this token ended
     *
     * @return the offset into the input in characters at which this token ended
     */
    public abstract int getCharEnd();
    
    /**
     * get a String that explains the error, if this token is an error.
     * 
     * @return a  String that explains the error, if this token is an error, null otherwise.
     */
    public abstract String errorString();

    /**
     * Get an integer representing the state the tokenizer is in after
     * returning this token.
     * Those who are interested in incremental tokenizing for performance
     * reasons will want to use this method to figure out where the tokenizer
     * may be restarted.  The tokenizer starts in Token.INITIAL_STATE, so
     * any time that it reports that it has returned to this state, the
     * tokenizer may be restarted from there.
     */
    public abstract int getState();
}
