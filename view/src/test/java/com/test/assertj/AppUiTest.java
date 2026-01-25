package com.test.assertj;

import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.view.subscriber.Seal;
import com.jsql.util.bruter.ActionCoder;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.manager.ManagerAdminPage;
import com.jsql.view.swing.manager.ManagerCoder;
import com.jsql.view.swing.manager.ManagerDatabase;
import com.jsql.view.swing.manager.ManagerScan;
import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.menubar.MenuWindows;
import com.jsql.view.swing.panel.PanelAddressBar;
import com.jsql.view.swing.panel.PanelConsoles;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.panel.address.ButtonStart;
import com.jsql.view.swing.panel.address.PanelTrailingAddress;
import com.jsql.view.swing.panel.consoles.NetworkTable;
import com.jsql.view.swing.panel.preferences.PanelConnection;
import com.jsql.view.swing.panel.preferences.PanelInjection;
import com.jsql.view.swing.panel.split.SplitNS;
import com.jsql.view.swing.tab.TabManagers;
import com.jsql.view.swing.tab.TabResults;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.timing.Timeout;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

// public for javadoc
@SuppressWarnings("java:S5786")
public class AppUiTest {
    private static int counterLog = 0;
    private static FrameFixture window;
    private static final Connection connection = Mockito.mock(Connection.class);
    private static final Document document = Mockito.mock(Document.class);
    private static MockedStatic<Jsoup> utilities;
    private static Robot robot;

    @BeforeAll
    public static void setUpOnce() {
        AppUiTest.logMethod();
        FailOnThreadViolationRepaintManager.install();

        AppUiTest.robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock();  // fix linux instabilities

        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);
        // Prevent URL calls and random failure on shouldFindNetworkHeader
        MediatorHelper.model().getMediatorUtils().preferencesUtil()
            .withIsCheckingUpdate(false)
            .withIsShowNews(false);

        JFrameView frame = GuiActionRunner.execute(() -> {  // Static mock on current ThreadLocal
            AppUiTest.logMethod();
            if (AppUiTest.utilities != null) {
                AppUiTest.utilities.close();
            }

            AppUiTest.utilities = Mockito.mockStatic(Jsoup.class);

            AppUiTest.utilities.when(() -> Jsoup.connect(ArgumentMatchers.anyString())).thenReturn(AppUiTest.connection);
            AppUiTest.utilities.when(() -> Jsoup.clean(ArgumentMatchers.anyString(), ArgumentMatchers.any(Safelist.class))).thenReturn("cleaned");

            Mockito.when(AppUiTest.connection.ignoreContentType(ArgumentMatchers.anyBoolean())).thenReturn(AppUiTest.connection);
            Mockito.when(AppUiTest.connection.ignoreHttpErrors(ArgumentMatchers.anyBoolean())).thenReturn(AppUiTest.connection);

            AppUiTest.logMethod();
            Mockito.when(AppUiTest.connection.get()).thenReturn(AppUiTest.document);
            Mockito.when(AppUiTest.document.html()).thenReturn("<html><input/>test</html>");

            Mockito.when(AppUiTest.document.text()).thenReturn("<html><input/>test</html>");
            AppUiTest.utilities.when(() -> Jsoup.parse(Mockito.anyString())).thenReturn(AppUiTest.document);

            AppUiTest.logMethod();
            UiUtil.applyTheme(FlatDarkFlatIJTheme.class.getName());  // init but not enough, reapplied next

            AppUiTest.logMethod();
            var view = new JFrameView(injectionModel);
            SwingUtilities.invokeLater(() -> {
                AppMenubar.applyTheme(FlatDarkFlatIJTheme.class.getName());
                view.setVisible(true);
            });
            return view;
        });

        AppUiTest.window = new FrameFixture(AppUiTest.robot, frame);

