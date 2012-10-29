package mvc.view;

import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class StatusPanel extends JPanel{
	private static final long serialVersionUID = -5439904812395393271L;
	
	public final String INFO_DEFAULT_VALUE = "-";
	public final ImageIcon squareIcon = new ImageIcon(getClass().getResource("/images/bullet_square_grey.png"));

	public JTextField labelDBVersion = new JTextField(INFO_DEFAULT_VALUE);
	public JTextField labelCurrentDB = new JTextField(INFO_DEFAULT_VALUE);
	public JTextField labelCurrentUser = new JTextField(INFO_DEFAULT_VALUE);
	public JTextField labelAuthenticatedUser = new JTextField(INFO_DEFAULT_VALUE);
	
	public JLabel labelNormal = new JLabel("Normal", squareIcon, SwingConstants.LEFT);
	public JLabel labelErrorBased = new JLabel("ErrorBased", squareIcon, SwingConstants.LEFT);
	public JLabel labelBlind = new JLabel("Blind", squareIcon, SwingConstants.LEFT);
	public JLabel labelTimeBased = new JLabel("TimeBased", squareIcon, SwingConstants.LEFT);
	
	public StatusPanel(){
		this.setLayout( new BoxLayout(this, BoxLayout.LINE_AXIS) );
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		JPanel connectionInfos = new JPanel();
		GroupLayout layout = new GroupLayout(connectionInfos);

		connectionInfos.setLayout(layout);
		connectionInfos.setAlignmentX(Component.LEFT_ALIGNMENT);

		this.add(connectionInfos);
		this.add(Box.createHorizontalGlue());
				
		Font boldFont = new Font(labelNormal.getFont().getName(),Font.BOLD,labelNormal.getFont().getSize());
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
		
		Font plainFont = new Font(labelNormal.getFont().getName(),Font.PLAIN,labelNormal.getFont().getSize());
		labelNormal.setFont(plainFont);
		labelErrorBased.setFont(plainFont);
		labelBlind.setFont(plainFont);
		labelTimeBased.setFont(plainFont);
		
		JLabel titleDatabaseVersion = new JLabel("Database version");
		JLabel titleCurrentDB = new JLabel("Current db");
		JLabel titleCurrentUser = new JLabel("Current user");
		JLabel titleAuthenticatedUser = new JLabel("Authenticated user");
		
		titleDatabaseVersion.setFont(plainFont);
		titleCurrentDB.setFont(plainFont);
		titleCurrentUser.setFont(plainFont);
		titleAuthenticatedUser.setFont(plainFont);
		
		JPanel injectionType = new JPanel();
		injectionType.setLayout( new BoxLayout(injectionType, BoxLayout.PAGE_AXIS) );
		injectionType.add(labelTimeBased);
		injectionType.add(labelBlind);
		injectionType.add(labelErrorBased);
		injectionType.add(labelNormal);
		
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