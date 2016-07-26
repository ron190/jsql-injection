package com.jsql.model.injection.vendor;

public enum Vendor {
    AUTO("<Database auto>", null),
    CUBRID("CUBRID", new CubridVendor()),
    DB2("DB2", new DB2Vendor()),
    DERBY("Derby", new DerbyVendor()),
    FIREBIRD("Firebird", new FirebirdVendor()),
    H2("H2", new H2Vendor()),
    HSQLDB("HSQLDB", new HSQLDBVendor()),
    INFORMIX("Informix", new InformixVendor()),
    INGRES("Ingres", new IngresVendor()),
    MARIADB("MariaDB", new MariaDBVendor()),
    MAXDB("MaxDB", new MaxDbVendor()),
    MYSQL("MySQL", new MySQLVendor()),
    ORACLE("Oracle", new OracleVendor()),
    POSTGRESQL("PostgreSQL", new PostgreSQLVendor()),
    SQLSERVER("SQL Server", new SQLServerVendor()),
    SYBASE("Sybase", new SybaseVendor()),
    TERADATA("Teradata", new TeradataVendor());

    private final AbstractVendorDefault instanceVendor;
    private final String labelVendor;
    
    Vendor(String labelVendor, AbstractVendorDefault instanceVendor) {
        this.labelVendor = labelVendor;
        this.instanceVendor = instanceVendor;
    }
    
    public AbstractVendorDefault instance() {
        return instanceVendor;
    }
    
    @Override
    public String toString() {
        return this.labelVendor;
    }
}
