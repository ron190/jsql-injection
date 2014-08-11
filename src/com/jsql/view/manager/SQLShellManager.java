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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.view.GUITools;
import com.jsql.view.list.dnd.DnDList;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.textcomponent.JPopupTextField;

/**
 * Manager for uploading PHP SQL shell to the host and send queries.
 */
@SuppressWarnings("serial")
public class SQLShellManager extends AbstractListManager {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(SQLShellManager.class);

    /**
     * Build the manager panel.
     */
    public SQLShellManager() {
        this.setLayout(new BorderLayout());
        this.setDefaultText("Create SQL shell");

        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUITools.COMPONENT_BORDER));
        
        JPanel infos = new JPanel();
        
        GroupLayout layout = new GroupLayout(infos);
        infos.setLayout(layout);
        infos.setAlignmentX(Component.LEFT_ALIGNMENT);
        infos.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        
        JLabel userLabel = new JLabel(" [Optional] User ");
        JLabel passLabel = new JLabel(" [Optional] Pass ");
        final JTextField user = new JPopupTextField("[Optional] User").getProxy();
        final JTextField pass = new JPopupTextField("[Optional] Pass").getProxy();
        
        user.setToolTipText("<html><b>MySQL username</b><br>" +
                "Users are stored in table <i>user</i> of database <i>mysql</i>.<br>" +
                "Can be left empty if no user has been defined.<br>" +
                "<i>Try to read an existing php file to get plaintext database credentials.</i></html>");
        pass.setToolTipText("<html><b>MySQL password</b><br>" +
                "Password hashes are stored in table <i>user</i> of database <i>mysql</i>.<br>" +
                "You can brute force the hash with type <i>mysql</i>.<br>" +
                "Can be left empty if no password has been defined.<br>" +
                "<i>Try to read an existing php file to get plaintext database credentials.</i></html>");
        
        user.setBorder(GUITools.BLU_ROUND_BORDER);
        JPanel m = new JPanel(new BorderLayout());
        m.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        m.add(pass);
        JPanel mm = new JPanel(new BorderLayout());
        mm.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        mm.add(passLabel);
        pass.setBorder(GUITools.BLU_ROUND_BORDER);
        
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(userLabel)
                            .addComponent(mm))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(user)
                            .addComponent(m))
            );

            layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(userLabel)
                            .addComponent(user))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(mm)
                            .addComponent(m))
            );
        
        this.add(infos, BorderLayout.NORTH);
        
        List<String> pathsList = new ArrayList<String>();
        try {
            InputStream in = this.getClass().getResourceAsStream("/com/jsql/list/shell.txt");
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                pathsList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        this.listPaths = new DnDList(pathsList);
        this.add(new JScrollPanePixelBorder(1, 1, 0, 0, this.listPaths), BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("[Optional] URL to the SQL shell directory:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        final JTextField shellURL = new JPopupTextField("[Optional] URL to the SQL shell directory").getProxy();
        
        String tooltip = "<html><b>How to use optional shell URL</b><br>" +
                "- Leave blank if the file from URL is in selected folder, shell will be created in this folder.<br>" +
                "<i>E.g Address bar is set with http://127.0.0.1/simulate_get.php?lib=, file simulate_get.php<br>" +
                "is located in selected '/var/www/', then shell will be created in that folder.</i><br>" +
                "- Or force a URL for selected folder.<br>" +
                "<i>E.g Shell is created in selected '/var/www/site/folder/' ; corresponding URL for this folder<br>" +
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

        this.run = new JButton(defaultText, new ImageIcon(getClass().getResource("/com/jsql/view/images/shellSearch.png")));
        this.run.setToolTipText("<html><b>Select folder(s) in which shell is created</b><br>" +
                "Path must be correct and correspond to a PHP folder, gives no result otherwise.<br>" +
                "<i>If necessary, you must set the URL of shell directory (see note on optional URL).</i>" +
                "</html>");
        this.run.setEnabled(false);
        
        this.run.setBorder(GUITools.BLU_ROUND_BORDER);
        
        this.run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (SQLShellManager.this.listPaths.getSelectedValuesList().isEmpty()) {
                    LOGGER.warn("Select at least one directory");
                    return;
                }
                
                if (!"".equals(shellURL.getText())) {
                    try {
                        new URL(shellURL.getText());
                    } catch (MalformedURLException e) {
                        LOGGER.warn("URL is malformed: no protocol");
                        return;
                    }
                }

                for (final Object path: SQLShellManager.this.listPaths.getSelectedValuesList()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                InjectionModel.RAO.getSQLShell(path.toString(), shellURL.getText(), user.getText(), pass.getText());
                            } catch (PreparationException e) {
                                LOGGER.warn("Problem writing into " + path);
                            } catch (StoppableException e) {
                                LOGGER.warn("Problem writing into " + path);
                            }
                        }
                    }, "getShell").start();
                }

            }
        });

        this.privilege = new JLabel("File privilege", GUITools.SQUARE_GREY, SwingConstants.LEFT);
        this.privilege.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GUITools.DEFAULT_BACKGROUND));
        this.privilege.setToolTipText("<html><b>Needs the file privilege to work</b><br>" +
                "Shows if the privilege FILE is granted to current user</html>");

        lastLine.add(this.privilege);
        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(this.run);

        southPanel.add(shellURL);
        southPanel.add(lastLine);
        this.add(southPanel, BorderLayout.SOUTH);
    }
}
