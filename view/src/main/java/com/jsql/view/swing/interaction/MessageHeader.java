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
package com.jsql.view.swing.interaction;

import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.HttpHeader;
import com.jsql.model.injection.strategy.blind.AbstractCallableBinary;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.panel.consoles.NetworkTable;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class MessageHeader implements InteractionCommand {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final String url;
    private final String post;
    private final Map<String, String> header;
    private final Map<String, String> response;
    private final String source;
    private final String size;
    private final String metadataProcess;
    private final String metadataStrategy;
    private final AbstractCallableBinary<?> metadataBoolean;

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
        this.metadataBoolean = (AbstractCallableBinary<?>) params.get(Header.METADATA_BOOLEAN);
    }

    @Override
    public void execute() {
        NetworkTable table = MediatorHelper.panelConsoles().getNetworkTable();
        table.addHeader(new HttpHeader(this.url, this.post, this.header, this.response, this.source));
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        try {
            model.addRow(new Object[] {
                this.url,
                this.size,
                this.metadataStrategy,
                Arrays.asList(this.metadataProcess, this.metadataBoolean)
            });
            
            Rectangle rect = table.getCellRect(table.getRowCount() - 1, 0, true);
            table.scrollRectToVisible(rect);
            
            MediatorHelper.tabConsoles().setBold("Network");
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            // Fix #4658, #2224, #1797 on model.addRow()
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
}
