package com.jsql.view.swing.panel.preferences;

import java.awt.Dimension;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class PanelProxy extends JPanel {

    private final JCheckBox checkboxIsUsingProxy = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getProxyUtil().isUsingProxyHttp());
    private final JCheckBox checkboxIsUsingProxyHttps = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getProxyUtil().isUsingProxyHttps());

    private final JTextField textProxyAddress = new JPopupTextField("e.g Tor address: 127.0.0.1", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyAddressHttp()).getProxy();
    private final JTextField textProxyPort = new JPopupTextField("e.g Tor port: 8118", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyPortHttp()).getProxy();
    private final JTextField textProxyAddressHttps = new JPopupTextField("e.g Tor address: 127.0.0.1", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyAddressHttps()).getProxy();
    private final JTextField textProxyPortHttps = new JPopupTextField("e.g Tor port: 8118", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyPortHttps()).getProxy();

    public PanelProxy(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Proxy label
        var labelProxyAddress = new JLabel("Address  ");
        var labelProxyPort = new JLabel("Port  ");
        var labelProxyAddressHttps = new JLabel("Address  ");
        var labelProxyPortHttps = new JLabel("Port  ");
        var buttonIsUsingProxy = new JButton("Proxy for http://");
        var buttonIsUsingProxyHttps = new JButton("Proxy for https://");
        var tooltipIsUsingProxy = "Enable proxy communication (e.g. TOR with Privoxy or Burp) for HTTP protocol.";
        buttonIsUsingProxy.setToolTipText(tooltipIsUsingProxy);
        var tooltipIsUsingProxyHttps = "Enable proxy communication (e.g. TOR with Privoxy or Burp) for HTTPS protocol.";
        buttonIsUsingProxyHttps.setToolTipText(tooltipIsUsingProxyHttps);

        // Proxy setting: IP, port, checkbox to activate proxy
        this.getCheckboxIsUsingProxy().setToolTipText(tooltipIsUsingProxy);
        this.getCheckboxIsUsingProxy().setFocusable(false);

        buttonIsUsingProxy.addActionListener(actionEvent -> {
            
            this.getCheckboxIsUsingProxy().setSelected(!this.getCheckboxIsUsingProxy().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        this.getCheckboxIsUsingProxyHttps().setToolTipText(tooltipIsUsingProxyHttps);
        this.getCheckboxIsUsingProxyHttps().setFocusable(false);
        
        buttonIsUsingProxyHttps.addActionListener(actionEvent -> {
            
            this.getCheckboxIsUsingProxyHttps().setSelected(!this.getCheckboxIsUsingProxyHttps().isSelected());
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });

        this.getTextProxyAddress().setMaximumSize(new Dimension(200, 0));
        this.getTextProxyPort().setMaximumSize(new Dimension(200, 0));
        this.getTextProxyAddressHttps().setMaximumSize(new Dimension(200, 0));
        this.getTextProxyPortHttps().setMaximumSize(new Dimension(200, 0));
        
        this.getTextProxyAddress().setFont(UiUtil.FONT_NON_MONO_BIG);
        this.getTextProxyPort().setFont(UiUtil.FONT_NON_MONO_BIG);
        this.getTextProxyAddressHttps().setFont(UiUtil.FONT_NON_MONO_BIG);
        this.getTextProxyPortHttps().setFont(UiUtil.FONT_NON_MONO_BIG);
        
        Stream
        .of(
            this.getCheckboxIsUsingProxy()
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        DocumentListener documentListenerSave = new DocumentListenerEditing() {
            
            @Override
            public void process() {
                
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        };

        Stream
        .of(
            this.getTextProxyAddress(),
            this.getTextProxyPort(),
            this.getTextProxyAddressHttps(),
            this.getTextProxyPortHttps()
        )
        .forEach(textField -> textField.getDocument().addDocumentListener(documentListenerSave));

        Stream
        .of(
            buttonIsUsingProxy,
            buttonIsUsingProxyHttps
        )
        .forEach(label -> {
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        var labelProxyHttpsHidden = new JLabel();
        var labelProxyHttps = new JLabel();
        labelProxyHttps.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        groupLayout
        .setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.getCheckboxIsUsingProxy())
                .addComponent(labelProxyAddress)
                .addComponent(labelProxyPort)
                .addComponent(labelProxyHttpsHidden)
                .addComponent(this.getCheckboxIsUsingProxyHttps())
                .addComponent(labelProxyAddressHttps)
                .addComponent(labelProxyPortHttps)
            ).addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(buttonIsUsingProxy)
                .addComponent(this.getTextProxyAddress())
                .addComponent(this.getTextProxyPort())
                .addComponent(labelProxyHttps)
                .addComponent(buttonIsUsingProxyHttps)
                .addComponent(this.getTextProxyAddressHttps())
                .addComponent(this.getTextProxyPortHttps())
            )
        );
        
        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxIsUsingProxy())
                .addComponent(buttonIsUsingProxy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyAddress)
                .addComponent(this.getTextProxyAddress())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyPort)
                .addComponent(this.getTextProxyPort())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyHttpsHidden)
                .addComponent(labelProxyHttps)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxIsUsingProxyHttps())
                .addComponent(buttonIsUsingProxyHttps)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyAddressHttps)
                .addComponent(this.getTextProxyAddressHttps())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyPortHttps)
                .addComponent(this.getTextProxyPortHttps())
            )
        );
    }
    
    
    // Getter and setter
    
    public JCheckBox getCheckboxIsUsingProxy() {
        return this.checkboxIsUsingProxy;
    }

    public JTextField getTextProxyAddress() {
        return this.textProxyAddress;
    }

    public JTextField getTextProxyPort() {
        return this.textProxyPort;
    }

    public JCheckBox getCheckboxIsUsingProxyHttps() {
        return this.checkboxIsUsingProxyHttps;
    }

    public JTextField getTextProxyAddressHttps() {
        return this.textProxyAddressHttps;
    }

    public JTextField getTextProxyPortHttps() {
        return this.textProxyPortHttps;
    }
}
