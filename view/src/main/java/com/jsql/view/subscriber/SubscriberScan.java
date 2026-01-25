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

import com.jsql.view.swing.util.MediatorHelper;

public class SubscriberScan extends AbstractSubscriber {
    @Override
    protected void execute(Seal request) {
        var urlByUser = MediatorHelper.model().getMediatorUtils().connectionUtil().getUrlByUser();
        switch (request) {
            case Seal.MarkEngineFound(var engine) -> MediatorHelper.managerScan().highlight(urlByUser, engine.toString());
            case Seal.ActivateEngine(var engine) -> MediatorHelper.managerScan().highlight(urlByUser, engine.toString());
            case Seal.MarkStrategyVulnerable(int ignored, var strategy) -> MediatorHelper.managerScan().highlight(urlByUser, strategy.toString());
            default -> {
                // empty
            }
        }
    }
}
