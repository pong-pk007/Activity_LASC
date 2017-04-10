package com.project.alertactivity_lasc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class home extends Activity {
    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 6000L;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Itim-Regular.ttf");
        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                Intent intent = new Intent(home.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}
