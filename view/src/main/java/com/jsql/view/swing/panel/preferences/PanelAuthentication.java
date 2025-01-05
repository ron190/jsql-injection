package com.jsql.view.swing.panel.preferences;

import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.stream.Stream;

public class PanelAuthentication extends JPanel {

    private final JCheckBox checkboxUseDigestAuthentication = new JCheckBox("<html>Enable <b>Basic</b> and <b>NTLM</b> (for <b>Digest</b>: do not enable but just set the user and password) :</html>", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().isAuthentEnabled());
    private final JCheckBox checkboxUseKerberos = new JCheckBox("Enable Kerberos :", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().isKerberos());

    private final JTextField textDigestAuthenticationUsername = new JPopupTextField("Host system user", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getUsernameAuthentication()).getProxy();
    private final JTextField textDigestAuthenticationPassword = new JPopupTextField("Host system password", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getPasswordAuthentication()).getProxy();
    private final JTextField textKerberosLoginConf = new JPopupTextField("Path to login.conf", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getPathKerberosLogin()).getProxy();
    private final JTextField textKerberosKrb5Conf = new JPopupTextField("Path to krb5.conf", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getPathKerberosKrb5()).getProxy();

    private static final String TAG_HTML_ON = "<html>";
    private static final String TAG_HTML_OFF = "</html>";
    
    public PanelAuthentication(PanelPreferences panelPreferences) {
        this.checkboxUseDigestAuthentication.setToolTipText(
            TAG_HTML_ON
            + "Enable <b>Basic</b>, <b>Digest</b>, <b>NTLM</b> authentication (e.g. WWW-Authenticate).<br>"
            + "Then define username and password for the host.<br>"
            + "<i><b>Negotiate</b> authentication is defined in URL.</i>"
            + TAG_HTML_OFF
        );

        var panelUsername = new JPanel();
        panelUsername.setLayout(new BoxLayout(panelUsername, BoxLayout.X_AXIS));
        panelUsername.add(new JLabel("Username "));
        panelUsername.add(this.textDigestAuthenticationUsername);
        panelUsername.setMaximumSize(new Dimension(325, this.textDigestAuthenticationUsername.getPreferredSize().height));

        var panelPassword = new JPanel();
        panelPassword.setLayout(new BoxLayout(panelPassword, BoxLayout.X_AXIS));
        panelPassword.add(new JLabel("Password "));
        panelPassword.add(this.textDigestAuthenticationPassword);
        panelPassword.setMaximumSize(new Dimension(325, this.textDigestAuthenticationPassword.getPreferredSize().height));
        panelPassword.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        String tooltipUseKerberos = TAG_HTML_ON
            + "Enable Kerberos authentication, then define path to <b>login.conf</b> and <b>krb5.conf</b>.<br>"
            + "Path to <b>.keytab</b> file is defined in login.conf ; name of <b>principal</b> must be correct.<br>"
            + "<b>Realm</b> and <b>kdc</b> are defined in krb5.conf.<br>"
            + "Finally use the <b>correct hostname</b> in URL, e.g. http://servicename.corp.test/[..]"
            + TAG_HTML_OFF;

        this.textKerberosLoginConf.setToolTipText(
            TAG_HTML_ON
            + "Define the path to <b>login.conf</b>. Sample :<br>"
            + "&emsp;<b>entry-name</b> {<br>"
            + "&emsp;&emsp;com.sun.security.auth.module.Krb5LoginModule<br>"
            + "&emsp;&emsp;required<br>"
            + "&emsp;&emsp;useKeyTab=true<br>"
            + "&emsp;&emsp;keyTab=\"<b>/path/to/my.keytab</b>\"<br>"
            + "&emsp;&emsp;principal=\"<b>HTTP/SERVICENAME.CORP.TEST@CORP.TEST</b>\"<br>"
            + "&emsp;&emsp;debug=false;<br>"
            + "&emsp;}<br>"
            + "<i>Principal name is case sensitive ; entry-name is read automatically.</i>"
            + TAG_HTML_OFF);
        this.textKerberosKrb5Conf.setToolTipText(
            TAG_HTML_ON
            + "Define the path to <b>krb5.conf</b>. Sample :<br>"
            + "&emsp;[libdefaults]<br>"
            + "&emsp;&emsp;default_realm = <b>CORP.TEST</b><br>"
            + "&emsp;&emsp;udp_preference_limit = 1<br>"
            + "&emsp;[realms]<br>"
            + "&emsp;&emsp;<b>CORP.TEST</b> = {<br>"
            + "&emsp;&emsp;&emsp;kdc = <b>127.0.0.1:88</b><br>"
            + "&emsp;&emsp;}<br>"
            + "<i>Realm and kdc are case sensitives.</i>"
            + TAG_HTML_OFF);
        this.checkboxUseKerberos.setToolTipText(tooltipUseKerberos);

        var panelLoginConf = new JPanel();
        panelLoginConf.setLayout(new BoxLayout(panelLoginConf, BoxLayout.X_AXIS));
        panelLoginConf.add(new JLabel("login.conf "));
        panelLoginConf.add(this.textKerberosLoginConf);
        panelLoginConf.setMaximumSize(new Dimension(325, this.textKerberosLoginConf.getPreferredSize().height));

        var panelKrb5Conf = new JPanel();
        panelKrb5Conf.setLayout(new BoxLayout(panelKrb5Conf, BoxLayout.X_AXIS));
        panelKrb5Conf.add(new JLabel("krb5.conf "));
        panelKrb5Conf.add(this.textKerberosKrb5Conf);
        panelKrb5Conf.setMaximumSize(new Dimension(325, this.textKerberosKrb5Conf.getPreferredSize().height));
        
        Stream.of(
            this.checkboxUseDigestAuthentication,
            this.checkboxUseKerberos
        )
        .forEach(button -> button.addActionListener(panelPreferences.getActionListenerSave()));
        
        DocumentListener documentListenerSave = new DocumentListenerEditing() {
            @Override
            public void process() {
                panelPreferences.getActionListenerSave().actionPerformed(null);
            }
        };

        Stream.of(
            this.textDigestAuthenticationPassword,
            this.textDigestAuthenticationUsername,
            this.textKerberosKrb5Conf,
            this.textKerberosLoginConf
        )
        .forEach(textField -> textField.getDocument().addDocumentListener(documentListenerSave));

        var labelOrigin = new JLabel("<html><b>Network secured connection</b></html>");
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
                .addComponent(this.checkboxUseDigestAuthentication)
                .addComponent(panelUsername)
                .addComponent(panelPassword)
                .addComponent(this.checkboxUseKerberos)
                .addComponent(panelLoginConf)
                .addComponent(panelKrb5Conf)
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
                .addComponent(this.checkboxUseDigestAuthentication)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelUsername)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelPassword)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.checkboxUseKerberos)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelLoginConf)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(panelKrb5Conf)
            )
        );
    }
    
    
    // Getter and setter
    
    public JCheckBox getCheckboxUseDigestAuthentication() {
        return this.checkboxUseDigestAuthentication;
    }

    public JTextField getTextDigestAuthenticationUsername() {
        return this.textDigestAuthenticationUsername;
    }

    public JTextField getTextDigestAuthenticationPassword() {
        return this.textDigestAuthenticationPassword;
    }

    public JCheckBox getCheckboxUseKerberos() {
        return this.checkboxUseKerberos;
    }

    public JTextField getTextKerberosKrb5Conf() {
        return this.textKerberosKrb5Conf;
    }

    public JTextField getTextKerberosLoginConf() {
        return this.textKerberosLoginConf;
    }
}
