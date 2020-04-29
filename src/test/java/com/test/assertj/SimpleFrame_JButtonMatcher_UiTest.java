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

public class SimpleFrame_JButtonMatcher_UiTest extends AssertJSwingJUnitTestCase {
    
    private JFrame frame;

    @Override
    protected void onSetUp() {
        System.out.println("1ho!");
        InjectionModel injectionModel = new InjectionModel();
        System.out.println("2ho!");
        MediatorHelper.register(injectionModel);
        System.out.println("3ho!");
        frame = GuiActionRunner.execute(() -> new JFrameView());
        System.out.println("4ho!");
    }

    @Test
    public void shoulFindOkButton() {
        System.out.println("5ho!");
        FrameFixture window = new FrameFixture(robot(), frame);
        System.out.println("6ho!");
        window.show(new Dimension(600, 400));
        window.resizeTo(new Dimension(800, 600));
        window.maximize();
        System.out.println("7ho!");
        window.button(withName("BUTTON_START_TOOLTIP").andText("")).click();
        System.out.println("8ho!");
    }
}
