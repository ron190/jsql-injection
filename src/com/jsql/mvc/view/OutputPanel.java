package com.jsql.mvc.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.view.bruteforce.HashBruter;
import com.jsql.mvc.view.component.CustomJList;
import com.jsql.mvc.view.component.CustomJList.StringObject;
import com.jsql.mvc.view.component.CustomJTabbedPane;
import com.jsql.mvc.view.component.JSplitPaneWithZeroSizeDivider;
import com.jsql.mvc.view.component.RoundedCornerBorder;
import com.jsql.mvc.view.component.TreeNodeEditor;
import com.jsql.mvc.view.component.TreeNodeRenderer;
import com.jsql.mvc.view.component.popup.JPopupTextArea;
import com.jsql.mvc.view.component.popup.JPopupTextAreaEditable;
import com.jsql.mvc.view.component.popup.JPopupTextField;
import com.jsql.tool.StringTool;


/**
 * Pane composed of tree and tabs on top, and info tabs on bottom.
 */
public class OutputPanel extends JSplitPaneWithZeroSizeDivider{
    private static final long serialVersionUID = -5696939494054282278L;
    
    JTabbedPane leftTabbedPane;
    
    JButton adminPageButton;
    JButton runFileButton;
    JButton shellrunFileButton;
    JButton bruteForceButton;
    JLabel filePrivilegeLabel;
    JLabel shellfilePrivilegeLabel;
    GUI mygui;
    
    int verticalSplitter,horizontalSplitter;
    
    public OutputPanel(final GUI gui){
        super(JSplitPane.VERTICAL_SPLIT, true);
        mygui = gui;
        
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        String ID1 = "verticalSplitter-0.4";
        String ID2 = "horizontalSplitter-0.4";
        this.verticalSplitter = prefs.getInt(ID1, 300);
        this.horizontalSplitter = prefs.getInt(ID2, 200);
//        if(this.horizontalSplitter > 365)
//            this.horizontalSplitter = 365;
        
        // First node in tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("No database");
        gui.databaseTree = new JTree(root);
        
        // Graphic manager for components
        TreeNodeRenderer renderer = new TreeNodeRenderer();
        gui.databaseTree.setCellRenderer(renderer);
        
        // Action manager for components
        TreeNodeEditor editor = new TreeNodeEditor(gui.databaseTree, gui.controller, gui.valuesTabbedPane);
        gui.databaseTree.setCellEditor(editor);
        
        // Tree setting
        gui.databaseTree.setEditable(true);    // allows repaint nodes
        gui.databaseTree.setShowsRootHandles(true);
        gui.databaseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // Dirty trick that allows repaint progressbar
        gui.databaseTree.getModel().addTreeModelListener(new TreeModelListener() { 
            @Override
            public void treeNodesChanged(TreeModelEvent arg0) {
                if(arg0 != null){
                    gui.databaseTree.firePropertyChange(
                        JTree.ROOT_VISIBLE_PROPERTY, 
                        !gui.databaseTree.isRootVisible(), 
                        gui.databaseTree.isRootVisible()
                    );
                }
            }
            @Override public void treeStructureChanged(TreeModelEvent arg0) {}
            @Override public void treeNodesRemoved(TreeModelEvent arg0) {}
            @Override public void treeNodesInserted(TreeModelEvent arg0) {}
        });
        
        // Give focus on tab change
        leftTabbedPane = new CustomJTabbedPane(true);
        leftTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        RoundJScrollPane scroller = new RoundJScrollPane(gui.databaseTree);
        leftTabbedPane.addTab("Database", new ImageIcon(getClass().getResource("/com/jsql/images/server_database.png")), scroller, "Explore databases from remote host");        
        leftTabbedPane.addTab("Admin page", new ImageIcon(getClass().getResource("/com/jsql/images/server_admin.png")), new AdminPageManager(), "Test admin pages on remote host");
        leftTabbedPane.addTab("File", new ImageIcon(getClass().getResource("/com/jsql/images/server_file.png")), new FileManager(), "Read files from remote host");
        leftTabbedPane.addTab("Webshell", new ImageIcon(getClass().getResource("/com/jsql/images/server_console.png")), new WebshellManager(), "Create webshell to remote host and open a terminal");
        leftTabbedPane.addTab("Brute force", new ImageIcon(getClass().getResource("/com/jsql/images/lock.png")), new BruteForceManager(), "Brute force hashes");
        leftTabbedPane.addTab("Coder", new ImageIcon(getClass().getResource("/com/jsql/images/text_letter_omega.png")), new CoderManager(), "Encode or decode a string");
//        leftTabbedPane.addTab("Upload", new ImageIcon(getClass().getResource("/com/jsql/images/server_file.png")), new JPanel(), "Upload files from local computer to remote host");
//        leftTabbedPane.addTab("VNC", new ImageIcon(getClass().getResource("/com/jsql/images/server_file.png")), new JPanel(), "VNC");
//        leftTabbedPane.addTab("Reverse shell", new ImageIcon(getClass().getResource("/com/jsql/images/server_file.png")), new JPanel(), "Reverse shell");
        leftTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Hotkeys ctrl-TAB, ctrl-shift-TAB
        new ActionHandler(leftTabbedPane);
        
        leftTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        gui.valuesTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        gui.valuesTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Tree and tabs on top
        final JSplitPaneWithZeroSizeDivider treeAndTableSplitPane = new JSplitPaneWithZeroSizeDivider(JSplitPane.HORIZONTAL_SPLIT, true);
        treeAndTableSplitPane.setLeftComponent( leftTabbedPane );
        treeAndTableSplitPane.setRightComponent( gui.valuesTabbedPane );
        treeAndTableSplitPane.setDividerLocation(this.verticalSplitter);
        treeAndTableSplitPane.setDividerSize(0);
        treeAndTableSplitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        this.setDividerSize(0);
        this.setBorder(new RoundedCornerBorder(2,2,true));
        
        // Infos tabs in bottom
        JTabbedPane infoTabs = new CustomJTabbedPane();
        new ActionHandler(infoTabs);
        infoTabs.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        infoTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) arg0.getSource();
                sourceTabbedPane.requestFocusInWindow();
            }
        });
        
        infoTabs.addTab("Console", new ImageIcon(getClass().getResource("/com/jsql/images/console.gif")), new RoundJScrollPane(gui.consoleArea), "General information");
        infoTabs.addTab("Chunk", new ImageIcon(getClass().getResource("/com/jsql/images/category.gif")), new RoundJScrollPane(gui.chunks), "Hexadecimal data recovered");
        infoTabs.addTab("Binary", new ImageIcon(getClass().getResource("/com/jsql/images/binary.gif")), new RoundJScrollPane(gui.binaryArea), "Time/Blind bytes");
        infoTabs.addTab("Header", new ImageIcon(getClass().getResource("/com/jsql/images/update.gif")), new RoundJScrollPane(gui.headers), "URL calls information");
        
        // Setting for top and bottom components
        this.setTopComponent(treeAndTableSplitPane);
        this.setBottomComponent( infoTabs );
        this.setDividerLocation(570 - this.horizontalSplitter);
        
        this.setResizeWeight(1); // defines left and bottom pane
        
        this.mygui.addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
                String ID1 = "verticalSplitter-0.4";
                String ID2 = "horizontalSplitter-0.4";
                prefs.putInt(ID1, treeAndTableSplitPane.getDividerLocation());
                prefs.putInt(ID2, OutputPanel.this.getHeight() - OutputPanel.this.getDividerLocation());
            }
        });
        
        gui.chunks.setLineWrap(true);
        gui.headers.setLineWrap(true);
        gui.binaryArea.setLineWrap(true);
        gui.consoleArea.setLineWrap(true);
    }
    
    public static JSplitPane setDividerLocation(final JSplitPane splitter,
            final double proportion) {
        if (splitter.isShowing()) {
            if(splitter.getWidth() > 0 && splitter.getHeight() > 0) {
                splitter.setDividerLocation(proportion);
            }
            else {
                splitter.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        splitter.removeComponentListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                });
            }
        }
        else {
            splitter.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 &&
                            splitter.isShowing()) {
                        splitter.removeHierarchyListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                }
            });
        }
        return splitter;
    }
    
