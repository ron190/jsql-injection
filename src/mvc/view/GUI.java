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
import javax.swing.JTextField;
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


public class GUI extends JFrame implements Observer {
	private static final long serialVersionUID = 9164724117078636255L;
	
	InjectionController controller;
	InjectionModel model;
		
	public JTree databaseTree;
	public JTabbedPane valuesTabbedPane = new JTabbedPane();

	public JTextArea consoleArea = new JTextArea();
	public JTextArea chunks = new JTextArea();
	public JTextArea headers = new JTextArea();
	public JTextArea binaryArea = new JTextArea();
	
	public InputPanel inputPanel;
	public StatusPanel statusPanel;
	public JTextField headersText;
	
	public GUI(InjectionController newController, InjectionModel newModel){		
		super("jSQL Injection");

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

		UIManager.put("Tree.leafIcon", new ImageIcon(getClass().getResource("/images/server_database.png")));
		UIManager.put("Tree.openIcon", new ImageIcon(getClass().getResource("/images/server_database.png")));
		UIManager.put("Tree.closedIcon", new ImageIcon(getClass().getResource("/images/server_database.png")));
		
		model = newModel;
		model.addObserver(this);
		this.controller = newController;
		
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
		
		this.getContentPane().setLayout( new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS) );
		JPanel mainPanel = new JPanel(new GridLayout(1,0));
		mainPanel.add(new OutputPanel(this));
		
		inputPanel = new InputPanel(controller, model);
		statusPanel = new StatusPanel();
		this.add(inputPanel);
		this.add(mainPanel);
		
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
		this.add(statusPanel);
        
