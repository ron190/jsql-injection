/*
 * This file is part of a syntax highlighting package
 * Copyright (C) 2002  Stephen Ostermiller
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
package com.jsql.view.swing.sql.lexer.syntax.lexer;

/** 
 * A SQLToken is a token that is returned by a lexer that is lexing an SQL
 * source file.  It has several attributes describing the token:
 * The type of token, the text of the token, the line number on which it
 * occurred, the number of characters into the input at which it started, and
 * similarly, the number of characters into the input at which it ended. <br>
 */ 
public class SQLToken extends Token {
  /**
   * A reserved word (keyword)
   */
  public final static int RESERVED_WORD = 0x100;
  
  /**
   * A variable, name, or other identifier
   */
  public final static int IDENTIFIER = 0x200;
  
  /**
   * A string literal
   */
  public final static int LITERAL_STRING = 0x300;
  /**
   * A bit-string
   */
  public final static int LITERAL_BIT_STRING = 0x310;
  /**
   * An integer
   */
  public final static int LITERAL_INTEGER = 0x320;
  /**
   * A floating point
   */
  public final static int LITERAL_FLOAT = 0x330;

  /**
   * A separator
   */
  public final static int SEPARATOR = 0x400;
  
  /**
   * An operator
   */
  public final static int OPERATOR = 0x500;
    
  /**
   * C style comment, (except possibly nested)
   */
  public final static int COMMENT_TRADITIONAL = 0xD00;
  
  /**
   * a -- to end of line comment.
   */
  public final static int COMMENT_END_OF_LINE = 0xD10;

  /**
   * White space
   */
  public final static int WHITE_SPACE = 0xE00;

  /**
   * An error
   */
  public final static int ERROR = 0xF00;
  /**
   * An comment start embedded in an operator
   */
  public final static int ERROR_UNCLOSED_COMMENT = 0xF02;
  /**
   * An comment start embedded in an operator
   */
  public final static int ERROR_UNCLOSED_STRING = 0xF03;
  /**
   * An comment start embedded in an operator
   */
  public final static int ERROR_UNCLOSED_BIT_STRING = 0xF04;
  /**
   * An comment start embedded in an operator
   */
  public final static int ERROR_BAD_BIT_STRING = 0xF05;
  
  private int ID;
  private String contents;
  private int lineNumber;
  private int charBegin;
  private int charEnd;
  private int state;

  /**
   * Create a new token.
   * The constructor is typically called by the lexer
   *
   * @param ID the id number of the token
   * @param contents A string representing the text of the token
   * @param lineNumber the line number of the input on which this token started
   * @param charBegin the offset into the input in characters at which this token started
   * @param charEnd the offset into the input in characters at which this token ended
   */
  public SQLToken(int ID, String contents, int lineNumber, int charBegin, int charEnd){
    this (ID, contents, lineNumber, charBegin, charEnd, Token.UNDEFINED_STATE);
  }

  /**
   * Create a new token.
   * The constructor is typically called by the lexer
   *
   * @param ID the id number of the token
   * @param contents A string representing the text of the token
   * @param lineNumber the line number of the input on which this token started
   * @param charBegin the offset into the input in characters at which this token started
   * @param charEnd the offset into the input in characters at which this token ended
   * @param state the state the tokenizer is in after returning this token.
   */
  public SQLToken(int ID, String contents, int lineNumber, int charBegin, int charEnd, int state){
	this.ID = ID;
	this.contents = new String(contents);
	this.lineNumber = lineNumber;
	this.charBegin = charBegin;
	this.charEnd = charEnd;
    this.state = state;
  }

  /**
     * Get an integer representing the state the tokenizer is in after
     * returning this token.
     * Those who are interested in incremental tokenizing for performance
     * reasons will want to use this method to figure out where the tokenizer
     * may be restarted.  The tokenizer starts in Token.INITIAL_STATE, so
     * any time that it reports that it has returned to this state, the
     * tokenizer may be restarted from there.
     */
  public int getState(){
    return state;
  }

  /** 
   * get the ID number of this token
   * 
   * @return the id number of the token
   */
  public int getID(){
  	return ID;
  }

  /** 
   * get the contents of this token
   * 
   * @return A string representing the text of the token
   */
  public String getContents(){
  	return (new String(contents));
  }

  /** 
   * get the line number of the input on which this token started
   * 
   * @return the line number of the input on which this token started
   */
  public int getLineNumber(){
  	return lineNumber;
  }