//    class CustomTreeRenderer extends DefaultTreeCellRenderer{
//        DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
//        
//        @Override
//        public Component getTreeCellRendererComponent( JTree aTree, Object aValue, boolean aSelected,
//            boolean aExpanded, boolean aLeaf, int aRow, boolean aHasFocus )
//        {
//
//            
//            JPanel l = new JPanel(new BorderLayout());
//            l.setBorder(null);
//            JLabel m = new JLabel((String)((DefaultMutableTreeNode)aValue).getUserObject());
//            l.add(m);
//
//            m.setBorder(new RoundedCornerBorder(4,1,false));
//            if( (aValue != null) && (aValue instanceof DefaultMutableTreeNode)){
//                if( aSelected )
//                  {
//                        l.setBackground( new Color(195,214,233) );
//                        m.setBorder(new RoundedCornerBorder(4,1,true));
//                  }else
//                      l.setBackground( Color.white );
//                if(aHasFocus)
//                    m.setBorder(new RoundedCornerBorder(4,1,true));
//                else
//                    m.setBorder(new RoundedCornerBorder(4,1,false));
//            // For everything else use default renderer.
//            return l;
//            }
//            return defaultRenderer.getTreeCellRendererComponent( aTree, aValue, aSelected, aExpanded, aLeaf,
//                    aRow, aHasFocus );
//        }
//    }
    
    class CoderManager extends JPanel{
    	CoderManager(){
    		super(new BorderLayout());
    		
    		final JPanel northPanel = new JPanel();
            northPanel.setLayout( new BoxLayout(northPanel, BoxLayout.X_AXIS) );
            
            final JComboBox<String> d = new JComboBox<String>(new String[]{
            		"base64-encode",
            		"base64-decode",
            		"hex-encode",
            		"hex-decode",
//            		"binary-encode",
//            		"binary-decode",
            		"url-encode",
            		"url-decode",
            		"html-encode",
            		"html-decode",
            		"base64(zipped)-encode",
            		"base64(zipped)-decode",
            		"hex(zipped)-encode",
            		"hex(zipped)-decode",
            		"md2",
            		"md5",
            		"sha-1",
            		"sha-256",
            		"sha-384",
            		"sha-512",
            		"mysql",
//            		"zip-encode",
//            		"zip-decode",
//            		"iso-8859-1",
//            		"utf-8",
            		});
            d.setMaximumSize(new Dimension(200,22));
            d.setSelectedItem("base64-decode");
            northPanel.add(d);
            northPanel.add(Box.createHorizontalGlue());
            
            JButton o = new JButton("Code", new ImageIcon(getClass().getResource("/com/jsql/images/tick.png")));
            o.setBorder(new RoundedCornerBorder(3, 3, true));
            northPanel.add(o);
            final JPopupTextAreaEditable a = new JPopupTextAreaEditable();
    		JPanel top = new JPanel(new BorderLayout());
    		top.add(northPanel, BorderLayout.SOUTH);
    		top.add(new RoundJScrollPane(a), BorderLayout.CENTER);
    		
    		final JPopupTextArea b = new JPopupTextArea();
    		JPanel bottom = new JPanel(new BorderLayout());
    		bottom.add(new RoundJScrollPane(b), BorderLayout.CENTER);
    		
    		a.setEditable(true);
    		a.setLineWrap(true);
    		b.setLineWrap(true);
    		
            o.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(Arrays.asList(new String[]{"md2","md5","sha-1","sha-256","sha-384","sha-512"}).contains(d.getSelectedItem())){
						MessageDigest md = null;
				        try {
				            md = MessageDigest.getInstance((String) d.getSelectedItem());
				        } catch (NoSuchAlgorithmException e1) {
				            JOptionPane.showMessageDialog(null, "No such algorithm for hashes exists", "Error", JOptionPane.ERROR_MESSAGE);
				        }
				        String passwordString = new String(a.getText().toCharArray());
				        byte[] passwordByte = passwordString.getBytes();
				        md.update(passwordByte, 0, passwordByte.length);
				        byte[] encodedPassword = md.digest();
				        String encodedPasswordInString = toHexString(encodedPassword);
				        b.setText(encodedPasswordInString);
					}else if(d.getSelectedItem().equals("mysql")){
						MessageDigest md = null;
				        try {
				            md = MessageDigest.getInstance("sha-1");
				        } catch (NoSuchAlgorithmException e1) {
				            JOptionPane.showMessageDialog(null, "No such algorithm for hashes exists", "Error", JOptionPane.ERROR_MESSAGE);
				        }
				        String passwordString = new String(a.getText().toCharArray());
				        byte[] passwordByte = passwordString.getBytes();
				        md.update(passwordByte, 0, passwordByte.length);
				        byte[] encodedPassword = md.digest();
				        String encodedPasswordInString = toHexString(encodedPassword);
				        
				        MessageDigest md2 = null;
				        try {
				            md2 = MessageDigest.getInstance("sha-1");
				        } catch (NoSuchAlgorithmException e1) {
				            JOptionPane.showMessageDialog(null, "No such algorithm for hashes exists", "Error", JOptionPane.ERROR_MESSAGE);
				        }
				        String passwordString2 = new String( StringTool.hexstr(encodedPasswordInString).toCharArray() );
				        byte[] passwordByte2 = passwordString2.getBytes();
				        md2.update(passwordByte2, 0, passwordByte2.length);
				        byte[] encodedPassword2 = md2.digest();
				        String encodedPasswordInString2 = toHexString(encodedPassword2);
				        
				        b.setText(encodedPasswordInString2);
					}else if(d.getSelectedItem().equals("hex-encode")){
						try {
							b.setText(Hex.encodeHexString(a.getText().getBytes("UTF-8")).trim());
						} catch (UnsupportedEncodingException e) {
							b.setText("Encoding error: "+e.getMessage());
						}
					}else if(d.getSelectedItem().equals("hex-decode")){
						try {
							b.setText(new String(Hex.decodeHex( a.getText().toCharArray()),"UTF-8"));
						} catch (Exception e) {
							b.setText("Decoding error: "+e.getMessage());
						}
					}else if(d.getSelectedItem().equals("hex(zipped)-encode")){
						try {
							b.setText(Hex.encodeHexString(compress(a.getText()).getBytes("UTF-8")).trim());
						} catch (Exception e) {
							b.setText("Encoding error: "+e.getMessage());
						}
					}else if(d.getSelectedItem().equals("hex(zipped)-decode")){
						try {
							b.setText(decompress(new String(Hex.decodeHex( a.getText().toCharArray()),"UTF-8")));
						} catch (Exception e) {
							b.setText("Decoding error: "+e.getMessage());
						}
					}else if(d.getSelectedItem().equals("base64(zipped)-encode")){
						try {
							b.setText(encode(compress( a.getText() )));
						} catch (IOException e) {
							b.setText("Encoding error: "+e.getMessage());
						}
					}else if(d.getSelectedItem().equals("base64(zipped)-decode")){
						try {
							b.setText(decompress( decode(a.getText()) ));
						} catch (IOException e) {
							b.setText("Decoding error: "+e.getMessage());
						}
//					}else if(d.getSelectedItem().equals("zip-encode")){
//						try {
//							b.setText((compress( a.getText() )));
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}else if(d.getSelectedItem().equals("zip-decode")){
//						try {
//							b.setText(decompress( (a.getText()) ));
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
					}else if(d.getSelectedItem().equals("base64-encode")){
						b.setText(encode( a.getText() ));
					}else if(d.getSelectedItem().equals("base64-decode")){
						b.setText(decode( a.getText() ));
					}else if(d.getSelectedItem().equals("html-encode")){
						b.setText(StringEscapeUtils.escapeHtml3( a.getText() ));
					}else if(d.getSelectedItem().equals("html-decode")){
						b.setText(StringEscapeUtils.unescapeHtml3( a.getText() ));
					}else if(d.getSelectedItem().equals("url-encode")){
						try {
							b.setText(URLEncoder.encode( a.getText(), "UTF-8" ));
						} catch (UnsupportedEncodingException e) {
							b.setText("Encoding error: "+e.getMessage());
						}
					}else if(d.getSelectedItem().equals("url-decode")){
						try {
							b.setText(URLDecoder.decode( a.getText(), "UTF-8" ));
						} catch (UnsupportedEncodingException e) {
							b.setText("Decoding error: "+e.getMessage());
						}
					}
				}
			});
            
    		JSplitPaneWithZeroSizeDivider k = new JSplitPaneWithZeroSizeDivider(JSplitPane.VERTICAL_SPLIT);
    		k.setBottomComponent(bottom);
    		k.setTopComponent(top);
    		
    		k.setDividerSize(0);
    		k.setResizeWeight(0.5);
    		
    		this.add(k, BorderLayout.CENTER);
        }
    }
    
	public static String base64Encode(String stringToEncode){  
		byte [] stringToEncodeBytes = stringToEncode.getBytes();  
		return Base64.encodeBase64String(stringToEncodeBytes);  
	}  
		   
	public static String base64Decode(String stringToDecode){  
		byte [] decodedBytes = Base64.decodeBase64(stringToDecode);  
		return new String(decodedBytes);  
	}  
    	 
	public String decode(String s) {
	    return StringUtils.newStringUtf8(Base64.decodeBase64(s));
	}
	public String encode(String s) {
	    return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
	}
	
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
//        System.out.println("String length : " + str.length());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        String outStr = out.toString("ISO-8859-1");
//        System.out.println("Output String lenght : " + outStr.length());
        return outStr;
     }
    
    public static String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
