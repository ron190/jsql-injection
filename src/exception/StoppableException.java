package exception;

/**
 * Exception class that manages the interruption of injection process, concerns
 * every actions taken by the user after the preparation
 */
public class StoppableException extends Exception {
	private static final long serialVersionUID = -3573501525824167565L;
	
	public StoppableException(){
		super("Execution stopped by user.");
	}
}
