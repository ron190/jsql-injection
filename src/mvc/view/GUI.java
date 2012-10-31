package mvc.view;

import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import mvc.controller.InjectionController;
import mvc.model.InjectionModel;
import mvc.model.ObserverEvent;
import mvc.model.database.Column;
import mvc.model.database.Database;
import mvc.model.database.ElementDatabase;
import mvc.model.database.Table;
import mvc.view.component.TabHeader;
import mvc.view.component.TablePanel;
import mvc.view.component.TreeNodeModel;

/**
 * View in the MVC pattern, define all the components and process actions sent by the model, 
 * Main groups of components:
 * - at the top: textfields input,
 * - at the center: tree on the left, table on the right,
 * - at the bottom: information labels 
 */
public class GUI extends JFrame implements Observer {
	private static final long serialVersionUID = 9164724117078636255L;
	
	InjectionController controller;
	InjectionModel model;
		
	// Tree for database components
	public JTree databaseTree;
	// Tabs for values displayed in a table
	public JTabbedPane valuesTabbedPane = new JTabbedPane();

	/**
	 * Text area for injection informations:
	 * - console: standard readable message,
	 * - chunk: data read from web page
	 * - header: result of HTTP connection
	 * - binary: blind/time progress
	 */
	public JTextArea consoleArea = new JTextArea();
	public JTextArea chunks = new JTextArea();
	public JTextArea headers = new JTextArea();
	public JTextArea binaryArea = new JTextArea();
	
	// Panel of textfields at the top
	public InputPanel inputPanel;
	// Panel of labels in the statusbar 
	public StatusPanel statusPanel;
	
	// Build the GUI: add app icon, tree icons, the 3 main panels 
	public GUI(InjectionController newController, InjectionModel newModel){		
		super("jSQL Injection");

		// Define a small and large app icon
        try {
			URL urlSmall = this.getClass().getResource("/images/database-icon-16x16.png");
			URL urlBig = this.getClass().getResource("/images/database-icon-32x32.png");
	        ArrayList<Image> images = new ArrayList<Image>();
			images.add( ImageIO.read(urlBig) );
	        images.add( ImageIO.read(urlSmall) );
	        this.setIconImages(images);
		} catch (IOException e) {
			e.printStackTrace();
		}

        // Change the default icon for tree nodes
		UIManager.put("Tree.leafIcon", new ImageIcon(getClass().getResource("/images/server_database.png")));
		UIManager.put("Tree.openIcon", new ImageIcon(getClass().getResource("/images/server_database.png")));
		UIManager.put("Tree.closedIcon", new ImageIcon(getClass().getResource("/images/server_database.png")));
		
		// Save model
		model = newModel;
		// Register the view to the model
		model.addObserver(this);
		// Save controller
		this.controller = newController;
		
		// Menubar
//		//Where the GUI is created:
//		JMenuBar menuBar = new JMenuBar();
//		JMenu menuFile = new JMenu("File");
//		menuFile.add(new JMenuItem("New connection..."));
//		menuFile.add(new JSeparator());
//		menuFile.add(new JMenuItem("Exit"));
//		
//		JMenu menuHelp = new JMenu("Help");
//		menuHelp.add(new JMenuItem("About"));
//		menuHelp.add(new JSeparator());
//		menuHelp.add(new JMenuItem("Preferences"));
//		
//		menuBar.add(menuFile);
//		menuBar.add(menuHelp);
//		
//		this.setJMenuBar(menuBar);
		
		// Define the default panel: each component on a vertical line
		this.getContentPane().setLayout( new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS) );
		
		// Textfields at the top
		inputPanel = new InputPanel(controller, model);
		this.add(inputPanel);

		// Main panel for tree ans tables in the middle
		JPanel mainPanel = new JPanel(new GridLayout(1,0));
		mainPanel.add(new OutputPanel(this));
		this.add(mainPanel);
		
		// A info bar
//		JPanel l = new JPanel();
//		l.setLayout( new BoxLayout(l, BoxLayout.LINE_AXIS) );
//		l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
//		headersText = new JTextField();
//		headersText.setEditable(false);
//		headersText.setForeground(Color.GRAY);
//		headersText.setHorizontalAlignment(JTextField.TRAILING);
//		headersText.setBackground(l.getBackground());
//		headersText.setBorder(BorderFactory.createEmptyBorder());
//		headersText.setMaximumSize( 
//			     new Dimension(Integer.MAX_VALUE, headersText.getPreferredSize().height) );
//		l.add(new JLabel("Headers infos"));
//		l.add(headersText);
//		
//		this.add(l);
		
		// Info on the bottom
		statusPanel = new StatusPanel();
		this.add(statusPanel);
        
		// Reduce size of components 
		this.pack(); // nécessaire après le masquage des param proxy
        // Size of window
		this.setSize(1024, 768);
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
			
//			treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
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
			
//			treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
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
			statusPanel.labelNormal.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		// Check the error based injection method as confirmed
		}else if( "add-errorbased".equals(""+oEvent) ){
			statusPanel.labelErrorBased.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		// Check the blind injection method as confirmed
		}else if( "add-blind".equals(""+oEvent) ){
			statusPanel.labelBlind.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		// Check the time based injection method as confirmed
		}else if( "add-timebased".equals(""+oEvent) ){
			statusPanel.labelTimeBased.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		// Reset the button after the end of preparation
		}else if( "end-preparation".equals(""+oEvent) ){
			inputPanel.submitButton.setText("Connect");
			inputPanel.submitButton.setEnabled(true);
			
		// Add a header string to the tab  
		}else if( "add-header".equals(""+oEvent) ){
			headers.append(oEvent.getArg()+"");
			
		}
	}
	
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
		
		// Empty infos tabs
		chunks.setText("");
		headers.setText("");
		binaryArea.setText("");
		
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
	}
}
