package com.bosch.si.emobility.bstp.manager;

import com.bosch.si.emobility.bstp.model.Driver;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.ParkingTransaction;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sgp0458 on 30/12/15.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    LatLng searchLatLng = null;
    ParkingLocation currentParkingLocation = null;
    List<ParkingTransaction> transactions = new ArrayList<>();
    ParkingTransaction currentTransaction;

    Driver currentDriver;

    public ParkingLocation getCurrentParkingLocation() {
        return currentParkingLocation;
    }

    public void setCurrentParkingLocation(ParkingLocation currentParkingLocation) {
        this.currentParkingLocation = currentParkingLocation;
    }

    public LatLng getSearchLatLng() {
        return searchLatLng;
    }

    public void setSearchLatLng(LatLng searchLatLng) {
        this.searchLatLng = searchLatLng;
    }

    public ParkingTransaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(ParkingTransaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public List<ParkingTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<ParkingTransaction> transactions) {
        this.transactions = transactions;
    }


    public Driver getCurrentDriver() {
        return currentDriver;
    }

    public void setCurrentDriver(Driver currentDriver) {
        this.currentDriver = currentDriver;
    }

}
