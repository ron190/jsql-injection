package com.jsql.model.suspendable;

import com.jsql.model.bean.database.AbstractElementDatabase;

import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;
        Input input = (Input) o;
        return this.countRowsToFind == input.countRowsToFind && this.isMultipleRows == input.isMultipleRows && Objects.equals(this.payload, input.payload) && Objects.deepEquals(this.sourcePage, input.sourcePage) && Objects.equals(this.metadataInjectionProcess, input.metadataInjectionProcess) && Objects.equals(this.elementDatabase, input.elementDatabase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.payload, Arrays.hashCode(this.sourcePage), this.isMultipleRows, this.countRowsToFind, this.elementDatabase, this.metadataInjectionProcess);
    }

    @Override
    public String toString() {
        return "Input{" +
            "payload='" + this.payload + '\'' +
            ", sourcePage=" + Arrays.toString(this.sourcePage) +
            ", isMultipleRows=" + this.isMultipleRows +
            ", countRowsToFind=" + this.countRowsToFind +
            ", elementDatabase=" + this.elementDatabase +
            ", metadataInjectionProcess='" + this.metadataInjectionProcess + '\'' +
        '}';
    }
}