package com.mcal.uidesigner.proxy.material;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

import com.google.android.material.button.MaterialButton;

public class ProxyMaterialButton {
    private final MaterialButton materialButton;

    public static final int ICONGRAVITY_start = 1;
    public static final int ICONGRAVITY_text_start = 2;
    public static final int ICONGRAVITY_end = 3;
    public static final int ICONGRAVITY_text_end = 4;
    public static final int ICONGRAVITY_top = 16;
    public static final int ICONGRAVITY_text_top = 32;

    public static final int TINTMODE_add = 0;
    public static final int TINTMODE_multiply = 1;
    public static final int TINTMODE_screen = 2;
    public static final int TINTMODE_src_atop = 3;
    public static final int TINTMODE_src_in = 4;
    public static final int TINTMODE_src_over = 5;

    private int tintColor = 0;
    private int tintMode = 0;

    public ProxyMaterialButton(Object obj) {
        materialButton = (MaterialButton) obj;
    }

    public void setBackgroundTint(int color) {
        materialButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void setBackgroundTintMode(String mode) {
        materialButton.setBackgroundTintMode(PorterDuff.Mode.valueOf(mode));
    }

    public void setIconGravity(int iconGravity) {
        materialButton.setIconGravity(iconGravity);
    }

    public void setIconTint(int color) {
        tintColor = color;
        updateButtonTint();
    }

    public void setIconTintMode(int mode) {
        tintMode = mode;
        updateButtonTint();
    }

    public void setRippleColor(int color) {
        materialButton.setRippleColor(ColorStateList.valueOf(color));
    }

    public void setStrokeWidth(int size) {
        materialButton.setStrokeWidth(size);
    }

    public void setStrokeColor(int color) {
        materialButton.setStrokeColor(ColorStateList.valueOf(color));
    }

    private void updateButtonTint() {
        PorterDuff.Mode mode;
        switch (tintMode) {
            case TINTMODE_add:
                mode = PorterDuff.Mode.ADD;
                break;
            case TINTMODE_multiply:
                mode = PorterDuff.Mode.MULTIPLY;
                break;
            case TINTMODE_screen:
                mode = PorterDuff.Mode.SCREEN;
                break;
            case TINTMODE_src_atop:
                mode = PorterDuff.Mode.SRC_ATOP;
                break;
            case TINTMODE_src_in:
                mode = PorterDuff.Mode.SRC_IN;
                break;
            case TINTMODE_src_over:
                mode = PorterDuff.Mode.SRC_OVER;
                break;
            default:
                mode = null;
                break;
        }
        if (mode != null && tintColor != 0) {
            materialButton.setIconTint(ColorStateList.valueOf(tintColor));
            materialButton.setIconTintMode(mode);
        }
    }
}
