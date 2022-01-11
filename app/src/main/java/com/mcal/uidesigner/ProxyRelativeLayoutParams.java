package com.mcal.uidesigner;

import android.widget.RelativeLayout;

public class ProxyRelativeLayoutParams {
    private final RelativeLayout.LayoutParams params;

    public ProxyRelativeLayoutParams(Object obj) {
        this.params = (RelativeLayout.LayoutParams) obj;
    }

    public void setBelow(int id) {
        this.params.addRule(3, id);
    }

    public void setAlignLeft(int id) {
        this.params.addRule(5, id);
    }

    public void setAlignRight(int id) {
        this.params.addRule(7, id);
    }

    public void setAlignStart(int id) {
        this.params.addRule(18, id);
    }

    public void setAlignEnd(int id) {
        this.params.addRule(19, id);
    }

    public void setAlignTop(int id) {
        this.params.addRule(6, id);
    }

    public void setAlignBottom(int id) {
        this.params.addRule(8, id);
    }

    public void setAlignBaseline(int id) {
        this.params.addRule(4, id);
    }

    public void setEndOf(int id) {
        this.params.addRule(17, id);
    }

    public void setStartOf(int id) {
        this.params.addRule(16, id);
    }

    public void setLeftOf(int id) {
        this.params.addRule(0, id);
    }

    public void setRightOf(int id) {
        this.params.addRule(1, id);
    }

    public void setAbove(int id) {
        this.params.addRule(2, id);
    }

    public void setAlignParentBottom(boolean b) {
        if (b) {
            this.params.addRule(12);
        }
    }

    public void setAlignParentTop(boolean b) {
        if (b) {
            this.params.addRule(10);
        }
    }

    public void setAlignParentLeft(boolean b) {
        if (b) {
            this.params.addRule(9);
        }
    }

    public void setAlignParentRight(boolean b) {
        if (b) {
            this.params.addRule(11);
        }
    }

    public void setAlignParentStart(boolean b) {
        if (b) {
            this.params.addRule(20);
        }
    }

    public void setAlignParentEnd(boolean b) {
        if (b) {
            this.params.addRule(21);
        }
    }

    public void setCenterInParent(boolean b) {
        if (b) {
            this.params.addRule(13);
        }
    }

    public void setCenterVertical(boolean b) {
        if (b) {
            this.params.addRule(15);
        }
    }

    public void setCenterHorizontal(boolean b) {
        if (b) {
            this.params.addRule(14);
        }
    }
}
