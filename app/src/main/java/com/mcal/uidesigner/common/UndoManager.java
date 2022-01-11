package com.mcal.uidesigner.common;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class UndoManager {
    private final Stack<Change> contents = new Stack<>();
    private final Stack<Change> redoContents = new Stack<>();
    private final List<UndoRedoListener> listeners = new ArrayList<>();

    public void addListener(UndoRedoListener l) {
        this.listeners.add(l);
    }

    public void removeListener(UndoRedoListener l) {
        this.listeners.remove(l);
    }

    public void addBaseVersion(String filepath, String content, int change) {
        if (filepath != null && !content.equals(getContent(filepath))) {
            this.redoContents.clear();
            this.contents.push(new Change(filepath, content, change));
            fireUndoRedoStateChange();
        }
    }

    public void addVersion(String filepath, String content, int change) {
        if (filepath != null) {
            this.redoContents.clear();
            this.contents.push(new Change(filepath, content, change));
            fireUndoRedoStateChange();
        }
    }

    public void load(Bundle bundle) {
        this.contents.clear();
        List<Change> undo = bundle.getParcelableArrayList("undo");
        if (undo != null) {
            this.contents.addAll(undo);
        }
        this.redoContents.clear();
        ArrayList<Change> redo = bundle.getParcelableArrayList("redo");
        if (redo != null) {
            this.redoContents.addAll(redo);
        }
    }

    public void save(Bundle bundle) {
        bundle.putParcelableArrayList("undo", new ArrayList<>(this.contents));
        bundle.putParcelableArrayList("redo", new ArrayList<>(this.redoContents));
    }

    public boolean canUndo() {
        Set<String> paths = new HashSet<>();
        for (int i = this.contents.size() - 1; i >= 0; i--) {
            String filepath = this.contents.get(i).filepath;
            if (paths.contains(filepath)) {
                return true;
            }
            paths.add(filepath);
        }
        return false;
    }

    public boolean canRedo() {
        return this.redoContents.size() > 0;
    }

    public void redo() {
        Change change = this.redoContents.pop();
        this.contents.push(change);
        fireUndoRedo(change);
    }

    public void undo() {
        Change change = this.contents.pop();
        this.redoContents.push(change);
        fireUndoRedo(change);
    }

    private String getContent(String filepath) {
        for (int i = this.contents.size() - 1; i >= 0; i--) {
            if (this.contents.get(i).filepath.equals(filepath)) {
                return this.contents.get(i).content;
            }
        }
        return "";
    }

    private void fireUndoRedoStateChange() {
        for (UndoRedoListener l : this.listeners) {
            l.undoRedoStateChanged();
        }
    }

    private void fireUndoRedo(Change change) {
        for (UndoRedoListener l : this.listeners) {
            l.undoRedoStateChanged();
            l.revertToVersion(change.filepath, getContent(change.filepath), change.change);
        }
    }

    public interface UndoRedoListener {
        void revertToVersion(String str, String str2, int i);

        void undoRedoStateChanged();
    }

    public static class Change implements Parcelable {
        public static final Parcelable.Creator<Change> CREATOR = new Parcelable.Creator<Change>() {

            @Override
            public Change createFromParcel(Parcel in) {
                return new Change(in);
            }


            @Override
            public Change[] newArray(int size) {
                return new Change[size];
            }
        };
        public int change;
        public String content;
        public String filepath;

        public Change(String filepath, String content, int change) {
            this.filepath = filepath;
            this.content = content;
            this.change = change;
        }

        public Change(Parcel in) {
            this(in.readString(), in.readString(), in.readInt());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.filepath);
            dest.writeString(this.content);
            dest.writeInt(this.change);
        }
    }
}
