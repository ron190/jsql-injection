/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.manager;

import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.list.DnDList;
import com.jsql.view.swing.list.DnDListScan;
import com.jsql.view.swing.list.ItemList;
import com.jsql.view.swing.list.ItemListScan;
import com.jsql.view.swing.manager.util.JButtonStateful;
import com.jsql.view.swing.manager.util.StateButton;
import com.jsql.view.swing.util.I18nViewUtil;
import com.jsql.view.swing.util.UiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

/**
 * Abstract manager containing a drag and drop list of item.
 */
public abstract class AbstractManagerList extends JPanel {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    protected final transient List<ItemList> itemsList = new ArrayList<>();
    protected final JPanel lastLine = new JPanel();

    /**
     * Contains the paths of files.
     */
    protected DnDList listPaths;

    /**
     * Starts the upload process.
     */
    protected JButtonStateful run;

    /**
     * Display the FILE privilege of current user.
     */
    protected JLabel privilege;

    /**
     * Text of the button that start the upload process.
     * Used to get back the default text after a search (defaultText->"Stop"->defaultText).
     */
    protected String defaultText;

    /**
     * A animated GIF displayed during processing.
     */
    protected final JProgressBar progressBar = new JProgressBar();
    protected final Component horizontalGlue = Box.createHorizontalGlue();

    protected AbstractManagerList() {
        // Nothing
    }
    
    protected AbstractManagerList(String nameFile) {
        this(nameFile, false);
    }

    protected AbstractManagerList(String nameFile, boolean isJson) {
        this.progressBar.setIndeterminate(true);
        this.progressBar.setVisible(false);
        this.setLayout(new BorderLayout());

        if (isJson) {
            var jsonScan = new StringBuilder();
            try (
                var inputStream = UiUtil.class.getClassLoader().getResourceAsStream(UiUtil.INPUT_STREAM_PAGES_SCAN);
                var inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
                var reader = new BufferedReader(inputStreamReader)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonScan.append(line);
                }
                var jsonArrayScan = new JSONArray(jsonScan.toString());
                for (var i = 0 ; i < jsonArrayScan.length() ; i++) {
                    this.itemsList.add(new ItemListScan(jsonArrayScan.getJSONObject(i)));
                }
                this.listPaths = new DnDListScan(this.itemsList);
            } catch (JSONException | IOException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        } else {
            try (
                var inputStream = UiUtil.class.getClassLoader().getResourceAsStream(nameFile);
                var inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
                var reader = new BufferedReader(inputStreamReader)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    this.itemsList.add(new ItemList(line));
                }
                this.listPaths = new DnDList(this.itemsList);
            } catch (IOException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }

        this.lastLine.setLayout(new BoxLayout(this.lastLine, BoxLayout.X_AXIS));
        this.add(new JScrollPane(this.listPaths), BorderLayout.CENTER);
    }

    /**
     * Add a new string to the list if it's not a duplicate.
     * @param element The string to add to the list
     */
    public void addToList(String element) {
        AtomicBoolean isFound = new AtomicBoolean(false);

        DefaultListModel<ItemList> listModel = (DefaultListModel<ItemList>) this.listPaths.getModel();
        Iterable<ItemList> iterable = () -> listModel.elements().asIterator();
        StreamSupport.stream(iterable.spliterator(), false)
            .filter(itemList -> itemList.toString().equals(element))
            .forEach(itemList -> isFound.set(true));

        if (!isFound.get()) {
            listModel.addElement(new ItemList(element));
        }
    }
    
    public void highlight(String url, String tag) {
        var itemLabel = String.format(" [%s]", tag);
        DefaultListModel<ItemList> listModel = (DefaultListModel<ItemList>) this.listPaths.getModel();
        for (var i = 0 ; i < listModel.getSize() ; i++) {
            ItemList itemList = listModel.getElementAt(i);
            if (url.contains(itemList.getOriginalString())) {
                itemList.setVulnerable(true);
                itemList.setInternalString(
                    itemList.getInternalString().replace(itemLabel, StringUtils.EMPTY) + itemLabel
                );
                listModel.setElementAt(itemList, i);
            }
        }
    }
    
    public void endProcess() {
        SwingUtilities.invokeLater(() -> {  // required to prevent scan glitches
            this.run.setText(I18nViewUtil.valueByKey(this.defaultText));
            this.setButtonEnable(true);
            this.progressBar.setVisible(false);
            this.horizontalGlue.setVisible(true);
            this.run.setState(StateButton.STARTABLE);
        });
    }

    /**
     * Unselect every element of the list.
     */
    public void clearSelection() {
        this.listPaths.clearSelection();
    }

    /**
     * Enable or disable the button.
     * @param isEnable The new state of the button
     */
    public void setButtonEnable(boolean isEnable) {
        this.run.setEnabled(isEnable);
    }

    /**
     * Display another icon to the Privilege label.
     * @param icon The new icon
     */
    public void changePrivilegeIcon(Icon icon) {
        this.privilege.setIcon(icon);
    }
}
