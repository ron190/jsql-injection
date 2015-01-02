package com.jsql.view.swing.radio;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;

/**
 * Radio label used to change injection strategy (normal, blind, etc).
 */
@SuppressWarnings("serial")
public class RadioLinkStatusbar extends AbstractRadioLink {
    /**
     * Group of radio label for status bar.
     */
    private static List<JLabel> group = new ArrayList<JLabel>();

    /**
     * A default radio label.
     * @param string Text for radio label
     */
    public RadioLinkStatusbar(String string) {
        super(string);
        init();
    }

    /**
     * A default radio label selected.
     * @param string Text for radio label
     * @param isSelected True if radio label should be selected
     */
    public RadioLinkStatusbar(String string, boolean isSelected) {
        super(string, isSelected);
        init();
    }

    /**
     * Add radio to the radio group.
     */
    private void init() {
        RadioLinkStatusbar.group.add(this);
        this.setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    void action() {
        MediatorGUI.model().applyStrategy(RadioLinkStatusbar.this.getText());
    }

    @Override
    List<JLabel> getGroup() {
        return RadioLinkStatusbar.group;
    }

    @Override
    protected boolean isActivable() {
        return RadioLinkStatusbar.this.getIcon() == HelperGUI.TICK
            && !RadioLinkStatusbar.this.getFont().getAttributes().containsValue(TextAttribute.WEIGHT_BOLD);
    }
}
