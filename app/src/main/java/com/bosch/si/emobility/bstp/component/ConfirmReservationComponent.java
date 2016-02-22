package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Component;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.model.Driver;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.Truck;

import java.util.List;

/**
 * Created by SSY1SGP on 21/1/16.
 */
public class ConfirmReservationComponent extends Component {

    private ImageView parkingIcon;
    private TextView parkingLocationName;
    private TextView parkingLocationAddress;

    private TextView fromDate;
    private TextView toDate;

    private TextView driverName;
    private Spinner truckRegNo;

    private String fromDateTime;
    private String toDateTime;

    private ParkingLocation parkingLocation;
    private Driver driver;
    private Truck truck;
    private List<Truck> associatedTrucks;

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

    public String getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(String toDateTime) {
        this.toDateTime = toDateTime;
    }

    public String getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(String fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public String getSelectedTruck() {
        Truck truck = (Truck) truckRegNo.getSelectedItem();
        return truck.getVehicleId();
    }

    public ConfirmReservationComponent(Activity activity) {
        super(activity);
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);

        layout = (ViewGroup) this.activity.findViewById(R.id.confirmReservationDetailsLayout);

        parkingIcon = (ImageView) this.activity.findViewById(R.id.parkingLocationIcon);
        parkingLocationName = (TextView) this.activity.findViewById(R.id.textViewReserveParkingName);
        parkingLocationAddress = (TextView) this.activity.findViewById(R.id.textViewReserveParkingAddress);
        fromDate = (TextView) this.activity.findViewById(R.id.textViewReserveFromDateTime);
        toDate = (TextView) this.activity.findViewById(R.id.textViewReserveToDateTime);
        driverName = (TextView) this.activity.findViewById(R.id.textViewReserveDriverName);
        truckRegNo = (Spinner) this.activity.findViewById(R.id.spinnerTruckRegNo);
    }

    public void populateData() {

        if (parkingLocation != null) {

            String imageName = Utils.getParkingIconName(parkingLocation);
            parkingIcon.setImageResource(Utils.getImage(imageName));

            parkingLocationName.setText(parkingLocation.getLocationTitle());
            parkingLocationAddress.setText(parkingLocation.getAddress());
        }

        if (fromDateTime != null)
            fromDate.setText(Utils.getFormattedDatetime(fromDateTime));

        if (toDateTime != null)
            toDate.setText(Utils.getFormattedDatetime(toDateTime));

        if (driver != null) {

            driverName.setText(driver.getDriverName());

            List<Truck> allTrucks = driver.getTrucks();
            if (allTrucks.size() > 0) {
                ArrayAdapter<Truck> dataAdapter = new ArrayAdapter<>(this.activity, android.R.layout.simple_spinner_item, allTrucks);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                truckRegNo.setAdapter(dataAdapter);
            }
        }

    }

    public void onCancelConfirmReserveButtonClicked(View view) {

    }

    public void onConfirmReserveButtonClicked(View view) {

    }
}
