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
package com.jsql.view.swing.menubar;

import com.jsql.util.GitUtil.ShowOnConsole;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.action.ActionSaveTab;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.console.JTextPaneAppender;
import com.jsql.view.swing.dialog.DialogAbout;
import com.jsql.view.swing.dialog.DialogTranslate;
import com.jsql.view.swing.dialog.translate.Language;
import com.jsql.view.swing.panel.preferences.PanelExploit;
import com.jsql.view.swing.panel.preferences.PanelTampering;
import com.jsql.view.swing.sql.SqlEngine;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JToolTipI18n;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.ModelSvgIcon;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Application main menubar.
 */
public class AppMenubar extends JMenuBar {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final MenuWindows menuWindows;

    protected static final List<ModelItemTranslate> ITEMS_TRANSLATE = Stream.of(
        Language.EN, Language.FR, Language.ES, Language.IT, Language.AR, Language.ZH, Language.RU, Language.TR, Language.KO,
        Language.SE, Language.FI, Language.CS, Language.PT, Language.PL, Language.ID, Language.NL, Language.RO, Language.DE
    ).map(ModelItemTranslate::new).toList();

    private static final List<ModelItemTranslate> ITEMS_TRANSLATE_INTO = Stream.of(
        Language.FR, Language.ES, Language.SE, Language.FI, Language.TR, Language.CS, Language.RO, Language.IT, Language.PT, Language.AR,
        Language.PL, Language.RU, Language.ZH, Language.DE, Language.ID, Language.JA, Language.KO, Language.HI, Language.NL, Language.TA
    ).map(ModelItemTranslate::new).toList();

    /**
     * Create a menubar on main frame.
     */
    public AppMenubar() {
        this.add(this.initMenuFile());
        this.add(this.initMenuEdit());
        this.add(this.initMenuCommunity());
        this.menuWindows = new MenuWindows(this);
        this.add(this.menuWindows);
        this.add(this.initMenuHelp());
    }

    private JMenu initMenuFile() {
        var menuFile = new JMenu(I18nUtil.valueByKey("MENUBAR_FILE"));
        I18nViewUtil.addComponentForKey("MENUBAR_FILE", menuFile);
        menuFile.setMnemonic('F');

        JMenuItem itemSave = new JMenuItem(new ActionSaveTab());
        I18nViewUtil.addComponentForKey("MENUBAR_FILE_SAVETABAS", itemSave);

        JMenuItem itemExit = new JMenuItem(I18nUtil.valueByKey("MENUBAR_FILE_EXIT"), 'x');
        I18nViewUtil.addComponentForKey("MENUBAR_FILE_EXIT", itemExit);
        itemExit.addActionListener(actionEvent -> MediatorHelper.frame().dispose());

        HotkeyUtil.addShortcut(this);

        menuFile.add(itemSave);
        menuFile.add(new JSeparator());
        menuFile.add(itemExit);
        return menuFile;
    }

