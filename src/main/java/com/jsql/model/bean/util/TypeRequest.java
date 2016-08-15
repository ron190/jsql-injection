package com.jsql.model.bean.util;

public enum TypeRequest {
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
    END_INDETERMINATE_PROGRESS("EndIndeterminateProgress"),
    END_PREPARATION("EndPreparation"),
    END_PROGRESS("EndProgress"),
    END_SCAN("EndScan"),
    END_UPLOAD("EndUpload"),
    GET_WEB_SHELL_RESULT("GetWebShellResult"),
    GET_SQL_SHELL_RESULT("GetSQLShellResult"),
    INTERACTION_COMMAND("InteractionCommand"),
    MARK_BLIND_INVULNERABLE("MarkBlindInvulnerable"),
    MARK_BLIND_STRATEGY("MarkBlindStrategy"),
    MARK_BLIND_VULNERABLE("MarkBlindVulnerable"),
    MARK_ERRORBASED_INVULNERABLE("MarkErrorbasedInvulnerable"),
    MARK_ERRORBASED_STRATEGY("MarkErrorbasedStrategy"),
    MARK_ERRORBASED_VULNERABLE("MarkErrorbasedVulnerable"),
    MARK_FILE_SYSTEM_INVULNERABLE("MarkFileSystemInvulnerable"),
    MARK_FILE_SYSTEM_VULNERABLE("MarkFileSystemVulnerable"),
    MARK_NORMAL_INVULNERABLE("MarkNormalInvulnerable"),
    MARK_NORMAL_STRATEGY("MarkNormalStrategy"),
    MARK_NORMAL_VULNERABLE("MarkNormalVulnerable"),
    MARK_TIMEBASED_INVULNERABLE("MarkTimebasedInvulnerable"),
    MARK_TIMEBASED_STRATEGY("MarkTimebasedStrategy"),
    MARK_TIMEBASED_VULNERABLE("MarkTimebasedVulnerable"),
    MESSAGE_BINARY("MessageBinary"),
    MESSAGE_CHUNK("MessageChunk"),
    MESSAGE_HEADER("MessageHeader"),
    RESET_INTERFACE("ResetInterface"),
    RESET_STRATEGY_LABEL("ResetStrategyLabel"),
    SET_VENDOR("SetVendor"),
    START_INDETERMINATE_PROGRESS("StartIndeterminateProgress"),
    START_PROGRESS("StartProgress"),
    UPDATE_PROGRESS("UpdateProgress"),
    DATABASE_IDENTIFIED("DatabaseIdentified");
    
    private String name;
    
    private TypeRequest(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}