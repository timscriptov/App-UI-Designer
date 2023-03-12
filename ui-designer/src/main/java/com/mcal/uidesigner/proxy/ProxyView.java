package com.mcal.uidesigner.proxy;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.View;

public class ProxyView {
    private int padding;
    private int paddingBottom;
    private int paddingEnd;
    private int paddingLeft;
    private int paddingRight;
    private int paddingStart;
    private int paddingTop;
    private final View view;

    public ProxyView(Object obj) {
        view = (View) obj;
    }

    public void setBackgroundTint(int color) {
        view.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void setBackgroundTintMode(String mode) {
        view.setBackgroundTintMode(PorterDuff.Mode.valueOf(mode));
    }

    public void setPadding(int size) {
        padding = size;
        updatePadding();
    }

    public void setPaddingLeft(int size) {
        paddingLeft = size;
        updatePadding();
    }

    public void setPaddingRight(int size) {
        paddingRight = size;
        updatePadding();
    }

    public void setPaddingTop(int size) {
        paddingTop = size;
        updatePadding();
    }

    public void setPaddingBottom(int size) {
        paddingBottom = size;
        updatePadding();
    }

    public void setPaddingStart(int size) {
        paddingStart = size;
        updatePadding();
    }

    public void setPaddingEnd(int size) {
        paddingEnd = size;
        updatePadding();
    }

    private void updatePadding() {
        if (padding > 0) {
            view.setPadding(padding, padding, padding, padding);
        } else if (paddingStart > 0 || paddingEnd > 0) {
            view.setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom);
        } else if (paddingLeft > 0 || paddingRight > 0 || paddingTop > 0 || paddingBottom > 0) {
            view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }
}