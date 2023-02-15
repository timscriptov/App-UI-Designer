package com.mcal.uidesigner.common;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mcal.uidesigner.ProxyTextView;

import org.jetbrains.annotations.Contract;

public class KeyStrokeDetector {
    private static final boolean DEBUG = false;
    private final Context context;
    private boolean altLeftDown;
    private boolean altRightDown;
    private boolean ctrlLeftDown;
    private boolean ctrlRightDown;
    private boolean isSoftKeyboard;
    private KeyCharacterMap keyCharacterMap;
    private int lastComposingTextLength;
    private boolean realShiftLeftDown;
    private boolean realShiftRightDown;
    private boolean shiftLeftDown;
    private boolean shiftRightDown;
    private boolean unknownDown;


    public KeyStrokeDetector(@NonNull Context context) {
        boolean z = true;
        this.context = context;
        this.isSoftKeyboard = context.getResources().getConfiguration().keyboard != 1 ? false : z;
        d("new KeyStrokeDetector() - isSoftKeyboard: " + this.isSoftKeyboard);
    }

    public void onConfigChange(@NonNull Context context) {
        boolean z = true;
        if (context.getResources().getConfiguration().keyboard != 1) {
            z = false;
        }
        this.isSoftKeyboard = z;
        d("KeyStrokeDetector.onConfigChange() - isSoftKeyboard: " + this.isSoftKeyboard);
        this.keyCharacterMap = null;
    }

    public void newWordStarted() {
        this.lastComposingTextLength = 0;
    }

    public boolean isCtrlKeyDown() {
        return this.ctrlLeftDown || this.ctrlRightDown;
    }

