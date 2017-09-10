/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import java.awt.ComponentOrientation;
import java.awt.IllegalComponentStateException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.EmptyStackException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jsql.i18n.I18n;
import com.jsql.view.i18n.I18nView;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tab.TabHeader;

/**
 * Create a new tab for an administration webpage.
 */
public class CreateAdminPageTab extends CreateTab implements InteractionCommand {

    /**
     * Url for the administration webpage.
     */
    private final String url;

    /**
     * @param interactionParams Url of the webpage
     */
    public CreateAdminPageTab(Object[] interactionParams) {
        this.url = (String) interactionParams[0];
    }

    @Override
    public void execute() {
        if (MediatorGui.tabResults() == null) {
            LOGGER.error("Unexpected unregistered MediatorGui.tabResults() in "+ this.getClass());
        }
        
        String htmlSource = "";
        // Fix #4081: SocketTimeoutException on get()
        // Fix #44642: NoClassDefFoundError on get()
        // Fix #44641: ExceptionInInitializerError on get()
        try {
            // Previous test for 2xx Success and 3xx Redirection was Header only,
            // now get the HTML content.
            // Proxy is used by jsoup
            htmlSource = Jsoup.clean(
                Jsoup.connect(this.url).get().html()
                    .replaceAll("<img.*>", "")
                    .replaceAll("<input.*type=\"?hidden\"?.*>", "")
                    .replaceAll("<input.*type=\"?(submit|button)\"?.*>", "<div style=\"background-color:#eeeeee;text-align:center;border:1px solid black;width:100px;\">button</div>")
                    .replaceAll("<input.*>", "<div style=\"text-align:center;border:1px solid black;width:100px;\">input</div>"),
                Whitelist.relaxed()
                    .addTags("center", "div", "span")
                    .addAttributes(":all", "style")
            );
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            LOGGER.warn("Jsoup not properly configured, please update jsql", e);
        }

        final JTextPane browser = new JTextPane();
        browser.setContentType("text/html");
        browser.setEditable(false);
        
        // Fix #43220: EmptyStackException on setText()
        try {
            browser.setText(htmlSource);
        } catch (EmptyStackException e) {
            LOGGER.error(e, e);
        }

        final JPopupMenu menu = new JPopupMenu();
        
        JMenuItem itemCopyUrl = new JMenuItem(I18n.valueByKey("CONTEXT_MENU_COPY_PAGE_URL"));
        I18nView.addComponentForKey("CONTEXT_MENU_COPY_PAGE_URL", itemCopyUrl);
        itemCopyUrl.setIcon(HelperUi.ICON_EMPTY);
        
        JMenuItem itemCopy = new JMenuItem();
        itemCopy.setAction(browser.getActionMap().get(DefaultEditorKit.copyAction));
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.setMnemonic('C');
        itemCopy.setText(I18n.valueByKey("CONTEXT_MENU_COPY"));
        I18nView.addComponentForKey("CONTEXT_MENU_COPY", itemCopy);
        itemCopy.setIcon(HelperUi.ICON_EMPTY);
        
        JMenuItem itemSelectAll = new JMenuItem();
        itemSelectAll.setIcon(HelperUi.ICON_EMPTY);
        itemSelectAll.setAction(browser.getActionMap().get(DefaultEditorKit.selectAllAction));
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.setText(I18n.valueByKey("CONTEXT_MENU_SELECT_ALL"));
        I18nView.addComponentForKey("CONTEXT_MENU_SELECT_ALL", itemSelectAll);
        itemSelectAll.setMnemonic('A');
        
        menu.add(itemCopyUrl);
        menu.add(new JSeparator());
        menu.add(itemCopy);
        menu.add(itemSelectAll);
        
        menu.applyComponentOrientation(ComponentOrientation.getOrientation(I18n.getLocaleDefault()));

        itemCopyUrl.addActionListener(actionEvent -> {
            StringSelection stringSelection = new StringSelection(CreateAdminPageTab.this.url);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });

        itemSelectAll.addActionListener(actionEvent -> browser.selectAll());
        
        browser.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                browser.getCaret().setVisible(true);
                browser.getCaret().setSelectionVisible(true);
            }
        });
        
        browser.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                browser.requestFocusInWindow();
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    // Fix #45348: IllegalComponentStateException on show()
                    try {
                        menu.show(evt.getComponent(), evt.getX(), evt.getY());
                    } catch (IllegalComponentStateException e) {
                        LOGGER.error(e, e);
                    }
                    
                    menu.setLocation(
                        ComponentOrientation.getOrientation(I18n.getLocaleDefault()) == ComponentOrientation.RIGHT_TO_LEFT
                        ? evt.getXOnScreen() - menu.getWidth()
                        : evt.getXOnScreen(),
                        evt.getYOnScreen()
                    );
                }
            }
        });

        final LightScrollPane scroller = new LightScrollPane(1, 0, 0, 0, browser);
        MediatorGui.tabResults().addTab(this.url.replaceAll(".*/", "") +" ", scroller);

        // Focus on the new tab
        MediatorGui.tabResults().setSelectedComponent(scroller);

        // Create a custom tab header with close button
        TabHeader header = new TabHeader(this.url.replaceAll(".*/", ""), HelperUi.ICON_ADMIN_SERVER);

        MediatorGui.tabResults().setToolTipTextAt(MediatorGui.tabResults().indexOfComponent(scroller), "<html>"+ this.url +"</html>");

        // Apply the custom header to the tab
        MediatorGui.tabResults().setTabComponentAt(MediatorGui.tabResults().indexOfComponent(scroller), header);

        // Give focus to the admin page
        browser.requestFocusInWindow();

        // Get back to the top
        SwingUtilities.invokeLater(() ->
            scroller.scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0))
        );
    }
    
}
