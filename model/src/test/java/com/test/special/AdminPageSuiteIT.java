package com.test.special;

import com.jsql.model.InjectionModel;
import com.jsql.view.terminal.SystemOutTerminal;
import com.test.vendor.mysql.ConcreteMySqlSuiteIT;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import java.time.Duration;
import java.util.List;

public class AdminPageSuiteIT extends ConcreteMySqlSuiteIT {

    int pagesFound = 0;

    @Override
    public void setupInjection() throws Exception {

        Awaitility.await().atMost(Duration.ofMinutes(2)).until(isSetupDone::get);
        InjectionModel model = new InjectionModel();
        pagesFound = model.getResourceAccess().createAdminPages("http://localhost:8080", List.of("greeting"));

        model.subscribe(new SystemOutTerminal());
    }
    
    @RepeatedTest(3)
    public void listAdminPages() {
        Assertions.assertEquals(1, pagesFound);
    }
}
