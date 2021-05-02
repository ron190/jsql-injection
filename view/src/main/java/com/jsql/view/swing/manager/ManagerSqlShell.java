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
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jsql.model.exception.JSqlException;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Manager for uploading PHP SQL shell to the host and send queries.
 */
@SuppressWarnings("serial")
public class ManagerSqlShell extends AbstractManagerShell {
    
    private final JTextField username = new JPopupTextField(I18nUtil.valueByKey("SQL_SHELL_USERNAME_LABEL")).getProxy();
    
    private final JTextField password = new JPopupTextField(I18nUtil.valueByKey("SQL_SHELL_PASSWORD_LABEL")).getProxy();
    
    /**
     * Build the manager panel.
     */
    public ManagerSqlShell() {

        this.run.setText("Create SQL shell(s)");
        
        var userPassPanel = new JPanel();
        
        var layout = new GroupLayout(userPassPanel);
        userPassPanel.setLayout(layout);
        userPassPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPassPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        this.username.setToolTipText(I18nUtil.valueByKey("SQL_SHELL_USERNAME_TOOLTIP"));
        this.password.setToolTipText(I18nUtil.valueByKey("SQL_SHELL_PASSWORD_TOOLTIP"));
        
        this.username.setBorder(UiUtil.BORDER_BLU);
        this.password.setBorder(UiUtil.BORDER_BLU);
        
        var panelPassword = new JPanel(new BorderLayout());
        panelPassword.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        panelPassword.add(this.password);
        
        layout
        .setHorizontalGroup(
            layout
            .createSequentialGroup()
            .addGroup(
                layout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
            )
            .addGroup(
                layout.createParallelGroup()
                .addComponent(this.username)
                .addComponent(panelPassword)
            )
        );

        layout
        .setVerticalGroup(
            layout
            .createSequentialGroup()
            .addGroup(
                layout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.username)
            )
            .addGroup(
                layout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelPassword)
            )
        );
        
        this.add(userPassPanel, BorderLayout.NORTH);
    }

    @Override
    protected void createPayload(String shellPath, String shellURL) throws JSqlException, InterruptedException {
        
        MediatorHelper.model().getResourceAccess().createSqlShell(shellPath, shellURL, this.username.getText(), this.password.getText());
    }
}
