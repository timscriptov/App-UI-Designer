package com.mcal.uidesigner;

import android.view.ViewGroup;

public class ProxyMarginLayoutParams {
    private final ViewGroup.MarginLayoutParams params;

    public ProxyMarginLayoutParams(Object obj) {
        this.params = (ViewGroup.MarginLayoutParams) obj;
    }

    public void setMargin(int size) {
        this.params.setMargins(size, size, size, size);
    }
}
