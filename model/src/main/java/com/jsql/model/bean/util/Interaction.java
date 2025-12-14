package com.jsql.model.bean.util;

public enum Interaction {
    
    ADD_COLUMNS("AddColumns"),
    ADD_DATABASES("AddDatabases"),
    ADD_TABLES("AddTables"),
    
    CREATE_ADMIN_PAGE_TAB("CreateAdminPageTab"),
    CREATE_FILE_TAB("CreateFileTab"),
    ADD_TAB_EXPLOIT_WEB("AddTabExploitWeb"),
    ADD_TAB_EXPLOIT_SQL("AddTabExploitSql"),
    ADD_TAB_EXPLOIT_UDF_MYSQL("AddTabExploitUdfMysql"),
    ADD_TAB_EXPLOIT_UDF_ORACLE("AddTabExploitUdfOracle"),
    ADD_TAB_EXPLOIT_UDF_EXTENSION_POSTGRES("AddTabExploitUdfExtensionPostgres"),
    ADD_TAB_EXPLOIT_UDF_WAL_POSTGRES("AddTabExploitUdfWalPostgres"),
    ADD_TAB_EXPLOIT_UDF_LIBRARY_POSTGRES("AddTabExploitUdfLibraryPostgres"),
    ADD_TAB_EXPLOIT_UDF_PROGRAM_POSTGRES("AddTabExploitUdfProgramPostgres"),
    ADD_TAB_EXPLOIT_UDF_SQLITE("AddTabExploitUdfSqlite"),
    ADD_TAB_EXPLOIT_UDF_H2("AddTabExploitUdfH2"),
    CREATE_VALUES_TAB("CreateValuesTab"),
    CREATE_ANALYSIS_REPORT("CreateAnalysisReport"),

    START_PROGRESS("StartProgress"),
    END_PROGRESS("EndProgress"),
    START_INDETERMINATE_PROGRESS("StartIndeterminateProgress"),
    END_INDETERMINATE_PROGRESS("EndIndeterminateProgress"),
    UPDATE_PROGRESS("UpdateProgress"),
    END_PREPARATION("EndPreparation"),
    
    MARK_FILE_SYSTEM_INVULNERABLE("MarkFileSystemInvulnerable"),
    MARK_FILE_SYSTEM_VULNERABLE("MarkFileSystemVulnerable"),
    GET_TERMINAL_RESULT("GetTerminalResult"),

    MARK_MULTIBIT_INVULNERABLE("MarkMultibitInvulnerable"),
    MARK_MULTIBIT_STRATEGY("MarkMultibitStrategy"),
    MARK_MULTIBIT_VULNERABLE("MarkMultibitVulnerable"),
    MARK_BLIND_BIT_INVULNERABLE("MarkBlindBitInvulnerable"),
    MARK_BLIND_BIT_STRATEGY("MarkBlindBitStrategy"),
    MARK_BLIND_BIT_VULNERABLE("MarkBlindBitVulnerable"),
    MARK_BLIND_BIN_INVULNERABLE("MarkBlindBinInvulnerable"),
    MARK_BLIND_BIN_STRATEGY("MarkBlindBinStrategy"),
    MARK_BLIND_BIN_VULNERABLE("MarkBlindBinVulnerable"),
    MARK_ERROR_INVULNERABLE("MarkErrorInvulnerable"),
    MARK_ERROR_STRATEGY("MarkErrorStrategy"),
    MARK_ERROR_VULNERABLE("MarkErrorVulnerable"),
    MARK_UNION_INVULNERABLE("MarkUnionInvulnerable"),
    MARK_UNION_STRATEGY("MarkUnionStrategy"),
    MARK_UNION_VULNERABLE("MarkUnionVulnerable"),
    MARK_TIME_INVULNERABLE("MarkTimeInvulnerable"),
    MARK_TIME_STRATEGY("MarkTimeStrategy"),
    MARK_TIME_VULNERABLE("MarkTimeVulnerable"),
    MARK_STACK_INVULNERABLE("MarkStackInvulnerable"),
    MARK_STACK_STRATEGY("MarkStackStrategy"),
    MARK_STACK_VULNERABLE("MarkStackVulnerable"),
    MARK_DNS_INVULNERABLE("MarkDnsInvulnerable"),
    MARK_DNS_STRATEGY("MarkDnsStrategy"),
    MARK_DNS_VULNERABLE("MarkDnsVulnerable"),

    MESSAGE_BINARY("MessageBinary"),
    MESSAGE_CHUNK("MessageChunk"),
    MESSAGE_HEADER("MessageHeader"),
    
    SET_VENDOR("SetVendor"),
    DATABASE_IDENTIFIED("DatabaseIdentified"),
    
    UNSUBSCRIBE("Unsubscribe");  // without real class to unsubscribe subscriber implicitly
    
    private final String name;
    
    Interaction(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}