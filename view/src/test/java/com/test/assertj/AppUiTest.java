package com.test.assertj;

import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.util.bruter.ActionCoder;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.manager.ManagerScan;
import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
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

class AppUiTest {
    private static int counterLog = 0;
    private static FrameFixture window;
    private static final Connection connection = Mockito.mock(Connection.class);
    private static final Document document = Mockito.mock(Document.class);
    private static MockedStatic<Jsoup> utilities;
    private static Robot robot;

    @BeforeAll
    static void setUpOnce() {
        AppUiTest.logMethod();
        FailOnThreadViolationRepaintManager.install();

        AppUiTest.robot = BasicRobot.robotWithCurrentAwtHierarchyWithoutScreenLock();  // fix linux instabilities

        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);

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

        injectionModel.subscribe(frame.getSubscriber());
        AppUiTest.logMethod();
    }

    @AfterAll  // when all test methods end, keeps class active
    static void afterAll()  {
        AppUiTest.logMethod();
        AppUiTest.window.cleanUp();  // allow mvn retry
        AppUiTest.robot.cleanUp();
    }

    @Test
    void shouldDnDList() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane("tabManagers").selectTab("Admin page");

        Assertions.assertEquals("admin", AppUiTest.window.list("listManagerAdminPage").valueAt(0));
        Assertions.assertNotEquals("admin", AppUiTest.window.list("listManagerAdminPage").valueAt(1));

        AppUiTest.window.list("listManagerAdminPage").drag(0);
        AppUiTest.window.list("listManagerAdminPage").drop(1);

        Assertions.assertNotEquals("admin", AppUiTest.window.list("listManagerAdminPage").valueAt(0));
        Assertions.assertEquals("admin", AppUiTest.window.list("listManagerAdminPage").valueAt(1));

        AppUiTest.logMethod();
        AppUiTest.window.list("listManagerAdminPage").selectItem(0).pressKey(KeyEvent.VK_DELETE);
        AppUiTest.window.list("listManagerAdminPage").selectItem(0);
        AppUiTest.window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C);
        AppUiTest.window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_C);
        AppUiTest.window.list("listManagerAdminPage").selectItem(1);
        AppUiTest.window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("admin", AppUiTest.window.list("listManagerAdminPage").valueAt(0));
        Assertions.assertEquals("admin", AppUiTest.window.list("listManagerAdminPage").valueAt(1));

        AppUiTest.logMethod();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("paste-from-clipboard");
        clipboard.setContents(stringSelection, null);
        AppUiTest.window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("paste-from-clipboard", AppUiTest.window.list("listManagerAdminPage").valueAt(1));

        AppUiTest.logMethod();
        AppUiTest.window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_X);
        AppUiTest.window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_X);
        AppUiTest.window.list("listManagerAdminPage").selectItem(3);
        AppUiTest.window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        AppUiTest.window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertNotEquals("paste-from-clipboard", AppUiTest.window.list("listManagerAdminPage").valueAt(1));
        Assertions.assertEquals("paste-from-clipboard", AppUiTest.window.list("listManagerAdminPage").valueAt(3));
        AppUiTest.logMethod();
    }

    @Test
    void shouldDnDScanList() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane("tabManagers").selectTab("Batch scan");
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
    void shouldDnDTabs() {
        AppUiTest.logMethod();
        var request = new Request();
        request.setMessage(Interaction.CREATE_FILE_TAB);
        request.setParameters("dragfile", "content", "path");
        MediatorHelper.model().sendToViews(request);

        request = new Request();
        request.setMessage(Interaction.CREATE_FILE_TAB);
        request.setParameters("jumpfile", "content", "path");
        MediatorHelper.model().sendToViews(request);

        request = new Request();
        request.setMessage(Interaction.CREATE_FILE_TAB);
        request.setParameters("dropfile", "content", "path");
        MediatorHelper.model().sendToViews(request);

        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane("tabResults").requireTitle("dragfile ", Index.atIndex(0));
        AppUiTest.window.tabbedPane("tabResults").requireTitle("jumpfile ", Index.atIndex(1));
        AppUiTest.window.tabbedPane("tabResults").requireTitle("dropfile ", Index.atIndex(2));

        AppUiTest.logMethod();
        AppUiTest.window.robot().pressMouse(
            AppUiTest.window.label("dragfile").target(),
            AppUiTest.window.label("dragfile").target().getLocation()
        );
        AppUiTest.window.robot().moveMouse(AppUiTest.window.label("dragfile").target());  // required
        AppUiTest.window.label("dropfile").drop();

        try {
            AppUiTest.window.tabbedPane("tabResults").requireTitle("jumpfile ", Index.atIndex(0));
            AppUiTest.window.tabbedPane("tabResults").requireTitle("dragfile ", Index.atIndex(1));
            AppUiTest.window.tabbedPane("tabResults").requireTitle("dropfile ", Index.atIndex(2));
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.logMethod();
        GuiActionRunner.execute(() -> {
            AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0);
            AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0);
            AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0);
        });
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindFile() {
        AppUiTest.logMethod();
        var request = new Request();
        request.setMessage(Interaction.CREATE_FILE_TAB);
        request.setParameters("file", "content", "path");
        MediatorHelper.model().sendToViews(request);

        try {
            AppUiTest.window.tabbedPane("tabResults").selectTab("file ").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.logMethod();
        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindWebshell() {
        AppUiTest.logMethod();
        var request = new Request();
        request.setMessage(Interaction.ADD_TAB_EXPLOIT_WEB);
        request.setParameters("http://webshell", "http://webshell/path");
        MediatorHelper.model().sendToViews(request);

        try {
            AppUiTest.window.tabbedPane("tabResults").selectTab("Web shell").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.logMethod();
        AppUiTest.window.textBox("webShell").pressAndReleaseKeys(
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

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindVendorAndErrorMethods() {
        AppUiTest.logMethod();
        AppUiTest.window.label("menuStrategy").click();
        AppUiTest.window.menuItem("itemRadioStrategyError").requireDisabled();

        var request = new Request();
        request.setMessage(Interaction.SET_VENDOR);
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.VENDOR, MediatorHelper.model().getMediatorVendor().getMysql());
        request.setParameters(msgHeader);
        MediatorHelper.model().sendToViews(request);

        msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, "");
        msgHeader.put(Header.INDEX_ERROR_STRATEGY, 0);
        msgHeader.put(Header.INJECTION_MODEL, MediatorHelper.model());
        msgHeader.put(Header.VENDOR, MediatorHelper.model().getMediatorVendor().getMysql());

        var requestError = new Request();
        requestError.setMessage(Interaction.MARK_ERROR_VULNERABLE);
        requestError.setParameters(msgHeader);
        MediatorHelper.model().sendToViews(requestError);

        AppUiTest.logMethod();
        AppUiTest.window.menuItem("itemRadioStrategyError").requireEnabled(Timeout.timeout(1000));

        Assertions.assertEquals("MySQL", AppUiTest.window.label("menuVendor").target().getText(), "Vendor should be MySQL");

        AppUiTest.logMethod();
        AppUiTest.window.label("menuVendor").click();
        AppUiTest.window.menuItem("itemRadioVendorMySQL").requireVisible();

        AppUiTest.window.label("menuStrategy").click();

        AppUiTest.window.robot().moveMouse(
            AppUiTest.window.menuItem("itemRadioStrategyError").target()  // faster than click
        );
        AppUiTest.logMethod();
        try {
            AppUiTest.window.menuItem("itemRadioErrorUnsigned:or").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.window.menuItem("itemRadioStrategyError").requireEnabled();
        AppUiTest.window.menuItem("itemRadioErrorUnsigned:or").click();
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindNetworkHeader() {
        AppUiTest.logMethod();
        Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
        msgHeader.put(Header.URL, "url");
        msgHeader.put(Header.POST, "post");
        msgHeader.put(Header.HEADER, new TreeMap<>(Map.of("key1","value1", "key2", "value2")));
        msgHeader.put(Header.RESPONSE, new TreeMap<>(Map.of("key1","value1", "key2", "value2")));
        msgHeader.put(Header.SOURCE, "source");
        msgHeader.put(Header.PAGE_SIZE, "1");
        msgHeader.put(Header.METADATA_PROCESS, "meta process");
        msgHeader.put(Header.METADATA_STRATEGY, "meta strategy");

        var request = new Request();
        request.setMessage(Interaction.MESSAGE_HEADER);
        request.setParameters(msgHeader);
        MediatorHelper.model().sendToViews(request);

        try {
            AppUiTest.window.label("CONSOLE_NETWORK_LABEL").click().requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.window.table("networkTable").selectRows(0).requireContents(new String[][] { { "url", "1", "meta strategy", "meta process" } });

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
    void shouldFindSqlshell() {
        AppUiTest.logMethod();
        var request = new Request();
        request.setMessage(Interaction.ADD_TAB_EXPLOIT_SQL);
        request.setParameters("http://sqlshell", "http://sqlshell/path", "username", "password");
        MediatorHelper.model().sendToViews(request);

        try {
            AppUiTest.logMethod();
            AppUiTest.window.tabbedPane("tabResults").selectTab("SQL shell").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    void shouldRunCoder() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane("tabManagers").selectTab("Encoding");
        AppUiTest.window.label("menuMethodManagerCoder").click();

        AppUiTest.window.textBox("textInputManagerCoder").setText("a");

        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("Base16").target());
        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("encodeToBase16").target());
        AppUiTest.window.textBox("resultManagerCoder").requireText(Pattern.compile("61", Pattern.DOTALL));

        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("Base32").target());
        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("encodeToBase32").target());
        AppUiTest.window.textBox("resultManagerCoder").requireText(Pattern.compile("ME======", Pattern.DOTALL));

        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("Base58").target());
        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("encodeToBase58").target());
        AppUiTest.window.textBox("resultManagerCoder").requireText(Pattern.compile("2g", Pattern.DOTALL));

        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("Base64").target());
        AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("encodeToBase64").target());
        AppUiTest.window.textBox("resultManagerCoder").requireText(Pattern.compile("YQ==", Pattern.DOTALL));

        AppUiTest.logMethod();
        try {
            AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("Hash").target());
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

            AppUiTest.window.robot().moveMouse(AppUiTest.window.menuItem("hashTo"+ hash).target());
            AppUiTest.window.textBox("resultManagerCoder").requireText(result);
        });
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindAdminpage() throws IOException {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane("tabManagers").selectTab("Admin page");
        AppUiTest.window.list("listManagerAdminPage").item(0).select().rightClick();

        var request = new Request();
        request.setMessage(Interaction.CREATE_ADMIN_PAGE_TAB);
        request.setParameters("http://adminpage");
        MediatorHelper.model().sendToViews(request);

        AppUiTest.logMethod();
        try {
            AppUiTest.window.tabbedPane("tabResults").selectTab("adminpage ").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        AppUiTest.verifyMockAdminPage();

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindDatabase() {
        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane("tabManagers").selectTab("Database");

        var nameDatabase = "database";
        Database database = new Database(nameDatabase, "1");

        var requestDatabase = new Request();
        requestDatabase.setMessage(Interaction.ADD_DATABASES);
        requestDatabase.setParameters(List.of(database));
        MediatorHelper.model().sendToViews(requestDatabase);
        Assertions.assertEquals(nameDatabase +" (1 table)", AppUiTest.window.tree("treeDatabases").valueAt(0));

        var nameTable = "table";
        Table table = new Table(nameTable, "2", database);
        var requestTable = new Request();
        requestTable.setMessage(Interaction.ADD_TABLES);
        requestTable.setParameters(List.of(table));
        MediatorHelper.model().sendToViews(requestTable);
        Assertions.assertEquals(nameTable +" (2 rows)", AppUiTest.window.tree("treeDatabases").valueAt(1));

        AppUiTest.logMethod();
        var nameColumn0 = "column 0";
        var nameColumn1 = "column 1";
        Column column1 = new Column(nameColumn0, table);
        Column column2 = new Column(nameColumn1, table);
        var request = new Request();
        request.setMessage(Interaction.ADD_COLUMNS);
        request.setParameters(Arrays.asList(column1, column2));
        MediatorHelper.model().sendToViews(request);
        Assertions.assertEquals(nameColumn0, AppUiTest.window.tree("treeDatabases").valueAt(2));
        Assertions.assertEquals(nameColumn1, AppUiTest.window.tree("treeDatabases").valueAt(3));

        var arrayColumns = new String[] { Strings.EMPTY, Strings.EMPTY, nameColumn0, nameColumn1 };
        var tableDatas = new String[][] {
            { "", "", "[0, 0]", "[0, 1]" },
            { "", "", "[1, 0]", "[1, 1]" }
        };
        var objectData = new Object[]{ arrayColumns, tableDatas, table };
        var requestValues = new Request();
        requestValues.setMessage(Interaction.CREATE_VALUES_TAB);
        requestValues.setParameters(objectData);
        MediatorHelper.model().sendToViews(requestValues);

        AppUiTest.logMethod();
        AppUiTest.window.tabbedPane("tabResults").selectTab(nameTable).requireVisible();
        AppUiTest.window.tree("treeDatabases").rightClickRow(0);
        AppUiTest.window.tabbedPane("tabResults").click();
        AppUiTest.window.tree("treeDatabases").rightClickRow(1);

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindOkButton() {
        AppUiTest.logMethod();
        try {
            AppUiTest.window.button("buttonInUrl").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldFindConsoleButton() {
        AppUiTest.logMethod();
        AppUiTest.window.label("buttonShowSouth").click();
        AppUiTest.window.label("buttonShowConsolesHidden").click();
        AppUiTest.window.label("buttonShowNorth").click();

        try {
            AppUiTest.window.label("buttonShowSouth").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldFindConnectionPreferences() {
        AppUiTest.logMethod();
        AppUiTest.window.label("advancedButton").click();
        AppUiTest.window.menuItem("menuWindows").click();
        AppUiTest.window.menuItem("itemPreferences").click();
        AppUiTest.window.list("listCategoriesPreference").selectItem(Pattern.compile(".*Connection.*"));

        AppUiTest.logMethod();
        Arrays.asList(  // init
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUnicodeDecodeDisabled(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCsrfUserTag(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotProcessingCookies(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isConnectionTimeout()
        ).forEach(Assertions::assertFalse);
        Assertions.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());

        AppUiTest.logMethod();
        AppUiTest.window.checkBox("checkboxIsFollowingRedirection").click();
        AppUiTest.window.checkBox("checkboxIsUnicodeDecodeDisabled").click();
        AppUiTest.window.checkBox("checkboxIsNotTestingConnection").click();
        AppUiTest.window.checkBox("checkboxIsProcessingCsrf").click();
        AppUiTest.window.checkBox("checkboxIsCsrfUserTag").click();
        AppUiTest.window.checkBox("checkboxIsNotProcessingCookies").click();
        AppUiTest.window.checkBox("checkboxIsLimitingThreads").click();
        AppUiTest.window.checkBox("checkboxIsConnectionTimeout").click();
        Arrays.asList(  // check
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUnicodeDecodeDisabled(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCsrfUserTag(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotProcessingCookies(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isConnectionTimeout()
        ).forEach(Assertions::assertTrue);
        Assertions.assertFalse(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());

        AppUiTest.logMethod();
        AppUiTest.window.checkBox("checkboxIsFollowingRedirection").click();
        AppUiTest.window.checkBox("checkboxIsUnicodeDecodeDisabled").click();
        AppUiTest.window.checkBox("checkboxIsNotTestingConnection").click();
        AppUiTest.window.checkBox("checkboxIsNotProcessingCookies").click();
        AppUiTest.window.checkBox("checkboxIsProcessingCsrf").click();
        AppUiTest.window.checkBox("checkboxIsCsrfUserTag").click();
        AppUiTest.window.checkBox("checkboxIsLimitingThreads").click();
        AppUiTest.window.checkBox("checkboxIsConnectionTimeout").click();
        Arrays.asList(  // uncheck
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isFollowingRedirection(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUnicodeDecodeDisabled(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotTestingConnection(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isProcessingCsrf(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCsrfUserTag(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotProcessingCookies(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isConnectionTimeout()
        ).forEach(Assertions::assertFalse);
        Assertions.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingThreads());

        AppUiTest.window.label("advancedButton").click();

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindInjectionPreferences() {
        AppUiTest.logMethod();
        AppUiTest.window.label("advancedButton").click();
        AppUiTest.window.menuItem("menuWindows").click();
        AppUiTest.window.menuItem("itemPreferences").click();

        AppUiTest.logMethod();
        Arrays.asList(
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isParsingForm(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingNormalIndex(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingSleepTimeStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isPerfIndexDisabled(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled()
        ).forEach(Assertions::assertFalse);

        AppUiTest.logMethod();
        AppUiTest.window.checkBox("checkboxIsParsingForm").click();
        AppUiTest.window.checkBox("checkboxIsNotInjectingMetadata").click();
        AppUiTest.window.checkBox("checkboxIsLimitingNormalIndex").click();
        AppUiTest.window.checkBox("checkboxIsLimitingSleepTimeStrategy").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllURLParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllRequestParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllHeaderParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllJSONParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllSOAPParam").click();
        AppUiTest.window.checkBox("checkboxIsPerfIndexDisabled").click();
        AppUiTest.window.checkBox("checkboxIsUrlEncodingDisabled").click();
        Arrays.asList(
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isParsingForm(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingNormalIndex(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingSleepTimeStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isPerfIndexDisabled(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled()
        ).forEach(Assertions::assertTrue);

        AppUiTest.logMethod();
        AppUiTest.window.checkBox("checkboxIsParsingForm").click();
        AppUiTest.window.checkBox("checkboxIsNotInjectingMetadata").click();
        AppUiTest.window.checkBox("checkboxIsLimitingNormalIndex").click();
        AppUiTest.window.checkBox("checkboxIsLimitingSleepTimeStrategy").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllURLParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllRequestParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllHeaderParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllJSONParam").click();
        AppUiTest.window.checkBox("checkboxIsCheckingAllSOAPParam").click();
        AppUiTest.window.checkBox("checkboxIsPerfIndexDisabled").click();
        AppUiTest.window.checkBox("checkboxIsUrlEncodingDisabled").click();
        Arrays.asList(
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isParsingForm(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingNormalIndex(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingSleepTimeStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isPerfIndexDisabled(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled()
        ).forEach(Assertions::assertFalse);

        AppUiTest.logMethod();
        AppUiTest.window.radioButton("radioIsZipStrategy").check();
        Assertions.assertArrayEquals(new boolean[] {
            false,
            false,
            true,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy(),
        });

        AppUiTest.logMethod();
        AppUiTest.window.radioButton("radioIsDiosStrategy").check();
        Assertions.assertArrayEquals(new boolean[] {
            false,
            true,
            false,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy(),
        });

        AppUiTest.logMethod();
        AppUiTest.window.radioButton("radioIsDefaultStrategy").check();
        Assertions.assertArrayEquals(new boolean[] {
            true,
            false,
            false,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy(),
        });

        AppUiTest.window.label("advancedButton").click();

        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
        AppUiTest.logMethod();
    }

    @Test
    void shouldFindSqlEngine() {
        AppUiTest.logMethod();
        AppUiTest.window.label("advancedButton").click();
        AppUiTest.window.menuItem("menuWindows").click();
        AppUiTest.window.menuItem("itemSqlEngine").click();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label("advancedButton").click();
        } catch (Exception e) {
            Assertions.fail();
        }
        GuiActionRunner.execute(() -> AppUiTest.window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindLanguage() {
        AppUiTest.logMethod();
        AppUiTest.window.label("advancedButton").click();
        AppUiTest.window.menuItem("menuWindows").click();
        AppUiTest.window.robot().moveMouse(
            AppUiTest.window.menuItem("menuTranslation").target()  // faster than click
        );
        AppUiTest.window.menuItem("itemRussian").click();

        AppUiTest.window.menuItem("menuWindows").click();
        AppUiTest.window.robot().moveMouse(
            AppUiTest.window.menuItem("menuTranslation").target()  // faster than click
        );
        AppUiTest.window.menuItem("itemEnglish").click();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label("advancedButton").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

//    Unstable?
//    @Test
//    void shouldFindReportIssue() {
//
//        window.label("advancedButton").click();
//        window.menuItem("menuCommunity").click();
//        window.menuItem("itemReportIssue").click();
//
//        DialogFixture dialog = window.dialog();
//        dialog.button(JButtonMatcher.withText("Cancel")).click();
//
//        try {
//            window.label("advancedButton").click();
//        } catch (Exception e) {
//            Assertions.fail();
//        }
//    }

    @Test
    void shouldFindIHelpTranslate() {
        AppUiTest.logMethod();
        AppUiTest.window.label("advancedButton").click();
        AppUiTest.window.menuItem("menuCommunity").click();
        AppUiTest.window.robot().moveMouse(
            AppUiTest.window.menuItem("menuI18nContribution").target()  // faster than click
        );
        AppUiTest.window.menuItem("itemIntoFrench").click();

        AppUiTest.logMethod();
        DialogFixture dialog = AppUiTest.window.dialog();
        dialog.close();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label("advancedButton").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldFindAbout() {
        AppUiTest.logMethod();
        AppUiTest.window.label("advancedButton").click();
        AppUiTest.window.menuItem("menuHelp").click();
        AppUiTest.window.menuItem("itemHelp").click();

        DialogFixture dialog = AppUiTest.window.dialog();
        dialog.button(JButtonMatcher.withText("Close")).click();

        AppUiTest.logMethod();
        try {
            AppUiTest.window.label("advancedButton").click();
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
