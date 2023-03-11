package com.mcal.uidesigner.proxy;

import android.annotation.SuppressLint;
import android.widget.GridLayout;

import java.lang.reflect.Field;

public class ProxyGridLayoutParams {
    private final GridLayout.LayoutParams params;
    private int column = 0;
    private int row = 0;
    private int columnSpan = 1;
    private int rowSpan = 1;
    private int gravity = 0;

    public ProxyGridLayoutParams(Object params) {
        this.params = (GridLayout.LayoutParams) params;
        updateSpecs();
    }

    private static GridLayout.Alignment getAlignment(int gravity, boolean horizontal) {
        switch ((gravity & (horizontal ? 7 : 112)) >> (horizontal ? 0 : 4)) {
            case 1:
                return GridLayout.CENTER;
            case 2:
            case 4:
            case 6:
            default:
                try {
                    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi"}) Field field = GridLayout.class.getDeclaredField("UNDEFINED_ALIGNMENT");
                    field.setAccessible(true);
                    return (GridLayout.Alignment) field.get(null);
                } catch (Throwable th) {
                    return GridLayout.CENTER;
                }
            case 3:
                return horizontal ? GridLayout.LEFT : GridLayout.TOP;
            case 5:
                return horizontal ? GridLayout.RIGHT : GridLayout.BOTTOM;
            case 7:
                return GridLayout.FILL;
        }
    }

    public void setColumn(int column) {
        this.column = column;
        updateSpecs();
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
        updateSpecs();
    }

    public void setRow(int row) {
        this.row = row;
        updateSpecs();
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
        updateSpecs();
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        updateSpecs();
    }

    private void updateSpecs() {
        this.params.columnSpec = GridLayout.spec(column, columnSpan, getAlignment(gravity, true));
        this.params.rowSpec = GridLayout.spec(row, rowSpan, getAlignment(gravity, false));
    }
}