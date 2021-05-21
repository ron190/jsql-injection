/*******************************************************************************
 * Copyhacked (H) 2012-2020.
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.jsql.util.I18nUtil;
import com.jsql.util.LogLevel;
import com.jsql.view.interaction.InteractionCommand;
import com.jsql.view.swing.menubar.JMenuItemWithMargin;
import com.jsql.view.swing.scrollpane.LightScrollPane;
import com.jsql.view.swing.tab.TabHeader;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.MediatorHelper;
import com.jsql.view.swing.util.UiUtil;

/**
 * Create a new tab for an administration webpage.
 */
public class CreateAdminPageTab extends CreateTabHelper implements InteractionCommand {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

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
        
        String htmlSource = StringUtils.EMPTY;
        
        // Fix #4081: SocketTimeoutException on get()
        // Fix #44642: NoClassDefFoundError on get()
        // Fix #44641: ExceptionInInitializerError on get()
        try {
            // Previous test for 2xx Success and 3xx Redirection was Header only,
            // now get the HTML content.
            // Proxy is used by jsoup
            htmlSource = Jsoup
                .clean(
                    Jsoup
                        .connect(this.url)
                        // Prevent exception on UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/*+xml
                        .ignoreContentType(true)
                        // Prevent exception on HTTP errors
                        .ignoreHttpErrors(true)
                        .get()
                        .html()
                        .replaceAll("<img[^>]*>", StringUtils.EMPTY)
                        .replaceAll("<input[^>]*type=\"?hidden\"?[^>]*>", StringUtils.EMPTY)
                        .replaceAll("<input[^>]*type=\"?(submit|button)\"?[^>]*>", "<div style=\"background-color:#eeeeee;text-align:center;border:1px solid black;width:100px;\">button</div>")
                        .replaceAll("<input[^>]*>", "<div style=\"text-align:center;border:1px solid black;width:100px;\">input</div>"),
                    Whitelist
                        .relaxed()
                        .addTags("center", "div", "span")
                        .addAttributes(":all", "style")
                );
            
        } catch (IOException e) {
            
            LOGGER.log(LogLevel.CONSOLE_ERROR, "Failure opening page: {}", e.getMessage());
            
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        final var browser = new JTextPane();
        browser.setContentType("text/html");
        browser.setEditable(false);
        
        // Fix #43220: EmptyStackException on setText()
        // Fix #94242: IndexOutOfBoundsException on setText()
        try {
            browser.setText(htmlSource);
            
        } catch (IndexOutOfBoundsException | EmptyStackException e) {
            
            LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
        }

        final var menu = new JPopupMenu();
        
        JMenuItem itemCopyUrl = new JMenuItemWithMargin(I18nUtil.valueByKey("CONTEXT_MENU_COPY_PAGE_URL"));
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_COPY_PAGE_URL", itemCopyUrl);
        
        JMenuItem itemCopy = new JMenuItemWithMargin();
        itemCopy.setAction(browser.getActionMap().get(DefaultEditorKit.copyAction));
        itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itemCopy.setMnemonic('C');
        itemCopy.setText(I18nUtil.valueByKey("CONTEXT_MENU_COPY"));
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_COPY", itemCopy);
        
        JMenuItem itemSelectAll = new JMenuItemWithMargin();
        itemSelectAll.setAction(browser.getActionMap().get(DefaultEditorKit.selectAllAction));
        itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        itemSelectAll.setText(I18nUtil.valueByKey("CONTEXT_MENU_SELECT_ALL"));
        I18nViewUtil.addComponentForKey("CONTEXT_MENU_SELECT_ALL", itemSelectAll);
        itemSelectAll.setMnemonic('A');
        
        menu.add(itemCopyUrl);
        menu.add(new JSeparator());
        menu.add(itemCopy);
        menu.add(itemSelectAll);
        
        menu.applyComponentOrientation(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()));

        itemCopyUrl.addActionListener(actionEvent -> {
            
            var stringSelection = new StringSelection(CreateAdminPageTab.this.url);
            var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
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
                        
                        LOGGER.log(LogLevel.CONSOLE_JAVA, e, e);
                    }
                    
                    menu.setLocation(
                        ComponentOrientation.RIGHT_TO_LEFT.equals(ComponentOrientation.getOrientation(I18nUtil.getLocaleDefault()))
                        ? evt.getXOnScreen() - menu.getWidth()
                        : evt.getXOnScreen(),
                        evt.getYOnScreen()
                    );
                }
            }
        });

        final var scroller = new LightScrollPane(1, 0, 0, 0, browser);
        MediatorHelper.tabResults().addTab(this.url.replaceAll(".*/", StringUtils.EMPTY) + StringUtils.SPACE, scroller);

        // Focus on the new tab
        MediatorHelper.tabResults().setSelectedComponent(scroller);

        // Create a custom tab header with close button
        var header = new TabHeader(
            this.url.replaceAll(
                ".*/",
                StringUtils.EMPTY
            ),
            UiUtil.ICON_ADMIN_SERVER
        );

        MediatorHelper.tabResults().setToolTipTextAt(
            MediatorHelper.tabResults().indexOfComponent(scroller),
            String.format(
                "<html>%s</html>",
                this.url
            )
        );

        // Apply the custom header to the tab
        MediatorHelper.tabResults().setTabComponentAt(MediatorHelper.tabResults().indexOfComponent(scroller), header);

        // Give focus to the admin page
        browser.requestFocusInWindow();

        // Get back to the top
        SwingUtilities.invokeLater(() ->
            scroller.scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0))
        );
    }
}
