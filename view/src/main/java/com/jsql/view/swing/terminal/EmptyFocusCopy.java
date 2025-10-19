package com.jsql.view.swing.terminal;

import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;

/**
 * Cancel every mouse click, only gives focus.
 */
public class EmptyFocusCopy implements MouseListener {

    private static final Logger LOGGER = LogManager.getRootLogger();

    private final AbstractExploit abstractExploit;

    public EmptyFocusCopy(AbstractExploit abstractExploit) {
        this.abstractExploit = abstractExploit;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        e.consume();
        this.abstractExploit.requestFocusInWindow();
        this.abstractExploit.setCaretPosition(this.abstractExploit.getDocument().getLength());
        if (Arrays.asList(MouseEvent.BUTTON2, MouseEvent.BUTTON3).contains(e.getButton())) {
            this.pasteClipboard();
        }
    }

    private void pasteClipboard() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            this.abstractExploit.append(data);
        } catch (UnsupportedFlavorException | IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        e.consume();
    }
}