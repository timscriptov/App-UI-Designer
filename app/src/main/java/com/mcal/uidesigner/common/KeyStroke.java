package com.mcal.uidesigner.common;

import android.annotation.SuppressLint;
import android.support.v4.util.TimeUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.mcal.uidesigner.ProxyTextView;

public class KeyStroke {
    private final boolean alt;
    private final char ch;
    private final boolean ctrl;
    private final int keyCode;
    private final boolean shift;

    public KeyStroke(int keyCode, char ch, boolean shift, boolean ctrl, boolean alt) {
        this.ch = ch;
        this.keyCode = keyCode;
        this.shift = shift;
        this.ctrl = ctrl;
        this.alt = alt;
    }

    public KeyStroke(int keyCode, boolean shift, boolean ctrl, boolean alt) {
        this.ch = 65535;
        this.keyCode = keyCode;
        this.shift = shift;
        this.ctrl = ctrl;
        this.alt = alt;
    }

    public static KeyStroke load(String value) {
        String[] split = value.split(",");
        if (split.length != 5) {
            return null;
        }
        return new KeyStroke(Integer.parseInt(split[0]), (char) Integer.parseInt(split[1]), Boolean.parseBoolean(split[2]), Boolean.parseBoolean(split[3]), Boolean.parseBoolean(split[4]));
    }

    public boolean isChar() {
        return this.ch != 65535;
    }

    public char getChar() {
        return this.ch;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public boolean isShift() {
        return this.shift;
    }

    public boolean isCtrl() {
        return this.ctrl;
    }

    public boolean isAlt() {
        return this.alt;
    }

    public boolean matches(KeyStroke pressedKeyStroke) {
        if (this.alt != pressedKeyStroke.alt || this.ctrl != pressedKeyStroke.ctrl || this.shift != pressedKeyStroke.shift) {
            return false;
        }
        if (this.keyCode == -1 || this.keyCode != pressedKeyStroke.keyCode) {
            return this.ch != 65535 && this.ch == pressedKeyStroke.ch;
        }
        return true;
    }

    public String toString() {
        String str = "";
        if (this.shift) {
            str = str + "Shift+";
        }
        if (this.ctrl) {
            str = str + "Ctrl+";
        }
        if (this.alt) {
            str = str + "Alt+";
        }
        return str + getDisplayLabel();
    }

    @SuppressLint("RestrictedApi")
    private String getDisplayLabel() {
        switch (this.keyCode) {
            case -1:
                return Character.toUpperCase(this.ch) + "";
            case TimeUtils.HUNDRED_DAY_FIELD_LEN:
                return "Up";
            case ProxyTextView.INPUTTYPE_date:
                return "Down";
            case 21:
                return "Left";
            case 22:
                return "Right";
            case 24:
                return "VolUp";
            case 25:
                return "VolDown";
            case 61:
                return "Tab";
            case 62:
                return "Space";
            case 66:
                return "Enter";
            case 67:
                return "Backspace";
            case 92:
                return "PgUp";
            case 93:
                return "PgDown";
            case 122:
                return "Home";
            case 123:
                return "End";
            case 164:
                return "VolMute";
            default:
                String displayLabel = (KeyCharacterMap.load(0).getDisplayLabel(this.keyCode) + "").trim();
                if (displayLabel.length() > 0) {
                    return displayLabel;
                }
                String name = KeyEvent.keyCodeToString(this.keyCode).toLowerCase();
                if (name.startsWith("keycode_")) {
                    name = name.substring("keycode_".length());
                }
                String name2 = name.replace("_", " ");
                return name2.substring(0, 1).toUpperCase() + name2.substring(1);
        }
    }

    public String store() {
        return this.keyCode + "," + ((int) this.ch) + "," + this.shift + "," + this.ctrl + "," + this.alt;
    }
}
