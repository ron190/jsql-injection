/*
 * This file is part of a syntax highlighting package
 * Copyright (C) 1999-2001 Stephen Ostermiller
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

import java.io.IOException;

/**
 * A lexer should implement this interface.
 */
public interface Lexer {
    
    /**
     * Returns the next token.
     *
     * @return the next token
     */
    Token getNextToken() throws IOException;

    /**
     * Closes the current input stream, and resets the scanner to read from a new input stream.
     * All internal variables are reset, the old input stream  cannot be reused
     * (content of the internal buffer is discarded and lost).
     * The lexical state is set to the initial state.
     * Subsequent tokens read from the lexer will start with the line, char, and column
     * values given here.
     *
     * @param reader The new input.
     * @param yyline The line number of the first token.
     * @param yychar The position (relative to the start of the stream) of the first token.
     * @param yycolumn The position (relative to the line) of the first token.
     * @throws IOException if an IOExecption occurs while switching readers.
     */
    void reset(java.io.Reader reader, int yyline, int yychar, int yycolumn) throws IOException;
}
