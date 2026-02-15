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
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.RadioItemNonClosing;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Pattern;
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

    private static final String[] METHODS = {"DELETE", StringUtil.GET, "HEAD", "OPTIONS", StringUtil.POST, "PUT", "TRACE"};
    private JPopupMenu popupMethods;
    private JRadioButton radioCustomMethod;
    private JTextField inputCustomMethod;
    private AdvancedButtonAdapter advancedButtonAdapter;

    public static final String NAME_ADVANCED_BUTTON = "advancedButton";
    private static final String KEY_BUTTON_ADVANCED = "BUTTON_ADVANCED";
    private static final String KEY_ADDRESS_BAR_PLACEHOLDER = "ADDRESS_BAR_PLACEHOLDER";
    private static final String HEADER_SEPARATOR = "\\r\\n";

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
            var i18nTooltip = String.format("FIELD_%s_TOOLTIP", modelLine.i18n());
            var tooltipTextfield = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(i18nTooltip)));
            modelLine.textfield().set(new JPopupTextField(new JTextFieldPlaceholder(
                modelLine.placeholder(),
                modelLine.radio() == this.atomicRadioRequest ? 18 : 0
            ) {
                @Override
                public JToolTip createToolTip() {
                    return tooltipTextfield.get();
                }
            }).getProxy());
            I18nViewUtil.addComponentForKey(i18nTooltip, tooltipTextfield.get());
            modelLine.textfield().get().addActionListener(new ActionEnterAddressBar(this));
            modelLine.textfield().get().setVisible(false);  // query will be set back to visible
            modelLine.textfield().get().setToolTipText(I18nUtil.valueByKey(i18nTooltip));

            var i18nRadio = String.format("METHOD_%s_TOOLTIP", modelLine.i18n());
            var tooltipRadio = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(i18nRadio)));
            modelLine.radio().set(
                new JRadioButton(modelLine.request()) {
                    @Override
                    public JToolTip createToolTip() {
                        return tooltipRadio.get();
                    }
                }
            );
            I18nViewUtil.addComponentForKey(i18nRadio, tooltipRadio.get());
            modelLine.radio().get().setToolTipText(I18nUtil.valueByKey(i18nRadio));
            modelLine.radio().get().setSelected(modelLine.radio() == this.atomicRadioRequest);
            modelLine.radio().get().setHorizontalTextPosition(SwingConstants.LEFT);
            modelLine.radio().get().setVisible(false);
            modelLine.radio().get().setBorder(BorderFactory.createEmptyBorder(
                modelLine.radio() == this.atomicRadioRequest ? 0 : 6, 3, 0, 3
            ));
            modelLine.radio().get().addActionListener(e -> MediatorHelper.panelAddressBar().setMethodInjection(modelLine.method()));
            buttonGroup.add(modelLine.radio().get());
        });

        Action originalPaste = this.atomicTextFieldAddress.get().getActionMap().get(DefaultEditorKit.pasteAction);
        this.atomicTextFieldAddress.get().getActionMap().put(DefaultEditorKit.pasteAction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                    String text = (String) cb.getData(DataFlavor.stringFlavor);
                    text = text.replace("\n", "\r\n").replace("\r\r\n", "\r\n");  // restore non-standardized

                    String regexStartLine = "([^"+ PanelAddressBar.HEADER_SEPARATOR +"]+)";
                    String regexHeaders = "((?:[^"+ PanelAddressBar.HEADER_SEPARATOR +"]+"+ PanelAddressBar.HEADER_SEPARATOR +")*)";
                    String regexBody = "(.*)";
                    var matcher = Pattern.compile(
                        "(?s)" + regexStartLine + PanelAddressBar.HEADER_SEPARATOR + regexHeaders + PanelAddressBar.HEADER_SEPARATOR + regexBody
                    ).matcher(text);

                    if (matcher.find()) {
                        String startLine = matcher.group(1);
                        var matcherStartLine = Pattern.compile("([^ ]+) +([^ ]+) +([^ ]+)").matcher(startLine);
                        if (matcherStartLine.find()) {
                            LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "HTTP request detected");
                            var method = matcherStartLine.group(1);
                            var requestTarget = matcherStartLine.group(2);  // absolute-form, authority-form and asterisk-form not managed
                            // httpVersion unused
                            var headers = matcher.group(2).trim();
                            var body = matcher.group(3).trim();

                            // Configure URL
                            if (requestTarget.startsWith("/")) {  // origin-form
                                var listHeaders = Pattern.compile(PanelAddressBar.HEADER_SEPARATOR)
                                    .splitAsStream(headers)
                                    .map(keyValue -> Arrays.copyOf(keyValue.split(":"), 2))
                                    .map(keyValue -> new AbstractMap.SimpleEntry<>(
                                        keyValue[0],
                                        keyValue[1] == null ? StringUtils.EMPTY : keyValue[1]
                                    )).toList();
                                var host = listHeaders.stream()
                                    .filter(e -> "Host".equalsIgnoreCase(e.getKey())).findFirst()
                                    .orElse(new AbstractMap.SimpleEntry<>("Host", StringUtils.EMPTY));
                                if (host.getValue().isEmpty()) {
                                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Missing Host in origin form");
                                    return;
                                }
                                requestTarget = "https://" + host.getValue().trim() + requestTarget;
                            }
                            PanelAddressBar.this.atomicTextFieldAddress.get().setText(requestTarget);

                            // Configure method
                            PanelAddressBar.this.atomicRadioMethod.get().setText(method);
                            if (!Arrays.asList(PanelAddressBar.METHODS).contains(method)) {
                                PanelAddressBar.this.inputCustomMethod.setText(method);
                                PanelAddressBar.this.radioCustomMethod.setSelected(true);
                            } else {
                                Arrays.stream(PanelAddressBar.this.popupMethods.getSubElements())
                                .map(JMenuItem.class::cast)
                                .filter(jMenuItem -> method.equals(jMenuItem.getText()))
                                .findFirst()
                                .ifPresent(jMenuItem -> jMenuItem.setSelected(true));
                            }

                            PanelAddressBar.this.atomicTextFieldRequest.get().setText(
                                body
                                .replace("\n", "\\n")
                                .replace("\r", "\\r")
                            );
                            PanelAddressBar.this.atomicTextFieldHeader.get().setText(
                                headers
                                .replace("\n", "\\n")
                                .replace("\r", "\\r")
                            );

                            PanelAddressBar.this.advancedButtonAdapter.mouseClicked(true);
                        } else {
                            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect HTTP request, start line must match 'method target version': {}", startLine);
                        }
                    } else {
                        originalPaste.actionPerformed(actionEvent);
                    }
                } catch (IOException | UnsupportedFlavorException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
                }
            }
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

        this.popupMethods = new JPopupMenu();
        final var buttonGroupMethods = new ButtonGroup();

        for (String method: PanelAddressBar.METHODS) {
            final JMenuItem newMenuItem = new RadioItemNonClosing(method, StringUtil.GET.equals(method));
            newMenuItem.addActionListener(actionEvent -> {
                this.typeRequest = newMenuItem.getText();
                this.atomicRadioMethod.get().setText(this.typeRequest);
                this.atomicRadioMethod.get().requestFocusInWindow();  // required to set proper focus
            });
            this.popupMethods.add(newMenuItem);
            buttonGroupMethods.add(newMenuItem);
        }

        var tooltipMethods = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey("METHOD_CUSTOM_TOOLTIP")));
        var panelCustomMethod = new JPanel(new BorderLayout()) {
            @Override
            public JToolTip createToolTip() {
                return tooltipMethods.get();
            }
        };
        I18nViewUtil.addComponentForKey("METHOD_CUSTOM_TOOLTIP", tooltipMethods.get());
        Supplier<Color> colorBackground = () -> UIManager.getColor("MenuItem.background");  // adapt to current theme
        Supplier<Color> colorSelectionBackground = () -> UIManager.getColor("MenuItem.selectionBackground");  // adapt to current theme
        panelCustomMethod.setBackground(colorBackground.get());  // required for correct color

        this.radioCustomMethod = new JRadioButton() {
            @Override
            public JToolTip createToolTip() {
                return tooltipMethods.get();
            }
        };
        this.radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        this.radioCustomMethod.setIcon(new FlatRadioButtonMenuItemIcon());
        this.radioCustomMethod.setBackground(colorBackground.get());  // required for correct color
        buttonGroupMethods.add(this.radioCustomMethod);

        this.inputCustomMethod = new JPopupTextField("CUSTOM"){
            @Override
            public JToolTip createToolTip() {
                return tooltipMethods.get();
            }
        }.getProxy();
        this.radioCustomMethod.addActionListener(actionEvent -> this.validate(this.inputCustomMethod));

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
                PanelAddressBar.this.radioCustomMethod.setBackground(colorSelectionBackground.get());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                panelCustomMethod.setBackground(colorBackground.get());
                PanelAddressBar.this.radioCustomMethod.setBackground(colorBackground.get());
            }
        };
        Arrays.asList(this.radioCustomMethod, this.inputCustomMethod, panelCustomMethod).forEach(component -> {
            component.addMouseListener(mouseAdapterSetBackground);
            component.setToolTipText(tooltipCustomMethod);
        });

        panelCustomMethod.add(this.radioCustomMethod, BorderLayout.LINE_START);
        panelCustomMethod.add(this.inputCustomMethod, BorderLayout.CENTER);
        this.popupMethods.insert(panelCustomMethod, this.popupMethods.getComponentCount());

        this.atomicRadioMethod.get().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(PanelAddressBar.this.popupMethods.getComponents()).map(a -> (JComponent) a).forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                PanelAddressBar.this.radioCustomMethod.setIcon(new FlatRadioButtonMenuItemIcon());
                PanelAddressBar.this.radioCustomMethod.updateUI();  // required: incorrect when dark/light mode switch
                PanelAddressBar.this.inputCustomMethod.updateUI();  // required: incorrect when dark/light mode switch
                PanelAddressBar.this.popupMethods.updateUI();  // required: incorrect when dark/light mode switch

                if (ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))) {
                    PanelAddressBar.this.radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
                } else {
                    PanelAddressBar.this.radioCustomMethod.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
                }

                PanelAddressBar.this.popupMethods.show(
                    e.getComponent(),
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
                        ? e.getComponent().getX() - e.getComponent().getWidth() - PanelAddressBar.this.popupMethods.getWidth()
                        : e.getComponent().getX(),
                    e.getComponent().getY() + e.getComponent().getHeight()
                );
                PanelAddressBar.this.popupMethods.setLocation(  // required for proper location
                    ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
                        ? e.getComponent().getLocationOnScreen().x + e.getComponent().getWidth() - PanelAddressBar.this.popupMethods.getWidth()
                        : e.getComponent().getLocationOnScreen().x,
                    e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight()
                );

                // Orientation set after popup placement, Fix #96032: NullPointerException on show() when arabic
                PanelAddressBar.this.popupMethods.applyComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()));
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
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Missing custom request method, forcing GET");
            Arrays.stream(this.popupMethods.getSubElements())
            .map(JMenuItem.class::cast)
            .filter(jMenuItem -> StringUtil.GET.equals(jMenuItem.getText()))
            .findFirst()
            .ifPresent(jMenuItem -> jMenuItem.setSelected(true));
        } else if (ParameterUtil.isInvalidName(inputCustomMethod.getText())) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Illegal request method: {}", inputCustomMethod.getText());
        } else {
            this.typeRequest = inputCustomMethod.getText();
            this.atomicRadioMethod.get().setText(this.typeRequest);
        }
    }

    private JLabel initAdvancedButton() {
        var tooltip = new AtomicReference<>(new JToolTipI18n(I18nUtil.valueByKey(PanelAddressBar.KEY_BUTTON_ADVANCED)));
        var advancedButton = new JLabel(UiUtil.ARROW_DOWN.getIcon()) {
            @Override
            public JToolTip createToolTip() {
                return tooltip.get();
            }
        };
        advancedButton.setName(PanelAddressBar.NAME_ADVANCED_BUTTON);
        advancedButton.setToolTipText(I18nUtil.valueByKey(PanelAddressBar.KEY_BUTTON_ADVANCED));
        I18nViewUtil.addComponentForKey(PanelAddressBar.KEY_BUTTON_ADVANCED, tooltip.get());
        this.advancedButtonAdapter = new AdvancedButtonAdapter(advancedButton);
        advancedButton.addMouseListener(this.advancedButtonAdapter);
        return advancedButton;
    }

    private class AdvancedButtonAdapter extends MouseAdapter implements Serializable {

        private final JLabel advancedButton;

        public AdvancedButtonAdapter(JLabel advancedButton) {
            this.advancedButton = advancedButton;
        }

        public void mouseClicked(boolean isVisible) {
            PanelAddressBar.this.atomicTextFieldRequest.get().setVisible(isVisible);
            PanelAddressBar.this.atomicTextFieldHeader.get().setVisible(isVisible);
            PanelAddressBar.this.atomicRadioRequest.get().setVisible(isVisible);
            PanelAddressBar.this.atomicRadioMethod.get().setVisible(isVisible);
            PanelAddressBar.this.atomicRadioHeader.get().setVisible(isVisible);
            PanelAddressBar.this.isAdvanceActivated = isVisible;
            MediatorHelper.menubar().setVisible(isVisible);
            this.advancedButton.setIcon(isVisible ? UiUtil.ARROW_UP.getIcon() : UiUtil.ARROW_DOWN.getIcon());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            boolean isVisible = this.advancedButton.getIcon() == UiUtil.ARROW_DOWN.getIcon();
            this.mouseClicked(isVisible);
        }
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