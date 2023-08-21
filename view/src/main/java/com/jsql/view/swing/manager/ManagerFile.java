/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager to read a file from the host.
 */
public class ManagerFile extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Create the manager panel to read a file.
     */
    public ManagerFile() {
        
        super("swing/list/file.txt");
        
        this.initializeRunButton();

        this.privilege = new JLabel(I18nUtil.valueByKey("PRIVILEGE_LABEL"), UiUtil.ICON_SQUARE_GREY, SwingConstants.LEFT);
        I18nViewUtil.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UiUtil.COLOR_DEFAULT_BACKGROUND));
        this.privilege.setToolTipText(I18nUtil.valueByKey("PRIVILEGE_TOOLTIP"));

        this.loader.setVisible(false);

        this.lastLine.add(this.privilege);
        this.lastLine.add(Box.createHorizontalGlue());
        this.lastLine.add(this.loader);
        this.lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        this.lastLine.add(this.run);
        
        this.add(this.lastLine, BorderLayout.SOUTH);
    }

    private void initializeRunButton() {
        
        this.defaultText = "FILE_RUN_BUTTON_LABEL";
        this.run = new JButtonStateful(this.defaultText);
        I18nViewUtil.addComponentForKey("FILE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("FILE_RUN_BUTTON_TOOLTIP"));
        
        this.run.setEnabled(false);
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(actionEvent -> {
            
            if (this.listFile.getSelectedValuesList().isEmpty()) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Select at least one file to read in the list");
                
                return;
            }

            new Thread(
                () -> {
                
                    if (ManagerFile.this.run.getState() == StateButton.STARTABLE) {
                        
                        ManagerFile.this.run.setText(I18nViewUtil.valueByKey("FILE_RUN_BUTTON_STOP"));
                        ManagerFile.this.run.setState(StateButton.STOPPABLE);
                        ManagerFile.this.loader.setVisible(true);
                        
                        MediatorHelper.managerWebshell().clearSelection();
                        MediatorHelper.managerSqlshell().clearSelection();
                        
                        try {
                            List<String> filePaths = this.listFile.getSelectedValuesList().stream().map(ItemList::toString).collect(Collectors.toList());
                            MediatorHelper.model().getResourceAccess().readFile(filePaths);
                            
                        } catch (InterruptedException e) {
                            
                            LOGGER.log(LogLevelUtil.IGNORE, e, e);
                            Thread.currentThread().interrupt();
                            
                        } catch (Exception e) {
                            
                            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, e, e);
                        }
                        
                    } else {
                        
                        MediatorHelper.model().getResourceAccess().stopSearchingFile();
                        ManagerFile.this.run.setEnabled(false);
                        ManagerFile.this.run.setState(StateButton.STOPPING);
                    }
                },
                "ThreadReadFile"
            ).start();
        });
    }
}
