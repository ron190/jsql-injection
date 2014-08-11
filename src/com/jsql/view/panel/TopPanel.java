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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.radio.RadioLinkAddressBar;
import com.jsql.view.textcomponent.JAddressBar;
import com.jsql.view.textcomponent.JPopupTextField;
import com.jsql.view.ui.ComponentBorder;
import com.jsql.view.ui.RoundBorder;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel.
 */
@SuppressWarnings("serial")
public class TopPanel extends JPanel {
    /**
     * Textfield decorated as an address bar.
     * Used by GET method.
     */
    public JTextField addressBar = new JAddressBar().getProxy();
    
    /**
     * Used by POST method.
     */
    private JTextField textPOST = new JPopupTextField("e.g. paramN=valueN&injectMe=").getProxy();
    
    /**
     * Used by COOKIE method.
     */
    private JTextField textCookie = new JPopupTextField("e.g. paramN=valueN;injectMe=").getProxy();
    
    /**
     * Used by HEADER method.
     */
    private JTextField textHeader = new JPopupTextField("e.g. paramN:valueN\\r\\ninjectMe:").getProxy();

    /**
     * Radio selected for GET injection.
     */
    private RadioLinkAddressBar radioGET;
    
    /**
     * Radio selected for POST injection.
     */
    private RadioLinkAddressBar radioPOST;
    
    /**
     * Radio selected for COOKIE injection.
     */
    private RadioLinkAddressBar radioCookie;
    
    /**
     * Radio selected for HEADER injection.
     */
    private RadioLinkAddressBar radioHeader;
    
    /**
     * Current injection method.
     */
    private String sendMethod = "GET";

    /**
     * Animated GIF displayed during injection.
     */
    public JLabel loader = new JLabel(GUITools.LOADER_GIF);

    /**
     * Connection button.
     */
    public ButtonAddressBar submitAddressBar = new ButtonAddressBar();

