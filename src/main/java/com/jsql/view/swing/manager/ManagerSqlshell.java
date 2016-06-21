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
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.i18n.I18n;
import com.jsql.model.accessible.RessourceAccess;
import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.text.JPopupTextField;

/**
 * Manager for uploading PHP SQL shell to the host and send queries.
 */
@SuppressWarnings("serial")
public class ManagerSqlshell extends ManagerAbstractShell {
    
    final JTextField username = new JPopupTextField(I18n.SQL_SHELL_USERNAME_LABEL).getProxy();
    
    final JTextField password = new JPopupTextField(I18n.SQL_SHELL_PASSWORD_LABEL).getProxy();
    
    /**
     * Build the manager panel.
     */
    public ManagerSqlshell() {
        super();
        
        JPanel userPassPanel = new JPanel();
        
        GroupLayout userPassLayout = new GroupLayout(userPassPanel);
        userPassPanel.setLayout(userPassLayout);
        userPassPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPassPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 1));
        
        username.setToolTipText(I18n.SQL_SHELL_USERNAME_TOOLTIP);
        password.setToolTipText(I18n.SQL_SHELL_PASSWORD_TOOLTIP);
        
        username.setBorder(HelperGui.BLU_ROUND_BORDER);
        password.setBorder(HelperGui.BLU_ROUND_BORDER);
        
        JPanel panelPassword = new JPanel(new BorderLayout());
        panelPassword.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        panelPassword.add(password);
        
        userPassLayout.setHorizontalGroup(
            userPassLayout
                .createSequentialGroup()
                .addGroup(
                    userPassLayout
                        .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                )
                .addGroup(
                    userPassLayout.createParallelGroup()
                        .addComponent(username)
                        .addComponent(panelPassword)
                )
        );

        userPassLayout.setVerticalGroup(
            userPassLayout
                .createSequentialGroup()
                .addGroup(
                    userPassLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(username)
                )
                .addGroup(
                    userPassLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(panelPassword)
                )
        );
        
        this.add(userPassPanel, BorderLayout.NORTH);
    }

    @Override
    void action(String shellPath, String shellURL) throws PreparationException, StoppableException {
        RessourceAccess.createSqlShell(shellPath, shellURL, username.getText(), password.getText());
    }
}
