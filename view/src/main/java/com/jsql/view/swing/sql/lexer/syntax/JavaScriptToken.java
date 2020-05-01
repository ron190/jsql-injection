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
 * A JavaScriptToken is a token that is returned by a lexer that is lexing a javascript
 * source file.  It has several attributes describing the token:
 * The type of token, the text of the token, the line number on which it
 * occurred, the number of characters into the input at which it started, and
 * similarly, the number of characters into the input at which it ended. <br>
 * The tokens should comply with the
 * <A Href="http://developer.netscape.com/docs/manuals/communicator/jsref/index.htm">
 * Java Script Reference</A>.
 */
public class JavaScriptToken extends Token {
  public final static int RESERVED_WORD_ABSTRACT = 0x101;
  public final static int RESERVED_WORD_BOOLEAN = 0x102;
  public final static int RESERVED_WORD_BREAK = 0x103;
  public final static int RESERVED_WORD_BYTE = 0x104;
  public final static int RESERVED_WORD_CASE = 0x105;
  public final static int RESERVED_WORD_CATCH = 0x106;
  public final static int RESERVED_WORD_CHAR = 0x107;
  public final static int RESERVED_WORD_CLASS = 0x108;
  public final static int RESERVED_WORD_CONST = 0x109;
  public final static int RESERVED_WORD_CONTINUE = 0x10A;
  public final static int RESERVED_WORD_DEFAULT = 0x10B;
  public final static int RESERVED_WORD_DO = 0x10C;
  public final static int RESERVED_WORD_DOUBLE = 0x10D;
  public final static int RESERVED_WORD_ELSE = 0x10E;
  public final static int RESERVED_WORD_EXTENDS = 0x10F;
  public final static int RESERVED_WORD_FALSE = 0x110;
  public final static int RESERVED_WORD_FINAL = 0x111;
  public final static int RESERVED_WORD_FINALLY = 0x112;
  public final static int RESERVED_WORD_FLOAT = 0x113;
  public final static int RESERVED_WORD_FOR = 0x114;
  public final static int RESERVED_WORD_FUNCTION = 0x115;
  public final static int RESERVED_WORD_GOTO = 0x116;
  public final static int RESERVED_WORD_IF = 0x117;
  public final static int RESERVED_WORD_IMPLEMENTS = 0x118;
  public final static int RESERVED_WORD_IMPORT = 0x119;
  public final static int RESERVED_WORD_IN = 0x11A;
  public final static int RESERVED_WORD_INSTANCEOF = 0x11B;
  public final static int RESERVED_WORD_INT = 0x11C;
  public final static int RESERVED_WORD_INTERFACE = 0x11D;
  public final static int RESERVED_WORD_LONG = 0x11E;
  public final static int RESERVED_WORD_NATIVE = 0x11F;
  public final static int RESERVED_WORD_NEW = 0x120;
  public final static int RESERVED_WORD_NULL = 0x121;
  public final static int RESERVED_WORD_PACKAGE = 0x122;
  public final static int RESERVED_WORD_PRIVATE = 0x123;
  public final static int RESERVED_WORD_PROTECTED = 0x124;
  public final static int RESERVED_WORD_PUBLIC = 0x125;
  public final static int RESERVED_WORD_RETURN = 0x126;
  public final static int RESERVED_WORD_SHORT = 0x127;
  public final static int RESERVED_WORD_STATIC = 0x128;
  public final static int RESERVED_WORD_SUPER = 0x129;
  public final static int RESERVED_WORD_SWITCH = 0x12A;
  public final static int RESERVED_WORD_SYNCHRONIZED = 0x12B;
  public final static int RESERVED_WORD_THIS = 0x12C;
  public final static int RESERVED_WORD_THROW = 0x12D;
  public final static int RESERVED_WORD_THROWS = 0x12E;
  public final static int RESERVED_WORD_TRANSIENT = 0x12F;
  public final static int RESERVED_WORD_TRUE = 0x130;
  public final static int RESERVED_WORD_TRY = 0x131;
  public final static int RESERVED_WORD_VAR = 0x132;
  public final static int RESERVED_WORD_VOID = 0x133;
  public final static int RESERVED_WORD_WHILE = 0x134;
  public final static int RESERVED_WORD_WITH = 0x135;
  
