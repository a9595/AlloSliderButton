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
  View mRootView;
  private AlloButton mImageButton;
  private SimpleFingerGestures mSwipeListener = new SimpleFingerGestures();
  private VerticalSeekBar mVerticalSeekBar;
  private TextView mPrivateYawn;
  private TextView mPublicYawn;
  public static final int SEEK_BAR_MAX = 100;
  public static final int FIRST_STEP_SNAPPER = 20;

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

    mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.vertical_Seekbar);
    mPrivateYawn = (TextView) findViewById(R.id.privateYawn);
    mPublicYawn = (TextView) findViewById(R.id.publicYawn);
    initSeekBar();
  }

  private void initSeekBar() {
    mVerticalSeekBar.setProgress(0);
    //final int progressStep = 90;
    //mVerticalSeekBar.incrementProgressBy(progressStep);
    final int[] stepSize = {FIRST_STEP_SNAPPER};
    final int privateYawnProgress = SEEK_BAR_MAX / 2;
    final int publicYawnProgress = SEEK_BAR_MAX;
    final int rangeStepYawn = 20;
    final int privateYawnStartRange = privateYawnProgress - rangeStepYawn;
    final int privateYawnEndRange = privateYawnProgress + rangeStepYawn;
    final int publicYawnStartRange = publicYawnProgress - rangeStepYawn;
    final int publicYawnEndRange = publicYawnProgress + rangeStepYawn;

    mVerticalSeekBar.setMax(SEEK_BAR_MAX);

    final Drawable drawableTransparent = ContextCompat.getDrawable(getContext(), android.R.drawable.screen_background_light_transparent);
    final Drawable drawableNormal = ContextCompat.getDrawable(getContext(), R.drawable.red_scrubber_progress);
    mVerticalSeekBar.setProgressDrawable(drawableTransparent);

    mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      private int mProgressAtStartTracking;
      private final int SENSITIVITY = 10;

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int step = stepSize[0];
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
          stepSize[0] = 1;
        } else if (progress < step) {
          stepSize[0] = FIRST_STEP_SNAPPER;
        }
      }

      private void showHideBorder(int progress) {
        //show border background:
        if (progress >= FIRST_STEP_SNAPPER) {
          mVerticalSeekBar.setProgressDrawable(drawableNormal);
          mPrivateYawn.setVisibility(VISIBLE);
          mPublicYawn.setVisibility(VISIBLE);
        } else if (progress < FIRST_STEP_SNAPPER) { // hide border
          mVerticalSeekBar.setProgressDrawable(drawableTransparent);
          mPrivateYawn.setVisibility(GONE);
          mPublicYawn.setVisibility(GONE);
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
        if (Math.abs(mProgressAtStartTracking - seekBar.getProgress()) <= SENSITIVITY) {
          Log.d(TAG, "MATH onStopTrackingTouch() called with: seekBar = [" + seekBar + "]");
        }
      }
    });
  }

  @NonNull
  private SimpleFingerGestures.OnFingerGestureListener getSwipeListener() {
    return new SimpleFingerGestures.OnFingerGestureListener() {
      @Override
      public boolean onSwipeUp(int fingers, long duration, double distance) {
        Log.d(TAG, "onSwipeUp() called with: fingers = [" + fingers + "], duration = [" + duration + "], distance = [" + distance + "]");
        return true;
      }

      @Override
      public boolean onSwipeDown(int fingers, long duration, double distance) {
        Log.d(TAG, "onSwipeUp() called with: fingers = [" + fingers + "], duration = [" + duration + "], distance = [" + distance + "]");
        return false;
      }

      @Override
      public boolean onSwipeLeft(int i, long l, double v) {
        return false;
      }

      @Override
      public boolean onSwipeRight(int i, long l, double v) {
        return false;
      }

      @Override
      public boolean onPinch(int i, long l, double v) {
        return false;
      }

      @Override
      public boolean onUnpinch(int i, long l, double v) {
        return false;
      }

      @Override
      public boolean onDoubleTap(int i) {
        return false;
      }
    };
  }

  private void initButton() {
    mImageButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick() called with: v = [" + v + "]");
      }
    });
    //mImageButton.setOnTouchListener(mSwipeListener);
    mImageButton.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
      public void onSwipeTop() {
        Log.d(TAG, "onSwipeTop() called");
      }

      public void onSwipeRight() {
      }

      public void onSwipeLeft() {
      }

      public void onSwipeBottom() {
        Log.d(TAG, "onSwipeBottom() called");
      }
    });
  }
}