		this.pack(); // nécessaire après le masquage des param proxy
        this.setSize(1024, 768);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		new ActionHandler(this.getRootPane(), valuesTabbedPane);
	}
	
	Map<ElementDatabase,DefaultMutableTreeNode> 
		treeNodeModels = new HashMap<ElementDatabase,DefaultMutableTreeNode>();
	
	@Override
	public void update(Observable arg0, Object arg1) {
		InjectionModel model = (InjectionModel) arg0;
		ObserverEvent oEvent = (ObserverEvent) arg1;
		DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		
		/* Design Pattern */
		if( "logs-message".equals(""+oEvent) ){
			chunks.append(""+oEvent.getArg());
			chunks.setCaretPosition(chunks.getDocument().getLength());
			
		}else if( "binary-message".equals(""+oEvent) ){
			binaryArea.append(""+oEvent.getArg());
			binaryArea.setCaretPosition(binaryArea.getDocument().getLength());
			
		}else if( "console-message".equals(""+oEvent) ){
			consoleArea.append(""+oEvent.getArg());
			consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
			
		}else if( "update-progressbar".equals(""+oEvent) ){
			Object[] progressionData = (Object[]) oEvent.getArg();
			ElementDatabase dataElementDatabase = (ElementDatabase) progressionData[0];
			int dataCount = (Integer) progressionData[1];
			
			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.childUpgradeCount = dataCount;
			
			treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "start-indeterminate-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasIndeterminatedProgress = true;
			
//			treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "end-indeterminate-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasIndeterminatedProgress = false;
			progressingTreeNodeModel.isRunning = false;
			
			treeModel.nodeChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "start-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasProgress = true;
			
//			treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "end-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasProgress = false;
			progressingTreeNodeModel.isRunning = false;
			progressingTreeNodeModel.childUpgradeCount = 0;
			
			treeModel.nodeChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "add-info".equals(""+oEvent) ){
			statusPanel.labelDBVersion.setText( model.versionDB );
			statusPanel.labelCurrentDB.setText( model.currentDB );
			statusPanel.labelCurrentUser.setText( model.currentUser );
			statusPanel.labelAuthenticatedUser.setText( model.authenticatedUser );
			
		}else if( "add-databases".equals(""+oEvent) ){
			List<?> newDatabases = (ArrayList<?>) oEvent.getArg();
			for(Object o: newDatabases){
				Database d = (Database) o;
				TreeNodeModel<Database> newTreeNodeModel = new TreeNodeModel<Database>(d);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
				treeNodeModels.put(d, newNode);
				root.add(newNode);
			}
			
			treeModel.reload(root); // partial nodes, forces reload
			databaseTree.expandPath( new TreePath(root.getPath()) ); // expands root
			
		}else if( "add-tables".equals(""+oEvent) ){
			List<?> newTables = (ArrayList<?>) oEvent.getArg();
			DefaultMutableTreeNode databaseNode = null;
			
			for(Object o: newTables){
				Table t = (Table) o;
				TreeNodeModel<Table> newTreeNodeModel = new TreeNodeModel<Table>(t);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
				treeNodeModels.put(t, newNode);
				
				databaseNode = treeNodeModels.get(t.getParent());
		        treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());
			}
			
			if(databaseNode != null){
				databaseTree.expandPath( new TreePath(databaseNode.getPath()) );
				((TreeNodeModel<?>) databaseNode.getUserObject()).hasBeenSearched = true;
			}

		}else if( "add-columns".equals(""+oEvent) ){
			List<?> newColumns = (List<?>) oEvent.getArg();
			DefaultMutableTreeNode tableNode = null;
			
			for(Object o: newColumns){
				Column c = (Column) o;
				TreeNodeModel<Column> newTreeNodeModel = new TreeNodeModel<Column>(c);
				newTreeNodeModel.hasCheckBox = true;
				
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
				tableNode = treeNodeModels.get(c.getParent());
				
		        treeModel.insertNodeInto(newNode, tableNode, tableNode.getChildCount());
			}
			
			if(tableNode != null){
				databaseTree.expandPath( new TreePath(tableNode.getPath()) );
				((TreeNodeModel<?>) tableNode.getUserObject()).hasBeenSearched = true;
			}

		}else if( "add-values".equals(""+oEvent) ){
			Object[] observerEventData = (Object[]) oEvent.getArg();
	        
			String[] columnNames = (String[]) observerEventData[0];
			String[][] data = (String[][]) observerEventData[1];
			ElementDatabase table = (ElementDatabase) observerEventData[2];
			
			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(table).getUserObject();
			progressingTreeNodeModel.childUpgradeCount = table.getCount(); // ends progress
			progressingTreeNodeModel.isRunning = false;

			TablePanel newTableJPanel = new TablePanel(data, columnNames, valuesTabbedPane);
			
			valuesTabbedPane.addTab(table.getParent()+"."+table+" "+
					"("+(columnNames.length-2)+" fields) ",newTableJPanel);
			valuesTabbedPane.setSelectedComponent(newTableJPanel);
			
			TabHeader header = new TabHeader(valuesTabbedPane);
			valuesTabbedPane.setTabComponentAt(valuesTabbedPane.indexOfComponent(newTableJPanel), header);
			
		}else if( "add-normal".equals(""+oEvent) ){
			statusPanel.labelNormal.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		}else if( "add-errorbased".equals(""+oEvent) ){
			statusPanel.labelErrorBased.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		}else if( "add-blind".equals(""+oEvent) ){
			statusPanel.labelBlind.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		}else if( "add-timebased".equals(""+oEvent) ){
			statusPanel.labelTimeBased.setIcon(new ImageIcon(getClass().getResource("/images/gradeit_icon.png")));
			
		}else if( "end-preparation".equals(""+oEvent) ){
			inputPanel.submitButton.setText("Connect"); // pas de pb rencontré: stop changé en submit
			inputPanel.submitButton.setEnabled(true);
			
		}else if( "add-header".equals(""+oEvent) ){
			headers.append(oEvent.getArg()+"");
			
		}
	}
	
	public void resetInterface(){
		DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		
		valuesTabbedPane.removeAll();
		root.removeAllChildren();
		treeModel.nodeChanged(root);
		treeModel.reload();
		
		chunks.setText("");
		headers.setText("");
		binaryArea.setText("");
		
		statusPanel.labelDBVersion.setText(statusPanel.INFO_DEFAULT_VALUE);
		statusPanel.labelCurrentDB.setText(statusPanel.INFO_DEFAULT_VALUE);
		statusPanel.labelCurrentUser.setText(statusPanel.INFO_DEFAULT_VALUE);
		statusPanel.labelAuthenticatedUser.setText(statusPanel.INFO_DEFAULT_VALUE);
		
		statusPanel.labelNormal.setIcon(statusPanel.squareIcon);
		statusPanel.labelErrorBased.setIcon(statusPanel.squareIcon);
		statusPanel.labelBlind.setIcon(statusPanel.squareIcon);
		statusPanel.labelTimeBased.setIcon(statusPanel.squareIcon);
	}
}
