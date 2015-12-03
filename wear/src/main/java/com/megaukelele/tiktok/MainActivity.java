package com.megaukelele.tiktok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    public static final String mToggleUserTempos = "com.megaUkulele.broadcast.toggleUserTempos";

    private ImageView mGlowingCircle;
    private Button mPlayButton;
    private BPMPicker mBPMPicker;
    private TextView mTapPrompt;
    private boolean playing;
    private Animation growAnimation, shrinkAnimation;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private IntentFilter mIntentFilter;

    //tap BPM variables
    static final int timeout = 2000;
    private ImageButton tapBPMButton;
    private int previousTime = 0;
    private ArrayList<Integer> tapTimes = new ArrayList<Integer>();
    private int count = 0;
    //

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

        mGlowingCircle = (ImageView) findViewById(R.id.ivCircle);
        mPlayButton = (Button) findViewById(R.id.btnPlay);

        tapBPMButton = (ImageButton) findViewById(R.id.tapBPMButton);

        int bpm = mBPMPicker.getValue();
        Log.e(TAG, String.valueOf(bpm));
        setGlowingRate(bpm);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    mGlowingCircle.clearAnimation();
                    mPlayButton.setBackgroundResource(R.drawable.play);
                } else {
                    mGlowingCircle.startAnimation(shrinkAnimation);
                    mPlayButton.setBackgroundResource(R.drawable.pause);
                }
                playing = !playing;
            }
        });

        tapBPMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                int currentTime = (int) time;
                if (currentTime - previousTime > timeout) {
                    tapTimes.clear();
                }
                tapTimes.add(currentTime);
                previousTime = currentTime;

                if (tapTimes.size() > 1) {
                    int last = tapTimes.remove(0);
                    ArrayList<Integer> differences = new ArrayList<Integer>();
                    for (Integer tapTime: tapTimes) {
                        differences.add(tapTime - last);
                        last = tapTime;
                    }
                    int sum = 0;

                    for (Integer d: differences) {
                        sum += d;
                    }

                    int average = sum / differences.size();
                    int bpm = 60000 / average;
                    setGlowingRate(bpm);
                    mBPMPicker.setValue(bpm);
                }

            }
        });

        mBPMPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setGlowingRate(oldVal);
            }
        });

        mTapPrompt = (TextView) findViewById(R.id.textView);

        mTapPrompt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (x1 > x2) {
                                // swipe left
                                Intent i = new Intent(
                                        MainActivity.this,
                                        SettingsActivity.class);
                                startActivity(i);
                            } else {
                                // swipe right
                            }

                        } else {
                            // consider as something else - a screen tap for example
                        }
                        break;
                }

                return true;
            }
        });

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mToggleUserTempos);

    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void setGlowingRate(int bpm) {
        long duration = (long) (1.0 / bpm * 60 * 0.5 * 1000);

        growAnimation = new ScaleAnimation( 0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f );
        growAnimation.setDuration(duration);

        shrinkAnimation = new ScaleAnimation( 1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f );
        shrinkAnimation.setDuration(duration);

        growAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // do tap and vibration
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (playing) mGlowingCircle.startAnimation(shrinkAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (playing) mGlowingCircle.startAnimation(growAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received broadcast at MainActivity");

            if (intent.getAction().equals(mToggleUserTempos)) {
                mTapPrompt = (TextView) findViewById(R.id.textView);
                mTapPrompt.setText("User Tempos");
            }
        }
    };
}
