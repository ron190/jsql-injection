package com.jsql.view.swing.radio;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.jsql.model.MediatorModel;
import com.jsql.model.strategy.Strategy;
import com.jsql.view.swing.HelperGui;

/**
 * Radio label used to change injection strategy (normal, blind, etc).
 */
@SuppressWarnings("serial")
public class RadioLinkStrategy extends AbstractRadioLink {
    /**
     * Group of radio label for status bar.
     */
    private static List<JLabel> group = new ArrayList<>();
    
    private Strategy strategy;

    /**
     * A default radio label.
     * @param string Text for radio label
     */
    public RadioLinkStrategy(String string, Strategy strategy) {
        super(string);
        this.strategy = strategy;
        
        this.addMouseListener(new RadioStrategyMouseAdapter());
        RadioLinkStrategy.group.add(this);
        
        this.setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    void action() {
        MediatorModel.model().setStrategy(this.strategy);
    }

    @Override
    List<JLabel> getGroup() {
        return RadioLinkStrategy.group;
    }

    @Override
    protected boolean isActivable() {
        return 
            RadioLinkStrategy.this.getIcon() == HelperGui.TICK && 
            !RadioLinkStrategy.this.getFont().getAttributes().containsValue(TextAttribute.WEIGHT_BOLD);
    }
}
