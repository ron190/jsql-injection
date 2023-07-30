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
package com.jsql.view.swing.panel.util;

import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A button displayed in address.
 */
public class ButtonExpandText extends JButton {

    /**
     * Create a button in address bar.
     */
    public ButtonExpandText(String titleFrame, JTextField textFieldHeader) {
        
        this.setPreferredSize(new Dimension(18, 16));
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);

        this.setIcon(UiUtil.ICON_EXPAND_TEXT);

        JTextArea textArea = new JPopupTextArea(new JTextAreaPlaceholder("Multiline text")).getProxy();
        textArea.getCaret().setBlinkRate(500);

        final JDialog frameWithTextarea = new JDialog(MediatorHelper.frame(), titleFrame, true);
        frameWithTextarea.getContentPane().add(new LightScrollPane(textArea));
        frameWithTextarea.pack();
        frameWithTextarea.setSize(400, 300);
        frameWithTextarea.setLocationRelativeTo(null);
        frameWithTextarea.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                textFieldHeader.setText(textArea.getText().replace("\n", "\\n").replace("\r", "\\r"));
                super.windowClosing(e);
            }
        });

        this.addActionListener(e -> {
            textArea.setText(textFieldHeader.getText().replace("\\n", "\n").replace("\\r", "\r"));
            frameWithTextarea.setVisible(!frameWithTextarea.isVisible());
        });

        frameWithTextarea
            .getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                "Cancel"
            );
        frameWithTextarea.getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                frameWithTextarea.dispose();
            }
        });
    }
}
