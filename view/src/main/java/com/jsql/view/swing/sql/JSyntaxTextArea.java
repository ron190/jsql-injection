package com.jsql.view.swing.sql;

import com.jsql.view.swing.text.SyntaxTextArea;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class JSyntaxTextArea extends SyntaxTextArea {
    
    private final transient Consumer<String> consumerSetter;
    private final transient Supplier<String> supplierGetter;
    
    public JSyntaxTextArea(Consumer<String> consumer, Supplier<String> supplier) {
        this.consumerSetter = consumer;
        this.supplierGetter = supplier;
    }

    public void setAttribute() {
        this.consumerSetter.accept(this.getText());
    }

    public Supplier<String> getSupplierGetter() {
        return this.supplierGetter;
    }
}