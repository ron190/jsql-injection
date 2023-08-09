package com.jsql.view.swing.sql.text;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JTextPaneLexer extends JTextPane implements JTextPaneObjectMethod {
    
    private final transient Consumer<String> consumerSetter;
    private final transient Supplier<String> supplierGetter;
    
    public JTextPaneLexer(
        Consumer<String> consumer,
        Supplier<String> supplier
    ) {
        this.consumerSetter = consumer;
        this.supplierGetter = supplier;
    }

    public void setAttribute() {
        
        if (StringUtils.isNotEmpty(this.getText())) {
            this.consumerSetter.accept(this.getText());
        }
    }

    public Supplier<String> getSupplierGetter() {
        return this.supplierGetter;
    }
}