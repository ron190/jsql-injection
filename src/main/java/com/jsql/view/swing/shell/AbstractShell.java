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
package com.jsql.view.swing.shell;

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
public abstract class AbstractShell extends JTextPane {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractShell.class);

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

    UUID uuidShell;
    
    String urlShell;

    /**
     * Document used to append colored text.
     */
    private StyledDocument styledDocument = this.getStyledDocument();

    /**
     * Style used for coloring text.
     */
    private Style style = this.addStyle("Necrophagist's next album is 2014.", null);

    /**
     *  Length of prompt.
     */
    String prompt = "";

    /**
     * Text to display next caret.
     */
    private String labelShell;
    
    /**
     * Build a shell instance.
     * @param uuidShell Unique identifier to discriminate beyond multiple opened terminals
     * @param urlShell URL of current shell
     * @param labelShell Type of shell to display on prompt
     */
    public AbstractShell(UUID uuidShell, String urlShell, String labelShell) {
        this.uuidShell = uuidShell;
        this.urlShell = urlShell;
        this.labelShell = labelShell;

        URL url = null;
        try {
            url = new URL(urlShell);
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed URL : no protocol", e);
        }
        host = url.getHost();

        this.setFont(new Font("Ubuntu Mono", Font.PLAIN, ((Font) UIManager.get("TextPane.font")).getSize()));
        this.setCaret(new BlockCaret());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.LIGHT_GRAY);

        // Disable antialiasing
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
        this.isEdited[0] = false;
        this.setEditable(true);
        this.displayPrompt();
        this.setCaretPosition(this.getDocument().getLength());
        this.setCursor(null);
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
        Element map = this.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", this.getDocument().getLength() + 1);
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
     * Simply display colored prompt.
     */
    public void displayPrompt() {
        displayPrompt(false);
    }

    /**
     * Style getter.
     * @return Style for document
     */
    public Style getStyle() {
        return style;
    }
    
    /**
     * Append prompt to textpane, measure prompt the first time is used.
     * @param isAddingPrompt Should we measure prompt length?
     */
    public void displayPrompt(boolean isAddingPrompt) {
        StyleConstants.setUnderline(style, true);
        appendPrompt("jsql", Color.LIGHT_GRAY, isAddingPrompt);
        StyleConstants.setUnderline(style, false);

        appendPrompt(" " + this.labelShell, Color.LIGHT_GRAY, isAddingPrompt);
        appendPrompt("[", new Color(50, 191, 50), isAddingPrompt);
        appendPrompt(host, new Color(191, 191, 25), isAddingPrompt);
        appendPrompt("]", new Color(50, 191, 50), isAddingPrompt);
        appendPrompt(" >", new Color(191, 100, 100), isAddingPrompt);
        appendPrompt(" ", Color.LIGHT_GRAY, isAddingPrompt);
    }

    /**
     * Add a colored string to the textpane, measure prompt at the same time.
     * @param string Text to append
     * @param color Color of text
     * @param isAddingPrompt Should we measure prompt length?
     */
    private void appendPrompt(String string, Color color, boolean isAddingPrompt) {
        try {
            StyleConstants.setForeground(style, color);
            styledDocument.insertString(styledDocument.getLength(), string, style);
            if (isAddingPrompt) {
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
            AbstractShell.this.requestFocusInWindow();
            AbstractShell.this.setCaretPosition(AbstractShell.this.getDocument().getLength());
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
