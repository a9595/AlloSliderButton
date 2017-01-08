package com.tieorange.allosliderbutton.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tieorange.allosliderbutton.draggableFAB.AlloDraggableButton;
import com.tieorange.allosliderbutton.draggableFAB.ITextViewSelectedListener;

public class DraggableActivity extends AppCompatActivity {
    private AlloDraggableButton mAlloButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draggable);
        mAlloButton = (AlloDraggableButton) findViewById(R.id.alloButton);

        mAlloButton.setOnMiddleTextViewListener(new ITextViewSelectedListener() {
            @Override
            public void selected() {
                Toast.makeText(DraggableActivity.this, "middle", Toast.LENGTH_SHORT).show();
            }
        });

        mAlloButton.setOnTopTextViewListener(new ITextViewSelectedListener() {
            @Override
            public void selected() {
                Toast.makeText(DraggableActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
        });

//        mAlloButton.seton

    }


}