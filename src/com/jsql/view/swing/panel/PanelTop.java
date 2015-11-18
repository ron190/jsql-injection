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
package com.jsql.view.swing.panel;

import java.awt.Color;
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

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.radio.RadioLinkAddressBar;
import com.jsql.view.swing.text.JAddressBar;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.ui.ComponentBorder;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel.
 */
@SuppressWarnings("serial")
public class PanelTop extends JPanel {
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
    public JLabel loader = new JLabel(HelperGUI.LOADER_GIF);

    /**
     * Connection button.
     */
    public ButtonAddressBar submitAddressBar = new ButtonAddressBar();

    public boolean isExpanded = false;
    
    /**
     * Create panel at the top with textfields and radio.
     */
    public PanelTop() {
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
        this.addressBar.setToolTipText(I18n.GET_TOOLTIP);
        this.textPOST.setToolTipText(I18n.POST_TOOLTIP);
        this.textCookie.setToolTipText(I18n.COOKIE_TOOLTIP);
        this.textHeader.setToolTipText(I18n.HEADER_TOOLTIP);

        this.radioGET.setToolTipText(I18n.GET_METHOD);
        this.radioPOST.setToolTipText(I18n.POST_METHOD);
        this.radioCookie.setToolTipText(I18n.COOKIE_METHOD);
        this.radioHeader.setToolTipText(I18n.HEADER_METHOD);

        /**
         * Define UI and the left padding for addressBar
         */
        this.addressBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 2, 3, 0, HelperGUI.DEFAULT_BACKGROUND),
                BorderFactory.createLineBorder(new Color(132, 172, 221))
            ),BorderFactory.createEmptyBorder(2, 23, 2, 23))
        );

        this.textPOST.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperGUI.DEFAULT_BACKGROUND),
                HelperGUI.BLU_ROUND_BORDER));
        this.textCookie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperGUI.DEFAULT_BACKGROUND),
                HelperGUI.BLU_ROUND_BORDER));
        this.textHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperGUI.DEFAULT_BACKGROUND),
                HelperGUI.BLU_ROUND_BORDER));

        this.textPOST.setPreferredSize(new Dimension(0, 27));
        this.textPOST.setFont(this.textPOST.getFont().deriveFont(Font.PLAIN, this.textPOST.getFont().getSize() + 2));
        this.textCookie.setPreferredSize(new Dimension(0, 27));
        this.textCookie.setFont(this.textCookie.getFont().deriveFont(Font.PLAIN, this.textCookie.getFont().getSize() + 2));
        this.textHeader.setPreferredSize(new Dimension(0, 27));
        this.textHeader.setFont(this.textHeader.getFont().deriveFont(Font.PLAIN, this.textHeader.getFont().getSize() + 2));

        this.addressBar.addActionListener(new ActionEnterAddressBar());
        this.textPOST.addActionListener(new ActionEnterAddressBar());
        this.textCookie.addActionListener(new ActionEnterAddressBar());
        this.textHeader.addActionListener(new ActionEnterAddressBar());

        this.submitAddressBar.setToolTipText(I18n.BUTTON_START_INJECTION);
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

        advancedButton.setToolTipText(I18n.BUTTON_ADVANCED);
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Boolean toggleVisibility = advancedButton.getDirection() == BasicArrowButton.SOUTH;

                PanelTop.this.radioGET.setVisible(toggleVisibility);

                PanelTop.this.textPOST.setVisible(toggleVisibility);
                PanelTop.this.radioPOST.setVisible(toggleVisibility);

                PanelTop.this.textCookie.setVisible(toggleVisibility);
                PanelTop.this.radioCookie.setVisible(toggleVisibility);

                PanelTop.this.textHeader.setVisible(toggleVisibility);
                PanelTop.this.radioHeader.setVisible(toggleVisibility);
                
                isExpanded = toggleVisibility;
                MediatorGUI.menubar().setVisible(toggleVisibility);

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
            if ("Connect".equals(PanelTop.this.submitAddressBar.getState())) {
                this.startInjection();

            // Injection currently running, stop the process
            } else if ("Stop".equals(submitAddressBar.getState())) {
                stopInjection();
            }
        }
        
        protected void startInjection() {
            // Register the view to the model
            // Used by manual injection and batch query
            MediatorGUI.model().deleteObservers();
            MediatorGUI.model().addObserver(MediatorGUI.gui());
            
            int option = 0;
            // Ask the user confirmation if injection already built
            if (MediatorGUI.model().isInjectionBuilt) {
                option = JOptionPane.showConfirmDialog(null, I18n.DIALOG_NEW_INJECTION_TEXT,
                        I18n.DIALOG_NEW_INJECTION_TITLE, JOptionPane.OK_CANCEL_OPTION);
            }

            // Then start injection
            if (!MediatorGUI.model().isInjectionBuilt || option == JOptionPane.OK_OPTION) {
                PanelTop.this.submitAddressBar.setInjectionRunning();
                PanelTop.this.loader.setVisible(true);

                MediatorGUI.model().controlInput(
                    PanelTop.this.addressBar.getText(),
                    PanelTop.this.textPOST.getText(),
                    PanelTop.this.textCookie.getText(),
                    PanelTop.this.textHeader.getText(),
                    PanelTop.this.sendMethod,
                    false
                );
            }
        }
        
        private void stopInjection() {
            PanelTop.this.loader.setVisible(false);
            PanelTop.this.submitAddressBar.setInjectionStopping();
            MediatorGUI.model().stop();
        }
    }
    
    private class ActionEnterAddressBar extends ActionStart {
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
            if ("Connect".equals(PanelTop.this.submitAddressBar.getState())) {
                this.startInjection();
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
