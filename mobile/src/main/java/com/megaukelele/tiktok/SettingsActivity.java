package com.megaukelele.tiktok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends Activity {
    private static String TAG = "SettingsActivity";
    public Button settings, mSendtoWear, green, red, blue, yellow;
    private GoogleApiClient mGoogleApiClient;
    private NumberPicker  mFirstTemp, mSecondTemp, mThirdTemp;
    private CheckBox mUserTempToggle;
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private static final String TOGGLE_MESSAGE = "toggle_user_tempos";
    private static final String UPDATE_TEMPO_MESSAGE = "update_user_tempos";
    private static final String UPDATE_BACKGROUND_COLOR = "update_background_color";
    private String nodeId;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        mSendtoWear = (Button) findViewById(R.id.btnSendtoWear);
        green = (Button) findViewById(R.id.green);
        red = (Button) findViewById(R.id.red);
        blue = (Button) findViewById(R.id.blue);
        yellow = (Button) findViewById(R.id.yellow);
        mFirstTemp = (NumberPicker) findViewById(R.id.npFirstTemp);
        mSecondTemp = (NumberPicker) findViewById(R.id.npSecondTemp);
        mThirdTemp = (NumberPicker) findViewById(R.id.npThirdTemp);
        mUserTempToggle = (CheckBox) findViewById(R.id.cbUserTempos);
        mFirstTemp.setMinValue(30);
        mFirstTemp.setMaxValue(200);
        mFirstTemp.setValue(120);
        mFirstTemp.setWrapSelectorWheel(false);
        mSecondTemp.setMinValue(30);
        mSecondTemp.setMaxValue(200);
        mSecondTemp.setValue(120);
        mSecondTemp.setWrapSelectorWheel(false);
        mThirdTemp.setMinValue(30);
        mThirdTemp.setMaxValue(200);
        mThirdTemp.setValue(120);
        mThirdTemp.setWrapSelectorWheel(false);

        enableComponents();

        mSendtoWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserTempos();
            }
        });

        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackgroundColor(0);
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackgroundColor(1);
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackgroundColor(2);
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackgroundColor(3);
            }
        });

        mUserTempToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setUserTemposMode(isChecked);
                enableComponents();
            }
        });

        ImageButton btn_temp=(ImageButton)findViewById(R.id.back);
        btn_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        SettingsActivity.this,
                        MainActivity.class);
                startActivity(i);
            }
        });

        initApi();

        // this is a temporary gesture just for demonstrating two different actions with one image button
        // swiping left triggers sending a message to wear, swiping right triggers switching activities
//        btn_temp.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        x1 = event.getX();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        x2 = event.getX();
//                        float deltaX = x2 - x1;
//                        if (Math.abs(deltaX) > MIN_DISTANCE) {
//                            if (x1 > x2) {
//                                // swipe left
//                                Log.d(TAG, "swipe left!");
//                                toggleUserTempos();
//                            } else {
//                                // swipe right
//                                Intent i = new Intent(
//                                        SettingsActivity.this,
//                                        MainActivity.class);
//                                startActivity(i);
//                            }
//
//                        } else {
//                            // consider as something else - a screen tap for example
//                        }
//                        break;
//                }
//
//                return true;
//            }
//        });
    }

    private void enableComponents() {
        boolean enabled = mUserTempToggle.isChecked();
        mFirstTemp.setEnabled(enabled);
        mSecondTemp.setEnabled(enabled);
        mThirdTemp.setEnabled(enabled);
        mSendtoWear.setEnabled(enabled);
    }

    private void initApi() {
        mGoogleApiClient = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                mGoogleApiClient.disconnect();
            }
        }).start();
    }

    /**
     * Sends a message to the connected wear device, telling it to toggle userTempos.
     */
    private void setUserTemposMode(final Boolean b) {
        Log.d(TAG, "toggleUserTempos from mobile");
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, nodeId);
                    mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, TOGGLE_MESSAGE, String.valueOf(b).getBytes());
                    mGoogleApiClient.disconnect();
                }
            }).start();
        }
    }

    /**
     * Sends a message to the connected wear device, telling it to toggle userTempos.
     */
    private void sendUserTempos() {
        Log.d(TAG, "sendUserTempos from mobile");
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, nodeId);
                    mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    int first, second, third;
                    String msgint;
                    first = mFirstTemp.getValue();
                    second = mSecondTemp.getValue();
                    third = mThirdTemp.getValue();
                    msgint = String.valueOf(first) + "," + String.valueOf(second) + "," + String.valueOf(third);
                    Log.e(TAG, msgint);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, UPDATE_TEMPO_MESSAGE, msgint.getBytes());
                    mGoogleApiClient.disconnect();
                }
            }).start();
        }
    }

    private void sendBackgroundColor(final int buttonNumber) {
        Log.d(TAG, "send color from mobile");
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, nodeId);
                    mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    String msgint;
                    msgint = String.valueOf(buttonNumber);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, UPDATE_BACKGROUND_COLOR, msgint.getBytes());
                    mGoogleApiClient.disconnect();
                }
            }).start();
        }
    }
}