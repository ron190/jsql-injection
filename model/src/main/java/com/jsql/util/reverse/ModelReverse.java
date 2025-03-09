package com.jsql.util.reverse;

public class ModelReverse {
    String name;
    String command;

    public ModelReverse(String name, String command) {
        this.name = name;
        this.command = command;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}