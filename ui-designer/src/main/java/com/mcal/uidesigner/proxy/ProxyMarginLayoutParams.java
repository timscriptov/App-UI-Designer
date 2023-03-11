package com.mcal.uidesigner.proxy;

import android.view.ViewGroup;

public class ProxyMarginLayoutParams {
    private final ViewGroup.MarginLayoutParams params;

    public ProxyMarginLayoutParams(Object obj) {
        params = (ViewGroup.MarginLayoutParams) obj;
    }

    public void setMargin(int size) {
        params.setMargins(size, size, size, size);
    }
}