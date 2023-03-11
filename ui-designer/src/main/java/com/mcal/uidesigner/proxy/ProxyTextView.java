package com.mcal.uidesigner.proxy;

import static com.mcal.uidesigner.utils.Utils.getAttr;
import static com.mcal.uidesigner.utils.Utils.getStyle;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

public class ProxyTextView {
    public static final int INPUTTYPE_date = 20;
    public static final int INPUTTYPE_datetime = 4;
    public static final int INPUTTYPE_none = 0;
    public static final int INPUTTYPE_number = 2;
    public static final int INPUTTYPE_numberDecimal = 8194;
    public static final int INPUTTYPE_numberPassword = 18;
    public static final int INPUTTYPE_numberSigned = 4098;
    public static final int INPUTTYPE_phone = 3;
    public static final int INPUTTYPE_text = 1;
    public static final int INPUTTYPE_textAutoComplete = 65537;
    public static final int INPUTTYPE_textAutoCorrect = 32769;
    public static final int INPUTTYPE_textCapCharacters = 4097;
    public static final int INPUTTYPE_textCapSentences = 16385;
    public static final int INPUTTYPE_textCapWords = 8193;
    public static final int INPUTTYPE_textEmailAddress = 33;
    public static final int INPUTTYPE_textEmailSubject = 49;
    public static final int INPUTTYPE_textFilter = 177;
    public static final int INPUTTYPE_textImeMultiLine = 262145;
    public static final int INPUTTYPE_textLongMessage = 81;
    public static final int INPUTTYPE_textMultiLine = 131073;
    public static final int INPUTTYPE_textNoSuggestions = 524289;
    public static final int INPUTTYPE_textPassword = 129;
    public static final int INPUTTYPE_textPersonName = 97;
    public static final int INPUTTYPE_textPhonetic = 193;
    public static final int INPUTTYPE_textPostalAddress = 113;
    public static final int INPUTTYPE_textShortMessage = 65;
    public static final int INPUTTYPE_textUri = 17;
    public static final int INPUTTYPE_textVisiblePassword = 145;
    public static final int INPUTTYPE_textWebEditText = 161;
    public static final int INPUTTYPE_textWebEmailAddress = 209;
    public static final int INPUTTYPE_textWebPassword = 225;
    public static final int INPUTTYPE_time = 36;
    public static final int TEXTSTYLE_bold = 1;
    public static final int TEXTSTYLE_italic = 2;
    public static final int TEXTSTYLE_normal = 0;
    public static final int TYPEFACE_MONOSPACE = 3;
    public static final int TYPEFACE_NORMAL = 0;
    public static final int TYPEFACE_SANS = 1;
    public static final int TYPEFACE_SERIF = 2;
    private final TextView textView;
    private int shadowColor;
    private float shadowDx;
    private float shadowDy;
    private float shadowRadius;
    private int textStyle = 0;
    private int typeface = 0;
    private String fontFamily = null;

    public ProxyTextView(Object obj) {
        textView = (TextView) obj;
    }

    public void setInputType(int type) {
        textView.setInputType(type);
    }

    public void setTextAppearance(String value) {
        try {
            if (value.startsWith("@android:style/")) {
                int styleID = getStyle(value);
                if (styleID >= 0) {
                    TextViewCompat.setTextAppearance(textView, styleID);
                }
            } else {
                int attrID = getAttr(value);
                if (attrID >= 0) {
                    Resources.Theme theme = textView.getContext().getTheme();
                    TypedValue styleID = new TypedValue();
                    if (theme.resolveAttribute(attrID, styleID, true)) {
                        TextViewCompat.setTextAppearance(textView, styleID.data);
                    }
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void setShadowRadius(float val) {
        shadowRadius = val;
        updateShadow();
    }

    public void setShadowDx(float val) {
        shadowDx = val;
        updateShadow();
    }

    public void setShadowDy(float val) {
        shadowDy = val;
        updateShadow();
    }

    public void setShadowColor(int color) {
        shadowColor = color;
        updateShadow();
    }

    private void updateShadow() {
        textView.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    public void setTextStyle(int style) {
        textStyle = style;
        updateFont();
    }

    public void setTypeface(int typeface) {
        this.typeface = typeface;
        updateFont();
    }

    public void setFontFamily(String family) {
        fontFamily = family;
        updateFont();
    }

    private void updateFont() {
        Typeface tf = null;
        if (fontFamily == null || (tf = Typeface.create(fontFamily, textStyle)) == null) {
            switch (typeface) {
                case 1:
                    tf = Typeface.SANS_SERIF;
                    break;
                case 2:
                    tf = Typeface.SERIF;
                    break;
                case 3:
                    tf = Typeface.MONOSPACE;
                    break;
            }
            textView.setTypeface(tf, textStyle);
            return;
        }
        textView.setTypeface(tf);
    }
}
