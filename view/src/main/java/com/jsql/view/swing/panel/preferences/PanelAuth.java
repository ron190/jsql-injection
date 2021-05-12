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
public class PanelAuth extends JPanel {

    private final JCheckBox checkboxUseDigestAuthentication = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().isAuthentEnabled());
    private final JCheckBox checkboxUseKerberos = new JCheckBox(StringUtils.EMPTY, MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().isKerberos());

    private final JTextField textDigestAuthenticationUsername = new JPopupTextField("Host system user", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getUsernameAuthentication()).getProxy();
    private final JTextField textDigestAuthenticationPassword = new JPopupTextField("Host system password", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getPasswordAuthentication()).getProxy();
    private final JTextField textKerberosLoginConf = new JPopupTextField("Path to login.conf", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getPathKerberosLogin()).getProxy();
    private final JTextField textKerberosKrb5Conf = new JPopupTextField("Path to krb5.conf", MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().getPathKerberosKrb5()).getProxy();

    private static final String TAG_HTML_ON = "<html>";
    private static final String TAG_HTML_OFF = "</html>";
    
    public PanelAuth(PanelPreferences panelPreferences) {

        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        var groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);

        // Digest label
        var labelDigestAuthenticationUsername = new JLabel("Username  ");
        var labelDigestAuthenticationPassword = new JLabel("Password  ");
        final var labelUseDigestAuthentication = new JButton("Enable Basic, Digest and NTLM");
        String tooltipUseDigestAuthentication =
            TAG_HTML_ON
            + "Enable <b>Basic</b>, <b>Digest</b>, <b>NTLM</b> authentication (e.g. WWW-Authenticate).<br>"
            + "Then define username and password for the host.<br>"
            + "<i><b>Negotiate</b> authentication is defined in URL.</i>"
            + TAG_HTML_OFF;
        labelUseDigestAuthentication.setToolTipText(tooltipUseDigestAuthentication);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        this.getCheckboxUseDigestAuthentication().setToolTipText(tooltipUseDigestAuthentication);
        this.getCheckboxUseDigestAuthentication().setFocusable(false);
        
        // Digest label
        var labelKerberosLoginConf = new JLabel("login.conf  ");
        var labelKerberosKrb5Conf = new JLabel("krb5.conf  ");
        final var labelUseKerberos = new JButton("Enable Kerberos");
        String tooltipUseKerberos =
            TAG_HTML_ON
            + "Enable Kerberos authentication, then define path to <b>login.conf</b> and <b>krb5.conf</b>.<br>"
            + "Path to <b>.keytab</b> file is defined in login.conf ; name of <b>principal</b> must be correct.<br>"
            + "<b>Realm</b> and <b>kdc</b> are defined in krb5.conf.<br>"
            + "Finally use the <b>correct hostname</b> in URL, e.g. http://servicename.corp.test/[..]"
            + TAG_HTML_OFF;
        labelUseKerberos.setToolTipText(tooltipUseKerberos);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        this.getTextKerberosLoginConf().setToolTipText(
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
        this.getTextKerberosKrb5Conf().setToolTipText(
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
        this.getCheckboxUseKerberos().setToolTipText(tooltipUseKerberos);
        this.getCheckboxUseKerberos().setFocusable(false);
        
        labelUseKerberos.addActionListener(actionEvent -> {
            
            this.getCheckboxUseKerberos().setSelected(!this.getCheckboxUseKerberos().isSelected());
            if (this.getCheckboxUseKerberos().isSelected()) {
                
                this.getCheckboxUseDigestAuthentication().setSelected(false);
            }
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        labelUseDigestAuthentication.addActionListener(actionEvent -> {
            
            this.getCheckboxUseDigestAuthentication().setSelected(!this.getCheckboxUseDigestAuthentication().isSelected());
            if (this.getCheckboxUseDigestAuthentication().isSelected()) {
                
                this.getCheckboxUseKerberos().setSelected(false);
            }
            
            panelPreferences.getActionListenerSave().actionPerformed(null);
        });
        
        this.getTextKerberosKrb5Conf().setMaximumSize(new Dimension(400, 0));
        this.getTextKerberosLoginConf().setMaximumSize(new Dimension(400, 0));
        this.getTextDigestAuthenticationUsername().setMaximumSize(new Dimension(200, 0));
        this.getTextDigestAuthenticationPassword().setMaximumSize(new Dimension(200, 0));

        this.getTextKerberosLoginConf().setFont(UiUtil.FONT_NON_MONO_BIG);
        this.getTextKerberosKrb5Conf().setFont(UiUtil.FONT_NON_MONO_BIG);
        
        this.getTextDigestAuthenticationUsername().setFont(UiUtil.FONT_NON_MONO_BIG);
        this.getTextDigestAuthenticationPassword().setFont(UiUtil.FONT_NON_MONO_BIG);
        
        Stream
        .of(
            this.getCheckboxUseDigestAuthentication(),
            this.getCheckboxUseKerberos()
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
            this.getTextDigestAuthenticationPassword(),
            this.getTextDigestAuthenticationUsername(),
            this.getTextKerberosKrb5Conf(),
            this.getTextKerberosLoginConf()
        )
        .forEach(textField -> textField.getDocument().addDocumentListener(documentListenerSave));

        Stream
        .of(
            labelUseDigestAuthentication,
            labelUseKerberos
        )
        .forEach(label -> {
            
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorderPainted(false);
            label.setContentAreaFilled(false);
        });
        
        var labelMarginHidden = new JLabel();
        var labelMargin = new JLabel();
        labelMargin.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        groupLayout
        .setHorizontalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                .addComponent(this.getCheckboxUseDigestAuthentication())
                .addComponent(labelDigestAuthenticationUsername)
                .addComponent(labelDigestAuthenticationPassword)
                .addComponent(labelMarginHidden)
                .addComponent(this.getCheckboxUseKerberos())
                .addComponent(labelKerberosLoginConf)
                .addComponent(labelKerberosKrb5Conf)
            )
            .addGroup(
                groupLayout
                .createParallelGroup()
                .addComponent(labelUseDigestAuthentication)
                .addComponent(this.getTextDigestAuthenticationUsername())
                .addComponent(this.getTextDigestAuthenticationPassword())
                .addComponent(labelMargin)
                .addComponent(labelUseKerberos)
                .addComponent(this.getTextKerberosLoginConf())
                .addComponent(this.getTextKerberosKrb5Conf())
            )
        );
        
        groupLayout
        .setVerticalGroup(
            groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxUseDigestAuthentication())
                .addComponent(labelUseDigestAuthentication)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelDigestAuthenticationUsername)
                .addComponent(this.getTextDigestAuthenticationUsername())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelDigestAuthenticationPassword)
                .addComponent(this.getTextDigestAuthenticationPassword())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelMarginHidden)
                .addComponent(labelMargin)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.getCheckboxUseKerberos())
                .addComponent(labelUseKerberos)
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelKerberosLoginConf)
                .addComponent(this.getTextKerberosLoginConf())
            )
            .addGroup(
                groupLayout
                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelKerberosKrb5Conf)
                .addComponent(this.getTextKerberosKrb5Conf())
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
