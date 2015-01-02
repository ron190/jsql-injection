package com.jsql.model.strategy;

import org.apache.log4j.Logger;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Request;
import com.jsql.model.injection.AbstractSuspendable;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.injection.StoppableGetInitialQuery;

/**
 * Injection strategy using normal attack.
 */
public class NormalStrategy extends AbstractInjectionStrategy {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(NormalStrategy.class);

    @Override
    public void checkApplicability() throws PreparationException {
        LOGGER.info("Normal test...");
        MediatorModel.model().initialQuery = new StoppableGetInitialQuery().beginSynchrone();

        this.isApplicable = !"".equals(MediatorModel.model().initialQuery);
        
        if (this.isApplicable) {
            allow();
        } else {
            unallow();
        }
    }

    @Override
    public void allow() {
        Request request = new Request();
        request.setMessage("MarkNormalVulnerable");
        MediatorModel.model().interact(request);
    }

    @Override
    public void unallow() {
        Request request = new Request();
        request.setMessage("MarkNormalInvulnerable");
        MediatorModel.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return MediatorModel.model().inject(
//                "select+" +
//                    "concat(" +
//                        "0x53514c69," +
//                        "mid(" +
//                            "(" + sqlQuery + ")," +
//                            startPosition + "," +
//                            "65536" +
//                        ")" +
//                    ")",
                MediatorModel.model().sqlStrategy.normalStrategy(sqlQuery, startPosition),
                null,
                true
        );
    }

    @Override
    public void applyStrategy() {
        LOGGER.info("Using normal injection...");
        MediatorModel.model().applyStrategy(this);
        
        Request request = new Request();
        request.setMessage("MarkNormalStrategy");
        MediatorModel.model().interact(request);
    }
    
    @Override
    public String getPerformanceLength() {
        return MediatorModel.model().performanceLength;
    }
}
