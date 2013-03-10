package com.jsql.mvc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.jsql.mvc.controller.InjectionController;
import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.view.component.RoundedCornerBorder;
import com.jsql.mvc.view.component.popup.JPopupTextField;


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
    private JPopupTextField textGET = new JPopupTextField("http://127.0.0.1/simulate_get.php?lib=", true, true);
    private JPopupTextField textPOST = new JPopupTextField();
    private JPopupTextField textCookie = new JPopupTextField();
    private JPopupTextField textHeader = new JPopupTextField();
    
    // Radio buttons, user choose the injection method, via url query string, post, cookie or header
    private JRadioButton radioGET = new JRadioButton("", IS_SELECTED);
    private JRadioButton radioPOST = new JRadioButton();
    private JRadioButton radioCookie = new JRadioButton();
    private JRadioButton radioHeader = new JRadioButton();
    // Group for radio buttons
    private ButtonGroup methodSelected = new ButtonGroup();
    
    // Connection button
    public JButton submitButton = new JButton("Connect", new ImageIcon(getClass().getResource("/com/jsql/images/server_go.png")));
    
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
                    BorderFactory.createTitledBorder(""),
                    BorderFactory.createEmptyBorder(5,5,5,5)
            ));
        this.add(connectionPanel);
        
        radioGET.setBorder(BorderFactory.createEmptyBorder(8, 5, 0, 0));
        radioPOST.setBorder(BorderFactory.createEmptyBorder(6, 5, 0, 0));
        radioCookie.setBorder(BorderFactory.createEmptyBorder(6, 5, 0, 0));
        radioHeader.setBorder(BorderFactory.createEmptyBorder(6, 5, 0, 0));
                
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
        ToolTipManager.sharedInstance().setInitialDelay(500);   // timer before showing tooltip
        ToolTipManager.sharedInstance().setDismissDelay(30000); // timer before closing automatically tooltip
        ToolTipManager.sharedInstance().setReshowDelay(1);      // timer used when mouse move to another component, show tooltip immediately if timer is not expired
        textGET.setToolTipText("<html>The connection url: <b>http://hostname:port/path</b><br>" +
                "Add optional GET query: <b>http://hostname:port/path?parameter1=value1&parameterN=valueN</b><br><br>" +
                "<b><u>If you know injection works with GET</u></b>, set GET query and select corresponding radio (if necessary),<br><br>" +
                "<b><u>Last parameter</u></b> in GET query is the injection parameter (parameterN in example),<br><br>" +
                "You can omit the value of the last parameter and let the application find the best one:<br>" +
                "<b>http://hostname:port/path?parameter1=value1&parameterN=</b><br><br>" +
                "Or you can force the value if you know it's the most appropriate:<br>" +
                "<b>http://hostname:port/path?parameter1=value1&parameterN=0'</b></html>");
        textPOST.setToolTipText("<html>Add optional POST data: <b>parameter1=value1&parameterN=valueN</b><br>" +
                "<br><b><u>If you know injection works with POST</u></b>, set POST data and select corresponding radio on the right,<br><br>" +
                "<b><u>Last parameter</u></b> in POST data is the injection parameter (parameterN in example),<br><br>" +
                "You can omit the value of the last parameter and let the application find the best one:<br>" +
                "<b>parameter1=value1&parameterN=</b><br><br>" +
                "Or you can force the value if you know it's the most appropriate:<br>" +
                "<b>parameter1=value1&parameterN=0'</b></html>");
        textCookie.setToolTipText("<html>Add optional Cookie data: <b>parameter1=value1;parameterN=valueN</b><br>" +
                "<br><b><u>If you know injection works with Cookie</u></b>, set Cookie data and select corresponding radio on the right,<br><br>" +
                "<b><u>Last parameter</u></b> in Cookie data is the injection parameter (parameterN in example),<br><br>" +
                "You can omit the value of the last parameter and let the application find the best one:<br>" +
                "<b>parameter1=value1;parameterN=</b><br><br>" +
                "Or you can force the value if you know it's the most appropriate:<br>" +
                "<b>parameter1=value1;parameterN=0'</b></html>");
        textHeader.setToolTipText("<html>Add optional Header data: <b>parameter1:value1\\r\\nparameterN:valueN</b><br>" +
                "<br><b><u>If you know injection works with Header</u></b>, set Header data and select corresponding radio on the right,<br><br>" +
                "<b><u>Last parameter</u></b> in Header data is the injection parameter (parameterN in example),<br><br>" +
                "You can omit the value of the last parameter and let the application find the best one:<br>" +
                "<b>parameter1:value1\\r\\nparameterN:</b><br><br>" +
                "Or you can force the value if you know it's the most appropriate:<br>" +
                "<b>parameter1:value1\\r\\nparameterN:0'</b></html>");
        
        radioGET.setToolTipText("Inject via GET data");
        radioPOST.setToolTipText("Inject via POST data");
        radioCookie.setToolTipText("Inject via cookie data");
        radioHeader.setToolTipText("Inject via header data");
        
        // Url GET bar
        textGET.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                        textGET.selectAll();
                    }
                });
            }
        });
        textGET.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor ( "Panel.background" ), 2), 
                new RoundedCornerBorder(22,3,true)));
        
        textPOST.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,2,UIManager.getColor ( "Panel.background" )), 
                new RoundedCornerBorder(3,3,true)));
        textCookie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,2,UIManager.getColor ( "Panel.background" )), 
                new RoundedCornerBorder(3,3,true)));
        textHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,2,UIManager.getColor ( "Panel.background" )), 
                new RoundedCornerBorder(3,3,true)));
        
        // Buttons under textfields
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Lining components
        buttonsPanel.setLayout( new BoxLayout(buttonsPanel, BoxLayout.X_AXIS) );
        
        submitButton.addActionListener(this);
        
        // Buttons position
        buttonsPanel.add(submitButton);
        buttonsPanel.add(Box.createHorizontalGlue());
        
        // Buttons format
        submitButton.setBorder(new RoundedCornerBorder(3,3,true));
        
        this.add(buttonsPanel);

        // Labels on the left
           final JLabel labelGET = new JLabel("Url ");
          final JLabel labelPOST = new JLabel("POST ");
        final JLabel labelCookie = new JLabel("Cookie ");
        final JLabel labelHeader = new JLabel("Header ");

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
        
        labelGET.setVisible(false);
        radioGET.setVisible(false);
        
        labelPOST.setVisible(false);
        textPOST.setVisible(false);
        radioPOST.setVisible(false);
        
        labelCookie.setVisible(false);
        textCookie.setVisible(false);
        radioCookie.setVisible(false);
        
        labelHeader.setVisible(false);
        textHeader.setVisible(false);
        radioHeader.setVisible(false);        
        
        final ImageIcon upIcon = new ImageIcon(getClass().getResource("/com/jsql/images/resultset_up.png"));
        final ImageIcon downIcon = new ImageIcon(getClass().getResource("/com/jsql/images/resultset_down.png"));
        
        final JButton advancedButton = new JButton(downIcon);
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Boolean mustChange = advancedButton.getIcon() == downIcon;
                
                labelGET.setVisible(mustChange);
                
                radioGET.setVisible(mustChange);
                radioGET.setSelected(!mustChange);
                
                labelPOST.setVisible(mustChange);
                textPOST.setVisible(mustChange);
                radioPOST.setVisible(mustChange);
                
                labelCookie.setVisible(mustChange);
                textCookie.setVisible(mustChange);
                radioCookie.setVisible(mustChange);
                
                labelHeader.setVisible(mustChange);
                textHeader.setVisible(mustChange);
                radioHeader.setVisible(mustChange);
                
                advancedButton.setIcon( mustChange?upIcon:downIcon );
            }
        });
        
        buttonsPanel.add(advancedButton);
        advancedButton.setBorder(new RoundedCornerBorder(3, 3, true));
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
                    methodSelected.getSelection().getActionCommand()
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