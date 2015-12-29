package com.bosch.si.emobility.bstp.activity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.UserSessionManager;
import com.bosch.si.emobility.bstp.component.HeaderComponent;
import com.bosch.si.emobility.bstp.component.MenuComponent;

public class UpcomingActivity extends Activity implements LocationListener {

    HeaderComponent headerComponent;
    MenuComponent menuComponent;

    @Override
    protected void onResume() {
        super.onResume();

        headerComponent = HeaderComponent.getInstance(this);
        menuComponent = MenuComponent.getInstance(this);
        menuComponent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuComponent.toggleView();
                if (position == 0) {
                    openMapActivity();
                } else if (position == 1) {
                    //do nothing
                } else if (position == 2) {
                    openAboutActivity();
                } else if (position == 3) {
                    onLogout();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (menuComponent.isShown()) {
            menuComponent.setEnabled(false, true);
        } else {
            openMapActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);
    }

    private void openAboutActivity() {
        //TODO
    }

    private void openMapActivity() {
        finish();
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

    public void onSearchButtonClicked(View view) {
    }

    public void onMenuButtonClicked(View view) {
        menuComponent.toggleView();
    }

    private void onLogout() {
        openMapActivity();
        UserSessionManager.getInstance().clearUserSession();
    }

}
