/*******************************************************************************
 * Copyhacked (H) 2012-2020.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.view.swing.shell;

import com.jsql.util.LogLevelUtil;
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

/**
 * Keyboard key processing for terminal.
 */
public class KeyAdapterTerminal extends KeyAdapter {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * Terminal where keys are processed.
     */
    private final AbstractShell terminal;

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
    public KeyAdapterTerminal(AbstractShell terminal) {
        this.terminal = terminal;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        try {
            final var root = this.terminal.getDocument().getDefaultRootElement();
            final int caretPosition = this.terminal.getCaretPosition();
    
            // Get current line
            int lineNumber = this.terminal.getLineOfOffset(caretPosition);
    
            // Cancel every user keyboard input if another command has just been sent
            if (this.terminal.getIsEdited()[0]) {
                
                keyEvent.consume();
                return;
            }
    
            // Get user input
            final var command = new String[]{ StringUtils.EMPTY };
            command[0] = this.terminal.getText(
                    root.getElement(lineNumber).getStartOffset(),
                    root.getElement(lineNumber).getEndOffset() - root.getElement(lineNumber).getStartOffset()
                )
                .replace(this.terminal.getPrompt(), StringUtils.EMPTY);
    
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

    private boolean isKeyNotAllowed(KeyEvent keyEvent, final int caretPosition) {
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

    private void moveCaretLeft(KeyEvent keyEvent, final int caretPosition, int lineNumber) throws BadLocationException {
        
        int columnnum = caretPosition - this.terminal.getLineStartOffset(lineNumber);

        if (columnnum <= this.terminal.getPrompt().length()) {
            keyEvent.consume();
        }
    }

    private void appendNextCommand(
        KeyEvent keyEvent,
        final Element root,
        int lineNumber,
        final String[] command
    ) throws BadLocationException {
        
        keyEvent.consume();
   
        if (this.indexCommandsHistory < this.commandsHistory.size()) {
            this.indexCommandsHistory++;
        }
   
        if (!this.commandsHistory.isEmpty() && this.indexCommandsHistory < this.commandsHistory.size()) {
            
            this.terminal.getDocument().remove(
                root.getElement(lineNumber).getStartOffset() + this.terminal.getPrompt().length(),
                command[0].length() - 1
            );
   
            this.terminal.append(this.commandsHistory.get(this.indexCommandsHistory));
            this.terminal.setCaretPosition(this.terminal.getDocument().getLength());
        }
    }

    private void appendPreviousCommand(
        KeyEvent keyEvent,
        final Element root,
        int lineNumber,
        final String[] command
    ) throws BadLocationException {
        
        keyEvent.consume();
   
        if (this.indexCommandsHistory > 0) {
            this.indexCommandsHistory--;
        }
   
        if (!this.commandsHistory.isEmpty()) {
            
            if (
                this.commandsHistory.size() > 1
                && this.indexCommandsHistory == this.commandsHistory.size() - 1
                && StringUtils.isNotEmpty(command[0].trim())
            ) {
                this.indexCommandsHistory--;
            }
   
            this.terminal.getDocument().remove(
                root.getElement(lineNumber).getStartOffset() + this.terminal.getPrompt().length(),
                command[0].length() - 1
            );
   
            this.terminal.append(this.commandsHistory.get(this.indexCommandsHistory));
            this.terminal.setCaretPosition(this.terminal.getDocument().getLength());
        }
    }

    private void runCommand(KeyEvent keyEvent, final String[] command) {
        
        this.terminal.getIsEdited()[0] = true;
        keyEvent.consume();
        this.terminal.setEditable(false);
   
        // Populate cmd list for key up/down
        if (StringUtils.isNotEmpty(command[0].trim())) {
            
            this.commandsHistory.add(command[0].trim());
            this.indexCommandsHistory = this.commandsHistory.size();
        }
   
        // Thread to give back control of the GUI to the user (SwingUtilities does not)
        new SwingWorker<>() {
            
            @Override
            protected Object doInBackground() {
                
                Thread.currentThread().setName("SwingWorkerKeyAdapterTerminal");
                
                AbstractShell terminalCommand = KeyAdapterTerminal.this.terminal;
                
                // Inside Swing thread to avoid flickering
                terminalCommand.append("\n");
                
                if (StringUtils.isNotEmpty(command[0].trim())) {
                    
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
                
                return null;
            }
        }.execute();
    }
}