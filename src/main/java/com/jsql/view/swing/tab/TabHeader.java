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
package com.jsql.view.swing.tab;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jsql.view.swing.MediatorGUI;

/**
 * Panel displayed as a header for tabs.
 */
@SuppressWarnings("serial")
public class TabHeader extends JPanel implements MouseListener {
    /**
     * Tab header with default tab icon.
     */
    public TabHeader() {
        this(new ImageIcon(TabHeader.class.getResource("/com/jsql/view/swing/resources/images/table.png")));
    }

    /**
     * Tab header with a custom icon.
     */
    public TabHeader(ImageIcon imageIcon) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.setOpaque(false);

        // Set the text of tab
        JLabel tabTitleLabel = new JLabel() {
            @Override
            public String getText() {
                int i = MediatorGUI.tabResults().indexOfTabComponent(TabHeader.this);
                if (i != -1) {
                    return MediatorGUI.tabResults().getTitleAt(i);
                }
                return null;
            }
        };
        tabTitleLabel.setIcon(imageIcon);
        this.add(tabTitleLabel);

        JButton tabCloseButton = new ButtonClose();
        tabCloseButton.addMouseListener(this);

        this.add(tabCloseButton);
    }

    /**
     * Action for close button: remove tab.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        int closeTabNumber = MediatorGUI.tabResults().indexOfTabComponent(TabHeader.this);
        MediatorGUI.tabResults().removeTabAt(closeTabNumber);
    }

    @Override public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
    @Override public void mouseExited(MouseEvent e) {
        // Do nothing
    }
    @Override public void mousePressed(MouseEvent e) {
        // Do nothing
    }
    @Override public void mouseReleased(MouseEvent e) {
        // Do nothing
    }
}
