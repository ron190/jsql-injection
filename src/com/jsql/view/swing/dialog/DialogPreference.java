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
package com.jsql.view.swing.dialog;

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

import org.apache.log4j.Logger;

import com.jsql.model.injection.InjectionModel;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.ui.RoundBorder;

/**
 * A dialog for saving application settings.
 */
@SuppressWarnings("serial")
public class DialogPreference extends JDialog {
    /**
     * Button getting focus.
     */
    private JButton okButton;

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(DialogPreference.class);

    /**
     * Create Preferences panel to save jSQL settings.
     */
    public DialogPreference() {
        super(MediatorGUI.gui(), "Preferences", Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        // Define a small and large app icon
        this.setIconImages(HelperGUI.getIcons());

        // Action for ESCAPE key
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogPreference.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        okButton = new JButton("Apply");
        okButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(132, 172, 221)),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)));
//        okButton.setBorder(new RoundBorder(7, 3, true));

        JButton cancelButton = new JButton("Close");
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(132, 172, 221)),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)));
//        cancelButton.setBorder(new RoundBorder(7, 3, true));
        cancelButton.addActionListener(escListener);

        this.getRootPane().setDefaultButton(okButton);

        this.setLayout(new BorderLayout());
        Container contentPane = this.getContentPane();
//        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JButton checkIPButton = new JButton("Check your IP", new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/wrench.png")));
        checkIPButton.setBorder(HelperGUI.BLU_ROUND_BORDER);
        checkIPButton.addActionListener(new ActionCheckIP());
        checkIPButton.setToolTipText("<html><b>Verify public IP address used by jSQL</b><br>"
                + "Address is your own public IP if you don't use a proxy. If you use a proxy<br>"
                + "like TOR, your public IP is hidden and another IP is used.</html>");

        mainPanel.add(checkIPButton);
        mainPanel.add(Box.createGlue());
        mainPanel.add(okButton);
        mainPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(cancelButton);
        contentPane.add(mainPanel, BorderLayout.SOUTH);

        // Second panel hidden by default, contain proxy setting
//        final JPanel settingPanel2 = new JPanel();
//        GroupLayout settingLayout2 = new GroupLayout(settingPanel2);
//        settingPanel2.setLayout(settingLayout2);
//        settingPanel2.setBorder(
//                BorderFactory.createCompoundBorder(
//                        BorderFactory.createCompoundBorder(
//                                BorderFactory.createEmptyBorder(5, 5, 5, 5),
//                                roundedTitledBorder2
//                                ), BorderFactory.createEmptyBorder(5, 5, 5, 5)
//                        ));
        final JCheckBox checkboxIsProxy2 = new JCheckBox("", MediatorGUI.model().updateAtStartup);
        JLabel labelUseProxy2 = new JLabel("<html><div style=\"text-align:right\">Check update<br>at startup</div></html>");
        final JCheckBox checkboxIsProxy3 = new JCheckBox("", MediatorGUI.model().reportBugs);
        JLabel labelUseProxy3 = new JLabel("<html><div style=\"text-align:right\">Report bugs<br>automatically</div></html>");
