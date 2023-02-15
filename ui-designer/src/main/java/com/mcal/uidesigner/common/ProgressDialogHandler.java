package com.mcal.uidesigner.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

public class ProgressDialogHandler {
    private final Activity activity;
    private final String message;
    private final Handler handler = new Handler();
    private final int delay = 500;
    private ProgressDialog progressDialog;
    private Runnable showProgressAction;

    public ProgressDialogHandler(Activity activity, String message) {
        this.activity = activity;
        this.message = message;
    }

    public void openDialogDelayed() {
        closeDialog();
        this.showProgressAction = () -> ProgressDialogHandler.this.showDialog();
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
        this.progressDialog.setOnCancelListener(dialog -> ProgressDialogHandler.this.onCancel());
        this.progressDialog.getWindow().clearFlags(2);
        this.progressDialog.show();
    }
}
