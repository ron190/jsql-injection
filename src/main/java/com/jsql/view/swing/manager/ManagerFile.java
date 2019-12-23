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
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * Manager to read a file from the host.
 */
@SuppressWarnings("serial")
public class ManagerFile extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Create the manager panel to read a file.
     */
    public ManagerFile() {
        super("swing/list/file.txt");
        
        this.defaultText = "FILE_RUN_BUTTON_LABEL";
        this.run = new JButtonStateful(this.defaultText);
        I18nView.addComponentForKey("FILE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18n.valueByKey("FILE_RUN_BUTTON_TOOLTIP"));
        
        this.run.setEnabled(false);
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(actionEvent -> {
            if (this.listFile.getSelectedValuesList().isEmpty()) {
                LOGGER.warn("Select at least one file to read in the list");
                return;
            }

            new Thread(() -> {
                if (ManagerFile.this.run.getState() == StateButton.STARTABLE) {
                    ManagerFile.this.run.setText(I18nView.valueByKey("FILE_RUN_BUTTON_STOP"));
                    ManagerFile.this.run.setState(StateButton.STOPPABLE);
                    ManagerFile.this.loader.setVisible(true);
                    
                    MediatorGui.managerWebshell().clearSelection();
                    MediatorGui.managerSqlshell().clearSelection();
                    try {
                        MediatorModel.model().getResourceAccess().readFile(this.listFile.getSelectedValuesList());
                    } catch (InterruptedException ex) {
                        LOGGER.warn("Interruption while waiting for Reading File termination", ex);
                        Thread.currentThread().interrupt();
                    } catch (Exception ex) {
                        LOGGER.warn(ex, ex);
                    }

                } else {
                    MediatorModel.model().getResourceAccess().stopSearchingFile();
                    ManagerFile.this.run.setEnabled(false);
                    ManagerFile.this.run.setState(StateButton.STOPPING);
                }
            }, "ThreadReadFile").start();
        });

        this.privilege = new JLabel(I18n.valueByKey("PRIVILEGE_LABEL"), HelperUi.ICON_SQUARE_GREY, SwingConstants.LEFT);
        I18nView.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, HelperUi.COLOR_DEFAULT_BACKGROUND));
        this.privilege.setToolTipText(I18n.valueByKey("PRIVILEGE_TOOLTIP"));

        this.loader.setVisible(false);

        this.lastLine.add(this.privilege);
        this.lastLine.add(Box.createHorizontalGlue());
        this.lastLine.add(this.loader);
        this.lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        this.lastLine.add(this.run);
        
        this.add(this.lastLine, BorderLayout.SOUTH);
    }
    
}
