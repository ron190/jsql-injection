/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.scrollpane.LightScrollPane;

/**
 * Manager to read a file from the host.
 */
@SuppressWarnings("serial")
public class ManagerFile extends ManagerAbstractList {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ManagerFile.class);

    /**
     * Create the manager panel to read a file.
     */
    public ManagerFile() {
        this.setLayout(new BorderLayout());
        this.setDefaultText(I18n.valueByKey("FILE_RUN_BUTTON"));
        
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

        this.add(new LightScrollPane(1, 1, 0, 0, listFile), BorderLayout.CENTER);

        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        run = new JButton(defaultText, new ImageIcon(ManagerFile.class.getResource("/com/jsql/view/swing/resources/images/icons/fileSearch.png")));

        run.setToolTipText(I18n.valueByKey("FILE_RUN_BUTTON_TOOLTIP"));
        run.setEnabled(false);
        
        run.setContentAreaFilled(false);
        run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        run.setBackground(new Color(200, 221, 242));
        
        run.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (run.isEnabled()) {
                    run.setContentAreaFilled(true);
                    run.setBorder(HelperUi.BLU_ROUND_BORDER);
                }
            }

            @Override public void mouseExited(MouseEvent e) {
                if (run.isEnabled()) {
                    run.setContentAreaFilled(false);
                    run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                }
            }
        });
        
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (listFile.getSelectedValuesList().isEmpty()) {
                    LOGGER.warn("Select one or more file");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (run.getText().equals(defaultText)) {
                            
                            run.setText("Stop");
                            try {
                                MediatorGui.tabManagers().shellManager.clearSelection();
                                MediatorGui.tabManagers().sqlShellManager.clearSelection();
                                loader.setVisible(true);
                                RessourceAccess.readFile(listFile.getSelectedValuesList());
                            } catch (StoppedByUserException e) {
                                LOGGER.warn("Reading File stopped by user", e);
                            } catch (InjectionFailureException e) {
                                LOGGER.warn("Reading File failed", e);
                            } catch (InterruptedException e) {
                                LOGGER.warn("Interruption while waiting for Reading File termination", e);
                                Thread.currentThread().interrupt();
                            }

                        } else {
                            RessourceAccess.isSearchFileStopped = true;
                            run.setEnabled(false);
                        }
                    }
                }, "getFile").start();
            }
        });

        privilege = new JLabel(I18n.valueByKey("PRIVILEGE_LABEL"), HelperUi.SQUARE_GREY, SwingConstants.LEFT);
        I18n.addComponentForKey("PRIVILEGE_LABEL", privilege);
        privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, HelperUi.DEFAULT_BACKGROUND));
        privilege.setToolTipText(I18n.valueByKey("PRIVILEGE_TOOLTIP"));

        loader.setVisible(false);

        lastLine.add(privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(run);
        
        this.add(lastLine, BorderLayout.SOUTH);
    }
}
