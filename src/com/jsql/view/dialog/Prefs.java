/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.textcomponent.JPopupTextField;
import com.jsql.view.ui.RoundBorder;

@SuppressWarnings("serial")
public class Prefs extends JDialog {
    
    public JButton okButton;

    public Prefs() {
        super(GUIMediator.gui(), "Preferences", Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        // Define a small and large app icon
        this.setIconImages(GUITools.getIcons());

        // Action for ESCAPE key
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Prefs.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        okButton = new JButton("Apply");
        okButton.setBorder(new RoundBorder(7, 3, true));

        JButton cancelButton = new JButton("Close");
        cancelButton.setBorder(new RoundBorder(7, 3, true));
        cancelButton.addActionListener(escListener);

        this.getRootPane().setDefaultButton(okButton);

        this.setLayout(new BorderLayout());
        Container contentPane = this.getContentPane();

        JButton checkIPButton = new JButton("Check your IP", new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/wrench.png")));
        checkIPButton.setBorder(GUITools.BLU_ROUND_BORDER);
        checkIPButton.addActionListener(new ActionCheckIP());
        checkIPButton.setToolTipText("<html><b>Verify public IP address used by jSQL</b><br>"
                + "Address is your own public IP if you don't use a proxy. If you use a proxy<br>"
                + "like TOR, your public IP is hidden and another IP is used, provided by the proxy.</html>");

        mainPanel.add(checkIPButton);
        mainPanel.add(Box.createGlue());
        mainPanel.add(okButton);
        mainPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(cancelButton);
        contentPane.add(mainPanel, BorderLayout.SOUTH);

        LineBorder roundedLineBorder = new LineBorder(Color.LIGHT_GRAY, 1, true);
        TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "Proxy Setting");

        // Second panel hidden by default, contain proxy setting
        final JPanel settingPanel = new JPanel();
        GroupLayout settingLayout = new GroupLayout(settingPanel);
        settingPanel.setLayout(settingLayout);
        settingPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                                roundedTitledBorder
                                ), BorderFactory.createEmptyBorder(5, 5, 5, 5)
                        ));

        // Proxy label
        JLabel labelProxyAddress = new JLabel("Proxy address  ");
        JLabel labelProxyPort = new JLabel("Proxy port  ");
        JLabel labelUseProxy = new JLabel("Use proxy  ");

        // Proxy setting: IP, port, checkbox to activate proxy
        final JTextField textProxyAddress = new JPopupTextField(GUIMediator.model().proxyAddress).getProxy();
        final JTextField textProxyPort = new JPopupTextField(GUIMediator.model().proxyPort).getProxy();
        final JCheckBox checkboxIsProxy = new JCheckBox("", GUIMediator.model().isProxyfied);

        textProxyAddress.setPreferredSize(new Dimension(0, 27));
        textProxyAddress.setFont(textProxyAddress.getFont().deriveFont(Font.PLAIN, textProxyAddress.getFont().getSize() + 2));
        textProxyPort.setPreferredSize(new Dimension(0, 27));
        textProxyPort.setFont(textProxyPort.getFont().deriveFont(Font.PLAIN, textProxyPort.getFont().getSize() + 2));
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Define proxy settings
                GUIMediator.model().isProxyfied = checkboxIsProxy.isSelected();
                GUIMediator.model().proxyAddress = textProxyAddress.getText();
                GUIMediator.model().proxyPort = textProxyPort.getText();

                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putBoolean("isProxyfied", GUIMediator.model().isProxyfied);
                prefs.put("proxyAddress", GUIMediator.model().proxyAddress);
                prefs.put("proxyPort", GUIMediator.model().proxyPort);

                if (GUIMediator.model().isProxyfied) {
                    System.setProperty("http.proxyHost", GUIMediator.model().proxyAddress);
                    System.setProperty("http.proxyPort", GUIMediator.model().proxyPort);
                } else {
                    System.setProperty("http.proxyHost", "");
                    System.setProperty("http.proxyPort", "");
                }

                InjectionModel.LOGGER.info("Preferences saved.");
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                textProxyAddress.setText(GUIMediator.model().proxyAddress);
                textProxyPort.setText(GUIMediator.model().proxyPort);
                checkboxIsProxy.setSelected(GUIMediator.model().isProxyfied);
            }
        });

        // Proxy settings, Horizontal column rules
        settingLayout.setHorizontalGroup(
                settingLayout.createSequentialGroup()
                .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(labelUseProxy)
                        .addComponent(labelProxyAddress)
                        .addComponent(labelProxyPort))
                        .addGroup(settingLayout.createParallelGroup()
                                .addComponent(checkboxIsProxy)
                                .addComponent(textProxyAddress)
                                .addComponent(textProxyPort))
                );

        // Proxy settings, Vertical line rules
        settingLayout.setVerticalGroup(
                settingLayout.createSequentialGroup()
                .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelUseProxy)
                        .addComponent(checkboxIsProxy))
                        .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(labelProxyAddress)
                                .addComponent(textProxyAddress))
                                .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelProxyPort)
                                        .addComponent(textProxyPort))
                );

        contentPane.add(settingPanel, BorderLayout.CENTER);

        this.pack();
        this.setSize(300, 180);
        this.getRootPane().setDefaultButton(okButton);
        cancelButton.requestFocusInWindow();
        this.setLocationRelativeTo(GUIMediator.gui());
    }

    private class ActionCheckIP implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Test if proxy is available then apply settings
                    if (GUIMediator.model().isProxyfied && !GUIMediator.model().proxyAddress.equals("") && !GUIMediator.model().proxyPort.equals("")) {
                        try {
                            InjectionModel.LOGGER.info("Testing proxy...");
                            new Socket(GUIMediator.model().proxyAddress, Integer.parseInt(GUIMediator.model().proxyPort)).close();
                        } catch (Exception e) {
                            InjectionModel.LOGGER.warn("Proxy connection failed: " 
                                    + GUIMediator.model().proxyAddress + ":" + GUIMediator.model().proxyPort
                                    + "\nVerify your proxy informations or disable proxy setting.");
                            return;
                        }
                        InjectionModel.LOGGER.info("Proxy is responding.");
                    }
                    
                    BufferedReader in = null;
                    try {
                        InjectionModel.LOGGER.info("Checking IP...");
                        
                        URL whatismyip = new URL("http://checkip.amazonaws.com");
                        HttpURLConnection con = (HttpURLConnection) whatismyip.openConnection();
                        con.setDefaultUseCaches(false);
                        con.setUseCaches(false);
                        con.setRequestProperty("Pragma", "no-cache");
                        con.setRequestProperty("Cache-Control", "no-cache");
                        con.setRequestProperty("Expires", "-1");
                        
                        in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                        String ip2 = in.readLine();
                        InjectionModel.LOGGER.info("Your IP information (AWS): " + ip2);
                        
                        whatismyip = new URL("http://freegeoip.net/csv/");
                        con = (HttpURLConnection) whatismyip.openConnection();
                        con.setDefaultUseCaches(false);
                        con.setUseCaches(false);
                        con.setRequestProperty("Pragma", "no-cache");
                        con.setRequestProperty("Cache-Control", "no-cache");
                        con.setRequestProperty("Expires", "-1");
                        
                        in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                        ip2 = in.readLine();
                        InjectionModel.LOGGER.info("Your IP information (freegeoip): " + ip2);
                    } catch (MalformedURLException e) {
                        InjectionModel.LOGGER.warn("Malformed URL: " + e.getMessage());
                    } catch (IOException e) {
                        InjectionModel.LOGGER.warn("Error during proxy test: " + e.getMessage());
                        InjectionModel.LOGGER.warn("Use your browser to verify your proxy is working.");
                    } finally {
                        if (in != null) {
                            try {
                                InjectionModel.LOGGER.info("Checking IP done.");
                                in.close();
                            } catch (IOException e) {
                                InjectionModel.LOGGER.warn("Error during proxy test: " + e.getMessage());
                                InjectionModel.LOGGER.warn("Use your browser to verify your proxy is working.");
                            }
                        }
                    }
                }
            }, "Prefs - Action check IP").start();
        }
    }
}
