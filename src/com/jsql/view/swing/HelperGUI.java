/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import org.apache.log4j.Logger;

import com.jsql.view.swing.ui.CustomBasicComboBoxUI;
import com.jsql.view.swing.ui.CustomBasicSpinnerUI;
import com.jsql.view.swing.ui.CustomMetalTabbedPaneUI;

/**
 * Build default component appearence, keyboard shortcuts and icons.
 */
@SuppressWarnings("serial")
public final class HelperGUI {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(HelperGUI.class);

    public static final Color SELECTION_BACKGROUND = (Color) UIManager.get("TabbedPane.selected");
    
    public static final Color DEFAULT_BACKGROUND = UIManager.getColor("Panel.background");
    public static final Color COMPONENT_BORDER = UIManager.getColor("controlShadow");
    public static final Color FOCUS_LOST = new Color(248, 249, 249);
    
    public static final Icon TICK = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/check.png"));
    public static final Icon SQUARE_RED = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/squareRed.png"));
    public static final Icon SQUARE_GREY = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/squareGrey.png"));
    public static final Icon LOADER_GIF = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/spinner.gif"));

    public static final Icon ADMIN_SERVER_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/adminServer.png"));
    public static final Icon SHELL_SERVER_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/shellServer.png"));
    public static final Icon DATABASE_SERVER_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/databaseServer.png"));
    public static final Icon FILE_SERVER_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/fileServer.png"));
    public static final Icon BRUTER_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/lock.png"));
    public static final Icon CODER_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/coder.png"));
    public static final Icon UPLOAD_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/server_add.png"));
    public static final Icon TABLE_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/table.png"));
    public static final Icon SCANLIST_ICON = new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/application_cascade.png"));

    public static final Icon EMPTY = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TRANSLUCENT));
    public static final Icon ZEROSIZE = new ImageIcon() {
        public void paintIcon(Component c, Graphics g, int x, int y) { 
            // Do nothing
        } 
    };

    public static final String PATH_PAUSE = "/com/jsql/view/swing/images/pause.png";
    public static final String PATH_PROGRESSBAR = "/com/jsql/view/swing/images/progressBar.gif";

    public static final Border BLU_ROUND_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(132, 172, 221)),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)
    );

    public static final Font MYFONT = new Font("Segoe UI", Font.PLAIN, UIManager.getDefaults().getFont("TextPane.font").getSize());

    public static final String CHUNK_VISIBLE = "chunk_visible";
    public static final String BINARY_VISIBLE = "binary_visible";
    public static final String NETWORK_VISIBLE = "header_visible";
    public static final String JAVA_VISIBLE = "java_visible";
    
    /**
     * Utility class.
     */
    private HelperGUI() {
        //not called
    }
    
    /**
     * Change the default style of various components.
     */
    public static void prepareGUI() {
        // timer before showing tooltip
        ToolTipManager.sharedInstance().setInitialDelay(500);
        // timer before closing automatically tooltip
        ToolTipManager.sharedInstance().setDismissDelay(30000);
        // timer used when mouse move to another component, show tooltip immediately if timer is not expired
        ToolTipManager.sharedInstance().setReshowDelay(1);

        UIManager.put("ToolTip.background", new Color(255, 255, 225));
        UIManager.put("ToolTip.backgroundInactive", new Color(255, 255, 225));
        UIManager.put("ToolTip.foreground", Color.BLACK);
        UIManager.put("ToolTip.foregroundInactive", Color.BLACK);
        
        // Change border of button in default Save as, Confirm dialogs
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(132, 172, 221)),
            BorderFactory.createEmptyBorder(2, 2, 2, 2))
        );
//        UIManager.put("Button.darkShadow", Color.RED);
//        UIManager.put("Button.background", Color.RED);
//        UIManager.put("Button.focus", Color.RED);
//        UIManager.put("Button.foreground", Color.RED);
//        UIManager.put("Button.highlight", Color.RED);
//        UIManager.put("Button.light", Color.RED);
        UIManager.put("Button.select", SELECTION_BACKGROUND);
