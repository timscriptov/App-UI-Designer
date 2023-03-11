package com.mcal.uidesigner.proxy.material;

import android.content.res.ColorStateList;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

public class ProxyTextInputLayout {
    private final TextInputLayout textInputLayout;

    public ProxyTextInputLayout(Object obj) {
        this.textInputLayout = (TextInputLayout) obj;
    }

    public void setBoxCornerRadiusTopStart(int cornerRadius) {
        setBoxCornerRadius(0, cornerRadius);
    }

    public void setBoxCornerRadiusTopEnd(int cornerRadius) {
        setBoxCornerRadius(1, cornerRadius);
    }

    public void setBoxCornerRadiusBottomStart(int cornerRadius) {
        setBoxCornerRadius(2, cornerRadius);
    }

    public void setBoxCornerRadiusBottomEnd(int cornerRadius) {
        setBoxCornerRadius(3, cornerRadius);
    }

    public void setDefaultHintTextColor(int hintTextColor) {
        textInputLayout.setDefaultHintTextColor(ColorStateList.valueOf(hintTextColor));
    }

    public void setHintTextColor(int hintTextColor) {
        textInputLayout.setHintTextColor(ColorStateList.valueOf(hintTextColor));
    }

    public void setBoxCornerRadius(int side, int cornerRadius) {
        float[] radii = getBoxCornerRadii(this.textInputLayout);
        radii[side] = cornerRadius;
        textInputLayout.setBoxCornerRadii(radii[0], radii[1], radii[2], radii[3]);
    }

    @NonNull
    private float[] getBoxCornerRadii(@NonNull TextInputLayout layout) {
        float[] radii = new float[4];
        radii[0] = layout.getBoxCornerRadiusTopStart();
        radii[1] = layout.getBoxCornerRadiusTopEnd();
        radii[2] = layout.getBoxCornerRadiusBottomStart();
        radii[3] = layout.getBoxCornerRadiusBottomEnd();
        return radii;
    }
}
