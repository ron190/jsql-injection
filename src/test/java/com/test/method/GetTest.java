package com.test.method;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Ignore;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySQLTestSuite;

@Ignore
public abstract class GetTest extends ConcreteMySQLTestSuite {

    @Override
    @BeforeClass
    public void initialize3() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
        model.displayVersion();

        model.addObserver(new SystemOutTerminal());

//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_get.php");
//        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
        model.connectionUtil.setUrlBase("http://localhost:8080/greeting");
        model.parameterUtil.setQueryString(Arrays.asList(new SimpleEntry<>("tenantId", "tenantId3"), new SimpleEntry<>("name", "1'")));
        model.connectionUtil.setMethodInjection(model.QUERY);

        model.beginInjection();

        model.setStrategy(model.NORMAL);
    }
    
}
