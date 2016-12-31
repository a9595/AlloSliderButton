package com.tieorange.allosliderbutton;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by root on 12/31/16.
 */
public class Tools {
    public static void setVisibility(int visibility, View view) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    static int convertDpToPx(int dp, Context context) {
        return Math.round(
                dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int convertPxToDp(int px) {
        return Math.round(
                px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
