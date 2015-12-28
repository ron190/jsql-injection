package com.test.method;

import org.junit.BeforeClass;

import com.jsql.exception.PreparationException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
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

        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_cookie.php";
        MediatorModel.model().headerData = "Cookie:lib=0";
        MediatorModel.model().method = "HEADER";

        MediatorModel.model().inputValidation();

        MediatorModel.model().injectionStrategy = MediatorModel.model().normalStrategy;
    }
}
