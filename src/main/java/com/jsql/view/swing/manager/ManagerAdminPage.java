/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
@SuppressWarnings("serial")
public class ManagerAdminPage extends AbstractManagerList {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Create admin page finder.
     */
    public ManagerAdminPage(String nameFile) {
        super(nameFile);

        this.defaultText = I18n.valueByKey("ADMIN_PAGE_RUN_BUTTON_LABEL");
        this.run = new JButtonStateful(this.defaultText);
        I18n.addComponentForKey("ADMIN_PAGE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18n.valueByKey("ADMIN_PAGE_RUN_BUTTON_TOOLTIP"));
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));

        this.run.addActionListener(actionEvent -> {
            if (this.listFile.getSelectedValuesList().isEmpty()) {
                LOGGER.warn("Select admin page(s) to find");
                return;
            }
            
            new Thread(() -> {
                if (ManagerAdminPage.this.run.getState() == StateButton.STARTABLE) {
                    if ("".equals(MediatorGui.panelAddressBar().getTextFieldAddress().getText())) {
                        LOGGER.warn("Enter the main address");
                    } else {
                        ManagerAdminPage.this.run.setText("Stop");
                        ManagerAdminPage.this.run.setState(StateButton.STOPPABLE);
                        ManagerAdminPage.this.loader.setVisible(true);
                        
                        try {
                            RessourceAccess.createAdminPages(
                                MediatorGui.panelAddressBar().getTextFieldAddress().getText(),
                                this.listFile.getSelectedValuesList()
                            );
                        } catch (InterruptedException ex) {
                            LOGGER.error("Interruption while waiting for Opening Admin Page termination", ex);
                            Thread.currentThread().interrupt();
                        }
                    }
                } else if (this.run.getState() == StateButton.STOPPABLE) {
                    RessourceAccess.setSearchAdminStopped(true);
                    ManagerAdminPage.this.run.setEnabled(false);
                    ManagerAdminPage.this.run.setState(StateButton.STOPPING);
                }
            }, "ThreadAdminPage").start();
        });

        this.loader.setVisible(false);

        this.lastLine.add(Box.createHorizontalGlue());
        this.lastLine.add(this.loader);
        this.lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        this.lastLine.add(this.run);
        
        this.add(this.lastLine, BorderLayout.SOUTH);
    }
    
}
