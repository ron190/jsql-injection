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
import java.awt.Component;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.StyleConstants;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.action.ActionSaveTab;
import com.jsql.view.swing.console.SwingAppender;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.interaction.CreateTab;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.sql.SqlEngine;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextFieldPlaceholder;

/**
 * Application main menubar.
 */
@SuppressWarnings("serial")
public class Menubar extends JMenuBar {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Checkbox item to show/hide chunk console.
     */
    private JCheckBoxMenuItem chunkMenu;

    /**
     * Checkbox item to show/hide java console.
     */
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
        JMenu menuWindows = new JMenu(I18n.valueByKey("MENUBAR_WINDOWS"));
        I18nView.addComponentForKey("MENUBAR_WINDOWS", menuWindows);
        menuWindows.setMnemonic('W');

        JMenuItem itemNewWindows = new JMenuItem(new ActionNewWindow());
        I18nView.addComponentForKey("NEW_WINDOW_MENU", itemNewWindows);
        
        menuWindows.add(itemNewWindows);
        JMenu menuAppearance = new JMenu("Appearance");
        JMenuItem itemNewWindows4k = new JMenuItem(
            new ActionNewWindow("New 4K Window", "-Dsun.java2d.uiScale=2.5")
        );
        menuAppearance.add(itemNewWindows4k);
        menuWindows.add(itemNewWindows);
        menuWindows.add(menuAppearance);
        menuWindows.add(new JSeparator());

        JMenu menuTranslation = this.initializeMenuTranslation();
        
        menuWindows.add(menuTranslation);
        menuWindows.add(new JSeparator());
        
        this.menuView = new JMenu(I18n.valueByKey("MENUBAR_VIEW"));
        I18nView.addComponentForKey("MENUBAR_VIEW", this.menuView);
        this.menuView.setMnemonic('V');
        
        JMenuItem database = new JMenuItem(I18n.valueByKey("DATABASE_TAB"), HelperUi.ICON_DATABASE_SERVER);
        I18nView.addComponentForKey("DATABASE_TAB", database);
        this.menuView.add(database);
        
        JMenuItem adminPage = new JMenuItem(I18n.valueByKey("ADMINPAGE_TAB"), HelperUi.ICON_ADMIN_SERVER);
        I18nView.addComponentForKey("ADMINPAGE_TAB", adminPage);
        this.menuView.add(adminPage);
        
        JMenuItem file = new JMenuItem(I18n.valueByKey("FILE_TAB"), HelperUi.ICON_FILE_SERVER);
        I18nView.addComponentForKey("FILE_TAB", file);
        this.menuView.add(file);
        
        JMenuItem webshell = new JMenuItem(I18n.valueByKey("WEBSHELL_TAB"), HelperUi.ICON_SHELL_SERVER);
        I18nView.addComponentForKey("WEBSHELL_TAB", webshell);
        this.menuView.add(webshell);
        
        JMenuItem sqlshell = new JMenuItem(I18n.valueByKey("SQLSHELL_TAB"), HelperUi.ICON_SHELL_SERVER);
        I18nView.addComponentForKey("SQLSHELL_TAB", sqlshell);
        this.menuView.add(sqlshell);
        
        JMenuItem upload = new JMenuItem(I18n.valueByKey("UPLOAD_TAB"), HelperUi.ICON_UPLOAD);
        I18nView.addComponentForKey("UPLOAD_TAB", upload);
        this.menuView.add(upload);
        
        JMenuItem bruteforce = new JMenuItem(I18n.valueByKey("BRUTEFORCE_TAB"), HelperUi.ICON_BRUTER);
        I18nView.addComponentForKey("BRUTEFORCE_TAB", bruteforce);
        this.menuView.add(bruteforce);
        
        JMenuItem coder = new JMenuItem(I18n.valueByKey("CODER_TAB"), HelperUi.ICON_CODER);
        I18nView.addComponentForKey("CODER_TAB", coder);
        this.menuView.add(coder);
        
