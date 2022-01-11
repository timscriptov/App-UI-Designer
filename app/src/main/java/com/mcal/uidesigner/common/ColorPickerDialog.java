package com.mcal.uidesigner.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mcal.designer.R;
import com.mcal.uidesigner.view.ColorPickerView;

public class ColorPickerDialog extends MessageBox {
    private boolean allowsNone;
    private int newColor;
    private String newHexColor;
    private final ColorRunnable ok;
    private final String oldHexColor;
    private final String title;
    private boolean updatingColorPicker;
    private boolean updatingEditText;

    public ColorPickerDialog(String title, String hexColor, ColorRunnable ok) {
        if (hexColor == null) {
            this.oldHexColor = "#000000";
        } else {
            this.oldHexColor = hexColor;
        }
        this.newHexColor = this.oldHexColor;
        this.newColor = ColorPickerView.parseColor(this.newHexColor);
        this.ok = ok;
        this.allowsNone = true;
        this.title = title;
    }

    public ColorPickerDialog(String title, int color, ColorRunnable ok) {
        this.oldHexColor = ColorPickerView.toHexColor(color);
        this.newHexColor = this.oldHexColor;
        this.newColor = color;
        this.ok = ok;
        this.title = title;
    }

    @Override
    protected Dialog buildDialog(Activity activity) {
        View layout = LayoutInflater.from(activity).inflate(R.layout.colorpicker, (ViewGroup) null);
        final ColorPickerView colorPickerView = (ColorPickerView) layout.findViewById(R.id.colorpickerColorPickerView);
        final EditText editText = (EditText) layout.findViewById(R.id.colorpickerEditText);
        colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void colorChanged(int color, String hexColor) {
                if (!updatingEditText) {
                    updatingColorPicker = true;
                    editText.setText(hexColor);
                    updatingColorPicker = false;
                }
                newHexColor = hexColor;
                newColor = color;
            }
        });
        colorPickerView.setInitialColor(this.oldHexColor);
        colorPickerView.setCurrentColor(this.oldHexColor);
        editText.setText(this.oldHexColor);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void onTextChanged(CharSequence str, int p2, int p3, int p4) {
                if (!updatingColorPicker) {
                    updatingEditText = true;
                    colorPickerView.setCurrentColor(str.toString());
                    updatingEditText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable p1) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(layout).setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                ok.run(newColor, newHexColor);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if (this.allowsNone) {
            builder.setNeutralButton("None", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ok.run(0, null);
                }
            });
        }
        builder.setTitle(this.title);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(2);
        return dialog;
    }

    public interface ColorRunnable {
        void run(int i, String str);
    }
}
