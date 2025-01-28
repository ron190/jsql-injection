/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.panel;

import com.jsql.model.InjectionModel;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.view.swing.console.JTextPaneAppender;
import com.jsql.view.swing.console.SimpleConsoleAdapter;
import com.jsql.view.swing.panel.consoles.NetworkTable;
import com.jsql.view.swing.panel.consoles.TabbedPaneNetworkTab;
import com.jsql.view.swing.panel.split.SplitNS;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholderConsole;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.Preferences;

/**
 * A panel with different consoles displayed on the bottom.
 */
public class PanelConsoles extends JPanel {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static final String CONSOLE_JAVA_TOOLTIP = "CONSOLE_JAVA_TOOLTIP";
    public static final String CONSOLE_CHUNK_TOOLTIP = "CONSOLE_CHUNK_TOOLTIP";
    public static final String CONSOLE_BINARY_TOOLTIP = "CONSOLE_BINARY_TOOLTIP";
    public static final String CONSOLE_MAIN_TOOLTIP = "CONSOLE_MAIN_TOOLTIP";

    /**
     * Console for java exception messages.
     */
    private final SimpleConsoleAdapter javaTextPane = new SimpleConsoleAdapter("Java", I18nUtil.valueByKey(PanelConsoles.CONSOLE_JAVA_TOOLTIP));
    
    /**
     * Console for raw SQL results.
     */
    private JTextArea chunkTextArea;

    /**
     * Panel displaying table of HTTP requests and responses.
     */
    private JSplitPane networkSplitPane;

    /**
     * Console for binary representation of characters found with blind/time injection.
     */
    private JTextArea binaryTextArea;

    private final TabbedPaneWheeled tabConsoles = new TabbedPaneWheeled();
    private TabbedPaneNetworkTab tabbedPaneNetworkTab;
    private NetworkTable networkTable;
    
    private final JLabel labelShowNorth = new JLabel(UiUtil.ARROW_UP.getIcon());
    private int dividerLocation = 0;
    
    /**
     * Create panel at the bottom with different consoles to report injection process.
     */
    public PanelConsoles() {
        I18nViewUtil.addComponentForKey(PanelConsoles.CONSOLE_JAVA_TOOLTIP, this.javaTextPane.getProxy());
        this.javaTextPane.getProxy().setEditable(false);
        JTextPaneAppender.registerJavaConsole(this.javaTextPane);
        
        this.initSplit();

        MediatorHelper.register(this.tabConsoles);
        this.initTabsConsoles();
        this.setLayout(new BorderLayout());

        JPanel expandPanel = this.initExpandPanel();
        this.tabConsoles.putClientProperty("JTabbedPane.trailingComponent", expandPanel);
        this.add(this.tabConsoles);

        this.tabConsoles.setAlignmentX(FlowLayout.LEADING);
    }

    private void initSplit() {
        this.networkSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.networkSplitPane.setDividerLocation(600);
        this.networkSplitPane.setPreferredSize(new Dimension(0,0));  // required for correct scroll placement

        this.tabbedPaneNetworkTab = new TabbedPaneNetworkTab();
        this.networkSplitPane.setRightComponent(this.tabbedPaneNetworkTab);
        this.networkTable = new NetworkTable(this.tabbedPaneNetworkTab);
        
        JPanel panelTable = new JPanel(new BorderLayout());  // required for correct scroll placement
        panelTable.add(new JScrollPane(this.networkTable), BorderLayout.CENTER);
        this.networkSplitPane.setLeftComponent(panelTable);
    }

