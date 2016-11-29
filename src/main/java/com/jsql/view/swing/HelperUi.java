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
package com.jsql.view.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import org.apache.log4j.Logger;

import com.jsql.view.swing.console.SwingAppender;
import com.jsql.view.swing.ui.CheckBoxIcon;
import com.jsql.view.swing.ui.CustomBasicComboBoxUI;

/**
 * Build default component appearence, keyboard shortcuts and icons.
 */
@SuppressWarnings("serial")
public final class HelperUi {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(HelperUi.class);

    public static final Color COLOR_SELECTION_BACKGROUND = (Color) UIManager.get("TabbedPane.selected");
    
    public static final Color COLOR_DEFAULT_BACKGROUND = UIManager.getColor("Panel.background");
    public static final Color COLOR_COMPONENT_BORDER = UIManager.getColor("controlShadow");
    public static final Color COLOR_FOCUS_LOST = new Color(248, 249, 249);
    
    public static final URL URL_GLOBE = HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/globe.png");
    
    public static final Icon ICON_TICK = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/check.png"));
    public static final Icon ICON_SQUARE_RED = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/squareRed.png"));
    public static final Icon ICON_SQUARE_GREY = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/squareGrey.png"));
    public static final Icon ICON_LOADER_GIF = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/spinner.gif"));

    public static final Icon ICON_ADMIN_SERVER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/admin.png"));
    public static final Icon ICON_SHELL_SERVER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/application_xp_terminal.png"));
    public static final Icon ICON_DATABASE_SERVER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/database.png"));
    public static final Icon ICON_FILE_SERVER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/file.png"));
    public static final Icon ICON_BRUTER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/lock_open.png"));
    public static final Icon ICON_CODER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/textfield.png"));
    public static final Icon ICON_UPLOAD = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/disk.png"));
    public static final Icon ICON_SCANLIST = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/table_multiple.png"));

    public static final Icon ICON_TABLE = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/table.png"));
    public static final Icon ICON_TABLE_GO = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/tableGo.png"));
    public static final Icon ICON_DATABASE = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/database.png"));
    public static final Icon ICON_DATABASE_GO = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/databaseGo.png"));
    
    public static final Icon ICON_CONSOLE = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/console.gif"));
    public static final Icon ICON_HEADER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/header.gif"));
    public static final Icon ICON_CHUNK = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/chunk.gif"));
    public static final Icon ICON_BINARY = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/binary.gif"));
    public static final Icon ICON_CUP = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/cup.png"));
    
    public static final Icon ICON_CLOSE = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/close.png"));
    public static final Icon ICON_CLOSE_ROLLOVER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/closeRollover.png"));
    public static final Icon ICON_CLOSE_PRESSED = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/closePressed.png"));

    public static final Icon ICON_ARROW_DEFAULT = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/arrowDefault.png"));
    public static final Icon ICON_ARROW_ROLLOVER = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/arrowRollover.png"));
    public static final Icon ICON_ARROW_PRESSED = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/arrowPressed.png"));

    public static final Icon ICON_FLAG_AR = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/ar.png"));
    public static final Icon ICON_FLAG_ZH = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/zh.png"));
    public static final Icon ICON_FLAG_RU = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/ru.png"));
    public static final Icon ICON_FLAG_TR = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/tr.png"));
    public static final Icon ICON_FLAG_EN = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/en.png"));
    public static final Icon ICON_FLAG_FR = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/fr.png"));
    public static final Icon ICON_FLAG_HI = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/hi.png"));
    public static final Icon ICON_FLAG_CS = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/flags/cs.png"));
    
    public static final URL URL_ICON_16 = HelperUi.class.getResource("/com/jsql/view/swing/resources/images/software/bug16.png");
    public static final URL URL_ICON_32 = HelperUi.class.getResource("/com/jsql/view/swing/resources/images/software/bug32.png");
    public static final URL URL_ICON_96 = HelperUi.class.getResource("/com/jsql/view/swing/resources/images/software/bug96.png");
    public static final URL URL_ICON_128 = HelperUi.class.getResource("/com/jsql/view/swing/resources/images/software/bug128.png");

