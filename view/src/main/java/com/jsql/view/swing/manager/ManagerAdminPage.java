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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
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
            
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Select at least one admin page in the list");
            return;
        }
        
        String urlAddressBar = MediatorHelper.panelAddressBar().getTextFieldAddress().getText();
        
        if (!urlAddressBar.isEmpty() && !urlAddressBar.matches("(?i)^https?://.*")) {
            
            if (!urlAddressBar.matches("(?i)^\\w+://.*")) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, () -> I18nUtil.valueByKey("LOG_ADMIN_NO_PROTOCOL"));
                urlAddressBar = "http://"+ urlAddressBar;
                
            } else {
                
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, () -> I18nUtil.valueByKey("LOG_ADMIN_UNKNOWN_PROTOCOL"));
                return;
            }
        }
        
        String urlFinal = urlAddressBar;
        
        new Thread(() -> this.searchAdminPages(urlFinal), "ThreadAdminPage").start();
    }

    private void searchAdminPages(String urlAddressBar) {
        
        if (ManagerAdminPage.this.run.getState() == StateButton.STARTABLE) {
            
            if (StringUtils.isEmpty(urlAddressBar)) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "URL in the address bar is missing");
                
            } else {
                
                LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Checking admin page(s)...");
                ManagerAdminPage.this.run.setText(I18nViewUtil.valueByKey("ADMIN_PAGE_RUN_BUTTON_STOP"));
                ManagerAdminPage.this.run.setState(StateButton.STOPPABLE);
                ManagerAdminPage.this.loader.setVisible(true);
                
                try {
                    MediatorHelper.model().getResourceAccess().createAdminPages(
                        urlAddressBar,
                        this.listFile.getSelectedValuesList().stream().map(ItemList::toString).collect(Collectors.toList())
                    );
                    
                } catch (InterruptedException e) {
                    
                    LOGGER.log(LogLevelUtil.IGNORE, e, e);
                    Thread.currentThread().interrupt();
                }
            }
            
        } else if (this.run.getState() == StateButton.STOPPABLE) {
            
            MediatorHelper.model().getResourceAccess().setSearchAdminStopped(true);
            ManagerAdminPage.this.run.setEnabled(false);
            ManagerAdminPage.this.run.setState(StateButton.STOPPING);
        }
    }
}
