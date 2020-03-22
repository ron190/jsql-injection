/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.view.interaction.ObserverInteraction;
import com.jsql.view.swing.action.ActionHandler;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.split.SplitHorizontalTopBottom;
import com.jsql.view.swing.shadow.ShadowPopupFactory;
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
public class JFrameView extends JFrame {

    /**
     * Main center panel, composed by left and right tabs.
     * @return Center panel
     */
    private SplitHorizontalTopBottom splitHorizontalTopBottom;

    /**
     * List of terminal by unique identifier.
     */
    private Map<UUID, AbstractShell> mapShells = new HashMap<>();

    /**
     *  Map a database element with the corresponding tree node.<br>
     *  The injection model send a database element to the view, then
     *  the view access its graphic component to update.
     */
    private transient Map<AbstractElementDatabase, DefaultMutableTreeNode> mapNodes = new HashMap<>();
    
    private transient ObserverInteraction observer = new ObserverInteraction("com.jsql.view.swing.interaction");
    
    /**
     * Build the GUI: add app icon, tree icons, the 3 main panels.
     * @param injectionModel
     */
    public JFrameView() {
        super("jSQL Injection");
        
        MediatorGui.register(this);

        // Load UI before any component
        HelperUi.prepareGUI();
        ShadowPopupFactory.install();
        
        this.initializePaneComponents();
        this.initializeWindow();
        this.initializeShortcuts();
    }

    private void initializeWindow() {
        
        // Define a small and large app icon
        this.setIconImages(HelperUi.getIcons());

        this.addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosing(WindowEvent e) {
                
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                prefs.putInt(
                    SplitHorizontalTopBottom.getNameVSplitpane(),
                    JFrameView.this.splitHorizontalTopBottom.getSplitVerticalLeftRight().getDividerLocation()
                );
                
                // Divider location change when window is maximized, we can't save getDividerLocation()
                prefs.putInt(
                    SplitHorizontalTopBottom.getNameHSplitpane(),
                    JFrameView.this.splitHorizontalTopBottom.getHeight() - JFrameView.this.splitHorizontalTopBottom.getDividerLocation()
                );
                
                prefs.putBoolean(HelperUi.BINARY_VISIBLE, false);
                prefs.putBoolean(HelperUi.CHUNK_VISIBLE, false);
                prefs.putBoolean(HelperUi.NETWORK_VISIBLE, false);
                prefs.putBoolean(HelperUi.JAVA_VISIBLE, false);
                
                for (int i = 0 ; i < MediatorGui.tabConsoles().getTabCount() ; i++) {
                    if ("CONSOLE_BINARY_LABEL".equals(MediatorGui.tabConsoles().getTabComponentAt(i).getName())) {
                        prefs.putBoolean(HelperUi.BINARY_VISIBLE, true);
                    } else if ("CONSOLE_CHUNK_LABEL".equals(MediatorGui.tabConsoles().getTabComponentAt(i).getName())) {
                        prefs.putBoolean(HelperUi.CHUNK_VISIBLE, true);
                    } else if ("CONSOLE_NETWORK_LABEL".equals(MediatorGui.tabConsoles().getTabComponentAt(i).getName())) {
                        prefs.putBoolean(HelperUi.NETWORK_VISIBLE, true);
                    } else if ("CONSOLE_JAVA_LABEL".equals(MediatorGui.tabConsoles().getTabComponentAt(i).getName())) {
                        prefs.putBoolean(HelperUi.JAVA_VISIBLE, true);
                    }
                }
            }
        });
        
        // Size of window
        this.setSize(1024, 768);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Center the window
        this.setLocationRelativeTo(null);
    }

    private void initializeShortcuts() {
        
        // Define the keyword shortcuts for tabs #Need to work even if the focus is not on tabs
        ActionHandler.addShortcut(this.getRootPane(), MediatorGui.tabResults());
        ActionHandler.addTextFieldShortcutSelectAll();
    }

    private void initializePaneComponents() {
        
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

        // Main panel for tree and tables in the middle
        JPanel mainPanel = new JPanel(new GridLayout(1, 0));
        this.splitHorizontalTopBottom = new SplitHorizontalTopBottom();
        mainPanel.add(this.splitHorizontalTopBottom);
        this.add(mainPanel);
        
        menubar.switchLocale(Locale.ENGLISH, I18n.getLocaleDefault(), true);
    }

    /**
     * Empty the interface.
     */
    public void resetInterface() {
        
        if (MediatorModel.model().getMediatorVendor().getVendorByUser() == MediatorModel.model().getMediatorVendor().getAuto()) {
            MediatorGui.panelAddressBar().getMenuVendor().setText(MediatorModel.model().getMediatorVendor().getAuto().toString());
        }
        
        MediatorGui.panelAddressBar().getMenuStrategy().setText("Strategy auto");
        
        for (int i = 0 ; i < MediatorGui.panelAddressBar().getMenuStrategy().getItemCount() ; i++) {
            MediatorGui.panelAddressBar().getMenuStrategy().getItem(i).setEnabled(false);
        }
        
        // TODO remove Error strategy magic number 2
        ((JMenu) MediatorGui.panelAddressBar().getMenuStrategy().getItem(2)).removeAll();
        MediatorGui.panelAddressBar().getGroupStrategy().clearSelection();
        
        this.mapNodes.clear();
        this.mapShells.clear();
        
        MediatorGui.panelConsoles().reset();
        
        // Tree model for refreshing the tree
        DefaultTreeModel treeModel = (DefaultTreeModel) MediatorGui.treeDatabase().getModel();
        // The tree root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Remove tree nodes
        root.removeAllChildren();
        // Refresh the root
        treeModel.nodeChanged(root);
        // Refresh the tree
        treeModel.reload();
        
        MediatorGui.treeDatabase().setRootVisible(true);
        
        for (int i = 0 ; i < MediatorGui.tabConsoles().getTabCount() ; i++) {
            Component tabComponent = MediatorGui.tabConsoles().getTabComponentAt(i);
            if (tabComponent != null) {
                tabComponent.setFont(tabComponent.getFont().deriveFont(Font.PLAIN));
            }
        }
        
        Stream.of(
            MediatorGui.managerUpload(),
            MediatorGui.managerFile(),
            MediatorGui.managerWebshell(),
            MediatorGui.managerSqlshell()
        ).forEach(managerList -> {
            managerList.setButtonEnable(false);
            managerList.changePrivilegeIcon(HelperUi.ICON_SQUARE_GREY);
        });
    }
    
    // Getters and setters

    /**
     * Get list of terminal by unique identifier.
     * @return Map of key/value UUID => Terminal
     */
    public final Map<UUID, AbstractShell> getConsoles() {
        return this.mapShells;
    }
    
    /**
     *  Get the database tree model.
     *  @return Tree model
     */
    public final Map<AbstractElementDatabase, DefaultMutableTreeNode> getTreeNodeModels() {
        return this.mapNodes;
    }

    public ObserverInteraction getObserver() {
        return this.observer;
    }

    public SplitHorizontalTopBottom getSplitHorizontalTopBottom() {
        return this.splitHorizontalTopBottom;
    }
}