        JMenuItem scanList = new JMenuItem(I18n.valueByKey("SCANLIST_TAB"), HelperUi.ICON_SCANLIST);
        I18nView.addComponentForKey("SCANLIST_TAB", scanList);
        this.menuView.add(scanList);
        menuWindows.add(this.menuView);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        JMenu menuPanel = new JMenu(I18n.valueByKey("MENUBAR_PANEL"));
        I18nView.addComponentForKey("MENUBAR_PANEL", menuPanel);
        this.menuView.setMnemonic('V');
        
        this.chunkMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_CHUNK_LABEL"),
            HelperUi.ICON_CHUNK,
            prefs.getBoolean(HelperUi.CHUNK_VISIBLE, true)
        );
        I18nView.addComponentForKey("CONSOLE_CHUNK_LABEL", this.chunkMenu);
        menuPanel.add(this.chunkMenu);
        
        JCheckBoxMenuItem binaryMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_BINARY_LABEL"),
            HelperUi.ICON_BINARY,
            prefs.getBoolean(HelperUi.BINARY_VISIBLE, true)
        );
        I18nView.addComponentForKey("CONSOLE_BINARY_LABEL", binaryMenu);
        menuPanel.add(binaryMenu);
        
        JCheckBoxMenuItem networkMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_NETWORK_LABEL"),
            HelperUi.ICON_HEADER,
            prefs.getBoolean(HelperUi.NETWORK_VISIBLE, true)
        );
        I18nView.addComponentForKey("CONSOLE_NETWORK_LABEL", networkMenu);
        menuPanel.add(networkMenu);
        
        this.javaDebugMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_JAVA_LABEL"),
            HelperUi.ICON_CUP,
            prefs.getBoolean(HelperUi.JAVA_VISIBLE, false)
        );
        I18nView.addComponentForKey("CONSOLE_JAVA_LABEL", this.javaDebugMenu);

        for (JCheckBoxMenuItem menuItem: new JCheckBoxMenuItem[]{this.chunkMenu, binaryMenu, networkMenu, this.javaDebugMenu}) {
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
                MediatorGui.panelConsoles().insertChunkTab();
            } else {
                MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab(HelperUi.ICON_CHUNK));
            }
        });
        
        binaryMenu.addActionListener(actionEvent -> {
            
            if (binaryMenu.isSelected()) {
                MediatorGui.panelConsoles().insertBooleanTab();
            } else {
                MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab(HelperUi.ICON_BINARY));
            }
        });
        
        networkMenu.addActionListener(actionEvent -> {
            
            if (networkMenu.isSelected()) {
                MediatorGui.panelConsoles().insertNetworkTab();
            } else {
                MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab(HelperUi.ICON_HEADER));
            }
        });
        
        this.javaDebugMenu.addActionListener(actionEvent -> {
            
            if (this.javaDebugMenu.isSelected()) {
                MediatorGui.panelConsoles().insertJavaTab();
            } else {
                MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab(HelperUi.ICON_CUP));
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

        for (int position = 0 ; position < this.menuView.getItemCount() ; position++) {
            
            final JMenuItem itemMenu = this.menuView.getItem(position);
            final int positionFinal = position;
            itemMenu.addActionListener(actionEvent -> MediatorGui.tabManagers().setSelectedIndex(positionFinal));
        }

        JMenuItem itemPreferences = this.initializeItemPreferences();
        JMenuItem itemSqlEngine = this.initializeItemSqlEngine();
        
        menuWindows.add(itemSqlEngine);
        menuWindows.add(itemPreferences);
        
        return menuWindows;
    }

    private JMenu initializeMenuHelp() {
        
        // Help Menu > about
        JMenu menuHelp = new JMenu(I18n.valueByKey("MENUBAR_HELP"));
        menuHelp.setMnemonic('H');
        I18nView.addComponentForKey("MENUBAR_HELP", menuHelp);
        
        JMenuItem itemHelp = new JMenuItem(I18n.valueByKey("MENUBAR_HELP_ABOUT"), 'A');
        itemHelp.setIcon(HelperUi.ICON_EMPTY);
        I18nView.addComponentForKey("MENUBAR_HELP_ABOUT", itemHelp);
        
        JMenuItem itemUpdate = new JMenuItem(I18n.valueByKey("MENUBAR_HELP_UPDATE"), 'U');
        itemUpdate.setIcon(HelperUi.ICON_EMPTY);
        I18nView.addComponentForKey("MENUBAR_HELP_UPDATE", itemUpdate);

        // Render the About dialog behind scene
        final DialogAbout aboutDiag = new DialogAbout();
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
        
        JMenuItem itemSqlEngine = new JMenuItem(I18n.valueByKey("MENUBAR_SQL_ENGINE"));
        I18nView.addComponentForKey("MENUBAR_SQL_ENGINE", itemSqlEngine);
        
        // Render the SQL Engine dialog behind scene
        String titleTabSqlEngine = "SQL Engine";
        
        itemSqlEngine.addActionListener(actionEvent -> {
            
            for (int i = 0; i < MediatorGui.tabResults().getTabCount() ; i++) {
                
                if (titleTabSqlEngine.equals(MediatorGui.tabResults().getTitleAt(i))) {
                    
                    MediatorGui.tabResults().setSelectedIndex(i);
                    return;
                }
            }
            
            CreateTab.initializeSplitOrientation();

            SqlEngine panelSqlEngine = new SqlEngine();
            
            MediatorGui.tabResults().addTab(titleTabSqlEngine, panelSqlEngine);

            // Focus on the new tab
            MediatorGui.tabResults().setSelectedComponent(panelSqlEngine);

            // Create a custom tab header with close button
            TabHeader header = new TabHeader(I18nView.valueByKey("MENUBAR_SQL_ENGINE"), HelperUi.ICON_COG, panelSqlEngine);
            I18nView.addComponentForKey("MENUBAR_SQL_ENGINE", header.getTabTitleLabel());

            // Apply the custom header to the tab
            MediatorGui.tabResults().setTabComponentAt(MediatorGui.tabResults().indexOfComponent(panelSqlEngine), header);
        });
        
        return itemSqlEngine;
    }

    private JMenuItem initializeItemPreferences() {
        
        JMenuItem itemPreferences = new JMenuItem(I18n.valueByKey("MENUBAR_PREFERENCES"), 'P');
        itemPreferences.setIcon(HelperUi.ICON_EMPTY);
        I18nView.addComponentForKey("MENUBAR_PREFERENCES", itemPreferences);
        
        // Render the Preferences dialog behind scene
        String titleTabPreferences = "Preferences";
        
        itemPreferences.addActionListener(actionEvent -> {
            
            for (int i = 0; i < MediatorGui.tabResults().getTabCount() ; i++) {
                
                if (titleTabPreferences.equals(MediatorGui.tabResults().getTitleAt(i))) {
                    
                    MediatorGui.tabResults().setSelectedIndex(i);
                    return;
                }
            }
            
            CreateTab.initializeSplitOrientation();
            
            AdjustmentListener singleItemScroll = adjustmentEvent -> {
                
                // The user scrolled the List (using the bar, mouse wheel or something else):
                if (adjustmentEvent.getAdjustmentType() == AdjustmentEvent.TRACK){
                    
                    // Jump to the next "block" (which is a row".
                    adjustmentEvent.getAdjustable().setBlockIncrement(100);
                    adjustmentEvent.getAdjustable().setUnitIncrement(100);
                }
            };

            LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, new PanelPreferences());
            scroller.scrollPane.getVerticalScrollBar().addAdjustmentListener(singleItemScroll);
            
            MediatorGui.tabResults().addTab(titleTabPreferences, scroller);

            // Focus on the new tab
            MediatorGui.tabResults().setSelectedComponent(scroller);

            // Create a custom tab header with close button
            TabHeader header = new TabHeader(I18nView.valueByKey("MENUBAR_PREFERENCES"), HelperUi.ICON_COG);
            I18nView.addComponentForKey("MENUBAR_PREFERENCES", header.getTabTitleLabel());

            // Apply the custom header to the tab
            MediatorGui.tabResults().setTabComponentAt(MediatorGui.tabResults().indexOfComponent(scroller), header);
        });
        
        return itemPreferences;
    }

    private JMenu initializeMenuCommunity() {
        
        // Help Menu > about
        JMenu menuCommunity = new JMenu(I18n.valueByKey("MENUBAR_COMMUNITY"));
        menuCommunity.setMnemonic('C');
        I18nView.addComponentForKey("MENUBAR_COMMUNITY", menuCommunity);
        
        JMenu menuI18nContribution = this.initializeMenuI18nContribution();
        JMenuItem itemReportIssue = this.initializeItemReportIssue();
        
        menuCommunity.add(menuI18nContribution);
        menuCommunity.add(new JSeparator());
        menuCommunity.add(itemReportIssue);
        
        return menuCommunity;
    }

    private JMenu initializeMenuTranslation() {
        
        JMenu menuTranslation = new JMenu(I18n.valueByKey("MENUBAR_LANGUAGE"));
        I18nView.addComponentForKey("MENUBAR_LANGUAGE", menuTranslation);
        
        Object[] languages = Stream
            .of("ru zh es fr tr ko se ar cs it pt pl in nl ro de".split(" "))
            .map(flag -> new Locale(flag).getLanguage())
            .collect(Collectors.toList())
            .toArray();
        
        boolean isEnglish = !ArrayUtils.contains(languages, Locale.getDefault().getLanguage());
    
        this.itemEnglish = new JRadioButtonMenuItem(
            new Locale("en").getDisplayLanguage(new Locale("en")),
            HelperUi.ICON_FLAG_EN,
            isEnglish
        );
        this.itemEnglish.addActionListener(actionEvent -> Menubar.this.switchLocale(Locale.ROOT));
        
        this.itemArabic = new JRadioButtonMenuItem(
            "<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("ar").getDisplayLanguage(new Locale("ar")) +"</span></html>",
            HelperUi.ICON_FLAG_AR,
            new Locale("ar").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemArabic.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ar")));
        this.itemArabic.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        this.itemRussian = new JRadioButtonMenuItem(
            new Locale("ru").getDisplayLanguage(new Locale("ru")),
            HelperUi.ICON_FLAG_RU,
            new Locale("ru").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemRussian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ru")));
        
        this.itemCzech = new JRadioButtonMenuItem(
            new Locale("cs").getDisplayLanguage(new Locale("cs")),
            HelperUi.ICON_FLAG_CS,
            new Locale("cs").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemCzech.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("cs")));
        
        this.itemItalian = new JRadioButtonMenuItem(
            new Locale("it").getDisplayLanguage(new Locale("it")),
            HelperUi.ICON_FLAG_IT,
            new Locale("it").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemItalian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("it")));
        
        this.itemIndonesian = new JRadioButtonMenuItem(
            new Locale("in", "ID").getDisplayLanguage(new Locale("in", "ID")),
            HelperUi.ICON_FLAG_IN_ID,
            new Locale("in", "ID").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemIndonesian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("in", "ID")));
        
        this.itemDutch = new JRadioButtonMenuItem(
            new Locale("nl").getDisplayLanguage(new Locale("nl")),
            HelperUi.ICON_FLAG_NL,
            new Locale("nl").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemDutch.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("nl")));
        
        this.itemGerman = new JRadioButtonMenuItem(
            new Locale("de").getDisplayLanguage(new Locale("de")),
            HelperUi.ICON_FLAG_DE,
            new Locale("de").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemGerman.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("de")));
        
        this.itemTurkish = new JRadioButtonMenuItem(
            new Locale("tr").getDisplayLanguage(new Locale("tr")),
            HelperUi.ICON_FLAG_TR,
            new Locale("tr").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemTurkish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("tr")));
        
        this.itemFrench = new JRadioButtonMenuItem(
            new Locale("fr").getDisplayLanguage(new Locale("fr")),
            HelperUi.ICON_FLAG_FR,
            new Locale("fr").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemFrench.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("fr")));
        
        this.itemSpanish = new JRadioButtonMenuItem(
            new Locale("es").getDisplayLanguage(new Locale("es")),
            HelperUi.ICON_FLAG_ES,
            new Locale("es").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemSpanish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("es")));
        
        this.itemPortuguese = new JRadioButtonMenuItem(
            new Locale("pt").getDisplayLanguage(new Locale("pt")),
            HelperUi.ICON_FLAG_PT,
            new Locale("pt").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemPortuguese.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("pt")));
        
        this.itemChinese = new JRadioButtonMenuItem(
            "<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("zh").getDisplayLanguage(new Locale("zh")) +"</span></html>",
            HelperUi.ICON_FLAG_ZH,
            new Locale("zh").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemChinese.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("zh")));
        
        this.itemPolish = new JRadioButtonMenuItem(
            new Locale("pl").getDisplayLanguage(new Locale("pl")),
            HelperUi.ICON_FLAG_PL,
            new Locale("pl").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemPolish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("pl")));
        
        this.itemRomanian = new JRadioButtonMenuItem(
            new Locale("ro").getDisplayLanguage(new Locale("ro")),
            HelperUi.ICON_FLAG_RO,
            new Locale("ro").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemRomanian.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ro")));
        
        this.itemSwedish = new JRadioButtonMenuItem(
            new Locale("se").getDisplayLanguage(new Locale("se")),
            HelperUi.ICON_FLAG_SE,
            new Locale("se").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemSwedish.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("se")));
        
        this.itemKorean = new JRadioButtonMenuItem(
            "<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("ko").getDisplayLanguage(new Locale("ko")) +"</span></html>",
            HelperUi.ICON_FLAG_KO,
            new Locale("ko").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        this.itemKorean.addActionListener(actionEvent -> Menubar.this.switchLocale(new Locale("ko")));
        
        ButtonGroup groupRadioLanguage = new ButtonGroup();
        
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
            this.itemArabic,
            this.itemCzech,
            this.itemItalian,
            this.itemPortuguese,
            this.itemPolish,
            this.itemIndonesian,
            this.itemDutch,
            this.itemRomanian,
            this.itemGerman
        ).forEach(menuItem -> {
            menuTranslation.add(menuItem);
            groupRadioLanguage.add(menuItem);
        });
        
        return menuTranslation;
    }

    private JMenu initializeMenuI18nContribution() {
        
        JMenu menuI18nContribution = new JMenu(I18n.valueByKey("MENUBAR_COMMUNITY_HELPTRANSLATE"));
        I18nView.addComponentForKey("MENUBAR_COMMUNITY_HELPTRANSLATE", menuI18nContribution);
        
        // Render the About dialog behind scene
        final DialogTranslate dialogTranslate = new DialogTranslate();
        
        class ActionTranslate implements ActionListener {
            
            Language language;
            
            ActionTranslate(Language language) {
                this.language = language;
            }
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                dialogTranslate.initializeDialog(this.language);
                
                // Center the dialog
                if (!dialogTranslate.isVisible()) {
                    
                    dialogTranslate.setSize(640, 460);
                    dialogTranslate.setLocationRelativeTo(MediatorGui.frame());
                    dialogTranslate.getRootPane().setDefaultButton(dialogTranslate.buttonSend);
                }
                
                dialogTranslate.setVisible(true);
            }
        }
        
        this.itemIntoHindi = new JMenuItem("<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("hi").getDisplayLanguage(new Locale("hi")) +"</span>...</html>", HelperUi.ICON_FLAG_HI);
        this.itemIntoArabic = new JMenuItem("<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("ar").getDisplayLanguage(new Locale("ar")) +"</span>...</html>", HelperUi.ICON_FLAG_AR);
        this.itemIntoRussia = new JMenuItem(new Locale("ru").getDisplayLanguage(new Locale("ru")) +"...", HelperUi.ICON_FLAG_RU);
        this.itemIntoChina = new JMenuItem("<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("zh").getDisplayLanguage(new Locale("zh")) +"</span>...</html>", HelperUi.ICON_FLAG_ZH);
        this.itemIntoFrench = new JMenuItem(new Locale("fr").getDisplayLanguage(new Locale("fr")) +"...", HelperUi.ICON_FLAG_FR);
        this.itemIntoTurkish = new JMenuItem(new Locale("tr").getDisplayLanguage(new Locale("tr")) +"...", HelperUi.ICON_FLAG_TR);
        this.itemIntoCzech = new JMenuItem(new Locale("cs").getDisplayLanguage(new Locale("cs")) +"...", HelperUi.ICON_FLAG_CS);
        this.itemIntoDutch = new JMenuItem(new Locale("nl").getDisplayLanguage(new Locale("nl")) +"...", HelperUi.ICON_FLAG_NL);
        this.itemIntoGerman = new JMenuItem(new Locale("de").getDisplayLanguage(new Locale("de")) +"...", HelperUi.ICON_FLAG_DE);
        this.itemIntoIndonesian = new JMenuItem(new Locale("in", "ID").getDisplayLanguage(new Locale("in", "ID")) +"...", HelperUi.ICON_FLAG_IN_ID);
        this.itemIntoItalian = new JMenuItem(new Locale("it").getDisplayLanguage(new Locale("it")) +"...", HelperUi.ICON_FLAG_IT);
        this.itemIntoSpanish = new JMenuItem(new Locale("es").getDisplayLanguage(new Locale("es")) +"...", HelperUi.ICON_FLAG_ES);
        this.itemIntoPortuguese = new JMenuItem(new Locale("pt").getDisplayLanguage(new Locale("pt")) +"...", HelperUi.ICON_FLAG_PT);
        this.itemIntoPolish = new JMenuItem(new Locale("pl").getDisplayLanguage(new Locale("pl")) +"...", HelperUi.ICON_FLAG_PL);
        this.itemIntoRomanian = new JMenuItem(new Locale("ro").getDisplayLanguage(new Locale("ro")) +"...", HelperUi.ICON_FLAG_RO);
        this.itemIntoTamil = new JMenuItem(new Locale("ta").getDisplayLanguage(new Locale("ta")) +"...", HelperUi.ICON_FLAG_LK);
        this.itemIntoJapanese = new JMenuItem("<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("ja").getDisplayLanguage(new Locale("ja")) +"</span>...</html>", HelperUi.ICON_FLAG_JA);
        this.itemIntoKorean = new JMenuItem("<html><span style=\"font-family:'"+ HelperUi.FONT_NAME_UBUNTU_REGULAR +"'\">"+ new Locale("ko").getDisplayLanguage(new Locale("ko")) +"</span>...</html>", HelperUi.ICON_FLAG_KO);
        this.itemIntoSwedish = new JMenuItem(new Locale("se").getDisplayLanguage(new Locale("se")) +"...", HelperUi.ICON_FLAG_SE);
        JMenuItem itemIntoOther = new JMenuItem(I18n.valueByKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE"));
        I18nView.addComponentForKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE", itemIntoOther);
        
        this.itemIntoArabic.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        Stream
        .of(
            this.itemIntoFrench,
            this.itemIntoSpanish,
            this.itemIntoSwedish,
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
        
        JMenuItem itemReportIssue = new JMenuItem(I18n.valueByKey("MENUBAR_COMMUNITY_REPORTISSUE"), 'R');
        itemReportIssue.setIcon(HelperUi.ICON_EMPTY);
        I18nView.addComponentForKey("MENUBAR_COMMUNITY_REPORTISSUE", itemReportIssue);
        
        itemReportIssue.addActionListener(actionEvent -> {
            
            JPanel panel = new JPanel(new BorderLayout());
            final JTextArea textarea = new JPopupTextArea(new JTextArea()).getProxy();
            textarea.setFont(new Font(
                HelperUi.FONT_NAME_MONOSPACED,
                Font.PLAIN,
                UIManager.getDefaults().getFont("TextField.font").getSize()
            ));
            textarea.setText(
                "## What's the expected behavior?\n\n"
                + "## And what's the actual behavior?\n\n"
                + "## Any detailed information about the Issue?\n\n"
                + "## Steps to reproduce the behavior\n\n"
                + "  1. ...\n"
                + "  2. ...\n\n"
                + "## [Community] Any request for a new feature?\n\n"
            );
            panel.add(new JLabel("Describe your issue or the bug you encountered " + ":"), BorderLayout.NORTH);
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
                MediatorGui.frame(),
                panel,
                "Report an issue or a bug",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Report", I18n.valueByKey("LIST_ADD_VALUE_CANCEL")},
                I18n.valueByKey("LIST_ADD_VALUE_CANCEL")
            );

            if (StringUtils.isNotEmpty(textarea.getText()) && result == JOptionPane.YES_OPTION) {
                MediatorModel.model().getMediatorUtils().getGitUtil().sendReport(textarea.getText(), ShowOnConsole.YES, "Report");
            }
        });
        
        return itemReportIssue;
    }

    private JMenu initializeMenuEdit() {
        
        // Edit Menu > copy | select all
        JMenu menuEdit = new JMenu(I18n.valueByKey("MENUBAR_EDIT"));
        I18nView.addComponentForKey("MENUBAR_EDIT", menuEdit);
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItem(I18n.valueByKey("CONTEXT_MENU_COPY"), 'C');
        I18nView.addComponentForKey("CONTEXT_MENU_COPY", itemCopy);
        itemCopy.setIcon(HelperUi.ICON_EMPTY);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.addActionListener(actionEvent -> {
            
            if (MediatorGui.tabResults().getSelectedComponent() instanceof PanelTable) {
                ((PanelTable) MediatorGui.tabResults().getSelectedComponent()).copyTable();
            } else if (MediatorGui.tabResults().getSelectedComponent() instanceof JScrollPane) {
                ((JTextArea) ((JScrollPane) MediatorGui.tabResults().getSelectedComponent()).getViewport().getView()).copy();
            }
        });

        JMenuItem itemSelectAll = new JMenuItem(I18n.valueByKey("CONTEXT_MENU_SELECT_ALL"), 'A');
        I18nView.addComponentForKey("CONTEXT_MENU_SELECT_ALL", itemSelectAll);
        itemSelectAll.setIcon(HelperUi.ICON_EMPTY);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.addActionListener(actionEvent -> {
            
            if (MediatorGui.tabResults().getSelectedComponent() instanceof PanelTable) {
                ((PanelTable) MediatorGui.tabResults().getSelectedComponent()).selectTable();
            // Textarea need focus to select all
            } else if (MediatorGui.tabResults().getSelectedComponent() instanceof JScrollPane) {
                ((JScrollPane) MediatorGui.tabResults().getSelectedComponent()).getViewport().getView().requestFocusInWindow();
                ((JTextArea) ((JScrollPane) MediatorGui.tabResults().getSelectedComponent()).getViewport().getView()).selectAll();
            }
        });

        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);
        
        return menuEdit;
    }

    private JMenu initializeMenuFile() {
        
        // File Menu > save tab | exit
        JMenu menuFile = new JMenu(I18n.valueByKey("MENUBAR_FILE"));
        I18nView.addComponentForKey("MENUBAR_FILE", menuFile);
        menuFile.setMnemonic('F');

        JMenuItem itemSave = new JMenuItem(new ActionSaveTab());
        I18nView.addComponentForKey("MENUBAR_FILE_SAVETABAS", itemSave);

        JMenuItem itemExit = new JMenuItem(I18n.valueByKey("MENUBAR_FILE_EXIT"), 'x');
        I18nView.addComponentForKey("MENUBAR_FILE_EXIT", itemExit);
        itemExit.setIcon(HelperUi.ICON_EMPTY);
        itemExit.addActionListener(actionEvent -> MediatorGui.frame().dispose());

        ActionHandler.addShortcut(Menubar.this);

        menuFile.add(itemSave);
        menuFile.add(new JSeparator());
        menuFile.add(itemExit);
        
        return menuFile;
    }
    
    public void switchLocale(Locale newLocale) {
        this.switchLocale(I18n.getLocaleDefault(), newLocale, false);
    }
    
    public void switchLocale(Locale oldLocale, Locale newLocale, boolean isStartup) {
        
        I18n.setLocaleDefault(ResourceBundle.getBundle("i18n.jsql", newLocale));
        this.switchNetworkTable(newLocale);
        this.switchI18nComponents(newLocale);
        this.switchOrientation(oldLocale, newLocale, isStartup);
        this.switchMenuItems();
        
        MediatorGui.treeDatabase().reloadNodes();
        
        // Fix glitches on Linux
        MediatorGui.frame().revalidate();
    }

    private void switchOrientation(Locale oldLocale, Locale newLocale, boolean isStartup) {
        
        ComponentOrientation componentOrientation = ComponentOrientation.getOrientation(I18n.getLocaleDefault());
        MediatorGui.frame().applyComponentOrientation(componentOrientation);
        
        if (ComponentOrientation.getOrientation(oldLocale) != ComponentOrientation.getOrientation(newLocale)) {
            
            Component componentLeft = MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getLeftComponent();
            Component componentRight = MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getRightComponent();
            
            MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setLeftComponent(null);
            MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setRightComponent(null);
            MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setLeftComponent(componentRight);
            MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setRightComponent(componentLeft);
            
            if (isStartup) {
                
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setDividerLocation(
                    MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getDividerLocation()
                );
            } else {
                
                MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().setDividerLocation(
                    MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getWidth() -
                    MediatorGui.frame().getSplitHorizontalTopBottom().getSplitVerticalLeftRight().getDividerLocation()
                );
            }
        }
        
        MediatorGui.tabResults().setComponentOrientation(ComponentOrientation.getOrientation(newLocale));
    }

    private void switchI18nComponents(Locale newLocale) {
        
        // TODO stream
        for (String key: I18nView.keys()) {
            for (Object componentSwing: I18nView.componentsByKey(key)) {
                
                Class<?> classComponent = componentSwing.getClass();
                try {
                    if (componentSwing instanceof JTextFieldPlaceholder) {
                        
                        Method setPlaceholderText = classComponent.getMethod("setPlaceholderText", String.class);
                        setPlaceholderText.invoke(componentSwing, I18n.valueByKey(key));
                    } else {
                        
                        Method methodSetText = classComponent.getMethod("setText", String.class);
                        methodSetText.setAccessible(true);
                        
                        if (I18n.isAsian(newLocale)) {
                            methodSetText.invoke(componentSwing, I18nView.valueByKey(key));
                        } else {
                            methodSetText.invoke(componentSwing, I18n.valueByKey(key));
                        }
                    }
                } catch (
                    NoSuchMethodException | SecurityException | IllegalAccessException |
                    IllegalArgumentException | InvocationTargetException e
                ) {
                    LOGGER.error("Reflection for "+ key +" failed while switching locale", e);
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
            this.itemIntoSwedish
        )
        .forEach(menuItem -> menuItem.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT));
    }

    private void switchNetworkTable(Locale newLocale) {
        
        JTableHeader header = MediatorGui.panelConsoles().getNetworkTable().getTableHeader();
        TableColumnModel colMod = header.getColumnModel();
        
        if (I18n.isAsian(newLocale)) {
            Stream
            .of(
                SwingAppender.ERROR,
                SwingAppender.WARN,
                SwingAppender.INFO,
                SwingAppender.DEBUG,
                SwingAppender.TRACE,
                SwingAppender.ALL
            )
            .forEach(attribute -> StyleConstants.setFontFamily(attribute, HelperUi.FONT_NAME_UBUNTU_REGULAR));
            
            MediatorGui.managerBruteForce().getResult().setFont(HelperUi.FONT_UBUNTU_REGULAR);
            
            colMod.getColumn(0).setHeaderValue(I18nView.valueByKey("NETWORK_TAB_METHOD_COLUMN"));
            colMod.getColumn(1).setHeaderValue(I18nView.valueByKey("NETWORK_TAB_URL_COLUMN"));
            colMod.getColumn(2).setHeaderValue(I18nView.valueByKey("NETWORK_TAB_SIZE_COLUMN"));
            colMod.getColumn(3).setHeaderValue(I18nView.valueByKey("NETWORK_TAB_TYPE_COLUMN"));
        } else {
            Stream
            .of(
                SwingAppender.ERROR,
                SwingAppender.WARN,
                SwingAppender.INFO,
                SwingAppender.DEBUG,
                SwingAppender.TRACE,
                SwingAppender.ALL
            )
            .forEach(attribute -> StyleConstants.setFontFamily(attribute, HelperUi.FONT_NAME_UBUNTU_MONO));
            
            MediatorGui.managerBruteForce().getResult().setFont(HelperUi.FONT_UBUNTU_MONO);
            
            colMod.getColumn(0).setHeaderValue(I18n.valueByKey("NETWORK_TAB_METHOD_COLUMN"));
            colMod.getColumn(1).setHeaderValue(I18n.valueByKey("NETWORK_TAB_URL_COLUMN"));
            colMod.getColumn(2).setHeaderValue(I18n.valueByKey("NETWORK_TAB_SIZE_COLUMN"));
            colMod.getColumn(3).setHeaderValue(I18n.valueByKey("NETWORK_TAB_TYPE_COLUMN"));
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
