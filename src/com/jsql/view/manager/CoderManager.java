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
package com.jsql.view.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.jsql.view.GUITools;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.splitpane.JSplitPaneWithZeroSizeDivider;
import com.jsql.view.textcomponent.JPopupTextArea;

/**
 * Manager to code/uncode string in various methods.
 */
@SuppressWarnings("serial")
public class CoderManager extends JPanel {
    /**
     * User input to encode. 
     */
    JTextArea entry;

    /**
     * Encoding user has choosed. 
     */
    JComboBox<String> encoding;

    /**
     * JTextArea displaying result of encoding/decoding.
     */
    JTextArea result;

    /**
     * Create a panel to encode a string.
     */
    public CoderManager() {
        super(new BorderLayout());

        entry = new JPopupTextArea(new JTextArea()).getProxy();
        entry.setEditable(true);
        entry.setLineWrap(true);

        JPanel topMixed = new JPanel(new BorderLayout());

        final JPanel middleLine = new JPanel();
        middleLine.setLayout(new BoxLayout(middleLine, BoxLayout.X_AXIS));
        middleLine.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 1));

        encoding = new JComboBox<String>(new String[]{
                "base64 < encode",
                "base64 > decode",
                "hex < encode",
                "hex > decode",
                "url < encode",
                "url > decode",
                "html < encode",
                "html > decode",
                "base64(zipped) < encode",
                "base64(zipped) > decode",
                "hex(zipped) < encode",
                "hex(zipped) > decode",
                "md2 < hash",
                "md5 < hash",
                "sha-1 < hash",
                "sha-256 < hash",
                "sha-384 < hash",
                "sha-512 < hash",
                "mysql < hash"
        });

        encoding.setSelectedItem("base64 > decode");

        JButton run = new JButton("Run", new ImageIcon(getClass().getResource("/com/jsql/view/images/tick.png")));
        run.setBorder(GUITools.BLU_ROUND_BORDER);

        middleLine.add(encoding);
        middleLine.add(Box.createRigidArea(new Dimension(1, 0)));
        middleLine.add(run);

        topMixed.add(new JScrollPanePixelBorder(1, 1, 1, 0, entry), BorderLayout.CENTER);
        topMixed.add(middleLine, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout());
        result = new JPopupTextArea().getProxy();
        result.setLineWrap(true);
        bottom.add(new JScrollPanePixelBorder(1, 1, 0, 0, result), BorderLayout.CENTER);

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
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));

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
    private void byte2hex(byte b, StringBuffer buf) {
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
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            this.byte2hex(block[i], buf);
        }
        return buf.toString();
    }
}
