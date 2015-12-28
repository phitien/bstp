package com.bosch.si.emobility.bstp.activity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.UserSessionManager;
import com.bosch.si.emobility.bstp.component.DetailComponent;
import com.bosch.si.emobility.bstp.component.LoginComponent;
import com.bosch.si.emobility.bstp.component.MapComponent;
import com.bosch.si.emobility.bstp.component.MenuComponent;
import com.bosch.si.emobility.bstp.component.SearchComponent;
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.SearchCriteria;
import com.bosch.si.emobility.bstp.model.User;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;
import org.json.XML;

public class MapsActivity extends Activity implements LocationListener {

    MapComponent mapComponent;
    LoginComponent loginComponent;
    SearchComponent searchComponent;
    MenuComponent menuComponent;
    DetailComponent detailComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        mapComponent = MapComponent.getInstance(this);
        loginComponent = LoginComponent.getInstance(this);
        loginComponent.setPasswordOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                }
                return false;
            }
        });
        searchComponent = SearchComponent.getInstance(this);
        detailComponent = DetailComponent.getInstance(this);
        menuComponent = MenuComponent.getInstance(this);
        menuComponent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuComponent.toggleView();
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                } else if (position == 3) {
                    onLogout();
                }
            }
        });

        checkAuthentication();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void onCameraChanged() {
        detailComponent.setEnabled(false, false);
    }

    private void doReLogin() {
        final User user = UserSessionManager.getInstance().getUser();
        LoginService loginService = new LoginService();
        loginService.user = user;

        loginService.executeAsync(new ServiceCallback() {
            @Override
            public void success(IService service) {
                //Save Authorization Cookie
                try {
                    JSONObject jsonObj = XML.toJSONObject(service.getResponseString());
                    user.setContextId(jsonObj.getJSONObject("ns2:identityContext").getString("ns2:contextId"));
                    UserSessionManager.getInstance().setUserSession(user);
                    Event.broadcast(Utils.getString(R.string.re_login_ok), Constants.EventType.RE_LOGIN_OK.toString());
                } catch (Exception e) {
                    onLogout();
                    Event.broadcast(Utils.getString(R.string.re_login_failed), Constants.EventType.RE_LOGIN_FAILED.toString());
                }
            }

            @Override
            public void failure(IService service) {
                onLogout();
                Event.broadcast(Utils.getString(R.string.login_failed), Constants.EventType.LOGIN_FAILED.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchComponent.isShown()) {
            searchComponent.setEnabled(false, true);
        }
        if (menuComponent.isShown()) {
            menuComponent.setEnabled(false, true);
        }
        if (detailComponent.isShown()) {
            detailComponent.setEnabled(false, true);
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

    private void checkAuthentication() {
        if (!UserSessionManager.getInstance().isLogged()) {//show login dialog
            showLoginDialog();
        } else {
            openMap();
        }
    }

    private void setEnabled(boolean enabled) {
        mapComponent.setEnabled(enabled, true);
        searchComponent.setEnabled(false, true);
        menuComponent.setEnabled(false, true);
        detailComponent.setEnabled(false, true);
    }

    private void showLoginDialog() {
        hideKeyboard();
        loginComponent.setEnabled(true, true);
        setEnabled(false);
    }

    public void onLoginButtonClicked(View view) {
        doLogin();
    }

    private void doLogin() {
        Utils.Indicator.show();
        //call login rest service and setup map after succeed
        final User user = loginComponent.getUser();

        LoginService loginService = new LoginService();
        loginService.user = user;

        loginService.executeAsync(new ServiceCallback() {
            @Override
            public void success(IService service) {
                //Save Authorization Cookie
                try {
                    JSONObject jsonObj = XML.toJSONObject(service.getResponseString());
                    user.setContextId(jsonObj.getJSONObject("ns2:identityContext").getString("ns2:contextId"));
                    UserSessionManager.getInstance().setUserSession(user);
                    Event.broadcast(Utils.getString(R.string.login_ok), Constants.EventType.LOGIN_OK.toString());
                } catch (Exception e) {
                    onLogout();
                    Event.broadcast(Utils.getString(R.string.login_failed), Constants.EventType.LOGIN_FAILED.toString());
                }
            }

            @Override
            public void failure(IService service) {
                onLogout();
                Event.broadcast(Utils.getString(R.string.login_failed), Constants.EventType.LOGIN_FAILED.toString());
            }
        });
    }

    private void onLogout() {
        showLoginDialog();
        UserSessionManager.getInstance().clearUserSession();
    }

    private void openMap() {
        hideKeyboard();
        loginComponent.setEnabled(false, true);
        setEnabled(true);
    }

    public void onSearchButtonClicked(View view) {
        searchComponent.toggleView();
    }

    public void onMenuButtonClicked(View view) {
        menuComponent.toggleView();
    }

    public LatLngBounds getCurrentLatLngBounds() {
        return mapComponent.getCurrentLatLngBounds();
    }

    public void moveCamera(Place place) {
        hideKeyboard();
        mapComponent.setSearchingLatLng(place.getLatLng());
    }

    public SearchCriteria getSearchCriteria() {
        return searchComponent.getSearchCriteria();
    }

    public void openLocationDetail(ParkingLocation parkingLocation) {
        detailComponent.setParkingLocation(parkingLocation);
    }

    public void onReserveButtonClicked(View view) {
        detailComponent.onReserveButtonClicked(view);
    }
}
