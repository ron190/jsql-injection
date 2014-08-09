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
package com.jsql.view.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.jsql.view.GUITools;
import com.jsql.view.radio.AbstractRadioLink;
import com.jsql.view.radio.RadioLinkStatusbar;
import com.jsql.view.textcomponent.JPopupLabel;

/**
 * Panel for statusbar.
 */
@SuppressWarnings("serial")
public class StatusbarPanel extends JPanel {
    /**
     * Default string in place of database infos.
     */
    private static final String INFO_DEFAULT_VALUE = "-";

    /**
     * Textfield not editable for database version.
     */
    private JTextField labelDBVersion = new JPopupLabel().getProxy();
    
    /**
     * Textfield not editable for database name.
     */
    private JTextField labelCurrentDB = new JPopupLabel().getProxy();
    
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
    public StatusbarPanel() {
        this.labelNormal = new RadioLinkStatusbar("Normal");
        this.labelErrorBased = new RadioLinkStatusbar("ErrorBased");
        this.labelBlind = new RadioLinkStatusbar("Blind");
        this.labelTimeBased = new RadioLinkStatusbar("TimeBased");

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER),
                        BorderFactory.createEmptyBorder(0, 5, 2, 5)));

        this.reset();

        // Panel for database infos
        JPanel infos = new JPanel();
        GroupLayout layout = new GroupLayout(infos);
        infos.setLayout(layout);
        infos.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(infos);
        this.add(Box.createHorizontalGlue());

        Font boldFont = new Font(((Font) UIManager.get("Label.font")).getName(), Font.BOLD, ((Font) UIManager.get("Label.font")).getSize());
        this.labelDBVersion.setFont(boldFont);
        this.labelCurrentDB.setFont(boldFont);
        this.labelCurrentUser.setFont(boldFont);
        this.labelAuthenticatedUser.setFont(boldFont);
        
        this.labelDBVersion.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelDBVersion.setEditable(false);
        this.labelDBVersion.setBackground(this.getBackground());
        this.labelCurrentDB.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelCurrentDB.setEditable(false);
        this.labelCurrentDB.setBackground(this.getBackground());
        this.labelCurrentUser.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelCurrentUser.setEditable(false);
        this.labelCurrentUser.setBackground(this.getBackground());
        this.labelAuthenticatedUser.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        this.labelAuthenticatedUser.setEditable(false);
        this.labelAuthenticatedUser.setBackground(this.getBackground());

        JLabel titleDatabaseVersion = new JLabel("Database version");
        JLabel titleCurrentDB = new JLabel("Current db");
        JLabel titleCurrentUser = new JLabel("Current user");
        JLabel titleAuthenticatedUser = new JLabel("Authenticated user");

        JPanel types = new JPanel();
        types.setLayout(new BoxLayout(types, BoxLayout.PAGE_AXIS));
        types.add(this.labelTimeBased);
        types.add(this.labelBlind);
        types.add(this.labelErrorBased);
        types.add(this.labelNormal);

        // Add pixels to the right to compensate width when strategy is selected
        this.labelTimeBased.setPreferredSize(new Dimension(this.labelTimeBased.getPreferredSize().width + 3, this.labelTimeBased.getPreferredSize().height));
        
        this.labelTimeBased.setToolTipText("<html><b>Slowest and less reliable method</b><br>"
                + "Boolean SQL test generates a 5s wait time for false SQL statement.<br>"
                + "<i>Read each bit of encoded characters (16 URL calls by character).</i></html>");
        this.labelBlind.setToolTipText("<html><b>Slow and less reliable method</b><br>"
                + "Boolean SQL test generates pageA for true SQL statement, pageB for false.<br>"
                + "<i>Read each bit of encoded characters (16 URL calls by character).</i></html>");
        this.labelErrorBased.setToolTipText("<html><b>Fast and accurate method</b><br>"
                + "<i>Read encoded data directly from source page.</i></html>");
        this.labelNormal.setToolTipText("<html><b>Fastest and accurate method</b><br>"
                + "<i>Read large encoded data directly from source page.</i></html>");

        this.add(types);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(titleDatabaseVersion)
                        .addComponent(titleCurrentDB)
                        .addComponent(titleCurrentUser)
                        .addComponent(titleAuthenticatedUser))
                .addGroup(layout.createParallelGroup()
                        .addComponent(this.labelDBVersion)
                        .addComponent(this.labelCurrentDB)
                        .addComponent(this.labelCurrentUser)
                        .addComponent(this.labelAuthenticatedUser))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(titleDatabaseVersion)
                        .addComponent(this.labelDBVersion))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(titleCurrentDB)
                        .addComponent(this.labelCurrentDB))
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
        this.labelDBVersion.setText(StatusbarPanel.INFO_DEFAULT_VALUE);
        this.labelCurrentDB.setText(StatusbarPanel.INFO_DEFAULT_VALUE);
        this.labelCurrentUser.setText(StatusbarPanel.INFO_DEFAULT_VALUE);
        this.labelAuthenticatedUser.setText(StatusbarPanel.INFO_DEFAULT_VALUE);
        
        this.labelNormal.setIcon(GUITools.SQUARE_GREY);
        this.labelErrorBased.setIcon(GUITools.SQUARE_GREY);
        this.labelBlind.setIcon(GUITools.SQUARE_GREY);
        this.labelTimeBased.setIcon(GUITools.SQUARE_GREY);
        
        this.labelNormal.setFont(GUITools.MYFONT);
        this.labelErrorBased.setFont(GUITools.MYFONT);
        this.labelBlind.setFont(GUITools.MYFONT);
        this.labelTimeBased.setFont(GUITools.MYFONT);
    }

    /**
     * Set database information in statusbar.
     * @param version Database version
     * @param database Name of current database
     * @param user User name logged into database
     * @param authenticatedUser User authenticated
     */
    public void setInfos(String version, String database, String user, String authenticatedUser) {
        this.labelDBVersion.setText(version);
        this.labelCurrentDB.setText(database);
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
