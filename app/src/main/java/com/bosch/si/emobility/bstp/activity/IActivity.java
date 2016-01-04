package com.bosch.si.emobility.bstp.activity;

import com.bosch.si.emobility.bstp.app.Event;

/**
 * Created by sgp0458 on 8/12/15.
 */
public interface IActivity {
    /**
     * View layout resource ID
     *
     * @return
     */
    int layoutResID();

    /**
     * Register EventBus for the activity, this method should be called in onCreate method
     * <p/>
     * Below is how to implement this method
     * <p/>
     * public void registerEventBus() {
     * EventBus.getDefault().register(this); // register EventBus
     * }
     */
    void registerEventBus();

    /**
     * Unregister EventBus for the activity, this method should be called in onDestroy method
     * <p/>
     * Below is how to implement this method
     * <p/>
     * public void unregisterEventBus() {
     * EventBus.getDefault().unregister(this); // unregister EventBus
     * }
     */
    void unregisterEventBus();

    /**
     * Override this method to handle events
     *
     * @param event
     */
    void onEventMainThread(Event event);

    /**
     * Method to trigger when re-login successfully
     */
    void onReloginOk();

    /**
     * Method to trigger when logout successfully
     */
    void onLogoutOk();
}
