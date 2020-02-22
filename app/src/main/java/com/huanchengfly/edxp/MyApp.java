package com.huanchengfly.edxp;

import android.content.Context;

import com.huanchengfly.theme.interfaces.ThemeSwitcher;
import com.huanchengfly.theme.utils.ThemeUtils;

import org.meowcat.bugcatcher.MeowCatApplication;
import org.meowcat.edxposed.manager.R;

public class MyApp extends MeowCatApplication implements ThemeSwitcher {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeUtils.init(this);
    }

    //TODO: 多主题支持
    @Override
    public int getColorByAttr(Context context, int attrId) {
        return getColorByAttr(context, attrId, R.color.transparent);
    }

    @Override
    public int getColorById(Context context, int colorId) {
        return context.getResources().getColor(colorId);
    }
}
