package com.mcal.uidesigner.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.mcal.uidesigner.common.AndroidHelper;

public class ColorPickerView extends View {
    private final int[] hueBarColors;
    private final int[] derivedColors;
    private int currentColor;
    private float currentHue;
    private int currentX;
    private int currentY;
    private int initialColor;
    private OnColorChangedListener listener;
    private Paint paint;
    private float scale;

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.currentHue = 0.0f;
        this.currentX = 0;
        this.currentY = 0;
        this.hueBarColors = new int[258];
        this.derivedColors = new int[65536];
        init();
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.currentHue = 0.0f;
        this.currentX = 0;
        this.currentY = 0;
        this.hueBarColors = new int[258];
        this.derivedColors = new int[65536];
        init();
    }

    public ColorPickerView(Context c) {
        super(c);
        this.currentHue = 0.0f;
        this.currentX = 0;
        this.currentY = 0;
        this.hueBarColors = new int[258];
        this.derivedColors = new int[65536];
        init();
    }

    @NonNull
    public static String toHexColor(int c) {
        if (Color.alpha(c) == 255) {
            return String.format("#%06X", 16777215 & c);
        }
        return String.format("#%08X", c & -1);
    }

    public static int parseColor(String hexColor) {
        if (hexColor != null && hexColor.length() == 9 && hexColor.startsWith("#")) {
            return (int) Long.parseLong(hexColor.substring(1), 16);
        }
        if (hexColor == null || hexColor.length() != 7 || !hexColor.startsWith("#")) {
            return ViewCompat.MEASURED_STATE_MASK;
        }
        return -16777216 | ((int) Long.parseLong(hexColor.substring(1), 16));
    }

    private void init() {
        updateMainColors();
        Context c = getContext();
        this.scale = c.getResources().getDisplayMetrics().density * (Math.min(300.0f, AndroidHelper.getScreenSizeDip(c) - 150.0f) / 256.0f);
        this.paint = new Paint(1);
        this.paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setTextSize(12.0f);
    }

    public void setCurrentColor(String hexColor) {
        this.currentColor = parseColor(hexColor);
        float[] hsv = new float[3];
        Color.colorToHSV(this.currentColor | ViewCompat.MEASURED_STATE_MASK, hsv);
        this.currentHue = hsv[0];
        updateDerivedColors();
        getCurrentXY();
        invalidate();
        this.listener.colorChanged(this.currentColor, toHexColor(this.currentColor));
    }

    public void setInitialColor(String hexColor) {
        this.initialColor = parseColor(hexColor);
    }

    public void setOnColorChangedListener(OnColorChangedListener l) {
        this.listener = l;
    }

    private void updateMainColors() {
        int index = 0;
        for (float i = 0.0f; i < 256.0f; i += 6.0f) {
            this.hueBarColors[index] = Color.rgb((int) MotionEventCompat.ACTION_MASK, 0, (int) i);
            index++;
        }
        for (float i2 = 0.0f; i2 < 256.0f; i2 += 6.0f) {
            this.hueBarColors[index] = Color.rgb(255 - ((int) i2), 0, (int) MotionEventCompat.ACTION_MASK);
            index++;
        }
        for (float i3 = 0.0f; i3 < 256.0f; i3 += 6.0f) {
            this.hueBarColors[index] = Color.rgb(0, (int) i3, (int) MotionEventCompat.ACTION_MASK);
            index++;
        }
        for (float i4 = 0.0f; i4 < 256.0f; i4 += 6.0f) {
            this.hueBarColors[index] = Color.rgb(0, (int) MotionEventCompat.ACTION_MASK, 255 - ((int) i4));
            index++;
        }
        for (float i5 = 0.0f; i5 < 256.0f; i5 += 6.0f) {
            this.hueBarColors[index] = Color.rgb((int) i5, (int) MotionEventCompat.ACTION_MASK, 0);
            index++;
        }
        for (float i6 = 0.0f; i6 < 256.0f; i6 += 6.0f) {
            this.hueBarColors[index] = Color.rgb((int) MotionEventCompat.ACTION_MASK, 255 - ((int) i6), 0);
            index++;
        }
    }

    private void getCurrentXY() {
        int minDist = 1000;
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                int dist = getColorDist(getDerivedColorForXY(x, y), this.currentColor | ViewCompat.MEASURED_STATE_MASK);
                if (dist == 0) {
                    this.currentX = x;
                    this.currentY = y;
                    return;
                }
                if (dist < minDist) {
                    this.currentX = x;
                    this.currentY = y;
                    minDist = dist;
                }
            }
        }
    }

    private int getColorDist(int c1, int c2) {
        return Math.abs(Color.red(c1) - Color.red(c2)) + Math.abs(Color.green(c1) - Color.green(c2)) + Math.abs(Color.blue(c1) - Color.blue(c2));
    }

    private int getCurrentMainColor() {
        int translatedHue = 255 - ((int) ((this.currentHue * 255.0f) / 360.0f));
        int index = 0;
        for (float i = 0.0f; i < 256.0f; i += 6.0f) {
            if (index == translatedHue) {
                return Color.rgb((int) MotionEventCompat.ACTION_MASK, 0, (int) i);
            }
            index++;
        }
        for (float i2 = 0.0f; i2 < 256.0f; i2 += 6.0f) {
            if (index == translatedHue) {
                return Color.rgb(255 - ((int) i2), 0, (int) MotionEventCompat.ACTION_MASK);
            }
            index++;
        }
        for (float i3 = 0.0f; i3 < 256.0f; i3 += 6.0f) {
            if (index == translatedHue) {
                return Color.rgb(0, (int) i3, (int) MotionEventCompat.ACTION_MASK);
            }
            index++;
        }
        for (float i4 = 0.0f; i4 < 256.0f; i4 += 6.0f) {
            if (index == translatedHue) {
                return Color.rgb(0, (int) MotionEventCompat.ACTION_MASK, 255 - ((int) i4));
            }
            index++;
        }
        for (float i5 = 0.0f; i5 < 256.0f; i5 += 6.0f) {
            if (index == translatedHue) {
                return Color.rgb((int) i5, (int) MotionEventCompat.ACTION_MASK, 0);
            }
            index++;
        }
        for (float i6 = 0.0f; i6 < 256.0f; i6 += 6.0f) {
            if (index == translatedHue) {
                return Color.rgb((int) MotionEventCompat.ACTION_MASK, 255 - ((int) i6), 0);
            }
            index++;
        }
        return SupportMenu.CATEGORY_MASK;
    }

    private void updateDerivedColors() {
        int mainColor = getCurrentMainColor();
        int index = 0;
        int[] topColors = new int[256];
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                if (y == 0) {
                    this.derivedColors[index] = Color.rgb(255 - (((255 - Color.red(mainColor)) * x) / MotionEventCompat.ACTION_MASK), 255 - (((255 - Color.green(mainColor)) * x) / MotionEventCompat.ACTION_MASK), 255 - (((255 - Color.blue(mainColor)) * x) / MotionEventCompat.ACTION_MASK));
                    topColors[x] = this.derivedColors[index];
                } else {
                    this.derivedColors[index] = Color.rgb(((255 - y) * Color.red(topColors[x])) / MotionEventCompat.ACTION_MASK, ((255 - y) * Color.green(topColors[x])) / MotionEventCompat.ACTION_MASK, ((255 - y) * Color.blue(topColors[x])) / MotionEventCompat.ACTION_MASK);
                }
                index++;
            }
        }
    }

    private int getDerivedColorForXY(int x, int y) {
        int index = (y * 256) + x;
        if (index < 0 || index >= this.derivedColors.length) {
            return 0;
        }
        return this.derivedColors[index];
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int translatedHue = 255 - ((int) ((this.currentHue * 255.0f) / 360.0f));
        for (int i = 0; i < 256; i++) {
            this.paint.setStrokeWidth(2.0f * this.scale);
            this.paint.setColor(this.hueBarColors[i]);
            canvas.drawLine(((float) (i + 10)) * this.scale, 0.0f, ((float) (i + 10)) * this.scale, 40.0f * this.scale, this.paint);
        }
        this.paint.setStrokeWidth(3.0f * this.scale);
        this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        canvas.drawLine(((float) (translatedHue + 10)) * this.scale, 0.0f, ((float) (translatedHue + 10)) * this.scale, 40.0f * this.scale, this.paint);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paint.setStrokeWidth(1.0f);
        canvas.drawRect(9.5f * this.scale, 0.0f * this.scale, 266.5f * this.scale, 40.0f * this.scale, this.paint);
        for (int i2 = 0; i2 < 256; i2++) {
            this.paint.setShader(new LinearGradient(0.0f, 50.0f * this.scale, 0.0f, 306.0f * this.scale, new int[]{this.derivedColors[i2], ViewCompat.MEASURED_STATE_MASK}, (float[]) null, Shader.TileMode.REPEAT));
            this.paint.setStrokeWidth(2.0f * this.scale);
            canvas.drawLine(((float) (i2 + 10)) * this.scale, 50.0f * this.scale, ((float) (i2 + 10)) * this.scale, 306.0f * this.scale, this.paint);
        }
        this.paint.setShader(null);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paint.setStrokeWidth(2.0f * this.scale);
        canvas.drawCircle(((float) (this.currentX + 10)) * this.scale, ((float) (this.currentY + 50)) * this.scale, 6.0f * this.scale, this.paint);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paint.setStrokeWidth(1.0f);
        canvas.drawRect(9.5f * this.scale, 50.0f * this.scale, 266.5f * this.scale, 306.0f * this.scale, this.paint);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(this.currentColor | ViewCompat.MEASURED_STATE_MASK);
        canvas.drawRect(9.5f * this.scale, 316.0f * this.scale, 139.0f * this.scale, 356.0f * this.scale, this.paint);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(this.initialColor | ViewCompat.MEASURED_STATE_MASK);
        canvas.drawRect(138.0f * this.scale, 316.0f * this.scale, 266.5f * this.scale, 356.0f * this.scale, this.paint);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paint.setStrokeWidth(1.0f);
        canvas.drawRect(9.5f * this.scale, 316.0f * this.scale, 266.5f * this.scale, 356.0f * this.scale, this.paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) (276.0f * this.scale), (int) (366.0f * this.scale));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == 0 || event.getAction() == 2) {
            float x = (event.getX() / this.scale) - 10.0f;
            if (x < 0.0f) {
                x = 0.0f;
            }
            if (x > 255.0f) {
                x = 255.0f;
            }
            float y = event.getY() / this.scale;
            if (y < 40.0f) {
                this.currentHue = ((255.0f - x) * 360.0f) / 255.0f;
                updateDerivedColors();
            } else {
                this.currentX = (int) x;
                this.currentY = ((int) y) - 50;
                if (this.currentY < 0) {
                    this.currentY = 0;
                }
                if (this.currentY > 255) {
                    this.currentY = MotionEventCompat.ACTION_MASK;
                }
            }
            int color = getDerivedColorForXY(this.currentX, this.currentY);
            this.currentColor = Color.argb(Color.alpha(this.currentColor), Color.red(color), Color.green(color), Color.blue(color));
            this.listener.colorChanged(this.currentColor, toHexColor(this.currentColor));
            invalidate();
        }
        return true;
    }

    public interface OnColorChangedListener {
        void colorChanged(int i, String str);
    }
}
