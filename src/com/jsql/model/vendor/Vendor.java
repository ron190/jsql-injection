package com.jsql.model.vendor;

public enum Vendor {
    
    UNDEFINED("<auto>", null),
    CUBRID("Cubrid", new CubridStrategy()),
    DB2("DB2", new DB2Strategy()),
    DERBY("Derby", new DerbyStrategy()),
    FIREBIRD("Firebird", new FirebirdStrategy()),
    H2("H2", new H2Strategy()),
    HSQLDB("HSQLDB", new HSQLDBStrategy()),
    INFORMIX("Informix", new InformixStrategy()),
    INGRES("Ingres", new IngresStrategy()),
    MARIADB("MariaDB", new MariaDBStrategy()),
    MAXDB("MaxDb", new MaxDbStrategy()),
    MYSQL("MySQL", new MySQLStrategy()),
    ORACLE("Oracle", new OracleStrategy()),
    POSTGRESQL("PostgreSQL", new PostgreSQLStrategy()),
    SQLSERVER("SQLServer", new SQLServerStrategy()),
    SYBASE("Sybase", new SybaseStrategy()),
    TERADATA("Teradata", new TeradataStrategy());

    private final AbstractVendorStrategy strat;
    private final String label;
    
    Vendor(String label, AbstractVendorStrategy strat) {
        this.label = label;
        this.strat = strat;
    }
    
    public AbstractVendorStrategy getStrategy() {
        return strat;
    }
    
    @Override
    public String toString() {
        return this.label +" ";
    }
}
