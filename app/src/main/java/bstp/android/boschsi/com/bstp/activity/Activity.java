package bstp.android.boschsi.com.bstp.activity;


import android.os.Bundle;

import bstp.android.boschsi.com.bstp.app.Event;
import bstp.android.boschsi.com.bstp.helper.Utils;
import de.greenrobot.event.EventBus;

/**
 * Created by sgp0458 on 8/12/15.
 */
public abstract class Activity extends android.app.Activity implements IActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this); // register EventBus
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); // unregister EventBus
    }

    // method that will be called when someone posts an event NetworkStateChanged
    public void onEventMainThread(Event event) {
        if (event.getType() == Event.TYPE.MESSAGE) {
            Utils.Notifier.notify(event.getMessage());
        }
    }
}
