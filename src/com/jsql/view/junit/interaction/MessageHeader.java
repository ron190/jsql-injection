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
package com.jsql.view.junit.interaction;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;

import com.jsql.model.bean.HTTPHeader;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.interaction.IInteractionCommand;

/**
 * Append a text to the tab Header.
 */
public class MessageHeader implements IInteractionCommand {
    // The text to append to the tab
    private String url;
    private String cookie;
    private String post;
    private String header;
    private Map<String, String> response;

    /**
     * @param interactionParams Text to append
     */
    @SuppressWarnings("unchecked")
    public MessageHeader(Object[] interactionParams) {
        Map<String, Object> params = (Map<String, Object>) interactionParams[0];
        url = (String) params.get("Url");
        cookie = (String) params.get("Cookie");
        post = (String) params.get("Post");
        header = (String) params.get("Header");
        response = (Map<String, String>) params.get("Response");
    }

    @Override
    public void execute() {
        System.out.println("Method: " + response.get("Method"));
        System.out.println("Url: " + url);
        System.out.println("Cookie: " + cookie);
        System.out.println("Post: " + post);
        System.out.println("Header: " + header);
        System.out.println("Content-Length: " + response.get("Content-Length"));
        System.out.println("Content-Type: " + response.get("Content-Type"));
        System.out.println();
    }
}
