package com.jsql.view.swing.panel.consoles;

import com.jsql.model.bean.util.HttpHeader;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.panel.util.HTMLEditorKitTextPaneWrap;
import com.jsql.view.swing.popupmenu.JPopupMenuComponent;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JPopupTextComponent;
import com.jsql.view.swing.text.JTextPanePlaceholder;
import com.jsql.view.swing.util.I18nViewUtil;
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
import java.util.stream.Stream;

public class TabbedPaneNetworkTab extends TabbedPaneWheeled {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final JTextArea textAreaNetworkTabUrl = new JPopupTextArea("Request URL").getProxy();
    private final JTextArea textAreaNetworkTabResponse = new JPopupTextArea("Response headers").getProxy();
    private final RSyntaxTextArea textAreaNetworkTabSource = new RSyntaxTextArea("Page source");
    private final JTextPane textAreaNetworkTabPreview = new JPopupTextComponent<>(new JTextPanePlaceholder("Page rendering"){
        @Override
        public boolean isEditable() {
            return false;
        }
    }).getProxy();
    private final JTextArea textAreaNetworkTabHeader = new JPopupTextArea("Request headers").getProxy();
    private final JTextArea textAreaNetworkTabParams = new JPopupTextArea("Request body").getProxy();
    
    public TabbedPaneNetworkTab() {
        this.setName("tabNetwork");
        
        Stream.of(
            new SimpleEntry<>("NETWORK_TAB_URL_LABEL", this.textAreaNetworkTabUrl),
            new SimpleEntry<>("NETWORK_TAB_HEADERS_LABEL", this.textAreaNetworkTabHeader),
            new SimpleEntry<>("NETWORK_TAB_PARAMS_LABEL", this.textAreaNetworkTabParams),
            new SimpleEntry<>("NETWORK_TAB_RESPONSE_LABEL", this.textAreaNetworkTabResponse),
            new SimpleEntry<>("NETWORK_TAB_SOURCE_LABEL", this.textAreaNetworkTabSource),
            new SimpleEntry<>("NETWORK_TAB_PREVIEW_LABEL", this.textAreaNetworkTabPreview)
        )
        .forEach(entry -> {
            this.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                entry.getValue() == this.textAreaNetworkTabSource
                ? new RTextScrollPane(entry.getValue())
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

        this.textAreaNetworkTabSource.setPopupMenu(new JPopupMenuComponent(this.textAreaNetworkTabSource));
        this.textAreaNetworkTabSource.setEditable(false);
        this.textAreaNetworkTabSource.getCaret().setBlinkRate(0);
        this.textAreaNetworkTabSource.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                TabbedPaneNetworkTab.this.textAreaNetworkTabSource.getCaret().setVisible(true);
                TabbedPaneNetworkTab.this.textAreaNetworkTabSource.getCaret().setSelectionVisible(true);
            }
        });
        this.textAreaNetworkTabSource.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        this.textAreaNetworkTabSource.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        this.textAreaNetworkTabSource.setHighlightSecondaryLanguages(true);
        this.applyTheme();

        this.textAreaNetworkTabHeader.setLineWrap(true);
        this.textAreaNetworkTabParams.setLineWrap(true);
        this.textAreaNetworkTabResponse.setLineWrap(true);
        this.textAreaNetworkTabUrl.setLineWrap(true);
        this.textAreaNetworkTabSource.setLineWrap(true);
        
        this.textAreaNetworkTabPreview.setEditorKit(new HTMLEditorKitTextPaneWrap());
        this.textAreaNetworkTabPreview.setContentType("text/html");
        this.textAreaNetworkTabPreview.setEditable(false);
        this.textAreaNetworkTabPreview.getCaret().setBlinkRate(0);
        this.textAreaNetworkTabPreview.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                TabbedPaneNetworkTab.this.textAreaNetworkTabPreview.getCaret().setVisible(true);
                TabbedPaneNetworkTab.this.textAreaNetworkTabPreview.getCaret().setSelectionVisible(true);
            }
        });
    }
    
    public void changeTextNetwork(HttpHeader networkData) {
        this.textAreaNetworkTabParams.setText(networkData.getPost());
        this.textAreaNetworkTabUrl.setText(networkData.getUrl());

        this.textAreaNetworkTabHeader.setText(StringUtils.EMPTY);
        if (networkData.getHeader() != null) {
            for (String key: networkData.getHeader().keySet()) {
                this.textAreaNetworkTabHeader.append(key + ": " + networkData.getHeader().get(key));
                this.textAreaNetworkTabHeader.append("\n");
            }
        }

        this.textAreaNetworkTabResponse.setText(StringUtils.EMPTY);
        if (networkData.getResponse() != null) {
            for (String key: networkData.getResponse().keySet()) {
                this.textAreaNetworkTabResponse.append(key + ": " + networkData.getResponse().get(key));
                this.textAreaNetworkTabResponse.append("\n");
            }
        }
        
        // Fix #53736: ArrayIndexOutOfBoundsException on setText()
        // Fix #54573: NullPointerException on setText()
        try {
            this.textAreaNetworkTabSource.setText(
                StringUtil
                .detectUtf8(networkData.getSource())
                .replaceAll("#{5,}", "#*")
                .trim()
            );
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        
        this.textAreaNetworkTabPreview.getEditorKit().createDefaultDocument();  // Reset EditorKit to disable previous document effect
        // Proxy is used by jsoup to display <img> tags
        // Previous test for 2xx Success and 3xx Redirection was Header only, now get the HTML content
        // Fix #35352: EmptyStackException on setText()
        // Fix #39841: RuntimeException on setText()
        // Fix #42523: ExceptionInInitializerError on clean()
        try {
            this.textAreaNetworkTabPreview.setText(
                Jsoup.clean(
                    String.format(
                        "<html>%s</html>",
                        StringUtil.detectUtf8(networkData.getSource())
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
                    Safelist.relaxed()
                        .addTags("center", "div", "span")
                        .addAttributes(":all", "style")
                )
            );
        } catch (Exception | ExceptionInInitializerError e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
    
    public void reset() {
        this.textAreaNetworkTabUrl.setText(StringUtils.EMPTY);
        this.textAreaNetworkTabHeader.setText(StringUtils.EMPTY);
        this.textAreaNetworkTabParams.setText(StringUtils.EMPTY);
        this.textAreaNetworkTabResponse.setText(StringUtils.EMPTY);
        
        // Fix #54572: NullPointerException on setText()
        try {
            this.textAreaNetworkTabSource.setText(StringUtils.EMPTY);
        } catch (NullPointerException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
        
        // Fix #41879: ArrayIndexOutOfBoundsException on setText()
        try {
            this.textAreaNetworkTabPreview.setText(StringUtils.EMPTY);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    public void applyTheme() {
        UiUtil.applyTheme(textAreaNetworkTabSource);
    }
}
