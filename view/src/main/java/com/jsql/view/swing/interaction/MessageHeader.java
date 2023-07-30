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
package com.jsql.view.swing.interaction;

import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.HttpHeader;
import com.jsql.model.injection.strategy.blind.AbstractCallableBoolean;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.scrollpane.JScrollIndicator;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

/**
 * Append a text to the tab Header.
 */
public class MessageHeader implements InteractionCommand {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    // The text to append to the tab
    private final String url;
    private final String post;
    private final Map<String, String> header;
    private final Map<String, String> response;
    private final String source;
    private final String size;
    private final String metadataProcess;
    private final String metadataStrategy;
    private final AbstractCallableBoolean<?> metadataBoolean;

    /**
     * @param interactionParams Text to append
     */
    @SuppressWarnings("unchecked")
    public MessageHeader(Object[] interactionParams) {
        
        Map<Header, Object> params = (Map<Header, Object>) interactionParams[0];
        
        this.url = (String) params.get(Header.URL);
        this.post = (String) params.get(Header.POST);
        this.header = (Map<String, String>) params.get(Header.HEADER);
        this.response = (Map<String, String>) params.get(Header.RESPONSE);
        this.source = (String) params.get(Header.SOURCE);
        this.size = (String) params.get(Header.PAGE_SIZE);
        this.metadataProcess = (String) params.get(Header.METADATA_PROCESS);
        this.metadataStrategy = (String) params.get(Header.METADATA_STRATEGY);
        this.metadataBoolean = (AbstractCallableBoolean<?>) params.get(Header.METADATA_BOOLEAN);
    }

    @Override
    public void execute() {
        
        MediatorHelper.panelConsoles().getNetworkTable().addHeader(new HttpHeader(this.url, this.post, this.header, this.response, this.source));
        
        JViewport viewport = ((JScrollIndicator) MediatorHelper.panelConsoles().getNetworkSplitPane().getLeftComponent()).getScrollPane().getViewport();
        JTable table = (JTable) viewport.getView();
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        try {
            model.addRow(new Object[] {
                    
                this.url,
                this.size,
                this.metadataStrategy,
                Arrays.asList(this.metadataProcess, this.metadataBoolean)
            });
            
            Rectangle rect = table.getCellRect(table.getRowCount() - 1, 0, true);
            Point pt = viewport.getViewPosition();
            rect.translate(-pt.x, -pt.y);
            viewport.scrollRectToVisible(rect);
            
            MediatorHelper.tabConsoles().highlightTab("Network");
            
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            
            // Fix #4658, #2224, #1797 on model.addRow()
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
}
