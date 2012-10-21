package exception;

public class StoppableException extends Exception {
	private static final long serialVersionUID = -3573501525824167565L;
	
	public StoppableException(){
		super("Execution stopped by user.");
	}
}
