package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.action.ActionCheckIp;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.stream.Stream;

public class PanelProxy extends JPanel {

    private final JCheckBox checkboxIsUsingProxy = new JCheckBox("<html>Enable proxy for <b>HTTP</b>:</html>", MediatorHelper.model().getMediatorUtils().getProxyUtil().isUsingProxyHttp());
    private final JCheckBox checkboxIsUsingProxyHttps = new JCheckBox("<html>Enable proxy for <b>HTTPS</b>:</html>", MediatorHelper.model().getMediatorUtils().getProxyUtil().isUsingProxyHttps());

    private final JTextField textProxyAddress = new JPopupTextField("e.g. Tor address: 127.0.0.1", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyAddressHttp()).getProxy();
    private final JTextField textProxyPort = new JPopupTextField("e.g. Tor port: 8118", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyPortHttp()).getProxy();
    private final JTextField textProxyAddressHttps = new JPopupTextField("e.g. Tor address: 127.0.0.1", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyAddressHttps()).getProxy();
    private final JTextField textProxyPortHttps = new JPopupTextField("e.g. Tor port: 8118", MediatorHelper.model().getMediatorUtils().getProxyUtil().getProxyPortHttps()).getProxy();

    public PanelProxy(PanelPreferences panelPreferences) {
        var panelHttpIpAddress = new JPanel();
        panelHttpIpAddress.setLayout(new BoxLayout(panelHttpIpAddress, BoxLayout.X_AXIS));
        panelHttpIpAddress.add(new JLabel("IP "));
        panelHttpIpAddress.add(this.textProxyAddress);
        panelHttpIpAddress.setMaximumSize(new Dimension(325, this.textProxyAddress.getPreferredSize().height));

        var panelHttpPort = new JPanel();
        panelHttpPort.setLayout(new BoxLayout(panelHttpPort, BoxLayout.X_AXIS));
        panelHttpPort.add(new JLabel("Port "));
        panelHttpPort.add(this.textProxyPort);
        panelHttpPort.setMaximumSize(new Dimension(325, this.textProxyPort.getPreferredSize().height));
        panelHttpPort.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        var panelHttpsIpAddress = new JPanel();
        panelHttpsIpAddress.setLayout(new BoxLayout(panelHttpsIpAddress, BoxLayout.X_AXIS));
        panelHttpsIpAddress.add(new JLabel("IP "));
        panelHttpsIpAddress.add(this.textProxyAddressHttps);
        panelHttpsIpAddress.setMaximumSize(new Dimension(325, this.textProxyAddressHttps.getPreferredSize().height));

        var panelHttpsPort = new JPanel();
        panelHttpsPort.setLayout(new BoxLayout(panelHttpsPort, BoxLayout.X_AXIS));
        panelHttpsPort.add(new JLabel("Port "));
        panelHttpsPort.add(this.textProxyPortHttps);
        panelHttpsPort.setMaximumSize(new Dimension(325, this.textProxyPortHttps.getPreferredSize().height));
        panelHttpsPort.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        this.checkboxIsUsingProxy.setToolTipText("Enable proxy for HTTP protocol");
        this.checkboxIsUsingProxyHttps.setToolTipText("Enable proxy for HTTPS protocol");

        Stream.of(
            this.checkboxIsUsingProxy,
            this.checkboxIsUsingProxyHttps
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        DocumentListener documentListenerSave = new DocumentListenerEditing() {
            @Override
            public void process() {
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        };

        Stream.of(
            this.textProxyAddress,
            this.textProxyPort,
            this.textProxyAddressHttps,
            this.textProxyPortHttps
        )
        .forEach(textField -> textField.getDocument().addDocumentListener(documentListenerSave));

        final var buttonCheckIp = new JButton("Check your IP address");
        buttonCheckIp.addActionListener(new ActionCheckIp());
        buttonCheckIp.setToolTipText(
            "<html><b>Show your public IP address</b><br>"
            + "Your internal IP is displayed if you don't set a proxy. If you set a proxy<br>"
            + "like TOR then another IP is used instead of your internal IP.</html>"
        );

        var labelOrigin = new JLabel("<html><b>Proxy settings (e.g Burp, Tor and Privoxy)</b></html>");
        labelOrigin.setBorder(PanelGeneral.MARGIN);

        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        
        groupLayout.setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(labelOrigin)
                .addComponent(this.checkboxIsUsingProxy)
                .addComponent(panelHttpIpAddress)
                .addComponent(panelHttpPort)
                .addComponent(this.checkboxIsUsingProxyHttps)
                .addComponent(panelHttpsIpAddress)
                .addComponent(panelHttpsPort)
                .addComponent(buttonCheckIp)
            )
        );
        
        groupLayout.setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelOrigin)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUsingProxy)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelHttpIpAddress)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelHttpPort)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxIsUsingProxyHttps)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelHttpsIpAddress)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelHttpsPort)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(buttonCheckIp)
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
