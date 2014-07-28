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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.JScrollPanePixelBorder;
import com.jsql.view.list.dnd.DnDList;

/**
 * Manager to display webpages frequently used as backoffice administration.
 */
@SuppressWarnings("serial")
public class AdminPageManager extends ListManager{

    public AdminPageManager(){
        this.setLayout(new BorderLayout());
        this.setDefaultText("Test admin page(s)");

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

        this.add(new JScrollPanePixelBorder(1,1,0,0,listFile), BorderLayout.CENTER);

        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout( new BoxLayout(lastLine, BoxLayout.X_AXIS) );

        lastLine.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createMatteBorder(0,1,0,0,GUITools.COMPONENT_BORDER), 
        		BorderFactory.createEmptyBorder(1, 0, 1, 1)));
        
        run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/adminSearch.png")));

        run.setToolTipText("<html><b>Select admin page(s) to test</b><br>" +
                "Page file must exist, gives no result otherwise.<br>" +
                "<i>Default list contains well known names of administration pages ; login and password are<br>" +
                "generally required to access them (see Database and Brute force).<br>" +
                "If main URL is http://website.com/folder/page.php?arg=value, then it searches for both<br>" +
                "http://website.com/[admin pages] and http://website.com/folder/[admin pages]</i></html>");
        run.setBorder(GUITools.BLU_ROUND_BORDER);

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
}
