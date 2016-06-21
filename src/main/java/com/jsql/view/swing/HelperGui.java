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

/**
 * Build default component appearence, keyboard shortcuts and icons.
 */
@SuppressWarnings("serial")
public final class HelperGui {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(HelperGui.class);

    public static final Color SELECTION_BACKGROUND = (Color) UIManager.get("TabbedPane.selected");
    
    public static final Color DEFAULT_BACKGROUND = UIManager.getColor("Panel.background");
    public static final Color COMPONENT_BORDER = UIManager.getColor("controlShadow");
    public static final Color FOCUS_LOST = new Color(248, 249, 249);
    
    public static final Icon TICK = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/check.png"));
    public static final Icon SQUARE_RED = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/squareRed.png"));
    public static final Icon SQUARE_GREY = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/squareGrey.png"));
    public static final Icon LOADER_GIF = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/spinner.gif"));

    public static final Icon ADMIN_SERVER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/adminServer.png"));
    public static final Icon SHELL_SERVER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/shellServer.png"));
    public static final Icon DATABASE_SERVER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/databaseServer.png"));
    public static final Icon FILE_SERVER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/fileServer.png"));
    public static final Icon BRUTER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/lock.png"));
    public static final Icon CODER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/coder.png"));
    public static final Icon UPLOAD_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/server_add.png"));
    public static final Icon SCANLIST_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/application_cascade.png"));

    public static final Icon TABLE_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/table.png"));
    public static final Icon TABLE_ICON_GO = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/tableGo.png"));
    public static final Icon DATABASE_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/database.png"));
    public static final Icon DATABASE_ICON_GO = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/databaseGo.png"));
    
    public static final Icon CONSOLE_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/console.gif"));
    public static final Icon HEADER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/header.gif"));
    public static final Icon CHUNK_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/chunk.gif"));
    public static final Icon BINARY_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/binary.gif"));
    public static final Icon CUP_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/cup.png"));
    
    public static final Icon CLOSE_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/close.png"));
    public static final Icon CLOSE_ROLLOVER_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/closeRollover.png"));
    public static final Icon CLOSE_PRESSED_ICON = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/closePressed.png"));

    public static final Icon ARROWDEFAULT = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/arrowDefault.png"));
    public static final Icon ARROWROLLOVER = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/arrowRollover.png"));
    public static final Icon ARROWPRESSED = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/arrowPressed.png"));

    public static final Icon FLAG_US = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/flags/us.png"));
    public static final Icon FLAG_FR = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/flags/fr.png"));
    public static final Icon FLAG_CN = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/flags/cn.png"));
    public static final Icon FLAG_RU = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/flags/ru.png"));
    public static final Icon FLAG_TR = new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/flags/tr.png"));
    
