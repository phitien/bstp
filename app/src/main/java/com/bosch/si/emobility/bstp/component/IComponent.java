package com.bosch.si.emobility.bstp.component;

import com.bosch.si.emobility.bstp.activity.MapsActivity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public interface IComponent {

    void setEnabled(boolean enabled, boolean noAnimation);

    void toggleView();

    boolean isShown();

    void setActivity(MapsActivity activity);
}
