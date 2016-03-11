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
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.Driver;
import com.bosch.si.emobility.bstp.service.GetDriverInfoService;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.gson.Gson;

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
        if (UserSessionManager.getInstance().isLogged()) {//show login dialog
            loadDriverInfo(new DriverLoaderCallback() {
                @Override
                public void afterLoaded(Driver driver) {
                    Utils.Indicator.hide();
                }

                @Override
                public void loadFailed() {
                    Utils.Notifier.notify(getString(R.string.unable_to_get_driver_details));
                }
            });
        }
    }

    protected interface DriverLoaderCallback {
        void afterLoaded(Driver driver);

        void loadFailed();
    }

    protected void loadDriverInfo(final DriverLoaderCallback callback) {
        final Driver[] drivers = {DataManager.getInstance().getCurrentDriver()};
        if (drivers[0] == null) {
            Utils.Indicator.show();

            GetDriverInfoService driverInfoService = new GetDriverInfoService();
            driverInfoService.executeAsync(new ServiceCallback() {
                @Override
                public void success(IService service) {
                    drivers[0] = new Gson().fromJson(service.getResponseString(), Driver.class);
                    if (drivers[0] != null) {
                        DataManager.getInstance().setCurrentDriver(drivers[0]);
                        callback.afterLoaded(drivers[0]);
                    } else {
                        callback.loadFailed();
                    }
                }

                @Override
                public void failure(IService service) {
                    callback.loadFailed();
                }

                @Override
                public void onPostExecute(IService service) {
                    Utils.Indicator.hide();
                }
            });
        } else {
            callback.afterLoaded(drivers[0]);
        }
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
        } else if (event.getType() == Constants.EventType.SESSION_EXPIRED.toString()) {
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
    public void onSessionExpired() {
        openMapsActivity();
    }

    @Override
    public void onBackPressed() {
        Utils.Indicator.hide();
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
