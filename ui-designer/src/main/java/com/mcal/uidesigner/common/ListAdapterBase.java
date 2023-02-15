package com.mcal.uidesigner.common;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class ListAdapterBase<T> extends BaseAdapter {
    private List<T> entries = new ArrayList<>();

    public void setEntries(List<T> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public T getEntry(int i) {
        return this.entries.get(i);
    }

    @Override
    public int getCount() {
        return this.entries.size();
    }

    @Override
    public Object getItem(int i) {
        return this.entries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }
}
