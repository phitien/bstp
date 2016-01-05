package com.bosch.si.emobility.bstp.activity;

import android.os.Handler;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Utils;

public class SplashActivity extends Activity {

    @Override
    public int layoutResID() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setup() {
        super.setup();
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

    @Override
    protected void openMapsActivity() {
        super.openMapsActivity();
        finish();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case Constants.PERMISSIONS_REQUEST_RESULT: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    openMapsActivity();
//                } else {
//                    checkPermissions();
//                }
//                return;
//            }
//        }
//    }
}
