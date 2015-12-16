package com.bosch.si.emobility.bstp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Constants;
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
        String message = event.getMessage();
        if (message != null || !message.isEmpty()) {
            Utils.Notifier.notify(message);
        }
    }

    protected void hideKeyboard() {
        View v = getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
