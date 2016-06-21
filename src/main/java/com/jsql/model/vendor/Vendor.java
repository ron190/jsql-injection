package com.jsql.model.vendor;

public enum Vendor {
    UNDEFINED("<auto>", null),
    CUBRID("Cubrid", new CubridVendor()),
    DB2("DB2", new DB2Vendor()),
    DERBY("Derby", new DerbyVendor()),
    FIREBIRD("Firebird", new FirebirdVendor()),
    H2("H2", new H2Vendor()),
    HSQLDB("HSQLDB", new HSQLDBVendor()),
    INFORMIX("Informix", new InformixVendor()),
    INGRES("Ingres", new IngresVendor()),
    MARIADB("MariaDB", new MariaDBVendor()),
    MAXDB("MaxDb", new MaxDbVendor()),
    MYSQL("MySQL", new MySQLVendor()),
    ORACLE("Oracle", new OracleVendor()),
    POSTGRESQL("PostgreSQL", new PostgreSQLVendor()),
    SQLSERVER("SQLServer", new SQLServerVendor()),
    SYBASE("Sybase", new SybaseVendor()),
    TERADATA("Teradata", new TeradataVendor());

    private final AbstractVendor vendor;
    private final String label;
    
    Vendor(String label, AbstractVendor vendor) {
        this.label = label;
        this.vendor = vendor;
    }
    
    public AbstractVendor getValue() {
        return vendor;
    }
    
    @Override
    public String toString() {
        return this.label +" ";
    }
}
