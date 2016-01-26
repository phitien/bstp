package com.bosch.si.emobility.bstp.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.DetailComponent;
import com.bosch.si.emobility.bstp.component.LoginComponent;
import com.bosch.si.emobility.bstp.component.MapComponent;
import com.bosch.si.emobility.bstp.component.SearchComponent;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Event;
import com.bosch.si.emobility.bstp.core.UserSessionManager;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.SearchCriteria;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapsActivity extends Activity {

    MapComponent mapComponent;
    LoginComponent loginComponent;
    SearchComponent searchComponent;
    DetailComponent detailComponent;

    @Override
    public int layoutResID() {
        return R.layout.activity_maps;
    }

    @Override
    protected void setup() {
        super.setup();

        mapComponent = new MapComponent(this);
        loginComponent = new LoginComponent(this);
        searchComponent = new SearchComponent(this);
        detailComponent = new DetailComponent(this);

        loginComponent.setPasswordOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin(loginComponent.getUser());
                }
                return false;
            }
        });

    }

    boolean authenticationChecked = false;

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthentication();
    }

    @Override
    public void onEventMainThread(Event event) {
        if (event.getType() == Constants.EventType.LOGIN_OK.toString()) {
            Utils.Indicator.hide();
            openMap();
        } else if (event.getType() == Constants.EventType.LOGIN_FAILED.toString()) {
            Utils.Indicator.hide();
            super.onEventMainThread(event);
        } else if (event.getType() == Constants.EventType.CAMERA_CHANGED.toString()) {
            onCameraChanged();
        } else if (event.getType() == Constants.EventType.SESSION_EXPIRED.toString()) {
            if (UserSessionManager.getInstance().getUser().isSaveCredentials()) {//if save credentials
                doReLogin();
            } else {//else open login dialog
                showLoginDialog();
            }
        } else {
            super.onEventMainThread(event);
        }
    }

    @Override
    public void onLogoutOk() {
        showLoginDialog();
    }

    @Override
    public void onReloginOk() {
        mapComponent.refresh();
    }

    private void showLoginDialog() {
        authenticationChecked = false;
        loginComponent.setEnabled(true, true);
        setEnabled(false);
    }

    public void onLoginButtonClicked(View view) {
        doLogin(loginComponent.getUser());
    }

    private void onCameraChanged() {
        detailComponent.setEnabled(false, false);
    }

    @Override
    public void onBackPressed() {
        if (menuComponent.isShown()) {
            menuComponent.setEnabled(false, true);
        } else if (searchComponent.isShown()) {
            searchComponent.setEnabled(false, true);
        } else if (detailComponent.isShown()) {
            detailComponent.setEnabled(false, true);
        }
    }

    private void checkAuthentication() {
        if (!UserSessionManager.getInstance().isLogged()) {//show login dialog
            showLoginDialog();
        } else {
            openMap();
        }
    }

    private void setEnabled(boolean enabled) {
        hideKeyboard();
        mapComponent.setEnabled(enabled, true);
        headerComponent.setEnabled(enabled, true);
        searchComponent.setEnabled(false, true);
        menuComponent.setEnabled(false, true);
        detailComponent.setEnabled(false, true);
    }

    private void openMap() {
        if (!authenticationChecked) {
            loginComponent.setEnabled(false, true);
            setEnabled(true);
            authenticationChecked = true;
        }
    }

    public void onSearchButtonClicked(View view) {
        searchComponent.toggleView();
    }

    public LatLngBounds getCurrentLatLngBounds() {
        return mapComponent.getCurrentLatLngBounds();
    }

    public void moveCamera(Place place) {
        hideKeyboard();
        DataManager.getInstance().setSearchLatLng(place.getLatLng());
        mapComponent.setSearchingLatLng(place.getLatLng());
    }

    public SearchCriteria getSearchCriteria() {
        return searchComponent.getSearchCriteria();
    }

    public void openLocationDetail(ParkingLocation parkingLocation) {
        DataManager.getInstance().setCurrentParkingLocation(parkingLocation);
        detailComponent.parkingLocationUpdated();
    }

    public void onReserveButtonClicked(View view) {
        Intent intent = new Intent(MapsActivity.this, ConfirmReservationDetailsActivity.class);
        intent.putExtra(Constants.PARKING_LOCATION_INTENT_DATA_KEY, DataManager.getInstance().getCurrentParkingLocation());
        intent.putExtra(Constants.FROM_DATE_TIME_INTENT_DATA_KEY, searchComponent.getSearchCriteria().getStartTime());
        intent.putExtra(Constants.TO_DATE_TIME_INTENT_DATA_KEY, searchComponent.getSearchCriteria().getEndTime());
        startActivity(intent);
    }
}
