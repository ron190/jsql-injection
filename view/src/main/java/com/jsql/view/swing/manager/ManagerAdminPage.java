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
import java.util.Map.Entry;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.model.accessible.CallableHttpHead;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.MenuBarCoder;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.manager.util.UserAgent;
import com.jsql.view.swing.manager.util.UserAgentType;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

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
    public ManagerAdminPage() {
        
        super("swing/list/admin-page.txt");

        this.initializeRunButton();
        
        this.initializeMenuUserAgent();
        
        this.lastLine.setLayout(new BorderLayout());
        this.lastLine.setPreferredSize(new Dimension(0, 26));
        
        this.lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, UiUtil.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        JPanel panelRunButton = new JPanel();
        panelRunButton.setLayout(new BoxLayout(panelRunButton, BoxLayout.X_AXIS));
        panelRunButton.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        panelRunButton.add(Box.createHorizontalGlue());
        panelRunButton.add(this.loader);
        panelRunButton.add(Box.createRigidArea(new Dimension(5, 0)));
        panelRunButton.add(this.run);
        
        this.lastLine.add(panelRunButton, BorderLayout.LINE_END);
        
        this.add(this.lastLine, BorderLayout.SOUTH);
    }

    private void initializeMenuUserAgent() {
        
        // TODO user agent
        JMenu menuUserAgent = MenuBarCoder.createMenu("<User-Agent default>");
        MenuBarCoder comboMenubar = new MenuBarCoder(menuUserAgent);
        comboMenubar.setOpaque(false);
        comboMenubar.setBorder(null);
        
        ButtonGroup groupUserAgent = new ButtonGroup();
        
        JRadioButtonMenuItem radioButtonMenuItemDefaultUserAgent = new JRadioButtonMenuItem("<User-Agent default>", true);
        radioButtonMenuItemDefaultUserAgent.addActionListener(actionEvent ->
            menuUserAgent.setText("<User-Agent default>")
        );
        radioButtonMenuItemDefaultUserAgent.setToolTipText("Java/"+ System.getProperty("java.version"));
        groupUserAgent.add(radioButtonMenuItemDefaultUserAgent);
        menuUserAgent.add(radioButtonMenuItemDefaultUserAgent);
        
        for (Entry<UserAgentType, List<UserAgent>> entryUserAgent: UserAgent.getList().entrySet()) {
            
            JMenu menuAgentType = new JMenu(entryUserAgent.getKey().getLabel());
            menuUserAgent.add(menuAgentType);
            
            for (UserAgent userAgent: entryUserAgent.getValue()) {
                
                JRadioButtonMenuItem radioButtonMenuItemUserAgent = new JRadioButtonMenuItem(userAgent.getLabel());
                radioButtonMenuItemUserAgent.addActionListener(actionEvent ->
                    menuUserAgent.setText(userAgent.getLabel())
                );
                
                radioButtonMenuItemUserAgent.setToolTipText(userAgent.getNameUserAgent());
                groupUserAgent.add(radioButtonMenuItemUserAgent);
                menuAgentType.add(radioButtonMenuItemUserAgent);
            }
        }
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
            
            LOGGER.warn("Select at least one admin page in the list");
            return;
        }
        
        String urlAddressBar = MediatorHelper.panelAddressBar().getTextFieldAddress().getText();
        
        if (!urlAddressBar.isEmpty() && !urlAddressBar.matches("(?i)^https?://.*")) {
            
            if (!urlAddressBar.matches("(?i)^\\w+://.*")) {
                
                LOGGER.info(I18nUtil.valueByKey("LOG_ADMIN_NO_PROTOCOL"));
                urlAddressBar = "http://"+ urlAddressBar;
                
            } else {
                
                LOGGER.info(I18nUtil.valueByKey("LOG_ADMIN_UNKNOWN_PROTOCOL"));
                return;
            }
        }
        
        String urlFinal = urlAddressBar;
        
        new Thread(() -> this.searchAdminPages(urlFinal), "ThreadAdminPage").start();
    }

    private void searchAdminPages(String urlAddressBar) {
        
        if (ManagerAdminPage.this.run.getState() == StateButton.STARTABLE) {
            
            if (StringUtils.isEmpty(urlAddressBar)) {
                
                LOGGER.warn("URL in the address bar is missing");
                
            } else {
                
                LOGGER.trace("Checking admin page(s)...");
                ManagerAdminPage.this.run.setText(I18nViewUtil.valueByKey("ADMIN_PAGE_RUN_BUTTON_STOP"));
                ManagerAdminPage.this.run.setState(StateButton.STOPPABLE);
                ManagerAdminPage.this.loader.setVisible(true);
                
                try {
                    this.createAdminPages(
                        urlAddressBar,
                        this.listFile.getSelectedValuesList()
                    );
                    
                } catch (InterruptedException ex) {
                    
                    LOGGER.error("Interruption while waiting for Opening Admin Page termination", ex);
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
        
        String urlWithoutProtocol = urlInjection.replaceAll("^https?://[^/]*", StringUtils.EMPTY);
        String urlProtocol = urlInjection.replace(urlWithoutProtocol, StringUtils.EMPTY);
        String urlWithoutFileName = urlWithoutProtocol.replaceAll("[^/]*$", StringUtils.EMPTY);
        
        List<String> directoryNames = new ArrayList<>();
        
        if (urlWithoutFileName.split("/").length == 0) {
            
            directoryNames.add("/");
        }
        
        for (String directoryName: urlWithoutFileName.split("/")) {
            
            directoryNames.add(directoryName +"/");
        }
        
        ExecutorService taskExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryCallable("CallableGetAdminPage"));
        CompletionService<CallableHttpHead> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
        
        StringBuilder urlPart = new StringBuilder();
        
        for (String segment: directoryNames) {
            
            urlPart.append(segment);
            
            for (ItemList pageName: pageNames) {
                
                taskCompletionService.submit(new CallableHttpHead(urlProtocol + urlPart.toString() + pageName.toString(), MediatorHelper.model()));
            }
        }

        int nbAdminPagesFound = 0;
        int submittedTasks = directoryNames.size() * pageNames.size();
        int tasksHandled;
        
        for (
            tasksHandled = 0;
            tasksHandled < submittedTasks && !MediatorHelper.model().getResourceAccess().isSearchAdminStopped();
            tasksHandled++
        ) {
            nbAdminPagesFound = MediatorHelper.model().getResourceAccess().callAdminPage(taskCompletionService, nbAdminPagesFound);
        }

        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);

        MediatorHelper.model().getResourceAccess().isSearchAdminStopped = false;

        MediatorHelper.model().getResourceAccess().logSearchAdminPage(nbAdminPagesFound, submittedTasks, tasksHandled);

        Request request = new Request();
        request.setMessage(Interaction.END_ADMIN_SEARCH);
        MediatorHelper.model().sendToViews(request);
    }
}
