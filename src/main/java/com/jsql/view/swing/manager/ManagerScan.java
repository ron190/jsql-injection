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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.list.BeanInjection;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.list.ItemListScan;
import com.jsql.view.swing.list.ListTransfertHandlerScan;
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

        StringBuilder jsonScan = new StringBuilder();
        try {
            InputStream in = HelperUi.INPUT_STREAM_PAGES_SCAN;
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                jsonScan.append(line);
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        List<ItemList> itemsList = new ArrayList<>();
        
        JSONArray jsonArrayScan;
        try {
            jsonArrayScan = new JSONArray(jsonScan.toString());
            
            for (int i = 0 ; i < jsonArrayScan.length() ; i++) {
                JSONObject jsonObjectScan = jsonArrayScan.getJSONObject(i);
                BeanInjection beanInjection = new BeanInjection(
                    jsonObjectScan.getString("url"),
                    jsonObjectScan.optString("request"),
                    jsonObjectScan.optString("header"),
                    jsonObjectScan.optString("injectionType"),
                    jsonObjectScan.optString("vendor"),
                    jsonObjectScan.optString("requestType")
                );
                itemsList.add(new ItemListScan(beanInjection));
            }
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        final DnDList dndListScan = new DnDList(itemsList);
        dndListScan.isScan = true;
        dndListScan.setName("scan");
        dndListScan.setTransferHandler(null);
        dndListScan.setTransferHandler(new ListTransfertHandlerScan());
        
        this.listPaths = dndListScan;
        this.getListPaths().setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));
        this.add(new LightScrollPane(1, 0, 0, 0, dndListScan), BorderLayout.CENTER);

        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));

        lastLine.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)
            )
        );
        
        this.defaultText = "SCAN_RUN_BUTTON_LABEL";
        this.run = new JButtonStateful(this.defaultText);
        I18nView.addComponentForKey("SCAN_RUN_BUTTON_LABEL", this.run);
        this.run.setToolTipText(I18n.valueByKey("SCAN_RUN_BUTTON_TOOLTIP"));

        this.run.setContentAreaFilled(false);
        this.run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        this.run.setBackground(new Color(200, 221, 242));
        
        this.run.addMouseListener(new FlatButtonMouseAdapter(this.run));
        
        this.run.addActionListener(actionEvent -> {
            
            if (dndListScan.getSelectedValuesList().isEmpty()) {
                LOGGER.warn("Select URL(s) to scan");
                return;
            }
            
            new Thread(() -> {
                
                if (ManagerScan.this.run.getState() == StateButton.STARTABLE) {
                    ManagerScan.this.run.setText(I18nView.valueByKey("SCAN_RUN_BUTTON_STOP"));
                    ManagerScan.this.run.setState(StateButton.STOPPABLE);
                    ManagerScan.this.loader.setVisible(true);
                    
                    DefaultListModel<ItemList> listModel = (DefaultListModel<ItemList>) dndListScan.getModel();
                    for (int i = 0 ; i < listModel.getSize() ; i++) {
                        listModel.get(i).reset();
                    }
                    
                    MediatorModel.model().getResourceAccess().scanList(dndListScan.getSelectedValuesList());
                } else {
                    MediatorModel.model().getResourceAccess().setScanStopped(true);
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
        
        dndListScan.addListSelectionListener(e -> {
            
            if (dndListScan.getSelectedValue() == null) {
                return;
            }
            BeanInjection beanInjection = ((ItemListScan) dndListScan.getSelectedValue()).getBeanInjection();
            
            MediatorGui.panelAddressBar().getTextFieldAddress().setText(beanInjection.getUrl());
            MediatorGui.panelAddressBar().getTextFieldHeader().setText(beanInjection.getHeader());
            MediatorGui.panelAddressBar().getTextFieldRequest().setText(beanInjection.getRequest());
            
            String requestType = beanInjection.getRequestType();
            if (requestType != null && !requestType.isEmpty()) {
                MediatorGui.panelAddressBar().getRadioRequest().setText(requestType);
            } else {
                MediatorGui.panelAddressBar().getRadioRequest().setText("POST");
            }
            
            MethodInjection injectionType = beanInjection.getInjectionTypeAsEnum();
            if (injectionType == MediatorModel.model().getMediatorMethodInjection().getHeader()) {
                MediatorGui.panelAddressBar().getRadioHeader().setSelected();
            } else if (injectionType == MediatorModel.model().getMediatorMethodInjection().getRequest()) {
                MediatorGui.panelAddressBar().getRadioRequest().setSelected();
            } else {
                MediatorGui.panelAddressBar().getRadioQueryString().setSelected();
            }
        });
    }
    
}
