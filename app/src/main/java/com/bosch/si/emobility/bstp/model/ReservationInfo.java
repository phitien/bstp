package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Model;
import com.bosch.si.emobility.bstp.service.ReserveParkingService;

/**
 * Created by SSY1SGP on 20/1/16.
 */
public class ReservationInfo extends Model {

    private String additionalInfo;
    private String driverId;
    private String endTime;
    private String parkingId;
    private String paymentMode = Constants.PAYMENT_MODE_CREDIT;
    private String startTime;
    private String vehicleId;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    private ReserveParkingService fillToReservationParkingService(ReserveParkingService service) {

        service.parkingId = parkingId;
        service.additionalInfo = additionalInfo;
        service.startTime = startTime;
        service.endTime = endTime;
        service.driverId = driverId;
        service.paymentMode = paymentMode;
        service.vehicleId = vehicleId;
        return service;
    }

    public ReserveParkingService createReserveParkingService() {
        ReserveParkingService service = new ReserveParkingService();
        return fillToReservationParkingService(service);
    }
}
