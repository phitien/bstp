package com.bosch.si.emobility.bstp.activity;

import android.os.Bundle;

import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by sgp0458 on 8/12/15.
 */
public abstract class Activity extends android.support.v4.app.FragmentActivity implements IActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterEventBus();
    }

    @Override
    public void registerEventBus() {
        EventBus.getDefault().register(this); // register EventBus
    }

    @Override
    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this); // unregister EventBus
    }

    // method that will be called when someone posts an event NetworkStateChanged
    @Override
    public void onEventMainThread(Event event) {
        if (event.getType() == Event.TYPE.MESSAGE) {
            Utils.Notifier.notify(event.getMessage());
        }
    }
}