    /**
     * Create panel at the top with textfields and radio.
     */
    public TopPanel() {
        radioGET = new RadioLinkAddressBar("GET", true);
        radioPOST = new RadioLinkAddressBar("POST");
        radioCookie = new RadioLinkAddressBar("Cookie");
        radioHeader = new RadioLinkAddressBar("Header");

        // Vertical positioning for components
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // First panel at the top, contains text components
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
        this.add(panel);

        this.radioGET.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        this.radioPOST.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));
        this.radioCookie.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));
        this.radioHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));

        // Tooltip setting
        this.addressBar.setToolTipText(
            "<html><b>Website URL</b><br>"
            + "jSQL <u>always</u> injects the last parameter (in any mode: default/GET, POST, Cookie or Header).<br>" 
            + "Leave last parameter blank to let jSQL search for the best value automatically:<br>"
            + "<i>Example: <<b>http://hostname/path?paramN=valueN&injectMe=</b></i><br>"
            + "Or force last parameter with your own value (use working id or well known string like 0' or -1):<br>"
            + "<i>Example: <b>http://hostname/path?paramN=valueN&injectMe=0'</b></i></html>"
        );
        this.textPOST.setToolTipText("<html><b>POST parameters</b><br>" +
                "jSQL <u>always</u> injects the last parameter (in any mode selected).<br>" +
                "<i>Automatic search for best value: <b>paramN=valueN&injectMe=</b><br>" +
                "Force your own value, example: <b>paramN=valueN&injectMe=0'</b></i></html>");
        this.textCookie.setToolTipText("<html><b>Cookie parameters</b><br>" +
                "jSQL <u>always</u> injects the last parameter (in any mode selected).<br>" +
                "<i>Automatic search for best value: <b>paramN=valueN;injectMe=</b><br>" +
                "Force your own value, example: <b>paramN=valueN;injectMe=0'</b></i></html>");
        this.textHeader.setToolTipText("<html><b>Header parameters</b><br>" +
                "jSQL <u>always</u> injects the last parameter (in any mode selected).<br>" +
                "<i>Automatic search for best value: <b>paramN:valueN\\r\\ninjectMe:</b><br>" +
                "Force your own value, example: <b>paramN:valueN\\r\\ninjectMe:0'</b></i></html>");

        this.radioGET.setToolTipText("Injection using GET parameters");
        this.radioPOST.setToolTipText("Injection using POST parameters");
        this.radioCookie.setToolTipText("Injection using Cookie parameters");
        this.radioHeader.setToolTipText("Injection using Header parameters");

        /**
         * Define UI and the left padding for addressBar
         */
        this.addressBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 2, 3, 0, GUITools.DEFAULT_BACKGROUND),
                new RoundBorder(24, 3, true)));

        this.textPOST.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, GUITools.DEFAULT_BACKGROUND),
                GUITools.BLU_ROUND_BORDER));
        this.textCookie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, GUITools.DEFAULT_BACKGROUND),
                GUITools.BLU_ROUND_BORDER));
        this.textHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, GUITools.DEFAULT_BACKGROUND),
                GUITools.BLU_ROUND_BORDER));

        this.textPOST.setPreferredSize(new Dimension(0, 27));
        this.textPOST.setFont(this.textPOST.getFont().deriveFont(Font.PLAIN, this.textPOST.getFont().getSize() + 2));
        this.textCookie.setPreferredSize(new Dimension(0, 27));
        this.textCookie.setFont(this.textCookie.getFont().deriveFont(Font.PLAIN, this.textCookie.getFont().getSize() + 2));
        this.textHeader.setPreferredSize(new Dimension(0, 27));
        this.textHeader.setFont(this.textHeader.getFont().deriveFont(Font.PLAIN, this.textHeader.getFont().getSize() + 2));

        this.addressBar.addActionListener(new ActionStart());
        this.textPOST.addActionListener(new ActionStart());
        this.textCookie.addActionListener(new ActionStart());
        this.textHeader.addActionListener(new ActionStart());

        this.submitAddressBar.setToolTipText("<html>Start injection</html>");
        this.submitAddressBar.addActionListener(new ActionStart());
        new ComponentBorder(this.submitAddressBar, 17, 0).install(this.addressBar);

        this.loader.setVisible(false);
        new ComponentBorder(this.loader, 17, 1).install(this.addressBar);
        this.loader.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        // Add pixels to the right to compensate width when strategy is selected
        this.radioHeader.setPreferredSize(new Dimension(radioHeader.getPreferredSize().width + 3, radioHeader.getPreferredSize().height));
        this.radioHeader.setMinimumSize(new Dimension(radioHeader.getPreferredSize().width + 3, radioHeader.getPreferredSize().height));
        this.radioHeader.setHorizontalAlignment(SwingConstants.RIGHT);

        final BasicArrowButton advancedButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        advancedButton.setBorderPainted(false);
        advancedButton.setOpaque(false);

        // Horizontal column rules
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                // Label width fixed
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(this.radioGET)
                        .addComponent(this.radioPOST)
                        .addComponent(this.radioCookie)
                        .addComponent(this.radioHeader))
                // Resizable textfields
                .addGroup(layout.createParallelGroup()
                        .addComponent(this.addressBar)
                        .addComponent(this.textPOST)
                        .addComponent(this.textCookie)
                        .addComponent(this.textHeader))
                // Radio width fixed
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(advancedButton))
                );

        // Vertical line rules
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                        .addComponent(this.radioGET)
                        .addComponent(this.addressBar)
                        .addComponent(advancedButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.radioPOST)
                        .addComponent(this.textPOST)
                        )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.radioCookie)
                        .addComponent(this.textCookie)
                        )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.radioHeader)
                        .addComponent(this.textHeader)
                        )
                );

        this.radioGET.setVisible(false);

        this.textPOST.setVisible(false);
        this.radioPOST.setVisible(false);

        this.textCookie.setVisible(false);
        this.radioCookie.setVisible(false);

        this.textHeader.setVisible(false);
        this.radioHeader.setVisible(false);

        advancedButton.setToolTipText("Advanced");
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Boolean toggleVisibility = advancedButton.getDirection() == BasicArrowButton.SOUTH;

                TopPanel.this.radioGET.setVisible(toggleVisibility);

                TopPanel.this.textPOST.setVisible(toggleVisibility);
                TopPanel.this.radioPOST.setVisible(toggleVisibility);

                TopPanel.this.textCookie.setVisible(toggleVisibility);
                TopPanel.this.radioCookie.setVisible(toggleVisibility);

                TopPanel.this.textHeader.setVisible(toggleVisibility);
                TopPanel.this.radioHeader.setVisible(toggleVisibility);

                advancedButton.setDirection(toggleVisibility ? BasicArrowButton.NORTH : BasicArrowButton.SOUTH);
            }
        });
    }

    /**
     * Start the connection.
     */
    private class ActionStart implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
            if ("Connect".equals(TopPanel.this.submitAddressBar.getState())) {
                int option = 0;
                // Ask the user confirmation if injection already built
                if (GUIMediator.model().isInjectionBuilt) {
                    option = JOptionPane.showConfirmDialog(null, "Start a new injection?", "New injection", JOptionPane.OK_CANCEL_OPTION);
                }

                // Then start injection
                if (!GUIMediator.model().isInjectionBuilt || option == JOptionPane.OK_OPTION) {
                    TopPanel.this.submitAddressBar.setInjectionRunning();
                    TopPanel.this.loader.setVisible(true);

                    GUIMediator.model().controlInput(
                        TopPanel.this.addressBar.getText(),
                        TopPanel.this.textPOST.getText(),
                        TopPanel.this.textCookie.getText(),
                        TopPanel.this.textHeader.getText(),
                        TopPanel.this.sendMethod
                    );
                }

            // Injection currently running, stop the process
            } else if ("Stop".equals(submitAddressBar.getState())) {
                TopPanel.this.loader.setVisible(false);
                TopPanel.this.submitAddressBar.setInjectionStopping();
                GUIMediator.model().stop();
            }
        }
    }

    /**
     * Change the injection method based on selected radio.
     * @param sendMethod The new method
     */
    public void setSendMethod(String sendMethod) {
        this.sendMethod = sendMethod;
    }
}
