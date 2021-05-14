package com.test.assertj;

import static org.assertj.swing.core.matcher.JButtonMatcher.withText;
import static org.junit.Assert.assertEquals;

import java.awt.AWTException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.util.Strings;
import org.assertj.swing.core.MouseButton;
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
    public void shouldDnDList() throws AWTException {

        window.tabbedPane("tabManagers").selectTab("Admin page");
        Assert.assertEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assert.assertNotEquals("admin", window.list("listManagerAdminPage").valueAt(1));
        window.list("listManagerAdminPage").drag(0);
        window.list("listManagerAdminPage").drop(1);
        Assert.assertNotEquals("admin", window.list("listManagerAdminPage").valueAt(0));
        Assert.assertEquals("admin", window.list("listManagerAdminPage").valueAt(1));
    }
    
//    @Test
//    public void shouldDnDTabs() throws AWTException {
//        
//        var request = new Request();
//        request.setMessage(Interaction.CREATE_FILE_TAB);
//        request.setParameters("dragfile", "content", "path");
//        MediatorHelper.model().sendToViews(request);
//        
//        request = new Request();
//        request.setMessage(Interaction.CREATE_FILE_TAB);
//        request.setParameters("jumpfile", "content", "path");
//        MediatorHelper.model().sendToViews(request);
//        
//        request = new Request();
//        request.setMessage(Interaction.CREATE_FILE_TAB);
//        request.setParameters("dropfile", "content", "path");
//        MediatorHelper.model().sendToViews(request);
//        
//        window.tabbedPane("tabResults").requireTitle("dragfile ", Index.atIndex(0));
//        window.tabbedPane("tabResults").requireTitle("jumpfile ", Index.atIndex(1));
//        window.tabbedPane("tabResults").requireTitle("dropfile ", Index.atIndex(2));
//        
//        window.robot().pressMouse(
//            window.label("dragfile").target(), 
//            window.label("dragfile").target().getLocation()
//        );
//        
//        window.robot().moveMouse(window.label("dragfile").target());  // required
//        window.label("dropfile").drop();
//        
//        window.tabbedPane("tabResults").requireTitle("jumpfile ", Index.atIndex(0));
//        window.tabbedPane("tabResults").requireTitle("dragfile ", Index.atIndex(1));
//        window.tabbedPane("tabResults").requireTitle("dropfile ", Index.atIndex(2));
//        
//        window.label("dragfile").click(MouseButton.MIDDLE_BUTTON);
//        window.label("jumpfile").click(MouseButton.MIDDLE_BUTTON);
//        window.label("dropfile").click(MouseButton.MIDDLE_BUTTON);
//    }
    
    @Test
    public void shouldFindFile() {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_FILE_TAB);
        request.setParameters("file", "content", "path");
        MediatorHelper.model().sendToViews(request);
        
        window.tabbedPane("tabResults").selectTab("file ").requireVisible();
        
        window.label("file").click(MouseButton.MIDDLE_BUTTON);
    }
    
    @Test
    public void shouldFindWebshell() {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_SHELL_TAB);
        request.setParameters("http://webshell", "http://webshell/path");
        MediatorHelper.model().sendToViews(request);
        
        window.tabbedPane("tabResults").selectTab("Web shell ").requireVisible();
        
        window.label("Web shell").click(MouseButton.MIDDLE_BUTTON);
    }

    @Test
    public void shouldFindSqlshell() {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_SQL_SHELL_TAB);
        request.setParameters("http://sqlshell", "http://sqlshell/path", "username", "password");
        MediatorHelper.model().sendToViews(request);
        
        window.tabbedPane("tabResults").selectTab("SQL shell ").requireVisible();
        
        window.label("SQL shell").click(MouseButton.MIDDLE_BUTTON);
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
        
