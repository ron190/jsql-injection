package com.jsql.view.swing.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseWheelListener;
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
import com.jsql.view.swing.tab.TabbedPaneMouseWheelListener;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class SqlEngine extends JPanel implements Cleanable {

    private static ModelYaml modelYaml = MediatorHelper.model().getMediatorVendor().getVendor().instance().getModelYaml();

    private JTabbedPane tabbedPaneError = new TabbedPaneWheeled(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);

    private transient Border borderRight = BorderFactory.createMatteBorder(0, 0, 0, 1, UiUtil.COLOR_COMPONENT_BORDER);
    
    private transient MouseWheelListener tabbedPaneMouseWheelListener = new TabbedPaneMouseWheelListener();
    
    private final List<JTextPaneLexer> textPanesError = new ArrayList<>();
    
    enum TEXT_WITH_COLOR {
        
        // Default
        TEXTAREA_DATABASE(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getSchema().setDatabase(v),
            () -> modelYaml.getResource().getSchema().getDatabase()
        )),
        TEXTAREA_TABLE(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getSchema().setTable(v),
            () -> modelYaml.getResource().getSchema().getTable()
        )),
        TEXTAREA_COLUMN(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getSchema().setColumn(v),
            () -> modelYaml.getResource().getSchema().getColumn()
        )),
        TEXTAREA_QUERY(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getSchema().getRow().setQuery(v),
            () -> modelYaml.getResource().getSchema().getRow().getQuery()
        )),
        TEXTAREA_FIELD(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getSchema().getRow().getFields().setField(v),
            () -> modelYaml.getResource().getSchema().getRow().getFields().getField()
        )),
        TEXTAREA_CONCAT(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getSchema().getRow().getFields().setConcat(v),
            () -> modelYaml.getResource().getSchema().getRow().getFields().getConcat()
        )),
        
        // Zip
        TEXTAREA_DATABASE_ZIP(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getZip().setDatabase(v),
            () -> modelYaml.getResource().getZip().getDatabase()
        )),
        TEXTAREA_TABLE_ZIP(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getZip().setTable(v),
            () -> modelYaml.getResource().getZip().getTable()
        )),
        TEXTAREA_COLUMN_ZIP(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getZip().setColumn(v),
            () -> modelYaml.getResource().getZip().getColumn()
        )),
        TEXTAREA_QUERY_ZIP(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getZip().getRow().setQuery(v),
            () -> modelYaml.getResource().getZip().getRow().getQuery()
        )),
        TEXTAREA_FIELD_ZIP(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getZip().getRow().getFields().setField(v),
            () -> modelYaml.getResource().getZip().getRow().getFields().getField()
        )),
        TEXTAREA_CONCAT_ZIP(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getZip().getRow().getFields().setConcat(v),
            () -> modelYaml.getResource().getZip().getRow().getFields().getConcat()
        )),
        
        // Dios
        TEXTAREA_DATABASE_DIOS(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getDios().setDatabase(v),
            () -> modelYaml.getResource().getDios().getDatabase()
        )),
        TEXTAREA_TABLE_DIOS(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getDios().setTable(v),
            () -> modelYaml.getResource().getDios().getTable()
        )),
        TEXTAREA_COLUMN_DIOS(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getDios().setColumn(v),
            () -> modelYaml.getResource().getDios().getColumn()
        )),
        TEXTAREA_QUERY_DIOS(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getDios().getRow().setQuery(v),
            () -> modelYaml.getResource().getDios().getRow().getQuery()
        )),
        TEXTAREA_FIELD_DIOS(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getDios().getRow().getFields().setField(v),
            () -> modelYaml.getResource().getDios().getRow().getFields().getField()
        )),
        TEXTAREA_CONCAT_DIOS(new JTextPaneLexer(
            (v) -> modelYaml.getResource().getDios().getRow().getFields().setConcat(v),
            () -> modelYaml.getResource().getDios().getRow().getFields().getConcat()
        )),
        
        TEXTAREA_INFO(new JTextPaneLexer(
            (v) -> modelYaml.getResource().setInfo(v),
            () -> modelYaml.getResource().getInfo()
        )),
        
        TEXTAREA_TRUTHY(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().getTest().setTruthy(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getTruthyAsString()
        )),
        TEXTAREA_FALSY(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().getTest().setFalsy(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getFalsyAsString()
        )),
         
        // Configuration
        TEXTAREA_SLIDING_WINDOW(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().setSlidingWindow(v),
            () -> modelYaml.getStrategy().getConfiguration().getSlidingWindow()
        )),
        TEXTAREA_LIMIT(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().setLimit(v),
            () -> modelYaml.getStrategy().getConfiguration().getLimit()
        )),
        TEXTAREA_FAILSAFE(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().setFailsafe(v),
            () -> modelYaml.getStrategy().getConfiguration().getFailsafe()
        )),
        TEXTAREA_CALIBRATOR(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().setCalibrator(v),
            () -> modelYaml.getStrategy().getConfiguration().getCalibrator()
        )),
        TEXTAREA_ENDING_COMMENT(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().setEndingComment(v),
            () -> modelYaml.getStrategy().getConfiguration().getEndingComment()
        )),
        TEXTAREA_LIMIT_BOUNDARY(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().setLimitBoundary(v),
            () -> modelYaml.getStrategy().getConfiguration().getLimitBoundary()
        )),
        TEXTAREA_ORDER_BY_ERROR_MESSAGE(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().getFingerprint().setOrderByErrorMessage(v),
            () -> modelYaml.getStrategy().getConfiguration().getFingerprint().getOrderByErrorMessage()
        )),
        TEXTAREA_INCORRECT_STRING_ERROR_MESSAGE(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getConfiguration().getFingerprint().setErrorMessageAsString(v),
            () -> modelYaml.getStrategy().getConfiguration().getFingerprint().getErrorMessageAsString()
        )),
        
        // Normal
        TEXTAREA_INDICES(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getNormal().setIndices(v),
            () -> modelYaml.getStrategy().getNormal().getIndices()
        )),
        TEXTAREA_CAPACITY(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getNormal().setCapacity(v),
            () -> modelYaml.getStrategy().getNormal().getCapacity()
        )),
        TEXTAREA_ORDER_BY(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getNormal().setOrderBy(v),
            () -> modelYaml.getStrategy().getNormal().getOrderBy()
        )),
        
        // Boolean
        TEXTAREA_MODE_AND(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().setModeAnd(v),
            () -> modelYaml.getStrategy().getBoolean().getModeAnd()
        )),
        TEXTAREA_MODE_OR(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().setModeOr(v),
            () -> modelYaml.getStrategy().getBoolean().getModeOr()
        )),
        TEXTAREA_BLIND(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().setBlind(v),
            () -> modelYaml.getStrategy().getBoolean().getBlind()
        )),
        TEXTAREA_TIME(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().setTime(v),
            () -> modelYaml.getStrategy().getBoolean().getTime()
        )),
        TEXTAREA_BIT_TEST(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().getTest().setBit(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getBit()
        )),
        TEXTAREA_LENGTH_TEST(new JTextPaneLexer(
            (v) -> modelYaml.getStrategy().getBoolean().getTest().setLength(v),
            () -> modelYaml.getStrategy().getBoolean().getTest().getLength()
        ))
        ;
        
        JTextPaneLexer text;

        public JTextPaneLexer getText() {
            return this.text;
        }

        TEXT_WITH_COLOR(JTextPaneLexer text) {
            this.text = text;
        }
    }
    
    public SqlEngine() {
        
        this.tabbedPaneError.addMouseWheelListener(this.tabbedPaneMouseWheelListener);

        this.initializeTextComponents();
        
        Stream
        .of(
            TEXT_WITH_COLOR.TEXTAREA_DATABASE,
            TEXT_WITH_COLOR.TEXTAREA_TABLE,
            TEXT_WITH_COLOR.TEXTAREA_COLUMN,
            TEXT_WITH_COLOR.TEXTAREA_QUERY,
            TEXT_WITH_COLOR.TEXTAREA_FIELD,
            TEXT_WITH_COLOR.TEXTAREA_CONCAT,
            TEXT_WITH_COLOR.TEXTAREA_INFO,
            
            TEXT_WITH_COLOR.TEXTAREA_DATABASE_ZIP,
            TEXT_WITH_COLOR.TEXTAREA_TABLE_ZIP,
            TEXT_WITH_COLOR.TEXTAREA_COLUMN_ZIP,
            TEXT_WITH_COLOR.TEXTAREA_QUERY_ZIP,
            TEXT_WITH_COLOR.TEXTAREA_FIELD_ZIP,
            TEXT_WITH_COLOR.TEXTAREA_CONCAT_ZIP,
            
            TEXT_WITH_COLOR.TEXTAREA_DATABASE_DIOS,
            TEXT_WITH_COLOR.TEXTAREA_TABLE_DIOS,
            TEXT_WITH_COLOR.TEXTAREA_COLUMN_DIOS,
            TEXT_WITH_COLOR.TEXTAREA_QUERY_DIOS,
            TEXT_WITH_COLOR.TEXTAREA_FIELD_DIOS,
            TEXT_WITH_COLOR.TEXTAREA_CONCAT_DIOS,
            
            TEXT_WITH_COLOR.TEXTAREA_MODE_AND,
            TEXT_WITH_COLOR.TEXTAREA_MODE_OR,
            TEXT_WITH_COLOR.TEXTAREA_BLIND,
            TEXT_WITH_COLOR.TEXTAREA_TIME,
            TEXT_WITH_COLOR.TEXTAREA_BIT_TEST,
            TEXT_WITH_COLOR.TEXTAREA_LENGTH_TEST
        )
        .forEach(textPane -> textPane.getText().setBorder(this.borderRight));
        
        JPanel panelStructure = this.getPanelStructure();
        JPanel panelStrategy = this.getPanelStrategy();
        JPanel panelConfiguration = this.getPanelConfiguration();
        JPanel panelFingerprinting = this.getPanelFingerprinting();

        JTabbedPane tabsBottom = new TabbedPaneWheeled(SwingConstants.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsBottom.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        
        Stream
        .of(
            new SimpleEntry<>("SQLENGINE_STRUCTURE", panelStructure),
            new SimpleEntry<>("SQLENGINE_STRATEGY", panelStrategy),
            new SimpleEntry<>("SQLENGINE_CONFIGURATION", panelConfiguration),
            new SimpleEntry<>("SQLENGINE_FINGERPRINTING", panelFingerprinting)
        )
        .forEach(entry -> {
            
            tabsBottom.addTab(I18nUtil.valueByKey(entry.getKey()), entry.getValue());
            
            JLabel label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsBottom.setTabComponentAt(
                tabsBottom.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });

        this.setLayout(new OverlayLayout(this));

        this.initializeMenuVendor();
        
        this.add(tabsBottom);
        
        tabsBottom.setAlignmentX(FlowLayout.LEADING);
        tabsBottom.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        
        MediatorHelper.menubar().switchLocale(I18nUtil.getLocaleDefault());
    }

    private JPanel getPanelStructure() {
        
        final String keyDatabases = "SQLENGINE_DATABASES";
        final String keyTables = "SQLENGINE_TABLES";
        final String keyColumns = "SQLENGINE_COLUMNS";
        final String keyRows = "SQLENGINE_ROWS";
        final String keyField = "SQLENGINE_FIELD";
        final String keyFieldSeparator = "SQLENGINE_FIELDS_SEPARATOR";
        
        JTabbedPane tabsStandard = new TabbedPaneWheeled(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsStandard.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        
        JTabbedPane tabsSchema = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsSchema.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        
        Stream
        .of(
            new SimpleEntry<>(keyDatabases, TEXT_WITH_COLOR.TEXTAREA_DATABASE.getText()),
            new SimpleEntry<>(keyTables, TEXT_WITH_COLOR.TEXTAREA_TABLE.getText()),
            new SimpleEntry<>(keyColumns, TEXT_WITH_COLOR.TEXTAREA_COLUMN.getText()),
            new SimpleEntry<>(keyRows, TEXT_WITH_COLOR.TEXTAREA_QUERY.getText()),
            new SimpleEntry<>(keyField, TEXT_WITH_COLOR.TEXTAREA_FIELD.getText()),
            new SimpleEntry<>(keyFieldSeparator, TEXT_WITH_COLOR.TEXTAREA_CONCAT.getText()),
            new SimpleEntry<>("SQLENGINE_METADATA", TEXT_WITH_COLOR.TEXTAREA_INFO.getText())
        )
        .forEach(entry -> {
            
            tabsSchema.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            JLabel label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsSchema.setTabComponentAt(
                tabsSchema.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsZip = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsZip.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        
        Stream
        .of(
            new SimpleEntry<>(keyDatabases, TEXT_WITH_COLOR.TEXTAREA_DATABASE_ZIP.getText()),
            new SimpleEntry<>(keyTables, TEXT_WITH_COLOR.TEXTAREA_TABLE_ZIP.getText()),
            new SimpleEntry<>(keyColumns, TEXT_WITH_COLOR.TEXTAREA_COLUMN_ZIP.getText()),
            new SimpleEntry<>(keyRows, TEXT_WITH_COLOR.TEXTAREA_QUERY_ZIP.getText()),
            new SimpleEntry<>(keyField, TEXT_WITH_COLOR.TEXTAREA_FIELD_ZIP.getText()),
            new SimpleEntry<>(keyFieldSeparator, TEXT_WITH_COLOR.TEXTAREA_CONCAT_ZIP.getText())
        )
        .forEach(entry -> {
            
            tabsZip.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            JLabel label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsZip.setTabComponentAt(
                tabsZip.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsDios = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsDios.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        
        Stream
        .of(
            new SimpleEntry<>(keyDatabases, TEXT_WITH_COLOR.TEXTAREA_DATABASE_DIOS.getText()),
            new SimpleEntry<>(keyTables, TEXT_WITH_COLOR.TEXTAREA_TABLE_DIOS.getText()),
            new SimpleEntry<>(keyColumns, TEXT_WITH_COLOR.TEXTAREA_COLUMN_DIOS.getText()),
            new SimpleEntry<>(keyRows, TEXT_WITH_COLOR.TEXTAREA_QUERY_DIOS.getText()),
            new SimpleEntry<>(keyField, TEXT_WITH_COLOR.TEXTAREA_FIELD_DIOS.getText()),
            new SimpleEntry<>(keyFieldSeparator, TEXT_WITH_COLOR.TEXTAREA_CONCAT_DIOS.getText())
        )
        .forEach(entry -> {
            
            tabsDios.addTab(
                I18nUtil.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            JLabel label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
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
            
            JLabel label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsStandard.setTabComponentAt(
                tabsStandard.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        JPanel panelStructure = new JPanel(new BorderLayout());
        panelStructure.add(tabsStandard, BorderLayout.CENTER);
        panelStructure.setBorder(BorderFactory.createEmptyBorder());
        
        return panelStructure;
    }

    private JPanel getPanelStrategy() {
        
        JTabbedPane tabsStrategy = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsStrategy.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_NORMAL"), new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_INDICES.getText()));
        
        JPanel panelStrategy = new JPanel(new BorderLayout());
        panelStrategy.add(tabsStrategy, BorderLayout.CENTER);
        panelStrategy.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        /* Error */
        JPanel panelError = new JPanel(new BorderLayout());
        panelError.add(this.tabbedPaneError, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_ERROR"), panelError);

        /* Boolean */
        JTabbedPane tabsBoolean = new TabbedPaneWheeled(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsBoolean.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        
        Stream
        .of(
            new SimpleEntry<>("AND mode", TEXT_WITH_COLOR.TEXTAREA_MODE_AND.getText()),
            new SimpleEntry<>("OR mode", TEXT_WITH_COLOR.TEXTAREA_MODE_OR.getText()),
            new SimpleEntry<>("Blind", TEXT_WITH_COLOR.TEXTAREA_BLIND.getText()),
            new SimpleEntry<>("Time", TEXT_WITH_COLOR.TEXTAREA_TIME.getText()),
            new SimpleEntry<>("Bit Test", TEXT_WITH_COLOR.TEXTAREA_BIT_TEST.getText()),
            new SimpleEntry<>("Length Test", TEXT_WITH_COLOR.TEXTAREA_LENGTH_TEST.getText())
        )
        .forEach(entry ->
            tabsBoolean.addTab(
                entry.getKey(),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            )
        );
        
        JPanel panelBoolean = new JPanel(new BorderLayout());
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
            
            JLabel label = new JLabel(I18nUtil.valueByKey(keyI18n));
            
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
        tabsConfiguration.addMouseWheelListener(this.tabbedPaneMouseWheelListener);

        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_SLIDING_WINDOW.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_LIMIT.getText()));
        tabsConfiguration.addTab("Limit start index", new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_LIMIT_BOUNDARY.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CAPACITY"), new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_CAPACITY.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CALIBRATOR"), new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_CALIBRATOR.getText()));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_TRAPCANCELLER"), new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_FAILSAFE.getText()));
        tabsConfiguration.addTab("End comment", new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_ENDING_COMMENT.getText()));
        
        Stream
        .of(
            "SQLENGINE_CHARACTERS_SLIDINGWINDOW",
            "SQLENGINE_ROWS_SLIDINGWINDOW",
            "SQLENGINE_CAPACITY",
            "SQLENGINE_CALIBRATOR",
            "SQLENGINE_TRAPCANCELLER"
        )
        .forEach(keyI18n -> {
            
            JLabel label = new JLabel(I18nUtil.valueByKey(keyI18n));
            tabsConfiguration.setTabComponentAt(
                tabsConfiguration.indexOfTab(I18nUtil.valueByKey(keyI18n)),
                label
            );
            
            I18nViewUtil.addComponentForKey(keyI18n, label);
        });
        
        JPanel panelConfiguration = new JPanel(new BorderLayout());
        panelConfiguration.add(tabsConfiguration, BorderLayout.CENTER);
        panelConfiguration.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        return panelConfiguration;
    }
    
    private JPanel getPanelFingerprinting() {
        
        JTabbedPane tabs = new TabbedPaneWheeled(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.addMouseWheelListener(this.tabbedPaneMouseWheelListener);
        
        tabs.addTab(I18nUtil.valueByKey("SQLENGINE_ORDER_BY"), new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_ORDER_BY.getText()));
        tabs.addTab("Order by error", new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_ORDER_BY_ERROR_MESSAGE.getText()));
        tabs.addTab("String error", new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_INCORRECT_STRING_ERROR_MESSAGE.getText()));
        tabs.addTab("Truthy", new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_TRUTHY.getText()));
        tabs.addTab("Falsy", new LightScrollPane(1, 0, 1, 0, TEXT_WITH_COLOR.TEXTAREA_FALSY.getText()));
        
        Stream
        .of("SQLENGINE_ORDER_BY")
        .forEach(keyI18n -> {
            
            JLabel label = new JLabel(I18nUtil.valueByKey(keyI18n));
            tabs.setTabComponentAt(
                tabs.indexOfTab(I18nUtil.valueByKey(keyI18n)),
                label
            );
            
            I18nViewUtil.addComponentForKey(keyI18n, label);
        });
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        return panel;
    }

    private void initializeMenuVendor() {
        
        JPanel panelCombo = new JPanel();
        panelCombo.setLayout(new BorderLayout());
        panelCombo.setOpaque(false);

        // Disable overlap with zerosizesplitter
        panelCombo.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        panelCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 25));
        panelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JMenuBar menuBarVendor = new JMenuBar();
        menuBarVendor.setOpaque(false);
        menuBarVendor.setBorder(null);
        
        JMenu comboMenuVendor = new ComboMenu(MediatorHelper.model().getMediatorVendor().getVendor().toString());
        menuBarVendor.add(comboMenuVendor);

        ButtonGroup groupVendor = new ButtonGroup();

        List<Vendor> listVendors = new LinkedList<>(MediatorHelper.model().getMediatorVendor().getVendors());
        listVendors.removeIf(vendor -> vendor == MediatorHelper.model().getMediatorVendor().getAuto());
        
        for (final Vendor vendor: listVendors) {
            
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(
                vendor.toString(),
                vendor == MediatorHelper.model().getMediatorVendor().getVendor()
            );
            
            itemRadioVendor.addActionListener(actionEvent -> {
                
                SqlEngine.modelYaml = vendor.instance().getModelYaml();
                this.initializeTextComponents();
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
        this.add(panelCombo);

        // Do Overlay
        panelCombo.setAlignmentX(FlowLayout.TRAILING);
        panelCombo.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    }
    
    /**
     * Configure all text components with new coloring and new modelYaml setter.
     */
    private void initializeTextComponents() {
        
        this.getTextPanes().forEach(SqlEngine::resetLexer);
        this.getTextPanes().forEach(JTextPaneObjectMethod::switchSetterToVendor);
        this.getTextPanes().forEach(textPaneLexer -> textPaneLexer.setText(StringUtils.EMPTY));
        
        Stream
        .of(TEXT_WITH_COLOR.values())
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

        this.populateTabError();
    }
    
    /**
     * Dynamically add textPanes to Error tab for current vendor.
     */
    private void populateTabError() {
        
        this.tabbedPaneError.removeAll();
        
        if (SqlEngine.modelYaml.getStrategy().getError() == null) {
            
            return;
        }
            
        for (Method methodError: SqlEngine.modelYaml.getStrategy().getError().getMethod()) {
            
            JPanel panelError = new JPanel(new BorderLayout());
            
            final Method[] refMethodError = new Method[]{methodError};
            
            JTextPaneLexer textPaneError = new JTextPaneLexer(
                (v) -> refMethodError[0].setQuery(v),
                () -> refMethodError[0].getQuery()
            );
            
            SqlEngine.resetLexer(textPaneError);
            textPaneError.switchSetterToVendor();
            textPaneError.setText(methodError.getQuery().trim());
            textPaneError.setBorder(this.borderRight);

            panelError.add(new LightScrollPane(1, 0, 1, 0, textPaneError), BorderLayout.CENTER);
            
            JPanel panelLimit = new JPanel();
            panelLimit.setLayout(new BoxLayout(panelLimit, BoxLayout.LINE_AXIS));
            panelLimit.add(new JLabel(" Overflow limit: "));
            panelLimit.add(new JTextField(Integer.toString(methodError.getCapacity())));
            
            // TODO Integrate Error limit
            panelError.add(panelLimit, BorderLayout.SOUTH);

            this.tabbedPaneError.addTab(methodError.getName(), panelError);
            
            this.tabbedPaneError.setTitleAt(
                this.tabbedPaneError.getTabCount() - 1,
                String.format(
                    "<html><div style=\"text-align:left;width:100px;\">%s</div></html>",
                    methodError.getName()
                )
            );
            
            this.textPanesError.add(textPaneError);
        }
    }

    /**
     * End coloring threads.
     * Used when Sql Engine is closed by ctrl+W or using tab header close icon and middle click.
     */
    @Override
    public void clean() {
        
        this.getTextPanes().forEach(UiUtil::stopDocumentColorer);
    }
    
    /**
     * Reset the textPane colorer.
     * @param textPane which colorer will be reset.
     */
    private static void resetLexer(JTextPaneLexer textPane) {
        
        UiUtil.stopDocumentColorer(textPane);
        
        HighlightedDocument document = new HighlightedDocument(HighlightedDocument.SQL_STYLE);
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
    private List<JTextPaneLexer> getTextPanes() {
        
        return
            Stream
            .concat(
                this.textPanesError.stream(),
                Stream
                .of(TEXT_WITH_COLOR.values())
                .map(v -> v.getText())
            )
            .collect(Collectors.toList());
    }
}