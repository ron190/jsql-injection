package com.jsql.view.swing.sql.text;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextPane;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class JTextPaneLexer extends JTextPane implements JTextPaneObjectMethod {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    protected transient AttributeSetterForVendor attributeSetter = null;
    
    public void setAttribute() {
        
        try {
            if (this.attributeSetter != null && !"".equals(this.getText())) {
                this.attributeSetter.getMethod().invoke(this.attributeSetter.getAttribute(), this.getText());
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
            LOGGER.debug(e1, e1);
        }
    }
}