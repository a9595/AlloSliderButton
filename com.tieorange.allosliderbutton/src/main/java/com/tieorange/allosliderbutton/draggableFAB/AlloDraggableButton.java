package com.tieorange.allosliderbutton.draggableFAB;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tieorange.allosliderbutton.R;

/**
 * Created by root on 1/7/17.
 */

public class AlloDraggableButton extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = AlloDraggableButton.class.getSimpleName();
    private static final float MAX_X_MOVE_ON_CLICK = 30f;
    private static final float MAX_Y_MOVE_ON_CLICK = 30f;
    private static Float THRESHOLD_SHOW_HUD;
    private static final int PERCENTS_OF_THRESHOLD = 100; // how many percents should view go to show HUD (global, local)
    private static int THRESHOLD_SNAPPING = 90;

    private Context mContext;
    private com.github.clans.fab.FloatingActionButton mFab;
    private float mDeltaY;
    private float mDeltaX;

    private int mLastAction;
    private float mX_initial_position;
    private static Float mY_initial_position = null;
    private View mRootView;
    private View mProgressLine;
    private TextView mTvGlobal;
    private TextView mTvLocal;
    private TextView mTvCancel;
    private TextView mTvFriends;

    private Float mProgressLineTopY;
    private float mTopHighestPoint;
    private float mTopLowestPoint;
    private float mProgressLineMiddleY;
    private float mMediumHighestPoint;
    private float mMediumLowestPoint;
    private boolean mTvLocalIsBold = false;
    private boolean mIsFabInMiddleZone;

    private ITextViewSelectedListener mITopTextViewSelectedListener;
    private ITextViewSelectedListener mIMiddleTextViewSelectedListener;
    private IFabOnClickListener mIFabOnClickListener;
    private IPercentsSliderListener mIPercentsSliderListener;

    private Animation mAnimationFadeIn;
    private Animation mAnimationFadeOut;
    private boolean mIsVisibleHUD;

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
        mFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabDraggable);
        mProgressLine = findViewById(R.id.progressLine);
        mTvGlobal = (TextView) findViewById(R.id.global);
        mTvLocal = (TextView) findViewById(R.id.local);
        mTvCancel = (TextView) findViewById(R.id.cancel);
        mTvFriends = (TextView) findViewById(R.id.friends);

        initFAB();
        initAnimations();

    }

    private void initAnimations() {
        mAnimationFadeIn = AnimationTools.getAnimationFadeIn();
        mAnimationFadeOut = AnimationTools.getAnimationFadeOut();
//        mProgressLine.startAnimation(animationFadeIn);
//        mProgressLine.setVisibility(VISIBLE);
    }

    private void initFAB() {
        mFab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mX_initial_position = mFab.getX();
                if (mY_initial_position == null) {
                    mY_initial_position = mFab.getY();

                    THRESHOLD_SHOW_HUD = (mY_initial_position * PERCENTS_OF_THRESHOLD) / 100;

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

//                    mFab.setY(mY_initial_position / 2);
                }
            }
        });

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mFab.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        Float yNewOfFAB;
        Float xNewOfFAB;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(event.getX()) < MAX_X_MOVE_ON_CLICK || Math.abs(event.getY()) < MAX_Y_MOVE_ON_CLICK) {
                    view.performClick();
                } else {
                    mDeltaY = view.getY() - event.getRawY();
                    mDeltaX = view.getX() - event.getRawX();
                    mLastAction = MotionEvent.ACTION_DOWN;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                yNewOfFAB = event.getRawY() + mDeltaY;
                xNewOfFAB = event.getRawX() + mDeltaX;

                performMove(view, yNewOfFAB);

                if (yNewOfFAB >= THRESHOLD_SHOW_HUD) {
                    changeVisibilityHUD(false);
                } else {
                    changeVisibilityHUD(true);
                }

                checkFriendsMakeBold(xNewOfFAB);
                makeTextViewsBold(yNewOfFAB, xNewOfFAB);
//                Log.d(TAG, "onTouch() MOVE called with:  X=" + view.getX() + "; Y=" + view.getY());
                Log.d(TAG, "onTouch() MOVE called with:  X=" + xNewOfFAB + "; Y=" + yNewOfFAB);
                break;

            case MotionEvent.ACTION_UP:
                yNewOfFAB = event.getRawY() + mDeltaY;
                if (mLastAction == MotionEvent.ACTION_DOWN) {
                    view.performClick();
                    if (mIFabOnClickListener != null) mIFabOnClickListener.onClick();
                }
                if (mLastAction == MotionEvent.ACTION_MOVE) {
                    Log.d(TAG, "onTouch() called with:  X=" + view.getX() + "; Y=" + view.getY());
                    checkListeners(yNewOfFAB);
                    restoreInitialX_Y(yNewOfFAB);
                    changeVisibilityHUD(false);
                }
                break;

            default:
                return false;
        }
        return true;


    }

    private void checkFriendsMakeBold(Float xNewOfFAB) {
        if (isFingerOnTheRightSide(xNewOfFAB)) { // friends
            mTvFriends.setTypeface(null, Typeface.BOLD);
        } else { // local
            mTvFriends.setTypeface(null, Typeface.NORMAL);
        }
    }

    private boolean isFingerOnTheRightSide(Float xNewOfFAB) {
        return xNewOfFAB >= mX_initial_position;
    }

    private void checkListeners(Float yNewOfFAB) {
        boolean fabInZoneMiddle = isFabInZoneMiddle(yNewOfFAB);
        boolean fabInZoneTop = isFabInZoneTop(yNewOfFAB);

        if (fabInZoneMiddle && mIMiddleTextViewSelectedListener != null) {
            mIMiddleTextViewSelectedListener.selected();

        } else if (fabInZoneTop && mITopTextViewSelectedListener != null) {
            mITopTextViewSelectedListener.selected();
        }

        // Y_init = 400; Y_new = 200; X = 50%;
        // Calculate the percentage of slider before finger release
        if (mIPercentsSliderListener != null) {
            int percents = (int) ((yNewOfFAB * 100) / mY_initial_position);
            percents = 100 - percents;
            if (percents > 100) percents = 100;
            if (percents < 0) percents = 0;

            if (mProgressLine.getVisibility() == View.VISIBLE)
                mIPercentsSliderListener.released(percents);
        }


        /*if (mTvGlobal != null) {
            boolean isGlobalBold = mTvGlobal.getTypeface().isBold();
            if (isGlobalBold) {
                if (mITopTextViewSelectedListener != null) mITopTextViewSelectedListener.selected();
            }
        } else if (mTvLocal != null) {
            boolean isLocalBold = mTvLocal.getTypeface().isBold();
            if (isLocalBold) {
                mIMiddleTextViewSelectedListener.selected();
            }
        }*/

    }

    private void changeVisibilityHUD(boolean isVisible) {
        changeVisibilityView(mProgressLine, isVisible);
        changeVisibilityView(mTvGlobal, isVisible);
        changeVisibilityView(mTvLocal, isVisible);
        changeVisibilityView(mTvCancel, isVisible);
        changeVisibilityView(mTvFriends, isVisible);
        mIsVisibleHUD = isVisible;
    }

    private void makeTextViewsBold(Float yNew, Float xNewOfFAB) {
        if (isFingerOnTheRightSide(xNewOfFAB)) {
            mTvGlobal.post(new Runnable() {
                @Override
                public void run() {
                    mTvGlobal.setTypeface(null, Typeface.NORMAL);
                }
            });
            mTvLocal.post(new Runnable() {
                @Override
                public void run() {
                    mTvLocal.setTypeface(null, Typeface.NORMAL);
                }
            });
            return;
        }

        // TOP:
        boolean mIsFabInZoneTop = isFabInZoneTop(yNew);
        boolean isFabNotInZone = yNew > mTopLowestPoint || yNew < mTopHighestPoint;
        if (mIsFabInZoneTop) {
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
                    mTvGlobal.setTypeface(null, Typeface.NORMAL);
                }
            });
        }


        // MIDDLE:
        boolean mIsFabInZoneMiddle = isFabInZoneMiddle(yNew);
        boolean isFabNotInZoneMiddle = yNew > mMediumLowestPoint || yNew < mMediumHighestPoint;
        if (mIsFabInZoneMiddle) {
            mTvLocal.post(new Runnable() {
                @Override
                public void run() {
                    mTvLocal.setTypeface(null, Typeface.BOLD);
//                    mTvGlobal.setTypeface(mTvGlobal.getTypeface(), Typeface.NORMAL); // TODO: 1/8/17 RM

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

    private boolean isFabInZoneMiddle(Float yNew) {
        return yNew < mMediumLowestPoint && yNew > mMediumHighestPoint;
    }

    private boolean isFabInZoneTop(Float yNew) {
        return yNew < mTopLowestPoint && yNew > mTopHighestPoint;
    }

    // TODO: 1/8/17 Animate
    private void changeVisibilityView(final View view, boolean isVisible) {
        if (view == null) return;

        if (isVisible || !mIsVisibleHUD) {
            if (isVisible && !mIsVisibleHUD) {
                Animation animationFadeIn = AnimationTools.getAnimationFadeIn();
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animationFadeIn);
            }
        } else {
            Animation animationFadeOut = AnimationTools.getAnimationFadeOut();
            animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animationFadeOut);
        }

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
    private void restoreInitialX_Y(Float yNewOfFAB) {
        mFab.setX(mX_initial_position);
//        mFab.setY(mY_initial_position);

        // Animation:
        float durationCoefficient = 1.3f; // the bigger - the longer is duration
        Float duration = (mY_initial_position - yNewOfFAB) / durationCoefficient;
        if (duration < 50) duration = 50f;
        mFab.animate().y(AlloDraggableButton.mY_initial_position).setDuration(duration.longValue()).setInterpolator(new DecelerateInterpolator()).start();

    }

    public void setOnTopTextViewListener(ITextViewSelectedListener iTextViewSelectedListener) {
        mITopTextViewSelectedListener = iTextViewSelectedListener;
    }

    public void setOnMiddleTextViewListener(ITextViewSelectedListener iTextViewSelectedListener) {
        mIMiddleTextViewSelectedListener = iTextViewSelectedListener;
    }

    public void setOnPercentsSliderListener(IPercentsSliderListener iPercentsSliderListener) {
        mIPercentsSliderListener = iPercentsSliderListener;
    }

    public void setOnFabClickListener(IFabOnClickListener iFabOnClickListener) {
        mIFabOnClickListener = iFabOnClickListener;
    }

    public void setFabDrawable(Drawable drawable) {
        mFab.setImageDrawable(drawable);
    }

    /**
     * Make a button not slidable
     */
    public void hideSlider() {
        mFab.setOnTouchListener(null);
    }

    /**
     * Make a button slidable and show a slider
     */
    public void showSlider() {
        mFab.setOnTouchListener(this);
    }
}
