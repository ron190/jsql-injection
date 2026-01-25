package com.jsql.view.swing.panel.consoles;

import com.jsql.view.subscriber.Seal;
import com.jsql.model.injection.engine.model.EngineYaml;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.panel.util.HTMLEditorKitTextPaneWrap;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.text.JPopupTextComponent;
import com.jsql.view.swing.text.JTextPanePlaceholder;
import com.jsql.view.swing.text.SyntaxTextArea;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class TabbedPaneNetworkTab extends TabbedPaneWheeled {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final RSyntaxTextArea textAreaUrl = new SyntaxTextArea(I18nUtil.valueByKey("NETWORK_LINE_PLACEHOLDER_URL"));
    private final RSyntaxTextArea textAreaResponse = new SyntaxTextArea(I18nUtil.valueByKey("NETWORK_LINE_PLACEHOLDER_RESPONSE"));
    private final RSyntaxTextArea textAreaSource = new SyntaxTextArea(I18nUtil.valueByKey("NETWORK_LINE_PLACEHOLDER_SOURCE"));
    private final JTextPane textPanePreview = new JPopupTextComponent<>(new JTextPanePlaceholder(I18nUtil.valueByKey("NETWORK_LINE_PLACEHOLDER_PREVIEW")){
        @Override
        public boolean isEditable() {
            return false;
        }
    }).getProxy();
    private final RSyntaxTextArea textAreaHeader = new SyntaxTextArea(I18nUtil.valueByKey("NETWORK_LINE_PLACEHOLDER_HEADERS"));
    private final RSyntaxTextArea textAreaRequest = new SyntaxTextArea(I18nUtil.valueByKey("NETWORK_LINE_PLACEHOLDER_REQUEST"));
    private final JCheckBox checkBoxDecode = new JCheckBox("Decode", MediatorHelper.model().getMediatorUtils().preferencesUtil().isUrlDecodeNetworkTab());

    public TabbedPaneNetworkTab() {
        this.setName("tabNetwork");
        var panelDecode = new JPanel(new BorderLayout());
        panelDecode.add(this.checkBoxDecode, BorderLayout.LINE_END);  // reduce to minimum size as checkbox expands by the label
        this.putClientProperty("JTabbedPane.trailingComponent", panelDecode);

        I18nViewUtil.addComponentForKey("NETWORK_LINE_PLACEHOLDER_URL", this.textAreaUrl);
        I18nViewUtil.addComponentForKey("NETWORK_LINE_PLACEHOLDER_RESPONSE", this.textAreaResponse);
        I18nViewUtil.addComponentForKey("NETWORK_LINE_PLACEHOLDER_SOURCE", this.textAreaSource);
        I18nViewUtil.addComponentForKey("NETWORK_LINE_PLACEHOLDER_PREVIEW", this.textPanePreview);
        I18nViewUtil.addComponentForKey("NETWORK_LINE_PLACEHOLDER_HEADERS", this.textAreaHeader);
        I18nViewUtil.addComponentForKey("NETWORK_LINE_PLACEHOLDER_REQUEST", this.textAreaRequest);
        Stream.of(
            new SimpleEntry<>("NETWORK_TAB_URL_LABEL", this.textAreaUrl),
            new SimpleEntry<>("NETWORK_TAB_HEADERS_LABEL", this.textAreaHeader),
            new SimpleEntry<>("NETWORK_TAB_PARAMS_LABEL", this.textAreaRequest),
            new SimpleEntry<>("NETWORK_TAB_RESPONSE_LABEL", this.textAreaResponse),
            new SimpleEntry<>("NETWORK_TAB_SOURCE_LABEL", this.textAreaSource),
            new SimpleEntry<>("NETWORK_TAB_PREVIEW_LABEL", this.textPanePreview)
        )
        .forEach(entry -> {
            this.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                entry.getValue() == this.textAreaSource
                ? new RTextScrollPane(entry.getValue(), false)
                : new JScrollPane(entry.getValue())
            );
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            label.setName("label"+ entry.getKey());
            this.setTabComponentAt(
                this.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);

            entry.getValue().setName("text"+ entry.getKey());

            DefaultCaret caret = (DefaultCaret) entry.getValue().getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        });

        Arrays.asList(this.textAreaUrl, this.textAreaHeader, this.textAreaRequest, this.textAreaResponse, this.textAreaSource).forEach(entry -> {
            entry.setEditable(false);
            entry.setLineWrap(true);
        });
        this.textAreaResponse.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        this.textAreaSource.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        this.textAreaSource.setHighlightSecondaryLanguages(true);
        this.applyTheme();
        
        this.textPanePreview.setEditorKit(new HTMLEditorKitTextPaneWrap());
        this.textPanePreview.setContentType("text/html");
        this.textPanePreview.setEditable(false);
        this.textPanePreview.getCaret().setBlinkRate(0);
        this.textPanePreview.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                TabbedPaneNetworkTab.this.textPanePreview.getCaret().setVisible(true);
                TabbedPaneNetworkTab.this.textPanePreview.getCaret().setSelectionVisible(true);
            }
        });
    }
    
    public void changeTextNetwork(Seal.MessageHeader networkData) {
        this.textAreaRequest.setText(this.getDecodedValue(this.checkBoxDecode.isSelected(), networkData.post()));
        this.textAreaUrl.setText(this.getDecodedValue(this.checkBoxDecode.isSelected(), networkData.url()));
        this.updateTextArea(this.textAreaHeader, networkData.header());
        this.updateTextArea(this.textAreaResponse, networkData.response());

        // Fix #53736: ArrayIndexOutOfBoundsException on setText()
        // Fix #54573: NullPointerException on setText()
        try {
            this.textAreaSource.setText(
                StringUtil.detectUtf8(networkData.source())
                .replaceAll(EngineYaml.CALIBRATOR_SQL +"{5,}", EngineYaml.CALIBRATOR_SQL +"*")
                .trim()
            );
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        
        this.textPanePreview.getEditorKit().createDefaultDocument();  // Reset EditorKit to disable previous document effect
        // Proxy is used by jsoup to display <img> tags
        // Previous test for 2xx Success and 3xx Redirection was Header only, now get the HTML content
        // Fix #35352: EmptyStackException on setText()
        // Fix #39841: RuntimeException on setText()
        // Fix #42523: ExceptionInInitializerError on clean()
        try {
            this.textPanePreview.setText(
                Jsoup.clean(
                    String.format(
                        "<html>%s</html>",
                        StringUtil.detectUtf8(networkData.source())
                    )
                    .replaceAll("<img[^>]*>",StringUtils.EMPTY)  // avoid loading external resources
                    .replaceAll("<input[^>]*type=\"?hidden\"?.*>", StringUtils.EMPTY)
                    .replaceAll(
                        "<input[^>]*type=\"?(submit|button)\"?.*>",
                        "<div style=\"background-color:#eeeeee;text-align:center;border:1px solid black;width:100px;\">button</div>"
                    )
                    .replaceAll(
                        "<input[^>]*>",
                        "<div style=\"text-align:center;border:1px solid black;width:100px;\">input</div>"
                    ),
                    Safelist.relaxed().addTags("center", "div", "span").addAttributes(":all", "style")
                )
            );
        } catch (Exception | ExceptionInInitializerError e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    private void updateTextArea(JTextArea textArea, Map<String, String> httpData) {
        textArea.setText(StringUtils.EMPTY);
        if (httpData != null) {
            httpData.forEach((key, value) -> {
                String decodedValue = this.getDecodedValue(this.checkBoxDecode.isSelected(), value);
                textArea.append(key + ": " + decodedValue + "\n");
            });
        }
    }

    private String getDecodedValue(boolean isSelected, String value) {
        // Fix #96095: IllegalArgumentException on URLDecoder.decode()
        try {
            return isSelected ? StringUtil.fromUrl(value) : value;
        } catch (IllegalArgumentException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, "Decoding failure: {}", e.getMessage());
            return value;
        }
    }

    public void reset() {
        this.textAreaUrl.setText(StringUtils.EMPTY);
        this.textAreaHeader.setText(StringUtils.EMPTY);
        this.textAreaRequest.setText(StringUtils.EMPTY);
        this.textAreaResponse.setText(StringUtils.EMPTY);
        
        // Fix #54572: NullPointerException on setText()
        try {
            this.textAreaSource.setText(StringUtils.EMPTY);
        } catch (NullPointerException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        
        // Fix #41879: ArrayIndexOutOfBoundsException on setText()
        try {
            this.textPanePreview.setText(StringUtils.EMPTY);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    public void applyTheme() {
        Arrays.asList(
            this.textAreaUrl, this.textAreaHeader, this.textAreaRequest, this.textAreaResponse, this.textAreaSource
        ).forEach(UiUtil::applySyntaxTheme);
    }

    public JCheckBox getCheckBoxDecode() {
        return this.checkBoxDecode;
    }
}
