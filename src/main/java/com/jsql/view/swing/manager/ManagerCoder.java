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
package com.jsql.view.swing.manager;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jsql.view.swing.manager.util.ActionCoder;
import com.jsql.view.swing.manager.util.MenuBarCoder;
import com.jsql.view.swing.panel.util.HTMLEditorKitTextPaneWrap;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.text.listener.DocumentListenerTyping;

/**
 * Manager to code/uncode string in various methods.
 */
@SuppressWarnings("serial")
public class ManagerCoder extends JPanel implements Manager {
    
    /**
     * User input to encode.
     */
    private JTextArea textInput;

    /**
     * Encoding choosed by user.
     */
    private JMenuItem menuMethod;

    /**
     * JTextArea displaying result of encoding/decoding.
     */
    private JTextPane result;
    
    private transient ActionCoder actionCoder = new ActionCoder(this);
    
    private class ChangeMenuListener implements ChangeListener {
        
        String nameMethod;
        
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

        this.textInput = new JPopupTextArea(new JTextAreaPlaceholder("Type a string to convert")).getProxy();
        this.textInput.setEditable(true);
        this.textInput.setLineWrap(true);
        
        this.textInput.getDocument().addDocumentListener(new DocumentListenerTyping() {
            
            @Override
            public void warn() {
                ManagerCoder.this.actionCoder.actionPerformed();
            }
            
        });

        JPanel topMixed = new JPanel(new BorderLayout());

        final JPanel middleLine = new JPanel();
        middleLine.setLayout(new BorderLayout());
        middleLine.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 1));

        Map<String, JMenu> menus = new LinkedHashMap<>();
        menus.put("Base64", new JMenu("Base64"));
        menus.put("Hex", new JMenu("Hex"));
        menus.put("Url", new JMenu("Url"));
        JMenu menuHtml = new JMenu("Html");
        menus.put("Html", menuHtml);
        menus.put("Base64(zipped)", new JMenu("Base64(zipped)"));
        menus.put("Hex(zipped)", new JMenu("Hex(zipped)"));

        JMenuItem menuEncodeHtmlDecimal = new JMenuItem("Encode to Html (decimal)");
        menuHtml.add(menuEncodeHtmlDecimal);
        menuEncodeHtmlDecimal.addActionListener(this.actionCoder);
        menuEncodeHtmlDecimal.addChangeListener(new ChangeMenuListener("Encode to Html (decimal)"));
        
        for (Entry<String, JMenu> entryMap: menus.entrySet()) {
            JMenuItem menuEncode = new JMenuItem("Encode to "+ entryMap.getKey());
            menuEncode.addActionListener(this.actionCoder);
            menuEncode.addChangeListener(new ChangeMenuListener("Encode to "+ entryMap.getKey()));
            
            JMenuItem menuDecode = new JMenuItem("Decode from "+ entryMap.getKey());
            menuDecode.addActionListener(this.actionCoder);
            menuDecode.addChangeListener(new ChangeMenuListener("Decode from "+ entryMap.getKey()));
            
            entryMap.getValue().add(menuEncode);
            entryMap.getValue().add(menuDecode);
        }

        menus.put("Hash", new JMenu("Hash"));
        
        for (
            String hash:
            new String[]{"Adler32", "Crc16", "Crc32", "Crc64", "Md2", "Md4", "Md5", "Sha-1", "Sha-256", "Sha-384", "Sha-512", "Mysql"}
        ) {
            JMenuItem menuEncode = new JMenuItem("Hash to "+ hash);
            menuEncode.addActionListener(this.actionCoder);
            menuEncode.addChangeListener(new ChangeMenuListener("Hash to "+ hash));
            
            menus.get("Hash").add(menuEncode);
        }

        JMenu comboMenu = MenuBarCoder.createMenu("Choose method...");
        this.menuMethod = comboMenu;
        
        for (JMenu menu: menus.values()) {
            comboMenu.add(menu);
        }

        MenuBarCoder comboMenubar = new MenuBarCoder(comboMenu);
        comboMenubar.setOpaque(false);
        comboMenubar.setBorder(null);
        
        this.menuMethod.setText("Encode to Base64");
        
        middleLine.add(comboMenubar);

        topMixed.add(new LightScrollPane(1, 0, 1, 0, this.textInput), BorderLayout.CENTER);
        topMixed.add(middleLine, BorderLayout.SOUTH);

        this.result = new JPopupTextPane("Result of conversion").getProxy();
        this.result.setContentType("text/html");
        this.result.setEditorKit(new HTMLEditorKitTextPaneWrap());
        
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new LightScrollPane(1, 0, 0, 0, this.result), BorderLayout.CENTER);

        JSplitPaneWithZeroSizeDivider divider = new JSplitPaneWithZeroSizeDivider(JSplitPane.VERTICAL_SPLIT);
        divider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        divider.setDividerSize(0);
        divider.setResizeWeight(0.5);

        divider.setTopComponent(topMixed);
        divider.setBottomComponent(bottom);

        this.add(divider, BorderLayout.CENTER);
    }
    
    // Getter and setter

    public JTextArea getTextInput() {
        return this.textInput;
    }

    public JMenuItem getMenuMethod() {
        return this.menuMethod;
    }

    public JTextPane getResult() {
        return this.result;
    }
    
}
