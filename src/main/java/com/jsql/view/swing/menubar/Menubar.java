/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

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
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.util.GitUtil;
import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.action.ActionSaveTab;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.dialog.Language;
import com.jsql.view.swing.dialog.PanelPreferences;
import com.jsql.view.swing.interaction.CreateTab;
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
    public JCheckBoxMenuItem chunkMenu;

    /**
     * Checkbox item to show/hide binary console.
     */
    public JCheckBoxMenuItem binaryMenu;

    /**
     * Checkbox item to show/hide network panel.
     */
    public JCheckBoxMenuItem networkMenu;

    /**
     * Checkbox item to show/hide java console.
     */
    public JCheckBoxMenuItem javaDebugMenu;
    
    public JMenu menuView;
    
    public JMenuItem itemArab;
    public JMenuItem itemEnglish;
    public JMenuItem itemChinese;
    public JMenuItem itemRussian;
    public JMenuItem itemFrench;
    public JMenuItem itemCzech;
    public JMenuItem itemTurkish;
    public JMenuItem itemGerman;
    public JMenuItem itemDutch;
    public JMenuItem itemIndonesian;
    public JMenuItem itemItalian;
    public JMenuItem itemSpanish;
    public JMenuItem itemPortuguese;
    
    public JMenuItem itemIntoHindi;
    public JMenuItem itemIntoArabic;
    public JMenuItem itemIntoRussia;
    public JMenuItem itemIntoChina;
    public JMenuItem itemIntoFrench;
    public JMenuItem itemIntoTurkish;
    public JMenuItem itemIntoCzech;
    public JMenuItem itemIntoGerman;
    public JMenuItem itemIntoDutch;
    public JMenuItem itemIntoIndonesian;
    public JMenuItem itemIntoItalian;
    public JMenuItem itemIntoSpanish;
    public JMenuItem itemIntoPortuguese;
    public JMenuItem itemIntoOther;

    /**
     * Create a menubar on main frame.
     */
    public Menubar() {
        // File Menu > save tab | exit
        JMenu menuFile = new JMenu(I18n.valueByKey("MENUBAR_FILE"));
        I18n.addComponentForKey("MENUBAR_FILE", menuFile);
        menuFile.setMnemonic('F');

        JMenuItem itemNewWindows = new JMenuItem(new ActionNewWindow());
        I18n.addComponentForKey("NEW_WINDOW_MENU", itemNewWindows);

        JMenuItem itemSave = new JMenuItem(new ActionSaveTab());

        JMenuItem itemExit = new JMenuItem(I18n.valueByKey("MENUBAR_FILE_EXIT"), 'x');
        I18n.addComponentForKey("MENUBAR_FILE_EXIT", itemExit);
        itemExit.setIcon(HelperUi.ICON_EMPTY);
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MediatorGui.frame().dispose();
            }
        });

        ActionHandler.addShortcut(Menubar.this);

        menuFile.add(itemNewWindows);
        menuFile.add(new JSeparator());
        menuFile.add(itemSave);
        menuFile.add(new JSeparator());
        menuFile.add(itemExit);

        // Edit Menu > copy | select all
        JMenu menuEdit = new JMenu(I18n.valueByKey("MENUBAR_EDIT"));
        I18n.addComponentForKey("MENUBAR_EDIT", menuEdit);
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItem(I18n.valueByKey("CONTEXT_MENU_COPY"), 'C');
        I18n.addComponentForKey("CONTEXT_MENU_COPY", itemCopy);
        itemCopy.setIcon(HelperUi.ICON_EMPTY);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MediatorGui.tabResults().getSelectedComponent() instanceof PanelTable) {
                    ((PanelTable) MediatorGui.tabResults().getSelectedComponent()).copyTable();
                } else if (MediatorGui.tabResults().getSelectedComponent() instanceof JScrollPane) {
                    ((JTextArea) ((JScrollPane) MediatorGui.tabResults().getSelectedComponent()).getViewport().getView()).copy();
                }
            }
        });

        JMenuItem itemSelectAll = new JMenuItem(I18n.valueByKey("CONTEXT_MENU_SELECT_ALL"), 'A');
        I18n.addComponentForKey("CONTEXT_MENU_SELECT_ALL", itemSelectAll);
        itemSelectAll.setIcon(HelperUi.ICON_EMPTY);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MediatorGui.tabResults().getSelectedComponent() instanceof PanelTable) {
                    ((PanelTable) MediatorGui.tabResults().getSelectedComponent()).selectTable();
                // Textarea need focus to select all
                } else if (MediatorGui.tabResults().getSelectedComponent() instanceof JScrollPane) {
                    ((JScrollPane) MediatorGui.tabResults().getSelectedComponent()).getViewport().getView().requestFocusInWindow();
                    ((JTextArea) ((JScrollPane) MediatorGui.tabResults().getSelectedComponent()).getViewport().getView()).selectAll();
                }
            }
        });

        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);

        // Window Menu > Preferences
        JMenu menuWindows = new JMenu(I18n.valueByKey("MENUBAR_WINDOWS"));
        I18n.addComponentForKey("MENUBAR_WINDOWS", menuWindows);
        menuWindows.setMnemonic('W');

        JMenu menuTranslation = new JMenu("Language");
        
        itemEnglish = new JRadioButtonMenuItem(
            new Locale("en").getDisplayLanguage(new Locale("en")),
            HelperUi.ICON_FLAG_EN, 
            !ArrayUtils.contains(
                new String[]{
                    new Locale("fr").getLanguage(), 
                    new Locale("cs").getLanguage(), 
                    new Locale("ar").getLanguage(), 
                    new Locale("ru").getLanguage(), 
                    new Locale("zh").getLanguage()
                }, 
                Locale.getDefault().getLanguage()
            )
        );
        itemEnglish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(Locale.ROOT);                
            }
        });
        
        itemArab = new JRadioButtonMenuItem(
            "<html><span style=\"font-family:'Monospace'\">"+ new Locale("ar").getDisplayLanguage(new Locale("ar")) +"</span></html>",
            HelperUi.ICON_FLAG_AR, 
            new Locale("ar").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemArab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("ar"));                
            }
        });
        itemArab.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        itemRussian = new JRadioButtonMenuItem(
            new Locale("ru").getDisplayLanguage(new Locale("ru")),
            HelperUi.ICON_FLAG_RU, 
            new Locale("ru").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemRussian.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("ru"));                
            }
        });
        
        itemCzech = new JRadioButtonMenuItem(
            new Locale("cs").getDisplayLanguage(new Locale("cs")),
            HelperUi.ICON_FLAG_CS, 
            new Locale("cs").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemCzech.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("cs"));                
            }
        });
        
        itemItalian = new JRadioButtonMenuItem(
            new Locale("it").getDisplayLanguage(new Locale("it")),
            HelperUi.ICON_FLAG_IT, 
            new Locale("it").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemItalian.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("it"));                
            }
        });
        
        itemIndonesian = new JRadioButtonMenuItem(
            new Locale("in", "ID").getDisplayLanguage(new Locale("in", "ID")),
            HelperUi.ICON_FLAG_IN_ID, 
            new Locale("in", "ID").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemIndonesian.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("in", "ID"));                
            }
        });
        
        itemDutch = new JRadioButtonMenuItem(
            new Locale("nl").getDisplayLanguage(new Locale("nl")),
            HelperUi.ICON_FLAG_NL, 
            new Locale("nl").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemDutch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("nl"));                
            }
        });
        
        itemGerman = new JRadioButtonMenuItem(
            new Locale("de").getDisplayLanguage(new Locale("de")),
            HelperUi.ICON_FLAG_DE, 
            new Locale("de").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemGerman.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("de"));                
            }
        });
        
        itemTurkish = new JRadioButtonMenuItem(
            new Locale("tr").getDisplayLanguage(new Locale("tr")),
            HelperUi.ICON_FLAG_TR, 
            new Locale("tr").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemTurkish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("tr"));                
            }
        });
        
        itemFrench = new JRadioButtonMenuItem(
            new Locale("fr").getDisplayLanguage(new Locale("fr")),
            HelperUi.ICON_FLAG_FR, 
            new Locale("fr").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemFrench.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("fr"));                
            }
        });
        
        itemSpanish = new JRadioButtonMenuItem(
            new Locale("es").getDisplayLanguage(new Locale("es")),
            HelperUi.ICON_FLAG_ES, 
            new Locale("es").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemSpanish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("es"));                
            }
        });
        
        itemPortuguese = new JRadioButtonMenuItem(
            new Locale("pt").getDisplayLanguage(new Locale("pt")),
            HelperUi.ICON_FLAG_PT, 
            new Locale("pt").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemPortuguese.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("pt"));                
            }
        });
        
        itemChinese = new JRadioButtonMenuItem(
            "<html><span style=\"font-family:'Monospace'\">"+ new Locale("zh").getDisplayLanguage(new Locale("zh")) +"</span></html>",
            HelperUi.ICON_FLAG_ZH, 
            new Locale("zh").getLanguage().equals(Locale.getDefault().getLanguage())
        );
        itemChinese.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menubar.this.switchLocale(new Locale("zh"));                
            }
        });
        
        menuTranslation.add(itemEnglish);
        menuTranslation.add(itemChinese);
        menuTranslation.add(itemArab);
        menuTranslation.add(itemRussian);
        menuTranslation.add(itemFrench);
        menuTranslation.add(itemCzech);
        menuTranslation.add(itemDutch);
        menuTranslation.add(itemGerman);
        menuTranslation.add(itemTurkish);
        menuTranslation.add(itemIndonesian);
        menuTranslation.add(itemItalian);
        menuTranslation.add(itemSpanish);
        menuTranslation.add(itemPortuguese);
        
        ButtonGroup groupRadioLanguage = new ButtonGroup();
        groupRadioLanguage.add(itemEnglish);
        groupRadioLanguage.add(itemArab);
        groupRadioLanguage.add(itemRussian);
        groupRadioLanguage.add(itemCzech);
        groupRadioLanguage.add(itemDutch);
        groupRadioLanguage.add(itemFrench);
        groupRadioLanguage.add(itemGerman);
        groupRadioLanguage.add(itemChinese);
        groupRadioLanguage.add(itemTurkish);
        groupRadioLanguage.add(itemIndonesian);
        groupRadioLanguage.add(itemItalian);
        groupRadioLanguage.add(itemSpanish);
        groupRadioLanguage.add(itemPortuguese);
        
        JMenu menuI18nContribution = new JMenu("I help translate jSQL into");
        
        // Render the About dialog behind scene
        final DialogTranslate dialogTranslate = new DialogTranslate();
        
        class ActionTranslate implements ActionListener {
            Language language;
            
            ActionTranslate(Language language) {
                this.language = language;
            }
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dialogTranslate.reinit(this.language);
                
                // Center the dialog
                if (!dialogTranslate.isVisible()) {
                    dialogTranslate.setSize(640, 460);
                    dialogTranslate.setLocationRelativeTo(MediatorGui.frame());
                    dialogTranslate.getRootPane().setDefaultButton(dialogTranslate.buttonSend);
                }
                
                dialogTranslate.setVisible(true);
            }
        }
        
        itemIntoHindi = new JMenuItem("<html><span style=\"font-family:'Monospace'\">"+ new Locale("hi").getDisplayLanguage(new Locale("hi")) +"</span>...</html>", HelperUi.ICON_FLAG_HI);
        itemIntoArabic = new JMenuItem("<html><span style=\"font-family:'Monospace'\">"+ new Locale("ar").getDisplayLanguage(new Locale("ar")) +"</span>...</html>", HelperUi.ICON_FLAG_AR);
        itemIntoRussia = new JMenuItem(new Locale("ru").getDisplayLanguage(new Locale("ru")) +"...", HelperUi.ICON_FLAG_RU);
        itemIntoChina = new JMenuItem("<html><span style=\"font-family:'Monospace'\">"+ new Locale("zh").getDisplayLanguage(new Locale("zh")) +"</span>...</html>", HelperUi.ICON_FLAG_ZH);
        itemIntoFrench = new JMenuItem(new Locale("fr").getDisplayLanguage(new Locale("fr")) +"...", HelperUi.ICON_FLAG_FR);
        itemIntoTurkish = new JMenuItem(new Locale("tr").getDisplayLanguage(new Locale("tr")) +"...", HelperUi.ICON_FLAG_TR);
        itemIntoCzech = new JMenuItem(new Locale("cs").getDisplayLanguage(new Locale("cs")) +"...", HelperUi.ICON_FLAG_CS);
        itemIntoDutch = new JMenuItem(new Locale("nl").getDisplayLanguage(new Locale("nl")) +"...", HelperUi.ICON_FLAG_NL);
        itemIntoGerman = new JMenuItem(new Locale("de").getDisplayLanguage(new Locale("de")) +"...", HelperUi.ICON_FLAG_DE);
        itemIntoIndonesian = new JMenuItem(new Locale("in", "ID").getDisplayLanguage(new Locale("in", "ID")) +"...", HelperUi.ICON_FLAG_IN_ID);
        itemIntoItalian = new JMenuItem(new Locale("it").getDisplayLanguage(new Locale("it")) +"...", HelperUi.ICON_FLAG_IT);
        itemIntoSpanish = new JMenuItem(new Locale("es").getDisplayLanguage(new Locale("es")) +"...", HelperUi.ICON_FLAG_ES);
        itemIntoPortuguese = new JMenuItem(new Locale("pt").getDisplayLanguage(new Locale("pt")) +"...", HelperUi.ICON_FLAG_PT);
        itemIntoOther = new JMenuItem("another language...");
        
        itemIntoArabic.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        menuI18nContribution.add(itemIntoHindi);
        menuI18nContribution.add(itemIntoArabic);
        menuI18nContribution.add(itemIntoRussia);
        menuI18nContribution.add(itemIntoChina);
        menuI18nContribution.add(itemIntoFrench);
        menuI18nContribution.add(itemIntoTurkish);
        menuI18nContribution.add(itemIntoCzech);
        menuI18nContribution.add(itemIntoGerman);
        menuI18nContribution.add(itemIntoDutch);
        menuI18nContribution.add(itemIntoIndonesian);
        menuI18nContribution.add(itemIntoItalian);
        menuI18nContribution.add(itemIntoSpanish);
        menuI18nContribution.add(itemIntoPortuguese);
        menuI18nContribution.add(new JSeparator());
        menuI18nContribution.add(itemIntoOther);
        
        itemIntoHindi.addActionListener(new ActionTranslate(Language.HI));
        itemIntoArabic.addActionListener(new ActionTranslate(Language.AR));
        itemIntoRussia.addActionListener(new ActionTranslate(Language.RU));
        itemIntoChina.addActionListener(new ActionTranslate(Language.ZH));
        itemIntoFrench.addActionListener(new ActionTranslate(Language.FR));
        itemIntoTurkish.addActionListener(new ActionTranslate(Language.TR));
        itemIntoCzech.addActionListener(new ActionTranslate(Language.CS));
        itemIntoGerman.addActionListener(new ActionTranslate(Language.DE));
        itemIntoDutch.addActionListener(new ActionTranslate(Language.NL));
        itemIntoIndonesian.addActionListener(new ActionTranslate(Language.IN_ID));
        itemIntoItalian.addActionListener(new ActionTranslate(Language.IT));
        itemIntoSpanish.addActionListener(new ActionTranslate(Language.ES));
        itemIntoPortuguese.addActionListener(new ActionTranslate(Language.PT));
        itemIntoOther.addActionListener(new ActionTranslate(Language.OT));
        
        menuWindows.add(menuTranslation);
        menuWindows.add(new JSeparator());
        
        menuView = new JMenu(I18n.valueByKey("MENUBAR_VIEW"));
        I18n.addComponentForKey("MENUBAR_VIEW", menuView);
        menuView.setMnemonic('V');
        
        JMenuItem database = new JMenuItem(I18n.valueByKey("DATABASE_TAB"), HelperUi.ICON_DATABASE_SERVER);
        I18n.addComponentForKey("DATABASE_TAB", database);
        menuView.add(database);
        
        JMenuItem adminPage = new JMenuItem(I18n.valueByKey("ADMINPAGE_TAB"), HelperUi.ICON_ADMIN_SERVER);
        I18n.addComponentForKey("ADMINPAGE_TAB", adminPage);
        menuView.add(adminPage);
        
        JMenuItem file = new JMenuItem(I18n.valueByKey("FILE_TAB"), HelperUi.ICON_FILE_SERVER);
        I18n.addComponentForKey("FILE_TAB", file);
        menuView.add(file);
        
        JMenuItem webshell = new JMenuItem(I18n.valueByKey("WEBSHELL_TAB"), HelperUi.ICON_SHELL_SERVER);
        I18n.addComponentForKey("WEBSHELL_TAB", webshell);
        menuView.add(webshell);
        
        JMenuItem sqlshell = new JMenuItem(I18n.valueByKey("SQLSHELL_TAB"), HelperUi.ICON_SHELL_SERVER);
        I18n.addComponentForKey("SQLSHELL_TAB", sqlshell);
        menuView.add(sqlshell);
        
        JMenuItem upload = new JMenuItem(I18n.valueByKey("UPLOAD_TAB"), HelperUi.ICON_UPLOAD);
        I18n.addComponentForKey("UPLOAD_TAB", upload);
        menuView.add(upload);
        
        JMenuItem bruteforce = new JMenuItem(I18n.valueByKey("BRUTEFORCE_TAB"), HelperUi.ICON_BRUTER);
        I18n.addComponentForKey("BRUTEFORCE_TAB", bruteforce);
        menuView.add(bruteforce);
        
        JMenuItem coder = new JMenuItem(I18n.valueByKey("CODER_TAB"), HelperUi.ICON_CODER);
        I18n.addComponentForKey("CODER_TAB", coder);
        menuView.add(coder);
        
        JMenuItem scanList = new JMenuItem(I18n.valueByKey("SCANLIST_TAB"), HelperUi.ICON_SCANLIST);
        I18n.addComponentForKey("SCANLIST_TAB", scanList);
        menuView.add(scanList);
        menuWindows.add(menuView);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        JMenu menuPanel = new JMenu(I18n.valueByKey("MENUBAR_PANEL"));
        I18n.addComponentForKey("MENUBAR_PANEL", menuPanel);
        menuView.setMnemonic('V');
        
        chunkMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_CHUNK_LABEL"), 
            HelperUi.ICON_CHUNK, 
            prefs.getBoolean(HelperUi.CHUNK_VISIBLE, true)
        );
        I18n.addComponentForKey("CONSOLE_CHUNK_LABEL", chunkMenu);
        menuPanel.add(chunkMenu);
        
        binaryMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_BINARY_LABEL"), 
            HelperUi.ICON_BINARY, 
            prefs.getBoolean(HelperUi.BINARY_VISIBLE, true)
        );
        I18n.addComponentForKey("CONSOLE_BINARY_LABEL", binaryMenu);
        menuPanel.add(binaryMenu);
        
        networkMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_NETWORK_LABEL"), 
            HelperUi.ICON_HEADER, 
            prefs.getBoolean(HelperUi.NETWORK_VISIBLE, true)
        );
        I18n.addComponentForKey("CONSOLE_NETWORK_LABEL", networkMenu);
        menuPanel.add(networkMenu);
        
        javaDebugMenu = new JCheckBoxMenuItem(
            I18n.valueByKey("CONSOLE_JAVA_LABEL"), 
            HelperUi.ICON_CUP, 
            prefs.getBoolean(HelperUi.JAVA_VISIBLE, false)
        );
        I18n.addComponentForKey("CONSOLE_JAVA_LABEL", javaDebugMenu);

        for (JCheckBoxMenuItem menuItem: new JCheckBoxMenuItem[]{chunkMenu, binaryMenu, networkMenu, javaDebugMenu}) {
            menuItem.setUI(
                new BasicCheckBoxMenuItemUI() {
                    @Override
                    protected void doClick(MenuSelectionManager msm) {
                        this.menuItem.doClick(0);
                    }
                }
            );
        }

        chunkMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chunkMenu.isSelected()) {
                    MediatorGui.panelConsoles().insertChunkTab();
                } else {
                    // Works even with i18n label
                    MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab("Chunk"));
                }
            }
        });
        
        binaryMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (binaryMenu.isSelected()) {
                    MediatorGui.panelConsoles().insertBinaryTab();
                } else {
                    // Works even with i18n label
                    MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab("Boolean"));
                }
            }
        });
        
        networkMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (networkMenu.isSelected()) {
                    MediatorGui.panelConsoles().insertNetworkTab();
                } else {
                    // Works even with i18n label
                    MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab("Network"));
                }
            }
        });
        
        javaDebugMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (javaDebugMenu.isSelected()) {
                    MediatorGui.panelConsoles().insertJavaDebugTab();
                } else {
                    // Works even with i18n label
                    MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab("Java"));
                }
            }
        });

        menuPanel.add(javaDebugMenu);
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

        for (int position = 0 ; position < menuView.getItemCount() ; position++) {
            final JMenuItem itemMenu = menuView.getItem(position);
            final int positionFinal = position;
            itemMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    MediatorGui.tabManagers().setSelectedIndex(positionFinal);
                }
            });
        }

        JMenuItem preferences = new JMenuItem(I18n.valueByKey("MENUBAR_PREFERENCES"), 'P');
        preferences.setIcon(HelperUi.ICON_EMPTY);
        I18n.addComponentForKey("MENUBAR_PREFERENCES", preferences);
        
        // Render the Preferences dialog behind scene
