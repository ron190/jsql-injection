/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatRadioButtonMenuItemIcon;
import com.jsql.model.injection.method.AbstractMethodInjection;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.panel.address.ActionEnterAddressBar;
import com.jsql.view.swing.panel.address.PanelTrailingAddress;
import com.jsql.view.swing.panel.address.RadioModel;
import com.jsql.view.swing.panel.util.ButtonExpandText;
import com.jsql.view.swing.text.*;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel.
 */
public class PanelAddressBar extends JPanel {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final AtomicReference<JTextField> atomicTextFieldAddress = new AtomicReference<>();  // atomic to build dynamically;
    private final AtomicReference<JTextField> atomicTextFieldRequest = new AtomicReference<>();
    private final AtomicReference<JTextField> atomicTextFieldHeader = new AtomicReference<>();

    private final AtomicReference<JRadioButton> atomicRadioRequest = new AtomicReference<>();  // atomic to build dynamically
    private final AtomicReference<JRadioButton> atomicRadioMethod = new AtomicReference<>();
    private final AtomicReference<JRadioButton> atomicRadioHeader = new AtomicReference<>();

    private static final String KEY_ADDRESS_BAR_PLACEHOLDER = "ADDRESS_BAR_PLACEHOLDER";

    // Current injection method
    private AbstractMethodInjection methodInjection = MediatorHelper.model().getMediatorMethod().getQuery();
    private String typeRequest = "GET";

    private final PanelTrailingAddress panelTrailingAddress;

    private boolean isAdvanceActivated = false;
    