//        System.out.println("Input String length : " + str.length());
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
//        String outStr = "";
//        String line;
//        while ((line=bf.readLine())!=null) {
//          outStr += line+"\n";
//        }
        
        char[] buff = new char[1024];
        int read;
        StringBuilder response= new StringBuilder();
        while((read = bf.read(buff)) != -1) {

            response.append( buff,0,read ) ;  
        }
        
//        System.out.println("Output String lenght : " + outStr.length());
//        return outStr;
        return response.toString();
     }
      
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
    
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
        }
        return buf.toString();
    }
    
    class BruteForceManager extends JPanel{
        BruteForceManager(){
            super(new BorderLayout());
            
            JPanel main = new JPanel(new BorderLayout());
            
            JPanel s = new JPanel(new BorderLayout());
            
            JLabel a = new JLabel("Hash");
            s.add(a,BorderLayout.WEST);
            
            final JPopupTextField hash = new JPopupTextField();
            hash.setToolTipText("<html><b>Hash to brute force</b><br>" +
            		"<i>Passwords for admin pages or for database users are<br>" +
            		"usually hashed inside database.</i></html>");
            s.add(hash,BorderLayout.CENTER);
            hash.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2,2,2,0,UIManager.getColor ( "Panel.background" )), 
                    new RoundedCornerBorder(3,3,true)));

            
