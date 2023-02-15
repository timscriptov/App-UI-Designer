package com.mcal.uidesigner.common;

import android.util.Log;

import androidx.annotation.NonNull;

public class AppLog {
    public static void d(String msg) {
        Log.d("Designer", msg);
    }

    public static void d(Object obj) {
        Log.d("Designer", obj == null ? "null" : obj.toString());
    }

    public static void d(int i) {
        Log.d("Designer", i + "");
    }

    public static void s(@NonNull Object obj, String method) {
        Log.i("Designer", obj.getClass().getName() + "." + method);
    }

    public static void s(String state) {
        Log.i("Designer", state);
    }

    public static void e(Throwable t) {
        Log.e("Designer", t.toString(), t);
    }

    public static void e(String msg) {
        Log.e("Designer", msg);
    }

    public static void e(String msg, Throwable t) {
        Log.e("Designer", msg, t);
    }

    public static void w(String msg) {
        Log.w("Designer", msg);
    }

    public static void crash(Throwable t) {
        Log.e("Designer", t.toString(), t);
    }
}
