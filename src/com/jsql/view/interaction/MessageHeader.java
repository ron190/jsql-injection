/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.interaction;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jsql.model.bean.HTTPHeader;
import com.jsql.view.GUIMediator;

/**
 * Append a text to the tab Header
 */
public class MessageHeader implements IInteractionCommand{
    // The text to append to the tab
    private String url;
    private String cookie;
    private String post;
    private String header;
    private Map<String, String> response;

    /**
     * @param mainGUI
     * @param interactionParams Text to append
     */
    @SuppressWarnings("unchecked")
	public MessageHeader(Object[] interactionParams){
    	Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        url = (String) params.get("Url");
        cookie = (String) params.get("Cookie");
        post = (String) params.get("Post");
        header = (String) params.get("Header");
        response = (Map<String, String>) params.get("Response");
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        GUIMediator.gui().getOutputPanel().listHTTPHeader.add(new HTTPHeader(url, cookie, post, header, response));
        DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane) GUIMediator.gui().network.getLeftComponent()).getViewport().getView()).getModel();
        model.addRow(new Object[]{response.get("Method"), url, response.get("Content-Length"), response.get("Content-Type")});
        
        Rectangle rect = ((JTable)((JScrollPane) GUIMediator.gui().network.getLeftComponent()).getViewport().getView()).getCellRect(((JTable)((JScrollPane) GUIMediator.gui().network.getLeftComponent()).getViewport().getView()).getRowCount()-1, 0 /* col */, true);
        Point pt = ((JScrollPane) GUIMediator.gui().network.getLeftComponent()).getViewport().getViewPosition();
        rect.translate(-pt.x, -pt.y);
        ((JScrollPane) GUIMediator.gui().network.getLeftComponent()).getViewport().scrollRectToVisible(rect);
    }
}