    public static final Icon EMPTY = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TRANSLUCENT));
    public static final Icon ZEROSIZE = new ImageIcon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) { 
            // Do nothing
        } 
    };

    public static final String PATH_PAUSE = "/com/jsql/view/swing/resources/images/pause.png";
    public static final String PATH_PROGRESSBAR = "/com/jsql/view/swing/resources/images/progressBar.gif";
    
    public static final Color BLU_COLOR = new Color(132, 172, 221);

    public static final Border BLU_ROUND_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 1, 1, 1, HelperGui.BLU_COLOR),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)
    );

    public static final Font SEGOE_FONT = new Font("Segoe UI", Font.PLAIN, UIManager.getDefaults().getFont("TextPane.font").getSize());

    public static final String CHUNK_VISIBLE = "chunk_visible";
    public static final String BINARY_VISIBLE = "binary_visible";
    public static final String NETWORK_VISIBLE = "header_visible";
    public static final String JAVA_VISIBLE = "java_visible";
    
    /**
     * Utility class.
     */
    private HelperGui() {
        //not called
    }
    
    /**
     * Change the default style of various components.
     */
    public static void prepareGUI() {
        // Custom tooltip
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
        UIManager.put("ToolTip.font", SEGOE_FONT);
        
        // Custom button
        // Change border of button in default Save as, Confirm dialogs
        UIManager.put("Button.border", BLU_ROUND_BORDER);
        UIManager.put("Button.select", SELECTION_BACKGROUND);
        
        // Change border of button in Save as dialog
        UIManager.put("ToggleButton.border", BLU_ROUND_BORDER);

        // No bold for menu + round corner
        UIManager.put("Menu.font", SEGOE_FONT);
        UIManager.put("Menu.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("Menu.borderPainted", false);
        UIManager.put("PopupMenu.font", SEGOE_FONT);
        UIManager.put("RadioButtonMenuItem.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("RadioButtonMenuItem.borderPainted", false);
        UIManager.put("MenuItem.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("MenuItem.font", SEGOE_FONT);
        UIManager.put("MenuItem.borderPainted", false);
        
        UIManager.put("CheckBoxMenuItem.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("CheckBoxMenuItem.font", SEGOE_FONT);
        UIManager.put("CheckBoxMenuItem.borderPainted", false);
        UIManager.put("CheckBoxMenuItem.checkIcon", new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/check.png")) {
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                ButtonModel m = ((AbstractButton) c).getModel();
                if (m.isSelected()) {
                    super.paintIcon(c, g, x, y);
                }
            }
        });

        // Custom tab
        UIManager.put("TabbedPane.font", SEGOE_FONT);
        // margin of current tab panel
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        // margin above tabs
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(3, 2, 0, 2));
        // margin around tab name
        UIManager.put("TabbedPane.tabInsets", new Insets(2, 3 + 5, 2, 3));
        // lighter unselected tab border
        UIManager.put("TabbedPane.darkShadow", new Color(190,198,205));
        
        UIManager.put("Button.font", SEGOE_FONT);
        UIManager.put("CheckBox.font", SEGOE_FONT);
        UIManager.put("TitledBorder.font", SEGOE_FONT);

        UIManager.put("Spinner.arrowButtonBorder", HelperGui.BLU_ROUND_BORDER);
        UIManager.put("Spinner.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HelperGui.BLU_COLOR),
            BorderFactory.createMatteBorder(2,2,2,2, Color.WHITE)
        ));
        
        UIManager.put("FileChooser.listFont", SEGOE_FONT);
        UIManager.put("FileChooser.listViewBorder", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HelperGui.BLU_COLOR),
            BorderFactory.createMatteBorder(2,2,2,2, Color.WHITE)
        ));

        // Custom text component
        // Admin page
        UIManager.put("TextPane.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("TextPane.font", new Font("Ubuntu Mono", Font.PLAIN, ((Font) UIManager.get("TextPane.font")).getSize() + 2));
        UIManager.put("TextPane.selectionBackground", SELECTION_BACKGROUND);
        
        UIManager.put("TextField.font", new Font(((Font) UIManager.get("TextField.font")).getName(), Font.PLAIN, ((Font) UIManager.get("TextField.font")).getSize()));
        UIManager.put("TextField.border", HelperGui.BLU_ROUND_BORDER);
        UIManager.put("TextField.selectionBackground", SELECTION_BACKGROUND);
        
        UIManager.put("EditorPane.selectionBackground", SELECTION_BACKGROUND);
        
        UIManager.put("TextArea.selectionBackground", SELECTION_BACKGROUND);
        UIManager.put("TextArea.font", new Font("Ubuntu Mono", Font.PLAIN, ((Font) UIManager.get("TextArea.font")).getSize() + 2));

        // Custom Label
        UIManager.put("Label.font", SEGOE_FONT);
        UIManager.put("Label.selectionBackground", SELECTION_BACKGROUND);

        // Custom table
        UIManager.put("Table.font", SEGOE_FONT);
        UIManager.put("TableHeader.font", SEGOE_FONT);
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
        UIManager.put("Tree.expandedIcon", new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/expanded.png")));
        UIManager.put("Tree.collapsedIcon", new ImageIcon(HelperGui.class.getResource("/com/jsql/view/swing/resources/images/collapsed.png")));
        UIManager.put("Tree.lineTypeDashed", true);
        // No default icon for tree nodes
        UIManager.put("Tree.leafIcon", new ImageIcon());
        UIManager.put("Tree.openIcon", new ImageIcon());
        UIManager.put("Tree.closedIcon", new ImageIcon());

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

        // Custom ComboBox
        UIManager.put("ComboBox.font", SEGOE_FONT);
        UIManager.put("ComboBox.selectionBackground", SELECTION_BACKGROUND);
        // Use ColorUIResource to preserve the background color for arrow
        UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
        UIManager.put("ComboBox.border", HelperGui.BLU_ROUND_BORDER);
        UIManager.put("ComboBoxUI", CustomBasicComboBoxUI.class.getName());
        
        UIManager.put("swing.boldMetal", Boolean.FALSE);
    }

    /**
     * Icons for application window.
     * @return List of a 16x16 (default) and 32x32 icon (alt-tab, taskbar)
     */
    public static List<Image> getIcons() {
        List<Image> images = new ArrayList<>();
        URL url16x16 = HelperGui.class.getResource("/com/jsql/view/swing/resources/images/app-16x16.png");
        URL url32x32 = HelperGui.class.getResource("/com/jsql/view/swing/resources/images/app-32x32.png");
        URL url96x96 = HelperGui.class.getResource("/com/jsql/view/swing/resources/images/app-96x96.png");
        try {
            images.add(ImageIO.read(url96x96));
            images.add(ImageIO.read(url32x32));
            images.add(ImageIO.read(url16x16));
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
        return images;
    }
}
