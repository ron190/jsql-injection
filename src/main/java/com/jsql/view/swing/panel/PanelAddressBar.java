/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.panel;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.radio.RadioLinkMethod;
import com.jsql.view.swing.text.JAddressBar;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.text.JTextFieldWithIcon;
import com.jsql.view.swing.ui.ComponentBorder;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel.
 */
@SuppressWarnings("serial")
public class PanelAddressBar extends JPanel {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(PanelAddressBar.class);
    
    /**
     * Textfield decorated as an address bar.
     * Used by GET method.
     */
    public JTextField fieldUrl;
    
    /**
     * Used by POST method.
     */
    private JTextField fieldRequest;
    
    /**
     * Used by HEADER method.
     */
    private JTextField fieldHeader;

    /**
     * Current injection method.
     */
    private MethodInjection methodInjection = MethodInjection.QUERY;

    private String typeRequest = "POST";

    /**
     * Animated GIF displayed during injection.
     */
    public JLabel loader = new JLabel(HelperUi.LOADER_GIF);

    /**
     * Connection button.
     */
    public ButtonAddressBar buttonInUrl = new ButtonAddressBar();

    public boolean advanceIsActivated = false;
    
    /**
     * Create panel at the top with textfields and radio.
     */
    public class JToolTipI18n extends JToolTip {
        String textTooltip;
        
        public JToolTipI18n(String textTooltip) {
            this.textTooltip = textTooltip;
        }
        
        public void setText(String textTooltip) {
            this.textTooltip = textTooltip;
        }
        
        @Override
        public String getTipText() {
            return textTooltip;
        }
    }
    
