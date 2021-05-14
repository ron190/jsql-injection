package com.jsql.view.swing.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.manager.util.ComboMenu;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.sql.lexer.HighlightedDocument;
import com.jsql.view.swing.sql.text.JTextPaneLexer;
import com.jsql.view.swing.sql.text.JTextPaneObjectMethod;
import com.jsql.view.swing.tab.TabHeader.Cleanable;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class SqlEngine extends JPanel implements Cleanable {

    private static ModelYaml modelYaml = MediatorHelper.model().getMediatorVendor().getVendor().instance().getModelYaml();

    private static JTabbedPane tabbedPaneError = new TabbedPaneWheeled(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);

    private static transient Border borderRight = BorderFactory.createMatteBorder(0, 0, 0, 1, UiUtil.COLOR_COMPONENT_BORDER);
    
    private static final List<JTextPaneLexer> textPanesError = new ArrayList<>();
    
    enum TextareaWithColor {
        
        // Default
        DATABASE_DEFAULT(new JTextPaneLexer(
            v -> modelYaml.getResource().getSchema().setDatabase(v),
            () -> modelYaml.getResource().getSchema().getDatabase()
        )),
        TABLE_DEFAULT(new JTextPaneLexer(
            v -> modelYaml.getResource().getSchema().setTable(v),
            () -> modelYaml.getResource().getSchema().getTable()
        )),
        COLUMN_DEFAULT(new JTextPaneLexer(
            v -> modelYaml.getResource().getSchema().setColumn(v),
            () -> modelYaml.getResource().getSchema().getColumn()
        )),
        QUERY_DEFAULT(new JTextPaneLexer(
            v -> modelYaml.getResource().getSchema().getRow().setQuery(v),
            () -> modelYaml.getResource().getSchema().getRow().getQuery()
        )),
        FIELD_DEFAULT(new JTextPaneLexer(
            v -> modelYaml.getResource().getSchema().getRow().getFields().setField(v),
            () -> modelYaml.getResource().getSchema().getRow().getFields().getField()
        )),
        CONCAT_DEFAULT(new JTextPaneLexer(
            v -> modelYaml.getResource().getSchema().getRow().getFields().setConcat(v),
            () -> modelYaml.getResource().getSchema().getRow().getFields().getConcat()
        )),
        
        // Zip
        DATABASE_ZIP(new JTextPaneLexer(
            v -> modelYaml.getResource().getZip().setDatabase(v),
            () -> modelYaml.getResource().getZip().getDatabase()
        )),
        TABLE_ZIP(new JTextPaneLexer(
            v -> modelYaml.getResource().getZip().setTable(v),
            () -> modelYaml.getResource().getZip().getTable()
        )),
        COLUMN_ZIP(new JTextPaneLexer(
            v -> modelYaml.getResource().getZip().setColumn(v),
            () -> modelYaml.getResource().getZip().getColumn()
        )),
        QUERY_ZIP(new JTextPaneLexer(
            v -> modelYaml.getResource().getZip().getRow().setQuery(v),
            () -> modelYaml.getResource().getZip().getRow().getQuery()
        )),
        FIELD_ZIP(new JTextPaneLexer(
            v -> modelYaml.getResource().getZip().getRow().getFields().setField(v),
            () -> modelYaml.getResource().getZip().getRow().getFields().getField()
        )),
        CONCAT_ZIP(new JTextPaneLexer(
            v -> modelYaml.getResource().getZip().getRow().getFields().setConcat(v),
            () -> modelYaml.getResource().getZip().getRow().getFields().getConcat()
        )),
        
        // Dios
        DATABASE_DIOS(new JTextPaneLexer(
            v -> modelYaml.getResource().getDios().setDatabase(v),
            () -> modelYaml.getResource().getDios().getDatabase()
        )),
        TABLE_DIOS(new JTextPaneLexer(
            v -> modelYaml.getResource().getDios().setTable(v),
            () -> modelYaml.getResource().getDios().getTable()
        )),
        COLUMN_DIOS(new JTextPaneLexer(
            v -> modelYaml.getResource().getDios().setColumn(v),
            () -> modelYaml.getResource().getDios().getColumn()
        )),
        QUERY_DIOS(new JTextPaneLexer(
            v -> modelYaml.getResource().getDios().getRow().setQuery(v),
            () -> modelYaml.getResource().getDios().getRow().getQuery()
        )),
        FIELD_DIOS(new JTextPaneLexer(
            v -> modelYaml.getResource().getDios().getRow().getFields().setField(v),
            () -> modelYaml.getResource().getDios().getRow().getFields().getField()
        )),
        CONCAT_DIOS(new JTextPaneLexer(
            v -> modelYaml.getResource().getDios().getRow().getFields().setConcat(v),
            () -> modelYaml.getResource().getDios().getRow().getFields().getConcat()
        )),
        
        INFO(new JTextPaneLexer(
            v -> modelYaml.getResource().setInfo(v),
            () -> modelYaml.getResource().getInfo()
        )),
        
        TRUTHY(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().getTest().setTruthy(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getTruthyAsString()
        )),
        FALSY(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().getTest().setFalsy(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getFalsyAsString()
        )),
         
        // Configuration
        SLIDING_WINDOW(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().setSlidingWindow(v),
            () -> modelYaml.getStrategy().getConfiguration().getSlidingWindow()
        )),
        LIMIT(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().setLimit(v),
            () -> modelYaml.getStrategy().getConfiguration().getLimit()
        )),
        FAILSAFE(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().setFailsafe(v),
            () -> modelYaml.getStrategy().getConfiguration().getFailsafe()
        )),
        CALIBRATOR(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().setCalibrator(v),
            () -> modelYaml.getStrategy().getConfiguration().getCalibrator()
        )),
        ENDING_COMMENT(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().setEndingComment(v),
            () -> modelYaml.getStrategy().getConfiguration().getEndingComment()
        )),
        LIMIT_BOUNDARY(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().setLimitBoundary(v),
            () -> modelYaml.getStrategy().getConfiguration().getLimitBoundary()
        )),
        ORDER_BY_ERROR_MESSAGE(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().getFingerprint().setOrderByErrorMessage(v),
            () -> modelYaml.getStrategy().getConfiguration().getFingerprint().getOrderByErrorMessage()
        )),
        INCORRECT_STRING_ERROR_MESSAGE(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getConfiguration().getFingerprint().setErrorMessageAsString(v),
            () -> modelYaml.getStrategy().getConfiguration().getFingerprint().getErrorMessageAsString()
        )),
        
        // Normal
        INDICES(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getNormal().setIndices(v),
            () -> modelYaml.getStrategy().getNormal().getIndices()
        )),
        CAPACITY(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getNormal().setCapacity(v),
            () -> modelYaml.getStrategy().getNormal().getCapacity()
        )),
        ORDER_BY(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getNormal().setOrderBy(v),
            () -> modelYaml.getStrategy().getNormal().getOrderBy()
        )),
        
        // Boolean
        MODE_AND(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().setModeAnd(v),
            () -> modelYaml.getStrategy().getBoolean().getModeAnd()
        )),
        MODE_OR(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().setModeOr(v),
            () -> modelYaml.getStrategy().getBoolean().getModeOr()
        )),
        BLIND(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().setBlind(v),
            () -> modelYaml.getStrategy().getBoolean().getBlind()
        )),
        TIME(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().setTime(v),
            () -> modelYaml.getStrategy().getBoolean().getTime()
        )),
        BIT_TEST(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().getTest().setBit(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getBit()
        )),
        LENGTH_TEST(new JTextPaneLexer(
            v -> modelYaml.getStrategy().getBoolean().getTest().setLength(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getLength()
        ))
        ;
        
        JTextPaneLexer text;

        public JTextPaneLexer getText() {
            return this.text;
        }

        TextareaWithColor(JTextPaneLexer text) {
            this.text = text;
        }
    }
    
    public SqlEngine() {
        
        SqlEngine.initializeTextComponents();
        
        Stream
        .of(
            TextareaWithColor.DATABASE_DEFAULT,
            TextareaWithColor.TABLE_DEFAULT,
            TextareaWithColor.COLUMN_DEFAULT,
            TextareaWithColor.QUERY_DEFAULT,
            TextareaWithColor.FIELD_DEFAULT,
            TextareaWithColor.CONCAT_DEFAULT,
            TextareaWithColor.INFO,
            
            TextareaWithColor.DATABASE_ZIP,
            TextareaWithColor.TABLE_ZIP,
            TextareaWithColor.COLUMN_ZIP,
            TextareaWithColor.QUERY_ZIP,
            TextareaWithColor.FIELD_ZIP,
            TextareaWithColor.CONCAT_ZIP,
            
            TextareaWithColor.DATABASE_DIOS,
            TextareaWithColor.TABLE_DIOS,
            TextareaWithColor.COLUMN_DIOS,
            TextareaWithColor.QUERY_DIOS,
            TextareaWithColor.FIELD_DIOS,
            TextareaWithColor.CONCAT_DIOS,
            
            TextareaWithColor.MODE_AND,
            TextareaWithColor.MODE_OR,
            TextareaWithColor.BLIND,
            TextareaWithColor.TIME,
            TextareaWithColor.BIT_TEST,
            TextareaWithColor.LENGTH_TEST
        )
        .forEach(textPane -> textPane.getText().setBorder(SqlEngine.borderRight));
        
        JPanel panelStructure = this.getPanelStructure();
        JPanel panelStrategy = this.getPanelStrategy();
        JPanel panelConfiguration = this.getPanelConfiguration();
        JPanel panelFingerprinting = this.getPanelFingerprinting();

        JTabbedPane tabsBottom = new TabbedPaneWheeled(SwingConstants.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
        
        Stream
        .of(
            new SimpleEntry<>("SQLENGINE_STRUCTURE", panelStructure),
            new SimpleEntry<>("SQLENGINE_STRATEGY", panelStrategy),
            new SimpleEntry<>("SQLENGINE_CONFIGURATION", panelConfiguration),
            new SimpleEntry<>("SQLENGINE_FINGERPRINTING", panelFingerprinting)
        )
        .forEach(entry -> {
            
            tabsBottom.addTab(I18nUtil.valueByKey(entry.getKey()), entry.getValue());
            
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsBottom.setTabComponentAt(
                tabsBottom.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });

        this.setLayout(new OverlayLayout(this));

        JPanel panelCombo = SqlEngine.initializeMenuVendor();
        this.add(panelCombo);
        
        this.add(tabsBottom);
        
        tabsBottom.setAlignmentX(FlowLayout.LEADING);
        tabsBottom.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        
        MediatorHelper.menubar().switchLocale(I18nUtil.getLocaleDefault());
    }

    private JPanel getPanelStructure() {
        
        final var keyDatabases = "SQLENGINE_DATABASES";
        final var keyTables = "SQLENGINE_TABLES";
        final var keyColumns = "SQLENGINE_COLUMNS";
        final var keyRows = "SQLENGINE_ROWS";
        final var keyField = "SQLENGINE_FIELD";
        final var keyFieldSeparator = "SQLENGINE_FIELDS_SEPARATOR";
        
        JTabbedPane tabsStandard = new TabbedPaneWheeled(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);
        
        JTabbedPane tabsSchema = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        
        Stream
        .of(
            new SimpleEntry<>(keyDatabases, TextareaWithColor.DATABASE_DEFAULT.getText()),
            new SimpleEntry<>(keyTables, TextareaWithColor.TABLE_DEFAULT.getText()),
            new SimpleEntry<>(keyColumns, TextareaWithColor.COLUMN_DEFAULT.getText()),
            new SimpleEntry<>(keyRows, TextareaWithColor.QUERY_DEFAULT.getText()),
            new SimpleEntry<>(keyField, TextareaWithColor.FIELD_DEFAULT.getText()),
            new SimpleEntry<>(keyFieldSeparator, TextareaWithColor.CONCAT_DEFAULT.getText()),
            new SimpleEntry<>("SQLENGINE_METADATA", TextareaWithColor.INFO.getText())
        )
        .forEach(entry -> {
            
            tabsSchema.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsSchema.setTabComponentAt(
                tabsSchema.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsZip = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        
        Stream
        .of(
            new SimpleEntry<>(keyDatabases, TextareaWithColor.DATABASE_ZIP.getText()),
            new SimpleEntry<>(keyTables, TextareaWithColor.TABLE_ZIP.getText()),
            new SimpleEntry<>(keyColumns, TextareaWithColor.COLUMN_ZIP.getText()),
            new SimpleEntry<>(keyRows, TextareaWithColor.QUERY_ZIP.getText()),
            new SimpleEntry<>(keyField, TextareaWithColor.FIELD_ZIP.getText()),
            new SimpleEntry<>(keyFieldSeparator, TextareaWithColor.CONCAT_ZIP.getText())
        )
        .forEach(entry -> {
            
            tabsZip.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsZip.setTabComponentAt(
                tabsZip.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsDios = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        
        Stream
        .of(
            new SimpleEntry<>(keyDatabases, TextareaWithColor.DATABASE_DIOS.getText()),
            new SimpleEntry<>(keyTables, TextareaWithColor.TABLE_DIOS.getText()),
            new SimpleEntry<>(keyColumns, TextareaWithColor.COLUMN_DIOS.getText()),
            new SimpleEntry<>(keyRows, TextareaWithColor.QUERY_DIOS.getText()),
            new SimpleEntry<>(keyField, TextareaWithColor.FIELD_DIOS.getText()),
            new SimpleEntry<>(keyFieldSeparator, TextareaWithColor.CONCAT_DIOS.getText())
        )
        .forEach(entry -> {
            
            tabsDios.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsDios.setTabComponentAt(
                tabsDios.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        Stream
        .of(
            new SimpleEntry<>("SQLENGINE_STANDARD", tabsSchema),
            new SimpleEntry<>("SQLENGINE_ZIP", tabsZip),
            new SimpleEntry<>("SQLENGINE_DIOS", tabsDios)
        )
        .forEach(entry -> {
            
            tabsStandard.addTab(I18nUtil.valueByKey(entry.getKey()), entry.getValue());
            
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsStandard.setTabComponentAt(
                tabsStandard.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        var panelStructure = new JPanel(new BorderLayout());
        panelStructure.add(tabsStandard, BorderLayout.CENTER);
        panelStructure.setBorder(BorderFactory.createEmptyBorder());
        
        return panelStructure;
    }

    private JPanel getPanelStrategy() {
        
        JTabbedPane tabsStrategy = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_NORMAL"), new LightScrollPane(1, 0, 1, 0, TextareaWithColor.INDICES.getText()));
        
        var panelStrategy = new JPanel(new BorderLayout());
        panelStrategy.add(tabsStrategy, BorderLayout.CENTER);
        panelStrategy.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        /* Error */
        var panelError = new JPanel(new BorderLayout());
        panelError.add(SqlEngine.tabbedPaneError, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_ERROR"), panelError);

        /* Boolean */
        JTabbedPane tabsBoolean = new TabbedPaneWheeled(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);
        
        Stream
        .of(
            new SimpleEntry<>("AND mode", TextareaWithColor.MODE_AND.getText()),
            new SimpleEntry<>("OR mode", TextareaWithColor.MODE_OR.getText()),
            new SimpleEntry<>("Blind", TextareaWithColor.BLIND.getText()),
            new SimpleEntry<>("Time", TextareaWithColor.TIME.getText()),
            new SimpleEntry<>("Bit Test", TextareaWithColor.BIT_TEST.getText()),
            new SimpleEntry<>("Length Test", TextareaWithColor.LENGTH_TEST.getText())
        )
        .forEach(entry ->
            tabsBoolean.addTab(
                entry.getKey(),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            )
        );
        
        var panelBoolean = new JPanel(new BorderLayout());
        panelBoolean.add(tabsBoolean, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_BOOLEAN"), panelBoolean);
        
        /* Strategy */
        Stream
        .of(
            "SQLENGINE_NORMAL",
            "SQLENGINE_ERROR",
            "SQLENGINE_BOOLEAN"
        )
        .forEach(keyI18n -> {
            
            var label = new JLabel(I18nUtil.valueByKey(keyI18n));
            
            tabsStrategy.setTabComponentAt(
                tabsStrategy.indexOfTab(I18nUtil.valueByKey(keyI18n)),
                label
            );
            
            I18nViewUtil.addComponentForKey(keyI18n, label);
        });
        
        return panelStrategy;
    }

    private JPanel getPanelConfiguration() {
        
        JTabbedPane tabsConfiguration = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, TextareaWithColor.SLIDING_WINDOW.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, TextareaWithColor.LIMIT.getText()));
        tabsConfiguration.addTab("Limit start index", new LightScrollPane(1, 0, 1, 0, TextareaWithColor.LIMIT_BOUNDARY.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CAPACITY"), new LightScrollPane(1, 0, 1, 0, TextareaWithColor.CAPACITY.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CALIBRATOR"), new LightScrollPane(1, 0, 1, 0, TextareaWithColor.CALIBRATOR.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_TRAPCANCELLER"), new LightScrollPane(1, 0, 1, 0, TextareaWithColor.FAILSAFE.getText()));
        tabsConfiguration.addTab("End comment", new LightScrollPane(1, 0, 1, 0, TextareaWithColor.ENDING_COMMENT.getText()));
        
        Stream
        .of(
            "SQLENGINE_CHARACTERS_SLIDINGWINDOW",
            "SQLENGINE_ROWS_SLIDINGWINDOW",
            "SQLENGINE_CAPACITY",
            "SQLENGINE_CALIBRATOR",
            "SQLENGINE_TRAPCANCELLER"
        )
        .forEach(keyI18n -> {
            
            var label = new JLabel(I18nUtil.valueByKey(keyI18n));
            tabsConfiguration.setTabComponentAt(
                tabsConfiguration.indexOfTab(I18nUtil.valueByKey(keyI18n)),
                label
            );
            
            I18nViewUtil.addComponentForKey(keyI18n, label);
        });
        
        var panelConfiguration = new JPanel(new BorderLayout());
        panelConfiguration.add(tabsConfiguration, BorderLayout.CENTER);
        panelConfiguration.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        return panelConfiguration;
    }
    
    private JPanel getPanelFingerprinting() {
        
        JTabbedPane tabs = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        
        tabs.addTab(I18nUtil.valueByKey("SQLENGINE_ORDER_BY"), new LightScrollPane(1, 0, 1, 0, TextareaWithColor.ORDER_BY.getText()));
        tabs.addTab("Order by error", new LightScrollPane(1, 0, 1, 0, TextareaWithColor.ORDER_BY_ERROR_MESSAGE.getText()));
        tabs.addTab("String error", new LightScrollPane(1, 0, 1, 0, TextareaWithColor.INCORRECT_STRING_ERROR_MESSAGE.getText()));
        tabs.addTab("Truthy", new LightScrollPane(1, 0, 1, 0, TextareaWithColor.TRUTHY.getText()));
        tabs.addTab("Falsy", new LightScrollPane(1, 0, 1, 0, TextareaWithColor.FALSY.getText()));
        
        Stream
        .of("SQLENGINE_ORDER_BY")
        .forEach(keyI18n -> {
            
            var label = new JLabel(I18nUtil.valueByKey(keyI18n));
            tabs.setTabComponentAt(
                tabs.indexOfTab(I18nUtil.valueByKey(keyI18n)),
                label
            );
            
            I18nViewUtil.addComponentForKey(keyI18n, label);
        });
        
        var panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        return panel;
    }

    private static JPanel initializeMenuVendor() {
        
        var panelCombo = new JPanel();
        panelCombo.setLayout(new BorderLayout());
        panelCombo.setOpaque(false);

        // Disable overlap with zerosizesplitter
        panelCombo.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        panelCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 25));
        panelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        var menuBarVendor = new JMenuBar();
        menuBarVendor.setOpaque(false);
        menuBarVendor.setBorder(null);
        
        JMenu comboMenuVendor = new ComboMenu(MediatorHelper.model().getMediatorVendor().getVendor().toString());
        menuBarVendor.add(comboMenuVendor);

        var groupVendor = new ButtonGroup();

        List<Vendor> listVendors = new LinkedList<>(MediatorHelper.model().getMediatorVendor().getVendors());
        listVendors.removeIf(vendor -> vendor == MediatorHelper.model().getMediatorVendor().getAuto());
        
        for (final Vendor vendor: listVendors) {
            
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(
                vendor.toString(),
                vendor == MediatorHelper.model().getMediatorVendor().getVendor()
            );
            
            itemRadioVendor.addActionListener(actionEvent -> {
                
                SqlEngine.modelYaml = vendor.instance().getModelYaml();
                SqlEngine.initializeTextComponents();
                comboMenuVendor.setText(vendor.toString());
            });
            
            itemRadioVendor.setUI(
                new BasicRadioButtonMenuItemUI() {
                    
                    @Override
                    protected void doClick(MenuSelectionManager msm) {
                        
                        this.menuItem.doClick(0);
                    }
                }
            );
            
            comboMenuVendor.add(itemRadioVendor);
            groupVendor.add(itemRadioVendor);
        }
        
        panelCombo.add(menuBarVendor, BorderLayout.LINE_END);

        // Do Overlay
        panelCombo.setAlignmentX(FlowLayout.TRAILING);
        panelCombo.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        
        return panelCombo;
    }
    
    /**
     * Configure all text components with new coloring and new modelYaml setter.
     */
    private static void initializeTextComponents() {
        
        SqlEngine.getTextPanes().forEach(SqlEngine::resetLexer);
        SqlEngine.getTextPanes().forEach(JTextPaneObjectMethod::switchSetterToVendor);
        SqlEngine.getTextPanes().forEach(textPaneLexer -> textPaneLexer.setText(StringUtils.EMPTY));
        
        Stream
        .of(TextareaWithColor.values())
        .forEach(entry ->
            entry
            .getText()
            .setText(
                entry
                .getText()
                .getSupplierGetter()
                .get()
                .trim()
            )
        );

        SqlEngine.populateTabError();
    }
    
    /**
     * Dynamically add textPanes to Error tab for current vendor.
     */
    private static void populateTabError() {
        
        SqlEngine.tabbedPaneError.removeAll();
        
        if (SqlEngine.modelYaml.getStrategy().getError() == null) {
            
            return;
        }
            
        for (Method methodError: SqlEngine.modelYaml.getStrategy().getError().getMethod()) {
            
            var panelError = new JPanel(new BorderLayout());
            
            final var refMethodError = new Method[]{ methodError };
            
            var textPaneError = new JTextPaneLexer(
                refMethodError[0]::setQuery,
                refMethodError[0]::getQuery
            );
            
            SqlEngine.resetLexer(textPaneError);
            textPaneError.switchSetterToVendor();
            textPaneError.setText(methodError.getQuery().trim());
            textPaneError.setBorder(SqlEngine.borderRight);

            panelError.add(new LightScrollPane(1, 0, 1, 0, textPaneError), BorderLayout.CENTER);
            
            var panelLimit = new JPanel();
            panelLimit.setLayout(new BoxLayout(panelLimit, BoxLayout.LINE_AXIS));
            panelLimit.add(new JLabel(" Overflow limit: "));
            panelLimit.add(new JTextField(Integer.toString(methodError.getCapacity())));
            
            // TODO Integrate Error limit
            panelError.add(panelLimit, BorderLayout.SOUTH);

            SqlEngine.tabbedPaneError.addTab(methodError.getName(), panelError);
            
            SqlEngine.tabbedPaneError.setTitleAt(
                SqlEngine.tabbedPaneError.getTabCount() - 1,
                String.format(
                    "<html><div style=\"text-align:left;width:100px;\">%s</div></html>",
                    methodError.getName()
                )
            );
            
            SqlEngine.textPanesError.add(textPaneError);
        }
    }

    /**
     * End coloring threads.
     * Used when Sql Engine is closed by ctrl+W or using tab header close icon and middle click.
     */
    @Override
    public void clean() {
        
        SqlEngine.getTextPanes().forEach(UiUtil::stopDocumentColorer);
    }
    
    /**
     * Reset the textPane colorer.
     * @param textPane which colorer will be reset.
     */
    private static void resetLexer(JTextPaneLexer textPane) {
        
        UiUtil.stopDocumentColorer(textPane);
        
        var document = new HighlightedDocument(HighlightedDocument.SQL_STYLE);
        document.setHighlightStyle(HighlightedDocument.SQL_STYLE);
        textPane.setStyledDocument(document);
        
        document.addDocumentListener(new DocumentListenerEditing() {
            
            @Override
            public void process() {
                
                textPane.setAttribute();
            }
        });
    }
    
    /**
     * Merge list of Error textPanes and list of other textAreas.
     * @return the merged list
     */
    private static List<JTextPaneLexer> getTextPanes() {
        
        return
            Stream
            .concat(
                SqlEngine.textPanesError.stream(),
                Stream
                .of(TextareaWithColor.values())
                .map(TextareaWithColor::getText)
            )
            .collect(Collectors.toList());
    }
}