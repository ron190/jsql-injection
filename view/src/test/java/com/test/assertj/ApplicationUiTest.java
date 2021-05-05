package com.test.assertj;

import static org.assertj.swing.core.matcher.JButtonMatcher.withText;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.util.MediatorHelper;

public class ApplicationUiTest {
    
    private static FrameFixture window;
    private static JFrameView frame;
    
    @BeforeClass
    public static void setUpOnce() {
        
        FailOnThreadViolationRepaintManager.install();
        
        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);
        frame = GuiActionRunner.execute(() -> new JFrameView());
        
        window = new FrameFixture(frame);
        
        injectionModel.addObserver(frame.getObserver());
    }

    @Test
    public void shouldFindWebshell() throws IOException, InterruptedException {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_SHELL_TAB);
        request.setParameters("http://webshell", "http://webshell/path");
        MediatorHelper.model().sendToViews(request);
        
        window.tabbedPane("tabResults").selectTab("Web shell ").requireVisible();
    }

    @Test
    public void shouldFindSqlshell() throws IOException, InterruptedException {
        
        var request = new Request();
        request.setMessage(Interaction.CREATE_SQL_SHELL_TAB);
        request.setParameters("http://sqlshell", "http://sqlshell/path", "username", "password");
        MediatorHelper.model().sendToViews(request);
        
        window.tabbedPane("tabResults").selectTab("SQL shell ").requireVisible();
    }
    
    @Test
    public void shouldFindAdminpage() throws IOException {
        
        try (MockedStatic<Jsoup> utilities = Mockito.mockStatic(Jsoup.class)) {
            
            var request = new Request();
            request.setMessage(Interaction.CREATE_ADMIN_PAGE_TAB);
            request.setParameters("http://adminpage");
            MediatorHelper.model().sendToViews(request);
            
            Connection connection = Mockito.mock(Connection.class);
            utilities.when(() -> Jsoup.connect(ArgumentMatchers.anyString())).thenReturn(connection);
            
            Mockito.when(connection.ignoreContentType(ArgumentMatchers.anyBoolean())).thenReturn(connection);
            Mockito.when(connection.ignoreHttpErrors(ArgumentMatchers.anyBoolean())).thenReturn(connection);
            
            Document document = Mockito.mock(Document.class);
            Mockito.when(connection.get()).thenReturn(document);
            Mockito.when(document.html()).thenReturn("<html>test</html>");
            
            Mockito.verify(document, Mockito.times(2));
            
            window.tabbedPane("tabResults").selectTab("adminpage ").requireVisible();
        }
    }
    
    @Test
    public void shouldFindDatabase() {
        
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
        
        window.tabbedPane("tabResults").selectTab(nameTable).requireVisible();
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

        try {
            window.button("advancedButton").click();
        } catch (Exception e) {
            Assert.fail();
        }
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
    }
    
    @Test
    public void shouldFindLanguage() {
        
        window.button("advancedButton").click();
        window.menuItem("menuWindows").click();
        window.menuItem("menuTranslation").click();
        window.menuItem("itemRussian").click();

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
}
