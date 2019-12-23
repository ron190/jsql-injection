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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * Manager to upload files to the host.
 */
@SuppressWarnings("serial")
public class ManagerUpload extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Build the manager panel.
     */
    public ManagerUpload() {
        this.setLayout(new BorderLayout());

        this.defaultText = "UPLOAD_RUN_BUTTON_LABEL";

        List<ItemList> pathsList = new ArrayList<>();
        try {
            InputStream in = HelperUi.class.getClassLoader().getResourceAsStream(HelperUi.PATH_WEB_FOLDERS);
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                pathsList.add(new ItemList(line));
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        this.listPaths = new DnDList(pathsList);
        this.getListPaths().setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        this.add(new LightScrollPane(1, 0, 0, 0, this.getListPaths()), BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        final JTextField shellURL = new JPopupTextField(I18n.valueByKey("UPLOAD_URL_LABEL")).getProxy();
        String urlTooltip = I18n.valueByKey("UPLOAD_URL_TOOLTIP");
        
        shellURL.setToolTipText(urlTooltip);
        shellURL.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER),
                    BorderFactory.createMatteBorder(1, 1, 0, 1, HelperUi.COLOR_DEFAULT_BACKGROUND)
                ),
                HelperUi.BORDER_BLU
            )
        );

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );

        this.run = new JButtonStateful("UPLOAD_RUN_BUTTON_LABEL");
        I18nView.addComponentForKey("UPLOAD_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18n.valueByKey("UPLOAD_RUN_BUTTON_TOOLTIP"));
        this.run.setEnabled(false);
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(actionEvent -> {
            if (ManagerUpload.this.getListPaths().getSelectedValuesList().isEmpty()) {
                LOGGER.warn("Select directory(ies) to upload a file into");
                return;
            }

            final JFileChooser filechooser = new JFileChooser(MediatorModel.model().getMediatorUtils().getPreferencesUtil().getPathFile());
            filechooser.setDialogTitle(I18n.valueByKey("UPLOAD_DIALOG_TEXT"));
            
            // Fix #2402: NullPointerException on showOpenDialog()
            // Fix #40547: ClassCastException on showOpenDialog()
            try {
                int returnVal = filechooser.showOpenDialog(MediatorGui.frame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    for (final Object path: ManagerUpload.this.getListPaths().getSelectedValuesList()) {
                        
                        new Thread(() -> {
                            File file = filechooser.getSelectedFile();
                            try {
                                ManagerUpload.this.loader.setVisible(true);
                                MediatorModel.model().getResourceAccess().uploadFile(path.toString(), shellURL.getText(), file);
                            } catch (JSqlException ex) {
                                LOGGER.warn("Payload creation error: "+ ex, ex);
                            } catch (IOException ex) {
                                LOGGER.warn("Posting file failed: "+ ex, ex);
                            }
                        }, "ThreadUpload").start();
                        
                    }
                }
            } catch (NullPointerException | ClassCastException ex) {
                LOGGER.error(ex, ex);
            }
        });

        this.privilege = new JLabel(I18n.valueByKey("PRIVILEGE_LABEL"), HelperUi.ICON_SQUARE_GREY, SwingConstants.LEFT);
        I18nView.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, HelperUi.COLOR_DEFAULT_BACKGROUND));
        this.privilege.setToolTipText(I18n.valueByKey("PRIVILEGE_TOOLTIP"));

        this.loader.setVisible(false);

        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);

        southPanel.add(shellURL);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }
    
}
