/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.terminal;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import sun.swing.SwingUtilities2;

/**
 * A Terminal completely built from swing text pane.
 */
@SuppressWarnings("serial")
public abstract class AbstractTerminal extends JTextPane {
    /**
     * True if terminal is processing command.
     */
    public boolean[] isEdited = {false};

    /**
     * Server name or IP to display on prompt.
     */
    private String host;

    /**
     * User and password for database.
     */
    protected String[] loginPassword = null;

    UUID terminalID;
    String wbhPath;

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractTerminal.class);

    /**
     * Build a shell instance.
     * @param terminalID Unique identifier to discriminate beyond multiple opened terminals
     * @param shellURL URL of current shell
     * @param shellLabel Type of shell to display on prompt
     */
    public AbstractTerminal(UUID terminalID, String shellURL, String shellLabel) {
        this.terminalID = terminalID;
        this.wbhPath = shellURL;

        this.shellLabel = shellLabel;

        URL u = null;
        try {
            u = new URL(shellURL);
        } catch (MalformedURLException e) {
            LOGGER.warn("URL is malformed: no protocol", e);
        }
        host = u.getHost();

        this.setFont(new Font("monospaced", Font.PLAIN, ((Font) UIManager.get("TextArea.font")).getSize() + 1));
        this.setCaret(new BlockCaret());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.LIGHT_GRAY);

        // Disable antialisaing
        this.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, null);

        this.displayPrompt(true);

        this.setCursor(null);
        this.setTransferHandler(null);
        this.setHighlighter(null);

        this.addMouseListener(new EmptyFocus());
        this.addKeyListener(new KeyAdapterTerminal(this));
    }

    /**
     * Update terminal and use default behavior.
     */
    public void reset() {
        AbstractTerminal.this.isEdited[0] = false;
        AbstractTerminal.this.setEditable(true);
        AbstractTerminal.this.displayPrompt();
        AbstractTerminal.this.setCaretPosition(AbstractTerminal.this.getDocument().getLength());
        AbstractTerminal.this.setCursor(null);
    }

    /**
     * Get index of line for current offset (generally cursor position).
     * @param offset Position on the line
     * @return Index of the line
     * @throws BadLocationException
     */
    public int getLineOfOffset(int offset) throws BadLocationException {
        String errorMsg = "Can't translate offset to line";
        Document doc = this.getDocument();
        if (offset < 0) {
            throw new BadLocationException(errorMsg, -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException(errorMsg, doc.getLength() + 1);
        } else {
            Element map = doc.getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    /**
     * Get position of the beginning of the line.
     * @param line Index of the line
     * @return Offset of line
     * @throws BadLocationException
     */
    public int getLineStartOffset(int line) throws BadLocationException {
        Element map = AbstractTerminal.this.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", AbstractTerminal.this.getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    /**
     * Add a text at the end of textpane.
     * @param string Text to add
     */
    public void append(String string) {
        try {
            Document doc = this.getDocument();
            doc.insertString(doc.getLength(), string, null);
        } catch (BadLocationException e) {
            LOGGER.error(e, e);
        }
    }
    
    /**
     * Document used to append colored text.
     */
    private StyledDocument styledDocument = this.getStyledDocument();

    /**
     * Style used for coloring text.
     */
    private Style style = this.addStyle("Necrophagist's next album is 2014.", null);

    /**
     * Style getter.
     * @return Style for document
     */
    public Style getStyle() {
        return style;
    }

    /**
     *  Length of prompt.
     */
    String prompt = "";

    /**
     * Simply display colored prompt.
     */
    public void displayPrompt() {
        displayPrompt(false);
    }

    /**
     * Text to display next caret.
     */
    private String shellLabel;

    /**
     * Append prompt to textpane, measure prompt the first time is used.
     * @param measurePrompt Should we measure prompt length?
     */
    public void displayPrompt(boolean measurePrompt) {
        StyleConstants.setUnderline(style, true);
        appendPrompt("jsql", Color.LIGHT_GRAY, measurePrompt);
        StyleConstants.setUnderline(style, false);

        appendPrompt(" " + this.shellLabel, Color.LIGHT_GRAY, measurePrompt);
        appendPrompt("[", new Color(50, 191, 50), measurePrompt);
        appendPrompt(host, new Color(191, 191, 25), measurePrompt);
        appendPrompt("]", new Color(50, 191, 50), measurePrompt);
        appendPrompt(" >", new Color(191, 100, 100), measurePrompt);
        appendPrompt(" ", Color.LIGHT_GRAY, measurePrompt);
    }

    /**
     * Add a colored string to the textpane, measure prompt at the same time.
     * @param string Text to append
     * @param color Color of text
     * @param measurePrompt Should we measure prompt length?
     */
    private void appendPrompt(String string, Color color, boolean measurePrompt) {
        try {
            StyleConstants.setForeground(style, color);
            styledDocument.insertString(styledDocument.getLength(), string, style);
            if (measurePrompt) {
                prompt += string;
            }
        } catch (BadLocationException e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Cancel every mouse click, only gives focus.
     */
    private class EmptyFocus implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {
            e.consume();
            AbstractTerminal.this.requestFocusInWindow();
            AbstractTerminal.this.setCaretPosition(AbstractTerminal.this.getDocument().getLength());
        }
        @Override public void mouseReleased(MouseEvent e) {
            e.consume();
        }
        @Override public void mouseExited(MouseEvent e) {
            e.consume();
        }
        @Override public void mouseEntered(MouseEvent e) {
            e.consume();
        }
        @Override public void mouseClicked(MouseEvent e) {
            e.consume();
        }
    }

    /*
     * NoWrap.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }

    /**
     * Cancel every mouse movement processing like drag/drop.
     */
    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        // Do nothing
    }

    /**
     * Run when cmd is validated.
     * @param cmd Command to execute
     * @param terminalID Unique ID for terminal instance
     * @param wbhPath URL of shell
     * @param arg Additional parameters (User and password for SQLShell)
     */
    abstract void action(String cmd, UUID terminalID, String wbhPath, String... arg);
}
