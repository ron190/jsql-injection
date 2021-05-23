package com.jsql.view.swing.panel.address;

import java.awt.Cursor;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolTip;
import javax.swing.MenuElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.StrategyInjectionError;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.swing.manager.util.ComboMenu;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.util.ButtonAddressBar;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.ui.ComponentBorder;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

@SuppressWarnings("serial")
public class AddressMenuBar extends JMenuBar {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private JMenu itemRadioStrategyError = new JMenu();

    private JMenu menuVendor;
    private JMenu menuStrategy;
    private ButtonGroup groupStrategy = new ButtonGroup();

    /**
     * Animated GIF displayed during injection.
     */
    private JLabel loader = new JLabel(UiUtil.ICON_LOADER_GIF);

    /**
     * Connection button.
     */
    private ButtonAddressBar buttonInUrl = new ButtonAddressBar();
    
    public AddressMenuBar(PanelAddressBar panelAddressBar) {
        
        this.buttonInUrl.setName("buttonInUrl");
        this.buttonInUrl.setToolTipText(I18nUtil.valueByKey("BUTTON_START_TOOLTIP"));
        this.buttonInUrl.addActionListener(new ActionStart(panelAddressBar));

        var buttonInTextfield = new ComponentBorder(this.buttonInUrl, 17, 0);
        buttonInTextfield.install(panelAddressBar.getTextFieldAddress());
        
        this.setOpaque(false);
        this.setBorder(null);

        this.menuStrategy = new ComboMenu("Strategy auto");
        this.menuStrategy.setName("menuStrategy");
        
        final var patternKeyTooltipStrategy = "STRATEGY_%s_TOOLTIP";

        for (final AbstractStrategy strategy: MediatorHelper.model().getMediatorStrategy().getStrategies()) {
            
            MenuElement itemRadioStrategy;

            if (strategy == MediatorHelper.model().getMediatorStrategy().getError()) {
                
                itemRadioStrategy = new JMenu(strategy.toString());
                this.itemRadioStrategyError = (JMenu) itemRadioStrategy;
                itemRadioStrategy.getComponent().setName("itemRadioStrategyError");
                
            } else {
                
                final var refTooltip = new JToolTipI18n[] {
                        
                    new JToolTipI18n(
                        I18nUtil.valueByKey(
                            String.format(
                                patternKeyTooltipStrategy,
                                strategy.getName().toUpperCase(Locale.ROOT)
                            )
                        )
                    )
                };
                
                itemRadioStrategy = new JRadioButtonMenuItem(strategy.toString()) {
                    
                    @Override
                    public JToolTip createToolTip() {
                        
                        JToolTip tipI18n = new JToolTipI18n(
                            I18nUtil.valueByKey(
                                String.format(
                                    patternKeyTooltipStrategy,
                                    strategy.getName().toUpperCase(Locale.ROOT)
                                )
                            )
                        );
                        
                        refTooltip[0] = (JToolTipI18n) tipI18n;
                        
                        return tipI18n;
                    }
                };
                
                itemRadioStrategy.getComponent().setName("itemRadioStrategy"+ strategy.toString());
                
                ((AbstractButton) itemRadioStrategy).addActionListener(actionEvent -> {
                    
                    this.menuStrategy.setText(strategy.toString());
                    MediatorHelper.model().getMediatorStrategy().setStrategy(strategy);
                });
                
                this.groupStrategy.add((AbstractButton) itemRadioStrategy);
            }

            this.menuStrategy.add((JMenuItem) itemRadioStrategy);
            
            ((JComponent) itemRadioStrategy).setToolTipText(
                I18nUtil.valueByKey(
                    String.format(
                        patternKeyTooltipStrategy,
                        strategy.getName().toUpperCase(Locale.ROOT)
                    )
                )
            );
            ((JComponent) itemRadioStrategy).setEnabled(false);
        }

        this.menuVendor = new ComboMenu(MediatorHelper.model().getMediatorVendor().getAuto().toString());
        this.menuVendor.setName("menuVendor");

        var groupVendor = new ButtonGroup();

        var indexVendor = 0;
        for (final Vendor vendor: MediatorHelper.model().getMediatorVendor().getVendors()) {
            
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == MediatorHelper.model().getMediatorVendor().getAuto());
            itemRadioVendor.setName("itemRadioVendor"+ vendor.toString());
            itemRadioVendor.addActionListener(actionEvent -> {
                
                this.menuVendor.setText(vendor.toString());
                MediatorHelper.model().getMediatorVendor().setVendorByUser(vendor);
            });
            
            this.menuVendor.add(itemRadioVendor);
            groupVendor.add(itemRadioVendor);
            
            if (indexVendor == 0) {
                this.menuVendor.add(new JSeparator());
            }
            
            indexVendor++;
        }

        this.add(Box.createHorizontalGlue());
        this.add(this.loader);
        this.add(Box.createHorizontalStrut(5));
        this.add(this.menuVendor);
        this.add(this.menuStrategy);

        this.loader.setVisible(false);
        this.loader.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    public void endPreparation() {
        
        this.buttonInUrl.setToolTipText(I18nUtil.valueByKey("BUTTON_START_TOOLTIP"));
        this.buttonInUrl.setInjectionReady();
        this.loader.setVisible(false);
    }
    
