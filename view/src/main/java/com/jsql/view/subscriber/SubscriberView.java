package com.jsql.view.subscriber;

import com.jsql.model.bean.util.Request3;
import com.jsql.util.LogLevelUtil;
import com.jsql.view.swing.panel.consoles.NetworkTable;
import com.jsql.view.swing.terminal.AbstractExploit;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

public class SubscriberView extends AbstractSubscriber {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    protected void execute(Request3 request) {
        this.addLog(request);
        this.progress(request);
        this.executeInjection(request);
        this.createTab(request);
        this.executeExploit(request);
    }

    private void executeInjection(Request3 request) {
        switch (request) {
            case Request3.SetVendor r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().setVendor(r.vendor());
            case Request3.AddColumns(var columns) -> MediatorHelper.treeDatabase().addColumns(columns);
            case Request3.AddTables(var tables) -> MediatorHelper.treeDatabase().addTables(tables);
            case Request3.AddDatabases(var databases) -> MediatorHelper.treeDatabase().addDatabases(databases);

            case Request3.MarkStrategyInvulnerable(var strategy) -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(strategy);
            case Request3.MarkErrorInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markErrorInvulnerable(r.indexError());

            case Request3.MarkStrategyVulnerable(var strategy) -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(strategy);
            case Request3.MarkErrorVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markErrorVulnerable(r.indexError());

            case Request3.MarkStrategy(var strategy) -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(strategy);
            case Request3.MarkErrorStrategy ignored -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markError();

            case Request3.MarkFileSystemInvulnerable ignored -> MediatorHelper.tabManagersCards().markFileSystemInvulnerable();
            case Request3.MarkFileSystemVulnerable ignored -> MediatorHelper.tabManagersCards().markFileSystemVulnerable();
            default -> {
                // ignore
            }
        }
    }

    private void executeExploit(Request3 request) {
        switch (request) {
            case Request3.AddTabExploitSql(var urlSuccess, var username, var password) -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitSql(urlSuccess, username, password));
            }
            case Request3.AddTabExploitUdfExtensionPostgres ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitPostgres().runRceExtensionCmd(command, terminalID)
                ));
            }
            case Request3.AddTabExploitUdfH2 ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitH2().runRce(command, terminalID)
                ));
            }
            case Request3.AddTabExploitUdfLibraryPostgres ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitPostgres().runRceLibraryCmd(command, terminalID)
                ));
            }
            case Request3.AddTabExploitUdfMysql ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitMysql().runRceCmd(command, terminalID)
                ));
            }
            case Request3.AddTabExploitUdfOracle ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitOracle().runRceCmd(command, terminalID)
                ));
            }
            case Request3.AddTabExploitUdfProgramPostgres ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitPostgres().runRceProgramCmd(command, terminalID)
                ));
            }
            case Request3.AddTabExploitUdfSqlite ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitSqlite().runRce(command, terminalID)
                ));
            }
            case Request3.AddTabExploitUdfWalPostgres ignored -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitUdf(
                    (String command, UUID terminalID) -> MediatorHelper.model().getResourceAccess().getExploitPostgres().runRceArchiveCmd(command, terminalID)
                ));
            }
            case Request3.AddTabExploitWeb(String urlSuccess) -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitWeb(urlSuccess));
            }
            case Request3.GetTerminalResult(UUID uuidShell, String result) -> {
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

    private void addLog(Request3 request) {
        switch (request) {
            case Request3.MessageBinary(var message) -> {
                MediatorHelper.panelConsoles().messageBinary(message);
                MediatorHelper.tabConsoles().setBold("Boolean");
            }
            case Request3.MessageChunk(var message) -> {
                MediatorHelper.panelConsoles().messageChunk(message);
                MediatorHelper.tabConsoles().setBold("Chunk");
            }
            case Request3.MessageHeader r -> {
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

    private void createTab(Request3 request) {
        switch (request) {
            case Request3.CreateAdminPageTab(String urlSuccess) -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addAdminTab(urlSuccess));
            }
            case Request3.CreateAnalysisReport(var content) -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addReportTab(content.trim()));
            }
            case Request3.CreateFileTab(var name, var content, var path) -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addFileTab(name, content, path));
            }
            case Request3.CreateValuesTab(var columns, var table, var tableBean) -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.treeDatabase().createValuesTab(table, columns, tableBean));
            }
            default -> {
                // ignore
            }
        }
    }

    private void progress(Request3 request) {
        switch (request) {
            case Request3.StartIndeterminateProgress(var table) -> MediatorHelper.treeDatabase().startIndeterminateProgress(table);
            case Request3.StartProgress(var elementDatabase) -> MediatorHelper.treeDatabase().startProgress(elementDatabase);
            case Request3.UpdateProgress(var database, var countProgress) -> MediatorHelper.treeDatabase().updateProgress(database, countProgress);
            case Request3.EndIndeterminateProgress(var table) -> MediatorHelper.treeDatabase().endIndeterminateProgress(table);
            case Request3.EndPreparation ignored -> {
                MediatorHelper.panelAddressBar().getPanelTrailingAddress().endPreparation();
                if (MediatorHelper.model().shouldErasePreviousInjection()) {
                    MediatorHelper.tabManagersCards().endPreparation();
                }
            }
            case Request3.EndProgress(var elementDatabase) -> MediatorHelper.treeDatabase().endProgress(elementDatabase);
            default -> {
                // ignore
            }
        }
    }
}
