package mvc.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.PreparationException;
import exception.StoppableException;

import mvc.model.InjectionModel;
import mvc.model.Interruptable;
import mvc.model.database.Column;
import mvc.model.database.Database;
import mvc.model.database.Table;
import mvc.view.GUI;


/**
 * Controller in the MVC pattern, is involved mainly when the user makes actions
 * on the GUI: uses Connect button, validates a database, table or values
 */
public class InjectionController {

	public InjectionModel injectionModel;
	private GUI gui;
	
	public InjectionController(InjectionModel newModel){
		injectionModel = newModel;
		
		gui = new GUI(this, newModel);
		
//		Console console = new Console();
//		model.addObserver(console);
	}
	
	/**
	 * Send each parameters from the GUI to the model in order to start the preparation,
	 * the injection process is started in a new thread via function inputValidation()
	 */
	public void controlInput(String getData, String postData, String cookieData, String headerData, String method, 
			boolean isProxyfied, String proxyAdress, String proxyPort) {
		try {
			injectionModel.isProxyfied = isProxyfied;
			injectionModel.proxyAdress = proxyAdress;
			injectionModel.proxyPort = proxyPort;
			
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
			
			injectionModel.postData = postData;
			injectionModel.cookieData = cookieData;
			injectionModel.headerData = headerData;
			injectionModel.method = method;
			
			injectionModel.securitySteps = 0;
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					injectionModel.inputValidation();
				}
			}, "InjectionController - controlInput").start();
			
			gui.resetInterface();
		} catch (MalformedURLException e) {
			injectionModel.sendMessage(e.getMessage());
		}
	}

	/**
	 * Process the user choice on the database list: start the search for tables
	 * @param databaseSelected corresponding DB model object selected by user
	 * @return Interruptable sent upward to the view, allows the start/pause/stop actions from the view
	 */
	public Interruptable selectDatabase(final Database databaseSelected){
		// Dirty object definition that allows to send the object itself to another function
		// in his own body
		final Interruptable[] interruptable = new Interruptable[1];
		
		interruptable[0] = new Interruptable(){
			@Override
			public void action(Object... args) {
				
				try {
					injectionModel.listTables(databaseSelected, interruptable[0]);
				} catch (NumberFormatException e) {
					injectionModel.sendErrorMessage(e.getMessage());
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
	 * Process the user choice on the table list: start the search for columns
	 * @param selectedTable corresponding DB model object selected by user
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
	 * Process the user choice on the column list: start the search for values
	 * @param databaseSelected corresponding DB model object selected by user
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
				} catch (PreparationException e) { // pas de notification si stoppé
					injectionModel.sendErrorMessage(e.getMessage());
				} catch (StoppableException e) { // pas de notification si stoppé
					injectionModel.sendErrorMessage(e.getMessage());
				}

			}
		};
		interruptable[0].begin();
		
		return interruptable[0];
	}
}
