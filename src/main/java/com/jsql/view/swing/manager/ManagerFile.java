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
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * Manager to read a file from the host.
 */
@SuppressWarnings("serial")
public class ManagerFile extends AbstractManagerList {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Create the manager panel to read a file.
     */
    public ManagerFile() {
        this.setLayout(new BorderLayout());
        this.setDefaultText(I18n.valueByKey("FILE_RUN_BUTTON_LABEL"));
        
        List<String> pathList = new ArrayList<>();
        try {
            InputStream in = ManagerFile.class.getResourceAsStream("/com/jsql/view/swing/resources/list/file.txt");
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
        
        this.run = new JButtonStateful(defaultText);

        this.run.setToolTipText(I18n.valueByKey("FILE_RUN_BUTTON_TOOLTIP"));
        this.run.setEnabled(false);
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (listFile.getSelectedValuesList().isEmpty()) {
                    LOGGER.warn("Select file(s) to read");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        
                        if (ManagerFile.this.run.getState() == StateButton.STARTABLE) {
                            ManagerFile.this.run.setText("Stop");
                            ManagerFile.this.run.setState(StateButton.STOPPABLE);
                            ManagerFile.this.loader.setVisible(true);
                            
                            MediatorGui.managerWebshell().clearSelection();
                            MediatorGui.managerSqlshell().clearSelection();
                            try {
                                RessourceAccess.readFile(listFile.getSelectedValuesList());
                            } catch (JSqlException | ExecutionException e) {
                                LOGGER.warn(e, e);
                            } catch (InterruptedException e) {
                                LOGGER.warn("Interruption while waiting for Reading File termination", e);
                                Thread.currentThread().interrupt();
                            }

                        } else {
                            RessourceAccess.stopSearchingFile();
                            ManagerFile.this.run.setEnabled(false);
                            ManagerFile.this.run.setState(StateButton.STOPPING);
                        }
                        
                    }
                }, "ThreadReadFile").start();
            }
        });

        this.privilege = new JLabel(I18n.valueByKey("PRIVILEGE_LABEL"), HelperUi.ICON_SQUARE_GREY, SwingConstants.LEFT);
        I18n.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, HelperUi.COLOR_DEFAULT_BACKGROUND));
        this.privilege.setToolTipText(I18n.valueByKey("PRIVILEGE_TOOLTIP"));

        this.loader.setVisible(false);

        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);
        
        this.add(lastLine, BorderLayout.SOUTH);
    }
    
}
