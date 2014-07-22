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
import java.awt.Component;
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
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.RoundScroller;
import com.jsql.view.component.popupmenu.JPopupTextField;
import com.jsql.view.dnd.list.DnDList;
import com.jsql.view.dnd.list.ListItem;

/**
 * Manager for uploading PHP webshell to the host
 */
@SuppressWarnings("serial")
public class WebshellManager extends JPanel{
    /**
     * Contains the paths of webshell.
     */
    private DnDList shellPaths;

    /**
     * Starts the upload process.
     */
    private JButton run;

    /**
     * Display the FILE privilege of current user.
     */
    private JLabel privilege;

    /**
     * Text of the button that start the upload process.
     * Used to get back the default text after a search (defaultText->"Stop"->defaultText).
     */
    private String defaultText = "Create web shell";

    /**
     * Build the manager panel.
     * @param gui The main frame
     */
    public WebshellManager(){
        super(new BorderLayout());

        ArrayList<String> pathsList = new ArrayList<String>();
        try {
            InputStream in = this.getClass().getResourceAsStream("/com/jsql/list/shell.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader( in ));
            while( (line = reader.readLine()) != null ) pathsList.add(line);
            reader.close();
        } catch (IOException e) {
        	GUIMediator.model().sendDebugMessage(e);
        }

        shellPaths = new DnDList(pathsList);
        this.add(new RoundScroller(shellPaths), BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("[Optional] URL to the web shell directory:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Works only for BoxLayout

        final JPopupTextField shellURL = new JPopupTextField();
        String tooltip = "<html><b>How to use</b><br>" +
                "- Leave blank if the file from address bar is located in selected folder(s), shell will also be in it.<br>" +
                "<i>E.g Address bar is set with http://127.0.0.1/simulate_get.php?lib=, file simulate_get.php<br>" +
                "is located in selected '/var/www/', then shell will be created in that folder.</i><br>" +
                "- Or force URL for the selected folder.<br>" +
                "<i>E.g Shell is created in selected '/var/www/site/folder/' ; corresponding URL for this folder<br>" +
                "is http://site.com/another/path/ (because of alias or url rewriting for example).</i></html>";
        shellURL.setToolTipText(tooltip);
        shellURL.setBorder(GUITools.BLU_ROUND_BORDER);

        JPanel lastLine = new JPanel();
        lastLine.setLayout( new BoxLayout(lastLine, BoxLayout.X_AXIS) );

        run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/shellSearch.png")));
        run.setToolTipText("<html><b>Select folder(s) in which shell is created</b><br>" +
                "Path must be correct and correspond to a PHP folder, gives no result otherwise.<br>" +
                "<i>If necessary, you must set the URL of shell directory (see note on text component).</i>" +
                "</html>");
        run.setEnabled(false);
        run.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, GUITools.DEFAULT_BACKGROUND),
                GUITools.BLU_ROUND_BORDER));
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(shellPaths.getSelectedValuesList().size() == 0){
                	GUIMediator.model().sendErrorMessage("Select at least one directory");
                    return;
                }

                for(final Object path: shellPaths.getSelectedValuesList()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            	GUIMediator.model().rao.getShell(path.toString(), shellURL.getText());
                            } catch (PreparationException e) {
                            	GUIMediator.model().sendErrorMessage("Problem writing into " + path);
                            } catch (StoppableException e) {
                            	GUIMediator.model().sendErrorMessage("Problem writing into " + path);
                            }
                        }
                    }, "getShell").start();
                }

            }
        });

        privilege = new JLabel("File privilege", GUITools.SQUARE_GREY, SwingConstants.LEFT);
        privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GUITools.DEFAULT_BACKGROUND));
        privilege.setToolTipText("<html><b>Needs the file privilege to work</b><br>" +
                "Shows if the privilege FILE is granted to current user</html>");

        lastLine.add(privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(run);

        southPanel.add(label);
        southPanel.add(shellURL);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Add a new string to the list if it's not a duplicate.
     * @param element The string to add to the list
     */
    public void addToList(String element){
        boolean found = false;
        for (int i = 0 ; i < ((DefaultListModel<ListItem>)shellPaths.getModel()).size() ; i++){
            if (((DefaultListModel<ListItem>)shellPaths.getModel()).get(i).toString().equals(element)) {
                found = true;
            }
        }
        if(!found){
            ListItem v = new ListItem(element);
            ((DefaultListModel<ListItem>)shellPaths.getModel()).addElement(v);
        }
    }

    /**
     * Unselect every element of the list.
     */
    public void clearSelection(){
        shellPaths.clearSelection();
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
