package com.bosch.si.emobility.bstp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.UserSessionManager;
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Activity implements LocationListener {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private RelativeLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        checkAuthentication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthentication();
    }

    private void setUpMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();
        if (map != null) {
            map.setMyLocationEnabled(true);
            /**
             * Move the button
             */
            View mapView = mapFragment.getView();
            if (mapView != null && mapView.findViewById(1) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 30);
            }
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {

                }
            });
            zoomToMyLocation();
        }

    }

    private void zoomToMyLocation() {
        Location location = Utils.getMyLocation(this);
        LatLng myLatLng;
        if (location != null)
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        else
            myLatLng = new LatLng(0, 0);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 11.0f));
        map.addMarker(new MarkerOptions().position(myLatLng).title("It's Me!"));
    }


    @Override
    public void onEventMainThread(Event event) {
        if (event.getType() == Event.TYPE.MESSAGE) {
            Utils.Notifier.notify(event.getMessage());
        }
    }

    public void onMyLocationButtonClicked(View view) {
        zoomToMyLocation();
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

    private void checkAuthentication() {
        if (!UserSessionManager.getInstance().isLogged()) {//show login dialog
            loginLayout.setVisibility(View.VISIBLE);
            loginLayout.setAlpha(0.0f);
            loginLayout.animate()
                    .translationY(loginLayout.getHeight())
                    .alpha(1.0f);
        } else {
            openMap();
        }
    }

    public void onLoginButtonClicked(View view) {
        //call login rest service and setup map after succeed
        loginLayout.animate()
                .translationY(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        openMap();
                    }
                });
    }

    private void openMap() {
        loginLayout.setVisibility(View.GONE);
        setUpMap();
    }
}
