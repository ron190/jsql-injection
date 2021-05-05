package com.jsql.view.swing.panel.address;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.util.MediatorHelper;

public class ActionStart implements ActionListener {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    protected PanelAddressBar panelAddressBar;
    
    public ActionStart(PanelAddressBar panelAddressBar) {
        
        this.panelAddressBar = panelAddressBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // No injection running
        if (this.panelAddressBar.getAddressMenuBar().getButtonInUrl().getState() == StateButton.STARTABLE) {
            
            this.startInjection();

        } else if (this.panelAddressBar.getAddressMenuBar().getButtonInUrl().getState() == StateButton.STOPPABLE) {
            
            // Injection currently running, stop the process
            this.stopInjection();
        }
    }
    
    protected void startInjection() {
        
        int option = JOptionPane.OK_OPTION;
        
        // Ask the user confirmation if injection already built
        if (MediatorHelper.model().shouldErasePreviousInjection()) {
            
            // Fix #93469: IllegalArgumentException on showConfirmDialog()
            // Fix #33930: ClassCastException on showConfirmDialog()
            // Implementation by sun.awt.image
            try {
                option = JOptionPane.showConfirmDialog(
                    null,
                    I18nUtil.valueByKey("DIALOG_NEW_INJECTION_TEXT"),
                    I18nUtil.valueByKey("DIALOG_NEW_INJECTION_TITLE"),
                    JOptionPane.OK_CANCEL_OPTION
                );
                
            } catch (IllegalArgumentException| ClassCastException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }

        // Then start injection
        if (option == JOptionPane.OK_OPTION) {
            
            this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setToolTipText(I18nUtil.valueByKey("BUTTON_STOP_TOOLTIP"));
            this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setInjectionRunning();
            this.panelAddressBar.getAddressMenuBar().getLoader().setVisible(true);

            // Erase everything in the view from a previous injection
            var requests = new Request();
            requests.setMessage(Interaction.RESET_INTERFACE);
            MediatorHelper.model().sendToViews(requests);

            MediatorHelper.model().getMediatorUtils().getParameterUtil().controlInput(
                this.panelAddressBar.getTextFieldAddress().getText().trim(),
                this.panelAddressBar.getTextFieldRequest().getText().trim(),
                this.panelAddressBar.getTextFieldHeader().getText().trim(),
                this.panelAddressBar.getMethodInjection(),
                this.panelAddressBar.getRequestPanel().getTypeRequest(),
                false
            );
        }
    }
    
    private void stopInjection() {
        
        this.panelAddressBar.getAddressMenuBar().getLoader().setVisible(false);
        this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setInjectionStopping();
        this.panelAddressBar.getAddressMenuBar().getButtonInUrl().setToolTipText(I18nUtil.valueByKey("BUTTON_STOPPING_TOOLTIP"));
        
        MediatorHelper.model().setIsStoppedByUser(true);
    }
}