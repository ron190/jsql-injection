package com.jsql.view.swing.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import com.jsql.util.AuthenticationUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.action.ActionCheckIP;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.text.listener.DocumentListenerTyping;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

@SuppressWarnings("serial")
public class PanelPreferences extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    final JCheckBox checkboxIsCheckingUpdate = new JCheckBox("", PreferencesUtil.isCheckUpdateActivated());
    final JCheckBox checkboxIsReportingBugs = new JCheckBox("", PreferencesUtil.isReportingBugs());
    final JCheckBox checkboxIsUsingProxy = new JCheckBox("", ProxyUtil.isUsingProxy());
    final JCheckBox checkboxIsUsingProxyHttps = new JCheckBox("", ProxyUtil.isUsingProxyHttps());
    
    final JCheckBox checkboxIsEvading = new JCheckBox("", PreferencesUtil.isEvasionEnabled());
    final JCheckBox checkboxIsFollowingRedirection = new JCheckBox("", PreferencesUtil.isFollowingRedirection());
    final JCheckBox checkboxIsInjectingMetadata = new JCheckBox("", PreferencesUtil.isInjectingMetadata());
    final JCheckBox checkboxIsNotTestingConnection = new JCheckBox("", PreferencesUtil.isNotTestingConnection());
    final JCheckBox checkboxIsParsingForm = new JCheckBox("", PreferencesUtil.isParsingForm());
    
    final JCheckBox checkboxIsCheckingAllParam = new JCheckBox("", PreferencesUtil.isCheckingAllParam());
    final JCheckBox checkboxIsCheckingAllURLParam = new JCheckBox("", PreferencesUtil.isCheckingAllURLParam());
    final JCheckBox checkboxIsCheckingAllRequestParam = new JCheckBox("", PreferencesUtil.isCheckingAllRequestParam());
    final JCheckBox checkboxIsCheckingAllHeaderParam = new JCheckBox("", PreferencesUtil.isCheckingAllHeaderParam());
    final JCheckBox checkboxIsCheckingAllJSONParam = new JCheckBox("", PreferencesUtil.isCheckingAllJSONParam());
    final JCheckBox checkboxIsCheckingAllCookieParam = new JCheckBox("", PreferencesUtil.isCheckingAllJSONParam());

    final JCheckBox checkboxProcessCookies = new JCheckBox("", PreferencesUtil.isProcessingCookies());
    final JCheckBox checkboxProcessCsrf = new JCheckBox("", PreferencesUtil.isProcessingCsrf());

    final JTextField textProxyAddress = new JPopupTextField("e.g Tor address: 127.0.0.1", ProxyUtil.getProxyAddress()).getProxy();
    final JTextField textProxyPort = new JPopupTextField("e.g Tor port: 8118", ProxyUtil.getProxyPort()).getProxy();
    final JTextField textProxyAddressHttps = new JPopupTextField("e.g Tor address: 127.0.0.1", ProxyUtil.getProxyAddressHttps()).getProxy();
    final JTextField textProxyPortHttps = new JPopupTextField("e.g Tor port: 8118", ProxyUtil.getProxyPortHttps()).getProxy();

    final JCheckBox checkboxUseDigestAuthentication = new JCheckBox("", AuthenticationUtil.isDigestAuthentication());
    final JCheckBox checkboxUseKerberos = new JCheckBox("", AuthenticationUtil.isKerberos());

    final JTextField textDigestAuthenticationUsername = new JPopupTextField("Host system user", AuthenticationUtil.getUsernameDigest()).getProxy();
    final JTextField textDigestAuthenticationPassword = new JPopupTextField("Host system password", AuthenticationUtil.getPasswordDigest()).getProxy();
    final JTextField textKerberosLoginConf = new JPopupTextField("Path to login.conf", AuthenticationUtil.getPathKerberosLogin()).getProxy();
    final JTextField textKerberosKrb5Conf = new JPopupTextField("Path to krb5.conf", AuthenticationUtil.getPathKerberosKrb5()).getProxy();

    private class ActionListenerSave implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            PreferencesUtil.set(
                PanelPreferences.this.checkboxIsCheckingUpdate.isSelected(),
                PanelPreferences.this.checkboxIsReportingBugs.isSelected(),
                PanelPreferences.this.checkboxIsEvading.isSelected(),
                PanelPreferences.this.checkboxIsFollowingRedirection.isSelected(),
                PanelPreferences.this.checkboxIsInjectingMetadata.isSelected(),
                
                PanelPreferences.this.checkboxIsCheckingAllParam.isSelected(),
                PanelPreferences.this.checkboxIsCheckingAllURLParam.isSelected(),
                PanelPreferences.this.checkboxIsCheckingAllRequestParam.isSelected(),
                PanelPreferences.this.checkboxIsCheckingAllHeaderParam.isSelected(),
                PanelPreferences.this.checkboxIsCheckingAllJSONParam.isSelected(),
                PanelPreferences.this.checkboxIsCheckingAllCookieParam.isSelected(),
                
                PanelPreferences.this.checkboxIsParsingForm.isSelected(),
                PanelPreferences.this.checkboxIsNotTestingConnection.isSelected(),
                PanelPreferences.this.checkboxProcessCookies.isSelected(),
                PanelPreferences.this.checkboxProcessCsrf.isSelected()
            );
            
            ProxyUtil.set(
                PanelPreferences.this.checkboxIsUsingProxy.isSelected(),
                PanelPreferences.this.textProxyAddress.getText(),
                PanelPreferences.this.textProxyPort.getText(),
                PanelPreferences.this.checkboxIsUsingProxyHttps.isSelected(),
                PanelPreferences.this.textProxyAddressHttps.getText(),
                PanelPreferences.this.textProxyPortHttps.getText()
            );
            
            AuthenticationUtil.set(
                PanelPreferences.this.checkboxUseDigestAuthentication.isSelected(),
                PanelPreferences.this.textDigestAuthenticationUsername.getText(),
                PanelPreferences.this.textDigestAuthenticationPassword.getText(),
                PanelPreferences.this.checkboxUseKerberos.isSelected(),
                PanelPreferences.this.textKerberosKrb5Conf.getText(),
                PanelPreferences.this.textKerberosLoginConf.getText()
            );
        }
        
    }
    
    private transient ActionListener actionListenerSave = new ActionListenerSave();

    private static final JPanel panelInjection = new JPanel(new BorderLayout());
    private static final JPanel panelAuthentication = new JPanel(new BorderLayout());
    private static final JPanel panelProxy = new JPanel(new BorderLayout());
    private static final JPanel panelGeneral = new JPanel(new BorderLayout());
    
    private enum CategoryPreference {
        
        GENERAL(panelGeneral),
        INJECTION(panelInjection),
        AUTHENTICATION(panelAuthentication),
        PROXY(panelProxy),
        USER_AGENT(new JPanel());
        
        private Component panel;

        private CategoryPreference(Component panel) {
            this.panel = panel;
        }
        
        @Override
        public String toString() {
            return "  "+ WordUtils.capitalizeFully(this.name()).replace('_', ' ') +"  ";
        }

        public Component getPanel() {
            return this.panel;
        }
        
    }
    
    public PanelPreferences() {
        BorderLayout borderLayoutPreferences = new BorderLayout();
        this.setLayout(borderLayoutPreferences);

        final JButton buttonCheckIp = new JButton("Check your IP");
        buttonCheckIp.addActionListener(new ActionCheckIP());
        buttonCheckIp.setToolTipText(
            "<html><b>Verify what public IP address is used by jSQL</b><br>"
            + "Usually it's your own public IP if you don't use a proxy. If you use a proxy<br>"
            + "like TOR then your public IP is hidden and another one is used instead.</html>"
        );
        buttonCheckIp.setContentAreaFilled(true);
        buttonCheckIp.setBorder(HelperUi.BORDER_ROUND_BLU);
        
        FlatButtonMouseAdapter flatButtonMouseAdapter = new FlatButtonMouseAdapter(buttonCheckIp);
        flatButtonMouseAdapter.setContentVisible(true);
        buttonCheckIp.addMouseListener(flatButtonMouseAdapter);

        this.checkboxIsCheckingUpdate.setFocusable(false);
        JButton labelIsCheckingUpdate = new JButton("<html>Check update at startup<br><span style=\"color: gray; font-style: italic;\">Hi!</span></html>");
        labelIsCheckingUpdate.addActionListener(actionEvent -> {
            this.checkboxIsCheckingUpdate.setSelected(!this.checkboxIsCheckingUpdate.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        String tooltipIsReportingBugs = "Send unhandled exception to developer in order to fix issues.";
        this.checkboxIsReportingBugs.setToolTipText(tooltipIsReportingBugs);
        this.checkboxIsReportingBugs.setFocusable(false);
        JButton labelIsReportingBugs = new JButton("Report unhandled exceptions");
        labelIsReportingBugs.setToolTipText(tooltipIsReportingBugs);
        labelIsReportingBugs.addActionListener(actionEvent -> {
            this.checkboxIsReportingBugs.setSelected(!this.checkboxIsReportingBugs.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        String tooltipIsEvading = "Use complex SQL syntaxes to bypass protection (slower).";
        this.checkboxIsEvading.setToolTipText(tooltipIsEvading);
        this.checkboxIsEvading.setFocusable(false);
        JButton labelIsEvading = new JButton("Enable evasion");
        labelIsEvading.setToolTipText(tooltipIsEvading);
        labelIsEvading.addActionListener(actionEvent -> {
            this.checkboxIsEvading.setSelected(!this.checkboxIsEvading.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        String tooltipIsFollowingRedirection = "Force redirection when the page has moved (e.g. HTTP/1.1 302 Found).";
        this.checkboxIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        this.checkboxIsFollowingRedirection.setFocusable(false);
        JButton labelIsFollowingRedirection = new JButton("Follow HTTP redirection");
        labelIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        labelIsFollowingRedirection.addActionListener(actionEvent -> {
            this.checkboxIsFollowingRedirection.setSelected(!this.checkboxIsFollowingRedirection.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        String tooltipTestConnection = "Disable initial connection test";
        this.checkboxIsNotTestingConnection.setToolTipText(tooltipTestConnection);
        this.checkboxIsNotTestingConnection.setFocusable(false);
        JButton labelTestConnection = new JButton("Disable initial connection test");
        labelTestConnection.setToolTipText(tooltipTestConnection);
        labelTestConnection.addActionListener(actionEvent -> {
            this.checkboxIsNotTestingConnection.setSelected(!this.checkboxIsNotTestingConnection.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        String tooltipProcessCookies = "Save session cookies";
        this.checkboxProcessCookies.setToolTipText(tooltipProcessCookies);
        this.checkboxProcessCookies.setFocusable(false);
        JButton labelProcessCookies = new JButton("Save session cookies");
        labelProcessCookies.setToolTipText(tooltipProcessCookies);
        labelProcessCookies.addActionListener(actionEvent -> {
            this.checkboxProcessCookies.setSelected(!this.checkboxProcessCookies.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        ActionListener actionListenerProcessCsrf = actionEvent -> {
            if (actionEvent.getSource() != this.checkboxProcessCsrf) {
                this.checkboxProcessCsrf.setSelected(!this.checkboxProcessCsrf.isSelected());
            }
            
            if (this.checkboxProcessCsrf.isSelected()) {
                this.checkboxProcessCookies.setSelected(this.checkboxProcessCsrf.isSelected());
            }
            this.checkboxProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
            labelProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
            
            this.actionListenerSave.actionPerformed(null);
        };
        
        this.checkboxProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
        labelProcessCookies.setEnabled(!this.checkboxProcessCsrf.isSelected());
        
        String tooltipProcessCsrf = "Process CSRF token";
        this.checkboxProcessCsrf.setToolTipText(tooltipProcessCsrf);
        this.checkboxProcessCsrf.setFocusable(false);
        JButton labelProcessCsrf = new JButton("Process CSRF token");
        labelProcessCsrf.setToolTipText(tooltipProcessCsrf);
        labelProcessCsrf.addActionListener(actionListenerProcessCsrf);
        this.checkboxProcessCsrf.addActionListener(actionListenerProcessCsrf);
        
        String tooltipParseForm = "Add <input> parameters found in HTML body to URL and Request";
        this.checkboxIsParsingForm.setToolTipText(tooltipParseForm);
        this.checkboxIsParsingForm.setFocusable(false);
        JButton labelParseForm = new JButton("Add <input> parameters found in HTML body to URL and Request");
        labelParseForm.setToolTipText(tooltipParseForm);
        labelParseForm.addActionListener(actionEvent -> {
            this.checkboxIsParsingForm.setSelected(!this.checkboxIsParsingForm.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        String tooltipIsInjectingMetadata = "Enable database's metadata injection (e.g version, username).";
        this.checkboxIsInjectingMetadata.setToolTipText(tooltipIsInjectingMetadata);
        this.checkboxIsInjectingMetadata.setFocusable(false);
        JButton labelIsInjectingMetadata = new JButton("Retreive database's metadata (disable to speed-up boolean process)");
        labelIsInjectingMetadata.setToolTipText(tooltipIsInjectingMetadata);
        labelIsInjectingMetadata.addActionListener(actionEvent -> {
            this.checkboxIsInjectingMetadata.setSelected(!this.checkboxIsInjectingMetadata.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        JPanel panelGeneralPreferences = new JPanel();
        panelGeneralPreferences.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelGeneral.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        panelGeneral.add(new JLabel("<html><b>General</b> / Standard options</html>"), BorderLayout.NORTH);
        panelGeneral.add(panelGeneralPreferences, BorderLayout.CENTER);
        
        JPanel panelInjectionPreferences = new JPanel();
        panelInjectionPreferences.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelInjection.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        panelInjection.add(new JLabel("<html><b>Injection</b> / Standard options</html>"), BorderLayout.NORTH);
        panelInjection.add(panelInjectionPreferences, BorderLayout.CENTER);
        
        JPanel panelAuthenticationPreferences = new JPanel();
        panelAuthenticationPreferences.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelAuthentication.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        panelAuthentication.add(new JLabel("<html><b>Authentication</b> / Basic, Digest, NTLM or Kerberos</html>"), BorderLayout.NORTH);
        panelAuthentication.add(panelAuthenticationPreferences, BorderLayout.CENTER);
        
        JPanel panelProxyPreferences = new JPanel();
        panelProxyPreferences.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelProxy.setLayout(new BoxLayout(panelProxy, BoxLayout.Y_AXIS));
        panelProxy.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        
        JLabel labelProxy = new JLabel("<html><b>Proxy</b> / Define proxy settings (e.g. TOR)</html>");
        JLabel labelProxyHttpHidden = new JLabel();
        JLabel labelProxyHttp = new JLabel("<html><b>Handling HTTP protocol</b></html>");
        JLabel labelProxyHttpsHidden = new JLabel();
        JLabel labelProxyHttps = new JLabel("<html><b>Handling HTTPS protocol</b></html>");
        labelProxyHttp.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        labelProxyHttps.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panelProxy.removeAll();
        panelProxy.add(labelProxy, BorderLayout.NORTH);
        panelProxy.add(panelProxyPreferences);
        
        JPanel panelCheckIp = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        panelCheckIp.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panelCheckIp.add(buttonCheckIp);
        panelCheckIp.add(Box.createGlue());
        panelProxy.add(panelCheckIp);
        
        labelProxy.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelProxyPreferences.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCheckIp.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GroupLayout groupLayoutGeneral = new GroupLayout(panelGeneralPreferences);
        panelGeneralPreferences.setLayout(groupLayoutGeneral);
        GroupLayout groupLayoutInjection = new GroupLayout(panelInjectionPreferences);
        panelInjectionPreferences.setLayout(groupLayoutInjection);
        GroupLayout groupLayoutAuthentication = new GroupLayout(panelAuthenticationPreferences);
        panelAuthenticationPreferences.setLayout(groupLayoutAuthentication);
        GroupLayout groupLayoutProxy = new GroupLayout(panelProxyPreferences);
        panelProxyPreferences.setLayout(groupLayoutProxy);

        JButton labelIsCheckingAllParam = new JButton("Inject every parameters");
        JButton labelIsCheckingAllURLParam = new JButton("<html>Inject every URL parameters <i style=\"color: gray\">- if method GET is selected</i></html>");
        JButton labelIsCheckingAllRequestParam = new JButton("<html>Inject every Request parameters <i style=\"color: gray\">- if method Request like POST is selected</i></html>");
        JButton labelIsCheckingAllHeaderParam = new JButton("<html>Inject every Header parameters <i style=\"color: gray\">- if method Header is selected</i></html>");
        JButton labelIsCheckingAllJSONParam = new JButton("Inject every JSON parameters");
        JButton labelIsCheckingAllCookieParam = new JButton("Inject every Cookie parameters");
        
        JLabel emptyLabelParamsInjection = new JLabel();
        JLabel labelParamsInjection = new JLabel("<html><br /><b>Parameters injection</b></html>");
        JLabel emptyLabelSessionManagement = new JLabel();
        JLabel labelSessionManagement = new JLabel("<html><br /><b>Session and Cookie management</b></html>");
        
        ActionListener actionListenerCheckingAllParam = actionEvent -> {
            if (actionEvent.getSource() != this.checkboxIsCheckingAllParam) {
                this.checkboxIsCheckingAllParam.setSelected(!this.checkboxIsCheckingAllParam.isSelected());
            }
            
            this.checkboxIsCheckingAllURLParam.setSelected(this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllRequestParam.setSelected(this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllHeaderParam.setSelected(this.checkboxIsCheckingAllParam.isSelected());
            
            this.checkboxIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            this.checkboxIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            
            labelIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            labelIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            labelIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
            
            this.actionListenerSave.actionPerformed(null);
        };
        
        this.checkboxIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        this.checkboxIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        this.checkboxIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        
        labelIsCheckingAllURLParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        labelIsCheckingAllRequestParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        labelIsCheckingAllHeaderParam.setEnabled(!this.checkboxIsCheckingAllParam.isSelected());
        
        labelIsCheckingAllParam.addActionListener(actionListenerCheckingAllParam);
        labelIsCheckingAllURLParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllURLParam.setSelected(!this.checkboxIsCheckingAllURLParam.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        labelIsCheckingAllRequestParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllRequestParam.setSelected(!this.checkboxIsCheckingAllRequestParam.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        labelIsCheckingAllHeaderParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllHeaderParam.setSelected(!this.checkboxIsCheckingAllHeaderParam.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        labelIsCheckingAllJSONParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllJSONParam.setSelected(!this.checkboxIsCheckingAllJSONParam.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        labelIsCheckingAllCookieParam.addActionListener(actionEvent -> {
            this.checkboxIsCheckingAllCookieParam.setSelected(!this.checkboxIsCheckingAllCookieParam.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        // Proxy label
        JLabel labelProxyAddress = new JLabel("Proxy address  ");
        JLabel labelProxyPort = new JLabel("Proxy port  ");
        JLabel labelProxyAddressHttps = new JLabel("Proxy address  ");
        JLabel labelProxyPortHttps = new JLabel("Proxy port  ");
        JButton buttonIsUsingProxy = new JButton("Use a proxy for HTTP protocol");
        JButton buttonIsUsingProxyHttps = new JButton("Use a proxy for HTTPS protocol");
        String tooltipIsUsingProxy = "Enable proxy communication (e.g. TOR with Privoxy or Burp) for HTTP protocol.";
        buttonIsUsingProxy.setToolTipText(tooltipIsUsingProxy);
        String tooltipIsUsingProxyHttps = "Enable proxy communication (e.g. TOR with Privoxy or Burp) for HTTPS protocol.";
        buttonIsUsingProxyHttps.setToolTipText(tooltipIsUsingProxyHttps);

        // Proxy setting: IP, port, checkbox to activate proxy
        this.checkboxIsUsingProxy.setToolTipText(tooltipIsUsingProxy);
        this.checkboxIsUsingProxy.setFocusable(false);

        buttonIsUsingProxy.addActionListener(actionEvent -> {
            this.checkboxIsUsingProxy.setSelected(!this.checkboxIsUsingProxy.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        this.checkboxIsUsingProxyHttps.setToolTipText(tooltipIsUsingProxyHttps);
        this.checkboxIsUsingProxyHttps.setFocusable(false);
        
        buttonIsUsingProxyHttps.addActionListener(actionEvent -> {
            this.checkboxIsUsingProxyHttps.setSelected(!this.checkboxIsUsingProxyHttps.isSelected());
            this.actionListenerSave.actionPerformed(null);
        });
        
        // Digest label
        JLabel labelDigestAuthenticationUsername = new JLabel("Username  ");
        JLabel labelDigestAuthenticationPassword = new JLabel("Password  ");
        final JButton labelUseDigestAuthentication = new JButton("Enable Basic, Digest, NTLM");
        String tooltipUseDigestAuthentication =
            "<html>"
            + "Enable <b>Basic</b>, <b>Digest</b>, <b>NTLM</b> authentication (e.g. WWW-Authenticate).<br>"
            + "Then define username and password for the host.<br>"
            + "<i><b>Negotiate</b> authentication is defined in URL.</i>"
            + "</html>";
        labelUseDigestAuthentication.setToolTipText(tooltipUseDigestAuthentication);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        this.checkboxUseDigestAuthentication.setToolTipText(tooltipUseDigestAuthentication);
        this.checkboxUseDigestAuthentication.setFocusable(false);
        
        // Digest label
        JLabel labelKerberosLoginConf = new JLabel("login.conf  ");
        JLabel labelKerberosKrb5Conf = new JLabel("krb5.conf  ");
        final JButton labelUseKerberos = new JButton("Enable Kerberos");
        String tooltipUseKerberos =
            "<html>"
            + "Activate Kerberos authentication, then define path to <b>login.conf</b> and <b>krb5.conf</b>.<br>"
            + "Path to <b>.keytab</b> file is defined in login.conf ; name of <b>principal</b> must be correct.<br>"
            + "<b>Realm</b> and <b>kdc</b> are defined in krb5.conf.<br>"
            + "Finally use the <b>correct hostname</b> in URL, e.g. http://servicename.corp.test/[..]"
            + "</html>";
        labelUseKerberos.setToolTipText(tooltipUseKerberos);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        this.textKerberosLoginConf.setToolTipText(
            "<html>"
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
            + "</html>");
        this.textKerberosKrb5Conf.setToolTipText(
            "<html>"
            + "Define the path to <b>krb5.conf</b>. Sample :<br>"
            + "&emsp;[libdefaults]<br>"
            + "&emsp;&emsp;default_realm = <b>CORP.TEST</b><br>"
            + "&emsp;&emsp;udp_preference_limit = 1<br>"
            + "&emsp;[realms]<br>"
            + "&emsp;&emsp;<b>CORP.TEST</b> = {<br>"
            + "&emsp;&emsp;&emsp;kdc = <b>127.0.0.1:88</b><br>"
            + "&emsp;&emsp;}<br>"
            + "<i>Realm and kdc are case sensitives.</i>"
            + "</html>");
        this.checkboxUseKerberos.setToolTipText(tooltipUseKerberos);
        this.checkboxUseKerberos.setFocusable(false);
        
        labelUseKerberos.addActionListener(actionEvent -> {
            this.checkboxUseKerberos.setSelected(!this.checkboxUseKerberos.isSelected());
            if (this.checkboxUseKerberos.isSelected()) {
                this.checkboxUseDigestAuthentication.setSelected(false);
            }
            this.actionListenerSave.actionPerformed(null);
        });
        
        labelUseDigestAuthentication.addActionListener(actionEvent -> {
            this.checkboxUseDigestAuthentication.setSelected(!this.checkboxUseDigestAuthentication.isSelected());
            if (this.checkboxUseDigestAuthentication.isSelected()) {
                this.checkboxUseKerberos.setSelected(false);
            }
            this.actionListenerSave.actionPerformed(null);
        });
        
        this.textKerberosKrb5Conf.setMaximumSize(new Dimension(400, 0));
        this.textKerberosLoginConf.setMaximumSize(new Dimension(400, 0));
        this.textDigestAuthenticationUsername.setMaximumSize(new Dimension(200, 0));
        this.textDigestAuthenticationPassword.setMaximumSize(new Dimension(200, 0));
        this.textProxyAddress.setMaximumSize(new Dimension(200, 0));
        this.textProxyPort.setMaximumSize(new Dimension(200, 0));
        this.textProxyAddressHttps.setMaximumSize(new Dimension(200, 0));
        this.textProxyPortHttps.setMaximumSize(new Dimension(200, 0));
        
        this.textProxyAddress.setFont(HelperUi.FONT_SEGOE_BIG);
        this.textProxyPort.setFont(HelperUi.FONT_SEGOE_BIG);
        this.textProxyAddressHttps.setFont(HelperUi.FONT_SEGOE_BIG);
        this.textProxyPortHttps.setFont(HelperUi.FONT_SEGOE_BIG);
        this.textKerberosLoginConf.setFont(HelperUi.FONT_SEGOE_BIG);
        this.textKerberosKrb5Conf.setFont(HelperUi.FONT_SEGOE_BIG);
        
        this.textDigestAuthenticationUsername.setFont(HelperUi.FONT_SEGOE_BIG);
        this.textDigestAuthenticationPassword.setFont(HelperUi.FONT_SEGOE_BIG);
        
        this.checkboxIsCheckingUpdate.addActionListener(this.actionListenerSave);
        this.checkboxIsEvading.addActionListener(this.actionListenerSave);
        this.checkboxIsFollowingRedirection.addActionListener(this.actionListenerSave);
        this.checkboxIsInjectingMetadata.addActionListener(this.actionListenerSave);
        this.checkboxIsReportingBugs.addActionListener(this.actionListenerSave);
        this.checkboxIsUsingProxy.addActionListener(this.actionListenerSave);
        this.checkboxIsParsingForm.addActionListener(this.actionListenerSave);
        this.checkboxIsNotTestingConnection.addActionListener(this.actionListenerSave);
        this.checkboxUseDigestAuthentication.addActionListener(this.actionListenerSave);
        this.checkboxUseKerberos.addActionListener(this.actionListenerSave);
        this.checkboxProcessCookies.addActionListener(this.actionListenerSave);
        this.checkboxProcessCsrf.addActionListener(this.actionListenerSave);
        
        this.checkboxIsCheckingAllParam.addActionListener(actionListenerCheckingAllParam);
        this.checkboxIsCheckingAllURLParam.addActionListener(this.actionListenerSave);
        this.checkboxIsCheckingAllRequestParam.addActionListener(this.actionListenerSave);
        this.checkboxIsCheckingAllHeaderParam.addActionListener(this.actionListenerSave);
        this.checkboxIsCheckingAllJSONParam.addActionListener(this.actionListenerSave);
        this.checkboxIsCheckingAllCookieParam.addActionListener(this.actionListenerSave);
        
        class DocumentListenerSave extends DocumentListenerTyping {
            
            @Override
            public void warn() {
                PanelPreferences.this.actionListenerSave.actionPerformed(null);
            }
            
        }
        
        DocumentListener documentListenerSave = new DocumentListenerSave();

        this.textDigestAuthenticationPassword.getDocument().addDocumentListener(documentListenerSave);
        this.textDigestAuthenticationUsername.getDocument().addDocumentListener(documentListenerSave);
        this.textKerberosKrb5Conf.getDocument().addDocumentListener(documentListenerSave);
        this.textKerberosLoginConf.getDocument().addDocumentListener(documentListenerSave);
        this.textProxyAddress.getDocument().addDocumentListener(documentListenerSave);
        this.textProxyPort.getDocument().addDocumentListener(documentListenerSave);
        this.textProxyAddressHttps.getDocument().addDocumentListener(documentListenerSave);
        this.textProxyPortHttps.getDocument().addDocumentListener(documentListenerSave);

        labelIsCheckingUpdate.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsCheckingUpdate.setBorderPainted(false);
        labelIsCheckingUpdate.setContentAreaFilled(false);
        
        labelIsReportingBugs.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsReportingBugs.setBorderPainted(false);
        labelIsReportingBugs.setContentAreaFilled(false);
        
        labelIsEvading.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsEvading.setBorderPainted(false);
        labelIsEvading.setContentAreaFilled(false);
        
        labelIsFollowingRedirection.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsFollowingRedirection.setBorderPainted(false);
        labelIsFollowingRedirection.setContentAreaFilled(false);
        
        labelTestConnection.setHorizontalAlignment(SwingConstants.LEFT);
        labelTestConnection.setBorderPainted(false);
        labelTestConnection.setContentAreaFilled(false);
        
        labelParseForm.setHorizontalAlignment(SwingConstants.LEFT);
        labelParseForm.setBorderPainted(false);
        labelParseForm.setContentAreaFilled(false);
        
        buttonIsUsingProxy.setHorizontalAlignment(SwingConstants.LEFT);
        buttonIsUsingProxy.setBorderPainted(false);
        buttonIsUsingProxy.setContentAreaFilled(false);
        
        buttonIsUsingProxyHttps.setHorizontalAlignment(SwingConstants.LEFT);
        buttonIsUsingProxyHttps.setBorderPainted(false);
        buttonIsUsingProxyHttps.setContentAreaFilled(false);
        
        labelUseDigestAuthentication.setHorizontalAlignment(SwingConstants.LEFT);
        labelUseDigestAuthentication.setBorderPainted(false);
        labelUseDigestAuthentication.setContentAreaFilled(false);
        
        labelUseKerberos.setHorizontalAlignment(SwingConstants.LEFT);
        labelUseKerberos.setBorderPainted(false);
        labelUseKerberos.setContentAreaFilled(false);
        
        labelIsInjectingMetadata.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsInjectingMetadata.setBorderPainted(false);
        labelIsInjectingMetadata.setContentAreaFilled(false);
        
        labelIsCheckingAllParam.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsCheckingAllParam.setBorderPainted(false);
        labelIsCheckingAllParam.setContentAreaFilled(false);
        
        labelIsCheckingAllURLParam.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsCheckingAllURLParam.setBorderPainted(false);
        labelIsCheckingAllURLParam.setContentAreaFilled(false);
        
        labelIsCheckingAllRequestParam.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsCheckingAllRequestParam.setBorderPainted(false);
        labelIsCheckingAllRequestParam.setContentAreaFilled(false);
        
        labelIsCheckingAllHeaderParam.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsCheckingAllHeaderParam.setBorderPainted(false);
        labelIsCheckingAllHeaderParam.setContentAreaFilled(false);
        
        labelIsCheckingAllJSONParam.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsCheckingAllJSONParam.setBorderPainted(false);
        labelIsCheckingAllJSONParam.setContentAreaFilled(false);
        
        labelIsCheckingAllCookieParam.setHorizontalAlignment(SwingConstants.LEFT);
        labelIsCheckingAllCookieParam.setBorderPainted(false);
        labelIsCheckingAllCookieParam.setContentAreaFilled(false);
        
        labelProcessCookies.setHorizontalAlignment(SwingConstants.LEFT);
        labelProcessCookies.setBorderPainted(false);
        labelProcessCookies.setContentAreaFilled(false);
        
        labelProcessCsrf.setHorizontalAlignment(SwingConstants.LEFT);
        labelProcessCsrf.setBorderPainted(false);
        labelProcessCsrf.setContentAreaFilled(false);
        
        // Proxy settings, Horizontal column rules

        groupLayoutGeneral.setHorizontalGroup(
            groupLayoutGeneral.createSequentialGroup()
            .addGroup(
                groupLayoutGeneral
                    .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(this.checkboxIsCheckingUpdate)
                    .addComponent(this.checkboxIsReportingBugs)
            ).addGroup(
                groupLayoutGeneral
                    .createParallelGroup()
                    .addComponent(labelIsCheckingUpdate)
                    .addComponent(labelIsReportingBugs)
        ));
        
        groupLayoutInjection.setHorizontalGroup(
            groupLayoutInjection.createSequentialGroup()
            .addGroup(
                groupLayoutInjection
                    .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(this.checkboxIsEvading)
                    .addComponent(this.checkboxIsFollowingRedirection)
                    .addComponent(this.checkboxIsInjectingMetadata)
                    .addComponent(this.checkboxIsNotTestingConnection)
                    .addComponent(this.checkboxIsParsingForm)
                    
                    .addComponent(emptyLabelParamsInjection)
                    .addComponent(this.checkboxIsCheckingAllParam)
                    .addComponent(this.checkboxIsCheckingAllURLParam)
                    .addComponent(this.checkboxIsCheckingAllRequestParam)
                    .addComponent(this.checkboxIsCheckingAllHeaderParam)
                    .addComponent(this.checkboxIsCheckingAllJSONParam)
                    .addComponent(this.checkboxIsCheckingAllCookieParam)
                    
                    .addComponent(emptyLabelSessionManagement)
                    .addComponent(this.checkboxProcessCsrf)
                    .addComponent(this.checkboxProcessCookies)
            ).addGroup(
                groupLayoutInjection
                    .createParallelGroup()
                    .addComponent(labelIsEvading)
                    .addComponent(labelIsFollowingRedirection)
                    .addComponent(labelIsInjectingMetadata)
                    .addComponent(labelTestConnection)
                    .addComponent(labelParseForm)
                    
                    .addComponent(labelParamsInjection)
                    .addComponent(labelIsCheckingAllParam)
                    .addComponent(labelIsCheckingAllURLParam)
                    .addComponent(labelIsCheckingAllRequestParam)
                    .addComponent(labelIsCheckingAllHeaderParam)
                    .addComponent(labelIsCheckingAllJSONParam)
                    .addComponent(labelIsCheckingAllCookieParam)
                    
                    .addComponent(labelSessionManagement)
                    .addComponent(labelProcessCsrf)
                    .addComponent(labelProcessCookies)
        ));

        groupLayoutAuthentication.setHorizontalGroup(
            groupLayoutAuthentication.createSequentialGroup()
            .addGroup(
                groupLayoutAuthentication
                    .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(this.checkboxUseDigestAuthentication)
                    .addComponent(labelDigestAuthenticationUsername)
                    .addComponent(labelDigestAuthenticationPassword)
                    .addComponent(this.checkboxUseKerberos)
                    .addComponent(labelKerberosLoginConf)
                    .addComponent(labelKerberosKrb5Conf)
            ).addGroup(
                groupLayoutAuthentication
                    .createParallelGroup()
                    .addComponent(labelUseDigestAuthentication)
                    .addComponent(this.textDigestAuthenticationUsername)
                    .addComponent(this.textDigestAuthenticationPassword)
                    .addComponent(labelUseKerberos)
                    .addComponent(this.textKerberosLoginConf)
                    .addComponent(this.textKerberosKrb5Conf)
        ));

        groupLayoutProxy.setHorizontalGroup(
            groupLayoutProxy.createSequentialGroup()
            .addGroup(
                groupLayoutProxy
                    .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(labelProxyHttpHidden)
                    .addComponent(this.checkboxIsUsingProxy)
                    .addComponent(labelProxyAddress)
                    .addComponent(labelProxyPort)
                    .addComponent(labelProxyHttpsHidden)
                    .addComponent(this.checkboxIsUsingProxyHttps)
                    .addComponent(labelProxyAddressHttps)
                    .addComponent(labelProxyPortHttps)
            ).addGroup(
                groupLayoutProxy
                    .createParallelGroup()
                    .addComponent(labelProxyHttp)
                    .addComponent(buttonIsUsingProxy)
                    .addComponent(this.textProxyAddress)
                    .addComponent(this.textProxyPort)
                    .addComponent(labelProxyHttps)
                    .addComponent(buttonIsUsingProxyHttps)
                    .addComponent(this.textProxyAddressHttps)
                    .addComponent(this.textProxyPortHttps)
        ));

        // Proxy settings, Vertical line rules

        groupLayoutGeneral.setVerticalGroup(
            groupLayoutInjection
                .createSequentialGroup()
                .addGroup(
                    groupLayoutGeneral
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsCheckingUpdate)
                        .addComponent(labelIsCheckingUpdate)
                ).addGroup(
                    groupLayoutGeneral
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsReportingBugs)
                        .addComponent(labelIsReportingBugs)
                )
        );
        
        groupLayoutInjection.setVerticalGroup(
            groupLayoutInjection
                .createSequentialGroup()
                .addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsEvading)
                        .addComponent(labelIsEvading)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsFollowingRedirection)
                        .addComponent(labelIsFollowingRedirection)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsInjectingMetadata)
                        .addComponent(labelIsInjectingMetadata)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsNotTestingConnection)
                        .addComponent(labelTestConnection)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsParsingForm)
                        .addComponent(labelParseForm)
                        
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(emptyLabelParamsInjection)
                        .addComponent(labelParamsInjection)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsCheckingAllParam)
                        .addComponent(labelIsCheckingAllParam)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsCheckingAllURLParam)
                        .addComponent(labelIsCheckingAllURLParam)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsCheckingAllRequestParam)
                        .addComponent(labelIsCheckingAllRequestParam)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsCheckingAllHeaderParam)
                        .addComponent(labelIsCheckingAllHeaderParam)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsCheckingAllJSONParam)
                        .addComponent(labelIsCheckingAllJSONParam)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsCheckingAllCookieParam)
                        .addComponent(labelIsCheckingAllCookieParam)
                        
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(emptyLabelSessionManagement)
                        .addComponent(labelSessionManagement)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxProcessCsrf)
                        .addComponent(labelProcessCsrf)
                ).addGroup(
                    groupLayoutInjection
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxProcessCookies)
                        .addComponent(labelProcessCookies)
                )
        );

        groupLayoutAuthentication.setVerticalGroup(
            groupLayoutAuthentication
                .createSequentialGroup()
                .addGroup(
                    groupLayoutAuthentication
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxUseDigestAuthentication)
                        .addComponent(labelUseDigestAuthentication)
                ).addGroup(
                    groupLayoutAuthentication
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelDigestAuthenticationUsername)
                        .addComponent(this.textDigestAuthenticationUsername)
                ).addGroup(
                    groupLayoutAuthentication
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelDigestAuthenticationPassword)
                        .addComponent(this.textDigestAuthenticationPassword)
                ).addGroup(
                    groupLayoutAuthentication
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxUseKerberos)
                        .addComponent(labelUseKerberos)
                ).addGroup(
                    groupLayoutAuthentication
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelKerberosLoginConf)
                        .addComponent(this.textKerberosLoginConf)
                ).addGroup(
                    groupLayoutAuthentication
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelKerberosKrb5Conf)
                        .addComponent(this.textKerberosKrb5Conf)
                )
        );

        groupLayoutProxy.setVerticalGroup(
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
                        .addComponent(this.checkboxIsUsingProxy)
                        .addComponent(buttonIsUsingProxy)
                ).addGroup(
                    groupLayoutProxy
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyAddress)
                        .addComponent(this.textProxyAddress)
                ).addGroup(
                    groupLayoutProxy
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyPort)
                        .addComponent(this.textProxyPort)
                )
                .addGroup(
                    groupLayoutProxy
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyHttpsHidden)
                        .addComponent(labelProxyHttps)
                )
                .addGroup(
                    groupLayoutProxy
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.checkboxIsUsingProxyHttps)
                        .addComponent(buttonIsUsingProxyHttps)
                ).addGroup(
                    groupLayoutProxy
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyAddressHttps)
                        .addComponent(this.textProxyAddressHttps)
                ).addGroup(
                    groupLayoutProxy
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyPortHttps)
                        .addComponent(this.textProxyPortHttps)
                )
        );
        
        this.add(panelGeneral, BorderLayout.CENTER);
        
        JList<CategoryPreference> categories = new JList<>(CategoryPreference.values());
        categories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categories.setSelectedIndex(0);
        
        categories.setBorder(BorderFactory.createLineBorder(HelperUi.COLOR_COMPONENT_BORDER));
        categories.addListSelectionListener(e -> {
            PanelPreferences.this.remove(borderLayoutPreferences.getLayoutComponent(BorderLayout.CENTER));
            PanelPreferences.this.add(categories.getSelectedValue().getPanel(), BorderLayout.CENTER);
            // Both required
            PanelPreferences.this.revalidate();
            PanelPreferences.this.repaint();
        });
        
        categories.setCellRenderer(new DefaultListCellRenderer(){

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel labelListItem = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                labelListItem.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(3, 3, 0, 3, Color.WHITE),
                        labelListItem.getBorder()
                    )
                );
                return labelListItem;
            }
            
        });
        
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(categories, BorderLayout.LINE_START);
    }
    
}
