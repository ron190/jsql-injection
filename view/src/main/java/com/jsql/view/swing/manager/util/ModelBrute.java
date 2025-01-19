package com.jsql.view.swing.manager.util;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class ModelBrute {
    public final AtomicReference<JCheckBox> checkbox;
    public final String text;
    public final String i18nTooltip;

    public ModelBrute(AtomicReference<JCheckBox> checkbox, String text, String i18nTooltip) {
        this.checkbox = checkbox;
        this.text = text;
        this.i18nTooltip = i18nTooltip;
    }
}
