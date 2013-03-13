package com.jsql.mvc.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.view.component.JSplitPaneWithZeroSizeDivider;
import com.jsql.mvc.view.component.RoundedCornerBorder;
import com.jsql.mvc.view.component.TreeNodeEditor;
import com.jsql.mvc.view.component.TreeNodeRenderer;
import com.jsql.mvc.view.component.popup.JPopupTextField;


/**
 * Pane composed of tree and tabs on top, and info tabs on bottom.
 */
public class OutputPanel extends JSplitPaneWithZeroSizeDivider{
    private static final long serialVersionUID = -5696939494054282278L;
    
    JButton runFileButton;
    JLabel filePrivilegeLabel;
    JButton shellrunFileButton;
    JLabel shellfilePrivilegeLabel;
//    JButton uploadrunFileButton;
//    JLabel uploadfilePrivilegeLabel;
    GUI mygui;
    
    int verticalSplitter,horizontalSplitter;
    
    public OutputPanel(final GUI gui){
        super(JSplitPane.VERTICAL_SPLIT, true);
        mygui = gui;
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        String ID1 = "verticalSplitter";
        String ID2 = "horizontalSplitter";
        this.verticalSplitter = prefs.getInt(ID1, 300);
        this.horizontalSplitter = prefs.getInt(ID2, 365);
//        if(this.horizontalSplitter > 365)
//            this.horizontalSplitter = 365;
        
        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("No database");
        gui.databaseTree = new JTree(root);
        
        // Graphic manager for components
        TreeNodeRenderer renderer = new TreeNodeRenderer();
        gui.databaseTree.setCellRenderer(renderer);
        
        // Action manager for components
        TreeNodeEditor editor = new TreeNodeEditor(gui.databaseTree, gui.controller, gui.valuesTabbedPane);
        gui.databaseTree.setCellEditor(editor);
        
        // Tree setting
        gui.databaseTree.setEditable(true);    // allows repaint nodes
        gui.databaseTree.setShowsRootHandles(true);
        gui.databaseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Dirty trick that allows repaint progressbar
        gui.databaseTree.getModel().addTreeModelListener(new TreeModelListener() { 
            @Override
            public void treeNodesChanged(TreeModelEvent arg0) {
                if(arg0 != null){
                    gui.databaseTree.firePropertyChange(
                        JTree.ROOT_VISIBLE_PROPERTY, 
                        !gui.databaseTree.isRootVisible(), 
                        gui.databaseTree.isRootVisible()
                    );
                }
            }
            @Override public void treeStructureChanged(TreeModelEvent arg0) {}
            @Override public void treeNodesRemoved(TreeModelEvent arg0) {}
            @Override public void treeNodesInserted(TreeModelEvent arg0) {}
        });
        
        // Give focus on tab change
        JTabbedPane leftTabbedPane = new JTabbedPane();
        leftTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        RoundJScrollPane scroller = new RoundJScrollPane(gui.databaseTree);
        leftTabbedPane.addTab("Database", new ImageIcon(getClass().getResource("/com/jsql/images/server_database.png")), scroller, "Explore databases from remote host");
        
        // Hotkeys ctrl-TAB, ctrl-shift-TAB
        new ActionHandler(leftTabbedPane);
        
//        final DefaultMutableTreeNode rootPath = new DefaultMutableTreeNode("File paths from various OS's");
//        ArrayList<String> pathList = new ArrayList<String>();
//        pathList.add("E:/Outils/EasyPHP-5.3.9/mysql/my.ini");
//        pathList.add("H:/HACK/listip.txt");
//        pathList.add("C:/Windows/System32/drivers/etc/hosts");
//        pathList.add("C:/Windows/system.ini");
//        pathList.add("C:/autoexec.bat");
//        pathList.add("/etc/mysql/my.cnf");
//        pathList.add("/etc/apache2/apache2.conf");
//        pathList.add("/etc/apache2/ports.conf");
//        pathList.add("/etc/lsb-release");
//        pathList.add("/etc/motd");
//        pathList.add("/etc/networks");
//        pathList.add("/etc/protocols");
//        pathList.add("/etc/services");
//        
//        for(String path: pathList)
//            rootPath.add(new DefaultMutableTreeNode(path));
//        
//        final JTree treeFile = new JTree(rootPath);
//        
//        JPanel fileMainPanel = new JPanel(new BorderLayout());
//        
//        JPanel fileNorthPanel = new JPanel();
//        fileNorthPanel.setLayout( new BoxLayout(fileNorthPanel, BoxLayout.X_AXIS) );
//        
//        JButton addButton = new JButton("Add");
//        addButton.setBorder(new RoundedCornerBorder(6, 1, true));
//
//        final JPopupTextField pathTextField = new JPopupTextField();
//        
//        addButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
//                DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
//                root.add(new DefaultMutableTreeNode(pathTextField.getText()));
//                model.reload(root);
//            }
//        });
//        
//        fileMainPanel.add(fileNorthPanel, BorderLayout.NORTH);
//        fileMainPanel.add(new RoundJScrollPane(treeFile), BorderLayout.CENTER);
//        
//        JPanel fileSouthPanel = new JPanel();
//        fileSouthPanel.setOpaque(false);
//        fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
//        
//        runFileButton = new JButton("Run");
//        runFileButton.setEnabled(false);
//        runFileButton.setBorder(new RoundedCornerBorder(6, 3, true));
//        runFileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                if(treeFile.getSelectionPaths() == null) return;
//                for(final TreePath path: treeFile.getSelectionPaths()){
//                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//                    if(node == null) return;
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                gui.model.getFile(node.toString());
//                            } catch (PreparationException e) {
//                                gui.model.sendErrorMessage("Can't read file: " + path.getLastPathComponent());
//                            } catch (StoppableException e) {
//                                gui.model.sendErrorMessage("Can't read file " + path.getLastPathComponent());
//                            }
//                        }
//                    }, "getFile").start();
//                }
//            }
//        });
//        
//        //Create a file chooser
//        final JFileChooser importFileDialog = new JFileChooser(gui.model.pathFile);
//        importFileDialog.setDialogTitle("Import a list of paths from a file");
//        
//        JButton importFileButton = new JButton("Import");
//        importFileButton.setBorder(new RoundedCornerBorder(6, 1, true));
//        importFileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                int returnVal = importFileDialog.showOpenDialog(gui);
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File file = importFileDialog.getSelectedFile();
//                    gui.model.pathFile = importFileDialog.getCurrentDirectory().toString();
//                    
//                    // Save path for further use
//                    Preferences prefs = Preferences.userRoot().node(gui.model.getClass().getName());
//                    String ID1 = "pathFile";
//                    prefs.put(ID1, gui.model.pathFile);
//                    
//                    BufferedReader in;
//                    try {
//                        in = new BufferedReader(new FileReader(file));
//                        String line;
//                        DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
//                        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
//                        while((line = in.readLine()) != null)
//                            root.add(new DefaultMutableTreeNode(line));
//                        
//                        model.reload(root);
//                    } catch (FileNotFoundException e1) {
//                        e1.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        
//        fileNorthPanel.add(importFileButton);
//        fileNorthPanel.add(Box.createHorizontalStrut(1));
//        fileNorthPanel.add(pathTextField);
//        fileNorthPanel.add(Box.createHorizontalStrut(1));
//        fileNorthPanel.add(addButton);
//
//        filePrivilegeLabel = new JLabel("File privilege", new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png")), SwingConstants.LEFT);
//        fileSouthPanel.add(filePrivilegeLabel);
//        fileSouthPanel.add(Box.createHorizontalGlue());
//        fileSouthPanel.add(runFileButton);
//        fileMainPanel.add(fileSouthPanel, BorderLayout.SOUTH);
//
//        leftTabbedPane.addTab("File", new ImageIcon(getClass().getResource("/com/jsql/images/server_file.png")), fileMainPanel);
        leftTabbedPane.addTab("File", new ImageIcon(getClass().getResource("/com/jsql/images/server_file.png")), new aze(), "Read a file from remote host");
        leftTabbedPane.addTab("Webshell", new ImageIcon(getClass().getResource("/com/jsql/images/server_console.png")), new azeb(), "Create a webshell to remote host and open a terminal");
//        leftTabbedPane.addTab("Upload", new ImageIcon(getClass().getResource("/com/jsql/images/server_file.png")), new azec(), "Upload a file from local computer to remote host");
        leftTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        leftTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        gui.valuesTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        // Tree and tabs on top
        final JSplitPaneWithZeroSizeDivider treeAndTableSplitPane = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        treeAndTableSplitPane.setLeftComponent( leftTabbedPane );
        treeAndTableSplitPane.setRightComponent( gui.valuesTabbedPane );
        treeAndTableSplitPane.setDividerLocation(this.verticalSplitter);
        treeAndTableSplitPane.setDividerSize(0);
        treeAndTableSplitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.setDividerSize(0);
        this.setBorder(new RoundedCornerBorder(2,2,true));
        
        // Infos tabs in bottom
        JTabbedPane infoTabs = new JTabbedPane();
        new ActionHandler(infoTabs);
        infoTabs.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        infoTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        infoTabs.addTab("Console", new ImageIcon(getClass().getResource("/com/jsql/images/console.gif")), new RoundJScrollPane(gui.consoleArea));
        infoTabs.addTab("Chunk", new ImageIcon(getClass().getResource("/com/jsql/images/category.gif")), new RoundJScrollPane(gui.chunks), "Hexadecimal data to parse");
        infoTabs.addTab("Binary", new ImageIcon(getClass().getResource("/com/jsql/images/binary.gif")), new RoundJScrollPane(gui.binaryArea), "Time/Blind data bytes");
        infoTabs.addTab("Header", new ImageIcon(getClass().getResource("/com/jsql/images/update.gif")), new RoundJScrollPane(gui.headers), "URL calls information");
        
        // Setting for top and bottom components
        this.setTopComponent(treeAndTableSplitPane);
        this.setBottomComponent( infoTabs );
        this.setDividerLocation(570 - this.horizontalSplitter);
//        setDividerLocation(this,this.horizontalSplitter);
        
//        this.setDividerSize(5);
        this.setResizeWeight(1); // defines left and bottom pane
        
        this.mygui.addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                String ID1 = "verticalSplitter";
                String ID2 = "horizontalSplitter";
                prefs.putInt(ID1, treeAndTableSplitPane.getDividerLocation());
                prefs.putInt(ID2, OutputPanel.this.getHeight() - OutputPanel.this.getDividerLocation());
            }
        });
        
        gui.chunks.setLineWrap(true);
        gui.headers.setLineWrap(true);
        gui.binaryArea.setLineWrap(true);
    }
    
    public static JSplitPane setDividerLocation(final JSplitPane splitter,
            final double proportion) {
        if (splitter.isShowing()) {
            if(splitter.getWidth() > 0 && splitter.getHeight() > 0) {
                splitter.setDividerLocation(proportion);
            }
            else {
                splitter.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        splitter.removeComponentListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                });
            }
        }
        else {
            splitter.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 &&
                            splitter.isShowing()) {
                        splitter.removeHierarchyListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                }
            });
        }
        return splitter;
    }
    
    class CustomTreeRenderer extends DefaultTreeCellRenderer{
        DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
        
        @Override
        public Component getTreeCellRendererComponent( JTree aTree, Object aValue, boolean aSelected,
            boolean aExpanded, boolean aLeaf, int aRow, boolean aHasFocus )
        {

            
            JPanel l = new JPanel(new BorderLayout());
            l.setBorder(null);
            JLabel m = new JLabel((String)((DefaultMutableTreeNode)aValue).getUserObject());
            l.add(m);
//            m.setBorder()
            m.setBorder(new RoundedCornerBorder(4,1,false));
            if( (aValue != null) && (aValue instanceof DefaultMutableTreeNode)){
                if( aSelected )
                  {
                        l.setBackground( new Color(195,214,233) );
                        m.setBorder(new RoundedCornerBorder(4,1,true));
                  }else
                      l.setBackground( Color.white );
                if(aHasFocus)
                    m.setBorder(new RoundedCornerBorder(4,1,true));
                else
                    m.setBorder(new RoundedCornerBorder(4,1,false));
//                m.setBorder(new RoundedCornerBorder(2,2,false));
//            JPanel panel = new JPanel(); // Create a new panel where we will show the data.
//            String text = (String)((DefaultMutableTreeNode)aValue).getUserObject();
//     
//            panel.add( new JLabel( text ) ); // Actually show the data.
//     
//            // If the value is not null and is a tree node and a leaf then paint it.
//            if( (aValue != null) && (aValue instanceof DefaultMutableTreeNode) && aLeaf )
//            {
//                if( aSelected )
//                {
//                    panel.setBackground( Color.RED );
//                }
//                else
//                {
//                    if( aRow % 2 == 0 )
//                    {
//                        panel.setBackground( Color.WHITE );
//                    }
//                    else
//                    {
//                        panel.setBackground( new Color( 230, 230, 230 ) );
//                    }
//                }
//                panel.setEnabled( aTree.isEnabled() );
//                return panel;
//            }
            // For everything else use default renderer.
            return l;
            }
            return defaultRenderer.getTreeCellRendererComponent( aTree, aValue, aSelected, aExpanded, aLeaf,
                    aRow, aHasFocus );
        }
    }
    
    class aze extends JPanel{
        aze(){
            super(new BorderLayout());
            
            final DefaultMutableTreeNode rootPath = new DefaultMutableTreeNode("Remote system files to read");
            ArrayList<String> pathList = new ArrayList<String>();
            pathList.add("/var/www/html/defaut/index.php");
            pathList.add("/var/www/html/defaut/index.html");
            pathList.add("/var/www/html/defaut/index.htm");
            pathList.add("/var/www/html/default/index.php");
            pathList.add("/var/www/html/default/index.html");
            pathList.add("/var/www/html/default/index.htm");
            pathList.add("/var/www/html/index.html");
            pathList.add("/var/www/html/index.php");
            pathList.add("/var/www/html/index.htm");
            pathList.add("/var/www/index.html");
            pathList.add("/var/www/index.php");
            pathList.add("/var/www/index.htm");
            pathList.add("/home/www/index.html");
            pathList.add("/home/www/index.php");
            pathList.add("/home/www/index.htm");
            pathList.add("/etc/apache2/apache2.conf");
            pathList.add("/etc/apache2/envvars");
            pathList.add("/etc/apache2/sites-enabled/000-default");
            pathList.add("/etc/apache2/sites-available/default");
            pathList.add("/etc/apache2/sites-available/httpd.conf");
            pathList.add("/etc/apache2/mods-available/alias.conf");
            pathList.add("/etc/apache2/mods-available/proxy_ftp.conf");
            pathList.add("/etc/apache2/ports.conf");
            pathList.add("/etc/php4/apache2/php.ini");
            pathList.add("/etc/php5/apache2/php.ini");
            pathList.add("/etc/mysql/my.cnf");
            pathList.add("/etc/hosts");
            pathList.add("/etc/motd");
            pathList.add("/etc/networks");
            pathList.add("/etc/protocols");
            pathList.add("/etc/services");
            pathList.add("/etc/passwd");
            pathList.add("/etc/group");
            pathList.add("/etc/lsb-release");
            pathList.add("/etc/ssh/sshd_config");
            pathList.add("/etc/network/options");
            pathList.add("/network/interfaces");
            pathList.add("/etc/sysconfig/routed");
            pathList.add("/etc/sysconfig/static-routes");
            pathList.add("C:/Windows/System32/drivers/etc/hosts");
            pathList.add("C:/Windows/system.ini");
            pathList.add("C:/autoexec.bat");
            pathList.add("C:/CONFIG.SYS");
            pathList.add("C:/Windows/WIN.INI");

            for(String path: pathList)
                rootPath.add(new DefaultMutableTreeNode(path));
            
            final JTree treeFile = new JTree(rootPath);
            
            treeFile.setCellRenderer(new CustomTreeRenderer());
//            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)treeFile.getCellRenderer();
//
//            Color backgroundSelection = renderer.getBackgroundSelectionColor();
//            renderer.setBackgroundSelectionColor(renderer.getBackgroundNonSelectionColor());
//            renderer.setBackgroundNonSelectionColor(backgroundSelection);
//
//            Color textSelection = renderer.getTextSelectionColor();
//            renderer.setTextSelectionColor(renderer.getTextNonSelectionColor());
//            renderer.setTextNonSelectionColor(textSelection);
//            renderer.setBorder(null);
//            renderer.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
            
            JPanel fileNorthPanel = new JPanel();
            fileNorthPanel.setLayout( new BoxLayout(fileNorthPanel, BoxLayout.X_AXIS) );
            
            JButton addButton = new JButton("Add");
            addButton.setBorder(new RoundedCornerBorder(6, 1, true));

            final JPopupTextField pathTextField = new JPopupTextField();
            
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                    root.add(new DefaultMutableTreeNode(pathTextField.getText().replace("\\", "/")));
                    model.reload(root);
                }
            });
            
            this.add(fileNorthPanel, BorderLayout.NORTH);
            this.add(new RoundJScrollPane(treeFile), BorderLayout.CENTER);
            
            JPanel fileSouthPanel = new JPanel();
            fileSouthPanel.setOpaque(false);
            fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
            
            runFileButton = new JButton("Read selected file(s)");
            runFileButton.setEnabled(false);
            runFileButton.setBorder(new RoundedCornerBorder(6, 3, true));
            runFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(treeFile.getSelectionPaths() == null) return;
                    for(final TreePath path: treeFile.getSelectionPaths()){
                        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if(node == null) return;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mygui.model.getFile(node.toString());
                                } catch (PreparationException e) {
                                    mygui.model.sendErrorMessage("Can't read file: " + path.getLastPathComponent());
                                } catch (StoppableException e) {
                                    mygui.model.sendErrorMessage("Can't read file " + path.getLastPathComponent());
                                }
                            }
                        }, "getFile").start();
                    }
                }
            });
            
            //Create a file chooser
            final JFileChooser importFileDialog = new JFileChooser(mygui.model.pathFile);
            importFileDialog.setDialogTitle("Import a list of file paths");
            
            JButton importFileButton = new JButton("Import");
            importFileButton.setBorder(new RoundedCornerBorder(6, 1, true));
            importFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int returnVal = importFileDialog.showOpenDialog(mygui);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = importFileDialog.getSelectedFile();
                        mygui.model.pathFile = importFileDialog.getCurrentDirectory().toString();
                        
                        // Save path for further use
                        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                        String ID1 = "pathFile";
                        prefs.put(ID1, mygui.model.pathFile);
                        
                        BufferedReader in;
                        try {
                            in = new BufferedReader(new FileReader(file));
                            String line;
                            DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
                            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                            while((line = in.readLine()) != null)
                                root.add(new DefaultMutableTreeNode(line));
                            
                            model.reload(root);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            fileNorthPanel.add(importFileButton);
            fileNorthPanel.add(Box.createHorizontalStrut(1));
            fileNorthPanel.add(pathTextField);
            fileNorthPanel.add(Box.createHorizontalStrut(1));
            fileNorthPanel.add(addButton);

            filePrivilegeLabel = new JLabel("File privilege", new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png")), SwingConstants.LEFT);
            fileSouthPanel.add(filePrivilegeLabel);
            fileSouthPanel.add(Box.createHorizontalGlue());
            fileSouthPanel.add(runFileButton);
            this.add(fileSouthPanel, BorderLayout.SOUTH);
        }
    }
    
    class azeb extends JPanel{
        azeb(){
            super(new BorderLayout());
            
            final DefaultMutableTreeNode rootPath = new DefaultMutableTreeNode("Remote folder paths for webshell creation");
            ArrayList<String> pathList = new ArrayList<String>();
            pathList.add("/var/www/html/defaut/");
            pathList.add("/var/www/html/default/");
            pathList.add("/var/www/html/");
            pathList.add("/var/www/");
            pathList.add("/home/www/");

            for(String path: pathList)
                rootPath.add(new DefaultMutableTreeNode(path));
            
            final JTree treeFile = new JTree(rootPath);
            treeFile.setCellRenderer(new CustomTreeRenderer());
            
            JPanel fileNorthPanel = new JPanel();
            fileNorthPanel.setLayout( new BoxLayout(fileNorthPanel, BoxLayout.X_AXIS) );
            
            JButton addButton = new JButton("Add");
            addButton.setBorder(new RoundedCornerBorder(6, 1, true));

            final JPopupTextField pathTextField = new JPopupTextField();
            
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                    root.add(new DefaultMutableTreeNode(pathTextField.getText().replace("\\", "/")));
                    model.reload(root);
                }
            });
            
            this.add(fileNorthPanel, BorderLayout.NORTH);
            this.add(new RoundJScrollPane(treeFile), BorderLayout.CENTER);
            
            JPanel mainSouthPanel = new JPanel();
            mainSouthPanel.setLayout(new BoxLayout(mainSouthPanel, BoxLayout.Y_AXIS));
            
            JPanel fileSouthPanel = new JPanel();
            fileSouthPanel.setOpaque(false);
            fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
            
            final JPopupTextField urlToWebshell = new JPopupTextField();

            shellrunFileButton = new JButton("Create into selected folder(s)");
            shellrunFileButton.setEnabled(false);
            shellrunFileButton.setBorder(new RoundedCornerBorder(6, 3, true));
            shellrunFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(treeFile.getSelectionPaths() == null) return;
                    for(final TreePath path: treeFile.getSelectionPaths()){
                        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if(node == null) return;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mygui.model.getShell(node.toString(), urlToWebshell.getText());
                                } catch (PreparationException e) {
                                    mygui.model.sendErrorMessage("Can't read file: " + path.getLastPathComponent()+"test_outfile.php");
                                } catch (StoppableException e) {
                                    mygui.model.sendErrorMessage("Can't read file " + path.getLastPathComponent()+"test_outfile.php");
                                }
                            }
                        }, "getFile").start();
                    }
                }
            });
            
            //Create a file chooser
            final JFileChooser importFileDialog = new JFileChooser(mygui.model.pathFile);
            importFileDialog.setDialogTitle("Import a list of paths from a file");
            
            JButton importFileButton = new JButton("Import");
            importFileButton.setBorder(new RoundedCornerBorder(6, 1, true));
            importFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int returnVal = importFileDialog.showOpenDialog(mygui);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = importFileDialog.getSelectedFile();
                        mygui.model.pathFile = importFileDialog.getCurrentDirectory().toString();
                        
                        // Save path for further use
                        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                        String ID1 = "pathFile";
                        prefs.put(ID1, mygui.model.pathFile);
                        
                        BufferedReader in;
                        try {
                            in = new BufferedReader(new FileReader(file));
                            String line;
                            DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
                            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                            while((line = in.readLine()) != null)
                                root.add(new DefaultMutableTreeNode(line));
                            
                            model.reload(root);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            fileNorthPanel.add(importFileButton);
            fileNorthPanel.add(Box.createHorizontalStrut(1));
            fileNorthPanel.add(pathTextField);
            fileNorthPanel.add(Box.createHorizontalStrut(1));
            fileNorthPanel.add(addButton);

            shellfilePrivilegeLabel = new JLabel("File privilege", new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png")), SwingConstants.LEFT);
            fileSouthPanel.add(shellfilePrivilegeLabel);
            fileSouthPanel.add(Box.createHorizontalGlue());
            fileSouthPanel.add(shellrunFileButton);
            
//            JPanel o = new JPanel()
            JPanel s = new JPanel(new BorderLayout());
            s.setLayout(new BoxLayout(s, BoxLayout.X_AXIS));
            s.add(Box.createHorizontalGlue());
            
            JLabel a = new JLabel("URL to the webshell directory [optional]:");
            s.add(a);
            s.add(Box.createHorizontalGlue());
            
            urlToWebshell.setBorder(new RoundedCornerBorder(3,3,true));
            
            String l = "<html>For example, webshell is created into /var/www/site/folder and<br>" +
            		   "corresponding Url uses virtual directory, in that case you need to specify the<br>" +
            		   "correct Url, that could be http://website.com/virtual_directory/.<br>" +
            		   "If directory Url is empty, jSQL uses the GET Url without the filename and assumes<br>" +
            		   "it points to selected folder(s)</html>";
            s.setToolTipText(l);
            urlToWebshell.setToolTipText(l);
            
            mainSouthPanel.add(s);
            mainSouthPanel.add(urlToWebshell);
            mainSouthPanel.add(fileSouthPanel);
            this.add(mainSouthPanel, BorderLayout.SOUTH);
//            this.add(fileSouthPanel, BorderLayout.SOUTH);
        }
    }
    
    class azec extends JPanel{
        azec(){
            super(new BorderLayout());
            
            final DefaultMutableTreeNode rootPath = new DefaultMutableTreeNode("Remote folder paths for file creation");
            ArrayList<String> pathList = new ArrayList<String>();
            pathList.add("E:/Outils/EasyPHP-5.3.9/www/");
            pathList.add("/bin/");
            pathList.add("/boot/");
            pathList.add("/dev/");
            pathList.add("/etc/");
            pathList.add("/etc/profile.d/");
            pathList.add("/etc/rc.d/");
            pathList.add("/etc/rc.d/init.d/");
            pathList.add("/etc/skel/");
            pathList.add("/etc/X11/");
            pathList.add("/home/");
            pathList.add("/lib/");
            pathList.add("/media/");
            pathList.add("/mnt/");
            pathList.add("/opt/");
            pathList.add("/proc/");
            pathList.add("/root/");
            pathList.add("/sbin/");
            pathList.add("/sys/");
            pathList.add("/tmp/");
            pathList.add("/usr/");
            pathList.add("/var/");
            pathList.add("/usr/local/bin/");
            pathList.add("/usr/share/doc/");
            
            for(String path: pathList)
                rootPath.add(new DefaultMutableTreeNode(path));
            
            final JTree treeFile = new JTree(rootPath);
            treeFile.setCellRenderer(new CustomTreeRenderer());
            
            JPanel fileNorthPanel = new JPanel();
            fileNorthPanel.setLayout( new BoxLayout(fileNorthPanel, BoxLayout.X_AXIS) );
            
            JButton addButton = new JButton("Add");
            addButton.setBorder(new RoundedCornerBorder(6, 1, true));

            final JPopupTextField pathTextField = new JPopupTextField();
            
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                    root.add(new DefaultMutableTreeNode(pathTextField.getText().replace("\\", "/")));
                    model.reload(root);
                }
            });
            
            this.add(fileNorthPanel, BorderLayout.NORTH);
            this.add(new RoundJScrollPane(treeFile), BorderLayout.CENTER);
            
            JPanel fileSouthPanel = new JPanel();
            fileSouthPanel.setOpaque(false);
            fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
            