    public PanelAddressBar() {
        final JToolTipI18n[] j = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("GET_TOOLTIP"))};
        JTextFieldWithIcon textI18nTip = new JTextFieldWithIcon(){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("GET_TOOLTIP"));
                j[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        };
        fieldUrl = new JAddressBar(textI18nTip).getProxy();
        I18n.addComponentForKey("GET_TOOLTIP", j[0]);
        
        final JToolTipI18n[] j2 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("REQUEST_METHOD_TOOLTIP"))};
        fieldRequest = new JPopupTextField(new JTextFieldPlaceholder("e.g. key=value&injectMe="){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("REQUEST_METHOD_TOOLTIP"));
                j2[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        }).getProxy();
        I18n.addComponentForKey("REQUEST_METHOD_TOOLTIP", j2[0]);
        
        final JToolTipI18n[] j3 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("HEADER_TOOLTIP"))};
        fieldHeader = new JPopupTextField(new JTextFieldPlaceholder("e.g. key:value\\r\\nCookie:cKey=cValue\\r\\nAuthorization: Basic dXNlcjpwYXNz\\r\\ninjectMe:"){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("HEADER_TOOLTIP"));
                j3[0] = (JToolTipI18n) tipI18n;
                return j3[0];
            }
        }).getProxy();
        I18n.addComponentForKey("HEADER_TOOLTIP", j3[0]);
                
        final RadioLinkMethod radioURL = new RadioLinkMethod("GET", true, MethodInjection.QUERY);
        final RadioLinkMethod radioMethod = new RadioLinkMethod("POST", MethodInjection.REQUEST);
        final RadioLinkMethod radioHeader = new RadioLinkMethod("Header", MethodInjection.HEADER);
        
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
        final ButtonGroup buttonGroup = new ButtonGroup();
        
        for (String protocol : new String[]{"OPTIONS", "HEAD", "POST", "PUT", "DELETE", "TRACE"}) {
            final JMenuItem newMenuItem = new JRadioButtonMenuItem(protocol, "POST".equals(protocol));
            newMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PanelAddressBar.this.typeRequest = newMenuItem.getText();
                    radioMethod.setText(PanelAddressBar.this.typeRequest);
                }
            });
            popup.add(newMenuItem);
            buttonGroup.add(newMenuItem);            
        }
        
        JPanel pnlmain = new JPanel(new BorderLayout());
        final JTextField field = new JPopupTextField("CUSTOM").getProxy();

        final JRadioButton a = new JRadioButton();
        a.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        a.setIcon(new CheckBoxMenuItemIconCustom());
        
        buttonGroup.add(a);
        a.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!"".equals(field.getText())) {
                    PanelAddressBar.this.typeRequest = field.getText();
                    radioMethod.setText(PanelAddressBar.this.typeRequest);
                    popup.setVisible(false);
                } else {
                    LOGGER.warn("Custom method undefined");
                }
            }
        });
      
        pnlmain.add(a, BorderLayout.WEST);
        pnlmain.add(field, BorderLayout.CENTER);
        popup.insert(pnlmain, popup.getComponentCount());
        
        buttonRequestMethod.addMouseListener(new MouseAdapter() {
            @Override
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
        this.fieldUrl.setToolTipText(I18n.valueByKey("GET_TOOLTIP"));
        this.fieldRequest.setToolTipText(I18n.valueByKey("REQUEST_METHOD_TOOLTIP"));
        this.fieldHeader.setToolTipText(I18n.valueByKey("HEADER_TOOLTIP"));

        radioURL.setToolTipText(I18n.valueByKey("GET_METHOD"));
        radioMethod.setToolTipText(I18n.valueByKey("REQUEST_METHOD"));
        radioHeader.setToolTipText(I18n.valueByKey("HEADER_METHOD"));

        /**
         * Define UI and the left padding for addressBar
         */
        this.fieldUrl.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(4, 2, 3, 0, HelperUi.DEFAULT_BACKGROUND),
                    BorderFactory.createLineBorder(HelperUi.BLU_COLOR)
                ),
                BorderFactory.createEmptyBorder(2, 23, 2, 23)
            )
        );

        this.fieldRequest.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperUi.DEFAULT_BACKGROUND),
                HelperUi.BLU_BORDER
            )
        );
        this.fieldHeader.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperUi.DEFAULT_BACKGROUND),
                HelperUi.BLU_BORDER
            )
        );

        this.fieldRequest.setPreferredSize(new Dimension(0, 27));
        this.fieldRequest.setFont(HelperUi.FONT_SEGOE_BIG);
        this.fieldHeader.setPreferredSize(new Dimension(0, 27));
        this.fieldHeader.setFont(HelperUi.FONT_SEGOE_BIG);

        this.fieldUrl.addActionListener(new ActionEnterAddressBar());
        this.fieldRequest.addActionListener(new ActionEnterAddressBar());
        this.fieldHeader.addActionListener(new ActionEnterAddressBar());

        this.buttonInUrl.setToolTipText(I18n.valueByKey("BUTTON_START_INJECTION"));
        this.buttonInUrl.addActionListener(new ActionStart());
        ComponentBorder buttonInTextfield = new ComponentBorder(this.buttonInUrl, 17, 0);
        buttonInTextfield.install(this.fieldUrl);

        this.loader.setVisible(false);
        ComponentBorder loaderInTextfield = new ComponentBorder(this.loader, 17, 1);
        loaderInTextfield.install(this.fieldUrl);
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
                        .addComponent(this.fieldUrl)
                        .addComponent(this.fieldRequest)
                        .addComponent(this.fieldHeader)
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
                        .addComponent(this.fieldUrl)
                        .addComponent(advancedButton)
                ).addGroup(
                    layoutTextFields.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(panelHttpProtocol)
                        .addComponent(this.fieldRequest)
                ).addGroup(
                    layoutTextFields
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(radioHeader)
                        .addComponent(this.fieldHeader)
                )
        );

        radioURL.setVisible(false);

        this.fieldRequest.setVisible(false);
        panelHttpProtocol.setVisible(false);

        this.fieldHeader.setVisible(false);
        radioHeader.setVisible(false);

        advancedButton.setToolTipText(I18n.valueByKey("BUTTON_ADVANCED"));
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Boolean isVisible = advancedButton.getDirection() == BasicArrowButton.SOUTH;

                radioURL.setVisible(isVisible);

                PanelAddressBar.this.fieldRequest.setVisible(isVisible);
                panelHttpProtocol.setVisible(isVisible);

                PanelAddressBar.this.fieldHeader.setVisible(isVisible);
                radioHeader.setVisible(isVisible);
                
                advanceIsActivated = isVisible;
                MediatorGui.menubar().setVisible(isVisible);

                advancedButton.setDirection(isVisible ? BasicArrowButton.NORTH : BasicArrowButton.SOUTH);
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
            if (PanelAddressBar.this.buttonInUrl.getState() == ButtonAddressBar.State.STARTABLE) {
                this.startInjection();

            // Injection currently running, stop the process
            } else if (PanelAddressBar.this.buttonInUrl.getState() == ButtonAddressBar.State.STOPPABLE) {
                this.stopInjection();
            }
        }
        
        protected void startInjection() {
            // Register the view to the model
            // Used by manual injection and batch query
            MediatorModel.model().deleteObservers();
            MediatorModel.model().addObserver(MediatorGui.frame());
            
            int option = 0;
            // Ask the user confirmation if injection already built
            if (MediatorModel.model().injectionIsFinished) {
                option = JOptionPane.showConfirmDialog(
                    null, 
                    I18n.valueByKey("DIALOG_NEW_INJECTION_TEXT"),
                    I18n.valueByKey("DIALOG_NEW_INJECTION_TITLE"), 
                    JOptionPane.OK_CANCEL_OPTION
                );
            }

            // Then start injection
            if (!MediatorModel.model().injectionIsFinished || option == JOptionPane.OK_OPTION) {
                PanelAddressBar.this.buttonInUrl.setToolTipText(I18n.valueByKey("BUTTON_STOP_INJECTION"));
                PanelAddressBar.this.buttonInUrl.setInjectionRunning();
                PanelAddressBar.this.loader.setVisible(true);

                MediatorModel.model().controlInput(
                    PanelAddressBar.this.fieldUrl.getText(),
                    PanelAddressBar.this.fieldRequest.getText(),
                    PanelAddressBar.this.fieldHeader.getText(),
                    PanelAddressBar.this.methodInjection,
                    PanelAddressBar.this.typeRequest,
                    false
                );
            }
        }
        
        private void stopInjection() {
            PanelAddressBar.this.loader.setVisible(false);
            PanelAddressBar.this.buttonInUrl.setInjectionStopping();
            PanelAddressBar.this.buttonInUrl.setToolTipText(I18n.valueByKey("BUTTON_STOPPING_INJECTION"));
            MediatorModel.model().setIsStoppedByUser(true);
        }
    }
    
    private class ActionEnterAddressBar extends ActionStart {
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
            if (PanelAddressBar.this.buttonInUrl.getState() == ButtonAddressBar.State.STARTABLE) {
                this.startInjection();
            }
        }
    }

    /**
     * Change the injection method based on selected radio.
     * @param methodInjection The new method
     */
    public void setMethodInjection(MethodInjection methodInjection) {
        this.methodInjection = methodInjection;
    }
    
    /**
     * Change the injection method based on selected radio.
     * @param methodInjection The new method
     */
    public void setHttpProtocol(String httpProtocol) {
        this.typeRequest = httpProtocol;
    }
}