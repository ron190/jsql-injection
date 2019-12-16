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
import com.jsql.model.injection.vendor.MediatorVendor.Vendor;
import com.jsql.model.injection.vendor.model.Model;
import com.jsql.model.injection.vendor.model.Model.Strategy.Error.Method;
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

    private static Model xmlModel = MediatorModel.model().mediatorVendor.getVendor().instance().getXmlModel();

    private static final List<JTextPaneLexer> MAP_TEXTPANE_TO_XML = new ArrayList<>();
    
    private static final JTabbedPane TAB_ERROR = new JTabbedPane(JTabbedPane.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);

    private static final Border BORDER_RIGHT = BorderFactory.createMatteBorder(0, 0, 0, 1, HelperUi.COLOR_COMPONENT_BORDER);
    
    private interface JTextPaneObjectMethod {
        default void switchSetterToVendor() {
            
        }
    }
    
    private static class JTextPaneLexer extends JTextPane implements JTextPaneObjectMethod {
        
        protected transient AttributeSetterForVendor attributeSetter = null;
        
        public JTextPaneLexer(boolean isGeneric) {
            if (isGeneric) {
                MAP_TEXTPANE_TO_XML.add(this);
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
    private static final JTextPaneLexer textareaDatabase = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getSchema(), "setDatabase");
        }
    };
    private static final JTextPaneLexer textareaTable = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getSchema(), "setTable");
        }
    };
    private static final JTextPaneLexer textareaColumn = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getSchema(), "setColumn");
        }
    };
    private static final JTextPaneLexer textareaQuery = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getSchema().getRow(), "setQuery");
        }
    };
    private static final JTextPaneLexer textareaField = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getSchema().getRow().getFields(), "setField");
        }
    };
    private static final JTextPaneLexer textareaConcat = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getSchema().getRow().getFields(), "setConcat");
        }
    };
    
    private static final JTextPaneLexer textareaInfo = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource(), "setInfo");
        }
    };
    
    // Zipped
    private static final JTextPaneLexer textareaDatabaseZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getZipped(), "setDatabase");
            }
        }
    };
    private static final JTextPaneLexer textareaTableZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getZipped(), "setTable");
            }
        }
    };
    private static final JTextPaneLexer textareaColumnZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getZipped(), "setColumn");
            }
        }
    };
    private static final JTextPaneLexer textareaQueryZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getZipped().getRow(), "setQuery");
            }
        }
    };
    private static final JTextPaneLexer textareaFieldZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getZipped().getRow().getFields(), "setField");
            }
        }
    };
    private static final JTextPaneLexer textareaConcatZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getZipped() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getZipped().getRow().getFields(), "setConcat");
            }
        }
    };
    
    // Dios
    private static final JTextPaneLexer textareaDatabaseDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getDios(), "setDatabase");
            }
        }
    };
    private static final JTextPaneLexer textareaTableDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getDios(), "setTable");
            }
        }
    };
    private static final JTextPaneLexer textareaColumnDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getDios(), "setColumn");
            }
        }
    };
    private static final JTextPaneLexer textareaQueryDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getDios().getRow(), "setQuery");
            }
        }
    };
    private static final JTextPaneLexer textareaFieldDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getDios().getRow().getFields(), "setField");
            }
        }
    };
    private static final JTextPaneLexer textareaConcatDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getResource().getDios() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource().getDios().getRow().getFields(), "setConcat");
            }
        }
    };
     
    // Configuration
    private static final JTextPaneLexer textareaSlidingWindow = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getConfiguration(), "setSlidingWindow");
        }
    };
    private static final JTextPaneLexer textareaLimit = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getConfiguration(), "setLimit");
        }
    };
    private static final JTextPaneLexer textareaFailsafe = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getConfiguration(), "setFailsafe");
        }
    };
    private static final JTextPaneLexer textareaCalibrator = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getConfiguration(), "setCalibrator");
        }
    };
    private static final JTextPaneLexer textareaCapacity = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getNormal(), "setCapacity");
        }
    };
    private static final JTextPaneLexer textareaOrderBy = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getNormal(), "setOrderBy");
        }
    };
    
    // Normal
    private static final JTextPaneLexer textareaIndices = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getNormal(), "setIndices");
        }
    };
     
    // Boolean
    private static final JTextPaneLexer textareaBlind = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getBoolean(), "setBlind");
            }
        }
    };
    private static final JTextPaneLexer textareaTime = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getBoolean(), "setTime");
            }
        }
    };
    private static final JTextPaneLexer textareaBitTest = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getBoolean().getTest(), "setBit");
            }
        }
    };
    private static final JTextPaneLexer textareaLengthTest = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getBoolean().getTest(), "setLength");
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
        
        List<Vendor> listVendors = new LinkedList<>(MediatorModel.model().mediatorVendor.vendors);
        listVendors.removeIf(i -> i == MediatorModel.model().mediatorVendor.AUTO);
        
        JComboBox<Vendor> comboBoxVendors = new JComboBox<>(listVendors.toArray(new Vendor[0]));
        comboBoxVendors.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                xmlModel = ((Vendor) itemEvent.getItem()).instance().getXmlModel();
                this.changeVendor();
            }
        });
        
        comboBoxVendors.setSelectedItem(MediatorModel.model().mediatorVendor.getVendor());
        this.changeVendor();
        
        JTabbedPane tabsStandard = new JTabbedPane(JTabbedPane.RIGHT);
        
        textareaDatabase.setBorder(SqlEngine.BORDER_RIGHT);
        textareaTable.setBorder(SqlEngine.BORDER_RIGHT);
        textareaColumn.setBorder(SqlEngine.BORDER_RIGHT);
        textareaQuery.setBorder(SqlEngine.BORDER_RIGHT);
        textareaField.setBorder(SqlEngine.BORDER_RIGHT);
        textareaConcat.setBorder(SqlEngine.BORDER_RIGHT);
        textareaInfo.setBorder(SqlEngine.BORDER_RIGHT);
        
        textareaDatabaseZipped.setBorder(SqlEngine.BORDER_RIGHT);
        textareaTableZipped.setBorder(SqlEngine.BORDER_RIGHT);
        textareaColumnZipped.setBorder(SqlEngine.BORDER_RIGHT);
        textareaQueryZipped.setBorder(SqlEngine.BORDER_RIGHT);
        textareaFieldZipped.setBorder(SqlEngine.BORDER_RIGHT);
        textareaConcatZipped.setBorder(SqlEngine.BORDER_RIGHT);
        
        textareaDatabaseDios.setBorder(SqlEngine.BORDER_RIGHT);
        textareaTableDios.setBorder(SqlEngine.BORDER_RIGHT);
        textareaColumnDios.setBorder(SqlEngine.BORDER_RIGHT);
        textareaQueryDios.setBorder(SqlEngine.BORDER_RIGHT);
        textareaFieldDios.setBorder(SqlEngine.BORDER_RIGHT);
        textareaConcatDios.setBorder(SqlEngine.BORDER_RIGHT);
        
        textareaBlind.setBorder(SqlEngine.BORDER_RIGHT);
        textareaTime.setBorder(SqlEngine.BORDER_RIGHT);
        textareaBitTest.setBorder(SqlEngine.BORDER_RIGHT);
        textareaLengthTest.setBorder(SqlEngine.BORDER_RIGHT);
        
        JTabbedPane tabsSchema = new JTabbedPane();
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_DATABASES"), new LightScrollPane(1, 0, 1, 0, textareaDatabase));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_TABLES"), new LightScrollPane(1, 0, 1, 0, textareaTable));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_COLUMNS"), new LightScrollPane(1, 0, 1, 0, textareaColumn));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_ROWS"), new LightScrollPane(1, 0, 1, 0, textareaQuery));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_FIELD"), new LightScrollPane(1, 0, 1, 0, textareaField));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_FIELDS_SEPARATOR"), new LightScrollPane(1, 0, 1, 0, textareaConcat));
        tabsSchema.addTab(I18n.valueByKey("SQLENGINE_METADATA"), new LightScrollPane(1, 0, 1, 0, textareaInfo));
        
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
        tabsZipped.addTab("Databases", new LightScrollPane(1, 0, 1, 0, textareaDatabaseZipped));
        tabsZipped.addTab("Tables", new LightScrollPane(1, 0, 1, 0, textareaTableZipped));
        tabsZipped.addTab("Columns", new LightScrollPane(1, 0, 1, 0, textareaColumnZipped));
        tabsZipped.addTab("Rows", new LightScrollPane(1, 0, 1, 0, textareaQueryZipped));
        tabsZipped.addTab("Field", new LightScrollPane(1, 0, 1, 0, textareaFieldZipped));
        tabsZipped.addTab("Fields separator", new LightScrollPane(1, 0, 1, 0, textareaConcatZipped));
