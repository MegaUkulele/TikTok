package com.megaukelele.tiktok;

import android.content.Context;
import android.graphics.Color;
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
        if(child instanceof EditText) {
            ((EditText) child).setTextSize(100);
            ((EditText) child).setTextColor(Color.WHITE);
        }
    }
}