    public PanelAddressBar() {
        var buttonGroup = new ButtonGroup();

        Stream.of(
            new RadioModel(
                "URL",
                true,
                MediatorHelper.model().getMediatorMethod().getQuery(),
                "METHOD_QUERYSTRING_TOOLTIP",
                atomicRadioRequest,
                "FIELD_QUERYSTRING_TOOLTIP",
                I18nUtil.valueByKey(KEY_ADDRESS_BAR_PLACEHOLDER),
                atomicTextFieldAddress,
                18
            ),
            new RadioModel(
                "GET",
                false,
                MediatorHelper.model().getMediatorMethod().getRequest(),
                "METHOD_REQUEST_TOOLTIP",
                atomicRadioMethod,
                "FIELD_REQUEST_TOOLTIP",
                "e.g. key=value&injectMe=",
                atomicTextFieldRequest,
                0
            ),
            new RadioModel(
                "Header",
                false,
                MediatorHelper.model().getMediatorMethod().getHeader(),
                "METHOD_HEADER_TOOLTIP",
                atomicRadioHeader,
                "FIELD_HEADER_TOOLTIP",
                "e.g. key: value\\r\\nCookie: cKey1=cValue1; cKey2=cValue2\\r\\nAuthorization: Basic dXNlcjpwYXNz\\r\\ninjectMe:",
                atomicTextFieldHeader,
                0
            )
        )
        .forEach(radioModel -> {
            var tooltipTextfield = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(radioModel.keyTooltipQuery)));
            radioModel.textfield.set(new JPopupTextField(new JTextFieldPlaceholder(radioModel.placeholder, radioModel.offset) {
                @Override
                public JToolTip createToolTip() {
                    tooltipTextfield.set(new JToolTipI18n(I18nUtil.valueByKey(radioModel.keyTooltipQuery)));
                    return tooltipTextfield.get();
                }
            }).getProxy());
            I18nViewUtil.addComponentForKey(radioModel.keyTooltipQuery, tooltipTextfield.get());
            radioModel.textfield.get().addActionListener(new ActionEnterAddressBar(this));
            radioModel.textfield.get().setVisible(false);  // query will be set back to visible
            radioModel.textfield.get().setToolTipText(I18nUtil.valueByKey(radioModel.keyTooltipQuery));

            var tooltipRadio = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(radioModel.i18nRadio)));
            radioModel.radio.set(
                new JRadioButton(radioModel.request) {
                    @Override
                    public JToolTip createToolTip() {
                        tooltipRadio.set(new JToolTipI18n(I18nUtil.valueByKey(radioModel.i18nRadio)));
                        return tooltipRadio.get();
                    }
                }
            );
            I18nViewUtil.addComponentForKey(radioModel.i18nRadio, tooltipRadio.get());
            radioModel.radio.get().setToolTipText(I18nUtil.valueByKey(radioModel.i18nRadio));
            radioModel.radio.get().setSelected(radioModel.isSelected);
            radioModel.radio.get().setHorizontalTextPosition(SwingConstants.LEFT);
            radioModel.radio.get().setVisible(false);
            radioModel.radio.get().addActionListener(e -> MediatorHelper.panelAddressBar().setMethodInjection(radioModel.method));
            buttonGroup.add(radioModel.radio.get());
        });

        this.atomicTextFieldAddress.get().setFont(UiUtil.FONT_NON_MONO_BIG);
        this.atomicTextFieldAddress.get().setName("textFieldAddress");
        this.atomicTextFieldAddress.get().setPreferredSize(new Dimension(50, 32));  // required to set correct height
        this.atomicTextFieldAddress.get().setVisible(true);
        I18nViewUtil.addComponentForKey(KEY_ADDRESS_BAR_PLACEHOLDER, this.atomicTextFieldAddress.get());  // only i18n placeholder

        atomicRadioRequest.get().setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        atomicRadioMethod.get().setBorder(BorderFactory.createEmptyBorder(6, 3, 0, 3));
        atomicRadioHeader.get().setBorder(BorderFactory.createEmptyBorder(6, 3, 0, 3));

        this.panelTrailingAddress = new PanelTrailingAddress(this);
        this.atomicTextFieldAddress.get().putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, this.panelTrailingAddress);
        this.atomicTextFieldAddress.get().putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, UiUtil.GLOBE.icon);
        this.atomicTextFieldRequest.get().putClientProperty(
            FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT,
            new ButtonExpandText("Add request body", this.atomicTextFieldRequest.get())
        );
        this.atomicTextFieldHeader.get().putClientProperty(
            FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT,
            new ButtonExpandText("Add header body", this.atomicTextFieldHeader.get())
        );

        this.initializeLayout();
    }

    private void initializeLayout() {
        final JLabel advancedButton = this.initializeAdvancedButton();
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        // First panel at the top, contains text components
        var panelTextFields = new JPanel();
        var groupLayout = new GroupLayout(panelTextFields);
        panelTextFields.setLayout(groupLayout);
        panelTextFields.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
        this.add(panelTextFields);

        final var popup = new JPopupMenu();
        final var buttonGroupMethod = new ButtonGroup();

        for (String method: new String[]{"DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT", "TRACE"}) {
            final JMenuItem newMenuItem = new JRadioButtonMenuItem(method, "GET".equals(method));
            newMenuItem.addActionListener(actionEvent -> {
                this.typeRequest = (newMenuItem.getText());
                atomicRadioMethod.get().setText(this.typeRequest);
                atomicRadioMethod.get().requestFocusInWindow();  // required to set proper focus
            });
            popup.add(newMenuItem);
            buttonGroupMethod.add(newMenuItem);
        }

        var panelCustomMethod = new JPanel(new BorderLayout());
        panelCustomMethod.setBackground(UIManager.getColor("MenuItem.background"));  // required for correct color

        final var radioCustomMethod = new JRadioButton();
        radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomMethod.setIcon(new FlatRadioButtonMenuItemIcon());
        radioCustomMethod.setBackground(UIManager.getColor("MenuItem.background"));  // required for correct color
        buttonGroupMethod.add(radioCustomMethod);

        final JTextField inputCustomMethod = new JPopupTextField("CUSTOM").getProxy();
        inputCustomMethod.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                radioCustomMethod.setSelected(!radioCustomMethod.isSelected());
            }
        });
        radioCustomMethod.addActionListener(actionEvent -> {
            if (StringUtils.isNotEmpty(inputCustomMethod.getText())) {
                this.typeRequest = inputCustomMethod.getText();
                atomicRadioMethod.get().setText(this.typeRequest);
            } else {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Missing request custom method label");
            }
        });

        var tooltipCustomMethod = "<html>Set user defined HTTP method.<br/>Must be one word in uppercase.</html>";
        MouseAdapter mouseAdapterSetBackground = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                panelCustomMethod.setBackground(UIManager.getColor("MenuItem.selectionBackground"));
                radioCustomMethod.setBackground(UIManager.getColor("MenuItem.selectionBackground"));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                panelCustomMethod.setBackground(UIManager.getColor("MenuItem.background"));
                radioCustomMethod.setBackground(UIManager.getColor("MenuItem.background"));
            }
        };
        Arrays.asList(radioCustomMethod, inputCustomMethod, panelCustomMethod).forEach(component -> {
            component.addMouseListener(mouseAdapterSetBackground);
            component.setToolTipText(tooltipCustomMethod);
        });

        panelCustomMethod.add(radioCustomMethod, BorderLayout.LINE_START);
        panelCustomMethod.add(inputCustomMethod, BorderLayout.CENTER);
        popup.insert(panelCustomMethod, popup.getComponentCount());

        atomicRadioMethod.get().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(popup.getComponents()).map(a -> (JComponent) a).forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                radioCustomMethod.setIcon(new FlatRadioButtonMenuItemIcon());
                radioCustomMethod.updateUI();  // required: incorrect when dark/light mode switch
                inputCustomMethod.updateUI();  // required: incorrect when dark/light mode switch
                popup.updateUI();  // required: incorrect when dark/light mode switch
                popup.applyComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));

                if (ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))) {
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
                } else {
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
                }

                popup.show(
                    e.getComponent(),
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                    ? e.getComponent().getX() - e.getComponent().getWidth() - popup.getWidth()
                    : e.getComponent().getX(),
                    e.getComponent().getY() + e.getComponent().getHeight()
                );
                popup.setLocation(  // required for proper location
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                    ? e.getComponent().getLocationOnScreen().x + e.getComponent().getWidth() - popup.getWidth()
                    : e.getComponent().getLocationOnScreen().x,
                    e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight()
                );
            }
        });

        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(atomicRadioRequest.get())
                .addComponent(atomicRadioMethod.get())
                .addComponent(atomicRadioHeader.get())
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(this.atomicTextFieldAddress.get())
                .addComponent(this.atomicTextFieldRequest.get())
                .addComponent(this.atomicTextFieldHeader.get())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(advancedButton)
            )
        );

        groupLayout.setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.CENTER, false)
                .addComponent(atomicRadioRequest.get())
                .addComponent(this.atomicTextFieldAddress.get())
                .addComponent(advancedButton)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(atomicRadioMethod.get())
                .addComponent(this.atomicTextFieldRequest.get())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(atomicRadioHeader.get())
                .addComponent(this.atomicTextFieldHeader.get())
            )
        );
    }

    private JLabel initializeAdvancedButton() {
        var advancedButton = new JLabel(UiUtil.ARROW_DOWN.icon);
        advancedButton.setName("advancedButton");
        advancedButton.setToolTipText(I18nUtil.valueByKey("BUTTON_ADVANCED"));
        advancedButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = advancedButton.getIcon() == UiUtil.ARROW_DOWN.icon;
                PanelAddressBar.this.atomicTextFieldRequest.get().setVisible(isVisible);
                PanelAddressBar.this.atomicTextFieldHeader.get().setVisible(isVisible);
                PanelAddressBar.this.atomicRadioRequest.get().setVisible(isVisible);
                PanelAddressBar.this.atomicRadioMethod.get().setVisible(isVisible);
                PanelAddressBar.this.atomicRadioHeader.get().setVisible(isVisible);
                PanelAddressBar.this.isAdvanceActivated = isVisible;
                MediatorHelper.menubar().setVisible(isVisible);
                advancedButton.setIcon(isVisible ? UiUtil.ARROW_UP.icon : UiUtil.ARROW_DOWN.icon);
            }
        });
        return advancedButton;
    }
    
    
    // Getter and setter

    public void setMethodInjection(AbstractMethodInjection methodInjection) {
        this.methodInjection = methodInjection;
    }

    public boolean isAdvanceActivated() {
        return this.isAdvanceActivated;
    }

    public JTextField getTextFieldAddress() {
        return this.atomicTextFieldAddress.get();
    }

    public JTextField getTextFieldRequest() {
        return this.atomicTextFieldRequest.get();
    }

    public JTextField getTextFieldHeader() {
        return this.atomicTextFieldHeader.get();
    }

    public AbstractMethodInjection getMethodInjection() {
        return this.methodInjection;
    }

    public PanelTrailingAddress getPanelTrailingAddress() {
        return this.panelTrailingAddress;
    }

    public String getTypeRequest() {
        return typeRequest;
    }

    public JRadioButton getAtomicRadioRequest() {
        return atomicRadioRequest.get();
    }

    public JRadioButton getAtomicRadioMethod() {
        return atomicRadioMethod.get();
    }

    public JRadioButton getAtomicRadioHeader() {
        return atomicRadioHeader.get();
    }
}