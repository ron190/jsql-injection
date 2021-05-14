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
package com.jsql.view.swing.tab;

import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Panel displayed as a header for tabs.
 */
@SuppressWarnings("serial")
public class TabHeader extends JPanel implements MouseListener {
    
    private transient Cleanable cleanableTab;
    
    public interface Cleanable {
        void clean();
    }
    
    private JLabel tabTitleLabel = new JLabel() {
        
        @Override
        public void setText(String text) {
            
            super.setText(text + StringUtils.SPACE);
        }
    };
    
    /**
     * Tab header with default tab icon.
     */
    public TabHeader() {
        this(UiUtil.ICON_TABLE);
    }

    /**
     * Tab header with a custom icon.
     */
    public TabHeader(Icon imageIcon) {
        
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.setOpaque(false);

        // Set the text of tab
        this.getTabTitleLabel().setIcon(imageIcon);
        this.add(this.getTabTitleLabel());

        JButton tabCloseButton = new ButtonClose();
        tabCloseButton.addMouseListener(this);

        this.add(tabCloseButton);
    }

    public TabHeader(String label, Icon imageIcon) {
        
        this(imageIcon);
        
        this.getTabTitleLabel().setText(label);
        tabTitleLabel.setName(label.trim());
    }

    public TabHeader(String label, Icon imageIcon, Cleanable cleanableTab) {
        
        this(imageIcon);
        
        this.getTabTitleLabel().setText(label);
        tabTitleLabel.setName(label.trim());
        
        this.cleanableTab = cleanableTab;
    }
    
    public TabHeader(String label) {
        
        this();
        
        this.getTabTitleLabel().setText(label);
        tabTitleLabel.setName(label.trim());
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
        
        if (this.getCleanableTab() != null) {
            
            this.getCleanableTab().clean();
        }
        
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

    public JLabel getTabTitleLabel() {
        return this.tabTitleLabel;
    }

    public Cleanable getCleanableTab() {
        return this.cleanableTab;
    }
}
