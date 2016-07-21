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
package com.jsql.view.scan.interaction;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;

import com.jsql.model.bean.util.HttpHeader;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.scrollpane.JScrollIndicator;

/**
 * Append a text to the tab Header.
 */
public class MessageHeader implements InteractionCommand {
    // The text to append to the tab
    private String url;
    private String post;
    private String header;
    private Map<String, String> response;
    private String source;

    Map<String, Object> params;
    /**
     * @param interactionParams Text to append
     */
    @SuppressWarnings("unchecked")
    public MessageHeader(Object[] interactionParams) {
        params = (Map<String, Object>) interactionParams[0];
        url = (String) params.get("Url");
        post = (String) params.get("Post");
        header = (String) params.get("Header");
        response = (Map<String, String>) params.get("Response");
        source = (String) params.get("Source");
    }

    @Override
    public void execute() {
        MediatorGui.panelConsoles().addHeader(new HttpHeader(url, post, header, response, source));
        
        JViewport viewport = ((JScrollIndicator) MediatorGui.panelConsoles().network.getLeftComponent()).scrollPane.getViewport();
        JTable table = (JTable) viewport.getView();
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{response.get("Method"), url, response.get("Content-Length"), response.get("Content-Type")});
        
        Rectangle rect = table.getCellRect(table.getRowCount() - 1, 0, true);
        Point pt = viewport.getViewPosition();
        rect.translate(-pt.x, -pt.y);
        viewport.scrollRectToVisible(rect);
        
        int tabIndex = MediatorGui.tabConsoles().indexOfTab("Network");
        if (0 <= tabIndex && tabIndex < MediatorGui.tabConsoles().getTabCount()) {
            Component tabHeader = MediatorGui.tabConsoles().getTabComponentAt(tabIndex);
            if (MediatorGui.tabConsoles().getSelectedIndex() != tabIndex) {
                tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
            }
        }
    }
}