//        // Proxy settings, Horizontal column rules
//        settingLayout2.setHorizontalGroup(
//                settingLayout2.createSequentialGroup()
//                .addGroup(settingLayout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
//                        .addComponent(labelUseProxy2)
//                        .addGroup(settingLayout2.createParallelGroup()
//                                .addComponent(checkboxIsProxy2))
//                )
//        );
//
//        // Proxy settings, Vertical line rules
//        settingLayout2.setVerticalGroup(
//                settingLayout2.createSequentialGroup()
//                .addGroup(settingLayout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
//                        .addComponent(labelUseProxy2)
//                        .addComponent(checkboxIsProxy2))
//                );

        LineBorder roundedLineBorder = new LineBorder(Color.LIGHT_GRAY, 1, true);
        TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "General");
        
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
        final JTextField textProxyAddress = new JPopupTextField("e.g Tor address: 127.0.0.1", MediatorGUI.model().proxyAddress).getProxy();
        final JTextField textProxyPort = new JPopupTextField("e.g Tor port: 8118", MediatorGUI.model().proxyPort).getProxy();
        final JCheckBox checkboxIsProxy = new JCheckBox("", MediatorGUI.model().isProxyfied);

        textProxyAddress.setPreferredSize(new Dimension(0, 27));
        textProxyAddress.setFont(textProxyAddress.getFont().deriveFont(Font.PLAIN, textProxyAddress.getFont().getSize() + 2));
        textProxyPort.setPreferredSize(new Dimension(0, 27));
        textProxyPort.setFont(textProxyPort.getFont().deriveFont(Font.PLAIN, textProxyPort.getFont().getSize() + 2));
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Define proxy settings
                MediatorGUI.model().isProxyfied = checkboxIsProxy.isSelected();
                MediatorGUI.model().updateAtStartup = checkboxIsProxy2.isSelected();
                MediatorGUI.model().reportBugs = checkboxIsProxy3.isSelected();
                MediatorGUI.model().proxyAddress = textProxyAddress.getText();
                MediatorGUI.model().proxyPort = textProxyPort.getText();

                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putBoolean("isProxyfied", MediatorGUI.model().isProxyfied);
                prefs.putBoolean("updateAtStartup", MediatorGUI.model().updateAtStartup);
                prefs.putBoolean("reportBugs", MediatorGUI.model().reportBugs);
                prefs.put("proxyAddress", MediatorGUI.model().proxyAddress);
                prefs.put("proxyPort", MediatorGUI.model().proxyPort);

                if (MediatorGUI.model().isProxyfied) {
                    System.setProperty("http.proxyHost", MediatorGUI.model().proxyAddress);
                    System.setProperty("http.proxyPort", MediatorGUI.model().proxyPort);
                } else {
                    System.setProperty("http.proxyHost", "");
                    System.setProperty("http.proxyPort", "");
                }

                LOGGER.info("Preferences saved.");
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                textProxyAddress.setText(MediatorGUI.model().proxyAddress);
                textProxyPort.setText(MediatorGUI.model().proxyPort);
                checkboxIsProxy.setSelected(MediatorGUI.model().isProxyfied);
                checkboxIsProxy2.setSelected(MediatorGUI.model().updateAtStartup);
                checkboxIsProxy3.setSelected(MediatorGUI.model().reportBugs);
            }
        });

        // Proxy settings, Horizontal column rules
        settingLayout.setHorizontalGroup(
                settingLayout.createSequentialGroup()
                .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(labelUseProxy)
                        .addComponent(labelProxyAddress)
                        .addComponent(labelProxyPort)
                        .addComponent(labelUseProxy2)
                        .addComponent(labelUseProxy3))
                .addGroup(settingLayout.createParallelGroup()
                        .addComponent(checkboxIsProxy)
                        .addComponent(textProxyAddress)
                        .addComponent(textProxyPort)
                        .addComponent(checkboxIsProxy2)
                        .addComponent(checkboxIsProxy3))
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
                .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelUseProxy2)
                        .addComponent(checkboxIsProxy2))
                .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelUseProxy3)
                        .addComponent(checkboxIsProxy3))
                );

        contentPane.add(settingPanel, BorderLayout.CENTER);
//        contentPane.add(settingPanel);
//        contentPane.add(settingPanel2);

        this.pack();
        this.setSize(300, 240);
//        this.setMinimumSize(new Dimension(300, 180));
        this.getRootPane().setDefaultButton(okButton);
        cancelButton.requestFocusInWindow();
        this.setLocationRelativeTo(MediatorGUI.gui());
    }

    public void requestButtonFocus() {
        this.okButton.requestFocusInWindow();
    }
}
