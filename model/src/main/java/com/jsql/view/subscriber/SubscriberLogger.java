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
import com.jsql.model.injection.strategy.StrategyError;
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
    protected void execute(Seal request) {
        switch (request) {
            case Seal.AddDatabases r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));
            case Seal.AddTables r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));
            case Seal.AddColumns r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));

            case Seal.AddTabExploitWeb r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));
            case Seal.GetTerminalResult r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));

            case Seal.MarkStrategyInvulnerable(var indexError, var strategy) -> {
                if (strategy instanceof StrategyError) {
                    LOGGER.debug(() -> AnsiColorUtil.addRedColor(
                        this.model.getMediatorEngine().getEngine().instance().getModelYaml().getStrategy().getError().getMethod().get(indexError).getName()
                    ));
                } else {
                    LOGGER.debug(() -> AnsiColorUtil.addRedColor(strategy.getClass().getSimpleName()));
                }
            }
            case Seal.MarkStrategyVulnerable(var indexError, var strategy) -> {
                if (strategy instanceof StrategyError) {
                    LOGGER.info(() -> AnsiColorUtil.addGreenColor(
                        this.model.getMediatorEngine().getEngine().instance().getModelYaml().getStrategy().getError().getMethod().get(indexError).getName()
                    ));
                } else {
                    LOGGER.info(() -> AnsiColorUtil.addGreenColor(strategy.getClass().getSimpleName()));
                }
            }

            case Seal.MarkFileSystemInvulnerable r -> LOGGER.debug(() -> AnsiColorUtil.addRedColor(r.getClass().getSimpleName()));
            case Seal.MarkFileSystemVulnerable r -> LOGGER.info(() -> AnsiColorUtil.addGreenColor(r.getClass().getSimpleName()));

            case Seal.MessageBinary(var message) -> LOGGER.info(message::trim);
            default -> {
                // empty
            }
        }
    }
}