//            final JPanel northPanel = new JPanel(new GridLayout(3, 4));
//            final JPanel northPanel = new JPanel(new FlowLayout());
            final JPanel mainOptionPanel = new JPanel(new BorderLayout());
            
            final JPanel northPanel = new JPanel();
            northPanel.setLayout( new BoxLayout(northPanel, BoxLayout.X_AXIS) );
            
            final JCheckBox low = new JCheckBox("a-z", true);
            final JCheckBox up = new JCheckBox("A-Z", true);
            final JCheckBox num = new JCheckBox("0-9", true);
            final JCheckBox spec = new JCheckBox("Special", true);
            
            northPanel.add(new JLabel("Type ", SwingConstants.RIGHT));
            
            final JComboBox<String> re = new JComboBox<String>(new String[]{"md2","md5","sha-1","sha-256","sha-384",
                    "sha-512","mysql"/*,"crc16","crc32","crc64","adler32"*/});
            re.setSelectedIndex(1);
            re.setMaximumSize( new Dimension((int) re.getPreferredSize().getWidth(),22) );
            re.setToolTipText("<html><b>Type of hash</b><br>" +
                    "<i>MD5 is commonly used to hash password of admin pages. MySQL passwords are<br>" +
                    "hashed differently (cf. Type mysql ; these are found into database 'mysql', table 'user').</i></html>");
            
            northPanel.add(re);
            
            northPanel.add(low);
            northPanel.add(up);
            northPanel.add(num);
            northPanel.add(spec);
            northPanel.add(Box.createGlue());
            
            low.setToolTipText("<html><b>Lower case characters</b><br>" +
                    "Check if searched string contains any of following characters:<br>" +
                    "<span style=\"font-family:'Courier New';\">abcdefghijklmnopqrstuvwxyz</span></html>");
            up.setToolTipText("<html><b>Upper case characters</b><br>" +
                    "Check if searched string contains any of following characters:<br>" +
                    "<span style=\"font-family:'Courier New';\">ABCDEFGHIJKLMNOPQRSTUVWXYZ</span></html>");
            num.setToolTipText("<html><b>Numeric characters</b><br>" +
                    "Check if searched string contains any of following characters:<br>" +
                    "<span style=\"font-family:'Courier New';\">0123456789</span></html>");
            spec.setToolTipText("<html><b>Special characters</b><br>" +
                    "Check if searched string contains any of following characters:<br>" +
                    "<span style=\"font-family:'Courier New';\">&nbsp;~`!@#$%^&*()_-+={}[]|\\;:'\"<.,>/?</span></html>");
            
            mainOptionPanel.add(northPanel, BorderLayout.NORTH);
            
//            northPanel.add(new JLabel("Exclude ", SwingConstants.RIGHT));
//            northPanel.add(new JPopupTextField());
//            northPanel.add(new JLabel("Min. length ", SwingConstants.RIGHT));
            
//            final JSpinner mini = new JSpinner(new SpinnerNumberModel(1, //initial value
//                    1, //min
//                    null, //max
//                    1));
//            final JSpinner max = new JSpinner(new SpinnerNumberModel(5, //initial value
//                    1, //min
//                    null, //max
//                    1));
            final JPopupTextField mini = new JPopupTextField("1");
            final JPopupTextField max = new JPopupTextField("5");
            
            mini.setToolTipText("<html><b>Minimum length of searched string</b><br>" +
            		"Speed up process by specifying the minimum length to search.</html>");
            max.setToolTipText("<html><b>Maximum length of searched string</b><br>" +
                    "Speed up process by specifying the maximum length to search.</html>");
