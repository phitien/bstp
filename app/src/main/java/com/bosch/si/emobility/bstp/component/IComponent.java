package com.bosch.si.emobility.bstp.component;

import android.content.res.Resources;
import android.view.View;

import com.bosch.si.emobility.bstp.activity.Activity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public interface IComponent {

    void setEnabled(boolean enabled, boolean noAnimation);

    void toggleView();

    boolean isShown();

    void setActivity(Activity activity);

    Activity getActivity();

    Resources getResources();

    View findViewById(int id);
}
