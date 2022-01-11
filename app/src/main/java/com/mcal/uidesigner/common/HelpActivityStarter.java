package com.mcal.uidesigner.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

public class HelpActivityStarter {
    public static final String EXTRA_CAT = "EXTRA_CAT";
    public static final String EXTRA_REFERENCE_URL = "EXTRA_URL";

    @SuppressLint("WrongConstant")
    public static void showHelp(Activity caller, String referenceUrl, String category) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(caller, "com.mcal.ui.activities.HelpViewActivity"));
        intent.putExtra(EXTRA_REFERENCE_URL, referenceUrl);
        intent.putExtra(EXTRA_CAT, category);
        intent.setFlags(268435456);
        caller.startActivity(intent);
    }
}
