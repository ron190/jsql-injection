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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.view.GUI;
import com.jsql.view.GUITools;
import com.jsql.view.RoundScroller;
import com.jsql.view.dnd.list.DnDList;

/**
 * Manager to read a file from the host.
 */
public class FileManager extends JPanel{
    private static final long serialVersionUID = -4685622112637438009L;

    private GUI gui;

    private JButton run;

    private JLabel privilege;

    private JLabel loader = new JLabel(GUITools.SPINNER);

    private final String defaultText = "Read file(s)";

    public FileManager(final GUI gui){
        super(new BorderLayout());

        this.gui = gui;

        ArrayList<String> pathList = new ArrayList<String>();
        try {
            InputStream in = this.getClass().getResourceAsStream("/com/jsql/list/file.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader( in ));
            while( (line = reader.readLine()) != null ) pathList.add(line);
            reader.close();
        } catch (IOException e) {
            gui.model.sendDebugMessage(e);
        }

        final JList listFile = new DnDList(gui, pathList);

        this.add(new RoundScroller(listFile), BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        southPanel.setLayout( new BoxLayout(southPanel, BoxLayout.X_AXIS) );

        run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/fileSearch.png")));

        run.setToolTipText("<html><b>Select file(s) to read</b><br>" +
                "Path must be correct, gives no result otherwise.<br>" +
                "<i>Default list contains well known file paths. Use a Full Path Disclosure tool to obtain an existing path<br>" +
                "from remote host, or in your browser try to output an error containing an existing file path as simply<br>" +
                "as followed: if remote host can be requested like http://site.com/index.php?page=about, then try to<br>" +
                "browse instead http://site.com/index.php?page[]=about, an error may show a complete file path.</i></html>");
        run.setEnabled(false);
        run.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(listFile.getSelectedValuesList().size() == 0){
                    gui.model.sendErrorMessage("Select at least one file");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(run.getText().equals(defaultText)){
                            run.setText("Stop");
                            try {
                                gui.getOutputPanel().shellManager.clearSelection();
                                gui.getOutputPanel().sqlShellManager.clearSelection();
                                loader.setVisible(true);
                                gui.model.rao.getFile(listFile.getSelectedValuesList());
                            } catch (PreparationException e) {
                                gui.model.sendErrorMessage("Problem reading file");
                            } catch (StoppableException e) {
                                gui.model.sendErrorMessage("Problem reading file");
                            }

                        }else{
                            gui.model.rao.endFileSearch = true;
                            run.setEnabled(false);
                        }
                    }
                }, "getFile").start();
            }
        });

        privilege = new JLabel("File privilege", GUITools.SQUARE_GREY, SwingConstants.LEFT);
        privilege.setBorder(BorderFactory.createMatteBorder(2,0,0,0,GUITools.DEFAULT_BACKGROUND));
        privilege.setToolTipText("<html><b>Needs the file privilege to work</b><br>" +
                "Shows if the privilege FILE is granted to current user</html>");

        loader.setVisible(false);

        southPanel.add(privilege);
        southPanel.add(Box.createHorizontalGlue());
        southPanel.add(loader);
        southPanel.add(Box.createRigidArea(new Dimension(5,0)));
        southPanel.add(run);
        
        this.add(southPanel, BorderLayout.SOUTH);
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
    public void setButtonEnable(boolean a){
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
