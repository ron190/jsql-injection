package exception;

public class PreparationException extends Exception {
	private static final long serialVersionUID = -5602296831875522603L;
	
	public PreparationException(){
		super("Execution stopped by user.");
	}
	
	public PreparationException(String message){
		super(message);
	}
}

