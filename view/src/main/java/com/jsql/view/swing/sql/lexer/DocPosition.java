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

/**
 * A wrapper for a position in a document appropriate for storing
 * in a collection.
 */
class DocPosition {

    /**
     * The actual position
     */
    private int position;
    
    /**
     * Construct a DocPosition from the given offset into the document.
     *
     * @param position The position this DocObject will represent
     */
    public DocPosition(int position) {
        this.position = position;
    }

    /**
     * Get the position represented by this DocPosition
     *
     * @return the position
     */
    int getPosition() {
        return this.position;
    }

    /**
     * Adjust this position.
     * This is useful in cases that an amount of text is inserted
     * or removed before this position.
     *
     * @param adjustment amount (either positive or negative) to adjust this position.
     * @return the DocPosition, adjusted properly.
     */
    public DocPosition adjustPosition(int adjustment) {
        
        this.position += adjustment;
        
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            
            return true;
        }
        
        if (obj == null) {
            
            return false;
        }
        
        if (this.getClass() != obj.getClass()) {
            
            return false;
        }
        
        DocPosition other = (DocPosition) obj;
        if (this.position != other.position) {
            
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        
        final int prime = 31;
        int result = 1;
        result = prime * result + this.position;
        
        return result;
    }

    /**
     * A string representation useful for debugging.
     *
     * @return A string representing the position.
     */
    @Override
    public String toString() {
        return Integer.toString(this.position);
    }
}
