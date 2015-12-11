package com.bosch.si.emobility.bstp.activity;

import com.bosch.si.emobility.bstp.app.Event;

/**
 * Created by sgp0458 on 8/12/15.
 */
public interface IActivity {
    void registerEventBus();
    void unregisterEventBus();
    void onEventMainThread(Event event);
}
