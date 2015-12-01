package com.megaukelele.tiktok;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by GodwinLaw on 11/19/15.
 */
public class BPMPicker extends NumberPicker {

    public BPMPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {

        if(view instanceof EditText){
            //TableLayout.LayoutParams params = new TableLayout.LayoutParams();
            //params.setMargins(15, 15, 15, 15);
            //((EditText) view).setLayoutParams(params);
            ((EditText) view).setTextSize(30);
            ((EditText) view).setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            ((EditText) view).setTextColor(Color.parseColor("#ffffff"));
        }
    }
}
