package com.bosch.si.emobility.bstp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.helper.Constants;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
                startActivity(intent);
            }

        }, Constants.SPLASH_TIME_OUT);
    }

}
