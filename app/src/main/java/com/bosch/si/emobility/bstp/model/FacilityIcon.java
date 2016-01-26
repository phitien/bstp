package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Model;

/**
 * Created by SSY1SGP on 25/1/16.
 */
public class FacilityIcon extends Model {

    private static String facilityName;
    private static Integer facilityId;
    private static Integer facilityImageView;
    private static Integer facilityImageSelected;
    private static Integer facilityImageDefault;

    public static String getFacilityName() {
        return facilityName;
    }

    public static void setFacilityName(String facilityName) {
        FacilityIcon.facilityName = facilityName;
    }

    public static Integer getFacilityId() {
        return facilityId;
    }

    public static void setFacilityId(Integer facilityId) {
        FacilityIcon.facilityId = facilityId;
    }

    public static Integer getFacilityImageSelected() {
        return facilityImageSelected;
    }

    public static void setFacilityImageSelected(Integer facilityImageSelected) {
        FacilityIcon.facilityImageSelected = facilityImageSelected;
    }

    public static Integer getFacilityImageView() {
        return facilityImageView;
    }

    public static void setFacilityImageView(Integer facilityImageView) {
        FacilityIcon.facilityImageView = facilityImageView;
    }

    public static Integer getFacilityImageDefault() {
        return facilityImageDefault;
    }

    public static void setFacilityImageDefault(Integer facilityImageDefault) {
        FacilityIcon.facilityImageDefault = facilityImageDefault;
    }

    public FacilityIcon(String facilityName, Integer facilityId, Integer facilityImageView, Integer facilityImageDefault, Integer facilityImageSelected) {

        this.facilityName = facilityName;
        this.facilityId = facilityId;
        this.facilityImageView = facilityImageView;
        this.facilityImageDefault = facilityImageDefault;
        this.facilityImageSelected = facilityImageSelected;
    }
}
