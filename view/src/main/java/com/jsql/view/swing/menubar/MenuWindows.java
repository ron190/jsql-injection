package com.jsql.view.swing.menubar;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.jsql.model.InjectionModel;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.interaction.CreateTabHelper;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.sql.SqlEngine;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

public class MenuWindows extends JMenu {

    private static final String KEY_MENU_SQL_ENGINE = "MENUBAR_SQL_ENGINE";
    private static final String KEY_MENU_PREFERENCES = "MENUBAR_PREFERENCES";
    private final AppMenubar appMenubar;

    private final JMenu menuView;

    public MenuWindows(AppMenubar appMenubar) {

        super(I18nUtil.valueByKey("MENUBAR_WINDOWS"));
        this.appMenubar = appMenubar;

        this.setName("menuWindows");
        I18nViewUtil.addComponentForKey("MENUBAR_WINDOWS", this);
        this.setMnemonic('W');

        JMenuItem itemNewWindows = new JMenuItem(new ActionNewWindow());
        I18nViewUtil.addComponentForKey("NEW_WINDOW_MENU", itemNewWindows);

        this.add(itemNewWindows);
        var menuAppearance = new JMenu("Appearance");
        menuAppearance.setMnemonic('A');

        JMenuItem itemNewWindows4k = new JMenuItem(
            new ActionNewWindow("New 4K Window", "-Dsun.java2d.uiScale=2.5")
        );
        menuAppearance.add(itemNewWindows4k);

        var groupRadio = new ButtonGroup();
        var menuThemes = new JMenu("Themes");
        menuThemes.setMnemonic('T');

        Arrays.asList(
            new AbstractMap.SimpleEntry<>(FlatLightFlatIJTheme.class.getName(), "IntelliJ"),
            new AbstractMap.SimpleEntry<>(FlatDarkFlatIJTheme.class.getName(), "IntelliJ Dark"),
            new AbstractMap.SimpleEntry<>(FlatMacLightLaf.class.getName(), "macOS"),
            new AbstractMap.SimpleEntry<>(FlatMacDarkLaf.class.getName(), "macOS Dark"),
            new AbstractMap.SimpleEntry<>(FlatGitHubIJTheme.class.getName(), "GitHub"),
            new AbstractMap.SimpleEntry<>(FlatGitHubDarkIJTheme.class.getName(), "GitHub Dark"),
            new AbstractMap.SimpleEntry<>(FlatHighContrastIJTheme.class.getName(), "High contrast")
        ).forEach(entry -> {
            JMenuItem item = new JRadioButtonMenuItem(
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        AppMenubar.applyTheme(entry.getKey());
                    }
                }
            );
            item.setText(entry.getValue());
            item.setSelected(entry.getKey().equals(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getThemeFlatLafName()));
            groupRadio.add(item);
            menuThemes.add(item);
        });

        this.add(itemNewWindows);
        this.add(menuAppearance);
        this.add(menuThemes);
        this.add(new JSeparator());
        this.add(this.initializeMenuTranslation());
        this.add(new JSeparator());

        this.menuView = new JMenu(I18nUtil.valueByKey("MENUBAR_VIEW"));
        I18nViewUtil.addComponentForKey("MENUBAR_VIEW", this.menuView);
        this.menuView.setMnemonic('V');

        AtomicInteger accelerator = new AtomicInteger(0x31);
        AtomicInteger tabPosition = new AtomicInteger();
        Arrays.asList(
            new AbstractMap.SimpleEntry<>("DATABASE_TAB", UiUtil.DATABASE_BOLD),
            new AbstractMap.SimpleEntry<>("ADMINPAGE_TAB", UiUtil.ADMIN),
            new AbstractMap.SimpleEntry<>("FILE_TAB", UiUtil.DOWNLOAD),
            new AbstractMap.SimpleEntry<>("WEBSHELL_TAB", UiUtil.TERMINAL),
            new AbstractMap.SimpleEntry<>("SQLSHELL_TAB", UiUtil.TERMINAL),
            new AbstractMap.SimpleEntry<>("UPLOAD_TAB", UiUtil.UPLOAD),
            new AbstractMap.SimpleEntry<>("BRUTEFORCE_TAB", UiUtil.LOCK),
            new AbstractMap.SimpleEntry<>("CODER_TAB", UiUtil.TEXTFIELD),
            new AbstractMap.SimpleEntry<>("SCANLIST_TAB", UiUtil.BATCH)
        ).forEach(entry -> {
            var menuItem = new JMenuItem(I18nUtil.valueByKey(entry.getKey()), entry.getValue().icon);
            I18nViewUtil.addComponentForKey(entry.getKey(), menuItem);
            menuItem.setName(entry.getKey());  // required by card manager switch
            this.menuView.add(menuItem);

            menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.getExtendedKeyCodeForChar(accelerator.get()),
                InputEvent.CTRL_DOWN_MASK
            ));
            accelerator.getAndIncrement();

            final var position = tabPosition.get();  // required by closure
            menuItem.addActionListener(actionEvent -> {  // setAction() could set action+text+icon but i18n not easy
                CardLayout cardLayout = (CardLayout) MediatorHelper.tabManagersCards().getLayout();
                cardLayout.show(MediatorHelper.tabManagersCards(), menuItem.getName());
                MediatorHelper.frame().getTabManagers().setSelectedIndex(position);
            });
            tabPosition.getAndIncrement();
        });

        this.add(this.menuView);

        Preferences preferences = Preferences.userRoot().node(InjectionModel.class.getName());

        var menuPanel = new JMenu(I18nUtil.valueByKey("MENUBAR_PANEL"));
        I18nViewUtil.addComponentForKey("MENUBAR_PANEL", menuPanel);
        menuPanel.setMnemonic('C');

        Arrays.asList(
            new ModelCheckboxMenu(
                "CONSOLE_CHUNK_LABEL",
                UiUtil.CHUNK_VISIBLE, 
                () -> MediatorHelper.panelConsoles().insertChunkTab(),
                UiUtil.CHUNK.icon
            ),
            new ModelCheckboxMenu(
                "CONSOLE_BINARY_LABEL",
                UiUtil.BINARY_VISIBLE,
                () -> MediatorHelper.panelConsoles().insertBooleanTab(),
                UiUtil.BINARY.icon
            ),
            new ModelCheckboxMenu(
                "CONSOLE_NETWORK_LABEL",
                UiUtil.NETWORK_VISIBLE,
                () -> MediatorHelper.panelConsoles().insertNetworkTab(),
                UiUtil.NETWORK.icon
            ),
            new ModelCheckboxMenu(
                "CONSOLE_JAVA_LABEL",
                UiUtil.JAVA_VISIBLE,
                () -> MediatorHelper.panelConsoles().insertJavaTab(),
                UiUtil.CUP.icon
            )
        ).forEach(model -> {
            var menuItem = new JCheckBoxMenuItem(
                I18nUtil.valueByKey(model.i18n),
                model.icon,
                preferences.getBoolean(model.keyPref, true)
            );
            I18nViewUtil.addComponentForKey(model.i18n, menuItem);
            menuPanel.add(menuItem);

            menuItem.addActionListener(actionEvent -> {
                if (menuItem.isSelected()) {
                    model.runnableInsertTab.run();
                } else {
                    MediatorHelper.tabConsoles().remove(MediatorHelper.tabConsoles().indexOfTab(model.icon));
                }
            });
        });

        this.add(menuPanel);
        this.add(new JSeparator());
        this.add(this.getMenuItemSqlEngine());
        this.add(this.getMenuItemPreferences());
    }

    private JMenuItem getMenuItemSqlEngine() {
        var itemSqlEngine = new JMenuItem(I18nUtil.valueByKey(KEY_MENU_SQL_ENGINE));
        I18nViewUtil.addComponentForKey(KEY_MENU_SQL_ENGINE, itemSqlEngine);
        itemSqlEngine.setName("itemSqlEngine");
        itemSqlEngine.setMnemonic('S');

        // Render the SQL Engine dialog behind scene
        var titleTabSqlEngine = "SQL Engine";

        itemSqlEngine.addActionListener(actionEvent -> {
            for (var i = 0; i < MediatorHelper.tabResults().getTabCount() ; i++) {
                if (titleTabSqlEngine.equals(MediatorHelper.tabResults().getTitleAt(i))) {
                    MediatorHelper.tabResults().setSelectedIndex(i);
                    return;
                }
            }

            CreateTabHelper.initializeSplitOrientation();

            var panelSqlEngine = new SqlEngine();
            MediatorHelper.tabResults().addTab(titleTabSqlEngine, panelSqlEngine);
            MediatorHelper.tabResults().setSelectedComponent(panelSqlEngine);  // Focus on the new tab

            // Create a custom tab header
            var header = new TabHeader(I18nViewUtil.valueByKey(KEY_MENU_SQL_ENGINE), UiUtil.COG.icon);
            I18nViewUtil.addComponentForKey(KEY_MENU_SQL_ENGINE, header.getTabLabel());

            // Apply the custom header to the tab
            MediatorHelper.tabResults().setTabComponentAt(MediatorHelper.tabResults().indexOfComponent(panelSqlEngine), header);

            FlatLaf.updateUI();  // required: light, open/close prefs, dark => light artifacts
            MediatorHelper.frame().revalidate();
            MediatorHelper.frame().repaint();
        });

        return itemSqlEngine;
    }

    private JMenuItem getMenuItemPreferences() {
        JMenuItem itemPreferences = new JMenuItem(I18nUtil.valueByKey(KEY_MENU_PREFERENCES), 'P');
        I18nViewUtil.addComponentForKey(KEY_MENU_PREFERENCES, itemPreferences);
        itemPreferences.setName("itemPreferences");

        // Render the Preferences dialog behind scene
        var titleTabPreferences = "Preferences";

        itemPreferences.addActionListener(actionEvent -> {
            for (var i = 0; i < MediatorHelper.tabResults().getTabCount() ; i++) {
                if (titleTabPreferences.equals(MediatorHelper.tabResults().getTitleAt(i))) {
                    MediatorHelper.tabResults().setSelectedIndex(i);
                    return;
                }
            }

            CreateTabHelper.initializeSplitOrientation();

            var panelPreferences = new PanelPreferences();
            MediatorHelper.tabResults().addTab(titleTabPreferences, panelPreferences);
            MediatorHelper.tabResults().setSelectedComponent(panelPreferences);  // Focus on the new tab

            // Create a custom tab header
            var header = new TabHeader(I18nViewUtil.valueByKey(KEY_MENU_PREFERENCES), UiUtil.COG.icon);
            I18nViewUtil.addComponentForKey(KEY_MENU_PREFERENCES, header.getTabLabel());

            // Apply the custom header to the tab
            MediatorHelper.tabResults().setTabComponentAt(MediatorHelper.tabResults().indexOfComponent(panelPreferences), header);

            FlatLaf.updateUI();  // required: light, open/close prefs, dark => light artifacts
            MediatorHelper.frame().revalidate();
            MediatorHelper.frame().repaint();
        });

        return itemPreferences;
    }

    private JMenu initializeMenuTranslation() {
        var menuTranslation = new JMenu(I18nUtil.valueByKey("MENUBAR_LANGUAGE"));
        I18nViewUtil.addComponentForKey("MENUBAR_LANGUAGE", menuTranslation);
        menuTranslation.setName("menuTranslation");
        menuTranslation.setMnemonic('L');

        var groupRadioLanguage = new ButtonGroup();
        var atomicIsAnySelected = new AtomicBoolean(false);
        this.appMenubar.modelsItem.forEach(model -> {
            atomicIsAnySelected.set(atomicIsAnySelected.get() || model.language.isCurrentLanguage());
            model.menuItem = new JRadioButtonMenuItem(
                model.language.getMenuItemLabel(),
                model.language.getFlag(),
                model.language.isCurrentLanguage()
            );
            model.menuItem.addActionListener(actionEvent -> {
                this.appMenubar.switchLocale(
                    model.language == Language.EN
                    ? Locale.ROOT  // required as no bundle 'en'
                    : Locale.forLanguageTag(model.language.getLanguageTag())
                );
                MediatorHelper.model().getMediatorUtils().getPreferencesUtil().withLanguageTag(model.language.getLanguageTag()).persist();
            });
            menuTranslation.add(model.menuItem);
            groupRadioLanguage.add(model.menuItem);
        });

        this.appMenubar.modelsItem.stream().filter(modelItem -> modelItem.language == Language.EN)
        .forEach(modelItem -> {
            modelItem.menuItem.setSelected(!atomicIsAnySelected.get());
            modelItem.menuItem.setName("itemEnglish");
        });
        this.appMenubar.modelsItem.stream().filter(modelItem -> modelItem.language == Language.RU)
        .forEach(modelItem -> modelItem.menuItem.setName("itemRussian"));
        this.appMenubar.modelsItem.stream().filter(modelItem -> modelItem.language == Language.AR)
        .forEach(modelItem -> modelItem.menuItem.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT));

        return menuTranslation;
    }

    public void switchLocaleFromPreferences() {
        this.appMenubar.modelsItem.stream()
        .filter(modelItem -> modelItem.language.getLanguageTag().equals(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getLanguageTag()))
        .forEach(modelItem -> modelItem.menuItem.doClick());
    }


    // Getter and setter

    public JMenu getMenuView() {
        return menuView;
    }
}
