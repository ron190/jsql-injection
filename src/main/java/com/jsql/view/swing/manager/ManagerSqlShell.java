/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.text.JPopupTextField;

/**
 * Manager for uploading PHP SQL shell to the host and send queries.
 */
@SuppressWarnings("serial")
public class ManagerSqlShell extends AbstractManagerShell {
    
    final JTextField username = new JPopupTextField(I18n.valueByKey("SQL_SHELL_USERNAME_LABEL")).getProxy();
    
    final JTextField password = new JPopupTextField(I18n.valueByKey("SQL_SHELL_PASSWORD_LABEL")).getProxy();
    
    /**
     * Build the manager panel.
     */
    public ManagerSqlShell() {

        this.run.setText("Create SQL shell(s)");
        
        JPanel userPassPanel = new JPanel();
        
        GroupLayout userPassLayout = new GroupLayout(userPassPanel);
        userPassPanel.setLayout(userPassLayout);
        userPassPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPassPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 1));
        
        this.username.setToolTipText(I18n.valueByKey("SQL_SHELL_USERNAME_TOOLTIP"));
        this.password.setToolTipText(I18n.valueByKey("SQL_SHELL_PASSWORD_TOOLTIP"));
        
        this.username.setBorder(HelperUi.BORDER_BLU);
        this.password.setBorder(HelperUi.BORDER_BLU);
        
        JPanel panelPassword = new JPanel(new BorderLayout());
        panelPassword.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        panelPassword.add(this.password);
        
        userPassLayout.setHorizontalGroup(
            userPassLayout
                .createSequentialGroup()
                .addGroup(
                    userPassLayout
                        .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                )
                .addGroup(
                    userPassLayout.createParallelGroup()
                        .addComponent(this.username)
                        .addComponent(panelPassword)
                )
        );

        userPassLayout.setVerticalGroup(
            userPassLayout
                .createSequentialGroup()
                .addGroup(
                    userPassLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.username)
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
    void createPayload(String shellPath, String shellURL) throws JSqlException, InterruptedException {
        MediatorModel.model().getResourceAccess().createSqlShell(shellPath, shellURL, this.username.getText(), this.password.getText());
    }
    
}
