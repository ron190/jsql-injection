package com.jsql.view.swing.manager.util;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class ModelSpinner {
    public final int value;
    public final AtomicReference<JSpinner> spinner;
    public final String i18n;

    public ModelSpinner(int value, AtomicReference<JSpinner> spinner, String i18n) {
        this.value = value;
        this.spinner = spinner;
        this.i18n = i18n;
    }
}