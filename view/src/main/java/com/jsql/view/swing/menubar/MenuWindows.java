package com.jsql.view.swing.menubar;

import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.jsql.model.InjectionModel;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.sql.SqlEngine;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.RadioItemPreventClose;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

public class MenuWindows extends JMenu {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private static final String I18N_SQL_ENGINE = "MENUBAR_SQL_ENGINE";
    private static final String I18N_PREFERENCES = "MENUBAR_PREFERENCES";
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
        var menuAppearance = new JMenu(I18nUtil.valueByKey("MENUBAR_APPEARANCE"));
        I18nViewUtil.addComponentForKey("MENUBAR_APPEARANCE", menuAppearance);
        menuAppearance.setMnemonic('A');

        JMenuItem itemNewWindows4k = new JMenuItem(
            new ActionNewWindow("New 4K Window", "-Dsun.java2d.uiScale=2.5")
        );
        menuAppearance.add(itemNewWindows4k);

        var groupRadio = new ButtonGroup();
        var menuThemes = new JMenu(I18nUtil.valueByKey("MENUBAR_THEMES"));
        I18nViewUtil.addComponentForKey("MENUBAR_THEMES", menuAppearance);
        menuThemes.setMnemonic('T');

        Arrays.asList(
            new AbstractMap.SimpleEntry<>(FlatLightFlatIJTheme.class.getName(), "IntelliJ"),
            new AbstractMap.SimpleEntry<>(FlatDarkFlatIJTheme.class.getName(), "IntelliJ Dark"),
            new AbstractMap.SimpleEntry<>(FlatMacLightLaf.class.getName(), "macOS"),
            new AbstractMap.SimpleEntry<>(FlatMacDarkLaf.class.getName(), "macOS Dark"),
            new AbstractMap.SimpleEntry<>(FlatMTGitHubIJTheme.class.getName(), "GitHub"),
            new AbstractMap.SimpleEntry<>(FlatMTGitHubDarkIJTheme.class.getName(), "GitHub Dark"),
            new AbstractMap.SimpleEntry<>(FlatHighContrastIJTheme.class.getName(), "High contrast")
        ).forEach(entry -> {
            JMenuItem item = new RadioItemPreventClose(
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
        this.add(this.initMenuTranslation());
        this.add(new JSeparator());

        this.menuView = new JMenu(I18nUtil.valueByKey("MENUBAR_VIEW"));
        I18nViewUtil.addComponentForKey("MENUBAR_VIEW", this.menuView);
        this.menuView.setMnemonic('V');

        AtomicInteger accelerator = new AtomicInteger(0x31);
        AtomicInteger tabPosition = new AtomicInteger();
        MediatorHelper.frame().getTabManagers().getIconsTabs().forEach(entry -> {
            var menuItem = new JMenuItem(I18nUtil.valueByKey(entry.getKeyLabel()), entry.getIcon());
            I18nViewUtil.addComponentForKey(entry.getKeyLabel(), menuItem);
            menuItem.setName(entry.getKeyLabel());  // required by card manager switch
            this.menuView.add(menuItem);

            menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.getExtendedKeyCodeForChar(accelerator.getAndIncrement()),
                InputEvent.CTRL_DOWN_MASK
            ));

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
                PreferencesUtil.CHUNK_VISIBLE,
                () -> MediatorHelper.panelConsoles().insertChunkTab(),
                UiUtil.CHUNK.getIcon()
            ),
            new ModelCheckboxMenu(
                "CONSOLE_BINARY_LABEL",
                PreferencesUtil.BINARY_VISIBLE,
                () -> MediatorHelper.panelConsoles().insertBooleanTab(),
                UiUtil.BINARY.getIcon()
            ),
            new ModelCheckboxMenu(
                "CONSOLE_NETWORK_LABEL",
                PreferencesUtil.NETWORK_VISIBLE,
                () -> MediatorHelper.panelConsoles().insertNetworkTab(),
                UiUtil.NETWORK.getIcon()
            ),
            new ModelCheckboxMenu(
                "CONSOLE_JAVA_LABEL",
                PreferencesUtil.JAVA_VISIBLE,
                () -> MediatorHelper.panelConsoles().insertJavaTab(),
                UiUtil.CUP.getIcon(),
                false
            )
        ).forEach(model -> {
            var menuItem = new JCheckBoxMenuItem(
                I18nUtil.valueByKey(model.i18n),
                model.icon,
                model.isChecked
            ) {
                @Override
                protected void processMouseEvent(MouseEvent e) {
                    if (!RadioItemPreventClose.preventClose(e, this)) {
                        super.processMouseEvent(e);
                    }
                }
            };
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
        var itemSqlEngine = new JMenuItem(I18nUtil.valueByKey(MenuWindows.I18N_SQL_ENGINE));
        I18nViewUtil.addComponentForKey(MenuWindows.I18N_SQL_ENGINE, itemSqlEngine);
        itemSqlEngine.setName("itemSqlEngine");
        itemSqlEngine.setMnemonic('S');

        // Render the SQL Engine dialog behind scene
        var titleTabSqlEngine = "SQL Engine";

        itemSqlEngine.addActionListener(actionEvent -> {
            for (var i = 0 ; i < MediatorHelper.tabResults().getTabCount() ; i++) {
                if (titleTabSqlEngine.equals(MediatorHelper.tabResults().getTitleAt(i))) {
                    MediatorHelper.tabResults().setSelectedIndex(i);
                    return;
                }
            }

            MediatorHelper.frame().getSplitNS().initSplitOrientation();

            var panelSqlEngine = new SqlEngine();
            MediatorHelper.tabResults().addTab(titleTabSqlEngine, panelSqlEngine);
            MediatorHelper.tabResults().setSelectedComponent(panelSqlEngine);  // Focus on the new tab

            // Create a custom tab header
            var header = new TabHeader(I18nViewUtil.valueByKey(MenuWindows.I18N_SQL_ENGINE), UiUtil.COG.getIcon());
            I18nViewUtil.addComponentForKey(MenuWindows.I18N_SQL_ENGINE, header.getTabLabel());

            // Apply the custom header to the tab
            MediatorHelper.tabResults().setTabComponentAt(MediatorHelper.tabResults().indexOfComponent(panelSqlEngine), header);
            MediatorHelper.tabResults().updateUI();  // required: light, open/close prefs, dark => light artifacts
        });

        return itemSqlEngine;
    }

    private JMenuItem getMenuItemPreferences() {
        JMenuItem itemPreferences = new JMenuItem(I18nUtil.valueByKey(MenuWindows.I18N_PREFERENCES), 'P');
        I18nViewUtil.addComponentForKey(MenuWindows.I18N_PREFERENCES, itemPreferences);
        itemPreferences.setName("itemPreferences");

        // Render the Preferences dialog behind scene
        var titleTabPreferences = "Preferences";

        itemPreferences.addActionListener(actionEvent -> {
            for (var i = 0 ; i < MediatorHelper.tabResults().getTabCount() ; i++) {
                if (titleTabPreferences.equals(MediatorHelper.tabResults().getTitleAt(i))) {
                    MediatorHelper.tabResults().setSelectedIndex(i);
                    return;
                }
            }

            MediatorHelper.frame().getSplitNS().initSplitOrientation();

            var panelPreferences = new PanelPreferences();
            MediatorHelper.tabResults().addTab(titleTabPreferences, panelPreferences);
            MediatorHelper.tabResults().setSelectedComponent(panelPreferences);  // Focus on the new tab

            // Create a custom tab header
            var header = new TabHeader(I18nViewUtil.valueByKey(MenuWindows.I18N_PREFERENCES), UiUtil.COG.getIcon());
            I18nViewUtil.addComponentForKey(MenuWindows.I18N_PREFERENCES, header.getTabLabel());

            // Apply the custom header to the tab
            MediatorHelper.tabResults().setTabComponentAt(MediatorHelper.tabResults().indexOfComponent(panelPreferences), header);

            MediatorHelper.tabResults().updateUI();  // required: light, open/close prefs, dark => light artifacts
        });

        return itemPreferences;
    }

    private JMenu initMenuTranslation() {
        var menuTranslation = new JMenu(I18nUtil.valueByKey("MENUBAR_LANGUAGE"));
        I18nViewUtil.addComponentForKey("MENUBAR_LANGUAGE", menuTranslation);
        menuTranslation.setName("menuTranslation");
        menuTranslation.setMnemonic('L');

        var groupRadioLanguage = new ButtonGroup();
        var atomicIsAnySelected = new AtomicBoolean(false);
        AppMenubar.MODELS_ITEM.forEach(model -> {
            atomicIsAnySelected.set(atomicIsAnySelected.get() || model.getLanguage().isCurrentLanguage());
            model.setMenuItem(new RadioItemPreventClose(
                model.getLanguage().getMenuItemLabel(),
                model.getLanguage().getFlag(),
                model.getLanguage().isCurrentLanguage()
            ));
            model.getMenuItem().addActionListener(actionEvent -> {
                this.appMenubar.switchLocale(
                    model.getLanguage() == Language.EN
                    ? Locale.ROOT  // required as no bundle 'en'
                    : Locale.forLanguageTag(model.getLanguage().getLanguageTag())
                );
                MediatorHelper.model().getMediatorUtils().getPreferencesUtil().withLanguageTag(model.getLanguage().getLanguageTag()).persist();
            });
            menuTranslation.add(model.getMenuItem());
            groupRadioLanguage.add(model.getMenuItem());
        });

        AppMenubar.MODELS_ITEM.stream().filter(model -> model.getLanguage() == Language.EN)
        .forEach(modelItem -> {
            modelItem.getMenuItem().setSelected(!atomicIsAnySelected.get());
            modelItem.getMenuItem().setName("itemEnglish");
        });
        AppMenubar.MODELS_ITEM.stream().filter(model -> model.getLanguage() == Language.RU)
        .forEach(modelItem -> modelItem.getMenuItem().setName("itemRussian"));
        AppMenubar.MODELS_ITEM.stream().filter(model -> model.getLanguage() == Language.AR)
        .forEach(modelItem -> modelItem.getMenuItem().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT));

        return menuTranslation;
    }

    public void switchLocaleFromPreferences() {
        AppMenubar.MODELS_ITEM.stream()
        .filter(model -> model.getLanguage().getLanguageTag().equals(
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getLanguageTag()
        ))
        .forEach(modelItem -> modelItem.getMenuItem().doClick());
    }


    // Getter and setter

    public JMenu getMenuView() {
        return this.menuView;
    }
}
