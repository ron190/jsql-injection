package com.jsql.mvc.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.view.component.RoundedCornerBorder;
import com.jsql.mvc.view.component.popup.JPopupTextField;

public class PreferencesDialog extends JDialog{
    private static final long serialVersionUID = 1093836441729193729L;

    JButton okButton;
    
    public PreferencesDialog(final GUI gui){
        super(gui, "Preferences", Dialog.ModalityType.MODELESS);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        
        // Define a small and large app icon
        this.setIconImages(gui.images);
        
        // Action for ESCAPE key
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreferencesDialog.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);        
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        
        okButton = new JButton("Ok");
        okButton.setBorder(new RoundedCornerBorder(20,3,true));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBorder(new RoundedCornerBorder(7,3,true));
        cancelButton.addActionListener(escListener);
        
        this.getRootPane().setDefaultButton(okButton);
        
        this.setLayout(new BorderLayout());
        Container contentPane = this.getContentPane();
        mainPanel.add(Box.createGlue());
        mainPanel.add(okButton);
        mainPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(cancelButton);
        contentPane.add(mainPanel, BorderLayout.SOUTH);
        
        LineBorder roundedLineBorder = new LineBorder(Color.LIGHT_GRAY, 1, true);
        TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "Proxy Setting");
        roundedTitledBorder.setTitleFont( gui.myFont );
        
        // Second panel hidden by default, contain proxy setting
        final JPanel settingPanel = new JPanel();
        GroupLayout settingLayout = new GroupLayout(settingPanel);
        settingPanel.setLayout(settingLayout);
        settingPanel.setBorder(
            BorderFactory.createCompoundBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(5,5,5,5),
                            roundedTitledBorder
//                            BorderFactory.createTitledBorder(null,"Proxy Setting", TitledBorder.DEFAULT_JUSTIFICATION, 
//                                    TitledBorder.DEFAULT_POSITION, 
//                                    gui.myFont, 
//                                    (Color)UIManager.get("TextField.color"))
                    ),
                    BorderFactory.createEmptyBorder(5,5,5,5)
            ));
        
        // Proxy label
        JLabel labelProxyAddress = new JLabel("Proxy address  ");
        JLabel labelProxyPort = new JLabel("Proxy port  ");
        JLabel labelUseProxy = new JLabel("Use proxy  ");
        
        // Proxy setting: IP, port, checkbox to activate proxy
        final JPopupTextField textProxyAddress = new JPopupTextField(gui.model.proxyAddress, true);
        final JPopupTextField textProxyPort = new JPopupTextField(gui.model.proxyPort, true);
        final JCheckBox checkboxIsProxy = new JCheckBox("",gui.model.isProxyfied);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Define proxy settings
                gui.model.isProxyfied = checkboxIsProxy.isSelected();
                gui.model.proxyAddress = textProxyAddress.getText();
                gui.model.proxyPort = textProxyPort.getText();

                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                String ID1 = "isProxyfied";
                String ID2 = "proxyAddress";
                String ID3 = "proxyPort";
                prefs.putBoolean(ID1, gui.model.isProxyfied);
                prefs.put(ID2, gui.model.proxyAddress);
                prefs.put(ID3, gui.model.proxyPort);
                
                PreferencesDialog.this.dispose();
            }
        });

        // Proxy settings, Horizontal column rules
        settingLayout.setHorizontalGroup(
            settingLayout.createSequentialGroup()
                    .addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
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
        this.setLocationRelativeTo(gui);
    }
}