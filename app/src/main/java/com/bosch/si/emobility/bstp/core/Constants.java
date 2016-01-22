package com.bosch.si.emobility.bstp.core;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sgp0458 on 9/12/15.
 */
public class Constants {
    public static final int SPLASH_TIME_OUT = 2000;
    public static final long MIN_TIME_BW_UPDATES = 1000;
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0F;
    //public static final String BASE_URL = "http://ivsrv034.bosch-si.com:9000";
    public static final String BASE_URL = "http://10.191.22.29:9000";
    public static final LatLng DEFAULT_LOCATION = new LatLng(50.1211277, 8.4964787);//Frankfurt
    public static final float DEFAULT_ZOOM_LEVEL = 9.5f;
    public static final float DEFAULT_ZOOM_RADIUS = 50000.0f;
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "hh:mm";
    public static final String DB_DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd / hh:mm";
    public static final String HOCKEY_APP_ID = "c94bedcf98a14355847b6ebc059b6463";
    public static final String NOT_USED_PARAM = "Notused";
    public static final String PAYMENT_MODE_CREDIT = "Credit";

    public static final String SECURITY_FENCE = "FENCE";
    public static final String SECURITY_CCTV = "CCTV";
    public static final String SECURITY_LIGHTING = "24H LIGHTING";
    public static final String SECURITY_GATE = "GATE";
    public static final String SECURITY_LPR = "LPR";

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
