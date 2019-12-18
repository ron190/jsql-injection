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

    @Override
    @BeforeClass
    public void initializeBackend() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
        model.displayVersion();

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getConnectionUtil().setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_cookie.php");
        model.getMediatorUtils().getParameterUtil().setHeader(Arrays.asList(new AbstractMap.SimpleEntry<>("Cookie", "lib=0")));
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethodInjection().getHeader());

        model.beginInjection();

        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getNORMAL());
    }
    
}
