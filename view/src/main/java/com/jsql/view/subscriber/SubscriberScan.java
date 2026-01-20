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
            case Request3.DatabaseIdentified r -> MediatorHelper.managerScan().highlight(r.url(), r.vendor().toString());
            case Request3.MarkBlindBinVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.MarkBlindBitVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.MarkDnsVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.MarkErrorVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.MarkMultibitVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.MarkStackVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.MarkTimeVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.MarkUnionVulnerable r -> MediatorHelper.managerScan().highlight(urlByUser, r.strategy().toString());
            case Request3.SetVendor r -> MediatorHelper.managerScan().highlight(r.url(), r.vendor().toString());
            default -> {}
        }
    }
}
