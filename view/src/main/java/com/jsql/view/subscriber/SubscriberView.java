package com.jsql.view.subscriber;

import com.jsql.model.injection.strategy.AbstractStrategy;
import com.jsql.model.injection.strategy.StrategyError;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.panel.consoles.NetworkTable;
import com.jsql.view.swing.terminal.AbstractExploit;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

public class SubscriberView extends AbstractSubscriber {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected void execute(Seal request) {
        this.addLog(request);
        this.progress(request);
        this.executeInjection(request);
        this.createTab(request);
        this.executeExploit(request);
    }

    private void executeInjection(Seal request) {
        switch (request) {
            case Seal.ActivateEngine r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().setEngine(r.engine());
            case Seal.AddColumns(var columns) -> MediatorHelper.treeDatabase().addColumns(columns);
            case Seal.AddTables(var tables) -> MediatorHelper.treeDatabase().addTables(tables);
            case Seal.AddDatabases(var databases) -> MediatorHelper.treeDatabase().addDatabases(databases);

            case Seal.MarkStrategyInvulnerable(int indexError, AbstractStrategy strategy) -> {
                if (strategy instanceof StrategyError) {
                    MediatorHelper.panelAddressBar().getPanelTrailingAddress().markInvulnerable(indexError, strategy);
                } else {
                    MediatorHelper.panelAddressBar().getPanelTrailingAddress().markInvulnerable(strategy);
                }
            }
            case Seal.MarkStrategyVulnerable(int indexError, var strategy) -> {
                if (strategy instanceof StrategyError) {
                    MediatorHelper.panelAddressBar().getPanelTrailingAddress().markVulnerable(indexError, strategy);
                } else {
                    MediatorHelper.panelAddressBar().getPanelTrailingAddress().markVulnerable(strategy);
                }
            }
            case Seal.ActivateStrategy(var strategy) -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().activateStrategy(strategy);

            case Seal.MarkFileSystemInvulnerable ignored -> MediatorHelper.tabManagersCards().markFileSystemInvulnerable();
            case Seal.MarkFileSystemVulnerable ignored -> MediatorHelper.tabManagersCards().markFileSystemVulnerable();
            default -> {
                // ignore
            }
        }
    }

    private void executeExploit(Seal request) {
        switch (request) {
            case Seal.AddTabExploitSql(var urlSuccess, var username, var password) -> MediatorHelper.tabResults().addTabExploitSql(urlSuccess, username, password);
            case Seal.AddTabExploitUdf r -> MediatorHelper.tabResults().addTabExploitUdf(r.biConsumerRunCmd());
            case Seal.AddTabExploitWeb(String urlSuccess) -> MediatorHelper.tabResults().addTabExploitWeb(urlSuccess);
            case Seal.GetTerminalResult(UUID uuidShell, String result) -> {
                AbstractExploit terminal = MediatorHelper.frame().getMapUuidShell().get(uuidShell);
                if (terminal != null) {  // null on reverse shell connection
                    terminal.append(result);
                    terminal.append("\n");
                    terminal.reset();
                }
            }
            default -> {
                // ignore
            }
        }
    }

    private void addLog(Seal request) {
        switch (request) {
            case Seal.MessageBinary(var message) -> {
                MediatorHelper.panelConsoles().messageBinary(message);
                MediatorHelper.tabConsoles().setBold("Boolean");
            }
            case Seal.MessageChunk(var message) -> {
                MediatorHelper.panelConsoles().messageChunk(message);
                MediatorHelper.tabConsoles().setBold("Chunk");
            }
            case Seal.MessageHeader r -> {
                NetworkTable table = MediatorHelper.panelConsoles().getNetworkTable();
                table.addHeader(r);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                try {
                    model.addRow(new Object[] {
                        r.url(),
                        r.size(),
                        r.metadataStrategy(),
                        Arrays.asList(r.metadataProcess(), r.metadataBoolean())
                    });

                    Rectangle rect = table.getCellRect(table.getRowCount() - 1, 0, true);
                    table.scrollRectToVisible(rect);

                    MediatorHelper.tabConsoles().setBold("Network");
                } catch(NullPointerException | IndexOutOfBoundsException e) {
                    // Fix #4658, #2224, #1797 on model.addRow()
                    LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
                }
            }
            default -> {
                // ignore
            }
        }
    }

    private void createTab(Seal request) {
        switch (request) {
            case Seal.CreateAdminPageTab(String urlSuccess) -> MediatorHelper.tabResults().addAdminTab(urlSuccess);
            case Seal.CreateAnalysisReport(var content) -> MediatorHelper.tabResults().addReportTab(content.trim());
            case Seal.CreateFileTab(var name, var content, var path) -> MediatorHelper.tabResults().addFileTab(name, content, path);
            case Seal.CreateValuesTab(var columns, var table, var tableBean) -> MediatorHelper.treeDatabase().createValuesTab(table, columns, tableBean);
            default -> {
                // ignore
            }
        }
    }

    private void progress(Seal request) {
        switch (request) {
            case Seal.StartIndeterminateProgress(var table) -> MediatorHelper.treeDatabase().startIndeterminateProgress(table);
            case Seal.StartProgress(var elementDatabase) -> MediatorHelper.treeDatabase().startProgress(elementDatabase);
            case Seal.UpdateProgress(var database, var countProgress) -> MediatorHelper.treeDatabase().updateProgress(database, countProgress);
            case Seal.EndIndeterminateProgress(var table) -> MediatorHelper.treeDatabase().endIndeterminateProgress(table);
            case Seal.EndPreparation ignored -> {
                MediatorHelper.panelAddressBar().getPanelTrailingAddress().endPreparation();
                if (MediatorHelper.model().shouldErasePreviousInjection()) {
                    MediatorHelper.tabManagersCards().endPreparation();
                }
            }
            case Seal.EndProgress(var elementDatabase) -> MediatorHelper.treeDatabase().endProgress(elementDatabase);
            default -> {
                // ignore
            }
        }
    }
}
