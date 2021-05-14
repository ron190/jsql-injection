package com.test.assertj;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.util.Strings;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.util.bruter.ActionCoder;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.util.MediatorHelper;

public class ApplicationUiTest {
    
    private static FrameFixture window;
    private static JFrameView frame;
    
    private static Connection connection = Mockito.mock(Connection.class);
    private static Document document = Mockito.mock(Document.class);
    
    @BeforeClass
    public static void setUpOnce() {
        
        FailOnThreadViolationRepaintManager.install();
        
        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);
        frame = GuiActionRunner.execute(() -> {
            
            // Static mock on current ThreadLocal
            ApplicationUiTest.initMockAdminPage();

            return new JFrameView();
        });
        
        window = new FrameFixture(frame);
        
        injectionModel.subscribe(frame.getObserver());
    }

    @Test
    public void shouldDnDList() {

        window.tabbedPane("tabManagers").selectTab("Admin page");
        Assert.assertEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assert.assertNotEquals("admin", window.list("listManagerAdminPage").valueAt(1));
        window.list("listManagerAdminPage").drag(0);
        window.list("listManagerAdminPage").drop(1);
        Assert.assertNotEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assert.assertEquals("admin", window.list("listManagerAdminPage").valueAt(1));
        
        window.list("listManagerAdminPage").selectItem(0).pressKey(KeyEvent.VK_DELETE);
        window.list("listManagerAdminPage").selectItem(0);
        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_C);
        window.list("listManagerAdminPage").selectItem(1);
        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);
        
        Assert.assertEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assert.assertEquals("admin", window.list("listManagerAdminPage").valueAt(1));
        
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("paste-from-clipboard");
        clipboard.setContents(stringSelection, null);
        window.list("listManagerAdminPage").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list("listManagerAdminPage").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);
        
        Assert.assertEquals("paste-from-clipboard", window.list("listManagerAdminPage").valueAt(1));
    }
    
    @Test
    public void shouldDnDScanList() {
        
        window.tabbedPane("tabManagers").selectTab("Batch scan");
        Assert.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list("listManagerScan").valueAt(0));
        Assert.assertNotEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list("listManagerScan").valueAt(1));
        window.list("listManagerScan").drag(0);
        window.list("listManagerScan").drop(1);
        Assert.assertNotEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list("listManagerScan").valueAt(0));
        Assert.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list("listManagerScan").valueAt(1));
        
        window.list("listManagerScan").selectItem(0).pressKey(KeyEvent.VK_DELETE);
        window.list("listManagerScan").selectItem(0);
        window.list("listManagerScan").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C);
        window.list("listManagerScan").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_C);
        window.list("listManagerScan").selectItem(1);
        window.list("listManagerScan").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list("listManagerScan").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);
        
        Assert.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list("listManagerScan").valueAt(0));
        Assert.assertEquals("http://testphp.vulnweb.com/artists.php?artist=", window.list("listManagerScan").valueAt(1));
        
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("paste-from-clipboard");
        clipboard.setContents(stringSelection, null);
        window.list("listManagerScan").pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V);
        window.list("listManagerScan").releaseKey(KeyEvent.VK_CONTROL).releaseKey(KeyEvent.VK_V);
        
        Assert.assertEquals("paste-from-clipboard", window.list("listManagerScan").valueAt(1));
    }
    
    @Test
    public void shouldDnDTabs() {
        
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
            Assert.fail();
        }
        
        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
            window.tabbedPane("tabResults").target().removeTabAt(0);
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }
    
    @Test
    public void shouldFindFile() {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_FILE_TAB);
        request.setParameters("file", "content", "path");
        MediatorHelper.model().sendToViews(request);
        
        try {
            window.tabbedPane("tabResults").selectTab("file ").requireVisible();
        } catch (Exception e) {
            Assert.fail();
        }
        
        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }
    
    @Test
    public void shouldFindWebshell() {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_SHELL_TAB);
        request.setParameters("http://webshell", "http://webshell/path");
        MediatorHelper.model().sendToViews(request);
        
        try {
            window.tabbedPane("tabResults").selectTab("Web shell ").requireVisible();
        } catch (Exception e) {
            Assert.fail();
        }
        
        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }

    @Test
    public void shouldFindSqlshell() {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_SQL_SHELL_TAB);
        request.setParameters("http://sqlshell", "http://sqlshell/path", "username", "password");
        MediatorHelper.model().sendToViews(request);
        
        try {
            window.tabbedPane("tabResults").selectTab("SQL shell ").requireVisible();
        } catch (Exception e) {
            Assert.fail();
        }
        
        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }
    
    @Test
    public void shouldRunCoder() throws IOException {

        window.tabbedPane("tabManagers").selectTab("Encoding");
        window.menuItem("menuMethodManagerCoder").click();
        
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
        
        window.robot().moveMouse(window.menuItem("Hash").target());
        
        Stream
        .of("Adler32", "Crc16", "Crc32", "Crc64", "Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql")
        .forEach(hash -> {
            
            String result = null;
            try {
                result = ActionCoder
                    .forName(hash)
                    .orElseThrow(() -> new NoSuchElementException("Unsupported encoding or decoding method"))
                    .run("a");
            } catch (NoSuchAlgorithmException | NoSuchElementException | DecoderException | IOException e) {
                Assert.fail();
            }
            
            window.robot().moveMouse(window.menuItem("hashTo"+ hash).target());
            window.textBox("resultManagerCoder").requireText(Pattern.compile(".*<span><font[^>]*>"+ result +"</font></span>.*", Pattern.DOTALL));
        });
    }
    
    @Test
    public void shouldFindAdminpage() throws IOException {

        window.tabbedPane("tabManagers").selectTab("Admin page");
        window.list("listManagerAdminPage").item(0).select().rightClick();
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_ADMIN_PAGE_TAB);
        request.setParameters("http://adminpage");
        MediatorHelper.model().sendToViews(request);
        
        window.tabbedPane("tabResults").selectTab("adminpage ").requireVisible();

        ApplicationUiTest.verifyMockAdminPage();
        
        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }
    
    @Test
    public void shouldFindDatabase() {

        window.tabbedPane("tabManagers").selectTab("Database");
        
        var nameDatabase = "database";
        Database database = new Database(nameDatabase, "1");
        
        var requestDatabase = new Request();
        requestDatabase.setMessage(Interaction.ADD_DATABASES);
        requestDatabase.setParameters(Arrays.asList(database));
        MediatorHelper.model().sendToViews(requestDatabase);
        
        Assert.assertEquals(nameDatabase +" (1 table)", window.tree("treeDatabases").valueAt(0));
        
        
        var nameTable = "table";
        Table table = new Table(nameTable, "2", database);
        
        var requestTable = new Request();
        requestTable.setMessage(Interaction.ADD_TABLES);
        requestTable.setParameters(Arrays.asList(table));
        MediatorHelper.model().sendToViews(requestTable);
        
        Assert.assertEquals(nameTable +" (2 rows)", window.tree("treeDatabases").valueAt(1));
        
        
        var nameColumn0 = "column 0";
        var nameColumn1 = "column 1";
        Column column1 = new Column(nameColumn0, table);
        Column column2 = new Column(nameColumn1, table);
        
        var request = new Request();
        request.setMessage(Interaction.ADD_COLUMNS);
        request.setParameters(Arrays.asList(column1, column2));
        MediatorHelper.model().sendToViews(request);
        
        Assert.assertEquals(nameColumn0, window.tree("treeDatabases").valueAt(2));
        Assert.assertEquals(nameColumn1, window.tree("treeDatabases").valueAt(3));
        
        
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
        
        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }

    @Test
    public void shouldFindOkButton() {
        
        try {
            window.button("buttonInUrl").click();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldFindConsoleButton() {
        
        window.button("buttonShowSouth").click();
        window.button("buttonShowConsolesHidden").click();
        window.button("buttonShowNorth").click();

        try {
            window.button("buttonShowSouth").click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindPreferences() {
        
        window.button("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemPreferences").click();
        
        window.checkBox("checkboxIsParsingForm").check();
        window.checkBox("checkboxIsNotInjectingMetadata").check();
        window.checkBox("checkboxIsLimitingNormalIndex").check();
        window.checkBox("checkboxIsLimitingSleepTimeStrategy").check();
        window.checkBox("checkboxIsCheckingAllURLParam").check();
        window.checkBox("checkboxIsCheckingAllRequestParam").check();
        window.checkBox("checkboxIsCheckingAllHeaderParam").check();
        window.checkBox("checkboxIsCheckingAllJSONParam").check();
        window.checkBox("checkboxIsCheckingAllSOAPParam").check();
        window.checkBox("checkboxIsPerfIndexDisabled").check();
        window.checkBox("checkboxIsUrlEncodingDisabled").check();
        
        Assert.assertArrayEquals(
            new boolean[] {
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
            }, new boolean[] {
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
                MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled(),
            }
        );

        
        window.radioButton("radioIsZipStrategy").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy());
        
        window.radioButton("radioIsDefaultStrategy").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy());
        
        window.radioButton("radioIsDiosStrategy").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy());
        
        window.button("advancedButton").click();

        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }
    
    @Test
    public void shouldFindSqlEngine() {
        
        window.button("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemSqlEngine").click();

        try {
            window.button("advancedButton").click();
        } catch (Exception e) {
            Assert.fail();
        }
        
        GuiActionRunner.execute(() -> {
            
            window.tabbedPane("tabResults").target().removeTabAt(0);
        });
    }
    
    @Test
    public void shouldFindLanguage() {
        
        window.button("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("menuTranslation").click();
        window.menuItem("itemRussian").click();
        
        window.menuItem("menuWindows").click();
        window.menuItem("menuTranslation").click();
        window.menuItem("itemEnglish").click();

        try {
            window.button("advancedButton").click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindReportIssue() {
        
        window.button("advancedButton").click();
        window.menuItem("menuCommunity").click();
        window.menuItem("itemReportIssue").click();
        
        DialogFixture dialog = window.dialog();
        dialog.button(JButtonMatcher.withText("Cancel")).click();

        try {
            window.button("advancedButton").click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindIHelpTranslate() {
        
        window.button("advancedButton").click();
        window.menuItem("menuCommunity").click();
        window.menuItem("menuI18nContribution").click();
        window.menuItem("itemIntoFrench").click();
        
        DialogFixture dialog = window.dialog();
        dialog.close();

        try {
            window.button("advancedButton").click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindAbout() {
        
        window.button("advancedButton").click();
        window.menuItem("menuHelp").click();
        window.menuItem("itemHelp").click();
        
        DialogFixture dialog = window.dialog();
        dialog.button(JButtonMatcher.withText("Close")).click();
        
        try {
            window.button("advancedButton").click();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private static void initMockAdminPage() throws IOException {
        
        MockedStatic<Jsoup> utilities = Mockito.mockStatic(Jsoup.class);
            
        utilities.when(() -> Jsoup.connect(ArgumentMatchers.anyString())).thenReturn(connection);
        utilities.when(() -> Jsoup.clean(ArgumentMatchers.anyString(), ArgumentMatchers.any(Whitelist.class))).thenReturn("cleaned");
        
        Mockito.when(connection.ignoreContentType(ArgumentMatchers.anyBoolean())).thenReturn(connection);
        Mockito.when(connection.ignoreHttpErrors(ArgumentMatchers.anyBoolean())).thenReturn(connection);
        
        Mockito.when(connection.get()).thenReturn(document);
        Mockito.when(document.html()).thenReturn("<html><input/>test</html>");
        
        Mockito.when(document.text()).thenReturn("<html><input/>test</html>");
        utilities.when(() -> Jsoup.parse(Mockito.anyString())).thenReturn(document);
    }

    private static void verifyMockAdminPage() throws IOException {
        
        Mockito.verify(document, Mockito.times(1)).html();
        Mockito.verify(connection, Mockito.times(1)).get();
        Mockito.verify(connection, Mockito.times(1)).ignoreContentType(ArgumentMatchers.anyBoolean());
        Mockito.verify(connection, Mockito.times(1)).ignoreHttpErrors(ArgumentMatchers.anyBoolean());
    }
}
