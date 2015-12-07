package com.megaukelele.tiktok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    public static final String mToggleUserTempos = "com.megaUkulele.broadcast.toggleUserTempos";
    public static final String mUpdateUserTempos = "com.megaUkulele.broadcast.updateUserTempos";
    public static final String mUpdateBackgroundColor = "com.megaUkulele.broadcast.updateBackgroundColor";

    private final long[] vibrationPattern = {0, 250};
    private final int repeateVibration = -1;

    private ImageView mGlowingCircle;
    private Button mPlayButton, mFirstTemp, mSecondTemp, mThirdTemp;
    private BPMPicker mBPMPicker;
    private TextView mTapPrompt;
    private LinearLayout mUserTempos;
    private boolean playing;
    private boolean userTempoMode;
    private Animation growAnimation, shrinkAnimation;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private IntentFilter mIntentFilter;
    private Vibrator vibrator;


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
        startService(new Intent(this, WatchListenerService.class));

        playing = false;

        initializeViews();
        setListeners();

        userTempoMode = true;
        toggleMetronomeMode();

        int bpm = mBPMPicker.getValue();
        setMetronomeTempo(bpm);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mToggleUserTempos);
    }

    private void initializeViews() {
        mGlowingCircle = (ImageView) findViewById(R.id.ivCircle);
        mPlayButton = (Button) findViewById(R.id.btnPlay);
        mFirstTemp = (Button) findViewById(R.id.btnFirstUserTemp);
        mSecondTemp = (Button) findViewById(R.id.btnSecondUserTemp);
        mThirdTemp = (Button) findViewById(R.id.btnThirdUserTemp);
        mUserTempos = (LinearLayout) findViewById(R.id.llUserTempos);
        mTapPrompt = (TextView) findViewById(R.id.tvMetronomeMode);
        tapBPMButton = (ImageButton) findViewById(R.id.tapBPMButton);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mBPMPicker = (BPMPicker) findViewById(R.id.numberPicker);
        mBPMPicker.setMinValue(30);
        mBPMPicker.setMaxValue(200);
        mBPMPicker.setValue(60);
        mBPMPicker.setWrapSelectorWheel(false);
    }

    private void setListeners() {
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

        mBPMPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setGlowingRate(oldVal);
            }
        });

        mFirstTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserTempo(1);
            }
        });

        mSecondTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserTempo(2);
            }
        });

        mThirdTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserTempo(3);
            }
        });
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

        growAnimation = new ScaleAnimation( 0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f );
        growAnimation.setDuration(duration);

        shrinkAnimation = new ScaleAnimation( 1.0f, 0.3f, 1.0f, 0.3f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f );
        shrinkAnimation.setDuration(duration);

        growAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
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
                // do tap and vibration
                vibrator.vibrate(vibrationPattern, repeateVibration);
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
            Log.d(TAG, intent.getAction());
            if (intent.getAction().equals(mToggleUserTempos)) {
                toggleMetronomeMode();
            } else if (intent.getAction().equals(mUpdateUserTempos)) {
                String first, second, third;
                first = intent.getStringExtra("first");
                second = intent.getStringExtra("second");
                third = intent.getStringExtra("third");
                System.out.println(first + second + third);
                updateUserTempos(first, second, third);
            } else if (intent.getAction().equals(mUpdateBackgroundColor)) {
                int option;
                option = intent.getIntExtra("option", 0);
                setBackgroundGradient(option);
            }
        }
    };

    private void updateUserTempos(String first, String second, String third) {
        mFirstTemp.setText(first);
        mSecondTemp.setText(second);
        mThirdTemp.setText(third);
    }

    private void toggleMetronomeMode() {
        System.out.println("toggling");
        //RelativeLayout.LayoutParams textviewparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!userTempoMode) {
            //textviewparams.setMargins(70, 0, 0, 0);
            //mTapPrompt.setLayoutParams(textviewparams);
            mTapPrompt.setText(R.string.usr_tempo);
            mUserTempos.setVisibility(View.VISIBLE);
            tapBPMButton.setVisibility(View.GONE);
            selectUserTempo(1);
        } else {
            //textviewparams.setMargins(80, 0, 0, 0);
            //mTapPrompt.setLayoutParams(textviewparams);
            mTapPrompt.setText(R.string.tap_instr);
            mUserTempos.setVisibility(View.GONE);
            tapBPMButton.setVisibility(View.VISIBLE);
        }
        userTempoMode = !userTempoMode;
    }

    private void selectUserTempo(int tempo) {
        int temp = 60;
        mFirstTemp.setTextColor(Color.parseColor("#C2C2C7"));
        mSecondTemp.setTextColor(Color.parseColor("#C2C2C7"));
        mThirdTemp.setTextColor(Color.parseColor("#C2C2C7"));
        switch (tempo) {
            case 1:
                mFirstTemp.setTextColor(Color.parseColor("#FFFFFF"));
                temp = Integer.parseInt(mFirstTemp.getText().toString());
                break;
            case 2:
                mSecondTemp.setTextColor(Color.parseColor("#FFFFFF"));
                temp = Integer.parseInt(mSecondTemp.getText().toString());
                break;
            case 3:
                mThirdTemp.setTextColor(Color.parseColor("#FFFFFF"));
                temp = Integer.parseInt(mThirdTemp.getText().toString());
                break;
        }
        setMetronomeTempo(temp);
    }

    private void setMetronomeTempo(int tempo) {
        mBPMPicker.setValue(tempo);
        setGlowingRate(tempo);
    }

    private void setBackgroundGradient(int option) {
        Drawable gradient = getResources().getDrawable(R.drawable.glowing_circle);
        switch (option) {
            case 1:
                gradient = getResources().getDrawable(R.drawable.glowing_circle_blue);
                break;
            case 2:
                gradient = getResources().getDrawable(R.drawable.glowing_circle_blue);
                break;
            case 3:
                gradient = getResources().getDrawable(R.drawable.glowing_circle_blue);
                break;
            default:
                gradient = getResources().getDrawable(R.drawable.glowing_circle);
                break;
        }
        mGlowingCircle.setBackground(gradient);
    }
}
