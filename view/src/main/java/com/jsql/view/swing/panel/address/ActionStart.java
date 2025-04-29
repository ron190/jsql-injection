package com.jsql.view.swing.panel.address;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionStart implements ActionListener {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    protected final PanelAddressBar panelAddressBar;
    
    public ActionStart(PanelAddressBar panelAddressBar) {
        this.panelAddressBar = panelAddressBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // No injection running
        if (this.panelAddressBar.getPanelTrailingAddress().getButtonStart().getState() == StateButton.STARTABLE) {
            this.startInjection();
        } else if (this.panelAddressBar.getPanelTrailingAddress().getButtonStart().getState() == StateButton.STOPPABLE) {
            this.stopInjection();  // Injection currently running, stop the process
        }
    }
    
    protected void startInjection() {
        int option = JOptionPane.OK_OPTION;
        if (MediatorHelper.model().shouldErasePreviousInjection()) {  // Ask the user confirmation if injection already built
            // Fix #93469: IllegalArgumentException on showConfirmDialog()
            // Fix #33930: ClassCastException on showConfirmDialog()
            // Implementation by sun.awt.image
            try {
                option = JOptionPane.showConfirmDialog(
                    MediatorHelper.frame(),
                    I18nUtil.valueByKey("DIALOG_NEW_INJECTION_TEXT"),
                    I18nUtil.valueByKey("DIALOG_NEW_INJECTION_TITLE"),
                    JOptionPane.OK_CANCEL_OPTION
                );
            } catch (IllegalArgumentException | ClassCastException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }

        if (option == JOptionPane.OK_OPTION) {  // Then start injection
            this.panelAddressBar.getPanelTrailingAddress().getButtonStart().setToolTipText(I18nUtil.valueByKey("BUTTON_STOP_TOOLTIP"));
            this.panelAddressBar.getPanelTrailingAddress().getButtonStart().setInjectionRunning();
            this.panelAddressBar.getPanelTrailingAddress().getLoader().setVisible(true);

            MediatorHelper.frame().resetInterface();  // Erase everything in the view from a previous injection

            MediatorHelper.model().getMediatorUtils().getParameterUtil().controlInput(
                this.panelAddressBar.getTextFieldAddress().getText().trim(),
                this.panelAddressBar.getTextFieldRequest().getText().trim(),
                this.panelAddressBar.getTextFieldHeader().getText().trim(),
                this.panelAddressBar.getMethodInjection(),
                this.panelAddressBar.getTypeRequest(),
                false
            );
        }
    }
    
    private void stopInjection() {
        this.panelAddressBar.getPanelTrailingAddress().getLoader().setVisible(false);
        this.panelAddressBar.getPanelTrailingAddress().getButtonStart().setInjectionStopping();
        MediatorHelper.model().setIsStoppedByUser(true);
    }
}