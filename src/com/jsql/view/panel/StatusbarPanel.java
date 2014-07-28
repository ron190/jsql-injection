/*******************************************************************************
 * Copyhacked (H) 2012-2013.
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
import javax.swing.UIManager;

import com.jsql.view.GUITools;
import com.jsql.view.component.popupmenu.JPopupTextLabel;

/**
 * Panel for statusbar
 */
@SuppressWarnings("serial")
public class StatusbarPanel extends JPanel{
	
    // Default string in place of database infos
    private final String INFO_DEFAULT_VALUE = "-";

    // Database infos
    private JPopupTextLabel labelDBVersion = new JPopupTextLabel("");
    private JPopupTextLabel labelCurrentDB = new JPopupTextLabel("");
    private JPopupTextLabel labelCurrentUser = new JPopupTextLabel("");
    private JPopupTextLabel labelAuthenticatedUser = new JPopupTextLabel("");

    // Injection methods
    public JLabel labelNormal;
    public JLabel labelErrorBased;
    public JLabel labelBlind;
    public JLabel labelTimeBased;
    
    public StatusbarPanel(){
        labelNormal = new RadioLinkStatusbar("Normal");
        labelErrorBased = new RadioLinkStatusbar("ErrorBased");
        labelBlind = new RadioLinkStatusbar("Blind");
        labelTimeBased = new RadioLinkStatusbar("TimeBased");
    	
        this.setLayout( new BoxLayout(this, BoxLayout.LINE_AXIS) );
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER),BorderFactory.createEmptyBorder(0, 5, 2, 5)));

        this.reset();

        // Panel for database infos
        JPanel infos = new JPanel();
        GroupLayout layout = new GroupLayout(infos);
        infos.setLayout(layout);
        infos.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(infos);
        this.add(Box.createHorizontalGlue());

        Font boldFont = new Font(((Font) UIManager.get("Label.font")).getName(),Font.BOLD,((Font) UIManager.get("Label.font")).getSize());
        labelDBVersion.setFont(boldFont);
        labelCurrentDB.setFont(boldFont);
        labelCurrentUser.setFont(boldFont);
        labelAuthenticatedUser.setFont(boldFont);

        labelDBVersion.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        labelDBVersion.setEditable(false);
        labelDBVersion.setBackground(this.getBackground());
        labelCurrentDB.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        labelCurrentDB.setEditable(false);
        labelCurrentDB.setBackground(this.getBackground());
        labelCurrentUser.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        labelCurrentUser.setEditable(false);
        labelCurrentUser.setBackground(this.getBackground());
        labelAuthenticatedUser.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        labelAuthenticatedUser.setEditable(false);
        labelAuthenticatedUser.setBackground(this.getBackground());

        JLabel titleDatabaseVersion = new JLabel("Database version");
        JLabel titleCurrentDB = new JLabel("Current db");
        JLabel titleCurrentUser = new JLabel("Current user");
        JLabel titleAuthenticatedUser = new JLabel("Authenticated user");

        JPanel types = new JPanel();
        types.setLayout( new BoxLayout(types, BoxLayout.PAGE_AXIS) );
        types.add(labelTimeBased);
        types.add(labelBlind);
        types.add(labelErrorBased);
        types.add(labelNormal);
        
        // Add pixels to the right to compensate width when strategy is selected 
        labelTimeBased.setPreferredSize(new Dimension(labelTimeBased.getPreferredSize().width+3,labelTimeBased.getPreferredSize().height));
        
        labelTimeBased.setToolTipText("<html><b>Slowest and less reliable method</b><br>" +
                "Boolean SQL test generates a 5s wait time for false SQL statement.<br>" +
                "<i>Read each bit of encoded characters (16 URL calls by character).</i></html>");
        labelBlind.setToolTipText("<html><b>Slow and less reliable method</b><br>" +
                "Boolean SQL test generates pageA for true SQL statement, pageB for false.<br>" +
                "<i>Read each bit of encoded characters (16 URL calls by character).</i></html>");
        labelErrorBased.setToolTipText("<html><b>Fast and accurate method</b><br>" +
                "<i>Read encoded data directly from source page.</i></html>");
        labelNormal.setToolTipText("<html><b>Fastest and accurate method</b><br>" +
                "<i>Read large encoded data directly from source page.</i></html>");

        this.add(types);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
                        .addComponent(titleDatabaseVersion)
                        .addComponent(titleCurrentDB)
                        .addComponent(titleCurrentUser)
                        .addComponent(titleAuthenticatedUser))
                .addGroup(layout.createParallelGroup()
                        .addComponent(labelDBVersion)
                        .addComponent(labelCurrentDB)
                        .addComponent(labelCurrentUser)
                        .addComponent(labelAuthenticatedUser))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(titleDatabaseVersion)
                        .addComponent(labelDBVersion))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(titleCurrentDB)
                        .addComponent(labelCurrentDB))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(titleCurrentUser)
                        .addComponent(labelCurrentUser))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(titleAuthenticatedUser)
                        .addComponent(labelAuthenticatedUser))
        );
    }

    public void reset(){
        labelDBVersion.setText(this.INFO_DEFAULT_VALUE);
        labelCurrentDB.setText(this.INFO_DEFAULT_VALUE);
        labelCurrentUser.setText(this.INFO_DEFAULT_VALUE);
        labelAuthenticatedUser.setText(this.INFO_DEFAULT_VALUE);

        labelNormal.setIcon(GUITools.SQUARE_GREY);
        labelErrorBased.setIcon(GUITools.SQUARE_GREY);
        labelBlind.setIcon(GUITools.SQUARE_GREY);
        labelTimeBased.setIcon(GUITools.SQUARE_GREY);
        
        labelNormal.setFont(GUITools.MYFONT);
		labelErrorBased.setFont(GUITools.MYFONT);
		labelBlind.setFont(GUITools.MYFONT);
		labelTimeBased.setFont(GUITools.MYFONT);
    }

    public void setInfos(String version, String database, String user, String authenticatedUser){
        labelDBVersion.setText(version);
        labelCurrentDB.setText(database);
        labelCurrentUser.setText(user);
        labelAuthenticatedUser.setText(authenticatedUser);
    }

    public void setNormalIcon(Icon icon){ labelNormal.setIcon(icon); }
    public void setErrorBasedIcon(Icon icon){ labelErrorBased.setIcon(icon); }
    public void setBlindIcon(Icon icon){ labelBlind.setIcon(icon); }
    public void setTimeBasedIcon(Icon icon){ labelTimeBased.setIcon(icon); }
}
