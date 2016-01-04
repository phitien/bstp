package com.bosch.si.emobility.bstp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgp0458 on 18/12/15.
 */
public class ParkingTransaction extends Model {

    private String additionalInfo;
    private String driverId;
    private String endTime;
    private ParkingLocation parkingLocation;
    private String paymentMode;
    private String startTime;
    private String transactedBy;
    private String transactedFare;
    private String transactedDate;
    private String transactionId;
    private String vehicleId;

    public ParkingLocation getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(ParkingLocation parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public String getTransactedFare() {
        return transactedFare;
    }

    public void setTransactedFare(String transactedFare) {
        this.transactedFare = transactedFare;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTransactedBy() {
        return transactedBy;
    }

    public void setTransactedBy(String transactedBy) {
        this.transactedBy = transactedBy;
    }

    public String getTransactedDate() {
        return transactedDate;
    }

    public void setTransactedDate(String transactedDate) {
        this.transactedDate = transactedDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}
