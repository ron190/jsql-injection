package com.jsql.mvc.view;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.jsql.mvc.controller.InjectionController;
import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.model.ObserverEvent;
import com.jsql.mvc.model.database.Column;
import com.jsql.mvc.model.database.Database;
import com.jsql.mvc.model.database.ElementDatabase;
import com.jsql.mvc.model.database.Table;
import com.jsql.mvc.view.component.BlockCaret2;
import com.jsql.mvc.view.component.RoundedCornerBorder;
import com.jsql.mvc.view.component.TabHeader;
import com.jsql.mvc.view.component.TablePanel;
import com.jsql.mvc.view.component.TestConsole;
import com.jsql.mvc.view.component.TreeNodeModel;
import com.jsql.mvc.view.component.popup.JPopupTextArea;


/**
 * View in the MVC pattern, define all the components and process actions sent by the model, 
 * Main groups of components:
 * - at the top: textfields input,
 * - at the center: tree on the left, table on the right,
 * - at the bottom: information labels 
 */
public class GUI extends JFrame implements Observer {
    private static final long serialVersionUID = 9164724117078636255L;
    
    // Used to call threads
    InjectionController controller;
    // Used to get proxy, path settings
    InjectionModel model;
        
    // Tree for database components
    public JTree databaseTree;
    // Tabs for values displayed in a table
    public JTabbedPane valuesTabbedPane = null;
    
    public Font myFont = new Font("Segoe UI",Font.PLAIN,UIManager.getDefaults().getFont("TextPane.font").getSize());

    /**
     * Text area for injection informations:
     * - console: standard readable message,
     * - chunk: data read from web page
     * - header: result of HTTP connection
     * - binary: blind/time progress
     */
    public JPopupTextArea consoleArea = null;
    public JPopupTextArea chunks = null;
    public JPopupTextArea headers = null;
    public JPopupTextArea binaryArea = null;
    
    // Panel of textfields at the top
    public InputPanel inputPanel;
    
    public OutputPanel outputPanel;
    // Panel of labels in the statusbar 
    public StatusPanel statusPanel;
    
    ArrayList<Image> images;
    