    public InputConnection createInputConnection(final View view, final KeyStrokeHandler keyStrokeHandler) {
        return new BaseInputConnection(view, true) {
            @Override
            public boolean performEditorAction(int actionCode) {
                d("performEditorAction" + actionCode);
                return super.performEditorAction(actionCode);
            }

            @Override
            public boolean setComposingText(CharSequence text, int newCursorPosition) {
                KeyStrokeDetector.this.d("setComposingText '" + ((Object) text) + "'");
                for (int i = 0; i < lastComposingTextLength; i++) {
                    keyStrokeHandler.onKeyStroke(new KeyStroke(67, false, false, false));
                }
                lastComposingTextLength = text.length();
                sendAsCharKeyStrokes(text, isSoftKeyboard, keyStrokeHandler);
                return true;
            }

            @Override
            public boolean beginBatchEdit() {
                d("beginBatchEdit");
                return super.beginBatchEdit();
            }

            @Override
            public boolean endBatchEdit() {
                d("endBatchEdit");
                return super.endBatchEdit();
            }

            @Override
            public boolean commitCompletion(CompletionInfo text) {
                d("commitCompletion");
                return super.commitCompletion(text);
            }

            @Override
            public boolean commitCorrection(CorrectionInfo correctionInfo) {
                d("commitCorrection");
                return super.commitCorrection(correctionInfo);
            }

            @Override
            public boolean finishComposingText() {
                d("finishComposingText");
                return super.finishComposingText();
            }

            @Override
            public boolean commitText(CharSequence text, int newCursorPosition) {
                d("commitText '" + ((Object) text) + "'");
                if (Build.VERSION.SDK_INT >= 17) {
                    for (int i = 0; i < lastComposingTextLength; i++) {
                        keyStrokeHandler.onKeyStroke(new KeyStroke(67, false, false, false));
                    }
                } else if (lastComposingTextLength > 0 && text.length() == 1 && text.charAt(0) == ' ') {
                    lastComposingTextLength = 0;
                    return true;
                }
                lastComposingTextLength = 0;
                if ("\n".equals(text.toString())) {
                    sendAsKeyEvents(text, isSoftKeyboard, view);
                } else {
                    sendAsCharKeyStrokes(text, isSoftKeyboard, keyStrokeHandler);
                }
                return true;
            }

            @Override
            public boolean deleteSurroundingText(int leftLength, int rightLength) {
                d("deleteSurroundingText " + leftLength + " " + rightLength);
                lastComposingTextLength = 0;
                for (int i = 0; i < leftLength; i++) {
                    keyStrokeHandler.onKeyStroke(new KeyStroke(67, false, false, false));
                }
                return super.deleteSurroundingText(leftLength, rightLength);
            }

            private void sendAsCharKeyStrokes(@NonNull CharSequence text, boolean isSoftKeyboard, KeyStrokeHandler keyStrokeHandler2) {
                for (int j = 0; j < text.length(); j++) {
                    char ch = text.charAt(j);
                    if (!isSoftKeyboard) {
                        if (realShiftLeftDown || realShiftRightDown) {
                            ch = Character.toUpperCase(ch);
                        } else {
                            ch = Character.toLowerCase(ch);
                        }
                    }

                    if (keyStrokeHandler2 != null) {
                        keyStrokeHandler2.onKeyStroke(KeyStrokeDetector.this.makeKeyStroke(ch));
                    }
                }
            }

            private void sendAsKeyEvents(CharSequence text, boolean isSoftKeyboard, View view2) {
                if (keyCharacterMap == null) {
                    keyCharacterMap = KeyCharacterMap.load(0);
                }
                for (int j = 0; j < text.length(); j++) {
                    char ch = text.charAt(j);
                    if (!isSoftKeyboard) {
                        if (realShiftLeftDown || realShiftRightDown) {
                            ch = Character.toUpperCase(ch);
                        } else {
                            ch = Character.toLowerCase(ch);
                        }
                    }
                    KeyEvent[] events = keyCharacterMap.getEvents(new char[]{ch});
                    if (events != null) {
                        for (KeyEvent event : events) {
                            sendKeyEvent(event);
                        }
                    }
                }
            }

            @NonNull
            @Contract("_ -> new")
            private KeyEvent transformEvent(@NonNull KeyEvent event) {
                return new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(), event.getKeyCode(), event.getRepeatCount(), event.getMetaState(), event.getDeviceId(), event.getScanCode(), event.getFlags() | 4 | 2);
            }

            @Override
            public boolean sendKeyEvent(@NonNull KeyEvent event) {
                d("sendKeyEvent " + event.getKeyCode());
                lastComposingTextLength = 0;
                return super.sendKeyEvent(transformEvent(event));
            }

            @Override
            public CharSequence getTextBeforeCursor(int length, int flags) {
                if (isSonyEricsson() || AndroidHelper.isAndroidTV(context)) {
                    return super.getTextBeforeCursor(length, flags);
                }
                int length2 = Math.min(length, 1024);
                StringBuilder sb = new StringBuilder(length2);
                for (int i = 0; i < length2; i++) {
                    sb.append(' ');
                }
                return sb;
            }

            private boolean isSonyEricsson() {
                String defaultInputMethodName = null;
                try {
                    defaultInputMethodName = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
                    KeyStrokeDetector.this.d("Default IME: " + defaultInputMethodName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (defaultInputMethodName == null || !defaultInputMethodName.startsWith("com.sonyericsson.")) {
                    return false;
                }
                return true;
            }
        };
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, KeyStrokeHandler keyStrokeHandler) {
        boolean z;
        debugOnKey("onKeyDown", keyCode, event);
        if (keyCode == 84) {
            keyCode = 57;
        }
        if ((event.getFlags() & 2) != 0) {
            z = true;
        } else {
            z = false;
        }
        handleMetaKeysDown(keyCode, z);
        KeyStroke keyStroke = makeKeyStroke(keyCode, event);
        if (keyStroke == null || !keyStrokeHandler.onKeyStroke(keyStroke)) {
            return keyCode == 84;
        }
        debugOnKeyStroke(keyStroke);
        return true;
    }

    public void d(String msg) {
    }

    private void debugOnKeyStroke(@NonNull KeyStroke keyStroke) {
        d("onKeyStroke " + keyStroke.toString());
    }

    private void debugOnKey(String method, int keyCode, @NonNull KeyEvent event) {
        d(method + " " + keyCode + "  " + event.getFlags() + (event.isAltPressed() ? " alt" : "") + (event.isShiftPressed() ? " shift" : "") + " " + (isCtrl(event.getMetaState()) ? " ctrl" : ""));
    }

    private boolean isCtrl(int metaState) {
        return (metaState & 12288) != 0;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event, KeyStrokeHandler keyStrokeHandler) {
        boolean z;
        debugOnKey("onKeyUp", keyCode, event);
        if (keyCode == 84) {
            keyCode = 57;
        }
        if ((event.getFlags() & 2) != 0) {
            z = true;
        } else {
            z = false;
        }
        handleMetaKeysUp(keyCode, z);
        return keyCode == 84;
    }

    public KeyStroke makeKeyStroke(char ch) {
        return new KeyStroke(-1, ch, Character.isUpperCase(ch) | this.realShiftLeftDown | this.realShiftRightDown, false, false);
    }

    @Nullable
    private KeyStroke makeKeyStroke(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 0:
            case 3:
            case 4:
            case 57:
            case 58:
            case 59:
            case 60:
            case ProxyTextView.INPUTTYPE_textPostalAddress:
            case 114:
                return null;
            default:
                boolean shift = this.shiftLeftDown | this.shiftRightDown | event.isShiftPressed();
                boolean ctrl = this.ctrlLeftDown | this.ctrlRightDown | isCtrl(event.getMetaState());
                boolean alt = this.altLeftDown | this.altRightDown | event.isAltPressed();
                char ch = 65535;
                int chi = event.getUnicodeChar();
                if (chi != 0 && !Character.isISOControl(chi)) {
                    ch = (char) chi;
                }
                return new KeyStroke(keyCode, ch, shift, ctrl, alt);
        }
    }

