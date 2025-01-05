package com.jsql.view.swing.sql;

import com.jsql.view.swing.popupmenu.JPopupMenuComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class JSyntaxTextArea extends RSyntaxTextArea {
    
    private final transient Consumer<String> consumerSetter;
    private final transient Supplier<String> supplierGetter;
    
    public JSyntaxTextArea(Consumer<String> consumer, Supplier<String> supplier) {
        this.consumerSetter = consumer;
        this.supplierGetter = supplier;

        this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        this.setMarkOccurrences(true);
        this.setPopupMenu(new JPopupMenuComponent(this));
    }

    public void setAttribute() {
        this.consumerSetter.accept(this.getText());
    }

    public Supplier<String> getSupplierGetter() {
        return this.supplierGetter;
    }
}