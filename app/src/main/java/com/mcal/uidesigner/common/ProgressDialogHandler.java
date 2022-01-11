package com.mcal.uidesigner.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;

public class ProgressDialogHandler {
    private final Activity activity;
    private final String message;
    private ProgressDialog progressDialog;
    private Runnable showProgressAction;
    private final Handler handler = new Handler();
    private final int delay = 500;

    public ProgressDialogHandler(Activity activity, String message) {
        this.activity = activity;
        this.message = message;
    }

    public void openDialogDelayed() {
        closeDialog();
        this.showProgressAction = new Runnable() {
            @Override
            public void run() {
                ProgressDialogHandler.this.showDialog();
            }
        };
        this.handler.postDelayed(this.showProgressAction, (long) this.delay);
    }

    public void closeDialog() {
        if (this.showProgressAction != null) {
            this.handler.removeCallbacks(this.showProgressAction);
            this.showProgressAction = null;
        }
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }

    public void openDialog() {
        closeDialog();
        showDialog();
    }

    protected void onCancel() {
    }


    public void showDialog() {
        this.progressDialog = new ProgressDialog(this.activity);
        this.progressDialog.setMessage(this.message);
        this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ProgressDialogHandler.this.onCancel();
            }
        });
        this.progressDialog.getWindow().clearFlags(2);
        this.progressDialog.show();
    }
}
