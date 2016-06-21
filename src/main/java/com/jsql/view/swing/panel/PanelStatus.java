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
package com.jsql.view.swing.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.jsql.i18n.I18n;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.Strategy;
import com.jsql.model.vendor.Vendor;
import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.radio.AbstractRadioLink;
import com.jsql.view.swing.radio.RadioLinkStrategy;
import com.jsql.view.swing.text.JPopupLabel;

/**
 * Panel for statusbar.
 */
@SuppressWarnings("serial")
public class PanelStatus extends JPanel {
    /**
     * Default string in place of database infos.
     */
    private static final String INFO_DEFAULT_VALUE = "-";

    /**
     * Textfield not editable for database version.
     */
    private JTextField labelDbVersion = new JPopupLabel().getProxy();
    
    /**
     * Textfield not editable for database name.
     */
    private JTextField labelCurrentDb = new JPopupLabel().getProxy();
    
    /**
     * Textfield not editable for current user in database.
     */
    private JTextField labelCurrentUser = new JPopupLabel().getProxy();
    
    /**
     * Textfield not editable for authenticated user in database.
     */
    private JTextField labelAuthenticatedUser = new JPopupLabel().getProxy();

    /**
     * Selectable link for normal strategy.
     */
    public AbstractRadioLink labelNormal;
    
    /**
     * Selectable link for error strategy.
     */
    public AbstractRadioLink labelErrorBased;
    
    /**
     * Selectable link for blind strategy.
     */
    public AbstractRadioLink labelBlind;
    
    /**
     * Selectable link for time strategy.
     */
    public AbstractRadioLink labelTimeBased;

