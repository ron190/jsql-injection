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
import com.jsql.view.swing.panel.preferences.PanelTampering;
import com.jsql.view.swing.sql.SqlEngine;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.text.JPopupTextArea;
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
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Application main menubar.
 */
public class AppMenubar extends JMenuBar {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final MenuWindows menuWindows;

    protected static final List<ModelItemTranslate> MODELS_ITEM = Stream.of(
        Language.EN, Language.FR, Language.RU, Language.ZH, Language.ES, Language.TR, Language.KO, Language.SE, Language.FI,
        Language.AR, Language.CS, Language.IT, Language.PT, Language.PL, Language.IN, Language.NL, Language.RO, Language.DE
    ).map(ModelItemTranslate::new).collect(Collectors.toList());

    private static final List<ModelItemTranslate> MODELS_ITEM_INTO = Stream.of(
        Language.FR, Language.ES, Language.SE, Language.FI, Language.TR, Language.CS, Language.RO, Language.IT, Language.PT, Language.AR,
        Language.PL, Language.RU, Language.ZH, Language.DE, Language.IN, Language.JA, Language.KO, Language.HI, Language.NL, Language.TA
    ).map(ModelItemTranslate::new).collect(Collectors.toList());

    /**
     * Create a menubar on main frame.
     */
    public AppMenubar() {
        this.add(this.initializeMenuFile());
        this.add(this.initializeMenuEdit());
        this.add(this.initializeMenuCommunity());
        this.menuWindows = new MenuWindows(this);
        this.add(this.menuWindows);
        this.add(this.initializeMenuHelp());
    }

