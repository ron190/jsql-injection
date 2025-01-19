package com.jsql.view.swing.manager.util;

public class ModelItemType {
    private final String keyLabel;
    private final String keyTooltip;

    public ModelItemType(String keyLabel, String keyTooltip) {
        this.keyLabel = keyLabel;
        this.keyTooltip = keyTooltip;
    }

    public String getKeyLabel() {
        return this.keyLabel;
    }

    public String getKeyTooltip() {
        return this.keyTooltip;
    }
}