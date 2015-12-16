package com.bosch.si.emobility.bstp.activity.component;

import android.view.View;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.activity.MapsActivity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public abstract class Component implements IComponent {

    protected MapsActivity activity;

    protected RelativeLayout layout;

    public Component() {

    }

    @Override
    public void setActivity(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void toggleView() {
        if (isShown()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        int visibility = enabled ? View.VISIBLE : View.GONE;
        layout.setEnabled(enabled);
        layout.setVisibility(visibility);
    }

    @Override
    public boolean isShown() {
        return layout.getVisibility() == View.VISIBLE;
    }


}
