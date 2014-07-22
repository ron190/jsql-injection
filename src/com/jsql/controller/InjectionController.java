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
package com.jsql.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.Interruptable;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.view.GUIMediator;

/**
 * Controller in the MVC pattern, is involved mainly when the user makes actions
 * on the GUI: uses Connect button, validates a database, table or values
 */
public class InjectionController {
    
    /**
     * Send each parameters from the GUI to the model in order to start the preparation of injection,
     * the injection process is started in a new thread via model function inputValidation()
     */
    public void controlInput(String getData, String postData, String cookieData, String headerData, String method) {
        try {
            // Parse url and GET query string
            GUIMediator.model().getData = "";
            Matcher regexSearch = Pattern.compile("(.*)(\\?.*)").matcher(getData);
            if(regexSearch.find()){
                URL url = new URL( getData );
                GUIMediator.model().initialUrl = regexSearch.group(1);
                if( !url.getQuery().equals("") )
                    GUIMediator.model().getData = regexSearch.group(2);
            }else{
                GUIMediator.model().initialUrl = getData;
            }
            
            // Define other methods
            GUIMediator.model().postData = postData;
            GUIMediator.model().cookieData = cookieData;
            GUIMediator.model().headerData = headerData;
            GUIMediator.model().method = method;
            
            // Reset level of evasion
            GUIMediator.model().securitySteps = 0;
            
            // Start the model injection process in a thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GUIMediator.model().inputValidation();
                }
            }, "InjectionController - controlInput").start();
            
            // Erase everything in the view from a previous injection
            GUIMediator.gui().resetInterface();
        } catch (MalformedURLException e) {
            GUIMediator.model().sendMessage(e.getMessage());
        }
    }

    /**
     * Process the user choice on the view: search for tables.
     * Interruptable action will be paused directly by the view, so we return it up to the view (@return),
     * also its state will be checked by the main SQL injection method, so we pass it as a parameter down to the model.
     * @param databaseSelected database selected by user
     * @return Interruptable sent upward to the view, allows the start/pause/stop actions from the view
     */
    public Interruptable selectDatabase(final Database databaseSelected){
        // Indirect class, allows to send itself to his own body
        final Interruptable[] interruptable = new Interruptable[1];
        
        interruptable[0] = new Interruptable(GUIMediator.model()){
            @Override
            public void action() {
                
                try {
                    GUIMediator.model().dao.listTables(databaseSelected, interruptable[0]);
                } catch (NumberFormatException e) {
                    GUIMediator.model().sendErrorMessage("Error during table search: incorrect number " + e.getMessage());
                } catch (PreparationException e) {
                    GUIMediator.model().sendErrorMessage(e.getMessage());
                } catch (StoppableException e) {
                    GUIMediator.model().sendErrorMessage(e.getMessage());
                }
                
            }
        };
        interruptable[0].begin();
        
        return interruptable[0];
    }

    /**
     * Process the user choice on the view: search for columns.
     * Interruptable action will be paused directly by the view, so we return it up to the view (@return),
     * also its state will be checked by the main SQL injection method, so we pass it as a parameter down to the model.
     * @param selectedTable table selected by user
     * @return Interruptable sent upward to the view, allows the start/pause/stop actions from the view
     */
    public Interruptable selectTable(final Table selectedTable) {
        // Indirect class, allows to send itself to his own body
        final Interruptable[] interruptable = new Interruptable[1];
        
        interruptable[0] = new Interruptable(GUIMediator.model()){
            @Override
            public void action() {
                
                try {
                    GUIMediator.model().dao.listColumns(selectedTable, interruptable[0]);
                } catch (PreparationException e) {
                    GUIMediator.model().sendErrorMessage(e.getMessage());
                } catch (StoppableException e) {
                    GUIMediator.model().sendErrorMessage(e.getMessage());
                }
                
            }
        };
        interruptable[0].begin();
        
        return interruptable[0];
    }
    
    /**
     * Process the user choice on the view: search for values.
     * Interruptable action will be paused directly by the view, so we return it up to the view (@return),
     * also its state will be checked by the main SQL injection method, so we pass it as a parameter down to the model.
     * @param values columns selected by user
     * @return Interruptable sent upward to the view, allows the start/pause/stop actions from the view
     */
    public Interruptable selectValues(final List<Column> values) {
        // Indirect class, allows to send itself to his own body
        final Interruptable[] interruptable = new Interruptable[1];
        
        interruptable[0] = new Interruptable(GUIMediator.model()){
            @Override
            public void action() {
                
                try {
                    GUIMediator.model().dao.listValues(values, interruptable[0]);
                } catch (PreparationException e) {
                    GUIMediator.model().sendErrorMessage(e.getMessage());
                } catch (StoppableException e) {
                    GUIMediator.model().sendErrorMessage(e.getMessage());
                }

            }
        };
        interruptable[0].begin();
        
        return interruptable[0];
    }
}