    private void initTabsConsoles() {
        var proxyChunk = new JTextAreaPlaceholderConsole(I18nUtil.valueByKey(PanelConsoles.CONSOLE_CHUNK_TOOLTIP));
        this.chunkTextArea = new JPopupTextArea(proxyChunk).getProxy();
        I18nViewUtil.addComponentForKey(PanelConsoles.CONSOLE_CHUNK_TOOLTIP, proxyChunk);
        this.chunkTextArea.setLineWrap(true);
        this.chunkTextArea.setEditable(false);

        var proxyBinary = new JTextAreaPlaceholderConsole(I18nUtil.valueByKey(PanelConsoles.CONSOLE_BINARY_TOOLTIP));
        I18nViewUtil.addComponentForKey(PanelConsoles.CONSOLE_BINARY_TOOLTIP, proxyBinary);
        this.binaryTextArea = new JPopupTextArea(proxyBinary).getProxy();
        this.binaryTextArea.setLineWrap(true);
        this.binaryTextArea.setEditable(false);

        var consoleTextPane = new SimpleConsoleAdapter("Console", I18nUtil.valueByKey(PanelConsoles.CONSOLE_MAIN_TOOLTIP));
        I18nViewUtil.addComponentForKey(PanelConsoles.CONSOLE_MAIN_TOOLTIP, consoleTextPane.getProxy());
        consoleTextPane.getProxy().setEditable(false);
        JTextPaneAppender.register(consoleTextPane);

        this.buildI18nTab(
            "CONSOLE_MAIN_LABEL",
            PanelConsoles.CONSOLE_MAIN_TOOLTIP,
                UiUtil.CONSOLE.getIcon(),
            new JScrollPane(consoleTextPane.getProxy()),
            0
        );

        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());  // Order is important
        if (preferences.getBoolean(PreferencesUtil.JAVA_VISIBLE, false)) {
            this.insertJavaTab();
        }
        if (preferences.getBoolean(PreferencesUtil.NETWORK_VISIBLE, true)) {
            this.insertNetworkTab();
        }
        if (preferences.getBoolean(PreferencesUtil.CHUNK_VISIBLE, true)) {
            this.insertChunkTab();
        }
        if (preferences.getBoolean(PreferencesUtil.BINARY_VISIBLE, true)) {
            this.insertBooleanTab();
        }

