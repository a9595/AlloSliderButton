package com.tieorange.allosliderbutton;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.graphics.Typeface.BOLD;

/**
 * Created by tieorange on 08/09/16.
 */

public class AlloButton extends RelativeLayout {
    private static final String TAG = AlloButton.class.getCanonicalName();
    public static final int MINIMAL_PROGRESS = 0;
    View mRootView;
    private VerticalSeekBar mVerticalSeekBar;
    private TextView mPrivateYawn;
    private TextView mPublicYawn;
    private TextView mCancelYawn;
    public static final int SEEK_BAR_MAX = 100;
    public static final int FIRST_STEP_SNAPPER = 1; //was 20
    private final int SENSITIVITY = 1; //was 10

    private Drawable mDrawableTransparent;
    private Drawable mDrawableNormal;
    private View mFabSendYawn;
    private boolean mIsSmall = true;
    private IOnViewMeasuredListener mOnViewMeasuredListener;
    private int mSmallHeightDp = 100;
    private int mBigHeightDp = 350;

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

        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekbar);
        // TODO: 12/31/16 Refactor:
        mOnViewMeasuredListener = new IOnViewMeasuredListener() {
            @Override
            public void measured(int width, int height) {
                Log.d(TAG, "measured() called with: width = [" + width + "], height = [" + height + "]");
//                changeHeight(true);
                mOnViewMeasuredListener = null;
            }
        };
        mVerticalSeekBar.setOnViewMeasuredListener(mOnViewMeasuredListener);

        mFabSendYawn = findViewById(R.id.fabSendYawn);
        mPrivateYawn = (TextView) findViewById(R.id.localYawn);
        mPublicYawn = (TextView) findViewById(R.id.globalYawn);
        mCancelYawn = (TextView) findViewById(R.id.cancel_yawn);
        initSeekBar();
    }

    /*private void initFAB() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Drawable thumb = mVerticalSeekBar.getThumb();
            mFabSendYawn.setBackground(thumb);
        }

        mFabSendYawn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Tools.setVisibility(View.GONE, view);
                Tools.setVisibility(View.VISIBLE, mVerticalSeekBar);
                return true;
            }
        });
    }
*/
    private void initSeekBar() {
//        changeHeight(true);
        Log.d(TAG, "initSeekBar: width = " + mVerticalSeekBar.getMeasuredWidth() + " height = " + mVerticalSeekBar.getMeasuredHeight());

        mVerticalSeekBar.setProgress(MINIMAL_PROGRESS);
        final int[] firstStepSnapper = {FIRST_STEP_SNAPPER};
        final int privateYawnProgress = SEEK_BAR_MAX / 2;
        final int publicYawnProgress = SEEK_BAR_MAX;
        final int rangeStepYawn = 20;
        final int privateYawnStartRange = privateYawnProgress - rangeStepYawn;
        final int privateYawnEndRange = privateYawnProgress + rangeStepYawn;
        final int publicYawnStartRange = publicYawnProgress - rangeStepYawn;
        final int publicYawnEndRange = publicYawnProgress + rangeStepYawn;

        mVerticalSeekBar.setMax(SEEK_BAR_MAX);

//        final Drawable drawableTransparent = ContextCompat.getDrawable(getContext(), android.R.drawable.screen_background_light_transparent);
        mDrawableTransparent = null;
        mDrawableNormal = getContext().getResources().getDrawable(R.drawable.progress);

        mVerticalSeekBar.setProgressDrawable(mDrawableTransparent);

        mVerticalSeekBar.setOnSeekBarChangeListener(getSeekBarChangeListener(firstStepSnapper, privateYawnStartRange, privateYawnEndRange, publicYawnStartRange, publicYawnEndRange, mDrawableTransparent, mDrawableNormal));
        Log.d(TAG, "initSeekBar: width = " + mVerticalSeekBar.getMeasuredWidth() + " height = " + mVerticalSeekBar.getMeasuredHeight());
    }

    private void changeHeight(boolean isSmall) {
        int width = mVerticalSeekBar.getWidth();
        if (isSmall) {
            mIsSmall = true;
            mSmallHeightDp = Tools.convertDpToPx(mSmallHeightDp, getContext());
            LayoutParams params = new LayoutParams(width, mSmallHeightDp);
            setTopMargin(params);
            mVerticalSeekBar.setLayoutParams(params);
            mVerticalSeekBar.setProgress(SEEK_BAR_MAX);
        } else {
            mIsSmall = false;
            mBigHeightDp = Tools.convertDpToPx(mBigHeightDp, getContext());
            LayoutParams params = new LayoutParams(width, mBigHeightDp);
            setTopMargin(params);
            mVerticalSeekBar.setLayoutParams(params);
            mVerticalSeekBar.setProgress(20);
        }
    }

    private void setTopMargin(LayoutParams params) {
        int top = 0;
        if (mIsSmall) {
            top = Tools.convertDpToPx(350, getContext());
            params.setMargins(0, top, 0, 0);
        } else {
            params.setMargins(0, top, 0, 0);
        }
    }

    @NonNull
    private SeekBar.OnSeekBarChangeListener getSeekBarChangeListener(final int[] firstStepSnapper, final int privateYawnStartRange, final int privateYawnEndRange, final int publicYawnStartRange, final int publicYawnEndRange, final Drawable drawableTransparent, final Drawable drawableNormal) {
        return new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int step = firstStepSnapper[0];
                progress = initStep(seekBar, progress, step);

//                checkHeight(progress);

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
                    firstStepSnapper[0] = 1;
                } else if (progress < step) {
                    firstStepSnapper[0] = FIRST_STEP_SNAPPER;
                }
            }

            private void showHideBorder(int progress) {
                //show border background:
                if (progress >= FIRST_STEP_SNAPPER) {
                    changeVisibility(true);
                } else if (progress < FIRST_STEP_SNAPPER) { // hide border
                    changeVisibility(false);
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

                // TODO: 12/28/16 Remove:
                /*if (seekBar.getProgress() < MINIMAL_PROGRESS) {
                    seekBar.setProgress(MINIMAL_PROGRESS);
                }*/
                if (Math.abs(mProgressAtStartTracking - seekBar.getProgress()) <= SENSITIVITY) {
                    Log.d(TAG, "MATH onStopTrackingTouch() called with: seekBar = [" + seekBar + "]");
                }
            }
        };
    }

    private void checkHeight(int progress) {
        if (mIsSmall) {
            if (progress >= SEEK_BAR_MAX) {
                changeHeight(false);
            }
        } else {
            if (progress <= 5) {
                changeHeight(true);
            }

        }
    }

    private void changeVisibility(boolean isVisible) {
        // TODO: 12/28/16 Add animation:
        Drawable drawable;
        int visible;
        if (isVisible) {
            drawable = mDrawableNormal;
            visible = View.VISIBLE;
        } else {
            drawable = mDrawableTransparent;
            visible = View.GONE;
        }

        mVerticalSeekBar.setProgressDrawable(drawable);
        mPrivateYawn.setVisibility(visible);
        mPublicYawn.setVisibility(visible);
        mCancelYawn.setVisibility(visible);
    }

}
