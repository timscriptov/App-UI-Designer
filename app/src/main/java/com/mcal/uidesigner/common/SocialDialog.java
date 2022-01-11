package com.mcal.uidesigner.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mcal.designer.BuildConfig;
import com.mcal.designer.R;

import java.util.ArrayList;
import java.util.List;

public class SocialDialog extends MessageBox {
    private final Runnable done;
    private final List<SocialListEntry> entries1;
    private final List<SocialListEntry> entries2;
    private final String message;
    private final String title;
    private boolean isExpanded;

    private SocialDialog(String title, String message, List<SocialListEntry> entries1, List<SocialListEntry> entries2, Runnable done) {
        this.title = title;
        this.message = message;
        this.done = done;
        this.entries1 = entries1;
        this.entries2 = entries2;
    }

    public static void showCommunityDialog(final Activity activity) {
        List<SocialListEntry> entries1 = new ArrayList<>();
        entries1.add(new SocialListEntry(R.drawable.round_email_24, "Google+", new Runnable() {
            @Override
            public void run() {
                Social.openGooglePlusPage(activity, "101304250883271700981");
            }
        }));
        entries1.add(new SocialListEntry(R.drawable.round_email_24, "Twitter", new Runnable() {
            @Override
            public void run() {
                Social.openTwitterPage(activity, "AndroidIDE");
            }
        }));
        entries1.add(new SocialListEntry(R.drawable.round_email_24, "Facebook", new Runnable() {
            @Override
            public void run() {
                Social.openFacebookPage(activity, "239564276138537");
            }
        }));
        List<SocialListEntry> entries2 = new ArrayList<>(entries1);
        entries2.add(new SocialListEntry(R.drawable.round_email_24, "G+ Community", new Runnable() {
            @Override
            public void run() {
                SocialDialog.openGooglePlusCommunity(activity);
            }
        }));
        entries2.add(new SocialListEntry(R.drawable.round_email_24, "Google Group", new Runnable() {
            @Override
            public void run() {
                Social.openGoogleGroup(activity, "android-ide");
            }
        }));
        entries2.add(new SocialListEntry(R.drawable.round_email_24, "Email", new Runnable() {
            @Override
            public void run() {
                Social.sendEmail(activity, "support@appfour.com", "App UI Designer Feedback", "With App UI Designer " + getVersionString() + " " + getOnMyDeviceString() + " (SDK " + Build.VERSION.SDK_INT + ")...\n\n[Write text here]");
            }

            private String getVersionString() {
                try {
                    return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    AppLog.e(e);
                    return "(version unknown)";
                }
            }

            private String getOnMyDeviceString() {
                String model = Build.MODEL;
                if (model == null || model.length() > 40) {
                    return "";
                }
                return "on my " + model;
            }
        }));
        if (!AndroidHelper.isOuyaEdition()) {
            entries2.add(new SocialListEntry(R.drawable.round_shop_24, activity.getResources().getString(R.string.dialog_community_rate), new Runnable() {
                @Override
                public void run() {
                    Social.openPlayPage(activity, BuildConfig.APPLICATION_ID, "community");
                }
            }));
        }
        entries1.add(new SocialListEntry(0, activity.getResources().getString(R.string.dialog_community_more)));
        MessageBox.showDialog(activity, new SocialDialog(activity.getResources().getString(R.string.dialog_community_title), activity.getResources().getString(R.string.dialog_community_message), entries1, entries2, null));
    }

    public static void openGooglePlusCommunity(Activity activity) {
        if (AndroidHelper.isOuyaEdition()) {
            Social.openGooglePlusPage(activity, "101304250883271700981");
        } else {
            Social.openGooglePlusCommunity(activity, "104927725094165066286");
        }
    }

    public static void showTrainerQuestionDialog(final Activity activity, String title, String message, Runnable skip) {
        List<SocialListEntry> entries = new ArrayList<>();
        entries.add(new SocialListEntry(R.drawable.round_email_24, activity.getResources().getString(R.string.dialog_community_ask), new Runnable() {
            @Override
            public void run() {
                SocialDialog.openGooglePlusCommunity(activity);
            }
        }));
        if (skip != null) {
            entries.add(new SocialListEntry(0, activity.getResources().getString(R.string.trainer_skip_lesson) + " ↷", skip, true));
        }
        entries.add(new SocialListEntry(0, activity.getResources().getString(R.string.dialog_community_continue) + " ≫"));
        MessageBox.showDialog(activity, new SocialDialog(title, message, entries, null, null));
    }