    private JMenu initializeMenuFile() {
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

    private JMenu initializeMenuEdit() {
        var menuEdit = new JMenu(I18nUtil.valueByKey("MENUBAR_EDIT"));
        I18nViewUtil.addComponentForKey("MENUBAR_EDIT", menuEdit);
        menuEdit.setMnemonic('E');

        JMenuItem itemCopy = new JMenuItem(I18nUtil.valueByKey("CONTEXT_MENU_COPY"), 'C');
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_COPY", itemCopy);
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        itemCopy.addActionListener(actionEvent -> {
            if (MediatorHelper.tabResults().getSelectedComponent() instanceof PanelTable) {
                ((PanelTable) MediatorHelper.tabResults().getSelectedComponent()).copyTable();
            } else if (MediatorHelper.tabResults().getSelectedComponent() instanceof JScrollPane) {
                ((JTextArea) ((JScrollPane) MediatorHelper.tabResults().getSelectedComponent()).getViewport().getView()).copy();
            }
        });

        JMenuItem itemSelectAll = new JMenuItem(I18nUtil.valueByKey("CONTEXT_MENU_SELECT_ALL"), 'A');
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_SELECT_ALL", itemSelectAll);
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        itemSelectAll.addActionListener(actionEvent -> {
            if (MediatorHelper.tabResults().getSelectedComponent() instanceof PanelTable) {
                ((PanelTable) MediatorHelper.tabResults().getSelectedComponent()).selectTable();
            } else if (MediatorHelper.tabResults().getSelectedComponent() instanceof JScrollPane) {
                // Textarea need focus to select all
                ((JScrollPane) MediatorHelper.tabResults().getSelectedComponent()).getViewport().getView().requestFocusInWindow();
                ((JTextArea) ((JScrollPane) MediatorHelper.tabResults().getSelectedComponent()).getViewport().getView()).selectAll();
            }
        });

        menuEdit.add(itemCopy);
        menuEdit.add(new JSeparator());
        menuEdit.add(itemSelectAll);
        return menuEdit;
    }

    private JMenu initializeMenuCommunity() {
        var menuCommunity = new JMenu(I18nUtil.valueByKey("MENUBAR_COMMUNITY"));
        menuCommunity.setMnemonic('C');
        menuCommunity.setName("menuCommunity");
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY", menuCommunity);

        JMenu menuI18nContribution = this.initializeMenuI18nContribution();
        menuI18nContribution.setName("menuI18nContribution");

        JMenuItem itemReportIssue = this.initializeItemReportIssue();
        itemReportIssue.setName("itemReportIssue");

        menuCommunity.add(menuI18nContribution);
        menuCommunity.add(new JSeparator());
        menuCommunity.add(itemReportIssue);
        return menuCommunity;
    }

    private JMenu initializeMenuI18nContribution() {
        var menuI18nContribution = new JMenu(I18nUtil.valueByKey("MENUBAR_COMMUNITY_HELPTRANSLATE"));
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_HELPTRANSLATE", menuI18nContribution);

        final var dialogTranslate = new DialogTranslate();  // Render the About dialog behind scene
        AppMenubar.MODELS_ITEM_INTO.forEach(model -> {
            model.setMenuItem(new JMenuItem(model.getLanguage().getMenuItemLabel(), model.getLanguage().getFlag()));
            model.getMenuItem().addActionListener(new ActionTranslate(dialogTranslate, model.getLanguage()));
            menuI18nContribution.add(model.getMenuItem());
        });

        var itemIntoOther = new JMenuItem(I18nUtil.valueByKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE"));
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_ANOTHERLANGUAGE", itemIntoOther);

        AppMenubar.MODELS_ITEM_INTO.stream().filter(model -> model.getLanguage() == Language.AR)
        .forEach(modelItemTranslate -> modelItemTranslate.getMenuItem().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT));
        AppMenubar.MODELS_ITEM_INTO.stream().filter(model -> model.getLanguage() == Language.FR)
        .forEach(modelItemTranslate -> modelItemTranslate.getMenuItem().setName("itemIntoFrench"));

        menuI18nContribution.add(new JSeparator());
        menuI18nContribution.add(itemIntoOther);
        itemIntoOther.addActionListener(new ActionTranslate(dialogTranslate, Language.OT));

        return menuI18nContribution;
    }

    private JMenuItem initializeItemReportIssue() {
        JMenuItem itemReportIssue = new JMenuItem(I18nUtil.valueByKey("MENUBAR_COMMUNITY_REPORTISSUE"), 'R');
        I18nViewUtil.addComponentForKey("MENUBAR_COMMUNITY_REPORTISSUE", itemReportIssue);

        itemReportIssue.addActionListener(actionEvent -> {

            var panel = new JPanel(new BorderLayout());
            final JTextArea textarea = new JPopupTextArea(new JTextArea()).getProxy();
            textarea.setFont(new Font(
                UiUtil.FONT_NAME_MONOSPACED,
                Font.PLAIN,
                UIManager.getDefaults().getFont("TextField.font").getSize()
            ));
            textarea.setText(
                "## What's the expected behavior?\n\n"
                + "## What's the actual behavior?\n\n"
                + "## Any other detailed information on the Issue?\n\n"
                + "## Steps to reproduce the problem\n\n"
                + "  1. ...\n"
                + "  2. ...\n\n"
                + "## [Community] Request for new feature\n\n"
            );
            panel.add(new JLabel("Describe your bug or issue :"), BorderLayout.NORTH);
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
                    "Report",
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

    private JMenu initializeMenuHelp() {
        var menuHelp = new JMenu(I18nUtil.valueByKey("MENUBAR_HELP"));
        menuHelp.setMnemonic('H');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP", menuHelp);
        menuHelp.setName("menuHelp");

        JMenuItem itemHelp = new JMenuItem(I18nUtil.valueByKey("MENUBAR_HELP_ABOUT"), 'A');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP_ABOUT", itemHelp);
        itemHelp.setName("itemHelp");

        JMenuItem itemUpdate = new JMenuItem(I18nUtil.valueByKey("MENUBAR_HELP_UPDATE"), 'U');
        I18nViewUtil.addComponentForKey("MENUBAR_HELP_UPDATE", itemUpdate);

        // Render the About dialog behind scene
        itemHelp.addActionListener(actionEvent -> {
            final var dialogAbout = new DialogAbout();
            if (!dialogAbout.isVisible()) {
                dialogAbout.initializeDialog();
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

    public static void applyTheme(String theme) {
        UiUtil.applyTheme(theme);

        Arrays.asList(
            UiUtil.DATABASE_BOLD, UiUtil.ADMIN, UiUtil.DOWNLOAD, UiUtil.TERMINAL, UiUtil.UPLOAD, UiUtil.LOCK, UiUtil.TEXTFIELD, UiUtil.BATCH,
            UiUtil.TABLE_LINEAR, UiUtil.TABLE_BOLD, UiUtil.NETWORK, UiUtil.DATABASE_LINEAR, UiUtil.COG, UiUtil.CUP, UiUtil.CONSOLE, UiUtil.BINARY, UiUtil.CHUNK,
            UiUtil.ARROW, UiUtil.ARROW_HOVER, UiUtil.ARROW_PRESSED, UiUtil.EXPAND, UiUtil.EXPAND_HOVER, UiUtil.EXPAND_PRESSED,
            UiUtil.HOURGLASS, UiUtil.ARROW_DOWN, UiUtil.ARROW_UP, UiUtil.SQUARE, UiUtil.GLOBE, UiUtil.TICK_GREEN, UiUtil.CROSS_RED,
            UiUtil.APP_ICON, UiUtil.APP_BIG, UiUtil.APP_MIDDLE
        ).forEach(ModelSvgIcon::setColorFilter);

        SqlEngine.applyTheme();
        PanelTampering.applyTheme();
        MediatorHelper.panelConsoles().getTabbedPaneNetworkTab().applyTheme();
        MediatorHelper.frame().setIconImages(UiUtil.getIcons());
        MediatorHelper.frame().revalidate();

        MediatorHelper.model().getMediatorUtils().getPreferencesUtil().withThemeFlatLafName(theme).persist();
    }
    
    public void switchLocale(Locale newLocale) {
        this.switchLocale(I18nUtil.getCurrentLocale(), newLocale, false);
    }
    
    public void switchLocaleWithStatus(Locale oldLocale, Locale newLocale, boolean isStartup) {
        this.switchLocale(oldLocale, newLocale, isStartup);
        MediatorHelper.model().getPropertiesUtil().displayStatus(newLocale);
    }

    public void switchLocale(Locale oldLocale, Locale newLocale, boolean isStartup) {
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
        this.switchOrientation(oldLocale, newLocale, isStartup);
        this.switchMenuItems();  // required to restore proper language orientation
        
        MediatorHelper.treeDatabase().reloadNodes();

        // IllegalArgumentException #92981 on revalidate()
        try {
            MediatorHelper.frame().revalidate();  // Fix glitches on Linux
        } catch (IllegalArgumentException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    private void switchOrientation(Locale oldLocale, Locale newLocale, boolean isStartup) {
        var componentOrientation = ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale());
        MediatorHelper.frame().applyComponentOrientation(componentOrientation);
        
        if (!ComponentOrientation.getOrientation(oldLocale).equals(ComponentOrientation.getOrientation(newLocale))) {
            
            JSplitPane splitPaneLeftRight = MediatorHelper.frame().getSplitNS().getSplitEW();
            var componentLeft = splitPaneLeftRight.getLeftComponent();
            var componentRight = splitPaneLeftRight.getRightComponent();

            // Reset components
            splitPaneLeftRight.setLeftComponent(null);
            splitPaneLeftRight.setRightComponent(null);
            splitPaneLeftRight.setLeftComponent(componentRight);
            splitPaneLeftRight.setRightComponent(componentLeft);

            if (isStartup) {
                // TODO unclear and not working properly when starting as locale arabic
                splitPaneLeftRight.setDividerLocation(
                    splitPaneLeftRight.getDividerLocation()
                );
            } else {  // required as switch to arabic uses reversed location
                splitPaneLeftRight.setDividerLocation(
                    splitPaneLeftRight.getWidth() -
                    splitPaneLeftRight.getDividerLocation()
                );
            }
        }
        
        MediatorHelper.tabResults().setComponentOrientation(ComponentOrientation.getOrientation(newLocale));
    }

    private void switchMenuItems() {
        Stream.concat(AppMenubar.MODELS_ITEM.stream(), AppMenubar.MODELS_ITEM_INTO.stream())
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
