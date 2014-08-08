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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.list.dnd.DnDList;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;

/**
 * Manager to read a file from the host.
 */
@SuppressWarnings("serial")
public class FileManager extends ListManager {

    public FileManager() {
        this.setLayout(new BorderLayout());
        this.setDefaultText("Read file(s)");
        
        List<String> pathList = new ArrayList<String>();
        try {
            InputStream in = this.getClass().getResourceAsStream("/com/jsql/list/file.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                pathList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            InjectionModel.LOGGER.error(e, e);
        }

        final DnDList listFile = new DnDList(pathList);

        this.add(new JScrollPanePixelBorder(1, 1, 0, 0, listFile), BorderLayout.CENTER);

        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.X_AXIS));
        lastLine.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, GUITools.COMPONENT_BORDER),
                BorderFactory.createEmptyBorder(1, 0, 1, 1)));
        
        run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/fileSearch.png")));

        run.setToolTipText("<html><b>Select file(s) to read</b><br>" +
                "Path must be correct, gives no result otherwise.<br>" +
                "<i>Default list contains well known file paths. Use a Full Path Disclosure tool to obtain an existing path<br>" +
                "from remote host, or in your browser try to output an error containing an existing file path as simply<br>" +
                "as followed: if remote host can be requested like http://site.com/index.php?page=about, then try to<br>" +
                "browse instead http://site.com/index.php?page[]=about, an error may show a complete file path.</i></html>");
        run.setEnabled(false);
        run.setBorder(GUITools.BLU_ROUND_BORDER);
        
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (listFile.getSelectedValuesList().isEmpty()) {
                    InjectionModel.LOGGER.warn("Select at least one file");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (run.getText().equals(defaultText)) {
                            run.setText("Stop");
                            try {
                                GUIMediator.left().shellManager.clearSelection();
                                GUIMediator.left().sqlShellManager.clearSelection();
                                loader.setVisible(true);
                                GUIMediator.model().rao.getFile(listFile.getSelectedValuesList());
                            } catch (PreparationException e) {
                                InjectionModel.LOGGER.warn("Problem reading file");
                            } catch (StoppableException e) {
                                InjectionModel.LOGGER.warn("Problem reading file");
                            }

                        } else {
                            GUIMediator.model().rao.endFileSearch = true;
                            run.setEnabled(false);
                        }
                    }
                }, "getFile").start();
            }
        });

        privilege = new JLabel("File privilege", GUITools.SQUARE_GREY, SwingConstants.LEFT);
        privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GUITools.DEFAULT_BACKGROUND));
        privilege.setToolTipText("<html><b>Needs the file privilege to work</b><br>" +
                "Shows if the privilege FILE is granted to current user</html>");

        loader.setVisible(false);

        lastLine.add(privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(loader);
        lastLine.add(Box.createRigidArea(new Dimension(5, 0)));
        lastLine.add(run);
        
        this.add(lastLine, BorderLayout.SOUTH);
    }
}
