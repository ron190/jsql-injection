/*
 * Copyright (C) 2007 lenny@mondogrigio.cjb.net
 *
 * This file is part of PJBS (http://sourceforge.net/projects/pjbs)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package pjbs.standalone;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lenny
 */
public class Server2 extends Thread {

    private ServerSocket serverSocket = null;
    private boolean listening = true;

    /**
     * Creates a new instance of Server
     */
    public Server2() {

        int port = 4444;
        String[] drivers = {
//            "org.postgresql.Driver",
            "org.hsqldb.jdbc.JDBCDriver"
        };

//        String[] p = Utils.parseFile("../conf/pjbs.conf");
        String[] p = Utils.parseFile("E:\\Outils\\EasyPHP-5.3.9\\www\\PJBS\\conf\\pjbs.conf");

        if (p != null && p.length >= 3) {

            port = Integer.parseInt(p[1]);
            drivers = new String[p.length - 2];

            for (int i = 2; i < p.length; i++)
                drivers[i - 2] = p[i];

        } else {

            Utils.log("warning", "invalid config file, using defaults");
        }

        try {

            serverSocket = new ServerSocket(port);
            Utils.log("notice", "listening on " + port);

        } catch (IOException e) {

            Utils.log("error", "could not listen on " + port);
            return;
        }

        try {

            for (int i = 0; i < drivers.length; i++) {

                Class.forName(drivers[i]);
                Utils.log("notice", "loaded " + drivers[i]);
            }

        } catch (ClassNotFoundException ex) {

            Utils.log("error", "could not load JDBC drivers: "+ ex);
            return;
        }
    }

    public void run() {

        while (listening) {

            try {

                new ServerThread(serverSocket.accept()).start();

            } catch (IOException ex) {

                Utils.log("error", "could not create thread");
                return;
            }
        }
    }

    public void shutdown() {

        listening = false;
        interrupt();
    }

    public static void main(String[] args) {

        org.hsqldb.server.Server serverHsqldb = new org.hsqldb.server.Server();
        serverHsqldb.setSilent(true);
        serverHsqldb.setDatabaseName(0, "mainDb");
        serverHsqldb.setDatabasePath(0, "mem:mainDb");
        serverHsqldb.setPort(9003);
        serverHsqldb.start();

        Server2 server = new Server2();

        try {

            server.start();
            server.join();

        } catch (InterruptedException ex) {

            Utils.log("error", "could not join thread");
        }
    }
}

class Utils {

    private static final int BUFFER_SIZE = 1024;
    private static int makeUID_i = 1;

    /**
     * Log something to the console.
     * @param l
     * @param s
     */
    public static synchronized void log(String l, String s) {

        System.out.println(l + ": " + s);
    }

