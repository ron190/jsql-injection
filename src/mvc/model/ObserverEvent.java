package mvc.model;

/**
 * Define what the view wait for, to update GUI, it is used by the Observer pattern
 * message: string id that defines the action
 * arg: the object required by the view to proceed the action
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
