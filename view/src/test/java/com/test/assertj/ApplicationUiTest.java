package com.test.assertj;

import static org.assertj.swing.core.matcher.JButtonMatcher.withName;
import static org.assertj.swing.core.matcher.JButtonMatcher.withText;

import javax.swing.JFrame;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.util.MediatorHelper;

public class ApplicationUiTest {
    
    private static FrameFixture window;
    private static JFrame frame;
    
    @BeforeClass
    public static void setUpOnce() {
        
        FailOnThreadViolationRepaintManager.install();
        
        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);
        frame = GuiActionRunner.execute(() -> new JFrameView());
        
        window = new FrameFixture(frame);
    }

    @Test
    public void shouldFindOkButton() {
        try {
            window.button(withName("buttonInUrl")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldFindConsoleButton() {
        
        window.button(withName("buttonShowSouth")).click();
        window.button(withName("buttonShowConsolesHidden")).click();
        window.button(withName("buttonShowNorth")).click();

        try {
            window.button(withName("buttonShowSouth")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindPreferences() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemPreferences").click();

        try {
            window.button(withName("advancedButton")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindSqlEngine() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemSqlEngine").click();

        try {
            window.button(withName("advancedButton")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindLanguage() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuWindows").click();
        window.menuItem("menuTranslation").click();
        window.menuItem("itemRussian").click();

        try {
            window.button(withName("advancedButton")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindReportIssue() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuCommunity").click();
        window.menuItem("itemReportIssue").click();
        
        DialogFixture dialog = window.dialog();
        dialog.button(withText("Cancel")).click();

        try {
            window.button(withName("advancedButton")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindIHelpTranslate() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuCommunity").click();
        window.menuItem("menuI18nContribution").click();
        window.menuItem("itemIntoFrench").click();
        
        DialogFixture dialog = window.dialog();
        dialog.close();

        try {
            window.button(withName("advancedButton")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void shouldFindAbout() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuHelp").click();
        window.menuItem("itemHelp").click();
        
        DialogFixture dialog = window.dialog();
        dialog.button(withText("Close")).click();
        
        try {
            window.button(withName("advancedButton")).click();
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
