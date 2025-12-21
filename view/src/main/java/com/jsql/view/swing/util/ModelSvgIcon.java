package com.jsql.view.swing.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ModelSvgIcon {
    private final Color from;
    private final String toDarkUi;
    private final Color toDark;
    private final FlatSVGIcon icon;
    private String keyLabel;
    private String keyTooltip;

    ModelSvgIcon(String name, float scale) {
        this(name, Color.BLACK, "ComboBox.foreground", scale);
    }

    ModelSvgIcon(String name, int from) {
        this(name, new Color(from), "ComboBox.foreground", 0.02f);
    }

    ModelSvgIcon(String name, Color from, String toDark, float scale) {
        this(name, from, toDark, UIManager.getColor(toDark), scale);
    }

    ModelSvgIcon(String name, Color from, String toDarkUi, Color toDark, float scale) {
        this.from = from;
        this.toDarkUi = toDarkUi;
        this.toDark = toDark;
        this.icon = this.createSvgIcon(
            name,
            from,
            toDarkUi != null
            ? UIManager.getColor(toDarkUi)
            : toDark,
            scale
        );
    }

    public FlatSVGIcon createSvgIcon(String name, Color from, Color toDark, float scale) {
        return new FlatSVGIcon(Objects.requireNonNull(UiUtil.class.getClassLoader().getResource(String.format(
            name.endsWith(".svg") ? "%s" : "swing/images/icons/%s.svg",
            name
        ))))
        .setColorFilter(new FlatSVGIcon.ColorFilter().add(from, null, toDark))
        .derive(scale);
    }

    public void setColorFilter() {
        this.icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(
            this.from,
            null,
            this.toDarkUi != null
            ? UIManager.getColor(this.toDarkUi)
            : this.toDark
        ));
    }

    public ModelSvgIcon withTab(String keyLabel, String keyTooltip) {
        this.keyLabel = keyLabel;
        this.keyTooltip = keyTooltip;
        return this;
    }

    public FlatSVGIcon getIcon() {
        return this.icon;
    }

    public String getKeyLabel() {
        return this.keyLabel;
    }

    public String getKeyTooltip() {
        return this.keyTooltip;
    }
}