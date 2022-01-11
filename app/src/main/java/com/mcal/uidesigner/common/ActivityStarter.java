package com.mcal.uidesigner.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

public class ActivityStarter {
    public static final String EXTRA_NAVIGATE_COLUMN = "NavigateColumn";
    public static final String EXTRA_NAVIGATE_FILE = "NavigateFile";
    public static final String EXTRA_NAVIGATE_LINE = "NavigateLine";

    @SuppressLint("WrongConstant")
    public static void navigateTo(Activity caller, String filepath, int line, int column) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(caller, "com.mcal.ui.MainActivity"));
        intent.putExtra(EXTRA_NAVIGATE_FILE, filepath);
        intent.putExtra(EXTRA_NAVIGATE_LINE, line);
        intent.putExtra(EXTRA_NAVIGATE_COLUMN, column);
        intent.addFlags(67108864);
        caller.startActivity(intent);
    }
}
