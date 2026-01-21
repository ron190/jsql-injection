package com.jsql.model.bean.util;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.blind.callable.AbstractCallableBit;
import com.jsql.model.injection.vendor.model.Vendor;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public sealed interface Request3 permits
    Request3.AddColumns, Request3.AddDatabases, Request3.AddTabExploitSql, Request3.AddTabExploitUdfExtensionPostgres,
    Request3.AddTabExploitUdfH2, Request3.AddTabExploitUdfLibraryPostgres, Request3.AddTabExploitUdfMysql, Request3.AddTabExploitUdfOracle,
    Request3.AddTabExploitUdfProgramPostgres, Request3.AddTabExploitUdfSqlite, Request3.AddTabExploitUdfWalPostgres, Request3.AddTabExploitWeb,
    Request3.AddTables, Request3.CreateAdminPageTab, Request3.CreateAnalysisReport, Request3.CreateFileTab, Request3.CreateValuesTab,
    Request3.DatabaseIdentified, Request3.EndIndeterminateProgress, Request3.EndPreparation, Request3.EndProgress, Request3.GetTerminalResult,
    Request3.MarkErrorInvulnerable, Request3.MarkErrorStrategy, Request3.MarkErrorVulnerable, Request3.MarkFileSystemInvulnerable,
    Request3.MarkFileSystemVulnerable, Request3.MarkStrategyInvulnerable, Request3.MarkStrategyVulnerable, Request3.MessageBinary,
    Request3.MessageChunk, Request3.MessageHeader, Request3.SetVendor, Request3.StartIndeterminateProgress, Request3.StartProgress,
    Request3.UpdateProgress, Request3.MarkStrategy {

    record AddColumns(List<Column> columns) implements Request3 {}
    record AddDatabases(List<Database> databases) implements Request3 {}
    record AddTables(List<Table> tables) implements Request3 {}
    record CreateValuesTab(String[] columns, String[][] table, Table tableBean) implements Request3 {}

    /** End the refreshing of administration page search button */
    record DatabaseIdentified(String url, Vendor vendor) implements Request3 {}
    record SetVendor(String url, Vendor vendor) implements Request3 {}

    record MarkStrategyInvulnerable(AbstractStrategy strategy) implements Request3 {}
    record MarkErrorInvulnerable(int indexError, AbstractStrategy strategy) implements Request3 {}

    record MarkStrategyVulnerable(AbstractStrategy strategy) implements Request3 {}
    record MarkErrorVulnerable(int indexError, AbstractStrategy strategy) implements Request3 {}

    record MarkStrategy(AbstractStrategy strategy) implements Request3 {}
    record MarkErrorStrategy(AbstractStrategy strategy) implements Request3 {}

    record MarkFileSystemInvulnerable() implements Request3 {}
    record MarkFileSystemVulnerable() implements Request3 {}

    record MessageChunk(String message) implements Request3 {}
    record MessageBinary(String message) implements Request3 {}
    record MessageHeader(
        String url,
        String post,
        Map<String, String> header,
        Map<String, String> response,
        String source,
        String size,
        String metadataStrategy,
        String metadataProcess,
        AbstractCallableBit<?> metadataBoolean
    ) implements Request3 {
        public MessageHeader(
            String url,
            String post,
            Map<String, String> header,
            Map<String, String> response,
            String source,
            String size,
            String metadataStrategy,
            String metadataProcess,
            AbstractCallableBit<?> metadataBoolean
        ) {
            this.url = url == null ? Strings.EMPTY : url;
            this.post = post == null ? Strings.EMPTY : post;
            this.header = header == null ? Collections.emptyMap() : header;
            this.response = response == null ? Collections.emptyMap() : response;
            this.source = source == null ? Strings.EMPTY : source;
            this.size = size == null ? Strings.EMPTY : size;
            this.metadataStrategy = metadataStrategy == null ? Strings.EMPTY : metadataStrategy;
            this.metadataProcess = metadataProcess == null ? Strings.EMPTY : metadataProcess;
            this.metadataBoolean = metadataBoolean;
        }
    }

    record AddTabExploitSql(String urlSuccess, String username, String password) implements Request3 {}
    record AddTabExploitUdfExtensionPostgres() implements Request3 {}
    record AddTabExploitUdfH2() implements Request3 {}
    record AddTabExploitUdfLibraryPostgres() implements Request3 {}
    record AddTabExploitUdfMysql() implements Request3 {}
    record AddTabExploitUdfOracle() implements Request3 {}
    record AddTabExploitUdfProgramPostgres() implements Request3 {}
    record AddTabExploitUdfSqlite() implements Request3 {}
    record AddTabExploitUdfWalPostgres() implements Request3 {}
    record AddTabExploitWeb(String urlSuccess) implements Request3 {}
    record GetTerminalResult(UUID uuidShell, String result) implements Request3 {}

    record CreateAdminPageTab(String urlSuccess) implements Request3 {}
    record CreateAnalysisReport(String content) implements Request3 {}
    record CreateFileTab(String name, String content, String path) implements Request3 {}

    record EndIndeterminateProgress(Table table) implements Request3 {}
    record EndPreparation() implements Request3 {}
    record EndProgress(AbstractElementDatabase elementDatabase) implements Request3 {}
    record StartIndeterminateProgress(Table table) implements Request3 {}
    record StartProgress(AbstractElementDatabase elementDatabase) implements Request3 {}
    record UpdateProgress(AbstractElementDatabase elementDatabase, int countProgress) implements Request3 {}
}
