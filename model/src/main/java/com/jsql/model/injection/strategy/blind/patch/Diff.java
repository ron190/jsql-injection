package com.jsql.model.injection.strategy.blind.patch;

import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch.Operation;

/**
 * Class representing one diff operation.
 */
public class Diff implements Comparable<Diff> {
    
    /**
     * One of: INSERT, DELETE or EQUAL.
     */
    private Operation operation;
    
    /**
     * The text associated with this diff operation.
     */
    private String text;

    /**
     * Constructor.  Initializes the diff with the provided values.
     * @param operation One of INSERT, DELETE or EQUAL.
     * @param text The text being applied.
     */
    public Diff(Operation operation, String text) {
        // Construct a diff with the specified operation and text.
        this.operation = operation;
        this.text = text;
    }

    /**
     * Display a human-readable version of this Diff.
     * @return text version.
     */
    @Override
    public String toString() {
        String prettyText = this.text.replace('\n', '\u00b6');
        return "Diff(" + this.operation + ",\"" + prettyText + "\")";
    }

    /**
     * Create a numeric hash value for a Diff.
     * This function is not used by DMP.
     * @return Hash value.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.operation == null ? 0 : this.operation.hashCode();
        result += prime * (this.text == null ? 0 : this.text.hashCode());
        return result;
    }

    /**
     * Is this Diff equivalent to another Diff?
     * @param obj Another Diff to compare against.
     * @return true or false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Diff other = (Diff) obj;
        if (this.operation != other.operation) {
            return false;
        }
        if (this.text == null) {
            if (other.text != null) {
                return false;
            }
        } else if (!this.text.equals(other.text)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Diff arg0) {
        return this.toString().equals(arg0.toString()) ? 0 : 1;
    }
    
    // Getter and setter

    public Operation getOperation() {
        return this.operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}
