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

    private Vendor AUTO;
    private Vendor ACCESS;
    private Vendor COCKROACHDB;
    private Vendor CUBRID;
    private Vendor DB2;
    private Vendor DERBY;
    private Vendor FIREBIRD;
    private Vendor H2;
    private Vendor HANA;
    private Vendor HSQLDB;
    private Vendor INFORMIX;
    private Vendor INGRES;
    private Vendor MAXDB;
    private Vendor MCKOI;
    private Vendor MEMSQL;
    private Vendor MYSQL;
    private Vendor NEO4J;
    private Vendor NUODB;
    private Vendor ORACLE;
    private Vendor POSTGRESQL;
    private Vendor SQLITE;
    private Vendor SQLSERVER;
    private Vendor SYBASE;
    private Vendor TERADATA;
    private Vendor VERTICA;
    
    private List<Vendor> vendors = Arrays.asList(
        this.ACCESS,
        this.COCKROACHDB,
		this.CUBRID,
		this.DB2,
		this.DERBY,
		this.FIREBIRD,
		this.getH2(),
		this.HANA,
		this.HSQLDB,
		this.INFORMIX,
		this.INGRES,
		this.MAXDB,
		this.MCKOI,
		this.MEMSQL,
		this.getMYSQL(),
		this.NEO4J,
		this.NUODB,
		this.ORACLE,
		this.getPOSTGRESQL(),
		this.getSQLITE(),
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
            this.getH2(),
            this.HANA,
            this.HSQLDB,
            this.INFORMIX,
            this.INGRES,
            this.MAXDB,
            this.MCKOI,
            this.MEMSQL,
            this.getMYSQL(),
            this.NEO4J,
            this.NUODB,
            this.ORACLE,
            this.getPOSTGRESQL(),
            this.getSQLITE(),
            this.SQLSERVER,
            this.SYBASE,
            this.TERADATA,
            this.VERTICA
        );
        
        this.vendor = this.getMYSQL();
        this.vendorByUser = this.getAUTO();
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

    public List<Vendor> getVendors() {
        return vendors;
    }

    public Vendor getH2() {
        return H2;
    }

    public Vendor getPOSTGRESQL() {
        return POSTGRESQL;
    }

    public Vendor getAUTO() {
        return AUTO;
    }

    public Vendor getMYSQL() {
        return MYSQL;
    }

    public Vendor getSQLITE() {
        return SQLITE;
    }
}
