package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Model;
import com.bosch.si.emobility.bstp.core.SecurityDetailsMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgp0458 on 18/12/15.
 */
public class ParkingLocation extends Model {

    private String parkingId;
    private double latitude;
    private double longitude;
    private String locationTitle;
    private String address;
    private String description;
    private String parkingFee;
    private int availabilityCount;
    private int totalCapacityCount;
    private String parkingType;
    private String securityLevel;
    private List<Facility> facilities = new ArrayList<>();
    private List<String> securityDetails = new ArrayList<>();

    private List<SecurityDetails> mappedSecurityDetails = null;

    public List<String> getSecurityDetails() {
        return securityDetails;
    }

    public void setSecurityDetails(List<String> securityDetails) {
        this.securityDetails = securityDetails;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(String parkingFee) {
        this.parkingFee = parkingFee;
    }


    public int getAvailabilityCount() {
        return availabilityCount;
    }

    public void setAvailabilityCount(int availabilityCount) {
        this.availabilityCount = availabilityCount;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getParkingType() {
        return parkingType;
    }

    public void setParkingType(String parkingType) {
        this.parkingType = parkingType;
    }

    public int getTotalCapacityCount() {
        return totalCapacityCount;
    }

    public void setTotalCapacityCount(int totalCapacityCount) {
        this.totalCapacityCount = totalCapacityCount;
    }

    public void merge(ParkingLocation parkingLocation) {
        if (parkingLocation != null) {
            if (parkingLocation.locationTitle != null)
                locationTitle = parkingLocation.locationTitle;
            if (parkingLocation.address != null)
                address = parkingLocation.address;
            if (parkingLocation.availabilityCount >= 0)
                availabilityCount = parkingLocation.availabilityCount;
            if (parkingLocation.totalCapacityCount >= 0)
                totalCapacityCount = parkingLocation.totalCapacityCount;
            if (parkingLocation.securityLevel != null)
                securityLevel = parkingLocation.securityLevel;
            if (parkingLocation.securityDetails != null)
                securityDetails = parkingLocation.securityDetails;
            if (parkingLocation.facilities != null)
                facilities = parkingLocation.facilities;
        }
    }

    public List<SecurityDetails> getMappedSecurityDetails(){

        if (mappedSecurityDetails == null){
            mappedSecurityDetails = SecurityDetailsMapper.mapSecurityDetails(securityDetails);
        }
        return mappedSecurityDetails;
    }
}
