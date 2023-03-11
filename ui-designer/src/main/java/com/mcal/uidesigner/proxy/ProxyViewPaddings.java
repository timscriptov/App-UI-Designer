package com.mcal.uidesigner.proxy;

import android.os.Build;
import android.view.View;

public class ProxyViewPaddings {
    private final View view;
    private int padding;
    private int paddingBottom;
    private int paddingEnd;
    private int paddingLeft;
    private int paddingRight;
    private int paddingStart;
    private int paddingTop;

    public ProxyViewPaddings(Object obj) {
        this.view = (View) obj;
    }

    public void setPadding(int size) {
        this.padding = size;
        updatePadding();
    }

    public void setPaddingLeft(int size) {
        this.paddingLeft = size;
        updatePadding();
    }

    public void setPaddingRight(int size) {
        this.paddingRight = size;
        updatePadding();
    }

    public void setPaddingTop(int size) {
        this.paddingTop = size;
        updatePadding();
    }

    public void setPaddingBottom(int size) {
        this.paddingBottom = size;
        updatePadding();
    }

    public void setPaddingStart(int size) {
        this.paddingStart = size;
        updatePadding();
    }

    public void setPaddingEnd(int size) {
        this.paddingEnd = size;
        updatePadding();
    }

    private void updatePadding() {
        if (this.padding > 0) {
            this.view.setPadding(this.padding, this.padding, this.padding, this.padding);
        } else if (this.paddingStart > 0 || this.paddingEnd > 0) {
            if (Build.VERSION.SDK_INT >= 17) {
                this.view.setPaddingRelative(this.paddingStart, this.paddingTop, this.paddingEnd, this.paddingBottom);
            }
        } else if (this.paddingLeft > 0 || this.paddingRight > 0 || this.paddingTop > 0 || this.paddingBottom > 0) {
            this.view.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
        }
    }
}