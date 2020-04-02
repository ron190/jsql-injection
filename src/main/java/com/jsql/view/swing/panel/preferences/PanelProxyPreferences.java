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

import com.jsql.model.MediatorModel;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.listener.DocumentListenerTyping;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class PanelProxyPreferences extends JPanel {

    private final JCheckBox checkboxIsUsingProxy = new JCheckBox("", MediatorModel.model().getMediatorUtils().getProxyUtil().isUsingProxyHttp());
    private final JCheckBox checkboxIsUsingProxyHttps = new JCheckBox("", MediatorModel.model().getMediatorUtils().getProxyUtil().isUsingProxyHttps());

    private final JTextField textProxyAddress = new JPopupTextField("e.g Tor address: 127.0.0.1", MediatorModel.model().getMediatorUtils().getProxyUtil().getProxyAddressHttp()).getProxy();
    private final JTextField textProxyPort = new JPopupTextField("e.g Tor port: 8118", MediatorModel.model().getMediatorUtils().getProxyUtil().getProxyPortHttp()).getProxy();
    private final JTextField textProxyAddressHttps = new JPopupTextField("e.g Tor address: 127.0.0.1", MediatorModel.model().getMediatorUtils().getProxyUtil().getProxyAddressHttps()).getProxy();
    private final JTextField textProxyPortHttps = new JPopupTextField("e.g Tor port: 8118", MediatorModel.model().getMediatorUtils().getProxyUtil().getProxyPortHttps()).getProxy();

    public PanelProxyPreferences(PanelPreferences panelPreferences) {
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Proxy label
        JLabel labelProxyAddress = new JLabel("Proxy address  ");
        JLabel labelProxyPort = new JLabel("Proxy port  ");
        JLabel labelProxyAddressHttps = new JLabel("Proxy address  ");
        JLabel labelProxyPortHttps = new JLabel("Proxy port  ");
        JButton buttonIsUsingProxy = new JButton("Use a proxy for http:// URLs");
        JButton buttonIsUsingProxyHttps = new JButton("Use a proxy for https:// URLs");
        String tooltipIsUsingProxy = "Enable proxy communication (e.g. TOR with Privoxy or Burp) for HTTP protocol.";
        buttonIsUsingProxy.setToolTipText(tooltipIsUsingProxy);
        String tooltipIsUsingProxyHttps = "Enable proxy communication (e.g. TOR with Privoxy or Burp) for HTTPS protocol.";
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
        
        this.getTextProxyAddress().setFont(UiUtil.FONT_SEGOE_BIG);
        this.getTextProxyPort().setFont(UiUtil.FONT_SEGOE_BIG);
        this.getTextProxyAddressHttps().setFont(UiUtil.FONT_SEGOE_BIG);
        this.getTextProxyPortHttps().setFont(UiUtil.FONT_SEGOE_BIG);
        
        Stream.of(
            this.getCheckboxIsUsingProxy()
        ).forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        DocumentListener documentListenerSave = new DocumentListenerTyping() {
            
            @Override
            public void process() {
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        };

        Stream.of(
            this.getTextProxyAddress(),
            this.getTextProxyPort(),
            this.getTextProxyAddressHttps(),
            this.getTextProxyPortHttps()
        )
        .forEach(textField -> textField.getDocument().addDocumentListener(documentListenerSave));

        Stream.of(
            buttonIsUsingProxy,
            buttonIsUsingProxyHttps
        )
        .forEach(label -> {
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        GroupLayout groupLayoutProxy = new GroupLayout(this);
        this.setLayout(groupLayoutProxy);
        
        JLabel labelProxyHttpHidden = new JLabel();
        JLabel labelProxyHttp = new JLabel("<html><b>Handling proxy for HTTP protocol</b></html>");
        JLabel labelProxyHttpsHidden = new JLabel();
        JLabel labelProxyHttps = new JLabel("<html><b>Handling proxy for HTTPS protocol</b></html>");
        labelProxyHttp.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        labelProxyHttps.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        groupLayoutProxy
        .setHorizontalGroup(
            groupLayoutProxy
            .createSequentialGroup()
            .addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(labelProxyHttpHidden)
                .addComponent(this.getCheckboxIsUsingProxy())
                .addComponent(labelProxyAddress)
                .addComponent(labelProxyPort)
                .addComponent(labelProxyHttpsHidden)
                .addComponent(this.getCheckboxIsUsingProxyHttps())
                .addComponent(labelProxyAddressHttps)
                .addComponent(labelProxyPortHttps)
            ).addGroup(
                groupLayoutProxy
                .createParallelGroup()
                .addComponent(labelProxyHttp)
                .addComponent(buttonIsUsingProxy)
                .addComponent(this.getTextProxyAddress())
                .addComponent(this.getTextProxyPort())
                .addComponent(labelProxyHttps)
                .addComponent(buttonIsUsingProxyHttps)
                .addComponent(this.getTextProxyAddressHttps())
                .addComponent(this.getTextProxyPortHttps())
            )
        );
        
        groupLayoutProxy
        .setVerticalGroup(
            groupLayoutProxy
            .createSequentialGroup()
            .addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyHttpHidden)
                .addComponent(labelProxyHttp)
            )
            .addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxIsUsingProxy())
                .addComponent(buttonIsUsingProxy)
            ).addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyAddress)
                .addComponent(this.getTextProxyAddress())
            ).addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyPort)
                .addComponent(this.getTextProxyPort())
            ).addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyHttpsHidden)
                .addComponent(labelProxyHttps)
            ).addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxIsUsingProxyHttps())
                .addComponent(buttonIsUsingProxyHttps)
            ).addGroup(
                groupLayoutProxy
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelProxyAddressHttps)
                .addComponent(this.getTextProxyAddressHttps())
            ).addGroup(
                groupLayoutProxy
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