  /** 
   * get the offset into the input in characters at which this token started
   *
   * @return the offset into the input in characters at which this token started
   */
  public int getCharBegin(){
  	return charBegin;
  }

  /** 
   * get the offset into the input in characters at which this token ended
   *
   * @return the offset into the input in characters at which this token ended
   */
  public int getCharEnd(){
 	return charEnd;
  }

  /** 
   * Checks this token to see if it is a reserved word.
   * Reserved words are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java 
   * Language Specification</A>.
   *
   * @return true if this token is a reserved word, false otherwise
   */
  public boolean isReservedWord(){
  	return((ID >> 8) == 0x1);
  }

  /** 
   * Checks this token to see if it is an identifier.
   * Identifiers are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java 
   * Language Specification</A>.
   *
   * @return true if this token is an identifier, false otherwise
   */
  public boolean isIdentifier(){
  	return((ID >> 8) == 0x2);
  }

  /** 
   * Checks this token to see if it is a literal.
   * Literals are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java 
   * Language Specification</A>.
   *
   * @return true if this token is a literal, false otherwise
   */
  public boolean isLiteral(){
  	return((ID >> 8) == 0x3);
  }
  
  /** 
   * Checks this token to see if it is a Separator.
   * Separators are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java 
   * Language Specification</A>.
   *
   * @return true if this token is a Separator, false otherwise
   */
  public boolean isSeparator(){
  	return((ID >> 8) == 0x4);
  }

  /** 
   * Checks this token to see if it is a Operator.
   * Operators are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java 
   * Language Specification</A>.
   *
   * @return true if this token is a Operator, false otherwise
   */
  public boolean isOperator(){
  	return((ID >> 8) == 0x5);
  }

  /** 
   * Checks this token to see if it is a comment.
   * 
   * @return true if this token is a comment, false otherwise
   */
  public boolean isComment(){
  	return((ID >> 8) == 0xD);
  }

  /** 
   * Checks this token to see if it is White Space.
   * Usually tabs, line breaks, form feed, spaces, etc.
   * 
   * @return true if this token is White Space, false otherwise
   */
  public boolean isWhiteSpace(){
  	return((ID >> 8) == 0xE);
  }

  /** 
   * Checks this token to see if it is an Error.
   * Unfinished comments, numbers that are too big, unclosed strings, etc.
   * 
   * @return true if this token is an Error, false otherwise
   */
  public boolean isError(){
  	return((ID >> 8) == 0xF);
  }

	/**
	 * A description of this token.  The description should
	 * be appropriate for syntax highlighting.  For example
	 * "comment" is returned for a comment.
     *
	 * @return a description of this token.
	 */
	public String getDescription(){
		if (isReservedWord()){
			return("reservedWord");
		} else if (isIdentifier()){
			return("identifier");
		} else if (isLiteral()){
			return("literal");
		} else if (isSeparator()){
			return("separator");
		} else if (isOperator()){
			return("operator");
		} else if (isComment()){
			return("comment");
		} else if (isWhiteSpace()){
			return("whitespace");
		} else if (isError()){
		 	return("error");
		} else {
			return("unknown");
		}
	}

  /**
   * get a String that explains the error, if this token is an error.
   * 
   * @return a  String that explains the error, if this token is an error, null otherwise.
   */
  public String errorString(){
  	String s;
  	if (isError()){
  		s = "Error on line " + lineNumber + ": ";
  		switch (ID){
  		case ERROR:
  			s += "Unexpected token: " + contents;
  		break; 
		case ERROR_UNCLOSED_COMMENT:
			s += "Unclosed comment: " + contents;
		break;
		case ERROR_UNCLOSED_STRING:
			s += "Unclosed string literal: " + contents;
		break;
		case ERROR_UNCLOSED_BIT_STRING:
			s += "Unclosed bit-string literal: " + contents;
		break;
		case ERROR_BAD_BIT_STRING:
			s += "Bit-strings can only contain 0 and 1: " + contents;
		break;
		}
  			
  	} else {
  		s = null;
  	}
  	return (s);
  }

  /** 
   * get a representation of this token as a human readable string.
   * The format of this string is subject to change and should only be used
   * for debugging purposes.
   *
   * @return a string representation of this token
   */  
  public String toString() {
      return ("Token #" + Integer.toHexString(ID) + ": " + getDescription() + " Line " + 
      	lineNumber + " from " +charBegin + " to " + charEnd + " : " + contents);
  }
  
}
