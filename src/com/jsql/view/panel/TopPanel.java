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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.ComponentBorder;
import com.jsql.view.component.RoundBorder;
import com.jsql.view.component.popupmenu.JPopupTextField;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel, and proxy setting in another
 */
@SuppressWarnings("serial")
public class TopPanel extends JPanel{

    // Every text input: user provides default injection data, every HTTP requests will include them
    public JPopupTextField textGET = new JPopupTextField("http://127.0.0.1/simulate_get.php?lib=", true, true);
    private JPopupTextField textPOST = new JPopupTextField(true);
    private JPopupTextField textCookie = new JPopupTextField(true);
    private JPopupTextField textHeader = new JPopupTextField(true);

    // Radio buttons, user choose the injection method, via url query string, post, cookie or header
    private RadioLinkAddressBar radioGET;
    private RadioLinkAddressBar radioPOST;
    private RadioLinkAddressBar radioCookie;
    private RadioLinkAddressBar radioHeader;
    
    private String sendMethod = "GET";

	public JLabel loader = new JLabel(GUITools.SPINNER);

    // Connection button
    public ButtonAddressBar submitAddressBar = new ButtonAddressBar();

    public TopPanel(){
        radioGET = new RadioLinkAddressBar("GET", true);
        radioPOST = new RadioLinkAddressBar("POST");
        radioCookie = new RadioLinkAddressBar("Cookie");
        radioHeader = new RadioLinkAddressBar("Header");

        // Vertical positioning for components
        this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );

        // First panel at the top, contains text components
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(7,5,5,0));
        this.add(panel);

        radioGET.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        radioPOST.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));
        radioCookie.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));
        radioHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));

        // Tooltip setting
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

        textGET.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createMatteBorder(2,2,4,0,UIManager.getColor ( "Panel.background" )),
                new RoundBorder(22,3,true)));

        textPOST.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,0,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));
        textCookie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,0,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));
        textHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,4,0,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));
        
        textGET.addActionListener(new ActionStart());
        textPOST.addActionListener(new ActionStart());
        textCookie.addActionListener(new ActionStart());
        textHeader.addActionListener(new ActionStart());
        
        submitAddressBar.setToolTipText("<html>Start injection of the address in the Location Bar</html>");
        submitAddressBar.addActionListener(new ActionStart());
        new ComponentBorder( submitAddressBar ).install( textGET );
        
        loader.setVisible(false);
        new ComponentBorder( loader ).install( textGET );
        
        // Add pixels to the right to compensate width when strategy is selected 
        radioHeader.setPreferredSize(new Dimension(radioHeader.getPreferredSize().width+3,radioHeader.getPreferredSize().height));
        radioHeader.setMinimumSize(new Dimension(radioHeader.getPreferredSize().width+3,radioHeader.getPreferredSize().height));
        radioHeader.setHorizontalAlignment(SwingConstants.RIGHT);

        final BasicArrowButton advancedButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        advancedButton.setBorderPainted(false);
        advancedButton.setOpaque(false);
        
        // Horizontal column rules
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                // Label width fixed
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
                        .addComponent(radioGET)  
                        .addComponent(radioPOST) 
                        .addComponent(radioCookie)
                        .addComponent(radioHeader))
                // Resizable textfields
                .addGroup(layout.createParallelGroup()
                        .addComponent(textGET)
                        .addComponent(textPOST)
                        .addComponent(textCookie)
                        .addComponent(textHeader))
                // Radio width fixed
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
		        		.addComponent(advancedButton))
        		);

        // Vertical line rules
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                        .addComponent(radioGET)
                        .addComponent(textGET)
                        .addComponent(advancedButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(radioPOST)
                        .addComponent(textPOST)
                        )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(radioCookie)
                        .addComponent(textCookie)
                        )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(radioHeader)
                        .addComponent(textHeader)
                        )
                );

        radioGET.setVisible(false);

        textPOST.setVisible(false);
        radioPOST.setVisible(false);

        textCookie.setVisible(false);
        radioCookie.setVisible(false);

        textHeader.setVisible(false);
        radioHeader.setVisible(false);

        advancedButton.setToolTipText("Advanced");
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Boolean toggleVisibility = advancedButton.getDirection() == BasicArrowButton.SOUTH;

                radioGET.setVisible(toggleVisibility);

                textPOST.setVisible(toggleVisibility);
                radioPOST.setVisible(toggleVisibility);

                textCookie.setVisible(toggleVisibility);
                radioCookie.setVisible(toggleVisibility);

                textHeader.setVisible(toggleVisibility);
                radioHeader.setVisible(toggleVisibility);

                advancedButton.setDirection( toggleVisibility ? BasicArrowButton.NORTH : BasicArrowButton.SOUTH );
            }
        });
    }

    /**
     * Start the connection.
     */
    private class ActionStart implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
        	if(submitAddressBar.state.equals("Connect")){
                int option = 0;
                // Ask the user confirmation if injection already built
                if(GUIMediator.model().isInjectionBuilt)
                    option = JOptionPane.showConfirmDialog(null, "Start a new injection?", "New injection", JOptionPane.OK_CANCEL_OPTION);
                
                // Then start injection
                if(!GUIMediator.model().isInjectionBuilt || option == JOptionPane.OK_OPTION){
                    submitAddressBar.setInjectionRunning();
                    GUIMediator.top().loader.setVisible(true);
                    
                    GUIMediator.controller().controlInput(
                        textGET.getText(),
                        textPOST.getText(),
                        textCookie.getText(),
                        textHeader.getText(),
                        sendMethod
                    );
                }
                
            // Injection currently running, stop the process
            }else if(submitAddressBar.state.equals("Stop")){
            	GUIMediator.top().loader.setVisible(false);
                submitAddressBar.setInjectionStopping();
                GUIMediator.model().stop();
            }
        }
    }
    
    public void setSendMethod(String sendMethod) {
		this.sendMethod = sendMethod;
	}
}