//            northPanel.add(mini);
//            northPanel.add(new JLabel("Max. length ", SwingConstants.RIGHT));
//            northPanel.add(max);
            
            JPanel north2ndPanel = new JPanel();
            north2ndPanel.setLayout( new BoxLayout(north2ndPanel, BoxLayout.X_AXIS) );
            
            north2ndPanel.add(new JLabel("Exclude ", SwingConstants.RIGHT));
            final JPopupTextField q = new JPopupTextField();
            q.setToolTipText("<html><b>Exclude characters</b><br>" +
                    "Speed up process by excluding characters from the search.</html>");
            north2ndPanel.add(q);
            
            north2ndPanel.add(new JLabel(" Length min. ", SwingConstants.RIGHT));
            north2ndPanel.add(mini);
            north2ndPanel.add(new JLabel("max. ", SwingConstants.RIGHT));
            north2ndPanel.add(max);
            
            mini.setHorizontalAlignment(JTextField.RIGHT);
            max.setHorizontalAlignment(JTextField.RIGHT);
            
            q.setMaximumSize(new Dimension(90,(int) q.getPreferredSize().getHeight()));
            q.setMinimumSize(new Dimension(90,(int) q.getPreferredSize().getHeight()));
            
            mini.setMaximumSize(new Dimension(30,(int) mini.getPreferredSize().getHeight()));
            max.setMaximumSize(new Dimension(30,(int) max.getPreferredSize().getHeight()));
            mini.setMinimumSize(new Dimension(30,(int) mini.getPreferredSize().getHeight()));
            max.setMinimumSize(new Dimension(30,(int) max.getPreferredSize().getHeight()));
            
//            north2ndPanel.add(Box.createGlue());
            
            mainOptionPanel.add(north2ndPanel, BorderLayout.SOUTH);
            
            main.add(s,BorderLayout.NORTH);
            main.add(mainOptionPanel, BorderLayout.SOUTH);
            this.add(main, BorderLayout.NORTH);

            
            JPanel southPanel = new JPanel();
            southPanel.setOpaque(false);
            southPanel.setLayout( new BoxLayout(southPanel, BoxLayout.X_AXIS) );
            bruteForceButton = new JButton("Start",new ImageIcon(getClass().getResource("/com/jsql/images/key.png")));
            bruteForceButton.setToolTipText("<html><b>Begin brute forcing the hash</b><br>" +
                    "<i>Such process calculates a hash for every possible combinations of characters, hoping<br>" +
                    "a hash will be equal to the user's one. It always either fails or never ends. Use instead<br>" +
                    "websites like md5decrypter.co.uk to search for precalculated pairs of hash and password,<br>" +
                    "also you may try other brute force softwares like John the Ripper.</i></html>");
//            bruteForceButton.setBorder(new RoundedCornerBorder(3, 3, true));
            bruteForceButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" )), 
                    new RoundedCornerBorder(3,3,true)));
            runBruteForceLoader.setVisible(false);
            
            southPanel.add(Box.createHorizontalGlue());

            southPanel.add(runBruteForceLoader);
            southPanel.add(Box.createRigidArea(new Dimension(5,0)));

            final Boolean[] doStop = {false};
            southPanel.add(bruteForceButton);
            this.add(southPanel, BorderLayout.SOUTH);
            
            final JPopupTextArea c = new JPopupTextArea();
            c.setLineWrap(true);
            
            bruteForceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(bruteForceButton.getText().equals("Stop")){
                        bruteForceButton.setEnabled(false);
                        doStop[0] = true;
                        
                    }
                    else{
                        try{
                            Integer.parseInt(max.getText());
                            Integer.parseInt(mini.getText());
                        }catch(NumberFormatException e){
                            c.setText("*** Incorrect length");
                            return;
                        }
                        
                        if(hash.getText().equals("")){
                            c.setText("*** Empty hash");
                            return;
                        }else if( 
                                !spec.isSelected()&&
                                !up.isSelected()&&
                                !low.isSelected()&&
                                !num.isSelected()){
                            c.setText("*** Select a character range");
                            return;
                        }else if( Integer.parseInt(max.getText()) < Integer.parseInt(mini.getText()) ){
                            c.setText("*** Incorrect minimum and maximum length");
                            return;
                        }
                        
                    new Thread(new Runnable() {
                        
                        @Override
                        public void run() {
                            bruteForceButton.setText("Stop");
                            runBruteForceLoader.setVisible(true);
                            
                            // TODO Auto-generated method stub
                            c.setText(null);
                            
                            final HashBruter hb = new HashBruter();
                            
                            hb.setMinLength(Integer.parseInt(mini.getText()));
                            hb.setMaxLength(Integer.parseInt(max.getText()));
                            
                            if(spec.isSelected())hb.addSpecialCharacters(); 
                            if(up.isSelected())hb.addUpperCaseLetters();
                            if(low.isSelected())hb.addLowerCaseLetters(); 
                            if(num.isSelected())hb.addDigits();
                            if(!q.getText().equals(""))hb.excludeChars(q.getText());
                            
                            hb.setType((String)re.getSelectedItem());
                            
                            hb.setHash(hash.getText().toUpperCase().replaceAll("[^a-zA-Z0-9]", "").trim());
                            
                            Thread thread = new Thread(new Runnable() { @Override public void run() { hb.tryBruteForce(); } });
                            thread.start();
                            
                            boolean trouve = true;
                            while (!hb.isDone() && !hb.isFound() && !doStop[0]) { 
                                hb.setEndtime(System.nanoTime());

                                try {
                                    Thread.sleep(1000); // /!\ KEEP IT: delay to update result panel /!\
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }                                
                                c.setText("Current string: " + hb.getPassword() + "\n"); 
                                c.append("Current hash: " + hb.getGeneratedHash() + "\n\n"); 
                                c.append("Number of possibilities: " + hb.getNumberOfPossibilities() + "\n"); 
                                c.append("Checked hashes: " + hb.getCounter() + "\n"); 
                                c.append("Estimated hashes left: " + hb.getRemainder() + "\n"); 
                                c.append("Per second: " + hb.getPerSecond() + "\n\n"); 
                                c.append( hb.calculateTimeElapsed() + "\n"); 
                                if(hb.getPerSecond()!=0){
                                    c.append( "Traversing remaining: " + 
                                            Math.round(Math.floor(Float.parseFloat(hb.getRemainder()+"")/(float)hb.getPerSecond()/60f/60.0f/24f)) + "days " +
                                            Math.round(Math.floor(Float.parseFloat(hb.getRemainder()+"")/(float)hb.getPerSecond()/60f/60f%24)) + "h " +
                                    		Math.round(Math.floor(Float.parseFloat(hb.getRemainder()+"")/(float)hb.getPerSecond()/60f%60)) + "min " +
                            				Math.round((Float.parseFloat(hb.getRemainder()+"")/(float)hb.getPerSecond())%60) + "s\n"); 
                                }
                                c.append("Percent done: " + (100*(float)hb.getCounter()/hb.getNumberOfPossibilities()) + "%"); 
                                if(doStop[0]){
                                    trouve = false;
                                    hb.setIsDone(true);
                                    hb.setFound(true);
                                    break;
                                }
                            }
                            
                            bruteForceButton.setEnabled(true);
                            
                            if(doStop[0])
                                c.append("\n\n*** Aborted\n");
                            else if(hb.isFound() && trouve)
                                c.append("\n\nFound hash:\n" + 
                                    hb.getGeneratedHash() + "\n" +
                            		"String: " + hb.getPassword());
                            else if(hb.isDone())
                                c.append("\n\n*** Hash not found");
                            
                            doStop[0] = false;
                            runBruteForceLoader.setVisible(false);
                            bruteForceButton.setText("Start");
                        }
                    }).start();
                    
                    }
                }
            });
            
            this.add(new RoundJScrollPane(c), BorderLayout.CENTER);
        }
    }
    public JLabel runBruteForceLoader = new JLabel(new ImageIcon(getClass().getResource("/com/jsql/images/ajax-loader-mini.gif")));

    class FileManager extends JPanel{
        FileManager(){
            super(new BorderLayout());
            
            ArrayList<String> pathList = new ArrayList<String>();
            try {
                InputStream in = this.getClass().getResourceAsStream("/com/jsql/list/file.txt");
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader( in ));
                while( (line = reader.readLine()) != null ) pathList.add(line);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            final JList<StringObject> listFile = new CustomJList<StringObject>(pathList);
            this.add(new RoundJScrollPane(listFile), BorderLayout.CENTER);
            
            JPanel fileSouthPanel = new JPanel();
            fileSouthPanel.setOpaque(false);
            fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
            
            runFileButton = new JButton("Read file(s)", new ImageIcon(getClass().getResource("/com/jsql/images/search_page_white_text.png")));
            runFileButton.setToolTipText("<html><b>Select file(s) to read</b><br>" +
            		"Path must be correct, gives no result otherwise.<br>" +
            		"<i>Default list contains well known file paths. Use a Full Path Disclosure tool to obtain an existing path<br>" +
            		"from remote host, or in your browser try to output an error containing an existing file path as simply<br>" +
            		"as followed: if remote host can be requested like http://site.com/index.php?page=about, then try to<br>" +
            		"browse instead http://site.com/index.php?page[]=about, an error may show a complete file path.</i></html>");
            runFileButton.setEnabled(false);
            runFileButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" )), 
                    new RoundedCornerBorder(3,3,true)));
            runFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(listFile.getSelectedValuesList().size() == 0){
                        mygui.model.sendErrorMessage("Select at least one file");
                        return;
                    }
              
