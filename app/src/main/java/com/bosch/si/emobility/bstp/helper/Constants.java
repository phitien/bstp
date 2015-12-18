package com.bosch.si.emobility.bstp.helper;

import com.bosch.si.emobility.bstp.app.Event;

/**
 * Created by sgp0458 on 9/12/15.
 */
public class Constants {
    public static final int SPLASH_TIME_OUT = 2000;
    public static final long MIN_TIME_BW_UPDATES = 1000;
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0F;
    public static final int PERMISSIONS_REQUEST_RESULT = 11;
    public static final String BASE_URL = "http://ivsrv034.bosch-si.com:9000";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TENANT_NAME = "tenantName";
    public static final String AUTHORIZATION_COOKIE = "authorizationCookie";

    public enum EventType {
        MESSAGE,
        LOGIN_OK,
        LOGIN_FAILED,
    }
}
