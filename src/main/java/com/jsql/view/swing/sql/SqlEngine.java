package com.jsql.view.swing.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.injection.vendor.Model;
import com.jsql.model.injection.vendor.Model.Strategy.Error.Method;
import com.jsql.model.injection.vendor.Vendor;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.sql.lexer.HighlightedDocument;

@SuppressWarnings("serial")
public class SqlEngine extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private static Model xmlModel = MediatorModel.model().getVendor().instance().getXmlModel();

    private static final List<JTextPaneLexer> mapTextPaneToXml = new ArrayList<>();
    
    private JTabbedPane tabError = new JTabbedPane();
    
    interface JTextPaneObjectMethod {
        default void switchSetterToVendor() {
            
        }
    }
    
    private static class JTextPaneLexer extends JTextPane implements JTextPaneObjectMethod {
        
        protected transient AttributeSetterForVendor attributeSetter = null;
        
        public JTextPaneLexer(boolean isGeneric) {
            if (isGeneric) {
                mapTextPaneToXml.add(this);
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
    private static final JTextPaneLexer textareaInfo = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getResource(), "setInfo");
        }
    };
     
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
     
    private static final JTextPaneLexer textareaIndices = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getNormal(), "setIndices");
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
    private static final JTextPaneLexer textareaBlindTimeBit = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getBoolean().getTest(), "setBit");
            }
        }
    };
    private static final JTextPaneLexer textareaBlindTimeLength = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            if (xmlModel.getStrategy().getBoolean() != null) {
                this.attributeSetter = new AttributeSetterForVendor(xmlModel.getStrategy().getBoolean().getTest(), "setLength");
            }
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
        
        List<Vendor> listVendors = new LinkedList<>(Arrays.asList(Vendor.values()));
        listVendors.removeIf(i -> i == Vendor.AUTO);
        
        JComboBox<Vendor> comboBoxVendors = new JComboBox<>(listVendors.toArray(new Vendor[0]));
        comboBoxVendors.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                xmlModel = ((Vendor) itemEvent.getItem()).instance().getXmlModel();
                this.changeVendor();
            }
        });
        
        comboBoxVendors.setSelectedItem(MediatorModel.model().getVendor());
        this.changeVendor();
        
        JTabbedPane tabsSchema = new JTabbedPane();
        tabsSchema.addTab("Databases", new LightScrollPane(1, 0, 1, 0, textareaDatabase));
        tabsSchema.addTab("Tables", new LightScrollPane(1, 0, 1, 0, textareaTable));
        tabsSchema.addTab("Columns", new LightScrollPane(1, 0, 1, 0, textareaColumn));
        tabsSchema.addTab("Rows", new LightScrollPane(1, 0, 1, 0, textareaQuery));
        tabsSchema.addTab("Field", new LightScrollPane(1, 0, 1, 0, textareaField));
        tabsSchema.addTab("Fields separator", new LightScrollPane(1, 0, 1, 0, textareaConcat));
        tabsSchema.addTab("Metadata", new LightScrollPane(1, 0, 1, 0, textareaInfo));
        
        JPanel panelSchema = new JPanel(new BorderLayout());
        panelSchema.add(tabsSchema, BorderLayout.CENTER);
        panelSchema.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
        
        JTabbedPane tabsStrategy = new JTabbedPane();
        tabsStrategy.addTab("Normal", new LightScrollPane(1, 0, 1, 0, textareaIndices));
        
        JPanel panelStrategy = new JPanel(new BorderLayout());
        panelStrategy.add(tabsStrategy, BorderLayout.CENTER);
        panelStrategy.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
        
        JTabbedPane tabsBottom = new JTabbedPane(SwingConstants.BOTTOM);
        tabsBottom.addTab("Structure", panelSchema);
        tabsBottom.addTab("Strategy", panelStrategy);

        /*Error*/
        JPanel panelError = new JPanel(new BorderLayout());
        panelError.add(this.tabError, BorderLayout.CENTER);
        panelError.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
        
        tabsStrategy.addTab("Error", panelError);

        /*Boolean*/
        JTabbedPane tabsBoolean = new JTabbedPane();
        tabsBoolean.addTab("Blind", new LightScrollPane(1, 0, 1, 0, textareaBlind));
        tabsBoolean.addTab("Time", new LightScrollPane(1, 0, 1, 0, textareaTime));
        tabsBoolean.addTab("Bit", new LightScrollPane(1, 0, 1, 0, textareaBlindTimeBit));
        tabsBoolean.addTab("Length", new LightScrollPane(1, 0, 1, 0, textareaBlindTimeLength));
        
        JPanel panelBoolean = new JPanel(new BorderLayout());
        panelBoolean.add(tabsBoolean, BorderLayout.CENTER);
        panelBoolean.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
        
        tabsStrategy.addTab("Boolean", panelBoolean);

        /**/
        JTabbedPane tabsConfiguration = new JTabbedPane();
        tabsConfiguration.addTab("Order by", new LightScrollPane(1, 0, 1, 0, textareaOrderBy));
        tabsConfiguration.addTab("Rows Sliding Window", new LightScrollPane(1, 0, 1, 0, textareaLimit));
        tabsConfiguration.addTab("Calibrator", new LightScrollPane(1, 0, 1, 0, textareaCalibrator));
        tabsConfiguration.addTab("Capacity", new LightScrollPane(1, 0, 1, 0, textareaCapacity));
        tabsConfiguration.addTab("Characters Sliding Window", new LightScrollPane(1, 0, 1, 0, textareaSlidingWindow));
        tabsConfiguration.addTab("Trap Canceller", new LightScrollPane(1, 0, 1, 0, textareaFailsafe));
        
        JPanel panelConfiguration = new JPanel(new BorderLayout());
        panelConfiguration.add(tabsConfiguration, BorderLayout.CENTER);
        panelConfiguration.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));

        tabsBottom.addTab("Configuration", panelConfiguration);

        this.setLayout(new OverlayLayout(this));

        JPanel panelCombo = new JPanel();
        panelCombo.setLayout(new BorderLayout());
        panelCombo.setOpaque(false);

        // Disable overlap with zerosizesplitter
        panelCombo.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        panelCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 25));
        panelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        panelCombo.add(comboBoxVendors, BorderLayout.LINE_END);
        this.add(panelCombo);
        this.add(tabsBottom);

        // Do Overlay
        panelCombo.setAlignmentX(FlowLayout.TRAILING);
        panelCombo.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        tabsBottom.setAlignmentX(FlowLayout.LEADING);
        tabsBottom.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    }
    
    private void initErrorTabs() {
        this.tabError.removeAll();
        
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
    
                panelError.add(new LightScrollPane(1, 0, 1, 0, textPane), BorderLayout.CENTER);
                
                JPanel panelLimit = new JPanel();
                panelLimit.setLayout(new BoxLayout(panelLimit, BoxLayout.LINE_AXIS));
                panelLimit.add(new JLabel(" Overflow limit: "));
                panelLimit.add(new JTextField(Integer.toString(methodError.getCapacity())));
                
                panelError.add(panelLimit, BorderLayout.SOUTH);
    
                this.tabError.addTab(methodError.getName(), panelError);
            }
        }
    }
    
    private void showSql(Model model) {
        mapTextPaneToXml.stream().forEach(textPaneLexer -> textPaneLexer.setText(""));

        textareaDatabase.setText(model.getResource().getSchema().getDatabase().trim());
        textareaTable.setText(model.getResource().getSchema().getTable().trim());
        textareaColumn.setText(model.getResource().getSchema().getColumn().trim());
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
            textareaBlindTimeBit.setText(model.getStrategy().getBoolean().getTest().getBit().trim());
            textareaBlindTimeLength.setText(model.getStrategy().getBoolean().getTest().getLength().trim());
        }

        textareaQuery.setText(model.getResource().getSchema().getRow().getQuery().trim());
        textareaField.setText(model.getResource().getSchema().getRow().getFields().getField().trim());
        textareaConcat.setText(model.getResource().getSchema().getRow().getFields().getConcat().trim());
        
        this.initErrorTabs();
    }
    
    private static void resetLexer(JTextPaneLexer textPane) {
        if (textPane.getStyledDocument() instanceof HighlightedDocument) {
            HighlightedDocument oldDocument = (HighlightedDocument) textPane.getStyledDocument();
            oldDocument.stopColorer();
        }
        
        HighlightedDocument document = new HighlightedDocument();
        document.setHighlightStyle(HighlightedDocument.SQL_STYLE);
        textPane.setStyledDocument(document);
        
        document.addDocumentListener(new DocumentListener() {
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                this.warn();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                this.warn();
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                this.warn();
            }
            
            public void warn() {
                textPane.setAttribute();
            }
            
        });
    }
    
    private void changeVendor() {
        mapTextPaneToXml.stream().forEach(SqlEngine::resetLexer);
        mapTextPaneToXml.stream().forEach(textPaneLexer -> textPaneLexer.switchSetterToVendor());
        SqlEngine.this.showSql(xmlModel);
    }

}
