package com.jsql.model.vendor;

public enum Vendor {
    
    Undefined,
    Cubrid,
    DB2,
    Derby,
    Firebird,
    H2,
    HSQLDB,
    Informix,
    Ingres,
    MariaDB,
    MaxDb,
    MySQL,
    Oracle,
    PostgreSQL,
    SQLServer,
    Sybase,
    Teradata;

    @Override
    public String toString() {
        switch (this) {
            case Undefined:
                return "<auto> ";
            default:
                return this.name() +" ";
        }
    }
}
