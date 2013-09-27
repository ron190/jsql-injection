/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.jsql.view.GUI;
import com.jsql.view.GUITools;
import com.jsql.view.RoundBorder;
import com.jsql.view.RoundScroller;
import com.jsql.view.component.popup.JPopupTextArea;
import com.jsql.view.component.popup.JPopupTextComponentMenu;

public class About extends JDialog{
    private static final long serialVersionUID = 2526528728988611986L;

    public JButton close = null;
    GUI gui = null;
    RoundScroller scrollPane;

    public About(final GUI newGui){
        super(newGui, "About jSQL Injection", Dialog.ModalityType.MODELESS);
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        gui = newGui;

        // Define a small and large app icon
        this.setIconImages(GUITools.getIcons());

        // Action for ESCAPE key
        ActionListener escapeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                About.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(escapeListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        close = new JButton("Close");
        close.setBorder(new RoundBorder(20, 3, true));
        close.addActionListener(escapeListener);

        this.setLayout(new BorderLayout());
        Container dialogPane = this.getContentPane();
        JButton webpage = new JButton("Webpage");
        webpage.setBorder(new RoundBorder(20, 3, true));
        webpage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Desktop.getDesktop().browse(new URI("http://code.google.com/p/jsql-injection/"));
                } catch (IOException e) {
                    gui.model.sendDebugMessage(e);
                } catch (URISyntaxException e) {
                    gui.model.sendDebugMessage(e);
                }
            }
        });
        lastLine.add(webpage);
        lastLine.add(Box.createGlue());
        lastLine.add(close);

        JLabel iconJSQL = new JLabel(new ImageIcon(newGui.getClass().getResource("/com/jsql/view/images/app-32x32.png")));
        iconJSQL.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));
        dialogPane.add(iconJSQL, BorderLayout.WEST);
        dialogPane.add(lastLine, BorderLayout.SOUTH);

        // Contact info, use HTML text
        final JEditorPane text = new JEditorPane("text/html",
                "<style>a{text-decoration: none}</style><div style=\"font-size:0.96em;font-family:'Segoe UI','Courier New'\">"+
                        "<b>jSQL Injection</b><br>" +
                        "Version: "+newGui.model.jSQLVersion+"<br>" +
                        "Build id: 20130927-0022<br><hr><br>"+
                        "Thanks for using this <b>free</b> program. I hope you like it.<br><br>"+
                        "Thanks to "+
                        "<a href=\"http://code.google.com/p/google-diff-match-patch/\">Neil Fraser</a>, " +
                        "<a href=\"http://www.codeproject.com/Articles/328417/Java-Console-apps-made-easy\">David MacDermot</a>, " +
                        "<a href=\"http://jsoup.org/\">Jonathan Hedley</a>, " +
                        "<a href=\"http://sourceforge.net/projects/jpassrecovery/\">David Kroukamp</a>, " +
                        "<a href=\"http://www.vogella.com/java.html\">Lars Vogel</a>, " +
                        "<a href=\"http://www.famfamfam.com/lab/icons/silk/\">Mark James</a>, " +
                        "<a href=\"http://www.jroller.com/pago/entry/improving_jtabbedpanes_mouse_support_like\">Patrick Gotthardt</a>, " +
                        "<a href=\"http://www.jgoodies.com/\">JGoodies</a>, " +
                        "<a href=\"http://java-swing-tips.blogspot.com\">Atsuhiro Terai</a>.<br><br>" +
                        "<b><u>Your support is important</u></b><br>" +
                        "Support the development of jSQL, you can do any or all of the following:<br>" +
                        "- Star the <a href=\"http://code.google.com/p/jsql-injection/\">project homepage</a>" +
                        " or the <a href=\"http://code.google.com/p/jsql-injection/downloads/list\">download page</a> (the star" +
                        " is displayed if you have previously logged into your Google account),<br>" +
                        "- Click the +1 on the <a href=\"http://code.google.com/p/jsql-injection/\">project homepage</a>" +
                        " or on the <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">group page</a>,<br>" +
                        "- Become a member of <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">jSQL group</a>,<br>" +
                        "- Post a comment in the <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">discussion group</a> or by email,<br>" +
                        "- <b>Spread the word</b>.<br><br>" +
                        "Visit jSQL Injection <a href=\"http://code.google.com/p/jsql-injection/\">webpage</a> and" +
                        " <a href=\"https://groups.google.com/forum/?fromgroups#!forum/jsql-injection\">forum</a>.<br>" +
                        "Contact: <b>ron190@ymail.com</b><br>"+
                        "Source code: <a href=\"http://code.google.com/p/jsql-injection/source/browse/#git%2Fsrc%2Fcom%2Fjsql%2Fmvc%2Fmodel\">Git repository</a><br><br>"+
                        "Stay informed about jSQL updates, subscribe to RSS feeds for <a href=\"https://code.google.com/feeds/p/jsql-injection/downloads/basic\">new releases</a> and" +
                        " <a href=\"https://code.google.com/feeds/p/jsql-injection/gitchanges/basic\">source changes</a>.<br><br>" +
                        "<hr><i>Attacking web-server is illegal without prior mutual consent. The end user is responsible and obeys all applicable laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program.</i>" +
                        "</div>"
                );

        text.setComponentPopupMenu(new JPopupTextComponentMenu(text));

        text.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                text.requestFocusInWindow();
            }
        });
        
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                text.getCaret().setVisible(true);
                text.getCaret().setSelectionVisible(true);
            }
        });

        text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        text.setDragEnabled(true);
        text.setEditable(false);

        text.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    try {
                        Desktop.getDesktop().browse(hle.getURL().toURI());
                    } catch (IOException e) {
                        newGui.model.sendErrorMessage(e.getMessage());
                    } catch (URISyntaxException e) {
                        gui.model.sendDebugMessage(e);
                    }
                }
            }
        });

        scrollPane = new RoundScroller(text);
        dialogPane.add(scrollPane, BorderLayout.CENTER);

        reinit();
    }

    public void reinit(){
        scrollPane.getViewport().setViewPosition(new Point(0,0));
        this.setSize(400, 300);
        this.setLocationRelativeTo(gui);
        close.requestFocusInWindow();
        this.getRootPane().setDefaultButton(close);
    }
}
