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
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.util.I18nUtil;
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
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel.
 */
@SuppressWarnings("serial")
public class PanelAddressBar extends JPanel {
    
    private JTextField textFieldAddress;
    private JTextField textFieldRequest;
    private JTextField textFieldHeader;
    
    private RadioLinkMethod[] radioQueryString = new RadioLinkMethod[1];
    private RadioLinkMethod[] radioRequest = new RadioLinkMethod[1];
    private RadioLinkMethod[] radioHeader = new RadioLinkMethod[1];
    
    private static final String KEY_TOOLTIP_QUERY = "FIELD_QUERYSTRING_TOOLTIP";
    private static final String KEY_TOOLTIP_REQUEST = "FIELD_REQUEST_TOOLTIP";
    private static final String KEY_TOOLTIP_HEADER = "FIELD_HEADER_TOOLTIP";

    // Current injection method.
    private AbstractMethodInjection methodInjection = MediatorHelper.model().getMediatorMethod().getQuery();

    private AddressMenuBar addressMenuBar;
    private RequestPanel requestPanel;

    private boolean isAdvanceActivated = false;
    
    public PanelAddressBar() {
        
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        final var tooltipQuery = new JToolTipI18n[]{ new JToolTipI18n(I18nUtil.valueByKey(KEY_TOOLTIP_QUERY)) };
        JTextFieldWithIcon fieldWithIcon = new JTextFieldWithIcon(I18nUtil.valueByKey("ADDRESS_BAR")) {
            
            @Override
            public JToolTip createToolTip() {
                
                JToolTip tipI18n = new JToolTipI18n(I18nUtil.valueByKey(KEY_TOOLTIP_QUERY));
                tooltipQuery[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        };
        this.textFieldAddress = new JTextFieldAddressBar(fieldWithIcon).getProxy();
        I18nViewUtil.addComponentForKey("ADDRESS_BAR", fieldWithIcon);
        I18nViewUtil.addComponentForKey(KEY_TOOLTIP_QUERY, tooltipQuery[0]);
        
        this.textFieldAddress.setName("textFieldAddress");
        
        final var tooltipRequest = new JToolTipI18n[]{ new JToolTipI18n(I18nUtil.valueByKey(KEY_TOOLTIP_REQUEST)) };
        this.textFieldRequest = new JPopupTextField(new JTextFieldPlaceholder("e.g. key=value&injectMe=") {
            
            @Override
            public JToolTip createToolTip() {
                
                JToolTip tipI18n = new JToolTipI18n(I18nUtil.valueByKey(KEY_TOOLTIP_REQUEST));
                tooltipRequest[0] = (JToolTipI18n) tipI18n;
                return tipI18n;
            }
        }).getProxy();
        I18nViewUtil.addComponentForKey(KEY_TOOLTIP_REQUEST, tooltipRequest[0]);
        
        final var tooltipHeader = new JToolTipI18n[]{ new JToolTipI18n(I18nUtil.valueByKey(KEY_TOOLTIP_HEADER)) };
        this.textFieldHeader = new JPopupTextField(new JTextFieldPlaceholder("e.g. key: value\\r\\nCookie: cKey1=cValue1; cKey2=cValue2\\r\\nAuthorization: Basic dXNlcjpwYXNz\\r\\ninjectMe:") {
            
            @Override
            public JToolTip createToolTip() {
                
                JToolTip tipI18n = new JToolTipI18n(I18nUtil.valueByKey(KEY_TOOLTIP_HEADER));
                tooltipHeader[0] = (JToolTipI18n) tipI18n;
                
                return tooltipHeader[0];
            }
        }).getProxy();
        I18nViewUtil.addComponentForKey(KEY_TOOLTIP_HEADER, tooltipHeader[0]);
        
        Stream
        .of(
            new RadioModel("GET", true, MediatorHelper.model().getMediatorMethod().getQuery(), "METHOD_QUERYSTRING_TOOLTIP", this.radioQueryString),
            new RadioModel("POST", false, MediatorHelper.model().getMediatorMethod().getRequest(), "METHOD_REQUEST_TOOLTIP", this.radioRequest),
            new RadioModel("Header", false, MediatorHelper.model().getMediatorMethod().getHeader(), "METHOD_HEADER_TOOLTIP", this.radioHeader)
        )
        .forEach(radioModel -> {
            
            var tooltip = new JToolTipI18n[]{ new JToolTipI18n(I18nUtil.valueByKey(radioModel.i18n)) };
            radioModel.radio[0] = new RadioLinkMethod(radioModel.request, radioModel.isSelected, radioModel.method) {
                
                @Override
                public JToolTip createToolTip() {
                    
                    tooltip[0] = new JToolTipI18n(I18nUtil.valueByKey(radioModel.i18n));
                    
                    return tooltip[0];
                }
            };
            I18nViewUtil.addComponentForKey(radioModel.i18n, tooltip[0]);
            
            radioModel.radio[0].setToolTipText(I18nUtil.valueByKey(radioModel.i18n));
        });
                
        this.requestPanel = new RequestPanel(this);

        this.radioQueryString[0].setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        this.requestPanel.setBorder(BorderFactory.createEmptyBorder(6, 3, 0, 3));
        this.radioHeader[0].setBorder(BorderFactory.createEmptyBorder(6, 3, 0, 3));

        // Tooltip setting
        this.textFieldAddress.setToolTipText(I18nUtil.valueByKey(KEY_TOOLTIP_QUERY));
        this.textFieldRequest.setToolTipText(I18nUtil.valueByKey(KEY_TOOLTIP_REQUEST));
        this.textFieldHeader.setToolTipText(I18nUtil.valueByKey(KEY_TOOLTIP_HEADER));

        /**
         * Define UI and the left padding for addressBar
         */
        this.textFieldAddress.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(4, 2, 3, 0, UiUtil.COLOR_DEFAULT_BACKGROUND),
                    BorderFactory.createLineBorder(UiUtil.COLOR_BLU)
                ),
                BorderFactory.createEmptyBorder(2, 23, 2, 23)
            )
        );

