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
package com.jsql.view.swing;

import java.awt.Component;
import java.awt.Font;
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

import com.jsql.model.bean.AbstractElementDatabase;
import com.jsql.model.bean.Request;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.dropshadow.ShadowPopupFactory;
import com.jsql.view.swing.interaction.InteractionCommand;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.PanelStatus;
import com.jsql.view.swing.panel.SplitCenterStatus;
import com.jsql.view.swing.shell.AbstractShell;

/**
 * View in the MVC pattern, defines all the components
 * and process actions sent by the model.<br>
 * Main groups of components:<br>
 * - at the top: textfields input,<br>
 * - at the center: tree on the left, table on the right,<br>
 * - at the bottom: information labels.
 */
@SuppressWarnings("serial")
public class FrameJSql extends JFrame implements Observer {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(FrameJSql.class);

    /**
     * Main center panel, composed by left and right tabs.
     * @return Center panel
     */
    public SplitCenterStatus splitPaneCenter;

    /**
     * List of terminal by unique identifier.
     */
    private Map<UUID, AbstractShell> mapShells = new HashMap<>();

    /**
     *  Map a database element with the corresponding tree node.<br>
     *  The injection model send a database element to the view, then
     *  the view access its graphic component to update.
     */
    private Map<AbstractElementDatabase, DefaultMutableTreeNode> mapNodes = new HashMap<>();
    
