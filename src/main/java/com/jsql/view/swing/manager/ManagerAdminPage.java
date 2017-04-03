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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.scrollpane.LightScrollPane;
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
        this.setLayout(new BorderLayout());
        this.setDefaultText(I18n.valueByKey("ADMIN_PAGE_RUN_BUTTON_LABEL"));
        
        List<String> pathList = new ArrayList<>();
        try {
            InputStream in = ManagerAdminPage.class.getResourceAsStream("/com/jsql/view/swing/resources/list/admin-page.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                pathList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        final DnDList listFile = new DnDList(pathList);

        listFile.setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        this.add(new LightScrollPane(1, 0, 0, 0, listFile), BorderLayout.CENTER);

        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));

        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER), 
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        this.run = new JButtonStateful(this.defaultText);
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));

        this.run.setToolTipText(I18n.valueByKey("ADMIN_PAGE_RUN_BUTTON_TOOLTIP"));

        this.run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                if (listFile.getSelectedValuesList().isEmpty()) {
                    LOGGER.warn("Select admin page(s) to find");
                    return;
                }
                
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        
                        if (ManagerAdminPage.this.run.getState() == StateButton.STARTABLE) {
                            if ("".equals(MediatorGui.panelAddressBar().textFieldAddress.getText())) {
                                LOGGER.warn("Enter the main address");
                            } else {
                                ManagerAdminPage.this.run.setText("Stop");
                                ManagerAdminPage.this.run.setState(StateButton.STOPPABLE);
                                ManagerAdminPage.this.loader.setVisible(true);
                                
                                try {
                                    RessourceAccess.createAdminPages(
                                        MediatorGui.panelAddressBar().textFieldAddress.getText(), 
                                        listFile.getSelectedValuesList()
                                    );
                                } catch (InterruptedException e) {
                                    LOGGER.error("Interruption while waiting for Opening Admin Page termination", e);
                                    Thread.currentThread().interrupt();
                                }
                            }
                        } else if (run.getState() == StateButton.STOPPABLE) {
                            RessourceAccess.setSearchAdminStopped(true);
                            ManagerAdminPage.this.run.setEnabled(false);
                            ManagerAdminPage.this.run.setState(StateButton.STOPPING);
                        }
                        
                    }
                }, "ThreadAdminPage").start();
            }
        });

        this.loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);
        
        this.add(lastLine, BorderLayout.SOUTH);
    }
    
}
