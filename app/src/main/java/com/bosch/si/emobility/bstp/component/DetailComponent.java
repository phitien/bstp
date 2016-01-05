package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.component.ux.DetailLayout;
import com.bosch.si.emobility.bstp.core.Component;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.ParkingLocation;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class DetailComponent extends Component {

    private ParkingLocation parkingLocation;
    private TextView textViewParkingName;
    private TextView textViewParkingAddress;
    private TextView textViewAvailability;
    private TextView textViewSecurityLevel;
    private ListView listViewSecurityDetails;

    public DetailComponent(Activity activity) {
        super(activity);
    }

    public ParkingLocation getParkingLocation() {
        return parkingLocation;
    }

    public void parkingLocationUpdated() {
        setParkingLocation(DataManager.getInstance().getCurrentParkingLocation());
    }

    public void setParkingLocation(ParkingLocation parkingLocation) {
        try {
            DetailLayout detailLayout = (DetailLayout) this.activity.findViewById(R.id.detailLayout);
            detailLayout.minimize();
        } catch (Exception e) {
        }
        this.parkingLocation = parkingLocation;
        setEnabled(true, false);
        populateData();
    }

    private void populateData() {
        if (this.parkingLocation != null) {
            //TODO remove these hardcoded lines below
            parkingLocation.setSecurityLevel("3");
            parkingLocation.getSecurityDetails().add("CCTV");
            parkingLocation.getSecurityDetails().add("Security fence");

            textViewParkingName.setText(parkingLocation.getLocationTitle());
            textViewParkingAddress.setText(parkingLocation.getAddress());
            textViewAvailability.setText(String.format("%d/%d",
                    Integer.valueOf(parkingLocation.getAvailabilityCount()),
                    Integer.valueOf(parkingLocation.getTotalCapacityCount())));
            textViewSecurityLevel.setText(parkingLocation.getSecurityLevel());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.activity, R.layout.security_item, R.id.text1, parkingLocation.getSecurityDetails());
            listViewSecurityDetails.setAdapter(adapter);
        }
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (ViewGroup) this.activity.findViewById(R.id.detailLayout);

        textViewParkingName = (TextView) this.activity.findViewById(R.id.textViewParkingName);
        textViewParkingAddress = (TextView) this.activity.findViewById(R.id.textViewParkingAddress);
        textViewAvailability = (TextView) this.activity.findViewById(R.id.textViewAvailability);
        textViewSecurityLevel = (TextView) this.activity.findViewById(R.id.textViewSecurityLevel);
        listViewSecurityDetails = (ListView) this.activity.findViewById(R.id.listViewSecurityDetails);

        setEnabled(false, true);
    }

    @Override
    protected boolean isFade() {
        return true;
    }

    public void onReserveButtonClicked(View view) {
        //TODO call rest service to reserve a parking lot
    }
}
