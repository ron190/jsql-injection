/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.terminal.util;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.terminal.AbstractExploit;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Keyboard key processing for terminal.
 */
public class KeyAdapterTerminal extends KeyAdapter {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Terminal where keys are processed.
     */
    private final AbstractExploit terminal;

    /**
     * Past commands entered by user.
     */
    private final List<String> commandsHistory = new ArrayList<>();

    /**
     * Current position in array of past commands.
     */
    private int indexCommandsHistory = 0;

    /**
     * Create a keyboard processor for a terminal.
     * @param terminal Terminal where keys are processed
     */
    public KeyAdapterTerminal(AbstractExploit terminal) {
        this.terminal = terminal;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            var root = this.terminal.getDocument().getDefaultRootElement();
            int caretPosition = this.terminal.getCaretPosition();
            int lineNumber = this.terminal.getLineOfOffset(caretPosition);  // Get current line

            if (this.terminal.getIsEdited().get()) {  // Cancel every user keyboard input if another command has just been sent
                keyEvent.consume();
                return;
            }
    
            // Get user input
            var command = new AtomicReference<>(StringUtils.EMPTY);
            command.set(
                this.terminal.getText(
                    root.getElement(lineNumber).getStartOffset(),
                    root.getElement(lineNumber).getEndOffset() - root.getElement(lineNumber).getStartOffset()
                )
                .replace(this.terminal.getPrompt(), StringUtils.EMPTY)
                .trim()
            );
    
            if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {  // Validate user input ; disable text editing
                this.runCommand(keyEvent, command);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {  // Get previous command
                this.appendPreviousCommand(keyEvent, root, lineNumber, command);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {  // Get next command
                this.appendNextCommand(keyEvent, root, lineNumber, command);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {  // Go to the left until prompt
                this.moveCaretLeft(keyEvent, caretPosition, lineNumber);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_HOME) {  // Get to the beginning of the line
                this.moveCaretHome(keyEvent, lineNumber);
            } else if (this.isKeyNotAllowed(keyEvent, caretPosition)) {
                keyEvent.consume();
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_C && (keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                this.cancelCommand(keyEvent);
            }
        } catch (BadLocationException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }
    }

    private boolean isKeyNotAllowed(KeyEvent keyEvent, int caretPosition) {
        return
            // Cancel the select all shortcut Ctrl+A
            keyEvent.getKeyCode() == KeyEvent.VK_A && (keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0
            // Cancel the *beep* sound if deleting while at the end of line
            || keyEvent.getKeyCode() == KeyEvent.VK_DELETE && caretPosition == this.terminal.getDocument().getLength()
            || (keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0 && (keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0
            || keyEvent.getKeyCode() == KeyEvent.VK_PAGE_UP
            || keyEvent.getKeyCode() == KeyEvent.VK_PAGE_DOWN
            || keyEvent.getKeyCode() == KeyEvent.VK_TAB;
    }

    private void cancelCommand(KeyEvent keyEvent) {
        keyEvent.consume();
        this.terminal.append("\n");
        this.terminal.reset();
    }

    private void moveCaretHome(KeyEvent keyEvent, int lineNumber) throws BadLocationException {
        keyEvent.consume();
        this.terminal.setCaretPosition(this.terminal.getLineStartOffset(lineNumber) + this.terminal.getPrompt().length());
    }

    private void moveCaretLeft(KeyEvent keyEvent, int caretPosition, int lineNumber) throws BadLocationException {
        int newCaretPosition = caretPosition - this.terminal.getLineStartOffset(lineNumber);
        if (newCaretPosition <= this.terminal.getPrompt().length()) {
            keyEvent.consume();
        }
    }

    private void appendNextCommand(
        KeyEvent keyEvent,
        Element root,
        int lineNumber,
        AtomicReference<String> command
    ) throws BadLocationException {
        keyEvent.consume();
        if (this.indexCommandsHistory < this.commandsHistory.size()) {
            this.indexCommandsHistory++;
        }
        this.terminal.getDocument().remove(  // remove any prompt command before appending history, or start empty when last
            root.getElement(lineNumber).getStartOffset() + this.terminal.getPrompt().length(),
            command.get().length()
        );
        if (this.indexCommandsHistory < this.commandsHistory.size()) {
            this.terminal.append(this.commandsHistory.get(this.indexCommandsHistory));
        }
        this.terminal.setCaretPosition(this.terminal.getDocument().getLength());
    }

    private void appendPreviousCommand(
        KeyEvent keyEvent,
        Element root,
        int lineNumber,
        AtomicReference<String> command
    ) throws BadLocationException {
        keyEvent.consume();
        if (this.indexCommandsHistory > 0) {
            this.indexCommandsHistory--;
        }
        this.terminal.getDocument().remove(  // remove any prompt command before appending history, also when first
            root.getElement(lineNumber).getStartOffset() + this.terminal.getPrompt().length(),
            command.get().length()
        );
        if (!this.commandsHistory.isEmpty()) {
            this.terminal.append(this.commandsHistory.get(this.indexCommandsHistory));
        }
        this.terminal.setCaretPosition(this.terminal.getDocument().getLength());
    }

    private void runCommand(KeyEvent keyEvent, AtomicReference<String> command) {
        this.terminal.getIsEdited().set(true);
        keyEvent.consume();
        this.terminal.setEditable(false);
   
        // Populate cmd list for key up/down
        if (StringUtils.isNotEmpty(command.get())) {
            this.commandsHistory.add(command.get());
            this.indexCommandsHistory = this.commandsHistory.size();
        }

        new SwingWorker<>() {  // Thread to give back control to user (SwingUtilities does not)
            @Override
            protected Object doInBackground() {
                // Inside Swing thread to avoid flickering
                Thread.currentThread().setName("SwingWorkerKeyAdapterTerminal");

                AbstractExploit terminalCommand = KeyAdapterTerminal.this.terminal;
                terminalCommand.append("\n");
                
                if (StringUtils.isNotEmpty(command.get())) {
                    terminalCommand.setCaretPosition(terminalCommand.getDocument().getLength());
                    terminalCommand.action(
                        command.get(),
                        terminalCommand.getUuidShell(),
                        terminalCommand.getUrlShell(),
                        terminalCommand.getLoginPassword()
                    );
                } else {
                    terminalCommand.reset();
                }
                return null;
            }
        }.execute();
    }
}