package com.test.assertj;

import static org.assertj.swing.core.matcher.JButtonMatcher.withName;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import com.jsql.model.InjectionModel;
import com.jsql.view.swing.JFrameView;
import com.jsql.view.swing.util.MediatorHelper;

public class ApplicationUiTest extends AssertJSwingJUnitTestCase {
    
    private JFrame frame;
    FrameFixture window;

    @Override
    protected void onSetUp() {
        
        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);
        frame = GuiActionRunner.execute(() -> new JFrameView());
        window = new FrameFixture(robot(), frame);
        window.show(new Dimension(600, 400));
        window.resizeTo(new Dimension(800, 600));
    }

    @Test
    public void shoulFindOkButton() {
        
        window.button(withName("buttonInUrl")).click();
    }
    
    @Test
    public void shoulFindPreferences() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemPreferences").click();
    }
    
    @Test
    public void shoulFindSqlEngine() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuWindows").click();
        window.menuItem("itemSqlEngine").click();
    }
    
    @Test
    public void shoulFindLanguage() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuWindows").click();
        window.menuItem("menuTranslation").click();
        window.menuItem("itemRussian").click();
    }
    
    @Test
    public void shoulFindReportIssue() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuCommunity").click();
        window.menuItem("itemReportIssue").click();
    }
    
    @Test
    public void shoulFindIHelpTranslate() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuCommunity").click();
        window.menuItem("menuI18nContribution").click();
        window.menuItem("itemIntoFrench").click();
    }
    
    @Test
    public void shoulFindAbout() {
        
        window.button(withName("advancedButton")).click();
        window.menuItem("menuHelp").click();
        window.menuItem("itemHelp").click();
    }
}
