package com.jsql.model.bean.util;

public enum Interaction {
    
    ADD_COLUMNS("AddColumns"),
    ADD_DATABASES("AddDatabases"),
    ADD_TABLES("AddTables"),
    
    CREATE_ADMIN_PAGE_TAB("CreateAdminPageTab"),
    CREATE_FILE_TAB("CreateFileTab"),
    ADD_TAB_EXPLOIT_WEB("AddTabExploitWeb"),
    ADD_TAB_EXPLOIT_SQL("AddTabExploitSql"),
    ADD_TAB_EXPLOIT_RCE_MYSQL("AddTabExploitRceMysql"),
    ADD_TAB_EXPLOIT_RCE_ORACLE("AddTabExploitRceOracle"),
    ADD_TAB_EXPLOIT_RCE_EXTENSION_POSTGRES("AddTabExploitRceExtensionPostgres"),
    ADD_TAB_EXPLOIT_RCE_WAL_POSTGRES("AddTabExploitRceWalPostgres"),
    ADD_TAB_EXPLOIT_RCE_PROGRAM_POSTGRES("AddTabExploitRceProgramPostgres"),
    ADD_TAB_EXPLOIT_RCE_SQLITE("AddTabExploitRceSqlite"),
    ADD_TAB_EXPLOIT_RCE_H2("AddTabExploitRceH2"),
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
    GET_EXPLOIT_WEB_RESULT("GetExploitWebResult"),
    GET_EXPLOIT_SQL_RESULT("GetExploitSqlResult"),
    GET_EXPLOIT_UDF_RESULT("GetExploitUdfResult"),
    GET_EXPLOIT_RCE_RESULT("GetExploitRceResult"),

    MARK_MULTI_INVULNERABLE("MarkMultiInvulnerable"),
    MARK_MULTI_STRATEGY("MarkMultiStrategy"),
    MARK_MULTI_VULNERABLE("MarkMultiVulnerable"),
    MARK_BLIND_INVULNERABLE("MarkBlindInvulnerable"),
    MARK_BLIND_STRATEGY("MarkBlindStrategy"),
    MARK_BLIND_VULNERABLE("MarkBlindVulnerable"),
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