/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.table;

import java.util.Comparator;

public class ColumnComparator implements Comparator<Object> {
    /**
     * Custom compare to sort numbers as numbers.
     * Strings as strings, with numbers ordered before strings.
     * 
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
                return -1; // numbers always smaller than letters
            }
        } else {
            if (isSecondNumeric) {
                return 1; // numbers always smaller than letters
            } else {
                try{
                    Integer.parseInt(o1);
                    isFirstNumeric = true;
                }catch(NumberFormatException e){
                    isFirstNumeric = false;
                }
                try{
                    Integer.parseInt(o2);
                    isSecondNumeric = true;
                }catch(NumberFormatException e){
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
                        return -1; // numbers always smaller than letters
                    }
                } else {
                    if (isSecondNumeric) {
                        return 1; // numbers always smaller than letters
                    } else {
                        return o1.compareToIgnoreCase(o2);
                    }
                }
            }
        }
    }
}
