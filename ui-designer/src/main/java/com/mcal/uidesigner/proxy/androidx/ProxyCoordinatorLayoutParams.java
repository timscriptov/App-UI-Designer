package com.mcal.uidesigner.proxy.androidx;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ProxyCoordinatorLayoutParams {
    private final CoordinatorLayout.LayoutParams params;

    public ProxyCoordinatorLayoutParams(Object obj) {
        params = (CoordinatorLayout.LayoutParams) obj;
    }

    public void setAnchorId(int id) {
        params.setAnchorId(id);
    }

    public void setAnchorGravity(int anchorGravity) {
        params.anchorGravity = anchorGravity;
    }
}