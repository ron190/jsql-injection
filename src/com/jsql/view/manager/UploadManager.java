/*******************************************************************************
 * Copyhacked (H) 2012-2014.
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.list.dnd.DnDList;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.textcomponent.JPopupTextField;

/**
 * Manager to upload files to the host.
 */
@SuppressWarnings("serial")
public class UploadManager extends AbstractListManager {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(UploadManager.class);

    /**
     * Build the manager panel.
     */
    public UploadManager() {
        this.setLayout(new BorderLayout());

        this.setDefaultText("Choose a file");

        List<String> pathsList = new ArrayList<String>();
        pathsList.add("/var/www/html/defaut/");
        pathsList.add("/var/www/html/default/");
        pathsList.add("/var/www/html/");
        pathsList.add("/var/www/");
        pathsList.add("/home/www/");
        pathsList.add("E:/Outils/EasyPHP-5.3.9/www/");

        this.listPaths = new DnDList(pathsList);
        this.add(new JScrollPanePixelBorder(1, 1, 0, 0, this.listPaths), BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("[Optional] URL to the upload directory:");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        final JTextField shellURL = new JPopupTextField("[Optional] URL to the upload directory").getProxy();
        String tooltip = "<html><b>How to use</b><br>" +
                "- Leave blank if the file from address bar is located in selected folder(s), webshell will also be in it.<br>" +
                "<i>E.g Address bar is set with http://127.0.0.1/simulate_get.php?lib=, file simulate_get.php<br>" +
                "is located in selected '/var/www/', then uploader will be created in that folder.</i><br>" +
                "- Or force URL for the selected folder.<br>" +
                "<i>E.g Uploader is created in selected '/var/www/site/folder/' ; corresponding URL for this folder<br>" +
                "is http://site.com/another/path/ (because of alias or url rewriting for example).</i></html>";
        
        shellURL.setToolTipText(tooltip);
        shellURL.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 1, 0, 0, GUITools.COMPONENT_BORDER),
                        BorderFactory.createMatteBorder(1, 1, 0, 1, GUITools.DEFAULT_BACKGROUND)),
                        GUITools.BLU_ROUND_BORDER));

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, GUITools.COMPONENT_BORDER), 
                BorderFactory.createEmptyBorder(1, 0, 1, 1)));

        this.run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/add.png")));
        this.run.setToolTipText("<html><b>Select folder(s) in which uploader is created, then choose a file to upload</b><br>" +
                "Path must be correct and correspond to a PHP folder, gives no result otherwise.<br>" +
                "<i>If necessary, you must set the URL of uploader directory (see note on text component).</i>" +
                "</html>");
        this.run.setEnabled(false);
        
        this.run.setBorder(GUITools.BLU_ROUND_BORDER);
        
        this.run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (UploadManager.this.listPaths.getSelectedValuesList().isEmpty()) {
                    LOGGER.warn("Select at least one directory");
                    return;
                }

                final JFileChooser filechooser = new JFileChooser(GUIMediator.model().prefPathFile);
                filechooser.setDialogTitle("Choose file to upload");
                
                int returnVal = filechooser.showOpenDialog(GUIMediator.gui());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    for (final Object path: UploadManager.this.listPaths.getSelectedValuesList()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File file = filechooser.getSelectedFile();
                                try {
                                    UploadManager.this.loader.setVisible(true);
                                    InjectionModel.RAO.upload(path.toString(), shellURL.getText(), file);
                                } catch (PreparationException e) {
                                    LOGGER.warn("Can't upload file " + file.getName() + " to " + path);
                                } catch (StoppableException e) {
                                    LOGGER.warn("Can't upload file " + file.getName() + " to " + path);
                                }
                            }
                        }, "upload").start();
                    }
                }
            }
        });

        this.privilege = new JLabel("File privilege", GUITools.SQUARE_GREY, SwingConstants.LEFT);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GUITools.DEFAULT_BACKGROUND));
        this.privilege.setToolTipText("<html><b>Needs the file privilege to work</b><br>" +
                "Shows if the privilege FILE is granted to current user</html>");

        this.loader.setVisible(false);

        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);

        southPanel.add(shellURL);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }
}
