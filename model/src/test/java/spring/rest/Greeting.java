package spring.rest;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class Greeting {

    private String content;

    public Greeting(String content) {
        this.content = content;
    }

    @JsonRawValue
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}