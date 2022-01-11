package com.mcal.uidesigner.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.mcal.designer.R;
import com.mcal.uidesigner.ProxyTextView;
import com.mcal.uidesigner.widget.KeyStrokeEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class MessageBox {
    private static final int ID = 123456;
    private static MessageBox shownBox;
    private static Dialog shownDialog;

    public static void showDialog(Activity activity, MessageBox box) {
        shownBox = box;
        if (shownDialog == null || !shownDialog.isShowing()) {
            activity.removeDialog(ID);
            activity.showDialog(ID);
        }
    }

    public static void hide() {
        if (shownDialog != null) {
            shownDialog.dismiss();
        }
    }

    public static boolean isShowing() {
        return shownDialog != null && shownDialog.isShowing();
    }

    public static void showError(Activity activity, String title, Throwable e) {
        showError(activity, title, e.getMessage());
    }

    public static void showError(Activity activity, String title, String message) {
        showError(activity, title, message, null, null);
    }

    public static void showError(Activity activity, final String title, final String message, final String negativeButton, final Runnable no) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setMessage(message).setCancelable(true).setPositiveButton(activity2.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                if (negativeButton != null) {
                    builder = builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            no.run();
                        }
                    });
                }
                if (title != null) {
                    builder = builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void showInfo(Activity activity, String title, String message, Runnable ok) {
        showInfo(activity, title, message, ok, null);
    }

    public static void showInfo(Activity activity, String title, String message, Runnable ok, Runnable cancelled) {
        showInfo(activity, title, message, true, ok, cancelled);
    }

    public static void showInfo(Activity activity, String title, String message, boolean cancelable, Runnable ok, Runnable cancelled) {
        showInfo(activity, title, message, cancelable, activity.getResources().getString(R.string.dialog_ok), ok, null, cancelled);
    }

    public static void showInfo(Activity activity, final String title, final String message, final boolean cancelable, final String okText, final Runnable ok, final String cancelText, final Runnable cancelled) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setMessage(message).setCancelable(cancelable);
                builder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (ok != null) {
                            ok.run();
                        }
                    }
                });
                if (cancelText != null) {
                    builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (cancelled != null) {
                                cancelled.run();
                            }
                        }
                    });
                }
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        if (cancelled != null) {
                            cancelled.run();
                        }
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                if (cancelable) {
                    dialog.setCanceledOnTouchOutside(true);
                }
                return dialog;
            }
        });
    }

    public static void queryYesNo(Activity activity, int title, int message, List<String> list, Runnable yes, Runnable no) {
        String listText = "\n";
        Iterator<String> i = list.iterator();
        while (i.hasNext()) {
            listText = (listText + "\n") + i.next();
        }
        queryYesNo(activity, activity.getResources().getString(title), activity.getResources().getString(message) + listText, activity.getResources().getString(R.string.dialog_no), no, activity.getResources().getString(R.string.dialog_yes), yes, null);
    }

    public static void queryYesNo(Activity activity, int title, int message, Runnable yes, Runnable no) {
        queryYesNo(activity, activity.getResources().getString(title), activity.getResources().getString(message), activity.getResources().getString(R.string.dialog_yes), yes, activity.getResources().getString(R.string.dialog_no), no, null);
    }

    public static void queryYesNo(Activity activity, String title, String message, Runnable yes, Runnable no) {
        queryYesNo(activity, title, message, yes, no, (Runnable) null);
    }

    public static void queryYesNo(Activity activity, String title, String message, Runnable yes, Runnable no, Runnable cancelled) {
        queryYesNo(activity, title, message, activity.getResources().getString(R.string.dialog_yes), yes, activity.getResources().getString(R.string.dialog_no), no, cancelled);
    }

    public static void queryYesNo(Activity activity, final String title, final String message, final String yesText, final Runnable yes, final String noText, final Runnable no, final Runnable cancelled) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setMessage(message).setCancelable(true).setPositiveButton(yesText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (yes != null) {
                            yes.run();
                        }
                    }
                }).setNegativeButton(noText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (no != null) {
                            no.run();
                        }
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (cancelled != null) {
                            cancelled.run();
                        }
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryKeyStroke(Activity activity, final String title, final String message, final KeyStroke oldStroke, final ValueRunnable<KeyStroke> ok) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(final Activity activity2) {
                final KeyStrokeEditText input = new KeyStrokeEditText(activity2);
                input.setKeyStroke(oldStroke);
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == 6) {
                            ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                            ok.run(input.getKeyStroke());
                        }
                        return false;
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setView(input).setMessage(message).setCancelable(true).setPositiveButton(activity2.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                        ok.run(input.getKeyStroke());
                    }
                }).setNegativeButton(activity2.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog.cancel();
                    }
                }).setNeutralButton("Default", new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog.cancel();
                        ok.run(null);
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onShow(DialogInterface dialog2) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).showSoftInput(input, 1);
                        input.selectAll();
                    }
                });
                return dialog;
            }
        });
    }

    public static void queryFromList(Activity activity, String title, List<String> values, ValueRunnable<String> ok) {
        queryFromList(activity, title, values, true, ok);
    }

    private static void queryFromList(Activity activity, final String title, final List<String> values, final boolean cancelable, final ValueRunnable<String> ok) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setCancelable(cancelable).setItems((CharSequence[]) values.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ok.run(values.get(which));
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(cancelable);
                return dialog;
            }
        });
    }

    public static void querySingleChoiceFromList(Activity activity, final String title, final List<String> values, final String selectedValue, final ValueRunnable<String> ok) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setCancelable(true).setSingleChoiceItems((CharSequence[]) values.toArray(new CharSequence[0]), values.indexOf(selectedValue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                    }
                }).setPositiveButton(activity2.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SparseBooleanArray items = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                        if (items != null) {
                            for (int i = 0; i < values.size(); i++) {
                                if (items.get(i)) {
                                    ok.run(values.get(i));
                                }
                            }
                        }
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryMultipleChoiceFromList(Activity activity, final String title, final List<String> values, final List<Boolean> selectedValues, final ValueRunnable<List<Integer>> ok) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                boolean[] checkedItems = new boolean[selectedValues.size()];
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = ((Boolean) selectedValues.get(i)).booleanValue();
                }
                builder.setCancelable(true).setMultiChoiceItems((CharSequence[]) values.toArray(new CharSequence[0]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    }
                }).setPositiveButton(activity2.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        SparseBooleanArray items = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                        if (items != null) {
                            for (int i2 = 0; i2 < values.size(); i2++) {
                                if (items.get(i2)) {
                                    arrayList.add(Integer.valueOf(i2));
                                }
                            }
                        }
                        ok.run(arrayList);
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryIndexFromList(Activity activity, final String title, final List<String> values, final ValueRunnable<Integer> ok) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setCancelable(true).setItems((CharSequence[]) values.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ok.run(Integer.valueOf(which));
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryIndexFromList(Activity activity, final String title, final List<String> values, final String okButtonText, final ValueRunnable<Integer> click, final Runnable ok) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setCancelable(true).setItems((CharSequence[]) values.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        click.run(Integer.valueOf(which));
                    }
                }).setPositiveButton(okButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ok.run();
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryInt(Activity activity, final String title, final String message, final int oldValue, final ValueRunnable<Integer> ok) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(final Activity activity2) {
                final AlertDialog[] dialog = new AlertDialog[1];
                final AppCompatEditText input = new AppCompatEditText(activity2) {
                    @Override
                    public boolean onKeyDown(int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            return true;
                        }
                        return super.onKeyDown(keyCode, event);
                    }

                    @SuppressLint("WrongConstant")
                    @Override
                    public boolean onKeyUp(int keyCode, KeyEvent event) {
                        if (keyCode != 66) {
                            return super.onKeyUp(keyCode, event);
                        }
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(getWindowToken(), 0);
                        try {
                            ok.run(Integer.valueOf(Integer.parseInt(getText().toString().trim())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        dialog[0].dismiss();
                        return true;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setView(input).setMessage(message).setCancelable(true).setPositiveButton(activity2.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog2, int id) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                        try {
                            ok.run(Integer.valueOf(Integer.parseInt(input.getText().toString().trim())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton(activity2.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog2, int id) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog2.cancel();
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                dialog[0] = builder.create();
                dialog[0].setOnShowListener(new DialogInterface.OnShowListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onShow(DialogInterface dialog2) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).showSoftInput(input, 1);
                        input.selectAll();
                    }
                });
                input.setText(oldValue + "");
                input.setImeOptions(268435456);
                input.setInputType(2);
                input.setTypeface(Typeface.DEFAULT);
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == 6) {
                            ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                            dialog[0].dismiss();
                            try {
                                ok.run(Integer.valueOf(Integer.parseInt(input.getText().toString().trim())));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });
                dialog[0].setCanceledOnTouchOutside(true);
                return dialog[0];
            }
        });
    }

    public static void queryPassword(Activity activity, String title, String message, ValueRunnable<String> ok, Runnable cancelled) {
        queryText(activity, title, message, null, "", true, ok, cancelled, null);
    }

    public static void queryPassword(Activity activity, String title, String message, ValueRunnable<String> ok) {
        queryText(activity, title, message, null, "", true, ok, null, null);
    }

    public static void queryText(Activity activity, String title, String message, String oldText, ValueRunnable<String> ok, Runnable cancelled) {
        queryText(activity, title, message, null, oldText, false, ok, cancelled, null);
    }

    public static void queryText(Activity activity, int title, int message, String oldText, ValueRunnable<String> ok) {
        queryText(activity, activity.getResources().getString(title), activity.getResources().getString(message), null, oldText, false, ok, null, null);
    }

    public static void queryText(Activity activity, String title, String message, String oldText, ValueRunnable<String> ok) {
        queryText(activity, title, message, null, oldText, false, ok, null, null);
    }

    public static void queryText(Activity activity, String title, String message, String neutralText, String oldText, ValueRunnable<String> ok, Runnable neutral) {
        queryText(activity, title, message, neutralText, oldText, false, ok, null, neutral);
    }

    private static void queryText(Activity activity, final String title, final String message, final String neutralText, final String oldText, final boolean isPassword, final ValueRunnable<String> ok, final Runnable cancelled, final Runnable neutral) {
        showDialog(activity, new MessageBox() {
            @Override
            public Dialog buildDialog(final Activity activity2) {
                final AlertDialog[] dialog = new AlertDialog[1];
                final AppCompatEditText input = new AppCompatEditText(activity2) {
                    @Override
                    public boolean onKeyDown(int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            return true;
                        }
                        return super.onKeyDown(keyCode, event);
                    }

                    @SuppressLint("WrongConstant")
                    @Override
                    public boolean onKeyUp(int keyCode, KeyEvent event) {
                        if (keyCode != 66) {
                            return super.onKeyUp(keyCode, event);
                        }
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(getWindowToken(), 0);
                        dialog[0].dismiss();
                        ok.run(getText().toString().trim());
                        return true;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setView(input).setMessage(message).setCancelable(true).setPositiveButton(activity2.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog2, int id) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog2.dismiss();
                        ok.run(input.getText().toString().trim());
                    }
                }).setNegativeButton(activity2.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialog2, int id) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog2.cancel();
                    }
                });
                if (neutralText != null) {
                    builder.setNeutralButton(neutralText, new DialogInterface.OnClickListener() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onClick(DialogInterface dialog2, int which) {
                            ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                            neutral.run();
                        }
                    });
                }
                if (title != null) {
                    builder.setTitle(title);
                }
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog2) {
                        if (cancelled != null) {
                            cancelled.run();
                        }
                    }
                });
                dialog[0] = builder.create();
                dialog[0].setOnShowListener(new DialogInterface.OnShowListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onShow(DialogInterface dialog2) {
                        ((InputMethodManager) activity2.getSystemService("input_method")).showSoftInput(input, 1);
                        input.selectAll();
                    }
                });
                input.setText(oldText);
                input.setImeOptions(268435456);
                if (isPassword) {
                    input.setInputType(ProxyTextView.INPUTTYPE_textPassword);
                } else {
                    input.setInputType(ProxyTextView.INPUTTYPE_textVisiblePassword);
                }
                input.setTypeface(Typeface.DEFAULT);
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == 6) {
                            ((InputMethodManager) activity2.getSystemService("input_method")).hideSoftInputFromWindow(input.getWindowToken(), 0);
                            dialog[0].dismiss();
                            ok.run(input.getText().toString().trim());
                        }
                        return false;
                    }
                });
                dialog[0].setCanceledOnTouchOutside(true);
                return dialog[0];
            }
        });
    }

    public static Dialog onCreateDialog(Activity activity, int id) {
        if (shownBox == null || id != ID) {
            return null;
        }
        shownDialog = shownBox.buildDialog(activity);
        shownBox = null;
        return shownDialog;
    }

    public static void queryMultipleValues(Activity activity, String title, final List<String> values, List<String> displayValues, String value, final ValueRunnable<String> ok) {
        List<Boolean> isSelected = new ArrayList<>();
        List<String> selectedValues = value == null ? new ArrayList<>() : Arrays.asList(value.split("\\|"));
        for (String v : values) {
            isSelected.add(Boolean.valueOf(selectedValues.contains(v)));
        }
        queryMultipleChoiceFromList(activity, title, displayValues, isSelected, new ValueRunnable<List<Integer>>() {
            public void run(List<Integer> t) {
                if (t.size() == 0) {
                    ok.run(null);
                    return;
                }
                String newValue = "";
                for (Integer num : t) {
                    int i = num.intValue();
                    if (newValue.length() > 0) {
                        newValue = newValue + "|";
                    }
                    newValue = newValue + ((String) values.get(i));
                }
                ok.run(newValue);
            }
        });
    }

    protected abstract Dialog buildDialog(Activity activity);
}
