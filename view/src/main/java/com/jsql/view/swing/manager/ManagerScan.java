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

import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.scan.ScanListTerminal;
import com.jsql.view.swing.list.*;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
public class ManagerScan extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static final String NAME = "listManagerScan";

    /**
     * Create admin page finder.
     */
    public ManagerScan() {
        super("swing/list/scan-page.json");

        this.listPaths.setTransferHandler(null);
        this.listPaths.setTransferHandler(new ListTransfertHandlerScan());
        this.listPaths.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ItemList itemList = (ItemList) value;
                if (itemList.isVulnerable()) {
                    label.setIcon(UiUtil.TICK_GREEN.icon);
                }
                return label;
            }
        });
        
        this.listPaths.setName(ManagerScan.NAME);

        JPanel lastLine = this.getLastLinePanel(this.listPaths);
        this.add(lastLine, BorderLayout.SOUTH);
        
        this.listPaths.addListSelectionListener(e -> {
            if (this.listPaths.getSelectedValue() == null) {
                return;
            }
            
            var beanInjection = ((ItemListScan) this.listPaths.getSelectedValue()).getBeanInjection();
            MediatorHelper.panelAddressBar().getTextFieldAddress().setText(beanInjection.getUrl());
            MediatorHelper.panelAddressBar().getTextFieldHeader().setText(beanInjection.getHeader());
            MediatorHelper.panelAddressBar().getTextFieldRequest().setText(beanInjection.getRequest());
            
            String requestType = beanInjection.getRequestType();
            if (requestType != null && !requestType.isEmpty()) {
                MediatorHelper.panelAddressBar().getAtomicRadioMethod().setText(requestType);
            } else {
                MediatorHelper.panelAddressBar().getAtomicRadioMethod().setText("GET");
            }
            
            AbstractMethodInjection method = beanInjection.getMethodInstance();
            if (method == MediatorHelper.model().getMediatorMethod().getHeader()) {
                MediatorHelper.panelAddressBar().getAtomicRadioHeader().setSelected(true);
            } else if (method == MediatorHelper.model().getMediatorMethod().getRequest()) {
                MediatorHelper.panelAddressBar().getAtomicRadioMethod().setSelected(true);
            } else {
                MediatorHelper.panelAddressBar().getAtomicRadioRequest().setSelected(true);
            }
        });
    }

    @Override
    public void buildList(String nameFile) {
        var jsonScan = new StringBuilder();
        try (
            var inputStream = UiUtil.class.getClassLoader().getResourceAsStream(nameFile);
            var inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
            var reader = new BufferedReader(inputStreamReader)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonScan.append(line);
            }
            var jsonArrayScan = new JSONArray(jsonScan.toString());
            for (var i = 0 ; i < jsonArrayScan.length() ; i++) {
                this.itemsList.add(new ItemListScan(jsonArrayScan.getJSONObject(i)));
            }
            this.listPaths = new DnDListScan(this.itemsList);
        } catch (JSONException | IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    private JPanel getLastLinePanel(final DnDList dndListScan) {
        var lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));

        this.initializeRunButton(dndListScan);

        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.horizontalGlue);
        lastLine.add(this.progressBar);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);
        return lastLine;
    }

    private void initializeRunButton(final DnDList dndListScan) {
        this.defaultText = "SCAN_RUN_BUTTON_LABEL";
        this.run = new JButtonStateful(this.defaultText);
        I18nViewUtil.addComponentForKey("SCAN_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("SCAN_RUN_BUTTON_TOOLTIP"));
        this.run.addActionListener(actionEvent -> {
            if (dndListScan.getSelectedValuesList().isEmpty()) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Select URL(s) to scan");
                return;
            }
            new Thread(
                () -> {
                    if (this.run.getState() == StateButton.STARTABLE) {
                        this.run.setText(I18nViewUtil.valueByKey("SCAN_RUN_BUTTON_STOP"));
                        this.run.setState(StateButton.STOPPABLE);
                        this.progressBar.setVisible(true);
                        this.horizontalGlue.setVisible(false);
                        DefaultListModel<ItemList> listModel = (DefaultListModel<ItemList>) dndListScan.getModel();
                        for (var i = 0 ; i < listModel.getSize() ; i++) {
                            listModel.get(i).reset();
                        }
                        this.scan(dndListScan.getSelectedValuesList());
                    } else {
                        MediatorHelper.model().getResourceAccess().setScanStopped(true);
                        MediatorHelper.model().setIsStoppedByUser(true);
                        this.run.setEnabled(false);
                        this.run.setState(StateButton.STOPPING);
                    }
                },
                "ThreadScan"
            ).start();
        });
    }
    
    /**
     * Start fast scan of URLs in sequence and display result.
     * Unplug any existing view and plug a console-like view in order to
     * respond appropriately to GUI message with simple text result instead of
     * build complex graphical components during the multi website injections.
     * At the end of the scan it plugs again the normal view.
     * @param urlsItemList contains a list of String URL
     */
    public void scan(List<ItemList> urlsItemList) {
        MediatorHelper.frame().resetInterface();  // Erase everything in the view from a previous injection
        
        // wait for ending of ongoing interaction between two injections
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }

        // Display result only in console
        var requestUnsubscribe = new Request();
        requestUnsubscribe.setMessage(Interaction.UNSUBSCRIBE);
        MediatorHelper.model().sendToViews(requestUnsubscribe);
        MediatorHelper.model().subscribe(new ScanListTerminal());
        
        MediatorHelper.model().setIsScanning(true);
        MediatorHelper.model().getResourceAccess().setScanStopped(false);
        
        for (ItemList urlItemList: urlsItemList) {
            if (  // detect interrupt by user to end intermediate scan
                MediatorHelper.model().isStoppedByUser()
                || MediatorHelper.model().getResourceAccess().isScanStopped()
            ) {
                break;
            }

            var urlItemListScan = (ItemListScan) urlItemList;
            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Scanning {}", urlItemListScan.getBeanInjection().getUrl());

            Optional<Vendor> optionalVendor = MediatorHelper.model().getMediatorVendor().getVendors()
                .stream()
                .filter(vendor -> vendor.toString().equalsIgnoreCase(urlItemListScan.getBeanInjection().getVendor()))
                .findAny();

            MediatorHelper.model().getMediatorVendor().setVendorByUser(
                optionalVendor.orElse(MediatorHelper.model().getMediatorVendor().getAuto())
            );
            MediatorHelper.model().getMediatorUtils().getParameterUtil().controlInput(
                urlItemListScan.getBeanInjection().getUrl(),
                urlItemListScan.getBeanInjection().getRequest(),
                urlItemListScan.getBeanInjection().getHeader(),
                urlItemListScan.getBeanInjection().getMethodInstance(),
                urlItemListScan.getBeanInjection().getRequestType(),
                true
            );
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
            }
        }
        
        // Get back the normal view
        MediatorHelper.model().sendToViews(requestUnsubscribe);
        MediatorHelper.model().subscribe(MediatorHelper.frame().getSubscriber());
        
        MediatorHelper.model().setIsScanning(false);
        MediatorHelper.model().setIsStoppedByUser(false);
        MediatorHelper.model().getResourceAccess().setScanStopped(false);

        this.endProcess();
    }
}
