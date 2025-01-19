/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.list;

/**
 * Basic object to avoid String incompatibility with drag and drop feature.
 */
public class ItemList {
    
    /**
     * Text displayed on item.
     */
    private final String originalString;
    private String internalString;
    private boolean isVulnerable = false;

    /**
     * Create a JList item.
     */
    public ItemList(String newString) {
        this.internalString = newString;
        this.originalString = newString;
    }
    
    public void reset() {
        this.internalString = this.originalString;
    }
    
    @Override
    public String toString() {
        return this.internalString;
    }
    
    
    // Getter and setter

    public String getInternalString() {
        return this.internalString;
    }

    public void setInternalString(String internalString) {
        this.internalString = internalString;
    }
    
    public String getOriginalString() {
        return this.originalString;
    }

    public boolean isVulnerable() {
        return this.isVulnerable;
    }

    public void setVulnerable(boolean vulnerable) {
        this.isVulnerable = vulnerable;
    }
}