    /**
     * Build the GUI: add app icon, tree icons, the 3 main panels.
     */
    public FrameJSql() {
        super("jSQL Injection");
        
        MediatorGui.register(this);
        
        // Define a small and large app icon
        this.setIconImages(HelperGui.getIcons());

        // Load UI before any component
        HelperGui.prepareGUI();
        ShadowPopupFactory.install();
        
        // Save controller
        Menubar menubar = new Menubar();
        this.setJMenuBar(menubar);
        MediatorGui.register(menubar);
        
        // Define the default panel: each component on a vertical line
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        // Textfields at the top
        PanelAddressBar panelAddressBar = new PanelAddressBar();
        this.add(panelAddressBar);
        MediatorGui.register(panelAddressBar);

        // Main panel for tree ans tables in the middle
        JPanel mainPanel = new JPanel(new GridLayout(1, 0));
        this.splitPaneCenter = new SplitCenterStatus();
        mainPanel.add(this.splitPaneCenter);
        this.add(mainPanel);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putInt(
                    SplitCenterStatus.NAME_V_SPLITPANE, 
                    FrameJSql.this.splitPaneCenter.splitManagerResult.getDividerLocation()
                );
                prefs.putInt(
                    SplitCenterStatus.NAME_H_SPLITPANE, 
                    FrameJSql.this.splitPaneCenter.getHeight() - FrameJSql.this.splitPaneCenter.getDividerLocation()
                );
                
                prefs.putBoolean(HelperGui.BINARY_VISIBLE, false);
                prefs.putBoolean(HelperGui.CHUNK_VISIBLE, false);
                prefs.putBoolean(HelperGui.NETWORK_VISIBLE, false);
                prefs.putBoolean(HelperGui.JAVA_VISIBLE, false);
                
                for (int i = 0; i < MediatorGui.tabConsoles().getTabCount(); i++) {
                    if ("Binary".equals(MediatorGui.tabConsoles().getTitleAt(i))) {
                        prefs.putBoolean(HelperGui.BINARY_VISIBLE, true);
                    } else if ("Chunk".equals(MediatorGui.tabConsoles().getTitleAt(i))) {
                        prefs.putBoolean(HelperGui.CHUNK_VISIBLE, true);
                    } else if ("Network".equals(MediatorGui.tabConsoles().getTitleAt(i))) {
                        prefs.putBoolean(HelperGui.NETWORK_VISIBLE, true);
                    } else if ("Java".equals(MediatorGui.tabConsoles().getTitleAt(i))) {
                        prefs.putBoolean(HelperGui.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        // Info on the bottom
        PanelStatus panelStatusBar = new PanelStatus();
        this.add(panelStatusBar);
        MediatorGui.register(panelStatusBar);

        // Reduce size of components
        this.pack(); // nécessaire après le masquage des param proxy

        // Size of window
        this.setSize(1024, 768);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window
        this.setLocationRelativeTo(null);

        // Define the keyword shortcuts for tabs #Need to work even if the focus is not on tabs
        ActionHandler.addShortcut(this.getRootPane(), MediatorGui.tabResults());
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
            Class<?> cl = Class.forName("com.jsql.view.swing.interaction." + interaction.getMessage());
            Class<?>[] types = new Class[]{Object[].class};
            Constructor<?> ct = cl.getConstructor(types);

            InteractionCommand interactionCommand = (InteractionCommand) ct.newInstance(new Object[]{interaction.getParameters()});
            interactionCommand.execute();
        } catch (
            ClassNotFoundException | InstantiationException | 
            IllegalAccessException | NoSuchMethodException | 
            SecurityException | IllegalArgumentException | 
            InvocationTargetException e
        ) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Empty the interface.
     */
    public void resetInterface() {
        for (AbstractElementDatabase supsendableTask : MediatorModel.model().suspendables.keySet()) {
            MediatorModel.model().suspendables.get(supsendableTask).stop();
        }
        
        // Empty tree objects
        MediatorModel.model().suspendables.clear();
        this.mapNodes.clear();
        this.mapShells.clear();
        
        MediatorGui.panelConsoles().listHttpHeader.clear();
        
        // Tree model for refreshing the tree
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();
        // The tree root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Delete tabs
        MediatorGui.tabResults().removeAll();
        // Remove tree nodes
        root.removeAllChildren();
        // Refresh the root
        treeModel.nodeChanged(root);
        // Refresh the tree
        treeModel.reload();
        MediatorGui.treeDatabase().setRootVisible(true);

        // Empty infos tabs
        MediatorGui.panelConsoles().chunkTab.setText("");
        MediatorGui.panelConsoles().binaryTab.setText("");
        ((DefaultTableModel) MediatorGui.panelConsoles().networkTable.getModel()).setRowCount(0);
        MediatorGui.panelConsoles().javaTab.getProxy().setText("");
        
        MediatorGui.panelConsoles().networkTabHeader.setText("");
        MediatorGui.panelConsoles().networkTabParam.setText("");
        MediatorGui.panelConsoles().networkTabResponse.setText("");
        MediatorGui.panelConsoles().networkTabTiming.setText("");
        MediatorGui.panelConsoles().networkTabSource.setText("");
        MediatorGui.panelConsoles().networkTabPreview.setText("");
        
        for (int i = 0; i < MediatorGui.tabConsoles().getTabCount(); i++) {
            Component tabComponent = MediatorGui.tabConsoles().getTabComponentAt(i);
            if (tabComponent != null) {
                tabComponent.setFont(tabComponent.getFont().deriveFont(Font.PLAIN));
            }
        }
        
        MediatorGui.tabManagers().fileManager.setButtonEnable(false);
        MediatorGui.tabManagers().shellManager.setButtonEnable(false);
        MediatorGui.tabManagers().sqlShellManager.setButtonEnable(false);

        // Default status info
        MediatorGui.panelStatus().reset();

        MediatorGui.tabManagers().fileManager.changePrivilegeIcon(HelperGui.SQUARE_GREY);
        MediatorGui.tabManagers().shellManager.changePrivilegeIcon(HelperGui.SQUARE_GREY);
        MediatorGui.tabManagers().sqlShellManager.changePrivilegeIcon(HelperGui.SQUARE_GREY);
        MediatorGui.tabManagers().uploadManager.changePrivilegeIcon(HelperGui.SQUARE_GREY);
    }

    /**
     * Get list of terminal by unique identifier.
     * @return Map of key/value UUID => Terminal
     */
    public final Map<UUID, AbstractShell> getConsoles() {
        return mapShells;
    }
    
    /**
     *  Get the database tree model.
     *  @return Tree model
     */
    public final Map<AbstractElementDatabase, DefaultMutableTreeNode> getTreeNodeModels() {
        return mapNodes;
    }
}
