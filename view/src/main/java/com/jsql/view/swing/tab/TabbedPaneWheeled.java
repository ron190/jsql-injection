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
package com.jsql.view.swing.tab;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.action.HotkeyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Tabs with mouse-wheel and right click action.
 */
public class TabbedPaneWheeled extends JTabbedPane {

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Create tabs with ctrl-TAB, mouse-wheel.
     */
    public TabbedPaneWheeled() {
        this(SwingConstants.TOP);
    }

    public TabbedPaneWheeled(int tabPlacement) {
        super(tabPlacement, JTabbedPane.SCROLL_TAB_LAYOUT);
        this.addMouseWheelListener(new TabbedPaneMouseWheelListener());
        HotkeyUtil.addShortcut(this);  // Hotkeys ctrl-TAB, ctrl-shift-TAB
    }

    /**
     * Highlight tab to mark when new content added
     */
    public void setBold(String label) {
        int tabIndex = this.indexOfTab(label);
        // Highlight only if tab not selected and tab exists
        if (
            this.getSelectedIndex() != tabIndex
            && 0 <= tabIndex && tabIndex < this.getTabCount()
        ) {
            var tabHeader = this.getTabComponentAt(tabIndex);
            // Unhandled ClassCastException #91158 on setFont()
            try {
                tabHeader.setFont(tabHeader.getFont().deriveFont(Font.BOLD));
            } catch (ClassCastException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }
    }
}
