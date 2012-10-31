package mvc.model;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

/**
 * Define the features of the injection model:
 * - stop the preparation of injection,
 * - Callable for parallelizing HTTP tasks,
 * - communication with view, via Observable
 */
public abstract class ModelObservable extends Observable {
	/**
	 * Simple boolean state, true if user wants to stop preparation.
	 * During the preparation, several methods will check if the execution must be stopped
	 */
	public boolean stopFlag = false;

	public void stop() {
		stopFlag = true;
	}
	
	/**
	 *  Function header for the inject() methods, definition needed by call(),
	 *  dataInjection: SQL query,
	 *  responseHeader unused,
	 *  useVisibleIndex false if injection indexes aren't needed,
	 *  return source page after the HTTP call 
	 */
	public abstract String inject( String dataInjection );
	public abstract String inject( String dataInjection, String[] responseHeader, boolean useVisibleIndex );
	
	/**
	 * Callable for parallelized HTTP tasks 
	 * url: SQL query
	 * content: source code of the web page
	 * tag: store user information (ex. current index)
	 */
	public class MyCallable implements Callable<MyCallable>{
		public String url, content, tag;
		MyCallable(String url){
			this.url = url;
		}
		
		MyCallable(String url, String tag){
			this(url);
			this.tag = tag;
		}
		
		@Override
		public MyCallable call() throws Exception {
//			Thread.sleep(1000);
			content = ModelObservable.this.inject(url);
			return this;
		}
	}
	
	/**
	 * Simple object to ease the communication with the view
	 * message: string id that defines the action
	 * arg: object sent to the view
	 */
	public class GUIThread{
		private String message;
		private Object arg;
		
		public GUIThread(String newMessage, Object newObject){
			super();
			this.message = newMessage;
			this.arg = newObject;
		}
		
		GUIThread(String newMessage){
			this(newMessage, null);
		}
		
		public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run () {
                	ModelObservable.this.setChanged();
                	ModelObservable.this.notifyObservers( new ObserverEvent(message,arg) );
                }
            });
        }
	}

	/**
	 * Observator pattern
	 */
	@Override
	public void addObserver(Observer o) {
		super.addObserver(o);
	}
	
	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
	}

}
