package com.jsql.view.swing.panel.address;

import com.jsql.model.injection.method.AbstractMethodInjection;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Util class to create GUI dynamically
 */
public record ModelAddressLine(
    String request,
    AbstractMethodInjection method,
    String i18n,
    AtomicReference<JRadioButton> radio,
    String placeholder,
    AtomicReference<JTextField> textfield
) {}