        this.tabConsoles.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int tabIndex = PanelConsoles.this.tabConsoles.indexAtLocation(e.getX(), e.getY());
                if (tabIndex == -1 && e.getButton() == MouseEvent.BUTTON2) {  // middle click on header with no tab
                    SplitNS.getActionHideShowConsole().actionPerformed(null);
                }
            }
        });
        this.tabConsoles.addChangeListener(changeEvent -> {  // Reset Font when tab is selected
            JTabbedPane tabs = this.tabConsoles;
            if (tabs.getSelectedIndex() > -1) {
                var currentTabHeader = tabs.getTabComponentAt(tabs.getSelectedIndex());
                if (currentTabHeader != null) {
                    currentTabHeader.setFont(currentTabHeader.getFont().deriveFont(Font.PLAIN));
                    currentTabHeader.setForeground(UIManager.getColor("TabbedPane.foreground"));
                }
            }
        });
    }

    private JPanel initExpandPanel() {
        var labelShowSouth = new JLabel(UiUtil.ARROW_DOWN.getIcon());
        labelShowSouth.setName("buttonShowSouth");
        labelShowSouth.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SplitNS.getActionHideShowConsole().actionPerformed(null);
            }
        });
        
        this.labelShowNorth.setName("buttonShowNorth");
        this.labelShowNorth.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SplitNS.getActionHideShowResult().actionPerformed(null);
            }
        });

        var panelExpander = new JPanel();
        panelExpander.setLayout(new BoxLayout(panelExpander, BoxLayout.X_AXIS));
        panelExpander.add(Box.createGlue());
        panelExpander.add(this.labelShowNorth);
        panelExpander.add(labelShowSouth);
        return panelExpander;
    }

    public void reset() {
        // Empty infos tabs
        this.chunkTextArea.setText(StringUtils.EMPTY);
        this.binaryTextArea.setText(StringUtils.EMPTY);
        this.javaTextPane.getProxy().setText(StringUtils.EMPTY);

        this.networkTable.getListHttpHeader().clear();
        // Fix #4657, Fix #1860: Multiple Exceptions on setRowCount()
        try {
            ((DefaultTableModel) this.networkTable.getModel()).setRowCount(0);
        } catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }

        this.tabbedPaneNetworkTab.reset();
    }

    /**
     * Add Chunk console to bottom panel.
     */
    public void insertChunkTab() {
        this.buildI18nTab(
            "CONSOLE_CHUNK_LABEL",
            PanelConsoles.CONSOLE_CHUNK_TOOLTIP,
            UiUtil.CHUNK.getIcon(),
            new JScrollPane(this.chunkTextArea),
            1
        );
    }

    /**
     * Add Binary console to bottom panel.
     */
    public void insertBooleanTab() {
        var positionFromChunk = this.tabConsoles.indexOfTab(UiUtil.CHUNK.getIcon()) != -1 ? 1 : 0;
        this.buildI18nTab(
            "CONSOLE_BINARY_LABEL",
            PanelConsoles.CONSOLE_BINARY_TOOLTIP,
            UiUtil.BINARY.getIcon(),
            new JScrollPane(this.binaryTextArea),
            1 + positionFromChunk
        );
    }

    /**
     * Add Network tab to bottom panel.
     */
    public void insertNetworkTab() {
        var positionFromJava = this.tabConsoles.indexOfTab(UiUtil.CUP.getIcon()) != -1 ? 1 : 0;
        this.buildI18nTab(
            "CONSOLE_NETWORK_LABEL",
            "CONSOLE_NETWORK_TOOLTIP",
            UiUtil.NETWORK.getIcon(),
            new JScrollPane(this.networkSplitPane),
            this.tabConsoles.getTabCount() - positionFromJava
        );
    }

    /**
     * Add Java console to bottom panel.
     */
    public void insertJavaTab() {
        this.buildI18nTab(
            "CONSOLE_JAVA_LABEL",
            PanelConsoles.CONSOLE_JAVA_TOOLTIP,
            UiUtil.CUP.getIcon(),
            new JScrollPane(this.javaTextPane.getProxy()),
            this.tabConsoles.getTabCount()
        );
    }
    
    private void buildI18nTab(String keyLabel, String keyTooltip, Icon icon, Component manager, int position) {
        var refJToolTipI18n = new AtomicReference<>(new JToolTipI18n(I18nViewUtil.valueByKey(keyTooltip)));
        
        var labelTab = new JLabel(I18nViewUtil.valueByKey(keyLabel), icon, SwingConstants.CENTER) {
            @Override
            public JToolTip createToolTip() {
                refJToolTipI18n.set(new JToolTipI18n(I18nViewUtil.valueByKey(keyTooltip)));
                return refJToolTipI18n.get();
            }
        };
        
        labelTab.setName(keyLabel);
        labelTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                // Fix #90428: IllegalArgumentException in setSelectedComponent()
                // ArrayIndexOutOfBoundsException #92973 on setSelectedComponent()
                try {
                    PanelConsoles.this.tabConsoles.setSelectedComponent(manager);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
                }
            }
        });
        
        this.tabConsoles.insertTab(I18nViewUtil.valueByKey(keyLabel), icon, manager, null, position);
        this.tabConsoles.setTabComponentAt(this.tabConsoles.indexOfTab(I18nViewUtil.valueByKey(keyLabel)), labelTab);
        
        I18nViewUtil.addComponentForKey(keyLabel, labelTab);
        I18nViewUtil.addComponentForKey(keyTooltip, refJToolTipI18n.get());
        labelTab.setToolTipText(I18nViewUtil.valueByKey(keyTooltip));
    }
    
    public void messageChunk(String text) {
        try {
            this.chunkTextArea.append(text +"\n");
            this.chunkTextArea.setCaretPosition(this.chunkTextArea.getDocument().getLength());
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            // Fix #67063: NullPointerException on chunkTab.append()
            // Fix #4770 on chunkTab.append()
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e.getMessage(), e);
        }
    }
    
    public void messageBinary(String text) {
        try {
            this.binaryTextArea.append(String.format("\t%s", text));
            this.binaryTextArea.setCaretPosition(this.binaryTextArea.getDocument().getLength());
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e.getMessage(), e);
        }
    }
    
    
    // Getter and setter

    public int getDividerLocation() {
        return this.dividerLocation;
    }

    public void setDividerLocation(int location) {
        this.dividerLocation = location;
    }

    public JLabel getLabelShowNorth() {
        return this.labelShowNorth;
    }

    public NetworkTable getNetworkTable() {
        return this.networkTable;
    }

    public TabbedPaneNetworkTab getTabbedPaneNetworkTab() {
        return this.tabbedPaneNetworkTab;
    }
}
