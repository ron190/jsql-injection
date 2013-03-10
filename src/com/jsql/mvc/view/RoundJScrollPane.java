package com.jsql.mvc.view;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.jsql.mvc.view.component.RoundedCornerBorder;

public class RoundJScrollPane extends JScrollPane {
    public RoundJScrollPane(Component c){
        super(c);
        
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setViewportBorder(new RoundedCornerBorder(2,2,true));
    }
}
