package com.test.vendor.db2;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.MethodInjection;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.util.ConnectionUtil;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.AbstractTestSuite;

public class DB2BlindGetTestSuite extends ConcreteDB2TestSuite {

    @BeforeClass
    public static void initialize() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.displayVersion();
        
        MediatorModel.model().addObserver(new SystemOutTerminal());

        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/db2_simulate_get.php");
        ConnectionUtil.setDataQuery("?lib=1");
        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);

        MediatorModel.model().beginInjection();

        MediatorModel.model().setStrategy(Strategy.BLIND);
    }
    
    @Override
    @Test
    @Ignore // Too Slow
    public void listDatabases() throws JSqlException {
        // Empty on purpose
    }
    
    @Override
    @Test
    @Ignore // Too Slow
    public void listTables() throws JSqlException {
        // Empty on purpose
    }
}
