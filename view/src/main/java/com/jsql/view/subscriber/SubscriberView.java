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
        switch (request) {
            case Request3.SetVendor r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().setVendor(r.vendor());
            case Request3.AddColumns r -> MediatorHelper.treeDatabase().addColumns(r.columns());
            case Request3.AddTables r -> MediatorHelper.treeDatabase().addTables(r.tables());
            case Request3.AddDatabases r -> MediatorHelper.treeDatabase().addDatabases(r.databases());

            case Request3.AddTabExploitSql r -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitSql(r.urlSuccess(), r.username(), r.password()));
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
            case Request3.AddTabExploitWeb r -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitWeb(r.urlSuccess()));
            }
            case Request3.CreateAdminPageTab r -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addAdminTab(r.urlSuccess()));
            }
            case Request3.CreateAnalysisReport r -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addReportTab(r.content().trim()));
            }
            case Request3.CreateFileTab r -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addFileTab(r.name(), r.content(), r.path()));
            }
            case Request3.CreateValuesTab r -> {
                MediatorHelper.frame().getSplitNS().initSplitOrientation();
                SwingUtilities.invokeLater(() -> MediatorHelper.treeDatabase().createValuesTab(r.table(), r.columns(), r.tableBean()));
            }
            case Request3.GetTerminalResult r -> {
                AbstractExploit terminal = MediatorHelper.frame().getMapUuidShell().get(r.uuidShell());
                if (terminal != null) {  // null on reverse shell connection
                    terminal.append(r.result());
                    terminal.append("\n");
                    terminal.reset();
                }
            }

            case Request3.MarkTimeInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(r.strategy());
            case Request3.MarkTimeStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(r.strategy());
            case Request3.MarkTimeVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(r.strategy());
            case Request3.MarkBlindBinInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(r.strategy());
            case Request3.MarkBlindBinStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(r.strategy());
            case Request3.MarkBlindBinVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(r.strategy());
            case Request3.MarkBlindBitInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(r.strategy());
            case Request3.MarkBlindBitStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(r.strategy());
            case Request3.MarkBlindBitVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(r.strategy());
            case Request3.MarkMultibitInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(r.strategy());
            case Request3.MarkMultibitStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(r.strategy());
            case Request3.MarkMultibitVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(r.strategy());
            case Request3.MarkDnsInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(r.strategy());
            case Request3.MarkDnsStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(r.strategy());
            case Request3.MarkDnsVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(r.strategy());
            case Request3.MarkErrorInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markErrorInvulnerable(r.indexError());
            case Request3.MarkErrorStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markError();
            case Request3.MarkErrorVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markErrorVulnerable(r.indexError());
            case Request3.MarkStackInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(r.strategy());
            case Request3.MarkStackStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(r.strategy());
            case Request3.MarkStackVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(r.strategy());
            case Request3.MarkUnionInvulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyInvulnerable(r.strategy());
            case Request3.MarkUnionStrategy r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategy(r.strategy());
            case Request3.MarkUnionVulnerable r -> MediatorHelper.panelAddressBar().getPanelTrailingAddress().markStrategyVulnerable(r.strategy());
            case Request3.MarkFileSystemInvulnerable ignored -> MediatorHelper.tabManagersCards().markFileSystemInvulnerable();
            case Request3.MarkFileSystemVulnerable ignored -> MediatorHelper.tabManagersCards().markFileSystemVulnerable();

            case Request3.MessageBinary r -> {
                MediatorHelper.panelConsoles().messageBinary(r.message());
                MediatorHelper.tabConsoles().setBold("Boolean");
            }
            case Request3.MessageChunk r -> {
                MediatorHelper.panelConsoles().messageChunk(r.message());
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

            case Request3.StartIndeterminateProgress r -> MediatorHelper.treeDatabase().startIndeterminateProgress(r.table());
            case Request3.StartProgress r -> MediatorHelper.treeDatabase().startProgress(r.elementDatabase());
            case Request3.UpdateProgress r -> MediatorHelper.treeDatabase().updateProgress(r.elementDatabase(), r.countProgress());
            case Request3.EndIndeterminateProgress r -> MediatorHelper.treeDatabase().endIndeterminateProgress(r.table());
            case Request3.EndPreparation ignored -> {
                MediatorHelper.panelAddressBar().getPanelTrailingAddress().endPreparation();
                if (MediatorHelper.model().shouldErasePreviousInjection()) {
                    MediatorHelper.tabManagersCards().endPreparation();
                }
            }
            case Request3.EndProgress r -> MediatorHelper.treeDatabase().endProgress(r.elementDatabase());

            default -> {}
        }
    }
}
