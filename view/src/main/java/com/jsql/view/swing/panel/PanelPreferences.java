package com.jsql.view.swing.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import org.apache.commons.text.WordUtils;

import com.jsql.view.swing.action.ActionCheckIP;
import com.jsql.view.swing.panel.preferences.ActionListenerSave;
import com.jsql.view.swing.panel.preferences.PanelAuth;
import com.jsql.view.swing.panel.preferences.PanelConnection;
import com.jsql.view.swing.panel.preferences.PanelGeneral;
import com.jsql.view.swing.panel.preferences.PanelInjection;
import com.jsql.view.swing.panel.preferences.PanelProxy;
import com.jsql.view.swing.panel.preferences.PanelTampering;
import com.jsql.view.swing.panel.preferences.PanelUserAgent;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class PanelPreferences extends JPanel {
    
    private transient ActionListener actionListenerSave = new ActionListenerSave(this);
    
    private PanelTampering panelTamperingPreferences = new PanelTampering(this);
    private PanelInjection panelInjectionPreferences = new PanelInjection(this);
    private PanelProxy panelProxyPreferences = new PanelProxy(this);
    private PanelAuth panelAuthPreferences = new PanelAuth(this);
    private PanelGeneral panelGeneralPreferences = new PanelGeneral(this);
    private PanelUserAgent panelUserAgentPreferences = new PanelUserAgent();
    private PanelConnection panelConnectionPreferences = new PanelConnection(this);

    private static final JPanel panelInjection = new JPanel(new BorderLayout());
    private static final JPanel panelAuth = new JPanel(new BorderLayout());
    private static final JPanel panelProxy = new JPanel(new BorderLayout());
    private static final JPanel panelGeneral = new JPanel(new BorderLayout());
    private static final JPanel panelUserAgent = new JPanel(new BorderLayout());
    private static final JPanel panelTampering = new JPanel(new BorderLayout());
    private static final JPanel panelConnection = new JPanel(new BorderLayout());
    
    private transient Border panelBorder = BorderFactory.createEmptyBorder(10, 15, 0, 15);
    
    private enum CategoryPreference {
        
        INJECTION(panelInjection),
        TAMPERING(panelTampering),
        CONNECTION(panelConnection),
        AUTH(panelAuth),
        USER_AGENT(panelUserAgent),
        PROXY(panelProxy),
        GENERAL(panelGeneral);
        
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
        
        var borderLayoutPreferences = new BorderLayout();
        this.setLayout(borderLayoutPreferences);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JList<CategoryPreference> categories = this.getCategories(borderLayoutPreferences);
        
        this.add(categories, BorderLayout.LINE_START);
        
        panelInjection.setBorder(this.panelBorder);
        panelInjection.add(new JLabel("<html><b>Injection</b> / Process configuration</html>"), BorderLayout.NORTH);
        panelInjection.add(this.panelInjectionPreferences, BorderLayout.CENTER);
        
        panelTampering.setBorder(this.panelBorder);
        panelTampering.add(new JLabel("<html><b>Tampering</b> / SQL transform to bypass Web Application Firewall</html>"), BorderLayout.NORTH);
        panelTampering.add(this.panelTamperingPreferences, BorderLayout.CENTER);
        
        panelConnection.setBorder(this.panelBorder);
        panelConnection.add(new JLabel("<html><b>Connection</b> / Network and threads</html>"), BorderLayout.NORTH);
        panelConnection.add(this.panelConnectionPreferences, BorderLayout.CENTER);
        
        panelAuth.setBorder(this.panelBorder);
        panelAuth.add(new JLabel("<html><b>Authentication</b> / Basic, Digest, NTLM or Kerberos connection</html>"), BorderLayout.NORTH);
        panelAuth.add(this.panelAuthPreferences, BorderLayout.CENTER);
        
        panelUserAgent.setBorder(this.panelBorder);
        panelUserAgent.add(new JLabel("<html><b>User Agent</b> / Network connection agents</html>"), BorderLayout.NORTH);
        panelUserAgent.add(this.panelUserAgentPreferences, BorderLayout.CENTER);
        
        this.initializePanelProxy();
        
        panelGeneral.setBorder(this.panelBorder);
        panelGeneral.add(new JLabel("<html><b>General</b> / Basic options</html>"), BorderLayout.NORTH);
        panelGeneral.add(this.panelGeneralPreferences, BorderLayout.CENTER);
        
        this.add(panelInjection, BorderLayout.CENTER);
    }

    private void initializePanelProxy() {
        
        panelProxy.setLayout(new BoxLayout(panelProxy, BoxLayout.Y_AXIS));
        panelProxy.setBorder(this.panelBorder);
        
        final var buttonCheckIp = new JButton("Check your IP");
        buttonCheckIp.addActionListener(new ActionCheckIP());
        buttonCheckIp.setToolTipText(
            "<html><b>Verify what public IP address is used by jSQL</b><br>"
            + "Usually it's your own public IP if you don't use a proxy. If you use a proxy<br>"
            + "like TOR then your public IP is hidden and another one is used instead.</html>"
        );
        buttonCheckIp.setContentAreaFilled(true);
        buttonCheckIp.setBorder(UiUtil.BORDER_ROUND_BLU);
        
        var flatButtonMouseAdapter = new FlatButtonMouseAdapter(buttonCheckIp);
        flatButtonMouseAdapter.setContentVisible(true);
        buttonCheckIp.addMouseListener(flatButtonMouseAdapter);
        
        var labelProxy = new JLabel("<html><b>Proxy</b> / Settings for tools like Burp and Tor</html>");
        panelProxy.removeAll();
        panelProxy.add(labelProxy, BorderLayout.NORTH);
        panelProxy.add(this.panelProxyPreferences);
        
        var panelCheckIp = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        panelCheckIp.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panelCheckIp.add(buttonCheckIp);
        panelCheckIp.add(Box.createGlue());
        panelProxy.add(panelCheckIp);
        
        labelProxy.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.panelProxyPreferences.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCheckIp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JList<CategoryPreference> getCategories(BorderLayout borderLayoutPreferences) {
        
        JList<CategoryPreference> categories = new JList<>(CategoryPreference.values());
        categories.setName("listCategoriesPreference");
        categories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categories.setSelectedIndex(0);
        
        categories.setBorder(BorderFactory.createLineBorder(UiUtil.COLOR_COMPONENT_BORDER));
        categories.addListSelectionListener(e -> {
            
            PanelPreferences.this.remove(borderLayoutPreferences.getLayoutComponent(BorderLayout.CENTER));
            PanelPreferences.this.add(categories.getSelectedValue().getPanel(), BorderLayout.CENTER);
            // Both required
            PanelPreferences.this.revalidate();
            PanelPreferences.this.repaint();
        });
        
        categories.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                
                JLabel labelItemList = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    
                    labelItemList.setBackground(UiUtil.COLOR_FOCUS_GAINED);
                    
                    // Hardcode color black for Mac uses white by default
                    labelItemList.setForeground(Color.BLACK);
                }
                
                labelItemList.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(3, 3, 0, 3, Color.WHITE),
                        isSelected
                        ? BorderFactory.createLineBorder(UiUtil.COLOR_COMPONENT_BORDER)
                        : labelItemList.getBorder()
                    )
                );
                
                return labelItemList;
            }
        });
        
        return categories;
    }
    
    
    // Getter and setter

    public PanelAuth getPanelAuth() {
        return this.panelAuthPreferences;
    }

    public PanelProxy getPanelProxy() {
        return this.panelProxyPreferences;
    }

    public PanelInjection getPanelInjection() {
        return this.panelInjectionPreferences;
    }

    public PanelTampering getPanelTampering() {
        return this.panelTamperingPreferences;
    }

    public PanelGeneral getPanelGeneral() {
        return this.panelGeneralPreferences;
    }
    
    public PanelUserAgent getPanelUserAgent() {
        return this.panelUserAgentPreferences;
    }
    
    public PanelConnection getPanelConnection() {
        return this.panelConnectionPreferences;
    }

    public ActionListener getActionListenerSave() {
        return this.actionListenerSave;
    }
}
