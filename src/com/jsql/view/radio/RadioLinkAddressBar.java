package com.jsql.view.radio;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.jsql.view.GUIMediator;

/**
 * Radio label for HTTP method (GET, POST, etc).
 */
@SuppressWarnings("serial")
public class RadioLinkAddressBar extends RadioLink {
    /**
     * Group of radio label for address bar.
     */
    private static List<JLabel> groupAddressBar = new ArrayList<JLabel>();

    /**
     * A default radio label.
     * @param string Text for radio label
     */
    public RadioLinkAddressBar(String string) {
        super(string);
        init();
    }

    /**
     * A default radio label selected.
     * @param string Text for radio label
     * @param isSelected True if radio label should be selected
     */
    public RadioLinkAddressBar(String string, boolean isSelected) {
        super(string, isSelected);
        init();
    }

    /**
     * Add radio to the radio group.
     */
    private void init() {
        RadioLinkAddressBar.groupAddressBar.add(this);
    }

    @Override
    void action() {
        GUIMediator.top().setSendMethod(RadioLinkAddressBar.this.getText());
    }

    @Override
    List<JLabel> getGroup() {
        return RadioLinkAddressBar.groupAddressBar;
    }
}
