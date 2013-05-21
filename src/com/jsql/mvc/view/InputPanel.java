package com.jsql.mvc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;

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
    JPopupTextField textGET = new JPopupTextField("http://127.0.0.1/simulate_get.php?lib=", true, true);
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
    
    public JLabel injectionLoader = new JLabel(new ImageIcon(getClass().getResource("/com/jsql/images/ajax-loader-mini.gif")));

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
        textGET.setToolTipText("<html><b>Website URL</b><br>" +
                "jSQL <u>always</u> injects the last parameter (in any mode: default/GET, POST, Cookie or Header).<br>" +
                "Leave last parameter blank to let jSQL search for the best value automatically:<br>" +
                "<i>Example: <<b>http://hostname/path?paramN=valueN&injectMe=</b></i><br>" +
                "Or force last parameter with your own value (use working id or well known string like 0' or -1):<br>" +
                "<i>Example: <b>http://hostname/path?paramN=valueN&injectMe=0'</b></i></html>");
        textPOST.setToolTipText("<html><b>POST parameters</b> (see formatting to use below)<br>" +
                "jSQL <u>always</u> injects the last parameter (in any mode selected).<br>" +
                "<i>Automatic search for best value: <b>paramN=valueN&injectMe=</b><br>" +
                "Force your own value, example: <b>paramN=valueN&injectMe=0'</b></i></html>");
        textCookie.setToolTipText("<html><b>Cookie parameters</b> (see formatting to use below)<br>" +
                "jSQL <u>always</u> injects the last parameter (in any mode selected).<br>" +
                "<i>Automatic search for best value: <b>paramN=valueN;injectMe=</b><br>" +
                "Force your own value, example: <b>paramN=valueN;injectMe=0'</b></i></html>");
        textHeader.setToolTipText("<html><b>Header parameters</b> (see formatting to use below)<br>" +
                "jSQL <u>always</u> injects the last parameter (in any mode selected).<br>" +
                "<i>Automatic search for best value: <b>paramN:valueN\\r\\ninjectMe:</b><br>" +
                "Force your own value, example: <b>paramN:valueN\\r\\ninjectMe:0'</b></i></html>");
        
        radioGET.setToolTipText("Inject using GET parameters");
        radioPOST.setToolTipText("Inject using POST parameters");
        radioCookie.setToolTipText("Inject using Cookie parameters");
        radioHeader.setToolTipText("Inject using Header parameters");
        
        // Url GET bar
//        textGET.addFocusListener(new java.awt.event.FocusAdapter() {
//            @Override
//            public void focusGained(FocusEvent e) {
//            	textGET.select(0, textGET.getText().length());
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//            	textGET.select(0, 0);
//            }
//        });
        
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
        injectionLoader.setVisible(false);
        
        // Buttons position
        buttonsPanel.add(submitButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
        buttonsPanel.add(injectionLoader);
        buttonsPanel.add(Box.createHorizontalGlue());
        
        // Buttons format
        submitButton.setBorder(new RoundedCornerBorder(3,3,true));
        
        this.add(buttonsPanel);

        // Labels on the left
           final JLabel labelGET = new JLabel("URL ");
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
        advancedButton.setToolTipText("Advanced");
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
//          ,0,new ImageIcon(getClass().getResource("/com/jsql/images/Retro Block Question 2.png")

            // Then start injection
            if(!model.isInjectionBuilt || option == JOptionPane.OK_OPTION){
                submitButton.setText("Stop");
                
//                Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/com/jsql/images/spinner.gif"));
//                ImageIcon spinIcon = new ImageIcon(image);
//                submitButton.setIcon(spinIcon);
                injectionLoader.setVisible(true);
                
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