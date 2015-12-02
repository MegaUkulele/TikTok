package com.megaukelele.tiktok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends Activity {
    private static String TAG = "SettingsActivity";
    public Button settings;
    private GoogleApiClient mGoogleApiClient;
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private static final String MESSAGE = "toggle_user_tempos";
    private String nodeId;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        ImageButton btn_temp=(ImageButton)findViewById(R.id.backtomain);
//        btn_temp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(
//                        SettingsActivity.this,
//                        MainActivity.class);
//                startActivity(i);
//            }
//        });

        initApi();

        // this is a temporary gesture just for demonstrating two different actions with one image button
        // swiping left triggers sending a message to wear, swiping right triggers switching activities
        btn_temp.setOnTouchListener(new View.OnTouchListener() {
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
                                Log.d(TAG, "swipe left!");
                                toggleUserTempos();
                            } else {
                                // swipe right
                                Intent i = new Intent(
                                        SettingsActivity.this,
                                        MainActivity.class);
                                startActivity(i);
                            }

                        } else {
                            // consider as something else - a screen tap for example
                        }
                        break;
                }

                return true;
            }
        });
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
                Log.d(TAG, "nodes" + nodes);
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
    private void toggleUserTempos() {
        Log.d(TAG, "toggleUserTempos from mobile");
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, nodeId);
                    mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, MESSAGE, null);
                    mGoogleApiClient.disconnect();
                    Log.d(TAG, "message sent from mobile");
                }
            }).start();
        }
    }
}