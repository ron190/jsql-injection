package com.jsql.view.swing.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import javax.swing.MenuSelectionManager;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

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
import com.jsql.view.swing.sql.text.AttributeSetterForVendor;
import com.jsql.view.swing.sql.text.JTextPaneLexer;
import com.jsql.view.swing.sql.text.JTextPaneObjectMethod;
import com.jsql.view.swing.tab.TabHeader.Cleanable;
import com.jsql.view.swing.text.listener.DocumentListenerTyping;

@SuppressWarnings("serial")
public class SqlEngine extends JPanel implements Cleanable {
    
    private ModelYaml modelYaml = MediatorModel.model().getMediatorVendor().getVendor().instance().getModelYaml();

    private static final JTabbedPane TAB_ERROR = new JTabbedPane(SwingConstants.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);

    private static final Border BORDER_RIGHT = BorderFactory.createMatteBorder(0, 0, 0, 1, HelperUi.COLOR_COMPONENT_BORDER);
    
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
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped(), "setDatabase");
        }
    };
    private final JTextPaneLexer textareaTableZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped(), "setTable");
        }
    };
    private final JTextPaneLexer textareaColumnZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped(), "setColumn");
        }
    };
    private final JTextPaneLexer textareaQueryZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped().getRow(), "setQuery");
        }
    };
    private final JTextPaneLexer textareaFieldZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped().getRow().getFields(), "setField");
        }
    };
    private final JTextPaneLexer textareaConcatZipped = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getZipped().getRow().getFields(), "setConcat");
        }
    };
    
    // Dios
    private final JTextPaneLexer textareaDatabaseDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios(), "setDatabase");
        }
    };
    private final JTextPaneLexer textareaTableDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios(), "setTable");
        }
    };
    private final JTextPaneLexer textareaColumnDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios(), "setColumn");
        }
    };
    private final JTextPaneLexer textareaQueryDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios().getRow(), "setQuery");
        }
    };
    private final JTextPaneLexer textareaFieldDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios().getRow().getFields(), "setField");
        }
    };
    private final JTextPaneLexer textareaConcatDios = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getResource().getDios().getRow().getFields(), "setConcat");
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
    private final JTextPaneLexer textareaEndingComment = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getConfiguration(), "setEndingComment");
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
    private final JTextPaneLexer textareaModeAnd = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean(), "setModeAnd");
        }
    };
    private final JTextPaneLexer textareaModeOr = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean(), "setModeOr");
        }
    };
    private final JTextPaneLexer textareaBlind = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean(), "setBlind");
        }
    };
    private final JTextPaneLexer textareaTime = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean(), "setTime");
        }
    };
    private final JTextPaneLexer textareaBitTest = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean().getTest(), "setBit");
        }
    };
    private final JTextPaneLexer textareaLengthTest = new JTextPaneLexer() {
        @Override
        public void switchSetterToVendor() {
            this.attributeSetter = new AttributeSetterForVendor(SqlEngine.this.modelYaml.getStrategy().getBoolean().getTest(), "setLength");
        }
    };
    
    private final List<JTextPaneLexer> textPanesError = new ArrayList<>();
    
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
        
        Stream.of(
            this.textareaDatabase,
            this.textareaTable,
            this.textareaColumn,
            this.textareaQuery,
            this.textareaField,
            this.textareaConcat,
            this.textareaInfo,
            
            this.textareaDatabaseZipped,
            this.textareaTableZipped,
            this.textareaColumnZipped,
            this.textareaQueryZipped,
            this.textareaFieldZipped,
            this.textareaConcatZipped,
            
            this.textareaDatabaseDios,
            this.textareaTableDios,
            this.textareaColumnDios,
            this.textareaQueryDios,
            this.textareaFieldDios,
            this.textareaConcatDios,
            
            this.textareaModeAnd,
            this.textareaModeOr,
            this.textareaBlind,
            this.textareaTime,
            this.textareaBitTest,
            this.textareaLengthTest
        ).forEach(textPane -> textPane.setBorder(SqlEngine.BORDER_RIGHT));
        
        JTabbedPane tabsSchema = new JTabbedPane();
        Stream.of(
            new SimpleEntry<>("SQLENGINE_DATABASES", this.textareaDatabase),
            new SimpleEntry<>("SQLENGINE_TABLES", this.textareaTable),
            new SimpleEntry<>("SQLENGINE_COLUMNS", this.textareaColumn),
            new SimpleEntry<>("SQLENGINE_ROWS", this.textareaQuery),
            new SimpleEntry<>("SQLENGINE_FIELD", this.textareaField),
            new SimpleEntry<>("SQLENGINE_FIELDS_SEPARATOR", this.textareaConcat),
            new SimpleEntry<>("SQLENGINE_METADATA", this.textareaInfo)
        )
        .forEach(entry -> {
            tabsSchema.addTab(
                I18n.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            JLabel label = new JLabel(I18n.valueByKey(entry.getKey()));
            tabsSchema.setTabComponentAt(
                tabsSchema.indexOfTab(I18n.valueByKey(entry.getKey())),
                label
            );
            I18nView.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsZipped = new JTabbedPane();
        Stream.of(
            new SimpleEntry<>("SQLENGINE_DATABASES", this.textareaDatabaseZipped),
            new SimpleEntry<>("SQLENGINE_TABLES", this.textareaTableZipped),
            new SimpleEntry<>("SQLENGINE_COLUMNS", this.textareaColumnZipped),
            new SimpleEntry<>("SQLENGINE_ROWS", this.textareaQueryZipped),
            new SimpleEntry<>("SQLENGINE_FIELD", this.textareaFieldZipped),
            new SimpleEntry<>("SQLENGINE_FIELDS_SEPARATOR", this.textareaConcatZipped)
        )
        .forEach(entry -> {
            tabsZipped.addTab(
                I18n.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            JLabel label = new JLabel(I18n.valueByKey(entry.getKey()));
            tabsZipped.setTabComponentAt(
                tabsZipped.indexOfTab(I18n.valueByKey(entry.getKey())),
                label
            );
            I18nView.addComponentForKey(entry.getKey(), label);
        });
        
        JTabbedPane tabsDios = new JTabbedPane();
        Stream.of(
            new SimpleEntry<>("SQLENGINE_DATABASES", this.textareaDatabaseDios),
            new SimpleEntry<>("SQLENGINE_TABLES", this.textareaTableDios),
            new SimpleEntry<>("SQLENGINE_COLUMNS", this.textareaColumnDios),
            new SimpleEntry<>("SQLENGINE_ROWS", this.textareaQueryDios),
            new SimpleEntry<>("SQLENGINE_FIELD", this.textareaFieldDios),
            new SimpleEntry<>("SQLENGINE_FIELDS_SEPARATOR", this.textareaConcatDios)
        )
        .forEach(entry -> {
            tabsDios.addTab(
                I18n.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            JLabel label = new JLabel(I18n.valueByKey(entry.getKey()));
            tabsDios.setTabComponentAt(
                tabsDios.indexOfTab(I18n.valueByKey(entry.getKey())),
                label
            );
            I18nView.addComponentForKey(entry.getKey(), label);
        });
        
        Stream.of(
            new SimpleEntry<>("SQLENGINE_STANDARD", tabsSchema),
            new SimpleEntry<>("SQLENGINE_ZIPPED", tabsZipped),
            new SimpleEntry<>("SQLENGINE_DIOS", tabsDios)
        ).forEach(entry -> {
            tabsStandard.addTab(I18n.valueByKey(entry.getKey()), entry.getValue());
            
            JLabel label = new JLabel(I18n.valueByKey(entry.getKey()));
            tabsStandard.setTabComponentAt(
                tabsStandard.indexOfTab(I18n.valueByKey(entry.getKey())),
                label
            );
            I18nView.addComponentForKey(entry.getKey(), label);
        });
        
        JPanel panelStructure = new JPanel(new BorderLayout());
        panelStructure.add(tabsStandard, BorderLayout.CENTER);
        panelStructure.setBorder(BorderFactory.createEmptyBorder());
        
        JTabbedPane tabsStrategy = new JTabbedPane();
        tabsStrategy.addTab(I18n.valueByKey("SQLENGINE_NORMAL"), new LightScrollPane(1, 0, 1, 0, this.textareaIndices));
        
        JPanel panelStrategy = new JPanel(new BorderLayout());
        panelStrategy.add(tabsStrategy, BorderLayout.CENTER);
        panelStrategy.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));
        
        
        /*Error*/
        JPanel panelError = new JPanel(new BorderLayout());
        panelError.add(SqlEngine.TAB_ERROR, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18n.valueByKey("SQLENGINE_ERROR"), panelError);

        /*Boolean*/
        JTabbedPane tabsBoolean = new JTabbedPane(SwingConstants.RIGHT);
        Stream.of(
            new SimpleEntry<>("AND mode", this.textareaModeAnd),
            new SimpleEntry<>("OR mode", this.textareaModeOr),
            new SimpleEntry<>("Blind", this.textareaBlind),
            new SimpleEntry<>("Time", this.textareaTime),
            new SimpleEntry<>("Bit Test", this.textareaBitTest),
            new SimpleEntry<>("Length Test", this.textareaLengthTest)
        )
        .forEach(entry -> tabsBoolean.addTab(entry.getKey(), new LightScrollPane(1, 0, 1, 0, entry.getValue())));
        
        JPanel panelBoolean = new JPanel(new BorderLayout());
        panelBoolean.add(tabsBoolean, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18n.valueByKey("SQLENGINE_BOOLEAN"), panelBoolean);
        
        /* Strategy */
        Stream.of(
            "SQLENGINE_NORMAL",
            "SQLENGINE_ERROR",
            "SQLENGINE_BOOLEAN"
        )
        .forEach(keyI18n -> {
            JLabel label = new JLabel(I18n.valueByKey(keyI18n));
            
            tabsStrategy.setTabComponentAt(
                tabsStrategy.indexOfTab(I18n.valueByKey(keyI18n)),
                label
            );
            
            I18nView.addComponentForKey(keyI18n, label);
        });

        /**/
        JTabbedPane tabsConfiguration = new JTabbedPane();
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_ORDER_BY"), new LightScrollPane(1, 0, 1, 0, this.textareaOrderBy));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, this.textareaSlidingWindow));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, this.textareaLimit));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CAPACITY"), new LightScrollPane(1, 0, 1, 0, this.textareaCapacity));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_CALIBRATOR"), new LightScrollPane(1, 0, 1, 0, this.textareaCalibrator));
        tabsConfiguration.addTab(I18n.valueByKey("SQLENGINE_TRAPCANCELLER"), new LightScrollPane(1, 0, 1, 0, this.textareaFailsafe));
        tabsConfiguration.addTab("End comment", new LightScrollPane(1, 0, 1, 0, this.textareaEndingComment));
        
        /* Configuration */

        Stream.of(
            "SQLENGINE_ORDER_BY",
            "SQLENGINE_CHARACTERS_SLIDINGWINDOW",
            "SQLENGINE_ROWS_SLIDINGWINDOW",
            "SQLENGINE_CAPACITY",
            "SQLENGINE_CALIBRATOR",
            "SQLENGINE_TRAPCANCELLER"
        )
        .forEach(keyI18n -> {
            JLabel label = new JLabel(I18n.valueByKey(keyI18n));
            tabsConfiguration.setTabComponentAt(
                tabsConfiguration.indexOfTab(I18n.valueByKey(keyI18n)),
                label
            );
            
            I18nView.addComponentForKey(keyI18n, label);
        });
        
        JPanel panelConfiguration = new JPanel(new BorderLayout());
        panelConfiguration.add(tabsConfiguration, BorderLayout.CENTER);
        panelConfiguration.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, HelperUi.COLOR_COMPONENT_BORDER));

        JTabbedPane tabsBottom = new JTabbedPane(SwingConstants.BOTTOM);
        Stream.of(
            new SimpleEntry<>("SQLENGINE_STRUCTURE", panelStructure),
            new SimpleEntry<>("SQLENGINE_STRATEGY", panelStrategy),
            new SimpleEntry<>("SQLENGINE_CONFIGURATION", panelConfiguration)
        )
        .forEach(entry -> {
            tabsBottom.addTab(I18n.valueByKey(entry.getKey()), entry.getValue());
            
            JLabel label = new JLabel(I18n.valueByKey(entry.getKey()));
            tabsBottom.setTabComponentAt(
                tabsBottom.indexOfTab(I18n.valueByKey(entry.getKey())),
                label
            );
            
            I18nView.addComponentForKey(entry.getKey(), label);
        });

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
                
                final Method[] refMethodError = new Method[]{methodError};
                JTextPaneLexer textPaneError = new JTextPaneLexer() {
                    
                    @Override
                    public void switchSetterToVendor() {
                        this.attributeSetter = new AttributeSetterForVendor(refMethodError[0], "setQuery");
                    }
                };
                
                SqlEngine.resetLexer(textPaneError);
                textPaneError.switchSetterToVendor();
                textPaneError.setText(methodError.getQuery().trim());
                textPaneError.setBorder(SqlEngine.BORDER_RIGHT);
    
                panelError.add(new LightScrollPane(1, 0, 1, 0, textPaneError), BorderLayout.CENTER);
                
                JPanel panelLimit = new JPanel();
                panelLimit.setLayout(new BoxLayout(panelLimit, BoxLayout.LINE_AXIS));
                panelLimit.add(new JLabel(" Overflow limit: "));
                panelLimit.add(new JTextField(Integer.toString(methodError.getCapacity())));
                
                panelError.add(panelLimit, BorderLayout.SOUTH);
    
                SqlEngine.TAB_ERROR.addTab(methodError.getName(), panelError);
                
                SqlEngine.TAB_ERROR.setTitleAt(SqlEngine.TAB_ERROR.getTabCount() - 1, "<html><div style=\"text-align:left;width:150px;\">"+ methodError.getName() +"</div></html>");
                
                this.textPanesError.add(textPaneError);
            }
        }
    }
    
    private void showSql(ModelYaml modelYaml) {
        
        this.getTextPanes().forEach(textPaneLexer -> textPaneLexer.setText(""));
        
        Stream.of(
            new SimpleEntry<>(this.textareaDatabase, modelYaml.getResource().getSchema().getDatabase()),
            new SimpleEntry<>(this.textareaTable, modelYaml.getResource().getSchema().getTable()),
            new SimpleEntry<>(this.textareaColumn, modelYaml.getResource().getSchema().getColumn()),
            new SimpleEntry<>(this.textareaQuery, modelYaml.getResource().getSchema().getRow().getQuery()),
            new SimpleEntry<>(this.textareaField, modelYaml.getResource().getSchema().getRow().getFields().getField()),
            new SimpleEntry<>(this.textareaConcat, modelYaml.getResource().getSchema().getRow().getFields().getConcat()),
            
            new SimpleEntry<>(this.textareaDatabaseZipped, modelYaml.getResource().getZipped().getDatabase()),
            new SimpleEntry<>(this.textareaTableZipped, modelYaml.getResource().getZipped().getTable()),
            new SimpleEntry<>(this.textareaColumnZipped, modelYaml.getResource().getZipped().getColumn()),
            new SimpleEntry<>(this.textareaQueryZipped, modelYaml.getResource().getZipped().getRow().getQuery()),
            new SimpleEntry<>(this.textareaFieldZipped, modelYaml.getResource().getZipped().getRow().getFields().getField()),
            new SimpleEntry<>(this.textareaConcatZipped, modelYaml.getResource().getZipped().getRow().getFields().getConcat()),
            
            new SimpleEntry<>(this.textareaDatabaseDios, modelYaml.getResource().getDios().getDatabase()),
            new SimpleEntry<>(this.textareaTableDios, modelYaml.getResource().getDios().getTable()),
            new SimpleEntry<>(this.textareaColumnDios, modelYaml.getResource().getDios().getColumn()),
            new SimpleEntry<>(this.textareaQueryDios, modelYaml.getResource().getDios().getRow().getQuery()),
            new SimpleEntry<>(this.textareaFieldDios, modelYaml.getResource().getDios().getRow().getFields().getField()),
            new SimpleEntry<>(this.textareaConcatDios, modelYaml.getResource().getDios().getRow().getFields().getConcat()),
            
            new SimpleEntry<>(this.textareaInfo, modelYaml.getResource().getInfo()),
            
            new SimpleEntry<>(this.textareaSlidingWindow, modelYaml.getStrategy().getConfiguration().getSlidingWindow()),
            new SimpleEntry<>(this.textareaLimit, modelYaml.getStrategy().getConfiguration().getLimit()),
            new SimpleEntry<>(this.textareaFailsafe, modelYaml.getStrategy().getConfiguration().getFailsafe()),
            new SimpleEntry<>(this.textareaEndingComment, modelYaml.getStrategy().getConfiguration().getEndingComment()),
            new SimpleEntry<>(this.textareaCalibrator, modelYaml.getStrategy().getConfiguration().getCalibrator()),
            
            new SimpleEntry<>(this.textareaIndices, modelYaml.getStrategy().getNormal().getIndices()),
            new SimpleEntry<>(this.textareaCapacity, modelYaml.getStrategy().getNormal().getCapacity()),
            new SimpleEntry<>(this.textareaOrderBy, modelYaml.getStrategy().getNormal().getOrderBy()),
            
            new SimpleEntry<>(this.textareaModeAnd, modelYaml.getStrategy().getBoolean().getModeAnd()),
            new SimpleEntry<>(this.textareaModeOr, modelYaml.getStrategy().getBoolean().getModeOr()),
            new SimpleEntry<>(this.textareaBlind, modelYaml.getStrategy().getBoolean().getBlind()),
            new SimpleEntry<>(this.textareaTime, modelYaml.getStrategy().getBoolean().getTime()),
            new SimpleEntry<>(this.textareaBitTest, modelYaml.getStrategy().getBoolean().getTest().getBit()),
            new SimpleEntry<>(this.textareaLengthTest, modelYaml.getStrategy().getBoolean().getTest().getLength())
        )
        .forEach(entry -> entry.getKey().setText(entry.getValue().trim()));

        this.initErrorTabs();
    }
    
    private static void resetLexer(JTextPaneLexer textPane) {
        
        clean(textPane);
        
        HighlightedDocument document = new HighlightedDocument(HighlightedDocument.SQL_STYLE);
        document.setHighlightStyle(HighlightedDocument.SQL_STYLE);
        textPane.setStyledDocument(document);
        
        document.addDocumentListener(new DocumentListenerTyping() {
            
            @Override
            public void process() {
                
                textPane.setAttribute();
            }
        });
    }
    
    private static void clean(JTextPaneLexer textPane) {
        
        if (textPane.getStyledDocument() instanceof HighlightedDocument) {
            HighlightedDocument oldDocument = (HighlightedDocument) textPane.getStyledDocument();
            oldDocument.stopColorer();
        }
    }
    
    private void changeVendor() {
        
        this.getTextPanes().forEach(SqlEngine::resetLexer);
        this.getTextPanes().forEach(JTextPaneObjectMethod::switchSetterToVendor);
        SqlEngine.this.showSql(this.modelYaml);
    }

    @Override
    public void clean() {
        
        this.getTextPanes().forEach(SqlEngine::clean);
    }
    
    private List<JTextPaneLexer> getTextPanes() {
        return Stream.concat(
            this.textPanesError.stream(),
            Stream.of(
                this.textareaDatabase,
                this.textareaTable,
                this.textareaColumn,
                this.textareaQuery,
                this.textareaField,
                this.textareaConcat,
                this.textareaInfo,
                
                this.textareaDatabaseZipped,
                this.textareaTableZipped,
                this.textareaColumnZipped,
                this.textareaQueryZipped,
                this.textareaFieldZipped,
                this.textareaConcatZipped,
                
                this.textareaDatabaseDios,
                this.textareaTableDios,
                this.textareaColumnDios,
                this.textareaQueryDios,
                this.textareaFieldDios,
                this.textareaConcatDios,
                
                this.textareaModeAnd,
                this.textareaModeOr,
                this.textareaBlind,
                this.textareaTime,
                this.textareaBitTest,
                this.textareaLengthTest,
                
                this.textareaSlidingWindow,
                this.textareaLimit,
                this.textareaFailsafe,
                this.textareaCalibrator,
                this.textareaCapacity,
                this.textareaOrderBy,
                this.textareaEndingComment,
                this.textareaIndices
            )
        )
        .collect(Collectors.toList());
    }

}