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
import com.jsql.util.*;
import com.jsql.view.interaction.SubscriberInteraction;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.split.SplitNS;
import com.jsql.view.swing.terminal.AbstractExploit;
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
import java.util.HashMap;
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

    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Map of terminal by unique identifier.
     */
    private final Map<UUID, AbstractExploit> mapUuidShell = new HashMap<>();
    private final transient SubscriberInteraction subscriber = new SubscriberInteraction("com.jsql.view.swing.interaction");
    private TabManagers tabManagers;
    private boolean isMaximized = false;
    private final InjectionModel injectionModel;
    private SplitNS splitNS;  // main

    public JFrameView(InjectionModel injectionModel) {  // Build the GUI: add app icon, tree icons, the 3 main panels
        super(StringUtil.APP_NAME);
        this.injectionModel = injectionModel;
        MediatorHelper.register(this);
        UiUtil.prepareGUI();  // Load UI before any component
        this.initPaneComponents();
        this.initWindow();
        this.initShortcuts();
        this.displayVersion();
        I18nUtil.checkCurrentLanguage();
        this.check4K();

        SwingUtilities.invokeLater(() -> {  // paint native blu svg in theme color behind the scene
            AppMenubar.applyTheme(injectionModel.getMediatorUtils().getPreferencesUtil().getThemeFlatLafName());  // refresh missing components
            if (injectionModel.getMediatorUtils().getProxyUtil().isNotLive(GitUtil.ShowOnConsole.YES)) {  // network access
                return;
            }
            if (injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingUpdate()) {
                injectionModel.getMediatorUtils().getGitUtil().checkUpdate(GitUtil.ShowOnConsole.NO);
            }
            if (injectionModel.getMediatorUtils().getPreferencesUtil().isShowNews()) {  // disabled when UT only
                injectionModel.getMediatorUtils().getGitUtil().showNews();
            }
            this.setVisible(true);
            MediatorHelper.panelAddressBar().getTextFieldAddress().requestFocusInWindow();  // required here to get focus
        });
    }

    private void initPaneComponents() {
        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        this.tabManagers = new TabManagers();  // Tab manager linked to cards
        this.add(this.tabManagers);

        var menubar = new AppMenubar();
        this.setJMenuBar(menubar);
        MediatorHelper.register(menubar);

        var panelAddressBar = new PanelAddressBar();  // Textfield at the top
        MediatorHelper.register(panelAddressBar);
        this.add(panelAddressBar);

        var mainPanel = new JPanel(new BorderLayout());  // Main panel for tree and tables in the middle
        this.splitNS = new SplitNS();
        mainPanel.add(this.splitNS);
        this.add(mainPanel);

        menubar.getMenuWindows().switchLocaleFromPreferences();
    }

    private void initWindow() {
        this.setIconImages(UiUtil.getIcons());  // define small and large app icons
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        this.addWindowStateListener(e -> this.isMaximized = (e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                double nsProportion = preferences.getDouble(PreferencesUtil.NS_SPLIT, 0.75);
                double ewProportion = preferences.getDouble(PreferencesUtil.EW_SPLIT, 0.33);
                if (preferences.getBoolean(PreferencesUtil.IS_MAXIMIZED, false)) {
                    JFrameView.this.setExtendedState(Frame.MAXIMIZED_BOTH);
                }
                // Proportion means different location relative to whether frame is maximized or not
                // Maximizing must wait AWT events to reflect the new divider location proportion
                SwingUtilities.invokeLater(() -> {
                    JFrameView.this.splitNS.setDividerLocation(
                        Math.max(0.0, Math.min(1.0, nsProportion))
                    );
                    JFrameView.this.splitNS.getSplitEW().setDividerLocation(
                        Math.max(0.0, Math.min(1.0, ewProportion))
                    );
                    MediatorHelper.panelConsoles().getNetworkSplitPane().setDividerLocation(
                        ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()).isLeftToRight()
                        ? 0.33
                        : 0.66
                    );
                });
            }

            @Override
            public void windowClosing(WindowEvent e) {
                preferences.putBoolean(PreferencesUtil.IS_MAXIMIZED, JFrameView.this.isMaximized);

                int ewDividerLocation = JFrameView.this.splitNS.getSplitEW().getDividerLocation();
                int ewHeight = JFrameView.this.splitNS.getSplitEW().getWidth();
                double ewProportion = 100.0 * ewDividerLocation / ewHeight;
                double ewLocationProportionCapped = Math.max(0.0, Math.min(1.0,
                    ewProportion / 100.0
                ));

                int nsDividerLocation = JFrameView.this.splitNS.getDividerLocation();
                int nsHeight = JFrameView.this.splitNS.getHeight();
                double nsProportion = 100.0 * nsDividerLocation / nsHeight;
                double nsLocationProportionCapped = Math.max(0.0, Math.min(1.0,
                    nsProportion / 100.0
                ));

                // Divider location changes when window is maximized, instead stores location percentage between 0.0 and 1.0
                preferences.putDouble(PreferencesUtil.NS_SPLIT, nsLocationProportionCapped);
                preferences.putDouble(PreferencesUtil.EW_SPLIT, ewLocationProportionCapped);

                preferences.putBoolean(PreferencesUtil.BINARY_VISIBLE, false);
                preferences.putBoolean(PreferencesUtil.CHUNK_VISIBLE, false);
                preferences.putBoolean(PreferencesUtil.NETWORK_VISIBLE, false);
                preferences.putBoolean(PreferencesUtil.JAVA_VISIBLE, false);
                for (var i = 0 ; i < MediatorHelper.tabConsoles().getTabCount() ; i++) {
                    if ("CONSOLE_BINARY_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        preferences.putBoolean(PreferencesUtil.BINARY_VISIBLE, true);
                    } else if ("CONSOLE_CHUNK_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        preferences.putBoolean(PreferencesUtil.CHUNK_VISIBLE, true);
                    } else if ("CONSOLE_NETWORK_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        preferences.putBoolean(PreferencesUtil.NETWORK_VISIBLE, true);
                    } else if ("CONSOLE_JAVA_LABEL".equals(MediatorHelper.tabConsoles().getTabComponentAt(i).getName())) {
                        preferences.putBoolean(PreferencesUtil.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        this.setSize(1024, 768);
        this.setLocationRelativeTo(null);  // center the window
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initShortcuts() {
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
        
        Stream.of(MediatorHelper.managerFile(), MediatorHelper.managerExploit()).forEach(managerList -> {
            managerList.setButtonEnable(false);
            managerList.changePrivilegeIcon(UiUtil.SQUARE.getIcon());
        });
    }

    private void displayVersion() {
        LOGGER.log(
            LogLevelUtil.CONSOLE_DEFAULT,
            "{} v{} on Java {}-{}-{}",
            () -> StringUtil.APP_NAME,
            () -> this.injectionModel.getPropertiesUtil().getVersionJsql(),
            () -> SystemUtils.JAVA_VERSION,
            () -> SystemUtils.OS_ARCH,
            () -> SystemUtils.USER_LANGUAGE
        );
    }

    private void check4K() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        if (width >= 3840 && !this.injectionModel.getMediatorUtils().getPreferencesUtil().is4K()) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Screen compatible with resolution 4K, enable high-definition in Preferences");
        }
    }


    // Getters and setters

    public final Map<UUID, AbstractExploit> getMapUuidShell() {
        return this.mapUuidShell;
    }

    public SubscriberInteraction getSubscriber() {
        return this.subscriber;
    }

    public SplitNS getSplitNS() {
        return this.splitNS;
    }

    public TabManagers getTabManagers() {
        return this.tabManagers;
    }
}
