package com.jsql.view.swing.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SvgIcon {
    public final String name;
    public final Color from;
    public final String toDarkUi;
    public final Color toDark;
    public final float scale;
    public final FlatSVGIcon icon;

    SvgIcon(String name, float scale) {
        this(name, Color.BLACK, "ComboBox.foreground", scale);
    }

    SvgIcon(String name, int from, float scale) {
        this(name, new Color(from), "ComboBox.foreground", scale);
    }

    SvgIcon(String name, Color from, String toDark, float scale) {
        this(name, from, toDark, UIManager.getColor(toDark), scale);
    }

    SvgIcon(String name, Color from, String toDarkUi, Color toDark, float scale) {
        this.name = name;
        this.from = from;
        this.toDarkUi = toDarkUi;
        this.toDark = toDark;
        this.scale = scale;
        this.icon = createSvgIcon(
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
}