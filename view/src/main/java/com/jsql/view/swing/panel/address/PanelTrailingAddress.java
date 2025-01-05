package com.jsql.view.swing.panel.address;

import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.StrategyInjectionError;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.util.I18nUtil;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

public class PanelTrailingAddress extends JPanel {
    
    private JMenu itemRadioStrategyError;

    private final JLabel labelVendor = new JLabel(UiUtil.ARROW_DOWN.icon, SwingConstants.LEFT);
    private final JLabel labelStrategy = new JLabel(UiUtil.ARROW_DOWN.icon, SwingConstants.LEFT);
    private final JPopupMenu popupMenuVendors = new JPopupMenu();
    private final JPopupMenu popupMenuStrategies = new JPopupMenu();

    private final ButtonGroup groupStrategy = new ButtonGroup();
    private static final String PREFIX_NAME_ERROR = "itemRadioError";
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
        this.labelStrategy.setName("menuStrategy");

        for (final AbstractStrategy strategy: MediatorHelper.model().getMediatorStrategy().getStrategies()) {
            var nameStrategy = strategy.getName().toUpperCase(Locale.ROOT);
            JMenuItem itemRadioStrategy;
            if (strategy == MediatorHelper.model().getMediatorStrategy().getError()) {
                itemRadioStrategy = new JMenu(strategy.toString());
                this.itemRadioStrategyError = (JMenu) itemRadioStrategy;
                itemRadioStrategy.getComponent().setName("itemRadioStrategyError");
            } else {
                var atomicTooltip = new AtomicReference<>(new JToolTipI18n(
                    I18nUtil.valueByKey(String.format(I18N_TOOLTIP_STRATEGY, nameStrategy))
                ));
                itemRadioStrategy = new JRadioButtonMenuItem(strategy.toString()) {
                    @Override
                    public JToolTip createToolTip() {
                        atomicTooltip.set(new JToolTipI18n(
                            I18nUtil.valueByKey(
                                String.format(I18N_TOOLTIP_STRATEGY, nameStrategy)
                            )
                        ));
                        return atomicTooltip.get();
                    }
                };
                I18nViewUtil.addComponentForKey(
                    String.format(I18N_TOOLTIP_STRATEGY, nameStrategy),
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
                I18nUtil.valueByKey(String.format(I18N_TOOLTIP_STRATEGY, nameStrategy))
            );
            ((JComponent) itemRadioStrategy).setEnabled(false);
        }

        this.labelVendor.setText(MediatorHelper.model().getMediatorVendor().getAuto().toString());
        this.labelVendor.setName("menuVendor");
        this.popupMenuVendors.setLayout(UiUtil.getColumnLayout(MediatorHelper.model().getMediatorVendor().getVendors().size()));
        var groupVendor = new ButtonGroup();
        for (final Vendor vendor: MediatorHelper.model().getMediatorVendor().getVendors()) {
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == MediatorHelper.model().getMediatorVendor().getAuto());
            itemRadioVendor.setName("itemRadioVendor"+ vendor);
            itemRadioVendor.addActionListener(actionEvent -> {
                this.labelVendor.setText(vendor.toString());
                MediatorHelper.model().getMediatorVendor().setVendorByUser(vendor);
            });
            popupMenuVendors.add(itemRadioVendor);
            groupVendor.add(itemRadioVendor);
        }

        this.loader = new JProgressBar();
        var dimension = UIManager.getDimension("ProgressBar.horizontalSize");
        this.loader.setPreferredSize(new Dimension(32, dimension.height));
        this.loader.setIndeterminate(true);
        this.add(this.loader);