    // Build the GUI: add app icon, tree icons, the 3 main panels 
    public GUI(InjectionController newController, InjectionModel newModel){
        super("jSQL Injection");

        // Define a small and large app icon
        images = new ArrayList<Image>();
        try {
            URL urlSmall = this.getClass().getResource("/com/jsql/images/database-icon-16x16.png");
            URL urlBig = this.getClass().getResource("/com/jsql/images/database-icon-32x32.png");
            images.add( ImageIO.read(urlBig) );
            images.add( ImageIO.read(urlSmall) );
            this.setIconImages(images);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // No default icon for tree nodes
        UIManager.put("Tree.leafIcon", new ImageIcon());
        UIManager.put("Tree.openIcon", new ImageIcon());
        UIManager.put("Tree.closedIcon", new ImageIcon());
        
        // No bold for menu + round corner
        UIManager.put("Menu.font", myFont);
        UIManager.put("PopupMenu.font", myFont);
        UIManager.put("PopupMenu.border", new RoundedCornerBorder(2,2,true));
        UIManager.put("MenuItem.selectionBackground", new Color(195,214,233));
        UIManager.put("MenuItem.font", myFont);
        UIManager.put("MenuItem.border", new RoundedCornerBorder(2,2,false));

        // Custom tab
        UIManager.put("TabbedPane.darkShadow", new Color(190,198,205));
        UIManager.put("TabbedPane.highlight", new Color(180,194,224));
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(3, 2, 0, 2));
        UIManager.put("TabbedPane.tabInsets", new Insets(2,3+5,2,3));
        
        // Replace square with bar
        UIManager.put("ScrollBar.squareButtons", false);
        UIManager.put("TextField.font", new Font(((Font) UIManager.get("TextField.font")).getName(),Font.PLAIN,((Font) UIManager.get("TextField.font")).getSize()));
        UIManager.put("TextArea.font", new Font("Courier New",Font.PLAIN,((Font) UIManager.get("TextArea.font")).getSize()));
        UIManager.put("Button.font", myFont);
        UIManager.put("Label.font", myFont);
        UIManager.put("CheckBox.font", myFont);
        UIManager.put("TabbedPane.font", myFont);
        UIManager.put("Table.font", myFont);
        UIManager.put("TableHeader.font", myFont);
        UIManager.put("ToolTip.font", myFont);
        UIManager.put("ToolTip.background", new Color(255,255,225));
        UIManager.put("ToolTip.border", new RoundedCornerBorder(2,2,true));
        
        // Custom tree
        UIManager.put("Tree.expandedIcon", new ImageIcon(GUI.this.getClass().getResource("/com/jsql/images/close.png")));
        UIManager.put("Tree.collapsedIcon", new ImageIcon(GUI.this.getClass().getResource("/com/jsql/images/collapse.png")));
        UIManager.put("Tree.lineTypeDashed", true);
        
        // Custom progress bar
        UIManager.put("ProgressBar.border", BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 0, 4, 0), new RoundedCornerBorder(2,2,true)));
        UIManager.put("ProgressBar.foreground", new Color(158,210,152));
        UIManager.put("ProgressBar.background", UIManager.get("Tree.background"));

        // Object creation after customization
        consoleArea = new JPopupTextArea();
        chunks = new JPopupTextArea();
        headers = new JPopupTextArea();
        binaryArea = new JPopupTextArea();
        
        valuesTabbedPane = new JTabbedPane();
        
        // Save model
        model = newModel;
        // Register the view to the model
        model.addObserver(this);
        // Save controller
        this.controller = newController;
        
        // Menubar
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu > save tab | exit
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');
        
        JMenuItem itemSave = new JMenuItem("Save Tab As...");
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itemSave.addActionListener(new ActionSaveTabListener(this));
        
        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GUI.this.dispose();
            }
        });
        
        menuFile.add(itemSave);
        menuFile.add(new JSeparator());
        menuFile.add(itemExit);
        
        // Add hotkeys to rootpane ctrl-tab, ctrl-shift-tab, ctrl-w
        new ActionHandler(valuesTabbedPane);
        
        // Edit Menu > copy | select all
        JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic('E');
        
        JMenuItem itemCopy = new JMenuItem("Copy");
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(valuesTabbedPane.getSelectedComponent() instanceof TablePanel)
                    ((TablePanel) valuesTabbedPane.getSelectedComponent()).copyTable();
                else if(valuesTabbedPane.getSelectedComponent() instanceof JScrollPane)
                    ((JTextArea) (((JViewport) (((JScrollPane) valuesTabbedPane.getSelectedComponent()).getViewport()))).getView()).copy();
            }
        });
        
        JMenuItem itemSelectAll = new JMenuItem("Select All");
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(valuesTabbedPane.getSelectedComponent() instanceof TablePanel)
                    ((TablePanel) valuesTabbedPane.getSelectedComponent()).selectTable();
                // Textarea need focus to select all
                else if(valuesTabbedPane.getSelectedComponent() instanceof JScrollPane){
                    ((JTextArea) (((JViewport) (((JScrollPane) valuesTabbedPane.getSelectedComponent()).getViewport()))).getView()).requestFocusInWindow();
                    ((JTextArea) (((JViewport) (((JScrollPane) valuesTabbedPane.getSelectedComponent()).getViewport()))).getView()).selectAll();
                }
            }
        });
        
        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);
        
        // Window Menu > Preferences
        JMenu menuTools = new JMenu("Window");
        menuTools.setMnemonic('W');
        JMenuItem itemTools = new JMenuItem("Preferences");
        
        // Render the Preferences dialog behind scene 
        final PreferencesDialog prefDiag = new PreferencesDialog(this);
        itemTools.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Center the dialog
                if(!prefDiag.isVisible()){
                    prefDiag.setLocationRelativeTo(GUI.this);
                    prefDiag.setVisible(true); // needed here for button focus
                    prefDiag.okButton.requestFocusInWindow();
                }
                prefDiag.setVisible(true);
            }
        });
        menuTools.add(itemTools);
        
        // Help Menu > about
        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic('H');
        JMenuItem itemHelp = new JMenuItem("About jSQL Injection");
        JMenuItem itemUpdate = new JMenuItem("Check for Updates");
        
        // Render the About dialog behind scene
        final AboutDialog aboutDiag = new AboutDialog(this);
        itemHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
             // Center the dialog
                if(!aboutDiag.isVisible()){
                    aboutDiag.reinit();
                    aboutDiag.setVisible(true); // needed here for button focus
                    aboutDiag.okButton.requestFocusInWindow();
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
                            URLConnection con = new URL("http://jsql-injection.googlecode.com/git/.version").openConnection();
                            con.setReadTimeout(60000);
                            con.setConnectTimeout(60000);
                    
                            BufferedReader reader = new BufferedReader(new InputStreamReader( con.getInputStream() ));
                            String line, pageSource = "";
                            while( (line = reader.readLine()) != null ) pageSource += line+"\n";
                            reader.close();
                            
        //                    pageSource = "0.4";
                            Float gitVersion = Float.parseFloat(pageSource);
                            if(gitVersion <= Float.parseFloat(model.jSQLVersion))
                                model.sendMessage("jSQL Injection is up to date.");
                            else{
                                model.sendErrorMessage("A new version of jSQL Injection is available.");
                                Desktop.getDesktop().browse(new URI("http://code.google.com/p/jsql-injection/downloads/list"));
                            }
                        } catch (NumberFormatException err) {
                            model.sendErrorMessage("A problem occured with repository version, you can visit the updates page here:\nhttp://code.google.com/p/jsql-injection/downloads/list");
                        } catch (IOException e1) {
                            model.sendErrorMessage("Repository website is not responding, you can visit the updates page here:\nhttp://code.google.com/p/jsql-injection/downloads/list");
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                        
                    }
                }).start();
            }
        });
        menuHelp.add(itemUpdate);
        menuHelp.add(new JSeparator());
        menuHelp.add(itemHelp);
        
        // Make menubar
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuTools);
        menuBar.add(menuHelp);
        this.setJMenuBar(menuBar);
        
        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout( new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS) );
        
        // Textfields at the top
        inputPanel = new InputPanel(controller, model);
        this.add(inputPanel);

        // Main panel for tree ans tables in the middle
        JPanel mainPanel = new JPanel(new GridLayout(1,0));
        outputPanel = new OutputPanel(this);
        mainPanel.add(outputPanel);
        this.add(mainPanel);
        
        // Info on the bottom
        statusPanel = new StatusPanel();
        this.add(statusPanel);
        
        // Reduce size of components 
        this.pack(); // nécessaire après le masquage des param proxy
        // Size of window
        this.setSize(1024, 768);
        inputPanel.submitButton.requestFocusInWindow();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Center the window
        this.setLocationRelativeTo(null);
        
        // Define the keyword shortcuts for tabs #Need to work even if the focus is not on tabs
        new ActionHandler(this.getRootPane(), valuesTabbedPane);
    }
    
    /**
     *  Map a database element with the corresponding tree node.
     *  The injection model send a database element to the view, then the view access its graphic component to update
     */
    Map<ElementDatabase,DefaultMutableTreeNode> 
        treeNodeModels = new HashMap<ElementDatabase,DefaultMutableTreeNode>();
    
    /**
     * Observer pattern
     * Receive an update order from the model:
     * - action string: unique string id for one action
     * - observer event: contains object required for the update (e.g list of values to display in a tab)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        // The model in pattern MVC #remove?
        InjectionModel model = (InjectionModel) arg0;
        // Event contains all data retrieved by the model during injection
        ObserverEvent oEvent = (ObserverEvent) arg1;
        
        // Tree model, update the tree (refresh, add node, etc)
        DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
        // First node in tree
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        
        /* Design Pattern? */
        // Add a chunk string to the tab  
        if( "logs-message".equals(""+oEvent) ){
            chunks.append(""+oEvent.getArg());
            chunks.setCaretPosition(chunks.getDocument().getLength());
            
        // Add a blind/time progression string to the tab
        }else if( "binary-message".equals(""+oEvent) ){
            binaryArea.append(""+oEvent.getArg());
            binaryArea.setCaretPosition(binaryArea.getDocument().getLength());
        
        // Add a log string to the tab
        }else if( "console-message".equals(""+oEvent) ){
            consoleArea.append(""+oEvent.getArg());
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
            
        /**
         * Update the progress bar of a component (aka increase value in loading bar)
         * Get the database element to update and the progress value from the event 
         */
        }else if( "update-progressbar".equals(""+oEvent) ){
            Object[] progressionData = (Object[]) oEvent.getArg();
            // Database element to update
            ElementDatabase dataElementDatabase = (ElementDatabase) progressionData[0];
            // Progress value
            int dataCount = (Integer) progressionData[1];
            
            // Get the node
            TreeNodeModel<?> progressingTreeNodeModel = 
                    (TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
            // Update the progress value of the model
            progressingTreeNodeModel.childUpgradeCount = dataCount;
            
            // Update the node
            treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase));
            
        /**
         * Start the loading progress of a component (aka display a loading bar)
         * Get the database element to update from the event 
         */
        }else if( "start-indeterminate-progress".equals(""+oEvent) ){
            // Database element to update
            ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

            // Get the node
            TreeNodeModel<?> progressingTreeNodeModel = 
                    (TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
            // Mark the node model as 'loading'
            progressingTreeNodeModel.hasIndeterminatedProgress = true;
            
//            treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
            // Update the node
            treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase));
            
        /**
         * End the loading progress of a component.
         * Get the database element to update from the event 
         */
        }else if( "end-indeterminate-progress".equals(""+oEvent) ){
            // Database element to update
            ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

            // Get the node
            TreeNodeModel<?> progressingTreeNodeModel = 
                    (TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
            // Mark the node model as 'no loading bar'
            progressingTreeNodeModel.hasIndeterminatedProgress = false;
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.isRunning = false;
            
            // Update the node
            treeModel.nodeChanged((TreeNode) treeNodeModels.get(dataElementDatabase));
            
        /**
         * Start the loading progress of a component (aka display a loading bar)
         * Get the database element to update from the event 
         */
        }else if( "start-progress".equals(""+oEvent) ){
            // Database element to update
            ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

            // Get the node
            TreeNodeModel<?> progressingTreeNodeModel = 
                    (TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
            // Mark the node model as 'display progress bar'
            progressingTreeNodeModel.hasProgress = true;
            
//            treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
            // Update the node
            treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase));
            
        /**
         * End the loading progress of a component.
         * Get the database element to update from the event 
         */
        }else if( "end-progress".equals(""+oEvent) ){
            // Database element to update
            ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

            // Get the node
            TreeNodeModel<?> progressingTreeNodeModel = 
                    (TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
            // Mark the node model as 'no progress bar'
            progressingTreeNodeModel.hasProgress = false;
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.isRunning = false;
            // Reset the progress value of the model
            progressingTreeNodeModel.childUpgradeCount = 0;
            
            // Update the node
            treeModel.nodeChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
                
        /**
         * Update the status bar. 
         */
        }else if( "add-info".equals(""+oEvent) ){
            statusPanel.labelDBVersion.setText( model.versionDB );
            statusPanel.labelCurrentDB.setText( model.currentDB );
            statusPanel.labelCurrentUser.setText( model.currentUser );
            statusPanel.labelAuthenticatedUser.setText( model.authenticatedUser );
            
        // Add databases to the tree. 
        }else if( "add-databases".equals(""+oEvent) ){
            // Get list of databases from the model
            List<?> newDatabases = (ArrayList<?>) oEvent.getArg();
            // Loop into the list
            for(Object o: newDatabases){
                // The database to add to the tree
                Database d = (Database) o;
                
                // Create a node model with the database element 
                TreeNodeModel<Database> newTreeNodeModel = new TreeNodeModel<Database>(d);
                // Create the node
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
                // Save the node
                treeNodeModels.put(d, newNode);
                // Add the node to the tree
                root.add(newNode);
            }
            
            // Refresh the tree
            treeModel.reload(root);
            // Open the root node
            databaseTree.expandPath( new TreePath(root.getPath()) );
            databaseTree.setRootVisible(false);
            
        // Add tables to the tree
        }else if( "add-tables".equals(""+oEvent) ){
            // Get list of tables from the model
            List<?> newTables = (ArrayList<?>) oEvent.getArg();
            // The database to update
            DefaultMutableTreeNode databaseNode = null;
            
            // Loop into the list of tables
            for(Object o: newTables){
                // The table to add to the tree
                Table t = (Table) o;
                // Create a node model with the table element 
                TreeNodeModel<Table> newTreeNodeModel = new TreeNodeModel<Table>(t);
                // Create the node
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
                // Save the node
                treeNodeModels.put(t, newNode);
                
                // Get the parent database
                databaseNode = treeNodeModels.get(t.getParent());
                // Add the table to the database
                treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());
            }
            
            if(databaseNode != null){
                // Open the database node
                databaseTree.expandPath( new TreePath(databaseNode.getPath()) );
                // The database has just been search (avoid double check)
                ((TreeNodeModel<?>) databaseNode.getUserObject()).hasBeenSearched = true;
            }

        // Add columns to the tree
        }else if( "add-columns".equals(""+oEvent) ){
            // Get list of columns from the model
            List<?> newColumns = (List<?>) oEvent.getArg();
            // The table to update
            DefaultMutableTreeNode tableNode = null;
            
            // Loop into the list of columns
            for(Object o: newColumns){
                // The column to add to the tree
                Column c = (Column) o;
                // Create a node model with the column element 
                TreeNodeModel<Column> newTreeNodeModel = new TreeNodeModel<Column>(c);
                // Mark this node as a checkbox component
                newTreeNodeModel.hasCheckBox = true;
                
                // Create the node
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
                // Get the parent table
                tableNode = treeNodeModels.get(c.getParent());
                // Add the column to the table
                treeModel.insertNodeInto(newNode, tableNode, tableNode.getChildCount());
            }
            
            if(tableNode != null){
                // Open the table node
                databaseTree.expandPath( new TreePath(tableNode.getPath()) );
                // The table has just been search (avoid double check)
                ((TreeNodeModel<?>) tableNode.getUserObject()).hasBeenSearched = true;
            }

        // Add values in a new tab
        }else if( "add-values".equals(""+oEvent) ){
            Object[] observerEventData = (Object[]) oEvent.getArg();
            
            // Array of column names, diplayed in header table
            String[] columnNames = (String[]) observerEventData[0];
            // 2D array of values
            String[][] data = (String[][]) observerEventData[1];
            // The table containing the data
            ElementDatabase table = (ElementDatabase) observerEventData[2];
            
            // Get the node
            TreeNodeModel<?> progressingTreeNodeModel = 
                    (TreeNodeModel<?>) treeNodeModels.get(table).getUserObject();
            
            // Update the progress value of the model, end the progress
            progressingTreeNodeModel.childUpgradeCount = table.getCount();
            // Mark the node model as 'no stop/pause/resume button'
            progressingTreeNodeModel.isRunning = false;

            // Create a new table to display the values
            TablePanel newTableJPanel = new TablePanel(data, columnNames, valuesTabbedPane);
            // Create a new tab: add header and table
            valuesTabbedPane.addTab(table.getParent()+"."+table+" "+
                    "("+(columnNames.length-2)+" fields) ",newTableJPanel);
            // Focus on the new tab
            valuesTabbedPane.setSelectedComponent(newTableJPanel);
            
            // Create a custom tab header with close button
            TabHeader header = new TabHeader(valuesTabbedPane);
            // Apply the custom header to the tab
            valuesTabbedPane.setTabComponentAt(valuesTabbedPane.indexOfComponent(newTableJPanel), header);
        
        // Check the normal injection method as confirmed
        }else if( "add-normal".equals(""+oEvent) ){
            statusPanel.labelNormal.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/gradeit_icon.png")));
            
        // Check the error based injection method as confirmed
        }else if( "add-errorbased".equals(""+oEvent) ){
            statusPanel.labelErrorBased.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/gradeit_icon.png")));
            
        // Check the blind injection method as confirmed
        }else if( "add-blind".equals(""+oEvent) ){
            statusPanel.labelBlind.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/gradeit_icon.png")));
            
        // Check the time based injection method as confirmed
        }else if( "add-timebased".equals(""+oEvent) ){
            statusPanel.labelTimeBased.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/gradeit_icon.png")));
            
        // Reset the button after the end of preparation
        }else if( "add-fileprivilege".equals(""+oEvent) ){
            outputPanel.filePrivilegeLabel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/gradeit_icon.png")));
            outputPanel.shellfilePrivilegeLabel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/gradeit_icon.png")));
//            outputPanel.uploadfilePrivilegeLabel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/gradeit_icon.png")));
            
        // Reset the button after the end of preparation
        }else if( "remove-normal".equals(""+oEvent) ){
            statusPanel.labelNormal.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_red.png")));
            
        // Reset the button after the end of preparation
        }else if( "remove-errorbased".equals(""+oEvent) ){
            statusPanel.labelErrorBased.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_red.png")));
            
        // Reset the button after the end of preparation
        }else if( "remove-blind".equals(""+oEvent) ){
            statusPanel.labelBlind.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_red.png")));
            
        // Reset the button after the end of preparation
        }else if( "remove-timebased".equals(""+oEvent) ){
            statusPanel.labelTimeBased.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_red.png")));
            
        // Reset the button after the end of preparation
        }else if( "remove-fileprivilege".equals(""+oEvent) ){
            outputPanel.filePrivilegeLabel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_red.png")));
            outputPanel.runFileButton.setEnabled(false);
            outputPanel.shellfilePrivilegeLabel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_red.png")));
            outputPanel.shellrunFileButton.setEnabled(false);
//            outputPanel.uploadfilePrivilegeLabel.setIcon(new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_red.png")));
//            outputPanel.uploadrunFileButton.setEnabled(false);
            
        // Reset the button after the end of preparation
        }else if( "end-preparation".equals(""+oEvent) ){
            inputPanel.submitButton.setText("Connect");
            inputPanel.submitButton.setEnabled(true);
            
            if(model.isInjectionBuilt){
                outputPanel.runFileButton.setEnabled(true);
                outputPanel.shellrunFileButton.setEnabled(true);
//                outputPanel.uploadrunFileButton.setEnabled(true);
            }
            
        // Add a header string to the tab  
        }else if( "add-header".equals(""+oEvent) ){
            headers.append(oEvent.getArg()+"");
            
        }else if( "add-file".equals(""+oEvent) ){
            ArrayList<String> observerEventData = (ArrayList<String>) oEvent.getArg();
            
            String fileName = observerEventData.get(0);
            String data = observerEventData.get(1);
            String fileCompletePath = observerEventData.get(2);
            
            JPopupTextArea fileText = new JPopupTextArea();
            fileText.setText(data);
            RoundJScrollPane scroller = new RoundJScrollPane(fileText);

            fileText.setCaretPosition(0);
            valuesTabbedPane.addTab(fileName+" ", scroller);
            
            // Focus on the new tab
            valuesTabbedPane.setSelectedComponent(scroller);
            
            // Create a custom tab header with close button
            TabHeader header = new TabHeader(valuesTabbedPane, new ImageIcon(getClass().getResource("/com/jsql/images/page_white_text.png")));
            header.setToolTipText(fileCompletePath);
            // Apply the custom header to the tab
            valuesTabbedPane.setTabComponentAt(valuesTabbedPane.indexOfComponent(scroller), header);

//            fileText.requestFocusInWindow();
        }else if( "add-shell".equals(""+oEvent) ){
            String[] observerEventData = (String[]) oEvent.getArg();
            
//            String fileName = observerEventData.get(0);
//            String data = observerEventData.get(1);
            
//            JPopupTextArea fileText = new JPopupTextArea();
//            fileText.setText(data);
//            JScrollPane scroller = new JScrollPane(fileText);
//            valuesTabbedPane.addTab(fileName+" ", scroller);
            UUID l = UUID.randomUUID();
            TestConsole z = new TestConsole(observerEventData[0], model, l, observerEventData[1]);
            consoles.put(l, z);
            
            JScrollPane scroller = new JScrollPane(z);
            valuesTabbedPane.addTab("Webshell ", scroller);
            
            // Focus on the new tab
            valuesTabbedPane.setSelectedComponent(scroller);
            
            // Create a custom tab header with close button
            TabHeader header = new TabHeader(valuesTabbedPane, new ImageIcon(getClass().getResource("/com/jsql/images/application_osx_terminal.png")));
            header.setToolTipText(observerEventData[1]);
            // Apply the custom header to the tab
            valuesTabbedPane.setTabComponentAt(valuesTabbedPane.indexOfComponent(scroller), header);

            z.requestFocusInWindow();
        }else if( "add-shell-cmd".equals(""+oEvent) ){
            Object[] observerEventData = (Object[]) oEvent.getArg();
            UUID m = (UUID)observerEventData[0];
            TestConsole b = consoles.get(m);
            String n = (String)observerEventData[1];
//            System.out.println("#a#"+(b==null));
//            System.out.println("#b#"+(n==null));
//            try{
                b.append(""+n);
//            }catch(Exception e){
//                System.out.println("why?!");
//            }
              b.isEdited[0] = false;
              b.setEditable(true);
              b.setCaret(new BlockCaret2());
              b.append("\n"+b.prompt);
              b.setCaretPosition(b.getDocument().getLength());

//            b.append(b.prompt);
        }
    }
    
    Map<UUID,TestConsole> 
    consoles = new HashMap<UUID,TestConsole>();

    
    /**
     * Empty the interface
     */
    public void resetInterface(){
        // Tree model for refresh the tree
        DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
        // The tree root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        
        // Delete tabs
        valuesTabbedPane.removeAll();
        // Remove tree nodes 
        root.removeAllChildren();
        // Refresh the root
        treeModel.nodeChanged(root);
        // Refresh the tree
        treeModel.reload();
        databaseTree.setRootVisible(true);
        
        // Empty infos tabs
        chunks.setText("");
        headers.setText("");
        binaryArea.setText("");
        
        outputPanel.runFileButton.setEnabled(false);
        outputPanel.shellrunFileButton.setEnabled(false);
//        outputPanel.uploadrunFileButton.setEnabled(false);
        
        // Default status info
        statusPanel.labelDBVersion.setText(statusPanel.INFO_DEFAULT_VALUE);
        statusPanel.labelCurrentDB.setText(statusPanel.INFO_DEFAULT_VALUE);
        statusPanel.labelCurrentUser.setText(statusPanel.INFO_DEFAULT_VALUE);
        statusPanel.labelAuthenticatedUser.setText(statusPanel.INFO_DEFAULT_VALUE);
        
        // Default icon for injection method
        statusPanel.labelNormal.setIcon(statusPanel.squareIcon);
        statusPanel.labelErrorBased.setIcon(statusPanel.squareIcon);
        statusPanel.labelBlind.setIcon(statusPanel.squareIcon);
        statusPanel.labelTimeBased.setIcon(statusPanel.squareIcon);
        
        outputPanel.filePrivilegeLabel.setIcon(statusPanel.squareIcon);
        outputPanel.shellfilePrivilegeLabel.setIcon(statusPanel.squareIcon);
//        outputPanel.uploadfilePrivilegeLabel.setIcon(statusPanel.squareIcon);
    }
}
