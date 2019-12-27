package groovy

import com.jsql.model.InjectionModel
import com.jsql.model.bean.database.Column
import com.jsql.model.bean.database.Database
import com.jsql.model.bean.database.Table
import com.jsql.model.exception.InjectionFailureException

import spock.lang.Specification

class BeanDatabaseSpock extends Specification {

    def 'Check bean database hierarchie and labels'() {
        expect:
            database.getParent() == null
            database.getLabelCount() == "database (5 tables)"
        and:
            table.getParent() == database
            table.getLabelCount() == "table (10 rows)"
        and:
            column.getParent() == table
            column.getLabelCount() == "column"
        
        where:
            database = new Database("database", "5"); 
            table = new Table("table", "10", database); 
            column = new Column("column", table); 
    }
    
}