package com.example.garbage.tools;

import android.content.Context;

public class GarbageTools {

    public static int convertDpsTpPixels(int dps, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}