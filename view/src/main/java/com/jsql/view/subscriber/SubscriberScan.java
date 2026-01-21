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
        var urlByUser = MediatorHelper.model().getMediatorUtils().getConnectionUtil().getUrlByUser();
        switch (request) {
            case Request3.DatabaseIdentified(var url, var vendor) -> MediatorHelper.managerScan().highlight(url, vendor.toString());
            case Request3.SetVendor(var url, var vendor) -> MediatorHelper.managerScan().highlight(url, vendor.toString());
            case Request3.MarkStrategyVulnerable(var strategy) -> MediatorHelper.managerScan().highlight(urlByUser, strategy.toString());
            case Request3.MarkErrorVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            default -> {
                // empty
            }
        }
    }
}
