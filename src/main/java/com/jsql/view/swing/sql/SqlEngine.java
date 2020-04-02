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

import com.jsql.i18n.I18nUtil;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;
import com.jsql.view.i18n.I18nViewUtil;
import com.jsql.view.swing.UiUtil;
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

    private static final Border BORDER_RIGHT = BorderFactory.createMatteBorder(0, 0, 0, 1, UiUtil.COLOR_COMPONENT_BORDER);
    
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
        
        this.initializeTextComponents();
        
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
        
        JPanel panelStructure = this.getPanelStructure();
        JPanel panelStrategy = this.getPanelStrategy();
        JPanel panelConfiguration = this.getPanelConfiguration();

        JTabbedPane tabsBottom = new JTabbedPane(SwingConstants.BOTTOM);
        Stream.of(
            new SimpleEntry<>("SQLENGINE_STRUCTURE", panelStructure),
            new SimpleEntry<>("SQLENGINE_STRATEGY", panelStrategy),
            new SimpleEntry<>("SQLENGINE_CONFIGURATION", panelConfiguration)
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
        
        MediatorGui.menubar().switchLocale(I18nUtil.getLocaleDefault());
    }

    private JPanel getPanelStructure() {
        
        JTabbedPane tabsStandard = new JTabbedPane(SwingConstants.RIGHT);
        
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
                I18nUtil.valueByKey(entry.getKey()),
                new LightScrollPane(1, 0, 1, 0, entry.getValue())
            );
            
            JLabel label = new JLabel(I18nUtil.valueByKey(entry.getKey()));
            tabsZipped.setTabComponentAt(
                tabsZipped.indexOfTab(I18nUtil.valueByKey(entry.getKey())),
                label
            );
            I18nViewUtil.addComponentForKey(entry.getKey(), label);
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
        
        Stream.of(
            new SimpleEntry<>("SQLENGINE_STANDARD", tabsSchema),
            new SimpleEntry<>("SQLENGINE_ZIPPED", tabsZipped),
            new SimpleEntry<>("SQLENGINE_DIOS", tabsDios)
        ).forEach(entry -> {
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
        
        JTabbedPane tabsStrategy = new JTabbedPane();
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_NORMAL"), new LightScrollPane(1, 0, 1, 0, this.textareaIndices));
        
        JPanel panelStrategy = new JPanel(new BorderLayout());
        panelStrategy.add(tabsStrategy, BorderLayout.CENTER);
        panelStrategy.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UiUtil.COLOR_COMPONENT_BORDER));
        
        
        /*Error*/
        JPanel panelError = new JPanel(new BorderLayout());
        panelError.add(SqlEngine.TAB_ERROR, BorderLayout.CENTER);
        
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_ERROR"), panelError);

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
        
        tabsStrategy.addTab(I18nUtil.valueByKey("SQLENGINE_BOOLEAN"), panelBoolean);
        
        /* Strategy */
        Stream.of(
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
        
        JTabbedPane tabsConfiguration = new JTabbedPane();
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_ORDER_BY"), new LightScrollPane(1, 0, 1, 0, this.textareaOrderBy));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CHARACTERS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, this.textareaSlidingWindow));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_ROWS_SLIDINGWINDOW"), new LightScrollPane(1, 0, 1, 0, this.textareaLimit));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CAPACITY"), new LightScrollPane(1, 0, 1, 0, this.textareaCapacity));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_CALIBRATOR"), new LightScrollPane(1, 0, 1, 0, this.textareaCalibrator));
        tabsConfiguration.addTab(I18nUtil.valueByKey("SQLENGINE_TRAPCANCELLER"), new LightScrollPane(1, 0, 1, 0, this.textareaFailsafe));
        tabsConfiguration.addTab("End comment", new LightScrollPane(1, 0, 1, 0, this.textareaEndingComment));
        
        Stream.of(
            "SQLENGINE_ORDER_BY",
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
        
        JMenu comboMenuVendor = new ComboMenu(MediatorModel.model().getMediatorVendor().getVendor().toString());
        menuBarVendor.add(comboMenuVendor);

        ButtonGroup groupVendor = new ButtonGroup();

        List<Vendor> listVendors = new LinkedList<>(MediatorModel.model().getMediatorVendor().getVendors());
        listVendors.removeIf(i -> i == MediatorModel.model().getMediatorVendor().getAuto());
        
        for (final Vendor vendor: listVendors) {
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == MediatorModel.model().getMediatorVendor().getVendor());
            
            itemRadioVendor.addActionListener(actionEvent -> {
                this.modelYaml = vendor.instance().getModelYaml();
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
        this.getTextPanes().forEach(textPaneLexer -> textPaneLexer.setText(""));
        
        Stream.of(
            new SimpleEntry<>(this.textareaDatabase, this.modelYaml.getResource().getSchema().getDatabase()),
            new SimpleEntry<>(this.textareaTable, this.modelYaml.getResource().getSchema().getTable()),
            new SimpleEntry<>(this.textareaColumn, this.modelYaml.getResource().getSchema().getColumn()),
            new SimpleEntry<>(this.textareaQuery, this.modelYaml.getResource().getSchema().getRow().getQuery()),
            new SimpleEntry<>(this.textareaField, this.modelYaml.getResource().getSchema().getRow().getFields().getField()),
            new SimpleEntry<>(this.textareaConcat, this.modelYaml.getResource().getSchema().getRow().getFields().getConcat()),
            
            new SimpleEntry<>(this.textareaDatabaseZipped, this.modelYaml.getResource().getZipped().getDatabase()),
            new SimpleEntry<>(this.textareaTableZipped, this.modelYaml.getResource().getZipped().getTable()),
            new SimpleEntry<>(this.textareaColumnZipped, this.modelYaml.getResource().getZipped().getColumn()),
            new SimpleEntry<>(this.textareaQueryZipped, this.modelYaml.getResource().getZipped().getRow().getQuery()),
            new SimpleEntry<>(this.textareaFieldZipped, this.modelYaml.getResource().getZipped().getRow().getFields().getField()),
            new SimpleEntry<>(this.textareaConcatZipped, this.modelYaml.getResource().getZipped().getRow().getFields().getConcat()),
            
            new SimpleEntry<>(this.textareaDatabaseDios, this.modelYaml.getResource().getDios().getDatabase()),
            new SimpleEntry<>(this.textareaTableDios, this.modelYaml.getResource().getDios().getTable()),
            new SimpleEntry<>(this.textareaColumnDios, this.modelYaml.getResource().getDios().getColumn()),
            new SimpleEntry<>(this.textareaQueryDios, this.modelYaml.getResource().getDios().getRow().getQuery()),
            new SimpleEntry<>(this.textareaFieldDios, this.modelYaml.getResource().getDios().getRow().getFields().getField()),
            new SimpleEntry<>(this.textareaConcatDios, this.modelYaml.getResource().getDios().getRow().getFields().getConcat()),
            
            new SimpleEntry<>(this.textareaInfo, this.modelYaml.getResource().getInfo()),
            
            new SimpleEntry<>(this.textareaSlidingWindow, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow()),
            new SimpleEntry<>(this.textareaLimit, this.modelYaml.getStrategy().getConfiguration().getLimit()),
            new SimpleEntry<>(this.textareaFailsafe, this.modelYaml.getStrategy().getConfiguration().getFailsafe()),
            new SimpleEntry<>(this.textareaEndingComment, this.modelYaml.getStrategy().getConfiguration().getEndingComment()),
            new SimpleEntry<>(this.textareaCalibrator, this.modelYaml.getStrategy().getConfiguration().getCalibrator()),
            
            new SimpleEntry<>(this.textareaIndices, this.modelYaml.getStrategy().getNormal().getIndices()),
            new SimpleEntry<>(this.textareaCapacity, this.modelYaml.getStrategy().getNormal().getCapacity()),
            new SimpleEntry<>(this.textareaOrderBy, this.modelYaml.getStrategy().getNormal().getOrderBy()),
            
            new SimpleEntry<>(this.textareaModeAnd, this.modelYaml.getStrategy().getBoolean().getModeAnd()),
            new SimpleEntry<>(this.textareaModeOr, this.modelYaml.getStrategy().getBoolean().getModeOr()),
            new SimpleEntry<>(this.textareaBlind, this.modelYaml.getStrategy().getBoolean().getBlind()),
            new SimpleEntry<>(this.textareaTime, this.modelYaml.getStrategy().getBoolean().getTime()),
            new SimpleEntry<>(this.textareaBitTest, this.modelYaml.getStrategy().getBoolean().getTest().getBit()),
            new SimpleEntry<>(this.textareaLengthTest, this.modelYaml.getStrategy().getBoolean().getTest().getLength())
        )
        .forEach(entry -> entry.getKey().setText(entry.getValue().trim()));

        this.populateTabError();
    }
    
    /**
     * Dynamically add textPanes to Error tab for current vendor.
     */
    private void populateTabError() {
        
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

    /**
     * End coloring threads.
     * Used when Sql Engine is closed by ctrl+W or using tab header close icon and middle click.
     */
    @Override
    public void clean() {
        
        this.getTextPanes().forEach(SqlEngine::stopDocumentColorer);
    }
    
    /**
     * Reset the textPane colorer.
     * @param textPane which colorer will be reset.
     */
    private static void resetLexer(JTextPaneLexer textPane) {
        
        stopDocumentColorer(textPane);
        
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
    
    /**
     * End the thread doing coloring.
     * @param textPane which coloring has to stop.
     */
    private static void stopDocumentColorer(JTextPaneLexer textPane) {
        
        if (textPane.getStyledDocument() instanceof HighlightedDocument) {
            
            HighlightedDocument oldDocument = (HighlightedDocument) textPane.getStyledDocument();
            oldDocument.stopColorer();
        }
    }
    
    /**
     * Merge list of Error textPanes and list of other textAreas.
     * @return the merged list
     */
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