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
import com.jsql.util.ParameterUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.panel.address.ActionEnterAddressBar;
import com.jsql.view.swing.panel.address.PanelTrailingAddress;
import com.jsql.view.swing.panel.address.ModelAddressLine;
import com.jsql.view.swing.panel.util.ButtonExpandText;
import com.jsql.view.swing.text.*;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.RadioItemPreventClose;
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
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Create panel at the top of the window.
 * Contains textfields in a panel.
 */
public class PanelAddressBar extends JPanel {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private final AtomicReference<JTextField> atomicTextFieldAddress = new AtomicReference<>();  // atomic to build dynamically
    private final AtomicReference<JTextField> atomicTextFieldRequest = new AtomicReference<>();
    private final AtomicReference<JTextField> atomicTextFieldHeader = new AtomicReference<>();

    private final AtomicReference<JRadioButton> atomicRadioRequest = new AtomicReference<>();  // atomic to build dynamically
    private final AtomicReference<JRadioButton> atomicRadioMethod = new AtomicReference<>();
    private final AtomicReference<JRadioButton> atomicRadioHeader = new AtomicReference<>();

    private static final String KEY_ADDRESS_BAR_PLACEHOLDER = "ADDRESS_BAR_PLACEHOLDER";
    private static final String BUTTON_ADVANCED = "BUTTON_ADVANCED";

    // Current injection method
    private AbstractMethodInjection methodInjection = MediatorHelper.model().getMediatorMethod().getQuery();
    private String typeRequest = StringUtil.GET;

    private final PanelTrailingAddress panelTrailingAddress;

    private boolean isAdvanceActivated = false;
    
