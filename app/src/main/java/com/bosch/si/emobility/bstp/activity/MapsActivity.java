package com.bosch.si.emobility.bstp.activity;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.component.DetailComponent;
import com.bosch.si.emobility.bstp.component.LoginComponent;
import com.bosch.si.emobility.bstp.component.MapComponent;
import com.bosch.si.emobility.bstp.component.SearchComponent;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.manager.UserSessionManager;
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
        } else if (event.getType() == Constants.EventType.RE_LOGIN_OK.toString()) {
            mapComponent.refresh();
        } else if (event.getType() == Constants.EventType.LOGIN_FAILED.toString()) {
            Utils.Indicator.hide();
            super.onEventMainThread(event);
        } else if (event.getType() == Constants.EventType.RE_LOGIN_FAILED.toString()) {
            onLogout();
        } else if (event.getType() == Constants.EventType.LOGOUT_OK.toString()) {
            showLoginDialog();
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

    private void showLoginDialog() {
        loginComponent.setEnabled(true, true);
        setEnabled(false);
    }

    public void onLoginButtonClicked(View view) {
        doLogin(loginComponent.getUser());
    }

    @Override
    protected void openUpcomingActivity() {
        setEnabled(true);
        super.openUpcomingActivity();
    }

    private void onCameraChanged() {
        detailComponent.setEnabled(false, false);
    }

    @Override
    public void onBackPressed() {
        if (searchComponent.isShown()) {
            searchComponent.setEnabled(false, true);
        } else if (detailComponent.isShown()) {
            detailComponent.setEnabled(false, true);
        } else {
            super.onBackPressed();
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
        loginComponent.setEnabled(false, true);
        setEnabled(true);
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
        detailComponent.setParkingLocation(parkingLocation);
    }

    public void onReserveButtonClicked(View view) {
        detailComponent.onReserveButtonClicked(view);
    }
}
