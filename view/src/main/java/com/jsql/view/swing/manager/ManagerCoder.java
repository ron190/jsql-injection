/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import com.jsql.view.swing.manager.util.CoderListener;
import com.jsql.view.swing.panel.util.HTMLEditorKitTextPaneWrap;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.UiUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Manager to code/decode string in various methods.
 */
public class ManagerCoder extends JPanel {
    
    /**
     * User input to encode.
     */
    private final JTextArea textInput;

    /**
     * JTextArea displaying result of encoding/decoding.
     */
    private final JTextPane result;

    /**
     * Encoding choice by user.
     */
    private JLabel menuMethod;

    private final transient CoderListener actionCoder = new CoderListener(this);

    public static final String[] HASHES = new String[]{
        "Adler32", "Crc16", "Crc32", "Crc64", "Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql"
    };

    public static final String[] HASHES_FOR_EMPTY_TEXT = new String[]{
        "Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql"
    };

    private class ChangeMenuListener implements ChangeListener {
        private final String nameMethod;
        
        ChangeMenuListener(String nameMethod) {
            this.nameMethod = nameMethod;
        }
        
        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) e.getSource();
                if (item.isSelected() || item.isArmed()) {
                    ManagerCoder.this.actionCoder.actionPerformed(this.nameMethod);
                }
            }
        }
    }

    /**
     * Create a panel to encode a string.
     */
    public ManagerCoder() {
        super(new BorderLayout());

        this.textInput = new JPopupTextArea(new JTextAreaPlaceholder("Type a text to convert")).getProxy();
        this.textInput.getCaret().setBlinkRate(500);
        this.textInput.setEditable(true);
        this.textInput.setLineWrap(true);
        this.textInput.setName("textInputManagerCoder");
        this.textInput.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                ManagerCoder.this.actionCoder.actionPerformed();
            }
        });

        JPanel topMixed = this.getTopPanel();

        this.result = new JPopupTextPane("Result of conversion").getProxy();
        this.result.setContentType("text/html");
        this.result.setFont(UIManager.getFont("TextArea.font"));  // required to increase text size
        this.result.setEditorKit(new HTMLEditorKitTextPaneWrap());
        this.result.setName("resultManagerCoder");
        
        var bottom = new JPanel(new BorderLayout());
        bottom.add(new JScrollPane(this.result), BorderLayout.CENTER);

        var divider = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        divider.setResizeWeight(0.5);
        divider.setTopComponent(topMixed);
        divider.setBottomComponent(bottom);
        this.add(divider, BorderLayout.CENTER);
    }

    private JPanel getTopPanel() {
        var comboMenubar = this.getLabelMenu();

        var topMixed = new JPanel(new BorderLayout());
        topMixed.add(new JScrollPane(this.textInput), BorderLayout.CENTER);
        topMixed.add(comboMenubar, BorderLayout.SOUTH);
        return topMixed;
    }

    private JLabel getLabelMenu() {
        Map<String, JMenu> mapMenus = new LinkedHashMap<>();
        
        mapMenus.put("Base16", new JMenu("Base16"));
        mapMenus.put("Base32", new JMenu("Base32"));
        mapMenus.put("Base58", new JMenu("Base58"));
        mapMenus.put("Base64", new JMenu("Base64"));
        mapMenus.put("Hex", new JMenu("Hex"));
        mapMenus.put("Url", new JMenu("Url"));
        mapMenus.put("Unicode", new JMenu("Unicode"));
        
        var menuHtml = new JMenu("Html");
        mapMenus.put("Html", menuHtml);
        mapMenus.put("Base64(zipped)", new JMenu("Base64(zipped)"));
        mapMenus.put("Hex(zipped)", new JMenu("Hex(zipped)"));

        var menuEncodeHtmlDecimal = new JMenuItem("Encode to Html (decimal)");
        menuHtml.add(menuEncodeHtmlDecimal);
        menuEncodeHtmlDecimal.addActionListener(this.actionCoder);
        menuEncodeHtmlDecimal.addChangeListener(new ChangeMenuListener("Encode to Html (decimal)"));
        
        mapMenus.forEach((key, value) -> {
            var menuEncode = new JMenuItem("Encode to " + key);
            menuEncode.addActionListener(this.actionCoder);
            menuEncode.addChangeListener(new ChangeMenuListener("Encode to " + key));
            menuEncode.setName("encodeTo" + key);

            var menuDecode = new JMenuItem("Decode from " + key);
            menuDecode.addActionListener(this.actionCoder);
            menuDecode.addChangeListener(new ChangeMenuListener("Decode from " + key));
            menuDecode.setName("decodeFrom" + key);

            value.add(menuEncode);
            value.add(menuDecode);
            value.setName(key);
        });

        mapMenus.put("Hash", new JMenu("Hash"));
        mapMenus.get("Hash").setName("Hash");
        
        Stream.of(ManagerCoder.HASHES).forEach(hash -> {
            var menuEncode = new JMenuItem("Hash to "+ hash);
            menuEncode.addActionListener(this.actionCoder);
            menuEncode.addChangeListener(new ChangeMenuListener("Hash to "+ hash));
            menuEncode.setName("hashTo"+ hash);
            mapMenus.get("Hash").add(menuEncode);
        });

        JPopupMenu popupMenu = new JPopupMenu();
        for (JMenu menu: mapMenus.values()) {
            popupMenu.add(menu);
        }

        JLabel labelMenu = new JLabel(UiUtil.ARROW_DOWN.icon, SwingConstants.LEFT);
        this.menuMethod = labelMenu;
        labelMenu.setText("Encode to Base64");
        labelMenu.setName("menuMethodManagerCoder");
        labelMenu.setBorder(UiUtil.BORDER_5PX);
        labelMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(popupMenu.getComponents()).map(component -> (JMenu) component).forEach(jMenu -> {
                    jMenu.updateUI();
                    for (var i = 0 ; i < jMenu.getItemCount() ; i++) {
                        jMenu.getItem(i).updateUI();
                    }
                });  // required: incorrect when dark/light mode switch
                popupMenu.updateUI();  // required: incorrect when dark/light mode switch
                popupMenu.show(e.getComponent(), e.getComponent().getX(),e.getComponent().getY() + e.getComponent().getHeight());
                popupMenu.setLocation(e.getComponent().getLocationOnScreen().x,e.getComponent().getLocationOnScreen().y + e.getComponent().getHeight());
            }
        });
        return labelMenu;
    }
    
    
    // Getter and setter

    public JTextArea getTextInput() {
        return this.textInput;
    }

    public JLabel getMenuMethod() {
        return this.menuMethod;
    }

    public JTextPane getResult() {
        return this.result;
    }
}
