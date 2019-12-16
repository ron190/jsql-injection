package com.jsql.model.injection.vendor;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.vendor.model.AbstractVendor;
import com.jsql.model.injection.vendor.model.VendorXml;

public class MediatorVendor {

    InjectionModel injectionModel;
    
    /**
     * Database vendor currently used.
     * It can be switched to another vendor by automatic detection or manual selection.
     */
    public Vendor vendor = this.MYSQL;

    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor.
     */
    private Vendor vendorByUser = this.AUTO;

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
        
        AUTO = new Vendor("Database auto", null);
        ACCESS = new Vendor("Access", new VendorXml("access.xml", this.injectionModel));
        COCKROACHDB = new Vendor("CockroachDB", new VendorXml("cockroachdb.xml", this.injectionModel));
        CUBRID = new Vendor("CUBRID", new VendorXml("cubrid.xml", this.injectionModel));
        DB2 = new Vendor("DB2", new VendorXml("db2.xml", this.injectionModel));
        DERBY = new Vendor("Derby", new VendorXml("derby.xml", this.injectionModel));
        FIREBIRD = new Vendor("Firebird", new VendorXml("firebird.xml", this.injectionModel));
        H2 = new Vendor("H2", new VendorXml("h2.xml", this.injectionModel));
        HANA = new Vendor("Hana", new VendorXml("hana.xml", this.injectionModel));
        HSQLDB = new Vendor("HSQLDB", new VendorXml("hsqldb.xml", this.injectionModel));
        INFORMIX = new Vendor("Informix", new VendorXml("informix.xml", this.injectionModel));
        INGRES = new Vendor("Ingres", new VendorXml("ingres.xml", this.injectionModel));
        MAXDB = new Vendor("MaxDB", new VendorXml("maxdb.xml", this.injectionModel));
        MCKOI = new Vendor("Mckoi", new VendorXml("mckoi.xml", this.injectionModel));
        MEMSQL = new Vendor("MemSQL", new VendorXml("memsql.xml", this.injectionModel));
        MYSQL = new Vendor("MySQL", new VendorXml("mysql.xml", this.injectionModel));
        NEO4J = new Vendor("Neo4j", new VendorXml("neo4j.xml", this.injectionModel));
        NUODB = new Vendor("NuoDB", new VendorXml("nuodb.xml", this.injectionModel));
        ORACLE = new Vendor("Oracle", new VendorXml("oracle.xml", this.injectionModel));
        POSTGRESQL = new Vendor("PostgreSQL", new VendorXml("postgresql.xml", this.injectionModel));
        SQLITE = new Vendor("SQLite", new VendorXml("sqlite.xml", this.injectionModel)) {
             
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
        SQLSERVER = new Vendor("SQL Server", new VendorXml("sqlserver.xml", this.injectionModel));
        SYBASE = new Vendor("Sybase", new VendorXml("sybase.xml", this.injectionModel));
        TERADATA = new Vendor("Teradata", new VendorXml("teradata.xml", this.injectionModel));
        VERTICA = new Vendor("Vertica", new VendorXml("vertica.xml", this.injectionModel));
        
        vendors = Arrays.asList(
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
        
        vendor = this.MYSQL;
        vendorByUser = this.AUTO;
    }
    
    public class Vendor {
        
        private final String labelVendor;
        
        private final AbstractVendor instanceVendor;
        
        private Vendor(String labelVendor, AbstractVendor instanceVendor) {
            this.labelVendor = labelVendor;
            this.instanceVendor = instanceVendor;
        }
        
        public AbstractVendor instance() {
            return this.instanceVendor;
        }
        
        @Override
        public String toString() {
            return this.labelVendor;
        }
        
        public String transform(String resultToParse) {
            return "";
        }
        
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
