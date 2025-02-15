/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
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
        this.buildRunButton("FILE_RUN_BUTTON_LABEL", "FILE_RUN_BUTTON_TOOLTIP");
        this.run.setEnabled(false);
        this.run.addActionListener(new ActionFile());
        this.buildPrivilege();
        this.add(this.lastLine, BorderLayout.SOUTH);
    }

    private class ActionFile implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (ManagerFile.this.listPaths.getSelectedValuesList().isEmpty()) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Select in the list at least one file to read");
                return;
            }
            if (!Arrays.asList(
                MediatorHelper.model().getMediatorVendor().getH2(),
                MediatorHelper.model().getMediatorVendor().getHsqldb(),
                MediatorHelper.model().getMediatorVendor().getMysql(),
                MediatorHelper.model().getMediatorVendor().getPostgres()
            ).contains(MediatorHelper.model().getMediatorVendor().getVendor())) {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_ERROR,
                    "Read file for [{}] not implemented, share a working example to GitHub to speed up release",
                    MediatorHelper.model().getMediatorVendor().getVendor()
                );
                return;
            }
            new SwingWorker<>() {
                @Override
                protected Object doInBackground() {
                    Thread.currentThread().setName("SwingWorkerManagerFile");
                    if (ManagerFile.this.run.getState() == StateButton.STARTABLE) {
                        ManagerFile.this.run.setText(I18nViewUtil.valueByKey("FILE_RUN_BUTTON_STOP"));
                        ManagerFile.this.run.setState(StateButton.STOPPABLE);
                        ManagerFile.this.horizontalGlue.setVisible(false);
                        ManagerFile.this.progressBar.setVisible(true);
                        try {
                            List<String> filePaths = ManagerFile.this.listPaths.getSelectedValuesList().stream().map(ItemList::toString).collect(Collectors.toList());
                            MediatorHelper.model().getResourceAccess().readFile(filePaths);
                        } catch (InterruptedException e) {
                            LOGGER.log(LogLevelUtil.IGNORE, e, e);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, e, e);
                        }
                        ManagerFile.this.endProcess();
                    } else {
                        MediatorHelper.model().getResourceAccess().stopSearchFile();
                        ManagerFile.this.run.setEnabled(false);
                        ManagerFile.this.run.setState(StateButton.STOPPING);
                    }
                    return null;
                }
            }.doInBackground();
        }
    }
}