package com.bosch.si.emobility.bstp.core;

import com.bosch.si.emobility.bstp.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sgp0458 on 9/12/15.
 */
public class Constants {
    public static final int SPLASH_TIME_OUT = 2000;
    public static final long MIN_TIME_BW_UPDATES = 1000;
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0F;
    //public static final String BASE_URL = "http://ivsrv034.bosch-si.com:9000";
    //public static final String BASE_URL = "http://10.191.22.29:9000";
    public static final String BASE_URL = "https://212.62.203.10/rest";
    public static final String IM_AUTH_BASE_URL = "https://212.62.203.10/im-server/1/rest/authentication";
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
    public static final String FROM_DATE_TIME_INTENT_DATA_KEY = "FROM_DATE_TIME_INTENT_DATA_KEY";
    public static final String TO_DATE_TIME_INTENT_DATA_KEY = "TO_DATE_TIME_INTENT_DATA_KEY";
    public static final String PARKING_LOCATION_INTENT_DATA_KEY = "PARKING_LOCATION_INTENT_DATA_KEY";

    public static final String SECURITY_FENCE = "FENCE";
    public static final String SECURITY_CCTV = "CCTV";
    public static final String SECURITY_LIGHTING = "24H LIGHTING";
    public static final String SECURITY_GATE = "GATE";
    public static final String SECURITY_LPR = "LPR";

    public static final int RESTAURANT_FACILITY_IMAGE_VIEW = R.id.restaurantFacility;
    public static final int TOILET_FACILITY_IMAGE_VIEW = R.id.toiletFacility;
    public static final int SHOWER_FACILITY_IMAGE_VIEW = R.id.showerFacility;
    public static final int FUEL_STATION_FACILITY_IMAGE_VIEW = R.id.fuelStationFacility;
    public static final int HOTEL_FACILITY_IMAGE_VIEW = R.id.hotelFacility;
    public static final int TRUCK_REPAIR_FACILITY_IMAGE_VIEW = R.id.truckRepairFacility;
    public static final int TRUCK_WASH_FACILITY_IMAGE_VIEW = R.id.truckWashFacility;
    public static final int TRUCK_REFREGERATION_FACILITY_IMAGE_VIEW = R.id.truckRefregerationFacility;
    public static final int ELECTRIC_CHARGING_FACILITY_IMAGE_VIEW = R.id.electricChargingFacility;
    public static final int WIFI_FACILITY_IMAGE_VIEW = R.id.wifiFacility;

    public static final int RESTAURANT_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_restaurant;
    public static final int TOILET_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_toilet;
    public static final int SHOWER_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_shower;
    public static final int FUEL_STATION_FACILITY_SRC_DEFAULT = R.drawable.icon_fuelstation;
    public static final int HOTEL_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_hotel;
    public static final int TRUCK_REPAIR_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_truckrepair;
    public static final int TRUCK_WASH_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_truckwash;
    public static final int TRUCK_REFREGERATION_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_refrigerator;
    public static final int ELECTRIC_CHARGING_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_electricity;
    public static final int WIFI_FACILITY_IMAGE_SRC_DEFAULT = R.drawable.icon_wifi;

    public static final int RESTAURANT_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_restaurant_hi;
    public static final int TOILET_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_toilet_hi;
    public static final int SHOWER_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_shower_hi;
    public static final int FUEL_STATION_FACILITY_SRC_HIGHLIGHTED = R.drawable.icon_fuelstation_hi;
    public static final int HOTEL_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_hotel_hi;
    public static final int TRUCK_REPAIR_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_truckrepair_hi;
    public static final int TRUCK_WASH_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_truckwash_hi;
    public static final int TRUCK_REFREGERATION_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_refrigerator_hi;
    public static final int ELECTRIC_CHARGING_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_electricity_hi;
    public static final int WIFI_FACILITY_IMAGE_SRC_HIGHLIGHTED = R.drawable.icon_wifi_hi;

    public static final String RESTAURANT_FACILITY_NAME = "RESTAURANT";
    public static final String TOILET_FACILITY_NAME = "TOILETS";
    public static final String SHOWER_FACILITY_NAME = "SHOWERS";
    public static final String FUEL_STATION_FACILITY_NAME = "FUEL STATION";
    public static final String HOTEL_FACILITY_NAME = "HOTEL";
    public static final String TRUCK_REPAIR_FACILITY_NAME = "REPAIR SERVICE";
    public static final String TRUCK_WASH_FACILITY_NAME = "TRUCK WASH";
    public static final String TRUCK_REFERGERATION_FACILITY_NAME = "REFRIGERATED TRUCK AREA";
    public static final String ELECTIRC_CHARGING_FACILITY_NAME = "ELECTRICITY";
    public static final String WIFI_FACILITY_NAME = "WIFI";

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