//        window.textBox("textInputManagerCoder").setText("YQ==");
//        window.menuItem("menuMethodManagerCoder").click();
//        window.menuItem("decodeFromBase64").focus();
//        window.textBox("resultManagerCoder").requireText(Pattern.compile(".*<span><font[^>]*>a</font></span>.*", Pattern.DOTALL));
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
        
        window.label("adminpage").click(MouseButton.MIDDLE_BUTTON);
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
        
        assertEquals(nameDatabase +" (1 table)", window.tree("treeDatabases").valueAt(0));
        
        
        var nameTable = "table";
        Table table = new Table(nameTable, "2", database);
        
        var requestTable = new Request();
        requestTable.setMessage(Interaction.ADD_TABLES);
        requestTable.setParameters(Arrays.asList(table));
        MediatorHelper.model().sendToViews(requestTable);
        
        assertEquals(nameTable +" (2 rows)", window.tree("treeDatabases").valueAt(1));
        
        
        var nameColumn0 = "column 0";
        var nameColumn1 = "column 1";
        Column column1 = new Column(nameColumn0, table);
        Column column2 = new Column(nameColumn1, table);
        
        var request = new Request();
        request.setMessage(Interaction.ADD_COLUMNS);
        request.setParameters(Arrays.asList(column1, column2));
        MediatorHelper.model().sendToViews(request);
        
        assertEquals(nameColumn0, window.tree("treeDatabases").valueAt(2));
        assertEquals(nameColumn1, window.tree("treeDatabases").valueAt(3));
        
        
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
        
        window.tabbedPane("tabResults").requireVisible();
        window.tabbedPane("tabResults").requireTitle(nameTable, Index.atIndex(0));
        window.tabbedPane("tabResults").selectTab(nameTable).requireVisible();
        
        
        window.tree("treeDatabases").rightClickRow(0);
        window.tabbedPane("tabResults").click();
        window.tree("treeDatabases").rightClickRow(1);
        
        window.label("table").click(MouseButton.MIDDLE_BUTTON);
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
        
        window.checkBox("checkboxIsNotInjectingMetadata").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isNotInjectingMetadata());
        
        window.checkBox("checkboxIsParsingForm").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isParsingForm());
        
        window.checkBox("checkboxIsCheckingAllURLParam").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllURLParam());
        
        window.checkBox("checkboxIsCheckingAllRequestParam").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllRequestParam());
        
        window.checkBox("checkboxIsCheckingAllHeaderParam").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllHeaderParam());
        
        window.checkBox("checkboxIsCheckingAllJSONParam").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllJsonParam());
        
//        window.checkBox("checkboxIsCheckingAllBase64Param").check();
//        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllBase64Param());
        
//        window.checkBox("checkboxIsCheckingAllCookieParam").check();
//        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllCookieParam());
        
        window.checkBox("checkboxIsCheckingAllSOAPParam").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam());
        
        window.checkBox("checkboxIsPerfIndexDisabled").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isPerfIndexDisabled());
        
        window.radioButton("radioIsZipStrategy").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isZipStrategy());
        
        window.radioButton("radioIsDefaultStrategy").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDefaultStrategy());
        
        window.radioButton("radioIsDiosStrategy").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isDiosStrategy());
        
        window.checkBox("checkboxIsUrlEncodingDisabled").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isUrlEncodingDisabled());
        
        window.checkBox("checkboxIsLimitingNormalIndex").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isLimitingNormalIndex());
        
        window.checkBox("checkboxIsSleepTimeStrategy").check();
        Assert.assertTrue(MediatorHelper.model().getMediatorUtils().getPreferencesUtil().isSleepTimeStrategy());

        try {
            window.button("advancedButton").click();
        } catch (Exception e) {
            Assert.fail();
        }
        
        window.label("Preferences").click(MouseButton.MIDDLE_BUTTON);
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
        
        window.label("SQL Engine").click(MouseButton.MIDDLE_BUTTON);
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
        dialog.button(withText("Cancel")).click();

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
        dialog.button(withText("Close")).click();
        
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
