package com.bosch.si.emobility.bstp.activity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.UserSessionManager;
import com.bosch.si.emobility.bstp.activity.component.LoginComponent;
import com.bosch.si.emobility.bstp.activity.component.MapComponent;
import com.bosch.si.emobility.bstp.activity.component.MenuComponent;
import com.bosch.si.emobility.bstp.activity.component.SearchComponent;
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.model.User;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;
import org.json.XML;

public class MapsActivity extends Activity implements LocationListener {

    MapComponent mapPart;
    LoginComponent loginPart;
    SearchComponent searchPart;
    MenuComponent menuPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        mapPart = MapComponent.getInstance(this);
        loginPart = LoginComponent.getInstance(this);
        searchPart = SearchComponent.getInstance(this);
        menuPart = MenuComponent.getInstance(this);
        menuPart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuPart.toggleView();
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                } else if (position == 3) {
                    logout();
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
        } else if (event.getType() == Constants.EventType.LOGIN_FAILED.toString()) {
            Utils.Indicator.hide();
            super.onEventMainThread(event);
        } else {
            super.onEventMainThread(event);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchPart.isShown()) {
            searchPart.toggleView();
        }
        if (menuPart.isShown()) {
            menuPart.toggleView();
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
        mapPart.setEnabled(enabled);
        searchPart.setEnabled(false);
        menuPart.setEnabled(false);
    }

    private void showLoginDialog() {
        loginPart.setEnabled(true);
        setEnabled(false);
    }

    public void onLoginButtonClicked(View view) {
        Utils.Indicator.show();
        //call login rest service and setup map after succeed
        final User user = loginPart.getUser();

        LoginService loginService = new LoginService();
        loginService.user = user;

        loginService.executeAsync(new ServiceCallback() {
            @Override
            public void success(IService service) {
                //Save Authorization Cookie
                try {
                    JSONObject jsonObj = XML.toJSONObject(service.getResponseString());
                    user.setAuthorizationCookie(jsonObj.getJSONObject("ns2:identityContext").getString("ns2:contextId"));
                    UserSessionManager.getInstance().setUserSession(user);
                    Event.broadcast(Utils.getString(R.string.login_ok), Constants.EventType.LOGIN_OK.toString());
                } catch (Exception e) {
                    logout();
                    Event.broadcast(Utils.getString(R.string.login_failed), Constants.EventType.LOGIN_FAILED.toString());
                }
            }

            @Override
            public void failure(IService service) {
                logout();
                Event.broadcast(Utils.getString(R.string.login_failed), Constants.EventType.LOGIN_FAILED.toString());
            }
        });
    }

    private void logout() {
        showLoginDialog();
        UserSessionManager.getInstance().clearUserSession();
    }

    private void openMap() {
        loginPart.setEnabled(false);
        setEnabled(true);
        mapPart.setUpMap();
    }

    public void onSearchButtonClicked(View view) {
        searchPart.toggleView();
    }

    public void onMenuButtonClicked(View view) {
        menuPart.toggleView();
    }

    public LatLngBounds getCurrentLatLngBounds() {
        LatLngBounds bounds = mapPart.getCurrentLatLngBounds();
        if (bounds == null) {
            Location location = Utils.getMyLocation(this);
            LatLng center;
            if (location != null)
                center = new LatLng(location.getLatitude(), location.getLongitude());
            else
                center = new LatLng(0, 0);
            double radius = 5000;
            LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
            LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
            bounds = new LatLngBounds(southwest, northeast);
        }
        return bounds;
    }

    public void moveCamera(LatLng latLng) {
        hideKeyboard();
        mapPart.moveCamera(latLng);
        //TODO display parking spaces
    }
}
