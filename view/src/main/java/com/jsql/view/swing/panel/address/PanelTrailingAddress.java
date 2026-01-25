package com.jsql.view.swing.panel.address;

import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.StrategyError;
import com.jsql.model.injection.engine.model.Engine;
import com.jsql.model.injection.engine.model.yaml.Method;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

public class PanelTrailingAddress extends JPanel {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static final String MENU_STRATEGY = "menuStrategy";
    public static final String ITEM_RADIO_STRATEGY_ERROR = "itemRadioStrategyError";
    public static final String MENU_VENDOR = "menuVendor";
    public static final String ITEM_RADIO_VENDOR = "itemRadioVendor";

    private JMenu itemRadioStrategyError;

    private final JLabel labelEngine = new JLabel(UiUtil.ARROW_DOWN.getIcon(), SwingConstants.LEFT);
    private final JLabel labelStrategy = new JLabel(UiUtil.ARROW_DOWN.getIcon(), SwingConstants.LEFT);
    private final JPopupMenu popupMenuEngines = new JPopupMenu();
    private final JPopupMenu popupMenuStrategies = new JPopupMenu();

    private final ButtonGroup groupStrategy = new ButtonGroup();
    public static final String PREFIX_NAME_ERROR = "itemRadioError";
    private static final String I18N_TOOLTIP_STRATEGY = "STRATEGY_%s_TOOLTIP";

    /**
     * Loader displayed during injection.
     */
    private final JProgressBar loader;

    /**
     * Connection button.
     */
    public final ButtonStart buttonStart = new ButtonStart();
    
