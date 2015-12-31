package com.bosch.si.emobility.bstp.activity;

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
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.component.HeaderComponent;
import com.bosch.si.emobility.bstp.component.MenuComponent;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.manager.UserSessionManager;
import com.bosch.si.emobility.bstp.model.User;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;

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
            headerComponent.setTitle(activityInfo.loadLabel(getPackageManager()).toString());

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
            e.printStackTrace();
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
        EventBus.getDefault().register(this);
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
            openMapsActivity();
        } else {
            String message = event.getMessage();
            if (message != null && !message.isEmpty()) {
                Utils.Notifier.notify(message);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (menuComponent.isShown()) {
            menuComponent.setEnabled(false, true);
        } else {
            super.onBackPressed();
        }
    }

    protected void hideKeyboard() {
        View v = getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
