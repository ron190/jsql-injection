package com.jsql.view.swing.panel.address;

import com.jsql.model.injection.method.AbstractMethodInjection;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Util class to create GUI dynamically
 */
public class ModelAddressLine {

    public final String request;
    public final Boolean isSelected;
    public final AbstractMethodInjection method;
    public final String i18nRadio;
    public final AtomicReference<JRadioButton> radio;
    public final String keyTooltipQuery;
    public final String placeholder;
    public final AtomicReference<JTextField> textfield;
    public final int offset;

    public ModelAddressLine(
        String request,
        Boolean isSelected,
        AbstractMethodInjection method,
        String i18nRadio,
        AtomicReference<JRadioButton> radio,
        String keyTooltipQuery,
        String placeholder,
        AtomicReference<JTextField> textfield,
        int offset
    ) {
        this.request = request;
        this.isSelected = isSelected;
        this.method = method;
        this.i18nRadio = i18nRadio;
        this.radio = radio;
        this.keyTooltipQuery = keyTooltipQuery;
        this.placeholder = placeholder;
        this.textfield = textfield;
        this.offset = offset;
    }
}