    public void initErrorMethods(Vendor vendor) {
        
        this.itemRadioStrategyError.removeAll();

        var indexError = 0;
        
        if (
            vendor != MediatorHelper.model().getMediatorVendor().getAuto()
            && vendor.instance().getModelYaml().getStrategy().getError() != null
        ) {
            
            for (Method methodError: vendor.instance().getModelYaml().getStrategy().getError().getMethod()) {
                
                JMenuItem itemRadioVendor = new JRadioButtonMenuItem(methodError.getName());
                itemRadioVendor.setEnabled(false);
                itemRadioVendor.setName("itemRadioVendor"+ methodError.getName());
                
                this.itemRadioStrategyError.add(itemRadioVendor);
                this.groupStrategy.add(itemRadioVendor);

                int indexErrorFinal = indexError;
                
                itemRadioVendor.addActionListener(actionEvent -> {
                    
                    this.menuStrategy.setText(methodError.getName());
                    
                    MediatorHelper.model().getMediatorStrategy().setStrategy(MediatorHelper.model().getMediatorStrategy().getError());
                    
                    MediatorHelper.model().getMediatorStrategy().getError().setIndexErrorStrategy(indexErrorFinal);
                });

                indexError++;
            }
        }
    }
    
    public void reset() {
        
        if (MediatorHelper.model().getMediatorVendor().getVendorByUser() == MediatorHelper.model().getMediatorVendor().getAuto()) {
            
            this.menuVendor.setText(MediatorHelper.model().getMediatorVendor().getAuto().toString());
        }
        
        this.menuStrategy.setText("Strategy auto");
        
        for (var i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
            
            this.menuStrategy.getItem(i).setEnabled(false);
        }
        
        this.getMenuError().removeAll();
        this.groupStrategy.clearSelection();
    }
    
    public void setVendor(Vendor vendor) {
        
        this.menuVendor.setText(vendor.toString());
        
        this.initErrorMethods(vendor);
    }
    
    public void resetLabelStrategy() {
        
        for (var i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
            
            this.menuStrategy.getItem(i).setEnabled(false);
            this.menuStrategy.getItem(i).setSelected(false);
        }
    }
    
    public void markStrategy(AbstractStrategy strategy) {
        
        this.menuStrategy.setText(strategy.toString());
        
        for (var i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
            
            if (this.menuStrategy.getItem(i).getText().equals(strategy.toString())) {
                
                this.menuStrategy.getItem(i).setSelected(true);
                
                break;
            }
        }
    }
    
    public void markStrategyInvulnerable(AbstractStrategy strategy) {
        
        for (var i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
            
            if (this.menuStrategy.getItem(i).getText().equals(strategy.toString())) {
                
                this.menuStrategy.getItem(i).setEnabled(false);
                
                break;
            }
        }
    }
    
    public void markErrorInvulnerable(int indexMethodError) {
        
        AbstractStrategy strategy = MediatorHelper.model().getMediatorStrategy().getError();
        
        // Fix #36975: ArrayIndexOutOfBoundsException on getItem()
        // Fix #40352: NullPointerException on ?
        try {
            for (var i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
                
                if (this.menuStrategy.getItem(i).getText().equals(strategy.toString())) {
                    
                    ((JMenu) this.menuStrategy.getItem(i)).getItem(indexMethodError).setEnabled(false);
                    
                    break;
                }
            }
            
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
    
    public void markError() {

        StrategyInjectionError strategy = MediatorHelper.model().getMediatorStrategy().getError();
        this.menuStrategy.setText(strategy.toString());
        
        JMenu menuError = this.getMenuError();
        
        int indexError = strategy.getIndexErrorStrategy();
        String nameError = MediatorHelper.model().getMediatorVendor().getVendor().instance().getModelYaml().getStrategy().getError().getMethod().get(indexError).getName();
        
        for (var i = 0 ; i < menuError.getItemCount() ; i++) {
            
            // Fix #44635: ArrayIndexOutOfBoundsException on getItem()
            try {
                if (menuError.getItem(i).getText().equals(nameError)) {
                    
                    menuError.getItem(i).setSelected(true);
                    this.menuStrategy.setText(nameError);
                    
                    break;
                }
                
            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                
                LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
            }
        }
    }

    private JMenu getMenuError() {
        
        return (JMenu) this.menuStrategy.getMenuComponent(2);
    }
    
    public void markErrorVulnerable(int indexMethodError) {
        
        AbstractStrategy strategy = MediatorHelper.model().getMediatorStrategy().getError();
        
        for (var i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
            
            JMenuItem menuItemStrategy = this.menuStrategy.getItem(i);
            
            if (menuItemStrategy.getText().equals(strategy.toString())) {
                
                JMenu menuError = (JMenu) menuItemStrategy;
                menuError.setEnabled(true);
                
                // Fix #46578: ArrayIndexOutOfBoundsException on getItem()
                if (0 <= indexMethodError && indexMethodError < menuError.getItemCount()) {
                    
                    menuError.getItem(indexMethodError).setEnabled(true);
                }
                
                break;
            }
        }
    }
    
    public void markStrategyVulnerable(AbstractStrategy strategy) {
        
        for (var i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
            
            if (this.menuStrategy.getItem(i).getText().equals(strategy.toString())) {
                
                this.menuStrategy.getItem(i).setEnabled(true);
                
                break;
            }
        }
    }
    
    
    // Getter and setter

    public JMenu getMenuVendor() {
        return this.menuVendor;
    }

    public JMenu getMenuStrategy() {
        return this.menuStrategy;
    }

    public ButtonGroup getGroupStrategy() {
        return this.groupStrategy;
    }

    public JLabel getLoader() {
        return this.loader;
    }

    public ButtonAddressBar getButtonInUrl() {
        return this.buttonInUrl;
    }
}
