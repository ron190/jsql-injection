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

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.exception.JSqlException;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Manager to upload files to the host.
 */
@SuppressWarnings("serial")
public class ManagerUpload extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Build the manager panel.
     */
    public ManagerUpload() {
        
        this.setLayout(new BorderLayout());

        this.defaultText = "UPLOAD_RUN_BUTTON_LABEL";

        List<ItemList> pathsList = new ArrayList<>();
        
        try (
            var inputStream = UiUtil.class.getClassLoader().getResourceAsStream(UiUtil.PATH_WEB_FOLDERS);
            var inputStreamReader = new InputStreamReader(inputStream);
            var reader = new BufferedReader(inputStreamReader)
        ) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                
                pathsList.add(new ItemList(line));
            }
            
        } catch (IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        this.listPaths = new DnDList(pathsList);
        
        this.getListPaths().setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        this.add(new LightScrollPane(0, 0, 0, 0, this.getListPaths()), BorderLayout.CENTER);
        
        var southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        final JTextField shellURL = new JPopupTextField(I18nUtil.valueByKey("UPLOAD_URL_LABEL")).getProxy();
        String urlTooltip = I18nUtil.valueByKey("UPLOAD_URL_TOOLTIP");
        
        shellURL.setToolTipText(urlTooltip);
        shellURL.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER),
                    BorderFactory.createMatteBorder(1, 1, 0, 1, UiUtil.COLOR_DEFAULT_BACKGROUND)
                ),
                UiUtil.BORDER_BLU
            )
        );

        var lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );

        this.initializeRunButton(shellURL);

        this.privilege = new JLabel(I18nUtil.valueByKey("PRIVILEGE_LABEL"), UiUtil.ICON_SQUARE_GREY, SwingConstants.LEFT);
        I18nViewUtil.addComponentForKey("PRIVILEGE_LABEL", this.privilege);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, UiUtil.COLOR_DEFAULT_BACKGROUND));
        this.privilege.setToolTipText(I18nUtil.valueByKey("PRIVILEGE_TOOLTIP"));

        this.loader.setVisible(false);

        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);

        southPanel.add(shellURL);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    private void initializeRunButton(final JTextField shellURL) {
        
        this.run = new JButtonStateful(this.defaultText);
        I18nViewUtil.addComponentForKey(this.defaultText, this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("UPLOAD_RUN_BUTTON_TOOLTIP"));
        this.run.setEnabled(false);
        
        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(actionEvent -> this.initializeRunAction(shellURL));
    }

    private void initializeRunAction(final JTextField shellURL) {
        
        if (ManagerUpload.this.getListPaths().getSelectedValuesList().isEmpty()) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Select directory(ies) to upload a file into");
            
            return;
        }

        final var filechooser = new JFileChooser(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getPathFile());
        filechooser.setDialogTitle(I18nUtil.valueByKey("UPLOAD_DIALOG_TEXT"));
        
        // Fix #2402: NullPointerException on showOpenDialog()
        // Fix #40547: ClassCastException on showOpenDialog()
        try {
            int returnVal = filechooser.showOpenDialog(MediatorHelper.frame());
            
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                
                return;
            }
                
            this.uploadFiles(shellURL, filechooser);
            
        } catch (NullPointerException | ClassCastException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }

    private void uploadFiles(final JTextField shellURL, final JFileChooser filechooser) {
        
        for (final Object path: ManagerUpload.this.getListPaths().getSelectedValuesList()) {
            
            new Thread(
                () -> {
                
                    var file = filechooser.getSelectedFile();
                    
                    try {
                        ManagerUpload.this.loader.setVisible(true);
                        MediatorHelper.model().getResourceAccess().uploadFile(path.toString(), shellURL.getText(), file);
                        
                    } catch (JSqlException e) {
                        
                        LOGGER.log(
                            LogLevel.CONSOLE_ERROR,
                            String.format("Payload creation error: %s", e.getMessage())
                        );
                        
                    } catch (IOException e) {
                        
                        LOGGER.log(
                            LogLevel.CONSOLE_ERROR,
                            String.format("Posting file failed: %s", e.getMessage()),
                            e
                        );
                        
                    } catch (InterruptedException e) {
                        
                        LOGGER.log(
                            LogLevel.CONSOLE_ERROR,
                            String.format("Posting file failed: %s", e.getMessage()),
                            e
                        );
                        Thread.currentThread().interrupt();
                    }
                },
                "ThreadUpload"
            ).start();
        }
    }
}
