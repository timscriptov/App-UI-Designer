package com.mcal.uidesigner.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;

import com.mcal.uidesigner.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class AndroidHelper {
    private static Locale defaultLocale;
    private static Boolean isAndroidTV;

    public static void switchLanguage(AppCompatActivity activity, String langCode) {
        Locale locale;
        if (langCode == null || "default".equals(langCode)) {
            locale = defaultLocale;
            defaultLocale = null;
        } else {
            locale = new Locale(langCode);
            defaultLocale = Locale.getDefault();
        }
        if (locale != null) {
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        }
    }

    public static boolean isMaterialTheme(Context context) {
        if (Build.VERSION.SDK_INT >= 20) {
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(R.attr.theme_name, tv, true)) {
                return "Material".equals(tv.string);
            }
        }
        return false;
    }

    public static boolean isOuyaEdition() {
        return false;
    }

    @TargetApi(21)
    public static void setAndroidTVPadding(AppCompatActivity activity) {
        if (isAndroidTV(activity)) {
            float density = activity.getResources().getDisplayMetrics().density;
            activity.getSupportActionBar().setElevation(3.0f * density);
            View av = activity.getWindow().getDecorView().findViewById(activity.getResources().getIdentifier("action_bar_container", "id", "android"));
            if (av != null) {
                av.setPadding((int) (48.0f * density), (int) (27.0f * density), (int) (48.0f * density), 0);
            }
        }
    }

    public static void setAndroidTVPadding(View contentView) {
        setAndroidTVPadding(contentView, false);
    }

    @TargetApi(21)
    public static void setAndroidTVPadding(@NonNull View contentView, boolean bottomPadding) {
        int i;
        if (isAndroidTV(contentView.getContext())) {
            AppCompatActivity activity = (AppCompatActivity) contentView.getContext();
            float density = activity.getResources().getDisplayMetrics().density;
            activity.getSupportActionBar().setElevation(3.0f * density);
            int i2 = (int) (48.0f * density);
            int i3 = (int) (48.0f * density);
            if (bottomPadding) {
                i = (int) (27.0f * density);
            } else {
                i = 0;
            }
            contentView.setPadding(i2, 0, i3, i);
            View av = activity.getWindow().getDecorView().findViewById(activity.getResources().getIdentifier("action_bar_container", "id", "android"));
            if (av != null) {
                av.setPadding((int) (48.0f * density), (int) (27.0f * density), (int) (48.0f * density), 0);
            }
        }
    }

    public static void makeToolbarFocusable(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            View tv = activity.findViewById(activity.getResources().getIdentifier("action_bar", "id", "android"));
            if (tv instanceof ViewGroup) {
                ((ViewGroup) tv).setTouchscreenBlocksFocus(false);
            }
            View av = activity.findViewById(activity.getResources().getIdentifier("action_bar_container", "id", "android"));
            if (av instanceof ViewGroup) {
                ((ViewGroup) av).setTouchscreenBlocksFocus(false);
            }
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean isAndroidTV(Context context) {
        boolean z = false;
        if (isAndroidTV == null) {
            isAndroidTV = false;
            if (Build.VERSION.SDK_INT >= 21) {
                if (((UiModeManager) context.getSystemService("uimode")).getCurrentModeType() == 4) {
                    z = true;
                }
                isAndroidTV = Boolean.valueOf(z);
            }
        }
        return isAndroidTV.booleanValue();
    }

    public static boolean hasHardwareKeyboard(@NonNull Context context) {
        return context.getResources().getConfiguration().keyboard == 2;
    }

    public static boolean hasTouchScreen(Context context) {
        return !isOuyaEdition() && !isAndroidTV(context);
    }

    public static boolean isMouseButtonEvent(@NonNull MotionEvent event) {
        if (event.getToolType(0) == 3) {
            return true;
        }
        switch (event.getSource()) {
            case 8194:
            case 1048584:
                return true;
            default:
                return false;
        }
    }

    public static void setActionBarEmbeddedTabs(AppCompatActivity activity, boolean enabled) {
        try {
            Method setHasEmbeddedTabsMethod = activity.getSupportActionBar().getClass().getDeclaredMethod("setHasEmbeddedTabs", Boolean.TYPE);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(activity.getSupportActionBar(), Boolean.valueOf(enabled));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setActionBarReplacementPopup(final AppCompatActivity activity) {
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("PrivateApi")
            @Override
            public void run() {
                try {
                    final ActionBar actionBar = activity.getSupportActionBar();
                    final AppCompatSpinner spinner = (AppCompatSpinner) AndroidHelper.findViewOf(activity.findViewById(activity.getResources().getIdentifier("action_bar_container", "id", "android")), AppCompatSpinner.class);
                    if (spinner != null) {
                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupMenu popup = new PopupMenu(activity, spinner);
                                Menu menu = popup.getMenu();
                                for (int i = 0; i < actionBar.getTabCount(); i++) {
                                    menu.add(0, i, 0, actionBar.getTabAt(i).getText());
                                }
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        actionBar.selectTab(actionBar.getTabAt(item.getItemId()));
                                        return true;
                                    }
                                });
                                popup.show();
                            }
                        };
                        Method method_GetListenerInfo = View.class.getDeclaredMethod("getListenerInfo", new Class[0]);
                        method_GetListenerInfo.setAccessible(true);
                        Class.forName("android.view.View$ListenerInfo").getDeclaredField("mOnClickListener").set(method_GetListenerInfo.invoke(spinner, new Object[0]), onClickListener);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, 200);
    }

    @Nullable
    public static <T extends View> T findViewOf(@NonNull View v, Class<T> c) {
        if (v.getClass() == c) {
            return (T) v;
        }
        if (v instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                T t = (T) findViewOf(viewGroup.getChildAt(i), c);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    public static void forceOptionsMenuButton(AppCompatActivity activity) {
        if (getScreenSizeDip(activity) >= 540.0f) {
            try {
                ViewConfiguration config = ViewConfiguration.get(activity);
                @SuppressLint("PrivateApi") Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static float getScreenSizeDip(Context context) {
        return Math.min(getScreenHeightDip(context), getScreenWidthDip(context));
    }

    public static float getScreenSizeMaxDip(Context context) {
        return Math.max(getScreenHeightDip(context), getScreenWidthDip(context));
    }

    public static int getScreenSizePX(@NonNull Context context) {
        @SuppressLint("WrongConstant") WindowManager wm = (WindowManager) context.getSystemService("window");
        return Math.min(wm.getDefaultDisplay().getHeight(), wm.getDefaultDisplay().getWidth());
    }

    public static boolean isLandscape(Context context) {
        return getScreenHeightDip(context) < getScreenWidthDip(context);
    }

    public static float getWindowHeightDip(Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        float density = activity.getResources().getDisplayMetrics().density;
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        return ((float) (outRect.bottom - outRect.top)) / density;
    }

    @SuppressLint("WrongConstant")
    public static float getScreenHeightDip(@NonNull Context context) {
        return ((float) ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight()) / context.getResources().getDisplayMetrics().density;
    }

    @SuppressLint("WrongConstant")
    public static float getScreenWidthDip(@NonNull Context context) {
        return ((float) ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth()) / context.getResources().getDisplayMetrics().density;
    }

    public static int correctCodeFontSize(Context context, int size) {
        if (getScreenSizeDip(context) >= 400.0f) {
            return (int) (((float) size) * 1.3f);
        }
        return size;
    }

    public static void correctCodeFontSize(@NonNull AppCompatTextView textView) {
        textView.setTextSize(((float) correctCodeFontSize(textView.getContext(), (int) textView.getTextSize())) / textView.getContext().getResources().getDisplayMetrics().scaledDensity);
    }

    public static int getTrainerCodeFontSize(Context context) {
        if (getScreenSizeDip(context) > 720.0f) {
            return 21;
        }
        if (getScreenSizeDip(context) >= 400.0f) {
            return 18;
        }
        return 14;
    }

    public static float getTrainerTextFontSize(Context context) {
        if (getScreenSizeDip(context) > 720.0f) {
            return 20.0f;
        }
        if (getScreenSizeDip(context) >= 400.0f) {
            return 18.0f;
        }
        return 15.0f;
    }

    public static float getTrainerButtonFontSize(Context context) {
        if (getScreenSizeDip(context) > 720.0f) {
            return 25.0f;
        }
        if (getScreenSizeDip(context) >= 400.0f) {
            return 22.0f;
        }
        return 20.0f;
    }

    public static float getTrainerIconFontSize(Context context) {
        if (getScreenSizeDip(context) > 720.0f) {
            return 35.0f;
        }
        if (getScreenSizeDip(context) >= 400.0f) {
            return 30.0f;
        }
        return 25.0f;
    }

    public static float getTrainerHeaderFontSize(Context context) {
        if (getScreenSizeDip(context) > 720.0f) {
            return 25.0f;
        }
        if (getScreenSizeDip(context) >= 400.0f) {
            return 22.0f;
        }
        return 20.0f;
    }

    public static float getSubscriptionSubTitleFontSize(Context context) {
        if (getScreenSizeDip(context) > 720.0f) {
            return 15.0f;
        }
        if (getScreenSizeDip(context) >= 400.0f) {
            return 12.0f;
        }
        return 10.0f;
    }

    public static int obtainImageResourceId(@NonNull Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        int iconId = ta.getResourceId(0, 0);
        ta.recycle();
        return iconId;
    }

    @NonNull
    public static Drawable obtainTintedDrawable(@NonNull Context context, int attr, int colorAttr) {
        Drawable dr = context.getResources().getDrawable(obtainImageResourceId(context, attr));
        dr.setColorFilter(obtainColor(context, colorAttr), PorterDuff.Mode.SRC_IN);
        return dr;
    }

    public static int obtainColor(@NonNull Context context, int attr) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attr});
        int color = a.getColor(0, -1);
        a.recycle();
        return color;
    }
}
