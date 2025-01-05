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

import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Panel displayed as a header for tabs.
 * Compatible with i18n.
 */
public class TabHeader extends JPanel implements MouseListener {
    
    private final JLabel tabLabel = new JLabel();

    public TabHeader(String label, Icon imageIcon) {
        super(new BorderLayout());
        this.setOpaque(false);  // required for transparency
        this.tabLabel.setIcon(imageIcon);
        this.tabLabel.setText(label);
        this.tabLabel.setName(label.trim());
        this.add(this.tabLabel);
    }

    /**
     * Action for close button: remove tab.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        int closeTabNumber = MediatorHelper.tabResults().indexOfTabComponent(TabHeader.this);
        ActionCloseTabResult.perform(closeTabNumber);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }

    public JLabel getTabLabel() {
        return this.tabLabel;
    }
}
