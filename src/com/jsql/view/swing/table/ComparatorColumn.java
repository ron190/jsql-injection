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
    public int compare(Object oo1, Object oo2) {
        boolean isFirstNumeric, isSecondNumeric;
        String o1 = oo1.toString(), o2 = oo2.toString();


        isFirstNumeric = o1.matches("\\d+");
        isSecondNumeric = o2.matches("\\d+");

        if (isFirstNumeric) {
            if (isSecondNumeric) {
                return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
            } else {
                // numbers always smaller than letters
                return -1;
            }
        } else {
            if (isSecondNumeric) {
                // numbers always smaller than letters
                return 1;
            } else {
                try {
                    Integer.parseInt(o1);
                    isFirstNumeric = true;
                } catch (NumberFormatException e) {
                    isFirstNumeric = false;
                }
                try {
                    Integer.parseInt(o2);
                    isSecondNumeric = true;
                } catch (NumberFormatException e) {
                    isSecondNumeric = false;
                }

                if (isFirstNumeric) {
                    if (isSecondNumeric) {
                        int intCompare = Integer.valueOf(o1.split("[^0-9]")[0]).compareTo(Integer.valueOf(o2.split("[^0-9]")[0]));
                        if (intCompare == 0) {
                            return o1.compareToIgnoreCase(o2);
                        }
                        return intCompare;
                    } else {
                        // numbers always smaller than letters
                        return -1;
                    }
                } else {
                    if (isSecondNumeric) {
                        // numbers always smaller than letters
                        return 1;
                    } else {
                        return o1.compareToIgnoreCase(o2);
                    }
                }
            }
        }
    }
}
