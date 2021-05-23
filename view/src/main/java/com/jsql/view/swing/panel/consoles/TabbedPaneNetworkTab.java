package com.jsql.view.swing.panel.consoles;

import java.awt.FontMetrics;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jsql.model.bean.util.HttpHeader;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.util.StringUtil;
import com.jsql.view.swing.panel.util.HTMLEditorKitTextPaneWrap;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextPanePlaceholder;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;
import com.jsql.view.swing.util.I18nViewUtil;

@SuppressWarnings("serial")
public class TabbedPaneNetworkTab extends TabbedPaneWheeled {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private JTextArea textAreaNetworkTabUrl = new JPopupTextArea("Request URL").getProxy();
    private JTextArea textAreaNetworkTabResponse = new JPopupTextArea("Header server response").getProxy();
    private JTextArea textAreaNetworkTabSource = new JPopupTextArea("Raw page source").getProxy();
    private JTextPane textAreaNetworkTabPreview = new JTextPanePlaceholder("Web browser rendering");
    private JTextArea textAreaNetworkTabHeader = new JPopupTextArea("Header client request").getProxy();
    private JTextArea textAreaNetworkTabParams = new JPopupTextArea("HTTP POST parameters").getProxy();
    
    public TabbedPaneNetworkTab() {

        this.setUI(new CustomMetalTabbedPaneUI() {
            
            @Override protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                
                return Math.max(65, super.calculateTabWidth(tabPlacement, tabIndex, metrics));
            }
        });
        
        Stream
        .of(
            new SimpleEntry<>("NETWORK_TAB_URL_LABEL", this.textAreaNetworkTabUrl),
            new SimpleEntry<>("NETWORK_TAB_RESPONSE_LABEL", this.textAreaNetworkTabResponse),
            new SimpleEntry<>("NETWORK_TAB_SOURCE_LABEL", this.textAreaNetworkTabSource),
            new SimpleEntry<>("NETWORK_TAB_PREVIEW_LABEL", this.textAreaNetworkTabPreview),
            new SimpleEntry<>("NETWORK_TAB_HEADERS_LABEL", this.textAreaNetworkTabHeader),
            new SimpleEntry<>("NETWORK_TAB_PARAMS_LABEL", this.textAreaNetworkTabParams)
        )
        .forEach(entry -> {
            
            this.addTab(I18nUtil.valueByKey(entry.getKey()), new LightScrollPane(1, 1, 0, 0, entry.getValue()));
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            this.setTabComponentAt(
                this.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
            
            label.setName("label"+ entry.getKey());
            entry.getValue().setName("text"+ entry.getKey());
            
            DefaultCaret caret = (DefaultCaret) entry.getValue().getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        });
        
        this.textAreaNetworkTabHeader.setLineWrap(true);
        this.textAreaNetworkTabParams.setLineWrap(true);
        this.textAreaNetworkTabResponse.setLineWrap(true);
        this.textAreaNetworkTabUrl.setLineWrap(true);
        this.textAreaNetworkTabSource.setLineWrap(true);
        
        this.textAreaNetworkTabPreview.setEditorKit(new HTMLEditorKitTextPaneWrap());
        this.textAreaNetworkTabPreview.setContentType("text/html");
        this.textAreaNetworkTabPreview.setEditable(false);
    }
    
    public void changeTextNetwork(HttpHeader networkData) {
        
        this.textAreaNetworkTabParams.setText(networkData.getPost());
        this.textAreaNetworkTabUrl.setText(networkData.getUrl());
        
        this.textAreaNetworkTabHeader.setText(StringUtils.EMPTY);
        this.textAreaNetworkTabResponse.setText(StringUtils.EMPTY);
        
        for (String key: networkData.getHeader().keySet()) {
            
            this.textAreaNetworkTabHeader.append(key + ": " + networkData.getHeader().get(key));
            this.textAreaNetworkTabHeader.append("\n");
        }
        
        for (String key: networkData.getResponse().keySet()) {
            
            this.textAreaNetworkTabResponse.append(key + ": " + networkData.getResponse().get(key));
            this.textAreaNetworkTabResponse.append("\n");
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
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        // Reset EditorKit to disable previous document effect
        this.textAreaNetworkTabPreview.getEditorKit().createDefaultDocument();
        
        // Proxy is used by jsoup to display <img> tags
        // Previous test for 2xx Success and 3xx Redirection was Header only,
        // now get the HTML content
        // Fix #35352: EmptyStackException on setText()
        // Fix #39841: RuntimeException on setText()
        // Fix #42523: ExceptionInInitializerError on clean()
        try {
            this.textAreaNetworkTabPreview.setText(
                Jsoup
                .clean(
                    String
                    .format(
                        "<html>%s</html>",
                        StringUtil.detectUtf8(networkData.getSource())
                    )
                    .replaceAll(
                        "<img[^>]*>",
                        StringUtils.EMPTY
                    )
                    .replaceAll(
                        "<input[^>]*type=\"?hidden\"?.*>",
                        StringUtils.EMPTY
                    )
                    .replaceAll(
                        "<input[^>]*type=\"?(submit|button)\"?.*>",
                        "<div style=\"background-color:#eeeeee;text-align:center;border:1px solid black;width:100px;\">button</div>"
                    )
                    .replaceAll(
                        "<input[^>]*>",
                        "<div style=\"text-align:center;border:1px solid black;width:100px;\">input</div>"
                    ),
                    Whitelist
                    .relaxed()
                    .addTags("center", "div", "span")
                    .addAttributes(":all", "style")
                )
            );
            
        } catch (Exception | ExceptionInInitializerError e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
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
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        // Fix #41879: ArrayIndexOutOfBoundsException on setText()
        try {
            this.textAreaNetworkTabPreview.setText(StringUtils.EMPTY);
            
        } catch (ArrayIndexOutOfBoundsException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
}
