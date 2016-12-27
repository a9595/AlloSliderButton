package com.tieorange.allosliderbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by tieorange on 08/09/16.
 */

public class VerticalSeekBar extends SeekBar {
  private static final String TAG = VerticalSeekBar.class.getCanonicalName();
  //http://stackoverflow.com/questions/9787906/android-seekbar-solution -- react only on finger move. not tapping

  public VerticalSeekBar(Context context) {
    super(context);
  }

  public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public VerticalSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(h, w, oldh, oldw);
  }

  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(heightMeasureSpec, widthMeasureSpec);
    setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
  }

  protected void onDraw(Canvas c) {
    c.rotate(-90);
    c.translate(-getHeight(), 0);

    super.onDraw(c);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!isEnabled()) {
      return false;
    }
    int i = 0;
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // TODO: 30/09/16    //http://stackoverflow.com/questions/9787906/android-seekbar-solution -- react only on finger move. not tapping
        Log.d(TAG, "onTouchEvent: DOWN");
        break;
      case MotionEvent.ACTION_MOVE:
        Log.d(TAG, "onTouchEvent: MOVE");
        i = getMax() - (int) (getMax() * event.getY() / getHeight());
        setProgress(i);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        break;
      case MotionEvent.ACTION_UP:
        i = 0;
        i = getMax() - (int) (getMax() * event.getY() / getHeight());
        setProgress(i);
        Log.i("UP: Progress", getProgress() + "");
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        setProgress(0);
        break;

      case MotionEvent.ACTION_CANCEL:
        Log.d(TAG, "onTouchEvent: CANCEL");
        break;
    }
    return true;
  }
}