package com.jsql.view.swing.radio;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Radio label for HTTP method (GET, POST, etc).
 */
@SuppressWarnings("serial")
public class RadioLinkMethod extends AbstractRadioLink {
    
    /**
     * Group of radio label for address bar.
     */
    private static List<JLabel> groupMethod = new ArrayList<>();

    private AbstractMethodInjection methodInjection = null;
    
    /**
     * A default radio label.
     * @param string Text for radio label
     * @param methodInjection
     */
    public RadioLinkMethod(String string, AbstractMethodInjection methodInjection) {
        
        super(string);
        
        this.init();
        this.methodInjection = methodInjection;
    }

    /**
     * A default radio label selected.
     * @param string Text for radio label
     * @param isSelected True if radio label should be selected
     * @param method
     */
    public RadioLinkMethod(String string, boolean isSelected, AbstractMethodInjection method) {
        
        super(string, isSelected);
        
        this.init();
        this.methodInjection = method;
    }

    /**
     * Add radio to the radio group.
     */
    private void init() {
        
        this.addMouseListener(new RadioMethodMouseAdapter());
        RadioLinkMethod.groupMethod.add(this);
    }

    @Override
    public void action() {
        
        MediatorHelper.panelAddressBar().setMethodInjection(this.methodInjection);
    }

    @Override
    public List<JLabel> getGroup() {
        
        return RadioLinkMethod.groupMethod;
    }
}
