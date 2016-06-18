/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.jsql.i18n.I18n;
import com.jsql.view.swing.HelperGUI;
import com.jsql.view.swing.combomenu.ComboMenuBar;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.swing.text.JPopupTextArea;

/**
 * Manager to code/uncode string in various methods.
 */
@SuppressWarnings("serial")
public class ManagerCoder extends JPanel {
    /**
     * User input to encode. 
     */
    JTextArea entry;

    /**
     * Encoding choosed by user. 
     */
    JMenuItem encoding;

    /**
     * JTextArea displaying result of encoding/decoding.
     */
    JTextArea result;

    /**
     * Create a panel to encode a string.
     */
    public ManagerCoder() {
        super(new BorderLayout());

        entry = new JPopupTextArea(new JTextArea()).getProxy();
        entry.setEditable(true);
        entry.setLineWrap(true);

        JPanel topMixed = new JPanel(new BorderLayout());

        final JPanel middleLine = new JPanel();
        middleLine.setLayout(new BorderLayout());
        middleLine.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 1));

        Map<String, JMenu> menus = new LinkedHashMap<>();
        menus.put("Base64", new JMenu("Base64"));
        menus.put("Hex", new JMenu("Hex"));
        menus.put("Url", new JMenu("Url"));
        menus.put("Html", new JMenu("Html"));
        menus.put("Base64(zipped)", new JMenu("Base64(zipped)"));
        menus.put("Hex(zipped)", new JMenu("Hex(zipped)"));

        for (String s: menus.keySet()) {
            menus.get(s).add(new JMenuItem("Encode to " + s));
            menus.get(s).add(new JMenuItem("Decode from " + s));
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

        JMenu menu = ComboMenuBar.createMenu("Choose method...");
        encoding = menu;
        
        for (JMenu m: menus.values()) {
            menu.add(m);
        }

        ComboMenuBar comboMenu = new ComboMenuBar(menu);
        this.encoding.setText("Decode from Base64");
        
        Set<Integer> set = new HashSet<>();
        set.addAll((Collection<Integer>)Arrays.asList(new Integer[]{0, 7, 14}));
        
        JButton run = new JButton(
            I18n.CODER_RUN_BUTTON, 
            new ImageIcon(ManagerCoder.class.getResource("/com/jsql/view/swing/resources/images/tick.png"))
        );
        run.setBorder(HelperGUI.BLU_ROUND_BORDER);

        middleLine.add(comboMenu);
        middleLine.add(run, BorderLayout.EAST);

        topMixed.add(new LightScrollPane(1, 1, 1, 0, entry), BorderLayout.CENTER);
        topMixed.add(middleLine, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout());
        result = new JPopupTextArea().getProxy();
        result.setLineWrap(true);
        bottom.add(new LightScrollPane(1, 1, 0, 0, result), BorderLayout.CENTER);

        run.addActionListener(new ActionCoder(this));

        JSplitPaneWithZeroSizeDivider divider = new JSplitPaneWithZeroSizeDivider(JSplitPane.VERTICAL_SPLIT);
        divider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        divider.setDividerSize(0);
        divider.setResizeWeight(0.5);

        divider.setTopComponent(topMixed);
        divider.setBottomComponent(bottom);

        this.add(divider, BorderLayout.CENTER);
    }

    /**
     * Adapter method for base64 decode.
     * @param s base64 decode
     * @return Base64 decoded string
     */
    String base64Decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    /**
     * Adapter method for base64 encode.
     * @param s String to base64 encode
     * @return Base64 encoded string
     */
    String base64Encode(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }

    /**
     * Zip a string.
     * @param str Text to zip
     * @return Zipped string
     * @throws IOException
     */
    String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return out.toString("ISO-8859-1");
    }

    /**
     * Unzip a String encoded from base64 or hexadecimal. 
     * @param str String to unzip
     * @return String unzipped
     * @throws IOException
     */
    String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        final String encode = "ISO-8859-1";
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes(encode)));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, encode));

        char[] buff = new char[1024];
        int read;
        StringBuilder response = new StringBuilder();
        while ((read = bf.read(buff)) != -1) {
            response.append(buff, 0, read);
        }
        return response.toString();
    }

    /**
     * Convert byte character to hexadecimal StringBuffer character.
     * @param b Byte character to convert
     * @param buf Hexadecimal converted character
     */
    private void byte2hex(byte b, StringBuilder buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = (b & 0xf0) >> 4;
        int low = b & 0x0f;
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /**
     * Convert a digest hash to a string representation.
     * @param block Digest array
     * @return Hash as a string
     */
    String digestToHexString(byte[] block) {
        StringBuilder  buf = new StringBuilder();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            this.byte2hex(block[i], buf);
        }
        return buf.toString();
    }
}
