package mvc.model;

/**
 * Object needed by the view to update GUI, used by the Observer pattern,
 * message: string id that defines the action
 * arg: object required by the view to proceed the action
 */
public class ObserverEvent {
	private String message;
	private Object arg;
	
	public ObserverEvent(String newMessage, Object newObject){
		this.message = newMessage;
		this.arg = newObject;
	}
	
	public Object getArg(){
		return arg;
	}
	
	@Override
	public String toString(){
		return this.message;
	}
}
