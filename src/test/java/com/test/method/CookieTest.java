package com.test.method;

import java.util.AbstractMap;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Ignore;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.AbstractTestSuite;
import com.test.vendor.mysql.ConcreteMySQLTestSuite;

@Ignore
public abstract class CookieTest extends ConcreteMySQLTestSuite {
	
    // pour chaque vendor/méthode/strategy
    /**
     * liste db, table, colonne, value
     * valeur à rallonge
     * caractère spécial \
     * @throws InjectionFailureException
     */

    @BeforeClass
    public void initialize2() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
        model.displayVersion();

        model.addObserver(new SystemOutTerminal());

        model.connectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_cookie.php");
        model.parameterUtil.setHeader(Arrays.asList(new AbstractMap.SimpleEntry<>("Cookie", "lib=0")));
        model.connectionUtil.setMethodInjection(model.HEADER);

        model.beginInjection();

        model.setStrategy(model.NORMAL);
    }
    
}
