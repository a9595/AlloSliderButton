package com.tieorange.allosliderbutton.draggableFAB;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tieorange.allosliderbutton.R;

/**
 * Created by root on 1/7/17.
 */

public class AlloDraggableButton extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = AlloDraggableButton.class.getSimpleName();
    private static int THRESHOLD_SNAPPING = 50;
    private Context mContext;
    private FloatingActionButton mFab;
    private float mDeltaY;
    private int mLastAction;
    private float mX_initial_position;
    private static Float mY_initial_position = null;
    private View mRootView;
    private View mProgressLine;
    private TextView mTvGlobal;
    private TextView mTvLocal;
    private TextView mTvCancel;
    private Float mProgressLineTopY;
    private float mTopHighestPoint;
    private float mTopLowestPoint;
    private float mProgressLineMiddleY;
    private float mMediumHighestPoint;
    private float mMediumLowestPoint;
    private boolean mTvLocalIsBold = false;
    private boolean mIsFabInMiddleZone;

    public AlloDraggableButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public AlloDraggableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mRootView = inflate(context, R.layout.allo_draggable_button_layout, this);
        mFab = (FloatingActionButton) findViewById(R.id.fabDraggable);
        mProgressLine = findViewById(R.id.progressLine);
        mTvGlobal = (TextView) findViewById(R.id.global);
        mTvLocal = (TextView) findViewById(R.id.local);
        mTvCancel = (TextView) findViewById(R.id.cancel);

        initFAB();
    }

    private void initFAB() {
        mFab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mX_initial_position = mFab.getX();
                if (mY_initial_position == null) {
                    mY_initial_position = mFab.getY();

                    // Highest (Global)
                    mProgressLineTopY = 0f;
                    mTopHighestPoint = mProgressLineTopY - THRESHOLD_SNAPPING;
                    mTopLowestPoint = mProgressLineTopY + THRESHOLD_SNAPPING;

                    // Middle (Local)
                    mProgressLineMiddleY = mY_initial_position / 2;
                    mMediumHighestPoint = mProgressLineMiddleY - THRESHOLD_SNAPPING;
                    mMediumLowestPoint = mProgressLineMiddleY + THRESHOLD_SNAPPING;
                    Log.d(TAG, "OnGlobalLayoutListener() called with:  X=" + mX_initial_position + "; Y=" + mY_initial_position);
                    Log.d(TAG, "OnGlobalLayoutListener() called with:  mMediumHighestPoint =" + mMediumHighestPoint + ";   mMediumLowestPoint=" + mMediumLowestPoint);
                }
            }
        });
        mFab.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDeltaY = view.getY() - event.getRawY();
                mLastAction = MotionEvent.ACTION_DOWN;

                break;

            case MotionEvent.ACTION_MOVE:
                Float yNew = event.getRawY() + mDeltaY;

                performMove(view, yNew);

                if (yNew >= mY_initial_position) {
                    changeVisibilityHUD(false);
                } else {
                    changeVisibilityHUD(true);
                }

//                makeTextViewsBold(yNew);
//                makeTextViewsBoldGeneric(yNew, mMediumLowestPoint, mMediumHighestPoint, mTvLocal);
//                makeTextViewsBoldGeneric(yNew, mTopLowestPoint, mTopHighestPoint, mTvGlobal);
                makeTextViewsBold(yNew);
                Log.d(TAG, "onTouch() called with:  X=" + view.getX() + "; Y=" + view.getY());
                break;

            case MotionEvent.ACTION_UP:
                if (mLastAction == MotionEvent.ACTION_DOWN)
                    Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
                if (mLastAction == MotionEvent.ACTION_MOVE) {
                    Log.d(TAG, "onTouch() called with:  X=" + view.getX() + "; Y=" + view.getY());
                    restoreInitialX_Y();
                    changeVisibilityHUD(false);
                }

                break;

            default:
                return false;
        }
        return true;


    }

    private void changeVisibilityHUD(boolean isVisible) {
        changeVisibilityView(mProgressLine, isVisible);
        changeVisibilityView(mTvGlobal, isVisible);
        changeVisibilityView(mTvLocal, isVisible);
        changeVisibilityView(mTvCancel, isVisible);
    }

    private void makeTextViewsBold(Float yNew) {
        // TOP:
        boolean mIsFabInZone = yNew < mTopLowestPoint && yNew > mTopHighestPoint;
        boolean isFabNotInZone = yNew > mTopLowestPoint || yNew < mTopHighestPoint;
        if (mIsFabInZone) {
            mTvGlobal.post(new Runnable() {
                @Override
                public void run() {
                    mTvGlobal.setTypeface(null, Typeface.BOLD);

                }
            });
            Log.d(TAG, "In Zone Top [" + yNew + "]");
        }
        if (isFabNotInZone) {
            mTvGlobal.post(new Runnable() {
                @Override
                public void run() {
                    mTvLocal.setTypeface(null, Typeface.NORMAL);
                }
            });
        }

        // TODO: 1/8/17 RM
        if (yNew > 50) {
            mTvGlobal.post(new Runnable() {
                @Override
                public void run() {
                    mTvLocal.setTypeface(null, Typeface.NORMAL);
                }
            });
        }


        // MIDDLE:
        boolean mIsFabInZoneMiddle = yNew < mMediumLowestPoint && yNew > mMediumHighestPoint;
        boolean isFabNotInZoneMiddle = yNew > mMediumLowestPoint || yNew < mMediumHighestPoint;
        if (mIsFabInZoneMiddle) {
            mTvLocal.post(new Runnable() {
                @Override
                public void run() {
                    mTvLocal.setTypeface(null, Typeface.BOLD);
                    mTvGlobal.setTypeface(null, Typeface.NORMAL); // TODO: 1/8/17 RM

                }
            });
            Log.d(TAG, "In Zone Middle [" + yNew + "]");
        }
        if (isFabNotInZoneMiddle) {
            mTvLocal.post(new Runnable() {
                @Override
                public void run() {
                    mTvLocal.setTypeface(null, Typeface.NORMAL);
                }
            });
        }


    }

    // TODO: 1/8/17 Animate
    private void changeVisibilityView(View view, boolean isVisible) {
        if (view == null) return;

        int visibility = View.VISIBLE;
        if (!isVisible) {
            visibility = View.GONE;
        }

        view.setVisibility(visibility);
    }

    private void performMove(View view, Float yNew) {
        if (yNew < 0) {
            yNew = 0f;
        } else if (yNew > mY_initial_position) {
            yNew = mY_initial_position;
        }
        view.setY(yNew);
        mLastAction = MotionEvent.ACTION_MOVE;
    }


    // TODO: 1/8/17 Animate
    private void restoreInitialX_Y() {
        mFab.setX(mX_initial_position);
        mFab.setY(mY_initial_position);
    }
}
