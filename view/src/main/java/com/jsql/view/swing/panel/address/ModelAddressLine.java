package com.jsql.view.swing.panel.address;

import com.jsql.model.injection.method.AbstractMethodInjection;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Util class to create GUI dynamically
 */
public class ModelAddressLine {

    public final String request;
    public final AbstractMethodInjection method;
    public final String i18n;
    public final AtomicReference<JRadioButton> radio;
    public final String placeholder;
    public final AtomicReference<JTextField> textfield;

    public ModelAddressLine(
        String request,
        AbstractMethodInjection method,
        String i18n,
        AtomicReference<JRadioButton> radio,
        String placeholder,
        AtomicReference<JTextField> textfield
    ) {
        this.request = request;
        this.method = method;
        this.i18n = i18n;
        this.radio = radio;
        this.placeholder = placeholder;
        this.textfield = textfield;
    }
}