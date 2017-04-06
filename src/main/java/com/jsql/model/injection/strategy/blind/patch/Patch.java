package com.jsql.model.injection.strategy.blind.patch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;

/**
 * Class representing one patch operation.
 */
public class Patch {
    public LinkedList<Diff> diffs;
    public int start1;
    public int start2;
    public int length1;
    public int length2;

    /**
     * Constructor.  Initializes with an empty list of diffs.
     */
    public Patch() {
        this.diffs = new LinkedList<Diff>();
    }

    /**
     * Emmulate GNU diff's format.
     * Header: @@ -382,8 +481,9 @@
     * Indicies are printed as 1-based, not 0-based.
     * @return The GNU diff string.
     */
    @Override
    public String toString() {
        String coords1;
        String coords2;
        if (this.length1 == 0) {
            coords1 = this.start1 + ",0";
        } else if (this.length1 == 1) {
            coords1 = Integer.toString(this.start1 + 1);
        } else {
            coords1 = (this.start1 + 1) + "," + this.length1;
        }
        if (this.length2 == 0) {
            coords2 = this.start2 + ",0";
        } else if (this.length2 == 1) {
            coords2 = Integer.toString(this.start2 + 1);
        } else {
            coords2 = (this.start2 + 1) + "," + this.length2;
        }
        StringBuilder text = new StringBuilder();
        text.append("@@ -").append(coords1).append(" +").append(coords2)
        .append(" @@\n");
        // Escape the body of the patch with %xx notation.
        for (Diff aDiff : this.diffs) {
            switch (aDiff.operation) {
            case INSERT:
                text.append('+');
                break;
            case DELETE:
                text.append('-');
                break;
            case EQUAL:
                text.append(' ');
                break;
            }
            try {
                text.append(URLEncoder.encode(aDiff.text, "UTF-8").replace('+', ' '))
                .append("\n");
            } catch (UnsupportedEncodingException e) {
                // Not likely on modern system.
                throw new IllegalArgumentException("This system does not support UTF-8.");
            }
        }
        return unescapeForEncodeUriCompatability(text.toString());
    }
    
    /**
     * Unescape selected chars for compatability with JavaScript's encodeURI.
     * In speed critical applications this could be dropped since the
     * receiving application will certainly decode these fine.
     * Note that this function is case-sensitive.  Thus "%3f" would not be
     * unescaped.  But this is ok because it is only called with the output of
     * URLEncoder.encode which returns uppercase hex.
     *
     * Example: "%3F" -> "?", "%24" -> "$", etc.
     *
     * @param str The string to escape.
     * @return The escaped string.
     */
    public static String unescapeForEncodeUriCompatability(String str) {
        return str.replace("%21", "!").replace("%7E", "~")
                .replace("%27", "'").replace("%28", "(").replace("%29", ")")
                .replace("%3B", ";").replace("%2F", "/").replace("%3F", "?")
                .replace("%3A", ":").replace("%40", "@").replace("%26", "&")
                .replace("%3D", "=").replace("%2B", "+").replace("%24", "$")
                .replace("%2C", ",").replace("%23", "#");
    }
}
