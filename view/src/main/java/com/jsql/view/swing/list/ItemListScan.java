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

import org.json.JSONObject;

/**
 * Basic object to avoid String incompatibility with drag and drop feature.
 */
public class ItemListScan extends ItemList {
    
    private final BeanInjection beanInjection;

    public ItemListScan(BeanInjection newString) {
        super(newString.getUrl());
        this.beanInjection = newString;
    }

    public ItemListScan(JSONObject json) {
        this(new BeanInjection(
            json.optString("url"),
            json.optString("request"),
            json.optString("header"),
            json.optString("method"),
            json.optString("engine"),
            json.optString("requestType")
        ));
    }

    public BeanInjection getBeanInjection() {
        return this.beanInjection;
    }
    
    public String getBeanInjectionToJSON() {
        return new JSONObject(this.beanInjection).toString();
    }
}