    public PanelTrailingAddress(PanelAddressBar panelAddressBar) {
        this.buttonStart.addActionListener(new ActionStart(panelAddressBar));
        this.setOpaque(false);
        this.setBorder(null);
        this.labelStrategy.setText("Strategy auto");
        this.labelStrategy.setName(PanelTrailingAddress.MENU_STRATEGY);

        for (final AbstractStrategy strategy: MediatorHelper.model().getMediatorStrategy().getStrategies()) {
            var nameStrategy = strategy.getName().toUpperCase(Locale.ROOT);
            JMenuItem itemRadioStrategy;
            if (strategy == MediatorHelper.model().getMediatorStrategy().getError()) {
                itemRadioStrategy = new JMenu(strategy.toString());
                this.itemRadioStrategyError = (JMenu) itemRadioStrategy;
                itemRadioStrategy.getComponent().setName(PanelTrailingAddress.ITEM_RADIO_STRATEGY_ERROR);
            } else {
                var atomicTooltip = new AtomicReference<>(new JToolTipI18n(
                    I18nUtil.valueByKey(String.format(PanelTrailingAddress.I18N_TOOLTIP_STRATEGY, nameStrategy))
                ));
                itemRadioStrategy = new JRadioButtonMenuItem(strategy.toString()) {
                    @Override
                    public JToolTip createToolTip() {
                        atomicTooltip.set(new JToolTipI18n(
                            I18nUtil.valueByKey(
                                String.format(PanelTrailingAddress.I18N_TOOLTIP_STRATEGY, nameStrategy)
                            )
                        ));
                        return atomicTooltip.get();
                    }
                };
                I18nViewUtil.addComponentForKey(
                    String.format(PanelTrailingAddress.I18N_TOOLTIP_STRATEGY, nameStrategy),
                    atomicTooltip.get()
                );
                itemRadioStrategy.getComponent().setName("itemRadioStrategy" + strategy);
                itemRadioStrategy.addActionListener(actionEvent -> {
                    this.labelStrategy.setText(strategy.toString());
                    MediatorHelper.model().getMediatorStrategy().setStrategy(strategy);
                });
                this.groupStrategy.add(itemRadioStrategy);
            }

            this.popupMenuStrategies.add(itemRadioStrategy);
            itemRadioStrategy.setToolTipText(
                I18nUtil.valueByKey(String.format(PanelTrailingAddress.I18N_TOOLTIP_STRATEGY, nameStrategy))
            );
            itemRadioStrategy.setEnabled(false);
        }

        this.labelEngine.setText(MediatorHelper.model().getMediatorEngine().getAuto().toString());
        this.labelEngine.setName(PanelTrailingAddress.MENU_VENDOR);
        this.popupMenuEngines.setLayout(UiUtil.getColumnLayout(MediatorHelper.model().getMediatorEngine().getEngines().size()));
        var groupEngine = new ButtonGroup();
        for (final Engine engine : MediatorHelper.model().getMediatorEngine().getEngines()) {
            JMenuItem itemRadioEngine = new JRadioButtonMenuItem(engine.toString(), engine == MediatorHelper.model().getMediatorEngine().getAuto());
            itemRadioEngine.setName(PanelTrailingAddress.ITEM_RADIO_VENDOR + engine);
            itemRadioEngine.addActionListener(actionEvent -> {
                this.labelEngine.setText(engine.toString());
                MediatorHelper.model().getMediatorEngine().setEngineByUser(engine);
            });
            this.popupMenuEngines.add(itemRadioEngine);
            groupEngine.add(itemRadioEngine);
        }

        this.loader = new JProgressBar();
        var dimension = UIManager.getDimension("ProgressBar.horizontalSize");
        this.loader.setPreferredSize(new Dimension(32, dimension.height));
        this.loader.setIndeterminate(true);
        this.add(this.loader);

        this.labelEngine.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(PanelTrailingAddress.this.popupMenuEngines.getComponents())
                    .map(JComponent.class::cast)
                    .forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                PanelTrailingAddress.this.popupMenuEngines.updateUI();  // required: incorrect when dark/light mode switch
                SwingUtilities.invokeLater(() -> {  // reduce flickering on linux
                    PanelTrailingAddress.this.popupMenuEngines.show(e.getComponent(), e.getComponent().getX(),5 + e.getComponent().getY() + e.getComponent().getHeight());
                    PanelTrailingAddress.this.popupMenuEngines.setLocation(e.getComponent().getLocationOnScreen().x,5 + e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight());
                });
            }
        });
        this.labelStrategy.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(PanelTrailingAddress.this.popupMenuStrategies.getComponents()).map(a -> (JComponent) a).forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                for (var i = 0 ; i < PanelTrailingAddress.this.getMenuError().getItemCount() ; i++) {
                    PanelTrailingAddress.this.getMenuError().getItem(i).updateUI();  // required: incorrect when dark/light mode switch
                }
                PanelTrailingAddress.this.popupMenuStrategies.updateUI();  // required: incorrect when dark/light mode switch
                SwingUtilities.invokeLater(() -> {  // reduce flickering on linux
                    PanelTrailingAddress.this.popupMenuStrategies.show(e.getComponent(), e.getComponent().getX(),5 + e.getComponent().getY() + e.getComponent().getHeight());
                    PanelTrailingAddress.this.popupMenuStrategies.setLocation(e.getComponent().getLocationOnScreen().x,5 + e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight());
                });
            }
        });

        this.add(this.labelEngine);
        this.add(this.labelStrategy);
        this.add(this.buttonStart);
        this.setCursor(Cursor.getDefaultCursor());
        this.loader.setVisible(false);
    }

    public void endPreparation() {
        this.buttonStart.setToolTipText(I18nUtil.valueByKey("BUTTON_START_TOOLTIP"));
        this.buttonStart.setInjectionReady();
        this.loader.setVisible(false);
    }
    
    public void reset() {
        this.labelStrategy.setText("Strategy auto");
        if (MediatorHelper.model().getMediatorEngine().getEngineByUser() == MediatorHelper.model().getMediatorEngine().getAuto()) {
            this.labelEngine.setText(MediatorHelper.model().getMediatorEngine().getAuto().toString());
        }
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .forEach(component -> component.setEnabled(false));
        this.getMenuError().removeAll();

        this.groupStrategy.clearSelection();
        Iterable<AbstractButton> iterable = () -> this.groupStrategy.getElements().asIterator();
        StreamSupport.stream(iterable.spliterator(), false)
            .filter(abstractButton -> abstractButton.getName().startsWith(PanelTrailingAddress.PREFIX_NAME_ERROR))
            .forEach(this.groupStrategy::remove);
    }
    
    public void setEngine(Engine engine) {
        this.labelEngine.setText(engine.toString());
        this.itemRadioStrategyError.removeAll();
        var indexError = 0;
        if (
            engine != MediatorHelper.model().getMediatorEngine().getAuto()
            && engine.instance().getModelYaml().getStrategy().getError() != null
        ) {
            for (Method methodError: engine.instance().getModelYaml().getStrategy().getError().getMethod()) {
                JMenuItem itemRadioError = new JRadioButtonMenuItem(methodError.getName());
                itemRadioError.setEnabled(false);
                itemRadioError.setName(PanelTrailingAddress.PREFIX_NAME_ERROR + methodError.getName());
                this.itemRadioStrategyError.add(itemRadioError);
                this.groupStrategy.add(itemRadioError);
                int indexErrorFinal = indexError;
                itemRadioError.addActionListener(actionEvent -> {
                    this.labelStrategy.setText(methodError.getName());
                    MediatorHelper.model().getMediatorStrategy().setStrategy(MediatorHelper.model().getMediatorStrategy().getError());
                    MediatorHelper.model().getMediatorStrategy().getError().setIndexErrorStrategy(indexErrorFinal);
                });
                indexError++;
            }
        }
    }
    
    public void markStrategy(AbstractStrategy strategy) {
        this.labelStrategy.setText(strategy.toString());
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(JMenuItem.class::cast)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .forEach(jMenuItem -> jMenuItem.setSelected(true));
    }
    
    public void markStrategyInvulnerable(AbstractStrategy strategy) {
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(JMenuItem.class::cast)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .forEach(jMenuItem -> jMenuItem.setEnabled(false));
    }
    
    public void markErrorInvulnerable(int indexMethodError) {
        AbstractStrategy strategy = MediatorHelper.model().getMediatorStrategy().getError();
        Arrays.stream(this.popupMenuStrategies.getSubElements())
            .map(JMenuItem.class::cast)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .map(JMenu.class::cast)
            .filter(jMenuItem -> {
                var isNotNull = true;
                // Fix #36975: ArrayIndexOutOfBoundsException on getItem()
                // Fix #40352: NullPointerException on ?
                // Fix #95855: NPE on setEnabled()
                try {
                    isNotNull = jMenuItem.getItem(indexMethodError) != null;
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
                    return false;
                }
                return isNotNull;
            })
            .forEach(jMenuItem -> jMenuItem.getItem(indexMethodError).setEnabled(false));
    }
    
    public void markError() {
        StrategyError strategy = MediatorHelper.model().getMediatorStrategy().getError();
        this.labelStrategy.setText(strategy.toString());
        int indexError = strategy.getIndexErrorStrategy();
        String nameError = MediatorHelper.model().getMediatorEngine().getEngine().instance().getModelYaml().getStrategy().getError().getMethod().get(indexError).getName();

        Arrays.stream(this.getMenuError().getMenuComponents())
            .map(JRadioButtonMenuItem.class::cast)
            .filter(component -> component.getText().equals(nameError))
            .forEach(jRadioButtonMenuItem -> {
                jRadioButtonMenuItem.setSelected(true);
                this.labelStrategy.setText(nameError);
            });
    }

    private JMenu getMenuError() {
        var nameError = MediatorHelper.model().getMediatorStrategy().getError().getName();
        return (JMenu) Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(JMenuItem.class::cast)
            .filter(jMenuItem -> jMenuItem.getText().equalsIgnoreCase(nameError))
            .findFirst()
            .orElse(new JMenuItem("Mock"));
    }

    public void markErrorVulnerable(int indexMethodError) {
        AbstractStrategy strategy = MediatorHelper.model().getMediatorStrategy().getError();
        // Fix #46578: ArrayIndexOutOfBoundsException on getItem()
        try {
            Arrays.stream(this.popupMenuStrategies.getComponents())
                .map(JMenuItem.class::cast)
                .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
                .map(JMenu.class::cast)
                .filter(jMenuItem -> jMenuItem.getItem(indexMethodError) != null)
                .forEach(jMenuItem -> {
                    jMenuItem.setEnabled(true);
                    jMenuItem.getItem(indexMethodError).setEnabled(true);
                });
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }
    
    public void markStrategyVulnerable(AbstractStrategy strategy) {
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(JMenuItem.class::cast)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .forEach(jMenuItem -> jMenuItem.setEnabled(true));
    }
    
    
    // Getter and setter

    public JComponent getLoader() {
        return this.loader;
    }

    public ButtonStart getButtonStart() {
        return this.buttonStart;
    }
}
