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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.accessible.CallableHttpHead;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
@SuppressWarnings("serial")
public class ManagerAdminPage extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    /**
     * Create admin page finder.
     */
    public ManagerAdminPage() {
        
        super("swing/list/admin-page.txt");

        this.initializeRunButton();
        
        this.listFile.setName("listManagerAdminPage");
        this.run.setName("runManagerAdminPage");
        
        this.lastLine.setLayout(new BorderLayout());
        this.lastLine.setPreferredSize(new Dimension(0, 26));
        
        var panelRunButton = new JPanel();
        panelRunButton.setLayout(new BoxLayout(panelRunButton, BoxLayout.X_AXIS));
        
        panelRunButton.add(Box.createHorizontalGlue());
        panelRunButton.add(this.loader);
        panelRunButton.add(Box.createRigidArea(new Dimension(5, 0)));
        panelRunButton.add(this.run);
        
        this.lastLine.add(panelRunButton, BorderLayout.LINE_END);
        
        this.add(this.lastLine, BorderLayout.SOUTH);
    }

    private void initializeRunButton() {
        
        this.defaultText = "ADMIN_PAGE_RUN_BUTTON_LABEL";
        this.run = new JButtonStateful(this.defaultText);
        I18nViewUtil.addComponentForKey("ADMIN_PAGE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("ADMIN_PAGE_RUN_BUTTON_TOOLTIP"));
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));

        this.run.addActionListener(actionEvent -> this.runSearch());

        this.loader.setVisible(false);
    }

    private void runSearch() {
        
        if (this.listFile.getSelectedValuesList().isEmpty()) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Select at least one admin page in the list");
            return;
        }
        
        String urlAddressBar = MediatorHelper.panelAddressBar().getTextFieldAddress().getText();
        
        if (!urlAddressBar.isEmpty() && !urlAddressBar.matches("(?i)^https?://.*")) {
            
            if (!urlAddressBar.matches("(?i)^\\w+://.*")) {
                
                LOGGER.log(LogLevel.CONSOLE_INFORM, () -> I18nUtil.valueByKey("LOG_ADMIN_NO_PROTOCOL"));
                urlAddressBar = "http://"+ urlAddressBar;
                
            } else {
                
                LOGGER.log(LogLevel.CONSOLE_INFORM, () -> I18nUtil.valueByKey("LOG_ADMIN_UNKNOWN_PROTOCOL"));
                return;
            }
        }
        
        String urlFinal = urlAddressBar;
        
        new Thread(() -> this.searchAdminPages(urlFinal), "ThreadAdminPage").start();
    }

    private void searchAdminPages(String urlAddressBar) {
        
        if (ManagerAdminPage.this.run.getState() == StateButton.STARTABLE) {
            
            if (StringUtils.isEmpty(urlAddressBar)) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "URL in the address bar is missing");
                
            } else {
                
                LOGGER.log(LogLevel.CONSOLE_DEFAULT, "Checking admin page(s)...");
                ManagerAdminPage.this.run.setText(I18nViewUtil.valueByKey("ADMIN_PAGE_RUN_BUTTON_STOP"));
                ManagerAdminPage.this.run.setState(StateButton.STOPPABLE);
                ManagerAdminPage.this.loader.setVisible(true);
                
                try {
                    this.createAdminPages(
                        urlAddressBar,
                        this.listFile.getSelectedValuesList()
                    );
                    
                } catch (InterruptedException e) {
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                    Thread.currentThread().interrupt();
                }
            }
            
        } else if (this.run.getState() == StateButton.STOPPABLE) {
            
            MediatorHelper.model().getResourceAccess().setSearchAdminStopped(true);
            ManagerAdminPage.this.run.setEnabled(false);
            ManagerAdminPage.this.run.setState(StateButton.STOPPING);
        }
    }
    
    /**
     * Check if every page in the list responds 200 Success.
     * @param urlInjection
     * @param pageNames List of admin pages to test
     * @throws InterruptedException
     */
    public void createAdminPages(String urlInjection, List<ItemList> pageNames) throws InterruptedException {
         
        var matcher = Pattern.compile("^((https?://)?[^/]*)(.*)").matcher(urlInjection);
        matcher.find();
        String urlProtocol = matcher.group(1);
        String urlWithoutProtocol = matcher.group(3);
        
        List<String> folderSplits = new ArrayList<>();
        
        // Hostname only
        if (urlWithoutProtocol.isEmpty() || !Pattern.matches("^/.*", urlWithoutProtocol)) {
            urlWithoutProtocol = "/dummy";
        }
        
        String[] splits = urlWithoutProtocol.split("/", -1);
        String[] folderNames = Arrays.copyOf(splits, splits.length - 1);
        for (String folderName: Arrays.asList(folderNames)) {
            
            folderSplits.add(folderName +"/");
        }
        
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryCallable("CallableGetAdminPage"));
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        var urlPart = new StringBuilder();
        
        for (String segment: folderSplits) {
            
            urlPart.append(segment);
            
            for (ItemList pageName: pageNames) {
                
                taskCompletionService.submit(
                    new CallableHttpHead(
                        urlProtocol + urlPart.toString() + pageName.toString(),
                        MediatorHelper.model(),
                        "check:page"
                    )
                );
            }
        }

        var resourceAccess = MediatorHelper.model().getResourceAccess();
        
        var nbAdminPagesFound = 0;
        int submittedTasks = folderSplits.size() * pageNames.size();
        int tasksHandled;
        
        for (
            tasksHandled = 0
            ; tasksHandled < submittedTasks && !resourceAccess.isSearchAdminStopped()
            ; tasksHandled++
        ) {
            nbAdminPagesFound = resourceAccess.callAdminPage(taskCompletionService, nbAdminPagesFound);
        }

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);

        resourceAccess.setSearchAdminStopped(false);

        resourceAccess.logSearchAdminPage(nbAdminPagesFound, submittedTasks, tasksHandled);

        var request = new Request();
        request.setMessage(Interaction.END_ADMIN_SEARCH);
        MediatorHelper.model().sendToViews(request);
    }
}
