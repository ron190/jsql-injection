package com.test.method;

import org.junit.BeforeClass;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.junit.SystemOutTerminal;
import com.test.mysql.ConcreteMysqlTestSuite;

public class CookieTest extends ConcreteMysqlTestSuite {
    // pour chaque vendor/méthode/strategy
    /**
     * liste db, table, colonne, value
     * valeur à rallonge
     * caractère spécial \
     * @throws PreparationException
     */

    @BeforeClass
    public static void initialize() throws PreparationException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();

        ConnectionUtil.initialUrl = "http://127.0.0.1/simulate_cookie.php";
        ConnectionUtil.headerData = "Cookie:lib=0";
        ConnectionUtil.method = "HEADER";

        MediatorModel.model().inputValidation();

        MediatorModel.model().setStrategy(Strategy.NORMAL);
    }
}
