/*******************************************************************************
 * Copyhacked (H) 2012-2013.
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import sun.swing.SwingUtilities2;

import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;

/**
 * A Terminal built from scratch.
 */
@SuppressWarnings("serial")
public abstract class Terminal extends JTextPane{
    
    private final boolean[] isEdited = {false};

    private ArrayList<String> cmds = new ArrayList<String>();
    private int cmdsIndex = 0;
    private String host;
    
    /**
     * NoWrap
     */
    @Override
    public boolean getScrollableTracksViewportWidth()
    {
        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }
    
    public Terminal(UUID terminalID, String wbhPath, String shellLabel){
        this.shellLabel= shellLabel; 
        
        URL u = null;
        try {
            u = new URL(wbhPath);
        } catch (MalformedURLException e) {
            GUIMediator.model().sendDebugMessage(e);
        }
        host = u.getHost();
        
        this.setFont(new Font("monospaced",Font.PLAIN,((Font) UIManager.get("TextArea.font")).getSize()+1));
        this.setCaret(new BlockCaret());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.LIGHT_GRAY);

        // Disable antialisaing
        putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, null);

        this.displayPrompt(true);

        this.setCursor(null);
        this.setTransferHandler(null);
        this.setHighlighter(null);

        this.addMouseListener(new EmptyFocus());
        this.addKeyListener(new TerminalKey(GUIMediator.model(), terminalID, wbhPath));
    }
    
    String[] args = null;
    
    /**
     * Keyboard key processing.
     */
    private class TerminalKey extends KeyAdapter{
        private UUID terminalID;
        private String wbhPath;
        private InjectionModel model;
        
        public TerminalKey(InjectionModel newModel, UUID newTerminalID, String newWbhPath){
            terminalID = newTerminalID;
            wbhPath = newWbhPath;
            model = newModel;
        }
        
        public void keyPressed(KeyEvent ke){
            final Element root = Terminal.this.getDocument().getDefaultRootElement();
            final int caretpos = Terminal.this.getCaretPosition();
            
            // Get current line
            int linenum = 0;
            try {
                linenum = Terminal.this.getLineOfOffset(caretpos);
            } catch (BadLocationException e) {
                this.model.sendDebugMessage(e);
            }

            // Cancel every user keyboard input if another command has just been send
            if(isEdited[0]){
                ke.consume();
                return;
            }
            
            // Get user input
            final String[] cmd = {""};
            try {
                cmd[0] = Terminal.this.getText(root.getElement(linenum).getStartOffset(),
                        root.getElement(linenum).getEndOffset() - root.getElement(linenum).getStartOffset())
                        .replace(prompt, "");
            } catch (BadLocationException e) {
                this.model.sendDebugMessage(e);
            }
            
            // Validate user input ; disable text editing
            if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                isEdited[0] = true;
                ke.consume();
                Terminal.this.setEditable(false);
                
                // Populate cmd list for key up/down
                if(!cmd[0].trim().equals("")){
                    cmds.add(cmd[0].trim());
                    cmdsIndex = cmds.size();
                }
                
                // SwingUtilities instead of Thread to avoid some flickering
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Terminal.this.append("\n"); // Inside Swing thread to avoid flickering
                        if(!cmd[0].trim().equals("")){
                            Terminal.this.setCaretPosition(Terminal.this.getDocument().getLength());
                            Terminal.this.action(cmd[0], terminalID, wbhPath, args);
                        }else{
                            Terminal.this.reset();
                        }
                    }
                });
            
            // Get previous command
            }else if(ke.getKeyCode() == KeyEvent.VK_UP){
                ke.consume();
                
                if(cmdsIndex > 0)
                    cmdsIndex--;
                
                if(cmds.size() > 0){
                    if(cmds.size() > 1 && cmdsIndex == cmds.size() - 1 && !cmd[0].trim().equals(""))
                        cmdsIndex--;
                    
                    try {
                        Terminal.this.getDocument().remove(root.getElement(linenum).getStartOffset() + prompt.length(), cmd[0].length()-1);
                    } catch (BadLocationException e) {
                        GUIMediator.model().sendDebugMessage(e);
                    }
                    
                    Terminal.this.append(cmds.get(cmdsIndex));
                    Terminal.this.setCaretPosition(Terminal.this.getDocument().getLength());
                }

            // Get next command
            }else if(ke.getKeyCode() == KeyEvent.VK_DOWN){
                ke.consume();
                
                if(cmdsIndex < cmds.size())
                    cmdsIndex++;
                    
                if(cmds.size() > 0 && cmdsIndex < cmds.size()){
                    try {
                        Terminal.this.getDocument().remove(root.getElement(linenum).getStartOffset() + prompt.length(), cmd[0].length()-1);
                    } catch (BadLocationException e) {
                        GUIMediator.model().sendDebugMessage(e);
                    }
                    
                    Terminal.this.append(cmds.get(cmdsIndex));
                    Terminal.this.setCaretPosition(Terminal.this.getDocument().getLength());
                }
                
            // Simply cancel text shortcuts
            }else if(ke.getKeyCode() == KeyEvent.VK_PAGE_UP ||
                    ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
                    ke.getKeyCode() == KeyEvent.VK_TAB){
                ke.consume();
                
            // Go to the left until prompt
            }else if(ke.getKeyCode() == KeyEvent.VK_LEFT){
                int columnnum = 1;
                try {
                    columnnum = caretpos - Terminal.this.getLineStartOffset(linenum);
                } catch (BadLocationException e) {
                    this.model.sendDebugMessage(e);
                }
                if(columnnum <= prompt.length())
                    ke.consume();

            // Delete to the left until prompt
            }else if(ke.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                try {
                    int columnnum = caretpos - Terminal.this.getLineStartOffset(linenum);
                    if(columnnum <= prompt.length())
                        ke.consume();
                } catch (BadLocationException e) {
                    this.model.sendDebugMessage(e);
                }
                
            // Get to the beginning of the line
            }else if(ke.getKeyCode() == KeyEvent.VK_HOME){
                ke.consume();
                try {
                    Terminal.this.setCaretPosition(Terminal.this.getLineStartOffset(linenum)+prompt.length() );
                } catch (BadLocationException e) {
                    this.model.sendDebugMessage(e);
                }
                
            // Cancel the select all shortcut Ctrl+A
            }else if ((ke.getKeyCode() == KeyEvent.VK_A) && ((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                ke.consume();
                
            // Cancel the *ting* sound if deleting while at the end of line
            }else if (ke.getKeyCode() == KeyEvent.VK_DELETE && caretpos == Terminal.this.getDocument().getLength()) {
                ke.consume();
                
            }else if (((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0) && ((ke.getModifiers() & KeyEvent.SHIFT_MASK) != 0)) {
                ke.consume();
                
            }else if ((ke.getKeyCode() == KeyEvent.VK_C) && ((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                ke.consume();
                
                Terminal.this.append("\n");
                Terminal.this.reset();
            }
        }
    }
    
    public void reset(){
        Terminal.this.isEdited[0] = false;
        Terminal.this.setEditable(true);
        Terminal.this.displayPrompt();
        Terminal.this.setCaretPosition(Terminal.this.getDocument().getLength());
        Terminal.this.setCursor(null);
    }

    /**
     * Get index of line for current offset (generally cursor position).
     * @param offset Position on the line
     * @return Index of the line
     * @throws BadLocationException
     */
    public int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = Terminal.this.getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
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
        Element map = Terminal.this.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", Terminal.this.getDocument().getLength() + 1);
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
            Document doc = Terminal.this.getDocument();
            doc.insertString(doc.getLength(), string, null);
        } catch(BadLocationException e) {
        	GUIMediator.model().sendDebugMessage(e);
        }
    }
    public void appendStyle(String string) {
        try {
            Document doc = Terminal.this.getDocument();
            doc.insertString(doc.getLength(), string, style);
        } catch(BadLocationException e) {
        	GUIMediator.model().sendDebugMessage(e);
        }
    }

    /**
     * Document used to append colored text.
     */
    private StyledDocument doc = this.getStyledDocument();
    
    /**
     * Style used for coloring text.
     */
    public Style style = this.addStyle("Necrophagist's next album is 2014.", null);
    
    /**
     *  Length of prompt.
     */
    private String prompt = "";
    
    /**
     * Simply display colored prompt.
     */
    public void displayPrompt() {
        displayPrompt(false);
    }
    
    protected String shellLabel;
    
    /**
     * Append prompt to textpane, measure prompt the first time is used.
     * @param measurePrompt Should we measure prompt length?
     */
    public void displayPrompt(boolean measurePrompt) {
        StyleConstants.setUnderline(style, true);
        appendPrompt("jsql", Color.LIGHT_GRAY, measurePrompt);
        StyleConstants.setUnderline(style, false);
        
        appendPrompt(" "+shellLabel, Color.LIGHT_GRAY, measurePrompt);
        appendPrompt("[", new Color(50,191,50), measurePrompt);
        appendPrompt(host, new Color(191,191,25), measurePrompt);
        appendPrompt("]", new Color(50,191,50), measurePrompt);
        appendPrompt(" >", new Color(191,100,100), measurePrompt);
        appendPrompt(" ", Color.LIGHT_GRAY, measurePrompt);
    }
    
    /**
     * Add a colored string to the textpane, measure prompt at the same time.
     * @param string Text to append
     * @param color Color of text
     * @param measurePrompt Should we measure prompt length?
     */
    private void appendPrompt(String string, Color color, boolean measurePrompt){
        try {
            StyleConstants.setForeground(style, color);
            doc.insertString(doc.getLength(), string, style);
            if(measurePrompt)
                prompt += string;
        } catch (BadLocationException e) {
        	GUIMediator.model().sendDebugMessage(e);
        }
    }
    
    /**
     * Cancel every mouse click, only gives focus.
     */
    private class EmptyFocus implements MouseListener{
        @Override
        public void mousePressed(MouseEvent e) {
            e.consume();
            Terminal.this.requestFocusInWindow();
            Terminal.this.setCaretPosition(Terminal.this.getDocument().getLength());
        }
        @Override public void mouseReleased(MouseEvent e) { e.consume(); }
        @Override public void mouseExited(MouseEvent e) { e.consume(); }
        @Override public void mouseEntered(MouseEvent e) { e.consume(); }
        @Override public void mouseClicked(MouseEvent e) { e.consume(); }
    }
    
    /**
     * Cancel every mouse movement processing like drag/drop.
     */
    @Override synchronized public void addMouseMotionListener(MouseMotionListener l){}
    
    /**
     * Run when cmd is validated
     * @param cmd Command to execute
     * @param terminalID Unique ID for terminal instance
     * @param wbhPath URL of shell
     * @param arg Additional parameters (User and password for SQLShell)
     */
    abstract void action(String cmd, UUID terminalID, String wbhPath, String... arg);
}
