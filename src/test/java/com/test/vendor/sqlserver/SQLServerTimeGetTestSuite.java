package com.test.vendor.sqlserver;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;

@Ignore
public class SQLServerTimeGetTestSuite extends ConcreteSQLServerTestSuite {

    @Override
    @BeforeClass
    public void setupInjection() throws InjectionFailureException {
//        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
//        model.displayVersion();
//
//        MediatorModel.model().addObserver(new SystemOutTerminal());
//
//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/sqlserver_simulate_get.php");
//        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("lib", "1")));
//        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
//
//        MediatorModel.model().beginInjection();
//
//        MediatorModel.model().setStrategy(StrategyInjection.TIME);
    }

    @Override
    @Test
    public void listDatabases() throws JSqlException {
        LOGGER.info("Ignore: too slow");
    }

    @Override
    @Test
    public void listTables() throws JSqlException {
        LOGGER.info("Ignore: too slow");
    }
    
}
