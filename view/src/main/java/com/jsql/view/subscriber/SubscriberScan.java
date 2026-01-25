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

import com.jsql.model.bean.util.Request3;
import com.jsql.view.swing.util.MediatorHelper;

public class SubscriberScan extends AbstractSubscriber {
    @Override
    protected void execute(Request3 request) {
        var urlByUser = MediatorHelper.model().getMediatorUtils().connectionUtil().getUrlByUser();
        switch (request) {
            case Request3.MarkEngineFound(var engine) -> MediatorHelper.managerScan().highlight(urlByUser, engine.toString());
            case Request3.ActivateEngine(var engine) -> MediatorHelper.managerScan().highlight(urlByUser, engine.toString());
            case Request3.MarkVulnerable(var strategy) -> MediatorHelper.managerScan().highlight(urlByUser, strategy.toString());
            case Request3.MarkErrorVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            default -> {
                // empty
            }
        }
    }
}
