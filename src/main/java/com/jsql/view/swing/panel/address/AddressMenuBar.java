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
import javax.swing.MenuElement;

import com.jsql.i18n.I18n;
import com.jsql.model.MediatorModel;
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.StrategyInjectionError;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.manager.util.ComboMenu;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.util.ButtonAddressBar;
import com.jsql.view.swing.ui.ComponentBorder;

@SuppressWarnings("serial")
public class AddressMenuBar extends JMenuBar {

    private JMenu[] itemRadioStrategyError = new JMenu[1];

    private JMenu menuVendor;
    private JMenu menuStrategy;
    private ButtonGroup groupStrategy = new ButtonGroup();

    /**
     * Animated GIF displayed during injection.
     */
    private JLabel loader = new JLabel(HelperUi.ICON_LOADER_GIF);

    /**
     * Connection button.
     */
    private ButtonAddressBar buttonInUrl = new ButtonAddressBar();
    
    public AddressMenuBar(PanelAddressBar panelAddressBar) {
        
        this.buttonInUrl.setToolTipText(I18n.valueByKey("BUTTON_START_TOOLTIP"));
        this.buttonInUrl.addActionListener(new ActionStart(panelAddressBar));

        ComponentBorder buttonInTextfield = new ComponentBorder(this.buttonInUrl, 17, 0);
        buttonInTextfield.install(panelAddressBar.getTextFieldAddress());
        
        this.setOpaque(false);
        this.setBorder(null);

        this.menuStrategy = new ComboMenu("Strategy auto");

        this.itemRadioStrategyError = new JMenu[1];

        for (final AbstractStrategy strategy: MediatorModel.model().getMediatorStrategy().getStrategies()) {
            
            MenuElement itemRadioStrategy;

            if (strategy == MediatorModel.model().getMediatorStrategy().getError()) {
                itemRadioStrategy = new JMenu(strategy.toString());
                this.itemRadioStrategyError[0] = (JMenu) itemRadioStrategy;
            } else {
                itemRadioStrategy = new JRadioButtonMenuItem(strategy.toString());
                ((AbstractButton) itemRadioStrategy).addActionListener(actionEvent -> {
                    this.menuStrategy.setText(strategy.toString());
                    MediatorModel.model().getMediatorStrategy().setStrategy(strategy);
                });
                this.groupStrategy.add((AbstractButton) itemRadioStrategy);
            }

            this.menuStrategy.add((JMenuItem) itemRadioStrategy);
            
            // TODO i18n dynamic tooltip missing
            ((JComponent) itemRadioStrategy).setToolTipText(I18n.valueByKey("STRATEGY_" + strategy.getName().toUpperCase(Locale.ROOT) + "_TOOLTIP"));
            ((JComponent) itemRadioStrategy).setEnabled(false);
        }

        this.menuVendor = new ComboMenu(MediatorModel.model().getMediatorVendor().getAuto().toString());

        ButtonGroup groupVendor = new ButtonGroup();

        for (final Vendor vendor: MediatorModel.model().getMediatorVendor().getVendors()) {
            
            JMenuItem itemRadioVendor = new JRadioButtonMenuItem(vendor.toString(), vendor == MediatorModel.model().getMediatorVendor().getAuto());
            itemRadioVendor.addActionListener(actionEvent -> {
                this.menuVendor.setText(vendor.toString());
                MediatorModel.model().getMediatorVendor().setVendorByUser(vendor);
            });
            
            this.menuVendor.add(itemRadioVendor);
            groupVendor.add(itemRadioVendor);
        }

        this.add(Box.createHorizontalGlue());
        this.add(this.loader);
        this.add(Box.createHorizontalStrut(5));
        this.add(this.menuVendor);
        this.add(this.menuStrategy);

        this.loader.setVisible(false);
        this.loader.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    public void initErrorMethods(Vendor vendor) {
        
        this.itemRadioStrategyError[0].removeAll();

        Integer[] i = { 0 };
        if (vendor != MediatorModel.model().getMediatorVendor().getAuto() && vendor.instance().getModelYaml().getStrategy().getError() != null) {
            
            for (Method methodError: vendor.instance().getModelYaml().getStrategy().getError().getMethod()) {
                
                JMenuItem itemRadioVendor = new JRadioButtonMenuItem(methodError.getName());
                itemRadioVendor.setEnabled(false);
                this.itemRadioStrategyError[0].add(itemRadioVendor);
                this.groupStrategy.add(itemRadioVendor);

                final int indexError = i[0];
                itemRadioVendor.addActionListener(actionEvent -> {
                    
                    this.menuStrategy.setText(methodError.getName());
                    MediatorModel.model().getMediatorStrategy().setStrategy(MediatorModel.model().getMediatorStrategy().getError());
                    ((StrategyInjectionError) MediatorModel.model().getMediatorStrategy().getError()).setIndexMethod(indexError);
                });

                i[0]++;
            }
        }
    }
    
    public void reset() {
        
        if (MediatorModel.model().getMediatorVendor().getVendorByUser() == MediatorModel.model().getMediatorVendor().getAuto()) {
            this.menuVendor.setText(MediatorModel.model().getMediatorVendor().getAuto().toString());
        }
        
        this.menuStrategy.setText("Strategy auto");
        
        for (int i = 0 ; i < this.menuStrategy.getItemCount() ; i++) {
            this.menuStrategy.getItem(i).setEnabled(false);
        }
        
        // TODO remove Error strategy magic number 2
        ((JMenu) this.menuStrategy.getItem(2)).removeAll();
        this.groupStrategy.clearSelection();
    }

    public JMenu[] getItemRadioStrategyError() {
        return this.itemRadioStrategyError;
    }

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
