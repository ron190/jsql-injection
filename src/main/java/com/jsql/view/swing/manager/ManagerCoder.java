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
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.manager.util.ActionCoder;
import com.jsql.view.swing.manager.util.MenuBarCoder;
import com.jsql.view.swing.panel.util.HTMLEditorKitTextPaneWrap;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.text.JPopupTextArea;
import com.jsql.view.swing.text.JPopupTextPane;
import com.jsql.view.swing.text.JTextAreaPlaceholder;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

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
    private JMenuItem encoding;

    /**
     * JTextArea displaying result of encoding/decoding.
     */
    private JTextPane result;

    /**
     * Create a panel to encode a string.
     */
    public ManagerCoder() {
        super(new BorderLayout());

        this.textInput = new JPopupTextArea(new JTextAreaPlaceholder("Type a string to convert")).getProxy();
        this.textInput.setEditable(true);
        this.textInput.setLineWrap(true);

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

        menuHtml.add(new JMenuItem("Encode to Html (decimal)"));
        for (Entry<String, JMenu> entryMap: menus.entrySet()) {
            entryMap.getValue().add(new JMenuItem("Encode to "+ entryMap.getKey()));
            entryMap.getValue().add(new JMenuItem("Decode from "+ entryMap.getKey()));
        }

        menus.put("Hash", new JMenu("Hash"));
        menus.get("Hash").add(new JMenuItem("Hash to Adler32"));
        menus.get("Hash").add(new JMenuItem("Hash to Crc16"));
        menus.get("Hash").add(new JMenuItem("Hash to Crc32"));
        menus.get("Hash").add(new JMenuItem("Hash to Crc64"));
        menus.get("Hash").add(new JMenuItem("Hash to Md2"));
        menus.get("Hash").add(new JMenuItem("Hash to Md4"));
        menus.get("Hash").add(new JMenuItem("Hash to Md5"));
        menus.get("Hash").add(new JMenuItem("Hash to Sha-1"));
        menus.get("Hash").add(new JMenuItem("Hash to Sha-256"));
        menus.get("Hash").add(new JMenuItem("Hash to Sha-384"));
        menus.get("Hash").add(new JMenuItem("Hash to Sha-512"));
        menus.get("Hash").add(new JMenuItem("Hash to Mysql"));

        JMenu comboMenu = MenuBarCoder.createMenu("Choose method...");
        this.encoding = comboMenu;
        
        for (JMenu menu: menus.values()) {
            comboMenu.add(menu);
        }

        MenuBarCoder comboMenubar = new MenuBarCoder(comboMenu);
        comboMenubar.setOpaque(false);
        comboMenubar.setBorder(null);
        
        this.encoding.setText("Encode to Base64");
        
        final JButton run = new JButton(I18n.valueByKey("CODER_RUN_BUTTON_LABEL"));
        I18n.addComponentForKey("CODER_RUN_BUTTON_LABEL", run);
        
        run.setContentAreaFilled(false);
        run.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        run.setBackground(new Color(200, 221, 242));
        
        run.addMouseListener(new FlatButtonMouseAdapter(run));
        
        run.addActionListener(new ActionCoder(this));

        middleLine.add(comboMenubar);
        middleLine.add(run, BorderLayout.EAST);

        topMixed.add(new LightScrollPane(1, 0, 1, 0, this.textInput), BorderLayout.CENTER);
        topMixed.add(middleLine, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout());
        this.result = new JPopupTextPane("Result of conversion").getProxy();
        this.result.setContentType("text/html");
        this.result.setEditorKit(new HTMLEditorKitTextPaneWrap());
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
        return textInput;
    }

    public JMenuItem getEncoding() {
        return encoding;
    }

    public JTextPane getResult() {
        return result;
    }
    
}