    public static final Icon ICON_EMPTY = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TRANSLUCENT));
    public static final Icon ICON_ZERO_SIZE = new ImageIcon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) { 
            // Do nothing
        } 
    };

    public static final String PATH_PAUSE = "/com/jsql/view/swing/resources/images/icons/pause.png";
    public static final String PATH_PROGRESSBAR = "/com/jsql/view/swing/resources/images/icons/progressBar.gif";
    
    public static final Icon ICON_ERROR = new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/error.png"));
    
    public static final Color COLOR_BLU = new Color(132, 172, 221);

    public static final Border BORDER_BLU = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 1, 1, 1, HelperUi.COLOR_BLU),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)
    );
    
    public static final Border BORDER_ROUND_BLU = new AbstractBorder() {
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int r = 5;
            RoundRectangle2D round = new RoundRectangle2D.Float(x, y, width-1, height-1, r, r);
            Container parent = c.getParent();
            if(parent!=null) {
                g2.setColor(parent.getBackground());
                Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
                corner.subtract(new Area(round));
                g2.fill(corner);
            }
            g2.setColor(Color.GRAY);
            g2.draw(round);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
        @Override public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 4;
            return insets;
        }
    };

    public static final String FONT_NAME = "Ubuntu Mono";

    public static final Font FONT_SEGOE = new Font("Segoe UI", Font.PLAIN, UIManager.getDefaults().getFont("TextPane.font").getSize());
    public static final Font FONT_SEGOE_BIG = new Font(
        UIManager.getDefaults().getFont("TextField.font").getName(), 
        Font.PLAIN, 
        UIManager.getDefaults().getFont("TextField.font").getSize() + 2
    );
    public static final Font FONT_UBUNTU = new Font(FONT_NAME, Font.PLAIN, UIManager.getDefaults().getFont("TextArea.font").getSize() + 2);

    public static final String CHUNK_VISIBLE = "chunk_visible";
    public static final String BINARY_VISIBLE = "binary_visible";
    public static final String NETWORK_VISIBLE = "header_visible";
    public static final String JAVA_VISIBLE = "java_visible";
    
    /**
     * Utility class.
     */
    private HelperUi() {
        //not called
    }
    
    /**
     * Change the default style of various components.
     */
    public static void prepareGUI() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try (InputStream fontStream = new BufferedInputStream(SwingAppender.class.getResourceAsStream("/com/jsql/view/swing/resources/font/UbuntuMono-R.ttf"))) {
            Font ubuntuFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            ge.registerFont(ubuntuFont);
        } catch (FontFormatException | IOException e) {
            LOGGER.warn("Loading Font Ubuntu failed", e);
        }
        
        // Custom tooltip
        // timer before showing tooltip
        ToolTipManager.sharedInstance().setInitialDelay(750);
        // timer before closing automatically tooltip
        ToolTipManager.sharedInstance().setDismissDelay(30000);
        // timer used when mouse move to another component, show tooltip immediately if timer is not expired
        ToolTipManager.sharedInstance().setReshowDelay(1);

        UIManager.put("ToolTip.background", new Color(255, 255, 225));
        UIManager.put("ToolTip.backgroundInactive", new Color(255, 255, 225));
        UIManager.put("ToolTip.foreground", Color.BLACK);
        UIManager.put("ToolTip.foregroundInactive", Color.BLACK);
        UIManager.put("ToolTip.font", FONT_SEGOE);
        
        // Custom button
        // Change border of button in default Save as, Confirm dialogs
        UIManager.put("Button.border", BORDER_BLU);
        UIManager.put("Button.select", new Color(155, 193, 232));
        
        // Change border of button in Save as dialog
        UIManager.put("ToggleButton.border", BORDER_BLU);

        // No bold for menu + round corner
        UIManager.put("Menu.font", FONT_SEGOE);
        UIManager.put("Menu.selectionBackground", COLOR_SELECTION_BACKGROUND);
        UIManager.put("Menu.borderPainted", false);
        UIManager.put("PopupMenu.font", FONT_SEGOE);
        UIManager.put("RadioButtonMenuItem.selectionBackground", COLOR_SELECTION_BACKGROUND);
        UIManager.put("RadioButtonMenuItem.font", FONT_SEGOE);
        UIManager.put("RadioButtonMenuItem.borderPainted", false);
        UIManager.put("MenuItem.selectionBackground", COLOR_SELECTION_BACKGROUND);
        UIManager.put("MenuItem.font", FONT_SEGOE);
        UIManager.put("MenuItem.borderPainted", false);
        
        UIManager.put("CheckBoxMenuItem.selectionBackground", COLOR_SELECTION_BACKGROUND);
        UIManager.put("CheckBoxMenuItem.font", FONT_SEGOE);
        UIManager.put("CheckBoxMenuItem.borderPainted", false);
        UIManager.put("CheckBoxMenuItem.checkIcon", new CheckBoxIcon());

        // Custom tab
        UIManager.put("TabbedPane.font", FONT_SEGOE);
        // margin of current tab panel
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        // margin above tabs
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(3, 2, 0, 2));
        // margin around tab name
        UIManager.put("TabbedPane.tabInsets", new Insets(2, 3 + 5, 2, 3));
        // lighter unselected tab border
        UIManager.put("TabbedPane.darkShadow", new Color(190,198,205));
        
        UIManager.put("Button.font", FONT_SEGOE);
        UIManager.put("CheckBox.font", FONT_SEGOE);
        UIManager.put("TitledBorder.font", FONT_SEGOE);

        UIManager.put("Spinner.arrowButtonBorder", HelperUi.BORDER_BLU);
        UIManager.put("Spinner.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HelperUi.COLOR_BLU),
            BorderFactory.createMatteBorder(2,2,2,2, Color.WHITE)
        ));
        
        UIManager.put("FileChooser.listFont", FONT_SEGOE);
        UIManager.put("FileChooser.listViewBorder", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HelperUi.COLOR_BLU),
            BorderFactory.createMatteBorder(2,2,2,2, Color.WHITE)
        ));

        // Custom text component
        // Admin page
        UIManager.put("TextPane.selectionBackground", COLOR_SELECTION_BACKGROUND);
        UIManager.put("TextPane.font", FONT_UBUNTU);
        UIManager.put("TextPane.selectionBackground", COLOR_SELECTION_BACKGROUND);
        
        UIManager.put("TextField.border", HelperUi.BORDER_BLU);
        UIManager.put("TextField.selectionBackground", COLOR_SELECTION_BACKGROUND);
        
        UIManager.put("EditorPane.selectionBackground", COLOR_SELECTION_BACKGROUND);
        
        UIManager.put("TextArea.selectionBackground", COLOR_SELECTION_BACKGROUND);
        UIManager.put("TextArea.font", FONT_UBUNTU);

        // Custom Label
        UIManager.put("Label.font", FONT_SEGOE);
        UIManager.put("Label.selectionBackground", COLOR_SELECTION_BACKGROUND);

        // Custom table
        UIManager.put("Table.font", FONT_SEGOE);
        UIManager.put("TableHeader.font", FONT_SEGOE);
        UIManager.put("Table.selectionBackground", COLOR_SELECTION_BACKGROUND);
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
        UIManager.put("Tree.expandedIcon", new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/expanded.png")));
        UIManager.put("Tree.collapsedIcon", new ImageIcon(HelperUi.class.getResource("/com/jsql/view/swing/resources/images/icons/collapsed.png")));
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
        UIManager.put("ComboBox.font", FONT_SEGOE);
        UIManager.put("ComboBox.selectionBackground", COLOR_SELECTION_BACKGROUND);
        // Use ColorUIResource to preserve the background color for arrow
        UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
        UIManager.put("ComboBox.border", HelperUi.BORDER_BLU);
        UIManager.put("ComboBoxUI", CustomBasicComboBoxUI.class.getName());
        
        UIManager.put("swing.boldMetal", Boolean.FALSE);
    }

    /**
     * Icons for application window.
     * @return List of a 16x16 (default) and 32x32 icon (alt-tab, taskbar)
     */
    public static List<Image> getIcons() {
        List<Image> images = new ArrayList<>();
        try {
            images.add(ImageIO.read(HelperUi.URL_ICON_128));
            images.add(ImageIO.read(HelperUi.URL_ICON_96));
            images.add(ImageIO.read(HelperUi.URL_ICON_32));
            images.add(ImageIO.read(HelperUi.URL_ICON_16));
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
        return images;
    }
}
