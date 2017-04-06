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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.ListItem;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
@SuppressWarnings("serial")
public class ManagerScan extends AbstractManagerList {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Create admin page finder.
     */
    public ManagerScan() {
        this.setLayout(new BorderLayout());
        this.setDefaultText(I18n.valueByKey("SCAN_RUN_BUTTON_LABEL"));

        List<String> pathsList = new ArrayList<>();
        try {
            InputStream in = ManagerScan.class.getResourceAsStream("/com/jsql/view/swing/resources/list/scan-page.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                pathsList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        final DnDList listFile = new DnDList(pathsList);
        this.listPaths = listFile;
        this.listPaths.setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
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

        this.run.setToolTipText(I18n.valueByKey("SCAN_RUN_BUTTON_TOOLTIP"));

        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(actionEvent -> {
            if (listFile.getSelectedValuesList().isEmpty()) {
                LOGGER.warn("Select URL(s) to scan");
                return;
            }
            
            new Thread(() -> {
                if (ManagerScan.this.run.getState() == StateButton.STARTABLE) {
                    ManagerScan.this.run.setText("Stop");
                    ManagerScan.this.run.setState(StateButton.STOPPABLE);
                    ManagerScan.this.loader.setVisible(true);
                    
                    DefaultListModel<ListItem> listModel = (DefaultListModel<ListItem>) listFile.getModel();
                    for (int i = 0 ; i < listModel.getSize() ; i++) {
                        listModel.get(i).reset();
                    }
                    
                    RessourceAccess.scanList(listFile.getSelectedValuesList());
                } else {
                    RessourceAccess.setScanStopped(true);
                    MediatorModel.model().setIsStoppedByUser(true);
                    ManagerScan.this.run.setEnabled(false);
                    ManagerScan.this.run.setState(StateButton.STOPPING);
                }
            }, "ThreadScan").start();
        });

        this.loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);
        
        this.add(lastLine, BorderLayout.SOUTH);
    }
    
}
