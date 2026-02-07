package com.jsql.model.suspendable;

import com.jsql.model.bean.database.AbstractElementDatabase;

public record Input(
    String payload,
    String[] sourcePage,
    boolean isMultipleRows,
    int countRowsToFind,
    AbstractElementDatabase elementDatabase,
    String metadataInjectionProcess
) {
    public Input(String charInsertion) {
        this(charInsertion, null, false, -1, null, null);
    }
}