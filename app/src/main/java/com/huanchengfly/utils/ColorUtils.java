package com.huanchengfly.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;

public final class ColorUtils {
    public static @ColorInt
    int getColorByAttr(Context context, @AttrRes int attr, @ColorRes int defaultColor) {
        int[] attrs = new int[]{attr};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int color = typedArray.getColor(0, context.getResources().getColor(defaultColor));
        typedArray.recycle();
        return color;
    }

    public static int getDarkerColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv
        // make darker
        hsv[1] = hsv[1] + 0.1f; // more saturation
        hsv[2] = hsv[2] - 0.1f; // less brightness
        return Color.HSVToColor(hsv);
    }

    public static int getLighterColor(@ColorInt int color) {
        return getLighterColor(color, 0.1f);
    }

    public static int getLighterColor(@ColorInt int color, float i) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv
        // make lighter
        hsv[1] = hsv[1] - i; // less saturation
        hsv[2] = hsv[2] + i; // more brightness
        return Color.HSVToColor(hsv);
    }

    public static int greifyColor(@ColorInt int color, @FloatRange(from = 0f, to = 1f) float sat) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] - sat;
        hsv[2] = hsv[2] - (sat / 3);
        return Color.HSVToColor(hsv);
    }
}
