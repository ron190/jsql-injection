/*******************************************************************************
 * Copyhacked (H) 2012-2016.
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

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.scrollpane.LightScrollPane;

/**
 * A Terminal completely built from swing text pane.
 */
@SuppressWarnings("serial")
public abstract class AbstractShell extends JTextPane {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    /**
     * True if terminal is processing command.
     */
    private boolean[] isEdited = {false};

    /**
     * Server name or IP to display on prompt.
     */
    private String host;

    /**
     * User and password for database.
     */
    protected String[] loginPassword = null;

    private UUID uuidShell;
    
    private String urlShell;

    /**
     * Document used to append colored text.
     */
    private transient StyledDocument styledDocument = this.getStyledDocument();

    /**
     * Style used for coloring text.
     */
    private transient Style style = this.addStyle("Necrophagist's next album is 2014.", null);

    /**
     *  Length of prompt.
     */
    private String prompt = "";

    /**
     * Text to display next caret.
     */
    private String labelShell;
    
    /**
     * Build a shell instance.
     * @param uuidShell Unique identifier to discriminate beyond multiple opened terminals
     * @param urlShell URL of current shell
     * @param labelShell Type of shell to display on prompt
     * @throws MalformedURLException
     */
    public AbstractShell(UUID uuidShell, String urlShell, String labelShell) throws MalformedURLException {
        this.uuidShell = uuidShell;
        this.urlShell = urlShell;
        this.labelShell = labelShell;

        URL url = new URL(urlShell);
        this.host = url.getHost();

        this.setFont(new Font(HelperUi.FONT_NAME_UBUNTU_MONO, Font.PLAIN, ((Font) UIManager.get("TextPane.font")).getSize()));
        this.setCaret(new BlockCaret());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.LIGHT_GRAY);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, LightScrollPane.THUMB_SIZE, 0));

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
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    /**
     * Simply display colored prompt.
     */
    public void displayPrompt() {
        this.displayPrompt(false);
    }

    /**
     * Style getter.
     * @return Style for document
     */
    public Style getStyle() {
        return this.style;
    }
    
    /**
     * Append prompt to textpane, measure prompt the first time is used.
     * @param isAddingPrompt Should we measure prompt length?
     */
    public void displayPrompt(boolean isAddingPrompt) {
        StyleConstants.setUnderline(this.style, true);
        this.appendPrompt("jsql", Color.LIGHT_GRAY, isAddingPrompt);
        StyleConstants.setUnderline(this.style, false);

        this.appendPrompt(" " + this.labelShell, Color.LIGHT_GRAY, isAddingPrompt);
        this.appendPrompt("[", new Color(50, 191, 50), isAddingPrompt);
        this.appendPrompt(this.host, new Color(191, 191, 25), isAddingPrompt);
        this.appendPrompt("]", new Color(50, 191, 50), isAddingPrompt);
        this.appendPrompt(" >", new Color(191, 100, 100), isAddingPrompt);
        this.appendPrompt(" ", Color.LIGHT_GRAY, isAddingPrompt);
    }

    /**
     * Add a colored string to the textpane, measure prompt at the same time.
     * @param string Text to append
     * @param color Color of text
     * @param isAddingPrompt Should we measure prompt length?
     */
    private void appendPrompt(String string, Color color, boolean isAddingPrompt) {
        try {
            StyleConstants.setForeground(this.style, color);
            this.styledDocument.insertString(this.styledDocument.getLength(), string, this.style);
            if (isAddingPrompt) {
                this.prompt += string;
            }
        } catch (BadLocationException e) {
            LOGGER.error(e.getMessage(), e);
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
        return this.getUI().getPreferredSize(this).width <= this.getParent().getSize().width;
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

    // Getter and setter
    
    public boolean[] getIsEdited() {
        return this.isEdited;
    }

    public UUID getUuidShell() {
        return this.uuidShell;
    }

    public String getUrlShell() {
        return this.urlShell;
    }

    public String getPrompt() {
        return this.prompt;
    }
    
}
