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
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.scan.ScanListTerminal;
import com.jsql.view.swing.list.BeanInjection;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.DnDListScan;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.list.ItemListScan;
import com.jsql.view.swing.list.ListTransfertHandlerScan;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
@SuppressWarnings("serial")
public class ManagerScan extends AbstractManagerList {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Create admin page finder.
     */
    public ManagerScan() {
        
        this.setLayout(new BorderLayout());

        List<ItemList> itemsList = this.getItemList();
        
        final DnDList dndListScan = new DnDListScan(itemsList);
        
        dndListScan.setName("scan");
        dndListScan.setTransferHandler(null);
        dndListScan.setTransferHandler(new ListTransfertHandlerScan());
        
        this.listPaths = dndListScan;
        this.listPaths.setName("listManagerScan");
        
        this.getListPaths().setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        this.add(new LightScrollPane(0, 0, 0, 0, dndListScan), BorderLayout.CENTER);

        JPanel lastLine = this.getLastLinePanel(dndListScan);
        this.add(lastLine, BorderLayout.SOUTH);
        
        dndListScan.addListSelectionListener(e -> {
            
            if (dndListScan.getSelectedValue() == null) {
                
                return;
            }
            
            var beanInjection = ((ItemListScan) dndListScan.getSelectedValue()).getBeanInjection();
            
            MediatorHelper.panelAddressBar().getTextFieldAddress().setText(beanInjection.getUrl());
            MediatorHelper.panelAddressBar().getTextFieldHeader().setText(beanInjection.getHeader());
            MediatorHelper.panelAddressBar().getTextFieldRequest().setText(beanInjection.getRequest());
            
            String requestType = beanInjection.getRequestType();
            if (requestType != null && !requestType.isEmpty()) {
                
                MediatorHelper.panelAddressBar().getRadioRequest().setText(requestType);
                
            } else {
                
                MediatorHelper.panelAddressBar().getRadioRequest().setText("POST");
            }
            
            AbstractMethodInjection method = beanInjection.getMethodInstance();
            
            if (method == MediatorHelper.model().getMediatorMethod().getHeader()) {
                
                MediatorHelper.panelAddressBar().getRadioHeader().setSelected();
                
            } else if (method == MediatorHelper.model().getMediatorMethod().getRequest()) {
                
                MediatorHelper.panelAddressBar().getRadioRequest().setSelected();
                
            } else {
                
                MediatorHelper.panelAddressBar().getRadioQueryString().setSelected();
            }
        });
    }

    private JPanel getLastLinePanel(final DnDList dndListScan) {
        
        var lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));

        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        this.initializeRunButton(dndListScan);

        this.loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(this.run);
        
        return lastLine;
    }

    private List<ItemList> getItemList() {
        
        var jsonScan = new StringBuilder();
        
        try (
            var inputStream = UiUtil.class.getClassLoader().getResourceAsStream(UiUtil.INPUT_STREAM_PAGES_SCAN);
            var inputStreamReader = new InputStreamReader(inputStream);
            var reader = new BufferedReader(inputStreamReader)
        ) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                
                jsonScan.append(line);
            }
            
        } catch (IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        List<ItemList> itemsList = new ArrayList<>();
        
        try {
            var jsonArrayScan = new JSONArray(jsonScan.toString());
            
            for (var i = 0 ; i < jsonArrayScan.length() ; i++) {
                
                var jsonObjectScan = jsonArrayScan.getJSONObject(i);
                
                var beanInjection = new BeanInjection(
                    jsonObjectScan.getString("url"),
                    jsonObjectScan.optString("request"),
                    jsonObjectScan.optString("header"),
                    jsonObjectScan.optString("method"),
                    jsonObjectScan.optString("vendor"),
                    jsonObjectScan.optString("requestType")
                );
                
                itemsList.add(new ItemListScan(beanInjection));
            }
            
        } catch (JSONException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        return itemsList;
    }

    private void initializeRunButton(final DnDList dndListScan) {
        
        this.defaultText = "SCAN_RUN_BUTTON_LABEL";
        this.run = new JButtonStateful(this.defaultText);
        I18nViewUtil.addComponentForKey("SCAN_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18nUtil.valueByKey("SCAN_RUN_BUTTON_TOOLTIP"));

        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(actionEvent -> {
            
            if (dndListScan.getSelectedValuesList().isEmpty()) {
                
                LOGGER.log(LogLevel.CONSOLE_ERROR, "Select URL(s) to scan");
                
                return;
            }
            
            new Thread(
                () -> {
                
                    if (ManagerScan.this.run.getState() == StateButton.STARTABLE) {
                        
                        ManagerScan.this.run.setText(I18nViewUtil.valueByKey("SCAN_RUN_BUTTON_STOP"));
                        ManagerScan.this.run.setState(StateButton.STOPPABLE);
                        ManagerScan.this.loader.setVisible(true);
                        
                        DefaultListModel<ItemList> listModel = (DefaultListModel<ItemList>) dndListScan.getModel();
                        for (var i = 0 ; i < listModel.getSize() ; i++) {
                            
                            listModel.get(i).reset();
                        }
                        
                        this.scan(dndListScan.getSelectedValuesList());
                        
                    } else {
                        
                        MediatorHelper.model().getResourceAccess().setScanStopped(true);
                        MediatorHelper.model().setIsStoppedByUser(true);
                        ManagerScan.this.run.setEnabled(false);
                        ManagerScan.this.run.setState(StateButton.STOPPING);
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
        
        // Erase everything in the view from a previous injection
        var requests = new Request();
        requests.setMessage(Interaction.RESET_INTERFACE);
        MediatorHelper.model().sendToViews(requests);
        
        // wait for ending of ongoing interaction between two injections
        try {
            Thread.sleep(500);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
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
            
            var urlItemListScan = (ItemListScan) urlItemList;
            if (MediatorHelper.model().isStoppedByUser() || MediatorHelper.model().getResourceAccess().isScanStopped()) {
                
                break;
            }
            
            LOGGER.log(LogLevel.CONSOLE_INFORM, "Scanning {}", urlItemListScan.getBeanInjection().getUrl());
            
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
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                Thread.currentThread().interrupt();
            }
        }
        
        // Get back the normal view
        MediatorHelper.model().sendToViews(requestUnsubscribe);
        MediatorHelper.model().subscribe(MediatorHelper.frame().getSubscriber());
        
        MediatorHelper.model().setIsScanning(false);
        MediatorHelper.model().setIsStoppedByUser(false);
        MediatorHelper.model().getResourceAccess().setScanStopped(false);

        var request = new Request();
        request.setMessage(Interaction.END_SCAN);
        MediatorHelper.model().sendToViews(request);
    }
}
