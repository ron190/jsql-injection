package com.jsql.model.tamper;

public abstract class ISelectEndComment {
    String comment;
    
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
