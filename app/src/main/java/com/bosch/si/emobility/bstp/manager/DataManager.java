package com.bosch.si.emobility.bstp.manager;

import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.model.Driver;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.ParkingTransaction;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String endTime;
    String startTime;

    List<ParkingTransaction> transactions = new ArrayList<>();
    ParkingTransaction currentTransaction;

    List<ParkingLocation> parkingLocations = new ArrayList<>();

    boolean justCanceledReservations = false;

    Driver currentDriver = null;

    public ParkingLocation getCurrentParkingLocation() {
        return currentParkingLocation;
    }

    public void setCurrentParkingLocation(ParkingLocation currentParkingLocation) {
        this.currentParkingLocation = currentParkingLocation;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public List<ParkingTransaction> getFilteredTransactions(String filter) {

        List<ParkingTransaction> filteredTransactions = new ArrayList<ParkingTransaction>();

        for (ParkingTransaction a : transactions) {
            // or equalsIgnoreCase or whatever your conditon is
            if (a.getStatus().equals(filter)) {
                // do something
                filteredTransactions.add(a);
            }
        }

        return filteredTransactions;
    }

    public List<ParkingTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<ParkingTransaction> transactions) {

        for (ParkingTransaction transaction : transactions) {
            transaction.setParkingLocation(getParkingLocation(transaction.getParkingId()));
        }

        this.transactions = transactions;
    }

    public Driver getCurrentDriver() {
        return currentDriver;
    }

    public void setCurrentDriver(Driver currentDriver) {
        this.currentDriver = currentDriver;
    }

    public List<ParkingLocation> getParkingLocations() {
        return parkingLocations;
    }

    public ParkingLocation getParkingLocation(String parkingId) {
        for (ParkingLocation parkingLocation : parkingLocations) {
            if (parkingLocation.getParkingId().equals(parkingId)) {
                return parkingLocation;
            }
        }
        return null;
    }

    public void setParkingLocations(List<ParkingLocation> parkingLocations) {
        this.parkingLocations = parkingLocations;
    }

    public boolean isJustCanceledReservations() {
        return justCanceledReservations;
    }

    public void setJustCanceledReservations(boolean justCanceledReservations) {
        this.justCanceledReservations = justCanceledReservations;
    }
}
