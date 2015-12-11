package com.megaukulele.tiktok;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WatchListenerService extends WearableListenerService implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG="WatchListenerService";
    private static final String TOGGLE_MESSAGE = "toggle_user_tempos";
    private static final String UPDATE_TEMPO_MESSAGE = "update_user_tempos";
    private static final String UPDATE_BACKGROUND_COLOR = "update_background_color";
    private GoogleApiClient mApiClient;


    public void onCreate(){
        super.onCreate();
        initGoogleApiClient();
    }

    private void initGoogleApiClient(){
        mApiClient=new GoogleApiClient.Builder(this)
        .addApi(Wearable.API)
        .addConnectionCallbacks(this)
        .build();

        if(mApiClient!=null&&!(mApiClient.isConnected()||mApiClient.isConnecting()))
        mApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        Intent broadcastIntent = new Intent();
        if (messageEvent.getPath().equals(TOGGLE_MESSAGE)) {
            broadcastIntent.setAction(MainActivity.mToggleUserTempos);
            String data = new String(messageEvent.getData());
            broadcastIntent.putExtra("usertempmode", data);
        } else if (messageEvent.getPath().equals(UPDATE_TEMPO_MESSAGE)) {
            broadcastIntent.setAction(MainActivity.mUpdateUserTempos);
            String data = new String(messageEvent.getData());
            String[] split = data.split(",");
            broadcastIntent.putExtra("first", split[0]);
            broadcastIntent.putExtra("second", split[1]);
            broadcastIntent.putExtra("third", split[2]);
        }
        if (messageEvent.getPath().equals(UPDATE_BACKGROUND_COLOR)) {
            broadcastIntent.setAction(MainActivity.mUpdateBackgroundColor);
            String data = new String(messageEvent.getData());
            broadcastIntent.putExtra("option", Integer.parseInt(data));
        }
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onConnected(Bundle bundle){
        Wearable.MessageApi.addListener(mApiClient,this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
}
