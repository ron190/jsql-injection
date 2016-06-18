/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
public class ComparatorColumn implements Comparator<Object> {
    /**
     * Custom compare to sort numbers as numbers.
     * Strings as strings, with numbers ordered before strings.
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Object object1, Object object2) {
        boolean isFirstNumeric = true;
        boolean isSecondNumeric = true;
        
        String value1 = object1.toString().trim();
        String value2 = object2.toString().trim();

        try {
            Long.parseLong(value1);
        } catch (NumberFormatException e) {
            isFirstNumeric = false;
        }
        
        try {
            Long.parseLong(value2);
        } catch (NumberFormatException e) {
            isSecondNumeric = false;
        }
        
        if (isFirstNumeric && isSecondNumeric) {
            return Long.valueOf(value1).compareTo(Long.valueOf(value2));
        } else if (isFirstNumeric && !isSecondNumeric) {
            return -1;
        } else if (!isFirstNumeric && isSecondNumeric) {
            return 1;
        } else {
            return value1.compareToIgnoreCase(value2);
        }        
    }
}
