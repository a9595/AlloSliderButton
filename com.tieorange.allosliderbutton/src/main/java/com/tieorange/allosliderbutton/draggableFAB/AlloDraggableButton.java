package com.tieorange.allosliderbutton.draggableFAB;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
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
    private ITextViewSelectedListener mIRightTextViewSelected;
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
        mAnimationFadeIn = AnimationTools.getAnimationFadeIn(0);
        mAnimationFadeOut = AnimationTools.getAnimationFadeOut(0);
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
                tutorialSlideToGlobal(); // TODO: 1/13/17 RM
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
                    changeVisibilityHUD(false, 0);
                } else {
                    changeVisibilityHUD(true, 0);
                }

                checkFriendsMakeBold(xNewOfFAB);
                makeTextViewsBold(yNewOfFAB, xNewOfFAB);
//                Log.d(TAG, "onTouch() MOVE called with:  X=" + view.getX() + "; Y=" + view.getY());
                Log.d(TAG, "onTouch() MOVE called with:  X=" + xNewOfFAB + "; Y=" + yNewOfFAB);
                break;

            case MotionEvent.ACTION_UP:
                yNewOfFAB = event.getRawY() + mDeltaY;
                xNewOfFAB = event.getRawX() + mDeltaX;
                if (mLastAction == MotionEvent.ACTION_DOWN) {
                    view.performClick();
                    if (mIFabOnClickListener != null) mIFabOnClickListener.onClick();
                }
                if (mLastAction == MotionEvent.ACTION_MOVE) {
                    Log.d(TAG, "onTouch() called with:  X=" + view.getX() + "; Y=" + view.getY());
                    checkListeners(yNewOfFAB, xNewOfFAB);
                    restoreInitialX_Y(yNewOfFAB);
                    changeVisibilityHUD(false, 0);
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

    private void checkListeners(Float yNewOfFAB, Float xNewOfFab) {
        boolean fabInZoneMiddle = isFabInZoneMiddle(yNewOfFAB);
        boolean fabInZoneTop = isFabInZoneTop(yNewOfFAB);
        boolean fingerOnTheRightSide = isFingerOnTheRightSide(xNewOfFab);

        if (fingerOnTheRightSide) {
            if (mIRightTextViewSelected != null) {
                mIRightTextViewSelected.selected();
            }

        } else {
            if (fabInZoneMiddle && mIMiddleTextViewSelectedListener != null) {
                mIMiddleTextViewSelectedListener.selected();

            } else if (fabInZoneTop && mITopTextViewSelectedListener != null) {
                mITopTextViewSelectedListener.selected();
            }
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

    private void changeVisibilityHUD(boolean isVisible, long animationOffset) {
        changeVisibilityView(mProgressLine, isVisible, animationOffset);
        changeVisibilityView(mTvGlobal, isVisible, animationOffset);
        changeVisibilityView(mTvLocal, isVisible, animationOffset);
        changeVisibilityView(mTvCancel, isVisible, animationOffset);
        changeVisibilityView(mTvFriends, isVisible, animationOffset);
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

    private void changeVisibilityView(final View view, boolean isVisible, long animationOffset) {
        if (view == null) return;

        if (isVisible || !mIsVisibleHUD) {
            if (isVisible && !mIsVisibleHUD) {
                Animation animationFadeIn = AnimationTools.getAnimationFadeIn(animationOffset);
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animationFadeIn);
            }
        } else {
            Animation animationFadeOut = AnimationTools.getAnimationFadeOut(animationOffset);
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


    private void restoreInitialX_Y(Float yNewOfFAB) {
        mFab.setX(mX_initial_position);
//        mFab.setY(mY_initial_position);

        // Animation:
        float durationCoefficient = 1.3f; // the bigger - the longer is duration
        Float duration = (mY_initial_position - yNewOfFAB) / durationCoefficient;
        if (duration < 50) duration = 50f;
        mFab.animate().y(AlloDraggableButton.mY_initial_position).setDuration(duration.longValue()).setInterpolator(new DecelerateInterpolator()).start();

    }

    //region Tutorial
    // Button will be swiped to the Top textView and come back (for tutorial)
    public void tutorialSlideToGlobal() {
        // FAB:
        Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tutorialSlideToCancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        int duration = 1500;
        mFab.animate().y(0).setDuration(duration).setInterpolator(new DecelerateInterpolator()).setListener(listener).start();

        // HUD:
        changeVisibilityHUD(true, 0);

        mTvGlobal.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvGlobal.setTypeface(null, Typeface.BOLD);
            }
        }, duration - 700);
    }

    private void tutorialSlideToCancel() {
        int startDelay = 2000;
        Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tutorialSlideToLocal();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        int durationFAB = 1500;
        ViewPropertyAnimator animator = mFab.animate().y(mY_initial_position).setDuration(durationFAB).setInterpolator(new DecelerateInterpolator()).setListener(listener);
        animator.setStartDelay(startDelay);
        animator.start();
        changeVisibilityHUD(false, startDelay + durationFAB);

        mTvGlobal.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvGlobal.setTypeface(null, Typeface.NORMAL);
            }
        }, 500 + startDelay);
    }

    private void tutorialSlideToLocal() {
        int startDelay = 2000;
        Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tutorialSlideToCancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        int durationFAB = 1000;
        ViewPropertyAnimator animator = mFab.animate().y(mY_initial_position / 2).setDuration(durationFAB).setInterpolator(new DecelerateInterpolator()).setListener(listener);
        animator.setStartDelay(startDelay);
        animator.start();
        changeVisibilityHUD(true, startDelay + durationFAB);

        mTvLocal.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvLocal.setTypeface(null, Typeface.BOLD);
            }
        }, 500 + startDelay);
    }
    //endregion

    public void setOnRightTextViewListener(ITextViewSelectedListener iTextViewSelectedListener) {
        mIRightTextViewSelected = iTextViewSelectedListener;
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
