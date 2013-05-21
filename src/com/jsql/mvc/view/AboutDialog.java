package com.jsql.mvc.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.jsql.mvc.view.component.RoundedCornerBorder;
import com.jsql.mvc.view.component.popup.JPopupTextComponentMenu;

public class AboutDialog extends JDialog{
    private static final long serialVersionUID = 2526528728988611986L;

    JButton okButton = null;
    GUI _gui = null;
    RoundJScrollPane s;
    
    public AboutDialog(final GUI gui){
        super(gui, "About jSQL Injection", Dialog.ModalityType.MODELESS);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        _gui = gui;
        
        // Define a small and large app icon
        this.setIconImages(gui.images);
        
        // Action for ESCAPE key
        ActionListener escapeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(escapeListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        okButton = new JButton("Close");
        okButton.setBorder(new RoundedCornerBorder(20, 3, true));
        okButton.addActionListener(escapeListener);
        
        this.setLayout(new BorderLayout());
        Container contentPane = this.getContentPane();
        JButton w = new JButton("Webpage");
        w.setBorder(new RoundedCornerBorder(20, 3, true));
        w.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Desktop.getDesktop().browse(new URI("http://code.google.com/p/jsql-injection/"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        mainPanel.add(w);
        mainPanel.add(Box.createGlue());
        mainPanel.add(okButton);
        
        JLabel iconJSQL = new JLabel(new ImageIcon(gui.getClass().getResource("/com/jsql/images/database-icon-32x32.png")));
        iconJSQL.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));
        contentPane.add(iconJSQL, BorderLayout.WEST);
        contentPane.add(mainPanel, BorderLayout.SOUTH);
        
        // Contact info, use HTML text
        final JEditorPane infos = new JEditorPane("text/html", 
            "<style>a{text-decoration: none}</style><div style=\"font-size:0.96em;font-family:'Segoe UI','Courier New'\">"+
            "jSQL Injection<br>" +
            "Version: "+gui.model.jSQLVersion+"<br>" +
    		"Build id: 20130309-0022<br><br>"+
    		"Thanks for using this FREE program. I hope you like it.<br><br>"+
            "Thanks to "+
            "<a href=\"http://code.google.com/p/google-diff-match-patch/\">Neil Fraser</a>, " +
            "<a href=\"http://www.codeproject.com/Articles/328417/Java-Console-apps-made-easy\">David MacDermot</a>, " +
            "<a href=\"http://jsoup.org/\">Jonathan Hedley</a>, " +
            "<a href=\"http://sourceforge.net/projects/jpassrecovery/\">David Kroukamp</a>, " +
            "<a href=\"http://www.vogella.com/java.html\">Lars Vogel</a>, " +
            "<a href=\"http://www.famfamfam.com/lab/icons/silk/\">Mark James</a>, " +
            "<a href=\"http://www.jroller.com/pago/entry/improving_jtabbedpanes_mouse_support_like\">Patrick Gotthardt</a>, " +
            "<a href=\"http://java-swing-tips.blogspot.fr/2012/03/rounded-border-for-jtextfield.html\">Atsuhiro Terai</a>.<br><br>" +
            "<u>Your support is important</u><br>" +
            "Support the development of jSQL, you can do any (or all) of the following:<br>" +
            "- Star the <a href=\"http://code.google.com/p/jsql-injection/\">project homepage</a>" +
            " or the <a href=\"http://code.google.com/p/jsql-injection/downloads/list\">download page</a> (the star" +
            " is displayed if you have previously logged into your Google account),<br>" +
            "- Click the +1 on the <a href=\"http://code.google.com/p/jsql-injection/\">project homepage</a>" +
            " or on the <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">group page</a>,<br>" +
            "- Become a member of <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">jSQL group</a>,<br>" +
            "- Post a comment in the <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">discussion group</a> or by email,<br>" +
            "- Spread the word.<br><br>" +
            "Visit jSQL Injection <a href=\"http://code.google.com/p/jsql-injection/\">webpage</a> and" +
            " <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">forum</a>.<br>" +
            "Contact: ron190@ymail.com<br>"+
            "Source code: <a href=\"http://code.google.com/p/jsql-injection/source/browse/#git%2Fsrc%2Fcom%2Fjsql%2Fmvc%2Fmodel\">Git repository</a><br><br>"+
            "Stay informed about jSQL updates, subscribe to RSS feeds for <a href=\"https://code.google.com/feeds/p/jsql-injection/downloads/basic\">new releases</a> and" +
            " <a href=\"https://code.google.com/feeds/p/jsql-injection/gitchanges/basic\">source changes</a>." +
            "</div>"
            
        );
        
        infos.setComponentPopupMenu(new JPopupTextComponentMenu(infos)); 
        
        infos.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                infos.getCaret().setVisible(true);
                infos.getCaret().setSelectionVisible(true);
            }
        });
        
        infos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infos.setDragEnabled(true);
        infos.setEditable(false); 
        
        infos.addHyperlinkListener(new HyperlinkListener() { 
            public void hyperlinkUpdate(HyperlinkEvent hle) { 
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) { 
                    try {
                        Desktop.getDesktop().browse(hle.getURL().toURI());
                    } catch (IOException e) {
                        gui.model.sendErrorMessage(e.getMessage());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } 
            } 
        }); 

        s = new RoundJScrollPane(infos);
        contentPane.add(s, BorderLayout.CENTER);

        reinit();
    }
    
    void reinit(){
        s.getViewport().setViewPosition(new Point(0,0));
        this.setSize(400, 300);
        this.setLocationRelativeTo(_gui);
        okButton.requestFocusInWindow();
        this.getRootPane().setDefaultButton(okButton);
    }
}