package mvc.view;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import mvc.controller.InjectionController;
import mvc.model.InjectionModel;
import mvc.view.component.RoundedCornerBorder;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel, and proxy setting in another
 */
public class InputPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = -1242173041381245542L;
	
	// Constant for selected radio
	private static final boolean IS_SELECTED = true;

	// MVC controller: receive every parameters validated by user
	private InjectionController controller;
	
	// MVC model: used to test if an injection has been built
	private InjectionModel model;
	
	// Every text input: user provides default injection data, every HTTP requests will include them
	private JTextField textGET = new JTextField("http://127.0.0.1/simulate_get.php?lib=");
	private JTextField textPOST = new JTextField();
	private JTextField textCookie = new JTextField();
	private JTextField textHeader = new JTextField();
	
	// Proxy setting: IP, port, checkbox to activate proxy
	private JTextField textProxyAdress = new JTextField("127.0.0.1");
	private JTextField textProxyPort = new JTextField("8118");
	private JCheckBox checkboxIsProxy = new JCheckBox("Proxy", true);
	
	// Radio buttons, user choose the injection method, via url query string, post, cookie or header
	private JRadioButton radioGET = new JRadioButton("", IS_SELECTED);
	private JRadioButton radioPOST = new JRadioButton();
	private JRadioButton radioCookie = new JRadioButton();
	private JRadioButton radioHeader = new JRadioButton();
	// Group for radio buttons
	private ButtonGroup methodSelected = new ButtonGroup();
	
	// Connection button
	public JButton submitButton = new JButton("Connect", new ImageIcon(getClass().getResource("/images/server_go.png")));
		
	public InputPanel(final InjectionController controller, final InjectionModel model){
		this.controller = controller;
		this.model = model;
		
		// Vertical positioning for components
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		// First panel at the top, contain text components
		JPanel connectionPanel = new JPanel();
		GroupLayout connectionLayout = new GroupLayout(connectionPanel);
		connectionPanel.setLayout(connectionLayout);
		connectionPanel.setBorder(
			BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Connection"),
                    BorderFactory.createEmptyBorder()
			));
		this.add(connectionPanel);
		
		// Second panel hidden by default, contain proxy setting
		final JPanel settingPanel = new JPanel();
		GroupLayout settingLayout = new GroupLayout(settingPanel);
		settingPanel.setLayout(settingLayout);
		settingPanel.setVisible(false);
		settingPanel.setBorder(
			BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Proxy Setting"),
                    BorderFactory.createEmptyBorder()
			));
	            
		// String id, injection method
		radioGET.setActionCommand("GET");
		radioPOST.setActionCommand("POST");
		radioCookie.setActionCommand("COOKIE");
		radioHeader.setActionCommand("HEADER");
		// Add radios to the group
		methodSelected.add(radioGET);
        methodSelected.add(radioPOST);
        methodSelected.add(radioCookie);
        methodSelected.add(radioHeader);
        
        // Tooltip setting
		ToolTipManager.sharedInstance().setDismissDelay(60000);
		textGET.setToolTipText("<html>The connection url: <b>http://hostname:port/path</b><br>" +
				"Add optional GET query: <b>http://hostname:port/path?parameter1=value1&parameterN=valueN</b><br><br>" +
				"<b><u>If you know injection works with GET</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in GET query is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>http://hostname:port/path?parameter1=value1&parameter2=</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>http://hostname:port/path?parameter1=value1&parameter2=0'</b></html>");
		textPOST.setToolTipText("<html>Add optional POST data: <b>parameter1=value1&parameterN=valueN</b><br>" +
				"<br><b><u>If you know injection works with POST</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in POST data is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>parameter1=value1&parameter2=</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>parameter1=value1&parameter2=0'</b></html>");
		textCookie.setToolTipText("<html>Add optional Cookie data: <b>parameter1=value1;parameterN=valueN</b><br>" +
				"<br><b><u>If you know injection works with Cookie</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in Cookie data is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>parameter1=value1;parameter2=</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>parameter1=value1;parameter2=0'</b></html>");
		textHeader.setToolTipText("<html>Add optional Header data: <b>parameter1:value1\\r\\nparameterN:valueN</b><br>" +
				"<br><b><u>If you know injection works with Header</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in Header data is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>parameter1:value1\\r\\nparameterN:</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>parameter1:value1\\r\\nparameterN:0'</b></html>");
		
		radioGET.setToolTipText("Inject via GET data");
		radioPOST.setToolTipText("Inject via POST data");
		radioCookie.setToolTipText("Inject via cookie data");
		radioHeader.setToolTipText("Inject via header data");
		
		checkboxIsProxy.setToolTipText("Use proxy connection");        
        
		// Buttons under textfields
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Lining components
        buttonsPanel.setLayout( new BoxLayout(buttonsPanel, BoxLayout.X_AXIS) );
        
		submitButton.addActionListener(this);	
		
		// Contact infos, hidden by default
	    final JPanel softwareInfo = new JPanel();
        softwareInfo.setLayout(new BoxLayout(softwareInfo, BoxLayout.X_AXIS));
		
		final JButton settingButton = new JButton("Show Settings", new ImageIcon(getClass().getResource("/images/cog.png")));
		settingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingPanel.setVisible(!settingPanel.isVisible());
				settingButton.setText(settingPanel.isVisible()?"Hide Settings":"Show Settings");
				
				softwareInfo.setVisible(!softwareInfo.isVisible());
			}
		});
		
		// Buttons position
		buttonsPanel.add(submitButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(settingButton);
		
		// Buttons format
		submitButton.setBorder(new RoundedCornerBorder(6,3,true));
		settingButton.setBorder(new RoundedCornerBorder(6,3,true));
		submitButton.setFont(new Font(submitButton.getFont().getName(),Font.PLAIN,submitButton.getFont().getSize()));
		settingButton.setFont(new Font(settingButton.getFont().getName(),Font.PLAIN,settingButton.getFont().getSize()));
		
		this.add(buttonsPanel);
		this.add(settingPanel);

		// Contact info, use HTML text
		JEditorPane softNfoHTML = new JEditorPane("text/html", "<div style=\"text-align:center;font-size:0.9em;font-family:'Courier New'\">Contact: <a href=\"mailto://ron190@ymail.com\">ron190@ymail.com</a> - Leave a shout: <a href=\"https://groups.google.com/forum/#!forum/jsql-injection\">https://groups.google.com/forum/#!forum/jsql-injection</a></div>"); 
		softNfoHTML.setEditable(false); 
		softNfoHTML.setOpaque(false); 
		softNfoHTML.addHyperlinkListener(new HyperlinkListener() { 
			public void hyperlinkUpdate(HyperlinkEvent hle) { 
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) { 
					try {
						Desktop.getDesktop().browse(hle.getURL().toURI());
					} catch (IOException e) {
						model.sendErrorMessage(e.getMessage());
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				} 
			} 
		});  
		
		softwareInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		softwareInfo.add(softNfoHTML);
		softwareInfo.setVisible(false);
		this.add(softwareInfo);
		
		// Labels on the left
		   JLabel labelGET = new JLabel("Url  ");
		  JLabel labelPOST = new JLabel("POST  ");
		JLabel labelCookie = new JLabel("Cookie  ");
		JLabel labelHeader = new JLabel("Header  ");
		// Proxy label
		JLabel labelProxyAdress = new JLabel("Proxy adress  ");
		JLabel labelProxyPort = new JLabel("Proxy port  ");

		// Change font
		Font plainFont = new Font(labelGET.getFont().getName(),Font.PLAIN,labelGET.getFont().getSize());
		labelGET.setFont(plainFont);
		labelPOST.setFont(plainFont);
		labelCookie.setFont(plainFont);
		labelHeader.setFont(plainFont);
		labelProxyAdress.setFont(plainFont);
		labelProxyPort.setFont(plainFont);
		checkboxIsProxy.setFont(plainFont);
		
		// Horizontal column rules
        connectionLayout.setHorizontalGroup(
	        connectionLayout.createSequentialGroup()
	        		// Label width fixed
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
	        				.addComponent(labelGET)
	        				.addComponent(labelPOST)
	        				.addComponent(labelCookie)
	        				.addComponent(labelHeader))
        			// Resizable textfields
	        		.addGroup(connectionLayout.createParallelGroup()
	        				.addComponent(textGET)
	        				.addComponent(textPOST)
	        				.addComponent(textCookie)
	        				.addComponent(textHeader))
	        		// Radio width fixed
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
	        				.addComponent(radioGET)
	        				.addComponent(radioPOST)
	        				.addComponent(radioCookie)
	        				.addComponent(radioHeader))
	 	);
	        
        // Vertical line rules
        connectionLayout.setVerticalGroup(
    		connectionLayout.createSequentialGroup()
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelGET)
	        				.addComponent(textGET)
	        				.addComponent(radioGET))
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelPOST)
	        				.addComponent(textPOST)
	        				.addComponent(radioPOST))
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelCookie)
	        				.addComponent(textCookie)
	        				.addComponent(radioCookie))
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelHeader)
	        				.addComponent(textHeader)
	        				.addComponent(radioHeader))   	    	        		
		);
        
        // Proxy settings, Horizontal column rules
        settingLayout.setHorizontalGroup(
	        settingLayout.createSequentialGroup()
	        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
	        				.addComponent(labelProxyAdress)
	        				.addComponent(labelProxyPort))
	        		.addGroup(settingLayout.createParallelGroup()
	        				.addComponent(textProxyAdress)
	        				.addComponent(textProxyPort))
	        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
	        				.addComponent(checkboxIsProxy))
	 	);
        
        // Proxy settings, Vertical line rules
        settingLayout.setVerticalGroup(
    		settingLayout.createSequentialGroup()
        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        				.addComponent(labelProxyAdress)
        				.addComponent(textProxyAdress)
        				.addComponent(checkboxIsProxy))
        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        				.addComponent(labelProxyPort)
        				.addComponent(textProxyPort))
		);
	}
	
	/**
	 * Connect button action.
	 * Start new injection, unless there is already one done previously, then ask the user confirmation
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// No injection running 
		if(submitButton.getText().equals("Connect")){
			int option = 0;
			// Ask the user confirmation if injection already built
			if(model.isInjectionBuilt)
				option = JOptionPane.showConfirmDialog(null, 
					"Start a new injection?", "New injection", JOptionPane.OK_CANCEL_OPTION);
			// Then start injection
			if(!model.isInjectionBuilt || option == JOptionPane.OK_OPTION){
				submitButton.setText("Stop");
				controller.controlInput(
					textGET.getText(), 
					textPOST.getText(), 
					textCookie.getText(), 
					textHeader.getText(), 
					methodSelected.getSelection().getActionCommand(),
					checkboxIsProxy.isSelected(),
					textProxyAdress.getText(),
					textProxyPort.getText()
				);
			}
		// Injection currently running, stop the process
		}else if(submitButton.getText().equals("Stop")){
			submitButton.setText("Stopping...");
			submitButton.setEnabled(false);
			controller.injectionModel.stop();
		} 
	}
}