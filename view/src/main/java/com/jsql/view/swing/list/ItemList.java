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
package com.jsql.view.swing.list;

/**
 * Basic object to avoid String incompatibility with drag and drop feature.
 */
public class ItemList {
    
    /**
     * Text displayed on item.
     */
    private String internalString;

    private String originalString;

    /**
     * 
     */
    private boolean isVulnerable = false;
    
    private Boolean isDatabaseConfirmed = false;
    
    /**
     * Create a JList item.
     * @param newString
     */
    public ItemList(String newString) {
        
        this.internalString = newString;
        this.originalString = newString;
    }
    
    public void reset() {
        
        this.internalString = this.originalString;
        this.isVulnerable = false;
        this.isDatabaseConfirmed = false;
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

    public boolean getIsVulnerable() {
        return this.isVulnerable;
    }

    public void setIsVulnerable(boolean isVulnerable) {
        this.isVulnerable = isVulnerable;
    }

    public boolean getIsDatabaseConfirmed() {
        return this.isDatabaseConfirmed;
    }

    public void setIsDatabaseConfirmed(boolean isDatabaseConfirmed) {
        this.isDatabaseConfirmed = isDatabaseConfirmed;
    }
    
    public String getOriginalString() {
        return this.originalString;
    }

    public void setOriginalString(String originalString) {
        this.originalString = originalString;
    }
}
