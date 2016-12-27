package com.tieorange.allosliderbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class Horizonta_swipe extends AppCompatActivity implements GestureDetector.OnGestureListener {
  private GestureDetector gDetector;
  private static final int SWIPE_MIN_DISTANCE = 120;
  private static final int SWIPE_MAX_OFF_PATH = 250;
  private static final int SWIPE_THRESHOLD_VELOCITY = 200;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_horizonta_swipe);

    gDetector = new GestureDetector(Horizonta_swipe.this, this);
    Log.d(Thread.currentThread().getStackTrace()[2].getClassName()
        .substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf(".") + 1) + "." + Thread.currentThread()
        .getStackTrace()[2].getMethodName() + "():" + Thread.currentThread().getStackTrace()[2].getLineNumber(), "it worked");
  }

  @Override
  public boolean onDown(MotionEvent e) {
    return false;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    super.dispatchTouchEvent(ev);
    return gDetector.onTouchEvent(ev);
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

    // Check movement along the Y-axis. If it exceeds
    // SWIPE_MAX_OFF_PATH,
    // then dismiss the swipe.
    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;

    // Swipe from right to left.
    // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
    // and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
      // next();
      Log.e(Thread.currentThread().getStackTrace()[2].getClassName()
          .substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf(".") + 1) + "." + Thread.currentThread()
          .getStackTrace()[2].getMethodName() + "():" + Thread.currentThread().getStackTrace()[2].getLineNumber(), "Swipe from right to left");
      return true;
    }

    // Swipe from left to right.
    // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
    // and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
    if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
      // previous();
      Log.e(Thread.currentThread().getStackTrace()[2].getClassName()
          .substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf(".") + 1) + "." + Thread.currentThread()
          .getStackTrace()[2].getMethodName() + "():" + Thread.currentThread().getStackTrace()[2].getLineNumber(), "Swipe from left to right");
      return true;
    }

    return false;
  }

  @Override
  public void onLongPress(MotionEvent arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void onShowPress(MotionEvent arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean onTouchEvent(MotionEvent me) {
    return gDetector.onTouchEvent(me);
  }

  @Override
  public boolean onSingleTapUp(MotionEvent arg0) {
    // TODO Auto-generated method stub
    return false;
  }
}
