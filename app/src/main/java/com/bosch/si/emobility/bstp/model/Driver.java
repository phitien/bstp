package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SSY1SGP on 21/1/16.
 */
public class Driver extends Model {

    private String driverId;
    private String driverName;
    private String driverContact;
    private String driverEmail;
    private String driverPassCode;
    private List<String> notificationChannels;
    private List<Truck> associatedTrucks;
    private List<Truck> trucks;

    private List<String> truckRegNos = null;

    public List<Truck> getAssociatedTrucks() {
        return associatedTrucks;
    }

    public void setAssociatedTrucks(List<Truck> associatedTrucks) {
        this.associatedTrucks = associatedTrucks;
    }

    public String getDriverPassCode() {
        return driverPassCode;
    }

    public void setDriverPassCode(String driverPassCode) {
        this.driverPassCode = driverPassCode;
    }

    public List<String> getNotificationChannels() {
        return notificationChannels;
    }

    public void setNotificationChannels(List<String> notificationChannels) {
        this.notificationChannels = notificationChannels;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverContact() {
        return driverContact;
    }

    public void setDriverContact(String driverContact) {
        this.driverContact = driverContact;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public void setTrucks(List<Truck> trucks) {
        this.trucks = trucks;
    }

    public List<String> getAllTruckRegNos() {

        if (truckRegNos == null) {
            truckRegNos = new ArrayList<String>();
            for (Truck truck: associatedTrucks) {
                truckRegNos.add(truck.getVehicleId());
            }
        }

        return truckRegNos;
    }
}
