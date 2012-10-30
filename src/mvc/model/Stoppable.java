package mvc.model;

import exception.PreparationException;
import exception.StoppableException;

/**
 * Runnable class, can stop preparation, whereas Interruptable also provides pause/resume.
 * Execution should be stopped after a state check with isPreparationStopped(),
 * How to use: subclass Stoppable, define the abstract method action() with task that can be stopped, then start it with begin()
 */
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
	/**
	 * Starting from the view, the controller calls selectDatabase() that creates an Interruptable,
	 * which call Stoppable_loopIntoResults(), so Interruptable must go through Stoppable in order to
	 * let the view pause itself
	 * #Need rework
	 * @param model needed for accessing the stopFlag 
	 * @param interruptable pass it for the view pause/resume
	 */
	public Stoppable(InjectionModel model, Interruptable interruptable){
		this.model = model;
		this.interruptable = interruptable;
	}

	// Return the user stop choice from model
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
	
	/**
	 * Start the job defined by action(), and wait for it to finish
	 * @return source page
	 * @throws PreparationException if action throws exception
	 */
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
	
	/**
	 * Runnable default job, run action() defined by subclass,
	 * Take care of result and error coming from action()
	 */
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