    public static void showRateDialog(final Activity activity, String title, String message, final String pakage, final String linkId, Runnable done) {
        List<SocialListEntry> entries = new ArrayList<>();
        entries.add(new SocialListEntry(R.drawable.round_shop_24, activity.getResources().getString(R.string.dialog_community_rate), new Runnable() {
            @Override
            public void run() {
                Social.openPlayPage(activity, pakage, linkId);
            }
        }));
        entries.add(new SocialListEntry(0, activity.getResources().getString(R.string.dialog_community_continue) + " ≫"));
        MessageBox.showDialog(activity, new SocialDialog(title, message, entries, null, done));
    }

    public static void showShareDialog(final Activity activity, String title, final String text, final String link, Runnable done) {
        List<SocialListEntry> entries = new ArrayList<>();
        entries.add(new SocialListEntry(R.drawable.round_email_24, "Google+", new Runnable() {
            @Override
            public void run() {
                Social.shareWithGooglePlus(activity, text, link);
            }
        }));
        entries.add(new SocialListEntry(R.drawable.round_email_24, "Twitter", new Runnable() {
            @Override
            public void run() {
                Social.shareWithTwitter(activity, text, link);
            }
        }));
        entries.add(new SocialListEntry(R.drawable.round_email_24, "Facebook", new Runnable() {
            @Override
            public void run() {
                Social.shareWithFacebook(activity, text, link);
            }
        }));
        entries.add(new SocialListEntry(0, activity.getResources().getString(R.string.dialog_community_continue) + " ≫"));
        MessageBox.showDialog(activity, new SocialDialog(title, "\"" + text + "\"", entries, null, done));
    }

    @Override
    protected Dialog buildDialog(final Activity activity) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.share_dialog, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(layout).setCancelable(true).setTitle(this.title);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog2) {
                if (SocialDialog.this.done != null) {
                    SocialDialog.this.done.run();
                }
            }
        });
        ((TextView) layout.findViewById(R.id.shareDialogMessage)).setText(this.message);
        final ListView listView = (ListView) layout.findViewById(R.id.shareDialogList);
        listView.setAdapter((ListAdapter) new ShareEntryAdapter(activity, this.entries1));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SocialListEntry entry = (SocialListEntry) parent.getItemAtPosition(position);
                if (entry.dismiss) {
                    if (SocialDialog.this.entries2 == null || SocialDialog.this.isExpanded) {
                        dialog.dismiss();
                        if (SocialDialog.this.done != null) {
                            SocialDialog.this.done.run();
                        }
                    } else {
                        SocialDialog.this.isExpanded = true;
                        listView.setAdapter((ListAdapter) new ShareEntryAdapter(activity, SocialDialog.this.entries2));
                    }
                }
                if (entry.runnable != null) {
                    entry.runnable.run();
                }
            }
        });
        return dialog;
    }

    private static class SocialListEntry {
        private final boolean dismiss;
        private final int icon;
        private final String label;
        private final Runnable runnable;

        public SocialListEntry(int icon, String label) {
            this.icon = icon;
            this.label = label;
            this.runnable = null;
            this.dismiss = true;
        }

        public SocialListEntry(int icon, String label, Runnable runnable) {
            this.icon = icon;
            this.label = label;
            this.runnable = runnable;
            this.dismiss = false;
        }

        public SocialListEntry(int icon, String label, Runnable runnable, boolean dismiss) {
            this.icon = icon;
            this.label = label;
            this.runnable = runnable;
            this.dismiss = dismiss;
        }
    }

    private static class ShareEntryAdapter extends ArrayAdapter<SocialListEntry> {
        public ShareEntryAdapter(Context context, List<SocialListEntry> entries) {
            super(context, R.layout.share_dialog_entry, entries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int i = 0;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.share_dialog_entry, parent, false);
            }
            SocialListEntry entry = getItem(position);
            ((TextView) view.findViewById(R.id.shareDialogEntryText)).setText(entry.label);
            ImageView imageView = (ImageView) view.findViewById(R.id.shareDialogEntryImage);
            if (entry.icon != 0) {
                imageView.setImageResource(entry.icon);
            }
            if (entry.icon == 0) {
                i = 4;
            }
            imageView.setVisibility(i);
            return view;
        }
    }
}
