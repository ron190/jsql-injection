/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.terminal;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.apache.log4j.Logger;

/**
 * Keyboard key processing for terminal.
 */
public class KeyAdapterTerminal extends KeyAdapter {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(KeyAdapterTerminal.class);

    /**
     * Terminal where keys are processed.
     */
    private AbstractTerminal terminal;

    /**
     * Past commands entered by user.
     */
    private List<String> cmds = new ArrayList<String>();

    /**
     * Current position in array of past commands.
     */
    private int cmdsIndex = 0;

    /**
     * Create a keyboard processor for a terminal.
     * @param terminal Terminal where keys are processed
     */
    public KeyAdapterTerminal(AbstractTerminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        final Element root = terminal.getDocument().getDefaultRootElement();
        final int caretpos = terminal.getCaretPosition();

        // Get current line
        int linenum = 0;
        try {
            linenum = terminal.getLineOfOffset(caretpos);
        } catch (BadLocationException e) {
            LOGGER.error(e, e);
        }

        // Cancel every user keyboard input if another command has just been send
        if (terminal.isEdited[0]) {
            ke.consume();
            return;
        }

        // Get user input
        final String[] cmd = {""};
        try {
            cmd[0] = terminal.getText(root.getElement(linenum).getStartOffset(),
                    root.getElement(linenum).getEndOffset() - root.getElement(linenum).getStartOffset())
                    .replace(terminal.prompt, "");
        } catch (BadLocationException e) {
            LOGGER.error(e, e);
        }

        // Validate user input ; disable text editing
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            terminal.isEdited[0] = true;
            ke.consume();
            terminal.setEditable(false);

            // Populate cmd list for key up/down
            if (!"".equals(cmd[0].trim())) {
                this.cmds.add(cmd[0].trim());
                this.cmdsIndex = this.cmds.size();
            }

            // SwingUtilities instead of Thread to avoid some flickering
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // Inside Swing thread to avoid flickering
                    terminal.append("\n");
                    if (!"".equals(cmd[0].trim())) {
                        terminal.setCaretPosition(terminal.getDocument().getLength());
                        terminal.action(cmd[0], terminal.terminalID, terminal.wbhPath, terminal.loginPassword);
                    } else {
                        terminal.reset();
                    }
                }
            });

            // Get previous command
        } else if (ke.getKeyCode() == KeyEvent.VK_UP) {
            ke.consume();

            if (this.cmdsIndex > 0) {
                this.cmdsIndex--;
            }

            if (!this.cmds.isEmpty()) {
                if (this.cmds.size() > 1 && this.cmdsIndex == this.cmds.size() - 1 && !"".equals(cmd[0].trim())) {
                    this.cmdsIndex--;
                }

                try {
                    terminal.getDocument().remove(root.getElement(linenum).getStartOffset() + terminal.prompt.length(), cmd[0].length() - 1);
                } catch (BadLocationException e) {
                    LOGGER.error(e, e);
                }

                terminal.append(this.cmds.get(this.cmdsIndex));
                terminal.setCaretPosition(terminal.getDocument().getLength());
            }

            // Get next command
        } else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
            ke.consume();

            if (this.cmdsIndex < this.cmds.size()) {
                this.cmdsIndex++;
            }

            if (!this.cmds.isEmpty() && this.cmdsIndex < this.cmds.size()) {
                try {
                    terminal.getDocument().remove(root.getElement(linenum).getStartOffset() + terminal.prompt.length(), cmd[0].length() - 1);
                } catch (BadLocationException e) {
                    LOGGER.error(e, e);
                }

                terminal.append(this.cmds.get(this.cmdsIndex));
                terminal.setCaretPosition(terminal.getDocument().getLength());
            }

            // Simply cancel text shortcuts
        } else if (ke.getKeyCode() == KeyEvent.VK_PAGE_UP
                || ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN
                || ke.getKeyCode() == KeyEvent.VK_TAB) {
            ke.consume();

            // Go to the left until prompt
        } else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            int columnnum = 1;
            try {
                columnnum = caretpos - terminal.getLineStartOffset(linenum);
            } catch (BadLocationException e) {
                LOGGER.error(e, e);
            }
            if (columnnum <= terminal.prompt.length()) {
                ke.consume();
            }

            // Delete to the left until prompt
        } else if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            try {
                int columnnum = caretpos - terminal.getLineStartOffset(linenum);
                if (columnnum <= terminal.prompt.length()) {
                    ke.consume();
                }
            } catch (BadLocationException e) {
                LOGGER.error(e, e);
            }

            // Get to the beginning of the line
        } else if (ke.getKeyCode() == KeyEvent.VK_HOME) {
            ke.consume();
            try {
                terminal.setCaretPosition(terminal.getLineStartOffset(linenum) + terminal.prompt.length());
            } catch (BadLocationException e) {
                LOGGER.error(e, e);
            }

            // Cancel the select all shortcut Ctrl+A
        } else if ((ke.getKeyCode() == KeyEvent.VK_A) && ((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            ke.consume();

            // Cancel the *ting* sound if deleting while at the end of line
        } else if (ke.getKeyCode() == KeyEvent.VK_DELETE && caretpos == terminal.getDocument().getLength()) {
            ke.consume();

        } else if (((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0) && ((ke.getModifiers() & KeyEvent.SHIFT_MASK) != 0)) {
            ke.consume();

        } else if ((ke.getKeyCode() == KeyEvent.VK_C) && ((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            ke.consume();

            terminal.append("\n");
            terminal.reset();
        }
    }
}