package com.huanchengfly.edxp.widgets.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.appcompat.widget.SwitchCompat;

import org.meowcat.edxposed.manager.R;

import de.robv.android.xposed.installer.XposedApp;

public class TintSwitchCompat extends SwitchCompat {
    public TintSwitchCompat(Context context) {
        this(context, null);
    }

    public TintSwitchCompat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public TintSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        int[] colors = new int[]{XposedApp.getColor(getContext()), getContext().getResources().getColor(R.color.color_switch_track_bg)
                , getContext().getResources().getColor(R.color.color_switch_track_disabled_bg)};
        int[][] states = new int[3][];
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_checked};
        states[0] = new int[]{android.R.attr.state_enabled};
        states[2] = new int[]{};
        setTrackTintList(new ColorStateList(states, colors));
    }
}
