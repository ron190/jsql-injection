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
package com.jsql.view;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.view.action.ActionHandler;
import com.jsql.view.dropshadow.ShadowPopupFactory;
import com.jsql.view.interaction.IInteractionCommand;
import com.jsql.view.menubar.Menubar;
import com.jsql.view.panel.LeftRightBottomPanel;
import com.jsql.view.panel.StatusbarPanel;
import com.jsql.view.panel.TopPanel;
import com.jsql.view.terminal.Terminal;

/**
 * View in the MVC pattern, define all the components and process actions sent by the model,
 * Main groups of components:
 * - at the top: textfields input,
 * - at the center: tree on the left, table on the right,
 * - at the bottom: information labels
 */
@SuppressWarnings("serial")
public class GUI extends JFrame implements Observer {

    public LeftRightBottomPanel outputPanel;
    
    // List of terminal by unique identifier
    public Map<UUID,Terminal> consoles = new HashMap<UUID,Terminal>();

    /**
     *  Map a database element with the corresponding tree node.
     *  The injection model send a database element to the view, then the view access its graphic component to update
     */
    Map<ElementDatabase, DefaultMutableTreeNode> treeNodeModels = new HashMap<ElementDatabase, DefaultMutableTreeNode>();
    
    public DefaultMutableTreeNode getNode(ElementDatabase elt){
    	return treeNodeModels.get(elt);
    }
    
    public void putNode(ElementDatabase elt, DefaultMutableTreeNode node){
    	treeNodeModels.put(elt, node);
    }
    

    // Build the GUI: add app icon, tree icons, the 3 main panels
    public GUI(){
        super("jSQL Injection");
        
        GUIMediator.register(this);

        // Define a small and large app icon
        this.setIconImages(GUITools.getIcons());

        // Load UI before any component
        GUITools.prepareGUI();
        ShadowPopupFactory.install();
        
        // Register the view to the model
        GUIMediator.model().addObserver(this);
        
        // Save controller
        GUIMediator.register(new Menubar());
        this.setJMenuBar(GUIMediator.menubar());

        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout( new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS) );

        // Textfields at the top
        GUIMediator.register(new TopPanel());
        this.add(GUIMediator.top());

        // Main panel for tree ans tables in the middle
        JPanel mainPanel = new JPanel(new GridLayout(1,0));
        outputPanel = new LeftRightBottomPanel();
        mainPanel.add(outputPanel);
        this.add(mainPanel);

        GUIMediator.gui().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putInt(outputPanel.VERTICALSPLITTER_PREFNAME, outputPanel.leftRight.getDividerLocation());
                prefs.putInt(outputPanel.HORIZONTALSPLITTER_PREFNAME, outputPanel.getHeight() - outputPanel.getDividerLocation());
                
                prefs.putBoolean(GUITools.BINARY_VISIBLE, false);
                prefs.putBoolean(GUITools.CHUNK_VISIBLE, false);
                prefs.putBoolean(GUITools.NETWORK_VISIBLE, false);
                prefs.putBoolean(GUITools.JAVA_VISIBLE, false);
                
                for(int i=0; i < GUIMediator.bottom().getTabCount() ;i++){
                    if (GUIMediator.bottom().getTitleAt(i).equals("Binary")) {
                        prefs.putBoolean(GUITools.BINARY_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Chunk")) {
                        prefs.putBoolean(GUITools.CHUNK_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Network")) {
                        prefs.putBoolean(GUITools.NETWORK_VISIBLE, true);
                    }else if (GUIMediator.bottom().getTitleAt(i).equals("Java")) {
                        prefs.putBoolean(GUITools.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        // Info on the bottom
        GUIMediator.register(new StatusbarPanel());
        this.add(GUIMediator.status());

        // Reduce size of components
        this.pack(); // nécessaire après le masquage des param proxy

        // Size of window
        this.setSize(1024, 768);
//        GUIMediator.top().submitAddressBar.requestFocusInWindow();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window
        this.setLocationRelativeTo(null);

        // Define the keyword shortcuts for tabs #Need to work even if the focus is not on tabs
        ActionHandler.addShortcut(this.getRootPane(), GUIMediator.right());
        ActionHandler.addShortcut();
    }

    /**
     * Observer pattern
     * Receive an update order from the model:
     * - Use the Request message to get the Interaction class
     * - Pass the parameters to that class
     */
    @Override
    public void update(Observable model, Object newInteraction) {
        Request interaction = (Request) newInteraction;

        try {
            Class<?> cl = Class.forName("com.jsql.view.interaction." + interaction.getMessage());
            Class<?>[] types = new Class[]{Object[].class};
            Constructor<?> ct = cl.getConstructor(types);

            IInteractionCommand o2 = (IInteractionCommand) ct.newInstance(new Object[]{interaction.getParameters()});
            o2.execute();
        } catch (ClassNotFoundException e) {
        	InjectionModel.logger.error(e, e);
        } catch (InstantiationException e) {
            InjectionModel.logger.error(e, e);
        } catch (IllegalAccessException e) {
            InjectionModel.logger.error(e, e);
        } catch (NoSuchMethodException e) {
            InjectionModel.logger.error(e, e);
        } catch (SecurityException e) {
            InjectionModel.logger.error(e, e);
        } catch (IllegalArgumentException e) {
            InjectionModel.logger.error(e, e);
        } catch (InvocationTargetException e) {
            InjectionModel.logger.error(e, e);
        }
    }

    /**
     * Empty the interface
     */
    public void resetInterface(){
    	// Empty tree objects
    	treeNodeModels.clear();
    	consoles.clear();
    	
        // Tree model for refresh the tree
        DefaultTreeModel treeModel = (DefaultTreeModel) GUIMediator.databaseTree().getModel();
        // The tree root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Delete tabs
        GUIMediator.right().removeAll();
        // Remove tree nodes
        root.removeAllChildren();
        // Refresh the root
        treeModel.nodeChanged(root);
        // Refresh the tree
        treeModel.reload();
        GUIMediator.databaseTree().setRootVisible(true);

        // Empty infos tabs
        GUIMediator.bottomPanel().chunks.setText("");
        ((DefaultTableModel) GUIMediator.bottomPanel().networkTable.getModel()).setRowCount(0);
        GUIMediator.bottomPanel().binaryArea.setText("");

//        GUIMediator.left().fileManager.setButtonEnable(false);
//        GUIMediator.left().shellManager.setButtonEnable(false);
//        GUIMediator.left().sqlShellManager.setButtonEnable(false);

        // Default status info
        GUIMediator.status().reset();

//        GUIMediator.left().fileManager.changeIcon(GUITools.SQUARE_GREY);
//        GUIMediator.left().shellManager.changeIcon(GUITools.SQUARE_GREY);
//        GUIMediator.left().sqlShellManager.changeIcon(GUITools.SQUARE_GREY);
    }

    /**
     * Getter for main center panel, composed by left and right tabs
     * @return Center panel
     */
    public LeftRightBottomPanel getOutputPanel(){
        return outputPanel;
    }
}
