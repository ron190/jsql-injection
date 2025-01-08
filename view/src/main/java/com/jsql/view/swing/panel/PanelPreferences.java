package com.jsql.view.swing.panel;

import com.jsql.util.I18nUtil;
import com.jsql.view.swing.panel.preferences.*;
import com.jsql.view.swing.panel.preferences.listener.ActionListenerSave;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.text.WordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelPreferences extends JPanel {
    
    private final transient ActionListener actionListenerSave = new ActionListenerSave(this);
    
    private final PanelInjection panelInjection = new PanelInjection(this);
    private final PanelTampering panelTampering = new PanelTampering(this);
    private final PanelConnection panelConnection = new PanelConnection(this);
    private final PanelStrategies panelStrategies = new PanelStrategies(this);
    private final PanelAuthentication panelAuthentication = new PanelAuthentication(this);
    private final PanelUserAgent panelUserAgent = new PanelUserAgent(this);
    private final PanelProxy panelProxy = new PanelProxy(this);
    private final PanelGeneral panelGeneral = new PanelGeneral(this);

    private enum CategoryPreference {
        INJECTION,
        TAMPERING,
        CONNECTION,
        STRATEGIES,
        AUTHENTICATION,
        USER_AGENT,
        PROXY,
        GENERAL;

        @Override
        public String toString() {
            return "  "+ WordUtils.capitalizeFully(this.name()).replace('_', ' ') +"  ";
        }
    }
    
    public PanelPreferences() {
        this.setLayout(new BorderLayout());
        this.setBorder(UiUtil.BORDER_5PX);

        var cards = new JPanel(new CardLayout());
        cards.setMinimumSize(new Dimension(0, 0));  // required

        JList<CategoryPreference> categories = PanelPreferences.getCategories(cards);
        this.add(categories, BorderLayout.LINE_START);

        this.addToCard(cards, panelInjection, CategoryPreference.INJECTION);
        this.addToCard(cards, panelTampering, CategoryPreference.TAMPERING);
        this.addToCard(cards, panelConnection, CategoryPreference.CONNECTION);
        this.addToCard(cards, panelStrategies, CategoryPreference.STRATEGIES);
        this.addToCard(cards, panelAuthentication, CategoryPreference.AUTHENTICATION);
        this.addToCard(cards, panelUserAgent, CategoryPreference.USER_AGENT);
        this.addToCard(cards, panelProxy, CategoryPreference.PROXY);
        this.addToCard(cards, panelGeneral, CategoryPreference.GENERAL);
        this.add(cards, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> MediatorHelper.menubar().switchLocale(I18nUtil.getCurrentLocale()));  // required for arabic
    }

    private static JList<CategoryPreference> getCategories(JPanel cards) {
        JList<CategoryPreference> categories = new JList<>(CategoryPreference.values());
        categories.setMinimumSize(new Dimension(0, 0));
        categories.setName("listCategoriesPreference");
        categories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categories.setSelectedIndex(0);
        categories.addListSelectionListener(e -> {
            CardLayout cardLayout = (CardLayout) cards.getLayout();
            cardLayout.show(cards, categories.getSelectedValue().name());
        });
        return categories;
    }

    private void addToCard(JPanel cards, JPanel panel, CategoryPreference category) {
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        var scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());  // required to hide border
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        cards.add(scrollPane, category.name());
    }

    
    // Getter and setter

    public PanelAuthentication getPanelAuthentication() {
        return this.panelAuthentication;
    }

    public PanelProxy getPanelProxy() {
        return this.panelProxy;
    }

    public PanelInjection getPanelInjection() {
        return this.panelInjection;
    }

    public PanelTampering getPanelTampering() {
        return this.panelTampering;
    }

    public PanelGeneral getPanelGeneral() {
        return this.panelGeneral;
    }
    
    public PanelConnection getPanelConnection() {
        return this.panelConnection;
    }

    public PanelStrategies getPanelStrategies() {
        return this.panelStrategies;
    }

    public PanelUserAgent getPanelUserAgent() {
        return panelUserAgent;
    }

    public ActionListener getActionListenerSave() {
        return this.actionListenerSave;
    }
}
