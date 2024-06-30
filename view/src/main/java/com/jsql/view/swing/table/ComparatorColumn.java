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
 * Comparator for table column values ; column with only int data is sorted like 3 lt 20 lt 100,
 * column with string will sort like 100 gt 20 gt 3 gt abc.
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
            sortOrder = Long.valueOf(valueCellLeft).compareTo(Long.valueOf(valueCellRight));  // or Sort by Number
        } else if (isFirstNumber) {
            sortOrder = -1;  // or Sort by Number first
        } else if (isSecondNumber) {
            sortOrder = 1;  // or Sort by Letter first
        } else {
            sortOrder = valueCellLeft.compareToIgnoreCase(valueCellRight);  // Sort by Letter
        }
        
        return sortOrder;
    }
}
