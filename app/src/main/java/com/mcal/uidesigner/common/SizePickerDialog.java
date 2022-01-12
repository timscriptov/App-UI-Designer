package com.mcal.uidesigner.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.uidesigner.R;

public class SizePickerDialog extends MessageBox {
    private AlertDialog dialog;
    private final Runnable neutral;
    private final ValueRunnable<String> ok;
    private final String oldValue;
    private final String title;
    private boolean updatingText;

    public SizePickerDialog(String title, String oldValue, ValueRunnable<String> ok, Runnable neutral) {
        this.title = title;
        this.oldValue = oldValue;
        this.ok = ok;
        this.neutral = neutral;
    }

    @Override
    protected Dialog buildDialog(final Activity activity) {
        View content = LayoutInflater.from(activity).inflate(R.layout.designer_sizedialog, (ViewGroup) null);
        final AppCompatEditText input = (AppCompatEditText) content.findViewById(R.id.designersizedialogEditText);
        input.setText(this.oldValue);
        final AppCompatSeekBar slider = (AppCompatSeekBar) content.findViewById(R.id.designersizedialogSeekBar);
        slider.setMax(100);
        updateSlider(slider, this.oldValue);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
                if (!SizePickerDialog.this.updatingText) {
                    input.setText(getSliderValue(slider, input.getText().toString()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                updatingText = true;
                updateSlider(slider, input.getText().toString());
                updatingText = false;
            }

            @Override
            public void afterTextChanged(Editable p1) {
            }
        });
        ((AppCompatTextView) content.findViewById(R.id.designersizedialogPlusButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                input.setText(increase(input.getText().toString()));
                updateSlider(slider, input.getText().toString());
            }
        });
        ((AppCompatTextView) content.findViewById(R.id.designersizedialogMinusButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                input.setText(decrease(input.getText().toString()));
                updateSlider(slider, input.getText().toString());
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(content).setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                dialog.dismiss();
                ok.run(input.getText().toString().trim());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                dialog.cancel();
            }
        }).setNeutralButton("None", new DialogInterface.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                neutral.run();
            }
        });
        if (this.title != null) {
            builder.setTitle(this.title);
        }
        this.dialog = builder.create();
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 6) {
                    ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                    dialog.dismiss();
                    ok.run(input.getText().toString().trim());
                }
                return false;
            }
        });
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.getWindow().setSoftInputMode(2);
        return this.dialog;
    }

    public String getSliderValue(AppCompatSeekBar slider, String size) {
        String unit = getUnit(size);
        if (unit == null) {
            unit = "";
        }
        return slider.getProgress() + unit;
    }

    public void updateSlider(@NonNull AppCompatSeekBar slider, String size) {
        slider.setProgress(Math.max(0, Math.min(100, getValue(size))));
    }

    public String increase(String size) {
        String unit = getUnit(size);
        if (unit != null) {
            return (getValue(size) + 1) + unit;
        }
        return size;
    }

    public String decrease(String size) {
        String unit = getUnit(size);
        if (unit != null) {
            return (getValue(size) - 1) + unit;
        }
        return size;
    }

    @Nullable
    private String getUnit(@NonNull String size) {
        if (size.length() == 0 || (!Character.isDigit(size.charAt(0)) && size.charAt(0) != '-')) {
            return null;
        }
        int p = 0;
        while (p < size.length() && (Character.isDigit(size.charAt(p)) || size.charAt(p) == '-')) {
            p++;
        }
        return size.substring(p, size.length());
    }

    private int getValue(@NonNull String size) {
        int p = 0;
        while (p < size.length() && (Character.isDigit(size.charAt(p)) || size.charAt(p) == '-')) {
            p++;
        }
        try {
            return Integer.parseInt(size.substring(0, p));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