//                    for(final StringObject path: listFile.getSelectedValuesList()){
                        new Thread(new Runnable() {
                              @Override
                              public void run() {
                                  if(runFileButton.getText().equals("Read file(s)")){
                                      
                                      runFileButton.setText("Stop");
                                      
                                  try {
                                      OutputPanel.this.listFile.clearSelection();
//                                      mygui.model.getFile(path.toString());
//                                      mygui.model.endFileSearch = true;
//                                      runFileButton.setEnabled(false);

                                      runFileLoader.setVisible(true);
                                      mygui.model.getFile(listFile.getSelectedValuesList());
                                  } catch (PreparationException e) {
//                                      mygui.model.sendErrorMessage("Can't read file: " + path);
                                  } catch (StoppableException e) {
//                                      mygui.model.sendErrorMessage("Can't read file " + path);
                                  }
                                  
                                  }else{
                                      mygui.model.endFileSearch = true;
                                      runFileButton.setEnabled(false);
                                  }
                              }
                          }, "getFile").start();
//                      }
                      
                }
            });
            
            filePrivilegeLabel = new JLabel("File privilege", new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png")), SwingConstants.LEFT);
            filePrivilegeLabel.setBorder(
                    BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" ))
                    );
            filePrivilegeLabel.setToolTipText("<html><b>File tab needs the file privilege to work</b><br>" +
            		"Shows if the privilege FILE is granted to current user</html>");
            fileSouthPanel.add(filePrivilegeLabel);
            fileSouthPanel.add(Box.createHorizontalGlue());
            
            runFileLoader.setVisible(false);
            fileSouthPanel.add(runFileLoader);
            fileSouthPanel.add(Box.createRigidArea(new Dimension(5,0)));

            fileSouthPanel.add(runFileButton);
            this.add(fileSouthPanel, BorderLayout.SOUTH);
        }
    }
    public JLabel runFileLoader = new JLabel(new ImageIcon(getClass().getResource("/com/jsql/images/ajax-loader-mini.gif")));
    
    class AdminPageManager extends JPanel{
        AdminPageManager(){
            super(new BorderLayout());
            
            ArrayList<String> pathList = new ArrayList<String>();
            try {
                InputStream in = this.getClass().getResourceAsStream("/com/jsql/list/admin-page.txt");
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader( in ));
                while( (line = reader.readLine()) != null ) pathList.add(line);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
//            final JList<StringObject> listFile = new CustomJList<StringObject>(pathList);
            final JList<StringObject> listFile = new CustomJList<StringObject>(pathList);

            this.add(new RoundJScrollPane(listFile), BorderLayout.CENTER);
            
            JPanel fileSouthPanel = new JPanel();
            fileSouthPanel.setOpaque(false);
            fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
            
            adminPageButton = new JButton("Test admin page(s)", 
                    new ImageIcon(getClass().getResource("/com/jsql/images/page_white_wrench_admin.png")));
            adminPageButton.setToolTipText("<html><b>Select admin page(s) to test</b><br>" +
                    "Page file must exist, gives no result otherwise.<br>" +
                    "<i>Default list contains well known names of administration pages ; login and password are<br>" +
                    "generally required to access them (see Database and Brute force).<br>" +
                    "If main URL is http://website.com/folder/page.php?arg=value, then it searches for both<br>" +
                    "http://website.com/[admin pages] and http://website.com/folder/[admin pages]</i></html>");
            adminPageButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" )), 
                    new RoundedCornerBorder(3,3,true)));
            adminPageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(listFile.getSelectedValuesList().size() == 0){
                        mygui.model.sendErrorMessage("Select at least one admin page");
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            adminPageButton.setEnabled(false);
                            if(adminPageButton.getText().equals("Test admin page(s)")){
                            
                            adminPageButton.setText("Stop");

//                                Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/com/jsql/images/spinner.gif"));
//                                ImageIcon spinIcon = new ImageIcon(image);
//                                adminPageButton.setIcon(spinIcon);
                            adminPageLoader.setVisible(true);
                            
                            mygui.model.getAdminPage(mygui.inputPanel.textGET.getText(), listFile.getSelectedValuesList());
                            }else{
                                mygui.model.endAdminSearch = true;
                                adminPageButton.setEnabled(false);
                            }
                        }
                    }, "getFile").start();