        this.textFieldRequest.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, UiUtil.COLOR_DEFAULT_BACKGROUND),
                UiUtil.BORDER_BLU
            )
        );
        
        this.textFieldHeader.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 2, 0, 0, UiUtil.COLOR_DEFAULT_BACKGROUND),
                UiUtil.BORDER_BLU
            )
        );

        this.textFieldRequest.setPreferredSize(new Dimension(0, 27));
        this.textFieldRequest.setFont(UiUtil.FONT_NON_MONO_BIG);
        this.textFieldHeader.setPreferredSize(new Dimension(0, 27));
        this.textFieldHeader.setFont(UiUtil.FONT_NON_MONO_BIG);

        this.textFieldAddress.addActionListener(new ActionEnterAddressBar(this));
        this.textFieldRequest.addActionListener(new ActionEnterAddressBar(this));
        this.textFieldHeader.addActionListener(new ActionEnterAddressBar(this));

        this.addressMenuBar = new AddressMenuBar(this);
        new ComponentBorder(this.addressMenuBar, 17, 0).install(this.textFieldAddress);

        this.radioQueryString[0].setVisible(false);
        this.textFieldRequest.setVisible(false);
        this.requestPanel.setVisible(false);
        this.textFieldHeader.setVisible(false);
        this.radioHeader[0].setVisible(false);
        
        this.initializeLayout();
    }

    private void initializeLayout() {
        
        final BasicArrowButton advancedButton = this.initializeAdvancedButton(this.requestPanel);
        
        // Vertical positioning for components
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        // First panel at the top, contains text components
        var panelTextFields = new JPanel();
        var layoutTextFields = new GroupLayout(panelTextFields);
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
                .addComponent(this.radioQueryString[0])
                .addComponent(this.requestPanel)
                .addComponent(this.radioHeader[0])
            )
            .addGroup(
                layoutTextFields
                .createParallelGroup()
                .addComponent(this.textFieldAddress)
                .addComponent(this.textFieldRequest)
                .addComponent(this.textFieldHeader)
            )
            .addGroup(
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
                .addComponent(this.radioQueryString[0])
                .addComponent(this.textFieldAddress)
                .addComponent(advancedButton)
            )
            .addGroup(
                layoutTextFields
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.requestPanel)
                .addComponent(this.textFieldRequest)
            )
            .addGroup(
                layoutTextFields
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.radioHeader[0])
                .addComponent(this.textFieldHeader)
            )
        );
    }

    private BasicArrowButton initializeAdvancedButton(final JPanel panelHttpProtocol) {
        
        final var advancedButton = new BasicArrowButton(SwingConstants.SOUTH);
        advancedButton.setName("advancedButton");
        advancedButton.setBorderPainted(false);
        advancedButton.setOpaque(false);

        advancedButton.setToolTipText(I18nUtil.valueByKey("BUTTON_ADVANCED"));
        advancedButton.addActionListener(actionEvent -> {
            
            boolean isVisible = advancedButton.getDirection() == SwingConstants.SOUTH;

            this.radioQueryString[0].setVisible(isVisible);

            PanelAddressBar.this.textFieldRequest.setVisible(isVisible);
            panelHttpProtocol.setVisible(isVisible);

            PanelAddressBar.this.textFieldHeader.setVisible(isVisible);
            this.radioHeader[0].setVisible(isVisible);
            
            this.isAdvanceActivated = isVisible;
            MediatorHelper.menubar().setVisible(isVisible);

            advancedButton.setDirection(isVisible ? SwingConstants.NORTH : SwingConstants.SOUTH);
        });
        
        return advancedButton;
    }
    
    private class RadioModel {
        
        private String request;
        private Boolean isSelected;
        private AbstractMethodInjection method;
        private String i18n;
        private RadioLinkMethod[] radio;

        public RadioModel(String request, Boolean isSelected, AbstractMethodInjection method, String i18n, RadioLinkMethod[] radio) {
            
            this.request = request;
            this.isSelected = isSelected;
            this.method = method;
            this.i18n = i18n;
            this.radio = radio;
        }
    }
    
    
    // Getter and setter

    public void setMethodInjection(AbstractMethodInjection methodInjection) {
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
        return this.radioQueryString[0];
    }

    public RadioLinkMethod getRadioHeader() {
        return this.radioHeader[0];
    }

    public RadioLinkMethod getRadioRequest() {
        return this.radioRequest[0];
    }

    public AbstractMethodInjection getMethodInjection() {
        return this.methodInjection;
    }

    public AddressMenuBar getAddressMenuBar() {
        return this.addressMenuBar;
    }

    public RequestPanel getRequestPanel() {
        return this.requestPanel;
    }
}