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

public class Application_UiTest extends AssertJSwingJUnitTestCase {
    
    private JFrame frame;

    @Override
    protected void onSetUp() {
        
        InjectionModel injectionModel = new InjectionModel();
        MediatorHelper.register(injectionModel);
        frame = GuiActionRunner.execute(() -> new JFrameView());
    }

    @Test
    public void shoulFindOkButton() {
        
        FrameFixture window = new FrameFixture(robot(), frame);
        window.show(new Dimension(600, 400));
        window.resizeTo(new Dimension(800, 600));
        window.button(withName("BUTTON_START_TOOLTIP").andText("")).click();
    }
}