    /**
     * Reads a whole file into a string.
     * @param fn Path and name of the file.
     * @return A string on success, null on failure.
     */
    public static String readFile(String fn) {

        try {

            FileReader fr = new FileReader(fn);
            StringBuffer sb = new StringBuffer();

            try {

                while (true) {

                    char[] b = new char[BUFFER_SIZE];
                    int l = fr.read(b, 0, BUFFER_SIZE);

                    if (l > 0)
                        sb.append(b, 0, l);
                    else if (l < 0)
                        break;
                }

                return sb.toString();

            } catch (IOException ex) {

                log("error", "could not read file " + fn);
            }

        } catch (FileNotFoundException ex) {

            log("error", "file not found " + fn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Split a string into words.
     * Needed to parse the commands from the PHP backend and
     * the main config file.
     * @param s String to split into words.
     * @param base64 The words are base64-encoded?
     * @return An array of words.
     */
    public static String[] parseString(String s, boolean base64) {

        if (base64) {

            String r[] = s.split(" ", -1);

            for (int i = 0; i < r.length; i ++)
                r[i] = Base64.decodeString(r[i]);

            return r;

        } else {

            return s.trim().split("[ \t\r\n]+", -1);
        }
    }

    /**
     * Same as parseString(s, false);
     * @param s String to split into words.
     * @return An array of words.
     */
    public static String[] parseString(String s) {

        return parseString(s, false);
    }

    /**
     * Split a file into words.
     * Needed to read the main config file.
     * @param fn Path and name of the file to read
     * @return An array of words on success, null on failure.
     */
    public static String[] parseFile(String fn) {

        String s = readFile(fn);

        if (s != null)
            return parseString(s);
        else
            return null;
    }

    /**
     * Create an UID.
     * Needed to keep track of the JDBC ResultSets with the PHP
     * backend.
     * @return The generated UID.
     */
    public static synchronized String makeUID() {

        return new String("PJBS_id_" + Integer.toString(makeUID_i ++));
    }

    /**
     * Make a filename safe.
     * @param fn Unsafe file name
     * @return Safe file name
     */
    public static String safeFn(String fn) {

        return fn.replaceAll("[^a-zA-Z0-9_.\\-]", "");
    }
}

class ServerThread extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ServerCommands serverCommands;

    public ServerThread(Socket socket) throws IOException {

        this.socket = socket;
        this.socket.setSoLinger(false, 0);

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.serverCommands = new ServerCommands(this);
    }

    public void run() {

        try {

            String line;

            while ((line = in.readLine()) != null) {

                String[] cmd = Utils.parseString(line, true);

                if (cmd[0].equals("connect")) {

                    serverCommands.connect(cmd);

                } else if (cmd[0].equals("exec")) {

                    serverCommands.exec(cmd);

                } else if (cmd[0].equals("fetch_array")) {

                    serverCommands.fetch_array(cmd);

                } else if (cmd[0].equals("free_result")) {

                    serverCommands.free_result(cmd);

                } else if (cmd[0].equals("index")) {

                    serverCommands.index(cmd);

                } else if (cmd[0].equals("search")) {

                    serverCommands.search(cmd);

                } else {

                    break;
                }
            }

            serverCommands.close();
            socket.close();

        } catch (IOException e) {

            Utils.log("error", "socket lost");
        }
    }

    public void write(String s) {

        out.println(Base64.encodeString(s));
    }

    public void write(String k, String v) {

        if (v == null)
            v = "";

        out.println(
                Base64.encodeString(k) + " " +
                        Base64.encodeString(v)
        );
    }

    void write(String k, int v) {

        out.println(
                Base64.encodeString(k) + " " +
                        Base64.encodeString(new Integer(v).toString())
        );
    }

    void write(String k, int v1, int v2) {

        out.println(
                Base64.encodeString(k) + " " +
                        Base64.encodeString(new Integer(v1).toString()) + " " +
                        Base64.encodeString(new Integer(v2).toString())
        );
    }
}

class Base64 {

    // Mapping table from 6-bit nibbles to Base64 characters.
    private static char[]    map1 = new char[64];
    static {
        int i=0;
        for (char c='A'; c<='Z'; c++) map1[i++] = c;
        for (char c='a'; c<='z'; c++) map1[i++] = c;
        for (char c='0'; c<='9'; c++) map1[i++] = c;
        map1[i++] = '+'; map1[i++] = '/'; }

    // Mapping table from Base64 characters to 6-bit nibbles.
    private static byte[]    map2 = new byte[128];
    static {
        for (int i=0; i<map2.length; i++) map2[i] = -1;
        for (int i=0; i<64; i++) map2[map1[i]] = (byte)i; }

    /**
     * Encodes a string into Base64 format.
     * No blanks or line breaks are inserted.
     * @param s  a String to be encoded.
     * @return   A String with the Base64 encoded data.
     */
    public static String encodeString(String s) {
        return new String(encode(s.getBytes())); }

    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted.
     * @param in  an array containing the data bytes to be encoded.
     * @return    A character array with the Base64 encoded data.
     */
    public static char[] encode(byte[] in) {
        return encode(in,in.length); }

    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted.
     * @param in   an array containing the data bytes to be encoded.
     * @param iLen number of bytes to process in <code>in</code>.
     * @return     A character array with the Base64 encoded data.
     */
    public static char[] encode(byte[] in, int iLen) {
        int oDataLen = (iLen*4+2)/3;       // output length without padding
        int oLen = ((iLen+2)/3)*4;         // output length including padding
        char[] out = new char[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++] & 0xff;
            int i1 = ip < iLen ? in[ip++] & 0xff : 0;
            int i2 = ip < iLen ? in[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : '='; op++;
            out[op] = op < oDataLen ? map1[o3] : '='; op++; }
        return out; }

    /**
     * Decodes a string from Base64 format.
     * @param s  a Base64 String to be decoded.
     * @return   A String containing the decoded data.
     * @throws   IllegalArgumentException if the input is not valid Base64 encoded data.
     */
    public static String decodeString(String s) {
        return new String(decode(s)); }

    /**
     * Decodes a byte array from Base64 format.
     * @param s  a Base64 String to be decoded.
     * @return   An array containing the decoded data bytes.
     * @throws   IllegalArgumentException if the input is not valid Base64 encoded data.
     */
    public static byte[] decode(String s) {
        return decode(s.toCharArray()); }

    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded data.
     * @param in  a character array containing the Base64 encoded data.
     * @return    An array containing the decoded data bytes.
     * @throws    IllegalArgumentException if the input is not valid Base64 encoded data.
     */
    public static byte[] decode(char[] in) {
        int iLen = in.length;
        if (iLen%4 != 0) throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        while (iLen > 0 && in[iLen-1] == '=') iLen--;
        int oLen = (iLen*3) / 4;
        byte[] out = new byte[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iLen ? in[ip++] : 'A';
            int i3 = ip < iLen ? in[ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            int o0 = ( b0       <<2) | (b1>>>4);
            int o1 = ((b1 & 0xf)<<4) | (b2>>>2);
            int o2 = ((b2 &   3)<<6) |  b3;
            out[op++] = (byte)o0;
            if (op<oLen) out[op++] = (byte)o1;
            if (op<oLen) out[op++] = (byte)o2; }
        return out; }

    private Base64() {
    }

} // end class Base64Coder

class ServerCommands {

    private ServerThread serverThread;
    private Connection conn = null;
    private Hashtable results = new Hashtable();

    /** Creates a new instance of ServerCommands */
    public ServerCommands(ServerThread serverThread) {

        this.serverThread = serverThread;
    }

    /**
     * Connect to a JDBC data source.
     * @param cmd
     */
    public void connect(String[] cmd) {

        if (conn == null && cmd.length == 4) {

            try {

                conn = DriverManager.getConnection(cmd[1], cmd[2], cmd[3]);
                serverThread.write("ok");

            } catch (SQLException ex) {

                serverThread.write("ex");
            }

        } else {

            serverThread.write("err");
        }
    }

    /**
     * Execute an SQL query.
     * @param cmd
     */
    public void exec(String[] cmd) {

        if (conn != null && cmd.length >= 2) {

            try {

                PreparedStatement st = conn.prepareStatement(cmd[1]);
                st.setFetchSize(1);

                for (int i = 2; i < cmd.length; i ++) {

                    try {

                        st.setDouble(i - 1, Double.parseDouble(cmd[i]));

                    } catch (NumberFormatException e) {

                        st.setString(i - 1, cmd[i]);
                    }
                }

                if (st.execute()) {

                    String id = Utils.makeUID();
                    results.put(id, st.getResultSet());
                    serverThread.write("ok", id);

                } else {

                    serverThread.write("ok", st.getUpdateCount());
                }

            } catch (SQLException ex) {

                serverThread.write("ex");
            }

        } else {

            serverThread.write("err");
        }
    }

    /**
     * Fetch a row from a ResultSet.
     * @param cmd
     */
    public void fetch_array(String[] cmd) {

        if (conn != null && cmd.length == 2) {

            ResultSet rs = (ResultSet)results.get(cmd[1]);

            if (rs != null) {

                try {

                    if (rs.next()) {

                        ResultSetMetaData rsmd = rs.getMetaData();
                        int cn = rsmd.getColumnCount();

                        serverThread.write("ok", cn);

                        for (int i = 1; i <= cn; i ++)
                            serverThread.write(rsmd.getColumnName(i), rs.getString(i));

                    } else {

                        serverThread.write("end");
                    }

                } catch (SQLException ex) {

                    serverThread.write("ex");
                }

            } else {

                serverThread.write("err");
            }

        } else {

            serverThread.write("err");
        }
    }

    /**
     * Release a ResultSet.
     * @param cmd
     */
    public void free_result(String[] cmd) {

        if (conn != null && cmd.length == 2) {

            ResultSet rs = (ResultSet)results.get(cmd[1]);

            if (rs != null) {

                results.remove(cmd[1]);
                serverThread.write("ok");

            } else {

                serverThread.write("err");
            }

        } else {

            serverThread.write("err");
        }
    }

    /**
     * Execute an SQL query and index the result with Lucene.
     * The first column is the key, other columns are concatenated
     * and indexed as a single value field.
     * @param cmd
     */
    public void index(String[] cmd) {

        if (conn != null && cmd.length == 3) {

            Search search = new Search(cmd[1]);
            search.startIndex();

            try {

                PreparedStatement st = conn.prepareStatement(cmd[2]);
                st.setFetchSize(1);

                if (st.execute()) {

                    ResultSet rs = st.getResultSet();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int cn = rsmd.getColumnCount();

                    if (cn > 1) {

                        try {

                            int rn = 0;

                            while (rs.next()) {

                                String key = rs.getString(1);

                                if (key != null) {

                                    StringBuffer value = new StringBuffer();

                                    for (int i = 2; i <= cn; i ++) {

                                        String s = rs.getString(i);

                                        if (s != null)
                                            value.append(s + " ");
                                    }

                                    search.addDocument(key, value.toString());
                                }

                                rn ++;

                                if ((rn % 100) == 0)
                                    Utils.log("indexer", rn + " records ...");
                            }

                            Utils.log("indexer", "done, " + rn + " records");
                            serverThread.write("ok");

                        } catch (SQLException ex) {

                            serverThread.write("ex");
                        }

                    } else {

                        serverThread.write("err");
                    }

                } else {

                    serverThread.write("err");
                }

            } catch (SQLException ex) {

                serverThread.write("ex");
            }

            search.endIndex();
        }
    }

    /**
     * Search inside the value field and return keys and scores.
     * @param cmd
     */
    public void search(String[] cmd) {

        if (cmd.length == 5) {

            Search search = new Search(cmd[1]);
            int off = Integer.parseInt(cmd[3]);
            int len = Integer.parseInt(cmd[4]);

            if (search.query(cmd[2], off, len)) {

                serverThread.write("ok", search.getCount(), search.getMatches());

                while (search.next())
                    serverThread.write(search.getKey(), search.getScore());

            } else {

                serverThread.write("err");
            }
        }
    }

    /**
     * Release the JDBC connection.
     */
    public void close() {

        if (conn != null) {

            try {

                conn.close();

            } catch (SQLException ex) {

                Utils.log("error", "could not close JDBC connection");
            }
        }
    }
}

class Search {

    private String partition;
    private IndexWriter indexwriter = null;
    private Vector keys, scores;
    private int matches, cur;

    /** Creates a new instance of Search */
    public Search(String pn) {

        this.partition = "../var/" + Utils.safeFn(pn);
        this.keys = new Vector();
        this.scores = new Vector();
        this.matches = 0;
        this.cur = -1;
    }

    public boolean startIndex() {

        try {

            indexwriter = new IndexWriter(partition, new StandardAnalyzer(), true);
            return true;

        } catch (IOException ex) {

            Utils.log("search", "could not open index on partition " + partition);
            return false;
        }
    }

    public boolean addDocument(String key, String value) {

        try {

            Document doc = new Document();
            doc.add(new Field("key", key, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field("value", value, Field.Store.NO, Field.Index.TOKENIZED));
            indexwriter.addDocument(doc);
            return true;

        } catch (IOException ex) {

            Utils.log("search", "could not add document on partition " + partition);
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean endIndex() {

        try {

            indexwriter.optimize();
            indexwriter.close();
            return true;

        } catch (IOException ex) {

            Utils.log("search", "could not close index on partition " + partition);
            return false;
        }
    }

    public boolean query(String s, int off, int len) {

        try {

            IndexSearcher is = new IndexSearcher(partition);
            QueryParser parser = new QueryParser("value", new StandardAnalyzer());

            try {

                Query query = parser.parse(s);
                Hits hits = is.search(query);

                int start = Math.min(off, hits.length());
                int end = Math.min(start + len, hits.length());

                matches = hits.length();

                for(int i = start; i < end; i ++) {

                    keys.add(hits.doc(i).get("key"));
                    scores.add(new Integer((int)(hits.score(i) * 100)).toString());
                }

                return true;

            } catch (ParseException ex) {

                Utils.log("search", "could not parse query");
                return false;
            }

        } catch (IOException ex) {

            Utils.log("search", "could not read index on partition " + partition);
            return false;
        }
    }

    public String getKey() {

        return (String)keys.get(cur);
    }

    public String getScore() {

        return (String)scores.get(cur);
    }

    public int getCount() {

        return keys.size();
    }

    public boolean next() {

        cur ++;

        if (cur < keys.size())
            return true;
        else
            return false;
    }

    public int getMatches() {

        return matches;
    }
}
