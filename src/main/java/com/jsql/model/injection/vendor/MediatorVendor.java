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
    private Vendor vendor;

    /**
     * Database vendor selected by user (default UNDEFINED).
     * If not UNDEFINED then the next injection will be forced to use the selected vendor.
     */
    private Vendor vendorByUser;

    private Vendor auto;
    private Vendor access;
    private Vendor cockroachDB;
    private Vendor cubrid;
    private Vendor db2;
    private Vendor derby;
    private Vendor firebird;
    private Vendor h2;
    private Vendor hana;
    private Vendor hsqldb;
    private Vendor informix;
    private Vendor ingres;
    private Vendor maxDB;
    private Vendor mckoi;
    private Vendor memSQL;
    private Vendor mySQL;
    private Vendor neo4j;
    private Vendor nuoDB;
    private Vendor oracle;
    private Vendor postgreSQL;
    private Vendor sqlite;
    private Vendor sqlServer;
    private Vendor sybase;
    private Vendor teradata;
    private Vendor vertica;
    
    private List<Vendor> vendors;
    
    public MediatorVendor(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        this.auto = new Vendor("Database auto", null);
        this.access = new Vendor("Access", new VendorXml("access.xml", this.injectionModel));
        this.cockroachDB = new Vendor("CockroachDB", new VendorXml("cockroachdb.xml", this.injectionModel));
        this.cubrid = new Vendor("CUBRID", new VendorXml("cubrid.xml", this.injectionModel));
        this.db2 = new Vendor("DB2", new VendorXml("db2.xml", this.injectionModel));
        this.derby = new Vendor("Derby", new VendorXml("derby.xml", this.injectionModel));
        this.firebird = new Vendor("Firebird", new VendorXml("firebird.xml", this.injectionModel));
        this.h2 = new Vendor("H2", new VendorXml("h2.xml", this.injectionModel));
        this.hana = new Vendor("Hana", new VendorXml("hana.xml", this.injectionModel));
        this.hsqldb = new Vendor("HSQLDB", new VendorXml("hsqldb.xml", this.injectionModel));
        this.informix = new Vendor("Informix", new VendorXml("informix.xml", this.injectionModel));
        this.ingres = new Vendor("Ingres", new VendorXml("ingres.xml", this.injectionModel));
        this.maxDB = new Vendor("MaxDB", new VendorXml("maxdb.xml", this.injectionModel));
        this.mckoi = new Vendor("Mckoi", new VendorXml("mckoi.xml", this.injectionModel));
        this.memSQL = new Vendor("MemSQL", new VendorXml("memsql.xml", this.injectionModel));
        this.mySQL = new Vendor("MySQL", new VendorXml("mysql.xml", this.injectionModel));
        this.neo4j = new Vendor("Neo4j", new VendorXml("neo4j.xml", this.injectionModel));
        this.nuoDB = new Vendor("NuoDB", new VendorXml("nuodb.xml", this.injectionModel));
        this.oracle = new Vendor("Oracle", new VendorXml("oracle.xml", this.injectionModel));
        this.postgreSQL = new Vendor("PostgreSQL", new VendorXml("postgresql.xml", this.injectionModel));
        this.sqlite = new Vendor("SQLite", new VendorXml("sqlite.xml", this.injectionModel)) {
             
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
        this.sqlServer = new Vendor("SQL Server", new VendorXml("sqlserver.xml", this.injectionModel));
        this.sybase = new Vendor("Sybase", new VendorXml("sybase.xml", this.injectionModel));
        this.teradata = new Vendor("Teradata", new VendorXml("teradata.xml", this.injectionModel));
        this.vertica = new Vendor("Vertica", new VendorXml("vertica.xml", this.injectionModel));
        
        this.vendors = Arrays.asList(
            this.access,
            this.cockroachDB,
            this.cubrid,
            this.db2,
            this.derby,
            this.firebird,
            this.h2,
            this.hana,
            this.hsqldb,
            this.informix,
            this.ingres,
            this.maxDB,
            this.mckoi,
            this.memSQL,
            this.mySQL,
            this.neo4j,
            this.nuoDB,
            this.oracle,
            this.postgreSQL,
            this.sqlite,
            this.sqlServer,
            this.sybase,
            this.teradata,
            this.vertica
        );
        
        this.setVendor(this.getMySQL());
        this.vendorByUser = this.getAuto();
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
        return this.vendors;
    }

    public Vendor getH2() {
        return this.h2;
    }

    public Vendor getPostgreSQL() {
        return this.postgreSQL;
    }

    public Vendor getAuto() {
        return this.auto;
    }

    public Vendor getMySQL() {
        return this.mySQL;
    }

    public Vendor getSqlite() {
        return this.sqlite;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
