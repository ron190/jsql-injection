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
package com.jsql.view.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.RoundScroller;
import com.jsql.view.dnd.list.DnDList;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
@SuppressWarnings("serial")
public class AdminPageManager extends JPanel{
	
    private JButton run;

    private JLabel privilege;

    private JLabel loader = new JLabel(GUITools.SPINNER);

    private final String defaultText = "Test admin page(s)";

    public AdminPageManager(){
        super(new BorderLayout());

        ArrayList<String> pathList = new ArrayList<String>();
        try {
            InputStream in = this.getClass().getResourceAsStream("/com/jsql/list/admin-page.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader( in ));
            while( (line = reader.readLine()) != null ) pathList.add(line);
            reader.close();
        } catch (IOException e) {
        	GUIMediator.model().sendDebugMessage(e);
        }

        final DnDList listFile = new DnDList(pathList);

        this.add(new RoundScroller(listFile), BorderLayout.CENTER);

        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout( new BoxLayout(lastLine, BoxLayout.X_AXIS) );

        run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/adminSearch.png")));

        run.setToolTipText("<html><b>Select admin page(s) to test</b><br>" +
                "Page file must exist, gives no result otherwise.<br>" +
                "<i>Default list contains well known names of administration pages ; login and password are<br>" +
                "generally required to access them (see Database and Brute force).<br>" +
                "If main URL is http://website.com/folder/page.php?arg=value, then it searches for both<br>" +
                "http://website.com/[admin pages] and http://website.com/folder/[admin pages]</i></html>");
        run.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" )), 
                GUITools.BLU_ROUND_BORDER));
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(listFile.getSelectedValuesList().size() == 0){
                	GUIMediator.model().sendErrorMessage("Select at least one admin page");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(run.getText().equals(defaultText)){
                            run.setText("Stop");
                            loader.setVisible(true);
                            GUIMediator.model().rao.getAdminPage(GUIMediator.top().textGET.getText(), listFile.getSelectedValuesList());
                        }else{
                        	GUIMediator.model().rao.endAdminSearch = true;
                            run.setEnabled(false);
                        }
                    }
                }, "getAdminPage").start();
            }
        });

        loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(loader);
        lastLine.add(Box.createRigidArea(new Dimension(5,0)));
        lastLine.add(run);
        this.add(lastLine, BorderLayout.SOUTH);
    }

    /**
     * Hide the loader icon.
     */
    public void hideLoader(){
        loader.setVisible(false);
    }

    /**
     * Enable or disable the button.
     * @param i The new state of the button
     */
    public void enableButton(boolean a){
        run.setEnabled(a);
    }

    /**
     * Display another icon to the Privilege label.
     * @param i The new icon
     */
    public void changeIcon(Icon i){
        privilege.setIcon(i);
    }

    /**
     * Restore the default text to the button after a search.
     */
    public void restoreButtonText(){
        run.setText(defaultText);
    }
}