    public void onActivityKeyUp(int keyCode, @NonNull KeyEvent event) {
        handleMetaKeysUp(keyCode, (event.getFlags() & 2) != 0);
    }

    public void onActivityKeyDown(int keyCode, @NonNull KeyEvent event) {
        handleMetaKeysDown(keyCode, (event.getFlags() & 2) != 0);
    }

    private void handleMetaKeysDown(int keyCode, boolean isSoftKeyboard) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8 = true;
        d("onMetaKeysDown " + keyCode);
        this.altLeftDown = (keyCode == 57) | this.altLeftDown;
        boolean z9 = this.altRightDown;
        if (keyCode == 58) {
            z = true;
        } else {
            z = false;
        }
        this.altRightDown = z | z9;
        boolean z10 = this.shiftLeftDown;
        if (keyCode == 59) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.shiftLeftDown = z2 | z10;
        boolean z11 = this.shiftRightDown;
        if (keyCode == 60) {
            z3 = true;
        } else {
            z3 = false;
        }
        this.shiftRightDown = z3 | z11;
        boolean z12 = this.realShiftLeftDown;
        if (keyCode != 59 || isSoftKeyboard) {
            z4 = false;
        } else {
            z4 = true;
        }
        this.realShiftLeftDown = z4 | z12;
        boolean z13 = this.realShiftRightDown;
        if (keyCode != 60 || isSoftKeyboard) {
            z5 = false;
        } else {
            z5 = true;
        }
        this.realShiftRightDown = z5 | z13;
        boolean z14 = this.unknownDown;
        if (keyCode == 0) {
            z6 = true;
        } else {
            z6 = false;
        }
        this.unknownDown = z6 | z14;
        boolean z15 = this.ctrlLeftDown;
        if (keyCode == 113) {
            z7 = true;
        } else {
            z7 = false;
        }
        this.ctrlLeftDown = z7 | z15;
        boolean z16 = this.ctrlRightDown;
        if (keyCode != 114) {
            z8 = false;
        }
        this.ctrlRightDown = z16 | z8;
    }

    private void handleMetaKeysUp(int keyCode, boolean isSoftKeyboard) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8 = true;
        d("onMetaKeysUp " + keyCode);
        this.altLeftDown = (keyCode != 57) & this.altLeftDown;
        boolean z9 = this.altRightDown;
        if (keyCode != 58) {
            z = true;
        } else {
            z = false;
        }
        this.altRightDown = z & z9;
        boolean z10 = this.shiftLeftDown;
        if (keyCode != 59) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.shiftLeftDown = z2 & z10;
        boolean z11 = this.shiftRightDown;
        if (keyCode != 60) {
            z3 = true;
        } else {
            z3 = false;
        }
        this.shiftRightDown = z3 & z11;
        boolean z12 = this.realShiftLeftDown;
        if (keyCode != 59 || isSoftKeyboard) {
            z4 = true;
        } else {
            z4 = false;
        }
        this.realShiftLeftDown = z4 & z12;
        boolean z13 = this.realShiftRightDown;
        if (keyCode != 60 || isSoftKeyboard) {
            z5 = true;
        } else {
            z5 = false;
        }
        this.realShiftRightDown = z5 & z13;
        boolean z14 = this.unknownDown;
        if (keyCode != 0) {
            z6 = true;
        } else {
            z6 = false;
        }
        this.unknownDown = z6 & z14;
        boolean z15 = this.ctrlLeftDown;
        if (keyCode != 113) {
            z7 = true;
        } else {
            z7 = false;
        }
        this.ctrlLeftDown = z7 & z15;
        boolean z16 = this.ctrlRightDown;
        if (keyCode == 114) {
            z8 = false;
        }
        this.ctrlRightDown = z16 & z8;
    }

    public interface KeyStrokeHandler {
        boolean onKeyStroke(KeyStroke keyStroke);
    }
}
