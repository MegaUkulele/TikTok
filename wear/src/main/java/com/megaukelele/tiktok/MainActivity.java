package com.megaukelele.tiktok;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    private ImageView mGlowingCircle;
    private Button mPlayButton;
    private BPMPicker mBPMPicker;
    private boolean playing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playing = false;
        mBPMPicker = (BPMPicker) findViewById(R.id.numberPicker);
        mBPMPicker.setMinValue(30);
        mBPMPicker.setMaxValue(200);
        mBPMPicker.setValue(120);
        mBPMPicker.setWrapSelectorWheel(false);

        final AnimationSet mAnimationSet = new AnimationSet(true);
        Animation grow, shrink;

        mGlowingCircle = (ImageView) findViewById(R.id.ivCircle);
        mPlayButton = (Button) findViewById(R.id.btnPlay);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    mGlowingCircle.clearAnimation();
                } else {
                    mGlowingCircle.startAnimation(mAnimationSet);
                }
                playing = !playing;
            }
        });
        long duration = 500;

        grow = new ScaleAnimation( 0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f );
        //grow.setDuration((long) (60 * 0.5 / 120));
        //grow.setFillEnabled(true);
        grow.setDuration(duration);
        //grow.setRepeatMode(Animation.INFINITE);
        mAnimationSet.addAnimation(grow);

        shrink = new ScaleAnimation( 1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f );
        //shrink.setDuration((long) (60 * 0.5 / 120));
        //shrink.setFillEnabled(true);
        shrink.setDuration(duration);
        shrink.setStartOffset(grow.getDuration());
        //shrink.setRepeatMode(Animation.INFINITE);
        mAnimationSet.addAnimation(shrink);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e(TAG, "Animation started");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e(TAG, "Animation ended");
                animation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.e(TAG, "Animation repeated");
            }
        });

        mAnimationSet.setRepeatMode(Animation.INFINITE);
        mGlowingCircle.setAnimation(mAnimationSet);
        mAnimationSet.start();
        //mGlowingCircle.startAnimation(mAnimationSet);
    }
}
