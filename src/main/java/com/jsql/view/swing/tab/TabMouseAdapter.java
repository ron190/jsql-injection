package com.jsql.view.swing.tab;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.MediatorGui;

/**
 * Display popupmenu on right click.
 * Used on manager tabs.
 */
public class TabMouseAdapter extends MouseAdapter {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (!SwingUtilities.isRightMouseButton(e)) {
            return;
        }
            
        Component componentSource = (Component) e.getSource();
        JPopupMenu menu = new JPopupMenu();

        // Copy menu items from menubar
        for (int position = 0 ; position < MediatorGui.menubar().getMenuView().getMenuComponentCount() ; position++) {
            
            // Fix #35348: SerializationException on clone()
            try {
                JMenuItem itemMenu = (JMenuItem) SerializationUtils.clone(MediatorGui.menubar().getMenuView().getMenuComponent(position));
                menu.add(itemMenu);
                
                final int positionFinal = position;
                itemMenu.addActionListener(actionEvent -> MediatorGui.tabManagers().setSelectedIndex(positionFinal));
            } catch (SerializationException ex) {
                LOGGER.error(ex, ex);
            }
        }

        menu.show(componentSource, e.getX(), e.getY());
        
        menu.setLocation(
            ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
            ? e.getXOnScreen() - menu.getWidth()
            : e.getXOnScreen(),
            e.getYOnScreen()
        );
    }
}