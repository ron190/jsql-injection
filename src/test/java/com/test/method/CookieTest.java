package com.test.method;

import org.junit.BeforeClass;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.terminal.SystemOutTerminal;
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
        model.sendVersionToView();
        new SystemOutTerminal();

        ConnectionUtil.urlByUser = "http://127.0.0.1/simulate_cookie.php";
        ConnectionUtil.dataHeader = "Cookie:lib=0";
        ConnectionUtil.methodInjection = MethodInjection.HEADER;

        MediatorModel.model().injection();

        MediatorModel.model().setStrategy(Strategy.NORMAL);
    }
}