  public final static int IDENTIFIER = 0x200;

  public final static int LITERAL_BOOLEAN = 0x300;
  public final static int LITERAL_INTEGER_DECIMAL = 0x310;
  public final static int LITERAL_INTEGER_OCTAL = 0x311;
  public final static int LITERAL_INTEGER_HEXIDECIMAL = 0x312;
  public final static int LITERAL_LONG_DECIMAL = 0x320;
  public final static int LITERAL_LONG_OCTAL = 0x321;
  public final static int LITERAL_LONG_HEXIDECIMAL = 0x322;
  public final static int LITERAL_FLOATING_POINT = 0x330;
  public final static int LITERAL_DOUBLE = 0x340;
  public final static int LITERAL_CHARACTER = 0x350;
  public final static int LITERAL_STRING = 0x360;
  public final static int LITERAL_NULL = 0x370;

  public final static int SEPARATOR_LPAREN = 0x400;
  public final static int SEPARATOR_RPAREN = 0x401;
  public final static int SEPARATOR_LBRACE = 0x410;
  public final static int SEPARATOR_RBRACE = 0x411;
  public final static int SEPARATOR_LBRACKET = 0x420;
  public final static int SEPARATOR_RBRACKET = 0x421;
  public final static int SEPARATOR_SEMICOLON = 0x430;
  public final static int SEPARATOR_COMMA = 0x440;
  public final static int SEPARATOR_PERIOD = 0x450;

  public final static int OPERATOR_GREATER_THAN = 0x500;
  public final static int OPERATOR_LESS_THAN = 0x501;
  public final static int OPERATOR_LESS_THAN_OR_EQUAL = 0x502;
  public final static int OPERATOR_GREATER_THAN_OR_EQUAL = 0x503;
  public final static int OPERATOR_EQUAL = 0x504;
  public final static int OPERATOR_NOT_EQUAL = 0x505;
  public final static int OPERATOR_LOGICAL_NOT = 0x510;
  public final static int OPERATOR_LOGICAL_AND = 0x511;
  public final static int OPERATOR_LOGICAL_OR = 0x512;
  public final static int OPERATOR_ADD = 0x520;
  public final static int OPERATOR_SUBTRACT = 0x521;
  public final static int OPERATOR_MULTIPLY = 0x522;
  public final static int OPERATOR_DIVIDE = 0x523;
  public final static int OPERATOR_MOD = 0x524;
  public final static int OPERATOR_BITWISE_COMPLIMENT = 0x530;
  public final static int OPERATOR_BITWISE_AND = 0x531;
  public final static int OPERATOR_BITWISE_OR = 0x532;
  public final static int OPERATOR_BITWISE_XOR = 0x533;
  public final static int OPERATOR_SHIFT_LEFT = 0x540;
  public final static int OPERATOR_SHIFT_RIGHT = 0x541;
  public final static int OPERATOR_SHIFT_RIGHT_UNSIGNED = 0x542;
  public final static int OPERATOR_ASSIGN = 0x550;
  public final static int OPERATOR_ADD_ASSIGN = 0x560;
  public final static int OPERATOR_SUBTRACT_ASSIGN = 0x561;
  public final static int OPERATOR_MULTIPLY_ASSIGN = 0x562;
  public final static int OPERATOR_DIVIDE_ASSIGN = 0x563;
  public final static int OPERATOR_MOD_ASSIGN = 0x564;
  public final static int OPERATOR_BITWISE_AND_ASSIGN = 0x571;
  public final static int OPERATOR_BITWISE_OR_ASSIGN = 0x572;
  public final static int OPERATOR_BITWISE_XOR_ASSIGN = 0x573;
  public final static int OPERATOR_SHIFT_LEFT_ASSIGN = 0x580;
  public final static int OPERATOR_SHIFT_RIGHT_ASSIGN = 0x581;
  public final static int OPERATOR_SHIFT_RIGHT_UNSIGNED_ASSIGN = 0x582;
  public final static int OPERATOR_INCREMENT = 0x590;
  public final static int OPERATOR_DECREMENT = 0x591;
  public final static int OPERATOR_QUESTION = 0x5A0;
  public final static int OPERATOR_COLON = 0x5A1;
  
