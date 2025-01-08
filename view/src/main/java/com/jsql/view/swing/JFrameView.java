/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing;

import com.jsql.model.InjectionModel;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.interaction.SubscriberInteraction;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.split.SplitHorizontalTopBottom;
import com.jsql.view.swing.shell.AbstractShell;
import com.jsql.view.swing.tab.TabManagers;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
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

/**
 * View in the MVC pattern, defines all the components
 * and process actions sent by the model.<br>
 * Main groups of components:<br>
 * - at the top: textfield inputs,<br>
 * - at the center: tree on the left, table on the right,<br>
 * - at the bottom: information labels.
 */
public class JFrameView extends JFrame {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    // Main center panel
    private SplitHorizontalTopBottom splitHorizontalTopBottom;

    /**
     * Get list of terminal by unique identifier.
     * Map of key/value UUID => Terminal
     */
    private final Map<UUID, AbstractShell> mapUuidShell = new HashMap<>();
    
    private final transient SubscriberInteraction subscriber = new SubscriberInteraction("com.jsql.view.swing.interaction");

    private JTabbedPane tabManagers;
    
    public JFrameView() {  // Build the GUI: add app icon, tree icons, the 3 main panels
        super("jSQL Injection");
        MediatorHelper.register(this);
        UiUtil.prepareGUI();  // Load UI before any component
        this.initializePaneComponents();
        this.initializeWindow();
        this.initializeShortcuts();
        this.displayVersion();
    }

    private void initializePaneComponents() {
        var menubar = new AppMenubar();
        this.setJMenuBar(menubar);
        MediatorHelper.register(menubar);

        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        tabManagers = new TabManagers();  // Tab manager linked to cards
        this.add(tabManagers);

        var panelAddressBar = new PanelAddressBar();  // Textfield at the top
        MediatorHelper.register(panelAddressBar);
        this.add(panelAddressBar);

        var mainPanel = new JPanel(new BorderLayout());  // Main panel for tree and tables in the middle
        this.splitHorizontalTopBottom = new SplitHorizontalTopBottom();
        mainPanel.add(this.splitHorizontalTopBottom);
        this.add(mainPanel);

        menubar.getMenuWindows().switchLocaleFromPreferences();
    }

    private void initializeWindow() {
        
        this.setIconImages(UiUtil.getIcons());  // define small and large app icons

        this.addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowOpened(WindowEvent event) {
                super.windowOpened(event);
                
                var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
                var horizontalTopBottomSplitter = preferences.getDouble(SplitHorizontalTopBottom.NAME_TOP_BOTTOM_SPLITPANE, 0.75);

                if (!(0.0 <= horizontalTopBottomSplitter && horizontalTopBottomSplitter <= 1.0)) {
                    horizontalTopBottomSplitter = 0.75;
                }
                JFrameView.this.splitHorizontalTopBottom.setDividerLocation(horizontalTopBottomSplitter);
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                
                var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
                preferences.putInt(
                    SplitHorizontalTopBottom.NAME_LEFT_RIGHT_SPLITPANE,
                    // TODO not compatible arabic location
                    JFrameView.this.splitHorizontalTopBottom.getSplitVerticalLeftRight().getDividerLocation()
                );
                
                var percentTopBottom = BigDecimal.valueOf(
                    JFrameView.this.splitHorizontalTopBottom.getDividerLocation() * 100.0
                    / JFrameView.this.splitHorizontalTopBottom.getHeight()
                    / 100
                );
                percentTopBottom = percentTopBottom.setScale(2, RoundingMode.HALF_UP);
                
                // Divider location change when window is maximized, we can't save getDividerLocation()
                preferences.putDouble(
                    SplitHorizontalTopBottom.NAME_TOP_BOTTOM_SPLITPANE,
                    percentTopBottom.doubleValue() - 0.01  // Fix scale
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
        
        this.setSize(1024, 768);
        this.setVisible(true);
        this.setLocationRelativeTo(null);  // center the window
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        MediatorHelper.panelAddressBar().getTextFieldAddress().requestFocusInWindow();
    }

    private void initializeShortcuts() {
        HotkeyUtil.addShortcut(this.getRootPane(), MediatorHelper.tabResults());
        HotkeyUtil.addTextFieldShortcutSelectAll();
    }

    public void resetInterface() {  // Empty the interface
        this.mapUuidShell.clear();
        MediatorHelper.panelAddressBar().getPanelTrailingAddress().reset();
        MediatorHelper.panelConsoles().reset();
        MediatorHelper.treeDatabase().reset();
        
        for (var i = 0 ; i < MediatorHelper.tabConsoles().getTabCount() ; i++) {
            var tabComponent = MediatorHelper.tabConsoles().getTabComponentAt(i);
            if (tabComponent != null) {
                tabComponent.setFont(tabComponent.getFont().deriveFont(Font.PLAIN));
            }
        }
        
        Stream.of(
            MediatorHelper.managerUpload(),
            MediatorHelper.managerFile(),
            MediatorHelper.managerWebshell(),
            MediatorHelper.managerSqlshell()
        )
        .forEach(managerList -> {
            managerList.setButtonEnable(false);
            managerList.changePrivilegeIcon(UiUtil.SQUARE.icon);
        });
    }

    private void displayVersion() {
        LOGGER.log(
            LogLevelUtil.CONSOLE_DEFAULT,
            "jSQL Injection v{} on Java {}-{}-{}",
            () -> MediatorHelper.model().getPropertiesUtil().getVersionJsql(),
            () -> SystemUtils.JAVA_VERSION,
            () -> SystemUtils.OS_ARCH,
            () -> SystemUtils.USER_LANGUAGE
        );
    }


    // Getters and setters

    public final Map<UUID, AbstractShell> getMapUuidShell() {
        return this.mapUuidShell;
    }

    public SubscriberInteraction getSubscriber() {
        return this.subscriber;
    }

    public SplitHorizontalTopBottom getSplitHorizontalTopBottom() {
        return this.splitHorizontalTopBottom;
    }

    public JTabbedPane getTabManagers() {
        return tabManagers;
    }
}
