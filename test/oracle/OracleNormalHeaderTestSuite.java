package oracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import suite.AbstractTestSuite;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.bean.Column;
import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.strategy.NormalStrategy;
import com.jsql.view.println.SystemOutTerminal;

public class OracleNormalHeaderTestSuite extends ConcreteOracleTestSuite {
    // pour chaque vendor/méthode/strategy
    /**
     * liste db, table, colonne, value
     * valeur à rallonge
     * caractère spécial \
     * @throws PreparationException 
     */
    
    @BeforeClass
    public static void initialize() throws PreparationException {
        PropertyConfigurator.configure("test/log4j.properties");
        
        InjectionModel model = new InjectionModel();
        MediatorModel.register(model);
        model.instanciationDone();
        new SystemOutTerminal();
        
        MediatorModel.model().initialUrl = "http://127.0.0.1/oracle_simulate_get.php";
        MediatorModel.model().getData = "?lib=0";
        MediatorModel.model().method = "GET";

        MediatorModel.model().inputValidation();
        
        MediatorModel.model().injectionStrategy = new NormalStrategy();
    }

}
