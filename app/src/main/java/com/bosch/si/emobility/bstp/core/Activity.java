package com.bosch.si.emobility.bstp.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.AboutActivity;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.activity.UpcomingActivity;
import com.bosch.si.emobility.bstp.component.HeaderComponent;
import com.bosch.si.emobility.bstp.component.MenuComponent;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import de.greenrobot.event.EventBus;

/**
 * Created by sgp0458 on 8/12/15.
 */
public abstract class Activity extends android.support.v4.app.FragmentActivity implements IActivity, LocationListener {

    protected HeaderComponent headerComponent;
    protected MenuComponent menuComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID());
        setup();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterEventBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerEventBus();
    }

    protected void setup() {
        try {
            headerComponent = new HeaderComponent(this);
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            //commenting out the title of the screens
            //headerComponent.setTitle(activityInfo.loadLabel(getPackageManager()).toString());

            menuComponent = new MenuComponent(this);
            menuComponent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    menuComponent.toggleView();
                    if (position == 0) {
                        openMapsActivity();
                    } else if (position == 1) {
                        //open Upcoming activity
                        openUpcomingActivity();
                    } else if (position == 2) {
                        openAboutActivity();
                    } else if (position == 3) {
                        onLogout();
                    }
                }
            });
        } catch (Exception e) {
            if (headerComponent != null) {
                if (menuComponent == null) {
                    headerComponent.setDisableMenu(true);
                }
            }
        }
    }

    public void onMenuButtonClicked(View view) {
        menuComponent.toggleView();
    }

    protected void openMapsActivity() {
        if (this instanceof MapsActivity)
            return;
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void openAboutActivity() {
        if (this instanceof AboutActivity)
            return;
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    protected void openUpcomingActivity() {
        if (this instanceof UpcomingActivity)
            return;
        Intent intent = new Intent(this, UpcomingActivity.class);
        startActivity(intent);
    }

    protected void onLogout() {
        UserSessionManager.getInstance().clearUserSession();
    }

    protected void doLogin(final User user) {
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
                        Event.broadcast(Utils.getString(R.string.login_ok), Constants.EventType.LOGIN_OK.toString());
                    } else {
                        onLogout();
                        Event.broadcast(Utils.getString(R.string.login_failed_with_reason_invalid_role), Constants.EventType.LOGIN_FAILED.toString());
                    }

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

            @Override
            public void onUnauthorized(IService service) {
                onLogout();
                Event.broadcast(Utils.getString(R.string.login_failed), Constants.EventType.LOGIN_FAILED.toString());
            }
        });
    }

    protected void doReLogin() {
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
                Event.broadcast(Utils.getString(R.string.re_login_failed), Constants.EventType.RE_LOGIN_FAILED.toString());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * Register EventBus
     */
    @Override
    public void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * Unregister EventBus
     */
    @Override
    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * method that will be called when someone posts an event NetworkStateChanged
     */
    @Override
    public void onEventMainThread(Event event) {
        if (event.getType() == Constants.EventType.LOGOUT_OK.toString()) {
            onLogoutOk();
        } else if (event.getType() == Constants.EventType.LOGIN_OK.toString()) {
            onLoginOk();
        } else if (event.getType() == Constants.EventType.RE_LOGIN_OK.toString()) {
            onReloginOk();
        } else if (event.getType() == Constants.EventType.RE_LOGIN_FAILED.toString()) {
            onLogout();
        } else if (event.getType() == Constants.EventType.SESSION_EXPIRED.toString()) {
            UserSessionManager.getInstance().setSessionExpired(true);
            onSessionExpired();
        } else {
            showMessage(event);
        }
    }

    private void showMessage(Event event) {
        showMessage(event.getMessage());
    }

    private void showMessage(String message) {
        if (message != null && !message.isEmpty()) {
            Utils.Notifier.notify(message);
        }
    }

    /**
     *
     */
    @Override
    public void onLogoutOk() {
        openMapsActivity();
    }

    @Override
    public void onLoginOk() {

    }

    /**
     * Should be overridden in sub class
     */
    @Override
    public void onReloginOk() {
        showMessage(Utils.getString(R.string.re_login_ok));
    }

    @Override
    public void onSessionExpired() {
        openMapsActivity();
    }

    @Override
    public void onBackPressed() {
        if (menuComponent != null && menuComponent.isShown()) {
            menuComponent.setEnabled(false, true);
        } else {
            super.onBackPressed();
        }
    }

    protected void hideKeyboard() {
        try {
            InputMethodManager im = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
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

    public void showIndicator() {
        headerComponent.showIndicator();
    }

    public void hideIndicator() {
        headerComponent.hideIndicator();
    }
}
