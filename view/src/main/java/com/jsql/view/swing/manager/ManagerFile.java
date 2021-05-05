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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.accessible.CallableFile;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Manager to read a file from the host.
 */
@SuppressWarnings("serial")
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
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Select at least one file to read in the list");
                
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
                            this.readFile(this.listFile.getSelectedValuesList());
                            
                        } catch (InterruptedException e) {
                            
                            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                            Thread.currentThread().interrupt();
                            
                        } catch (Exception e) {
                            
                            LOGGER.log(LogLevel.CONSOLE_ERROR, e, e);
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
    
    /**
     * Attempt to read files in parallel by their path from the website using injection.
     * Reading file needs a FILE right on the server.
     * The user can interrupt the process at any time.
     * @param pathsFiles List of file paths to read
     * @throws JSqlException when an error occurs during injection
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException if the computation threw an exception
     */
    public void readFile(List<ItemList> pathsFiles) throws JSqlException, InterruptedException, ExecutionException {
        
        if (!MediatorHelper.model().getResourceAccess().isReadingAllowed()) {
            
            return;
        }

        var countFileFound = 0;
        
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryCallable("CallableReadFile"));
        CompletionService<CallableFile> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);

        for (ItemList pathFile: pathsFiles) {
            
            var callableFile = new CallableFile(pathFile.toString(), MediatorHelper.model());
            taskCompletionService.submit(callableFile);
            
            MediatorHelper.model().getResourceAccess().getCallablesReadFile().add(callableFile);
        }

        List<String> duplicate = new ArrayList<>();
        int submittedTasks = pathsFiles.size();
        int tasksHandled;
        
        for (
            tasksHandled = 0
            ; tasksHandled < submittedTasks && !MediatorHelper.model().getResourceAccess().isSearchFileStopped()
            ; tasksHandled++
        ) {
            
            var currentCallable = taskCompletionService.take().get();
            
            if (StringUtils.isNotEmpty(currentCallable.getSourceFile())) {
                
                var name = currentCallable
                    .getPathFile()
                    .substring(
                        currentCallable.getPathFile().lastIndexOf('/') + 1,
                        currentCallable.getPathFile().length()
                    );
                String content = currentCallable.getSourceFile();
                String path = currentCallable.getPathFile();

                var request = new Request();
                request.setMessage(Interaction.CREATE_FILE_TAB);
                request.setParameters(name, content, path);
                MediatorHelper.model().sendToViews(request);

                if (!duplicate.contains(path.replace(name, StringUtils.EMPTY))) {
                    
                    LOGGER.log(
                        LogLevel.CONSOLE_INFORM,
                        "Shell might be possible in folder {}",
                        () -> path.replace(name, StringUtils.EMPTY)
                    );
                }
                
                duplicate.add(path.replace(name, StringUtils.EMPTY));

                countFileFound++;
            }
        }
        
        // Force ongoing suspendables to stop immediately
        for (CallableFile callableReadFile: MediatorHelper.model().getResourceAccess().getCallablesReadFile()) {
            
            callableReadFile.getSuspendableReadFile().stop();
        }
        
        MediatorHelper.model().getResourceAccess().getCallablesReadFile().clear();

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        
        MediatorHelper.model().getResourceAccess().setSearchFileStopped(false);
        
        var result = String
            .format(
                "Found %s file%s%s on %s files checked",
                countFileFound,
                countFileFound > 1 ? 's' : StringUtils.EMPTY,
                tasksHandled != submittedTasks ? " of "+ tasksHandled +" processed " : StringUtils.EMPTY,
                submittedTasks
            );
        
        if (countFileFound > 0) {
            
            LOGGER.log(LogLevel.CONSOLE_SUCCESS, result);
            
        } else {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, result);
        }
        
        var request = new Request();
        request.setMessage(Interaction.END_FILE_SEARCH);
        MediatorHelper.model().sendToViews(request);
    }
}
