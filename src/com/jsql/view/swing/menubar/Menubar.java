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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.injection.InjectionModel;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.MediatorGUI;
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
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(Menubar.class);

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
        JMenuItem scanList = new JMenuItem(I18n.SCANLIST, HelperGUI.SCANLIST_ICON);
        menuView.add(scanList);
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

        for (JCheckBoxMenuItem i: new JCheckBoxMenuItem[]{chunkMenu, binaryMenu, networkMenu, javaDebugMenu}) {
            i.setUI(new BasicCheckBoxMenuItemUI() {
                @Override
                protected void doClick(MenuSelectionManager msm) {
                    menuItem.doClick(0);
                }
            });
        }

        chunkMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chunkMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertChunkTab();
                } else {
                    // Works even with i18n label
                    MediatorGUI.bottom().remove(MediatorGUI.bottom().indexOfTab("Chunk"));
                }
            }
        });
        binaryMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (binaryMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertBinaryTab();
                } else {
                    // Works even with i18n label
                    MediatorGUI.bottom().remove(MediatorGUI.bottom().indexOfTab("Binary"));
                }
            }
        });
        networkMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (networkMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertNetworkTab();
                } else {
                    // Works even with i18n label
                    MediatorGUI.bottom().remove(MediatorGUI.bottom().indexOfTab("Network"));
                }
            }
        });
        javaDebugMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (javaDebugMenu.isSelected()) {
                    MediatorGUI.bottomPanel().insertJavaDebugTab();
                } else {
                    // Works even with i18n label
                    MediatorGUI.bottom().remove(MediatorGUI.bottom().indexOfTab("Java"));
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
        scanList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9, ActionEvent.CTRL_MASK));

        final Map<JMenuItem, Integer> p = new HashMap<JMenuItem, Integer>();
        p.put(database, 0);
        p.put(adminPage, 1);
        p.put(file, 2);
        p.put(webshell, 3);
        p.put(sqlshell, 4);
        p.put(upload, 5);
        p.put(bruteforce, 6);
        p.put(coder, 7);
        p.put(scanList, 8);
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
        JMenuItem itemReportIssue = new JMenuItem(I18n.ITEM_REPORTISSUE, 'R');
        itemReportIssue.setIcon(HelperGUI.EMPTY);

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
                textarea.setText("Reporter: Anonymous\n\nSubject: \n\nDescription: ");
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
                    MediatorGUI.gui(),
                    panel,
                    "Report an issue or a bug",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Report", I18n.CANCEL},
                    I18n.CANCEL
                );

                if (!"".equals(textarea.getText()) && result == JOptionPane.YES_OPTION) {
                    // Test if proxy is available then apply settings
                    if (MediatorGUI.model().isProxyfied && !"".equals(MediatorGUI.model().proxyAddress) && !"".equals(MediatorGUI.model().proxyPort)) {
                        try {
                            LOGGER.info("Testing proxy...");
                            new Socket(MediatorGUI.model().proxyAddress, Integer.parseInt(MediatorGUI.model().proxyPort)).close();
                        } catch (Exception e) {
                            LOGGER.warn("Proxy connection failed: " 
                                    + MediatorGUI.model().proxyAddress + ":" + MediatorGUI.model().proxyPort
                                    + "\nVerify your proxy informations or disable proxy setting.", e);
                            return;
                        }
                        LOGGER.trace("Proxy is responding.");
                    }

                    HttpURLConnection connection = null;
                    try {
                        LOGGER.info("Sending report...");
                        URL githubUrl = new URL("https://api.github.com/repos/ron190/jsql-injection/issues");
                        connection = (HttpURLConnection) githubUrl.openConnection();
                        connection.setDefaultUseCaches(false);
                        connection.setUseCaches(false);
                        connection.setRequestProperty("Pragma", "no-cache");
                        connection.setRequestProperty("Cache-Control", "no-cache");
                        connection.setRequestProperty("Expires", "-1");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Authorization", "token f96eec3e1d02ed5139da531b9a7495e40c1a3a83");
                        connection.setReadTimeout(15000);
                        connection.setConnectTimeout(15000);
                        connection.setDoOutput(true);
    
                        DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
                        dataOut.writeBytes(
                                "{\"title\": \"Report\", \"body\": \""+ 
                                textarea.getText().replaceAll("(\\r|\\n)+", "\\\\n") +"\"}");
                        dataOut.flush();
                        dataOut.close();
                    } catch (IOException e) {
                        LOGGER.warn("Error during JSON connection " + e.getMessage(), e);
                    }
    
                    // Request the web page to the server
                    String line, pageSource = "";
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            pageSource += line + "\r\n";
                        }
                        reader.close();
                        
                        LOGGER.debug("Report sent successfully.");
                        System.out.println(pageSource);
                    } catch (MalformedURLException e) {
                        LOGGER.warn("Malformed URL " + e.getMessage(), e);
                    } catch (IOException e) {
                        /* lot of timeout in local use */
                        LOGGER.warn("Read error " + e.getMessage(), e);
                    }
                    
                }
            }
        });
        
        menuHelp.add(itemReportIssue);
        menuHelp.add(new JSeparator());
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
