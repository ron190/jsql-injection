package exception;

/**
 * Exception class that manages the interruption of injection build process (aka preparation),
 * concerns every steps before the user can do injection choices (database, table, etc)
 */
public class PreparationException extends Exception {
	private static final long serialVersionUID = -5602296831875522603L;
	
	public PreparationException(){
		super("Execution stopped by user.");
	}
	
	public PreparationException(String message){
		super(message);
	}
}

