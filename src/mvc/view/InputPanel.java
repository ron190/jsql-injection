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

public class InputPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = -1242173041381245542L;
	
	private static final boolean IS_SELECTED = true;

	private InjectionController controller;
	private InjectionModel model;
	
	private JTextField textGET = new JTextField("http://127.0.0.1/simulate_get.php?lib=");
	private JTextField textPOST = new JTextField();
	private JTextField textCookie = new JTextField();
	private JTextField textHeader = new JTextField();
	private JTextField textProxyAdress = new JTextField("127.0.0.1");
	private JTextField textProxyPort = new JTextField("8118");
	
	private JCheckBox checkboxIsProxy = new JCheckBox("Proxy", true);
	private ButtonGroup methodSelected = new ButtonGroup();
	
	private JRadioButton radioGET = new JRadioButton("", IS_SELECTED);
	private JRadioButton radioPOST = new JRadioButton();
	private JRadioButton radioCookie = new JRadioButton();
	private JRadioButton radioHeader = new JRadioButton();
	
	public JButton submitButton = new JButton("Connect", new ImageIcon(getClass().getResource("/server_go.png")));
		
	public InputPanel(final InjectionController controller, final InjectionModel model){
		this.controller = controller;
		this.model = model;
		this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
		
		JPanel connectionPanel = new JPanel();
		GroupLayout connectionLayout = new GroupLayout(connectionPanel);
		connectionPanel.setLayout(connectionLayout);
		
		connectionPanel.setBorder(
			BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Connection"),
                    BorderFactory.createEmptyBorder()
			));
	    
		this.add(connectionPanel);
		
		final JPanel settingPanel = new JPanel();
		GroupLayout settingLayout = new GroupLayout(settingPanel);
		settingPanel.setLayout(settingLayout);
		settingPanel.setVisible(false);
		
		settingPanel.setBorder(
			BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Proxy Setting"),
                    BorderFactory.createEmptyBorder()
			));
	                		
		radioGET.setActionCommand("GET");
		radioPOST.setActionCommand("POST");
		radioCookie.setActionCommand("COOKIE");
		radioHeader.setActionCommand("HEADER");

		methodSelected.add(radioGET);
        methodSelected.add(radioPOST);
        methodSelected.add(radioCookie);
        methodSelected.add(radioHeader);
        
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
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPanel.setLayout( new BoxLayout(buttonsPanel, BoxLayout.X_AXIS) );
        
		submitButton.addActionListener(this);	
		
	    final JPanel softwareInfo = new JPanel();
        softwareInfo.setLayout(new BoxLayout(softwareInfo, BoxLayout.X_AXIS));
		
		final JButton settingButton = new JButton("Show Settings", new ImageIcon(getClass().getResource("/cog.png")));
		settingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingPanel.setVisible(!settingPanel.isVisible());
				settingButton.setText(settingPanel.isVisible()?"Hide Settings":"Show Settings");
				
				softwareInfo.setVisible(!softwareInfo.isVisible());
			}
		});
		
		buttonsPanel.add(submitButton);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(settingButton);
		
		submitButton.setBorder(new RoundedCornerBorder(6,3,true));
		settingButton.setBorder(new RoundedCornerBorder(6,3,true));
		submitButton.setFont(new Font(submitButton.getFont().getName(),Font.PLAIN,submitButton.getFont().getSize()));
		settingButton.setFont(new Font(settingButton.getFont().getName(),Font.PLAIN,settingButton.getFont().getSize()));
		
		this.add(buttonsPanel);
		this.add(settingPanel);

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
		this.add(softwareInfo);
		softwareInfo.add(softNfoHTML);
		softwareInfo.setVisible(false);
		
		   JLabel labelGET = new JLabel("Url  ");
		  JLabel labelPOST = new JLabel("POST  ");
		JLabel labelCookie = new JLabel("Cookie  ");
		JLabel labelHeader = new JLabel("Header  ");
		
		JLabel labelProxyAdress = new JLabel("Proxy adress  ");
		JLabel labelProxyPort = new JLabel("Proxy port  ");

		Font plainFont = new Font(labelGET.getFont().getName(),Font.PLAIN,labelGET.getFont().getSize());
		labelGET.setFont(plainFont);
		labelPOST.setFont(plainFont);
		labelCookie.setFont(plainFont);
		labelHeader.setFont(plainFont);
		labelProxyAdress.setFont(plainFont);
		labelProxyPort.setFont(plainFont);
		checkboxIsProxy.setFont(plainFont);
		
        connectionLayout.setHorizontalGroup(
	        connectionLayout.createSequentialGroup()
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
	        				.addComponent(labelGET)
	        				.addComponent(labelPOST)
	        				.addComponent(labelCookie)
	        				.addComponent(labelHeader)
	        				.addComponent(labelProxyAdress)
	        				.addComponent(labelProxyPort))
	        		.addGroup(connectionLayout.createParallelGroup()
	        				.addComponent(textGET)
	        				.addComponent(textPOST)
	        				.addComponent(textCookie)
	        				.addComponent(textHeader)
	        				.addComponent(textProxyAdress)
	        				.addComponent(textProxyPort))
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
	        				.addComponent(radioGET)
	        				.addComponent(radioPOST)
	        				.addComponent(radioCookie)
	        				.addComponent(radioHeader)
	        				.addComponent(checkboxIsProxy))
	 	);
	        
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
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelProxyAdress)
	        				.addComponent(textProxyAdress)
	        				.addComponent(checkboxIsProxy))
	        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelProxyPort)
	        				.addComponent(textProxyPort))  	    	    	        		
		);
        
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(submitButton.getText().equals("Connect")){
			int option = 0;
			if(model.isInjectionBuilt)
				option = JOptionPane.showConfirmDialog(null, 
					"Start a new injection?", "New injection", JOptionPane.OK_CANCEL_OPTION);
			
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
		}else if(submitButton.getText().equals("Stop")){
			submitButton.setText("Stopping...");
			submitButton.setEnabled(false);
			controller.injectionModel.stop();
		} 
	}
}