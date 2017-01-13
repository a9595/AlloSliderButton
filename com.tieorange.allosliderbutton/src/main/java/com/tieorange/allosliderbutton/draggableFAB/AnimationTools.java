package com.tieorange.allosliderbutton.draggableFAB;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by root on 1/8/17.
 */

public class AnimationTools {

    public static final int DURATION_MILLIS_FADE_IN = 1500;
    public static final int DURATION_MILLIS_FADE_OUT = 300;

    public static Animation getAnimationFadeIn(long animationOffset) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(DURATION_MILLIS_FADE_IN);
        fadeIn.setStartOffset(animationOffset);

//        AnimationSet animation = new AnimationSet(false); //change to false
//        animation.addAnimation(fadeIn);
//        animation.addAnimation(fadeOut);
//        view.setAnimation(animation);
        return fadeIn;
    }

    public static Animation getAnimationFadeOut(long animationOffset) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(animationOffset);
        fadeOut.setDuration(DURATION_MILLIS_FADE_OUT);

        return fadeOut;
    }
}