//        UIManager.put("Button.shadow", Color.RED);
//        UIManager.put("Button.toolBarBorderBackground", Color.RED);
        
        // Change border of button in Save as dialog
        UIManager.put("ToggleButton.border", BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(132, 172, 221)),
            BorderFactory.createEmptyBorder(2, 2, 2, 2))
        );

        // Use ColorUIResource to preserve the background color for arrow
        UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(Color.WHITE));

        // No default icon for tree nodes
        UIManager.put("Tree.leafIcon", new ImageIcon());
        UIManager.put("Tree.openIcon", new ImageIcon());
        UIManager.put("Tree.closedIcon", new ImageIcon());

        // Admin page
        UIManager.put("TextPane.selectionBackground", SELECTION_BACKGROUND);
        
        // No bold for menu + round corner
        UIManager.put("Menu.font", MYFONT);
        UIManager.put("PopupMenu.font", MYFONT);
        UIManager.put("Menu.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("MenuItem.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("MenuItem.font", MYFONT);
        UIManager.put("MenuItem.borderPainted", false);
        
        UIManager.put("CheckBoxMenuItem.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("CheckBoxMenuItem.font", MYFONT);
        UIManager.put("CheckBoxMenuItem.borderPainted", false);
        UIManager.put("CheckBoxMenuItem.checkIcon", new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/check.png")) {
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                ButtonModel m = ((AbstractButton) c).getModel();
                if (m.isSelected()) {
                    super.paintIcon(c, g, x, y);
                }
            }
        });

        // Custom tab
        // margin of current tab panel
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        // margin above tabs
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(3, 2, 0, 2));
        // margin around tab name
        UIManager.put("TabbedPane.tabInsets", new Insets(2, 3 + 5, 2, 3));
        
        // lighter unselected tab border
        UIManager.put("TabbedPane.darkShadow", new Color(190,198,205));
        
        UIManager.put("TextField.font", new Font(((Font) UIManager.get("TextField.font")).getName(), Font.PLAIN, ((Font) UIManager.get("TextField.font")).getSize()));
        UIManager.put("TextArea.font", new Font("Ubuntu Mono", Font.PLAIN, ((Font) UIManager.get("TextArea.font")).getSize() + 2));
        UIManager.put("TextPane.font", new Font("Ubuntu Mono", Font.PLAIN, ((Font) UIManager.get("TextPane.font")).getSize() + 2));
        UIManager.put("ComboBox.font", MYFONT);
        UIManager.put("Button.font", MYFONT);
        UIManager.put("Label.font", MYFONT);
        UIManager.put("CheckBox.font", MYFONT);
        UIManager.put("TabbedPane.font", MYFONT);
        UIManager.put("Table.font", MYFONT);
        UIManager.put("TableHeader.font", MYFONT);
        UIManager.put("ToolTip.font", MYFONT);
        UIManager.put("TitledBorder.font", MYFONT);
        UIManager.put("FileChooser.listFont", MYFONT);

//        UIManager.put("Spinner.arrowButtonSize", new Dimension(10, 10));
//        UIManager.put("Spinner.arrowButtonInsets", new Insets(0, 0, 0, 0));
        UIManager.put("Spinner.arrowButtonBorder", HelperGUI.BLU_ROUND_BORDER);
        UIManager.put("Spinner.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(132, 172, 221)),
            BorderFactory.createMatteBorder(2,2,2,2, Color.WHITE)
        ));
        UIManager.put("ComboBox.border", HelperGUI.BLU_ROUND_BORDER);
        UIManager.put("TextField.border", HelperGUI.BLU_ROUND_BORDER);
        
        UIManager.put("FileChooser.listViewBorder", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(132, 172, 221)),
            BorderFactory.createMatteBorder(2,2,2,2, Color.WHITE)
        ));
        
        UIManager.put("ComboBox.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("TextField.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("TextPane.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("TextArea.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("Label.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("EditorPane.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("Table.selectionBackground", SELECTION_BACKGROUND);

        UIManager.put("Table.focusCellHighlightBorder",
            BorderFactory.createCompoundBorder(
                new AbstractBorder() {
                    @Override
                    public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
                        Graphics2D gg = (Graphics2D) g;
                        gg.setColor(Color.GRAY);
                        gg.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0));
                        gg.drawRect(x, y, w - 1, h - 1);
                    }
                },
                BorderFactory.createEmptyBorder(0, 1, 0, 0)
            )
        );
        
        // Custom tree
        UIManager.put("Tree.expandedIcon", new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/expanded.png")));
        UIManager.put("Tree.collapsedIcon", new ImageIcon(HelperGUI.class.getResource("/com/jsql/view/swing/images/collapsed.png")));
        UIManager.put("Tree.lineTypeDashed", true);

        // Custom progress bar
        UIManager.put("ProgressBar.border", BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(3, 0, 4, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createLineBorder(Color.WHITE)
            )
        ));
        UIManager.put("ProgressBar.foreground", new Color(136, 183, 104));
        UIManager.put("ProgressBar.background", UIManager.get("Tree.background"));
        
        UIManager.put("TabbedPaneUI", CustomMetalTabbedPaneUI.class.getName());
//        UIManager.put("TabbedPaneUI", BasicTabbedPaneUI.class.getName());
        UIManager.put("ComboBoxUI", CustomBasicComboBoxUI.class.getName());
        UIManager.put("SpinnerUI", CustomBasicSpinnerUI.class.getName());
    }

    /**
     * Icons for application window.
     * @return List of a 16x16 (default) and 32x32 icon (alt-tab, taskbar)
     */
    public static List<Image> getIcons() {
        List<Image> images = new ArrayList<Image>();
        URL urlSmall = HelperGUI.class.getResource("/com/jsql/view/swing/images/app-16x16.png");
        URL urlBig = HelperGUI.class.getResource("/com/jsql/view/swing/images/app-32x32.png");
        URL urlBig2 = HelperGUI.class.getResource("/com/jsql/view/swing/images/app-96x96.png");
        try {
            images.add(ImageIO.read(urlBig2));
            images.add(ImageIO.read(urlBig));
            images.add(ImageIO.read(urlSmall));
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
        return images;
    }
}
