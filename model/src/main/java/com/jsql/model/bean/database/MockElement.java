package com.jsql.model.bean.database;

public class MockElement extends AbstractElementDatabase {

    /**
     * Used by non-progressing threads like File, metadata and shells.
     * Required for suspendable concurrent map tracking.
     */
    public static final AbstractElementDatabase MOCK = new MockElement();

    @Override
    public AbstractElementDatabase getParent() {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getLabelWithCount() {
        return null;
    }
}