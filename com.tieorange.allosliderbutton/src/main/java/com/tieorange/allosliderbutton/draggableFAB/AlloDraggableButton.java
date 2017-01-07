package com.tieorange.allosliderbutton.draggableFAB;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tieorange.allosliderbutton.R;

/**
 * Created by root on 1/7/17.
 */

public class AlloDraggableButton extends RelativeLayout implements View.OnTouchListener {
    private Context mContext;
    private FloatingActionButton mFab;
    private View mRootLayout;
    private float dY;
    private int lastAction;
    private View mRootView;

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

        mFab.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
//                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
//                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show();
                break;

            default:
                return false;
        }
        return true;


    }
}
