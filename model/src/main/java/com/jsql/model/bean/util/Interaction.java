package com.jsql.model.bean.util;

public enum Interaction {
    
    ADD_COLUMNS("AddColumns"),
    ADD_DATABASES("AddDatabases"),
    ADD_TABLES("AddTables"),
    
    CREATE_ADMIN_PAGE_TAB("CreateAdminPageTab"),
    CREATE_FILE_TAB("CreateFileTab"),
    CREATE_SHELL_TAB("CreateShellTab"),
    CREATE_SQL_SHELL_TAB("CreateSQLShellTab"),
    CREATE_TAB("CreateTab"),
    CREATE_VALUES_TAB("CreateValuesTab"),
    
    END_ADMIN_SEARCH("EndAdminSearch"),
    END_FILE_SEARCH("EndFileSearch"),
    END_SCAN("EndScan"),
    END_UPLOAD("EndUpload"),

    START_PROGRESS("StartProgress"),
    END_PROGRESS("EndProgress"),
    START_INDETERMINATE_PROGRESS("StartIndeterminateProgress"),
    END_INDETERMINATE_PROGRESS("EndIndeterminateProgress"),
    UPDATE_PROGRESS("UpdateProgress"),
    END_PREPARATION("EndPreparation"),
    
    MARK_FILE_SYSTEM_INVULNERABLE("MarkFileSystemInvulnerable"),
    MARK_FILE_SYSTEM_VULNERABLE("MarkFileSystemVulnerable"),
    GET_WEB_SHELL_RESULT("GetWebShellResult"),
    GET_SQL_SHELL_RESULT("GetSQLShellResult"),
    INTERACTION_COMMAND("InteractionCommand"),
    
    MARK_BLIND_INVULNERABLE("MarkBlindInvulnerable"),
    MARK_BLIND_STRATEGY("MarkBlindStrategy"),
    MARK_BLIND_VULNERABLE("MarkBlindVulnerable"),
    MARK_ERROR_INVULNERABLE("MarkErrorInvulnerable"),
    MARK_ERROR_STRATEGY("MarkErrorStrategy"),
    MARK_ERROR_VULNERABLE("MarkErrorVulnerable"),
    MARK_NORMAL_INVULNERABLE("MarkNormalInvulnerable"),
    MARK_NORMAL_STRATEGY("MarkNormalStrategy"),
    MARK_NORMAL_VULNERABLE("MarkNormalVulnerable"),
    MARK_TIME_INVULNERABLE("MarkTimeInvulnerable"),
    MARK_TIME_STRATEGY("MarkTimeStrategy"),
    MARK_TIME_VULNERABLE("MarkTimeVulnerable"),
    
    MESSAGE_BINARY("MessageBinary"),
    MESSAGE_CHUNK("MessageChunk"),
    MESSAGE_HEADER("MessageHeader"),
    
    RESET_INTERFACE("ResetInterface"),
    RESET_STRATEGY_LABEL("ResetStrategyLabel"),
    
    SET_VENDOR("SetVendor"),
    DATABASE_IDENTIFIED("DatabaseIdentified"),
    
    UNSUBSCRIBE("Unsubscribe");
    
    private String name;
    
    private Interaction(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}