  public final static int COMMENT_TRADITIONAL = 0xD00;
  public final static int COMMENT_END_OF_LINE = 0xD10;
  public final static int COMMENT_DOCUMENTATION = 0xD20;

  public final static int WHITE_SPACE = 0xE00;

  public final static int ERROR_IDENTIFIER = 0xF00;
  public final static int ERROR_UNCLOSED_STRING = 0xF10;
  public final static int ERROR_MALFORMED_STRING = 0xF11;
  public final static int ERROR_MALFORMED_UNCLOSED_STRING = 0xF12;
  public final static int ERROR_UNCLOSED_CHARACTER = 0xF20;
  public final static int ERROR_MALFORMED_CHARACTER = 0xF21;
  public final static int ERROR_MALFORMED_UNCLOSED_CHARACTER = 0xF22;
  public final static int ERROR_INTEGER_DECIMIAL_SIZE = 0xF30;
  public final static int ERROR_INTEGER_OCTAL_SIZE = 0xF31;
  public final static int ERROR_INTEGER_HEXIDECIMAL_SIZE = 0xF32;
  public final static int ERROR_LONG_DECIMIAL_SIZE = 0xF33;
  public final static int ERROR_LONG_OCTAL_SIZE = 0xF34;
  public final static int ERROR_LONG_HEXIDECIMAL_SIZE = 0xF35;
  public final static int ERROR_FLOAT_SIZE = 0xF36;
  public final static int ERROR_DOUBLE_SIZE = 0xF37;
  public final static int ERROR_FLOAT = 0xF38;
  public final static int ERROR_UNCLOSED_COMMENT = 0xF40;
  
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
  public JavaScriptToken(int ID, String contents, int lineNumber, int charBegin, int charEnd) {
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
  public JavaScriptToken(int ID, String contents, int lineNumber, int charBegin, int charEnd, int state) {
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
  @Override
public int getState() {
    return this.state;
  }
  /**
   * get the ID number of this token
   * 
   * @return the id number of the token
   */
  @Override
public int getID() {
      return this.ID;
  }

  /**
   * get the contents of this token
   * 
   * @return A string representing the text of the token
   */
  @Override
public String getContents() {
      return (new String(this.contents));
  }

  /**
   * get the line number of the input on which this token started
   * 
   * @return the line number of the input on which this token started
   */
  @Override
public int getLineNumber() {
      return this.lineNumber;
  }

  /**
   * get the offset into the input in characters at which this token started
   *
   * @return the offset into the input in characters at which this token started
   */
  @Override
public int getCharBegin() {
      return this.charBegin;
  }

  /**
   * get the offset into the input in characters at which this token ended
   *
   * @return the offset into the input in characters at which this token ended
   */
  @Override
public int getCharEnd() {
     return this.charEnd;
  }

  /**
   * Checks this token to see if it is a reserved word.
   * Reserved words are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java
   * Language Specification</A>.
   *
   * @return true if this token is a reserved word, false otherwise
   */
  public boolean isReservedWord() {
      return((this.ID >> 8) == 0x1);
  }

  /**
   * Checks this token to see if it is an identifier.
   * Identifiers are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java
   * Language Specification</A>.
   *
   * @return true if this token is an identifier, false otherwise
   */
  public boolean isIdentifier() {
      return((this.ID >> 8) == 0x2);
  }

  /**
   * Checks this token to see if it is a literal.
   * Literals are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java
   * Language Specification</A>.
   *
   * @return true if this token is a literal, false otherwise
   */
  public boolean isLiteral() {
      return((this.ID >> 8) == 0x3);
  }
  
  /**
   * Checks this token to see if it is a Separator.
   * Separators are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java
   * Language Specification</A>.
   *
   * @return true if this token is a Separator, false otherwise
   */
  public boolean isSeparator() {
      return((this.ID >> 8) == 0x4);
  }

  /**
   * Checks this token to see if it is a Operator.
   * Operators are explained in <A Href=http://java.sun.com/docs/books/jls/html/>Java
   * Language Specification</A>.
   *
   * @return true if this token is a Operator, false otherwise
   */
  public boolean isOperator() {
      return((this.ID >> 8) == 0x5);
  }

  /**
   * Checks this token to see if it is a comment.
   * 
   * @return true if this token is a comment, false otherwise
   */
  @Override
public boolean isComment() {
      return((this.ID >> 8) == 0xD);
  }

  /**
   * Checks this token to see if it is White Space.
   * Usually tabs, line breaks, form feed, spaces, etc.
   * 
   * @return true if this token is White Space, false otherwise
   */
  @Override
public boolean isWhiteSpace() {
      return((this.ID >> 8) == 0xE);
  }

  /**
   * Checks this token to see if it is an Error.
   * Unfinished comments, numbers that are too big, unclosed strings, etc.
   * 
   * @return true if this token is an Error, false otherwise
   */
  @Override
public boolean isError() {
      return((this.ID >> 8) == 0xF);
  }

    /**
     * A description of this token.  The description should
     * be appropriate for syntax highlighting.  For example
     * "comment" is returned for a comment.
     *
     * @return a description of this token.
     */
    @Override
    public String getDescription() {
        if (this.isReservedWord()) {
            return("reservedWord");
        } else if (this.isIdentifier()) {
            return("identifier");
        } else if (this.isLiteral()) {
            return("literal");
        } else if (this.isSeparator()) {
            return("separator");
        } else if (this.isOperator()) {
            return("operator");
        } else if (this.isComment()) {
            return("comment");
        } else if (this.isWhiteSpace()) {
            return("whitespace");
        } else if (this.isError()) {
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
  @Override
public String errorString() {
      String s;
      if (this.isError()) {
          s = "Error on line " + this.lineNumber + ": ";
          switch (this.ID) {
          case ERROR_IDENTIFIER:
              s += "Unrecognized Identifier: " + this.contents;
          break;
        case ERROR_UNCLOSED_STRING:
              s += "'\"' expected after " + this.contents;
          break;
        case ERROR_MALFORMED_STRING:
        case ERROR_MALFORMED_UNCLOSED_STRING:
              s += "Illegal character in " + this.contents;
          break;
        case ERROR_UNCLOSED_CHARACTER:
              s += "\"'\" expected after " + this.contents;
          break;
        case ERROR_MALFORMED_CHARACTER:
        case ERROR_MALFORMED_UNCLOSED_CHARACTER:
              s += "Illegal character in " + this.contents;
          break;
        case ERROR_INTEGER_DECIMIAL_SIZE:
        case ERROR_INTEGER_OCTAL_SIZE:
        case ERROR_FLOAT:
              s += "Illegal character in " + this.contents;
          break;
        case ERROR_INTEGER_HEXIDECIMAL_SIZE:
        case ERROR_LONG_DECIMIAL_SIZE:
        case ERROR_LONG_OCTAL_SIZE:
        case ERROR_LONG_HEXIDECIMAL_SIZE:
        case ERROR_FLOAT_SIZE:
        case ERROR_DOUBLE_SIZE:
              s += "Literal out of bounds: " + this.contents;
          break;
        case ERROR_UNCLOSED_COMMENT:
              s += "*/ expected after " + this.contents;
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
  @Override
public String toString() {
      return ("Token #" + Integer.toHexString(this.ID) + ": " + this.getDescription() + " Line " +
          this.lineNumber + " from " +this.charBegin + " to " + this.charEnd + " : " + this.contents);
  }
  
}
