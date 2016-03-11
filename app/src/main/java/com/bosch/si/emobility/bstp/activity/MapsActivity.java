package com.bosch.si.emobility.bstp.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.bosch.si.emobility.bstp.core.User;
import com.bosch.si.emobility.bstp.core.UserSessionManager;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.Driver;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.SearchCriteria;
import com.bosch.si.emobility.bstp.service.GetDriverInfoService;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

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
        if (event.getType() == Constants.EventType.CAMERA_CHANGED.toString()) {
            onCameraChanged();
        } else {
            super.onEventMainThread(event);
        }
    }

    @Override
    public void onLogoutOk() {
        showLoginDialog();
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

    @Override
    public void onSessionExpired() {
        showLoginDialog();
    }

    private void checkAuthentication() {
        if (!UserSessionManager.getInstance().isLogged()) {//show login dialog
            onSessionExpired();
        } else {
            openMap();
        }
    }

    private void setEnabled(boolean enabled) {
        hideKeyboard();
        mapComponent.setEnabled(enabled, true);
        headerComponent.setEnabled(enabled, true);
        searchComponent.setEnabled(enabled, true);
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
        loadDriverInfo(new DriverLoaderCallback() {
            @Override
            public void afterLoaded(Driver driver) {
                openConfirmReservationDetailsActivity();
            }

            @Override
            public void loadFailed() {
                Utils.Notifier.notify(getString(R.string.unable_to_get_driver_details));
            }
        });
    }

    private void openConfirmReservationDetailsActivity() {
        Intent intent = new Intent(MapsActivity.this, ConfirmReservationDetailsActivity.class);
        DataManager.getInstance().setStartTime(searchComponent.getSearchCriteria().getStartTime());
        DataManager.getInstance().setEndTime(searchComponent.getSearchCriteria().getEndTime());
        startActivity(intent);
    }

    protected void doLogin(final User user) {

        hideKeyboard();

        if (user.getUsername().trim().isEmpty()) {
            Utils.Notifier.notify(getString(R.string.username_empty));
            return;
        }

        if (user.getPassword().trim().isEmpty()) {
            Utils.Notifier.notify(getString(R.string.password_empty));
            return;
        }

        Utils.Indicator.show();
        //call login rest service and setup map after succeed
        LoginService loginService = new LoginService();
        loginService.user = user;

        loginService.executeAsync(new ServiceCallback() {
            @Override
            public void success(IService service) {
                //Save Authorization Cookie
                try {
                    JSONObject jsonObj = XML.toJSONObject(service.getResponseString());
                    user.setContextId(jsonObj.getJSONObject("ns2:identityContext").getString("ns2:contextId"));
                    JSONArray currentUserRoles = jsonObj.getJSONObject("ns2:identityContext").getJSONObject("ns2:tenantRelatedData").getJSONArray("ns2:role");
                    Boolean isADriver = false;

                    for (int i = 0; i < currentUserRoles.length(); i++) {
                        JSONObject item = currentUserRoles.getJSONObject(i);
                        if (item.getString("ns2:name").equalsIgnoreCase(Constants.IM_USER_ROLE_FOR_DRIVER) == true) {
                            isADriver = true;
                            break;
                        }
                    }

                    if (isADriver == true) {
                        UserSessionManager.getInstance().setUserSession(user);
                        openMap();
                    } else {
                        Utils.Notifier.notify(Utils.getString(R.string.login_failed_with_reason_invalid_role));
                    }

                } catch (Exception e) {
                    Utils.Notifier.notify(Utils.getString(R.string.login_failed));
                }
            }

            @Override
            public void failure(IService service) {
                Utils.Notifier.notify(Utils.getString(R.string.login_failed));
            }

            @Override
            public void onUnauthorized(IService service) {
                Utils.Notifier.notify(Utils.getString(R.string.login_failed));
            }

            @Override
            public void onPostExecute(IService service) {
                Utils.Indicator.hide();
            }
        });
    }

}
