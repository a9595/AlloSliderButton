package com.tieorange.allosliderbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.SeekBar;

import static com.tieorange.allosliderbutton.AlloButton.MINIMAL_PROGRESS;

/**
 * Created by tieorange on 08/09/16.
 */

public class VerticalSeekBar extends SeekBar {
    private static final String TAG = VerticalSeekBar.class.getCanonicalName();
    private float mThumbX;
    private float mThumbY;
    private IOnViewMeasuredListener mOnViewMeasuredListener;
    //http://stackoverflow.com/questions/9787906/android-seekbar-solution -- react only on finger move. not tapping

    //thumb onclick:
    private int scaledTouchSlop = 0;
    private float initTouchY = 0;
    private boolean thumbPressed = false;

    public VerticalSeekBar(Context context) {
        super(context);
        init(context);
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

    private void init(Context context) {
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    protected synchronized void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

//        c.save();
//        c.rotate(-90);
//        c.translate(-getHeight(), 0);
        super.onDraw(c);
//        c.restore();

        /*String progressText = String.valueOf(getProgress());
        Rect bounds = new Rect();

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float mThumbSize = 60;
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        mThumbX = progressRatio * width + leftPadding + thumbOffset;
        mThumbY = getHeight() / 2f + bounds.height() / 2f;
        Log.d(TAG, "onDraw() called with: X = [" + mThumbX + "]   Y = " + mThumbY);
*/
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        /*if (!isTouchInThumbBounds(event)) {
            return false;
        }*/


        int i = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // TODO: 30/09/16    //http://stackoverflow.com/questions/9787906/android-seekbar-solution -- react only on finger move. not tapping
                Log.d(TAG, "onTouchEvent: DOWN");

                //thumb:
                Drawable thumb;
                thumb = getThumb();
                if (thumb != null) {
                    //contains current position of thumb in view as bounds
                    RectF bounds = new RectF(thumb.getBounds());

                    thumbPressed = bounds.contains(event.getX(), event.getY());
                    Log.d("Thumb", "onTouchEvent() called with touch:  = [" + event.getX() + "; " + event.getY() + "]");
                    Log.d("Thumb", "onTouchEvent() called with rectangle:  = left:" + bounds.left + "; right: "
                            + bounds.right + "; bottom = " + bounds.bottom + "; top = " + bounds.top);

                    if (thumbPressed) {
                        Log.d("Thumb", "pressed");
                        initTouchY = event.getY();
                        return true;
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                // thumb:
                if (thumbPressed) {
                    if (Math.abs(initTouchY - event.getY()) > scaledTouchSlop) {
                        initTouchY = 0;
                        thumbPressed = false;
                        return super.onTouchEvent(event);
                    }
                    Log.d("Thumb", "move blocked");
                    return true;
                }

                i = getMax() - (int) (getMax() * event.getY() / getHeight());
                Log.d(TAG, "onTouchEvent: MOVE = " + i);
                i = checkMinimalValue(i);
                setProgress(i);
                onSizeChanged(getWidth(), getHeight(), 0, 0);


                break;
            case MotionEvent.ACTION_UP:
                // thumb:
                if (thumbPressed) {
                    Log.d("Thumb", "was pressed -- listener call");
                    thumbPressed = false;
                }


                i = 0;
                i = getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(i);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                // i = checkMinimalValue(i);
                setProgress(MINIMAL_PROGRESS);


                Log.i("ACTION_UP: Progress = ", getProgress() + "");
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent: CANCEL");
                break;
        }
        return true;
    }

    private boolean isTouchInThumbBounds(MotionEvent event) {
        Rect bounds = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            bounds = getThumb().getBounds();
        } else {
            return false;
        }
        int x = (int) event.getX() + getLeft();
        int y = (int) event.getY() + getTop();

        Log.d(TAG, "isTouchInThumbBounds() called with: bounds=  = [" + bounds + " ------- x = " + x + " y = " + y);

        return bounds.contains(x, y);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int height = getHeight();
        int width = getWidth();
        mOnViewMeasuredListener.measured(width, height);
    }

    public void setOnViewMeasuredListener(IOnViewMeasuredListener listener) {
        mOnViewMeasuredListener = listener;
    }

    private int checkMinimalValue(int i) {
        if (i < MINIMAL_PROGRESS) {
            i = MINIMAL_PROGRESS;
        }
        return i;
    }
}