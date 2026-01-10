/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.action;

import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.util.MediatorHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Keyword shortcut definition. <br>
 * - ctrl TAB: switch to next tab, <br>
 * - ctrl shift TAB: switch to previous tab, <br>
 * - ctrl W: delete tab
 */
public final class HotkeyUtil {
    
    private static final String STR_CTRL_TAB = "ctrl TAB";
    private static final String STR_CTRL_SHIFT_TAB = "ctrl shift TAB";
    private static final String STR_SELECT_TAB = "actionString-selectTab";
    
    private HotkeyUtil() {
        // Utility class
    }
    
    /**
     * Select all textfield content when focused.
     */
    public static void addTextFieldShortcutSelectAll() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
            "permanentFocusOwner",
            propertyChangeEvent -> {
                if (propertyChangeEvent.getNewValue() instanceof JTextField textField) {
                    SwingUtilities.invokeLater(textField::selectAll);
                }
            }
        );
    }
    
    /**
     * Add action to a single tabbedpane (ctrl-tab, ctrl-shift-tab).
     */
    public static void addShortcut(JTabbedPane tabbedPane) {
        var ctrlTab = KeyStroke.getKeyStroke(HotkeyUtil.STR_CTRL_TAB);
        var ctrlShiftTab = KeyStroke.getKeyStroke(HotkeyUtil.STR_CTRL_SHIFT_TAB);

        // Remove ctrl-tab from default focus traversal
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(ctrlTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        // Remove ctrl-shift-tab from default focus traversal
        Set<AWTKeyStroke> backwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.remove(ctrlShiftTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        // Add keys to the tab's input map
        var inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlTab, "navigateNext");
        inputMap.put(ctrlShiftTab, "navigatePrevious");
    }
    
    /**
     * Add action to global root (ctrl-tab, ctrl-shift-tab, ctrl-W).
     */
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
        forwardKeys.remove(KeyStroke.getKeyStroke(HotkeyUtil.STR_CTRL_TAB));
        rootPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
        
        Set<AWTKeyStroke> backwardKeys = new HashSet<>(rootPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.remove(KeyStroke.getKeyStroke(HotkeyUtil.STR_CTRL_SHIFT_TAB));
        rootPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
        
        var inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ctrl W"), "actionString-closeTab");
        actionMap.put("actionString-closeTab", closeTab);
        
        inputMap.put(KeyStroke.getKeyStroke(HotkeyUtil.STR_CTRL_TAB), "actionString-nextTab");
        actionMap.put("actionString-nextTab", nextTab);

        inputMap.put(KeyStroke.getKeyStroke(HotkeyUtil.STR_CTRL_SHIFT_TAB), "actionString-previousTab");
        actionMap.put("actionString-previousTab", previousTab);

        int tabCount = MediatorHelper.tabManagersCards().getComponentCount();
        
        for (var currentTab = 1 ; currentTab <= tabCount ; currentTab++) {
            inputMap.put(KeyStroke.getKeyStroke("ctrl "+ currentTab), HotkeyUtil.STR_SELECT_TAB + currentTab);
            inputMap.put(KeyStroke.getKeyStroke("ctrl NUMPAD"+ currentTab), HotkeyUtil.STR_SELECT_TAB + currentTab);
            
            final int currentTabFinal = currentTab;
            actionMap.put(HotkeyUtil.STR_SELECT_TAB + currentTab, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MediatorHelper.frame().getTabManagers().setSelectedIndex(currentTabFinal - 1);
                }
            });
        }
        
        inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "actionString-saveTab");
        actionMap.put("actionString-saveTab", new ActionSaveTab());
    }

    /**
     * Create Alt shortcut to display menubar ; remove menubar when focus is set to a component.
     * @param appMenubar The menubar to display
     */
    public static void addShortcut(final AppMenubar appMenubar) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(  // Hide Menubar when focusing any component
            "permanentFocusOwner",
            propertyChangeEvent -> SwingUtilities.invokeLater(() -> {
                if (
                    // Fix #40924: NullPointerException on MediatorGui.panelAddressBar()
                    MediatorHelper.panelAddressBar() != null
                    && MediatorHelper.panelAddressBar().isAdvanceActivated()
                ) {
                    appMenubar.setVisible(false);
                }
            })
        );
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(  // Show/Hide the Menubar with Alt key (not Alt Graph)
            new AltKeyEventDispatcher()
        );
    }
}
