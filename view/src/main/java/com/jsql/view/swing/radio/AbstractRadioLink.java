package com.jsql.view.swing.radio;

import java.awt.Cursor;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import com.jsql.view.swing.util.UiUtil;

/**
 * A label to mimic a radiobox contained in a group.
 * Display as underlined if label is selected.
 */
@SuppressWarnings("serial")
public abstract class AbstractRadioLink extends JLabel {
    
    /**
     * Build a radio label.
     * @param string Text for label
     * @param isSelected Is the radio selected by default?
     */
    protected AbstractRadioLink(String string, boolean isSelected) {
        
        this(string);
        
        if (isSelected) {
            
            this.setUnderlined();
        }
    }

    /**
     * Build a radio label.
     * @param string Text for label
     */
    protected AbstractRadioLink(String string) {
        super(string);
    }

    /**
     * An action run when radio is checked by user.
     */
    public abstract void action();
    
    /**
     * Group of radio components, either the radio for HTTP method or the one for injection strategy.
     * @return
     */
    public abstract List<JLabel> getGroup();

    /**
     * Radio is selectable/hoverable if it is not already selected (bold).
     * @return True if radio is not already selected
     */
    protected boolean isActivable() {
        
        return !AbstractRadioLink.this.getFont().getAttributes().containsValue(TextAttribute.WEIGHT_BOLD);
    }

    /**
     * Change font of radio label to underline.
     */
    public final void setUnderlined() {
        
        var font = this.getFont();
        
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        
        this.setFont(font.deriveFont(attributes));
    }

    /**
     * Change font of radio label to default.
     */
    public void removeFont() {
        
        var font = this.getFont();
        Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
        
        this.setFont(font.deriveFont(attributes));
    }
    
    public void setSelected() {
        
        for (JLabel label: this.getGroup()) {
            
            if (this != label) {
                
                label.setFont(UiUtil.FONT_NON_MONO);
                
            } else {
                
                this.action();
            }
        }

        this.setUnderlined();

        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
