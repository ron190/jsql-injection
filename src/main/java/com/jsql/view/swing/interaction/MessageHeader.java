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
package com.jsql.view.swing.interaction;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.jsql.model.bean.util.HttpHeader;
import com.jsql.model.bean.util.TypeHeader;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.scrollpane.JScrollIndicator;

/**
 * Append a text to the tab Header.
 */
public class MessageHeader implements InteractionCommand {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
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
        this.params = (Map<String, Object>) interactionParams[0];
        url = (String) this.params.get(TypeHeader.URL);
        post = (String) this.params.get(TypeHeader.POST);
        header = (String) this.params.get(TypeHeader.HEADER);
        response = (Map<String, String>) this.params.get(TypeHeader.RESPONSE);
        source = (String) this.params.get(TypeHeader.SOURCE);
    }

    @Override
    public void execute() {
        MediatorGui.panelConsoles().addHeader(new HttpHeader(this.url, this.post, this.header, this.response, this.source));
        
        JViewport viewport = ((JScrollIndicator) MediatorGui.panelConsoles().network.getLeftComponent()).scrollPane.getViewport();
        JTable table = (JTable) viewport.getView();
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        try {
            model.addRow(new Object[]{response.get("Method"), this.url, this.response.get("Content-Length"), response.get("Content-Type")});
            
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
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            // Fix #4658, #2653, #2224, #1797 on model.addRow()
            LOGGER.error(e, e);
        }
    }
    
}
