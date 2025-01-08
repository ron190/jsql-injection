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
package com.jsql.view.swing.manager;

import com.jsql.model.exception.JSqlException;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Manager for uploading PHP SQL shell to the host and send queries.
 */
public class ManagerSqlShell extends AbstractManagerShell {
    
    private final JTextField username = new JPopupTextField(I18nUtil.valueByKey("SQL_SHELL_USERNAME_LABEL")).getProxy();
    private final JTextField password = new JPopupTextField(I18nUtil.valueByKey("SQL_SHELL_PASSWORD_LABEL")).getProxy();
    
    /**
     * Build the manager panel.
     */
    public ManagerSqlShell() {
        this.run.setText("Create SQL shell(s)");
        
        var userPassPanel = new JPanel();
        var groupLayout = new GroupLayout(userPassPanel);
        userPassPanel.setLayout(groupLayout);
        userPassPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.username.setToolTipText(I18nUtil.valueByKey("SQL_SHELL_USERNAME_TOOLTIP"));
        this.password.setToolTipText(I18nUtil.valueByKey("SQL_SHELL_PASSWORD_TOOLTIP"));

        var panelPassword = new JPanel(new BorderLayout());
        panelPassword.add(this.password);
        
        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
            )
            .addGroup(
                groupLayout.createParallelGroup()
                .addComponent(this.username)
                .addComponent(panelPassword)
            )
        );

        groupLayout.setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.username)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelPassword)
            )
        );
        
        this.add(userPassPanel, BorderLayout.NORTH);
    }

    @Override
    protected void createPayload(String shellPath, String shellURL) throws JSqlException {
        MediatorHelper.model().getResourceAccess().createSqlShell(shellPath, shellURL, this.username.getText(), this.password.getText());
    }
}
