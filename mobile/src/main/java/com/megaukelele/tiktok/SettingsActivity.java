package com.megaukelele.tiktok;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class SettingsActivity extends Activity {
    public Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
    }
}