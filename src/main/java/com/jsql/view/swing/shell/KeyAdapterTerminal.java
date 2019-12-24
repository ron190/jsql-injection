/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.shell;

import java.awt.event.InputEvent;
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
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * Terminal where keys are processed.
     */
    private AbstractShell terminal;

    /**
     * Past commands entered by user.
     */
    private List<String> cmds = new ArrayList<>();

    /**
     * Current position in array of past commands.
     */
    private int cmdsIndex = 0;

    /**
     * Create a keyboard processor for a terminal.
     * @param terminal Terminal where keys are processed
     */
    public KeyAdapterTerminal(AbstractShell terminal) {
        this.terminal = terminal;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            final Element root = this.terminal.getDocument().getDefaultRootElement();
            final int caretPosition = this.terminal.getCaretPosition();
    
            // Get current line
            int lineNumber = this.terminal.getLineOfOffset(caretPosition);
    
            // Cancel every user keyboard input if another command has just been send
            if (this.terminal.getIsEdited()[0]) {
                keyEvent.consume();
                return;
            }
    
            // Get user input
            final String[] command = {""};
            command[0] =
                this.terminal.getText(
                    root.getElement(lineNumber).getStartOffset(),
                    root.getElement(lineNumber).getEndOffset() - root.getElement(lineNumber).getStartOffset()
                ).replace(this.terminal.getPrompt(), "");
    
            // Validate user input ; disable text editing
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                this.terminal.getIsEdited()[0] = true;
                keyEvent.consume();
                this.terminal.setEditable(false);
    
                // Populate cmd list for key up/down
                if (!"".equals(command[0].trim())) {
                    this.cmds.add(command[0].trim());
                    this.cmdsIndex = this.cmds.size();
                }
    
                // SwingUtilities instead of Thread to avoid some flickering
                // Thread to give back control of the GUI to the user (SwingUtilities does not)
                new Thread(() -> {
                    
                    AbstractShell terminalCommand = KeyAdapterTerminal.this.terminal;
                    
                    // Inside Swing thread to avoid flickering
                    terminalCommand.append("\n");
                    if (!"".equals(command[0].trim())) {
                        terminalCommand.setCaretPosition(terminalCommand.getDocument().getLength());
                        terminalCommand.action(
                            command[0],
                            terminalCommand.getUuidShell(),
                            terminalCommand.getUrlShell(),
                            terminalCommand.loginPassword
                        );
                    } else {
                        terminalCommand.reset();
                    }
                    
                }).start();
    
            // Get previous command
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                keyEvent.consume();
    
                if (this.cmdsIndex > 0) {
                    this.cmdsIndex--;
                }
    
                if (!this.cmds.isEmpty()) {
                    if (
                        this.cmds.size() > 1 &&
                        this.cmdsIndex == this.cmds.size() - 1 &&
                        !"".equals(command[0].trim())
                    ) {
                        this.cmdsIndex--;
                    }
    
                    this.terminal.getDocument().remove(
                        root.getElement(lineNumber).getStartOffset() + this.terminal.getPrompt().length(),
                        command[0].length() - 1
                    );
    
                    this.terminal.append(this.cmds.get(this.cmdsIndex));
                    this.terminal.setCaretPosition(this.terminal.getDocument().getLength());
                }
    
            // Get next command
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                keyEvent.consume();
    
                if (this.cmdsIndex < this.cmds.size()) {
                    this.cmdsIndex++;
                }
    
                if (!this.cmds.isEmpty() && this.cmdsIndex < this.cmds.size()) {
                    this.terminal.getDocument().remove(
                        root.getElement(lineNumber).getStartOffset() + this.terminal.getPrompt().length(),
                        command[0].length() - 1
                    );
    
                    this.terminal.append(this.cmds.get(this.cmdsIndex));
                    this.terminal.setCaretPosition(this.terminal.getDocument().getLength());
                }
    
            // Go to the left until prompt
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                int columnnum = caretPosition - this.terminal.getLineStartOffset(lineNumber);

                if (columnnum <= this.terminal.getPrompt().length()) {
                    keyEvent.consume();
                }
    
            // Get to the beginning of the line
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_HOME) {
                keyEvent.consume();
                
                this.terminal.setCaretPosition(this.terminal.getLineStartOffset(lineNumber) + this.terminal.getPrompt().length());
    
            } else if (
                // Cancel the select all shortcut Ctrl+A
                keyEvent.getKeyCode() == KeyEvent.VK_A && (keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0 ||
                // Cancel the *beep* sound if deleting while at the end of line
                keyEvent.getKeyCode() == KeyEvent.VK_DELETE && caretPosition == this.terminal.getDocument().getLength() ||
                (keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0 && (keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0 ||
                keyEvent.getKeyCode() == KeyEvent.VK_PAGE_UP ||
                keyEvent.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
                keyEvent.getKeyCode() == KeyEvent.VK_TAB
            ) {
                keyEvent.consume();
    
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_C && (keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                keyEvent.consume();
    
                this.terminal.append("\n");
                this.terminal.reset();
            }
        } catch (BadLocationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
}