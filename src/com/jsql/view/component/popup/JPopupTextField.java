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
package com.jsql.view.component.popup;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.jsql.view.GUITools;
import com.jsql.view.RoundBorder;


public class JPopupTextField extends JTextField {
    private static final long serialVersionUID = 3353663305066550031L;
    
    private boolean bigTextField = false;
    private boolean drawPic = false;
    BufferedImage image;
    int x0 = 0;

    public JPopupTextField(){
        initialize();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(drawPic){
            int y = (getHeight() - image.getHeight())/2;
            g.drawImage(image, x0, y, this);
        }
    }
    
    public JPopupTextField(boolean big) {
        bigTextField = big;
        initialize();
    }

    public JPopupTextField(String string, boolean bigTextField, boolean drawPic) {
        super(string);
        this.bigTextField = bigTextField;
        this.drawPic = drawPic;
        initialize();
    }

    public JPopupTextField(String string, boolean big) {
        super(string);
        bigTextField = big;
        initialize();
    }

    public JPopupTextField(String string) {
        super(string);
        initialize();
    }

    public void initialize(){
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                JPopupTextField.this.requestFocusInWindow();
            }
        });
        
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor ( "Panel.background" ), 2),
                GUITools.BLU_ROUND_BORDER));
        
        if(drawPic){
            URL url = this.getClass().getResource("/com/jsql/view/images/globe.png");
            try {
                image = ImageIO.read(url);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Border border = UIManager.getBorder("TextField.border");
            x0 = border.getBorderInsets(this).left + 4;
            this.setMargin(new Insets(0, x0 + image.getWidth() + 2, 0, 0));
        }
        
        this.setComponentPopupMenu(new JPopupTextComponentMenu(this, true));
        
        if(bigTextField){
            this.setPreferredSize(new Dimension(0, 26));
            Font plainFont = new Font(this.getFont().getName(),Font.PLAIN,this.getFont().getSize()+2);
            this.setFont(plainFont);
        }
        
        this.setDragEnabled(true);
        
        final UndoManager undo = new UndoManager();
        Document doc = this.getDocument();
        
        // Listen for undo and redo events
        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undo.addEdit(evt.getEdit());
            }
        });
        
        // Create an undo action and add it to the text component
        this.getActionMap().put("Undo",
            new AbstractAction("Undo") {
                private static final long serialVersionUID = 4306924502441733626L;

                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canUndo()) {
                            undo.undo();
                        }
                    } catch (CannotUndoException e) {
                    }
                }
           });
        
        // Bind the undo action to ctl-Z
        this.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        
        // Create a redo action and add it to the text component
        this.getActionMap().put("Redo",
            new AbstractAction("Redo") {
                private static final long serialVersionUID = 4771013112601866334L;

                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (undo.canRedo()) {
                            undo.redo();
                        }
                    } catch (CannotRedoException e) {
                    }
                }
            });
        
        // Bind the redo action to ctl-Y
        this.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
    }
}
