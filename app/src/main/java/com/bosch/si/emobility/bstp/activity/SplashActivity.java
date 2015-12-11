package com.bosch.si.emobility.bstp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;

public class SplashActivity extends Activity implements LocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkPermissions();
    }

    private void checkPermissions() {
        if (!Utils.hasAccessToLocation(this)) {
            Utils.askPermissionsForLocation(this);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMapsActivity();
                }
            }, Constants.SPLASH_TIME_OUT);
        }

    }

    private void openMapsActivity() {
        Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openMapsActivity();
                } else {
                    checkPermissions();
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
