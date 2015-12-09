package com.megaukelele.tiktok;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    //shared preferences
    SharedPreferences pref;

    private static final String TAG = "SettingsActivity";
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    public CheckBox cbVibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //shared preferences
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        cbVibrate = (CheckBox) findViewById(R.id.cbVibrate);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LinearLayout layout_settings = (LinearLayout)findViewById(R.id.llwearsettings);

        /*
        cbVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if checkbox is checked
                if (((CheckBox) v).isChecked()) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("vibrateOn", true);
                    editor.commit();
                } else {
                    //else it is not checked
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("vibrateOn", false);
                    editor.commit();
                }
            }
        });
        */

            layout_settings.setOnTouchListener(new View.OnTouchListener()

            {
                @Override
                public boolean onTouch (View v, MotionEvent event){

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
            }

            );
        }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
