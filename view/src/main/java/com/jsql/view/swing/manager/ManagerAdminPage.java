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

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        this.buildRunButton("ADMIN_PAGE_RUN_BUTTON_LABEL", "ADMIN_PAGE_RUN_BUTTON_TOOLTIP");
        this.run.setName("runManagerAdminPage");
        this.run.addActionListener(actionEvent -> this.runSearch());
        this.listPaths.setName("listManagerAdminPage");  // no tooltip, too annoying

        this.lastLine.add(this.horizontalGlue);
        this.lastLine.add(this.progressBar);
        this.lastLine.add(this.run);
        this.add(this.lastLine, BorderLayout.SOUTH);
    }

    private void runSearch() {
        if (this.listPaths.getSelectedValuesList().isEmpty()) {
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
        if (this.run.getState() == StateButton.STARTABLE) {
            if (StringUtils.isEmpty(urlAddressBar)) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Missing URL in address bar");
            } else {
                LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, I18nUtil.valueByKey("LOG_CHECKING") +" admin pages...");
                this.run.setText(I18nViewUtil.valueByKey("ADMIN_PAGE_RUN_BUTTON_STOP"));
                this.run.setState(StateButton.STOPPABLE);
                this.progressBar.setVisible(true);
                this.horizontalGlue.setVisible(false);
                MediatorHelper.model().getResourceAccess().createAdminPages(
                    urlAddressBar,
                    this.listPaths.getSelectedValuesList().stream().map(ItemList::toString).collect(Collectors.toList())
                );
                this.endProcess();
            }
        } else if (this.run.getState() == StateButton.STOPPABLE) {
            MediatorHelper.model().getResourceAccess().stopSearchAdmin();
            this.run.setEnabled(false);
            this.run.setState(StateButton.STOPPING);
        }
    }
}
