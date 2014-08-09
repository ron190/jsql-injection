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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.jsql.tool.StringTool;
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
    private JTextArea entry;

    /**
     * Encoding user has choosed. 
     */
    private JComboBox<String> encoding;

    /**
     * JTextArea displaying result of encoding/decoding.
     */
    private JTextArea result;

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

        run.addActionListener(new ActionCoder());

        JSplitPaneWithZeroSizeDivider divider = new JSplitPaneWithZeroSizeDivider(JSplitPane.VERTICAL_SPLIT);
        divider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        divider.setDividerSize(0);
        divider.setResizeWeight(0.5);

        divider.setTopComponent(topMixed);
        divider.setBottomComponent(bottom);

        this.add(divider, BorderLayout.CENTER);
    }

    /**
     * Action runned when encoding.
     */
    private class ActionCoder implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (Arrays.asList(new String[]{ "md2", "md5", "sha-1", "sha-256", "sha-384", "sha-512" } ).contains(encoding.getSelectedItem().toString().replace(" < hash", ""))) {
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance(encoding.getSelectedItem().toString().replace(" < hash", ""));
                } catch (NoSuchAlgorithmException e1) {
                    result.setText("No such algorithm for hashes exists");
                }
                
                String passwordString = new String(entry.getText().toCharArray());
                byte[] passwordByte = passwordString.getBytes();
                md.update(passwordByte, 0, passwordByte.length);
                byte[] encodedPassword = md.digest();
                String encodedPasswordInString = digestToHexString(encodedPassword);
                
                result.setText(encodedPasswordInString);
            } else if ("mysql".equals(encoding.getSelectedItem().toString().replace(" < hash", ""))) {
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("sha-1");
                } catch (NoSuchAlgorithmException e1) {
                    result.setText("No such algorithm for hashes exists");
                }
                
                String password = new String(entry.getText().toCharArray());
                byte[] passwordBytes = password.getBytes();
                md.update(passwordBytes, 0, passwordBytes.length);
                byte[] hashSHA1 = md.digest();
                String stringSHA1 = digestToHexString(hashSHA1);
                
                String passwordSHA1 = new String(StringTool.hexstr(stringSHA1).toCharArray());
                byte[] passwordSHA1Bytes = passwordSHA1.getBytes();
                md.update(passwordSHA1Bytes, 0, passwordSHA1Bytes.length);
                byte[] hashSHA1SH1 = md.digest();
                String mysqlHash = digestToHexString(hashSHA1SH1);
                
                result.setText(mysqlHash);
            } else if ("hex < encode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(Hex.encodeHexString(entry.getText().getBytes("UTF-8")).trim());
                } catch (UnsupportedEncodingException e) {
                    result.setText("Encoding error: " + e.getMessage());
                }
            } else if ("hex > decode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(new String(Hex.decodeHex(entry.getText().toCharArray()), "UTF-8"));
                } catch (Exception e) {
                    result.setText("Decoding error: " + e.getMessage());
                }
            } else if ("hex(zipped) < encode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(Hex.encodeHexString(compress(entry.getText()).getBytes("UTF-8")).trim());
                } catch (Exception e) {
                    result.setText("Encoding error: " + e.getMessage());
                }
            } else if ("hex(zipped) > decode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(decompress(new String(Hex.decodeHex(entry.getText().toCharArray()), "UTF-8")));
                } catch (Exception e) {
                    result.setText("Decoding error: " + e.getMessage());
                }
            } else if ("base64(zipped) < encode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(base64Encode(compress(entry.getText())));
                } catch (IOException e) {
                    result.setText("Encoding error: " + e.getMessage());
                }
            } else if ("base64(zipped) > decode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(decompress(base64Decode(entry.getText())));
                } catch (IOException e) {
                    result.setText("Decoding error: " + e.getMessage());
                }
            } else if ("base64 < encode".equals(encoding.getSelectedItem())) {
                result.setText(base64Encode(entry.getText()));
            } else if ("base64 > decode".equals(encoding.getSelectedItem())) {
                result.setText(base64Decode(entry.getText()));
            } else if ("html < encode".equals(encoding.getSelectedItem())) {
                result.setText(StringEscapeUtils.escapeHtml3(entry.getText()));
            } else if ("html > decode".equals(encoding.getSelectedItem())) {
                result.setText(StringEscapeUtils.unescapeHtml3(entry.getText()));
            } else if ("url < encode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(URLEncoder.encode(entry.getText(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    result.setText("Encoding error: " + e.getMessage());
                }
            } else if ("url > decode".equals(encoding.getSelectedItem())) {
                try {
                    result.setText(URLDecoder.decode(entry.getText(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    result.setText("Decoding error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Adapter method for base64 decode.
     * @param s base64 decode
     * @return Base64 decoded string
     */
    private String base64Decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    /**
     * Adapter method for base64 encode.
     * @param s String to base64 encode
     * @return Base64 encoded string
     */
    private String base64Encode(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }

    /**
     * Zip a string.
     * @param str Text to zip
     * @return Zipped string
     * @throws IOException
     */
    private String compress(String str) throws IOException {
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
    private String decompress(String str) throws IOException {
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
    private String digestToHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            this.byte2hex(block[i], buf);
        }
        return buf.toString();
    }
}