    private JMenu initMenuEdit() {
        var menuEdit = new JMenu(I18nUtil.valueByKey("MENUBAR_EDIT"));
        I18nViewUtil.addComponentForKey("MENUBAR_EDIT", menuEdit);
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItem(I18nUtil.valueByKey("CONTEXT_MENU_COPY"), 'C');
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_COPY", itemCopy);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        itemCopy.addActionListener(actionEvent -> {
            if (MediatorHelper.tabResults().getSelectedComponent() instanceof PanelTable panelTable) {
                panelTable.copyTable();
            } else if (MediatorHelper.tabResults().getSelectedComponent() instanceof JScrollPane jScrollPane) {
                ((JTextComponent) jScrollPane.getViewport().getView()).copy();
            }
        });

        JMenuItem itemSelectAll = new JMenuItem(I18nUtil.valueByKey("CONTEXT_MENU_SELECT_ALL"), 'A');
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_SELECT_ALL", itemSelectAll);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        itemSelectAll.addActionListener(actionEvent -> {
            if (MediatorHelper.tabResults().getSelectedComponent() instanceof PanelTable panelTable) {
                panelTable.selectTable();
            } else if (MediatorHelper.tabResults().getSelectedComponent() instanceof JScrollPane jScrollPane) {
                // Textarea need focus to select all
                jScrollPane.getViewport().getView().requestFocusInWindow();
                ((JTextComponent) jScrollPane.getViewport().getView()).selectAll();
            }
        });

        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);
        return menuEdit;
    }

    private JMenu initMenuCommunity() {
        var menuCommunity = new JMenu(I18nUtil.valueByKey("MENUBAR_COMMUNITY"));
        menuCommunity.setMnemonic('C');
        menuCommunity.setName("menuCommunity");
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY", menuCommunity);

        JMenu menuI18nContribution = this.initMenuI18nContribution();
        menuI18nContribution.setName("menuI18nContribution");

        JMenuItem itemReportIssue = this.initItemReportIssue();
        itemReportIssue.setName("itemReportIssue");

        menuCommunity.add(menuI18nContribution);
        menuCommunity.add(new JSeparator());
        menuCommunity.add(itemReportIssue);
        return menuCommunity;
    }

    private JMenu initMenuI18nContribution() {
        var menuI18nContribution = new JMenu(I18nUtil.valueByKey("MENUBAR_COMMUNITY_HELPTRANSLATE"));
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_HELPTRANSLATE", menuI18nContribution);

        final var dialogTranslate = new DialogTranslate();  // Render the About dialog behind the scene
        AppMenubar.ITEMS_TRANSLATE_INTO.forEach(model -> {
            model.setMenuItem(new JMenuItem(model.getLanguage().getMenuItemLabel(), model.getLanguage().getFlag()));
            model.getMenuItem().addActionListener(new ActionTranslate(dialogTranslate, model.getLanguage()));
            menuI18nContribution.add(model.getMenuItem());
        });

        var itemIntoOther = new JMenuItem(I18nUtil.valueByKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE"));
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE", itemIntoOther);

        AppMenubar.ITEMS_TRANSLATE_INTO.stream().filter(model -> model.getLanguage() == Language.AR)
        .forEach(modelItemTranslate -> modelItemTranslate.getMenuItem().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT));
        AppMenubar.ITEMS_TRANSLATE_INTO.stream().filter(model -> model.getLanguage() == Language.FR)
        .forEach(modelItemTranslate -> modelItemTranslate.getMenuItem().setName("itemIntoFrench"));

        menuI18nContribution.add(new JSeparator());
        menuI18nContribution.add(itemIntoOther);
        itemIntoOther.addActionListener(new ActionTranslate(dialogTranslate, Language.OT));

        return menuI18nContribution;
    }

    private JMenuItem initItemReportIssue() {
        JMenuItem itemReportIssue = new JMenuItem(I18nUtil.valueByKey("MENUBAR_COMMUNITY_REPORTISSUE"), 'R');
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_REPORTISSUE", itemReportIssue);

        String reportBody = """
            ## What's the expected behavior?
            
            ## What's the actual behavior?
            
            ## Any other detailed information on the Issue?
            
            ## Steps to reproduce the problem
            
              1. ...
              2. ...
            
            ## [Community] Request for new feature
            
            """;
        itemReportIssue.addActionListener(actionEvent -> {
            var panel = new JPanel(new BorderLayout());
            final JTextArea textarea = new JPopupTextArea(new JTextArea()).getProxy();
            textarea.setFont(new Font(
                UiUtil.FONT_NAME_MONOSPACED,
                Font.PLAIN,
                UIManager.getDefaults().getFont("TextField.font").getSize()
            ));
            textarea.setText(reportBody);
            panel.add(new JLabel("Describe your bug or issue:"), BorderLayout.NORTH);
            panel.add(new JScrollPane(textarea));
            panel.setPreferredSize(new Dimension(500, 350));
            panel.setMinimumSize(new Dimension(500, 350));

            textarea.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    textarea.requestFocusInWindow();
                }
            });

            int result = JOptionPane.showOptionDialog(
                MediatorHelper.frame(),
                panel,
                "Report an issue or a bug",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] {
                    "Send",
                    I18nUtil.valueByKey("LIST_ADD_VALUE_CANCEL")
                },
                I18nUtil.valueByKey("LIST_ADD_VALUE_CANCEL")
            );

            if (StringUtils.isNotEmpty(textarea.getText()) && result == JOptionPane.YES_OPTION) {
                MediatorHelper.model().getMediatorUtils().getGitUtil().sendReport(textarea.getText(), ShowOnConsole.YES, "Report");
            }
        });

        return itemReportIssue;
    }

    private JMenu initMenuHelp() {
        var menuHelp = new JMenu(I18nUtil.valueByKey("MENUBAR_HELP"));
        menuHelp.setMnemonic('H');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP", menuHelp);
        menuHelp.setName("menuHelp");

        JMenuItem itemHelp = new JMenuItem(I18nUtil.valueByKey("MENUBAR_HELP_ABOUT"), 'A');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP_ABOUT", itemHelp);
        itemHelp.setName("itemHelp");

        JMenuItem itemUpdate = new JMenuItem(I18nUtil.valueByKey("MENUBAR_HELP_UPDATE"), 'U');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP_UPDATE", itemUpdate);

        // Render the About dialog behind the scene
        itemHelp.addActionListener(actionEvent -> {
            final var dialogAbout = new DialogAbout();
            if (!dialogAbout.isVisible()) {
                dialogAbout.initDialog();
                dialogAbout.setVisible(true);  // needed here for button focus
                dialogAbout.requestButtonFocus();
            }
            dialogAbout.setVisible(true);
        });
        itemUpdate.addActionListener(new ActionCheckUpdate());

        menuHelp.add(itemUpdate);
        menuHelp.add(new JSeparator());
        menuHelp.add(itemHelp);
        return menuHelp;
    }

    public static void applyTheme(String nameTheme) {
        UiUtil.applyTheme(nameTheme);

        for (String key : I18nViewUtil.keys()) {
            for (Object component : I18nViewUtil.componentsByKey(key)) {
                if (component instanceof JToolTipI18n jToolTipI18n) {
                    jToolTipI18n.updateUI();  // required
                }
            }
        }

        Arrays.asList(
            UiUtil.DATABASE_BOLD, UiUtil.ADMIN, UiUtil.DOWNLOAD, UiUtil.TERMINAL, UiUtil.UPLOAD, UiUtil.LOCK, UiUtil.TEXTFIELD, UiUtil.BATCH,
            UiUtil.TABLE_LINEAR, UiUtil.TABLE_BOLD, UiUtil.NETWORK, UiUtil.DATABASE_LINEAR, UiUtil.COG, UiUtil.CUP, UiUtil.CONSOLE, UiUtil.BINARY, UiUtil.CHUNK,
            UiUtil.ARROW, UiUtil.ARROW_HOVER, UiUtil.ARROW_PRESSED, UiUtil.ARROW_LEFT, UiUtil.ARROW_LEFT_HOVER, UiUtil.ARROW_LEFT_PRESSED,
            UiUtil.EXPAND, UiUtil.EXPAND_HOVER, UiUtil.EXPAND_PRESSED,
            UiUtil.HOURGLASS, UiUtil.ARROW_DOWN, UiUtil.ARROW_UP, UiUtil.SQUARE, UiUtil.GLOBE, UiUtil.TICK_GREEN, UiUtil.CROSS_RED,
            UiUtil.APP_ICON, UiUtil.APP_BIG, UiUtil.APP_MIDDLE
        ).forEach(ModelSvgIcon::setColorFilter);

        SqlEngine.applyTheme();
        PanelTampering.applyTheme();
        PanelExploit.applyTheme();
        MediatorHelper.panelConsoles().getTabbedPaneNetworkTab().applyTheme();
        MediatorHelper.frame().setIconImages(UiUtil.getIcons());
        MediatorHelper.frame().revalidate();

        MediatorHelper.model().getMediatorUtils().getPreferencesUtil().withThemeFlatLafName(nameTheme).persist();
    }
    
    public void switchLocale(Locale newLocale) {
        Locale oldLocale = I18nUtil.getCurrentLocale();
        I18nUtil.setCurrentBundle(newLocale);

        Stream.of(
            JTextPaneAppender.ATTRIBUTE_WARN,
            JTextPaneAppender.ATTRIBUTE_INFORM,
            JTextPaneAppender.ATTRIBUTE_SUCCESS,
            JTextPaneAppender.ATTRIBUTE_ALL
        )
        .forEach(attribute -> {
            StyleConstants.setFontFamily(attribute, I18nViewUtil.isNonUbuntu(newLocale) ? UiUtil.FONT_NAME_MONO_ASIAN : UiUtil.FONT_NAME_MONO_NON_ASIAN);
            StyleConstants.setFontSize(attribute, I18nViewUtil.isNonUbuntu(newLocale) ? UiUtil.FONT_SIZE_MONO_ASIAN : UiUtil.FONT_SIZE_MONO_NON_ASIAN);
        });

        MediatorHelper.managerBruteForce().getResult().setFont(I18nViewUtil.isNonUbuntu(newLocale) ? UiUtil.FONT_MONO_ASIAN : UiUtil.FONT_MONO_NON_ASIAN);

        this.switchNetworkTable();
        I18nViewUtil.switchI18nComponents();
        this.switchOrientation(oldLocale, newLocale);
        this.switchMenuItems();  // required to restore proper language orientation
        
        MediatorHelper.treeDatabase().reloadNodes();
        MediatorHelper.panelAddressBar().getPanelTrailingAddress().buttonStart.setIcons();
        int textPosition = ComponentOrientation.getOrientation(newLocale).isLeftToRight()  // leading/trailing not working
            ? SwingConstants.LEFT
            : SwingConstants.RIGHT;
        MediatorHelper.panelAddressBar().getAtomicRadioRequest().setHorizontalTextPosition(textPosition);  // component orientation not working
        MediatorHelper.panelAddressBar().getAtomicRadioMethod().setHorizontalTextPosition(textPosition);
        MediatorHelper.panelAddressBar().getAtomicRadioHeader().setHorizontalTextPosition(textPosition);

        // Fix #92981: IllegalArgumentException on revalidate()
        // Fix #96185: NullPointerException on revalidate()
        // Fix #96226: ArrayIndexOutOfBoundsException on revalidate()
        try {
            MediatorHelper.frame().revalidate();  // Fix glitches on Linux
        } catch (NullPointerException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    private void switchOrientation(Locale oldLocale, Locale newLocale) {
        var componentOrientation = ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale());
        MediatorHelper.frame().applyComponentOrientation(componentOrientation);
        
        if (!ComponentOrientation.getOrientation(oldLocale).equals(ComponentOrientation.getOrientation(newLocale))) {
            // not rendered at startup, only on switch, unreliable components width can be 0 at startup
            // use event windowOpen instead when required
            AppMenubar.reverse(MediatorHelper.frame().getSplitNS().getSplitEW());
            AppMenubar.reverse(MediatorHelper.panelConsoles().getNetworkSplitPane());
        }
        
        MediatorHelper.tabResults().setComponentOrientation(ComponentOrientation.getOrientation(newLocale));
    }

    private static void reverse(JSplitPane splitPane) {
        var componentLeft = splitPane.getLeftComponent();
        var componentRight = splitPane.getRightComponent();

        // Reset components
        splitPane.setLeftComponent(null);
        splitPane.setRightComponent(null);
        splitPane.setLeftComponent(componentRight);
        splitPane.setRightComponent(componentLeft);

        // required as switch to arabic uses reversed location
        splitPane.setDividerLocation(splitPane.getWidth() - splitPane.getDividerLocation());
    }

    private void switchMenuItems() {
        Stream.concat(AppMenubar.ITEMS_TRANSLATE.stream(), AppMenubar.ITEMS_TRANSLATE_INTO.stream())
        .forEach(model -> model.getMenuItem().setComponentOrientation(
            model.getLanguage().isRightToLeft()
            ? ComponentOrientation.RIGHT_TO_LEFT
            : ComponentOrientation.LEFT_TO_RIGHT
        ));
    }

    private void switchNetworkTable() {
        JTableHeader header = MediatorHelper.panelConsoles().getNetworkTable().getTableHeader();
        TableColumnModel columnModel = header.getColumnModel();
        columnModel.getColumn(0).setHeaderValue(I18nUtil.valueByKey("NETWORK_TAB_URL_COLUMN"));
        columnModel.getColumn(1).setHeaderValue(I18nUtil.valueByKey("NETWORK_TAB_SIZE_COLUMN") +" (KB)");
        columnModel.getColumn(2).setHeaderValue(I18nUtil.valueByKey("SQLENGINE_STRATEGY"));
        columnModel.getColumn(3).setHeaderValue(I18nUtil.valueByKey("SQLENGINE_METADATA"));
        header.repaint();
    }
    
    
    // Getter and setter

    public JMenu getMenuView() {
        return this.menuWindows.getMenuView();
    }

    public MenuWindows getMenuWindows() {
        return this.menuWindows;
    }
}
