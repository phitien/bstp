package com.bosch.si.emobility.bstp.core;

import android.widget.ImageView;

import com.bosch.si.emobility.bstp.model.Facility;
import com.bosch.si.emobility.bstp.model.FacilityIcon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by SSY1SGP on 25/1/16.
 */
public class FacilityViewManager {

    private Activity context;

    private static final Map<String, FacilityIcon> facilityIcons;
    static
    {
        facilityIcons = new HashMap<String, FacilityIcon>();
        facilityIcons.put(Constants.RESTAURANT_FACILITY_NAME, new FacilityIcon(Constants.RESTAURANT_FACILITY_NAME,
                                                                                0,
                                                                                Constants.RESTAURANT_FACILITY_IMAGE_VIEW,
                                                                                Constants.RESTAURANT_FACILITY_IMAGE_SRC_DEFAULT,
                                                                                Constants.RESTAURANT_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.SHOWER_FACILITY_NAME, new FacilityIcon(Constants.SHOWER_FACILITY_NAME,
                                                                            1,
                                                                            Constants.SHOWER_FACILITY_IMAGE_VIEW,
                                                                            Constants.SHOWER_FACILITY_IMAGE_SRC_DEFAULT,
                                                                            Constants.SHOWER_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.TOILET_FACILITY_NAME, new FacilityIcon(Constants.TOILET_FACILITY_NAME,
                                                                            2,
                                                                            Constants.TOILET_FACILITY_IMAGE_VIEW,
                                                                            Constants.TOILET_FACILITY_IMAGE_SRC_DEFAULT,
                                                                            Constants.TOILET_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.FUEL_STATION_FACILITY_NAME, new FacilityIcon(Constants.FUEL_STATION_FACILITY_NAME,
                                                                                    3,
                                                                                    Constants.FUEL_STATION_FACILITY_IMAGE_VIEW,
                                                                                    Constants.FUEL_STATION_FACILITY_SRC_DEFAULT,
                                                                                    Constants.FUEL_STATION_FACILITY_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.HOTEL_FACILITY_NAME, new FacilityIcon(Constants.HOTEL_FACILITY_NAME,
                                                                            4,
                                                                            Constants.HOTEL_FACILITY_IMAGE_VIEW,
                                                                            Constants.HOTEL_FACILITY_IMAGE_SRC_DEFAULT,
                                                                            Constants.HOTEL_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.TRUCK_REPAIR_FACILITY_NAME, new FacilityIcon(Constants.TRUCK_REPAIR_FACILITY_NAME,
                                                                                    5,
                                                                                    Constants.TRUCK_REPAIR_FACILITY_IMAGE_VIEW,
                                                                                    Constants.TRUCK_REPAIR_FACILITY_IMAGE_SRC_DEFAULT,
                                                                                    Constants.TRUCK_REFREGERATION_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.TRUCK_REFERGERATION_FACILITY_NAME, new FacilityIcon(Constants.TRUCK_REFERGERATION_FACILITY_NAME,
                                                                                            6,
                                                                                            Constants.TRUCK_REFREGERATION_FACILITY_IMAGE_VIEW,
                                                                                            Constants.TRUCK_REFREGERATION_FACILITY_IMAGE_SRC_DEFAULT,
                                                                                            Constants.TRUCK_REFREGERATION_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.TRUCK_WASH_FACILITY_NAME, new FacilityIcon(Constants.TRUCK_WASH_FACILITY_NAME,
                                                                                7,
                                                                                Constants.TRUCK_WASH_FACILITY_IMAGE_VIEW,
                                                                                Constants.TRUCK_REFREGERATION_FACILITY_IMAGE_SRC_DEFAULT,
                                                                                Constants.TRUCK_REFREGERATION_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.ELECTIRC_CHARGING_FACILITY_NAME, new FacilityIcon(Constants.ELECTIRC_CHARGING_FACILITY_NAME,
                                                                                        8,
                                                                                        Constants.ELECTRIC_CHARGING_FACILITY_IMAGE_VIEW,
                                                                                        Constants.ELECTRIC_CHARGING_FACILITY_IMAGE_SRC_DEFAULT,
                                                                                        Constants.ELECTRIC_CHARGING_FACILITY_IMAGE_SRC_HIGHLIGHTED));

        facilityIcons.put(Constants.WIFI_FACILITY_NAME, new FacilityIcon(Constants.WIFI_FACILITY_NAME,
                                                                            9,
                                                                            Constants.WIFI_FACILITY_IMAGE_VIEW,
                                                                            Constants.WIFI_FACILITY_IMAGE_SRC_DEFAULT,
                                                                            Constants.WIFI_FACILITY_IMAGE_SRC_HIGHLIGHTED));

    }

    public FacilityViewManager(Activity context) {
        this.context = context;
    }

    private void resetIcons(){

        Iterator it = facilityIcons.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            FacilityIcon facilityIcon = (FacilityIcon)pair.getValue();
            if (facilityIcon != null){
                ImageView iconImage = (ImageView) context.findViewById((int)facilityIcon.getFacilityImageView());
                if (iconImage != null) {
                    iconImage.setImageResource((int)facilityIcon.getFacilityImageDefault());
                }
            }
        }
    }

    public void updateIcons(List<Facility> facilities) {

        resetIcons();

        for (Facility facility: facilities) {

            FacilityIcon facilityIcon = facilityIcons.get(facility.getFacilityName());
            if (facilityIcon != null){
                ImageView iconImage = (ImageView) context.findViewById((int)facilityIcon.getFacilityImageView());
                if (iconImage != null) {
                    iconImage.setImageResource((int)facilityIcon.getFacilityImageSelected());
                }
            }
        }
    }
}
