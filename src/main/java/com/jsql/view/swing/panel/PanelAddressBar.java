/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.panel;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.panel.address.ActionEnterAddressBar;
import com.jsql.view.swing.panel.address.AddressMenuBar;
import com.jsql.view.swing.panel.address.RequestPanel;
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
    
    private JTextField textFieldAddress;
    private JTextField textFieldRequest;
    private JTextField textFieldHeader;
    
    private RadioLinkMethod radioQueryString;
    private RadioLinkMethod radioMethod;
    private RadioLinkMethod radioHeader;

    /**
     * Current injection method.
     */
    private MethodInjection methodInjection = MediatorModel.model().getMediatorMethodInjection().getQuery();

    private AddressMenuBar addressMenuBar;
    private RequestPanel requestPanel;

    private boolean isAdvanceActivated = false;
    
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
        
        final JToolTipI18n[] j4 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("METHOD_QUERYSTRING_TOOLTIP"))};
        this.radioQueryString = new RadioLinkMethod("GET", true, MediatorModel.model().getMediatorMethodInjection().getQuery()){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("METHOD_QUERYSTRING_TOOLTIP"));
                j4[0] = (JToolTipI18n) tipI18n;
                return j4[0];
            }
        };
        I18nView.addComponentForKey("METHOD_QUERYSTRING_TOOLTIP", j4[0]);
        
        final JToolTipI18n[] j5 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("METHOD_REQUEST_TOOLTIP"))};
        this.radioMethod = new RadioLinkMethod("POST", MediatorModel.model().getMediatorMethodInjection().getRequest()){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("METHOD_REQUEST_TOOLTIP"));
                j5[0] = (JToolTipI18n) tipI18n;
                return j5[0];
            }
        };
        I18nView.addComponentForKey("METHOD_REQUEST_TOOLTIP", j5[0]);
        
        final JToolTipI18n[] j6 = new JToolTipI18n[]{new JToolTipI18n(I18n.valueByKey("METHOD_HEADER_TOOLTIP"))};
        this.radioHeader = new RadioLinkMethod("Header", MediatorModel.model().getMediatorMethodInjection().getHeader()){
            @Override
            public JToolTip createToolTip() {
                JToolTip tipI18n = new JToolTipI18n(I18n.valueByKey("METHOD_HEADER_TOOLTIP"));
                j6[0] = (JToolTipI18n) tipI18n;
                return j6[0];
            }
        };
        I18nView.addComponentForKey("METHOD_HEADER_TOOLTIP", j6[0]);
                
        this.requestPanel = new RequestPanel(this);

        this.radioQueryString.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        this.requestPanel.setBorder(BorderFactory.createEmptyBorder(6, 3, 0, 3));
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

        this.textFieldAddress.addActionListener(new ActionEnterAddressBar(this));
        this.textFieldRequest.addActionListener(new ActionEnterAddressBar(this));
        this.textFieldHeader.addActionListener(new ActionEnterAddressBar(this));

        this.addressMenuBar = new AddressMenuBar(this);
        new ComponentBorder(this.addressMenuBar, 17, 0).install(this.textFieldAddress);

        this.radioQueryString.setVisible(false);
        this.textFieldRequest.setVisible(false);
        this.requestPanel.setVisible(false);
        this.textFieldHeader.setVisible(false);
        this.radioHeader.setVisible(false);
        
        this.initializeLayout();
    }

    private void initializeLayout() {
        
        final BasicArrowButton advancedButton = this.initializeAdvancedButton(this.requestPanel);
        
        // Vertical positioning for components
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        // First panel at the top, contains text components
        JPanel panelTextFields = new JPanel();
        GroupLayout layoutTextFields = new GroupLayout(panelTextFields);
        panelTextFields.setLayout(layoutTextFields);
        panelTextFields.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
        this.add(panelTextFields);

        // Horizontal column rules
        layoutTextFields
        .setHorizontalGroup(
            layoutTextFields
            .createSequentialGroup()
            .addGroup(
                layoutTextFields
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.radioQueryString)
                .addComponent(this.requestPanel)
                .addComponent(this.radioHeader)
            ).addGroup(
                layoutTextFields
                .createParallelGroup()
                .addComponent(this.textFieldAddress)
                .addComponent(this.textFieldRequest)
                .addComponent(this.textFieldHeader)
            ).addGroup(
                layoutTextFields
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(advancedButton)
            )
        );

        // Vertical line rules
        layoutTextFields
        .setVerticalGroup(
            layoutTextFields
            .createSequentialGroup()
            .addGroup(
                layoutTextFields
                .createParallelGroup(GroupLayout.Alignment.CENTER, false)
                .addComponent(this.radioQueryString)
                .addComponent(this.textFieldAddress)
                .addComponent(advancedButton)
            ).addGroup(
                layoutTextFields
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.requestPanel)
                .addComponent(this.textFieldRequest)
            ).addGroup(
                layoutTextFields
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioHeader)
                .addComponent(this.textFieldHeader)
            )
        );
    }

    private BasicArrowButton initializeAdvancedButton(final JPanel panelHttpProtocol) {
        
        final BasicArrowButton advancedButton = new BasicArrowButton(SwingConstants.SOUTH);
        advancedButton.setBorderPainted(false);
        advancedButton.setOpaque(false);

        advancedButton.setToolTipText(I18n.valueByKey("BUTTON_ADVANCED"));
        advancedButton.addActionListener(actionEvent -> {
            
            boolean isVisible = advancedButton.getDirection() == SwingConstants.SOUTH;

            this.radioQueryString.setVisible(isVisible);

            PanelAddressBar.this.textFieldRequest.setVisible(isVisible);
            panelHttpProtocol.setVisible(isVisible);

            PanelAddressBar.this.textFieldHeader.setVisible(isVisible);
            this.radioHeader.setVisible(isVisible);
            
            this.isAdvanceActivated = isVisible;
            MediatorGui.menubar().setVisible(isVisible);

            advancedButton.setDirection(isVisible ? SwingConstants.NORTH : SwingConstants.SOUTH);
        });
        
        return advancedButton;
    }

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

    public boolean isAdvanceActivated() {
        return this.isAdvanceActivated;
    }

    public JTextField getTextFieldRequest() {
        return this.textFieldRequest;
    }

    public JTextField getTextFieldHeader() {
        return this.textFieldHeader;
    }

    public RadioLinkMethod getRadioQueryString() {
        return this.radioQueryString;
    }

    public RadioLinkMethod getRadioHeader() {
        return this.radioHeader;
    }

    public RadioLinkMethod getRadioMethod() {
        return this.radioMethod;
    }

    public MethodInjection getMethodInjection() {
        return this.methodInjection;
    }

    public AddressMenuBar getAddressMenuBar() {
        return this.addressMenuBar;
    }

    public RequestPanel getRequestPanel() {
        return this.requestPanel;
    }
}