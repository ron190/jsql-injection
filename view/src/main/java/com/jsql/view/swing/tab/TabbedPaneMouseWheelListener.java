package com.jsql.view.swing.tab;

import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Mouse wheel allows to navigate to next/previous tab.
 */
public class TabbedPaneMouseWheelListener implements MouseWheelListener {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        
        JTabbedPane tabPane = (JTabbedPane) event.getSource();

        int dir = -event.getWheelRotation();
        int selIndex = tabPane.getSelectedIndex();
        int maxIndex = tabPane.getTabCount() - 1;
        
        if ((selIndex == 0 && dir < 0) || (selIndex == maxIndex && dir > 0)) {
            selIndex = maxIndex - selIndex;
        } else {
            selIndex += dir;
        }
        
        if (0 <= selIndex && selIndex < tabPane.getTabCount()) {
            // Fix #54575: NullPointerException on setSelectedIndex()
            // Fix #90835: IllegalArgumentException on setSelectedIndex()
            try {
                tabPane.setSelectedIndex(selIndex);
            } catch (IllegalArgumentException | NullPointerException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }
    }
}