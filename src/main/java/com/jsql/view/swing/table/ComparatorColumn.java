/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.table;

import java.util.Comparator;

/**
 * Comporator for table column values ; column with only int data is sorted like 3 < 20 < 100,
 * column with string will sort like 100 < 20 < 3.
 */
public class ComparatorColumn<T> implements Comparator<T> {
    
    /**
     * Custom compare to sort numbers as numbers.
     * Strings as strings, with numbers ordered before strings.
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(T object1, T object2) {
        boolean isFirstNumber = true;
        boolean isSecondNumber = true;
        
        String value1 = object1.toString().trim();
        String value2 = object2.toString().trim();

        try {
            Long.parseLong(value1);
        } catch (NumberFormatException e) {
            isFirstNumber = false;
        }
        
        try {
            Long.parseLong(value2);
        } catch (NumberFormatException e) {
            isSecondNumber = false;
        }
        
        int sortOrder;
        if (isFirstNumber && isSecondNumber) {
            // or Sort by Number
            sortOrder = Long.valueOf(value1).compareTo(Long.valueOf(value2));
        } else if (isFirstNumber) {
            // or Sort by Number first
            sortOrder = -1;
        } else if (isSecondNumber) {
            // or Sort by Letter first
            sortOrder = 1;
        } else {
            // Sort by Letter
            sortOrder = value1.compareToIgnoreCase(value2);
        }
        
        return sortOrder;
    }
    
}
