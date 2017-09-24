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
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

import javax.swing.AbstractButton;
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
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.panel.util.ButtonAddressBar;
import com.jsql.view.swing.panel.util.RadioMenuItemIconCustom;
import com.jsql.view.swing.radio.RadioLinkMethod;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.JTextFieldAddressBar;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.text.JTextFieldWithIcon;
import com.jsql.view.swing.text.JToolTipI18n;
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
    private static final Logger LOGGER = Logger.getRootLogger();
    
    /**
     * Textfield decorated as an address bar.
     * Used by GET method.
     */
    private JTextField textFieldAddress;
    
    /**
     * Used by POST method.
     */
    private JTextField textFieldRequest;
    
    /**
     * Used by HEADER method.
     */
    private JTextField textFieldHeader;

    /**
     * Current injection method.
     */
    private MethodInjection methodInjection = MethodInjection.QUERY;

    private String typeRequest = "POST";

    /**
     * Animated GIF displayed during injection.
     */
    private JLabel loader = new JLabel(HelperUi.ICON_LOADER_GIF);

    /**
     * Connection button.
     */
    private ButtonAddressBar buttonInUrl = new ButtonAddressBar();

    private boolean advanceIsActivated = false;
    
    private final RadioLinkMethod radioQueryString = new RadioLinkMethod("GET", true, MethodInjection.QUERY);
    private final RadioLinkMethod radioMethod = new RadioLinkMethod("POST", MethodInjection.REQUEST);
    private final RadioLinkMethod radioHeader = new RadioLinkMethod("Header", MethodInjection.HEADER);
    
    public PanelAddressBar() {
        final JToolTipI18n[] j = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("FIELD_QUERYSTRING_TOOLTIP"))};
        JTextFieldWithIcon fieldWithIcon = new JTextFieldWithIcon(I18n.valueByKey("ADDRESS_BAR")){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("FIELD_QUERYSTRING_TOOLTIP"));
                j[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        };
        this.textFieldAddress = new JTextFieldAddressBar(fieldWithIcon).getProxy();
        I18nView.addComponentForKey("ADDRESS_BAR", fieldWithIcon);
        I18nView.addComponentForKey("FIELD_QUERYSTRING_TOOLTIP", j[0]);
        
        final JToolTipI18n[] j2 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("FIELD_REQUEST_TOOLTIP"))};
        this.textFieldRequest = new JPopupTextField(new JTextFieldPlaceholder("e.g. key=value&injectMe="){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("FIELD_REQUEST_TOOLTIP"));
                j2[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        }).getProxy();
        I18nView.addComponentForKey("FIELD_REQUEST_TOOLTIP", j2[0]);
        
        final JToolTipI18n[] j3 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("FIELD_HEADER_TOOLTIP"))};
        this.textFieldHeader = new JPopupTextField(new JTextFieldPlaceholder("e.g. key: value\\r\\nCookie: cKey1=cValue1; cKey2=cValue2\\r\\nAuthorization: Basic dXNlcjpwYXNz\\r\\ninjectMe:"){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("FIELD_HEADER_TOOLTIP"));
                j3[0] = (JToolTipI18n) tipI18n;
                return j3[0];
            }
        }).getProxy();
        I18nView.addComponentForKey("FIELD_HEADER_TOOLTIP", j3[0]);
                
        final JPanel panelHttpProtocol = new JPanel();
        panelHttpProtocol.setLayout(new BoxLayout(panelHttpProtocol, BoxLayout.LINE_AXIS));
        panelHttpProtocol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        panelHttpProtocol.setBorder(null);
        
        JButton buttonRequestMethod = new BasicArrowButton(SwingConstants.SOUTH);
        buttonRequestMethod.setBorderPainted(false);
        buttonRequestMethod.setOpaque(false);
        
        panelHttpProtocol.add(buttonRequestMethod);
        panelHttpProtocol.add(this.radioMethod);
        
        final JPopupMenu popup = new JPopupMenu();
        final ButtonGroup buttonGroup = new ButtonGroup();
        
        for (String protocol : new String[]{"OPTIONS", "HEAD", "POST", "PUT", "DELETE", "TRACE"}) {
            final JMenuItem newMenuItem = new JRadioButtonMenuItem(protocol, "POST".equals(protocol));
            newMenuItem.addActionListener(actionEvent -> {
                PanelAddressBar.this.typeRequest = newMenuItem.getText();
                this.radioMethod.setText(PanelAddressBar.this.typeRequest);
            });
            popup.add(newMenuItem);
            buttonGroup.add(newMenuItem);
        }
        
        for (AbstractButton radioButton: Collections.list(buttonGroup.getElements())) {
            radioButton.setUI(
                new BasicRadioButtonMenuItemUI() {
                    @Override
                    protected void doClick(MenuSelectionManager msm) {
                        this.menuItem.doClick(0);
                    }
                }
            );
        }
        
        JPanel panelCustomMethod = new JPanel(new BorderLayout());
        final JTextField inputCustomMethod = new JPopupTextField("CUSTOM").getProxy();

        final JRadioButton radioCustomMethod = new JRadioButton();
        radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomMethod.setIcon(new RadioMenuItemIconCustom());
        
        buttonGroup.add(radioCustomMethod);
        
        radioCustomMethod.addActionListener(actionEvent -> {
            if (!"".equals(inputCustomMethod.getText())) {
                PanelAddressBar.this.typeRequest = inputCustomMethod.getText();
                this.radioMethod.setText(PanelAddressBar.this.typeRequest);
            } else {
                LOGGER.warn("Define label of Custom request method");
            }
        });
      
        panelCustomMethod.add(radioCustomMethod, BorderLayout.LINE_START);
        panelCustomMethod.add(inputCustomMethod, BorderLayout.CENTER);
        popup.insert(panelCustomMethod, popup.getComponentCount());
        
        buttonRequestMethod.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popup.applyComponentOrientation(ComponentOrientation.getOrientation(I18n.getLocaleDefault()));
                if (ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT) {
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
                } else {
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
                }
                
                popup.show(
                    e.getComponent(),
                    ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                    ? e.getComponent().getX() - e.getComponent().getWidth() - popup.getWidth()
                    : e.getComponent().getX(),
                    e.getComponent().getY() + e.getComponent().getWidth()
                );
                
                popup.setLocation(
                    ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                    ? e.getComponent().getLocationOnScreen().x + e.getComponent().getWidth() - popup.getWidth()
                    : e.getComponent().getLocationOnScreen().x,
                    e.getComponent().getLocationOnScreen().y + e.getComponent().getWidth()
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

        this.radioQueryString.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        panelHttpProtocol.setBorder(BorderFactory.createEmptyBorder(6, 3, 0, 3));
        this.radioHeader.setBorder(BorderFactory.createEmptyBorder(6, 3, 0, 3));

        // Tooltip setting
        this.textFieldAddress.setToolTipText(I18n.valueByKey("FIELD_QUERYSTRING_TOOLTIP"));
        this.textFieldRequest.setToolTipText(I18n.valueByKey("FIELD_REQUEST_TOOLTIP"));
        this.textFieldHeader.setToolTipText(I18n.valueByKey("FIELD_HEADER_TOOLTIP"));

        this.radioQueryString.setToolTipText(I18n.valueByKey("METHOD_QUERYSTRING_TOOLTIP"));
        this.radioMethod.setToolTipText(I18n.valueByKey("METHOD_REQUEST_TOOLTIP"));
        this.radioHeader.setToolTipText(I18n.valueByKey("METHOD_HEADER_TOOLTIP"));

        /**
         * Define UI and the left padding for addressBar
         */
        this.textFieldAddress.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(4, 2, 3, 0, HelperUi.COLOR_DEFAULT_BACKGROUND),
                    BorderFactory.createLineBorder(HelperUi.COLOR_BLU)
                ),
                BorderFactory.createEmptyBorder(2, 23, 2, 23)
            )
        );

        this.textFieldRequest.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperUi.COLOR_DEFAULT_BACKGROUND),
                HelperUi.BORDER_BLU
            )
        );
        this.textFieldHeader.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, HelperUi.COLOR_DEFAULT_BACKGROUND),
                HelperUi.BORDER_BLU
            )
        );

        this.textFieldRequest.setPreferredSize(new Dimension(0, 27));
        this.textFieldRequest.setFont(HelperUi.FONT_SEGOE_BIG);
        this.textFieldHeader.setPreferredSize(new Dimension(0, 27));
        this.textFieldHeader.setFont(HelperUi.FONT_SEGOE_BIG);

        this.textFieldAddress.addActionListener(new ActionEnterAddressBar());
        this.textFieldRequest.addActionListener(new ActionEnterAddressBar());
        this.textFieldHeader.addActionListener(new ActionEnterAddressBar());

        this.buttonInUrl.setToolTipText(I18n.valueByKey("BUTTON_START_TOOLTIP"));
        this.buttonInUrl.addActionListener(new ActionStart());
        ComponentBorder buttonInTextfield = new ComponentBorder(this.buttonInUrl, 17, 0);
        buttonInTextfield.install(this.textFieldAddress);

        this.loader.setVisible(false);
        ComponentBorder loaderInTextfield = new ComponentBorder(this.loader, 17, 1);
        loaderInTextfield.install(this.textFieldAddress);
        this.loader.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        final BasicArrowButton advancedButton = new BasicArrowButton(SwingConstants.SOUTH);
        advancedButton.setBorderPainted(false);
        advancedButton.setOpaque(false);

        // Horizontal column rules
        layoutTextFields.setHorizontalGroup(
            layoutTextFields.createSequentialGroup()
                // Label width fixed
                .addGroup(
                    layoutTextFields.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(this.radioQueryString)
                        .addComponent(panelHttpProtocol)
                        .addComponent(this.radioHeader)
                // Resizable textfields
                ).addGroup(
                    layoutTextFields.createParallelGroup()
                        .addComponent(this.textFieldAddress)
                        .addComponent(this.textFieldRequest)
                        .addComponent(this.textFieldHeader)
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
                        .addComponent(this.radioQueryString)
                        .addComponent(this.textFieldAddress)
                        .addComponent(advancedButton)
                ).addGroup(
                    layoutTextFields.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(panelHttpProtocol)
                        .addComponent(this.textFieldRequest)
                ).addGroup(
                    layoutTextFields
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.radioHeader)
                        .addComponent(this.textFieldHeader)
                )
        );

        this.radioQueryString.setVisible(false);

        this.textFieldRequest.setVisible(false);
        panelHttpProtocol.setVisible(false);

        this.textFieldHeader.setVisible(false);
        this.radioHeader.setVisible(false);

        advancedButton.setToolTipText(I18n.valueByKey("BUTTON_ADVANCED"));
        advancedButton.addActionListener(actionEvent -> {
            Boolean isVisible = advancedButton.getDirection() == SwingConstants.SOUTH;

            this.radioQueryString.setVisible(isVisible);

            PanelAddressBar.this.textFieldRequest.setVisible(isVisible);
            panelHttpProtocol.setVisible(isVisible);

            PanelAddressBar.this.textFieldHeader.setVisible(isVisible);
            this.radioHeader.setVisible(isVisible);
            
            this.advanceIsActivated = isVisible;
            MediatorGui.menubar().setVisible(isVisible);

            advancedButton.setDirection(isVisible ? SwingConstants.NORTH : SwingConstants.SOUTH);
        });
    }

    /**
     * Start the connection.
     */
    private class ActionStart implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
            if (PanelAddressBar.this.getButtonInUrl().getState() == StateButton.STARTABLE) {
                this.startInjection();

            // Injection currently running, stop the process
            } else if (PanelAddressBar.this.getButtonInUrl().getState() == StateButton.STOPPABLE) {
                this.stopInjection();
            }
        }
        
        protected void startInjection() {
            int option = 0;
            // Ask the user confirmation if injection already built
            if (MediatorModel.model().isInjectionAlreadyBuilt()) {
                // Fix #33930: ClassCastException on showConfirmDialog()
                // Implementation by sun.awt.image
                try {
                    option = JOptionPane.showConfirmDialog(
                        null,
                        I18n.valueByKey("DIALOG_NEW_INJECTION_TEXT"),
                        I18n.valueByKey("DIALOG_NEW_INJECTION_TITLE"),
                        JOptionPane.OK_CANCEL_OPTION
                    );
                } catch (ClassCastException e) {
                    LOGGER.error(e, e);
                }
            }

            // Then start injection
            if (!MediatorModel.model().isInjectionAlreadyBuilt() || option == JOptionPane.OK_OPTION) {
                PanelAddressBar.this.getButtonInUrl().setToolTipText(I18n.valueByKey("BUTTON_STOP_TOOLTIP"));
                PanelAddressBar.this.getButtonInUrl().setInjectionRunning();
                PanelAddressBar.this.getLoader().setVisible(true);

                // Erase everything in the view from a previous injection
                Request requests = new Request();
                requests.setMessage(Interaction.RESET_INTERFACE);
                MediatorModel.model().sendToViews(requests);

                MediatorModel.model().controlInput(
                    PanelAddressBar.this.getTextFieldAddress().getText(),
                    PanelAddressBar.this.textFieldRequest.getText(),
                    PanelAddressBar.this.textFieldHeader.getText(),
                    PanelAddressBar.this.methodInjection,
                    PanelAddressBar.this.typeRequest,
                    false
                );
            }
        }
        
        private void stopInjection() {
            PanelAddressBar.this.getLoader().setVisible(false);
            PanelAddressBar.this.getButtonInUrl().setInjectionStopping();
            PanelAddressBar.this.getButtonInUrl().setToolTipText(I18n.valueByKey("BUTTON_STOPPING_TOOLTIP"));
            MediatorModel.model().setIsStoppedByUser(true);
        }
    }
    
    private class ActionEnterAddressBar extends ActionStart {
        @Override
        public void actionPerformed(ActionEvent e) {
            // No injection running
            if (PanelAddressBar.this.getButtonInUrl().getState() == StateButton.STARTABLE) {
                this.startInjection();
            }
        }
    }
    
    // Getter and setter

    /**
     * Change the injection method based on selected radio.
     * @param methodInjection The new method
     */
    public void setMethodInjection(MethodInjection methodInjection) {
        this.methodInjection = methodInjection;
    }

    public JTextField getTextFieldAddress() {
        return this.textFieldAddress;
    }

    public JLabel getLoader() {
        return this.loader;
    }

    public ButtonAddressBar getButtonInUrl() {
        return this.buttonInUrl;
    }

    public boolean isAdvanceIsActivated() {
        return this.advanceIsActivated;
    }

    public JTextField getTextFieldRequest() {
        return this.textFieldRequest;
    }

    public JTextField getTextFieldHeader() {
        return this.textFieldHeader;
    }

    public String getTypeRequest() {
        return this.typeRequest;
    }

    public RadioLinkMethod getRadioQueryString() {
        return this.radioQueryString;
    }

    public RadioLinkMethod getRadioRequest() {
        return this.radioMethod;
    }

    public RadioLinkMethod getRadioHeader() {
        return this.radioHeader;
    }
    
}