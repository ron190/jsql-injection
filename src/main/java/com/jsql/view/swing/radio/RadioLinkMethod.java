package com.jsql.view.swing.radio;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.jsql.view.swing.MediatorGui;

/**
 * Radio label for HTTP method (GET, POST, etc).
 */
@SuppressWarnings("serial")
public class RadioLinkMethod extends AbstractRadioLink {
    /**
     * Group of radio label for address bar.
     */
    private static List<JLabel> groupMethod = new ArrayList<>();

    /**
     * A default radio label.
     * @param string Text for radio label
     */
    public RadioLinkMethod(String string) {
        super(string);
        init();
    }

    /**
     * A default radio label selected.
     * @param string Text for radio label
     * @param isSelected True if radio label should be selected
     */
    public RadioLinkMethod(String string, boolean isSelected) {
        super(string, isSelected);
        init();
    }

    /**
     * Add radio to the radio group.
     */
    private void init() {
        this.addMouseListener(new RadioMethodMouseAdapter());
        RadioLinkMethod.groupMethod.add(this);
    }

    @Override
    void action() {
        MediatorGui.panelAddress().setSendMethod(RadioLinkMethod.this.getText());
    }

    @Override
    List<JLabel> getGroup() {
        return RadioLinkMethod.groupMethod;
    }
}