//        final DialogPreferences dialoguePreferences = new DialogPreferences();
        preferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                for (int i = 0; i < MediatorGui.tabResults().getTabCount() ; i++) {
                    if (MediatorGui.tabResults().getTitleAt(i).equals("Preferences")) {
                        MediatorGui.tabResults().setSelectedIndex(i);
                        return;
                    }
                }
                
                CreateTab.initializeSplitOrientation();
                
                AdjustmentListener singleItemScroll = new AdjustmentListener() {
                    @Override
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        // The user scrolled the List (using the bar, mouse wheel or something else):
                        if (e.getAdjustmentType() == AdjustmentEvent.TRACK){
                            // Jump to the next "block" (which is a row".
                            e.getAdjustable().setBlockIncrement(100);
                            e.getAdjustable().setUnitIncrement(100);
                        }
                    }
                };

//                LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, dialoguePreferences.mainPanel2);
                LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, new PanelPreferences());
                scroller.scrollPane.getVerticalScrollBar().addAdjustmentListener(singleItemScroll);
                
                MediatorGui.tabResults().addTab("Preferences", scroller);

                // Focus on the new tab
                MediatorGui.tabResults().setSelectedComponent(scroller);

                // Create a custom tab header with close button
                TabHeader header = new TabHeader("Preferences ", HelperUi.ICON_FILE_SERVER);

                MediatorGui.tabResults().setToolTipTextAt(MediatorGui.tabResults().indexOfComponent(scroller), "Preferences");

                // Apply the custom header to the tab
                MediatorGui.tabResults().setTabComponentAt(MediatorGui.tabResults().indexOfComponent(scroller), header);
            }
        });
        menuWindows.add(preferences);
        
        JMenuItem sqlEngine = new JMenuItem("SQL engine");
        
        // Render the Preferences dialog behind scene
        sqlEngine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                for (int i = 0; i < MediatorGui.tabResults().getTabCount() ; i++) {
                    if (MediatorGui.tabResults().getTitleAt(i).equals("SQL engine")) {
                        MediatorGui.tabResults().setSelectedIndex(i);
                        return;
                    }
                }
                
                CreateTab.initializeSplitOrientation();

                SqlEngine panelSqlEngine = new SqlEngine();
                
                MediatorGui.tabResults().addTab("SQL engine", panelSqlEngine);

                // Focus on the new tab
                MediatorGui.tabResults().setSelectedComponent(panelSqlEngine);

                // Create a custom tab header with close button
                TabHeader header = new TabHeader("SQL engine ", HelperUi.ICON_FILE_SERVER);

                MediatorGui.tabResults().setToolTipTextAt(MediatorGui.tabResults().indexOfComponent(panelSqlEngine), "SQL engine");

                // Apply the custom header to the tab
                MediatorGui.tabResults().setTabComponentAt(MediatorGui.tabResults().indexOfComponent(panelSqlEngine), header);
            }
        });
        menuWindows.add(sqlEngine);

        // Help Menu > about
        JMenu menuHelp = new JMenu(I18n.valueByKey("MENUBAR_HELP"));
        menuHelp.setMnemonic('H');
        I18n.addComponentForKey("MENUBAR_HELP", menuHelp);
        JMenuItem itemHelp = new JMenuItem(I18n.valueByKey("MENUBAR_HELP_ABOUT"), 'A');
        itemHelp.setIcon(HelperUi.ICON_EMPTY);
        I18n.addComponentForKey("MENUBAR_HELP_ABOUT", itemHelp);
        JMenuItem itemUpdate = new JMenuItem(I18n.valueByKey("MENUBAR_HELP_UPDATE"), 'U');
        itemUpdate.setIcon(HelperUi.ICON_EMPTY);
        I18n.addComponentForKey("MENUBAR_HELP_UPDATE", itemUpdate);

        // Render the About dialog behind scene
        final DialogAbout aboutDiag = new DialogAbout();
        itemHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Center the dialog
                if (!aboutDiag.isVisible()) {
                    aboutDiag.reinit();
                    // needed here for button focus
                    aboutDiag.setVisible(true);
                    aboutDiag.requestButtonFocus();
                }
                aboutDiag.setVisible(true);
            }
        });
        itemUpdate.addActionListener(new ActionCheckUpdate());
        
        menuHelp.add(itemUpdate);
        menuHelp.add(new JSeparator());
        menuHelp.add(itemHelp);

        // Help Menu > about
        JMenu menuCommunity = new JMenu("Community");
        menuHelp.setMnemonic('C');
        JMenuItem itemReportIssue = new JMenuItem(I18n.valueByKey("MENUBAR_COMMUNITY_REPORTISSUE"), 'R');
        itemReportIssue.setIcon(HelperUi.ICON_EMPTY);
        I18n.addComponentForKey("MENUBAR_COMMUNITY_REPORTISSUE", itemReportIssue);
        itemReportIssue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JPanel panel = new JPanel(new BorderLayout());
                final JTextArea textarea = new JPopupTextArea(new JTextArea()).getProxy();
                textarea.setText("Username: -\n\nSubject: -\n\nDescription: -");
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

                if (!"".equals(textarea.getText()) && result == JOptionPane.YES_OPTION) {
                    GitUtil.sendReport(textarea.getText(), ShowOnConsole.YES, "Report");
                }
            }
        });
        
        menuCommunity.add(menuI18nContribution);
        menuCommunity.add(new JSeparator());
        menuCommunity.add(itemReportIssue);
        
        // Make menubar
        this.add(menuFile);
        this.add(menuEdit);
        this.add(menuCommunity);
        this.add(menuWindows);
        this.add(menuHelp);
    }
    
    public void switchLocale(Locale newLocale) {
        switchLocale(I18n.getLocaleDefault(), newLocale);
    }
    
    public void switchLocale(Locale oldLocale, Locale newLocale) {
        I18n.setLocaleDefault(ResourceBundle.getBundle("com.jsql.i18n.jsql", newLocale));
        
        for (String key: I18n.keys()) {
            for (Object componentSwing: I18n.componentsByKey(key)) {
                Class<?> classComponent = componentSwing.getClass();
                try {
                    if (componentSwing instanceof JTextFieldPlaceholder) {
                        Method setPlaceholderText = classComponent.getMethod("setPlaceholderText", new Class<?>[]{String.class});
                        setPlaceholderText.invoke(componentSwing, I18n.valueByKey(key));
                    } else {
                        Method methodSetText = classComponent.getMethod("setText", new Class<?>[]{String.class});
                        if (newLocale.getLanguage() == new Locale("zh").getLanguage()) {
                            methodSetText.invoke(componentSwing, "<html><span style=\"font-family:'Monospace'\">"+ I18n.valueByKey(key) +"</span></html>");
                        } else {
                            methodSetText.invoke(componentSwing, I18n.valueByKey(key));
                        }
                    }
                } catch (
                    NoSuchMethodException | SecurityException | IllegalAccessException | 
                    IllegalArgumentException | InvocationTargetException e
                ) {
                    LOGGER.warn("Reflection for "+ key +" failed while switching locale", e);
                }
            }
        }
        
        ComponentOrientation componentOrientation = ComponentOrientation.getOrientation(I18n.getLocaleDefault());
        MediatorGui.frame().applyComponentOrientation(componentOrientation);
        itemArab.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        itemEnglish.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemChinese.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemRussian.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemFrench.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemCzech.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemDutch.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemGerman.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemTurkish.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIndonesian.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemItalian.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemSpanish.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemPortuguese.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        
        itemIntoArabic.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        itemIntoHindi.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoRussia.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoChina.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoFrench.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoTurkish.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoCzech.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoGerman.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoDutch.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoIndonesian.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoItalian.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoSpanish.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoPortuguese.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        itemIntoOther.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        
        if (ComponentOrientation.getOrientation(oldLocale) != ComponentOrientation.getOrientation(newLocale)) {
            Component c1 = MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.getLeftComponent();
            Component c2 = MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.getRightComponent();
            
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setLeftComponent(null);
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setRightComponent(null);
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setLeftComponent(c2);
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setRightComponent(c1);
            
            MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.setDividerLocation(
                MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.getWidth() -
                MediatorGui.frame().splitHorizontalTopBottom.splitVerticalLeftRight.getDividerLocation()
            );
        }
        
        MediatorGui.tabResults().setComponentOrientation(ComponentOrientation.getOrientation(newLocale));
        
        // Fix glitches on Linux
        MediatorGui.frame().revalidate();
    }
    
}