//                    }
                    
                  }
              });
            
            adminPageLoader.setVisible(false);
            
            fileSouthPanel.add(Box.createHorizontalGlue());
            fileSouthPanel.add(adminPageLoader);
            fileSouthPanel.add(Box.createRigidArea(new Dimension(5,0)));
            fileSouthPanel.add(adminPageButton);
            this.add(fileSouthPanel, BorderLayout.SOUTH);
        }
    }
    public JLabel adminPageLoader = new JLabel(new ImageIcon(getClass().getResource("/com/jsql/images/ajax-loader-mini.gif")));

    public JList<StringObject> listFile;
    
    class WebshellManager extends JPanel{
        WebshellManager(){
            super(new BorderLayout());
            
            ArrayList<String> pathList = new ArrayList<String>();
            pathList.add("/var/www/html/defaut/");
            pathList.add("/var/www/html/default/");
            pathList.add("/var/www/html/");
            pathList.add("/var/www/");
            pathList.add("/home/www/");
//            pathList.add("E:/Outils/EasyPHP-5.3.9/www/");
            
            listFile = new CustomJList<StringObject>(pathList);

            JPanel mainSouthPanel = new JPanel();
            mainSouthPanel.setLayout(new BoxLayout(mainSouthPanel, BoxLayout.Y_AXIS));
            
            this.add(new RoundJScrollPane(listFile), BorderLayout.CENTER);
            final JPopupTextField urlToWebshell = new JPopupTextField();
              
            JPanel fileSouthPanel = new JPanel();
            fileSouthPanel.setOpaque(false);
            fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
            
            shellrunFileButton = new JButton("Create webshell", new ImageIcon(getClass().getResource("/com/jsql/images/search_application_osx_terminal.png")));
            shellrunFileButton.setToolTipText("<html><b>Select folder(s) to create webshell in</b><br>" +
                    "Path must be correct and correspond to a PHP folder, gives no result otherwise.<br>" +
                    "<i>If necessary, you must set the URL of webshell directory (see note on text component).</i>" +
                    "</html>");
            shellrunFileButton.setEnabled(false);
            shellrunFileButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" )), 
                    new RoundedCornerBorder(3,3,true)));
            shellrunFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if(listFile.getSelectedValuesList().size() == 0){
                        mygui.model.sendErrorMessage("Select at least one directory");
                        return;
                    }
              
                      for(final StringObject path: listFile.getSelectedValuesList()){
                          new Thread(new Runnable() {
                              @Override
                              public void run() {
                                  try {
                                      mygui.model.getShell(path.toString(), urlToWebshell.getText());
                                  } catch (PreparationException e) {
                                      mygui.model.sendErrorMessage("Can't read file " + path);
                                  } catch (StoppableException e) {
                                      mygui.model.sendErrorMessage("Can't read file " + path);
                                  }
                              }
                          }, "getFile").start();
                      }
                      
                }
            });
            
            shellfilePrivilegeLabel = new JLabel("File privilege", new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png")), SwingConstants.LEFT);
            shellfilePrivilegeLabel.setBorder(
                    BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" ))
                    );
            shellfilePrivilegeLabel.setToolTipText("<html><b>Webshell tab needs the file privilege to work</b><br>" +
                    "Shows if the privilege FILE is granted to current user</html>");
            fileSouthPanel.add(shellfilePrivilegeLabel);
            fileSouthPanel.add(Box.createHorizontalGlue());
            fileSouthPanel.add(shellrunFileButton);
            
            JPanel s = new JPanel(new BorderLayout());
            s.setLayout(new BoxLayout(s, BoxLayout.X_AXIS));
            s.add(Box.createHorizontalGlue());
            
            JLabel a = new JLabel("[Optional] URL to the webshell directory:");
            s.add(a);
            s.add(Box.createHorizontalGlue());
            
            urlToWebshell.setBorder(new RoundedCornerBorder(3,3,true));
            
            String l = "<html><b>How to use</b><br>" +
            		"- Leave blank if the file from address bar is located in selected folder(s), webshell will also be in it.<br>" +
            		"<i>E.g Address bar is set with http://127.0.0.1/simulate_get.php?lib=, file simulate_get.php<br>" +
            		"is located in selected '/var/www/', then webshell will be created in that folder.</i><br>" +
            		"- Or force URL for the selected folder.<br>" +
            		"<i>E.g Webshell is created in selected '/var/www/site/folder/' ; corresponding URL for this folder<br>" +
            		"is http://site.com/another/path/ (because of alias or url rewriting for example).</i></html>";
            urlToWebshell.setToolTipText(l);
            
            mainSouthPanel.add(s);
            mainSouthPanel.add(urlToWebshell);
            mainSouthPanel.add(fileSouthPanel);
            this.add(mainSouthPanel, BorderLayout.SOUTH);
        }
    }
    
