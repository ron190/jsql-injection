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

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/**
 * A reader interface for an abstract document.  Since
 * the syntax highlighting packages only accept Stings and
 * Readers, this must be used.
 * Since the close() method does nothing and a seek() method
 * has been added, this allows us to get some performance
 * improvements through reuse.  It can be used even after the
 * lexer explicitly closes it by seeking to the place that
 * we want to read next, and reseting the lexer.
 */
class DocumentReader extends Reader {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Current position in the document. Incremented
     * whenever a character is read.
     */
    private long position = 0;
    
    /**
     * Saved position used in the mark and reset methods.
     */
    private long mark = -1;
    
    /**
     * The document that we are working with.
     */
    private AbstractDocument document;
    
    /**
     * Construct a reader on the given document.
     *
     * @param document the document to be read.
     */
    public DocumentReader(AbstractDocument document) {
        this.document = document;
    }

    /**
     * Modifying the document while the reader is working is like
     * pulling the rug out from under the reader.  Alerting the
     * reader with this method (in a nice thread safe way, this
     * should not be called at the same time as a read) allows
     * the reader to compensate.
     */
    public void update(int position, int adjustment) {
        
        if (position < this.position) {
            
            if (this.position < position - adjustment) {
                
                this.position = position;
                
            } else {
                
                this.position += adjustment;
            }
        }
    }

    /**
     * Has no effect.  This reader can be used even after
     * it has been closed.
     */
    @Override
    public void close() {
        // nothing
    }

    /**
     * Save a position for reset.
     *
     * @param readAheadLimit ignored.
     */
    @Override
    public void mark(int readAheadLimit) {
        this.mark = this.position;
    }

    /**
     * This reader support mark and reset.
     *
     * @return true
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * Read a single character.
     *
     * @return the character or -1 if the end of the document has been reached.
     */
    @Override
    public int read() {
        
        if (this.position < this.document.getLength()) {
            
            try {
                char c = this.document.getText((int)this.position, 1).charAt(0);
                this.position++;
                
                return c;
                
            } catch (BadLocationException e) {
                
                LOGGER.log(LogLevel.IGNORE, e);
                return -1;
            }
            
        } else {
            
            return -1;
        }
    }

    /**
     * Read and fill the buffer.
     * This method will always fill the buffer unless the end of the document is reached.
     *
     * @param cbuf the buffer to fill.
     * @return the number of characters read or -1 if no more characters are available in the document.
     */
    @Override
    public int read(char[] cbuf) {
        return this.read(cbuf, 0, cbuf.length);
    }

    /**
     * Read and fill the buffer.
     * This method will always fill the buffer unless the end of the document is reached.
     *
     * @param cbuf the buffer to fill.
     * @param off offset into the buffer to begin the fill.
     * @param len maximum number of characters to put in the buffer.
     * @return the number of characters read or -1 if no more characters are available in the document.
     */
    @Override
    public int read(char[] cbuf, int off, int len) {
        
        if (this.position < this.document.getLength()) {
            
            int length = len;
            
            if (this.position + length >= this.document.getLength()) {
                length = this.document.getLength() - (int)this.position;
            }
            
            if (off + length >= cbuf.length) {
                length = cbuf.length - off;
            }
            
            try {
                String s = this.document.getText((int)this.position, length);
                this.position += length;
                
                for (int i=0; i<length; i++) {
                    cbuf[off+i] = s.charAt(i);
                }
                
                return length;
                
            } catch (BadLocationException e) {
                
                LOGGER.log(LogLevel.IGNORE, e);
                return -1;
            }
        } else {
            
            return -1;
        }
    }

    /**
     * @return true
     */
    @Override
    public boolean ready() {
        return true;
    }

    /**
     * Reset this reader to the last mark, or the beginning of the document if a mark has not been set.
     */
    @Override
    public void reset() {
        
        if (this.mark == -1) {
            
            this.position = 0;
            
        } else {
            
            this.position = this.mark;
        }
        
        this.mark = -1;
    }

    /**
     * Skip characters of input.
     * This method will always skip the maximum number of characters unless
     * the end of the file is reached.
     *
     * @param n number of characters to skip.
     * @return the actual number of characters skipped.
     */
    @Override
    public long skip(long n) {
        
        if (this.position + n <= this.document.getLength()) {
            
            this.position += n;
            return n;
            
        } else {
            
            long oldPos = this.position;
            this.position = this.document.getLength();
            
            return this.document.getLength() - oldPos;
        }
    }

    /**
     * Seek to the given position in the document.
     *
     * @param n the offset to which to seek.
     */
    public void seek(long n) {
        
        if (n <= this.document.getLength()) {
            
            this.position = n;
            
        } else {
            
            this.position = this.document.getLength();
        }
    }
}
