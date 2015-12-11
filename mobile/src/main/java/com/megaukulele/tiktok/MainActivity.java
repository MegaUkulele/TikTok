package com.megaukulele.tiktok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    public Button settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_go=(Button)findViewById(R.id.settings);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("clicks", "You Clicked B1");
                Intent i = new Intent(
                        MainActivity.this,
                        SettingsActivity.class);
                startActivity(i);
            }
        });
    }

}
