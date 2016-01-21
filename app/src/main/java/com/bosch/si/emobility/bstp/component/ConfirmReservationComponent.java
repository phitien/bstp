package com.bosch.si.emobility.bstp.component;

import android.widget.ImageView;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Component;
import com.bosch.si.emobility.bstp.model.Driver;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.Truck;

/**
 * Created by SSY1SGP on 21/1/16.
 */
public class ConfirmReservationComponent extends Component{

    private ImageView parkingIcon;
    private TextView parkingLocationName;
    private TextView parkingLocationAddress;

    private TextView fromDate;
    private TextView toDate;

    private TextView driverName;
    private TextView truckRegNo;

    private ParkingLocation parkingLocation;
    private Driver driver;
    private Truck truck;

    public ParkingLocation getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(ParkingLocation parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public ConfirmReservationComponent(Activity activity) {
        super(activity);
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);

    }
}
