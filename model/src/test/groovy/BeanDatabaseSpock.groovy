import com.jsql.model.bean.database.Column
import com.jsql.model.bean.database.Database
import com.jsql.model.bean.database.Table
import spock.lang.Specification

class BeanDatabaseSpock extends Specification {

    def 'Check bean database hierarchie and labels'() {
        
        expect:
            database.getParent() == null
            database.getLabelWithCount() == "database (5 tables)"
        and:
            table.getParent() == database
            table.getLabelWithCount() == "table (10 rows)"
        and:
            column.getParent() == table
            column.getLabelWithCount() == "column"
        
        where:
            database = new Database("database", "5")
            table = new Table("table", "10", database)
            column = new Column("column", table)
    }

    def 'Check bean database hierarchie and labels without count'() {
        
        expect:
            database.getParent() == null
            database.getLabelWithCount() == "database (0 table)"
        and:
            table.getParent() == database
            table.getLabelWithCount() == "table (0 row)"
        
        where:
            database = new Database("database", "0")
            table = new Table("table", "0", database)
    }

    def 'Check bean database hierarchies and labels with incorrect count'() {
        
        expect:
            database.getParent() == null
            database.getLabelWithCount() == "database (0 table)"
        and:
            table.getParent() == database
            table.getLabelWithCount() == "table (0 row)"
        
        where:
            database = new Database("database", "X")
            table = new Table("table", "X", database)
    }

    def 'Check bean database hierarchies and labels with information_schema'() {
        
        expect:
            database.getParent() == null
            database.getLabelWithCount() == "information_schema (5 tables)"
        and:
            table.getParent() == database
            table.getLabelWithCount() == "table (? rows)"
        
        where:
            database = new Database("information_schema", "5")
            table = new Table("table", "5", database)
    }
}