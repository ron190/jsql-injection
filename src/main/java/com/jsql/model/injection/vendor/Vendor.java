package com.jsql.model.injection.vendor;

public enum Vendor {
	
    AUTO("<Database auto>", null),
    ACCESS("Access", new VendorXml("access.xml")),
    CUBRID("CUBRID", new VendorXml("cubrid.xml")),
    DB2("DB2", new VendorXml("db2.xml")),
    DERBY("Derby", new VendorXml("derby.xml")),
    FIREBIRD("Firebird", new VendorXml("firebird.xml")),
    H2("H2", new VendorXml("h2.xml")),
    HSQLDB("HSQLDB", new VendorXml("hsqldb.xml")),
    INFORMIX("Informix", new VendorXml("informix.xml")),
    INGRES("Ingres", new VendorXml("ingres.xml")),
    MARIADB("MariaDB", new VendorXml("mysql.xml")),
    MAXDB("MaxDB", new VendorXml("maxdb.xml")),
    MYSQL("MySQL", new VendorXml("mysql.xml")),
    ORACLE("Oracle", new VendorXml("oracle.xml")),
    POSTGRESQL("PostgreSQL", new VendorXml("postgresql.xml")),
    SQLITE("SQLite", new VendorXml("sqlite.xml")),
    SQLSERVER("SQL Server", new VendorXml("sqlserver.xml")),
    SYBASE("Sybase", new VendorXml("sybase.xml")),
    TERADATA("Teradata", new VendorXml("teradata.xml"));

    private final String labelVendor;
    
    private final AbstractVendorDefault instanceVendor;
    
    private Vendor(String labelVendor, AbstractVendorDefault instanceVendor) {
        this.labelVendor = labelVendor;
        this.instanceVendor = instanceVendor;
    }
    
    public AbstractVendorDefault instance() {
        return this.instanceVendor;
    }
    
    @Override
    public String toString() {
        return this.labelVendor;
    }
    
}
