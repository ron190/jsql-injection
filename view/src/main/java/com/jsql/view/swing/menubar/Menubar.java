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
package com.jsql.view.swing.menubar;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.action.ActionSaveTab;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.console.JTextPaneAppender;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.interaction.CreateTabHelper;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.sql.SqlEngine;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.tree.model.NodeModelEmpty;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Application main menubar.
 */
@SuppressWarnings("serial")
public class Menubar extends JMenuBar {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    // Checkbox item to show/hide chunk console.
    private JCheckBoxMenuItem chunkMenu;

    // Checkbox item to show/hide java console.
    private JCheckBoxMenuItem javaDebugMenu;
    
    private JMenu menuView;
    
    private JMenuItem itemArabic;
    private JMenuItem itemEnglish;
    private JMenuItem itemChinese;
    private JMenuItem itemRussian;
    private JMenuItem itemFrench;
    private JMenuItem itemCzech;
    private JMenuItem itemTurkish;
    private JMenuItem itemGerman;
    private JMenuItem itemDutch;
    private JMenuItem itemIndonesian;
    private JMenuItem itemItalian;
    private JMenuItem itemSpanish;
    private JMenuItem itemPortuguese;
    private JMenuItem itemPolish;
    private JMenuItem itemRomanian;
    private JMenuItem itemKorean;
    private JMenuItem itemSwedish;
    private JMenuItem itemFinnish;
    
    private JMenuItem itemIntoHindi;
    private JMenuItem itemIntoArabic;
    private JMenuItem itemIntoRussia;
    private JMenuItem itemIntoChina;
    private JMenuItem itemIntoFrench;
    private JMenuItem itemIntoTurkish;
    private JMenuItem itemIntoCzech;
    private JMenuItem itemIntoGerman;
    private JMenuItem itemIntoDutch;
    private JMenuItem itemIntoIndonesian;
    private JMenuItem itemIntoItalian;
    private JMenuItem itemIntoSpanish;
    private JMenuItem itemIntoSwedish;
    private JMenuItem itemIntoPortuguese;
    private JMenuItem itemIntoPolish;
    private JMenuItem itemIntoKorean;
    private JMenuItem itemIntoJapanese;
    private JMenuItem itemIntoRomanian;
    private JMenuItem itemIntoTamil;
    private JMenuItem itemIntoFinnish;
    
    private static final String KEY_MENU_SQL_ENGINE = "MENUBAR_SQL_ENGINE";
    private static final String KEY_MENU_PREFERENCES = "MENUBAR_PREFERENCES";

    /**
     * Create a menubar on main frame.
     */
    public Menubar() {

        JMenu menuFile = this.initializeMenuFile();
        JMenu menuEdit = this.initializeMenuEdit();
        JMenu menuCommunity = this.initializeMenuCommunity();
        JMenu menuWindows = this.initializeMenuWindows();
        JMenu menuHelp = this.initializeMenuHelp();

        this.add(menuFile);
        this.add(menuEdit);
        this.add(menuCommunity);
        this.add(menuWindows);
        this.add(menuHelp);
    }

