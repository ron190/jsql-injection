package com.jsql.view.swing.manager.util;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public record ModelBrute(
    AtomicReference<JCheckBox> checkbox,
    String text,
    String i18nTooltip
) {}
