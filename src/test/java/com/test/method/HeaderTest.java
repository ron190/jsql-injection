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
public abstract class HeaderTest extends ConcreteMySQLTestSuite {

    @Override
    @BeforeClass
    public void setupInjection() throws InjectionFailureException {
        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
        model.displayVersion();

        model.addObserver(new SystemOutTerminal());

        model.getMediatorUtils().getConnectionUtil().setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/simulate_header.php");
        model.getMediatorUtils().getParameterUtil().setHeader(Arrays.asList(new AbstractMap.SimpleEntry<>("lib", "0")));
        model.getMediatorUtils().getConnectionUtil().setMethodInjection(model.getMediatorMethodInjection().getHeader());

        model.beginInjection();

        model.getMediatorStrategy().setStrategy(model.getMediatorStrategy().getNormal());
    }
    
}
