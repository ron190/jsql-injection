package com.jsql.view.swing.tab;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.commons.lang3.SerializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Display popupmenu on right click.
 * Used on manager tabs.
 */
public class TabMouseAdapter extends MouseAdapter {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private final TabbedPaneWheeled tabbedPaneWheeled;
    
    public TabMouseAdapter(TabbedPaneWheeled tabbedPaneWheeled) {
        this.tabbedPaneWheeled = tabbedPaneWheeled;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (!SwingUtilities.isRightMouseButton(event)) {
            return;
        }

        // Copy menu items from menubar
        var menu = new JPopupMenu();
        for (var i = 0 ; i < MediatorHelper.menubar().getMenuView().getMenuComponentCount() ; i++) {
            final int positionFinal = i;
            // Fix #35348: SerializationException on clone()
            try {
                // clone and setAction() not efficient (performance and i18n)
                var itemOrigin = (JMenuItem) MediatorHelper.menubar().getMenuView().getMenuComponent(i);
                JMenuItem itemMenu = new JMenuItem(itemOrigin.getText(), itemOrigin.getIcon());
                itemMenu.addActionListener(actionEvent -> this.tabbedPaneWheeled.setSelectedIndex(positionFinal));
                menu.add(itemMenu);
            } catch (SerializationException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }

        var componentSource = (Component) event.getSource();
        menu.show(componentSource, event.getX(), event.getY());
        menu.setLocation(
            ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
            ? event.getXOnScreen() - menu.getWidth()
            : event.getXOnScreen(),
            event.getYOnScreen()
        );
    }
}