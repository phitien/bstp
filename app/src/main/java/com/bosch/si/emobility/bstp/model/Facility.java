package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Model;

import java.io.Serializable;

/**
 * Created by sgp0458 on 18/12/15.
 */
public class Facility extends Model implements Serializable {

    private String facilityId;
    private String facilityName;
    private String imageUrlOrCss;
    private String parkingId;

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getImageUrlOrCss() {
        return imageUrlOrCss;
    }

    public void setImageUrlOrCss(String imageUrlOrCss) {
        this.imageUrlOrCss = imageUrlOrCss;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }
}
