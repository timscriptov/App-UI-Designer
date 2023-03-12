package com.mcal.uidesigner.proxy;

import android.widget.ScrollView;

public class ProxyScrollView {
    private final ScrollView scrollView;

    public ProxyScrollView(Object obj) {
        scrollView = (ScrollView) obj;
    }

    public void setScrollBars(String value) {
        if ("none".equals(value)) {
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setVerticalScrollBarEnabled(false);
        } else if ("horizontal".equals(value)) {
            scrollView.setHorizontalScrollBarEnabled(true);
            scrollView.setVerticalScrollBarEnabled(false);
        } else if ("vertical".equals(value)) {
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setVerticalScrollBarEnabled(true);
        } else {
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setVerticalScrollBarEnabled(false);
        }
    }
}