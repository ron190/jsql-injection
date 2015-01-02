package mysql;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import suite.AbstractTestSuite;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;
import com.jsql.view.println.SystemOutTerminal;


public class MysqlTimeHeaderTestSuite extends ConcreteMysqlTestSuite {
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
        
        MediatorModel.model().initialUrl = "http://127.0.0.1/simulate_header.php";
        MediatorModel.model().headerData = "lib:1";
        MediatorModel.model().method = "HEADER";

        MediatorModel.model().inputValidation();
        
        MediatorModel.model().injectionStrategy = MediatorModel.model().timeStrategy;
    }

    @Override
    @Test
    @Ignore
    public void listDatabases() throws PreparationException, StoppableException {
        
    }
    
    @Override
    @Test
    @Ignore
    public void listTables() throws PreparationException, StoppableException {

    }
}
