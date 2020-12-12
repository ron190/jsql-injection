package com.jsql.view.swing.sql.text;

import java.util.function.Consumer;

import javax.swing.JTextPane;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class JTextPaneLexer extends JTextPane implements JTextPaneObjectMethod {
    
    private Consumer<String> consumerSetter;
    
    public JTextPaneLexer(Consumer<String> object) {
        this.consumerSetter = object;
    }

    public void setAttribute() {
        
        if (StringUtils.isNotEmpty(this.getText())) {
            consumerSetter.accept(this.getText());
        }
    }
}