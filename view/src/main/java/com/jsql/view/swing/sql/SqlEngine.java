package com.jsql.view.swing.sql;

import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.tab.TabbedPaneWheeled;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlEngine extends JPanel {

    private static ModelYaml modelYaml = MediatorHelper.model().getMediatorVendor().getVendor().instance().getModelYaml();
    private static final JTabbedPane tabsError = new TabbedPaneWheeled(SwingConstants.RIGHT);
    private static final List<JSyntaxTextArea> listTextareasError = new ArrayList<>();
    
    enum TextareaWithColor {
        
        // Default
        DATABASE_DEFAULT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getSchema().setDatabase(v),
            () -> SqlEngine.modelYaml.getResource().getSchema().getDatabase()
        )),
        TABLE_DEFAULT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getSchema().setTable(v),
            () -> SqlEngine.modelYaml.getResource().getSchema().getTable()
        )),
        COLUMN_DEFAULT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getSchema().setColumn(v),
            () -> SqlEngine.modelYaml.getResource().getSchema().getColumn()
        )),
        QUERY_DEFAULT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getSchema().getRow().setQuery(v),
            () -> SqlEngine.modelYaml.getResource().getSchema().getRow().getQuery()
        )),
        FIELD_DEFAULT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getSchema().getRow().getFields().setField(v),
            () -> SqlEngine.modelYaml.getResource().getSchema().getRow().getFields().getField()
        )),
        CONCAT_DEFAULT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getSchema().getRow().getFields().setConcat(v),
            () -> SqlEngine.modelYaml.getResource().getSchema().getRow().getFields().getConcat()
        )),
        
        // Zip
        DATABASE_ZIP(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getZip().setDatabase(v),
            () -> SqlEngine.modelYaml.getResource().getZip().getDatabase()
        )),
        TABLE_ZIP(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getZip().setTable(v),
            () -> SqlEngine.modelYaml.getResource().getZip().getTable()
        )),
        COLUMN_ZIP(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getZip().setColumn(v),
            () -> SqlEngine.modelYaml.getResource().getZip().getColumn()
        )),
        QUERY_ZIP(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getZip().getRow().setQuery(v),
            () -> SqlEngine.modelYaml.getResource().getZip().getRow().getQuery()
        )),
        FIELD_ZIP(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getZip().getRow().getFields().setField(v),
            () -> SqlEngine.modelYaml.getResource().getZip().getRow().getFields().getField()
        )),
        CONCAT_ZIP(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getZip().getRow().getFields().setConcat(v),
            () -> SqlEngine.modelYaml.getResource().getZip().getRow().getFields().getConcat()
        )),
        
        // Dios
        DATABASE_DIOS(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getDios().setDatabase(v),
            () -> SqlEngine.modelYaml.getResource().getDios().getDatabase()
        )),
        TABLE_DIOS(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getDios().setTable(v),
            () -> SqlEngine.modelYaml.getResource().getDios().getTable()
        )),
        COLUMN_DIOS(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getDios().setColumn(v),
            () -> SqlEngine.modelYaml.getResource().getDios().getColumn()
        )),
        QUERY_DIOS(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getDios().getRow().setQuery(v),
            () -> SqlEngine.modelYaml.getResource().getDios().getRow().getQuery()
        )),
        FIELD_DIOS(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getDios().getRow().getFields().setField(v),
            () -> SqlEngine.modelYaml.getResource().getDios().getRow().getFields().getField()
        )),
        CONCAT_DIOS(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getDios().getRow().getFields().setConcat(v),
            () -> SqlEngine.modelYaml.getResource().getDios().getRow().getFields().getConcat()
        )),
        
        INFO(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().setInfo(v),
            () -> SqlEngine.modelYaml.getResource().getInfo()
        )),

        // Configuration
        SLIDING_WINDOW(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().setSlidingWindow(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getSlidingWindow()
        )),
        LIMIT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().setLimit(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getLimit()
        )),
        FAILSAFE(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().setFailsafe(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getFailsafe()
        )),
        CALIBRATOR(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().setCalibrator(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getCalibrator()
        )),
        ENDING_COMMENT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().setEndingComment(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getEndingComment()
        )),
        LIMIT_BOUNDARY(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().setLimitBoundary(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getLimitBoundary()
        )),
        
        // Normal
        INDICES(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getNormal().setIndices(v),
            () -> SqlEngine.modelYaml.getStrategy().getNormal().getIndices()
        )),
        CAPACITY(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getNormal().setCapacity(v),
            () -> SqlEngine.modelYaml.getStrategy().getNormal().getCapacity()
        )),

        STACK(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().setStack(v),
            () -> SqlEngine.modelYaml.getStrategy().getStack()
        )),

        // Boolean
        MODE_AND(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().setModeAnd(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getModeAnd()
        )),
        MODE_OR(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().setModeOr(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getModeOr()
        )),
        MODE_STACK(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().setModeStack(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getModeStack()
        )),
        BLIND(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().setBlind(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getBlind()
        )),
        TIME(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().setTime(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getTime()
        )),
        MULTIBIT(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().setMultibit(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getMultibit()
        )),
        BIT_TEST(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().getTest().setBit(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getTest().getBit()
        )),

        // File
        FILE_PRIVILEGE(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getFile().setPrivilege(v),
            () -> SqlEngine.modelYaml.getResource().getFile().getPrivilege()
        )),
        FILE_READ(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getFile().setRead(v),
            () -> SqlEngine.modelYaml.getResource().getFile().getRead()
        )),
        FILE_WRITE_BODY(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getFile().getWrite().setBody(v),
            () -> SqlEngine.modelYaml.getResource().getFile().getWrite().getBody()
        )),
        FILE_WRITE_PATH(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getResource().getFile().getWrite().setPath(v),
            () -> SqlEngine.modelYaml.getResource().getFile().getWrite().getPath()
        )),

        // Fingerprint
        TRUTHY(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().getTest().setTruthy(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getTest().getTruthyAsString()
        )),
        FALSY(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getBinary().getTest().setFalsy(v),
            () -> SqlEngine.modelYaml.getStrategy().getBinary().getTest().getFalsyAsString()
        )),
        INCORRECT_STRING_ERROR_MESSAGE(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().getFingerprint().setErrorMessageAsString(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getFingerprint().getErrorMessageAsString()
        )),
        ORDER_BY_ERROR_MESSAGE(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().getFingerprint().setOrderByErrorMessage(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getFingerprint().getOrderByErrorMessage()
        )),
        ORDER_BY(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getNormal().setOrderBy(v),
            () -> SqlEngine.modelYaml.getStrategy().getNormal().getOrderBy()
        )),
        VENDOR_SPECIFIC(new JSyntaxTextArea(
            v -> SqlEngine.modelYaml.getStrategy().getConfiguration().getFingerprint().setVendorSpecific(v),
            () -> SqlEngine.modelYaml.getStrategy().getConfiguration().getFingerprint().getVendorSpecific()
        )),
        ;
        
        final JSyntaxTextArea textarea;

        public JSyntaxTextArea getTextArea() {
            return this.textarea;
        }

        TextareaWithColor(JSyntaxTextArea textarea) {
            this.textarea = textarea;
        }
    }
    
    public SqlEngine() {
        // user can switch to another vendor then close, so restore current vendor
        SqlEngine.modelYaml = MediatorHelper.model().getMediatorVendor().getVendor().instance().getModelYaml();

        SqlEngine.initializeTextComponents();

        JTabbedPane panelStructure = this.getPanelStructure();
        JTabbedPane panelFile = this.getPanelFile();
        JTabbedPane panelStrategy = this.getPanelStrategy();
        JTabbedPane panelConfiguration = this.getPanelConfiguration();
        JTabbedPane panelFingerprinting = this.getPanelFingerprinting();

        JTabbedPane tabsBottom = new TabbedPaneWheeled(SwingConstants.BOTTOM);
        
        Stream.of(
            new SimpleEntry<>("SQLENGINE_STRUCTURE", panelStructure),
            new SimpleEntry<>("SQLENGINE_STRATEGY", panelStrategy),
            new SimpleEntry<>("SQLENGINE_CONFIGURATION", panelConfiguration),
            new SimpleEntry<>("SQLENGINE_FINGERPRINTING", panelFingerprinting),
            new SimpleEntry<>("SQLENGINE_FILE", panelFile)
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

        this.setLayout(new BorderLayout());

        JPanel panelCombo = SqlEngine.initializeMenuVendor();
        tabsBottom.putClientProperty("JTabbedPane.trailingComponent", panelCombo);
        this.add(tabsBottom);

        SwingUtilities.invokeLater(() -> MediatorHelper.menubar().switchLocale(I18nUtil.getCurrentLocale()));  // required for arabic
    }

    private JTabbedPane getPanelStructure() {
        final var keyDatabases = "SQLENGINE_DATABASES";
        final var keyTables = "SQLENGINE_TABLES";
        final var keyColumns = "SQLENGINE_COLUMNS";
        final var keyRows = "SQLENGINE_ROWS";
        final var keyField = "SQLENGINE_FIELD";
        final var keyFieldSeparator = "SQLENGINE_FIELDS_SEPARATOR";

        JTabbedPane tabsDefault = new TabbedPaneWheeled();
        Stream.of(
            new SimpleEntry<>(keyDatabases, TextareaWithColor.DATABASE_DEFAULT.getTextArea()),
            new SimpleEntry<>(keyTables, TextareaWithColor.TABLE_DEFAULT.getTextArea()),
            new SimpleEntry<>(keyColumns, TextareaWithColor.COLUMN_DEFAULT.getTextArea()),
            new SimpleEntry<>(keyRows, TextareaWithColor.QUERY_DEFAULT.getTextArea()),
            new SimpleEntry<>(keyField, TextareaWithColor.FIELD_DEFAULT.getTextArea()),
            new SimpleEntry<>(keyFieldSeparator, TextareaWithColor.CONCAT_DEFAULT.getTextArea()),
            new SimpleEntry<>("SQLENGINE_METADATA", TextareaWithColor.INFO.getTextArea())
        )
        .forEach(entry -> {
            tabsDefault.addTab(I18nUtil.valueByKey(entry.getKey()), new RTextScrollPane(entry.getValue(), false));
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsDefault.setTabComponentAt(tabsDefault.indexOfTab(I18nUtil.valueByKey(entry.getKey())), label);
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsZip = new TabbedPaneWheeled();
        Stream.of(
            new SimpleEntry<>(keyDatabases, TextareaWithColor.DATABASE_ZIP.getTextArea()),
            new SimpleEntry<>(keyTables, TextareaWithColor.TABLE_ZIP.getTextArea()),
            new SimpleEntry<>(keyColumns, TextareaWithColor.COLUMN_ZIP.getTextArea()),
            new SimpleEntry<>(keyRows, TextareaWithColor.QUERY_ZIP.getTextArea()),
            new SimpleEntry<>(keyField, TextareaWithColor.FIELD_ZIP.getTextArea()),
            new SimpleEntry<>(keyFieldSeparator, TextareaWithColor.CONCAT_ZIP.getTextArea())
        )
        .forEach(entry -> {
            tabsZip.addTab(I18nUtil.valueByKey(entry.getKey()), new RTextScrollPane(entry.getValue(), false));
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsZip.setTabComponentAt(tabsZip.indexOfTab(I18nUtil.valueByKey(entry.getKey())), label);
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsDios = new TabbedPaneWheeled();
        Stream.of(
            new SimpleEntry<>(keyDatabases, TextareaWithColor.DATABASE_DIOS.getTextArea()),
            new SimpleEntry<>(keyTables, TextareaWithColor.TABLE_DIOS.getTextArea()),
            new SimpleEntry<>(keyColumns, TextareaWithColor.COLUMN_DIOS.getTextArea()),
            new SimpleEntry<>(keyRows, TextareaWithColor.QUERY_DIOS.getTextArea()),
            new SimpleEntry<>(keyField, TextareaWithColor.FIELD_DIOS.getTextArea()),
            new SimpleEntry<>(keyFieldSeparator, TextareaWithColor.CONCAT_DIOS.getTextArea())
        )
        .forEach(entry -> {
            tabsDios.addTab(I18nUtil.valueByKey(entry.getKey()), new RTextScrollPane(entry.getValue(), false));
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsDios.setTabComponentAt(tabsDios.indexOfTab(I18nUtil.valueByKey(entry.getKey())), label);
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });

        JTabbedPane tabs = new TabbedPaneWheeled(SwingConstants.RIGHT);
        Stream.of(
            new SimpleEntry<>("SQLENGINE_STANDARD", tabsDefault),
            new SimpleEntry<>("SQLENGINE_ZIP", tabsZip),
            new SimpleEntry<>("SQLENGINE_DIOS", tabsDios)
        )
        .forEach(entry -> {
            tabs.addTab(I18nUtil.valueByKey(entry.getKey()), entry.getValue());
            var label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabs.setTabComponentAt(tabs.indexOfTab(I18nUtil.valueByKey(entry.getKey())), label);
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
        });
        return tabs;
    }

    private JTabbedPane getPanelFile() {
        JTabbedPane tabs = new TabbedPaneWheeled();
        tabs.addTab("Privilege", new RTextScrollPane(TextareaWithColor.FILE_PRIVILEGE.getTextArea(), false));
        tabs.addTab("Read", new RTextScrollPane(TextareaWithColor.FILE_READ.getTextArea(), false));
        tabs.addTab("Write body", new RTextScrollPane(TextareaWithColor.FILE_WRITE_BODY.getTextArea(), false));
        tabs.addTab("Write path", new RTextScrollPane(TextareaWithColor.FILE_WRITE_PATH.getTextArea(), false));
        return tabs;
    }

    private JTabbedPane getPanelStrategy() {
        JTabbedPane tabs = new TabbedPaneWheeled();
        tabs.addTab(I18nUtil.valueByKey("SQLENGINE_NORMAL"), new RTextScrollPane(TextareaWithColor.INDICES.getTextArea(), false));
        tabs.addTab(I18nUtil.valueByKey("SQLENGINE_STACK"), new RTextScrollPane(TextareaWithColor.STACK.getTextArea(), false));
        tabs.addTab(I18nUtil.valueByKey("SQLENGINE_ERROR"), SqlEngine.tabsError);

        JTabbedPane tabsBoolean = new TabbedPaneWheeled(SwingConstants.RIGHT);
        Stream.of(
            new SimpleEntry<>("AND mode", TextareaWithColor.MODE_AND.getTextArea()),
            new SimpleEntry<>("OR mode", TextareaWithColor.MODE_OR.getTextArea()),
            new SimpleEntry<>("Stack mode", TextareaWithColor.MODE_STACK.getTextArea()),
            new SimpleEntry<>("Blind", TextareaWithColor.BLIND.getTextArea()),
            new SimpleEntry<>("Time", TextareaWithColor.TIME.getTextArea()),
            new SimpleEntry<>("Multibit", TextareaWithColor.MULTIBIT.getTextArea()),
            new SimpleEntry<>("Bit test", TextareaWithColor.BIT_TEST.getTextArea())
        )
        .forEach(entry -> {
            tabsBoolean.addTab(entry.getKey(), new RTextScrollPane(entry.getValue(), false));
            tabsBoolean.setTitleAt(
                tabsBoolean.getTabCount() - 1,
                String.format(
                    "<html><div style=\"text-align:left;width:60px;\">%s</div></html>",
                    entry.getKey()
                )
            );
        });
        tabs.addTab(I18nUtil.valueByKey("SQLENGINE_BOOLEAN"), tabsBoolean);

        Stream.of(
            "SQLENGINE_NORMAL",
            "SQLENGINE_STACK",
            "SQLENGINE_ERROR",
            "SQLENGINE_BOOLEAN"
        )
        .forEach(keyI18n -> {
            var label = new JLabel(I18nUtil.valueByKey(keyI18n));
            tabs.setTabComponentAt(tabs.indexOfTab(I18nUtil.valueByKey(keyI18n)), label);
            I18nViewUtil.addComponentForKey(keyI18n, label);
        });
        return tabs;
    }

    private JTabbedPane getPanelConfiguration() {
        JTabbedPane tabs = new TabbedPaneWheeled();
        Stream.of(
            new SimpleEntry<>("SQLENGINE_CHARACTERS_SLIDINGWINDOW", new RTextScrollPane(TextareaWithColor.SLIDING_WINDOW.getTextArea(), false)),
            new SimpleEntry<>("SQLENGINE_ROWS_SLIDINGWINDOW", new RTextScrollPane(TextareaWithColor.LIMIT.getTextArea(), false)),
            new SimpleEntry<>("SQLENGINE_LIMIT_START_INDEX", new RTextScrollPane(TextareaWithColor.LIMIT_BOUNDARY.getTextArea(), false)),
            new SimpleEntry<>("SQLENGINE_CAPACITY", new RTextScrollPane(TextareaWithColor.CAPACITY.getTextArea(), false)),
            new SimpleEntry<>("SQLENGINE_CALIBRATOR", new RTextScrollPane(TextareaWithColor.CALIBRATOR.getTextArea(), false)),
            new SimpleEntry<>("SQLENGINE_FAILSAFE", new RTextScrollPane(TextareaWithColor.FAILSAFE.getTextArea(), false)),
            new SimpleEntry<>("SQLENGINE_END_COMMENT", new RTextScrollPane(TextareaWithColor.ENDING_COMMENT.getTextArea(), false))
        )
        .forEach(keyI18n -> {
            tabs.addTab(I18nUtil.valueByKey(keyI18n.getKey()), keyI18n.getValue());
            var label = new JLabel(I18nUtil.valueByKey(keyI18n.getKey()));
            tabs.setTabComponentAt(tabs.indexOfTab(I18nUtil.valueByKey(keyI18n.getKey())), label);
            I18nViewUtil.addComponentForKey(keyI18n.getKey(), label);
        });
        return tabs;
    }
    
    private JTabbedPane getPanelFingerprinting() {
        JTabbedPane tabs = new TabbedPaneWheeled();
        tabs.addTab(I18nUtil.valueByKey("SQLENGINE_ORDER_BY"), new RTextScrollPane(TextareaWithColor.ORDER_BY.getTextArea(), false));
        tabs.addTab("Order by error", new RTextScrollPane(TextareaWithColor.ORDER_BY_ERROR_MESSAGE.getTextArea(), false));
        tabs.addTab("String error", new RTextScrollPane(TextareaWithColor.INCORRECT_STRING_ERROR_MESSAGE.getTextArea(), false));
        tabs.addTab("Vendor specific", new RTextScrollPane(TextareaWithColor.VENDOR_SPECIFIC.getTextArea(), false));
        tabs.addTab("Truthy", new RTextScrollPane(TextareaWithColor.TRUTHY.getTextArea(), false));
        tabs.addTab("Falsy", new RTextScrollPane(TextareaWithColor.FALSY.getTextArea(), false));
        
        Stream.of("SQLENGINE_ORDER_BY").forEach(keyI18n -> {
            var label = new JLabel(I18nUtil.valueByKey(keyI18n));
            tabs.setTabComponentAt(tabs.indexOfTab(I18nUtil.valueByKey(keyI18n)), label);
            I18nViewUtil.addComponentForKey(keyI18n, label);
        });
        return tabs;
    }

    private static JPanel initializeMenuVendor() {
        var panelMenuVendor = new JPanel();  // required for label on right
        panelMenuVendor.setLayout(new BorderLayout());

        JPopupMenu popupMenuVendors = new JPopupMenu();
        popupMenuVendors.setLayout(UiUtil.getColumnLayout(MediatorHelper.model().getMediatorVendor().getVendors().size()));

        JLabel labelVendor = new JLabel(MediatorHelper.model().getMediatorVendor().getVendor().toString(), UiUtil.ARROW_DOWN.icon, SwingConstants.LEFT);
        labelVendor.setBorder(UiUtil.BORDER_5PX);  // required for padding
        labelVendor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(popupMenuVendors.getComponents()).map(a -> (JComponent) a).forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                popupMenuVendors.updateUI();  // required: incorrect when dark/light mode switch
                popupMenuVendors.show(e.getComponent(), e.getComponent().getX(),e.getComponent().getY() + e.getComponent().getHeight());
                popupMenuVendors.setLocation(e.getComponent().getLocationOnScreen().x,e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight());
            }
        });

        List<Vendor> listVendors = new LinkedList<>(MediatorHelper.model().getMediatorVendor().getVendors());
        listVendors.removeIf(vendor -> vendor == MediatorHelper.model().getMediatorVendor().getAuto());

        var groupVendor = new ButtonGroup();
        for (final Vendor vendor: listVendors) {
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(
                vendor.toString(),
                vendor == MediatorHelper.model().getMediatorVendor().getVendor()
            );
            
            itemRadioVendor.addActionListener(actionEvent -> {
                SqlEngine.modelYaml = vendor.instance().getModelYaml();
                SqlEngine.initializeTextComponents();
                labelVendor.setText(vendor.toString());
            });
            
            groupVendor.add(itemRadioVendor);
            popupMenuVendors.add(itemRadioVendor);
        }
        
        panelMenuVendor.add(labelVendor, BorderLayout.LINE_END);  // required to set on right
        return panelMenuVendor;
    }
    
    /**
     * Configure all text components with new coloring and new modelYaml setter.
     */
    private static void initializeTextComponents() {
        SqlEngine.getTextareas().forEach(SqlEngine::reset);
        SqlEngine.getTextareas().forEach(textPaneLexer -> textPaneLexer.setText(StringUtils.EMPTY));

        Stream.of(TextareaWithColor.values())
        .map(textareaWithColor -> textareaWithColor.textarea)
        .forEach(textArea -> {
            textArea.setText(textArea.getSupplierGetter().get().trim());
            textArea.setCaretPosition(0);
        });

        SqlEngine.populateTabError();
        SqlEngine.applyTheme();
    }

    public static void applyTheme() {
        SqlEngine.getTextareas().forEach(UiUtil::applySyntaxTheme);
    }

    /**
     * Dynamically add textPanes to Error tab for current vendor.
     */
    private static void populateTabError() {
        SqlEngine.tabsError.removeAll();

        if (SqlEngine.modelYaml.getStrategy().getError() == null) {
            return;
        }
            
        for (Method methodError: SqlEngine.modelYaml.getStrategy().getError().getMethod()) {
            var panelError = new JPanel(new BorderLayout());

            var textareaError = new JSyntaxTextArea(methodError::setQuery, methodError::getQuery);
            SqlEngine.reset(textareaError);
            textareaError.setText(methodError.getQuery().trim());
            textareaError.setCaretPosition(0);
            panelError.add(new RTextScrollPane(textareaError, false), BorderLayout.CENTER);
            
            var panelLimit = new JPanel();  // TODO Integrate Error limit
            panelLimit.setLayout(new BoxLayout(panelLimit, BoxLayout.LINE_AXIS));
            panelLimit.add(new JLabel(" Overflow limit: "));
            panelLimit.add(new JTextField(Integer.toString(methodError.getCapacity())));
            panelError.add(panelLimit, BorderLayout.SOUTH);

            SqlEngine.tabsError.addTab(methodError.getName(), panelError);
            SqlEngine.tabsError.setTitleAt(
                SqlEngine.tabsError.getTabCount() - 1,
                String.format(
                    "<html><div style=\"text-align:left;width:100px;\">%s</div></html>",
                    methodError.getName()
                )
            );
            SqlEngine.listTextareasError.add(textareaError);
        }
    }
    
    /**
     * Reset the textarea colorer.
     * @param textarea which colorer will be reset.
     */
    private static void reset(JSyntaxTextArea textarea) {
        textarea.setDocument(new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_SQL));  // required else empty on reopen
        textarea.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                textarea.setAttribute();
            }
        });
    }
    
    /**
     * Merge list of Error textPanes and list of other textAreas.
     * @return the merged list
     */
    private static List<JSyntaxTextArea> getTextareas() {
        return Stream.concat(
            SqlEngine.listTextareasError.stream(),
            Stream.of(TextareaWithColor.values()).map(TextareaWithColor::getTextArea)
        )
        .collect(Collectors.toList());
    }
}