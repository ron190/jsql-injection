/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view;

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
import javax.swing.KeyStroke;

/**
 * Keyword shortcut definition:
 * - ctrl TAB: switch to next tab,
 * - ctrl shift TAB: switch to previous tab,
 * - ctrl W: delete tab
 */
public class ActionHandler {
    private JRootPane rootPane;
    private JTabbedPane valuesTabbedPane;
    
    /**
     * Add action to a single tabbedpane (ctrl-tab, ctrl-shift-tab)
     */
    public ActionHandler(JTabbedPane tabbedPane)
    {
        KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
        KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");

        // Remove ctrl-tab from normal focus traversal
        Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(ctrlTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        // Remove ctrl-shift-tab from normal focus traversal
        Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.remove(ctrlShiftTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        // Add keys to the tab's input map
        InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlTab, "navigateNext");
        inputMap.put(ctrlShiftTab, "navigatePrevious");
    }
    
    /**
     * Add action to global root (ctrl-tab, ctrl-shift-tab, ctrl-W)
     */
    public ActionHandler(JRootPane newRootPane, JTabbedPane newTabbedPane){
        this.rootPane = newRootPane;
        this.valuesTabbedPane = newTabbedPane;
        
        Action closeTab = new ActionRemoveTab();
        Action nextTab = new ActionNextTab();
        Action previousTab = new ActionPreviousTab();
        
        Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(rootPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(KeyStroke.getKeyStroke("ctrl TAB"));
        rootPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
        
        Set<AWTKeyStroke> forwardKeys2 = new HashSet<AWTKeyStroke>(rootPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
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
    }
    
    private class ActionRemoveTab extends AbstractAction {
        private static final long serialVersionUID = -6234281651977146545L;

        public void actionPerformed(ActionEvent e) {
            if(valuesTabbedPane.getTabCount() > 0){
                valuesTabbedPane.removeTabAt(valuesTabbedPane.getSelectedIndex());
            }
        }
    }
    
    private class ActionNextTab extends AbstractAction {
        private static final long serialVersionUID = 3514524611956271798L;

        public void actionPerformed(ActionEvent e) {
            if(valuesTabbedPane.getTabCount() > 0){
                int selectedIndex = valuesTabbedPane.getSelectedIndex();
                if(selectedIndex+1 < valuesTabbedPane.getTabCount()){
                    valuesTabbedPane.setSelectedIndex(selectedIndex+1);
                }else{
                    valuesTabbedPane.setSelectedIndex(0);
                }
            }
        }
    }
    
    private class ActionPreviousTab extends AbstractAction {
        private static final long serialVersionUID = -984315842794140182L;

        public void actionPerformed(ActionEvent e) {
            if(valuesTabbedPane.getTabCount() > 0){
                int selectedIndex = valuesTabbedPane.getSelectedIndex();
                if(selectedIndex-1 > -1){
                    valuesTabbedPane.setSelectedIndex(selectedIndex-1);
                }else{
                    valuesTabbedPane.setSelectedIndex(valuesTabbedPane.getTabCount()-1);
                }
            }
        }
    }
}
