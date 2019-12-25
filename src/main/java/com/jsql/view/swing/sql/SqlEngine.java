package com.jsql.view.swing.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.MenuSelectionManager;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.manager.util.ComboMenu;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.sql.lexer.HighlightedDocument;
import com.jsql.view.swing.text.listener.DocumentListenerTyping;

@SuppressWarnings("serial")
public class SqlEngine extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private ModelYaml modelYaml = MediatorModel.model().getMediatorVendor().getVendor().instance().getModelYaml();

    private static final List<JTextPaneLexer> TEXTPANES_LEXER = new ArrayList<>();
    
    private static final JTabbedPane TAB_ERROR = new JTabbedPane(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);

    private static final Border BORDER_RIGHT = BorderFactory.createMatteBorder(0, 0, 0, 1, HelperUi.COLOR_COMPONENT_BORDER);
    
    private interface JTextPaneObjectMethod {
        default void switchSetterToVendor() {
            
        }
    }
    
    private static class JTextPaneLexer extends JTextPane implements JTextPaneObjectMethod {
        
        protected transient AttributeSetterForVendor attributeSetter = null;
        
        public JTextPaneLexer(boolean isGeneric) {
            if (isGeneric) {
                TEXTPANES_LEXER.add(this);
            }
        }
        
        public JTextPaneLexer() {
            this(true);
        }
        
        protected void setAttribute() {
            try {
                if (this.attributeSetter != null && !"".equals(this.getText())) {
                    this.attributeSetter.getSetter().invoke(this.attributeSetter.getAttribute(), this.getText());
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                LOGGER.debug(e1, e1);
            }
        }
        
    }
    
    // Standard
    private final JTextPaneLexer textareaDatabase = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getSchema(), "setDatabase");
        }
    };
    private final JTextPaneLexer textareaTable = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getSchema(), "setTable");
        }
    };
    private final JTextPaneLexer textareaColumn = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getSchema(), "setColumn");
        }
    };
    private final JTextPaneLexer textareaQuery = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getSchema().getRow(), "setQuery");
        }
    };
    private final JTextPaneLexer textareaField = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getSchema().getRow().getFields(), "setField");
        }
    };
    private final JTextPaneLexer textareaConcat = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getSchema().getRow().getFields(), "setConcat");
        }
    };
    
    private final JTextPaneLexer textareaInfo = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource(), "setInfo");
        }
    };
    
    // Zipped
    private final JTextPaneLexer textareaDatabaseZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped(), "setDatabase");
            }
        }
    };
    private final JTextPaneLexer textareaTableZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped(), "setTable");
            }
        }
    };
    private final JTextPaneLexer textareaColumnZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped(), "setColumn");
            }
        }
    };
    private final JTextPaneLexer textareaQueryZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped().getRow(), "setQuery");
            }
        }
    };
    private final JTextPaneLexer textareaFieldZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped().getRow().getFields(), "setField");
            }
        }
    };
    private final JTextPaneLexer textareaConcatZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped().getRow().getFields(), "setConcat");
            }
        }
    };
    
    // Dios
    private final JTextPaneLexer textareaDatabaseDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios(), "setDatabase");
            }
        }
    };
    private final JTextPaneLexer textareaTableDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios(), "setTable");
            }
        }
    };
    private final JTextPaneLexer textareaColumnDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios(), "setColumn");
            }
        }
    };
    private final JTextPaneLexer textareaQueryDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios().getRow(), "setQuery");
            }
        }
    };
    private final JTextPaneLexer textareaFieldDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios().getRow().getFields(), "setField");
            }
        }
    };
    private final JTextPaneLexer textareaConcatDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios().getRow().getFields(), "setConcat");
            }
        }
    };
     
    // Configuration
    private final JTextPaneLexer textareaSlidingWindow = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getConfiguration(), "setSlidingWindow");
        }
    };
    private final JTextPaneLexer textareaLimit = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getConfiguration(), "setLimit");
        }
    };
    private final JTextPaneLexer textareaFailsafe = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getConfiguration(), "setFailsafe");
        }
    };
    private final JTextPaneLexer textareaCalibrator = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getConfiguration(), "setCalibrator");
        }
    };
    private final JTextPaneLexer textareaCapacity = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getNormal(), "setCapacity");
        }
    };
    private final JTextPaneLexer textareaOrderBy = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getNormal(), "setOrderBy");
        }
    };
    
    // Normal
    private final JTextPaneLexer textareaIndices = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getNormal(), "setIndices");
        }
    };
     
    // Boolean
    private final JTextPaneLexer textareaBlind = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean(), "setBlind");
            }
        }
    };
    private final JTextPaneLexer textareaTime = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean(), "setTime");
            }
        }
    };
    private final JTextPaneLexer textareaBitTest = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean().getTest(), "setBit");
            }
        }
    };
    private final JTextPaneLexer textareaLengthTest = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (SqlEngine.this.modelYaml.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean().getTest(), "setLength");
            }
        }
    };
    
    private static class AttributeSetterForVendor {
        
        Object attributeVendor;
        java.lang.reflect.Method method;

        public AttributeSetterForVendor(Object attributeVendor, String nameSetter) {
            this.attributeVendor = attributeVendor;
            try {
                this.method = attributeVendor.getClass().getMethod(nameSetter, String.class);
            } catch (NoSuchMethodException | SecurityException e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }
        
        public Object getAttribute() {
            return this.attributeVendor;
        }

        public java.lang.reflect.Method getSetter() {
            return this.method;
        }
        
    }
    
    public SqlEngine() {
        
        List<Vendor> listVendors = new LinkedList<>(MediatorModel.model().getMediatorVendor().getVendors());
        listVendors.removeIf(i -> i == MediatorModel.model().getMediatorVendor().getAuto());
        
        JComboBox<Vendor> comboBoxVendors = new JComboBox<>(listVendors.toArray(new Vendor[0]));
        comboBoxVendors.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                this.modelYaml = ((Vendor) itemEvent.getItem()).instance().getModelYaml();
                this.changeVendor();
            }
        });
        
        comboBoxVendors.setSelectedItem(MediatorModel.model().getMediatorVendor().getVendor());
        this.changeVendor();
        
        JTabbedPane tabsStandard = new JTabbedPane(SwingConstants.RIGHT);
        
        this.textareaDatabase.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaTable.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaColumn.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaQuery.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaField.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaConcat.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaInfo.setBorder(SqlEngine.BORDER_RIGHT);
        
        this.textareaDatabaseZipped.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaTableZipped.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaColumnZipped.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaQueryZipped.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaFieldZipped.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaConcatZipped.setBorder(SqlEngine.BORDER_RIGHT);
        
        this.textareaDatabaseDios.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaTableDios.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaColumnDios.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaQueryDios.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaFieldDios.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaConcatDios.setBorder(SqlEngine.BORDER_RIGHT);
        
        this.textareaBlind.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaTime.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaBitTest.setBorder(SqlEngine.BORDER_RIGHT);
        this.textareaLengthTest.setBorder(SqlEngine.BORDER_RIGHT);
        
        JTabbedPane tabsSchema = new JTabbedPane();
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_DATABASES"), new LightScrollPane(1, 0, 1, 0, this.textareaDatabase));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_TABLES"), new LightScrollPane(1, 0, 1, 0, this.textareaTable));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_COLUMNS"), new LightScrollPane(1, 0, 1, 0, this.textareaColumn));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_ROWS"), new LightScrollPane(1, 0, 1, 0, this.textareaQuery));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_FIELD"), new LightScrollPane(1, 0, 1, 0, this.textareaField));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_FIELDS_SEPARATOR"), new LightScrollPane(1, 0, 1, 0, this.textareaConcat));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_METADATA"), new LightScrollPane(1, 0, 1, 0, this.textareaInfo));
        
        /* Structure */
        JLabel labelDatabase = new JLabel(I18n.valueByKey("SQLENGINE_DATABASES"));
        tabsSchema.setTabComponentAt(
            tabsSchema.indexOfTab(I18n.valueByKey("SQLENGINE_DATABASES")),
            labelDatabase
        );
        I18nView.addComponentForKey("SQLENGINE_DATABASES", labelDatabase);
        
        JLabel labelTable = new JLabel(I18n.valueByKey("SQLENGINE_TABLES"));
        tabsSchema.setTabComponentAt(
            tabsSchema.indexOfTab(I18n.valueByKey("SQLENGINE_TABLES")),
            labelTable
        );
        I18nView.addComponentForKey("SQLENGINE_TABLES", labelTable);
        
        JLabel labelColumn = new JLabel(I18n.valueByKey("SQLENGINE_COLUMNS"));
        tabsSchema.setTabComponentAt(
            tabsSchema.indexOfTab(I18n.valueByKey("SQLENGINE_COLUMNS")),
            labelColumn
        );
        I18nView.addComponentForKey("SQLENGINE_COLUMNS", labelColumn);
        
        JLabel labelRow = new JLabel(I18n.valueByKey("SQLENGINE_ROWS"));
        tabsSchema.setTabComponentAt(
            tabsSchema.indexOfTab(I18n.valueByKey("SQLENGINE_ROWS")),
            labelRow
        );
        I18nView.addComponentForKey("SQLENGINE_ROWS", labelRow);
        
        JLabel labelField = new JLabel(I18n.valueByKey("SQLENGINE_FIELD"));
        tabsSchema.setTabComponentAt(
            tabsSchema.indexOfTab(I18n.valueByKey("SQLENGINE_FIELD")),
            labelField
        );
        I18nView.addComponentForKey("SQLENGINE_FIELD", labelField);
        
        JLabel labelFieldSeparator = new JLabel(I18n.valueByKey("SQLENGINE_FIELDS_SEPARATOR"));
        tabsSchema.setTabComponentAt(
            tabsSchema.indexOfTab(I18n.valueByKey("SQLENGINE_FIELDS_SEPARATOR")),
            labelFieldSeparator
        );
        I18nView.addComponentForKey("SQLENGINE_FIELDS_SEPARATOR", labelFieldSeparator);
        
        JLabel labelMetadata = new JLabel(I18n.valueByKey("SQLENGINE_METADATA"));
        tabsSchema.setTabComponentAt(
            tabsSchema.indexOfTab(I18n.valueByKey("SQLENGINE_METADATA")),
            labelMetadata
        );
        I18nView.addComponentForKey("SQLENGINE_METADATA", labelMetadata);
        /**/
        
        JTabbedPane tabsZipped = new JTabbedPane();
        tabsZipped.addTab("Databases", new LightScrollPane(1, 0, 1, 0, this.textareaDatabaseZipped));
        tabsZipped.addTab("Tables", new LightScrollPane(1, 0, 1, 0, this.textareaTableZipped));
        tabsZipped.addTab("Columns", new LightScrollPane(1, 0, 1, 0, this.textareaColumnZipped));
        tabsZipped.addTab("Rows", new LightScrollPane(1, 0, 1, 0, this.textareaQueryZipped));
        tabsZipped.addTab("Field", new LightScrollPane(1, 0, 1, 0, this.textareaFieldZipped));
        tabsZipped.addTab("Fields separator", new LightScrollPane(1, 0, 1, 0, this.textareaConcatZipped));
        
        JTabbedPane tabsDios = new JTabbedPane();
        tabsDios.addTab("Databases", new LightScrollPane(1, 0, 1, 0, this.textareaDatabaseDios));
        tabsDios.addTab("Tables", new LightScrollPane(1, 0, 1, 0, this.textareaTableDios));
        tabsDios.addTab("Columns", new LightScrollPane(1, 0, 1, 0, this.textareaColumnDios));
        tabsDios.addTab("Rows", new LightScrollPane(1, 0, 1, 0, this.textareaQueryDios));
        tabsDios.addTab("Field", new LightScrollPane(1, 0, 1, 0, this.textareaFieldDios));
        tabsDios.addTab("Fields separator", new LightScrollPane(1, 0, 1, 0, this.textareaConcatDios));
        
        tabsStandard.addTab(I18n.valueByKey("SQLENGINE_STANDARD"), tabsSchema);
        tabsStandard.addTab(I18n.valueByKey("SQLENGINE_ZIPPED"), tabsZipped);
        tabsStandard.addTab(I18n.valueByKey("SQLENGINE_DIOS"), tabsDios);
        
        /* Structure */
        JLabel labelStandard = new JLabel(I18n.valueByKey("SQLENGINE_STANDARD"));
        tabsStandard.setTabComponentAt(
            tabsStandard.indexOfTab(I18n.valueByKey("SQLENGINE_STANDARD")),
            labelStandard
        );
        I18nView.addComponentForKey("SQLENGINE_STANDARD", labelStandard);
        
        JLabel labelZipped = new JLabel(I18n.valueByKey("SQLENGINE_ZIPPED"));
        tabsStandard.setTabComponentAt(
            tabsStandard.indexOfTab(I18n.valueByKey("SQLENGINE_ZIPPED")),
            labelZipped
        );
        I18nView.addComponentForKey("SQLENGINE_ZIPPED", labelZipped);
        
        JLabel labelDios = new JLabel(I18n.valueByKey("SQLENGINE_DIOS"));
        tabsStandard.setTabComponentAt(
            tabsStandard.indexOfTab(I18n.valueByKey("SQLENGINE_DIOS")),
            labelDios
        );
        I18nView.addComponentForKey("SQLENGINE_DIOS", labelDios);
        /**/
        
        JPanel panelStructure = new JPanel(new BorderLayout());
        panelStructure.add(tabsStandard, BorderLayout.CENTER);
        panelStructure.setBorder(BorderFactory.createEmptyBorder());
        
        JTabbedPane tabsStrategy = new JTabbedPane();
        tabsStrategy.addTab(I18n.valueByKey("SQLENGINE_NORMAL"), new LightScrollPane(1, 0, 1, 0, this.textareaIndices));
        
        JPanel panelStrategy = new JPanel(new BorderLayout());
        panelStrategy.add(tabsStrategy, BorderLayout.CENTER);
        panelStrategy.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
        
        JTabbedPane tabsBottom = new JTabbedPane(SwingConstants.BOTTOM);
        tabsBottom.addTab(I18n.valueByKey("SQLENGINE_STRUCTURE"), panelStructure);
        tabsBottom.addTab(I18n.valueByKey("SQLENGINE_STRATEGY"), panelStrategy);
        
        /**/
        JLabel labelStructure = new JLabel(I18n.valueByKey("SQLENGINE_STRUCTURE"));
        tabsBottom.setTabComponentAt(
            tabsBottom.indexOfTab(I18n.valueByKey("SQLENGINE_STRUCTURE")),
            labelStructure
        );
        I18nView.addComponentForKey("SQLENGINE_STRUCTURE", labelStructure);
        
        JLabel labelStrategy = new JLabel(I18n.valueByKey("SQLENGINE_STRATEGY"));
        tabsBottom.setTabComponentAt(
            tabsBottom.indexOfTab(I18n.valueByKey("SQLENGINE_STRATEGY")),
            labelStrategy
        );
        I18nView.addComponentForKey("SQLENGINE_STRATEGY", labelStrategy);
        /**/

        /*Error*/
        JPanel panelError = new JPanel(new BorderLayout());
        panelError.add(SqlEngine.TAB_ERROR, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18n.valueByKey("SQLENGINE_ERROR"), panelError);

        /*Boolean*/
        JTabbedPane tabsBoolean = new JTabbedPane(SwingConstants.RIGHT);
        tabsBoolean.addTab("Blind", new LightScrollPane(1, 0, 1, 0, this.textareaBlind));
        tabsBoolean.addTab("Time", new LightScrollPane(1, 0, 1, 0, this.textareaTime));
        tabsBoolean.addTab("Bit Test", new LightScrollPane(1, 0, 1, 0, this.textareaBitTest));
        tabsBoolean.addTab("Length Test", new LightScrollPane(1, 0, 1, 0, this.textareaLengthTest));
        
        JPanel panelBoolean = new JPanel(new BorderLayout());
        panelBoolean.add(tabsBoolean, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18n.valueByKey("SQLENGINE_BOOLEAN"), panelBoolean);
        
        /* Strategy */
        JLabel labelNormal = new JLabel(I18n.valueByKey("SQLENGINE_NORMAL"));
        tabsStrategy.setTabComponentAt(
            tabsStrategy.indexOfTab(I18n.valueByKey("SQLENGINE_NORMAL")),
            labelNormal
        );
        I18nView.addComponentForKey("SQLENGINE_NORMAL", labelNormal);

        
        JLabel labelError = new JLabel(I18n.valueByKey("SQLENGINE_ERROR"));
        tabsStrategy.setTabComponentAt(
            tabsStrategy.indexOfTab(I18n.valueByKey("SQLENGINE_ERROR")),
            labelError
        );
        I18nView.addComponentForKey("SQLENGINE_ERROR", labelError);

        
        JLabel labelBoolean = new JLabel(I18n.valueByKey("SQLENGINE_BOOLEAN"));
        tabsStrategy.setTabComponentAt(
            tabsStrategy.indexOfTab(I18n.valueByKey("SQLENGINE_BOOLEAN")),
            labelBoolean
        );
        I18nView.addComponentForKey("SQLENGINE_BOOLEAN", labelBoolean);
        /**/

        /**/
        JTabbedPane tabsConfiguration = new JTabbedPane();
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_ORDER_BY"), new LightScrollPane(1, 0, 1, 0, this.textareaOrderBy));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, this.textareaSlidingWindow));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, this.textareaLimit));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CAPACITY"), new LightScrollPane(1, 0, 1, 0, this.textareaCapacity));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CALIBRATOR"), new LightScrollPane(1, 0, 1, 0, this.textareaCalibrator));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_TRAPCANCELLER"), new LightScrollPane(1, 0, 1, 0, this.textareaFailsafe));
        // TODO Add tab also for payload
        tabsConfiguration.addTab("End comment", null);
        
        /* Configuration */
        JLabel labelOrderBy = new JLabel(I18n.valueByKey("SQLENGINE_ORDER_BY"));
        tabsConfiguration.setTabComponentAt(
            tabsConfiguration.indexOfTab(I18n.valueByKey("SQLENGINE_ORDER_BY")),
            labelOrderBy
        );
        I18nView.addComponentForKey("SQLENGINE_ORDER_BY", labelOrderBy);

        JLabel labelCharactersSlidingWindows = new JLabel(I18n.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW"));
        tabsConfiguration.setTabComponentAt(
            tabsConfiguration.indexOfTab(I18n.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW")),
            labelCharactersSlidingWindows
        );
        I18nView.addComponentForKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW", labelCharactersSlidingWindows);

        JLabel labelRowsSlidingWindows = new JLabel(I18n.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW"));
        tabsConfiguration.setTabComponentAt(
            tabsConfiguration.indexOfTab(I18n.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW")),
            labelRowsSlidingWindows
        );
        I18nView.addComponentForKey("SQLENGINE_ROWS_SLIDINGWINDOW", labelRowsSlidingWindows);

        JLabel labelCapacity = new JLabel(I18n.valueByKey("SQLENGINE_CAPACITY"));
        tabsConfiguration.setTabComponentAt(
            tabsConfiguration.indexOfTab(I18n.valueByKey("SQLENGINE_CAPACITY")),
            labelCapacity
        );
        I18nView.addComponentForKey("SQLENGINE_CAPACITY", labelCapacity);

        JLabel labelCalibrator = new JLabel(I18n.valueByKey("SQLENGINE_CALIBRATOR"));
        tabsConfiguration.setTabComponentAt(
            tabsConfiguration.indexOfTab(I18n.valueByKey("SQLENGINE_CALIBRATOR")),
            labelCalibrator
        );
        I18nView.addComponentForKey("SQLENGINE_CALIBRATOR", labelCalibrator);

        JLabel labelTrapCanceller = new JLabel(I18n.valueByKey("SQLENGINE_TRAPCANCELLER"));
        tabsConfiguration.setTabComponentAt(
            tabsConfiguration.indexOfTab(I18n.valueByKey("SQLENGINE_TRAPCANCELLER")),
            labelTrapCanceller
        );
        I18nView.addComponentForKey("SQLENGINE_TRAPCANCELLER", labelTrapCanceller);
        /**/
        
        JPanel panelConfiguration = new JPanel(new BorderLayout());
        panelConfiguration.add(tabsConfiguration, BorderLayout.CENTER);
        panelConfiguration.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));

        tabsBottom.addTab(I18n.valueByKey("SQLENGINE_CONFIGURATION"), panelConfiguration);
        
        JLabel labelConfiguration = new JLabel(I18n.valueByKey("SQLENGINE_CONFIGURATION"));
        tabsBottom.setTabComponentAt(
            tabsBottom.indexOfTab(I18n.valueByKey("SQLENGINE_CONFIGURATION")),
            labelConfiguration
        );
        I18nView.addComponentForKey("SQLENGINE_CONFIGURATION", labelConfiguration);

        this.setLayout(new OverlayLayout(this));

        JPanel panelCombo = new JPanel();
        panelCombo.setLayout(new BorderLayout());
        panelCombo.setOpaque(false);

        // Disable overlap with zerosizesplitter
        panelCombo.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        panelCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 25));
        panelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        panelCombo.add(comboBoxVendors, BorderLayout.LINE_END);
        
        JMenuBar menuBarVendor = new JMenuBar();
        menuBarVendor.setOpaque(false);
        menuBarVendor.setBorder(null);
        
        JMenu comboMenuVendor = new ComboMenu(MediatorModel.model().getMediatorVendor().getVendor().toString());
        menuBarVendor.add(comboMenuVendor);

        ButtonGroup groupVendor = new ButtonGroup();

        for (final Vendor vendor: listVendors) {
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == MediatorModel.model().getMediatorVendor().getVendor());
            itemRadioVendor.addActionListener(actionEvent -> {
                this.modelYaml = vendor.instance().getModelYaml();
                this.changeVendor();
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
        this.add(tabsBottom);

        // Do Overlay
        panelCombo.setAlignmentX(FlowLayout.TRAILING);
        panelCombo.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        tabsBottom.setAlignmentX(FlowLayout.LEADING);
        tabsBottom.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        
        MediatorGui.menubar().switchLocale(I18n.getLocaleDefault());
    }
    
    private void initErrorTabs() {
        SqlEngine.TAB_ERROR.removeAll();
        
        if (this.modelYaml.getStrategy().getError() != null) {
            for (Method methodError : this.modelYaml.getStrategy().getError().getMethod()) {
                JPanel panelError = new JPanel(new BorderLayout());
                
                final Method[] m = new Method[]{methodError};
                JTextPaneLexer textPane = new JTextPaneLexer(false) {
                    @Override
                    public void switchSetterToVendor() {
                        this.attributeSetter = new AttributeSetterForVendor(m[0], "setQuery");
                    }
                };
                
                SqlEngine.resetLexer(textPane);
                textPane.switchSetterToVendor();
                textPane.setText(methodError.getQuery().trim());
                textPane.setBorder(SqlEngine.BORDER_RIGHT);
    
                panelError.add(new LightScrollPane(1, 0, 1, 0, textPane), BorderLayout.CENTER);
                
                JPanel panelLimit = new JPanel();
                panelLimit.setLayout(new BoxLayout(panelLimit, BoxLayout.LINE_AXIS));
                panelLimit.add(new JLabel(" Overflow limit: "));
                panelLimit.add(new JTextField(Integer.toString(methodError.getCapacity())));
                
                panelError.add(panelLimit, BorderLayout.SOUTH);
    
                SqlEngine.TAB_ERROR.addTab(methodError.getName(), panelError);
                
                SqlEngine.TAB_ERROR.setTitleAt(SqlEngine.TAB_ERROR.getTabCount() - 1, "<html><div style=\"text-align:left;width:150px;\">"+ methodError.getName() +"</div></html>");
            }
        }
    }
    
    private void showSql(ModelYaml modelYaml) {
        TEXTPANES_LEXER.stream().forEach(textPaneLexer -> textPaneLexer.setText(""));

        this.textareaDatabase.setText(modelYaml.getResource().getSchema().getDatabase().trim());
        this.textareaTable.setText(modelYaml.getResource().getSchema().getTable().trim());
        this.textareaColumn.setText(modelYaml.getResource().getSchema().getColumn().trim());
        this.textareaQuery.setText(modelYaml.getResource().getSchema().getRow().getQuery().trim());
        this.textareaField.setText(modelYaml.getResource().getSchema().getRow().getFields().getField().trim());
        this.textareaConcat.setText(modelYaml.getResource().getSchema().getRow().getFields().getConcat().trim());
        
        if (modelYaml.getResource().getZipped() != null) {
            this.textareaDatabaseZipped.setText(modelYaml.getResource().getZipped().getDatabase().trim());
            this.textareaTableZipped.setText(modelYaml.getResource().getZipped().getTable().trim());
            this.textareaColumnZipped.setText(modelYaml.getResource().getZipped().getColumn().trim());
            this.textareaQueryZipped.setText(modelYaml.getResource().getZipped().getRow().getQuery().trim());
            this.textareaFieldZipped.setText(modelYaml.getResource().getZipped().getRow().getFields().getField().trim());
            this.textareaConcatZipped.setText(modelYaml.getResource().getZipped().getRow().getFields().getConcat().trim());
        }
        
        if (modelYaml.getResource().getDios() != null) {
            this.textareaDatabaseDios.setText(modelYaml.getResource().getDios().getDatabase().trim());
            this.textareaTableDios.setText(modelYaml.getResource().getDios().getTable().trim());
            this.textareaColumnDios.setText(modelYaml.getResource().getDios().getColumn().trim());
            this.textareaQueryDios.setText(modelYaml.getResource().getDios().getRow().getQuery().trim());
            this.textareaFieldDios.setText(modelYaml.getResource().getDios().getRow().getFields().getField().trim());
            this.textareaConcatDios.setText(modelYaml.getResource().getDios().getRow().getFields().getConcat().trim());
        }
        
        this.textareaInfo.setText(modelYaml.getResource().getInfo().trim());
        
        this.textareaSlidingWindow.setText(modelYaml.getStrategy().getConfiguration().getSlidingWindow().trim());
        this.textareaLimit.setText(modelYaml.getStrategy().getConfiguration().getLimit().trim());
        this.textareaFailsafe.setText(modelYaml.getStrategy().getConfiguration().getFailsafe().trim());
        this.textareaCalibrator.setText(modelYaml.getStrategy().getConfiguration().getCalibrator().trim());

        this.textareaIndices.setText(modelYaml.getStrategy().getNormal().getIndices().trim());
        this.textareaCapacity.setText(modelYaml.getStrategy().getNormal().getCapacity().trim());
        this.textareaOrderBy.setText(modelYaml.getStrategy().getNormal().getOrderBy().trim());

        if (modelYaml.getStrategy().getBoolean() != null) {
            if (modelYaml.getStrategy().getBoolean().getBlind() != null) {
                this.textareaBlind.setText(modelYaml.getStrategy().getBoolean().getBlind().trim());
            }
            if (modelYaml.getStrategy().getBoolean().getTime() != null) {
                this.textareaTime.setText(modelYaml.getStrategy().getBoolean().getTime().trim());
            }
            this.textareaBitTest.setText(modelYaml.getStrategy().getBoolean().getTest().getBit().trim());
            this.textareaLengthTest.setText(modelYaml.getStrategy().getBoolean().getTest().getLength().trim());
        }

        this.initErrorTabs();
    }
    
    private static void resetLexer(JTextPaneLexer textPane) {
        if (textPane.getStyledDocument() instanceof HighlightedDocument) {
            HighlightedDocument oldDocument = (HighlightedDocument) textPane.getStyledDocument();
            oldDocument.stopColorer();
        }
        
        HighlightedDocument document = new HighlightedDocument(HighlightedDocument.SQL_STYLE);
        document.setHighlightStyle(HighlightedDocument.SQL_STYLE);
        textPane.setStyledDocument(document);
        
        document.addDocumentListener(new DocumentListenerTyping() {
            
            @Override
            public void warn() {
                textPane.setAttribute();
            }
            
        });
    }
    
    private void changeVendor() {
        TEXTPANES_LEXER.stream().forEach(SqlEngine::resetLexer);
        TEXTPANES_LEXER.stream().forEach(JTextPaneObjectMethod::switchSetterToVendor);
        SqlEngine.this.showSql(this.modelYaml);
    }

}
