package com.tieorange.allosliderbutton.draggableFAB;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tieorange.allosliderbutton.R;
import com.tieorange.allosliderbutton.Tools;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentIntroAlloButton#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentIntroAlloButton extends Fragment {
    private static final String TAG = FragmentIntroAlloButton.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final boolean DEBUG_MODE = true;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AlloDraggableButton mAlloButton;
    private RelativeLayout mRootLayout;
    private View mBottomSheetView;


    public FragmentIntroAlloButton() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentIntroAlloButton newInstance() {
        FragmentIntroAlloButton fragment = new FragmentIntroAlloButton();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }
    private void initViews() {
        // TODO: 1/17/17 uncomment (Debug only)
        if(DEBUG_MODE) return;
        mAlloButton.initTutorial();
        initTutorialFinishedListener(mAlloButton);

        mAlloButton.setOnFabClickListener(new IFabOnClickListener() {
            @Override
            public void onClick() {
                Log.d("Clicked", "FRAGMENT fab onClick() called");
            }
        });
    }

    private void initTutorialFinishedListener(AlloDraggableButton alloButton) {
        alloButton.setOnTutorialFinishedListener(new ITutorialFinishedListener() {
            @Override
            public void finished() {
                Log.d(TAG, "finished() called");
                recreateAlloButton();
            }
        });
    }

    private void recreateAlloButton() {
        // Remove view
        ViewGroup.LayoutParams layoutParams = mAlloButton.getLayoutParams();
        ((ViewGroup) mAlloButton.getParent()).removeView(mAlloButton);

        // Add view back
        AlloDraggableButton newButton = new AlloDraggableButton(getContext());
        mAlloButton = newButton;

        mRootLayout.addView(newButton, layoutParams);
        mAlloButton.initTutorial();
        initTutorialFinishedListener(mAlloButton);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro_allo_button, container, false);
        mAlloButton = (AlloDraggableButton) view.findViewById(R.id.alloButtonTutorial);
        mRootLayout = (RelativeLayout) view.findViewById(R.id.rootLayoutIntro);
        mBottomSheetView = view.findViewById(R.id.bottomSheet);
        return view;
    }

    public void setBottomSheetHeight(Context context, int heightInDp) {
        int heightConverted = Tools.convertDpToPx(heightInDp, context);
        ViewGroup.LayoutParams layoutParams = mBottomSheetView.getLayoutParams();
        layoutParams.height = heightConverted;
        mBottomSheetView.setLayoutParams(layoutParams);
    }

    /*private void initExtras() {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

}
