package mvc.model;

import exception.PreparationException;
import exception.StoppableException;

public abstract class Stoppable implements Runnable {
//	synchronized void myresume() {
//		notify();
//	}
	InjectionModel model;
	Interruptable interruptable;
	
	public String errorResponse;
	public String threadResponse = "";
	
	public Stoppable(InjectionModel model){
		this.model = model;
	}	
	public Stoppable(InjectionModel model, Interruptable interruptable){
		this.model = model;
		this.interruptable = interruptable;
	}

	public boolean isPreparationStopped(){
		synchronized(this) {
			if(model.stopFlag){
				return true;
			}else{
				return false;
			}
//			while(suspendFlag) {
//				wait();
//			}
		}
	}
	
	String begin() throws PreparationException{
		Thread t = new Thread(this, "Stoppable - begin");
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(errorResponse != null){
			throw new PreparationException(errorResponse);
		}else{
			return threadResponse;
		}
	}
	
	@Override
	public void run() {
		
		try {
			threadResponse = action();
		} catch (PreparationException e) {
			errorResponse = e.getMessage();
		} catch (StoppableException e) {
			errorResponse = e.getMessage();
		}
		
	}
	
	abstract String action(Object... args) throws PreparationException, StoppableException;
}
