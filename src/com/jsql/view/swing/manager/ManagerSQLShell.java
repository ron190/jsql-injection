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
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.text.JPopupTextField;

/**
 * Manager for uploading PHP SQL shell to the host and send queries.
 */
@SuppressWarnings("serial")
public class ManagerSQLShell extends ManagerAbstractShell {
    final JTextField user = new JPopupTextField(I18n.SQL_SHELL_USERNAME_LABEL).getProxy();
    final JTextField pass = new JPopupTextField(I18n.SQL_SHELL_PASSWORD_LABEL).getProxy();
    
    /**
     * Build the manager panel.
     */
    public ManagerSQLShell() {
        super();
        
        JPanel userPassPanel = new JPanel();
        
        GroupLayout userPassLayout = new GroupLayout(userPassPanel);
        userPassPanel.setLayout(userPassLayout);
        userPassPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPassPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 1));
        
        user.setToolTipText(I18n.SQL_SHELL_USERNAME_TOOLTIP);
        pass.setToolTipText(I18n.SQL_SHELL_PASSWORD_TOOLTIP);
        
        user.setBorder(HelperGUI.BLU_ROUND_BORDER);
        pass.setBorder(HelperGUI.BLU_ROUND_BORDER);
        
        JPanel m = new JPanel(new BorderLayout());
        m.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        m.add(pass);
        
        userPassLayout.setHorizontalGroup(
            userPassLayout.createSequentialGroup()
                .addGroup(userPassLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false))
                .addGroup(userPassLayout.createParallelGroup()
                        .addComponent(user)
                        .addComponent(m))
        );

        userPassLayout.setVerticalGroup(
            userPassLayout.createSequentialGroup()
                .addGroup(userPassLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(user))
                .addGroup(userPassLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(m))
        );
        
        this.add(userPassPanel, BorderLayout.NORTH);
    }

    @Override
    void action(String path, String shellURL) throws PreparationException, StoppableException {
        MediatorModel.model().ressourceAccessObject.getSQLShell(path, shellURL, user.getText(), pass.getText());
    }
}