        injectionModel.subscribe(frame.getSubscriberView());
        AppUiTest.logMethod();
    }

    @AfterAll  // when all test methods end, keeps class active
    public static void afterAll()  {
        AppUiTest.logMethod();
        AppUiTest.window.cleanUp();  // allow mvn retry
        AppUiTest.robot.cleanUp();
    }

    @Test
    public void shouldDnDList() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane(TabManagers.TAB_MANAGERS).selectTab("Admin page");

        Assertions.assertEquals("admin", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(0));
        Assertions.assertNotEquals("admin", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(1));

        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).drag(0);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).drop(1);

        Assertions.assertNotEquals("admin", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(0));
        Assertions.assertEquals("admin", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(1));

        AppUiTest.logMethod();
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).selectItem(0).pressKey(KeyEvent.VK_DELETE);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).selectItem(0);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_C);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).selectItem(1);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("admin", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(0));
        Assertions.assertEquals("admin", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(1));

        AppUiTest.logMethod();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("paste-from-clipboard");
        clipboard.setContents(stringSelection, null);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("paste-from-clipboard", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(1));

        AppUiTest.logMethod();
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_X);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_X);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).selectItem(3);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertNotEquals("paste-from-clipboard", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(1));
        Assertions.assertEquals("paste-from-clipboard", AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).valueAt(3));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldDnDScanList() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane(TabManagers.TAB_MANAGERS).selectTab("Batch scan");
        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", AppUiTest.window.list(ManagerScan.NAME).valueAt(0));
        Assertions.assertNotEquals("http://testphp.vulnweb.com/artists.php?artist=", AppUiTest.window.list(ManagerScan.NAME).valueAt(1));
        AppUiTest.window.list(ManagerScan.NAME).drag(0);
        AppUiTest.window.list(ManagerScan.NAME).drop(1);
        Assertions.assertNotEquals("http://testphp.vulnweb.com/artists.php?artist=", AppUiTest.window.list(ManagerScan.NAME).valueAt(0));
        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", AppUiTest.window.list(ManagerScan.NAME).valueAt(1));

        AppUiTest.logMethod();
        AppUiTest.window.list(ManagerScan.NAME).selectItem(0).pressKey(KeyEvent.VK_DELETE);
        AppUiTest.window.list(ManagerScan.NAME).selectItem(0);
        AppUiTest.window.list(ManagerScan.NAME).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C);
        AppUiTest.window.list(ManagerScan.NAME).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_C);
        AppUiTest.window.list(ManagerScan.NAME).selectItem(1);
        AppUiTest.window.list(ManagerScan.NAME).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list(ManagerScan.NAME).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", AppUiTest.window.list(ManagerScan.NAME).valueAt(0));
        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", AppUiTest.window.list(ManagerScan.NAME).valueAt(1));

        AppUiTest.logMethod();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("paste-from-clipboard");
        clipboard.setContents(stringSelection, null);
        AppUiTest.window.list(ManagerScan.NAME).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list(ManagerScan.NAME).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("paste-from-clipboard", AppUiTest.window.list(ManagerScan.NAME).valueAt(1));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldDnDTabs() {
        AppUiTest.logMethod();
        MediatorHelper.model().sendToViews(new Seal.CreateFileTab("dragfile", "content", "path"));
        MediatorHelper.model().sendToViews(new Seal.CreateFileTab("jumpfile", "content", "path"));
        MediatorHelper.model().sendToViews(new Seal.CreateFileTab("dropfile", "content", "path"));

        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).requireTitle("dragfile ", Index.atIndex(0));
        AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).requireTitle("jumpfile ", Index.atIndex(1));
        AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).requireTitle("dropfile ", Index.atIndex(2));

        AppUiTest.logMethod();
        AppUiTest.window.robot().pressMouse(
            GuiActionRunner.execute(() -> AppUiTest.window.label("dragfile").target()),
            GuiActionRunner.execute(() -> AppUiTest.window.label("dragfile").target()).getLocation()
        );
        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.label("dragfile").target()));  // required
        AppUiTest.window.label("dropfile").drop();

        try {
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).requireTitle("jumpfile ", Index.atIndex(0));
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).requireTitle("dragfile ", Index.atIndex(1));
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).requireTitle("dropfile ", Index.atIndex(2));
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.logMethod();
        GuiActionRunner.execute(() -> {
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0);
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0);
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0);
        });
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindFile() {
        AppUiTest.logMethod();
        MediatorHelper.model().sendToViews(new Seal.CreateFileTab("file", "content", "path"));

        try {
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).selectTab("file ").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.logMethod();
        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindWebshell() {
        AppUiTest.logMethod();
        MediatorHelper.model().sendToViews(new Seal.AddTabExploitWeb("http://webshell"));

        try {
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).selectTab("Web shell").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.logMethod();
        AppUiTest.window.textBox(TabResults.WEB_SHELL).pressAndReleaseKeys(
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
            KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN,
            KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_TAB,
            KeyEvent.VK_A, KeyEvent.VK_B,
            KeyEvent.VK_HOME, KeyEvent.VK_END,
            KeyEvent.VK_BACK_SPACE,
            KeyEvent.VK_BACK_SPACE,
            KeyEvent.VK_DELETE, KeyEvent.VK_DELETE,
            KeyEvent.VK_ENTER, KeyEvent.VK_ENTER, KeyEvent.VK_ENTER,
            KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_ENTER,
            KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN
        );

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindEngineAndErrorMethods() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelTrailingAddress.MENU_STRATEGY).click();
        AppUiTest.window.menuItem(PanelTrailingAddress.ITEM_RADIO_STRATEGY_ERROR).requireDisabled();

        MediatorHelper.model().sendToViews(new Seal.ActivateEngine(MediatorHelper.model().getMediatorEngine().getMysql()));
        MediatorHelper.model().sendToViews(
            new Seal.MarkStrategyVulnerable(0, MediatorHelper.model().getMediatorStrategy().getError())
        );

        AppUiTest.logMethod();
        AppUiTest.window.menuItem(PanelTrailingAddress.ITEM_RADIO_STRATEGY_ERROR).requireEnabled(Timeout.timeout(1000));

        Assertions.assertEquals(
            "MySQL",
            GuiActionRunner.execute(() -> AppUiTest.window.label(PanelTrailingAddress.MENU_VENDOR).target()).getText(),
            "Engine should be MySQL"
        );

        AppUiTest.logMethod();
        AppUiTest.window.label(PanelTrailingAddress.MENU_VENDOR).click();
        AppUiTest.window.menuItem(PanelTrailingAddress.ITEM_RADIO_VENDOR + "MySQL").requireVisible();

        AppUiTest.window.label(PanelTrailingAddress.MENU_STRATEGY).click();

        AppUiTest.window.robot().moveMouse(
            GuiActionRunner.execute(() -> AppUiTest.window.menuItem(PanelTrailingAddress.ITEM_RADIO_STRATEGY_ERROR)).target()  // faster than click
        );
        AppUiTest.logMethod();
        try {
            AppUiTest.window.menuItem(PanelTrailingAddress.PREFIX_NAME_ERROR + "Unsigned:or").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.window.menuItem(PanelTrailingAddress.ITEM_RADIO_STRATEGY_ERROR).requireEnabled();
        AppUiTest.window.menuItem(PanelTrailingAddress.PREFIX_NAME_ERROR + "Unsigned:or").click();
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindNetworkHeader() {
        AppUiTest.logMethod();

        MediatorHelper.model().sendToViews(new Seal.MessageHeader(
            "url",
            "post",
            new TreeMap<>(Map.of("key1","value1", "key2", "value2")),
            new TreeMap<>(Map.of("key1","value1", "key2", "value2")),
            "source",
            "1",
            "meta strategy",
            "meta process",
            null
        ));

        try {
            AppUiTest.window.label("CONSOLE_NETWORK_LABEL").click().requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.window.table(NetworkTable.NETWORK_TABLE).selectRows(0).requireContents(new String[][] { { "url", "1", "meta strategy", "meta process" } });

        AppUiTest.logMethod();
        AppUiTest.window.label("labelNETWORK_TAB_URL_LABEL").click();
        AppUiTest.window.textBox("textNETWORK_TAB_URL_LABEL").requireText("url");
        AppUiTest.window.label("labelNETWORK_TAB_RESPONSE_LABEL").click();
        AppUiTest.window.textBox("textNETWORK_TAB_RESPONSE_LABEL").requireText(Pattern.compile(".*key1: value1.*key2: value2.*", Pattern.DOTALL));
        AppUiTest.window.label("labelNETWORK_TAB_SOURCE_LABEL").click();
        AppUiTest.window.textBox("textNETWORK_TAB_SOURCE_LABEL").requireText("source");
        AppUiTest.window.label("labelNETWORK_TAB_PREVIEW_LABEL").click();
        AppUiTest.window.textBox("textNETWORK_TAB_PREVIEW_LABEL").requireText(Pattern.compile(".*<html>.*cleaned.*</html>.*", Pattern.DOTALL));
        AppUiTest.window.label("labelNETWORK_TAB_HEADERS_LABEL").click();
        AppUiTest.window.textBox("textNETWORK_TAB_HEADERS_LABEL").requireText(Pattern.compile(".*key1: value1.*key2: value2.*", Pattern.DOTALL));
        AppUiTest.window.label("labelNETWORK_TAB_PARAMS_LABEL").click();
        AppUiTest.window.textBox("textNETWORK_TAB_PARAMS_LABEL").requireText("post");
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindSqlshell() {
        AppUiTest.logMethod();
        MediatorHelper.model().sendToViews(
            new Seal.AddTabExploitSql("http://sqlshell", "username", "password")
        );

        try {
            AppUiTest.logMethod();
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).selectTab("SQL shell").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldRunCoder() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane(TabManagers.TAB_MANAGERS).selectTab("Encoding");
        AppUiTest.window.label(ManagerCoder.MENU_METHOD_MANAGER_CODER).click();

        AppUiTest.window.textBox(ManagerCoder.TEXT_INPUT_MANAGER_CODER).setText("a");

        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("Base16").target()));
        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("encodeToBase16").target()));
        AppUiTest.window.textBox(ManagerCoder.RESULT_MANAGER_CODER).requireText(Pattern.compile("61", Pattern.DOTALL));

        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("Base32").target()));
        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("encodeToBase32").target()));
        AppUiTest.window.textBox(ManagerCoder.RESULT_MANAGER_CODER).requireText(Pattern.compile("ME======", Pattern.DOTALL));

        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("Base58").target()));
        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("encodeToBase58").target()));
        AppUiTest.window.textBox(ManagerCoder.RESULT_MANAGER_CODER).requireText(Pattern.compile("2g", Pattern.DOTALL));

        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("Base64").target()));
        AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("encodeToBase64").target()));
        AppUiTest.window.textBox(ManagerCoder.RESULT_MANAGER_CODER).requireText(Pattern.compile("YQ==", Pattern.DOTALL));

        AppUiTest.logMethod();
        try {
            AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("Hash").target()));
        } catch (Exception e) {
            Assertions.fail();
        }

        ActionCoder.getHashes().forEach(hash -> {
            String result = null;
            try {
                result = ActionCoder.forName(hash).orElseThrow().run("a");
            } catch (NoSuchAlgorithmException | NoSuchElementException | IOException e) {
                Assertions.fail();
            }

            AppUiTest.window.robot().moveMouse(GuiActionRunner.execute(() -> AppUiTest.window.menuItem("hashTo"+ hash).target()));
            AppUiTest.window.textBox(ManagerCoder.RESULT_MANAGER_CODER).requireText(result);
        });
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindAdminpage() throws IOException {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane(TabManagers.TAB_MANAGERS).selectTab("Admin page");
        AppUiTest.window.list(ManagerAdminPage.LIST_MANAGER_ADMIN_PAGE).item(0).select().rightClick();

        MediatorHelper.model().sendToViews(new Seal.CreateAdminPageTab("http://adminpage"));

        AppUiTest.logMethod();
        try {
            AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).selectTab("adminpage ").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.verifyMockAdminPage();

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindDatabase() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane(TabManagers.TAB_MANAGERS).selectTab("Database");

        var nameDatabase = "database";
        Database database = new Database(nameDatabase, "1");

        MediatorHelper.model().sendToViews(new Seal.AddDatabases(List.of(database)));
        Assertions.assertEquals(nameDatabase +" (1 table)", AppUiTest.window.tree(ManagerDatabase.TREE_DATABASES).valueAt(0));

        var nameTable = "table";
        Table table = new Table(nameTable, "2", database);
        MediatorHelper.model().sendToViews(new Seal.AddTables(List.of(table)));
        Assertions.assertEquals(nameTable +" (2 rows)", AppUiTest.window.tree(ManagerDatabase.TREE_DATABASES).valueAt(1));

        AppUiTest.logMethod();
        var nameColumn0 = "column 0";
        var nameColumn1 = "column 1";
        Column column1 = new Column(nameColumn0, table);
        Column column2 = new Column(nameColumn1, table);
        MediatorHelper.model().sendToViews(new Seal.AddColumns(
            Arrays.asList(column1, column2)
        ));
        Assertions.assertEquals(nameColumn0, AppUiTest.window.tree(ManagerDatabase.TREE_DATABASES).valueAt(2));
        Assertions.assertEquals(nameColumn1, AppUiTest.window.tree(ManagerDatabase.TREE_DATABASES).valueAt(3));

        var arrayColumns = new String[] { Strings.EMPTY, Strings.EMPTY, nameColumn0, nameColumn1 };
        var tableDatas = new String[][] {
            { StringUtils.EMPTY, StringUtils.EMPTY, "[0, 0]", "[0, 1]" },
            { StringUtils.EMPTY, StringUtils.EMPTY, "[1, 0]", "[1, 1]" }
        };
        MediatorHelper.model().sendToViews(new Seal.CreateValuesTab(arrayColumns, tableDatas, table));

        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).selectTab(nameTable).requireVisible();
        AppUiTest.window.tree(ManagerDatabase.TREE_DATABASES).rightClickRow(0);
        AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).click();
        AppUiTest.window.tree(ManagerDatabase.TREE_DATABASES).rightClickRow(1);

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindOkButton() {
        AppUiTest.logMethod();
        try {
            AppUiTest.window.button(ButtonStart.BUTTON_IN_URL).click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void shouldFindConsoleButton() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelConsoles.BUTTON_SHOW_SOUTH).click();
        AppUiTest.window.label(SplitNS.BUTTON_SHOW_CONSOLES_HIDDEN).click();
        AppUiTest.window.label(PanelConsoles.BUTTON_SHOW_NORTH).click();

        try {
            AppUiTest.window.label(PanelConsoles.BUTTON_SHOW_SOUTH).click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void shouldFindConnectionPreferences() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        AppUiTest.window.menuItemWithPath("Windows", "Preferences").click();
        AppUiTest.window.list(PanelPreferences.LIST_CATEGORIES_PREFERENCE).selectItem(Pattern.compile(".*Connection.*"));

        AppUiTest.logMethod();
        Arrays.asList(  // init
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isFollowingRedirection()
        ).forEach(Assertions::assertFalse);

        AppUiTest.logMethod();
        AppUiTest.window.checkBox(PanelConnection.CHECKBOX_IS_FOLLOWING_REDIRECTION).click();
        Arrays.asList(  // check
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isFollowingRedirection()
        ).forEach(Assertions::assertTrue);

        AppUiTest.logMethod();
        AppUiTest.window.checkBox(PanelConnection.CHECKBOX_IS_FOLLOWING_REDIRECTION).click();
        Arrays.asList(  // uncheck
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isFollowingRedirection()
        ).forEach(Assertions::assertFalse);

        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindInjectionPreferences() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        AppUiTest.window.menuItemWithPath("Windows", "Preferences").click();
        AppUiTest.window.list(PanelPreferences.LIST_CATEGORIES_PREFERENCE).selectItem(Pattern.compile(".*Injection.*"));

        AppUiTest.logMethod();
        Arrays.asList(
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isParsingForm()
        ).forEach(Assertions::assertFalse);

        AppUiTest.logMethod();
        AppUiTest.window.checkBox(PanelInjection.CHECKBOX_IS_PARSING_FORM).click();
        Arrays.asList(
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isParsingForm()
        ).forEach(Assertions::assertTrue);

        AppUiTest.logMethod();
        AppUiTest.window.checkBox(PanelInjection.CHECKBOX_IS_PARSING_FORM).click();
        Arrays.asList(
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isParsingForm()
        ).forEach(Assertions::assertFalse);

        AppUiTest.logMethod();
        AppUiTest.window.radioButton(PanelInjection.RADIO_IS_ZIP_STRATEGY).check();
        Assertions.assertArrayEquals(new boolean[] {
            false,
            false,
            true,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isDiosStrategy(),
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isZipStrategy(),
        });

        AppUiTest.logMethod();
        AppUiTest.window.radioButton(PanelInjection.RADIO_IS_DIOS_STRATEGY).check();
        Assertions.assertArrayEquals(new boolean[] {
            false,
            true,
            false,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isDiosStrategy(),
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isZipStrategy(),
        });

        AppUiTest.logMethod();
        AppUiTest.window.radioButton(PanelInjection.RADIO_IS_DEFAULT_STRATEGY).check();
        Assertions.assertArrayEquals(new boolean[] {
            true,
            false,
            false,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isDiosStrategy(),
            MediatorHelper.model().getMediatorUtils().preferencesUtil().isZipStrategy(),
        });

        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    public void shouldFindSqlEngine() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        AppUiTest.window.menuItemWithPath("Windows", "SQL Engine").click();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        } catch (Exception e) {
            Assertions.fail();
        }
        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane(TabResults.TAB_RESULTS).target().removeTabAt(0));
    }

    @Test
    public void shouldFindLanguage() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        AppUiTest.window.menuItem(MenuWindows.MENU_WINDOWS).click();
        AppUiTest.window.robot().moveMouse(
            GuiActionRunner.execute(() -> AppUiTest.window.menuItem(MenuWindows.MENU_TRANSLATION).target())  // faster than click
        );
        AppUiTest.window.menuItem(MenuWindows.ITEM_RUSSIAN).click();
        AppUiTest.window.menuItem(MenuWindows.ITEM_ENGLISH).click();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

