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
package com.jsql.view.interaction;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jsql.i18n.I18n;
import com.jsql.view.MediatorGUI;
import com.jsql.view.ToolsGUI;
import com.jsql.view.scrollpane.JScrollPanePixelBorder;
import com.jsql.view.tab.TabHeader;

/**
 * Create a new tab for an administration webpage.
 */
public class CreateAdminPageTab implements IInteractionCommand {
    /**
     * Url for the administration webpage.
     */
    private final String url;

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(CreateAdminPageTab.class);

    /**
     * @param interactionParams Url of the webpage
     */
    public CreateAdminPageTab(Object[] interactionParams) {
        url = (String) interactionParams[0];
    }

    @Override
    public void execute() {
        String htmlSource = "";
        try {
            htmlSource = Jsoup.clean(Jsoup.connect(url).get().html()
                    .replaceAll("<img.*>", "")
                    .replaceAll("<input.*type=\"?hidden\"?.*>", "")
                    .replaceAll("<input.*type=\"?(submit|button)\"?.*>", "<div style=\"background-color:#eeeeee;text-align:center;border:1px solid black;width:100px;\">button</div>") 
                    .replaceAll("<input.*>", "<div style=\"text-align:center;border:1px solid black;width:100px;\">input</div>"),
                    Whitelist.relaxed()
                    .addTags("center", "div", "span")
                    //              .addAttributes("input","type","value","disabled")
                    //              .addAttributes("img","src","width","height")
                    .addAttributes(":all", "style")
                    //              .addEnforcedAttribute("input", "disabled", "disabled")
                    );
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        final JTextPane browser = new JTextPane();
        browser.setContentType("text/html");
        browser.setEditable(false);
        browser.setText(htmlSource);

        final JPopupMenu menu = new JPopupMenu();
        
        JMenuItem item = new JMenuItem(I18n.COPY_PAGE_URL);
        item.setIcon(ToolsGUI.EMPTY);
        
        JMenuItem copyItem = new JMenuItem();
        copyItem.setAction(browser.getActionMap().get(DefaultEditorKit.copyAction));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copyItem.setMnemonic('C');
        copyItem.setText(I18n.COPY);
        copyItem.setIcon(ToolsGUI.EMPTY);
        
        JMenuItem itemSelectAll = new JMenuItem();
        itemSelectAll.setIcon(ToolsGUI.EMPTY);
        itemSelectAll.setAction(browser.getActionMap().get(DefaultEditorKit.selectAllAction));
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.setText(I18n.SELECT_ALL);
        itemSelectAll.setMnemonic('A');
        
        menu.add(item);
        menu.add(new JSeparator());
        menu.add(copyItem);
        menu.add(itemSelectAll);

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                StringSelection stringSelection = new StringSelection(url);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });

        itemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                browser.selectAll();
            }
        });
        
        browser.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        final JScrollPanePixelBorder scroller = new JScrollPanePixelBorder(1, 0, 0, 0, browser);
        MediatorGUI.right().addTab(url.replaceAll(".*/", "") + " ", scroller);

        // Focus on the new tab
        MediatorGUI.right().setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(new ImageIcon(getClass().getResource("/com/jsql/view/images/admin.png")));

        MediatorGUI.right().setToolTipTextAt(MediatorGUI.right().indexOfComponent(scroller), "<html>" + url + "</html>");

        // Apply the custom header to the tab
        MediatorGUI.right().setTabComponentAt(MediatorGUI.right().indexOfComponent(scroller), header);

        browser.requestFocusInWindow();

        // Get back to the top
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scroller.getViewport().setViewPosition(new java.awt.Point(0, 0));
            }
        });
    }
}
