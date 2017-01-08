package com.tieorange.allosliderbutton.draggableFAB;

import android.content.Context;
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
    private static int THRESHOLD_SNAPPING = 30;
    private Context mContext;
    private FloatingActionButton mFab;
    private float mDeltaY;
    private int mLastAction;
    private float mX_initial_position;
    private float mY_initial_position;
    private View mRootView;
    private View mProgressLine;
    private TextView mTvGlobal;
    private TextView mTvLocal;
    private TextView mTvCancel;
    private float mProgressLineMiddleY;

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
                mY_initial_position = mFab.getY();
                mProgressLineMiddleY = mY_initial_position / 2;
                Log.d(TAG, "init() called with:  X=" + mX_initial_position + "; Y=" + mY_initial_position);
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
                float yNew = event.getRawY() + mDeltaY;

                performMove(view, yNew);

//                changeVisibilityView(mProgressLine, true);
                changeVisibilityHUD(true);

                changeVisibilityTextViews(yNew);

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

    private void changeVisibilityTextViews(float yNew) {
        // Middle:
        float mediumHighestPoint = mProgressLineMiddleY - THRESHOLD_SNAPPING;
        float mediumLowestPoint = mProgressLineMiddleY + THRESHOLD_SNAPPING;
        if (yNew < mediumLowestPoint && yNew > mediumHighestPoint) {
            Log.d(TAG, "MIDDLE [" + yNew + "]");
        }
    }

    // TODO: 1/8/17 Animate
    private void changeVisibilityView(View view, boolean isVisible) {
        if (view == null) return;

        int visibility = View.VISIBLE;
        if (!isVisible) {
            visibility = View.GONE;
        }

        mProgressLine.setVisibility(visibility);
    }

    private void performMove(View view, float yNew) {
        if (yNew < 0) {
            view.setY(0);
        } else if (yNew > mY_initial_position) {
            view.setY(mY_initial_position);
        } else {
            view.setY(yNew);
        }
        mLastAction = MotionEvent.ACTION_MOVE;
    }


    // TODO: 1/8/17 Animate
    private void restoreInitialX_Y() {
        mFab.setX(mX_initial_position);
        mFab.setY(mY_initial_position);
    }
}
