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
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.MenuBarCoder;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.manager.util.UserAgent;
import com.jsql.view.swing.manager.util.UserAgentType;
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
    public ManagerAdminPage() {
        super("swing/list/admin-page.txt");

        this.defaultText = "ADMIN_PAGE_RUN_BUTTON_LABEL";
        this.run = new JButtonStateful(this.defaultText);
        I18nView.addComponentForKey("ADMIN_PAGE_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18n.valueByKey("ADMIN_PAGE_RUN_BUTTON_TOOLTIP"));
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));

        this.run.addActionListener(actionEvent -> {
            if (this.listFile.getSelectedValuesList().isEmpty()) {
                LOGGER.warn("Select at least one admin page in the list");
                return;
            }
            
            String[] urlQuery = new String[]{MediatorGui.panelAddressBar().getTextFieldAddress().getText()};
            if (!urlQuery[0].isEmpty() && !urlQuery[0].matches("(?i)^https?://.*")) {
                if (!urlQuery[0].matches("(?i)^\\w+://.*")) {
                    LOGGER.info("Undefined URL protocol, forcing to [http://]");
                    urlQuery[0] = "http://"+ urlQuery[0];
                } else {
                    LOGGER.info("Unknown URL protocol");
                    return;
                }
            }
            
            new Thread(() -> {
                if (ManagerAdminPage.this.run.getState() == StateButton.STARTABLE) {
                    if ("".equals(urlQuery[0])) {
                        LOGGER.warn("Enter the main address");
                    } else {
                        LOGGER.trace("Checking admin page(s)...");
                        ManagerAdminPage.this.run.setText(I18nView.valueByKey("ADMIN_PAGE_RUN_BUTTON_STOP"));
                        ManagerAdminPage.this.run.setState(StateButton.STOPPABLE);
                        ManagerAdminPage.this.loader.setVisible(true);
                        
                        try {
                            MediatorModel.model().getResourceAccess().createAdminPages(
                                urlQuery[0],
                                this.listFile.getSelectedValuesList()
                            );
                        } catch (InterruptedException ex) {
                            LOGGER.error("Interruption while waiting for Opening Admin Page termination", ex);
                            Thread.currentThread().interrupt();
                        }
                    }
                } else if (this.run.getState() == StateButton.STOPPABLE) {
                    MediatorModel.model().getResourceAccess().setSearchAdminStopped(true);
                    ManagerAdminPage.this.run.setEnabled(false);
                    ManagerAdminPage.this.run.setState(StateButton.STOPPING);
                }
            }, "ThreadAdminPage").start();
        });

        this.loader.setVisible(false);
        
        JMenu m = MenuBarCoder.createMenu("<User-Agent default>");
        MenuBarCoder comboMenubar = new MenuBarCoder(m);
        comboMenubar.setOpaque(false);
        comboMenubar.setBorder(null);
        
        ButtonGroup groupVendor = new ButtonGroup();
        
        JRadioButtonMenuItem r = new JRadioButtonMenuItem("<User-Agent default>", true);
        r.addActionListener(actionEvent ->
            m.setText("<User-Agent default>")
        );
        r.setToolTipText("Java/"+ System.getProperty("java.version"));
        groupVendor.add(r);
        m.add(r);
        
        for (Entry<UserAgentType, List<UserAgent>> e: UserAgent.getList().entrySet()) {
            JMenu mm = new JMenu(e.getKey().getLabel());
            m.add(mm);
            for (UserAgent u: e.getValue()) {
                JRadioButtonMenuItem rr = new JRadioButtonMenuItem(u.getLabel());
                rr.addActionListener(actionEvent ->
                    m.setText(u.getLabel())
                );
                rr.setToolTipText(u.getNameUserAgent());
                groupVendor.add(rr);
                mm.add(rr);
            }
        }
        
        this.lastLine.setLayout(new BorderLayout());
        this.lastLine.setPreferredSize(new Dimension(0, 26));
        
        this.lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        p.add(Box.createHorizontalGlue());
        p.add(this.loader);
        p.add(Box.createRigidArea(new Dimension(5, 0)));
        p.add(this.run);
        
        this.lastLine.add(p, BorderLayout.LINE_END);
        
        this.add(this.lastLine, BorderLayout.SOUTH);
    }
    
}
