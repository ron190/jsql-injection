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


public class TestConsole extends JTextArea{
    private static final long serialVersionUID = 7020035385573317805L;
    
    @Override public void addMouseMotionListener(MouseMotionListener l){}
    
    public final String prompt = "webshell:~$ ";

    public final boolean[] isEdited = {false};
    
//    @Override
//    public void paintComponent (Graphics g)
//    {
//       Graphics2D g2d = (Graphics2D) g;
//       g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//       super.paintComponent (g2d);
//    }
    
    public TestConsole(final String observerEventData, final InjectionModel a, final UUID l, final String wbhPath){
        super(8, 32);
        this.setFont(new Font("Courier New",Font.PLAIN,((Font) UIManager.get("TextArea.font")).getSize()));
        this.setCaret(new BlockCaret2());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.LIGHT_GRAY);
        this.append(prompt);
        this.setCaretPosition(this.getDocument().getLength());
        this.setCursor(null);
//        this.putClientProperty(sun.swing.SwingUtilities2.AA_TEXT_PROPERTY_KEY, null);

        //    area.addCaretListener(new CaretListener() {
        //        public void caretUpdate(CaretEvent e) {
        //            
        //          int dot = e.getDot();
        //          System.out.println("dot is the caret position:" + dot);
        //
        //          int mark = e.getMark();
        //          System.out.println("mark is the non-caret end of the selection: " + mark);
        //          
        ////            try {
        ////          int caretpos = area.getCaretPosition();
        ////          int linenum = 1;
        ////          int columnnum = 1;
        ////                linenum = area.getLineOfOffset(caretpos);
        ////          columnnum = caretpos - area.getLineStartOffset(linenum);
        ////          if(columnnum<=3)
        ////              area.setCaretPosition(columnnum + area.getLineStartOffset(linenum) + 2);
        ////            } catch (BadLocationException e1) {
        ////                // TODO Auto-generated catch block
        ////                e1.printStackTrace();
        ////            }
        //        }
        //      });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
                TestConsole.this.requestFocusInWindow();
                TestConsole.this.setCaretPosition(TestConsole.this.getText().length());
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
                    TestConsole.this.getCaret().setBlinkRate(0);
                    TestConsole.this.setEditable(false);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                int start = TestConsole.this.getLineStartOffset(TestConsole.this.getLineCount() - 1);
                                int end = TestConsole.this.getLineEndOffset(TestConsole.this.getLineCount() - 1);
                                String lineText = TestConsole.this.getText(start, end - start).replace(prompt, "");
                                if(!lineText.trim().equals("") && lineText!=null){
                                    TestConsole.this.append("\n");
                                    a.executeShell(observerEventData, lineText, l, wbhPath);
                                }else{
                                  isEdited[0] = false;
                                  TestConsole.this.setEditable(true);
                                  TestConsole.this.setCaret(new BlockCaret2());
                                  TestConsole.this.setCaretPosition(TestConsole.this.getDocument().getLength());
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
                        int caretpos = TestConsole.this.getCaretPosition();
                        int linenum = 1;
                        int columnnum = 1;
                        linenum = TestConsole.this.getLineOfOffset(caretpos);
                        columnnum = caretpos - TestConsole.this.getLineStartOffset(linenum);
                        if(columnnum<=prompt.length())
                            ke.consume();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }else if(ke.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    try {
                        int caretpos = TestConsole.this.getCaretPosition();
                        int linenum = 1;
                        int columnnum = 1;
                        linenum = TestConsole.this.getLineOfOffset(caretpos);
                        columnnum = caretpos - TestConsole.this.getLineStartOffset(linenum);
                        if(columnnum<=prompt.length())
                            ke.consume();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }else if(ke.getKeyCode() == KeyEvent.VK_HOME){
                    try {
                        ke.consume();
                        int caretpos = TestConsole.this.getCaretPosition();
                        int linenum = 1;
                        linenum = TestConsole.this.getLineOfOffset(caretpos);
                        TestConsole.this.setCaretPosition(TestConsole.this.getLineStartOffset(linenum) + prompt.length());
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
