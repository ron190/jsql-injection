package com.test.special;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

import java.util.List;

public class AdminPageSuiteIT extends ConcreteMySqlSuiteIT {

    int pagesFound = 0;

    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        pagesFound = model.getResourceAccess().createAdminPages("http://localhost:8080", List.of("greeting"));

        model.subscribe(new SystemOutTerminal());
    }
    
    @Override
    @RetryingTest(3)
    public void listDatabases() throws JSqlException {
        Assertions.assertEquals(1, pagesFound);
    }
}
