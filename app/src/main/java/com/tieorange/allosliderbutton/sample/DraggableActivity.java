package com.tieorange.allosliderbutton.sample;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tieorange.allosliderbutton.draggableFAB.AlloDraggableButton;
import com.tieorange.allosliderbutton.draggableFAB.IFabOnClickListener;
import com.tieorange.allosliderbutton.draggableFAB.IPercentsSliderListener;
import com.tieorange.allosliderbutton.draggableFAB.ITextViewSelectedListener;

public class DraggableActivity extends AppCompatActivity {
    private AlloDraggableButton mAlloButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draggable);
        mAlloButton = (AlloDraggableButton) findViewById(R.id.alloButton);

//        initFAB();
//        mAlloButton.tutorialSlideToGlobal();
    }

    private void initFAB() {
        mAlloButton.setOnMiddleTextViewListener(new ITextViewSelectedListener() {
            @Override
            public void selected() {
//                Toast.makeText(DraggableActivity.this, "middle", Toast.LENGTH_SHORT).show();
            }
        });

        mAlloButton.setOnTopTextViewListener(new ITextViewSelectedListener() {
            @Override
            public void selected() {
                Toast.makeText(DraggableActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
        });

        mAlloButton.setOnFabClickListener(new IFabOnClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(DraggableActivity.this, "Click", Toast.LENGTH_SHORT).show();
            }
        });

        mAlloButton.setOnPercentsSliderListener(new IPercentsSliderListener() {
            @Override
            public void released(int percents) {
                Toast.makeText(DraggableActivity.this, percents + "%", Toast.LENGTH_SHORT).show();
            }
        });

        mAlloButton.setOnRightTextViewListener(new ITextViewSelectedListener() {
            @Override
            public void selected() {
                Toast.makeText(DraggableActivity.this, "Friends", Toast.LENGTH_SHORT).show();
            }
        });

//        Drawable fabDrawable = ContextCompat.getDrawable(this, R.drawable.fab_add);
//        mAlloButton.setFabDrawable(fabDrawable);
    }


}