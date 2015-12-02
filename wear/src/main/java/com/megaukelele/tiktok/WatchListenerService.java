package com.megaukelele.tiktok;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WatchListenerService extends WearableListenerService implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG="WatchListenerService";
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
        Log.d(TAG,"message received");
    }

    @Override
    public void onConnected(Bundle bundle){
        Wearable.MessageApi.addListener(mApiClient,this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
}
