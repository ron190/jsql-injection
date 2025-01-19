package com.jsql.view.swing.interaction;

import com.jsql.view.swing.util.MediatorHelper;

/**
 * Set result tab panel orientation according to locale when first
 * tab is inserted.
 */
public class CreateTabHelper {
    protected CreateTabHelper() {
        MediatorHelper.frame().getSplitNS().initializeSplitOrientation();
    }
}
