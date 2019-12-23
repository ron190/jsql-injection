/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.action;

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.menubar.Menubar;

/**
 * Keyword shortcut definition. <br>
 * - ctrl TAB: switch to next tab, <br>
 * - ctrl shift TAB: switch to previous tab, <br>
 * - ctrl W: delete tab
 */
public final class ActionHandler {
    
    /**
     * Utility class without constructor.
     */
    private ActionHandler() {
        //not called
    }
    
    /**
     * Select all textfield content when focused.
     */
    public static void addTextFieldShortcutSelectAll() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
            "permanentFocusOwner",
            propertyChangeEvent -> {
                if (propertyChangeEvent.getNewValue() instanceof JTextField) {
                    SwingUtilities.invokeLater(() -> {
                        JTextField textField = (JTextField) propertyChangeEvent.getNewValue();
                        textField.selectAll();
                    });
                }
            }
        );
    }
    
    /**
     * Add action to a single tabbedpane (ctrl-tab, ctrl-shift-tab).
     */
    public static void addShortcut(JTabbedPane tabbedPane) {
        KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
        KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");

        // Remove ctrl-tab from normal focus traversal
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(ctrlTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        // Remove ctrl-shift-tab from normal focus traversal
        Set<AWTKeyStroke> backwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.remove(ctrlShiftTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        // Add keys to the tab's input map
        InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlTab, "navigateNext");
        inputMap.put(ctrlShiftTab, "navigatePrevious");
    }
    
    /**
     * Add action to global root (ctrl-tab, ctrl-shift-tab, ctrl-W).
     */
    @SuppressWarnings("serial")
    public static void addShortcut(JRootPane rootPane, final JTabbedPane valuesTabbedPane) {
        Action closeTab = new ActionCloseTabResult();
        
        Action nextTab = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (valuesTabbedPane.getTabCount() > 0) {
                    int selectedIndex = valuesTabbedPane.getSelectedIndex();
                    if (selectedIndex + 1 < valuesTabbedPane.getTabCount()) {
                        valuesTabbedPane.setSelectedIndex(selectedIndex + 1);
                    } else {
                        valuesTabbedPane.setSelectedIndex(0);
                    }
                }
            }
        };
        
        Action previousTab = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (valuesTabbedPane.getTabCount() > 0) {
                    int selectedIndex = valuesTabbedPane.getSelectedIndex();
                    if (selectedIndex - 1 > -1) {
                        valuesTabbedPane.setSelectedIndex(selectedIndex - 1);
                    } else {
                        valuesTabbedPane.setSelectedIndex(valuesTabbedPane.getTabCount() - 1);
                    }
                }
            }
        };
        
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(rootPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(KeyStroke.getKeyStroke("ctrl TAB"));
        rootPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
        
        Set<AWTKeyStroke> forwardKeys2 = new HashSet<>(rootPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        forwardKeys2.remove(KeyStroke.getKeyStroke("ctrl shift TAB"));
        rootPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, forwardKeys2);
        
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ctrl W"), "actionString-closeTab");
        actionMap.put("actionString-closeTab", closeTab);
        
        inputMap.put(KeyStroke.getKeyStroke("ctrl TAB"), "actionString-nextTab");
        actionMap.put("actionString-nextTab", nextTab);

        inputMap.put(KeyStroke.getKeyStroke("ctrl shift TAB"), "actionString-previousTab");
        actionMap.put("actionString-previousTab", previousTab);
        
        int i = MediatorGui.tabManagers().getTabCount();
        for (int k = 1 ; k <= i ; k++) {
            inputMap.put(KeyStroke.getKeyStroke("ctrl "+ k), "actionString-selectTab"+ k);
            inputMap.put(KeyStroke.getKeyStroke("ctrl NUMPAD"+ k), "actionString-selectTab"+ k);
            
            final int l = k;
            actionMap.put("actionString-selectTab"+ k, new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    MediatorGui.tabManagers().setSelectedIndex(l-1);
                }
            });
        }
        
        inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "actionString-saveTab");
        actionMap.put("actionString-saveTab", new ActionSaveTab());
    }

    /**
     * Create Alt shortcut to display menubar ; remove menubar when focus is set to a component.
     * @param menubar The menubar to display
     */
    public static void addShortcut(final Menubar menubar) {
        /* Hide Menubar when focusing any component */
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner",
            propertyChangeEvent -> SwingUtilities.invokeLater(() -> {
                if (
                    // Fix #40924: NullPointerException on MediatorGui.panelAddressBar()
                    MediatorGui.panelAddressBar() != null
                    && !MediatorGui.panelAddressBar().isAdvanceIsActivated()
                ) {
                    menubar.setVisible(false);
                }
            })
        );
        
        /* Show/Hide the Menubar with Alt key (not Alt Graph) */
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
            new AltKeyEventDispatcher()
        );
    }
    
}
