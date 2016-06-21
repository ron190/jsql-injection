/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

import com.jsql.i18n.I18n;
import com.jsql.model.injection.InjectionModel;
import com.jsql.util.GitUtil;
import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.action.ActionSaveTab;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.dialog.DialogPreference;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;

/**
 * Application main menubar.
 */
@SuppressWarnings("serial")
public class Menubar extends JMenuBar {
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

    /**
     * Create a menubar on main frame.
     */
    public Menubar() {
        // File Menu > save tab | exit
        JMenu menuFile = new JMenu(I18n.MENU_FILE);
        I18n.components.get("MENU_FILE").add(menuFile);
        menuFile.setMnemonic('F');

        JMenuItem itemNewWindows = new JMenuItem(new ActionNewWindow());

        JMenuItem itemSave = new JMenuItem(new ActionSaveTab());

        JMenuItem itemExit = new JMenuItem(I18n.ITEM_EXIT, 'x');
        I18n.components.get("ITEM_EXIT").add(itemExit);
        itemExit.setIcon(HelperGui.EMPTY);
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
        JMenu menuEdit = new JMenu(I18n.MENU_EDIT);
        I18n.components.get("MENU_EDIT").add(menuEdit);
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItem(I18n.COPY, 'C');
        I18n.components.get("COPY").add(itemCopy);
        itemCopy.setIcon(HelperGui.EMPTY);
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

        JMenuItem itemSelectAll = new JMenuItem(I18n.SELECT_ALL, 'A');
        I18n.components.get("SELECT_ALL").add(itemSelectAll);
        itemSelectAll.setIcon(HelperGui.EMPTY);
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
        JMenu menuWindows = new JMenu(I18n.MENU_WINDOWS);
        I18n.components.get("MENU_WINDOWS").add(menuWindows);
        menuWindows.setMnemonic('W');

        JMenu menuTranslation = new JMenu("Language");
        JMenuItem itemEnglish = new JRadioButtonMenuItem(
            "English", 
            HelperGui.FLAG_US, 
            ArrayUtils.contains(new Locale[]{Locale.US, Locale.UK}, Locale.getDefault())
        );
        itemEnglish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                I18n.CURRENT_LOCALE = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.ROOT);
                switchLocale();                
            }
        });
        menuTranslation.add(itemEnglish);
        JMenuItem itemFrench = new JRadioButtonMenuItem("French", HelperGui.FLAG_FR, Locale.getDefault() == Locale.FRENCH);
        itemFrench.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                I18n.CURRENT_LOCALE = ResourceBundle.getBundle("com.jsql.i18n.jsql", Locale.FRENCH);
                switchLocale();                
            }
        });
        menuTranslation.add(itemFrench);
        
        ButtonGroup groupRadioLanguage = new ButtonGroup();
        groupRadioLanguage.add(itemEnglish);
        groupRadioLanguage.add(itemFrench);
        
        JMenu menuI18nContribution = new JMenu("I help translating");
        JMenuItem itemRussia = new JMenuItem("Russian", HelperGui.FLAG_RU);
        menuI18nContribution.add(itemRussia);
        JMenuItem itemChina = new JMenuItem("Chinese", HelperGui.FLAG_CN);
        menuI18nContribution.add(itemChina);
        JMenuItem itemTurkey = new JMenuItem("Turkish", HelperGui.FLAG_TR);
        menuI18nContribution.add(itemTurkey);
        
        menuTranslation.add(new JSeparator());
        menuTranslation.add(menuI18nContribution);
        
        menuWindows.add(menuTranslation);
        menuWindows.add(new JSeparator());
        
        JMenu menuView = new JMenu(I18n.MENU_VIEW);
        I18n.components.get("MENU_VIEW").add(menuView);
        menuView.setMnemonic('V');
        JMenuItem database = new JMenuItem(I18n.DATABASE, HelperGui.DATABASE_SERVER_ICON);
        I18n.components.get("DATABASE").add(database);
        menuView.add(database);
        JMenuItem adminPage = new JMenuItem(I18n.ADMINPAGE, HelperGui.ADMIN_SERVER_ICON);
        I18n.components.get("ADMINPAGE").add(adminPage);
        menuView.add(adminPage);
        JMenuItem file = new JMenuItem(I18n.FILE, HelperGui.FILE_SERVER_ICON);
        I18n.components.get("FILE").add(file);
        menuView.add(file);
        JMenuItem webshell = new JMenuItem(I18n.WEBSHELL, HelperGui.SHELL_SERVER_ICON);
        I18n.components.get("WEBSHELL").add(webshell);
        menuView.add(webshell);
        JMenuItem sqlshell = new JMenuItem(I18n.SQLSHELL, HelperGui.SHELL_SERVER_ICON);
        I18n.components.get("SQLSHELL").add(sqlshell);
        menuView.add(sqlshell);
        JMenuItem upload = new JMenuItem(I18n.UPLOAD, HelperGui.UPLOAD_ICON);
        I18n.components.get("UPLOAD").add(upload);
        menuView.add(upload);
        JMenuItem bruteforce = new JMenuItem(I18n.BRUTEFORCE, HelperGui.BRUTER_ICON);
        I18n.components.get("BRUTEFORCE").add(bruteforce);
        menuView.add(bruteforce);
        JMenuItem coder = new JMenuItem(I18n.CODER, HelperGui.CODER_ICON);
        I18n.components.get("CODER").add(coder);
        menuView.add(coder);
        JMenuItem scanList = new JMenuItem(I18n.SCANLIST, HelperGui.SCANLIST_ICON);
        I18n.components.get("SCANLIST").add(scanList);
        menuView.add(scanList);
        menuWindows.add(menuView);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        JMenu menuPanel = new JMenu(I18n.MENU_PANEL);
        I18n.components.get("MENU_PANEL").add(menuPanel);
        menuView.setMnemonic('V');
        chunkMenu = new JCheckBoxMenuItem(
            I18n.CHUNK_TAB_LABEL, 
            HelperGui.CHUNK_ICON, 
            prefs.getBoolean(HelperGui.CHUNK_VISIBLE, true)
        );
        I18n.components.get("CHUNK_TAB_LABEL").add(chunkMenu);
        menuPanel.add(chunkMenu);
        binaryMenu = new JCheckBoxMenuItem(
            I18n.BINARY_TAB_LABEL, 
            HelperGui.BINARY_ICON, 
            prefs.getBoolean(HelperGui.BINARY_VISIBLE, true)
        );
        I18n.components.get("BINARY_TAB_LABEL").add(binaryMenu);
        menuPanel.add(binaryMenu);
        networkMenu = new JCheckBoxMenuItem(
            I18n.NETWORK_TAB_LABEL, 
            HelperGui.HEADER_ICON, 
            prefs.getBoolean(HelperGui.NETWORK_VISIBLE, true)
        );
        I18n.components.get("NETWORK_TAB_LABEL").add(networkMenu);
        menuPanel.add(networkMenu);
        javaDebugMenu = new JCheckBoxMenuItem(
            I18n.JAVA_TAB_LABEL, 
            HelperGui.CUP_ICON, 
            prefs.getBoolean(HelperGui.JAVA_VISIBLE, false)
        );
        I18n.components.get("JAVA_TAB_LABEL").add(javaDebugMenu);

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
                    MediatorGui.tabConsoles().remove(MediatorGui.tabConsoles().indexOfTab("Binary"));
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

        final Map<JMenuItem, Integer> mapMenuItem = new HashMap<>();
        mapMenuItem.put(database, 0);
        mapMenuItem.put(adminPage, 1);
        mapMenuItem.put(file, 2);
        mapMenuItem.put(webshell, 3);
        mapMenuItem.put(sqlshell, 4);
        mapMenuItem.put(upload, 5);
        mapMenuItem.put(bruteforce, 6);
        mapMenuItem.put(coder, 7);
        mapMenuItem.put(scanList, 8);
        for (final JMenuItem menuItem: mapMenuItem.keySet()) {
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    MediatorGui.tabManagers().setSelectedIndex(mapMenuItem.get(menuItem));
                }
            });
        }

        JMenuItem preferences = new JMenuItem(I18n.MENU_PREFERENCES, 'P');
        preferences.setIcon(HelperGui.EMPTY);
        I18n.components.get("MENU_PREFERENCES").add(preferences);
        
        // Render the Preferences dialog behind scene
        final DialogPreference prefDiag = new DialogPreference();
        preferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Center the dialog
                if (!prefDiag.isVisible()) {
                    prefDiag.setSize(prefDiag.width, prefDiag.height);
                    prefDiag.setLocationRelativeTo(MediatorGui.frame());
                    // needed here for button focus
                    prefDiag.setVisible(true);
                    prefDiag.requestButtonFocus();
                }
                prefDiag.setVisible(true);
            }
        });
        menuWindows.add(preferences);

        // Help Menu > about
        JMenu menuHelp = new JMenu(I18n.MENU_HELP);
        menuHelp.setMnemonic('H');
        I18n.components.get("MENU_HELP").add(menuHelp);
        JMenuItem itemHelp = new JMenuItem(I18n.ITEM_ABOUT, 'A');
        itemHelp.setIcon(HelperGui.EMPTY);
        I18n.components.get("ITEM_ABOUT").add(itemHelp);
        JMenuItem itemUpdate = new JMenuItem(I18n.ITEM_UPDATE, 'U');
        itemUpdate.setIcon(HelperGui.EMPTY);
        I18n.components.get("ITEM_UPDATE").add(itemUpdate);
        JMenuItem itemReportIssue = new JMenuItem(I18n.ITEM_REPORTISSUE, 'R');
        itemReportIssue.setIcon(HelperGui.EMPTY);
        I18n.components.get("ITEM_REPORTISSUE").add(itemReportIssue);

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
                    new String[]{"Report", I18n.CANCEL},
                    I18n.CANCEL
                );

                if (!"".equals(textarea.getText()) && result == JOptionPane.YES_OPTION) {
                    GitUtil.sendReport(textarea.getText());
                }
            }
        });
        
        menuHelp.add(itemReportIssue);
        menuHelp.add(itemUpdate);
        menuHelp.add(new JSeparator());
        menuHelp.add(itemHelp);

        // Make menubar
        this.add(menuFile);
        this.add(menuEdit);
        this.add(menuWindows);
        this.add(menuHelp);
    }
    
    public void switchLocale() {
        Class<?> cl = I18n.class;
        Field[] ct = cl.getFields();
        for (Field f: ct) {
            if (!f.getType().equals(String.class)) continue;
            for (Object o: I18n.components.get(f.getName())) {
                Class<?> cl2 = o.getClass();
                try {
                    Method ct2 = cl2.getMethod("setText", new Class<?>[]{String.class});
                    ct2.invoke(o, (String) I18n.CURRENT_LOCALE.getObject(f.getName()));
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
            
            try {
                f.set(null, (String) I18n.CURRENT_LOCALE.getObject(f.getName()));
            } catch (IllegalArgumentException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
    }
}
