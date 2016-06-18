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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.i18n.I18n;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.util.ConfigurationUtil;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextField;

/**
 * Manager to upload files to the host.
 */
@SuppressWarnings("serial")
public class ManagerUpload extends ManagerAbstractList {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ManagerUpload.class);

    /**
     * Build the manager panel.
     */
    public ManagerUpload() {
        this.setLayout(new BorderLayout());

        this.setDefaultText(I18n.UPLOAD_RUN_BUTTON);

        List<String> pathsList = new ArrayList<>();
        try {
            InputStream in = ManagerUpload.class.getResourceAsStream("/com/jsql/view/swing/resources/list/upload.txt");
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

        final JTextField shellURL = new JPopupTextField(I18n.UPLOAD_URL_LABEL).getProxy();
        String urlTooltip = I18n.UPLOAD_URL_TOOLTIP;
        
        shellURL.setToolTipText(urlTooltip);
        shellURL.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 0, 0, HelperGUI.COMPONENT_BORDER),
                        BorderFactory.createMatteBorder(1, 1, 0, 1, HelperGUI.DEFAULT_BACKGROUND)),
                        HelperGUI.BLU_ROUND_BORDER));

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, HelperGUI.COMPONENT_BORDER), 
                BorderFactory.createEmptyBorder(1, 0, 1, 1)));

        this.run = new JButton(I18n.UPLOAD_DIALOG_TEXT, new ImageIcon(ManagerUpload.class.getResource("/com/jsql/view/swing/resources/images/add.png")));
        this.run.setToolTipText(I18n.UPLOAD_RUN_BUTTON_TOOLTIP);
        this.run.setEnabled(false);
        
        this.run.setBorder(HelperGUI.BLU_ROUND_BORDER);
        
        this.run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (ManagerUpload.this.listPaths.getSelectedValuesList().isEmpty()) {
                    LOGGER.warn("Select at least one directory");
                    return;
                }

                final JFileChooser filechooser = new JFileChooser(ConfigurationUtil.prefPathFile);
                filechooser.setDialogTitle(I18n.UPLOAD_DIALOG_TEXT);
                
                int returnVal = filechooser.showOpenDialog(MediatorGUI.jFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    for (final Object path: ManagerUpload.this.listPaths.getSelectedValuesList()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File file = filechooser.getSelectedFile();
                                try {
                                    ManagerUpload.this.loader.setVisible(true);
                                    RessourceAccess.uploadFile(path.toString(), shellURL.getText(), file);
                                } catch (PreparationException | StoppableException e) {
                                    LOGGER.warn("Can't upload file " + file.getName() + " to " + path, e);
                                }
                            }
                        }, "upload").start();
                    }
                }
            }
        });

        this.privilege = new JLabel(I18n.PRIVILEGE_LABEL, HelperGUI.SQUARE_GREY, SwingConstants.LEFT);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, HelperGUI.DEFAULT_BACKGROUND));
        this.privilege.setToolTipText(I18n.PRIVILEGE_TOOLTIP);

        this.loader.setVisible(false);

        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);

        southPanel.add(shellURL);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }
}
