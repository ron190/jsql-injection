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
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.view.GUI;
import com.jsql.view.GUITools;
import com.jsql.view.RoundScroller;
import com.jsql.view.component.popup.JPopupTextField;
import com.jsql.view.dialog.FileChooser;
import com.jsql.view.dnd.list.DnDList;
import com.jsql.view.dnd.list.ListItem;

/**
 * Manager for uploading PHP webshell to the host
 */
public class UploadManager extends JPanel{
    private static final long serialVersionUID = -8504371566082352384L;

    /**
     * Main frame.
     */
    private GUI gui;

    /**
     * Contains the paths of webshell.
     */
    private JList<ListItem> folderPaths;

    /**
     * Starts the upload process.
     */
    private JButton run;

    private JLabel loader = new JLabel(GUITools.SPINNER);

    /**
     * Display the FILE privilege of current user.
     */
    private JLabel privilege;

    /**
     * Text of the button that start the upload process.
     * Used to get back the default text after a search (defaultText->"Stop"->defaultText).
     */
    private String defaultText = "Choose a file";

    /**
     * Build the manager panel.
     * @param gui The main frame
     */
    public UploadManager(final GUI gui){
        super(new BorderLayout());

        this.gui = gui;

        ArrayList<String> pathsList = new ArrayList<String>();
        pathsList.add("/var/www/html/defaut/");
        pathsList.add("/var/www/html/default/");
        pathsList.add("/var/www/html/");
        pathsList.add("/var/www/");
        pathsList.add("/home/www/");
        pathsList.add("E:/Outils/EasyPHP-5.3.9/www/");

        folderPaths = new DnDList(gui, pathsList);
        this.add(new RoundScroller(folderPaths), BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("[Optional] URL to the upload directory:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Works only for BoxLayout

        final JPopupTextField shellURL = new JPopupTextField();
        String tooltip = "<html><b>How to use</b><br>" +
                "- Leave blank if the file from address bar is located in selected folder(s), webshell will also be in it.<br>" +
                "<i>E.g Address bar is set with http://127.0.0.1/simulate_get.php?lib=, file simulate_get.php<br>" +
                "is located in selected '/var/www/', then uploader will be created in that folder.</i><br>" +
                "- Or force URL for the selected folder.<br>" +
                "<i>E.g Uploader is created in selected '/var/www/site/folder/' ; corresponding URL for this folder<br>" +
                "is http://site.com/another/path/ (because of alias or url rewriting for example).</i></html>";
        shellURL.setToolTipText(tooltip);
        shellURL.setBorder(GUITools.BLU_ROUND_BORDER);

        JPanel lastLine = new JPanel();
        lastLine.setLayout( new BoxLayout(lastLine, BoxLayout.X_AXIS) );

        run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/add.png")));
        run.setToolTipText("<html><b>Select folder(s) in which uploader is created, then choose a file to upload</b><br>" +
                "Path must be correct and correspond to a PHP folder, gives no result otherwise.<br>" +
                "<i>If necessary, you must set the URL of uploader directory (see note on text component).</i>" +
                "</html>");
        run.setEnabled(false);
        run.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, GUITools.DEFAULT_BACKGROUND),
                GUITools.BLU_ROUND_BORDER));
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(folderPaths.getSelectedValuesList().size() == 0){
                    gui.model.sendErrorMessage("Select at least one directory");
                    return;
                }

                final JFileChooser filechooser = new JFileChooser(gui.model.pathFile);
                filechooser.setDialogTitle("Choose file to upload");
                
                int returnVal = filechooser.showOpenDialog(gui);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    for(final ListItem path: folderPaths.getSelectedValuesList()){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File file = filechooser.getSelectedFile();
                                try{
                                    loader.setVisible(true);
                                    gui.model.rao.upload(path.toString(), shellURL.getText(), file);
                                }catch (PreparationException e){
                                    gui.model.sendErrorMessage("Can't upload file "+ file.getName() +" to " + path);
                                }catch (StoppableException e){
                                    gui.model.sendErrorMessage("Can't upload file "+ file.getName() +" to " + path);
                                }
                            }
                        }, "upload").start();
                    }
                }
            }
        });

        privilege = new JLabel("File privilege", GUITools.SQUARE_GREY, SwingConstants.LEFT);
        privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GUITools.DEFAULT_BACKGROUND));
        privilege.setToolTipText("<html><b>Needs the file privilege to work</b><br>" +
                "Shows if the privilege FILE is granted to current user</html>");

        loader.setVisible(false);

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
        int i = 0;
        boolean found = false;
        for (;i < ((DefaultListModel<ListItem>)folderPaths.getModel()).size();i++){
            if (((DefaultListModel<ListItem>)folderPaths.getModel()).get(i).toString().equals(element)) {
                found = true;
            }
        }
        if(!found){
            ListItem v = new ListItem(element);
            ((DefaultListModel<ListItem>)folderPaths.getModel()).addElement(v);
        }
    }

    /**
     * Hide the loader icon.
     */
    public void hideLoader(){
        loader.setVisible(false);
    }

    /**
     * Unselect every element of the list.
     */
    public void clearSelection(){
        folderPaths.clearSelection();
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
