package com.jsql.mvc.view.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.UUID;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;

import com.jsql.mvc.model.InjectionModel;


public class CustomTerminal extends JTextArea{
    private static final long serialVersionUID = 7020035385573317805L;
    
    @Override public void addMouseMotionListener(MouseMotionListener l){}
    
    public final String prompt = "webshell:~$ ";
    public final boolean[] isEdited = {false};
    
    public CustomTerminal(final String observerEventData, final InjectionModel a, final UUID l, final String wbhPath){
        super(8, 32);
        this.setFont(new Font("Lucida Console",Font.PLAIN,((Font) UIManager.get("TextArea.font")).getSize()));
        this.setCaret(new BlockCaret());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.LIGHT_GRAY);
        this.append(prompt);
        this.setCaretPosition(this.getDocument().getLength());
        this.setCursor(null);
        this.setLineWrap(true);
        this.setTransferHandler(null);

        this.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
                CustomTerminal.this.requestFocusInWindow();
                CustomTerminal.this.setCaretPosition(CustomTerminal.this.getText().length());
            }
            @Override public void mouseReleased(MouseEvent e) { e.consume(); }
            @Override public void mouseExited(MouseEvent e) { e.consume(); }
            @Override public void mouseEntered(MouseEvent e) { e.consume(); }
            @Override public void mouseClicked(MouseEvent e) { e.consume(); }
        });

        this.setHighlighter(null);
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke){
                if(isEdited[0]){
                    ke.consume();
                    return;
                }
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    isEdited[0] = true;
                    ke.consume();
                    CustomTerminal.this.getCaret().setBlinkRate(0);
                    CustomTerminal.this.setEditable(false);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int start = CustomTerminal.this.getLineStartOffset(CustomTerminal.this.getLineCount() - 1);
                                int end = CustomTerminal.this.getLineEndOffset(CustomTerminal.this.getLineCount() - 1);
                                String lineText = CustomTerminal.this.getText(start, end - start).replace(prompt, "");
                                if(!lineText.trim().equals("") && lineText!=null){
                                    CustomTerminal.this.append("\n");
                                    a.executeShell(observerEventData, lineText, l, wbhPath);
                                }else{
                                  isEdited[0] = false;
                                  CustomTerminal.this.setEditable(true);
                                  CustomTerminal.this.setCaret(new BlockCaret());
                                  CustomTerminal.this.setCaretPosition(CustomTerminal.this.getDocument().getLength());
                                }
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }).start();
                }else if(ke.getKeyCode() == KeyEvent.VK_UP || 
                         ke.getKeyCode() == KeyEvent.VK_PAGE_UP || 
                         ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN ||
                         ke.getKeyCode() == KeyEvent.VK_TAB){
                    ke.consume();
                }else if(ke.getKeyCode() == KeyEvent.VK_LEFT){
                    try {
                        int caretpos = CustomTerminal.this.getCaretPosition();
                        int linenum = 1;
                        int columnnum = 1;
                        linenum = CustomTerminal.this.getLineOfOffset(caretpos);
                        columnnum = caretpos - CustomTerminal.this.getLineStartOffset(linenum);
                        if(columnnum<=prompt.length())
                            ke.consume();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }else if(ke.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    try {
                        int caretpos = CustomTerminal.this.getCaretPosition();
                        int linenum = 1;
                        int columnnum = 1;
                        linenum = CustomTerminal.this.getLineOfOffset(caretpos);
                        columnnum = caretpos - CustomTerminal.this.getLineStartOffset(linenum);
                        if(columnnum<=prompt.length())
                            ke.consume();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }else if(ke.getKeyCode() == KeyEvent.VK_HOME){
                    try {
                        ke.consume();
                        int caretpos = CustomTerminal.this.getCaretPosition();
                        int linenum = 1;
                        linenum = CustomTerminal.this.getLineOfOffset(caretpos);
                        CustomTerminal.this.setCaretPosition(CustomTerminal.this.getLineStartOffset(linenum) + prompt.length());
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
