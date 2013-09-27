/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.component;

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
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

import com.jsql.model.InjectionModel;
import com.jsql.view.GUI;
import com.jsql.view.GUITools;
import com.jsql.view.RoundScroller;
import com.jsql.view.SaveTabAction;
import com.jsql.view.dialog.About;
import com.jsql.view.dialog.Prefs;
import com.jsql.view.panel.LeftRightBottom;
import com.jsql.view.table.TablePanel;

/**
 * Software menubar.
 */
public class Menubar extends JMenuBar {
    private static final long serialVersionUID = 3568728818280839048L;

    public Menubar(final GUI gui){
        // File Menu > save tab | exit
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');

        JMenuItem itemSave = new JMenuItem("Save Tab As...", 'S');
        itemSave.setIcon(GUITools.EMPTY);
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itemSave.addActionListener(new SaveTabAction(gui));

        JMenuItem itemExit = new JMenuItem("Exit", 'x');
        itemExit.setIcon(GUITools.EMPTY);
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                gui.dispose();
            }
        });

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
                if(gui.right.getSelectedComponent() instanceof TablePanel)
                    ((TablePanel) gui.right.getSelectedComponent()).copyTable();
                else if(gui.right.getSelectedComponent() instanceof JScrollPane)
                    ((JTextArea) (((JViewport) (((JScrollPane) gui.right.getSelectedComponent()).getViewport()))).getView()).copy();
            }
        });

        JMenuItem itemSelectAll = new JMenuItem("Select All", 'A');
        itemSelectAll.setIcon(GUITools.EMPTY);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gui.right.getSelectedComponent() instanceof TablePanel)
                    ((TablePanel) gui.right.getSelectedComponent()).selectTable();
                // Textarea need focus to select all
                else if(gui.right.getSelectedComponent() instanceof JScrollPane){
                    ((JTextArea) (((JViewport) (((JScrollPane) gui.right.getSelectedComponent()).getViewport()))).getView()).requestFocusInWindow();
                    ((JTextArea) (((JViewport) (((JScrollPane) gui.right.getSelectedComponent()).getViewport()))).getView()).selectAll();
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
        JMenuItem database = new JMenuItem("Database", GUITools.DATABASE_SERVER);
        menuView.add(database);
        JMenuItem adminPage = new JMenuItem("Admin page", GUITools.ADMIN_SERVER);
        menuView.add(adminPage);
        JMenuItem file = new JMenuItem("File", GUITools.FILE_SERVER);
        menuView.add(file);
        JMenuItem webshell = new JMenuItem("Web shell", GUITools.SHELL_SERVER);
        menuView.add(webshell);
        JMenuItem sqlshell = new JMenuItem("SQL shell", GUITools.SHELL_SERVER);
        menuView.add(sqlshell);
        JMenuItem bruteforce = new JMenuItem("Brute force", GUITools.BRUTER);
        menuView.add(bruteforce);
        JMenuItem coder = new JMenuItem("Coder", GUITools.CODER);
        menuView.add(coder);
        menuTools.add(menuView);
        menuTools.add(new JSeparator());

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        
        JMenu menuPanel = new JMenu("Show Panel");
        menuView.setMnemonic('V');
        final JCheckBoxMenuItem chunk = new JCheckBoxMenuItem("Chunk", new ImageIcon(getClass().getResource("/com/jsql/view/images/chunk.gif")), prefs.getBoolean(gui.CHUNK_VISIBLE, true));
        menuPanel.add(chunk);
        final JCheckBoxMenuItem binary = new JCheckBoxMenuItem("Binary", new ImageIcon(getClass().getResource("/com/jsql/view/images/binary.gif")), prefs.getBoolean(gui.BINARY_VISIBLE, true));
        menuPanel.add(binary);
        final JCheckBoxMenuItem header = new JCheckBoxMenuItem("Header", new ImageIcon(getClass().getResource("/com/jsql/view/images/header.gif")), prefs.getBoolean(gui.HEADER_VISIBLE, true));
        menuPanel.add(header);
        final JCheckBoxMenuItem javaDebug = new JCheckBoxMenuItem("Java", new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/cup.png")), prefs.getBoolean(gui.JAVA_VISIBLE, false));
        
        class StayOpenCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
            @Override
            protected void doClick(MenuSelectionManager msm) {
                menuItem.doClick(0);
            }
        }
        
        for(JCheckBoxMenuItem i: new JCheckBoxMenuItem[]{chunk,binary,header,javaDebug}){
            i.setUI(new StayOpenCheckBoxMenuItemUI());
        }
        
        chunk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(chunk.isSelected())
                    gui.outputPanel.bottom.insertTab("Chunk", new ImageIcon(getClass().getResource("/com/jsql/view/images/chunk.gif")), new RoundScroller(gui.chunks), "Hexadecimal data recovered",
                            javaDebug.isSelected()&&header.isSelected()&&binary.isSelected()?
                                    gui.outputPanel.bottom.getTabCount()-3
                            :
                                javaDebug.isSelected()&&header.isSelected()||javaDebug.isSelected()&&binary.isSelected()||header.isSelected()&&binary.isSelected()?    
                                        gui.outputPanel.bottom.getTabCount()-2
                                :
                                    javaDebug.isSelected()||binary.isSelected()||header.isSelected()?
                                        gui.outputPanel.bottom.getTabCount()-1
                                    :
                                        gui.outputPanel.bottom.getTabCount()
                            );
                else
                    for(int i=0; i < gui.outputPanel.bottom.getTabCount() ;i++)
                        if (gui.outputPanel.bottom.getTitleAt(i).equals("Chunk")) {
                            gui.outputPanel.bottom.remove(gui.outputPanel.bottom.getComponentAt(i));
                            break;
                        }
            }
        });
        binary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(binary.isSelected())
                    gui.outputPanel.bottom.insertTab("Binary", new ImageIcon(getClass().getResource("/com/jsql/view/images/binary.gif")), new RoundScroller(gui.binaryArea), "Time/Blind bytes", 
                            javaDebug.isSelected()&&header.isSelected()?
                                    gui.outputPanel.bottom.getTabCount()-2
                            :
                                javaDebug.isSelected()||header.isSelected()?
                                        gui.outputPanel.bottom.getTabCount()-1
                                :
                                        gui.outputPanel.bottom.getTabCount()
                                );
                else
                    for(int i=0; i < gui.outputPanel.bottom.getTabCount() ;i++)
                        if (gui.outputPanel.bottom.getTitleAt(i).equals("Binary")) {
                            gui.outputPanel.bottom.remove(gui.outputPanel.bottom.getComponentAt(i));
                            break;
                        }
            }
        });
        header.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(header.isSelected())
                    gui.outputPanel.bottom.insertTab("Header", new ImageIcon(getClass().getResource("/com/jsql/view/images/header.gif")), new RoundScroller(gui.headers), "URL calls information", javaDebug.isSelected()?gui.outputPanel.bottom.getTabCount()-1:gui.outputPanel.bottom.getTabCount());
                else
                    for(int i=0; i < gui.outputPanel.bottom.getTabCount() ;i++)
                        if (gui.outputPanel.bottom.getTitleAt(i).equals("Header")) {
                            gui.outputPanel.bottom.remove(gui.outputPanel.bottom.getComponentAt(i));
                            break;
                        }
            }
        });
        javaDebug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(javaDebug.isSelected())
                    gui.outputPanel.bottom.insertTab("Java", new ImageIcon(getClass().getResource("/com/jsql/view/images/cup.png")), new RoundScroller(gui.javaDebug), "Java console", gui.outputPanel.bottom.getTabCount());
                else
                    for(int i=0; i < gui.outputPanel.bottom.getTabCount() ;i++)
                        if (gui.outputPanel.bottom.getTitleAt(i).equals("Java")) {
                            gui.outputPanel.bottom.remove(gui.outputPanel.bottom.getComponentAt(i));
                            break;
                        }
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
                    gui.getOutputPanel().left.setSelectedIndex(p.get(m));
                }
            });
        }

        // Render the Preferences dialog behind scene
        final Prefs prefDiag = new Prefs(gui);
        preferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Center the dialog
                if(!prefDiag.isVisible()){
                    prefDiag.setLocationRelativeTo(gui);
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
        final About aboutDiag = new About(gui);
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
                            gui.model.sendMessage("Checking updates...");
                            URLConnection con = new URL("http://jsql-injection.googlecode.com/git/.version").openConnection();
                            con.setReadTimeout(60000);
                            con.setConnectTimeout(60000);

                            BufferedReader reader = new BufferedReader(new InputStreamReader( con.getInputStream() ));
                            String line, pageSource = "";
                            while( (line = reader.readLine()) != null ) pageSource += line+"\n";
                            reader.close();

                            Float gitVersion = Float.parseFloat(pageSource);
                            if(gitVersion <= Float.parseFloat(gui.model.jSQLVersion))
                                gui.model.sendMessage("jSQL Injection is up to date.");
                            else{
                                gui.model.sendErrorMessage("A new version of jSQL Injection is available.");
                                Desktop.getDesktop().browse(new URI("http://code.google.com/p/jsql-injection/downloads/list"));
                            }
                        } catch (NumberFormatException err) {
                            gui.model.sendErrorMessage("A problem occured with repository version, you can visit the updates page here:\nhttp://code.google.com/p/jsql-injection/downloads/list");
                        } catch (IOException e1) {
                            gui.model.sendErrorMessage("Repository website is not responding, you can visit the updates page here:\nhttp://code.google.com/p/jsql-injection/downloads/list");
                        } catch (URISyntaxException e) {
                            gui.model.sendDebugMessage(e);
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
