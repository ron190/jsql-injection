/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.tab;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import com.jsql.util.reverse.ModelReverse;
import com.jsql.view.swing.action.ActionCloseTabResult;
import com.jsql.view.swing.action.HotkeyUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuText;
import com.jsql.view.swing.tab.dnd.DnDTabbedPane;
import com.jsql.view.swing.tab.dnd.TabTransferHandler;
import com.jsql.view.swing.table.PanelTable;
import com.jsql.view.swing.terminal.AbstractExploit;
import com.jsql.view.swing.terminal.ExploitReverseShell;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.RadioItemPreventClose;
import com.jsql.view.swing.util.UiStringUtil;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

/**
 * TabbedPane containing result injection panels.
 */
public class TabResults extends DnDTabbedPane {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static final String TAB_EXPLOIT_FAILURE_INCORRECT_URL = "Tab exploit failure: incorrect URL";
    public static final String RCE_SHELL = "RCE shell";
    public static final String SQL_SHELL = "sqlShell";
    public static final String WEB_SHELL = "webShell";
    public static final String REV_SHELL = "revShell";
    public static final String REVERSE_SHELL = "Reverse shell";

    /**
     * Create the panel containing injection results.
     */
    public TabResults() {
        this.setName("tabResults");
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.setTransferHandler(new TabTransferHandler());
        this.putClientProperty("JTabbedPane.tabClosable", true);
        this.putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) ActionCloseTabResult::perform);
        UIManager.put("TabbedPane.closeHoverForeground", LogLevelUtil.COLOR_RED);
        HotkeyUtil.addShortcut(this);  // Add hotkeys to root-pane ctrl-tab, ctrl-shift-tab, ctrl-w
        this.addMouseWheelListener(new TabbedPaneMouseWheelListener());
        MediatorHelper.register(this);
    }

    public void addFileTab(String label, String content, String path) {
        JTextArea fileText = new JPopupTextArea().getProxy();
        fileText.setText(content);
        fileText.setFont(new Font(UiUtil.FONT_NAME_MONO_NON_ASIAN, Font.PLAIN, 14));
        fileText.setCaretPosition(0);
        this.addTextTab(label, path, fileText, UiUtil.DOWNLOAD.getIcon());
        MediatorHelper.tabManagersCards().addToLists(path, label);
    }

    public void addReportTab(String content) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText("<html><span style=\"white-space: nowrap; font-family:'"+ UiUtil.FONT_NAME_MONO_NON_ASIAN +"'\">" + content + "</span></html>");
        editorPane.setFont(UIManager.getFont("TextArea.font"));  // required to increase text size
        editorPane.setDragEnabled(true);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);
        editorPane.getCaret().setBlinkRate(0);
        editorPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        editorPane.setComponentPopupMenu(new JPopupMenuText(editorPane));
        editorPane.addHyperlinkListener(linkEvent -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(linkEvent.getEventType())) {
                try {
                    Desktop.getDesktop().browse(linkEvent.getURL().toURI());
                } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Failing to browse Url", e);
                }
            }
        });
        editorPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                editorPane.getCaret().setVisible(true);
                editorPane.getCaret().setSelectionVisible(true);
                editorPane.getCaret().setBlinkRate(0);
            }
        });
        UiUtil.init(editorPane);  // silent delete

        this.addTextTab("Vulnerability report", "Analysis report with all payloads detected", editorPane, UiUtil.APP_ICON.getIcon());
    }

    public void addTextTab(String label, String toolTipText, JComponent componentText, FlatSVGIcon icon) {
        var scroller = new JScrollPane(componentText);
        this.addTab(label + StringUtils.SPACE, scroller);
        this.setSelectedComponent(scroller);  // Focus on the new tab
        this.setToolTipTextAt(this.indexOfComponent(scroller), toolTipText);
        var header = new TabHeader(label, icon);
        this.setTabComponentAt(this.indexOfComponent(scroller), header);

        this.updateUI();  // required: light, open/close prefs, dark => light artifacts
    }

    public void addTabExploitWeb(String url) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new AbstractExploit(terminalID, url, "web") {
                @Override
                public void action(String command, UUID terminalID, String urlShell, String... arg) {
                    MediatorHelper.model().getResourceAccess().runWebShell(command, terminalID, urlShell);
                }
            };
            terminal.setName(TabResults.WEB_SHELL);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JPanel panelTerminalWithReverse = this.getTerminalWithMenu(terminal);
            this.addTab("Web shell", panelTerminalWithReverse);
            this.setSelectedComponent(panelTerminalWithReverse);  // Focus on the new tab

            var header = new TabHeader("Web shell", UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(panelTerminalWithReverse), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }

    public void addTabExploitReverseShell(String port) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new ExploitReverseShell(terminalID, port);
            terminal.setName(TabResults.REV_SHELL);
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab(TabResults.REVERSE_SHELL, scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab

            var header = new TabHeader(TabResults.REVERSE_SHELL, UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (URISyntaxException | IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }

    public void addTabExploitRce(BiConsumer<String, UUID> biConsumerRunCmd) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new AbstractExploit(terminalID, null, "rce") {
                @Override
                public void action(String command, UUID terminalID, String urlShell, String... arg) {
                    biConsumerRunCmd.accept(command, terminalID);
                }
            };
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JPanel panelTerminalWithReverse = this.getTerminalWithMenu(terminal);
            this.addTab(TabResults.RCE_SHELL, panelTerminalWithReverse);
            this.setSelectedComponent(panelTerminalWithReverse);  // Focus on the new tab

            var header = new TabHeader(TabResults.RCE_SHELL, UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(panelTerminalWithReverse), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }

    public void addTabExploitSql(String url, String user, String pass) {
        try {
            var terminalID = UUID.randomUUID();
            var terminal = new AbstractExploit(terminalID, url, "sql") {
                @Override
                public void action(String cmd, UUID terminalID, String wbhPath, String... arg) {
                    MediatorHelper.model().getResourceAccess().runSqlShell(cmd, terminalID, wbhPath, arg[0], arg[1]);
                }
            };
            terminal.setName(TabResults.SQL_SHELL);
            terminal.setLoginPassword(new String[]{ user, pass });
            MediatorHelper.frame().getMapUuidShell().put(terminalID, terminal);

            JScrollPane scroller = new JScrollPane(terminal);
            this.addTab("SQL shell", scroller);
            this.setSelectedComponent(scroller);  // Focus on the new tab

            var header = new TabHeader("SQL shell", UiUtil.TERMINAL.getIcon());
            this.setTabComponentAt(this.indexOfComponent(scroller), header);
            terminal.requestFocusInWindow();

            this.updateUI();  // required: light, open/close prefs, dark => light artifacts
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, TabResults.TAB_EXPLOIT_FAILURE_INCORRECT_URL, e);
        }
    }
    
    public void addTabValues(String[][] data, String[] columnNames, AbstractElementDatabase table) {
        var panelTable = new PanelTable(data, columnNames);
        
        this.addTab(StringUtil.detectUtf8(table.toString()), panelTable);
        panelTable.setComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()));
        
        this.setSelectedComponent(panelTable);  // Focus on the new tab

        var header = new TabHeader(UiStringUtil.detectUtf8Html(table.toString()), UiUtil.TABLE_BOLD.getIcon());
        this.setTabComponentAt(this.indexOfComponent(panelTable), header);

        this.updateUI();  // required: light, open/close prefs, dark => light artifacts
    }

    private JPanel getTerminalWithMenu(AbstractExploit terminal) {
        JPanel panelTerminalWithReverse = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;  // both components always visible
            }
        };
        OverlayLayout overlay = new OverlayLayout(panelTerminalWithReverse);
        panelTerminalWithReverse.setLayout(overlay);

        var panelReverseMargin = new JPanel();
        panelReverseMargin.setLayout(new BoxLayout(panelReverseMargin, BoxLayout.LINE_AXIS));
        panelReverseMargin.setOpaque(false);
        panelReverseMargin.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

        var menuReverse = new JLabel(TabResults.REVERSE_SHELL, UiUtil.ARROW_DOWN.getIcon(), SwingConstants.LEFT);
        menuReverse.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                var popupMenu = TabResults.this.showMenu(terminal);
                popupMenu.updateUI();  // required: incorrect when dark/light mode switch
                popupMenu.show(e.getComponent(), e.getComponent().getX(),5 + e.getComponent().getY() + e.getComponent().getHeight());
                popupMenu.setLocation(e.getComponent().getLocationOnScreen().x,5 + e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight());
            }
        });
        menuReverse.setMaximumSize(menuReverse.getPreferredSize());
        JScrollPane scrollerTerminal = new JScrollPane(terminal);
        scrollerTerminal.setAlignmentX(1f);
        scrollerTerminal.setAlignmentY(0f);
        panelReverseMargin.setAlignmentX(1f);
        panelReverseMargin.setAlignmentY(0f);
        panelReverseMargin.add(menuReverse);
        panelTerminalWithReverse.add(panelReverseMargin);
        panelTerminalWithReverse.add(scrollerTerminal);

        return panelTerminalWithReverse;
    }

    private JPopupMenu showMenu(AbstractExploit terminal) {
        JPopupMenu menuReverse = new JPopupMenu();

        var menuListen = new JMenu("Listen");
        menuListen.setComponentOrientation(
            ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
            ? ComponentOrientation.LEFT_TO_RIGHT
            : ComponentOrientation.RIGHT_TO_LEFT
        );
        var panelPublicAddress = new JPanel(new BorderLayout());
        panelPublicAddress.add(new JLabel("<html><b>Your public address (listener) :</b></html>"));
        menuListen.add(panelPublicAddress);
        menuListen.add(new JSeparator());
        var address = new JTextFieldPlaceholder("Local IP/domain", "10.0.2.2");
        menuListen.add(address);
        var port = new JTextFieldPlaceholder("Local port", "4444");
        menuListen.add(port);

        var panelServerConnection = new JPanel(new BorderLayout());
        panelServerConnection.add(new JLabel("<html><b>Server method (connector) :</b></html>"));
        menuListen.add(panelServerConnection);
        menuListen.add(new JSeparator());
        var buttonGroup = new ButtonGroup();
        List<ModelReverse> commandsReverse = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getCommandsReverse();
        commandsReverse.forEach(modelReverse -> {
            var radio = new RadioItemPreventClose(modelReverse.getName());
            radio.setActionCommand(modelReverse.getName());
            radio.setSelected("bash".equals(modelReverse.getName()));
            buttonGroup.add(radio);
            menuListen.add(radio);
        });

        Runnable runnableReverse = () -> {
            try {
                Thread.sleep(2500);
                MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getCommandsReverse().stream()
                .filter(modelReverse -> modelReverse.getName().equals(buttonGroup.getSelection().getActionCommand()))
                .findFirst()
                .ifPresent(modelReverse -> MediatorHelper.model().getResourceAccess().runWebShell(
                    // TODO mysql UDF, pg Program/Extension/Archive, sqlite
                    String.format(modelReverse.getCommand(), address.getText(), port.getText()),
                    null,  // ignore connection response
                    terminal.getUrlShell(),
                    true
                ));
            } catch (InterruptedException e) {
                LOGGER.log(LogLevelUtil.IGNORE, e, e);
                Thread.currentThread().interrupt();
            }
        };

        var panelOpenIn = new JPanel(new BorderLayout());
        panelOpenIn.add(new JLabel("<html><b>Open In :</b></html>"));
        menuListen.add(panelOpenIn);
        menuListen.add(new JSeparator());

        var menuBuiltInShell = new RadioItemPreventClose("Built-in shell", true);
        var menuExternalShell = new RadioItemPreventClose("External listening shell");
        var buttonTypeShell = new ButtonGroup();
        buttonTypeShell.add(menuBuiltInShell);
        buttonTypeShell.add(menuExternalShell);
        menuListen.add(menuBuiltInShell);
        menuListen.add(menuExternalShell);
        menuListen.add(new JSeparator());
        var panelCreate = new JPanel(new BorderLayout());
        panelCreate.add(new JButton(new AbstractAction("Create reverse shell") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuBuiltInShell.isSelected()) {
                    SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitReverseShell(port.getText()));
                }
                new Thread(runnableReverse).start();
                menuReverse.setVisible(false);
            }
        }));
        menuListen.add(panelCreate);

        var menuConnect = new JMenu("Connect");
        menuConnect.setComponentOrientation(
            ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getCurrentLocale()))
            ? ComponentOrientation.LEFT_TO_RIGHT
            : ComponentOrientation.RIGHT_TO_LEFT
        );
        var panelServerPublicAddress = new JPanel(new BorderLayout());
        panelServerPublicAddress.add(new JLabel("<html><b>Server public address (listener) :</b></html>"));
        menuConnect.add(panelServerPublicAddress);
        menuConnect.add(new JSeparator());
        menuConnect.add(new JTextFieldPlaceholder("Target IP/domain"));
        menuConnect.add(new JTextFieldPlaceholder("Target port"));
        menuConnect.add(new JSeparator());

        var panelServerListeningConnection = new JPanel(new BorderLayout());
        panelServerListeningConnection.add(new JLabel("<html><b>Server listening method :</b></html>"));
        menuConnect.add(panelServerListeningConnection);
        var buttonGroupListening = new ButtonGroup();
        Arrays.asList("netcat").forEach(method -> {
            var radio = new JRadioButtonMenuItem(method) {
                @Override
                protected void processMouseEvent(MouseEvent evt) {
                    if (evt.getID() == MouseEvent.MOUSE_RELEASED && this.contains(evt.getPoint())) {
                        this.doClick();
                        this.setArmed(true);
                    } else {
                        super.processMouseEvent(evt);
                    }
                }
            };
            radio.setSelected("netcat".equals(method));
            buttonGroupListening.add(radio);
            menuConnect.add(radio);
        });
        menuConnect.add(new JSeparator());
        menuConnect.add(new JMenuItem("Create"));

        menuReverse.add(menuListen);
        menuReverse.add(menuConnect);

        return menuReverse;
    }
}
