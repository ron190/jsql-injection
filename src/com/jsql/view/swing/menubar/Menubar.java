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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

import com.jsql.i18n.I18n;
import com.jsql.model.injection.InjectionModel;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.action.ActionSaveTab;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.dialog.DialogPreference;
import com.jsql.view.swing.table.PanelTable;

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
        menuFile.setMnemonic('F');

        JMenuItem itemNewWindows = new JMenuItem(new ActionNewWindow());

        JMenuItem itemSave = new JMenuItem(new ActionSaveTab());

        JMenuItem itemExit = new JMenuItem(I18n.ITEM_EXIT, 'x');
        itemExit.setIcon(HelperGUI.EMPTY);
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MediatorGUI.gui().dispose();
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
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItem(I18n.COPY, 'C');
        itemCopy.setIcon(HelperGUI.EMPTY);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MediatorGUI.right().getSelectedComponent() instanceof PanelTable) {
                    ((PanelTable) MediatorGUI.right().getSelectedComponent()).copyTable();
                } else if (MediatorGUI.right().getSelectedComponent() instanceof JScrollPane) {
                    ((JTextArea) ((JScrollPane) MediatorGUI.right().getSelectedComponent()).getViewport().getView()).copy();
                }
            }
        });

        JMenuItem itemSelectAll = new JMenuItem(I18n.SELECT_ALL, 'A');
        itemSelectAll.setIcon(HelperGUI.EMPTY);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MediatorGUI.right().getSelectedComponent() instanceof PanelTable) {
                    ((PanelTable) MediatorGUI.right().getSelectedComponent()).selectTable();
                // Textarea need focus to select all
                } else if (MediatorGUI.right().getSelectedComponent() instanceof JScrollPane) {
                    ((JScrollPane) MediatorGUI.right().getSelectedComponent()).getViewport().getView().requestFocusInWindow();
                    ((JTextArea) ((JScrollPane) MediatorGUI.right().getSelectedComponent()).getViewport().getView()).selectAll();
                }
            }
        });

        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);

        // Window Menu > Preferences
        JMenu menuTools = new JMenu(I18n.MENU_WINDOWS);
        menuTools.setMnemonic('W');
        JMenuItem preferences = new JMenuItem(I18n.MENU_PREFERENCES, 'P');
        preferences.setIcon(HelperGUI.EMPTY);

        JMenu menuView = new JMenu(I18n.MENU_VIEW);
        menuView.setMnemonic('V');
        JMenuItem database = new JMenuItem(I18n.DATABASE, HelperGUI.DATABASE_SERVER_ICON);
        menuView.add(database);
        JMenuItem adminPage = new JMenuItem(I18n.ADMINPAGE, HelperGUI.ADMIN_SERVER_ICON);
        menuView.add(adminPage);
        JMenuItem file = new JMenuItem(I18n.FILE, HelperGUI.FILE_SERVER_ICON);
        menuView.add(file);
        JMenuItem webshell = new JMenuItem(I18n.WEBSHELL, HelperGUI.SHELL_SERVER_ICON);
        menuView.add(webshell);
        JMenuItem sqlshell = new JMenuItem(I18n.SQLSHELL, HelperGUI.SHELL_SERVER_ICON);
        menuView.add(sqlshell);
        JMenuItem upload = new JMenuItem(I18n.UPLOAD, HelperGUI.UPLOAD_ICON);
        menuView.add(upload);
        JMenuItem bruteforce = new JMenuItem(I18n.BRUTEFORCE, HelperGUI.BRUTER_ICON);
        menuView.add(bruteforce);
        JMenuItem coder = new JMenuItem(I18n.CODER, HelperGUI.CODER_ICON);
        menuView.add(coder);
        menuTools.add(menuView);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        JMenu menuPanel = new JMenu(I18n.MENU_PANEL);
        menuView.setMnemonic('V');
        chunkMenu = new JCheckBoxMenuItem(I18n.CHUNK_TAB_LABEL, new ImageIcon(getClass().getResource("/com/jsql/view/swing/images/chunk.gif")), prefs.getBoolean(HelperGUI.CHUNK_VISIBLE, true));
        menuPanel.add(chunkMenu);
        binaryMenu = new JCheckBoxMenuItem(I18n.BINARY_TAB_LABEL, new ImageIcon(getClass().getResource("/com/jsql/view/swing/images/binary.gif")), prefs.getBoolean(HelperGUI.BINARY_VISIBLE, true));
        menuPanel.add(binaryMenu);
        networkMenu = new JCheckBoxMenuItem(I18n.NETWORK_TAB_LABEL, new ImageIcon(getClass().getResource("/com/jsql/view/swing/images/header.gif")), prefs.getBoolean(HelperGUI.NETWORK_VISIBLE, true));
        menuPanel.add(networkMenu);
        javaDebugMenu = new JCheckBoxMenuItem(I18n.JAVA_TAB_LABEL, new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/cup.png")), prefs.getBoolean(HelperGUI.JAVA_VISIBLE, false));

        class StayOpenCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
            @Override
            protected void doClick(MenuSelectionManager msm) {
                menuItem.doClick(0);
            }
        }

        for (JCheckBoxMenuItem i: new JCheckBoxMenuItem[]{chunkMenu, binaryMenu, networkMenu, javaDebugMenu}) {
            i.setUI(new StayOpenCheckBoxMenuItemUI());
        }

        chunkMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chunkMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertChunkTab();
                } else {
                    MediatorGUI.bottom().remove(MediatorGUI.bottomPanel().chunkTab.getParent().getParent());
                }
            }
        });
        binaryMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (binaryMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertBinaryTab();
                } else {
                    MediatorGUI.bottom().remove(MediatorGUI.bottomPanel().binaryTab.getParent().getParent());
                }
            }
        });
        networkMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (networkMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertNetworkTab();
                } else {
                    MediatorGUI.bottom().remove(MediatorGUI.bottomPanel().network);
                }
            }
        });
        javaDebugMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (javaDebugMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertJavaDebugTab();
                } else {
                    MediatorGUI.bottom().remove(MediatorGUI.bottomPanel().javaTab.getParent().getParent());
                }
            }
        });

        menuPanel.add(javaDebugMenu);
        menuTools.add(menuPanel);
        menuTools.add(new JSeparator());

        database.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        adminPage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        file.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
        webshell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
        sqlshell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));
        upload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.CTRL_MASK));
        bruteforce.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, ActionEvent.CTRL_MASK));
        coder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, ActionEvent.CTRL_MASK));

        final Map<JMenuItem, Integer> p = new HashMap<JMenuItem, Integer>();
        p.put(database, 0);
        p.put(adminPage, 1);
        p.put(file, 2);
        p.put(webshell, 3);
        p.put(sqlshell, 4);
        p.put(bruteforce, 5);
        p.put(coder, 6);
        for (final JMenuItem m: p.keySet()) {
            m.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    MediatorGUI.left().setSelectedIndex(p.get(m));
                }
            });
        }

        // Render the Preferences dialog behind scene
        final DialogPreference prefDiag = new DialogPreference();
        preferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Center the dialog
                if (!prefDiag.isVisible()) {
                    prefDiag.setLocationRelativeTo(MediatorGUI.gui());
                    // needed here for button focus
                    prefDiag.setVisible(true);
                    prefDiag.requestButtonFocus();
                }
                prefDiag.setVisible(true);
            }
        });
        menuTools.add(preferences);

        // Help Menu > about
        JMenu menuHelp = new JMenu(I18n.MENU_HELP);
        menuHelp.setMnemonic('H');
        JMenuItem itemHelp = new JMenuItem(I18n.ITEM_ABOUT, 'A');
        itemHelp.setIcon(HelperGUI.EMPTY);
        JMenuItem itemUpdate = new JMenuItem(I18n.ITEM_UPDATE, 'U');
        itemUpdate.setIcon(HelperGUI.EMPTY);

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

        // Make menubar
        this.add(menuFile);
        this.add(menuEdit);
        this.add(menuTools);
        this.add(menuHelp);
    }
}
