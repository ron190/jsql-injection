package mvc.model;

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
