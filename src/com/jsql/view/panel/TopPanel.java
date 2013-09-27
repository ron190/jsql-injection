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
import javax.swing.UIManager;

import com.jsql.controller.InjectionController;
import com.jsql.model.InjectionModel;
import com.jsql.view.GUITools;
import com.jsql.view.RoundBorder;
import com.jsql.view.component.popup.JPopupTextField;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel, and proxy setting in another
 */
public class TopPanel extends JPanel{
    private static final long serialVersionUID = -1242173041381245542L;

    // Constant for selected radio
    private static final boolean IS_SELECTED = true;

    // MVC controller: receive every parameters validated by user
    private InjectionController controller;

    // MVC model: used to test if an injection has been built
    private InjectionModel model;

    // Every text input: user provides default injection data, every HTTP requests will include them
    public JPopupTextField textGET = new JPopupTextField("http://127.0.0.1/simulate_get.php?lib=", true, true);
    private JPopupTextField textPOST = new JPopupTextField();
    private JPopupTextField textCookie = new JPopupTextField();
    private JPopupTextField textHeader = new JPopupTextField();

    // Radio buttons, user choose the injection method, via url query string, post, cookie or header
    private JRadioButton radioGET = new JRadioButton("", IS_SELECTED);
    private JRadioButton radioPOST = new JRadioButton();
    private JRadioButton radioCookie = new JRadioButton();
    private JRadioButton radioHeader = new JRadioButton();
    
    // Group for radio buttons
    private ButtonGroup method = new ButtonGroup();

    // Connection button
    public JButton submit = new JButton("Connect", new ImageIcon(getClass().getResource("/com/jsql/view/images/connect.png")));

    public JLabel loader = new JLabel(GUITools.SPINNER);

    public TopPanel(final InjectionController controller, final InjectionModel model){
        this.controller = controller;
        this.model = model;

        // Vertical positioning for components
        this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );

        // First panel at the top, contain text components
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(""),
                        BorderFactory.createEmptyBorder(5,5,5,5)
                        ));
        this.add(panel);

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
        method.add(radioGET);
        method.add(radioPOST);
        method.add(radioCookie);
        method.add(radioHeader);

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
                BorderFactory.createLineBorder(UIManager.getColor ( "Panel.background" ), 2),
                new RoundBorder(22,3,true)));

        textPOST.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,2,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));
        textCookie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,2,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));
        textHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,2,1,2,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));

        // Buttons under textfields
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Lining components
        buttonsPanel.setLayout( new BoxLayout(buttonsPanel, BoxLayout.X_AXIS) );

        submit.addActionListener(new ActionStart());
        loader.setVisible(false);

        // Buttons position
        buttonsPanel.add(submit);
        buttonsPanel.add(Box.createRigidArea(new Dimension(5,0)));
        buttonsPanel.add(loader);
        buttonsPanel.add(Box.createHorizontalGlue());

        // Buttons format
        submit.setBorder(GUITools.BLU_ROUND_BORDER);

        this.add(buttonsPanel);

        // Labels on the left
        final JLabel labelGET = new JLabel("URL ");
        final JLabel labelPOST = new JLabel("POST ");
        final JLabel labelCookie = new JLabel("Cookie ");
        final JLabel labelHeader = new JLabel("Header ");

        // Horizontal column rules
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                // Label width fixed
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
                        .addComponent(labelGET)
                        .addComponent(labelPOST)
                        .addComponent(labelCookie)
                        .addComponent(labelHeader))
                // Resizable textfields
                .addGroup(layout.createParallelGroup()
                        .addComponent(textGET)
                        .addComponent(textPOST)
                        .addComponent(textCookie)
                        .addComponent(textHeader))
                // Radio width fixed
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
                        .addComponent(radioGET)
                        .addComponent(radioPOST)
                        .addComponent(radioCookie)
                        .addComponent(radioHeader))
                );

        // Vertical line rules
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelGET)
                        .addComponent(textGET)
                        .addComponent(radioGET))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelPOST)
                        .addComponent(textPOST)
                        .addComponent(radioPOST))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelCookie)
                        .addComponent(textCookie)
                        .addComponent(radioCookie))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
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

        final ImageIcon upIcon = new ImageIcon(getClass().getResource("/com/jsql/view/images/arrowUp.png"));
        final ImageIcon downIcon = new ImageIcon(getClass().getResource("/com/jsql/view/images/arrowDown.png"));

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
        advancedButton.setBorder(GUITools.BLU_ROUND_BORDER);
    }

    /**
     * Start the connection.
     */
    private class ActionStart implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
            if(submit.getText().equals("Connect")){
                int option = 0;
                // Ask the user confirmation if injection already built
                if(model.isInjectionBuilt)
                    option = JOptionPane.showConfirmDialog(null,
                            "Start a new injection?", "New injection", JOptionPane.OK_CANCEL_OPTION);
                
                // Then start injection
                if(!model.isInjectionBuilt || option == JOptionPane.OK_OPTION){
                    submit.setText("Stop");
                    loader.setVisible(true);
                    
                    controller.controlInput(
                            textGET.getText(),
                            textPOST.getText(),
                            textCookie.getText(),
                            textHeader.getText(),
                            method.getSelection().getActionCommand()
                            );
                }
                
            // Injection currently running, stop the process
            }else if(submit.getText().equals("Stop")){
                submit.setText("Stopping...");
                submit.setEnabled(false);
                controller.injectionModel.stop();
            }
        }
    }
}
