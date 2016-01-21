package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Model;

/**
 * Created by SSY1SGP on 21/1/16.
 */
public class Truck extends Model {

    private String vehicleId;
    private String vehicleType;
    private String vehicleRegNumber;
    private String vehicleModelName;
    private String parkingMode;
    private String vehicleInfo;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleModelName() {
        return vehicleModelName;
    }

    public void setVehicleModelName(String vehicleModelName) {
        this.vehicleModelName = vehicleModelName;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public String getParkingMode() {
        return parkingMode;
    }

    public void setParkingMode(String parkingMode) {
        this.parkingMode = parkingMode;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }
}
