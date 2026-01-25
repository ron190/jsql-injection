/*******************************************************************************
 * Copyhacked (H) 2012-2025.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.subscriber;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Request3;
import com.jsql.util.AnsiColorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * View in the MVC pattern for integration test, process actions sent by the model.<br>
 */
public class SubscriberLogger extends AbstractSubscriber {

    private static final Logger LOGGER = LogManager.getRootLogger();
    private final InjectionModel model;

    public SubscriberLogger(InjectionModel model) {
        this.model = model;
    }

    @Override
    protected void execute(Request3 request) {
        switch (request) {
            case Request3.AddDatabases r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));
            case Request3.AddTables r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));
            case Request3.AddColumns r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));

            case Request3.AddTabExploitWeb r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));
            case Request3.GetTerminalResult r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));

            case Request3.MarkInvulnerable(var strategy) -> LOGGER.debug(() -> AnsiColorUtil.addRedColor(strategy.getClass().getSimpleName()));
            case Request3.MarkErrorInvulnerable r -> LOGGER.debug(() -> AnsiColorUtil.addRedColor(
                this.model.getMediatorEngine().getEngine().instance().getModelYaml().getStrategy().getError().getMethod().get(r.indexError()).getName()
            ));

            case Request3.MarkVulnerable(var strategy) -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(strategy.getClass().getSimpleName()));
            case Request3.MarkErrorVulnerable r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(
                this.model.getMediatorEngine().getEngine().instance().getModelYaml().getStrategy().getError().getMethod().get(r.indexError()).getName()
            ));

            case Request3.MarkFileSystemInvulnerable r -> LOGGER.debug(() -> AnsiColorUtil.addRedColor(r.getClass().getSimpleName()));
            case Request3.MarkFileSystemVulnerable r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));

            case Request3.MessageBinary(var message) -> LOGGER.info(message::trim);
            default -> {
                // empty
            }
        }
    }
}
