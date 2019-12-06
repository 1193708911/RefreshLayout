package com.ssports.refreshlayout.utils;

import android.content.Context;

/**
 * create  by tomcat on 2019-12-06
 */
public class ScreenUtils {

    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);

    }
}
