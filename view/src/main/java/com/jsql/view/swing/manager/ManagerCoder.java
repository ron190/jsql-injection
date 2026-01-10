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

import com.jsql.util.I18nUtil;
import com.jsql.util.bruter.ActionCoder;
import com.jsql.util.bruter.Coder;
import com.jsql.view.swing.manager.util.CoderListener;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerEditing;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.JSplitPaneWithZeroSizeDivider;
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
    private final JTextArea result;

    /**
     * Encoding choice by user.
     */
    private JLabel menuMethod;

    private static final String ENCODE_TO = "Encode to ";
    private final transient CoderListener actionCoder = new CoderListener(this);

    private class ChangeMenuListener implements ChangeListener {
        private final String nameMethod;
        ChangeMenuListener(String nameMethod) {
            this.nameMethod = nameMethod;
        }
        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() instanceof JMenuItem item && (item.isSelected() || item.isArmed())) {
                ManagerCoder.this.actionCoder.actionPerformed(this.nameMethod);
            }
        }
    }

    /**
     * Create a panel to encode a string.
     */
    public ManagerCoder() {
        super(new BorderLayout());

        var placeholderInput = new JTextAreaPlaceholder(I18nUtil.valueByKey("CODER_INPUT"));
        this.textInput = new JPopupTextArea(placeholderInput).getProxy();
        I18nViewUtil.addComponentForKey("CODER_INPUT", placeholderInput);
        this.textInput.getCaret().setBlinkRate(500);
        this.textInput.setLineWrap(true);
        this.textInput.setName("textInputManagerCoder");
        this.textInput.getDocument().addDocumentListener(new DocumentListenerEditing() {
            @Override
            public void process() {
                ManagerCoder.this.actionCoder.actionPerformed();
            }
        });

        JPanel topMixed = this.getTopPanel();

        var placeholderResult = new JTextAreaPlaceholder(I18nUtil.valueByKey("CODER_RESULT"));
        this.result = new JPopupTextArea(placeholderResult).getProxy();
        I18nViewUtil.addComponentForKey("CODER_RESULT", placeholderResult);
        this.result.setName("resultManagerCoder");
        this.result.setLineWrap(true);
        this.result.setEditable(false);

        var bottom = new JPanel(new BorderLayout());
        bottom.add(new JScrollPane(this.result), BorderLayout.CENTER);

        var divider = new JSplitPaneWithZeroSizeDivider(JSplitPane.VERTICAL_SPLIT);
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
        
        mapMenus.put(Coder.BASE16.label, new JMenu());
        mapMenus.put(Coder.BASE32.label, new JMenu());
        mapMenus.put(Coder.BASE58.label, new JMenu());
        mapMenus.put(Coder.BASE64.label, new JMenu());
        mapMenus.put(Coder.HEX.label, new JMenu());
        mapMenus.put(Coder.URL.label, new JMenu());
        mapMenus.put(Coder.UNICODE.label, new JMenu());
        var menuHtml = new JMenu();
        mapMenus.put(Coder.HTML.label, menuHtml);
        mapMenus.put(Coder.BASE64_ZIP.label, new JMenu());
        mapMenus.put(Coder.HEX_ZIP.label, new JMenu());

        var menuEncodeHtmlDecimal = new JMenuItem(ManagerCoder.ENCODE_TO + Coder.HTML_DECIMAL.label);
        menuHtml.add(menuEncodeHtmlDecimal);
        menuEncodeHtmlDecimal.addActionListener(this.actionCoder);
        menuEncodeHtmlDecimal.addChangeListener(new ChangeMenuListener(ManagerCoder.ENCODE_TO + Coder.HTML_DECIMAL.label));
        
        mapMenus.forEach((label, menu) -> {
            var menuEncode = new JMenuItem(ManagerCoder.ENCODE_TO + label);
            menuEncode.addActionListener(this.actionCoder);
            menuEncode.addChangeListener(new ChangeMenuListener(ManagerCoder.ENCODE_TO + label));
            menuEncode.setName("encodeTo"+ label);

            var menuDecode = new JMenuItem("Decode from "+ label);
            menuDecode.addActionListener(this.actionCoder);
            menuDecode.addChangeListener(new ChangeMenuListener("Decode from "+ label));
            menuDecode.setName("decodeFrom"+ label);

            menu.setText(label);
            menu.add(menuEncode);
            menu.add(menuDecode);
            menu.setName(label);
        });

        mapMenus.put("Hash", new JMenu("Hash"));
        mapMenus.get("Hash").setName("Hash");

        ActionCoder.getHashes().forEach(hash -> {
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

        JLabel labelMenu = new JLabel(UiUtil.ARROW_DOWN.getIcon(), SwingConstants.LEFT);
        this.menuMethod = labelMenu;
        labelMenu.setText(ManagerCoder.ENCODE_TO + Coder.BASE64.label);
        labelMenu.setName("menuMethodManagerCoder");
        labelMenu.setBorder(UiUtil.BORDER_5PX);
        labelMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Arrays.stream(popupMenu.getComponents()).map(JMenu.class::cast).forEach(menu -> {
                    menu.updateUI();
                    for (var i = 0 ; i < menu.getItemCount() ; i++) {
                        menu.getItem(i).updateUI();
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

    public JTextArea getResult() {
        return this.result;
    }
}
