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
import com.jsql.view.swing.panel.preferences.PanelAuthenticationPreferences;
import com.jsql.view.swing.panel.preferences.PanelGeneralPreferences;
import com.jsql.view.swing.panel.preferences.PanelInjectionPreferences;
import com.jsql.view.swing.panel.preferences.PanelProxyPreferences;
import com.jsql.view.swing.panel.preferences.PanelTamperingPreferences;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class PanelPreferences extends JPanel {
    
    private transient ActionListener actionListenerSave = new ActionListenerSave(this);
    
    private PanelTamperingPreferences panelTamperingPreferences = new PanelTamperingPreferences(this);
    private PanelInjectionPreferences panelInjectionPreferences = new PanelInjectionPreferences(this);
    private PanelProxyPreferences panelProxyPreferences = new PanelProxyPreferences(this);
    private PanelAuthenticationPreferences panelAuthenticationPreferences = new PanelAuthenticationPreferences(this);
    private PanelGeneralPreferences panelGeneralPreferences = new PanelGeneralPreferences(this);

    private static final JPanel panelInjection = new JPanel(new BorderLayout());
    private static final JPanel panelAuthentication = new JPanel(new BorderLayout());
    private static final JPanel panelProxy = new JPanel(new BorderLayout());
    private static final JPanel panelGeneral = new JPanel(new BorderLayout());
    private static final JPanel panelTampering = new JPanel(new BorderLayout());
    
    private transient Border panelBorder = BorderFactory.createEmptyBorder(10, 15, 0, 15);
    
    private enum CategoryPreference {
        
        INJECTION(panelInjection),
        TAMPERING(panelTampering),
        PROXY(panelProxy),
        AUTHENTICATION(panelAuthentication),
        GENERAL(panelGeneral),
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
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JList<CategoryPreference> categories = this.getCategories(borderLayoutPreferences);
        
        this.add(categories, BorderLayout.LINE_START);
        
        panelTampering.setBorder(this.panelBorder);
        panelTampering.add(new JLabel("<html><b>Tampering</b> / SQL expression alteration to bypass Web Application Firewall</html>"), BorderLayout.NORTH);
        panelTampering.add(this.panelTamperingPreferences, BorderLayout.CENTER);
        
        panelGeneral.setBorder(this.panelBorder);
        panelGeneral.add(new JLabel("<html><b>General</b> / Standard options</html>"), BorderLayout.NORTH);
        panelGeneral.add(this.panelGeneralPreferences, BorderLayout.CENTER);
        
        panelInjection.setBorder(this.panelBorder);
        panelInjection.add(new JLabel("<html><b>Injection</b> / Algorithm configuration</html>"), BorderLayout.NORTH);
        panelInjection.add(this.panelInjectionPreferences, BorderLayout.CENTER);
        
        panelAuthentication.setBorder(this.panelBorder);
        panelAuthentication.add(new JLabel("<html><b>Authentication</b> / Basic, Digest, NTLM or Kerberos</html>"), BorderLayout.NORTH);
        panelAuthentication.add(this.panelAuthenticationPreferences, BorderLayout.CENTER);
        
        this.initializePanelProxy();
        
        this.add(panelInjection, BorderLayout.CENTER);
    }

    private void initializePanelProxy() {
        
        panelProxy.setLayout(new BoxLayout(panelProxy, BoxLayout.Y_AXIS));
        panelProxy.setBorder(this.panelBorder);
        
        final JButton buttonCheckIp = new JButton("Check your IP");
        buttonCheckIp.addActionListener(new ActionCheckIP());
        buttonCheckIp.setToolTipText(
            "<html><b>Verify what public IP address is used by jSQL</b><br>"
            + "Usually it's your own public IP if you don't use a proxy. If you use a proxy<br>"
            + "like TOR then your public IP is hidden and another one is used instead.</html>"
        );
        buttonCheckIp.setContentAreaFilled(true);
        buttonCheckIp.setBorder(UiUtil.BORDER_ROUND_BLU);
        
        FlatButtonMouseAdapter flatButtonMouseAdapter = new FlatButtonMouseAdapter(buttonCheckIp);
        flatButtonMouseAdapter.setContentVisible(true);
        buttonCheckIp.addMouseListener(flatButtonMouseAdapter);
        
        JLabel labelProxy = new JLabel("<html><b>Proxy</b> / Define proxy settings (e.g. TOR)</html>");
        panelProxy.removeAll();
        panelProxy.add(labelProxy, BorderLayout.NORTH);
        panelProxy.add(this.panelProxyPreferences);
        
        JPanel panelCheckIp = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
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
                
                labelItemList.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(3, 3, 0, 3, Color.WHITE),
                        labelItemList.getBorder()
                    )
                );
                
                return labelItemList;
            }
        });
        
        return categories;
    }
    
    // Getter and setter

    public PanelAuthenticationPreferences getPanelAuthenticationPreferences() {
        return this.panelAuthenticationPreferences;
    }

    public PanelProxyPreferences getPanelProxyPreferences() {
        return this.panelProxyPreferences;
    }

    public PanelInjectionPreferences getPanelInjectionPreferences() {
        return this.panelInjectionPreferences;
    }

    public PanelTamperingPreferences getPanelTamperingPreferences() {
        return this.panelTamperingPreferences;
    }

    public PanelGeneralPreferences getPanelGeneralPreferences() {
        return this.panelGeneralPreferences;
    }

    public ActionListener getActionListenerSave() {
        return this.actionListenerSave;
    }
}
