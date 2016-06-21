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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import com.jsql.i18n.I18n;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.radio.RadioLinkMethod;
import com.jsql.view.swing.text.JAddressBar;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.ui.ComponentBorder;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel.
 */
@SuppressWarnings("serial")
public class PanelAddressBar extends JPanel {
    /**
     * Textfield decorated as an address bar.
     * Used by GET method.
     */
    public JTextField urlTextField = new JAddressBar().getProxy();
    
    /**
     * Used by POST method.
     */
    private JTextField requestTextField = new JPopupTextField("e.g. key=value&injectMe=").getProxy();
    
    /**
     * Used by HEADER method.
     */
    private JTextField headerTextField = new JPopupTextField("e.g. key:value\\r\\nAuthorization: Basic dXNlcjpwYXNz\\r\\nCookie:cKey=cValue&injectMe=").getProxy();

    /**
     * Current injection method.
     */
    private String injectionMethod = "GET";

    private String requestMethod = "POST";

    /**
     * Animated GIF displayed during injection.
     */
    public JLabel loader = new JLabel(HelperGui.LOADER_GIF);

    /**
     * Connection button.
     */
    public ButtonAddressBar buttonAddressBar = new ButtonAddressBar();

    public boolean isExpanded = false;
    
    /**
     * Create panel at the top with textfields and radio.
     */
    public PanelAddressBar() {
        final RadioLinkMethod radioURL = new RadioLinkMethod("GET", true);
        final RadioLinkMethod radioMethod = new RadioLinkMethod("POST");
        final RadioLinkMethod radioHeader = new RadioLinkMethod("Header");
        
        final JPanel panelHttpProtocol = new JPanel();
        panelHttpProtocol.setLayout(new BoxLayout(panelHttpProtocol, BoxLayout.X_AXIS));
        panelHttpProtocol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        panelHttpProtocol.setBorder(null);
        
        JButton buttonRequestMethod = new BasicArrowButton(BasicArrowButton.SOUTH);
        buttonRequestMethod.setBorderPainted(false);
        buttonRequestMethod.setOpaque(false);
        
        panelHttpProtocol.add(buttonRequestMethod);
        panelHttpProtocol.add(radioMethod);
        
        final JPopupMenu popup = new JPopupMenu();
        ButtonGroup buttonGroup = new ButtonGroup();
        
        for (String protocols : new String[]{"POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"}) {
            final JMenuItem newMenuItem = new JRadioButtonMenuItem(protocols);
            newMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PanelAddressBar.this.requestMethod = newMenuItem.getText();
                    radioMethod.setText(PanelAddressBar.this.requestMethod);
                }
            });
            popup.add(newMenuItem);
            buttonGroup.add(newMenuItem);            
        }
        
        buttonRequestMethod.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popup.show(
                    e.getComponent(), 
                    e.getComponent().getX(), 
                    e.getComponent().getY() + e.getComponent().getWidth()
                );
            }
        });
                
        // Vertical positioning for components
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // First panel at the top, contains text components
        JPanel panelTextFields = new JPanel();
        GroupLayout layoutTextFields = new GroupLayout(panelTextFields);
        panelTextFields.setLayout(layoutTextFields);
        panelTextFields.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
        this.add(panelTextFields);

        radioURL.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        panelHttpProtocol.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));
        radioHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 3));

        // Tooltip setting
        this.urlTextField.setToolTipText(I18n.GET_TOOLTIP);
        this.requestTextField.setToolTipText(I18n.REQUEST_METHOD_TOOLTIP);
        this.headerTextField.setToolTipText(I18n.HEADER_TOOLTIP);

        radioURL.setToolTipText(I18n.GET_METHOD);
        radioMethod.setToolTipText(I18n.REQUEST_METHOD);
        radioHeader.setToolTipText(I18n.HEADER_METHOD);

        /**
         * Define UI and the left padding for addressBar
         */
        this.urlTextField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(4, 2, 3, 0, HelperGui.DEFAULT_BACKGROUND),
                    BorderFactory.createLineBorder(HelperGui.BLU_COLOR)
                ),
                BorderFactory.createEmptyBorder(2, 23, 2, 23)
            )
        );

        this.requestTextField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperGui.DEFAULT_BACKGROUND),
                HelperGui.BLU_ROUND_BORDER
            )
        );
        this.headerTextField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperGui.DEFAULT_BACKGROUND),
                HelperGui.BLU_ROUND_BORDER
            )
        );

        this.requestTextField.setPreferredSize(new Dimension(0, 27));
        this.requestTextField.setFont(this.requestTextField.getFont().deriveFont(Font.PLAIN, this.requestTextField.getFont().getSize() + 2));
        this.headerTextField.setPreferredSize(new Dimension(0, 27));
        this.headerTextField.setFont(this.headerTextField.getFont().deriveFont(Font.PLAIN, this.headerTextField.getFont().getSize() + 2));

        this.urlTextField.addActionListener(new ActionEnterAddressBar());
        this.requestTextField.addActionListener(new ActionEnterAddressBar());
        this.headerTextField.addActionListener(new ActionEnterAddressBar());

        this.buttonAddressBar.setToolTipText(I18n.BUTTON_START_INJECTION);
        this.buttonAddressBar.addActionListener(new ActionStart());
        ComponentBorder buttonInTextfield = new ComponentBorder(this.buttonAddressBar, 17, 0);
        buttonInTextfield.install(this.urlTextField);

        this.loader.setVisible(false);
        ComponentBorder loaderInTextfield = new ComponentBorder(this.loader, 17, 1);
        loaderInTextfield.install(this.urlTextField);
        this.loader.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        // Add pixels to the right to compensate width when strategy is selected
        radioHeader.setPreferredSize(new Dimension(radioHeader.getPreferredSize().width + 3, radioHeader.getPreferredSize().height));
        radioHeader.setMinimumSize(new Dimension(radioHeader.getPreferredSize().width + 3, radioHeader.getPreferredSize().height));
        radioHeader.setHorizontalAlignment(SwingConstants.RIGHT);

        final BasicArrowButton advancedButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        advancedButton.setBorderPainted(false);
        advancedButton.setOpaque(false);

        // Horizontal column rules
        layoutTextFields.setHorizontalGroup(
            layoutTextFields.createSequentialGroup()
                // Label width fixed
                .addGroup(
                    layoutTextFields.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(radioURL)
                        .addComponent(panelHttpProtocol)
                        .addComponent(radioHeader)
                // Resizable textfields
                ).addGroup(
                    layoutTextFields.createParallelGroup()
                        .addComponent(this.urlTextField)
                        .addComponent(this.requestTextField)
                        .addComponent(this.headerTextField)
                // Radio width fixed
                ).addGroup(
                    layoutTextFields.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(advancedButton)
                )
        );

        // Vertical line rules
        layoutTextFields.setVerticalGroup(
            layoutTextFields.createSequentialGroup()
                .addGroup(
                    layoutTextFields.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                        .addComponent(radioURL)
                        .addComponent(this.urlTextField)
                        .addComponent(advancedButton)
                ).addGroup(
                    layoutTextFields.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(panelHttpProtocol)
                        .addComponent(this.requestTextField)
                ).addGroup(
                    layoutTextFields
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(radioHeader)
                        .addComponent(this.headerTextField)
                )
        );

        radioURL.setVisible(false);

        this.requestTextField.setVisible(false);
        panelHttpProtocol.setVisible(false);

        this.headerTextField.setVisible(false);
        radioHeader.setVisible(false);

        advancedButton.setToolTipText(I18n.BUTTON_ADVANCED);
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Boolean toggleVisibility = advancedButton.getDirection() == BasicArrowButton.SOUTH;

                radioURL.setVisible(toggleVisibility);

                PanelAddressBar.this.requestTextField.setVisible(toggleVisibility);
                panelHttpProtocol.setVisible(toggleVisibility);

                PanelAddressBar.this.headerTextField.setVisible(toggleVisibility);
                radioHeader.setVisible(toggleVisibility);
                
                isExpanded = toggleVisibility;
                MediatorGui.menubar().setVisible(toggleVisibility);

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
            if ("Connect".equals(PanelAddressBar.this.buttonAddressBar.getState())) {
                this.startInjection();

            // Injection currently running, stop the process
            } else if ("Stop".equals(buttonAddressBar.getState())) {
                stopInjection();
            }
        }
        
        protected void startInjection() {
            // Register the view to the model
            // Used by manual injection and batch query
            MediatorModel.model().deleteObservers();
            MediatorModel.model().addObserver(MediatorGui.frame());
            
            int option = 0;
            // Ask the user confirmation if injection already built
            if (MediatorModel.model().isInjectionBuilt) {
                option = JOptionPane.showConfirmDialog(
                    null, 
                    I18n.DIALOG_NEW_INJECTION_TEXT,
                    I18n.DIALOG_NEW_INJECTION_TITLE, 
                    JOptionPane.OK_CANCEL_OPTION
                );
            }

            // Then start injection
            if (!MediatorModel.model().isInjectionBuilt || option == JOptionPane.OK_OPTION) {
                PanelAddressBar.this.buttonAddressBar.setInjectionRunning();
                PanelAddressBar.this.loader.setVisible(true);

                MediatorModel.model().controlInput(
                    PanelAddressBar.this.urlTextField.getText(),
                    PanelAddressBar.this.requestTextField.getText(),
                    PanelAddressBar.this.headerTextField.getText(),
                    PanelAddressBar.this.injectionMethod,
                    PanelAddressBar.this.requestMethod,
                    false
                );
            }
        }
        
        private void stopInjection() {
            PanelAddressBar.this.loader.setVisible(false);
            PanelAddressBar.this.buttonAddressBar.setInjectionStopping();
            MediatorModel.model().stop();
        }
    }
    
    private class ActionEnterAddressBar extends ActionStart {
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
            if ("Connect".equals(PanelAddressBar.this.buttonAddressBar.getState())) {
                this.startInjection();
            }
        }
    }

    /**
     * Change the injection method based on selected radio.
     * @param sendMethod The new method
     */
    public void setSendMethod(String sendMethod) {
        this.injectionMethod = sendMethod;
    }
    
    /**
     * Change the injection method based on selected radio.
     * @param injectionMethod The new method
     */
    public void setHttpProtocol(String httpProtocol) {
        this.requestMethod = httpProtocol;
    }
}