//    Unstable?
//    @Test
//    public void shouldFindReportIssue() {
//
//        window.label(PanelAddressBar.ADVANCED_BUTTON).click();
//        window.menuItem("menuCommunity").click();
//        window.menuItem("itemReportIssue").click();
//
//        DialogFixture dialog = window.dialog();
//        dialog.button(JButtonMatcher.withText("Cancel")).click();
//
//        try {
//            window.label(PanelAddressBar.ADVANCED_BUTTON).click();
//        } catch (Exception e) {
//            Assertions.fail();
//        }
//    }

    @Test
    public void shouldFindIHelpTranslate() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        AppUiTest.window.menuItem(AppMenubar.MENU_COMMUNITY).click();
        AppUiTest.window.robot().moveMouse(
            GuiActionRunner.execute(() -> AppUiTest.window.menuItem(AppMenubar.MENU_I18N_CONTRIBUTION).target())  // faster than click
        );
        AppUiTest.window.menuItem(AppMenubar.ITEM_INTO_FRENCH).click();

        AppUiTest.logMethod();
        DialogFixture dialog = AppUiTest.window.dialog();
        dialog.close();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void shouldFindAbout() {
        AppUiTest.logMethod();
        AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        AppUiTest.window.menuItem(AppMenubar.MENU_HELP).click();
        AppUiTest.window.menuItem(AppMenubar.ITEM_HELP).click();

        DialogFixture dialog = AppUiTest.window.dialog();
        dialog.button(JButtonMatcher.withText("Close")).click();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label(PanelAddressBar.ADVANCED_BUTTON).click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    private static void verifyMockAdminPage() throws IOException {
        Mockito.verify(AppUiTest.document, Mockito.times(1)).html();
        Mockito.verify(AppUiTest.connection, Mockito.times(1)).get();
        Mockito.verify(AppUiTest.connection, Mockito.times(1)).ignoreContentType(ArgumentMatchers.anyBoolean());
        Mockito.verify(AppUiTest.connection, Mockito.times(1)).ignoreHttpErrors(ArgumentMatchers.anyBoolean());
    }

    private static void logMethod() {
        AppUiTest.counterLog++;
        System.out.println(  // no logger
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
            +" "+ AppUiTest.counterLog +": "+
            StackWalker.getInstance()
            .walk(s -> s.skip(1).findFirst())
            .orElseThrow()
            .getMethodName()
        );
    }
}
