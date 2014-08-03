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
package com.jsql.view.menubar;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.action.ActionHandler;
import com.jsql.view.action.SaveTabAction;
import com.jsql.view.dialog.About;
import com.jsql.view.dialog.Prefs;
import com.jsql.view.table.TablePanel;

/**
 * Software menubar.
 */
@SuppressWarnings("serial")
public class Menubar extends JMenuBar {
	
	public JCheckBoxMenuItem chunk;
	public JCheckBoxMenuItem binary;
	public JCheckBoxMenuItem network;
	public JCheckBoxMenuItem javaDebug;
    
    public Menubar(){
        // File Menu > save tab | exit
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');

        JMenuItem itemSave = new JMenuItem("Save Tab As...", 'S');
        itemSave.setIcon(GUITools.EMPTY);
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itemSave.addActionListener(new SaveTabAction());

        JMenuItem itemExit = new JMenuItem("Exit", 'x');
        itemExit.setIcon(GUITools.EMPTY);
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	GUIMediator.gui().dispose();
            }
        });
        
        ActionHandler.addShortcut(Menubar.this);

        menuFile.add(itemSave);
        menuFile.add(new JSeparator());
        menuFile.add(itemExit);

        // Edit Menu > copy | select all
        JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItem("Copy", 'C');
        itemCopy.setIcon(GUITools.EMPTY);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GUIMediator.right().getSelectedComponent() instanceof TablePanel)
                    ((TablePanel) GUIMediator.right().getSelectedComponent()).copyTable();
                else if(GUIMediator.right().getSelectedComponent() instanceof JScrollPane)
                    ((JTextArea) ((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport().getView()).copy();
            }
        });

        JMenuItem itemSelectAll = new JMenuItem("Select All", 'A');
        itemSelectAll.setIcon(GUITools.EMPTY);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GUIMediator.right().getSelectedComponent() instanceof TablePanel)
                    ((TablePanel) GUIMediator.right().getSelectedComponent()).selectTable();
                // Textarea need focus to select all
                else if(GUIMediator.right().getSelectedComponent() instanceof JScrollPane){
                    ((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport().getView().requestFocusInWindow();
                    ((JTextArea) ((JScrollPane) GUIMediator.right().getSelectedComponent()).getViewport().getView()).selectAll();
                }
            }
        });

        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);

        // Window Menu > Preferences
        JMenu menuTools = new JMenu("Windows");
        menuTools.setMnemonic('W');
        JMenuItem preferences = new JMenuItem("Preferences", 'P');
        preferences.setIcon(GUITools.EMPTY);

        JMenu menuView = new JMenu("Show View");
        menuView.setMnemonic('V');
        JMenuItem database = new JMenuItem("Database", GUITools.DATABASE_SERVER_ICON);
        menuView.add(database);
        JMenuItem adminPage = new JMenuItem("Admin page", GUITools.ADMIN_SERVER_ICON);
        menuView.add(adminPage);
        JMenuItem file = new JMenuItem("File", GUITools.FILE_SERVER_ICON);
        menuView.add(file);
        JMenuItem webshell = new JMenuItem("Web shell", GUITools.SHELL_SERVER_ICON);
        menuView.add(webshell);
        JMenuItem sqlshell = new JMenuItem("SQL shell", GUITools.SHELL_SERVER_ICON);
        menuView.add(sqlshell);
        JMenuItem bruteforce = new JMenuItem("Brute force", GUITools.BRUTER_ICON);
        menuView.add(bruteforce);
        JMenuItem coder = new JMenuItem("Coder", GUITools.CODER_ICON);
        menuView.add(coder);
        menuTools.add(menuView);

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        JMenu menuPanel = new JMenu("Show Panel");
        menuView.setMnemonic('V');
        chunk = new JCheckBoxMenuItem("Chunk", new ImageIcon(getClass().getResource("/com/jsql/view/images/chunk.gif")), prefs.getBoolean(GUITools.CHUNK_VISIBLE, true));
        menuPanel.add(chunk);
        binary = new JCheckBoxMenuItem("Binary", new ImageIcon(getClass().getResource("/com/jsql/view/images/binary.gif")), prefs.getBoolean(GUITools.BINARY_VISIBLE, true));
        menuPanel.add(binary);
        network = new JCheckBoxMenuItem("Network", new ImageIcon(getClass().getResource("/com/jsql/view/images/header.gif")), prefs.getBoolean(GUITools.NETWORK_VISIBLE, true));
        menuPanel.add(network);
        javaDebug = new JCheckBoxMenuItem("Java", new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/cup.png")), prefs.getBoolean(GUITools.JAVA_VISIBLE, false));
        
        class StayOpenCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
            @Override
            protected void doClick(MenuSelectionManager msm) {
                menuItem.doClick(0);
            }
        }
        
        for(JCheckBoxMenuItem i: new JCheckBoxMenuItem[]{chunk,binary,network,javaDebug}){
            i.setUI(new StayOpenCheckBoxMenuItemUI());
        }
        
        chunk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(chunk.isSelected())
                	GUIMediator.bottomPanel().insertChunkTab();
                else
                	GUIMediator.bottom().remove(GUIMediator.bottomPanel().chunks.getParent().getParent());
            }
        });
        binary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(binary.isSelected())
                	GUIMediator.bottomPanel().insertBinaryTab();
                else
                	GUIMediator.bottom().remove(GUIMediator.bottomPanel().binaryArea.getParent().getParent());
            }
        });
        network.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(network.isSelected()){
                	GUIMediator.bottomPanel().insertNetworkTab();
                }else
                	GUIMediator.bottom().remove(GUIMediator.bottomPanel().network);
            }
        });
        javaDebug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(javaDebug.isSelected())
                	GUIMediator.bottomPanel().insertJavaDebugTab();
                else
                	GUIMediator.bottom().remove(GUIMediator.bottomPanel().javaDebug.getParent().getParent());
            }
        });
        
        menuPanel.add(javaDebug);
        menuTools.add(menuPanel);
        menuTools.add(new JSeparator());
        
        database.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        adminPage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        file.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
        webshell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
        sqlshell.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));
        bruteforce.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.CTRL_MASK));
        coder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, ActionEvent.CTRL_MASK));

        final Map<JMenuItem, Integer> p = new HashMap<JMenuItem, Integer>();
        p.put(database, 0); p.put(adminPage, 1); p.put(file, 2); p.put(webshell, 3); p.put(sqlshell, 4); p.put(bruteforce, 5); p.put(coder, 6);
        for(final JMenuItem m: p.keySet()){
            m.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                	GUIMediator.left().setSelectedIndex(p.get(m));
                }
            });
        }

        // Render the Preferences dialog behind scene
        final Prefs prefDiag = new Prefs();
        preferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Center the dialog
                if(!prefDiag.isVisible()){
                    prefDiag.setLocationRelativeTo(GUIMediator.gui());
                    prefDiag.setVisible(true); // needed here for button focus
                    prefDiag.okButton.requestFocusInWindow();
                }
                prefDiag.setVisible(true);
            }
        });
        menuTools.add(preferences);

        // Help Menu > about
        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic('H');
        JMenuItem itemHelp = new JMenuItem("About jSQL Injection", 'A');
        itemHelp.setIcon(GUITools.EMPTY);
        JMenuItem itemUpdate = new JMenuItem("Check for Updates", 'U');
        itemUpdate.setIcon(GUITools.EMPTY);

        // Render the About dialog behind scene
        final About aboutDiag = new About();
        itemHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Center the dialog
                if(!aboutDiag.isVisible()){
                    aboutDiag.reinit();
                    aboutDiag.setVisible(true); // needed here for button focus
                    aboutDiag.close.requestFocusInWindow();
                }
                aboutDiag.setVisible(true);
            }
        });
        itemUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                        	InjectionModel.logger.info("Checking updates...");
                            URLConnection con = new URL("http://jsql-injection.googlecode.com/git/.version").openConnection();
                            con.setReadTimeout(60000);
                            con.setConnectTimeout(60000);

                            BufferedReader reader = new BufferedReader(new InputStreamReader( con.getInputStream() ));
                            String line, pageSource = "";
                            while( (line = reader.readLine()) != null ) pageSource += line+"\n";
                            reader.close();

                            Float gitVersion = Float.parseFloat(pageSource);
                            GUIMediator.model();
							if(gitVersion <= Float.parseFloat(InjectionModel.JSQLVERSION))
								InjectionModel.logger.info("jSQL Injection is up to date.");
                            else{
                                InjectionModel.logger.warn("A new version of jSQL Injection is available.");
                                Desktop.getDesktop().browse(new URI("http://code.google.com/p/jsql-injection/downloads/list"));
                            }
                        } catch (NumberFormatException e) {
                            InjectionModel.logger.warn("An error occured while checking updates, download the latest version from official website :");
                            InjectionModel.logger.warn("http://code.google.com/p/jsql-injection/downloads/list");
                            InjectionModel.logger.error(e, e);
                        } catch (IOException e) {
                            InjectionModel.logger.warn("An error occured while checking updates, download the latest version from official website :");
                            InjectionModel.logger.warn("http://code.google.com/p/jsql-injection/downloads/list");
                            InjectionModel.logger.error(e, e);
                        } catch (URISyntaxException e) {
                            InjectionModel.logger.warn("An error occured while checking updates, download the latest version from official website :");
                            InjectionModel.logger.warn("http://code.google.com/p/jsql-injection/downloads/list");
                            InjectionModel.logger.error(e, e);
                        }

                    }
                }, "Menubar - Check update").start();
            }
        });
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