    public PanelAddressBar() {
        var buttonGroup = new ButtonGroup();

        Stream.of(
            new ModelAddressLine(
                "URL",
                MediatorHelper.model().getMediatorMethod().getQuery(),
                "QUERYSTRING",
                this.atomicRadioRequest,
                I18nUtil.valueByKey(PanelAddressBar.KEY_ADDRESS_BAR_PLACEHOLDER),
                this.atomicTextFieldAddress
            ),
            new ModelAddressLine(
                StringUtil.GET,
                MediatorHelper.model().getMediatorMethod().getRequest(),
                "REQUEST",
                this.atomicRadioMethod,
                "e.g. key=value&injectMe=",
                this.atomicTextFieldRequest
            ),
            new ModelAddressLine(
                "Header",
                MediatorHelper.model().getMediatorMethod().getHeader(),
                "HEADER",
                this.atomicRadioHeader,
                String.format(
                    "e.g. key: value\\r\\nCookie: cKey1=cValue1; cKey2=cValue2\\r\\n%s: %s %s\\r\\ninjectMe:",
                    "Authorization",
                    "Basic",
                    "dXNlcjpwYXNz"
                ),
                this.atomicTextFieldHeader
            )
        )
        .forEach(modelLine -> {
            var i18nTooltip = String.format("FIELD_%s_TOOLTIP", modelLine.i18n);
            var tooltipTextfield = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(i18nTooltip)));
            modelLine.textfield.set(new JPopupTextField(new JTextFieldPlaceholder(
                modelLine.placeholder,
                modelLine.radio == this.atomicRadioRequest ? 18 : 0
            ) {
                @Override
                public JToolTip createToolTip() {
                    return tooltipTextfield.get();
                }
            }).getProxy());
            I18nViewUtil.addComponentForKey(i18nTooltip, tooltipTextfield.get());
            modelLine.textfield.get().addActionListener(new ActionEnterAddressBar(this));
            modelLine.textfield.get().setVisible(false);  // query will be set back to visible
            modelLine.textfield.get().setToolTipText(I18nUtil.valueByKey(i18nTooltip));

            var i18nRadio = String.format("METHOD_%s_TOOLTIP", modelLine.i18n);
            var tooltipRadio = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(i18nRadio)));
            modelLine.radio.set(
                new JRadioButton(modelLine.request) {
                    @Override
                    public JToolTip createToolTip() {
                        return tooltipRadio.get();
                    }
                }
            );
            I18nViewUtil.addComponentForKey(i18nRadio, tooltipRadio.get());
            modelLine.radio.get().setToolTipText(I18nUtil.valueByKey(i18nRadio));
            modelLine.radio.get().setSelected(modelLine.radio == this.atomicRadioRequest);
            modelLine.radio.get().setHorizontalTextPosition(SwingConstants.LEFT);
            modelLine.radio.get().setVisible(false);
            modelLine.radio.get().setBorder(BorderFactory.createEmptyBorder(
                modelLine.radio == this.atomicRadioRequest ? 0 : 6, 3, 0, 3
            ));
            modelLine.radio.get().addActionListener(e -> MediatorHelper.panelAddressBar().setMethodInjection(modelLine.method));
            buttonGroup.add(modelLine.radio.get());
        });

        this.atomicTextFieldAddress.get().setFont(UiUtil.FONT_NON_MONO_BIG);
        this.atomicTextFieldAddress.get().setName("textFieldAddress");
        this.atomicTextFieldAddress.get().setPreferredSize(new Dimension(50, 32));  // required to set correct height
        this.atomicTextFieldAddress.get().setVisible(true);
        I18nViewUtil.addComponentForKey(PanelAddressBar.KEY_ADDRESS_BAR_PLACEHOLDER, this.atomicTextFieldAddress.get());  // only i18n placeholder

        this.panelTrailingAddress = new PanelTrailingAddress(this);
        this.atomicTextFieldAddress.get().putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, this.panelTrailingAddress);
        this.atomicTextFieldAddress.get().putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, UiUtil.GLOBE.getIcon());
        this.atomicTextFieldRequest.get().putClientProperty(
            FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT,
            new ButtonExpandText(this.atomicTextFieldRequest.get())
        );
        this.atomicTextFieldHeader.get().putClientProperty(
            FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT,
            new ButtonExpandText(this.atomicTextFieldHeader.get())
        );

        this.initLayout();
    }

    private void initLayout() {
        final JLabel advancedButton = this.initAdvancedButton();
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        // First panel at the top, contains text components
        var panelTextFields = new JPanel();
        var groupLayout = new GroupLayout(panelTextFields);
        panelTextFields.setLayout(groupLayout);
        panelTextFields.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
        this.add(panelTextFields);

        final var popup = new JPopupMenu();
        final var buttonGroupMethod = new ButtonGroup();

        for (String method: new String[]{ "DELETE", StringUtil.GET, "HEAD", "OPTIONS", StringUtil.POST, "PUT", "TRACE" }) {
            final JMenuItem newMenuItem = new RadioItemPreventClose(method, StringUtil.GET.equals(method));
            newMenuItem.addActionListener(actionEvent -> {
                this.typeRequest = newMenuItem.getText();
                this.atomicRadioMethod.get().setText(this.typeRequest);
                this.atomicRadioMethod.get().requestFocusInWindow();  // required to set proper focus
            });
            popup.add(newMenuItem);
            buttonGroupMethod.add(newMenuItem);
        }

        var tooltipPanel = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey("METHOD_CUSTOM_TOOLTIP")));
        var panelCustomMethod = new JPanel(new BorderLayout()) {
            @Override
            public JToolTip createToolTip() {
                return tooltipPanel.get();
            }
        };
        I18nViewUtil.addComponentForKey("METHOD_CUSTOM_TOOLTIP", tooltipPanel.get());
        Supplier<Color> colorBackground = () -> UIManager.getColor("MenuItem.background");  // adapt to current theme
        Supplier<Color> colorSelectionBackground = () -> UIManager.getColor("MenuItem.selectionBackground");  // adapt to current theme
        panelCustomMethod.setBackground(colorBackground.get());  // required for correct color

        final var radioCustomMethod = new JRadioButton() {
            @Override
            public JToolTip createToolTip() {
                return tooltipPanel.get();
            }
        };
        radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        radioCustomMethod.setIcon(new FlatRadioButtonMenuItemIcon());
        radioCustomMethod.setBackground(colorBackground.get());  // required for correct color
        buttonGroupMethod.add(radioCustomMethod);

        final JTextField inputCustomMethod = new JPopupTextField("CUSTOM"){
            @Override
            public JToolTip createToolTip() {
                return tooltipPanel.get();
            }
        }.getProxy();
        inputCustomMethod.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radioCustomMethod.setSelected(!radioCustomMethod.isSelected());
            }
        });
        inputCustomMethod.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                PanelAddressBar.this.validate(inputCustomMethod);
            }
        });
        radioCustomMethod.addActionListener(actionEvent -> this.validate(inputCustomMethod));

        var tooltipCustomMethod = "<html>Set user defined HTTP method.<br/>" +
            "A valid method is limited to chars:<br>" +
            "!#$%&'*+-.^_`|~0123456789<br>" +
            "abcdefghijklmnopqrstuvwxyz<br>" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "</html>";
        MouseAdapter mouseAdapterSetBackground = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                panelCustomMethod.setBackground(colorSelectionBackground.get());
                radioCustomMethod.setBackground(colorSelectionBackground.get());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                panelCustomMethod.setBackground(colorBackground.get());
                radioCustomMethod.setBackground(colorBackground.get());
            }
        };
        Arrays.asList(radioCustomMethod, inputCustomMethod, panelCustomMethod).forEach(component -> {
            component.addMouseListener(mouseAdapterSetBackground);
            component.setToolTipText(tooltipCustomMethod);
        });

        panelCustomMethod.add(radioCustomMethod, BorderLayout.LINE_START);
        panelCustomMethod.add(inputCustomMethod, BorderLayout.CENTER);
        popup.insert(panelCustomMethod, popup.getComponentCount());

        this.atomicRadioMethod.get().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(popup.getComponents()).map(a -> (JComponent) a).forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                radioCustomMethod.setIcon(new FlatRadioButtonMenuItemIcon());
                radioCustomMethod.updateUI();  // required: incorrect when dark/light mode switch
                inputCustomMethod.updateUI();  // required: incorrect when dark/light mode switch
                popup.updateUI();  // required: incorrect when dark/light mode switch
                popup.applyComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()));

                if (ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))) {
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
                } else {
                    radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
                }

                // TODO Failure on arabic
                // Fix #96032: NullPointerException on show()
                try {
                    popup.show(
                        e.getComponent(),
                        ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
                            ? e.getComponent().getX() - e.getComponent().getWidth() - popup.getWidth()
                            : e.getComponent().getX(),
                        e.getComponent().getY() + e.getComponent().getHeight()
                    );
                    popup.setLocation(  // required for proper location
                        ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
                            ? e.getComponent().getLocationOnScreen().x + e.getComponent().getWidth() - popup.getWidth()
                            : e.getComponent().getLocationOnScreen().x,
                        e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight()
                    );
                } catch (NullPointerException ex) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, ex);
                }
            }
        });

        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.atomicRadioRequest.get())
                .addComponent(this.atomicRadioMethod.get())
                .addComponent(this.atomicRadioHeader.get())
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
                .addComponent(this.atomicRadioRequest.get())
                .addComponent(this.atomicTextFieldAddress.get())
                .addComponent(advancedButton)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.atomicRadioMethod.get())
                .addComponent(this.atomicTextFieldRequest.get())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.atomicRadioHeader.get())
                .addComponent(this.atomicTextFieldHeader.get())
            )
        );
    }

    private void validate(JTextField inputCustomMethod) {
        if (StringUtils.isEmpty(inputCustomMethod.getText())) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Missing custom method label");
        } else if (ParameterUtil.isInvalidName(inputCustomMethod.getText())) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, () -> String.format("Illegal method: \"%s\"", inputCustomMethod.getText()));
        } else {
            this.typeRequest = inputCustomMethod.getText();
            this.atomicRadioMethod.get().setText(this.typeRequest);
        }
    }

    private JLabel initAdvancedButton() {
        var tooltip = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(PanelAddressBar.BUTTON_ADVANCED)));
        var advancedButton = new JLabel(UiUtil.ARROW_DOWN.getIcon()) {
            @Override
            public JToolTip createToolTip() {
                return tooltip.get();
            }
        };
        advancedButton.setName("advancedButton");
        advancedButton.setToolTipText(I18nUtil.valueByKey(PanelAddressBar.BUTTON_ADVANCED));
        I18nViewUtil.addComponentForKey(PanelAddressBar.BUTTON_ADVANCED, tooltip.get());
        advancedButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isVisible = advancedButton.getIcon() == UiUtil.ARROW_DOWN.getIcon();
                PanelAddressBar.this.atomicTextFieldRequest.get().setVisible(isVisible);
                PanelAddressBar.this.atomicTextFieldHeader.get().setVisible(isVisible);
                PanelAddressBar.this.atomicRadioRequest.get().setVisible(isVisible);
                PanelAddressBar.this.atomicRadioMethod.get().setVisible(isVisible);
                PanelAddressBar.this.atomicRadioHeader.get().setVisible(isVisible);
                PanelAddressBar.this.isAdvanceActivated = isVisible;
                MediatorHelper.menubar().setVisible(isVisible);
                advancedButton.setIcon(isVisible ? UiUtil.ARROW_UP.getIcon() : UiUtil.ARROW_DOWN.getIcon());
            }
        });
        return advancedButton;
    }
    
    
    // Getter and setter

    public void setMethodInjection(AbstractMethodInjection methodInjection) {
        this.methodInjection = methodInjection;
    }

    public boolean isAdvanceActivated() {
        return !this.isAdvanceActivated;
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
        return this.typeRequest;
    }

    public JRadioButton getAtomicRadioRequest() {
        return this.atomicRadioRequest.get();
    }

    public JRadioButton getAtomicRadioMethod() {
        return this.atomicRadioMethod.get();
    }

    public JRadioButton getAtomicRadioHeader() {
        return this.atomicRadioHeader.get();
    }
}