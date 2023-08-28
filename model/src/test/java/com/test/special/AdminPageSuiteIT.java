package com.test.special;

import com.jsql.model.InjectionModel;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;

class AdminPageSuiteIT extends ConcreteMySqlSuiteIT {

    @Override
    public void setupInjection() throws Exception {

        this.injectionModel = new InjectionModel();

        this.injectionModel.subscribe(new SystemOutTerminal());
    }
    
    @RepeatedTest(3)
    void listAdminPages() throws InterruptedException {
        int pagesFound = this.injectionModel.getResourceAccess().createAdminPages("http://localhost:8080", List.of("greeting"));
        Assertions.assertEquals(1, pagesFound);
    }
}
