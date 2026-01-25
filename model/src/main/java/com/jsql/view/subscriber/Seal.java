package com.jsql.view.subscriber;

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

public sealed interface Seal permits
    Seal.AddColumns,
    Seal.AddDatabases,
    Seal.AddTables,
    Seal.CreateValuesTab,

    Seal.MarkEngineFound,
    Seal.ActivateEngine,

    Seal.MarkStrategyInvulnerable,
    Seal.MarkStrategyVulnerable,
    Seal.ActivateStrategy,

    Seal.MarkFileSystemInvulnerable,
    Seal.MarkFileSystemVulnerable,

    Seal.MessageBinary,
    Seal.MessageChunk,
    Seal.MessageHeader,

    Seal.AddTabExploitSql,
    Seal.AddTabExploitUdf,
    Seal.AddTabExploitWeb,
    Seal.GetTerminalResult,

    Seal.CreateAdminPageTab,
    Seal.CreateAnalysisReport,
    Seal.CreateFileTab,

    Seal.EndIndeterminateProgress,
    Seal.EndPreparation,
    Seal.EndProgress,
    Seal.StartIndeterminateProgress,
    Seal.StartProgress,
    Seal.UpdateProgress {

    record AddColumns(List<Column> columns) implements Seal {}
    record AddDatabases(List<Database> databases) implements Seal {}
    record AddTables(List<Table> tables) implements Seal {}
    record CreateValuesTab(String[] columns, String[][] table, Table tableBean) implements Seal {}

    /** End the refreshing of administration page search button */
    record MarkEngineFound(Engine engine) implements Seal {}
    record ActivateEngine(Engine engine) implements Seal {}

    record MarkStrategyInvulnerable(int indexError, AbstractStrategy strategy) implements Seal {
        public MarkStrategyInvulnerable(AbstractStrategy strategy) {
            this(-1, strategy);
        }
    }
    record MarkStrategyVulnerable(int indexError, AbstractStrategy strategy) implements Seal {
        public MarkStrategyVulnerable(AbstractStrategy strategy) {
            this(-1, strategy);
        }
    }
    record ActivateStrategy(AbstractStrategy strategy) implements Seal {}

    record MarkFileSystemInvulnerable() implements Seal {}
    record MarkFileSystemVulnerable() implements Seal {}

    record MessageChunk(String message) implements Seal {}
    record MessageBinary(String message) implements Seal {}
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
    ) implements Seal {
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

    record AddTabExploitSql(String urlSuccess, String username, String password) implements Seal {}
    record AddTabExploitUdf(BiConsumer<String, UUID> biConsumerRunCmd) implements Seal {}
    record AddTabExploitWeb(String urlSuccess) implements Seal {}
    record GetTerminalResult(UUID uuidShell, String result) implements Seal {}

    record CreateAdminPageTab(String urlSuccess) implements Seal {}
    record CreateAnalysisReport(String content) implements Seal {}
    record CreateFileTab(String name, String content, String path) implements Seal {}

    record EndIndeterminateProgress(Table table) implements Seal {}
    record EndPreparation() implements Seal {}
    record EndProgress(AbstractElementDatabase elementDatabase) implements Seal {}
    record StartIndeterminateProgress(Table table) implements Seal {}
    record StartProgress(AbstractElementDatabase elementDatabase) implements Seal {}
    record UpdateProgress(AbstractElementDatabase elementDatabase, int countProgress) implements Seal {}
}
