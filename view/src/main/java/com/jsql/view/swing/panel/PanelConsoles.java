/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.OverlayLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.console.JTextPaneAppender;
import com.jsql.view.swing.console.JavaConsoleAdapter;
import com.jsql.view.swing.console.SimpleConsoleAdapter;
import com.jsql.view.swing.panel.consoles.NetworkTable;
import com.jsql.view.swing.panel.consoles.TabbedPaneNetworkTab;
import com.jsql.view.swing.panel.split.SplitHorizontalTopBottom;
import com.jsql.view.swing.scrollpane.JScrollIndicator;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.tab.TabConsoles;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholderConsole;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * A panel with different consoles displayed on the bottom.
 */
@SuppressWarnings("serial")
public class PanelConsoles extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Console for java exception messages.
     */
    private JavaConsoleAdapter javaTextPane = new JavaConsoleAdapter("Java", "Java unhandled exception");
    
    /**
     * Console for raw SQL results.
     */
    private JTextArea chunkTextArea;

    /**
     * Panel displaying table of HTTP requests and responses.
     */
    private JSplitPaneWithZeroSizeDivider networkSplitPane;

    /**
     * Console for binary representation of characters found with blind/time injection.
     */
    private JTextArea binaryTextArea;

    private TabConsoles tabConsoles = new TabConsoles();
    private TabbedPaneNetworkTab tabbedPaneNetworkTab;
    private NetworkTable networkTable;
    
    private BasicArrowButton buttonShowNorth = new BasicArrowButton(SwingConstants.NORTH);
    private int location = 0;
    
    /**
     * Create panel at the bottom with different consoles to report injection process.
     */
    public PanelConsoles() {
        
        this.javaTextPane.getProxy().setEditable(false);

        JTextPaneAppender.register(this.javaTextPane);
        
        this.initializeSplit();

        MediatorHelper.register(this.tabConsoles);
        
        this.initializeTabsConsoles();

        this.setLayout(new OverlayLayout(this));

        JPanel expandPanel = this.initializeExpandPanel();
        
        this.add(expandPanel);
        this.add(this.tabConsoles);

        // Do Overlay
        expandPanel.setAlignmentX(FlowLayout.TRAILING);
        expandPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        this.tabConsoles.setAlignmentX(FlowLayout.LEADING);
        this.tabConsoles.setAlignmentY(Component.TOP_ALIGNMENT);
    }

    private void initializeSplit() {
        
        this.networkSplitPane = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT);
        
        this.networkSplitPane.setResizeWeight(1);
        this.networkSplitPane.setDividerSize(0);
        this.networkSplitPane.setDividerLocation(600);
        this.networkSplitPane.setBorder(BorderFactory.createEmptyBorder());
        
        this.tabbedPaneNetworkTab = new TabbedPaneNetworkTab();
        
        this.networkSplitPane.setRightComponent(this.tabbedPaneNetworkTab);
        
        this.networkTable = new NetworkTable(this.tabbedPaneNetworkTab);
        
        JScrollIndicator scrollerNetwork = this.initializeScrollerTable();
        this.networkSplitPane.setLeftComponent(scrollerNetwork);
    }

    private JScrollIndicator initializeScrollerTable() {
        
        var scrollerNetwork = new JScrollIndicator(this.networkTable, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollerNetwork.getScrollPane().setBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        scrollerNetwork.getScrollPane().setViewportBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1));
        
        AdjustmentListener singleItemScroll = adjustmentEvent -> {
            
            // The user scrolled the List (using the bar, mouse wheel or something else):
            if (adjustmentEvent.getAdjustmentType() == AdjustmentEvent.TRACK) {
                
                // Jump to the next "block" (which is a row".
                adjustmentEvent.getAdjustable().setBlockIncrement(100);
                adjustmentEvent.getAdjustable().setUnitIncrement(100);
            }
        };

        scrollerNetwork.getScrollPane().getVerticalScrollBar().addAdjustmentListener(singleItemScroll);
        scrollerNetwork.getScrollPane().getHorizontalScrollBar().addAdjustmentListener(singleItemScroll);
        
        return scrollerNetwork;
    }

    private void initializeTabsConsoles() {
        
        this.chunkTextArea = new JPopupTextArea(new JTextAreaPlaceholderConsole("Raw data extracted during injection")).getProxy();
        this.chunkTextArea.setEditable(false);
        
        this.binaryTextArea = new JPopupTextArea(new JTextAreaPlaceholderConsole("Characters extracted during blind or time injection")).getProxy();
        this.binaryTextArea.setEditable(false);

        this.chunkTextArea.setLineWrap(true);
        this.binaryTextArea.setLineWrap(true);
        
        var consoleTextPane = new SimpleConsoleAdapter("Console", "Event logging");
        
        // Object creation after customization
        consoleTextPane.getProxy().setEditable(false);

        JTextPaneAppender.register(consoleTextPane);
        
        this.tabConsoles.setUI(new CustomMetalTabbedPaneUI() {
            
            @Override protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                
                return Math.max(80, super.calculateTabWidth(tabPlacement, tabIndex, metrics));
            }
        });
        
        this.tabConsoles.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

        this.buildI18nTab(
            "CONSOLE_MAIN_LABEL",
            "CONSOLE_MAIN_TOOLTIP",
            UiUtil.ICON_CONSOLE,
            new LightScrollPane(1, 0, 0, 0, consoleTextPane.getProxy()),
            0
        );

        // Order is important
        var preferences = Preferences.userRoot().node(InjectionModel.class.getName());
        if (preferences.getBoolean(UiUtil.JAVA_VISIBLE, false)) {
            
            this.insertJavaTab();
        }
        
        if (preferences.getBoolean(UiUtil.NETWORK_VISIBLE, true)) {
            
            this.insertNetworkTab();
        }
        
        if (preferences.getBoolean(UiUtil.CHUNK_VISIBLE, true)) {
            
            this.insertChunkTab();
        }
        
        if (preferences.getBoolean(UiUtil.BINARY_VISIBLE, true)) {
            
            this.insertBooleanTab();
        }

        // Reset Font when tab is selected
        this.tabConsoles.addChangeListener(changeEvent -> {
            
            JTabbedPane tabs = this.tabConsoles;
            
            if (tabs.getSelectedIndex() > -1) {
                
                var currentTabHeader = tabs.getTabComponentAt(tabs.getSelectedIndex());
                
                if (currentTabHeader != null) {
                    
                    currentTabHeader.setFont(currentTabHeader.getFont().deriveFont(Font.PLAIN));
                    currentTabHeader.setForeground(Color.BLACK);
                }
            }
        });
    }

    private JPanel initializeExpandPanel() {
        
        var buttonShowSouth = new BasicArrowButton(SwingConstants.SOUTH);
        buttonShowSouth.setName("buttonShowSouth");
        
        buttonShowSouth.setBorderPainted(false);
        buttonShowSouth.setPreferredSize(new Dimension(buttonShowSouth.getPreferredSize().width, buttonShowSouth.getPreferredSize().height));
        buttonShowSouth.setMaximumSize(buttonShowSouth.getPreferredSize());
        buttonShowSouth.setOpaque(false);
        buttonShowSouth.setBorder(BorderFactory.createEmptyBorder());
        buttonShowSouth.addActionListener(SplitHorizontalTopBottom.getActionHideShowConsole());
        
        this.buttonShowNorth.setBorderPainted(false);
        this.buttonShowNorth.setPreferredSize(new Dimension(this.buttonShowNorth.getPreferredSize().width, this.buttonShowNorth.getPreferredSize().height));
        this.buttonShowNorth.setMaximumSize(this.buttonShowNorth.getPreferredSize());
        this.buttonShowNorth.setOpaque(false);
        this.buttonShowNorth.setBorder(BorderFactory.createEmptyBorder());
        this.buttonShowNorth.addActionListener(SplitHorizontalTopBottom.getActionHideShowResult());
        this.buttonShowNorth.setName("buttonShowNorth");

        var arrowDownPanel = new JPanel();
        arrowDownPanel.setLayout(new BorderLayout());
        arrowDownPanel.setOpaque(false);
        
        // Disable overlap with zerosizesplitter
        arrowDownPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        arrowDownPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 26));
        arrowDownPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        var panelExpander = new JPanel(new BorderLayout());
        panelExpander.setBorder(BorderFactory.createEmptyBorder());
        panelExpander.add(buttonShowSouth, BorderLayout.LINE_END);
        panelExpander.add(this.buttonShowNorth, BorderLayout.LINE_START);
        arrowDownPanel.add(panelExpander, BorderLayout.LINE_END);
        
        return arrowDownPanel;
    }
    
    public void reset() {
        
        this.networkTable.getListHttpHeader().clear();
        
        // Empty infos tabs
        this.getChunkTab().setText(StringUtils.EMPTY);
        this.getBinaryTab().setText(StringUtils.EMPTY);
        
        // Fix #4657, Fix #1860: Multiple Exceptions on setRowCount()
        try {
            ((DefaultTableModel) this.networkTable.getModel()).setRowCount(0);
            
        } catch(NullPointerException | ArrayIndexOutOfBoundsException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
        
        this.javaTextPane.getProxy().setText(StringUtils.EMPTY);
        
        this.tabbedPaneNetworkTab.reset();
    }

    /**
     * Add Chunk console to bottom panel.
     */
    public void insertChunkTab() {
        
        this.buildI18nTab(
            "CONSOLE_CHUNK_LABEL",
            "CONSOLE_CHUNK_TOOLTIP",
            UiUtil.ICON_CHUNK,
            new LightScrollPane(1, 0, 0, 0, PanelConsoles.this.chunkTextArea),
            1
        );
    }

    /**
     * Add Binary console to bottom panel.
     */
    public void insertBooleanTab() {
        
        this.buildI18nTab(
            "CONSOLE_BINARY_LABEL",
            "CONSOLE_BINARY_TOOLTIP",
            UiUtil.ICON_BINARY,
            new LightScrollPane(1, 0, 0, 0, PanelConsoles.this.binaryTextArea),
            1 + (MediatorHelper.menubar().getChunkMenu().isSelected() ? 1 : 0)
        );
    }

    /**
     * Add Network tab to bottom panel.
     */
    public void insertNetworkTab() {
        
        this.buildI18nTab(
            "CONSOLE_NETWORK_LABEL",
            "CONSOLE_NETWORK_TOOLTIP",
            UiUtil.ICON_HEADER,
            new LightScrollPane(1, 0, 0, 0, PanelConsoles.this.networkSplitPane),
            this.tabConsoles.getTabCount() - (MediatorHelper.menubar().getJavaDebugMenu().isSelected() ? 1 : 0)
        );
    }

    /**
     * Add Java console to bottom panel.
     */
    public void insertJavaTab() {
        
        this.buildI18nTab(
            "CONSOLE_JAVA_LABEL",
            "CONSOLE_JAVA_TOOLTIP",
            UiUtil.ICON_CUP,
            new LightScrollPane(1, 0, 0, 0, this.javaTextPane.getProxy()),
            this.tabConsoles.getTabCount()
        );
    }
    
    private void buildI18nTab(String keyLabel, String keyTooltip, Icon icon, Component manager, int position) {
        
        final var refJToolTipI18n = new JToolTipI18n[]{ new JToolTipI18n(I18nViewUtil.valueByKey(keyTooltip)) };
        
        var labelTab = new JLabel(I18nViewUtil.valueByKey(keyLabel), icon, SwingConstants.CENTER) {
            
            @Override
            public JToolTip createToolTip() {
                
                JToolTip tipI18n = new JToolTipI18n(I18nViewUtil.valueByKey(keyTooltip));
                refJToolTipI18n[0] = (JToolTipI18n) tipI18n;
                
                return tipI18n;
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
                    
                    LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                }
                
                super.mousePressed(event);
            }
        });
        
        this.tabConsoles.insertTab(I18nViewUtil.valueByKey(keyLabel), icon, manager, null, position);
        this.tabConsoles.setTabComponentAt(
            this.tabConsoles.indexOfTab(I18nViewUtil.valueByKey(keyLabel)),
            labelTab
        );
        
        I18nViewUtil.addComponentForKey(keyLabel, labelTab);
        I18nViewUtil.addComponentForKey(keyTooltip, refJToolTipI18n[0]);
        labelTab.setToolTipText(I18nViewUtil.valueByKey(keyTooltip));
    }
    
    public void messageChunk(String text) {
        
        try {
            this.chunkTextArea.append(text +"\n");
            this.chunkTextArea.setCaretPosition(this.chunkTextArea.getDocument().getLength());
            
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            
            // Fix #67063: NullPointerException on chunkTab.append()
            // Fix #4770 on chunkTab.append()
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
        }
    }
    
    public void messageBinary(String text) {
        
        try {
            this.binaryTextArea.append(
                String
                .format(
                    "\t%s",
                    text
                )
            );
            this.binaryTextArea.setCaretPosition(this.binaryTextArea.getDocument().getLength());
        
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e.getMessage(), e);
        }
    }
    
    
    // Getter and setter

    public JTextArea getChunkTab() {
        return this.chunkTextArea;
    }

    public JSplitPaneWithZeroSizeDivider getNetworkSplitPane() {
        return this.networkSplitPane;
    }

    public JTextArea getBinaryTab() {
        return this.binaryTextArea;
    }

    public int getDividerLocation() {
        return this.location;
    }

    public void setDividerLocation(int location) {
        this.location = location;
    }

    public BasicArrowButton getButtonShowNorth() {
        return this.buttonShowNorth;
    }

    public NetworkTable getNetworkTable() {
        return this.networkTable;
    }
}
