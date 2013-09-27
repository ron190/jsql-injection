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
package com.jsql.view;

import java.awt.GridLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.jsql.controller.InjectionController;
import com.jsql.model.InjectionModel;
import com.jsql.model.bean.ElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.view.component.Menubar;
import com.jsql.view.component.popup.JPopupTextArea;
import com.jsql.view.dnd.tab.DnDTabbedPane;
import com.jsql.view.dnd.tab.TabTransferHandler;
import com.jsql.view.dropshadow.ShadowPopupFactory;
import com.jsql.view.interaction.Interaction;
import com.jsql.view.panel.LeftRightBottom;
import com.jsql.view.panel.Statusbar;
import com.jsql.view.panel.TopPanel;
import com.jsql.view.terminal.Terminal;

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
    public InjectionController controller;
    
    // Used to get proxy, path settings
    public InjectionModel model;

    // Tree for database components
    public JTree databaseTree;
    
    // Tabs for values displayed in a table
    public DnDTabbedPane right;

    /**
     * Text area for injection informations:
     * - console: standard readable message,
     * - chunk: data read from web page
     * - header: result of HTTP connection
     * - binary: blind/time progress
     */
    public JPopupTextArea consoleArea;
    public JPopupTextArea chunks;
    public JPopupTextArea headers;
    public JPopupTextArea binaryArea;
    public JPopupTextArea javaDebug;

    // Panel of textfields at the top
    private TopPanel top;

    public LeftRightBottom outputPanel;
    
    // Panel of labels in the statusbar
    private Statusbar statusPanel;

    // List of terminal by unique identifier
    public Map<UUID,Terminal> consoles = new HashMap<UUID,Terminal>();

    public final String CHUNK_VISIBLE = "chunk_visible";
    public final String BINARY_VISIBLE = "binary_visible";
    public final String HEADER_VISIBLE = "header_visible";
    public final String JAVA_VISIBLE = "java_visible";
    
    // Build the GUI: add app icon, tree icons, the 3 main panels
    public GUI(InjectionController controller, InjectionModel model){
        super("jSQL Injection");

        // Define a small and large app icon
        this.setIconImages(GUITools.getIcons());

        GUITools.prepareGUI();
        
        ShadowPopupFactory.install();

        // Object creation after customization
        consoleArea = new JPopupTextArea();
        chunks = new JPopupTextArea();
        headers = new JPopupTextArea();
        binaryArea = new JPopupTextArea();
        javaDebug = new JPopupTextArea();

        right = new DnDTabbedPane();
        TransferHandler handler = new TabTransferHandler();
        right.setTransferHandler(handler);

        // Save model
        this.model = model;
        // Register the view to the model
        this.model.addObserver(this);
        // Save controller
        this.controller = controller;

        this.setJMenuBar(new Menubar(this));

        // Add hotkeys to rootpane ctrl-tab, ctrl-shift-tab, ctrl-w
        GUITools.addShortcut(right);

        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout( new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS) );

        // Textfields at the top
        top = new TopPanel(this.controller, this.model);
        this.add(top);

        // Main panel for tree ans tables in the middle
        JPanel mainPanel = new JPanel(new GridLayout(1,0));
        outputPanel = new LeftRightBottom(this);
        mainPanel.add(outputPanel);
        this.add(mainPanel);

        // Info on the bottom
        statusPanel = new Statusbar();
        this.add(statusPanel);

        // Reduce size of components
        this.pack(); // nécessaire après le masquage des param proxy

        // Size of window
        this.setSize(1024, 768);
        top.submit.requestFocusInWindow();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window
        this.setLocationRelativeTo(null);

        // Define the keyword shortcuts for tabs #Need to work even if the focus is not on tabs
        GUITools.addShortcut(this.getRootPane(), right);
    }

    /**
     *  Map a database element with the corresponding tree node.
     *  The injection model send a database element to the view, then the view access its graphic component to update
     */
    private Map<ElementDatabase, DefaultMutableTreeNode> treeNodeModels = new HashMap<ElementDatabase, DefaultMutableTreeNode>();

    public DefaultMutableTreeNode getNode(ElementDatabase elt){
        return treeNodeModels.get(elt);
    }
    
    public void putNode(ElementDatabase elt, DefaultMutableTreeNode node){
        treeNodeModels.put(elt, node);
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

            Class<?>[] types = new Class[]{com.jsql.view.GUI.class, Object[].class};

            cl.getConstructors();
            Constructor<?> ct = cl.getConstructor(types);

            Interaction o2 = (Interaction) ct.newInstance(new Object[]{this, interaction.getParameters()});
            o2.execute();
        } catch (ClassNotFoundException e) {
            this.model.sendDebugMessage(e);
        } catch (InstantiationException e) {
            this.model.sendDebugMessage(e);
        } catch (IllegalAccessException e) {
            this.model.sendDebugMessage(e);
        } catch (NoSuchMethodException e) {
            this.model.sendDebugMessage(e);
        } catch (SecurityException e) {
            this.model.sendDebugMessage(e);
        } catch (IllegalArgumentException e) {
            this.model.sendDebugMessage(e);
        } catch (InvocationTargetException e) {
            this.model.sendDebugMessage(e);
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
        right.removeAll();
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

        outputPanel.fileManager.setButtonEnable(false);
        outputPanel.shellManager.setButtonEnable(false);
        outputPanel.sqlShellManager.setButtonEnable(false);

        // Default status info
        statusPanel.reset();

        outputPanel.fileManager.changeIcon(GUITools.SQUARE_GREY);
        outputPanel.shellManager.changeIcon(GUITools.SQUARE_GREY);
        outputPanel.sqlShellManager.changeIcon(GUITools.SQUARE_GREY);
    }

    /**
     * Getter for main center panel, composed by left and right tabs
     * @return Center panel
     */
    public LeftRightBottom getOutputPanel(){
        return outputPanel;
    }

    /**
     * Getter for user input panel at the top of window.
     * @return Top panel
     */
    public TopPanel getInputPanel(){
        return top;
    }

    /**
     * Getter for statusbar.
     * @return Statusbar panel
     */
    public Statusbar getStatusPanel(){
        return statusPanel;
    }
}
