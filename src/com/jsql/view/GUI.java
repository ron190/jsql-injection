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

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.view.action.ActionHandler;
import com.jsql.view.dropshadow.ShadowPopupFactory;
import com.jsql.view.interaction.IInteractionCommand;
import com.jsql.view.menubar.Menubar;
import com.jsql.view.panel.PanelLeftRightBottom;
import com.jsql.view.panel.PanelStatusbar;
import com.jsql.view.panel.PanelTop;
import com.jsql.view.terminal.AbstractTerminal;

/**
 * View in the MVC pattern, defines all the components
 * and process actions sent by the model.<br>
 * Main groups of components:<br>
 * - at the top: textfields input,<br>
 * - at the center: tree on the left, table on the right,<br>
 * - at the bottom: information labels.
 */
@SuppressWarnings("serial")
public class GUI extends JFrame implements Observer {
    /**
     * Main center panel, composed by left and right tabs.
     * @return Center panel
     */
    public PanelLeftRightBottom outputPanel;

    /**
     * List of terminal by unique identifier.
     */
    private Map<UUID, AbstractTerminal> consoles = new HashMap<UUID, AbstractTerminal>();

    /**
     *  Map a database element with the corresponding tree node.<br>
     *  The injection model send a database element to the view, then
     *  the view access its graphic component to update.
     */
    private Map<AbstractElementDatabase, DefaultMutableTreeNode> treeNodeModels
                = new HashMap<AbstractElementDatabase, DefaultMutableTreeNode>();
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(GUI.class);

    /**
     * Build the GUI: add app icon, tree icons, the 3 main panels.
     */
    public GUI() {
        super("jSQL Injection");
        
        MediatorGUI.register(this);

        // Define a small and large app icon
        this.setIconImages(ToolsGUI.getIcons());

        // Load UI before any component
        ToolsGUI.prepareGUI();
        ShadowPopupFactory.install();
        
        // Register the view to the model
        MediatorGUI.model().addObserver(this);
        
        // Save controller
        MediatorGUI.register(new Menubar());
        this.setJMenuBar(MediatorGUI.menubar());

        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        // Textfields at the top
        MediatorGUI.register(new PanelTop());
        this.add(MediatorGUI.top());

        // Main panel for tree ans tables in the middle
        JPanel mainPanel = new JPanel(new GridLayout(1, 0));
        this.outputPanel = new PanelLeftRightBottom();
        mainPanel.add(this.outputPanel);
        this.add(mainPanel);

        MediatorGUI.gui().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putInt(PanelLeftRightBottom.VERTICALSPLITTER_PREFNAME, GUI.this.outputPanel.leftRight.getDividerLocation());
                prefs.putInt(PanelLeftRightBottom.HORIZONTALSPLITTER_PREFNAME, GUI.this.outputPanel.getHeight() - GUI.this.outputPanel.getDividerLocation());
                
                prefs.putBoolean(ToolsGUI.BINARY_VISIBLE, false);
                prefs.putBoolean(ToolsGUI.CHUNK_VISIBLE, false);
                prefs.putBoolean(ToolsGUI.NETWORK_VISIBLE, false);
                prefs.putBoolean(ToolsGUI.JAVA_VISIBLE, false);
                
                for (int i = 0; i < MediatorGUI.bottom().getTabCount(); i++) {
                    if ("Binary".equals(MediatorGUI.bottom().getTitleAt(i))) {
                        prefs.putBoolean(ToolsGUI.BINARY_VISIBLE, true);
                    } else if ("Chunk".equals(MediatorGUI.bottom().getTitleAt(i))) {
                        prefs.putBoolean(ToolsGUI.CHUNK_VISIBLE, true);
                    } else if ("Network".equals(MediatorGUI.bottom().getTitleAt(i))) {
                        prefs.putBoolean(ToolsGUI.NETWORK_VISIBLE, true);
                    } else if ("Java".equals(MediatorGUI.bottom().getTitleAt(i))) {
                        prefs.putBoolean(ToolsGUI.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        // Info on the bottom
        MediatorGUI.register(new PanelStatusbar());
        this.add(MediatorGUI.status());

        // Reduce size of components
        this.pack(); // nécessaire après le masquage des param proxy

        // Size of window
        this.setSize(1024, 768);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window
        this.setLocationRelativeTo(null);

        // Define the keyword shortcuts for tabs #Need to work even if the focus is not on tabs
        ActionHandler.addShortcut(this.getRootPane(), MediatorGUI.right());
        ActionHandler.addTextFieldShortcutSelectAll();
    }

    /**
     * Observer pattern.<br>
     * Receive an update order from the model:<br>
     * - Use the Request message to get the Interaction class,<br>
     * - Pass the parameters to that class.
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
            LOGGER.error(e, e);
        } catch (InstantiationException e) {
            LOGGER.error(e, e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e, e);
        } catch (NoSuchMethodException e) {
            LOGGER.error(e, e);
        } catch (SecurityException e) {
            LOGGER.error(e, e);
        } catch (IllegalArgumentException e) {
            LOGGER.error(e, e);
        } catch (InvocationTargetException e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Empty the interface.
     */
    public void resetInterface() {
        // Empty tree objects
        this.treeNodeModels.clear();
        this.consoles.clear();
        
        /*
         * TODO : clear properly paused processes remaining when another new
         * injection is run.
         */
        // GUIMediator.model().suspendables.clear();
        
        MediatorGUI.bottomPanel().listHTTPHeader.clear();
        
        // Tree model for refreshing the tree
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGUI.databaseTree().getModel();
        // The tree root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Delete tabs
        MediatorGUI.right().removeAll();
        // Remove tree nodes
        root.removeAllChildren();
        // Refresh the root
        treeModel.nodeChanged(root);
        // Refresh the tree
        treeModel.reload();
        MediatorGUI.databaseTree().setRootVisible(true);

        // Empty infos tabs
        MediatorGUI.bottomPanel().chunks.setText("");
        ((DefaultTableModel) MediatorGUI.bottomPanel().networkTable.getModel()).setRowCount(0);
        MediatorGUI.bottomPanel().binaryArea.setText("");

        MediatorGUI.left().fileManager.setButtonEnable(false);
        MediatorGUI.left().shellManager.setButtonEnable(false);
        MediatorGUI.left().sqlShellManager.setButtonEnable(false);

        // Default status info
        MediatorGUI.status().reset();

        MediatorGUI.left().fileManager.changePrivilegeIcon(ToolsGUI.SQUARE_GREY);
        MediatorGUI.left().shellManager.changePrivilegeIcon(ToolsGUI.SQUARE_GREY);
        MediatorGUI.left().sqlShellManager.changePrivilegeIcon(ToolsGUI.SQUARE_GREY);
    }

    /**
     * Get list of terminal by unique identifier.
     * @return Map of key/value UUID => Terminal
     */
    public final Map<UUID, AbstractTerminal> getConsoles() {
        return consoles;
    }
    
    /**
     *  Get the database tree model.
     *  @return Tree model
     */
    public final Map<AbstractElementDatabase, DefaultMutableTreeNode>
                    getTreeNodeModels() {
        return treeNodeModels;
    }
}
