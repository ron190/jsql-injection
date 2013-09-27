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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;


/**
 * Build default component appearence, keyboard shortcuts and icons.
 */
public class GUITools {
    public static final Color MENU_BLU = new Color(200,221,242);

    public static final Color SELECTION_BACKGROUND = new Color(211,230,255);
    public static final Color DEFAULT_BACKGROUND = UIManager.getColor("Panel.background");

    public static final Icon TICK = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/check.png"));
    public static final Icon SQUARE_RED = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/squareRed.png"));
    public static final Icon SQUARE_GREY = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/squareGrey.png"));
    public static final Icon SPINNER = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/spinner.gif"));

    public static final Icon ADMIN_SERVER = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/adminServer.png"));
    public static final Icon SHELL_SERVER = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/shellServer.png"));
    public static final Icon DATABASE_SERVER = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/databaseServer.png"));
    public static final Icon FILE_SERVER = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/fileServer.png"));
    public static final Icon BRUTER = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/lock.png"));
    public static final Icon CODER = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/coder.png"));
    public static final Icon UPLOAD = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/server_add.png"));

    public static final Icon TABLE = new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/table.png"));
    public static final Icon EMPTY = new ImageIcon(new BufferedImage(16,16, BufferedImage.TRANSLUCENT));

    public static final String PATH_PAUSE = "/com/jsql/view/images/pause.png";
    public static final String PATH_PROGRESSBAR = "/com/jsql/view/images/progressBar.gif";

    public static final Border BLU_ROUND_BORDER = new RoundBorder(3, 3, true);

    public static final Font myFont = new Font("Segoe UI",Font.PLAIN,UIManager.getDefaults().getFont("TextPane.font").getSize());

    /**
     * Change the default style of various components.
     */
    public static void prepareGUI(){
        ToolTipManager.sharedInstance().setInitialDelay(500);   // timer before showing tooltip
        ToolTipManager.sharedInstance().setDismissDelay(30000); // timer before closing automatically tooltip
        ToolTipManager.sharedInstance().setReshowDelay(1);      // timer used when mouse move to another component, show tooltip immediately if timer is not expired

        // Change border of button in default Save as, Confirm dialogs
        UIManager.put("Button.border", new RoundBorder(7, 3, true));
        // Change border of button in Save as dialog
        UIManager.put("ToggleButton.border", new RoundBorder(7, 3, true));

        // Use ColorUIResource to preserve the background color for arrow
        UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
        //      UIManager.put("JTextField.background", new ColorUIResource(Color.yellow));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(Color.WHITE));

        // No default icon for tree nodes
        UIManager.put("Tree.leafIcon", new ImageIcon());
        UIManager.put("Tree.openIcon", new ImageIcon());
        UIManager.put("Tree.closedIcon", new ImageIcon());

        // No bold for menu + round corner
        UIManager.put("Menu.font", myFont);
        UIManager.put("PopupMenu.font", myFont);
//        UIManager.put("PopupMenu.border", new RoundBorder(2,2,true, Color.LIGHT_GRAY));
        UIManager.put("Menu.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("MenuItem.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("MenuItem.font", myFont);
//        UIManager.put("MenuItem.border", new RoundBorder(2,2,false));
        UIManager.put("MenuItem.borderPainted", false);
        UIManager.put("CheckBoxMenuItem.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("CheckBoxMenuItem.font", myFont);
        UIManager.put("CheckBoxMenuItem.borderPainted", false);
        UIManager.put("CheckBoxMenuItem.checkIcon", new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/check.png")){
            private static final long serialVersionUID = 4678543809597003902L;

            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                ButtonModel m = ((AbstractButton)c).getModel();
                if(m.isSelected())
                    super.paintIcon(c, g, x, y);
            }
        });

        // Custom tab
        //        UIManager.put("TabbedPane.darkShadow", new Color(190,198,205));
        //        UIManager.put("TabbedPane.highlight", new Color(180,194,224));
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(3, 2, 0, 2));
        UIManager.put("TabbedPane.tabInsets", new Insets(2,3+5,2,3));

