package com.mcal.uidesigner.appwizard.runtime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

public class Backgrounds {
    public static BitmapDrawable createLinedBackground(Context context, int color) {
        int size = (int) (6.0f * context.getResources().getDisplayMetrics().density);
        Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(color);
        c.drawRect(0.0f, 0.0f, (float) bm.getWidth(), (float) bm.getHeight(), p);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.8f;
        p.setColor(Color.HSVToColor(hsv));
        c.drawLine(0.0f, 0.0f, (float) bm.getWidth(), (float) bm.getHeight(), p);
        BitmapDrawable d = new BitmapDrawable(bm);
        d.setDither(false);
        d.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return d;
    }
}
