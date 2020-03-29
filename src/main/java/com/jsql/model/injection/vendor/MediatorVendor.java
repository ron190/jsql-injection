package com.jsql.model.injection.vendor;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.injection.vendor.model.VendorYaml;

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
        this.access = new Vendor("Access", new VendorYaml("access.yml", this.injectionModel));
        this.cockroachDB = new Vendor("CockroachDB", new VendorYaml("cockroachdb.yml", this.injectionModel));
        this.cubrid = new Vendor("CUBRID", new VendorYaml("cubrid.yml", this.injectionModel));
        this.db2 = new Vendor("DB2", new VendorYaml("db2.yml", this.injectionModel));
        this.derby = new Vendor("Derby", new VendorYaml("derby.yml", this.injectionModel));
        this.firebird = new Vendor("Firebird", new VendorYaml("firebird.yml", this.injectionModel));
        this.h2 = new Vendor("H2", new VendorYaml("h2.yml", this.injectionModel));
        this.hana = new Vendor("Hana", new VendorYaml("hana.yml", this.injectionModel));
        this.hsqldb = new Vendor("HSQLDB", new VendorYaml("hsqldb.yml", this.injectionModel));
        this.informix = new Vendor("Informix", new VendorYaml("informix.yml", this.injectionModel));
        this.ingres = new Vendor("Ingres", new VendorYaml("ingres.yml", this.injectionModel));
        this.maxDB = new Vendor("MaxDB", new VendorYaml("maxdb.yml", this.injectionModel));
        this.mckoi = new Vendor("Mckoi", new VendorYaml("mckoi.yml", this.injectionModel));
        this.memSQL = new Vendor("MemSQL", new VendorYaml("memsql.yml", this.injectionModel));
        this.mySQL = new Vendor("MySQL", new VendorYaml("mysql.yml", this.injectionModel));
        this.neo4j = new Vendor("Neo4j", new VendorYaml("neo4j.yml", this.injectionModel));
        this.nuoDB = new Vendor("NuoDB", new VendorYaml("nuodb.yml", this.injectionModel));
        this.oracle = new Vendor("Oracle", new VendorYaml("oracle.yml", this.injectionModel));
        this.postgreSQL = new Vendor("PostgreSQL", new VendorYaml("postgresql.yml", this.injectionModel));
        this.sqlite = new Vendor("SQLite", new VendorYaml("sqlite.yml", this.injectionModel)) {
             
            @Override
            public String transformSQLite(String resultToParse) {
                
                StringBuilder resultSQLite = new StringBuilder();
                String resultTmp = resultToParse.replaceFirst(".+?\\(", "").trim().replaceAll("\\)$", "");
                resultTmp = resultTmp.replaceAll("\\(.+?\\)", "");
                
                for (String columnNameAndType: resultTmp.split(",")) {
                    // Some recent SQLite use tabulation character as a separator => split() by any white space \s
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
        this.sqlServer = new Vendor("SQL Server", new VendorYaml("sqlserver.yml", this.injectionModel));
        this.sybase = new Vendor("Sybase", new VendorYaml("sybase.yml", this.injectionModel));
        this.teradata = new Vendor("Teradata", new VendorYaml("teradata.yml", this.injectionModel));
        this.vertica = new Vendor("Vertica", new VendorYaml("vertica.yml", this.injectionModel));
        
        this.vendors = Arrays.asList(
            this.auto,
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

    public Vendor getSqlServer() {
        return this.sqlServer;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
