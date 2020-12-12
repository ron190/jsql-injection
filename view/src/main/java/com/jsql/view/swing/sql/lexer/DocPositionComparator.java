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

import java.util.Comparator;

/**
 * A comparator appropriate for use with Collections of
 * DocPositions.
 */
class DocPositionComparator implements Comparator<DocPosition> {
    
    public static final DocPositionComparator instance = new DocPositionComparator();
    
    private DocPositionComparator() {
        
    }
    
    /**
     * Does this Comparator equal another?
     * Since all DocPositionComparators are the same, they
     * are all equal.
     *
     * @return true for DocPositionComparators, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        
        return this == obj;
    }

    /**
     * Compare two DocPositions
     *
     * @param o1 first DocPosition
     * @param o2 second DocPosition
     * @return negative if first < second, 0 if equal, positive if first > second
     */
    @Override
    public int compare(DocPosition o1, DocPosition o2) {
        
        DocPosition d1 = o1;
        DocPosition d2 = o2;
        
        return d1.getPosition() - d2.getPosition();
    }
}

