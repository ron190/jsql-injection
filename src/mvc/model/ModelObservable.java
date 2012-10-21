package mvc.model;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;


public abstract class ModelObservable extends Observable {
	public boolean stopFlag = false;

	public void stop() {
		stopFlag = true;
	}
	
	public abstract String inject( String dataInjection );

	public abstract String inject( String dataInjection, String[] responseHeader, boolean useVisibleIndex );

	
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

	@Override
	public void addObserver(Observer o) {
		super.addObserver(o);
	}
	
	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
	}

}
