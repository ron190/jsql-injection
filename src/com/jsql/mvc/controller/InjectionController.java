package com.jsql.mvc.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.mvc.model.InjectionModel;
import com.jsql.mvc.model.Interruptable;
import com.jsql.mvc.model.database.Column;
import com.jsql.mvc.model.database.Database;
import com.jsql.mvc.model.database.Table;
import com.jsql.mvc.view.GUI;


/**
 * Controller in the MVC pattern, is involved mainly when the user makes actions
 * on the GUI: uses Connect button, validates a database, table or values
 */
public class InjectionController {

    // The model
    public InjectionModel injectionModel;
    // The view
    private GUI gui;
    
    public InjectionController(InjectionModel newModel){
        injectionModel = newModel;
        gui = new GUI(this, newModel);
//        Console console = new Console();
//        model.addObserver(console);
    }
    
    /**
     * Send each parameters from the GUI to the model in order to start the preparation of injection,
     * the injection process is started in a new thread via model function inputValidation()
     */
    public void controlInput(String getData, String postData, String cookieData, String headerData, String method) {
        try {
            // Parse url and GET query string
            injectionModel.getData = "";
            Matcher regexSearch = Pattern.compile("(.*)(\\?.*)").matcher(getData);
            if(regexSearch.find()){
                URL url = new URL( getData );
                injectionModel.initialUrl = regexSearch.group(1);
                if( !url.getQuery().equals("") )
                    injectionModel.getData = regexSearch.group(2);
            }else{
                injectionModel.initialUrl = getData;
            }
            
            // Define other methods
            injectionModel.postData = postData;
            injectionModel.cookieData = cookieData;
            injectionModel.headerData = headerData;
            injectionModel.method = method;
            
            // Reset level of evasion
            injectionModel.securitySteps = 0;
            
            // Start the model injection process in a thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    injectionModel.inputValidation();
                }
            }, "InjectionController - controlInput").start();
            
            // Erase everything in the view from a previous injection
            gui.resetInterface();
        } catch (MalformedURLException e) {
            injectionModel.sendMessage(e.getMessage());
        }
    }

    /**
     * Process the user choice on the view: search for tables
     * @param databaseSelected database selected by user
     * @return Interruptable sent upward to the view, allows the start/pause/stop actions from the view
     */
    public Interruptable selectDatabase(final Database databaseSelected){
        // Dirty object definition that allows to send the object itself to another function
        // in his own body #Need more clean solution
        final Interruptable[] interruptable = new Interruptable[1];
        
        interruptable[0] = new Interruptable(){
            @Override
            public void action(Object... args) {
                
                try {
                    injectionModel.listTables(databaseSelected, interruptable[0]);
                } catch (NumberFormatException e) {
                    injectionModel.sendErrorMessage("Error during table search: incorrect number " + e.getMessage());
                } catch (PreparationException e) {
                    injectionModel.sendErrorMessage(e.getMessage());
                } catch (StoppableException e) {
                    injectionModel.sendErrorMessage(e.getMessage());
                }
                
            }
        };
        interruptable[0].begin();
        
        return interruptable[0];
    }

    /**
     * Process the user choice on the view: search for columns
     * @param selectedTable table selected by user
     * @return Interruptable sent upward to the view, allows the start/pause/stop actions from the view
     */
    public Interruptable selectTable(final Table selectedTable) {
        // Dirty object definition that allows to send the object itself to another function
        // in his own body
        final Interruptable[] interruptable = new Interruptable[1];
        
        interruptable[0] = new Interruptable(){
            @Override
            public void action(Object... args) {
                
                try {
                    injectionModel.listColumns(selectedTable, interruptable[0]);
                } catch (PreparationException e) {
                    injectionModel.sendErrorMessage(e.getMessage());
                } catch (StoppableException e) {
                    injectionModel.sendErrorMessage(e.getMessage());
                }
                
            }
        };
        interruptable[0].begin();
        
        return interruptable[0];
    }
    
    /**
     * Process the user choice on the view: search for values
     * @param values columns selected by user
     * @return Interruptable sent upward to the view, allows the start/pause/stop actions from the view
     */
    public Interruptable selectValues(final List<Column> values) {
        // Dirty object definition that allows to send the object itself to another function
        // in his own body
        final Interruptable[] interruptable = new Interruptable[1];
        
        interruptable[0] = new Interruptable(){
            @Override
            public void action(Object... args) {
                
                try {
                    injectionModel.listValues(values, interruptable[0]);
                } catch (PreparationException e) {
                    injectionModel.sendErrorMessage(e.getMessage());
                } catch (StoppableException e) {
                    injectionModel.sendErrorMessage(e.getMessage());
                }

            }
        };
        interruptable[0].begin();
        
        return interruptable[0];
    }
}
