/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.jsql.model.InjectionModel;
import com.jsql.util.I18nUtil;
import com.jsql.view.interaction.SubscriberInteraction;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.split.SplitHorizontalTopBottom;
import com.jsql.view.swing.shadow.ShadowPopupFactory;
import com.jsql.view.swing.shell.AbstractShell;
import com.jsql.view.swing.tab.TabManagers;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * View in the MVC pattern, defines all the components
 * and process actions sent by the model.<br>
 * Main groups of components:<br>
 * - at the top: textfield inputs,<br>
 * - at the center: tree on the left, table on the right,<br>
 * - at the bottom: information labels.
 */
@SuppressWarnings("serial")
public class JFrameView extends JFrame {

    // Main center panel
    private SplitHorizontalTopBottom splitHorizontalTopBottom;

    // List of terminal by unique identifier
    private Map<UUID, AbstractShell> mapShells = new HashMap<>();
    
    private transient SubscriberInteraction subscriber = new SubscriberInteraction("com.jsql.view.swing.interaction");
    
    // Build the GUI: add app icon, tree icons, the 3 main panels
    public JFrameView() {
        
        super("jSQL Injection");
        
        MediatorHelper.register(this);

        // Load UI before any component
        UiUtil.prepareGUI();
        ShadowPopupFactory.install();
        
        this.initializePaneComponents();
        this.initializeWindow();
        this.initializeShortcuts();
    }

    private void initializeWindow() {
        
        // Define a small and large app icon
        this.setIconImages(UiUtil.getIcons());

        this.addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowOpened(WindowEvent event) {
                
                super.windowOpened(event);
                
                var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
                var horizontalTopBottomSplitter = preferences.getDouble(SplitHorizontalTopBottom.getNameHSplitpane(), 0.75);
                
                if (!(0.0 <= horizontalTopBottomSplitter && horizontalTopBottomSplitter <= 1.0)) {
                    
                    horizontalTopBottomSplitter = 0.75;
                }

                JFrameView.this.splitHorizontalTopBottom.setDividerLocation(horizontalTopBottomSplitter);
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                
                var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
                preferences.putInt(
                    SplitHorizontalTopBottom.getNameVSplitpane(),
                    JFrameView.this.splitHorizontalTopBottom.getSplitVerticalLeftRight().getDividerLocation()
                );
                
                var roundDecimal = BigDecimal.valueOf(
                    JFrameView.this.splitHorizontalTopBottom.getDividerLocation() * 100.0
                    / JFrameView.this.splitHorizontalTopBottom.getHeight()
                    / 100
                );
                roundDecimal = roundDecimal.setScale(2, RoundingMode.HALF_UP);
                
                // Divider location change when window is maximized, we can't save getDividerLocation()
                preferences.putDouble(
                    SplitHorizontalTopBottom.getNameHSplitpane(),
                    // Fix scale
                    roundDecimal.doubleValue() - 0.01
                );
                
                preferences.putBoolean(UiUtil.BINARY_VISIBLE, false);
                preferences.putBoolean(UiUtil.CHUNK_VISIBLE, false);
                preferences.putBoolean(UiUtil.NETWORK_VISIBLE, false);
                preferences.putBoolean(UiUtil.JAVA_VISIBLE, false);
                
                for (var i = 0 ; i < MediatorHelper.tabConsoles().getTabCount() ; i++) {
                    
                    if ("CONSOLE_BINARY_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        
                        preferences.putBoolean(UiUtil.BINARY_VISIBLE, true);
                        
                    } else if ("CONSOLE_CHUNK_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        
                        preferences.putBoolean(UiUtil.CHUNK_VISIBLE, true);
                        
                    } else if ("CONSOLE_NETWORK_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        
                        preferences.putBoolean(UiUtil.NETWORK_VISIBLE, true);
                        
                    } else if ("CONSOLE_JAVA_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        
                        preferences.putBoolean(UiUtil.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        // Size of window
        this.setSize(1024, 768);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Center the window
        this.setLocationRelativeTo(null);
        
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        MediatorHelper.panelAddressBar().getTextFieldAddress().requestFocusInWindow();
    }

    private void initializeShortcuts() {
        
        // Define the keyword shortcuts for tabs #Need to work even if the focus is not on tabs
        HotkeyUtil.addShortcut(this.getRootPane(), MediatorHelper.tabResults());
        HotkeyUtil.addTextFieldShortcutSelectAll();
    }

    private void initializePaneComponents() {
        
        // Save controller
        var menubar = new Menubar();
        this.setJMenuBar(menubar);
        MediatorHelper.register(menubar);
        
        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        // Main panel for tree and tables in the middle
        // Set proxy tabs dependency
        var mainPanel = new JPanel(new GridLayout(1, 0));
        this.splitHorizontalTopBottom = new SplitHorizontalTopBottom();
        mainPanel.add(this.splitHorizontalTopBottom);
        
        // Textfields at the top
        var panelAddressBar = new PanelAddressBar();
        
        // Tab manager use proxy tabs dependency
        JTabbedPane tabManagers = new TabManagers();

        this.add(tabManagers);
        this.add(panelAddressBar);
        MediatorHelper.register(panelAddressBar);

        this.add(mainPanel);
        
        menubar.switchLocale(Locale.ENGLISH, I18nUtil.getLocaleDefault(), true);
    }

    // Empty the interface
    public void resetInterface() {
        
        MediatorHelper.panelAddressBar().getAddressMenuBar().reset();
        
        MediatorHelper.treeDatabase().getTreeNodeModels().clear();
        this.mapShells.clear();
        
        MediatorHelper.panelConsoles().reset();
        MediatorHelper.treeDatabase().reset();
        
        for (var i = 0 ; i < MediatorHelper.tabConsoles().getTabCount() ; i++) {
            
            var tabComponent = MediatorHelper.tabConsoles().getTabComponentAt(i);
            if (tabComponent != null) {
                
                tabComponent.setFont(tabComponent.getFont().deriveFont(Font.PLAIN));
            }
        }
        
        Stream
        .of(
            MediatorHelper.managerUpload(),
            MediatorHelper.managerFile(),
            MediatorHelper.managerWebshell(),
            MediatorHelper.managerSqlshell()
        )
        .forEach(managerList -> {
            
            managerList.setButtonEnable(false);
            managerList.changePrivilegeIcon(UiUtil.ICON_SQUARE_GREY);
        });
    }
    
    
    // Getters and setters

    /**
     * Get list of terminal by unique identifier.
     * @return Map of key/value UUID => Terminal
     */
    public final Map<UUID, AbstractShell> getConsoles() {
        return this.mapShells;
    }

    public SubscriberInteraction getSubscriber() {
        return this.subscriber;
    }

    public SplitHorizontalTopBottom getSplitHorizontalTopBottom() {
        return this.splitHorizontalTopBottom;
    }
}
