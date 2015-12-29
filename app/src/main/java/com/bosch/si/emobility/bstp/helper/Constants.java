package com.bosch.si.emobility.bstp.helper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sgp0458 on 9/12/15.
 */
public class Constants {
    public static final int SPLASH_TIME_OUT = 2000;
    public static final long MIN_TIME_BW_UPDATES = 1000;
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0F;
    public static final String BASE_URL = "http://ivsrv034.bosch-si.com:9000";
    public static final LatLng DEFAULT_LOCATION = new LatLng(50.1211277, 8.4964787);//Frankfurt
    public static final float DEFAULT_ZOOM_LEVEL = 11.0f;
    public static final double DEFAULT_ZOOM_RADIUS = 50000;

    public enum EventType {
        MESSAGE,
        LOGIN_OK,
        LOGIN_FAILED,
        SESSION_EXPIRED,
        RE_LOGIN_OK,
        RE_LOGIN_FAILED,
        CAMERA_CHANGED,
        LOGOUT_OK,
    }
}