        labelVendor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(popupMenuVendors.getComponents())
                    .map(component -> (JComponent) component)
                    .forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                popupMenuVendors.updateUI();  // required: incorrect when dark/light mode switch
                popupMenuVendors.show(e.getComponent(), e.getComponent().getX(),5 + e.getComponent().getY() + e.getComponent().getHeight());
                popupMenuVendors.setLocation(e.getComponent().getLocationOnScreen().x,5 + e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight());
            }
        });
        labelStrategy.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(popupMenuStrategies.getComponents()).map(a -> (JComponent) a).forEach(JComponent::updateUI);  // required: incorrect when dark/light mode switch
                for (var i = 0 ; i < getMenuError().getItemCount() ; i++) {
                    getMenuError().getItem(i).updateUI();  // required: incorrect when dark/light mode switch
                }
                popupMenuStrategies.updateUI();  // required: incorrect when dark/light mode switch
                popupMenuStrategies.show(e.getComponent(), e.getComponent().getX(),5 + e.getComponent().getY() + e.getComponent().getHeight());
                popupMenuStrategies.setLocation(e.getComponent().getLocationOnScreen().x,5 + e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight());
            }
        });

        this.add(this.labelVendor);
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
        if (MediatorHelper.model().getMediatorVendor().getVendorByUser() == MediatorHelper.model().getMediatorVendor().getAuto()) {
            this.labelVendor.setText(MediatorHelper.model().getMediatorVendor().getAuto().toString());
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
    
    public void setVendor(Vendor vendor) {
        this.labelVendor.setText(vendor.toString());
        this.itemRadioStrategyError.removeAll();
        var indexError = 0;
        if (
            vendor != MediatorHelper.model().getMediatorVendor().getAuto()
            && vendor.instance().getModelYaml().getStrategy().getError() != null
        ) {
            for (Method methodError: vendor.instance().getModelYaml().getStrategy().getError().getMethod()) {
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
            .map(component -> (JMenuItem) component)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .forEach(jMenuItem -> jMenuItem.setSelected(true));
    }
    
    public void markStrategyInvulnerable(AbstractStrategy strategy) {
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(component -> (JMenuItem) component)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .forEach(jMenuItem -> jMenuItem.setEnabled(false));
    }
    
    public void markErrorInvulnerable(int indexMethodError) {
        AbstractStrategy strategy = MediatorHelper.model().getMediatorStrategy().getError();
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(component -> (JMenuItem) component)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .map(component -> (JMenu) component)
            .forEach(jMenuItem -> jMenuItem.getItem(indexMethodError).setEnabled(false));
    }
    
    public void markError() {
        StrategyInjectionError strategy = MediatorHelper.model().getMediatorStrategy().getError();
        this.labelStrategy.setText(strategy.toString());
        int indexError = strategy.getIndexErrorStrategy();
        String nameError = MediatorHelper.model().getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getError().getMethod().get(indexError).getName();

        Arrays.stream(this.getMenuError().getMenuComponents())
            .map(component -> (JRadioButtonMenuItem) component)
            .filter(component -> component.getText().equals(nameError))
            .forEach(jRadioButtonMenuItem -> {
                jRadioButtonMenuItem.setSelected(true);
                this.labelStrategy.setText(nameError);
            });
    }

    private JMenu getMenuError() {
        var nameError = MediatorHelper.model().getMediatorStrategy().getError().getName();
        return (JMenu) Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(component -> (JMenuItem) component)
            .filter(jMenuItem -> jMenuItem.getText().equalsIgnoreCase(nameError))
            .findFirst()
            .orElse(new JMenuItem("Mock"));
    }
    
    public void markErrorVulnerable(int indexMethodError) {
        AbstractStrategy strategy = MediatorHelper.model().getMediatorStrategy().getError();
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(component -> (JMenuItem) component)
            .filter(jMenuItem -> jMenuItem.getText().equals(strategy.toString()))
            .map(component -> (JMenu) component)
            .forEach(jMenuItem -> {
                jMenuItem.setEnabled(true);
                // Fix #46578: ArrayIndexOutOfBoundsException on getItem()
                if (0 <= indexMethodError && indexMethodError < jMenuItem.getItemCount()) {
                    jMenuItem.getItem(indexMethodError).setEnabled(true);
                }
            });
    }
    
    public void markStrategyVulnerable(AbstractStrategy strategy) {
        Arrays.stream(this.popupMenuStrategies.getComponents())
            .map(component -> (JMenuItem) component)
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
