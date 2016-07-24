package com.jsql.model.injection.strategy;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.util.Request;
import com.jsql.model.bean.util.TypeRequest;
import com.jsql.model.exception.StoppedByUserException;
import com.jsql.model.suspendable.AbstractSuspendable;

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
            MediatorModel.model().getCharInsertion() + 
            MediatorModel.model().vendor.instance().sqlTestErrorBased()
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
        markVulnerable(TypeRequest.MARK_ERRORBASED_VULNERABLE);
    }

    @Override
    public void unallow() {
        markVulnerable(TypeRequest.MARK_ERRORBASED_INVULNERABLE);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable<String> stoppable) throws StoppedByUserException {
        return MediatorModel.model().injectWithoutIndex(
            MediatorModel.model().getCharInsertion() +
            MediatorModel.model().vendor.instance().sqlErrorBased(sqlQuery, startPosition)
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using strategy ["+ this.getName() +"]");
        MediatorModel.model().setStrategy(Strategy.ERRORBASED);
        
        Request request = new Request();
        request.setMessage(TypeRequest.MARK_ERRORBASED_STRATEGY);
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