//    class ReverseShellManager extends JPanel{
//        ReverseShellManager(){
//            super(new BorderLayout());
//            
//            final DefaultMutableTreeNode rootPath = new DefaultMutableTreeNode("Remote folder paths for file creation");
//            ArrayList<String> pathList = new ArrayList<String>();
//            pathList.add("E:/Outils/EasyPHP-5.3.9/www/");
//            pathList.add("/bin/");
//            pathList.add("/boot/");
//            pathList.add("/dev/");
//            pathList.add("/etc/");
//            pathList.add("/etc/profile.d/");
//            pathList.add("/etc/rc.d/");
//            pathList.add("/etc/rc.d/init.d/");
//            pathList.add("/etc/skel/");
//            pathList.add("/etc/X11/");
//            pathList.add("/home/");
//            pathList.add("/lib/");
//            pathList.add("/media/");
//            pathList.add("/mnt/");
//            pathList.add("/opt/");
//            pathList.add("/proc/");
//            pathList.add("/root/");
//            pathList.add("/sbin/");
//            pathList.add("/sys/");
//            pathList.add("/tmp/");
//            pathList.add("/usr/");
//            pathList.add("/var/");
//            pathList.add("/usr/local/bin/");
//            pathList.add("/usr/share/doc/");
//            
//            for(String path: pathList)
//                rootPath.add(new DefaultMutableTreeNode(path));
//            
//            final JTree treeFile = new JTree(rootPath);
//            treeFile.setCellRenderer(new CustomTreeRenderer());
//            
//            JPanel fileNorthPanel = new JPanel();
//            fileNorthPanel.setLayout( new BoxLayout(fileNorthPanel, BoxLayout.X_AXIS) );
//            
//            JButton addButton = new JButton("Add");
//            addButton.setBorder(new RoundedCornerBorder(6, 1, true));
//
//            final JPopupTextField pathTextField = new JPopupTextField();
//            
//            addButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent arg0) {
//                    DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
//                    DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
//                    root.add(new DefaultMutableTreeNode(pathTextField.getText().replace("\\", "/")));
//                    model.reload(root);
//                }
//            });
//            
//            this.add(fileNorthPanel, BorderLayout.NORTH);
//            this.add(new RoundJScrollPane(treeFile), BorderLayout.CENTER);
//            
//            JPanel fileSouthPanel = new JPanel();
//            fileSouthPanel.setOpaque(false);
//            fileSouthPanel.setLayout( new BoxLayout(fileSouthPanel, BoxLayout.X_AXIS) );
//            
////            uploadrunFileButton = new JButton("Upload into selected folder(s)");
////            uploadrunFileButton.setEnabled(false);
////            uploadrunFileButton.setBorder(new RoundedCornerBorder(6, 3, true));
////            uploadrunFileButton.addActionListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent arg0) {
////                    if(treeFile.getSelectionPaths() == null) return;
////                    for(final TreePath path: treeFile.getSelectionPaths()){
////                        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
////                        if(node == null) return;
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
////                                try {
////                                    mygui.model.getFile(node.toString());
////                                } catch (PreparationException e) {
////                                    mygui.model.sendErrorMessage("Can't read file: " + path.getLastPathComponent());
////                                } catch (StoppableException e) {
////                                    mygui.model.sendErrorMessage("Can't read file " + path.getLastPathComponent());
////                                }
////                            }
////                        }, "getFile").start();
////                    }
////                }
////            });
//            
//            //Create a file chooser
//            final JFileChooser importFileDialog = new JFileChooser(mygui.model.pathFile);
//            importFileDialog.setDialogTitle("Import a list of paths from a file");
//            
//            JButton importFileButton = new JButton("Import");
//            importFileButton.setBorder(new RoundedCornerBorder(6, 1, true));
//            importFileButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent arg0) {
//                    int returnVal = importFileDialog.showOpenDialog(mygui);
//                    if (returnVal == JFileChooser.APPROVE_OPTION) {
//                        File file = importFileDialog.getSelectedFile();
//                        mygui.model.pathFile = importFileDialog.getCurrentDirectory().toString();
//                        
//                        // Save path for further use
//                        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
//                        String ID1 = "pathFile";
//                        prefs.put(ID1, mygui.model.pathFile);
//                        
//                        BufferedReader in;
//                        try {
//                            in = new BufferedReader(new FileReader(file));
//                            String line;
//                            DefaultTreeModel model = (DefaultTreeModel)treeFile.getModel();
//                            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
//                            while((line = in.readLine()) != null)
//                                root.add(new DefaultMutableTreeNode(line));
//                            
//                            model.reload(root);
//                        } catch (FileNotFoundException e1) {
//                            e1.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            
//            fileNorthPanel.add(importFileButton);
//            fileNorthPanel.add(Box.createHorizontalStrut(1));
//            fileNorthPanel.add(pathTextField);
//            fileNorthPanel.add(Box.createHorizontalStrut(1));
//            fileNorthPanel.add(addButton);
//
////            uploadfilePrivilegeLabel = new JLabel("File privilege", new ImageIcon(getClass().getResource("/com/jsql/images/bullet_square_grey.png")), SwingConstants.LEFT);
////            fileSouthPanel.add(uploadfilePrivilegeLabel);
////            fileSouthPanel.add(Box.createHorizontalGlue());
////            fileSouthPanel.add(uploadrunFileButton);
////            this.add(fileSouthPanel, BorderLayout.SOUTH);
//        }
//    }
}