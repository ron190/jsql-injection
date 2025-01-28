package com.jsql.view.swing.text;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.popupmenu.JPopupMenuComponent;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SyntaxTextArea extends RSyntaxTextArea {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String placeholderText;

    public SyntaxTextArea() {
        this(StringUtils.EMPTY);
    }

    public SyntaxTextArea(String text) {
        this.placeholderText = text;

        this.setPopupMenu(new JPopupMenuComponent(this));
        this.getCaret().setBlinkRate(0);
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                SyntaxTextArea.this.getCaret().setVisible(true);
                SyntaxTextArea.this.getCaret().setSelectionVisible(true);
            }
        });
        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        this.setMarkOccurrences(true);
        this.setMarkOccurrencesDelay(200);
    }

    @Override
    public void paint(Graphics g) {
        // Fix #6350: ArrayIndexOutOfBoundsException on paint()
        // Fix #90822: IllegalArgumentException on paint()
        // Fix #90761: StateInvariantError on paint()
        // StateInvariantError possible on jdk 8 when WrappedPlainView.drawLine in paint()
        try {
            super.paint(g);
            if (StringUtils.isEmpty(this.getText()) && StringUtils.isNotEmpty(this.placeholderText)) {
                UiUtil.drawPlaceholder(this, g, this.placeholderText);
            }
        } catch (IllegalArgumentException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    @Override
    public Font getFont() {
        return UiUtil.FONT_MONO_NON_ASIAN;
    }
    @Override
    public Font getFontForToken(Token token) {
        return UiUtil.FONT_MONO_NON_ASIAN;
    }
    @Override
    public Font getFontForTokenType(int type) {
        return UiUtil.FONT_MONO_NON_ASIAN;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }
}
