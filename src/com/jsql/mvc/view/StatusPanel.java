package com.jsql.mvc.view;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jsql.mvc.view.component.popup.JPopupTextLabel;


/**
 * Panel for statusbar
 */
public class StatusPanel extends JPanel{
    private static final long serialVersionUID = -5439904812395393271L;
    
    // Default string in place of database infos
    public final String INFO_DEFAULT_VALUE = "-";
    // Display icon for injection methods on the right
    public final ImageIcon squareIcon = new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png"));

    // Database infos
    public JPopupTextLabel labelDBVersion = new JPopupTextLabel(INFO_DEFAULT_VALUE);
    public JPopupTextLabel labelCurrentDB = new JPopupTextLabel(INFO_DEFAULT_VALUE);
    public JPopupTextLabel labelCurrentUser = new JPopupTextLabel(INFO_DEFAULT_VALUE);
    public JPopupTextLabel labelAuthenticatedUser = new JPopupTextLabel(INFO_DEFAULT_VALUE);
    
    // Injection methods
    public JLabel labelNormal = new JLabel("Normal", squareIcon, SwingConstants.LEFT);
    public JLabel labelErrorBased = new JLabel("ErrorBased", squareIcon, SwingConstants.LEFT);
    public JLabel labelBlind = new JLabel("Blind", squareIcon, SwingConstants.LEFT);
    public JLabel labelTimeBased = new JLabel("TimeBased", squareIcon, SwingConstants.LEFT);
    
    public StatusPanel(){
        this.setLayout( new BoxLayout(this, BoxLayout.LINE_AXIS) );
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        // Panel for database infos
        JPanel connectionInfos = new JPanel();
        GroupLayout layout = new GroupLayout(connectionInfos);
        connectionInfos.setLayout(layout);
        connectionInfos.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(connectionInfos);
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
        
        JPanel injectionType = new JPanel();
        injectionType.setLayout( new BoxLayout(injectionType, BoxLayout.PAGE_AXIS) );
        injectionType.add(labelTimeBased);
        injectionType.add(labelBlind);
        injectionType.add(labelErrorBased);
        injectionType.add(labelNormal);
        
        labelTimeBased.setToolTipText("<html>Boolean SQL test generates a specific wait time.<br>Read each bit of encoded data (16 URL calls for one character).<br>Slowest and less reliable method</html>");
        labelBlind.setToolTipText("<html>Boolean SQL test generates pageA for true and pageB for false.<br>Read each bit of encoded data (16 URL calls for one character).<br>Slow and less reliable method</html>");
        labelErrorBased.setToolTipText("<html>Read encoded data directly from source page.<br>Fast and accurate method</html>");
        labelNormal.setToolTipText("<html>Read large encoded data directly from source page.<br>Fastest and accurate method</html>");
        
        this.add(injectionType);
        
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
}