        // Replace square with bar
        UIManager.put("ScrollBar.squareButtons", false);
        UIManager.put("TextField.font", new Font(((Font) UIManager.get("TextField.font")).getName(),Font.PLAIN,((Font) UIManager.get("TextField.font")).getSize()));
        UIManager.put("TextArea.font", new Font("Courier New",Font.PLAIN,((Font) UIManager.get("TextArea.font")).getSize()));
        UIManager.put("ComboBox.font", myFont);
        UIManager.put("Button.font", myFont);
        UIManager.put("Label.font", myFont);
        UIManager.put("CheckBox.font", myFont);
        UIManager.put("TabbedPane.font", myFont);
        UIManager.put("Table.font", myFont);
        UIManager.put("TableHeader.font", myFont);
        UIManager.put("ToolTip.font", myFont);
        UIManager.put("TitledBorder.font", myFont);

        //        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", SELECTION_BACKGROUND);

        UIManager.put("ToolTip.background", new Color(255,255,225));
        UIManager.put("ToolTip.backgroundInactive", new Color(255,255,225));
//        UIManager.put("ToolTip.border", new RoundBorder(2,2,true));
//        UIManager.put("ToolTip.borderInactive", new RoundBorder(2,2,true));
        UIManager.put("ToolTip.foreground", Color.BLACK);
        UIManager.put("ToolTip.foregroundInactive", Color.BLACK);
        UIManager.put("TextField.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("TextArea.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("Label.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("EditorPane.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("Table.selectionBackground", SELECTION_BACKGROUND);

        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createCompoundBorder( new AbstractBorder() {
            @Override
            public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
                Graphics2D gg = (Graphics2D) g;
                gg.setColor(Color.GRAY);
                gg.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0));
                gg.drawRect(x, y, w - 1, h - 1);
            }
        },BorderFactory.createEmptyBorder(0, 1, 0, 0)));
        
        // Custom tree
        UIManager.put("Tree.expandedIcon", new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/expanded.png")));
        UIManager.put("Tree.collapsedIcon", new ImageIcon(GUITools.class.getResource("/com/jsql/view/images/collapsed.png")));
        UIManager.put("Tree.lineTypeDashed", true);

        // Custom progress bar
        UIManager.put("ProgressBar.border", BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 0, 4, 0), new RoundBorder(2,2,true,Color.GRAY)));
        //        UIManager.put("ProgressBar.foreground", new Color(158,210,152));
        UIManager.put("ProgressBar.foreground", new Color(136,183,104));
        UIManager.put("ProgressBar.background", UIManager.get("Tree.background"));
    }

    private static JTabbedPane valuesTabbedPane;

    /**
     * Add action to a single tabbedpane (ctrl-tab, ctrl-shift-tab)
     */
    public static void addShortcut(JTabbedPane tabbedPane)
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
     * Add action to application window (ctrl-tab, ctrl-shift-tab, ctrl-W)
     */
    public static void addShortcut(JRootPane rootPane, JTabbedPane valuesTabbedPane){
        GUITools.valuesTabbedPane = valuesTabbedPane;

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

    private static class ActionRemoveTab extends AbstractAction {
        private static final long serialVersionUID = -6234281651977146545L;

        public void actionPerformed(ActionEvent e) {
            if(valuesTabbedPane.getTabCount() > 0){
                valuesTabbedPane.removeTabAt(valuesTabbedPane.getSelectedIndex());
            }
        }
    }

    private static class ActionNextTab extends AbstractAction {
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

    private static class ActionPreviousTab extends AbstractAction {
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

    /**
     * Icons for application window.
     * @return List of a 16x16 (default) and 32x32 icon (alt-tab, taskbar)
     */
    public static List<Image> getIcons(){
        List<Image> images = new ArrayList<Image>();
        URL urlSmall = GUITools.class.getResource("/com/jsql/view/images/app-16x16.png");
        URL urlBig = GUITools.class.getResource("/com/jsql/view/images/app-32x32.png");
        try {
            images.add( ImageIO.read(urlBig) );
            images.add( ImageIO.read(urlSmall) );
        } catch (IOException e) {
            // Window not created, debug message displayed only in console
            e.printStackTrace();
        }
        return images;
    }
}
