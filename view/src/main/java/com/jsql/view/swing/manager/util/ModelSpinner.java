package com.jsql.view.swing.manager.util;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public record ModelSpinner(
    int value,
    AtomicReference<JSpinner> spinner,
    String label,
    String i18n
) {}