package com.jsql.model.injection.vendor;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.VendorXml;

public class MediatorVendor {

    InjectionModel injectionModel;
    
    /**
     * Database vendor currently used.
     * It can be switched to another vendor by automatic detection or manual selection.
     */
    public Vendor vendor;

    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor.
     */
    private Vendor vendorByUser;

    public Vendor AUTO;
    public Vendor ACCESS;
    public Vendor COCKROACHDB;
    public Vendor CUBRID;
    public Vendor DB2;
    public Vendor DERBY;
    public Vendor FIREBIRD;
    public Vendor H2;
    public Vendor HANA;
    public Vendor HSQLDB;
    public Vendor INFORMIX;
    public Vendor INGRES;
    public Vendor MAXDB;
    public Vendor MCKOI;
    public Vendor MEMSQL;
    public Vendor MYSQL;
    public Vendor NEO4J;
    public Vendor NUODB;
    public Vendor ORACLE;
    public Vendor POSTGRESQL;
    public Vendor SQLITE;
    public Vendor SQLSERVER;
    public Vendor SYBASE;
    public Vendor TERADATA;
    public Vendor VERTICA;
    
    public List<Vendor> vendors = Arrays.asList(
        this.ACCESS,
        this.COCKROACHDB,
		this.CUBRID,
		this.DB2,
		this.DERBY,
		this.FIREBIRD,
		this.H2,
		this.HANA,
		this.HSQLDB,
		this.INFORMIX,
		this.INGRES,
		this.MAXDB,
		this.MCKOI,
		this.MEMSQL,
		this.MYSQL,
		this.NEO4J,
		this.NUODB,
		this.ORACLE,
		this.POSTGRESQL,
		this.SQLITE,
		this.SQLSERVER,
		this.SYBASE,
		this.TERADATA,
		this.VERTICA
	);
    
    public MediatorVendor(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        this.AUTO = new Vendor("Database auto", null);
        this.ACCESS = new Vendor("Access", new VendorXml("access.xml", this.injectionModel));
        this.COCKROACHDB = new Vendor("CockroachDB", new VendorXml("cockroachdb.xml", this.injectionModel));
        this.CUBRID = new Vendor("CUBRID", new VendorXml("cubrid.xml", this.injectionModel));
        this.DB2 = new Vendor("DB2", new VendorXml("db2.xml", this.injectionModel));
        this.DERBY = new Vendor("Derby", new VendorXml("derby.xml", this.injectionModel));
        this.FIREBIRD = new Vendor("Firebird", new VendorXml("firebird.xml", this.injectionModel));
        this.H2 = new Vendor("H2", new VendorXml("h2.xml", this.injectionModel));
        this.HANA = new Vendor("Hana", new VendorXml("hana.xml", this.injectionModel));
        this.HSQLDB = new Vendor("HSQLDB", new VendorXml("hsqldb.xml", this.injectionModel));
        this.INFORMIX = new Vendor("Informix", new VendorXml("informix.xml", this.injectionModel));
        this.INGRES = new Vendor("Ingres", new VendorXml("ingres.xml", this.injectionModel));
        this.MAXDB = new Vendor("MaxDB", new VendorXml("maxdb.xml", this.injectionModel));
        this.MCKOI = new Vendor("Mckoi", new VendorXml("mckoi.xml", this.injectionModel));
        this.MEMSQL = new Vendor("MemSQL", new VendorXml("memsql.xml", this.injectionModel));
        this.MYSQL = new Vendor("MySQL", new VendorXml("mysql.xml", this.injectionModel));
        this.NEO4J = new Vendor("Neo4j", new VendorXml("neo4j.xml", this.injectionModel));
        this.NUODB = new Vendor("NuoDB", new VendorXml("nuodb.xml", this.injectionModel));
        this.ORACLE = new Vendor("Oracle", new VendorXml("oracle.xml", this.injectionModel));
        this.POSTGRESQL = new Vendor("PostgreSQL", new VendorXml("postgresql.xml", this.injectionModel));
        this.SQLITE = new Vendor("SQLite", new VendorXml("sqlite.xml", this.injectionModel)) {
             
             @Override
             public String transform(String resultToParse) {
                 
                 StringBuilder resultSQLite = new StringBuilder();
                 String resultTmp = resultToParse.replaceFirst(".+?\\(", "").trim().replaceAll("\\)$", "");
                 resultTmp = resultTmp.replaceAll("\\(.+?\\)", "");
                 
                 for (String columnNameAndType: resultTmp.split(",")) {
                     // Some recent SQLite use tabulation character as a separator => split() by any  white space \s
                     String columnName = columnNameAndType.trim().split("\\s")[0];
                     
                     // Some recent SQLite enclose names with ` => strip those `
                     columnName = StringUtils.strip(columnName, "`");
                     
                     if (!"CONSTRAINT".equals(columnName) && !"UNIQUE".equals(columnName)) {
                         resultSQLite.append((char) 4 + columnName + (char) 5 + "0" + (char) 4 + (char) 6);
                     }
                 }
         
                 return resultSQLite.toString();
                 
             }
             
         };
        this.SQLSERVER = new Vendor("SQL Server", new VendorXml("sqlserver.xml", this.injectionModel));
        this.SYBASE = new Vendor("Sybase", new VendorXml("sybase.xml", this.injectionModel));
        this.TERADATA = new Vendor("Teradata", new VendorXml("teradata.xml", this.injectionModel));
        this.VERTICA = new Vendor("Vertica", new VendorXml("vertica.xml", this.injectionModel));
        
        this.vendors = Arrays.asList(
            this.ACCESS,
            this.COCKROACHDB,
            this.CUBRID,
            this.DB2,
            this.DERBY,
            this.FIREBIRD,
            this.H2,
            this.HANA,
            this.HSQLDB,
            this.INFORMIX,
            this.INGRES,
            this.MAXDB,
            this.MCKOI,
            this.MEMSQL,
            this.MYSQL,
            this.NEO4J,
            this.NUODB,
            this.ORACLE,
            this.POSTGRESQL,
            this.SQLITE,
            this.SQLSERVER,
            this.SYBASE,
            this.TERADATA,
            this.VERTICA
        );
        
        this.vendor = this.MYSQL;
        this.vendorByUser = this.AUTO;
    }
    
    public Vendor getVendor() {
        return this.vendor;
    }

    public Vendor getVendorByUser() {
        return this.vendorByUser;
    }

    public void setVendorByUser(Vendor vendorByUser) {
        this.vendorByUser = vendorByUser;
    }
}
