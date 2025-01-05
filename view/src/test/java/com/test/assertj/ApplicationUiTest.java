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
import com.jsql.view.swing.manager.ManagerCoder;
import com.jsql.view.swing.manager.ManagerScan;
import com.jsql.view.swing.menubar.AppMenubar;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.codec.DecoderException;
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
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class ApplicationUiTest {

    private static FrameFixture window;

    private static final Connection connection = Mockito.mock(Connection.class);
    private static final Document document = Mockito.mock(Document.class);
    private static MockedStatic<Jsoup> utilities;
    private static Robot robot;

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();

        // fix linux instabilities
        robot = BasicRobot.robotWithNewAwtHierarchy();
//        robot.settings().delayBetweenEvents(240);
//        robot.settings().eventPostingDelay(400);

        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);

        // Static mock on current ThreadLocal
        JFrameView frame = GuiActionRunner.execute(() -> {
            if (utilities != null) {
                utilities.close();
            }

            utilities = Mockito.mockStatic(Jsoup.class);

            utilities.when(() -> Jsoup.connect(ArgumentMatchers.anyString())).thenReturn(connection);
            utilities.when(() -> Jsoup.clean(ArgumentMatchers.anyString(), ArgumentMatchers.any(Safelist.class))).thenReturn("cleaned");

            Mockito.when(connection.ignoreContentType(ArgumentMatchers.anyBoolean())).thenReturn(connection);
            Mockito.when(connection.ignoreHttpErrors(ArgumentMatchers.anyBoolean())).thenReturn(connection);

            Mockito.when(connection.get()).thenReturn(document);
            Mockito.when(document.html()).thenReturn("<html><input/>test</html>");

            Mockito.when(document.text()).thenReturn("<html><input/>test</html>");
            utilities.when(() -> Jsoup.parse(Mockito.anyString())).thenReturn(document);

            UiUtil.applyTheme(FlatDarkFlatIJTheme.class.getName());  // init but not enough, reapplied next
            var view = new JFrameView();
            SwingUtilities.invokeLater(() -> AppMenubar.applyTheme(FlatDarkFlatIJTheme.class.getName()));
            return view;
        });

        window = new FrameFixture(robot, frame);

        injectionModel.subscribe(frame.getSubscriber());
    }

    @AfterAll  // when all test methods end, keeps class active
    static void afterAll()  {
        window.cleanUp();  // allow mvn retry
        robot.cleanUp();
    }

    @Test
    void shouldDnDList() {
        window.tabbedPane("tabManagers").selectTab("Admin page");

        Assertions.assertEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assertions.assertNotEquals("admin", window.list("listManagerAdminPage").valueAt(1));

        window.list("listManagerAdminPage").drag(0);
        window.list("listManagerAdminPage").drop(1);

        Assertions.assertNotEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assertions.assertEquals("admin", window.list("listManagerAdminPage").valueAt(1));

        window.list("listManagerAdminPage").selectItem(0).pressKey(KeyEvent.VK_DELETE);
        window.list("listManagerAdminPage").selectItem(0);
        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_C);
        window.list("listManagerAdminPage").selectItem(1);
        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assertions.assertEquals("admin", window.list("listManagerAdminPage").valueAt(1));

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("paste-from-clipboard");
        clipboard.setContents(stringSelection, null);
        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("paste-from-clipboard", window.list("listManagerAdminPage").valueAt(1));

        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_X);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_X);
        window.list("listManagerAdminPage").selectItem(3);
        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertNotEquals("paste-from-clipboard", window.list("listManagerAdminPage").valueAt(1));
        Assertions.assertEquals("paste-from-clipboard", window.list("listManagerAdminPage").valueAt(3));
    }

    @Test
    void shouldDnDScanList() {
        window.tabbedPane("tabManagers").selectTab("Batch scan");
        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list(ManagerScan.NAME).valueAt(0));
        Assertions.assertNotEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list(ManagerScan.NAME).valueAt(1));
        window.list(ManagerScan.NAME).drag(0);
        window.list(ManagerScan.NAME).drop(1);
        Assertions.assertNotEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list(ManagerScan.NAME).valueAt(0));
        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list(ManagerScan.NAME).valueAt(1));

        window.list(ManagerScan.NAME).selectItem(0).pressKey(KeyEvent.VK_DELETE);
        window.list(ManagerScan.NAME).selectItem(0);
        window.list(ManagerScan.NAME).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C);
        window.list(ManagerScan.NAME).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_C);
        window.list(ManagerScan.NAME).selectItem(1);
        window.list(ManagerScan.NAME).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list(ManagerScan.NAME).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list(ManagerScan.NAME).valueAt(0));
        Assertions.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list(ManagerScan.NAME).valueAt(1));

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("paste-from-clipboard");
        clipboard.setContents(stringSelection, null);
        window.list(ManagerScan.NAME).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list(ManagerScan.NAME).releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);

        Assertions.assertEquals("paste-from-clipboard", window.list(ManagerScan.NAME).valueAt(1));
    }

    @Test
    void shouldDnDTabs() {
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

        window.tabbedPane("tabResults").requireTitle("dragfile ", Index.atIndex(0));
        window.tabbedPane("tabResults").requireTitle("jumpfile ", Index.atIndex(1));
        window.tabbedPane("tabResults").requireTitle("dropfile ", Index.atIndex(2));

        window.robot().pressMouse(
            window.label("dragfile").target(),
            window.label("dragfile").target().getLocation()
        );
        window.robot().moveMouse(window.label("dragfile").target());  // required
        window.label("dropfile").drop();

        try {
            window.tabbedPane("tabResults").requireTitle("jumpfile ", Index.atIndex(0));
            window.tabbedPane("tabResults").requireTitle("dragfile ", Index.atIndex(1));
            window.tabbedPane("tabResults").requireTitle("dropfile ", Index.atIndex(2));
        } catch (Exception e) {
            Assertions.fail();
        }

        GuiActionRunner.execute(() -> {
            window.tabbedPane("tabResults").target().removeTabAt(0);
            window.tabbedPane("tabResults").target().removeTabAt(0);
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }

    @Test
    void shouldFindFile() {
        var request = new Request();
        request.setMessage(Interaction.CREATE_FILE_TAB);
        request.setParameters("file", "content", "path");
        MediatorHelper.model().sendToViews(request);

        try {
            window.tabbedPane("tabResults").selectTab("file ").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindWebshell() {
        var request = new Request();
        request.setMessage(Interaction.CREATE_SHELL_TAB);
        request.setParameters("http://webshell", "http://webshell/path");
        MediatorHelper.model().sendToViews(request);

        try {
            window.tabbedPane("tabResults").selectTab("Web shell").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        window.textBox("webShell").pressAndReleaseKeys(
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

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindVendorAndErrorMethods() {
        window.label("menuStrategy").click();
        window.menuItem("itemRadioStrategyError").requireDisabled();

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

        window.menuItem("itemRadioStrategyError").requireEnabled(Timeout.timeout(1000));

        Assertions.assertEquals("MySQL", window.label("menuVendor").target().getText(), "Vendor should be MySQL");

        window.label("menuVendor").click();
        window.menuItem("itemRadioVendorMySQL").requireVisible();

        window.label("menuStrategy").click();
        window.label("menuStrategy").requireVisible();

        window.menuItem("itemRadioStrategyError").click();
        try {
            window.menuItem("itemRadioErrorUnsigned:or").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        window.menuItem("itemRadioStrategyError").requireEnabled();
        window.menuItem("itemRadioErrorUnsigned:or").click();
    }

    @Test
    void shouldFindNetworkHeader() {
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
            window.label("CONSOLE_NETWORK_LABEL").click().requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        window.table("networkTable").selectRows(0).requireContents(new String[][] { { "url", "1", "meta strategy", "meta process" } });

        window.label("labelNETWORK_TAB_URL_LABEL").click();
        window.textBox("textNETWORK_TAB_URL_LABEL").requireText("url");
        window.label("labelNETWORK_TAB_RESPONSE_LABEL").click();
        window.textBox("textNETWORK_TAB_RESPONSE_LABEL").requireText(Pattern.compile(".*key1: value1.*key2: value2.*", Pattern.DOTALL));
        window.label("labelNETWORK_TAB_SOURCE_LABEL").click();
        window.textBox("textNETWORK_TAB_SOURCE_LABEL").requireText("source");
        window.label("labelNETWORK_TAB_PREVIEW_LABEL").click();
        window.textBox("textNETWORK_TAB_PREVIEW_LABEL").requireText(Pattern.compile(".*<html>.*cleaned.*</html>.*", Pattern.DOTALL));
        window.label("labelNETWORK_TAB_HEADERS_LABEL").click();
        window.textBox("textNETWORK_TAB_HEADERS_LABEL").requireText(Pattern.compile(".*key1: value1.*key2: value2.*", Pattern.DOTALL));
        window.label("labelNETWORK_TAB_PARAMS_LABEL").click();
        window.textBox("textNETWORK_TAB_PARAMS_LABEL").requireText("post");
    }

    @Test
    void shouldFindSqlshell() {
        var request = new Request();
        request.setMessage(Interaction.CREATE_SQL_SHELL_TAB);
        request.setParameters("http://sqlshell", "http://sqlshell/path", "username", "password");
        MediatorHelper.model().sendToViews(request);

        try {
            window.tabbedPane("tabResults").selectTab("SQL shell").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldRunCoder() {
        window.tabbedPane("tabManagers").selectTab("Encoding");
        window.label("menuMethodManagerCoder").click();

        window.textBox("textInputManagerCoder").setText("a");

        window.robot().moveMouse(window.menuItem("Base16").target());
        window.robot().moveMouse(window.menuItem("encodeToBase16").target());
        window.textBox("resultManagerCoder").requireText(Pattern.compile(".*<span><font[^>]*>61</font></span>.*", Pattern.DOTALL));

        window.robot().moveMouse(window.menuItem("Base32").target());
        window.robot().moveMouse(window.menuItem("encodeToBase32").target());
        window.textBox("resultManagerCoder").requireText(Pattern.compile(".*<span><font[^>]*>ME======</font></span>.*", Pattern.DOTALL));

        window.robot().moveMouse(window.menuItem("Base58").target());
        window.robot().moveMouse(window.menuItem("encodeToBase58").target());
        window.textBox("resultManagerCoder").requireText(Pattern.compile(".*<span><font[^>]*>2g</font></span>.*", Pattern.DOTALL));

        window.robot().moveMouse(window.menuItem("Base64").target());
        window.robot().moveMouse(window.menuItem("encodeToBase64").target());
        window.textBox("resultManagerCoder").requireText(Pattern.compile(".*<span><font[^>]*>YQ==</font></span>.*", Pattern.DOTALL));

        try {
            window.robot().moveMouse(window.menuItem("Hash").target());
        } catch (Exception e) {
            Assertions.fail();
        }

        Stream.of(ManagerCoder.HASHES)
        .forEach(hash -> {
            String result = null;
            try {
                result = ActionCoder.forName(hash)
                    .orElseThrow(() -> new NoSuchElementException("Unsupported encoding or decoding method"))
                    .run("a");
            } catch (NoSuchAlgorithmException | NoSuchElementException | DecoderException | IOException e) {
                Assertions.fail();
            }

            window.robot().moveMouse(window.menuItem("hashTo"+ hash).target());
            window.textBox("resultManagerCoder").requireText(Pattern.compile(".*<span><font[^>]*>"+ result +"</font></span>.*", Pattern.DOTALL));
        });
    }

    @Test
    void shouldFindAdminpage() throws IOException {
        window.tabbedPane("tabManagers").selectTab("Admin page");
        window.list("listManagerAdminPage").item(0).select().rightClick();

        var request = new Request();
        request.setMessage(Interaction.CREATE_ADMIN_PAGE_TAB);
        request.setParameters("http://adminpage");
        MediatorHelper.model().sendToViews(request);

        try {
            window.tabbedPane("tabResults").selectTab("adminpage ").requireVisible();
        } catch (Exception e) {
            Assertions.fail();
        }

        ApplicationUiTest.verifyMockAdminPage();

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindDatabase() {
        window.tabbedPane("tabManagers").selectTab("Database");

        var nameDatabase = "database";
        Database database = new Database(nameDatabase, "1");

        var requestDatabase = new Request();
        requestDatabase.setMessage(Interaction.ADD_DATABASES);
        requestDatabase.setParameters(List.of(database));
        MediatorHelper.model().sendToViews(requestDatabase);
        Assertions.assertEquals(nameDatabase +" (1 table)", window.tree("treeDatabases").valueAt(0));

        var nameTable = "table";
        Table table = new Table(nameTable, "2", database);
        var requestTable = new Request();
        requestTable.setMessage(Interaction.ADD_TABLES);
        requestTable.setParameters(List.of(table));
        MediatorHelper.model().sendToViews(requestTable);
        Assertions.assertEquals(nameTable +" (2 rows)", window.tree("treeDatabases").valueAt(1));

        var nameColumn0 = "column 0";
        var nameColumn1 = "column 1";
        Column column1 = new Column(nameColumn0, table);
        Column column2 = new Column(nameColumn1, table);
        var request = new Request();
        request.setMessage(Interaction.ADD_COLUMNS);
        request.setParameters(Arrays.asList(column1, column2));
        MediatorHelper.model().sendToViews(request);
        Assertions.assertEquals(nameColumn0, window.tree("treeDatabases").valueAt(2));
        Assertions.assertEquals(nameColumn1, window.tree("treeDatabases").valueAt(3));

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

        window.tabbedPane("tabResults").selectTab(nameTable).requireVisible();
        window.tree("treeDatabases").rightClickRow(0);
        window.tabbedPane("tabResults").click();
        window.tree("treeDatabases").rightClickRow(1);

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindOkButton() {
        try {
            window.button("buttonInUrl").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldFindConsoleButton() {
        window.label("buttonShowSouth").click();
        window.label("buttonShowConsolesHidden").click();
        window.label("buttonShowNorth").click();

        try {
            window.label("buttonShowSouth").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldFindConnectionPreferences() {
        window.label("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemPreferences").click();
        window.list("listCategoriesPreference").selectItem(Pattern.compile(".*Connection.*"));

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

        window.checkBox("checkboxIsFollowingRedirection").click();
        window.checkBox("checkboxIsUnicodeDecodeDisabled").click();
        window.checkBox("checkboxIsNotTestingConnection").click();
        window.checkBox("checkboxIsProcessingCsrf").click();
        window.checkBox("checkboxIsCsrfUserTag").click();
        window.checkBox("checkboxIsNotProcessingCookies").click();
        window.checkBox("checkboxIsLimitingThreads").click();
        window.checkBox("checkboxIsConnectionTimeout").click();
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

        window.checkBox("checkboxIsFollowingRedirection").click();
        window.checkBox("checkboxIsUnicodeDecodeDisabled").click();
        window.checkBox("checkboxIsNotTestingConnection").click();
        window.checkBox("checkboxIsNotProcessingCookies").click();
        window.checkBox("checkboxIsProcessingCsrf").click();
        window.checkBox("checkboxIsCsrfUserTag").click();
        window.checkBox("checkboxIsLimitingThreads").click();
        window.checkBox("checkboxIsConnectionTimeout").click();
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

        window.label("advancedButton").click();

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindInjectionPreferences() {
        window.label("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemPreferences").click();

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

        window.checkBox("checkboxIsParsingForm").click();
        window.checkBox("checkboxIsNotInjectingMetadata").click();
        window.checkBox("checkboxIsLimitingNormalIndex").click();
        window.checkBox("checkboxIsLimitingSleepTimeStrategy").click();
        window.checkBox("checkboxIsCheckingAllURLParam").click();
        window.checkBox("checkboxIsCheckingAllRequestParam").click();
        window.checkBox("checkboxIsCheckingAllHeaderParam").click();
        window.checkBox("checkboxIsCheckingAllJSONParam").click();
        window.checkBox("checkboxIsCheckingAllSOAPParam").click();
        window.checkBox("checkboxIsPerfIndexDisabled").click();
        window.checkBox("checkboxIsUrlEncodingDisabled").click();
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

        window.checkBox("checkboxIsParsingForm").click();
        window.checkBox("checkboxIsNotInjectingMetadata").click();
        window.checkBox("checkboxIsLimitingNormalIndex").click();
        window.checkBox("checkboxIsLimitingSleepTimeStrategy").click();
        window.checkBox("checkboxIsCheckingAllURLParam").click();
        window.checkBox("checkboxIsCheckingAllRequestParam").click();
        window.checkBox("checkboxIsCheckingAllHeaderParam").click();
        window.checkBox("checkboxIsCheckingAllJSONParam").click();
        window.checkBox("checkboxIsCheckingAllSOAPParam").click();
        window.checkBox("checkboxIsPerfIndexDisabled").click();
        window.checkBox("checkboxIsUrlEncodingDisabled").click();
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

        window.radioButton("radioIsZipStrategy").check();
        Assertions.assertArrayEquals(new boolean[] {
            true,
            false,
            false,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy(),
        });

        window.radioButton("radioIsDefaultStrategy").check();
        Assertions.assertArrayEquals(new boolean[] {
            false,
            true,
            false,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy(),
        });

        window.radioButton("radioIsDiosStrategy").check();
        Assertions.assertArrayEquals(new boolean[] {
            false,
            false,
            true,
        }, new boolean[] {
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy(),
            MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy(),
        });

        window.label("advancedButton").click();

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindSqlEngine() {
        window.label("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemSqlEngine").click();

        try {
            window.label("advancedButton").click();
        } catch (Exception e) {
            Assertions.fail();
        }

        GuiActionRunner.execute(() -> window.tabbedPane("tabResults").target().removeTabAt(0));
    }

    @Test
    void shouldFindLanguage() {
        window.label("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("menuTranslation").click();
        window.menuItem("itemRussian").click();

        window.menuItem("menuWindows").click();
        window.menuItem("menuTranslation").click();
        window.menuItem("itemEnglish").click();

        try {
            window.label("advancedButton").click();
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
        window.label("advancedButton").click();
        window.menuItem("menuCommunity").click();
        window.menuItem("menuI18nContribution").click();
        window.menuItem("itemIntoFrench").click();

        DialogFixture dialog = window.dialog();
        dialog.close();

        try {
            window.label("advancedButton").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldFindAbout() {
        window.label("advancedButton").click();
        window.menuItem("menuHelp").click();
        window.menuItem("itemHelp").click();

        DialogFixture dialog = window.dialog();
        dialog.button(JButtonMatcher.withText("Close")).click();

        try {
            window.label("advancedButton").click();
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    private static void verifyMockAdminPage() throws IOException {
        Mockito.verify(document, Mockito.times(1)).html();
        Mockito.verify(connection, Mockito.times(1)).get();
        Mockito.verify(connection, Mockito.times(1)).ignoreContentType(ArgumentMatchers.anyBoolean());
        Mockito.verify(connection, Mockito.times(1)).ignoreHttpErrors(ArgumentMatchers.anyBoolean());
    }
}