//        tabsZipped.addTab("Metadata", new LightScrollPane(1, 0, 1, 0, textareaInfo));
        
        JTabbedPane tabsDios = new JTabbedPane();
        tabsDios.addTab("Databases", new LightScrollPane(1, 0, 1, 0, textareaDatabaseDios));
        tabsDios.addTab("Tables", new LightScrollPane(1, 0, 1, 0, textareaTableDios));
        tabsDios.addTab("Columns", new LightScrollPane(1, 0, 1, 0, textareaColumnDios));
        tabsDios.addTab("Rows", new LightScrollPane(1, 0, 1, 0, textareaQueryDios));
        tabsDios.addTab("Field", new LightScrollPane(1, 0, 1, 0, textareaFieldDios));
        tabsDios.addTab("Fields separator", new LightScrollPane(1, 0, 1, 0, textareaConcatDios));
//        tabsDios.addTab("Metadata", new LightScrollPane(1, 0, 1, 0, textareaInfo));
        
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
        tabsStrategy.addTab(I18n.valueByKey("SQLENGINE_NORMAL"), new LightScrollPane(1, 0, 1, 0, textareaIndices));
        
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
        JTabbedPane tabsBoolean = new JTabbedPane(JTabbedPane.RIGHT);
        tabsBoolean.addTab("Blind", new LightScrollPane(1, 0, 1, 0, textareaBlind));
        tabsBoolean.addTab("Time", new LightScrollPane(1, 0, 1, 0, textareaTime));
        tabsBoolean.addTab("Bit Test", new LightScrollPane(1, 0, 1, 0, textareaBitTest));
        tabsBoolean.addTab("Length Test", new LightScrollPane(1, 0, 1, 0, textareaLengthTest));
        
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
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_ORDER_BY"), new LightScrollPane(1, 0, 1, 0, textareaOrderBy));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, textareaSlidingWindow));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, textareaLimit));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CAPACITY"), new LightScrollPane(1, 0, 1, 0, textareaCapacity));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CALIBRATOR"), new LightScrollPane(1, 0, 1, 0, textareaCalibrator));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_TRAPCANCELLER"), new LightScrollPane(1, 0, 1, 0, textareaFailsafe));
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
        
        JMenu comboMenuVendor = new ComboMenu(MediatorModel.model().mediatorVendor.getVendor().toString());
        menuBarVendor.add(comboMenuVendor);

        ButtonGroup groupVendor = new ButtonGroup();

        for (final Vendor vendor: listVendors) {
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == MediatorModel.model().mediatorVendor.getVendor());
            itemRadioVendor.addActionListener(actionEvent -> {
                xmlModel = vendor.instance().getXmlModel();
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
        
        if (xmlModel.getStrategy().getError() != null) {
            for (Method methodError : xmlModel.getStrategy().getError().getMethod()) {
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
    
    private void showSql(Model model) {
        MAP_TEXTPANE_TO_XML.stream().forEach(textPaneLexer -> textPaneLexer.setText(""));

        textareaDatabase.setText(model.getResource().getSchema().getDatabase().trim());
        textareaTable.setText(model.getResource().getSchema().getTable().trim());
        textareaColumn.setText(model.getResource().getSchema().getColumn().trim());
        textareaQuery.setText(model.getResource().getSchema().getRow().getQuery().trim());
        textareaField.setText(model.getResource().getSchema().getRow().getFields().getField().trim());
        textareaConcat.setText(model.getResource().getSchema().getRow().getFields().getConcat().trim());
        
        if (model.getResource().getZipped() != null) {
            textareaDatabaseZipped.setText(model.getResource().getZipped().getDatabase().trim());
            textareaTableZipped.setText(model.getResource().getZipped().getTable().trim());
            textareaColumnZipped.setText(model.getResource().getZipped().getColumn().trim());
            textareaQueryZipped.setText(model.getResource().getZipped().getRow().getQuery().trim());
            textareaFieldZipped.setText(model.getResource().getZipped().getRow().getFields().getField().trim());
            textareaConcatZipped.setText(model.getResource().getZipped().getRow().getFields().getConcat().trim());
        }
        
        if (model.getResource().getDios() != null) {
            textareaDatabaseDios.setText(model.getResource().getDios().getDatabase().trim());
            textareaTableDios.setText(model.getResource().getDios().getTable().trim());
            textareaColumnDios.setText(model.getResource().getDios().getColumn().trim());
            textareaQueryDios.setText(model.getResource().getDios().getRow().getQuery().trim());
            textareaFieldDios.setText(model.getResource().getDios().getRow().getFields().getField().trim());
            textareaConcatDios.setText(model.getResource().getDios().getRow().getFields().getConcat().trim());
        }
        
        textareaInfo.setText(model.getResource().getInfo().trim());
        
        textareaSlidingWindow.setText(model.getStrategy().getConfiguration().getSlidingWindow().trim());
        textareaLimit.setText(model.getStrategy().getConfiguration().getLimit().trim());
        textareaFailsafe.setText(model.getStrategy().getConfiguration().getFailsafe().trim());
        textareaCalibrator.setText(model.getStrategy().getConfiguration().getCalibrator().trim());

        textareaIndices.setText(model.getStrategy().getNormal().getIndices().trim());
        textareaCapacity.setText(model.getStrategy().getNormal().getCapacity().trim());
        textareaOrderBy.setText(model.getStrategy().getNormal().getOrderBy().trim());

        if (model.getStrategy().getBoolean() != null) {
            if (model.getStrategy().getBoolean().getBlind() != null) {
                textareaBlind.setText(model.getStrategy().getBoolean().getBlind().trim());
            }
            if (model.getStrategy().getBoolean().getTime() != null) {
                textareaTime.setText(model.getStrategy().getBoolean().getTime().trim());
            }
            textareaBitTest.setText(model.getStrategy().getBoolean().getTest().getBit().trim());
            textareaLengthTest.setText(model.getStrategy().getBoolean().getTest().getLength().trim());
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
        MAP_TEXTPANE_TO_XML.stream().forEach(SqlEngine::resetLexer);
        MAP_TEXTPANE_TO_XML.stream().forEach(JTextPaneObjectMethod::switchSetterToVendor);
        SqlEngine.this.showSql(xmlModel);
    }

}