//            uploadrunFileButton = new JButton("Upload into selected folder(s)");
//            uploadrunFileButton.setEnabled(false);
//            uploadrunFileButton.setBorder(new RoundedCornerBorder(6, 3, true));
//            uploadrunFileButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent arg0) {
//                    if(treeFile.getSelectionPaths() == null) return;
//                    for(final TreePath path: treeFile.getSelectionPaths()){
//                        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//                        if(node == null) return;
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    mygui.model.getFile(node.toString());
//                                } catch (PreparationException e) {
//                                    mygui.model.sendErrorMessage("Can't read file: " + path.getLastPathComponent());
//                                } catch (StoppableException e) {
//                                    mygui.model.sendErrorMessage("Can't read file " + path.getLastPathComponent());
//                                }
//                            }
//                        }, "getFile").start();
//                    }
//                }
//            });
            
            //Create a file chooser
            final JFileChooser importFileDialog = new JFileChooser(mygui.model.pathFile);
            importFileDialog.setDialogTitle("Import a list of paths from a file");
            
            JButton importFileButton = new JButton("Import");
            importFileButton.setBorder(new RoundedCornerBorder(6, 1, true));
            importFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int returnVal = importFileDialog.showOpenDialog(mygui);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = importFileDialog.getSelectedFile();
                        mygui.model.pathFile = importFileDialog.getCurrentDirectory().toString();
                        
                        // Save path for further use
                        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                        String ID1 = "pathFile";
                        prefs.put(ID1, mygui.model.pathFile);
                        
                        BufferedReader in;
                        try {
                            in = new BufferedReader(new FileReader(file));
                            String line;
                            DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
                            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                            while((line = in.readLine()) != null)
                                root.add(new DefaultMutableTreeNode(line));
                            
                            model.reload(root);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            fileNorthPanel.add(importFileButton);
            fileNorthPanel.add(Box.createHorizontalStrut(1));
            fileNorthPanel.add(pathTextField);
            fileNorthPanel.add(Box.createHorizontalStrut(1));
            fileNorthPanel.add(addButton);

//            uploadfilePrivilegeLabel = new JLabel("File privilege", new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png")), SwingConstants.LEFT);
//            fileSouthPanel.add(uploadfilePrivilegeLabel);
//            fileSouthPanel.add(Box.createHorizontalGlue());
//            fileSouthPanel.add(uploadrunFileButton);
//            this.add(fileSouthPanel, BorderLayout.SOUTH);
        }
    }
}