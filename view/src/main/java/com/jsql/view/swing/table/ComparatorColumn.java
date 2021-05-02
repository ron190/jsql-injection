/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
 * Comparator for table column values ; column with only int data is sorted like 3 < 20 < 100,
 * column with string will sort like 100 < 20 < 3 < a.
 */
public class ComparatorColumn<T> implements Comparator<T> {
    
    /**
     * Custom compare to sort numbers as numbers.
     * Strings as strings, with numbers ordered before strings.
     * @param cellLeft
     * @param cellRight
     * @return
     */
    @Override
    public int compare(T cellLeft, T cellRight) {
        
        var isFirstNumber = true;
        var isSecondNumber = true;
        
        String valueCellLeft = cellLeft.toString().trim();
        String valueCellRight = cellRight.toString().trim();

        try {
            Long.parseLong(valueCellLeft);
            
        } catch (NumberFormatException e) {
            
            isFirstNumber = false;
        }
        
        try {
            Long.parseLong(valueCellRight);
            
        } catch (NumberFormatException e) {
            
            isSecondNumber = false;
        }
        
        int sortOrder;
        if (isFirstNumber && isSecondNumber) {
            
            // or Sort by Number
            sortOrder = Long.valueOf(valueCellLeft).compareTo(Long.valueOf(valueCellRight));
            
        } else if (isFirstNumber) {
            
            // or Sort by Number first
            sortOrder = -1;
            
        } else if (isSecondNumber) {
            
            // or Sort by Letter first
            sortOrder = 1;
            
        } else {
            
            // Sort by Letter
            sortOrder = valueCellLeft.compareToIgnoreCase(valueCellRight);
        }
        
        return sortOrder;
    }
}