    private JMenu initializeMenuWindows() {
        
        // Window Menu > Preferences
        var menuWindows = new JMenu(I18nUtil.valueByKey("MENUBAR_WINDOWS"));
        menuWindows.setName("menuWindows");
        I18nViewUtil.addComponentForKey("MENUBAR_WINDOWS", menuWindows);
        menuWindows.setMnemonic('W');

        JMenuItem itemNewWindows = new JMenuItemWithMargin(new ActionNewWindow());
        I18nViewUtil.addComponentForKey("NEW_WINDOW_MENU", itemNewWindows);
        
        menuWindows.add(itemNewWindows);
        var menuAppearance = new JMenu("Appearance");
        JMenuItem itemNewWindows4k = new JMenuItemWithMargin(
            new ActionNewWindow("New 4K Window", "-Dsun.java2d.uiScale=2.5")
        );
        menuAppearance.add(itemNewWindows4k);
        menuWindows.add(itemNewWindows);
        menuWindows.add(menuAppearance);
        menuWindows.add(new JSeparator());

        JMenu menuTranslation = this.initializeMenuTranslation();
        
        menuWindows.add(menuTranslation);
        menuWindows.add(new JSeparator());
        
        this.menuView = new JMenu(I18nUtil.valueByKey("MENUBAR_VIEW"));
        I18nViewUtil.addComponentForKey("MENUBAR_VIEW", this.menuView);
        this.menuView.setMnemonic('V');
        
        var database = new JMenuItem(I18nUtil.valueByKey("DATABASE_TAB"), UiUtil.ICON_DATABASE_SERVER);
        I18nViewUtil.addComponentForKey("DATABASE_TAB", database);
        this.menuView.add(database);
        
        var adminPage = new JMenuItem(I18nUtil.valueByKey("ADMINPAGE_TAB"), UiUtil.ICON_ADMIN_SERVER);
        I18nViewUtil.addComponentForKey("ADMINPAGE_TAB", adminPage);
        this.menuView.add(adminPage);
        
        var file = new JMenuItem(I18nUtil.valueByKey("FILE_TAB"), UiUtil.ICON_FILE_SERVER);
        I18nViewUtil.addComponentForKey("FILE_TAB", file);
        this.menuView.add(file);
        
        var webshell = new JMenuItem(I18nUtil.valueByKey("WEBSHELL_TAB"), UiUtil.ICON_SHELL_SERVER);
        I18nViewUtil.addComponentForKey("WEBSHELL_TAB", webshell);
        this.menuView.add(webshell);
        
        var sqlshell = new JMenuItem(I18nUtil.valueByKey("SQLSHELL_TAB"), UiUtil.ICON_SHELL_SERVER);
        I18nViewUtil.addComponentForKey("SQLSHELL_TAB", sqlshell);
        this.menuView.add(sqlshell);
        
        var upload = new JMenuItem(I18nUtil.valueByKey("UPLOAD_TAB"), UiUtil.ICON_UPLOAD);
        I18nViewUtil.addComponentForKey("UPLOAD_TAB", upload);
        this.menuView.add(upload);
        
        var bruteforce = new JMenuItem(I18nUtil.valueByKey("BRUTEFORCE_TAB"), UiUtil.ICON_BRUTER);
        I18nViewUtil.addComponentForKey("BRUTEFORCE_TAB", bruteforce);
        this.menuView.add(bruteforce);
        
        var coder = new JMenuItem(I18nUtil.valueByKey("CODER_TAB"), UiUtil.ICON_CODER);
        I18nViewUtil.addComponentForKey("CODER_TAB", coder);
        this.menuView.add(coder);
        
        var scanList = new JMenuItem(I18nUtil.valueByKey("SCANLIST_TAB"), UiUtil.ICON_SCANLIST);
        I18nViewUtil.addComponentForKey("SCANLIST_TAB", scanList);
        this.menuView.add(scanList);
        menuWindows.add(this.menuView);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        var menuPanel = new JMenu(I18nUtil.valueByKey("MENUBAR_PANEL"));
        I18nViewUtil.addComponentForKey("MENUBAR_PANEL", menuPanel);
        this.menuView.setMnemonic('V');
        
        this.chunkMenu = new JCheckBoxMenuItem(
            I18nUtil.valueByKey("CONSOLE_CHUNK_LABEL"),
            UiUtil.ICON_CHUNK,
            prefs.getBoolean(UiUtil.CHUNK_VISIBLE, true)
        );
        I18nViewUtil.addComponentForKey("CONSOLE_CHUNK_LABEL", this.chunkMenu);
        menuPanel.add(this.chunkMenu);
        
        var binaryMenu = new JCheckBoxMenuItem(
            I18nUtil.valueByKey("CONSOLE_BINARY_LABEL"),
            UiUtil.ICON_BINARY,
            prefs.getBoolean(UiUtil.BINARY_VISIBLE, true)
        );
        I18nViewUtil.addComponentForKey("CONSOLE_BINARY_LABEL", binaryMenu);
        menuPanel.add(binaryMenu);
        
        var networkMenu = new JCheckBoxMenuItem(
            I18nUtil.valueByKey("CONSOLE_NETWORK_LABEL"),
            UiUtil.ICON_HEADER,
            prefs.getBoolean(UiUtil.NETWORK_VISIBLE, true)
        );
        I18nViewUtil.addComponentForKey("CONSOLE_NETWORK_LABEL", networkMenu);
        menuPanel.add(networkMenu);
        
        this.javaDebugMenu = new JCheckBoxMenuItem(
            I18nUtil.valueByKey("CONSOLE_JAVA_LABEL"),
            UiUtil.ICON_CUP,
            prefs.getBoolean(UiUtil.JAVA_VISIBLE, false)
        );
        I18nViewUtil.addComponentForKey("CONSOLE_JAVA_LABEL", this.javaDebugMenu);

        for (var menuItem: new JCheckBoxMenuItem[]{ this.chunkMenu, binaryMenu, networkMenu, this.javaDebugMenu }) {
            
            menuItem.setUI(
                new BasicCheckBoxMenuItemUI() {
                    
                    @Override
                    protected void doClick(MenuSelectionManager msm) {
                        
                        this.menuItem.doClick(0);
                    }
                }
            );
        }

        this.chunkMenu.addActionListener(actionEvent -> {
            
            if (this.chunkMenu.isSelected()) {
                
                MediatorHelper.panelConsoles().insertChunkTab();
                
            } else {
                
                MediatorHelper.tabConsoles().remove(MediatorHelper.tabConsoles().indexOfTab(UiUtil.ICON_CHUNK));
            }
        });
        
        binaryMenu.addActionListener(actionEvent -> {
            
            if (binaryMenu.isSelected()) {
                
                MediatorHelper.panelConsoles().insertBooleanTab();
                
            } else {
                
                MediatorHelper.tabConsoles().remove(MediatorHelper.tabConsoles().indexOfTab(UiUtil.ICON_BINARY));
            }
        });
        
        networkMenu.addActionListener(actionEvent -> {
            
            if (networkMenu.isSelected()) {
                
                MediatorHelper.panelConsoles().insertNetworkTab();
                
            } else {
                
                MediatorHelper.tabConsoles().remove(MediatorHelper.tabConsoles().indexOfTab(UiUtil.ICON_HEADER));
            }
        });
        
        this.javaDebugMenu.addActionListener(actionEvent -> {
            
            if (this.javaDebugMenu.isSelected()) {
                
                MediatorHelper.panelConsoles().insertJavaTab();
                
            } else {
                
                MediatorHelper.tabConsoles().remove(MediatorHelper.tabConsoles().indexOfTab(UiUtil.ICON_CUP));
            }
        });

        menuPanel.add(this.javaDebugMenu);
        
        menuWindows.add(menuPanel);
        menuWindows.add(new JSeparator());

        database.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        adminPage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        file.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
        webshell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
        sqlshell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));
        upload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.CTRL_MASK));
        bruteforce.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, ActionEvent.CTRL_MASK));
        coder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, ActionEvent.CTRL_MASK));
        scanList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9, ActionEvent.CTRL_MASK));

        for (var position = 0 ; position < this.menuView.getItemCount() ; position++) {
            
            final JMenuItem itemMenu = this.menuView.getItem(position);
            final int positionFinal = position;
            itemMenu.addActionListener(actionEvent -> MediatorHelper.tabManagers().setSelectedIndex(positionFinal));
        }

        JMenuItem itemPreferences = this.initializeItemPreferences();
        itemPreferences.setName("itemPreferences");
        JMenuItem itemSqlEngine = this.initializeItemSqlEngine();
        itemSqlEngine.setName("itemSqlEngine");
        
        menuWindows.add(itemSqlEngine);
        menuWindows.add(itemPreferences);
        
        return menuWindows;
    }

    private JMenu initializeMenuHelp() {
        
        // Help Menu > about
        var menuHelp = new JMenu(I18nUtil.valueByKey("MENUBAR_HELP"));
        menuHelp.setMnemonic('H');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP", menuHelp);
        menuHelp.setName("menuHelp");
        
        JMenuItem itemHelp = new JMenuItemWithMargin(I18nUtil.valueByKey("MENUBAR_HELP_ABOUT"), 'A');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP_ABOUT", itemHelp);
        itemHelp.setName("itemHelp");
        
        JMenuItem itemUpdate = new JMenuItemWithMargin(I18nUtil.valueByKey("MENUBAR_HELP_UPDATE"), 'U');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP_UPDATE", itemUpdate);

        // Render the About dialog behind scene
        final var aboutDiag = new DialogAbout();
        itemHelp.addActionListener(actionEvent -> {
            
            // Center the dialog
            if (!aboutDiag.isVisible()) {
                
                aboutDiag.initializeDialog();
                // needed here for button focus
                aboutDiag.setVisible(true);
                aboutDiag.requestButtonFocus();
            }
            
            aboutDiag.setVisible(true);
        });
        itemUpdate.addActionListener(new ActionCheckUpdate());
        
        menuHelp.add(itemUpdate);
        menuHelp.add(new JSeparator());
        menuHelp.add(itemHelp);
        
        return menuHelp;
    }

    private JMenuItem initializeItemSqlEngine() {
        
        var itemSqlEngine = new JMenuItem(I18nUtil.valueByKey(KEY_MENU_SQL_ENGINE));
        I18nViewUtil.addComponentForKey(KEY_MENU_SQL_ENGINE, itemSqlEngine);
        
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

            // Focus on the new tab
            MediatorHelper.tabResults().setSelectedComponent(panelSqlEngine);

            // Create a custom tab header with close button
            var header = new TabHeader(I18nViewUtil.valueByKey(KEY_MENU_SQL_ENGINE), UiUtil.ICON_COG, panelSqlEngine);
            I18nViewUtil.addComponentForKey(KEY_MENU_SQL_ENGINE, header.getTabTitleLabel());

            // Apply the custom header to the tab
            MediatorHelper.tabResults().setTabComponentAt(MediatorHelper.tabResults().indexOfComponent(panelSqlEngine), header);
        });
        
        return itemSqlEngine;
    }

    private JMenuItem initializeItemPreferences() {
        
        JMenuItem itemPreferences = new JMenuItemWithMargin(I18nUtil.valueByKey(KEY_MENU_PREFERENCES), 'P');
        I18nViewUtil.addComponentForKey(KEY_MENU_PREFERENCES, itemPreferences);
        
        // Render the Preferences dialog behind scene
        var titleTabPreferences = "Preferences";
        
        // Single rendering
        var panelPreferences = new PanelPreferences();
        
        itemPreferences.addActionListener(actionEvent -> {
            
            for (var i = 0; i < MediatorHelper.tabResults().getTabCount() ; i++) {
                
                if (titleTabPreferences.equals(MediatorHelper.tabResults().getTitleAt(i))) {
                    
                    MediatorHelper.tabResults().setSelectedIndex(i);
                    
                    return;
                }
            }
            
            CreateTabHelper.initializeSplitOrientation();
            
            AdjustmentListener singleItemScroll = adjustmentEvent -> {
                
                // The user scrolled the List (using the bar, mouse wheel or something else):
                if (adjustmentEvent.getAdjustmentType() == AdjustmentEvent.TRACK) {
                    
                    // Jump to the next "block" (which is a row".
                    adjustmentEvent.getAdjustable().setBlockIncrement(100);
                    adjustmentEvent.getAdjustable().setUnitIncrement(100);
                }
            };

            var scroller = new LightScrollPane(1, 0, 0, 0, panelPreferences);
            scroller.scrollPane.getVerticalScrollBar().addAdjustmentListener(singleItemScroll);
            
            MediatorHelper.tabResults().addTab(titleTabPreferences, scroller);

            // Focus on the new tab
            MediatorHelper.tabResults().setSelectedComponent(scroller);

            // Create a custom tab header with close button
            var header = new TabHeader(I18nViewUtil.valueByKey(KEY_MENU_PREFERENCES), UiUtil.ICON_COG, panelPreferences.getPanelTampering());
            I18nViewUtil.addComponentForKey(KEY_MENU_PREFERENCES, header.getTabTitleLabel());

            // Apply the custom header to the tab
            MediatorHelper.tabResults().setTabComponentAt(MediatorHelper.tabResults().indexOfComponent(scroller), header);
        });
        
        return itemPreferences;
    }

    private JMenu initializeMenuCommunity() {
        
        // Help Menu > about
        var menuCommunity = new JMenu(I18nUtil.valueByKey("MENUBAR_COMMUNITY"));
        menuCommunity.setMnemonic('C');
        menuCommunity.setName("menuCommunity");
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY", menuCommunity);
        
        JMenu menuI18nContribution = this.initializeMenuI18nContribution();
        menuI18nContribution.setName("menuI18nContribution");
        
        JMenuItem itemReportIssue = this.initializeItemReportIssue();
        itemReportIssue.setName("itemReportIssue");
        
        menuCommunity.add(menuI18nContribution);
        menuCommunity.add(new JSeparator());
        menuCommunity.add(itemReportIssue);
        
        return menuCommunity;
    }

    private JMenu initializeMenuTranslation() {
        
        var patternAsianDisplay = "<html><span style=\"font-family:'%s'\">%s</span></html>";
        
        var menuTranslation = new JMenu(I18nUtil.valueByKey("MENUBAR_LANGUAGE"));
        I18nViewUtil.addComponentForKey("MENUBAR_LANGUAGE", menuTranslation);
        menuTranslation.setName("menuTranslation");
        
        Object[] languages =
            Stream
            .of("ru zh es fr tr ko se ar cs it pt pl in nl ro de".split(StringUtils.SPACE))
            .map(flag -> new Locale(flag).getLanguage())
            .collect(Collectors.toList())
            .toArray();
        
        boolean isEnglish = !ArrayUtils.contains(languages, Locale.getDefault().getLanguage());
    
        this.itemEnglish = new JRadioButtonMenuItem(
            new Locale("en").getDisplayLanguage(new Locale("en")),
            UiUtil.ICON_FLAG_EN,
            isEnglish
        );
        this.itemEnglish.addActionListener(actionEvent -> Menubar.this.switchLocale(Locale.ROOT));
        this.itemEnglish.setName("itemEnglish");
        
        // Unhandled ClassFormatError #73790 on constructor: Unknown constant tag 73 in class file java/awt/font/TextLine
        this.itemArabic = new JRadioButtonMenuItem(
            String.format(
                patternAsianDisplay,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("ar").getDisplayLanguage(new Locale("ar"))
            ),
            UiUtil.ICON_FLAG_AR,
            new Locale("ar").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemArabic.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ar")));
        this.itemArabic.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        this.itemRussian = new JRadioButtonMenuItem(
            new Locale("ru").getDisplayLanguage(new Locale("ru")),
            UiUtil.ICON_FLAG_RU,
            new Locale("ru").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemRussian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ru")));
        this.itemRussian.setName("itemRussian");
        
        this.itemCzech = new JRadioButtonMenuItem(
            new Locale("cs").getDisplayLanguage(new Locale("cs")),
            UiUtil.ICON_FLAG_CS,
            new Locale("cs").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemCzech.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("cs")));
        
        this.itemItalian = new JRadioButtonMenuItem(
            new Locale("it").getDisplayLanguage(new Locale("it")),
            UiUtil.ICON_FLAG_IT,
            new Locale("it").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemItalian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("it")));
        
        this.itemIndonesian = new JRadioButtonMenuItem(
            new Locale("in", "ID").getDisplayLanguage(new Locale("in", "ID")),
            UiUtil.ICON_FLAG_IN_ID,
            new Locale("in", "ID").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemIndonesian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("in", "ID")));
        
        this.itemDutch = new JRadioButtonMenuItem(
            new Locale("nl").getDisplayLanguage(new Locale("nl")),
            UiUtil.ICON_FLAG_NL,
            new Locale("nl").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemDutch.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("nl")));
        
        this.itemGerman = new JRadioButtonMenuItem(
            new Locale("de").getDisplayLanguage(new Locale("de")),
            UiUtil.ICON_FLAG_DE,
            new Locale("de").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemGerman.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("de")));
        
        this.itemTurkish = new JRadioButtonMenuItem(
            new Locale("tr").getDisplayLanguage(new Locale("tr")),
            UiUtil.ICON_FLAG_TR,
            new Locale("tr").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemTurkish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("tr")));
        
        this.itemFrench = new JRadioButtonMenuItem(
            new Locale("fr").getDisplayLanguage(new Locale("fr")),
            UiUtil.ICON_FLAG_FR,
            new Locale("fr").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemFrench.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("fr")));
        
        this.itemSpanish = new JRadioButtonMenuItem(
            new Locale("es").getDisplayLanguage(new Locale("es")),
            UiUtil.ICON_FLAG_ES,
            new Locale("es").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemSpanish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("es")));
        
        this.itemPortuguese = new JRadioButtonMenuItem(
            new Locale("pt").getDisplayLanguage(new Locale("pt")),
            UiUtil.ICON_FLAG_PT,
            new Locale("pt").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemPortuguese.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("pt")));
        
        this.itemChinese = new JRadioButtonMenuItem(
            String
            .format(
                patternAsianDisplay,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("zh").getDisplayLanguage(new Locale("zh"))
            ),
            UiUtil.ICON_FLAG_ZH,
            new Locale("zh").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemChinese.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("zh")));
        
        this.itemPolish = new JRadioButtonMenuItem(
            new Locale("pl").getDisplayLanguage(new Locale("pl")),
            UiUtil.ICON_FLAG_PL,
            new Locale("pl").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemPolish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("pl")));
        
        this.itemRomanian = new JRadioButtonMenuItem(
            new Locale("ro").getDisplayLanguage(new Locale("ro")),
            UiUtil.ICON_FLAG_RO,
            new Locale("ro").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemRomanian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ro")));
        
        this.itemSwedish = new JRadioButtonMenuItem(
            new Locale("se").getDisplayLanguage(new Locale("se")),
            UiUtil.ICON_FLAG_SE,
            new Locale("se").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemSwedish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("se")));
        
        this.itemFinnish = new JRadioButtonMenuItem(
            new Locale("fi").getDisplayLanguage(new Locale("fi")),
            UiUtil.ICON_FLAG_FI,
            new Locale("fi").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemFinnish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("fi")));
        
        this.itemKorean = new JRadioButtonMenuItem(
            String
            .format(
                patternAsianDisplay,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("ko").getDisplayLanguage(new Locale("ko"))
            ),
            UiUtil.ICON_FLAG_KO,
            new Locale("ko").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemKorean.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ko")));
        
        var groupRadioLanguage = new ButtonGroup();
        
        Stream
        .of(
            this.itemEnglish,
            this.itemRussian,
            this.itemChinese,
            this.itemSpanish,
            this.itemFrench,
            this.itemTurkish,
            this.itemKorean,
            this.itemSwedish,
            this.itemFinnish,
            this.itemArabic,
            this.itemCzech,
            this.itemItalian,
            this.itemPortuguese,
            this.itemPolish,
            this.itemIndonesian,
            this.itemDutch,
            this.itemRomanian,
            this.itemGerman
        )
        .forEach(menuItem -> {
            
            menuTranslation.add(menuItem);
            groupRadioLanguage.add(menuItem);
        });
        
        return menuTranslation;
    }

    private JMenu initializeMenuI18nContribution() {
        
        var menuI18nContribution = new JMenu(I18nUtil.valueByKey("MENUBAR_COMMUNITY_HELPTRANSLATE"));
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_HELPTRANSLATE", menuI18nContribution);
        
        // Render the About dialog behind scene
        final var dialogTranslate = new DialogTranslate();
        
        class ActionTranslate implements ActionListener {
            
            private Language language;
            
            ActionTranslate(Language language) {
                
                this.language = language;
            }
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                dialogTranslate.initializeDialog(this.language);
                
                // Center the dialog
                if (!dialogTranslate.isVisible()) {
                    
                    dialogTranslate.setSize(640, 460);
                    dialogTranslate.setLocationRelativeTo(MediatorHelper.frame());
                    dialogTranslate.getRootPane().setDefaultButton(dialogTranslate.getButtonSend());
                }
                
                dialogTranslate.setVisible(true);
            }
        }
        
        var formatMenuItemUTF8 = "<html><span style=\"font-family:'%s'\">%s</span>...</html>";
        
        this.itemIntoHindi = new JMenuItem(
            String
            .format(
                formatMenuItemUTF8,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("hi").getDisplayLanguage(new Locale("hi"))
            ),
            UiUtil.ICON_FLAG_HI
        );
        this.itemIntoArabic = new JMenuItem(
            String
            .format(
                formatMenuItemUTF8,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("ar").getDisplayLanguage(new Locale("ar"))
            ), UiUtil.ICON_FLAG_AR
        );
        this.itemIntoRussia = new JMenuItem(new Locale("ru").getDisplayLanguage(new Locale("ru")) +"...", UiUtil.ICON_FLAG_RU);
        this.itemIntoChina = new JMenuItem(
            String
            .format(
                formatMenuItemUTF8,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("zh").getDisplayLanguage(new Locale("zh"))
            ),
            UiUtil.ICON_FLAG_ZH
        );
        this.itemIntoFrench = new JMenuItem(new Locale("fr").getDisplayLanguage(new Locale("fr")) +"...", UiUtil.ICON_FLAG_FR);
        this.itemIntoTurkish = new JMenuItem(new Locale("tr").getDisplayLanguage(new Locale("tr")) +"...", UiUtil.ICON_FLAG_TR);
        this.itemIntoCzech = new JMenuItem(new Locale("cs").getDisplayLanguage(new Locale("cs")) +"...", UiUtil.ICON_FLAG_CS);
        this.itemIntoDutch = new JMenuItem(new Locale("nl").getDisplayLanguage(new Locale("nl")) +"...", UiUtil.ICON_FLAG_NL);
        this.itemIntoGerman = new JMenuItem(new Locale("de").getDisplayLanguage(new Locale("de")) +"...", UiUtil.ICON_FLAG_DE);
        this.itemIntoIndonesian = new JMenuItem(new Locale("in", "ID").getDisplayLanguage(new Locale("in", "ID")) +"...", UiUtil.ICON_FLAG_IN_ID);
        this.itemIntoItalian = new JMenuItem(new Locale("it").getDisplayLanguage(new Locale("it")) +"...", UiUtil.ICON_FLAG_IT);
        this.itemIntoSpanish = new JMenuItem(new Locale("es").getDisplayLanguage(new Locale("es")) +"...", UiUtil.ICON_FLAG_ES);
        this.itemIntoPortuguese = new JMenuItem(new Locale("pt").getDisplayLanguage(new Locale("pt")) +"...", UiUtil.ICON_FLAG_PT);
        this.itemIntoPolish = new JMenuItem(new Locale("pl").getDisplayLanguage(new Locale("pl")) +"...", UiUtil.ICON_FLAG_PL);
        this.itemIntoRomanian = new JMenuItem(new Locale("ro").getDisplayLanguage(new Locale("ro")) +"...", UiUtil.ICON_FLAG_RO);
        this.itemIntoTamil = new JMenuItem(new Locale("ta").getDisplayLanguage(new Locale("ta")) +"...", UiUtil.ICON_FLAG_LK);
        this.itemIntoJapanese = new JMenuItem(
            String
            .format(
                formatMenuItemUTF8,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("ja").getDisplayLanguage(new Locale("ja"))
            ),
            UiUtil.ICON_FLAG_JA
        );
        this.itemIntoKorean = new JMenuItem(
            String
            .format(
                formatMenuItemUTF8,
                UiUtil.FONT_NAME_MONO_ASIAN,
                new Locale("ko").getDisplayLanguage(new Locale("ko"))
            ),
            UiUtil.ICON_FLAG_KO
        );
        this.itemIntoSwedish = new JMenuItem(new Locale("se").getDisplayLanguage(new Locale("se")) +"...", UiUtil.ICON_FLAG_SE);
        this.itemIntoFinnish = new JMenuItem(new Locale("fi").getDisplayLanguage(new Locale("fi")) +"...", UiUtil.ICON_FLAG_FI);
        var itemIntoOther = new JMenuItem(I18nUtil.valueByKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE"));
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE", itemIntoOther);
        
        this.itemIntoArabic.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        this.itemIntoFrench.setName("itemIntoFrench");
        
        Stream
        .of(
            this.itemIntoFrench,
            this.itemIntoSpanish,
            this.itemIntoSwedish,
            this.itemIntoFinnish,
            this.itemIntoTurkish,
            this.itemIntoCzech,
            this.itemIntoRomanian,
            this.itemIntoItalian,
            this.itemIntoPortuguese,
            this.itemIntoArabic,
            this.itemIntoPolish,
            this.itemIntoRussia,
            this.itemIntoChina,
            this.itemIntoGerman,
            this.itemIntoIndonesian,
            this.itemIntoJapanese,
            this.itemIntoKorean,
            this.itemIntoHindi,
            this.itemIntoDutch,
            this.itemIntoTamil,
            new JSeparator(),
            itemIntoOther
        )
        .forEach(menuI18nContribution::add);
        
        Stream
        .of(
            new SimpleEntry<>(this.itemIntoHindi, Language.HI),
            new SimpleEntry<>(this.itemIntoArabic, Language.AR),
            new SimpleEntry<>(this.itemIntoRussia, Language.RU),
            new SimpleEntry<>(this.itemIntoChina, Language.ZH),
            new SimpleEntry<>(this.itemIntoFrench, Language.FR),
            new SimpleEntry<>(this.itemIntoTurkish, Language.TR),
            new SimpleEntry<>(this.itemIntoCzech, Language.CS),
            new SimpleEntry<>(this.itemIntoGerman, Language.DE),
            new SimpleEntry<>(this.itemIntoRomanian, Language.RO),
            new SimpleEntry<>(this.itemIntoTamil, Language.TA),
            new SimpleEntry<>(this.itemIntoDutch, Language.NL),
            new SimpleEntry<>(this.itemIntoIndonesian, Language.IN_ID),
            new SimpleEntry<>(this.itemIntoItalian, Language.IT),
            new SimpleEntry<>(this.itemIntoSpanish, Language.ES),
            new SimpleEntry<>(this.itemIntoPortuguese, Language.PT),
            new SimpleEntry<>(this.itemIntoPolish, Language.PL),
            new SimpleEntry<>(this.itemIntoKorean, Language.KO),
            new SimpleEntry<>(this.itemIntoJapanese, Language.JA),
            new SimpleEntry<>(this.itemIntoSwedish, Language.SE),
            new SimpleEntry<>(this.itemIntoFinnish, Language.FI),
            new SimpleEntry<>(itemIntoOther, Language.OT)
        )
        .forEach(
            entry -> entry.getKey().addActionListener(
                new ActionTranslate(entry.getValue())
            )
        );
        
        return menuI18nContribution;
    }

    private JMenuItem initializeItemReportIssue() {
        
        JMenuItem itemReportIssue = new JMenuItemWithMargin(I18nUtil.valueByKey("MENUBAR_COMMUNITY_REPORTISSUE"), 'R');
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_REPORTISSUE", itemReportIssue);
        
        itemReportIssue.addActionListener(actionEvent -> {
            
            var panel = new JPanel(new BorderLayout());
            final JTextArea textarea = new JPopupTextArea(new JTextArea()).getProxy();
            textarea.setFont(new Font(
                UiUtil.FONT_NAME_MONOSPACED,
                Font.PLAIN,
                UIManager.getDefaults().getFont("TextField.font").getSize()
            ));
            textarea.setText(
                "## What's the expected behavior?\n\n"
                + "## What's the actual behavior?\n\n"
                + "## Any other detailed information on the Issue?\n\n"
                + "## Steps to reproduce the problem\n\n"
                + "  1. ...\n"
                + "  2. ...\n\n"
                + "## [Community] Request for new feature\n\n"
            );
            panel.add(new JLabel("Describe your issue or the bug you encountered :"), BorderLayout.NORTH);
            panel.add(new LightScrollPane(1, 1, 1, 1, textarea));
            
            panel.setPreferredSize(new Dimension(400, 250));
            panel.setMinimumSize(new Dimension(400, 250));
            
            textarea.addMouseListener(new MouseAdapter() {
                
                @Override
                public void mousePressed(MouseEvent e) {
                    
                    super.mousePressed(e);
                    textarea.requestFocusInWindow();
                }
            });

            int result = JOptionPane.showOptionDialog(
                MediatorHelper.frame(),
                panel,
                "Report an issue or a bug",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] {
                    "Report",
                    I18nUtil.valueByKey("LIST_ADD_VALUE_CANCEL")
                },
                I18nUtil.valueByKey("LIST_ADD_VALUE_CANCEL")
            );

            if (StringUtils.isNotEmpty(textarea.getText()) && result == JOptionPane.YES_OPTION) {
                
                MediatorHelper.model().getMediatorUtils().getGitUtil().sendReport(textarea.getText(), ShowOnConsole.YES, "Report");
            }
        });
        
        return itemReportIssue;
    }

    private JMenu initializeMenuEdit() {
        
        // Edit Menu > copy | select all
        var menuEdit = new JMenu(I18nUtil.valueByKey("MENUBAR_EDIT"));
        I18nViewUtil.addComponentForKey("MENUBAR_EDIT", menuEdit);
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItemWithMargin(I18nUtil.valueByKey("CONTEXT_MENU_COPY"), 'C');
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_COPY", itemCopy);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.addActionListener(actionEvent -> {
            
            if (MediatorHelper.tabResults().getSelectedComponent() instanceof PanelTable) {
                
                ((PanelTable) MediatorHelper.tabResults().getSelectedComponent()).copyTable();
                
            } else if (MediatorHelper.tabResults().getSelectedComponent() instanceof JScrollPane) {
                
                ((JTextArea) ((JScrollPane) MediatorHelper.tabResults().getSelectedComponent()).getViewport().getView()).copy();
            }
        });

        JMenuItem itemSelectAll = new JMenuItemWithMargin(I18nUtil.valueByKey("CONTEXT_MENU_SELECT_ALL"), 'A');
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_SELECT_ALL", itemSelectAll);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.addActionListener(actionEvent -> {
            
            if (MediatorHelper.tabResults().getSelectedComponent() instanceof PanelTable) {
                
                ((PanelTable) MediatorHelper.tabResults().getSelectedComponent()).selectTable();
                
            } else if (MediatorHelper.tabResults().getSelectedComponent() instanceof JScrollPane) {
                
                // Textarea need focus to select all
                
                ((JScrollPane) MediatorHelper.tabResults().getSelectedComponent()).getViewport().getView().requestFocusInWindow();
                ((JTextArea) ((JScrollPane) MediatorHelper.tabResults().getSelectedComponent()).getViewport().getView()).selectAll();
            }
        });

        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);
        
        return menuEdit;
    }

    private JMenu initializeMenuFile() {
        
        // File Menu > save tab | exit
        var menuFile = new JMenu(I18nUtil.valueByKey("MENUBAR_FILE"));
        I18nViewUtil.addComponentForKey("MENUBAR_FILE", menuFile);
        menuFile.setMnemonic('F');

        JMenuItem itemSave = new JMenuItemWithMargin(new ActionSaveTab());
        I18nViewUtil.addComponentForKey("MENUBAR_FILE_SAVETABAS", itemSave);

        JMenuItem itemExit = new JMenuItemWithMargin(I18nUtil.valueByKey("MENUBAR_FILE_EXIT"), 'x');
        I18nViewUtil.addComponentForKey("MENUBAR_FILE_EXIT", itemExit);
        itemExit.addActionListener(actionEvent -> MediatorHelper.frame().dispose());

        HotkeyUtil.addShortcut(Menubar.this);

        menuFile.add(itemSave);
        menuFile.add(new JSeparator());
        menuFile.add(itemExit);
        
        return menuFile;
    }
    
    public void switchLocale(Locale newLocale) {
        
        this.switchLocale(I18nUtil.getLocaleDefault(), newLocale, false);
    }
    
    public void switchLocale(Locale oldLocale, Locale newLocale, boolean isStartup) {
        
        I18nUtil.setLocaleDefault(ResourceBundle.getBundle("i18n.jsql", newLocale));
        this.switchNetworkTable(newLocale);
        this.switchI18nComponents(newLocale);
        this.switchOrientation(oldLocale, newLocale, isStartup);
        this.switchMenuItems();
        
        MediatorHelper.treeDatabase().reloadNodes();

        // IllegalArgumentException #92981 on revalidate()
        try {
            // Fix glitches on Linux
            MediatorHelper.frame().revalidate();
            
        } catch (IllegalArgumentException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }

    private void switchOrientation(Locale oldLocale, Locale newLocale, boolean isStartup) {
        
        var componentOrientation = ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault());
        MediatorHelper.frame().applyComponentOrientation(componentOrientation);
        
        if (!ComponentOrientation.getOrientation(oldLocale).equals(ComponentOrientation.getOrientation(newLocale))) {
            
            JSplitPane splitPaneLeftRight = MediatorHelper.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight();
            
            var componentLeft = splitPaneLeftRight.getLeftComponent();
            var componentRight = splitPaneLeftRight.getRightComponent();

            // Reset components
            splitPaneLeftRight.setLeftComponent(null);
            splitPaneLeftRight.setRightComponent(null);
            
            splitPaneLeftRight.setLeftComponent(componentRight);
            splitPaneLeftRight.setRightComponent(componentLeft);
            
            if (isStartup) {
                
                splitPaneLeftRight.setDividerLocation(
                    splitPaneLeftRight.getDividerLocation()
                );
                
            } else {
                
                splitPaneLeftRight.setDividerLocation(
                    splitPaneLeftRight.getWidth() -
                    splitPaneLeftRight.getDividerLocation()
                );
            }
        }
        
        MediatorHelper.tabResults().setComponentOrientation(ComponentOrientation.getOrientation(newLocale));
    }

    private void switchI18nComponents(Locale newLocale) {
        
        for (String key: I18nViewUtil.keys()) {
            
            String textI18n = I18nViewUtil.valueByKey(key, newLocale);
            
            for (Object componentSwing: I18nViewUtil.componentsByKey(key)) {
                
                if (componentSwing instanceof JTextFieldPlaceholder) {
                    
                    // Textfield does not need <html> tags for asian fonts
                    ((JTextFieldPlaceholder) componentSwing).setPlaceholderText(I18nUtil.valueByKey(key));
                    
                } else if (componentSwing instanceof JToolTipI18n) {
                    
                    ((JToolTipI18n) componentSwing).setText(textI18n);
                    
                } else if (componentSwing instanceof JLabel) {
                    
                    ((JLabel) componentSwing).setText(textI18n);
                    
                } else if (componentSwing instanceof JCheckBoxMenuItem) {
                    
                    ((JCheckBoxMenuItem) componentSwing).setText(textI18n);
                    
                } else if (componentSwing instanceof JMenuItem) {
                    
                    ((JMenuItem) componentSwing).setText(textI18n);
                    
                } else if (componentSwing instanceof JButtonStateful) {
                    
                    ((JButtonStateful) componentSwing).setText(textI18n);
                    
                } else if (componentSwing instanceof NodeModelEmpty) {
                    
                    ((NodeModelEmpty) componentSwing).setText(textI18n);
                    
                } else {
                        
                    ((JTextComponent) componentSwing).setText(textI18n);
                }
            }
        }
    }

    private void switchMenuItems() {
        
        Stream
        .of(this.itemArabic, this.itemIntoArabic)
        .forEach(menuItem -> menuItem.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT));
        
        Stream
        .of(
            this.itemEnglish,
            this.itemChinese,
            this.itemRussian,
            this.itemFrench,
            this.itemCzech,
            this.itemDutch,
            this.itemGerman,
            this.itemRomanian,
            this.itemSwedish,
            this.itemFinnish,
            this.itemKorean,
            this.itemTurkish,
            this.itemIndonesian,
            this.itemItalian,
            this.itemSpanish,
            this.itemPortuguese,
            this.itemPolish,
            
            this.itemIntoHindi,
            this.itemIntoRussia,
            this.itemIntoChina,
            this.itemIntoFrench,
            this.itemIntoTurkish,
            this.itemIntoCzech,
            this.itemIntoGerman,
            this.itemIntoRomanian,
            this.itemIntoDutch,
            this.itemIntoIndonesian,
            this.itemIntoItalian,
            this.itemIntoSpanish,
            this.itemIntoPortuguese,
            this.itemIntoPolish,
            this.itemIntoKorean,
            this.itemIntoJapanese,
            this.itemIntoTamil,
            this.itemIntoSwedish,
            this.itemIntoFinnish
        )
        .forEach(menuItem -> menuItem.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT));
    }

    private void switchNetworkTable(Locale newLocale) {
        
        JTableHeader header = MediatorHelper.panelConsoles().getNetworkTable().getTableHeader();
        TableColumnModel columnModel = header.getColumnModel();
        
        if (I18nUtil.isAsian(newLocale)) {
            
            Stream
            .of(
                JTextPaneAppender.ATTRIBUTE_WARN,
                JTextPaneAppender.ATTRIBUTE_INFORM,
                JTextPaneAppender.ATTRIBUTE_SUCCESS,
                JTextPaneAppender.ATTRIBUTE_ALL
            )
            .forEach(attribute -> {
                StyleConstants.setFontFamily(attribute, UiUtil.FONT_NAME_MONO_ASIAN);
                StyleConstants.setFontSize(attribute, UiUtil.FONT_SIZE_MONO_ASIAN);
            });
            
            MediatorHelper.managerBruteForce().getResult().setFont(UiUtil.FONT_MONO_ASIAN);
            
            columnModel.getColumn(0).setHeaderValue(I18nViewUtil.valueByKey("NETWORK_TAB_URL_COLUMN"));
            columnModel.getColumn(1).setHeaderValue(I18nViewUtil.valueByKey("NETWORK_TAB_SIZE_COLUMN") +" (KB)");
            columnModel.getColumn(2).setHeaderValue("Strategy");
            
        } else {
            
            Stream
            .of(
                JTextPaneAppender.ATTRIBUTE_WARN,
                JTextPaneAppender.ATTRIBUTE_INFORM,
                JTextPaneAppender.ATTRIBUTE_SUCCESS,
                JTextPaneAppender.ATTRIBUTE_ALL
            )
            .forEach(attribute -> {
                StyleConstants.setFontFamily(attribute, UiUtil.FONT_NAME_MONO_NON_ASIAN);
                StyleConstants.setFontSize(attribute, UiUtil.FONT_SIZE_MONO_NON_ASIAN);
            });
            
            MediatorHelper.managerBruteForce().getResult().setFont(UiUtil.FONT_MONO_NON_ASIAN);
            
            columnModel.getColumn(0).setHeaderValue(I18nUtil.valueByKey("NETWORK_TAB_URL_COLUMN"));
            columnModel.getColumn(1).setHeaderValue(I18nUtil.valueByKey("NETWORK_TAB_SIZE_COLUMN") +" (KB)");
            columnModel.getColumn(2).setHeaderValue("Strategy");
        }
        
        header.repaint();
    }
    
    
    // Getter and setter

    public JCheckBoxMenuItem getChunkMenu() {
        return this.chunkMenu;
    }

    public JCheckBoxMenuItem getJavaDebugMenu() {
        return this.javaDebugMenu;
    }

    public JMenu getMenuView() {
        return this.menuView;
    }
}
