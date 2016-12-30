package com.tieorange.allosliderbutton;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

import static android.graphics.Typeface.BOLD;

/**
 * Created by tieorange on 08/09/16.
 */

public class AlloButton extends RelativeLayout {
    private static final String TAG = AlloButton.class.getCanonicalName();
    public static final int MINIMAL_PROGRESS = 0;
    View mRootView;
    private AlloButton mImageButton;
    private SimpleFingerGestures mSwipeListener = new SimpleFingerGestures();
    private VerticalSeekBar mVerticalSeekBar;
    private TextView mPrivateYawn;
    private TextView mPublicYawn;
    public static final int SEEK_BAR_MAX = 100;
    public static final int FIRST_STEP_SNAPPER = 10;
    private final int SENSITIVITY = 20;

    private Drawable mDrawableTransparent;
    private Drawable mDrawableNormal;

    public AlloButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlloButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.allo_button_layout, this);
        //mImageButton = (AlloButton) findViewById(R.id.imageButton);
        //mSwipeListener.setOnFingerGestureListener(getSwipeListener());

        //initButton();

        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekbar);
        mPrivateYawn = (TextView) findViewById(R.id.privateYawn);
        mPublicYawn = (TextView) findViewById(R.id.publicYawn);
        initSeekBar();
    }

    private void initSeekBar() {
        mVerticalSeekBar.setProgress(MINIMAL_PROGRESS);
        //final int progressStep = 90;
        //mVerticalSeekBar.incrementProgressBy(progressStep);
        final int[] firstStepSnapper = {FIRST_STEP_SNAPPER};
        final int privateYawnProgress = SEEK_BAR_MAX / 2;
        final int publicYawnProgress = SEEK_BAR_MAX;
        final int rangeStepYawn = 20;
        final int privateYawnStartRange = privateYawnProgress - rangeStepYawn;
        final int privateYawnEndRange = privateYawnProgress + rangeStepYawn;
        final int publicYawnStartRange = publicYawnProgress - rangeStepYawn;
        final int publicYawnEndRange = publicYawnProgress + rangeStepYawn;

        mVerticalSeekBar.setMax(SEEK_BAR_MAX);

//        final Drawable drawableTransparent = ContextCompat.getDrawable(getContext(), android.R.drawable.screen_background_light_transparent);
        mDrawableTransparent = null;
        mDrawableNormal = ContextCompat.getDrawable(getContext(), R.drawable.progress);
        mVerticalSeekBar.setProgressDrawable(mDrawableTransparent);

        mVerticalSeekBar.setOnSeekBarChangeListener(getSeekBarChangeListener(firstStepSnapper, privateYawnStartRange, privateYawnEndRange, publicYawnStartRange, publicYawnEndRange, mDrawableTransparent, mDrawableNormal));
    }

    @NonNull
    private SeekBar.OnSeekBarChangeListener getSeekBarChangeListener(final int[] firstStepSnapper, final int privateYawnStartRange, final int privateYawnEndRange, final int publicYawnStartRange, final int publicYawnEndRange, final Drawable drawableTransparent, final Drawable drawableNormal) {
        return new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int step = firstStepSnapper[0];
                progress = initStep(seekBar, progress, step);

                snapThumb(progress, step);
                showHideBorder(progress);
                privateYawnBold(progress, privateYawnStartRange, privateYawnEndRange, mPrivateYawn);
                privateYawnBold(progress, publicYawnStartRange, publicYawnEndRange, mPublicYawn);
            }

            private int initStep(SeekBar seekBar, int progress, int step) {
                progress = (progress / step) * step; // step
                seekBar.setProgress(progress);
                return progress;
            }

            private void privateYawnBold(int progress, int startRange, int endRange, TextView textView) {
                // bold
                if (progress >= startRange && progress <= endRange) {
                    textView.setTypeface(textView.getTypeface(), BOLD);
                } // normal
                if (progress < startRange || progress > endRange) {
                    textView.setTypeface(Typeface.DEFAULT);
                }
            }

            private void snapThumb(int progress, int step) {
                if (progress >= step) {
                    firstStepSnapper[0] = 1;
                } else if (progress < step) {
                    firstStepSnapper[0] = FIRST_STEP_SNAPPER;
                }
            }

            private void showHideBorder(int progress) {
                //show border background:
                if (progress >= FIRST_STEP_SNAPPER) {
                    changeVisibility(true);
                } else if (progress < FIRST_STEP_SNAPPER) { // hide border
                    changeVisibility(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch() called with: seekBar = [" + seekBar + "]");
                mProgressAtStartTracking = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch() called with: seekBar = [" + seekBar + "]");

                // TODO: 12/28/16 Remove:
                /*if (seekBar.getProgress() < MINIMAL_PROGRESS) {
                    seekBar.setProgress(MINIMAL_PROGRESS);
                }*/
                if (Math.abs(mProgressAtStartTracking - seekBar.getProgress()) <= SENSITIVITY) {
                    Log.d(TAG, "MATH onStopTrackingTouch() called with: seekBar = [" + seekBar + "]");
                }
            }
        };
    }

    private void changeVisibility(boolean isVisible) {
        // TODO: 12/28/16 Add animation:
        Drawable drawable;
        int visible;
        if (isVisible) {
            drawable = mDrawableNormal;
            visible = View.VISIBLE;
        } else {
            drawable = mDrawableTransparent;
            visible = View.GONE;
        }

        mVerticalSeekBar.setProgressDrawable(drawable);
        mPrivateYawn.setVisibility(visible);
        mPublicYawn.setVisibility(visible);
    }

}
