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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextField;

/**
 * Manager for uploading PHP webshell to the host and send system commands.
 */
@SuppressWarnings("serial")
public abstract class ManagerAbstractShell extends ManagerAbstractList {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ManagerAbstractShell.class);

    final JTextField urlShell = new JPopupTextField(I18n.valueByKey("SHELL_URL_LABEL")).getProxy();
    
    /**
     * Build the manager panel.
     */
    public ManagerAbstractShell() {
        this.setLayout(new BorderLayout());

        this.setDefaultText(I18n.valueByKey("SHELL_RUN_BUTTON"));
        
        List<String> pathsList = new ArrayList<>();
        try {
            InputStream in = ManagerAbstractShell.class.getResourceAsStream("/com/jsql/view/swing/resources/list/shell.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                pathsList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        this.listPaths = new DnDList(pathsList);
        this.add(new LightScrollPane(1, 1, 0, 0, this.listPaths), BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        String urlTooltip = I18n.valueByKey("SHELL_URL_TOOLTIP");
        
        this.urlShell.setToolTipText(urlTooltip);
        this.urlShell.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COMPONENT_BORDER),
                    BorderFactory.createMatteBorder(1, 1, 0, 1, HelperUi.DEFAULT_BACKGROUND)
                ),
                HelperUi.BLU_BORDER
            )
        );

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperUi.COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        this.run = new JButton(
            I18n.valueByKey("SHELL_RUN_BUTTON"), 
            new ImageIcon(ManagerAbstractShell.class.getResource("/com/jsql/view/swing/resources/images/icons/shellSearch.png"))
        );
        I18n.addComponentForKey("SHELL_RUN_BUTTON", this.run);
        this.run.setToolTipText(I18n.valueByKey("SHELL_RUN_BUTTON_TOOLTIP"));
        this.run.setEnabled(false);

        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new MouseAdapter() {
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

        this.run.addActionListener(new ActionRunShell());

        this.privilege = new JLabel(I18n.valueByKey("PRIVILEGE_LABEL"), HelperUi.SQUARE_GREY, SwingConstants.LEFT);
        I18n.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, HelperUi.DEFAULT_BACKGROUND));
        this.privilege.setToolTipText(I18n.valueByKey("PRIVILEGE_TOOLTIP"));

        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalStrut(5));
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);

        southPanel.add(this.urlShell);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }
    
    abstract void action(String pathShell, String urlShell) throws InjectionFailureException, StoppedByUserException;
    
    private class ActionRunShell implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (ManagerAbstractShell.this.listPaths.getSelectedValuesList().isEmpty()) {
                LOGGER.warn("Select one or more directory");
                return;
            }

            if (!"".equals(urlShell.getText())) {
                try {
                    new URL(urlShell.getText());
                } catch (MalformedURLException e) {
                    LOGGER.warn("URL is malformed: no protocol", e);
                    return;
                }
            }

            for (final Object pathShell: ManagerAbstractShell.this.listPaths.getSelectedValuesList()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ManagerAbstractShell.this.action(pathShell.toString(), urlShell.getText());
                        } catch (InjectionFailureException | StoppedByUserException e) {
                            LOGGER.warn("Problem writing into " + pathShell, e);
                        }
                    }
                }, "getShell").start();
            }
        }
    }
}