    /**
     * Create status panel on south of frame.
     */
    public PanelStatus() {
        this.labelNormal = new RadioLinkStrategy(I18n.LABEL_NORMAL, Strategy.NORMAL);
        I18n.components.get("LABEL_NORMAL").add(labelNormal);
        this.labelErrorBased = new RadioLinkStrategy(I18n.LABEL_ERRORBASED, Strategy.ERRORBASED);
        I18n.components.get("LABEL_ERRORBASED").add(labelErrorBased);
        this.labelBlind = new RadioLinkStrategy(I18n.LABEL_BLIND, Strategy.BLIND);
        I18n.components.get("LABEL_BLIND").add(labelBlind);
        this.labelTimeBased = new RadioLinkStrategy(I18n.LABEL_TIMEBASED, Strategy.TIME);
        I18n.components.get("LABEL_TIMEBASED").add(labelTimeBased);

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, HelperGui.COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(0, 5, 2, 5)
            )
        );

        this.reset();

        // Panel for database infos
        JPanel infos = new JPanel();
        GroupLayout layout = new GroupLayout(infos);
        infos.setLayout(layout);
        infos.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(infos);
        this.add(Box.createHorizontalGlue());

        Font boldFont = new Font(((Font) UIManager.get("Label.font")).getName(), Font.BOLD, ((Font) UIManager.get("Label.font")).getSize());
        this.labelDbVersion.setFont(boldFont);
        this.labelCurrentDb.setFont(boldFont);
        this.labelCurrentUser.setFont(boldFont);
        this.labelAuthenticatedUser.setFont(boldFont);
        
        this.labelDbVersion.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelDbVersion.setEditable(false);
        this.labelDbVersion.setBackground(this.getBackground());
        this.labelCurrentDb.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelCurrentDb.setEditable(false);
        this.labelCurrentDb.setBackground(this.getBackground());
        this.labelCurrentUser.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelCurrentUser.setEditable(false);
        this.labelCurrentUser.setBackground(this.getBackground());
        this.labelAuthenticatedUser.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelAuthenticatedUser.setEditable(false);
        this.labelAuthenticatedUser.setBackground(this.getBackground());

        final JComboBox<Vendor> vendorsCombo = new JComboBox<>(Vendor.values());
        vendorsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MediatorModel.model().selectedVendor = (Vendor) vendorsCombo.getSelectedItem();
            }
        });
        ((JLabel) vendorsCombo.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
        vendorsCombo.setToolTipText(
            "<html>"
            + "Choose <b>&lt;auto&gt;</b> if you don't know the type of database.<br>"
            + "</html>"
        );
        
        JLabel titleCurrentDB = new JLabel(I18n.TITLE_CURRENTDB);
        I18n.components.get("TITLE_CURRENTDB").add(titleCurrentDB);
        JLabel titleCurrentUser = new JLabel(I18n.TITLE_CURRENTUSER);
        I18n.components.get("TITLE_CURRENTUSER").add(titleCurrentUser);
        JLabel titleAuthenticatedUser = new JLabel(I18n.TITLE_AUTHENTICATEDUSER);
        I18n.components.get("TITLE_AUTHENTICATEDUSER").add(titleAuthenticatedUser);

        JPanel types = new JPanel();
        types.setLayout(new BoxLayout(types, BoxLayout.PAGE_AXIS));
        types.add(this.labelTimeBased);
        types.add(this.labelBlind);
        types.add(this.labelErrorBased);
        types.add(this.labelNormal);

        // Add pixels to the right to compensate width when strategy is selected
        this.labelTimeBased.setPreferredSize(new Dimension(this.labelTimeBased.getPreferredSize().width + 3, this.labelTimeBased.getPreferredSize().height));
        
        this.labelTimeBased.setToolTipText(I18n.LABEL_TIMEBASED_TOOLTIP);
        this.labelBlind.setToolTipText(I18n.LABEL_BLIND_TOOLTIP);
        this.labelErrorBased.setToolTipText(I18n.LABEL_ERRORBASED_TOOLTIP);
        this.labelNormal.setToolTipText(I18n.LABEL_NORMAL_TOOLTIP);

        this.add(types);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(vendorsCombo)
                .addComponent(titleCurrentDB)
                .addComponent(titleCurrentUser)
                .addComponent(titleAuthenticatedUser))
            .addGroup(layout.createParallelGroup()
                .addComponent(this.labelDbVersion)
                .addComponent(this.labelCurrentDb)
                .addComponent(this.labelCurrentUser)
                .addComponent(this.labelAuthenticatedUser))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(vendorsCombo)
                .addComponent(this.labelDbVersion))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(titleCurrentDB)
                .addComponent(this.labelCurrentDb))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(titleCurrentUser)
                .addComponent(this.labelCurrentUser))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(titleAuthenticatedUser)
                .addComponent(this.labelAuthenticatedUser))
        );
    }

    /**
     * Set default values for database information. 
     */
    public final void reset() {
        this.labelDbVersion.setText(PanelStatus.INFO_DEFAULT_VALUE);
        this.labelCurrentDb.setText(PanelStatus.INFO_DEFAULT_VALUE);
        this.labelCurrentUser.setText(PanelStatus.INFO_DEFAULT_VALUE);
        this.labelAuthenticatedUser.setText(PanelStatus.INFO_DEFAULT_VALUE);
        
        this.labelNormal.setIcon(HelperGui.SQUARE_GREY);
        this.labelErrorBased.setIcon(HelperGui.SQUARE_GREY);
        this.labelBlind.setIcon(HelperGui.SQUARE_GREY);
        this.labelTimeBased.setIcon(HelperGui.SQUARE_GREY);
        
        this.labelNormal.setFont(HelperGui.SEGOE_FONT);
        this.labelErrorBased.setFont(HelperGui.SEGOE_FONT);
        this.labelBlind.setFont(HelperGui.SEGOE_FONT);
        this.labelTimeBased.setFont(HelperGui.SEGOE_FONT);
    }

    /**
     * Set database information in statusbar.
     * @param version Database version
     * @param database Name of current database
     * @param user User name logged into database
     * @param authenticatedUser User authenticated
     */
    public void setInfos(String version, String database, String user, String authenticatedUser) {
        this.labelDbVersion.setText(version);
        this.labelCurrentDb.setText(database);
        this.labelCurrentUser.setText(user);
        this.labelAuthenticatedUser.setText(authenticatedUser);
    }

    /**
     * Displays an icon next to normal label.
     * @param icon Icon to display
     */
    public void setNormalIcon(Icon icon) {
        this.labelNormal.setIcon(icon);
    }
    
    /**
     * Displays an icon next to errorbased label.
     * @param icon Icon to display
     */
    public void setErrorBasedIcon(Icon icon) {
        this.labelErrorBased.setIcon(icon);
    }
    
    /**
     * Displays an icon next to blind label.
     * @param icon Icon to display
     */
    public void setBlindIcon(Icon icon) {
        this.labelBlind.setIcon(icon);
    }
    
    /**
     * Displays an icon next to timebased label.
     * @param icon Icon to display
     */
    public void setTimeBasedIcon(Icon icon) {
        this.labelTimeBased.setIcon(icon);
    }
}
