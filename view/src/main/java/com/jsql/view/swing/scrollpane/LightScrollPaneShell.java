package com.jsql.view.swing.scrollpane;

import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;

public class LightScrollPaneShell extends LightScrollPane {
    
    public LightScrollPaneShell(JComponent component) {
        
        super(component);
        
        this.colorThumb = UiUtil.COLOR_FOCUS_GAINED;
        this.scrollBarAlphaRollover = 175;
        this.scrollBarAlpha = 100;
        
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
    }
}