package com.tieorange.allosliderbutton.draggableFAB;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tieorange.allosliderbutton.R;

/**
 * Created by root on 1/7/17.
 */

public class AlloDraggableButton extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = AlloDraggableButton.class.getSimpleName();
    private Context mContext;
    private FloatingActionButton mFab;
    private View mRootLayout;
    private float dY;
    private int lastAction;
    private View mRootView;
    private float mX_initial_position;
    private float mY_initial_position;
    private ViewGroup.LayoutParams mInitLayoutParams;

    public AlloDraggableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlloDraggableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.allo_draggable_button_layout, this);
        mContext = context;

        mRootLayout = findViewById(R.id.rootLayoutFabDraggable);
        mFab = (FloatingActionButton) findViewById(R.id.fabDraggable);

        initFAB();
    }

    private void initFAB() {
        mFab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mX_initial_position = mFab.getX();
                mY_initial_position = mFab.getY();

                Log.d(TAG, "init() called with:  X=" + mX_initial_position + "; Y=" + mY_initial_position);
            }
        });
        mFab.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;

                break;

            case MotionEvent.ACTION_MOVE:
                float yNew = event.getRawY() + dY;
                if (yNew < 0) {
                    view.setY(0);
                } else if (yNew > mY_initial_position) {
                    view.setY(mY_initial_position);
                } else {
                    view.setY(yNew);
                }
                lastAction = MotionEvent.ACTION_MOVE;

                Log.d(TAG, "onTouch() called with:  X=" + view.getX() + "; Y=" + view.getY());
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
                if (lastAction == MotionEvent.ACTION_MOVE) {
                    Log.d(TAG, "onTouch() called with:  X=" + view.getX() + "; Y=" + view.getY());
                    restoreInitialX_Y();
                }
                break;

            default:
                return false;
        }
        return true;


    }


    // TODO: 1/7/17  MAKE IT COME BACK
    private void restoreInitialX_Y() {
        mFab.setX(mX_initial_position);
        mFab.setY(mY_initial_position);
    }
}
