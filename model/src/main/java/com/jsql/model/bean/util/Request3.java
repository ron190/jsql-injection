package com.jsql.model.bean.util;

import com.jsql.model.bean.database.AbstractElementDatabase;
import com.jsql.model.bean.database.Column;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.blind.callable.AbstractCallableBit;
import com.jsql.model.injection.engine.model.Engine;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public sealed interface Request3 permits
    Request3.AddColumns, Request3.AddDatabases, Request3.AddTabExploitSql, Request3.AddTabExploitUdfExtensionPostgres,
    Request3.AddTabExploitUdfH2, Request3.AddTabExploitUdfLibraryPostgres, Request3.AddTabExploitUdfMysql, Request3.AddTabExploitUdfOracle,
    Request3.AddTabExploitUdfProgramPostgres, Request3.AddTabExploitUdfSqlite, Request3.AddTabExploitUdfWalPostgres, Request3.AddTabExploitWeb,
    Request3.AddTables, Request3.CreateAdminPageTab, Request3.CreateAnalysisReport, Request3.CreateFileTab, Request3.CreateValuesTab,
    Request3.MarkEngineFound, Request3.EndIndeterminateProgress, Request3.EndPreparation, Request3.EndProgress, Request3.GetTerminalResult,
    Request3.MarkErrorInvulnerable, Request3.MarkErrorStrategy, Request3.MarkErrorVulnerable, Request3.MarkFileSystemInvulnerable,
    Request3.MarkFileSystemVulnerable, Request3.MarkInvulnerable, Request3.MarkVulnerable, Request3.MessageBinary,
    Request3.MessageChunk, Request3.MessageHeader, Request3.ActivateEngine, Request3.StartIndeterminateProgress, Request3.StartProgress,
    Request3.UpdateProgress, Request3.ActivateStrategy {

    record AddColumns(List<Column> columns) implements Request3 {}
    record AddDatabases(List<Database> databases) implements Request3 {}
    record AddTables(List<Table> tables) implements Request3 {}
    record CreateValuesTab(String[] columns, String[][] table, Table tableBean) implements Request3 {}

    /** End the refreshing of administration page search button */
    record MarkEngineFound(Engine engine) implements Request3 {}
    record ActivateEngine(Engine engine) implements Request3 {}

    record MarkInvulnerable(AbstractStrategy strategy) implements Request3 {}
    record MarkErrorInvulnerable(int indexError, AbstractStrategy strategy) implements Request3 {}

    record MarkVulnerable(AbstractStrategy strategy) implements Request3 {}
    record MarkErrorVulnerable(int indexError, AbstractStrategy strategy) implements Request3 {}

    record ActivateStrategy(AbstractStrategy strategy) implements Request3 {}
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
    record AddTabExploitUdfExtensionPostgres(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
    record AddTabExploitUdfH2(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
    record AddTabExploitUdfLibraryPostgres(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
    record AddTabExploitUdfMysql(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
    record AddTabExploitUdfOracle(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
    record AddTabExploitUdfProgramPostgres(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
    record AddTabExploitUdfSqlite(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
    record AddTabExploitUdfWalPostgres(BiConsumer<String, UUID> biConsumerRunCmd) implements Request3 {}
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
